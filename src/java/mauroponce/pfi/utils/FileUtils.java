package mauroponce.pfi.utils;

import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FileUtils {
	
	public static String fileToString(String path){
	  FileInputStream stream = null;
	  String returnString = null;
		try {
			stream = new FileInputStream(new File(path));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			returnString =  Charset.defaultCharset().decode(bb).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnString;
	}  
	
    public static void writeToFile(String pFilename, StringBuffer pData) throws IOException {  
        BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
        out.write(pData.toString());  
        out.flush();  
        out.close();  
    }  
    
    public static void appendToFile(String pFilename, StringBuffer pData) throws IOException {  
    	BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
    	out.append(pData.toString());  
    	out.flush();  
    	out.close();  
    }  
	
	public static String encodeFileBase64(String inputPath){
		File file = new File(inputPath);
		byte[] fileData = new byte[(int) file.length()];
        try {
        	FileInputStream fileInputStream = new FileInputStream(file);
	        fileInputStream.read(fileData);
	        fileInputStream.close();	        	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(fileData);
	}
	public static void decodeFileBase64(String base64String, String outputPath) {
        byte[] fileData = Base64.decodeBase64(base64String);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
			fileOutputStream.write(fileData);	        
	        fileOutputStream.close();    
		} catch (Exception e) {
			e.printStackTrace();
		}            
    }
	public static byte[] decodeFileBase64(String base64String){
		return Base64.decodeBase64(base64String);
	}
	public static IplImage convertFloatImageToUcharImage(
			final IplImage sourceIplImage) {
		IplImage destinationIplImage;
		if ((sourceIplImage != null)
				&& (sourceIplImage.width() > 0 && sourceIplImage.height() > 0)) {
			// Spread the 32bit floating point pixels to fit within 8bit pixel
			// range.
			final CvPoint minloc = new CvPoint();
			final CvPoint maxloc = new CvPoint();
			final double[] minVal = new double[1];
			final double[] maxVal = new double[1];
			cvMinMaxLoc(sourceIplImage, minVal, maxVal, minloc, maxloc, null);
			// Deal with NaN and extreme values, since the DFT seems to give
			// some NaN results.
			if (minVal[0] < -1e30) {
				minVal[0] = -1e30;
			}
			if (maxVal[0] > 1e30) {
				maxVal[0] = 1e30;
			}
			if (maxVal[0] - minVal[0] == 0.0f) {
				maxVal[0] = minVal[0] + 0.001; // remove potential divide by
												// zero errors.
			} // Convert the format
			destinationIplImage = cvCreateImage(
					cvSize(sourceIplImage.width(), sourceIplImage.height()), 8,
					1);
			cvConvertScale(sourceIplImage, destinationIplImage,
					255.0 / (maxVal[0] - minVal[0]), -minVal[0] * 255.0
							/ (maxVal[0] - minVal[0]));
			return destinationIplImage;
		}
		return null;
	}

	public static void DeleteFile(String filePathToDelete) {
		File fileToDelete = new File(filePathToDelete);
		fileToDelete.delete();
	}
}
