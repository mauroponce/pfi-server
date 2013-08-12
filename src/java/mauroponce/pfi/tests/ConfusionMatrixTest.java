package mauroponce.pfi.tests;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauroponce.pfi.recognition.FaceRecognizer;
import mauroponce.pfi.utils.AppConstants;

public class ConfusionMatrixTest {

	private static Map<Integer, Integer> lusIndex;
	private static Map<Integer, Integer> lusActualCount;
	private static Integer[][] confusionMatriz;
	private static Integer[][] confusionMatrizKNearest;
	private static FaceRecognizer recognitionService;
	private static Integer LEARNING_IMAGES_COUNT = 3;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		String recognitionCourseFolder = "a_reconocer";
		Integer k = getStudentsCount(recognitionCourseFolder);
		for (int t = 1; t <= LEARNING_IMAGES_COUNT; t++) {
			processFolders(t, recognitionCourseFolder, k);			
		}
	}

	private static Integer getStudentsCount(String recognitionCourseFolder) {
		 File recognitionFolder = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + recognitionCourseFolder);
		 Integer count = 0;
		 count = recognitionFolder.list().length;
		 return count;		
	}

	private static void processFolders(Integer t,
			String recognitionCourseFolder, Integer k) {
		recognitionService = new FaceRecognizer();
		recognitionService.learn("course_"+t);
		System.out.println("\n\n PRUEBA T = "+t);
		for (int i = 1; i <= k; i++) {
			createConfusionMatriz(recognitionCourseFolder, i);
			System.out.println("\n--- Matriz de Confusion "+i+" mas cercanos---");
			printConfusionMatriz(confusionMatrizKNearest);
			System.out.println("\n--- Matriz de Confusion Porcentual "+i+" mas cercanos---");
			printProcentualConfusionMatriz(confusionMatrizKNearest);			
		}
	}

	private static void printConfusionMatriz(Integer[][] matriz) {
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
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				System.out.format("|%5s   ",luPredictedCount);
			}
			System.out.print("\n");		
		}		
	}
	
	private static void printProcentualConfusionMatriz(Integer[][] matriz) {
		System.out.print("      ");
		for (Integer lu : lusIndex.keySet()) {
			System.out.print("| "+lu+" ");			
		}		
		System.out.print("\n");	
		float performance = 0;
		for (Integer luActual : lusIndex.keySet()) {
			Integer luActualIndex = lusIndex.get(luActual);
			System.out.print(luActual);
			Integer luActualCount = lusActualCount.get(luActual);
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				float percentFloat = luPredictedCount/(float)luActualCount*100;
				String porcentString = new DecimalFormat("#").format(percentFloat);
				System.out.format("|%5s%%  ",porcentString);
				if (luActual == luPredicted){
					performance += percentFloat;
				}
			}
			System.out.print("\n");
		}		
		performance = performance/lusIndex.keySet().size();
		System.out.println("Performance: "+new DecimalFormat("#").format(performance));
	}

	private static void createConfusionMatriz(String recognitionCourseFolder, Integer k) {
		recognitionService = new FaceRecognizer();
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
					List<Integer> luNearest = recognitionService.recognize(imgPath, k);
					if (!luNearest.isEmpty()){
//						Integer luPredicted = luNearest.get(0);
//						Integer LuPredictedIndex = lusIndex.get(luPredicted);
//						//Add one in the confusion matriz for de actual and predicted index
//						confusionMatriz[luActualIndex][LuPredictedIndex]++;
						for (Integer luPredictedK: luNearest) {
							//Create the matriz for the K nearest
							Integer LuPredictedKIndex = lusIndex.get(luPredictedK);
							//Add one in the confusion matriz for de actual and predicted index
							confusionMatrizKNearest[luActualIndex][LuPredictedKIndex]++;							
						}
					}
				}
			 }			
			 lusActualCount.put(luActual,luActualCount);
		 }
	}

	private static void initializeConfusionMatriz(int luLength) {
		confusionMatriz = new Integer[luLength][luLength];
		confusionMatrizKNearest = new Integer[luLength][luLength];
		for (int i = 0; i < luLength; i++) {
			for (int j = 0; j < luLength; j++) {
				confusionMatriz[i][j] = 0;				
				confusionMatrizKNearest[i][j] = 0;				
			}			
		}
	}

}