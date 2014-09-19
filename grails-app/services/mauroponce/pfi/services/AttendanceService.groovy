package mauroponce.pfi.services

import mauroponce.pfi.domain.Attendance
import mauroponce.pfi.domain.Course
import mauroponce.pfi.domain.Student

import org.joda.time.DateTime

class AttendanceService {

    def saveAttendance(final Integer studentLU, final Integer courseNumber,
			final Boolean attended, final Date date){
		validateFields(studentLU, courseNumber, attended, date)
		Date d = date != null ? date : new DateTime().toDate()
		def student = Student.get(studentLU)
		def course = Course.get(courseNumber)
		def attendance = new Attendance(student, course, attended, d)
		return attendance.save()
	}
			
	Course logIn (final String username, final Date currentDate) {
		CourseService courseService = new CourseService()
		def course = courseService.findCourse(username, currentDate)
		if(course == null){
			throw new NullPointerException("Course not found")
		}
		return course
	}
	    
    private void validateFields(final Integer studentLU, final Integer courseNumber,
			final Boolean attended, final Date date){
    	validateStudentLU(studentLU)
    	validateCourseNumber(courseNumber)
    	validateDate(date)
    }
    
    private void validateStudentLU(final Integer studentLU){
    	//TODO: Check it is a number
    	if(studentLU == null){
    		throw new IllegalArgumentException("Student LU is required")
    	}
    }
    
    private void validateCourseNumber(final Integer courseNumber){
    	if(courseNumber == null){
    		throw new IllegalArgumentException("Course number is required")
    	}
    }
    
    private void validateDate(final Date date){
    	if(date != null){
    		DateTime dateTime = new DateTime(date)
    		if(dateTime.isAfterNow()){
    			throw new IllegalArgumentException("Date can't be after today")
    		}
    	}
    }
}
