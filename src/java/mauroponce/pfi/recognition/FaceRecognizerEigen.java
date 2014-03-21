package mauroponce.pfi.recognition;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_L1;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_READ;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_WRITE;
import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_ITER;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAttrList;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvNormalize;
import static com.googlecode.javacv.cpp.opencv_core.cvOpenFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvReadByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadIntByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadStringByName;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvTermCriteria;
import static com.googlecode.javacv.cpp.opencv_core.cvWrite;
import static com.googlecode.javacv.cpp.opencv_core.cvWriteInt;
import static com.googlecode.javacv.cpp.opencv_core.cvWriteString;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_legacy.CV_EIGOBJ_NO_CALLBACK;
import static com.googlecode.javacv.cpp.opencv_legacy.cvCalcEigenObjects;
import static com.googlecode.javacv.cpp.opencv_legacy.cvEigenDecomposite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import mauroponce.pfi.domain.IndexDistance;
import mauroponce.pfi.utils.FileUtils;

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FaceRecognizerEigen extends FaceRecognizer {
	
//	final String DATA_PATH = "C:/Users/Mauro/Desktop/data";
	public final static String DATA_PATH = "C:/Users/smoral/Documents/PFI/data";
	IplImage[] trainingFaceImgArr;
	private int nTrainFaces = 0;
	CvMat projectedTrainFaceMat;
	int nEigens = 0;
	IplImage[] eigenVectArr;
	CvMat eigenValMat;
	IplImage pAvgTrainImg;
	IplImage[] testFaceImgArr;
	CvMat trainPersonNumMat;

	/* (non-Javadoc)
	 * @see mauroponce.pfi.recognition.IFaceRecognizer#learn(java.lang.String)
	 */
	public void learn(final String courseFolder){
		trainingFaceImgArr = loadFaceImgArrayFromImagesFolders(courseFolder);
		nTrainFaces = trainingFaceImgArr.length;
		if (nTrainFaces < 3) {//Se necesitan al menos 3 imagenes de entrenamiento
			return;
		}
		doPCA();
		projectedTrainFaceMat = cvCreateMat(nTrainFaces, // rows
				nEigens, // cols
				CV_32FC1); // type, 32-bit float, 1 channel

		for (int i1 = 0; i1 < nTrainFaces; i1++) {
			for (int j1 = 0; j1 < nEigens; j1++) {
				projectedTrainFaceMat.put(i1, j1, 0.0);
			}
		}
		final FloatPointer floatPointer = new FloatPointer(nEigens);
		for (int i = 0; i < nTrainFaces; i++) {
			cvEigenDecomposite(trainingFaceImgArr[i],
					nEigens,
					new PointerPointer(eigenVectArr),
					0,
					null,
					pAvgTrainImg,
					floatPointer);
			for (int j1 = 0; j1 < nEigens; j1++) {
				projectedTrainFaceMat.put(i, j1, floatPointer.get(j1));
			}
		}
		storeTrainingData();
		storeEigenfaceImages();
	}	
	
	private void doPCA() {
		int i;
		CvTermCriteria calcLimit;
		CvSize faceImgSize = new CvSize();

		nEigens = nTrainFaces - 1;
		// System.out.println("Eigenvalues: " + nEigens);
		
		faceImgSize.width(trainingFaceImgArr[0].width());
		faceImgSize.height(trainingFaceImgArr[0].height());
		eigenVectArr = new IplImage[nEigens];
		for (i = 0; i < nEigens; i++) {
			eigenVectArr[i] = cvCreateImage(faceImgSize, // size
					IPL_DEPTH_32F, // depth
					1); // channels
		}

		// allocate the eigenvalue array
		eigenValMat = cvCreateMat(1, // rows
				nEigens, // cols
				CV_32FC1); // type, 32-bit float, 1 channel

		// allocate the averaged image
		pAvgTrainImg = cvCreateImage(faceImgSize, // size
				IPL_DEPTH_32F, // depth
				1); // channels

		// set the PCA termination criterion
		calcLimit = cvTermCriteria(CV_TERMCRIT_ITER, // type
				nEigens, // max_iter
				1); // epsilon

		cvCalcEigenObjects(nTrainFaces, // nObjects
				new PointerPointer(trainingFaceImgArr), // input
				new PointerPointer(eigenVectArr), // output
				CV_EIGOBJ_NO_CALLBACK, // ioFlags
				0, // ioBufSize
				null, // userData
				calcLimit, pAvgTrainImg, // avg
				eigenValMat.data_fl()); // eigVals
		
		cvNormalize(eigenValMat, // src (CvArr)
				eigenValMat, // dst (CvArr)
				1, // a
				0, // b
				CV_L1, // norm_type
				null); // mask
	}

	private void storeTrainingData() {
		CvFileStorage fileStorage;
		fileStorage = cvOpenFileStorage(DATA_PATH + "/facedata.xml",
				null,
				CV_STORAGE_WRITE,
				null);

		cvWriteInt(fileStorage, "nPersons", nPersons);

		for (int i = 0; i < nPersons; i++) {
			String varname = "personName_" + (i + 1);
			cvWriteString(fileStorage,
					varname,
					personNames.get(i),
					0);
		}
		cvWriteInt(fileStorage, "nEigens", nEigens);
		cvWriteInt(fileStorage, "nTrainFaces", nTrainFaces);
		cvWrite(fileStorage, "trainPersonNumMat", personNumTruthMat, cvAttrList());
		cvWrite(fileStorage, "eigenValMat", eigenValMat, cvAttrList());
		cvWrite(fileStorage, "projectedTrainFaceMat", projectedTrainFaceMat, cvAttrList());
		cvWrite(fileStorage, "avgTrainImg", pAvgTrainImg, cvAttrList());

		for (int i = 0; i < nEigens; i++) {
			String varname = "eigenVect_" + i;
			cvWrite(fileStorage,
					varname,
					eigenVectArr[i],
					cvAttrList());
		}
		cvReleaseFileStorage(fileStorage);
	}
	
	public String getFacesData(final String courseFolder){
		learn(courseFolder);
		return FileUtils.fileToString(DATA_PATH + "/facedata.xml");
	}
	
	private void storeEigenfaceImages() {
		cvSaveImage(DATA_PATH + "/averageImage.bmp", pAvgTrainImg);
		if (nEigens > 0) {
			int COLUMNS = 8;
			int nCols = Math.min(nEigens, COLUMNS);
			int nRows = 1 + (nEigens / COLUMNS);
			int w = eigenVectArr[0].width();
			int h = eigenVectArr[0].height();
			CvSize size = cvSize(nCols * w, nRows * h);
			final IplImage bigImg = cvCreateImage(size, IPL_DEPTH_8U, 1);
			for (int i = 0; i < nEigens; i++) {
				IplImage byteImg = FileUtils.convertFloatImageToUcharImage(eigenVectArr[i]);
				int x = w * (i % COLUMNS);
				int y = h * (i / COLUMNS);
				CvRect ROI = cvRect(x, y, w, h);
				cvSetImageROI(bigImg, ROI);
				cvCopy(byteImg, bigImg, null);
				cvResetImageROI(bigImg);
				cvReleaseImage(byteImg);
			}
			cvSaveImage(DATA_PATH + "/eigenfaces.bmp", bigImg);
			cvReleaseImage(bigImg);
		}
	}
	
	/* (non-Javadoc)
	 * @see mauroponce.pfi.recognition.IFaceRecognizer#recognize(java.lang.String, int)
	 */
	public ArrayList<Integer> recognize(final String testImagePath, final int k) {
		int i = 0;
		int nTestFaces = 0;
		float[] projectedTestFace;
		float confidence = 0.0f;
		ArrayList<Integer> nearestsLus = null;
		
		
		
		testFaceImgArr = loadFaceImgArrayFromImagePath(testImagePath);
		nTestFaces = testFaceImgArr.length;
		trainPersonNumMat = loadTrainingData();
		if (trainPersonNumMat == null) {
			return nearestsLus;
		}
		projectedTestFace = new float[nEigens];

		for (i = 0; i < nTestFaces; i++) {
			int nearest;

			cvEigenDecomposite(testFaceImgArr[i], // obj
					nEigens, // nEigObjs
					new PointerPointer(eigenVectArr), // eigInput (Pointer)
					0, // ioFlags
					null, // userData
					pAvgTrainImg, // avg
					projectedTestFace); // coeffs

			final FloatPointer pConfidence = new FloatPointer(confidence);
			int [] knn = getKNN(projectedTestFace, new FloatPointer(
					pConfidence), k);
			
			confidence = pConfidence.get();
			nearest = knn[0];
			nearestsLus = new ArrayList<Integer>();
			for(int j = 0 ; j < knn.length; j++){
				nearestsLus.add(knn[j]);
			}
			// System.out.println("Mas cercano: " + nearest);
		}
		
		return nearestsLus; 
	}

	private CvMat loadTrainingData() {
		CvMat pTrainPersonNumMat = null; // the person numbers during training
		CvFileStorage fileStorage;
		int i;
		fileStorage = cvOpenFileStorage(DATA_PATH + "/facedata.xml", // filename
				null, // memstorage
				CV_STORAGE_READ, // flags
				null); // encoding
		if (fileStorage == null) {
			return null;
		}

		personNames.clear(); // Make sure it starts as empty.
		nPersons = cvReadIntByName(fileStorage, // fs
				null, // map
				"nPersons", // name
				0); // default_value
		if (nPersons == 0) {
			return null;
		}
		for (i = 0; i < nPersons; i++) {
			String sPersonName;
			String varname = "personName_" + (i + 1);
			sPersonName = cvReadStringByName(fileStorage, // fs
					null, // map
					varname, "");
			personNames.add(sPersonName);
		}
		nEigens = cvReadIntByName(fileStorage, // fs
				null, // map
				"nEigens", 0); // default_value
		nTrainFaces = cvReadIntByName(fileStorage, null, // map
				"nTrainFaces", 0); // default_value
		Pointer pointer = cvReadByName(fileStorage, // fs
				null, // map
				"trainPersonNumMat", // name
				cvAttrList()); // attributes
		pTrainPersonNumMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, // fs
				null, // map
				"eigenValMat", // nmae
				cvAttrList()); // attributes
		eigenValMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, // fs
				null, // map
				"projectedTrainFaceMat", // name
				cvAttrList()); // attributes
		projectedTrainFaceMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null, // map
				"avgTrainImg", cvAttrList()); // attributes
		pAvgTrainImg = new IplImage(pointer);

		eigenVectArr = new IplImage[nEigens];
		for (i = 0; i < nEigens; i++) {
			String varname = "eigenVect_" + i;
			pointer = cvReadByName(fileStorage, null, // map
					varname, cvAttrList()); // attributes
			eigenVectArr[i] = new IplImage(pointer);
		}
		cvReleaseFileStorage(fileStorage);
		
		return pTrainPersonNumMat;
	}
	
	/**Get the K nearest neighbors.*/
	private int [] getKNN(float projectedTestFace[],
			FloatPointer pConfidencePointer, int k) {
		double leastDistSq = Double.MAX_VALUE;
		int i = 0;
		int iNearest = 0;
		int [] nNearestIndexes = new int[k];
		Set<IndexDistance> indexDistances = new TreeSet<IndexDistance>();
		double [] distances = new double [nTrainFaces];
		
		for (int iTrain = 0; iTrain < nTrainFaces; iTrain++) {
			double distSq = 0;

			for (i = 0; i < nEigens; i++) {
				if (!Double.isNaN(projectedTestFace[i])) {
					float projectedTrainFaceDistance = (float) projectedTrainFaceMat
							.get(iTrain, i);
					float d_i = projectedTestFace[i]
							- projectedTrainFaceDistance;
					distSq += d_i * d_i;
				}
			}

			if (distSq < leastDistSq) {
				leastDistSq = distSq;
				iNearest = iTrain;
			}
			distances[iTrain] = distSq;
			indexDistances.add(new IndexDistance(iTrain, distSq));
			// System.out.println("iTrain: " + iTrain + ", distance: " + distSq);
		}
		// System.out.println("Least: " + iNearest + ", Distance: "+ leastDistSq);
		// System.out.println("\n\nTreeSet\n\n");
		
		for(IndexDistance indexDistance : indexDistances){
			// System.out.println("Index: " + indexDistance.getIndex()
					//+ ", Distance: " + indexDistance.getDistance());
		}
		nNearestIndexes = getKNearestIndexes(k, indexDistances);
		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq
				/ (float) (nTrainFaces * nEigens)) / 255.0f);
		pConfidencePointer.put(pConfidence);
		
		return nNearestIndexes;
	}

	private int[] getKNearestIndexes(int k, Set<IndexDistance> indexDistances) {
		int [] nNearestIndexes = new int[k];
		int j = 0;
		for(Iterator<IndexDistance> it = indexDistances.iterator(); it.hasNext() && j < k; j++){
			boolean luWasAdded = false;
			int luToAdd = trainPersonNumMat.data_i().get(it.next().getIndex());
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

