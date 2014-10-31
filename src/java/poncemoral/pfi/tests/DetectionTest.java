package poncemoral.pfi.tests;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import poncemoral.pfi.recognition.FaceDetection;


/***
 * http://blog.csdn.net/ljsspace/article/details/6664011
 */
public class DetectionTest {
	public static void main(String arg[]) throws Exception {
//		String fileInputPath = "C:\\Users\\smoral\\Pictures\\Fotos\\Dsc00664.jpg";
		

		File fotosFolder = new File("C:/Users/smoral/Documents/PFI/fotos");
		
		for (File studentFolder : fotosFolder.listFiles()) {
			if (studentFolder.isDirectory()){
				for (File studentImg : studentFolder.listFiles()) {
					if (!studentImg.isDirectory()){
						if(studentImg.getName().equals("95.jpg")||studentImg.getName().equals("96.jpg")){
							FaceDetection.detectMultipleFaces(studentImg.getAbsolutePath(),
									"haarcascade_frontalface_alt.xml",
									studentFolder.getAbsolutePath() + "/detected/" + "detected_"+new Date().getTime()); 
						}
					}
				}
			}
		}
		
//		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_alt2.xml");
//		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_alt_tree.xml");
//		FaceDetection.detectFaces(fileInputPath, "haarcascade_frontalface_default.xml");
	}

}