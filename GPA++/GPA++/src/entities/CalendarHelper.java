package entities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.widget.Toast;

public class CalendarHelper
{
	/**
	 * Sets a one time event for into the calendar app. Default reminder is 30 min, and
	 * default duration is 30 min
	 * @param activity - the activity using this method
	 * @param date - the date to set the event
	 * @param title - title of the event
	 * @param description - detailed description of the event
	 * @return - the calendar event ID created on the calendar app
	 */
	public long setOneTimeEvent(Activity activity, Date date, String title, String description)
	{
		String cID = getFirstCalendarID(activity);
		if (cID != null)
		{
			long calID = Long.parseLong(cID);
			long startMillis = 0;
			long endMillis = 0;
			Calendar beginTime = Calendar.getInstance();
			//Note: month and only month is 0-based
			beginTime.setTime(date);
			startMillis = beginTime.getTimeInMillis();
			
			Calendar endTime = beginTime;
			endTime.add(Calendar.MINUTE, 30);
			endMillis = endTime.getTimeInMillis();

			ContentResolver cr = activity.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(Events.DTSTART, startMillis);
			values.put(Events.DTEND, endMillis);
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.CALENDAR_ID, calID);
			TimeZone timeZone = TimeZone.getDefault();
			values.put(Events.EVENT_TIMEZONE, timeZone.getID());
			Uri uri = cr.insert(Events.CONTENT_URI, values);

			//add reminder
			long eventID = Long.parseLong(uri.getLastPathSegment());
			addReminder(eventID, activity);

			Toast.makeText(activity, "Event and reminder set for: " + title,
					Toast.LENGTH_LONG).show();
			
			return eventID;
		}
		else
		{
			Toast.makeText(activity,
					"Cannot create reminders. Please create a calendar in the calendar app.",
					Toast.LENGTH_LONG).show();
			//-1 means no event has been created
			return -1;
		}
	}

	/**
	 * Sets a reoccuring time event for into the calendar app. Default reminder is 30 min, and
	 * default duration is 30 min
	 * @param activity - the activity using this method
	 * @param startDate - the first date and time of the reoccuring event
	 * @param repeatDays - the number of days to repeat in a week
	 * @param numOfWeeks - the number of weeks to repeat
	 * @param title - title of the event
	 * @param description - detailed description of the event
	 * @return - the calendar event ID created on the calendar app
	 */
	public long setReoccuringEvents(Activity activity, Date startDate, int durationMinutes, String repeatDayOfWeek,
			int numOfWeeks, String title, String description)
	{
		//check if we really have a repeating event, if not, create a one time event
		if (repeatDayOfWeek.isEmpty() || numOfWeeks <= 0)
		{
			return setOneTimeEvent(activity, startDate, title, description);
		}
		
		String cID = getFirstCalendarID(activity);
		if (cID != null)
		{
			long calID = Long.parseLong(cID);
			long startMillis = 0;
			Calendar beginTime = Calendar.getInstance();
			//Note: month and only month is 0-based
			beginTime.setTime(startDate);
			//see if we need to move forward the calendar event
			Calendar baseDate = Calendar.getInstance();
			baseDate.setTime(startDate);
			//align the start date with the day of the week
			if (repeatDayOfWeek.equalsIgnoreCase("MO"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("TU"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("WE"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("TH"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("FR"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("SA"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			}
			else if (repeatDayOfWeek.equalsIgnoreCase("SU"))
			{
				beginTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			}
			
			//move ahead by 1 week if the aligned date has been passed
			if (beginTime.getTimeInMillis() < baseDate.getTimeInMillis())
			{
				beginTime.add(Calendar.DATE, 7);
			}
			
			startMillis = beginTime.getTimeInMillis();

			ContentResolver cr = activity.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(Events.DTSTART, startMillis);
			//Cannot have DTEND for re-occuring events, but a must-have for one time events
			//reoccurrence rules:
			//http://www.grokkingandroid.com/recurrence-rule-and-duration-formats/
			//e.g. FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR;COUNT=15
			//COUNT=9: for a 3 days / week event creates 3 weeks of events
			int count = numOfWeeks;
			StringBuilder repeatRuleSB = new StringBuilder("FREQ=WEEKLY;BYDAY=");
			repeatRuleSB.append(repeatDayOfWeek);
			repeatRuleSB.append(";COUNT=").append(count);
			
			values.put(Events.RRULE, repeatRuleSB.toString());
			//each event is 1 hour long by default
			if (durationMinutes < 1)
			{
				durationMinutes = 60;
			}
			values.put(Events.DURATION, "+P" + durationMinutes + "M");
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.CALENDAR_ID, calID);
			TimeZone timeZone = TimeZone.getDefault();
			values.put(Events.EVENT_TIMEZONE, timeZone.getID());
			Uri uri = cr.insert(Events.CONTENT_URI, values);

			//add reminder
			long eventID = Long.parseLong(uri.getLastPathSegment());
			addReminder(eventID, activity);

			Toast.makeText(activity, "Event and reminder set for: " + title,
					Toast.LENGTH_LONG).show();

			return eventID;
		}
		else
		{
			Toast.makeText(activity,
					"Cannot create reminders. Please create a calendar in the calendar app.",
					Toast.LENGTH_LONG).show();
			//-1 means no event has been created
			return -1;
		}
	}

	public void deleteEvent(long eventID, Activity activity)
	{
		ContentResolver cr = activity.getContentResolver();
		ContentValues values = new ContentValues();
		Uri deleteUri = null;
		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = activity.getContentResolver().delete(deleteUri, null, null);
	}

	private void addReminder(long eventID, Activity activity)
	{
		ContentResolver cr = activity.getContentResolver();
		ContentValues values = new ContentValues();
		//default is a 30 min reminder
		values.put(Reminders.MINUTES, 30);
		values.put(Reminders.EVENT_ID, eventID);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		Uri uri = cr.insert(Reminders.CONTENT_URI, values);
	}

	private String getFirstCalendarID(Activity activity)
	{
		String[] projection = new String[] { "_id", "calendar_displayName" };
		Uri calendars;
		if (Build.VERSION.SDK_INT >= 8)
		{
			calendars = Uri.parse("content://com.android.calendar/calendars");
		}
		else
		{
			calendars = Uri.parse("content://calendar/calendars");
		}
		Cursor cursor = activity.managedQuery(calendars, projection, null, null, null); //get all calendars
		if (cursor.moveToFirst())
		{
			String calId;
			int l_idCol = cursor.getColumnIndex(projection[0]);
			calId = cursor.getString(l_idCol);

			return calId;
		}
		else
		{
			return null;
		}
	}
}
