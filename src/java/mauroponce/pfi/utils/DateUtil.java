package mauroponce.pfi.utils;

import org.joda.time.Interval;
import org.joda.time.LocalTime;

public class DateUtil {	
    public static boolean isInInterval(final LocalTime currentTime, String hourFrom, String hourTo){
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
}
