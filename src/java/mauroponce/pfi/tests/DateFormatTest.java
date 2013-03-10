package mauroponce.pfi.tests;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormatTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm");
		DateTime date1 = formatter.parseDateTime("2012-10-15-09:30");
		
		DateTime date2 = new DateTime();
		System.out.println("Date 1: " + date1.toDate());
		System.out.println("Date 2: " + date2.toString(formatter));
	}

}
