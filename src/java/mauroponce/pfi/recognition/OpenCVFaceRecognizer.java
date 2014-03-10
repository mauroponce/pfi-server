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

public class OpenCVFaceRecognizer {
    public static void main(String[] args) {
        String trainingDir = AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + "course_1";
        IplImage testImage = cvLoadImage("C:/Users/smoral/Documents/PFI/faces/UADE_Santiago_Moral_.jpg");

        List<File> imageFolderFiles = new ArrayList<File>();
		
		String courseFolder = "course_1";
		File courseFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder);

		if(courseFolderFile.isDirectory()){
			for (String imageFolderName : courseFolderFile.list()) {
				File imageFolderFile = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + courseFolder + "/" + imageFolderName);
				imageFolderFiles.addAll(Arrays.asList(imageFolderFile.listFiles()));
			} 
		}
		
		Object[] imageFiles = imageFolderFiles.toArray();
		
        MatVector images = new MatVector(imageFiles.length);

        int[] labels = new int[imageFiles.length];
        String[] labelsString = new String[imageFiles.length];

        int counter = 0;

        IplImage img;
        IplImage grayImg;

        for (Object object : imageFiles) {
        	File image = (File) object;
            img = cvLoadImage(image.getAbsolutePath());
            img = ImageUtils.resizeImage(img, 200, 200);
            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);

            cvCvtColor(img, grayImg, CV_BGR2GRAY);

            images.put(counter, grayImg);

            labels[counter] = counter;
            labelsString[counter] = image.getName();

            counter++;
        }

        IplImage greyTestImage = IplImage.create(testImage.width(), testImage.height(), IPL_DEPTH_8U, 1);

        com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//        com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()

        faceRecognizer.train(images, labels);

        cvCvtColor(testImage, greyTestImage, CV_BGR2GRAY);

        int predictedLabel = faceRecognizer.predict(greyTestImage);

        System.out.println("Predicted label: " + labelsString[predictedLabel]);
    }
}