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
	boolean zipped = false;

	public static final String ORIENTATION_AS_ACQUIRED = "As Acquired";

	String rowAxis;
	String colAxis;
	int width = 0, height = 0, depth = 0, bitDepth = 0;

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
		ImagePlus rtn = null;
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
				System.out.println("directory ::" + directory);
				ImagePlus imp = opener.openImage(directory, list[i]);
				if (imp != null && stack == null) {
					System.out.println("imp != null ::" + imp);
					width = imp.getWidth();
					height = imp.getHeight();
					depth = imp.getStackSize();
					bitDepth = imp.getBitDepth();
					cal = imp.getCalibration();
					ColorModel cm = imp.getProcessor().getColorModel();
					stack = new ImageStack(width, height, cm);
				}

				if (imp == null) {
					if (!list[i].startsWith(".")) {
						IJ.log(list[i] + ": unable to open");
					}
					continue;
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
					String label2 = null;
					if (depth > 1) {
						label2 = "" + slice;
					}
					stack.addSlice(ip);
				}
				if (count >= n) {
					break;
				}
			}
			if (stack != null && stack.getSize() > 0) {
				ImagePlus imp2 = new ImagePlus(title, stack);
				if (imp2.getType() == ImagePlus.GRAY16 || imp2.getType() == ImagePlus.GRAY32) {
					imp2.getProcessor().setMinAndMax(min, max);
				}
				imp2.setFileInfo(fi); // saves FileInfo of the first image
				if (allSameCalibration) {
					imp2.setCalibration(cal); // use calibration from first image
				}
				if (imp2.getStackSize() == 1 && info1 != null) {
					imp2.setProperty("Info", info1);
				}
				rtn = imp2;
			}
		} catch (OutOfMemoryError e) {
		} finally {
			if (zipped) {
				FileUtils.deleteFile(directory, true);
			}
		}
		return rtn;
	}

	/**
	 * @param scan
	 * @param cachepaths
	 * @param montageFlag
	 * @return
	 * @throws Exception
	 */
	public File createThumbnail(XnatImagescandataBean scan, String cachepaths, boolean montageFlag) throws Exception {
		ImagePlus baseimage = getImagePlus();
		File targetFile = null;
		ImagePlus snapshot = getSnapshot(baseimage, montageFlag);

		if (snapshot != null) {
			BitConverter converter = new BitConverter();
			converter.convertTo8BitColor(snapshot);
			String tbfilenameroot = scan.getImageSessionId() + "_" + scan.getId() + "_qc";
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
	 * @return
	 */
	private ImagePlus getSnapshot(ImagePlus baseimage, boolean montage) {
		ImagePlus rtn = null;
		if (montage) {
			rtn = createMontage(baseimage);
		} else {
			if (baseimage != null) {
				int sliceNo = 5;
				if (baseimage.getStackSize() == 1) {
					sliceNo = 1;
				} else if (baseimage.getStackSize() < sliceNo) {
					sliceNo = 2;
				}
				baseimage.setSlice(sliceNo);//
				baseimage.updateImage();// five images or 5th fi
				baseimage.getProcessor().setColor(Color.WHITE);
				baseimage.getProcessor().setFont(new Font("Serif", Font.BOLD, 10));
				baseimage.getProcessor().drawString("Frame: " + sliceNo, baseimage.getWidth() - 50,
						baseimage.getHeight() - 5);
				baseimage.updateImage();
				rtn = baseimage;
			}
		}
		return rtn;
	}

	/**
	 * @param image
	 * @return
	 */
	private ImagePlus createMontage(ImagePlus image) {
		PlexiMontageMaker mm = new PlexiMontageMaker();
		int columns = 1;
		int rows = 1;
		if (image.getStackSize() == 1) {
			columns = 1;
			rows = 1;
		} else if (image.getStackSize() == 2) {
			rows = 1;
			columns = 2;
		} else if (image.getStackSize() == 3) {
			rows = 1;
			columns = 3;
		} else { // extract the nearest square
			for (; columns * columns <= image.getStackSize(); columns++) {
				;
			}
			columns--;
			rows = columns;
		}
		// If there are too many frames then we reduce the grid size
		if (columns > 7) {
			columns = 7;
			rows = columns;
		}
		Hashtable attribs = ImageUtils.getSliceIncrement(image, columns * rows);

		int startslice = ((Integer) attribs.get("startslice")).intValue();
		int endslice = ((Integer) attribs.get("endslice")).intValue();
		int increment = ((Integer) attribs.get("increment")).intValue();
		IntensitySetter is = new IntensitySetter(image, true);
		is.autoAdjust(image, image.getProcessor());

		image = mm.makeMontage(image, columns, rows, 0.5, startslice, endslice, increment, true, false);
		image.getProcessor().resetMinAndMax();
		return image;
	}

	/**
	 * @param f
	 */
	public void deleteFile(File f) {
		if (f != null && f.exists()) {
			f.delete();
		}
	}
}
