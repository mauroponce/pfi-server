package mauroponce.pfi.services

import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mauroponce.pfi.domain.Attendance;
import mauroponce.pfi.domain.Course;
import mauroponce.pfi.domain.Student;
import mauroponce.pfi.recognition.FaceRecognizer;
import mauroponce.pfi.utils.AppConstants;
import mauroponce.pfi.utils.FileUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

class AttendanceService {

    def saveAttendance(final Integer studentLU, final Integer courseNumber,
			final Boolean attended, final Date date){
		validateFields(studentLU, courseNumber, attended, date);
		Date d = date != null ? date : new DateTime().toDate();
		def student = Student.get(studentLU);
		def course = Course.get(courseNumber);
		def attendance = new Attendance(student, course, attended, d);
		return attendance.save();
	}
	
	String getTrainingData(final String username, final Date currentDate) {
		CourseService courseService = new CourseService();
		def course = courseService.findCourse(username, currentDate);
		if(course == null){
			throw new NullPointerException("Course not found");
		}
		/*Obtengo los legajos de los alumnos de este curso.
		 * por */
		String [] luArray = new String[course.getStudents().size()];
		int i = 0;
		for(Student s : course.getStudents()){
			luArray[i] = s.getLU().toString();
			i++;
		}
		FaceRecognizer recognitionService = new FaceRecognizer();
		System.out.println("Students: " + course.getStudents().size());
		return recognitionService.getFacesData("course_"+course.getCourseNumber());
	}
	
	def sendTrainingImage(final Integer studentLU, final String encodedImageBase64, final String fileExtension){
		String fileName = new Date().getTime().toString() + "." + fileExtension;
		String outputPath = AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + studentLU + "/" + fileName;
			
		FileUtils.decodeFileBase64(encodedImageBase64, outputPath);
		
		CourseService courseService = new CourseService();
		courseService.processGenerateFacesdata(studentLU);		
	}
	    
    private void validateFields(final Integer studentLU, final Integer courseNumber,
			final Boolean attended, final Date date){
    	validateStudentLU(studentLU);
    	validateCourseNumber(courseNumber);
    	validateDate(date);
    }
    
    private void validateStudentLU(final Integer studentLU){
    	//TODO: Check it is a number
    	if(studentLU == null){
    		throw new IllegalArgumentException("Student LU is required");
    	}
    }
    
    private void validateCourseNumber(final Integer courseNumber){
    	if(courseNumber == null){
    		throw new IllegalArgumentException("Course number is required");
    	}
    }
    
    private void validateDate(final Date date){
    	if(date != null){
    		DateTime dateTime = new DateTime(date);
    		if(dateTime.isAfterNow()){
    			throw new IllegalArgumentException("Date can't be after today");
    		}
    	}
    }
}
