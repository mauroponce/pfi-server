package mauroponce.pfi.tests;

import mauroponce.pfi.recognition.*;

public class RecognitionTest {
//	static String testImagePath = "C:/Users/Mauro/Documents/PFI/test/moralito.jpg";
	static String testImagePath = "C:/Users/smoral/Documents/PFI/faces/course_1/131900/1369949889431.jpg";
	
	public static void main(String[] args) {
		int k = 3;//number of nearest neighbors
		FaceRecognizer recognitionService = new FaceRecognizer();
//		String [] imagesFolders = {"131445", "131900", "131455", "131431"};
		String courseFolderName = "course_1";
		recognitionService.learn(courseFolderName);
		recognitionService.recognize(testImagePath, k);
	}	
}
