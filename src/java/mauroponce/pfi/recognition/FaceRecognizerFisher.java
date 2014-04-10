package mauroponce.pfi.recognition;
import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.subspaceProject;
import static com.googlecode.javacv.cpp.opencv_core.cvReshape;
import static com.googlecode.javacv.cpp.opencv_core.cvSVD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import mauroponce.pfi.domain.IndexDistance;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

public class FaceRecognizerFisher extends FaceRecognizer {
	com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;
	IplImage greyTestImage;
	MatVector images;
	int[] labels;
	IplImage testImage;


	@Override
	public void learn(String courseFolder) {
		// In the following method the images an do students names were set
		IplImage[] imageFolderFiles = loadFaceImgArrayFromImagesFolders(courseFolder);		
		
        images = new MatVector(imageFolderFiles.length);

        labels = new int[imageFolderFiles.length];

        int counter = 0;

        for (IplImage image : imageFolderFiles) {
            images.put(counter, image);

            labels[counter] = Integer.valueOf(personNamesWithRepetitions.get(counter));

            counter++;
        }
     

        faceRecognizer = createFisherFaceRecognizer();

        faceRecognizer.train(images, labels);	
	}
	
	public ArrayList<Integer> recognize(String testImagePath, int k) {
		ArrayList<Integer> nearestsLus = new ArrayList<Integer>();
		testImage = loadFaceImgArrayFromImagePath(testImagePath)[0];

        int[] personsPredicted = new int[k];
		double[] arg2 = new double[k];
		faceRecognizer.predict(testImage, personsPredicted, arg2);
		
		for (int personPredicted : personsPredicted) {
			nearestsLus.add(personPredicted);
		}
		
		return nearestsLus;
	}

//	@Override
//	public ArrayList<Integer> recognize(String testImagePath, int k) {
//		ArrayList<Integer> nearestsLus = new ArrayList<Integer>();
//		testImage = loadFaceImgArrayFromImagePath(testImagePath)[0];
//
////        int[] personsPredicted = new int[k];
////		double[] arg2 = new double[k];
////		faceRecognizer.predict(testImage, personsPredicted, arg2);
////		StringVector a = new StringVector();
////		String bbb = new String();
////		faceRecognizer.getParams(a);
////		
////		for (int i = 0; i<a.size();i++){
////			System.out.println(a.get(i));			
////		}
////        System.out.println("Predicted label: " + personsPredicted[0]);
//        
//		// https://code.google.com/p/javacv/source/browse/src/main/java/com/googlecode/javacv/cpp/opencv_contrib.java?r=a1f204f9cf107e00b556a235ede8552e4cbd1f9c&spec=svn31e4015a2349905bbe877fb7c57e68a9a5c7b61d
//		MatVector projections = faceRecognizer.getMatVector("projections");
//		CvMat mean = faceRecognizer.getMat("mean");
//		CvMat eigenvectors = faceRecognizer.getMat("eigenvectors");
//		CvMat labels = faceRecognizer.getMat("labels");
//		Double threshold = faceRecognizer.getDouble("threshold");
//		
////		// project into LDA subspace
////	    Mat q = subspaceProject(_eigenvectors, _mean, src.reshape(1,1));
//		CvMat rowHeader = new CvMat();
//		CvMat row = new CvMat();
//		// http://elar-framework.googlecode.com/svn-history/r1/trunk/src/com/inca/algorithms/KNearestNRecg.java
//		row = cvReshape(testImage, rowHeader, 1, 1);
//		CvMat q = subspaceProject(eigenvectors, mean, row);
//
//		Set<IndexDistance> indexDistances = new TreeSet<IndexDistance>();
////	    // find 1-nearest neighbor
//	    Double minDist = Double.MAX_VALUE;
//	    Double minClass = -1D;
//	    for(int i = 0; i < projections.size(); i++) {
//	        double dist = normNuestro(projections.getCvMat(i), 2D , q);
////	        double dist2 = normNuestro(projections.getCvMat(i), 2D, q);
////	        if((dist < minDist) && (dist < threshold)) {
////	            minDist = dist;
////	            minClass = labels.get(i);
////	        }
//	        minClass = labels.get(i);
//			indexDistances.add(new IndexDistance(minClass.intValue(), dist));
//	    }
//	    
////		for (int personPredicted : personsPredicted) {
////			nearestsLus.add(personPredicted);
////		}
//		
//		int[] knn = getKNearestIndexes(k, indexDistances);
//		nearestsLus = new ArrayList<Integer>();
//		for(int j = 0 ; j < knn.length; j++){
//			nearestsLus.add(knn[j]);
//		}
//		return nearestsLus;
//	}
	
	private static double normNuestro(CvMat A, double p, CvMat W) {
        double norm = -1;

        if (p == 1.0) {
            int cols = A.cols(), rows = A.rows();
            for (int j = 0; j < cols; j++) {
                double n = 0;
                for (int i = 0; i < rows; i++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else if (p == 2.0) {
            int size = Math.min(A.rows(), A.cols());
            if (W == null || W.rows() != size || W.cols() != 1) {
                W = CvMat.create(size, 1);
            }
            cvSVD(A, W, null, null, 0);
            norm = W.get(0); // largest singular value
        } else if (p == Double.POSITIVE_INFINITY) {
            int rows = A.rows(), cols = A.cols();
            for (int i = 0; i < rows; i++) {
                double n = 0;
                for (int j = 0; j < cols; j++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else {
            assert(false);
        }
        return norm;
    }
	
	private int[] getKNearestIndexes(int k, Set<IndexDistance> indexDistances) {
		int [] nNearestIndexes = new int[k];
		int j = 0;
		for(Iterator<IndexDistance> it = indexDistances.iterator(); it.hasNext() && j < k; j++){
			boolean luWasAdded = false;
			int luToAdd = it.next().getIndex();
			for (int luAdded : nNearestIndexes) {
				if (luAdded == luToAdd){
					luWasAdded = true;
					break;
				}
			}
			if (!luWasAdded){
				nNearestIndexes[j] = luToAdd;
			}else{
				j--;
			}
		}
		return nNearestIndexes;
	}
}

