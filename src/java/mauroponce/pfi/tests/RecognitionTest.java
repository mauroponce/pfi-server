package mauroponce.pfi.tests;

import java.util.ArrayList;

import mauroponce.pfi.recognition.*;

public class RecognitionTest {
//	static String testImagePath = "C:/Users/Mauro/Documents/PFI/test/moralito.jpg";
//	static String testImagePath = "C:/Users/smoral/Documents/PFI/faces/UADE_Santiago_Moral_.jpg";
	static String testImagePath = "C:/Users/smoral/Documents/PFI/faces/131385/1394600670758.jpg";
	
	public static void main(String[] args) {
		int k = 3;//number of nearest neighbors
		String courseFolderName = "course_1";
//		String [] imagesFolders = {"131445", "131900", "131455", "131431"};
		
		System.out.println("----------------------- EIGEN FACES ------------------------");
		IFaceRecognizer recognitionService = new FaceRecognizerEigen();
		recognitionService.learn(courseFolderName);
		ArrayList<Integer> recognizeEigen = recognitionService.recognize(testImagePath, k);
		System.out.println("Recognized: "+recognizeEigen.get(0));
		
		
		System.out.println("----------------------- FISHER FACES ------------------------");
		FaceRecognizerFisher faceRecognizerFisher = new FaceRecognizerFisher();
		faceRecognizerFisher.learn(courseFolderName);
		ArrayList<Integer> recognizeFisher = faceRecognizerFisher.recognize(testImagePath, k);
		System.out.println("Recognized: "+recognizeFisher.get(0));
	}	
}
