package org.nrg.xnat.snapshot.services.convert;

import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xnat.plexiviewer.lite.io.PlexiFileSaver;
import org.nrg.xnat.plexiviewer.reader.DICOMReader;
import org.nrg.xnat.plexiviewer.utils.DicomSorter;
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
import ij.process.StackProcessor;

/**
 * @author pradeep.d
 *
 */
public class SnapshotDicomConventImage {
	private String directory;
	private String[] list;
	private boolean zipped = false;
	private int n, start, increment;
	private double scale = 100.0;
	private FileInfo fi;
	private String info1;
	private String orientation = null;
	private String patientOrientation = null;
	private String title;
	private String rowAxis;
	private String colAxis;

	/**
	 * @param directory
	 */
	public SnapshotDicomConventImage(String directory) {
		this.directory = directory;
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
				dir.mkdir();
				for (int i = 0; i < list.length; i++) {
					new UnzipFile().gunzip(directory + File.separator + list[i], dir.getPath());
				}
				directory = dir.getPath();
				list = (new File(directory)).list();
			} catch (IOException ioe) {
				System.out.println("DicomSequence:: Unable to create temporary directory");
			}
		}
	}

	/**
	 * @return
	 */
	public ImagePlus getImagePlus() {
		return getImagePlus(null, true, false);
	}

	/**
	 * @param filter
	 * @param convertToGrayscale
	 * @param convertToRGB
	 * @return
	 */
	public ImagePlus getImagePlus(String filter, boolean convertToGrayscale, boolean convertToRGB) {
		ImagePlus rtn = null;
		list = sortFileList(list);
		n = list.length;
		start = 1;
		increment = 1;
		scale = 100;
		if (IJ.debugMode) {
			IJ.log("DicomSequence: " + directory + " (" + list.length + " files)");
		}
		int width = 0, height = 0, depth = 0, bitDepth = 0;
		ImageStack stack = null;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		Calibration cal = null;
		boolean allSameCalibration = true;
		try {
			for (int i = 0; i < list.length; i++) {
				if (list[i].endsWith(".txt")) {
					continue;
				}
				ImagePlus imp = (new Opener()).openImage(directory, list[i]);
				if (imp != null) {
					width = imp.getWidth();
					height = imp.getHeight();
					bitDepth = imp.getBitDepth();
					fi = imp.getOriginalFileInfo();
				}
			}
			if (width == 0) {
				IJ.error("Import Sequence", "This folder does not appear to contain any TIFF,\n"
						+ "JPEG, BMP, DICOM, GIF, FITS or PGM files.");
				return null;
			}

			if (filter != null && (filter.equals("") || filter.equals("*"))) {
				filter = null;
			}
			if (filter != null) {
				int filteredImages = 0;
				for (int i = 0; i < list.length; i++) {
					if (list[i].indexOf(filter) >= 0) {
						filteredImages++;
					} else {
						list[i] = null;
					}
				}
				if (filteredImages == 0) {
					IJ.error("None of the " + list.length + " files contain\n the string '" + filter
							+ "' in their name.");
					return null;
				}
				String[] list2 = new String[filteredImages];
				int j = 0;
				for (int i = 0; i < list.length; i++) {
					if (list[i] != null) {
						list2[j++] = list[i];
					}
				}
				list = list2;
			}

			if (n < 1) {
				n = list.length;
			}
			if (start < 1 || start > list.length) {
				start = 1;
			}
			if (start + n - 1 > list.length) {
				n = list.length - start + 1;
			}
			int count = 0;
			int counter = 0;
			for (int i = start - 1; i < list.length; i++) {
				if (list[i].endsWith(".txt")) {
					continue;
				}
				if ((counter++ % increment) != 0) {
					continue;
				}
				Opener opener = new Opener();
				opener.setSilentMode(true);
				ImagePlus imp = opener.openImage(directory, list[i]);
				if (imp != null && stack == null) {
					width = imp.getWidth();
					height = imp.getHeight();
					depth = imp.getStackSize();
					bitDepth = imp.getBitDepth();
					cal = imp.getCalibration();
					if (convertToRGB) {
						bitDepth = 24;
					}
					if (convertToGrayscale) {
						bitDepth = 8;
					}
					ColorModel cm = imp.getProcessor().getColorModel();
					if (scale < 100.0) {
						stack = new ImageStack((int) (width * scale / 100.0), (int) (height * scale / 100.0), cm);
					} else {
						stack = new ImageStack(width, height, cm);
					}
					info1 = (String) imp.getProperty("Info");
					if (orientation == null) {
						setOrientation();
					}
					if (patientOrientation == null) {
						setPatientOrientation();
					}
				}
				if (imp == null) {
					if (!list[i].startsWith(".")) {
						IJ.log(list[i] + ": unable to open");
					}
					continue;
				}
				if (imp.getWidth() != width || imp.getHeight() != height) {
					IJ.log(list[i] + ": wrong size; " + width + "x" + height + " expected, " + imp.getWidth() + "x"
							+ imp.getHeight() + " found");
					continue;
				}
				String label = imp.getTitle();
				if (depth == 1) {
					String info = (String) imp.getProperty("Info");
					if (info != null) {
						label += "\n" + info;
					}
				}
				if (imp.getCalibration().pixelWidth != cal.pixelWidth) {
					allSameCalibration = false;
				}
				ImageStack inputStack = imp.getStack();
				for (int slice = 1; slice <= inputStack.getSize(); slice++) {
					ImageProcessor ip = inputStack.getProcessor(slice);
					int bitDepth2 = imp.getBitDepth();
					if (convertToRGB) {
						ip = ip.convertToRGB();
						bitDepth2 = 24;
					} else if (convertToGrayscale) {
						ip = ip.convertToByte(true);
						bitDepth2 = 8;
					}
					if (bitDepth2 != bitDepth) {
						if (bitDepth == 8) {
							ip = ip.convertToByte(true);
							bitDepth2 = 8;
						} else if (bitDepth == 24) {
							ip = ip.convertToRGB();
							bitDepth2 = 24;
						}
					}
					if (bitDepth2 != bitDepth) {
						IJ.log(list[i] + ": wrong bit depth; " + bitDepth + " expected, " + bitDepth2 + " found");
						break;
					}
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
					String label2 = label;
					if (depth > 1) {
						label2 = "" + slice;
					}
					stack.addSlice(label2, ip);
				}
				if (count >= n) {
					break;
				}
			}
		} catch (OutOfMemoryError e) {
			if (stack != null) {
				stack.trim();
			}
		}
		if (stack != null && stack.getSize() > 0) {
			if (info1 != null && info1.lastIndexOf("7FE0,0010") > 0) {
				stack = (new DicomSorter()).sort(stack);
			}
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
		reorientImage(rtn);
		if (zipped) {
			FileUtils.deleteFile(directory);
		}
		return rtn;
	}

	/**
	 * @param img
	 * @return
	 */
	private ImagePlus reorientImage(ImagePlus img) {
		StackProcessor sp = null;
		ImagePlus rtn = img;
		if (orientation.equals("SAGITTAL")) {
			if (rowAxis.equals("A") && colAxis.equals("H")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("F") && colAxis.equals("A")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("H") && colAxis.equals("A")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
			} else if (rowAxis.equals("P") && colAxis.equals("H")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipHorizontal();
				sp.flipVertical();
			} else if (rowAxis.equals("P") && colAxis.equals("F")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipHorizontal();
			} else if (rowAxis.equals("H") && colAxis.equals("P")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipHorizontal();
			} else if (rowAxis.equals("F") && colAxis.equals("P")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
			}
		} else if (orientation.equals("TRANSVERSE")) {
			if (rowAxis.equals("R") && colAxis.equals("A")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipVertical();
				sp.flipHorizontal();
			} else if (rowAxis.equals("R") && colAxis.equals("P")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipHorizontal();
			} else if (rowAxis.equals("L") && colAxis.equals("A")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("A") && colAxis.equals("R")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("A") && colAxis.equals("L")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
			} else if (rowAxis.equals("P") && colAxis.equals("R")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
			} else if (rowAxis.equals("P") && colAxis.equals("L")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipHorizontal();
			}
		} else if (orientation.equals("CORONAL")) {
			if (rowAxis.equals("R") && colAxis.equals("H")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipVertical();
				sp.flipHorizontal();
			} else if (rowAxis.equals("R") && colAxis.equals("F")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipHorizontal();
			} else if (rowAxis.equals("L") && colAxis.equals("H")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("H") && colAxis.equals("R")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipVertical();
			} else if (rowAxis.equals("H") && colAxis.equals("L")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
			} else if (rowAxis.equals("F") && colAxis.equals("R")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateRight();
				rtn = new ImagePlus(img.getTitle(), stack);
			} else if (rowAxis.equals("F") && colAxis.equals("L")) {
				sp = new StackProcessor(img.getStack(), img.getProcessor());
				ImageStack stack = sp.rotateLeft();
				rtn = new ImagePlus(img.getTitle(), stack);
				sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
				sp.flipVertical();
			}
			rtn = new ImagePlus(rtn.getTitle(), reverseStack(rtn.getStack(), rtn));
		}
		orientation += "F";
		return rtn;
	}

	/**
	 * @param stack
	 * @param imp
	 * @return
	 */
	private ImageStack reverseStack(ImageStack stack, ImagePlus imp) {
		int n;
		ImageStack stack2 = imp.createEmptyStack();
		while ((n = stack.getSize()) > 0) {
			stack2.addSlice(stack.getSliceLabel(n), stack.getProcessor(n));
			stack.deleteLastSlice();
		}
		return stack2;
	}

	/**
	 * 
	 */
	private void setPatientOrientation() {
		int patientOri = info1.indexOf("0020,0020");
		if (patientOri == -1) {
			return;
		}
		patientOrientation = (((info1.substring(patientOri + 10).split("\n"))[0]).split(":"))[1];
	}

	/**
	 * 
	 */
	private void setOrientation() {
		int imagePositionPatient = info1.indexOf("0020,0037");
		String dcs = (((info1.substring(imagePositionPatient + 10).split("\n"))[0]).split(":"))[1];
		rowAxis = DICOMReader.getRowAxis(dcs);
		colAxis = DICOMReader.getColumnAxis(dcs);
		orientation = DICOMReader.setOrientation(dcs);
	}

	/**
	 * @param list
	 * @return
	 */
	String[] sortFileList(String[] list) {
		int listLength = list.length;
		int first = listLength > 1 ? 1 : 0;
		if ((list[first].length() == list[listLength - 1].length())
				&& (list[first].length() == list[listLength / 2].length())) {
			ij.util.StringSorter.sort(list);
			return list;
		}
		int maxDigits = 15;
		String[] list2 = null;
		char ch;
		for (int i = 0; i < listLength; i++) {
			int len = list[i].length();
			String num = "";
			for (int j = 0; j < len; j++) {
				ch = list[i].charAt(j);
				if (ch >= 48 && ch <= 57) {
					num += ch;
				}
			}
			if (list2 == null) {
				list2 = new String[listLength];
			}
			num = "000000000000000" + num; // prepend maxDigits leading zeroes
			num = num.substring(num.length() - maxDigits);
			list2[i] = num + list[i];
		}
		if (list2 != null) {
			ij.util.StringSorter.sort(list2);
			for (int i = 0; i < listLength; i++) {
				list2[i] = list2[i].substring(maxDigits);
			}
			return list2;
		} else {
			ij.util.StringSorter.sort(list);
			return list;
		}
	}

	/**
	 * @param baseimage
	 * @param scan
	 * @param sessionId
	 * @param cachepaths
	 * @return
	 * @throws Exception
	 */
	public File createThumbnail(ImagePlus baseimage, XnatImagescandataBean scan, String sessionId, String cachepaths)
			throws Exception {
		File targetFile = null;
		ImagePlus snapshot = createMontage(baseimage);
		if (snapshot != null) {
			BitConverter converter = new BitConverter();
			converter.convertTo8BitColor(snapshot);
			String tbfilenameroot = sessionId + "_" + scan.getId() + "_qc";
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
