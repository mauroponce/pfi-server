package poncemoral.pfi.tests;
import java.util.Date;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import poncemoral.pfi.domain.Course;
import poncemoral.pfi.services.CourseService;

public class CourseServiceTest {
	
	CourseService courseService = new CourseService();
	private final String TEACHER_MIRALLES_USERNAME = "mmiralles";
	private final Integer MAURO_PONCE_LU = 131445;
	
	@Test
	public void testFindCurrentCourse(){
		// yyyy, mm, dd, hh, MM, s y s
		Date date = new DateTime(2012, 10, 15, 9, 30, 0, 0).toDate();
		Course course = courseService.findCourse(TEACHER_MIRALLES_USERNAME, date);
		Assert.assertNotNull(course);
	}
	
	@Test
	public void testFindCoursesByStudentLu(){
//		List<Course> courses = courseService.findCoursesByStudentLu(MAURO_PONCE_LU);
//		Assert.assertNotNull(courses);
	}
	
	@Test
	public void testGetTrainingData(){
		String trainingData = courseService.createTrainingData(3);
		
		Assert.assertNotNull(trainingData);
	}
	
}
