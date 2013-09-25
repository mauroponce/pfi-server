package mauroponce.pfi.services

import mauroponce.pfi.domain.Course
import mauroponce.pfi.domain.Student
import mauroponce.pfi.utils.AppConstants
import mauroponce.pfi.utils.FileUtils

class StudentService {
	public static void createStudentsFromImageFolder(Course course){
		File courseFolder = new File(AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/course_" + course.courseNumber);		
		for (File studentFolder : courseFolder.listFiles()) {			
			Student student = new Student()
			student.setLU(studentFolder.getName().toInteger())			
			for (File studentImg : studentFolder.listFiles()) {				
				def nameParts = studentImg.getName().split("_")
				if (nameParts.length > 2 && "UADE".equals(nameParts[0])){
					student.setFirstName(nameParts[1])
					student.setLastName(nameParts[2])
					student.setEncodedImage(FileUtils.encodeFileBase64(studentImg.getAbsolutePath()))
					break					
				}				
			}
			student.save()
			course.getStudents().add(student)
		}
	}
	
}
