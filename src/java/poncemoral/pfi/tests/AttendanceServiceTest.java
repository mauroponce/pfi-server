package poncemoral.pfi.tests;
import java.util.Date;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import poncemoral.pfi.domain.Course;
import poncemoral.pfi.services.AttendanceService;
import poncemoral.pfi.services.CourseService;

public class AttendanceServiceTest {
	
	AttendanceService attendanceService = new AttendanceService();
	private final String TEACHER_MIRALLES_USERNAME = "mmiralles";
	private final Integer MAURO_PONCE_LU = 131445;
	
	@Test
	public void testSaveTodayAttendanceRight(){
        attendanceService.saveAttendance(new Integer(MAURO_PONCE_LU), new Integer(1),
        		Boolean.valueOf(true), null);
	}
	
	@Test(expected = Exception.class)
	public void testSaveTodayAttendanceWrong(){
        attendanceService.saveAttendance(null, new Integer(1),
        		Boolean.valueOf(true), null);
	}
}
