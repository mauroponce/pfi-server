package mauroponce.pfi.controllers

import grails.converters.JSON
import mauroponce.pfi.domain.Student

class StudentController {
	
	static allowedMethods = [
		data: 'GET'
	]
	
	// http://localhost:8080/PFI/student/data/131445
	def data() {
		Student student
		Student.withTransaction {
			student = Student.get(params.id.toInteger())
		}
		def jsonData = [
			lu: student.getLU(),
			firstName: student.getFirstName(),
			lastName: student.getLastName(),
			encodedImage: student.getEncodedImage()
		]
		
		render jsonData as JSON
	}
}