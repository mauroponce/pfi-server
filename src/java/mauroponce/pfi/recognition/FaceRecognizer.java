package mauroponce.pfi.recognition;

import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mauroponce.pfi.utils.AppConstants;
import mauroponce.pfi.utils.FileUtils;
import mauroponce.pfi.utils.ImageUtils;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class FaceRecognizer implements IFaceRecognizer {

	protected int nPersons = 0;
	protected final List<String> personNames = new ArrayList<String>();
	protected final List<String> personNamesWithRepetitions = new ArrayList<String>();
	protected CvMat personNumTruthMat;

	public FaceRecognizer() {
		super();
	}

	/**
	 * Return the images procesed and sets personNames
	 * @param courseFolder
	 * @return
	 */
	protected IplImage[] loadFaceImgArrayFromImagesFolders(final String courseFolder) {
			
			IplImage[] faceImgArr;
			
			int iFace = 0;
			int nFaces = 0;
			int width = -1;
			int height = -1;
	
			nPersons = 0;
			
			List<File> imageFolderFiles = new ArrayList<File>();
			
			File courseFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder);
	
			if(courseFolderFile.isDirectory()){
				for (String imageFolderName : courseFolderFile.list()) {
					File imageFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder + "/" + imageFolderName);
					if(imageFolderFile.isDirectory()){
						nFaces += imageFolderFile.listFiles().length;
					}
					imageFolderFiles.add(imageFolderFile);
				} 
			}
			
			
			/*for (final File personDir : topDir.listFiles()) {
				if (personDir.isDirectory()) {
					final File imagesDir = new File(imagesFolderPath + "/"
							+ personDir.getName());
					nFaces += imagesDir.listFiles().length;
				}
			}*/
	
			// allocate the face-image array and person number matrix
			faceImgArr = new IplImage[nFaces];
			personNumTruthMat = cvCreateMat(1, // rows
					nFaces, // cols
					CV_32SC1); // type, 32-bit unsigned, one channel
	
			// initialize the person number matrix - for ease of debugging
			for (int j1 = 0; j1 < nFaces; j1++) {
				personNumTruthMat.put(0, j1, 0);
			}
	
			personNames.clear(); // Make sure it starts as empty.
			personNamesWithRepetitions.clear();
			
			for(File imageFolderFile : imageFolderFiles){
				int personNumber = 0;
				if(imageFolderFile.isDirectory() && !imageFolderFile.getName().contains("resized")){
					nPersons++;
					String personName = imageFolderFile.getName();
					personNumber = Integer.valueOf(personName);
					personNames.add(personName);
					
					for (final File imgFile : imageFolderFile.listFiles()) {
						personNamesWithRepetitions.add(personName);
						String imgFilePath = imgFile.getAbsolutePath();
						personNumTruthMat.put(0, iFace, personNumber);
						
						String studentFolder = imageFolderFile.getAbsolutePath();
						String imgDetectedFilePath = studentFolder+File.pathSeparator+"detected"+imgFile.getName();
						if (imgFile.getName().contains("UADE")){
							// the image is from UADE, we must to detect the face and then delete it
							imgDetectedFilePath = studentFolder+File.pathSeparator+"detected"+imgFile.getName();
							FaceDetection.detectOneFace(imgFilePath, null, imgDetectedFilePath);
						}else{
							imgDetectedFilePath = imgFilePath;				
						}
						
						IplImage faceDetectedImage 
						= cvLoadImage(imgDetectedFilePath, CV_LOAD_IMAGE_GRAYSCALE);
						if (faceDetectedImage == null) {
							throw new RuntimeException("No se puede leer la imagen "
									+ imgFilePath);
						}
						
						if (width == -1) {
							width = faceDetectedImage.width();
							height = faceDetectedImage.height();
						} else if (faceDetectedImage.width() != width
								|| faceDetectedImage.height() != height) {
							faceDetectedImage = ImageUtils.resizeImage(faceDetectedImage, AppConstants.IMAGE_FACE_WIDTH, AppConstants.IMAGE_FACE_HEIGHT);
	//						throw new RuntimeException("wrong size face in "
	//								+ imgFilename + "\nwanted " + width + "x"
	//								+ height + ", but found " + faceImage.width()
	//								+ "x" + faceImage.height());
						}
						
						/* esto es para pasarles las imagenes en 64 x 64 a pablo */
						String cropedStudentFolder = imgFile.getAbsolutePath().replace("course", "crop_course");
						
						faceDetectedImage = preapareImageAndSaveToTestFolder(faceDetectedImage,
								cropedStudentFolder);
						
						faceImgArr[iFace] = faceDetectedImage;
						iFace++;
						
						if (imgFile.getName().contains("UADE")){
							//delete de cropped image
							FileUtils.DeleteFile(imgDetectedFilePath);
						}
					}
				}
			}
			/*
			// store the face images in an array
			for (final File personDir : topDir.listFiles()) {
				int personNumber = 0;
				if (personDir.isDirectory()) {
					nPersons++;
					String personName = personDir.getName();
					personNumber = Integer.valueOf(personName);
					personNames.add(personName);
					final File imagesDir = new File(imagesFolderPath + "/"
							+ personDir.getName());
					for (final File imgFile : imagesDir.listFiles()) {
						String imgFilename = imgFile.getAbsolutePath();
						personNumTruthMat.put(0, iFace, personNumber);
						final IplImage faceImage 
							= cvLoadImage(imgFilename, CV_LOAD_IMAGE_GRAYSCALE);
						if (faceImage == null) {
							throw new RuntimeException("No se puede leer la imagen "
									+ imgFilename);
						}
						if (width == -1) {
							width = faceImage.width();
							height = faceImage.height();
						} else if (faceImage.width() != width
								|| faceImage.height() != height) {
							throw new RuntimeException("wrong size face in "
									+ imgFilename + "\nwanted " + width + "x"
									+ height + ", but found " + faceImage.width()
									+ "x" + faceImage.height());
						}
						faceImgArr[iFace] = faceImage;
						iFace++;
					}
				}
			}
			*/
			// System.out.println("Images: " + nFaces);
			// System.out.println("Persons: " + nPersons);
			if (nPersons > 0) {
				for (String pName : personNames) {
					// System.out.println(pName);
				}
			}
	
			return faceImgArr;
		}

	protected IplImage[] loadFaceImgArrayFromImagePath(final String imagePath) {
		IplImage[] faceImgArr;
		int iFace = 0;
		int nFaces = 1;
		int width = -1;
		int height = -1;
		nPersons = 0;
	
		faceImgArr = new IplImage[nFaces];
		personNumTruthMat = cvCreateMat(1, // rows
				nFaces, // cols
				CV_32SC1); // type, 32-bit unsigned, one channel
	
		for (int j1 = 0; j1 < nFaces; j1++) {
			personNumTruthMat.put(0, j1, 0);
		}
		nPersons = 1;
		int personNumber = 0;
		personNumTruthMat.put(0, // i
				iFace, // j
				personNumber); // v
		IplImage faceImage = cvLoadImage(imagePath, // filename
				CV_LOAD_IMAGE_GRAYSCALE); // isColor
		if (faceImage == null) {
			throw new RuntimeException("Can't load image from " + imagePath);
		}
		if (width == -1) {
			width = faceImage.width();
			height = faceImage.height();
		} else if (faceImage.width() != width || faceImage.height() != height) {
			throw new RuntimeException("wrong size face in " + imagePath
					+ "\nwanted " + width + "x" + height + ", but found "
					+ faceImage.width() + "x" + faceImage.height());
		}		

		String cropedStudentFolder = imagePath.replace("a_reconocer", "crop_a_reconocer");
		
		faceImage = preapareImageAndSaveToTestFolder(faceImage,
				cropedStudentFolder);
		
		faceImgArr[iFace] = faceImage;
		iFace++;		
		return faceImgArr;
	}

	private IplImage preapareImageAndSaveToTestFolder(IplImage faceImage,
			String cropedStudentFolder) {
		faceImage = ImageUtils.resizeImage(faceImage, AppConstants.IMAGE_FACE_WIDTH, AppConstants.IMAGE_FACE_HEIGHT);

		// Give the image a standard brightness and contrast.
		cvEqualizeHist(faceImage, faceImage);
		
		
		File imageFile = new File(cropedStudentFolder);
		File parentFile = imageFile.getParentFile();
		if (!parentFile.exists()){
			parentFile.mkdir();
		}
		
//		cvSaveImage(cropedStudentFolder, faceImage);

//		//Read the file to a BufferedImage  
//		BufferedImage image = faceImage.getBufferedImage(); 
//		
//		//Create a file for the output  
//		File output = new File(cropedStudentFolder.replace(".jpg", ".bmp"));  
//		  
//		//Write the image to the destination as a BMP  
//		try {
//			ImageIO.write(image, "bmp", output);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		
		return faceImage;
	}

}