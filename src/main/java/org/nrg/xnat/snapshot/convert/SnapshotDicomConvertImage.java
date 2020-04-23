package org.nrg.xnat.snapshot.convert;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xnat.plexiviewer.lite.io.PlexiFileSaver;
import org.nrg.xnat.plexiviewer.utils.FileUtils;
import org.nrg.xnat.plexiviewer.utils.ImageUtils;
import org.nrg.xnat.plexiviewer.utils.UnzipFile;
import org.nrg.xnat.plexiviewer.utils.transform.BitConverter;
import org.nrg.xnat.plexiviewer.utils.transform.IntensitySetter;
import org.nrg.xnat.plexiviewer.utils.transform.PlexiMontageMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.ImageProcessor;

/**
 * @author pradeep.d
 *
 */
public class SnapshotDicomConvertImage {

	private double scale = 100.0;
	private int n, start;
	private FileInfo fi;
	private String info1;
	private String directory;
	private String[] list;
	private String title;
	private boolean zipped = false;
	private int width = 0, height = 0, depth = 0;

	/**
	 * @param dir
	 */
	public SnapshotDicomConvertImage(String dir) {
		directory = dir;
		list = (new File(directory)).list();
		String zext = ".gz";
		for (int i = 0; i < list.length; i++) {
			if (list[i].endsWith(zext)) {
				zipped = true;
			}
		}
		unzip();
	}

