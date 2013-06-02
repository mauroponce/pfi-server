package mauroponce.pfi.controllers

import grails.converters.JSON
import mauroponce.pfi.domain.Student

class StudentController {
	
	static allowedMethods = [
		data: 'GET'
	]
	
	// http://localhost:8080/PFI/student/data?lu=131445
    def data() { 
		Integer lu = params.lu.toInteger()
		// obtener Student por pk LU
		def student = Student.completar
		render student as JSON
	}
}