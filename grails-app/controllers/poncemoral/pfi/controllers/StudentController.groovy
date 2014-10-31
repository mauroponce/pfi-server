package poncemoral.pfi.controllers

import grails.converters.JSON
import poncemoral.pfi.domain.Student
import poncemoral.pfi.domain.Course

class StudentController {
	
	def studentService
	
	static allowedMethods = [
		data: 'GET',
		course_students: 'GET',
		batch_data: 'GET'
	]
	
	// http://localhost:8080/PFI/student/data/131445
	def data() {
		Integer lu = params.id.toInteger()
		def jsonData = student_data(lu)
		
		render jsonData as JSON		
	}
	
	// http://localhost:8080/PFI/student/course_students/1
	// returns all the students of the course
	def course_students(){
		Integer courseId = params.id.toInteger()
				
		render students_data as JSON
	}
	
	
	// http://localhost:8080/PFI/student/batch_data/131445_131900_131455
	def batch_data(){
		String lus = params.id.toString()
		def students_data = []
		lus.split("_").each { k ->
			def student_data = student_data(k)
			if(student_data != null){
				students_data.add(student_data)
			}				
		}
		render students_data as JSON		
	}
	
	// http://localhost:8080/PFI/student/clear_training_data
	def clear_training_data() {
		render studentService.clearTrainingData()
	}
	
	private def student_data(lu){
		Student student
		def data;
		Student.withTransaction {
			student = Student.get(lu)
		}
		if(student != null){
			data = [
				lu: student.getLU(),
            	firstName: student.getFirstName(),
            	lastName: student.getLastName(),
            	encodedImage: student.getEncodedImage()
            ]			
		}
		
		return data
	}
	
}