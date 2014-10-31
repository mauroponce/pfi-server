package poncemoral.pfi.controllers

import grails.converters.JSON
import poncemoral.pfi.domain.Course

class CourseController {

    def index() { }
	
	def courseService
	
	static allowedMethods = [
		send_training_data: 'POST',
		get_training_data: 'GET',
		create_training_data: 'GET'
	]
	
	// http://localhost:8080/PFI/course/get_training_data/3
	def get_training_data() {
		Integer courseNumber = params.id.toInteger()
		Course course
		Course.withTransaction{
			course = Course.get(courseNumber)
		}
		def result = [courseNumber: course.courseNumber,
						creationDateFacesData: course.creationDateFacesData.time,
						encodedFacesData: course.encodedFacesData]
		
		System.out.println("training_data: ${result.toString().substring(0,25)}");
		render result as JSON
	}
	
	// http://localhost:8080/PFI/course/create_training_data/3
	def create_training_data() {
		Integer courseNumber = params.id.toInteger()
		render courseService.createTrainingData(courseNumber)
	}
	
	// http://localhost:8080/PFI/course/send_training_data
	// post: Integer studentLU, String encodedImageBase64, String fileExtension
	def send_training_data(){
		def result = "OK";
		try {
			def jsonParams = request.JSON
			courseService.sendTrainingImage(jsonParams.studentLU, jsonParams.encodedImageBase64, jsonParams.fileExtension)
		}catch (Exception ex){
			result = "ERROR"
		}
		render ([result: result] as JSON)		
	}
}
