package mauroponce.pfi.tests;

import mauroponce.pfi.recognition.FaceDetection;

/***
 * http://blog.csdn.net/ljsspace/article/details/6664011
 */
public class DetectionTest {
	public static void main(String arg[]) throws Exception {
		String fileInputPath = "C:\\Users\\smoral\\Pictures\\Fotos\\All Boys de La Pampa (2000).jpg";
		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_alt.xml"); 
		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_alt2.xml");
//		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_alt_tree.xml");
//		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_default.xml");
	}

}