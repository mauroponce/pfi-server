package mauroponce.pfi.controllers

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

import grails.converters.JSON
import grails.converters.XML
import mauroponce.pfi.domain.*

class AttendanceController {
	def attendanceService
	
	static allowedMethods = [
		postput:['POST', 'PUT'],
		facesdata: 'GET'		
	]
	
	// http://localhost:8080/PFI/attendance/facesdata?usr=mmiralles&d=2012-10-15-09:30
	def facesdata() {
		println(params)
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm");
		// DateTime date1 = formatter.parseDateTime("2012-10-15 09:30");
		String username = params.usr		
		Date currentDate = formatter.parseDateTime(params.d).toDate();
		String facesData = attendanceService.getTrainingData(username, currentDate) 
		def jsonResult = [facesdata: facesData] 
		render jsonResult as JSON
	}
	// http://localhost:8080/PFI/attendance/postput
	def postput() {
		def jsonParams = request.JSON
		 
		def students = Student.list()
		def jsonResult = [name : "POST!", message: jsonParams.nombre, studentsAmount: students.size()]
		println(jsonParams)
		render jsonResult as JSON
	}
}
