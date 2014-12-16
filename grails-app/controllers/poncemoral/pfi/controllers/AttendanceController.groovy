package poncemoral.pfi.controllers

import grails.converters.JSON
import groovy.json.JsonBuilder
import poncemoral.pfi.domain.*

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class AttendanceController {
	def attendanceService
	def courseService
	def studentService
	
	static allowedMethods = [
		save: 'POST',
		log_in: 'GET'
	]
	
	// http://localhost:8080/PFI/attendance/log_in?usr=mmiralles&d=2012-10-15-09:30
	def log_in() {		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm");
		// DateTime date1 = formatter.parseDateTime("2012-10-15 09:30");
		String username = params.usr
		System.out.println("loged: ${username}")
		Date currentDate = formatter.parseDateTime(params.d).toDate();
		Course course = attendanceService.logIn(username, currentDate)
		def result = [courseNumber: course.courseNumber,
						creationDateFacesData: course.creationDateFacesData.time,
						students: studentService.getStudentsJSONByCourseNumber(course.courseNumber)]
		render result as JSON
	}
	
	// http://localhost:8080/PFI/attendance/save
	def save(){
		def jsonParams = request.JSON
		Date date = new DateTime(2012, 10, 15, 9, 30, 0, 0).toDate(); //monday 9:30 (fisica general)
		def returned = attendanceService.saveAttendance(jsonParams.studentLU, jsonParams.courseNumber, true, date) // null for current date
		def result = [saved: returned != null]
		if (result){
			Course.withTransaction{
				Student student = Student.get(jsonParams.studentLU)
				Course course = Course.get(jsonParams.courseNumber)
				System.out.println("Saved attendance to student: "+student.lastName+", "+student.firstName+" for course: "+course.name)		
			}
		}
		render result as JSON
		/*Agregar restricciones a Attendance para q no permita duplicados. Ahora guarda siempre*/
	}
}
