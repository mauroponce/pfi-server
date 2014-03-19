package mauroponce.pfi.recognition;

import java.util.ArrayList;

public interface IFaceRecognizer {

	public void learn(final String courseFolder);

	/**Finds the k most similar faces.*/
	public ArrayList<Integer> recognize(final String testImagePath,
			final int k);

}