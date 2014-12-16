package poncemoral.pfi.tests;

import java.io.File;


import org.junit.Assert;
import org.junit.Test;

import poncemoral.pfi.utils.FileUtils;

public class Base64Test {

	@Test
	public void testBase64EncodingAndDecoding(){
		String inputPathMauro = "data/131445.jpg";
		String inputPathCho = "data/131455.jpg";
		
		String encodedImageMauro = FileUtils.encodeFileBase64(inputPathMauro);
		String encodedImageCho = FileUtils.encodeFileBase64(inputPathCho);
		
		System.out.println("Mauro:");
		System.out.println(encodedImageMauro);
		System.out.println("Cho:");
		System.out.println(encodedImageCho);
		
		Assert.assertNotNull(encodedImageMauro);
		
		String outputPath = "data/131445_output.jpg";
		FileUtils.decodeFileBase64(encodedImageMauro, outputPath);
		File outputFile = new File(outputPath);
		Assert.assertNotNull(outputFile);
	}
	public static void main(String[] args) {
//		AttendanceService attendanceService = new AttendanceService();
//		String encodedImageBase64 = "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";
//		attendanceService.sendTrainingImage(131445, encodedImageBase64, "jpg");

		System.out.println(FileUtils.encodeFileBase64("C:/Users/smoral/Documents/PFI/faces/course_1/131431/ID_121077.jpg"));
	}
}