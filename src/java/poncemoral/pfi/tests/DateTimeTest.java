package poncemoral.pfi.tests;

import java.util.Date;

import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeTest {

	LocalTime hourInInterval = new LocalTime(8, 0);//08:00
	LocalTime hourOutOfInterval = new LocalTime(12, 0);//08:00
	String timeFromString = "07:45";
	String timeToString = "11:45";
		
	@Test
	public void testIsInInterval(){		
		String [] timeFromArray = timeFromString.split(":");
		Integer hourFrom = Integer.parseInt(timeFromArray[0]);
		Integer minuteFrom = Integer.parseInt(timeFromArray[1]);
		
		String [] timeToArray = timeToString.split(":");
		Integer hourTo = Integer.parseInt(timeToArray[0]);
		Integer minuteTo = Integer.parseInt(timeToArray[1]);
		
		LocalTime from = new LocalTime(hourFrom, minuteFrom);
		
		LocalTime to = new LocalTime(hourTo, minuteTo);
		Interval interval = new Interval(from.toDateTimeToday(), to.toDateTimeToday());
		Assert.assertTrue(interval.contains(hourInInterval.toDateTimeToday()));
	}
	@Test
	public void testIsOutOfInterval(){		
		String [] timeFromArray = timeFromString.split(":");
		Integer hourFrom = Integer.parseInt(timeFromArray[0]);
		Integer minuteFrom = Integer.parseInt(timeFromArray[1]);
		
		String [] timeToArray = timeToString.split(":");
		Integer hourTo = Integer.parseInt(timeToArray[0]);
		Integer minuteTo = Integer.parseInt(timeToArray[1]);
		
		LocalTime from = new LocalTime(hourFrom, minuteFrom);
		
		LocalTime to = new LocalTime(hourTo, minuteTo);
		Interval interval = new Interval(from.toDateTimeToday(), to.toDateTimeToday());
		Assert.assertFalse(interval.contains(hourOutOfInterval.toDateTimeToday()));
	}
	
	public static void main(String[] args) {
		
		long l = new Date().getTime();
		System.out.println(l);
	}
}
