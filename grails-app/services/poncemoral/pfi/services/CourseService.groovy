package poncemoral.pfi.services

import poncemoral.pfi.domain.Course
import poncemoral.pfi.domain.Student
import poncemoral.pfi.recognition.FaceRecognizer
import poncemoral.pfi.utils.AppConstants
import poncemoral.pfi.utils.DateUtil
import poncemoral.pfi.utils.FileUtils;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime
import org.joda.time.LocalTime

class CourseService {
	def processGenerateFacesdata(final Integer studentLU){
		def courses = this.findCoursesByStudentLu(studentLU)
		for (Course course in courses){
			course.setGenerateFacesdata(Boolean.TRUE)
			course.save()
		}
	}
	
	Course findCourse(final String teacherUsername, final Date date){
    	def foundCourse
    	DateTime dateTime = new DateTime(date);
    	int dayOfWeek = dateTime.dayOfWeek().get();
    	System.out.println("Day of Week: " + dayOfWeek);
		
    	String q = "select c from Course c join c.teachers t where t.username = :username " +
    			"and c.dayOfWeek = :dayOfWeek";
				
//		def query = Course.where {
//			teachers.username == username && dayOfWeek == dayOfWeek
//		}
//		Course.find(username: teacherUsername, dayOfWeek: dayOfWeek)
				
		//String q = "from Course as c where c.dayOfWeek = :dayOfWeek";
    	
//		def courses = Course.findAll(q, [dayOfWeek: dayOfWeek])
		def courses = Course.executeQuery(q, [username: teacherUsername, dayOfWeek: dayOfWeek])     	
    	LocalTime currentTime = new LocalTime(dateTime.getHourOfDay(), dateTime.getMinuteOfHour())    	
    	for(Course c in courses){
    		if(DateUtil.isInInterval(currentTime, c.getHourFrom(), c.getHourTo())){
    			foundCourse = c;
    		}
    	}
		System.out.println("Course: "+foundCourse.name);
    	return foundCourse;
    }
	
	def findCoursesByStudentLu(final Integer studentLU){
//		Course.findAll("from Course c inner join c.students s where s.LU="+studentLU);
		return Course.withCriteria {
			students {
				eq 'LU', studentLU
			}
		}
    }
	
	String createTrainingData(final Integer courseNumber) {
		Course course = Course.get(courseNumber)
		if(course == null){
			throw new NullPointerException("Course not found")
		}
		FaceRecognizer recognitionService = new FaceRecognizer()
		System.out.println("Students: " + course.getStudents().size())
		String facesData = recognitionService.createFacesData(AppConstants.TRAINING_IMAGES_ROOT_FOLDER, course.getStudents())
		
		course.encodedFacesData = Base64.encodeBase64String(facesData.getBytes())
		course.creationDateFacesData= new Date()
		course.generateFacesdata = Boolean.FALSE
		course.save()
		System.out.println("Generated facesdata for course: "+course.name);
	}
	
	def sendTrainingImage(final Integer studentLU, final String encodedImageBase64, final String fileExtension){
		String fileName = new Date().getTime().toString() + "." + fileExtension
		String outputPath = AppConstants.TRAINING_IMAGES_ROOT_FOLDER + "/" + studentLU + "/" + fileName
			
		FileUtils.decodeFileBase64(encodedImageBase64, outputPath)
		
		this.processGenerateFacesdata(studentLU)
		
		Student student
		Course.withTransaction{
			student = Student.get(studentLU)
			System.out.println("Added training image of student: "+student.lastName+", "+student.firstName)	
		}
	}
}