	/**
	 * 
	 */
	private void unzip() {
		if (zipped) {
			String suffix = "_" + new Random().nextInt();
			File tempDir = new File(FileUtils.getTempFolder());
			try {
				File dir = File.createTempFile("NRG", suffix, tempDir);
				if (dir.exists()) {
					dir.delete();
				}
				System.out.println("DicomSequence tempdir " + dir.getPath());

				boolean success = dir.mkdir();
				System.out.println("DicomSequence tempdir " + dir.getPath() + " success " + success);
				for (int i = 0; i < list.length; i++) {
					new UnzipFile().gunzip(directory + File.separator + list[i], dir.getPath());
				}
				directory = dir.getPath();
				list = (new File(directory)).list();
			} catch (IOException ioe) {
				System.out.println("DicomSequence:: Unable to create temporary directory " + ioe.getMessage());
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public ImagePlus getImagePlus() {
		ImagePlus imagesPlus = null;
		n = list.length;
		ImageStack stack = null;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		Calibration cal = null;
		boolean allSameCalibration = true;
		int count = 0;
		System.out.println("list ::" + list);

		try {
			for (int i = start; i < list.length; i++) {
				Opener opener = new Opener();
				opener.setSilentMode(true);
				ImagePlus imp = opener.openImage(directory, list[i]);
				if (imp != null && stack == null) {
					System.out.println("imp != null ::" + imp);
					width = imp.getWidth();
					height = imp.getHeight();
					cal = imp.getCalibration();
					ColorModel cm = imp.getProcessor().getColorModel();
					if (scale < 100.0) {
						stack = new ImageStack((int) (width * scale / 100.0), (int) (height * scale / 100.0), cm);
					} else {
						stack = new ImageStack(width, height, cm);
					}
				}

				if (imp == null) {
					if (!list[i].startsWith(".")) {
						IJ.log(list[i] + ": unable to open");
					}
					continue;
				}
				String label = imp.getTitle();
				if (depth == 1) {
					String info = (String) imp.getProperty("Info");
					if (info != null) {
						label += "\n" + info;
					}
				}

				ImageStack inputStack = imp.getStack();
				for (int slice = 1; slice <= inputStack.getSize(); slice++) {
					ImageProcessor ip = inputStack.getProcessor(slice);
					if (slice == 1) {
						count++;
					}
					if (scale < 100.0) {
						ip = ip.resize((int) (width * scale / 100.0), (int) (height * scale / 100.0));
					}
					if (ip.getMin() < min) {
						min = ip.getMin();
					}
					if (ip.getMax() > max) {
						max = ip.getMax();
					}
					if (depth > 1) {
						label = "" + slice;
					}
					stack.addSlice(ip);
				}
				if (count >= n) {
					break;
				}
			}
			if (stack != null && stack.getSize() > 0) {
				ImagePlus imagePlus = new ImagePlus(title, stack);
				if (imagePlus.getType() == ImagePlus.GRAY16 || imagePlus.getType() == ImagePlus.GRAY32) {
					imagePlus.getProcessor().setMinAndMax(min, max);
				}
				imagePlus.setFileInfo(fi);
				if (allSameCalibration) {
					imagePlus.setCalibration(cal);
				}
				if (imagePlus.getStackSize() == 1 && info1 != null) {
					imagePlus.setProperty("Info", info1);
				}
				imagesPlus = imagePlus;
			}
		} catch (OutOfMemoryError e) {

		} finally {
			if (zipped) {
				FileUtils.deleteFile(directory, true);
			}
		}
		return imagesPlus;
	}

	/**
	 * @param scan
	 * @param cachepaths
	 * @param montageFlag
	 * @param gridview
	 * @return
	 * @throws Exception
	 */
	public File createSnapshotImage(XnatImagescandataBean scan, String cachepaths, boolean montageFlag, String gridview)
			throws Exception {
		ImagePlus baseimage = getImagePlus();
		File targetFile = null;
		ImagePlus snapshot = getSnapshot(baseimage, montageFlag, gridview);

		if (snapshot != null) {
			BitConverter converter = new BitConverter();
			converter.convertTo8BitColor(snapshot);
			String tbfilenameroot = scan.getImageSessionId() + "_" + scan.getId() ;
			if(!gridview.isEmpty()) {
				tbfilenameroot = tbfilenameroot+ "_" + gridview.toUpperCase();
			} 
			PlexiFileSaver fs = new PlexiFileSaver(snapshot.getImage());
			String fileName = tbfilenameroot + ".gif";
			String filePath = cachepaths + File.separator + fileName;
			boolean saved = fs.saveImageAsGif(filePath);
			if (!saved) {
				throw new Exception(
						"Couldnt save file snapshot for scan " + scan.getId() + " at the location " + filePath);
			}
			targetFile = new File(filePath);
		}
		return targetFile;
	}

	/**
	 * @param baseimage
	 * @param montage
	 * @param gridview
	 * @return
	 * @throws Exception
	 */
	private ImagePlus getSnapshot(ImagePlus baseimage, boolean montage, String gridview) throws Exception {
		ImagePlus rtn = null;
		if (montage) {
			rtn = createMontage(baseimage, gridview);
		} else {
			if (baseimage != null) {
				int sliceNo = 5;
				if (baseimage.getStackSize() == 1) {
					sliceNo = 1;
				} else if (baseimage.getStackSize() < sliceNo) {
					sliceNo = 2;
				}
				baseimage.setSlice(sliceNo);
				baseimage.updateImage();
				baseimage.getProcessor().setColor(Color.WHITE);
				baseimage.getProcessor().setFont(new Font("Serif", Font.BOLD, 10));
				baseimage.getProcessor().drawString("Frame: " + sliceNo, baseimage.getWidth(),
						baseimage.getHeight());
				baseimage.updateImage();
				rtn = baseimage;
			}
		}
		return rtn;
	}

	/**
	 * @param image
	 * @param gridViews
	 * @return
	 * @throws Exception
	 */
	private ImagePlus createMontage(ImagePlus image, String gridViews) throws Exception {
		int columns = 1;
		int rows = 1;
		PlexiMontageMaker mm = new PlexiMontageMaker();
		try {
			if (gridViews != null && !gridViews.isEmpty()) {
				String[] rowCol = gridViews.toUpperCase().split("X");
				rows = Integer.parseInt(rowCol[0]);
				columns = Integer.parseInt(rowCol[1]);
			}
		} catch (NumberFormatException ex) {
			_log.error("Error createMontage :: " + ex.getMessage());
			throw new NumberFormatException("Provide valid Grid views ROWXCOL paramter");

		} catch (Exception ex) {
			_log.error("Error createMontage :: " + ex.getMessage());
			throw new Exception("Provide valid Grid views ROWXCOL paramter");
		} 
		
		if(image.getStackSize() <= 0) {
			_log.error("Error createMontage :: DICOM Image does not exist ");
			throw new Exception("DICOM Image does not exist");
		}
		Hashtable<?, ?> attribs = ImageUtils.getSliceIncrement(image, columns * rows);

		int startslice = ((Integer) attribs.get("startslice")).intValue();
		int endslice = ((Integer) attribs.get("endslice")).intValue();
		int increment = ((Integer) attribs.get("increment")).intValue();
		IntensitySetter is = new IntensitySetter(image, true);
		is.autoAdjust(image, image.getProcessor());

		image = mm.makeMontage(image, columns, rows, 1.0, startslice, endslice, increment, true, false);
		image.getProcessor().resetMinAndMax();
		return image;
	}

	/**
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file != null && file.exists()) {
			file.delete();
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(SnapshotDicomConvertImage.class);
}
