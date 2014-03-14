package mauroponce.pfi.tests;

import mauroponce.pfi.recognition.*;

public class RecognitionTest {
//	static String testImagePath = "C:/Users/Mauro/Documents/PFI/test/moralito.jpg";
//	static String testImagePath = "C:/Users/smoral/Documents/PFI/faces/UADE_Santiago_Moral_.jpg";
	static String testImagePath = "C:/Users/smoral/Documents/PFI/faces/131385/1394600670758.jpg";
	
	public static void main(String[] args) {
		int k = 3;//number of nearest neighbors
		FaceRecognizer recognitionService = new FaceRecognizer();
//		String [] imagesFolders = {"131445", "131900", "131455", "131431"};
		String courseFolderName = "course_4";
		recognitionService.learn(courseFolderName);
		recognitionService.recognize(testImagePath, k);
		
		System.out.println("----------------------- FISHER FACES ------------------------");
		
		FaceRecognizerFisher faceRecognizerFisher = new FaceRecognizerFisher();
		faceRecognizerFisher.fisherFacesRecognition(courseFolderName, testImagePath);
	}	
}
