package mauroponce.pfi.services

import grails.converters.JSON
import mauroponce.pfi.domain.Course
import mauroponce.pfi.domain.Student
import mauroponce.pfi.utils.AppConstants
import mauroponce.pfi.utils.FileUtils

class StudentService {
	public static void createStudentsFromImageFolder(Course course1, Course course2, Course course4){
		File courseFolder = new File(AppConstants.APPLICATION_IMAGES_FOLDER + "/course_" + course1.courseNumber);
		int i = 0		
		for (File studentFolder : courseFolder.listFiles()) {			
			Student student = new Student()
			student.setLU(studentFolder.getName().toInteger())
			
			for (File studentImg : studentFolder.listFiles()) {
				// La encoded image tiene que ser la que se llama como el legajo
				student.setEncodedImage(FileUtils.encodeFileBase64(studentImg.getAbsolutePath()))
				
				// lo siguiente no anda asi q por ahora asumo q hay una sola imagen de arranque (que seria lo correcto)
//				if (studentImg.path.concat(student.getLU() + "\\" + student.getLU()) == student.getLU()){
//					student.setEncodedImage(FileUtils.encodeFileBase64(studentImg.getAbsolutePath()))
//				}
				
				// pensar algo menos hardcodeado o directamente no setear nada mas q el legajo y la imagen del alumno
				def nameParts = studentImg.getName().split("_")
				if (nameParts.length > 2 && "UADE".equals(nameParts[0])){
					student.setFirstName(nameParts[1])
					student.setLastName(nameParts[2])
					student.setEncodedImage(FileUtils.encodeFileBase64(studentImg.getAbsolutePath()))
					break					
				}				
			}
			student.save()
			course1.getStudents().add(student)
			if (i % 2 == 0){
				course2.getStudents().add(student)				
			}
			if ((student.LU in 130001..130010)
					|| student.LU in [130023, 130025, 130036, 130980, 131385, 131431, 131900, 131903] ){
				course4.students.add(student)
			}
		}
	}
	
	def getStudentsJSONByCourseNumber(Integer courseNumber){
		Course course;
		Course.withTransaction{
			course = Course.get(courseNumber)
		}
		def students = course.getStudents()
		def students_data = []
		
		for(Student student : students){
			def data = [
				LU: student.getLU(),
				firstName: student.getFirstName(),
				lastName: student.getLastName(),
				encodedImage: student.getEncodedImage()
			]
			students_data.add(data)
		}
		return students_data;
	}
	
}
