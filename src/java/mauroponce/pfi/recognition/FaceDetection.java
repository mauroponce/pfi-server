package mauroponce.pfi.recognition;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import mauroponce.pfi.utils.AppConstants;
import mauroponce.pfi.utils.ImageUtils;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

/***
 * http://blog.csdn.net/ljsspace/article/details/6664011
 */
public class FaceDetection {

	// The cascade definition to be used for detection.
//	private static final String CASCADE_FILE = "C:\\Users\\smoral\\Desktop\\tmp\\haarcascade_frontalface_alt.xml";
	private static final String CASCADE_FILE = "C:\\Users\\smoral\\Desktop\\tmp\\";

	public static void detectMultipleFaces(String fileInputPath, String haarCascadeFileName, String fileOutputPath) throws Exception {
		

		// Load the original image.
		IplImage originalImage = cvLoadImage(fileInputPath,1);

		// We need a grayscale image in order to do the recognition, so we
		// create a new image of the same size as the original one.
		IplImage grayImage = IplImage.create(originalImage.width(),
				originalImage.height(), IPL_DEPTH_8U, 1);

		// We convert the original image to grayscale.
		 cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);

		CvMemStorage storage = CvMemStorage.create();

		// We instantiate a classifier cascade to be used for detection, using
		// the cascade definition.
		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
				cvLoad(CASCADE_FILE+haarCascadeFileName));

		// We detect the faces.
		CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, 1,
				0);

		// We iterate over the discovered faces and draw yellow rectangles
		// around them.
		for (int i = 0; i < faces.total(); i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			IplImage imageCropped = ImageUtils.cropImage(originalImage, r);
			
//			cvRectangle(originalImage, cvPoint(r.x(), r.y()),
//					cvPoint(r.x() + r.width(), r.y() + r.height()),
//					CvScalar.YELLOW, 1, CV_AA, 0);
			
			// Save cropped image to a new file.
//			cvSaveImage(fileOutputPath+i+".jpg", imageCropped);
			IplImage imageResized = ImageUtils.resizeImage(imageCropped,103,106); 
			// Save resized image to a new file.
			cvSaveImage(fileOutputPath+i+".jpg", imageResized);
		}
//
//		// Save the image to a new file.
//		cvSaveImage("C:\\Users\\smoral\\Desktop\\tmp\\"+fileOutputName+".jpg", originalImage);
		// System.out.println("Printe image "+haarCascadeFileName);
	}
	
	public static void detectOneFace(String fileInputPath, String haarCascadeFileName, String fileOutputPath) {
		

		// Load the original image.
		IplImage originalImage = cvLoadImage(fileInputPath,1);
		
		// Change image resolution to improve velocity
		if (originalImage.width() > 500){
			int newWidth = originalImage.width()*50/100;
			int newHeight = originalImage.height()*50/100;
			originalImage = ImageUtils.resizeImage(originalImage,newWidth,newHeight);
		}

		// We need a grayscale image in order to do the recognition, so we
		// create a new image of the same size as the original one.
		IplImage grayImage = IplImage.create(originalImage.width(),
				originalImage.height(), IPL_DEPTH_8U, 1);

		// We convert the original image to grayscale.
		 cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);

		CvMemStorage storage = CvMemStorage.create();

		// We instantiate a classifier cascade to be used for detection, using
		// the cascade definition.
		if (haarCascadeFileName == null){
			haarCascadeFileName = "haarcascade_frontalface_alt.xml";//TODO order this mess in a better way
		}
		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
				cvLoad(CASCADE_FILE+haarCascadeFileName));

		// We detect the faces.
		CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, CV_HAAR_FIND_BIGGEST_OBJECT|CV_HAAR_DO_ROUGH_SEARCH,
				0);//CV_HAAR_FIND_BIGGEST_OBJECT|CV_HAAR_DO_ROUGH_SEARCH indicates to find the biggest object and stop the process

		// We iterate over the discovered faces and draw yellow rectangles
		// around them.
		if (faces.total()>0){
			CvRect r = new CvRect(cvGetSeqElem(faces, 0));
			IplImage imageCropped = ImageUtils.cropImage(originalImage, r);
			
//			cvRectangle(originalImage, cvPoint(r.x(), r.y()),
//					cvPoint(r.x() + r.width(), r.y() + r.height()),
//					CvScalar.YELLOW, 1, CV_AA, 0);
			
			// Save cropped image to a new file.
//			cvSaveImage(fileOutputPath+i+".jpg", imageCropped);
			IplImage imageResized = ImageUtils.resizeImage(imageCropped,AppConstants.IMAGE_FACE_WIDTH,AppConstants.IMAGE_FACE_HEIGHT); 
			// Save resized image to a new file.
			
			cvSaveImage(fileOutputPath, imageResized);
			// System.out.println("Saved image "+fileOutputPath);
		}
	}
}