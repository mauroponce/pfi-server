package poncemoral.pfi.recognition;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
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
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_legacy.CV_EIGOBJ_NO_CALLBACK;
import static com.googlecode.javacv.cpp.opencv_legacy.cvCalcEigenObjects;
import static com.googlecode.javacv.cpp.opencv_legacy.cvEigenDecomposite;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import poncemoral.pfi.domain.IndexDistance;
import poncemoral.pfi.domain.Student;
import poncemoral.pfi.utils.AppConstants;
import poncemoral.pfi.utils.FileUtils;
import poncemoral.pfi.utils.ImageUtils;


import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FaceRecognizer {
	
	IplImage[] trainingFaceImgArr;
	private int nTrainFaces = 0;
	CvMat projectedTrainFaceMat;
	int nEigens = 0;
	int nPersons = 0;
	IplImage[] eigenVectArr;
	CvMat eigenValMat;
	IplImage pAvgTrainImg;
	final List<String> personNames = new ArrayList<String>();
	CvMat personNumTruthMat;
	IplImage[] testFaceImgArr;
	CvMat trainPersonNumMat;

	/**
	 * If students is empty useas all the images into the folders of studentsFolder, else use only the images for the students 
	 * @param studentsFolder
	 * @param students
	 */
	public void learn(final String studentsFolder, List<Student> students){
		//Call garbage collector to release file facesdata.xml, cvReleaseFileStorage does not work
		System.gc();
		trainingFaceImgArr = loadFaceImgArrayFromImagesFolders(studentsFolder, students);
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
		System.gc();
	}	
	
	private IplImage[] loadFaceImgArrayFromImagesFolders(
			final String studentsFolder, List<Student> students) {
		
		IplImage[] faceImgArr;
		
		int iFace = 0;
		int nFaces = 0;

		nPersons = 0;
		
		List<File> imageFolderFiles = new ArrayList<File>();
		if (students != null){
			nFaces = prepareImageFolders(studentsFolder, nFaces, imageFolderFiles, students);			
		}else{
			nFaces = prepareImageFolders(studentsFolder, nFaces, imageFolderFiles);			
		}
		
		
		/*for (final File personDir : topDir.listFiles()) {
			if (personDir.isDirectory()) {
				final File imagesDir = new File(imagesFolderPath + "/"
						+ personDir.getName());
				nFaces += imagesDir.listFiles().length;
			}
		}*/

		// allocate the face-image array and person number matrix
		faceImgArr = new IplImage[nFaces];
		personNumTruthMat = cvCreateMat(1, // rows
				nFaces, // cols
				CV_32SC1); // type, 32-bit unsigned, one channel

		// initialize the person number matrix - for ease of debugging
		for (int j1 = 0; j1 < nFaces; j1++) {
			personNumTruthMat.put(0, j1, 0);
		}

		personNames.clear(); // Make sure it starts as empty.

		for(File imageFolderFile : imageFolderFiles){
			int personNumber = 0;
			if(imageFolderFile.isDirectory()){
				nPersons++;
				String personName = imageFolderFile.getName();
				personNumber = Integer.valueOf(personName);
				personNames.add(personName);
				
				for (final File imgFile : imageFolderFile.listFiles()) {
					String imgFilePath = imgFile.getAbsolutePath();
					personNumTruthMat.put(0, iFace, personNumber);
										
					IplImage faceDetectedImage 
					= cvLoadImage(imgFilePath, CV_LOAD_IMAGE_GRAYSCALE);
					if (faceDetectedImage == null) {
						throw new RuntimeException("No se puede leer la imagen "
								+ imgFilePath);
					}
					
					faceImgArr[iFace] = faceDetectedImage;
					iFace++;
				}
			}
		}

		return faceImgArr;
	}

	private int prepareImageFolders(String studentsFolder, int nFaces,
			List<File> imageFolderFiles, List<Student> students) {
		for (Student student : students) {
			File studentFolderFile = new File(studentsFolder+File.separator+student.getLU());			
			if(studentFolderFile.isDirectory()){
				nFaces += studentFolderFile.listFiles().length;
				imageFolderFiles.add(studentFolderFile);
			}			
		}
		
		return nFaces;
	}

	private int prepareImageFolders(final String studentsFolder, int nFaces,
			List<File> imageFolderFiles) {
		File courseFolderFile = new File(studentsFolder);

		if(courseFolderFile.isDirectory()){
			for (File imageFolderFile : courseFolderFile.listFiles()) {
				if(imageFolderFile.isDirectory()){
					nFaces += imageFolderFile.listFiles().length;
				}
				imageFolderFiles.add(imageFolderFile);
			} 
		}
		return nFaces;
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
		fileStorage = cvOpenFileStorage(AppConstants.FACEDATA_PATH,
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
	
	public String createFacesData(final String studentFolder, List<Student> students){
		learn(studentFolder, students);
		return FileUtils.fileToString(AppConstants.FACEDATA_PATH);
	}
	
	private void storeEigenfaceImages() {
		cvSaveImage(AppConstants.AVERAGE_IMAGE_PATH, pAvgTrainImg);
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
			cvSaveImage(AppConstants.DATA_PATH + "/eigenfaces.bmp", bigImg);
			cvReleaseImage(bigImg);
		}
	}
	
	/**Finds the k most similar faces.*/
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
		fileStorage = cvOpenFileStorage(AppConstants.FACEDATA_PATH, // filename
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
	
	private IplImage[] loadFaceImgArrayFromImagePath(final String imagePath) {
		IplImage[] faceImgArr;
		int iFace = 0;
		int nFaces = 1;
		int width = -1;
		int height = -1;
		nPersons = 0;

		faceImgArr = new IplImage[nFaces];
		personNumTruthMat = cvCreateMat(1, // rows
				nFaces, // cols
				CV_32SC1); // type, 32-bit unsigned, one channel

		for (int j1 = 0; j1 < nFaces; j1++) {
			personNumTruthMat.put(0, j1, 0);
		}
		nPersons = 1;
		int personNumber = 0;
		personNumTruthMat.put(0, // i
				iFace, // j
				personNumber); // v
		final IplImage faceImage = cvLoadImage(imagePath, // filename
				CV_LOAD_IMAGE_GRAYSCALE); // isColor
		if (faceImage == null) {
			throw new RuntimeException("Can't load image from " + imagePath);
		}
		if (width == -1) {
			width = faceImage.width();
			height = faceImage.height();
		} else if (faceImage.width() != width || faceImage.height() != height) {
			throw new RuntimeException("wrong size face in " + imagePath
					+ "\nwanted " + width + "x" + height + ", but found "
					+ faceImage.width() + "x" + faceImage.height());
		}
		
		// Give the image a standard brightness and contrast.
		cvEqualizeHist(faceImage, faceImage);
		
		faceImgArr[iFace] = faceImage;
		iFace++;		
		return faceImgArr;
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
		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq
				/ (float) (nTrainFaces * nEigens)) / 255.0f);
		pConfidencePointer.put(pConfidence);
		
		return nNearestIndexes;
	}
}
