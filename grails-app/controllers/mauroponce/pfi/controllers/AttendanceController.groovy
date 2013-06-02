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
		save: 'POST',
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
		def result = [facesdata: facesData] 
		render result as JSON
	}
	
	// http://localhost:8080/PFI/attendance/save
	def save(){
		def jsonParams = request.JSON
		Date date = new DateTime(2012, 10, 15, 9, 30, 0, 0).toDate(); //monday 9:30 (fisica general)
		def returned = attendanceService.saveAttendance(jsonParams.studentLU, jsonParams.courseNumber, true, date) // null for current date
		def result = [saved: returned != null]
		render result as JSON
		/*Agregar restricciones a Attendance para q no permita duplicados. Ahora guarda siempre*/
	}	
}
