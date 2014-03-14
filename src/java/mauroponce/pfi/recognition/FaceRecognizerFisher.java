package mauroponce.pfi.recognition;
import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mauroponce.pfi.utils.AppConstants;
import mauroponce.pfi.utils.ImageUtils;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

public class FaceRecognizerFisher {
	com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;
	IplImage greyTestImage;
	MatVector images;
	int[] labels;
	IplImage testImage;
	String[] labelsStrings;
	private Object[] imageFiles;
	private List<File> imageFolderFiles;
	private File courseFolderFile;
	private int predictedLabel;

	
	
	public void fisherFacesRecognition(String courseFolder, String imageDir) {
        testImage = cvLoadImage(imageDir);

        imageFolderFiles = new ArrayList<File>();
		
		courseFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder);

		if(courseFolderFile.isDirectory()){
			for (String imageFolderName : courseFolderFile.list()) {
				File imageFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder + "/" + imageFolderName);
				imageFolderFiles.addAll(Arrays.asList(imageFolderFile.listFiles()));
			} 
		}
		
		imageFiles = imageFolderFiles.toArray();
		
        images = new MatVector(imageFiles.length);

        labels = new int[imageFiles.length];
        labelsStrings = new String[imageFiles.length];

        int counter = 0;

        IplImage img;
        IplImage grayImg;

        for (Object object : imageFiles) {
        	File image = (File) object;
            img = cvLoadImage(image.getAbsolutePath());
            // the imge must be multiple of 8 or 16 (http://stackoverflow.com/questions/20739574/getting-opencv-error-image-step-is-wrong-in-eigenfaces-predict-method)
            img = ImageUtils.resizeImage(img, 200, 200);
            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);

            cvCvtColor(img, grayImg, CV_BGR2GRAY);

            images.put(counter, grayImg);

            labels[counter] = counter;
            labelsStrings[counter] = image.getName();

            counter++;
        }

        greyTestImage = IplImage.create(testImage.width(), testImage.height(), IPL_DEPTH_8U, 1);

        faceRecognizer = createFisherFaceRecognizer();

        faceRecognizer.train(images, labels);

        cvCvtColor(testImage, greyTestImage, CV_BGR2GRAY);
        greyTestImage = ImageUtils.resizeImage(greyTestImage, 200, 200);

        int[] labelsPredicted = new int[3];
		double[] arg2 = new double[3];
		faceRecognizer.predict(greyTestImage, labelsPredicted, arg2);

        System.out.println("Predicted label: " + labelsStrings[labelsPredicted[0]]);
    }
}

