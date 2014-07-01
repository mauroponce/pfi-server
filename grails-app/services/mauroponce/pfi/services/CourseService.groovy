package mauroponce.pfi.services

import mauroponce.pfi.domain.Course
import mauroponce.pfi.utils.DateUtil

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
			
		//String q = "from Course as c where c.dayOfWeek = :dayOfWeek";
    	
//		def courses = Course.findAll(q, [dayOfWeek: dayOfWeek])
		def courses = Course.executeQuery(q, [username: teacherUsername, dayOfWeek: dayOfWeek])     	
    	LocalTime currentTime = new LocalTime(dateTime.getHourOfDay(), dateTime.getMinuteOfHour())    	
    	for(Course c in courses){
    		if(DateUtil.isInInterval(currentTime, c.getHourFrom(), c.getHourTo())){
    			foundCourse = c;
    		}
    	}
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
}
