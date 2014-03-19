package mauroponce.pfi.recognition;
import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

public class FaceRecognizerFisher extends FaceRecognizer {
	com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;
	IplImage greyTestImage;
	MatVector images;
	int[] labels;
	IplImage testImage;


	@Override
	public void learn(String courseFolder) {
		// In the following method the images an do students names were set
		IplImage[] imageFolderFiles = loadFaceImgArrayFromImagesFolders(courseFolder);		
		
        images = new MatVector(imageFolderFiles.length);

        labels = new int[imageFolderFiles.length];

        int counter = 0;

        

        for (IplImage image : imageFolderFiles) {
            images.put(counter, image);

            labels[counter] = Integer.valueOf(personNamesWithRepetitions.get(counter));

            counter++;
        }

        

        faceRecognizer = createFisherFaceRecognizer();

        faceRecognizer.train(images, labels);	
	}



	@Override
	public ArrayList<Integer> recognize(String testImagePath, int k) {
		ArrayList<Integer> nearestsLus = new ArrayList<Integer>();
		
		testImage = loadFaceImgArrayFromImagePath(testImagePath)[0];

        int[] personsPredicted = new int[k];
		double[] arg2 = new double[k];
		faceRecognizer.predict(testImage, personsPredicted, arg2);

//        System.out.println("Predicted label: " + personsPredicted[0]);
        
        for (int personPredicted : personsPredicted) {
        	nearestsLus.add(personPredicted);
		}
		return nearestsLus;
	}
}

