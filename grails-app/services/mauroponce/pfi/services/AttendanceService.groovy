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
		attendance.save();
	}
	
	String getTrainingData(final String username, final Date currentDate) {
		def course = findCourse(username, currentDate);
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
		return recognitionService.getFacesData(luArray);
	}
	
	Course findCourse(final String teacherUsername, final Date date){
    	def foundCourse
    	DateTime dateTime = new DateTime(date);
    	int dayOfWeek = dateTime.dayOfWeek().get();
    	System.out.println("Day of Week: " + dayOfWeek);
		
    	/*String q = "from Course as c inner join c.teachers t where t.username = :username " +
    			"and c.dayOfWeek = :dayOfWeek";*/
		
		// FIX!!!		
		String q = "from Course as c where c.dayOfWeek = :dayOfWeek";
    	
		def courses = Course.findAll(q, [dayOfWeek: dayOfWeek])
		// def courses = Course.findAll(q, [username: teacherUsername, dayOfWeek: dayOfWeek])     	
    	LocalTime currentTime = new LocalTime(dateTime.getHourOfDay(), dateTime.getMinuteOfHour())    	
    	for(Course c in courses){
    		if(isInInterval(currentTime, c.getHourFrom(), c.getHourTo())){
    			foundCourse = c;
    		}
    	}
    	return foundCourse;
    }
	
	def sendTrainingImage(Integer studentLU, String encodedImageBase64, String fileExtension){
		String outputPath = AppConstants.TRAINING_IMAGES_ROOT_FOLDER		
			+ "/" + studentLU + "/" + new Date().getTime() + "." + fileExtension;
		System.out.println(outputPath);
		FileUtils.decodeFileBase64(encodedImageBase64, outputPath);
	}
	
    private boolean isInInterval(final LocalTime currentTime, String hourFrom, String hourTo){
    	String [] timeFromArray = hourFrom.split(":");
		Integer hourFromInt = Integer.parseInt(timeFromArray[0]);
		Integer minuteFromInt = Integer.parseInt(timeFromArray[1]);    		
		String [] timeToArray = hourTo.split(":");
		Integer hourToInt = Integer.parseInt(timeToArray[0]);
		Integer minuteToInt = Integer.parseInt(timeToArray[1]);
		
		LocalTime timeFrom = new LocalTime(hourFromInt, minuteFromInt);		
		LocalTime timeTo = new LocalTime(hourToInt, minuteToInt);		
		Interval interval = new Interval(timeFrom.toDateTimeToday(), timeTo.toDateTimeToday());
		
    	return interval.contains(currentTime.toDateTimeToday());
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
