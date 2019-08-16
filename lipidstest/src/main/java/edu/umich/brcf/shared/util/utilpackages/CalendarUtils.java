package edu.umich.brcf.shared.util.utilpackages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//METWorks

public class CalendarUtils
	{
	private static final String DATE_AND_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	private static final String YMD_DATE_FORMAT = "yyyy/MM/dd";
	private static final String YMD_DATE_FORMAT2 = "yyyyMMdd";

	public static Calendar calendarWithTime(
			String slashAndColonSeparatedMmDdYyyyHhMm)
		{
		SimpleDateFormat df = new SimpleDateFormat(DATE_AND_TIME_FORMAT);
		Calendar cal;

		try
			{
			Date dt = df.parse(slashAndColonSeparatedMmDdYyyyHhMm);
			cal = new GregorianCalendar();
			cal.setTime(dt);
			} catch (ParseException e)
			{
			cal = null;
			}

		return cal;
		}

	public static Calendar calendarFromString(String dateStr)
		{
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		Calendar cal;

		try
			{
			Date dt = df.parse(dateStr);
			cal = new GregorianCalendar();
			cal.setTime(dt);
			} catch (ParseException e)
			{
			cal = null;
			}
		return cal;
		}
	
	
	public static Calendar calendarFromString(String dateStr, String format)
		{
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar cal;

		try
			{
			Date dt = df.parse(dateStr);
			cal = new GregorianCalendar();
			cal.setTime(dt);
			} catch (ParseException e)
			{
			cal = null;
			}
		return cal;
		}
	

	private static Date addToDate(Date dt, int amount)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.DATE, amount);
		return cal.getTime();
		}

	public static Date subtractADay(Date dt)
		{
		return addToDate(dt, -1);
		}

	public static Date addADay(Date dt)
		{
		return addToDate(dt, 1);
		}

	public static Date subtractAWeek(Date dt)
		{
		return addToDate(dt, -7);
		}

	public static Date addAWeek(Date dt)
		{
		return addToDate(dt, 7);
		}

	public static String dateTimeDisplayStringFrom(Date date)
		{
		SimpleDateFormat df = new SimpleDateFormat(DATE_AND_TIME_FORMAT);
		return df.format(date);
		}

	public static String dateDisplayStringFrom(Date dt)
		{
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(dt);
		}

	public static String YMDDateDisplayStringFrom(Date dt)
		{
		SimpleDateFormat df = new SimpleDateFormat(YMD_DATE_FORMAT);
		return df.format(dt);
		}

	public static String getDateAsYYYYMMDDString(Date date)
		{
		SimpleDateFormat df = new SimpleDateFormat(YMD_DATE_FORMAT2);
		return df.format(date);
		}

	public static Calendar getFirstWeekDate()
		{
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DATE, 1 - weekday);

		return calendar1;
		}

	public static Calendar getLastWeekDate()
		{
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, 1 - weekday);
		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DATE, 6);

		return calendar1;
		}

	public static Calendar objectifyTheDate(String date, Integer hour,
			Integer min, String AMPM)
		{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm:a");
		Calendar cal = Calendar.getInstance();

		try
			{
			String stringDate = date + " " + hour + ":" + min + ":" + AMPM;
			cal.setTime(df.parse(stringDate));
			} catch (ParseException pe)
			{
			throw new RuntimeException(pe);
			}

		return cal;
		}

	public static Long getDuration(Calendar start, Calendar end)
		{
		return (end.getTimeInMillis() - start.getTimeInMillis()) / 60000;
		}
	}