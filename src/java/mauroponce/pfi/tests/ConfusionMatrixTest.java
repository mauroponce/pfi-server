package mauroponce.pfi.tests;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauroponce.pfi.recognition.FaceRecognizer;
import mauroponce.pfi.utils.AppConstants;

public class ConfusionMatrixTest {

	private static Map<Integer, Integer> lusIndex;
	private static Map<Integer, Integer> lusActualCount;
	private static Integer[][] confusionMatriz;
	private static FaceRecognizer recognitionService;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		recognitionService = new FaceRecognizer();
		String learnCourseFolder = "course_1";
		String recognitionCourseFolder = "course_2";
		recognitionService.learn(learnCourseFolder);
		createConfusionMatriz(recognitionCourseFolder);	
		printConfusionMatriz();
	}

	private static void printConfusionMatriz() {
		System.out.print("      ");
		for (Integer lu : lusIndex.keySet()) {
			System.out.print("| "+lu+" ");			
		}		
		System.out.print("\n");		
		for (Integer luActual : lusIndex.keySet()) {
			Integer luActualIndex = lusIndex.get(luActual);
			System.out.print(luActual);
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = confusionMatriz[luActualIndex][luPredictedIndex];
				System.out.print("|   "+luPredictedCount+"    ");
			}
			System.out.print("\n");		
		}		
	}

	private static void createConfusionMatriz(String recognitionCourseFolder) {
		 lusIndex = new HashMap<Integer, Integer>();
		 lusActualCount = new HashMap<Integer, Integer>();
		 File courseFolder = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + recognitionCourseFolder);
		 int luLength = courseFolder.listFiles().length;
		 initializeConfusionMatriz(luLength);
		 
		 /**
		  * First gets all the lus and assign an index to them
		  */
		 Integer index = 0; 
		 for (File studentFolder : courseFolder.listFiles()) {
			Integer luActual = Integer.parseInt(studentFolder.getName());
			lusIndex.put(luActual,index);			
			index++;
		 }
		 
		 for (File studentFolder : courseFolder.listFiles()) {
			 Integer luActual = Integer.parseInt(studentFolder.getName());
			 Integer luActualIndex = lusIndex.get(luActual);
			 Integer luActualCount = 0;
			 for (File img : studentFolder.listFiles()) {
				String[] nameParts = img.getName().split("_");
				if (!"UADE".equals(nameParts[0])){
					luActualCount++;
					String imgPath = img.getAbsolutePath();
					List<Integer> luNearest = recognitionService.recognize(imgPath, 1);
					if (!luNearest.isEmpty()){
						Integer luPredicted = luNearest.get(0);
						Integer LuPredictedIndex = lusIndex.get(luPredicted);
						//Add one in the confusion matriz for de actual and predicted index
						confusionMatriz[luActualIndex][LuPredictedIndex]++;
					}
				}
			 }			
			 lusActualCount.put(luActual,luActualCount);
		 }
	}

	private static void initializeConfusionMatriz(int luLength) {
		confusionMatriz = new Integer[luLength][luLength];
		for (int i = 0; i < luLength; i++) {
			for (int j = 0; j < luLength; j++) {
				confusionMatriz[i][j] = 0;				
			}			
		}
	}

}
