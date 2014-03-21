package mauroponce.pfi.tests;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauroponce.pfi.recognition.FaceRecognizerFisher;
import mauroponce.pfi.recognition.IFaceRecognizer;
import mauroponce.pfi.utils.AppConstants;

public class ConfusionMatrixTest {

	private static Map<Integer, Integer> lusIndex;
	private static Map<Integer, Integer> lusActualCount;
	private static Integer[][] confusionMatriz;
	private static Integer[][] confusionMatrizKNearest;
	private static IFaceRecognizer recognitionService;
	private static Integer LEARNING_IMAGES_COUNT = 3;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		String recognitionCourseFolder = "a_reconocer";
		Integer k = getStudentsCount(recognitionCourseFolder);
		recognitionService = new FaceRecognizerFisher();
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
//		recognitionService = new FaceRecognizer();
		recognitionService.learn("course_"+t);
		System.out.println("<table><tr><td colspan='"+(k+1)+"'>PRUEBA T = "+t+"</td></tr></table>");
		for (int i = 1; i <= k; i++) {
			createConfusionMatriz(recognitionCourseFolder, i);
			System.out.println("<table><tr><td colspan='"+(k+1)+"'>--- Matriz de Confusion "+i+" mas cercanos---</td></tr></table>");
			printConfusionMatriz(confusionMatrizKNearest);
			System.out.println("<table><tr><td colspan='"+(k+1)+"'>--- Matriz de Confusion Porcentual "+i+" mas cercanos---</td></tr></table>");
			printProcentualConfusionMatriz(confusionMatrizKNearest, k);			
		}
	}

	private static void printConfusionMatriz(Integer[][] matriz) {
		System.out.println("<table>");
		System.out.println("<tr>");
		System.out.println("<td>&nbsp;</td>");			
		for (Integer lu : lusIndex.keySet()) {
			System.out.print("<td>"+lu+"</td>");			
		}		
		System.out.println("</tr>");	
		for (Integer luActual : lusIndex.keySet()) {
			System.out.println("<tr>");
			Integer luActualIndex = lusIndex.get(luActual);
			System.out.print("<td>"+luActual+"</td>");
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				System.out.print("<td>"+luPredictedCount+"</td>");
//				System.out.format("|%5s   ",luPredictedCount);
			}
			System.out.println("</tr>");		
		}
		System.out.println("</table>");
	}
	
	private static void printProcentualConfusionMatriz(Integer[][] matriz, Integer k) {
		System.out.println("<table>");
		System.out.println("<tr>");
		System.out.println("<td>&nbsp;</td>");			
		for (Integer lu : lusIndex.keySet()) {
			System.out.print("<td>"+lu+"</td>");			
		}		
		System.out.println("</tr>");	
		float performance = 0;
		for (Integer luActual : lusIndex.keySet()) {
			System.out.println("<tr>");
			Integer luActualIndex = lusIndex.get(luActual);
			System.out.print("<td>"+luActual+"</td>");
			Integer luActualCount = lusActualCount.get(luActual);
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				float percentFloat = luPredictedCount/(float)luActualCount*100;
				String porcentString = new DecimalFormat("#").format(percentFloat);
				System.out.print("<td>"+porcentString+"</td>");
//				System.out.format("|%5s%%  ",porcentString);
				if (luActual == luPredicted){
					performance += percentFloat;
				}
			}
			System.out.println("</tr>");	
		}		
		System.out.println("</table>");
		performance = performance/lusIndex.keySet().size();
		System.out.println("<table><tr><td colspan='"+(k+1)+"'>Performance: "+new DecimalFormat("#").format(performance)+"</td></tr></table>");
//		System.out.println("Performance: "+new DecimalFormat("#").format(performance));
	}

	private static void createConfusionMatriz(String recognitionCourseFolder, Integer k) {
//		recognitionService = new FaceRecognizer();
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

						//add one to the nearest if luActual is not in the list
						Integer luPredictedKIndex = lusIndex.get(luNearest.get(0));
						for (Integer luPredictedK: luNearest) {
							if (luPredictedK.equals(luActual)){
								//Add one in the confusion matriz for the luActual
								luPredictedKIndex = lusIndex.get(luPredictedK);
							}
						}
						confusionMatrizKNearest[luActualIndex][luPredictedKIndex]++;							
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
