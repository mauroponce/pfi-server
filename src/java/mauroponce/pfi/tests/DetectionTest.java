package mauroponce.pfi.tests;

import java.io.File;
import java.util.Date;

import mauroponce.pfi.recognition.FaceDetection;

/***
 * http://blog.csdn.net/ljsspace/article/details/6664011
 */
public class DetectionTest {
	public static void main(String arg[]) throws Exception {
//		String fileInputPath = "C:\\Users\\smoral\\Pictures\\Fotos\\Dsc00664.jpg";
		

		File fotosFolder = new File("C:/Users/smoral/Documents/PFI/fotos");
		
		for (File studentFolder : fotosFolder.listFiles()) {
			if (studentFolder.isDirectory()){
				if (studentFolder.getName().equals("130032")
						|| studentFolder.getName().equals("130032")){
					String detectedStudentFolder = studentFolder.getAbsolutePath().replace("fotos", "fotos_a_agregar")+File.separator;
					File dir = new File(detectedStudentFolder);
					if (!dir.exists()){
						dir.mkdir();
					}
					for (File studentImg : studentFolder.listFiles()) {
						if (!studentImg.isDirectory()){
//							if(studentImg.getName().equals("aaa.jpg")){
								
								FaceDetection.detectOneFace(studentImg.getAbsolutePath(),
										"haarcascade_frontalface_alt.xml",
										detectedStudentFolder + "detected_" + new Date().getTime() + ".jpg"); 
//							}
								
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