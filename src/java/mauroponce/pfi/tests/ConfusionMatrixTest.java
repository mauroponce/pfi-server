package mauroponce.pfi.tests;

import java.io.File;
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
	private static Integer k = 3;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String recognitionCourseFolder = "a_reconocer";
		processFolders("course_1", recognitionCourseFolder);
		processFolders("course_2", recognitionCourseFolder);
		processFolders("course_3", recognitionCourseFolder);
	}

	private static void processFolders(String learnCourseFolder,
			String recognitionCourseFolder) {
		recognitionService = new FaceRecognizer();
		recognitionService.learn(learnCourseFolder);
		createConfusionMatriz(recognitionCourseFolder);	
		System.out.println("\n--- Matriz de Confusi�n ---");
		printConfusionMatriz(confusionMatriz);
		System.out.println("\n--- Matriz de Confusi�n Porcentual ---");
		printProcentualConfusionMatriz(confusionMatriz);		
		System.out.println("\n--- Matriz de Confusi�n "+k+" mas cercanos---");
		printConfusionMatriz(confusionMatrizKNearest);
		System.out.println("\n--- Matriz de Confusi�n Porcentual "+k+" mas cercanos---");
		printProcentualConfusionMatriz(confusionMatrizKNearest);
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

	private static void createConfusionMatriz(String recognitionCourseFolder) {
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
						Integer luPredicted = luNearest.get(0);
						Integer LuPredictedIndex = lusIndex.get(luPredicted);
						//Add one in the confusion matriz for de actual and predicted index
						confusionMatriz[luActualIndex][LuPredictedIndex]++;
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
