package Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

public class Util
{
	public static final int DATE_YEAR_OFFSET = 1900;
	public static final String DATE_TIME_FORMAT = "MM/dd/yyyy, HH:mm";
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	/**
	 * @param args
	 */

	public String makeFragmentName(int viewId, long itemId)
	{
		return "android:switcher:" + viewId + ":" + itemId;
	}

	public void highlight(View v)
	{
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(1500);
		fadeIn.setStartOffset(500);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(1500);
		fadeOut.setDuration(1500);

		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(fadeOut);
		animation.addAnimation(fadeIn);
		animation.addAnimation(fadeOut);
		animation.addAnimation(fadeIn);
		v.setAnimation(animation);
	}

	/**
	 * The date string must be in the format of month/day/year hour:min
	 * 
	 * @param dateString - a string presenting a date, must be in the format of 
	 * 					month/day/year hour:min
	 * @return - a date object representing this string, or the current date if
	 * 			an error has occurred
	 */
	public Date stringToDateTime(String dateString)
	{
		try
		{
			return new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateString);
		}
		//if parsing failed, then return the current time
		catch (ParseException e)
		{
			return new Date();
		}
	}
	
	/**
	 * The date string must be in the format of month/day/year
	 * 
	 * @param dateString - a string presenting a date, must be in the format of 
	 * 					month/day/year
	 * @return - a date object representing this string, or the current date if
	 * 			an error has occurred
	 */
	public Date stringToDate(String dateString)
	{
		try
		{
			return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
		}
		//if parsing failed, then return the current time
		catch (ParseException e)
		{
			return new Date();
		}
	}

	/**
	 * Builds a string based on the a date and time
	 * 
	 * @param year - integer representing the year, no offsets
	 * @param month - starts at 1
	 * @param day - the day of the month
	 * @param hour - the hour
	 * @param min - minutes
	 * @return - date object
	 */
	public String rawDateToString(int year, int month, int day, int hour, int min)
	{
		String dateString = new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date(year,
				month, day, hour, min));

		return dateString;
	}
	
	/**
	 * Builds a string based on the a date
	 * 
	 * @param year - integer representing the year, no offsets
	 * @param month - starts at 1
	 * @param day - the day of the month
	 * @param hour - the hour
	 * @param min - minutes
	 * @return - date object
	 */
	public String rawDateToString(int year, int month, int day)
	{
		String dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date(year,
				month, day));

		return dateString;
	}
	
	public String dateToString(Date date)
	{
		return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
	}
}
