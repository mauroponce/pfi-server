package mauroponce.pfi.tests;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauroponce.pfi.recognition.FaceRecognizerEigen;
import mauroponce.pfi.recognition.IFaceRecognizer;
import mauroponce.pfi.utils.AppConstants;
import mauroponce.pfi.utils.FileUtils;

public class ConfusionMatrixTest {

	private static Map<Integer, Integer> lusIndex;
	private static Map<Integer, Integer> lusActualCount;
	private static Integer[][] confusionMatriz;
	private static Integer[][] confusionMatrizKNearest;
	private static IFaceRecognizer recognitionService;
	private static Integer LEARNING_IMAGES_COUNT = 3;
	private static StringBuffer result;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		result = new StringBuffer();
		String recognitionCourseFolder = "a_reconocer";
		Integer k = getStudentsCount(recognitionCourseFolder);
		recognitionService = new FaceRecognizerEigen();
		for (int t = 1; t <= LEARNING_IMAGES_COUNT; t++) {
			System.out.println("T = "+t);
			processFolders(t, recognitionCourseFolder, k);			
		}
		try {
			FileUtils.writeToFile("C:/Users/smoral/Documents/PFI/faces/result.xls", result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		result.append("<table><tr><td colspan='"+(k+1)+"'>PRUEBA T = "+t+"</td></tr></table>"+"\n");
		for (int i = 1; i <= k; i++) {
			System.out.println("------- K = "+i);
			createConfusionMatriz(recognitionCourseFolder, i);
			result.append("<table><tr><td colspan='"+(k+1)+"'>--- Matriz de Confusion "+i+" mas cercanos---</td></tr></table>"+"\n");
			printConfusionMatriz(confusionMatrizKNearest);
			result.append("<table><tr><td colspan='"+(k+1)+"'>--- Matriz de Confusion Porcentual "+i+" mas cercanos---</td></tr></table>"+"\n");
			printProcentualConfusionMatriz(confusionMatrizKNearest, k);			
		}
	}

	private static void printConfusionMatriz(Integer[][] matriz) {
		result.append("<table>"+"\n");
		result.append("<tr>"+"\n");
		result.append("<td>&nbsp;</td>"+"\n");			
		for (Integer lu : lusIndex.keySet()) {
			result.append("<td>"+lu+"</td>");			
		}		
		result.append("</tr>"+"\n");	
		for (Integer luActual : lusIndex.keySet()) {
			result.append("<tr>"+"\n");
			Integer luActualIndex = lusIndex.get(luActual);
			result.append("<td>"+luActual+"</td>");
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				result.append("<td>"+luPredictedCount+"</td>");
//				System.out.format("|%5s   ",luPredictedCount);
			}
			result.append("</tr>"+"\n");		
		}
		result.append("</table>"+"\n");
	}
	
	private static void printProcentualConfusionMatriz(Integer[][] matriz, Integer k) {
		result.append("<table>"+"\n");
		result.append("<tr>"+"\n");
		result.append("<td>&nbsp;</td>"+"\n");			
		for (Integer lu : lusIndex.keySet()) {
			result.append("<td>"+lu+"</td>");			
		}		
		result.append("</tr>"+"\n");	
		float performance = 0;
		for (Integer luActual : lusIndex.keySet()) {
			result.append("<tr>"+"\n");
			Integer luActualIndex = lusIndex.get(luActual);
			result.append("<td>"+luActual+"</td>");
			Integer luActualCount = lusActualCount.get(luActual);
			for (Integer luPredicted : lusIndex.keySet()) {
				Integer luPredictedIndex = lusIndex.get(luPredicted);
				Integer luPredictedCount = matriz[luActualIndex][luPredictedIndex];
				float percentFloat = luPredictedCount/(float)luActualCount*100;
				String porcentString = new DecimalFormat("#").format(percentFloat);
				result.append("<td>"+porcentString+"</td>");
//				System.out.format("|%5s%%  ",porcentString);
				if (luActual == luPredicted){
					performance += percentFloat;
				}
			}
			result.append("</tr>"+"\n");	
		}		
		result.append("</table>"+"\n");
		performance = performance/lusIndex.keySet().size();
		result.append("<table><tr><td colspan='"+(k+1)+"'>Performance: "+new DecimalFormat("#").format(performance)+"</td></tr></table>"+"\n");
		System.out.println("------- Performance = "+new DecimalFormat("#").format(performance));
//		result.appendln("Performance: "+new DecimalFormat("#").format(performance));
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
