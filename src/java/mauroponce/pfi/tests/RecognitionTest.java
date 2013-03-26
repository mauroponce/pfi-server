package mauroponce.pfi.tests;

import mauroponce.pfi.recognition.*;

public class RecognitionTest {
//	static String testImagePath = "C:/Users/Mauro/Documents/PFI/test/moralito.jpg";
	static String testImagePath = "C:/Users/smoral/Documents/PFI/test/moralito.jpg";
	
	public static void main(String[] args) {
		int k = 4;//number of nearest neighbors
		FaceRecognizer recognitionService = new FaceRecognizer();
		String [] imagesFolders = {"131445", "131455", "131431", "131900"};
		recognitionService.learn(imagesFolders);
		recognitionService.recognize(testImagePath, k);
	}	
}
