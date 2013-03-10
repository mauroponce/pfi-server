package mauroponce.pfi.tests;
import mauroponce.pfi.recognition.FaceRecognizer;

public class RecognitionTest {
	static String testImagePath = "C:/Users/Mauro/Documents/PFI/test/moralito.jpg";
	
	public static void main(String[] args) {
		int k = 3;//number of nearest neighbors
		FaceRecognizer recognitionService = new FaceRecognizer();
		String [] imagesFolders = {"131445", "131900", "131455"};
		imagesFolders[0] = String.valueOf(131445);
		recognitionService.learn(imagesFolders);
		recognitionService.recognize(testImagePath, k);
	}	
}
