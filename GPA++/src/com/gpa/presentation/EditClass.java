package com.gpa.presentation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import DBLayout.DBFunc;
import Utility.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.gpa.R;

import entities.CalendarHelper;
import entities.ClassAdapter;
import entities.ClassDetail;

public class EditClass extends Activity implements OnTouchListener, OnClickListener
{
	private EditText etStartDate;
	private EditText etEndDate;
	private EditText etClassName;
	private EditText etLocation;
	private EditText etClassId;
	private Button btAddSched;
	private Button btOk;
	private ListView lvSchedule;
	private Activity mActivity;
	private ArrayList<ScheduleDS> scheduleList;
	private boolean editMode;
	private int classId;
	String className;
	String location;
	String startDate;
	String endDate;
	DBFunc db;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_edit_class);
		mActivity = this;
		scheduleList = new ArrayList<ScheduleDS>();

		etStartDate = (EditText) this.findViewById(R.id.et_startdate);
		etEndDate = (EditText) this.findViewById(R.id.et_enddate);
		etStartDate.setOnTouchListener(this);
		etEndDate.setOnTouchListener(this);
		etClassName = (EditText) this.findViewById(R.id.et_classname);
		etClassId = (EditText) this.findViewById(R.id.et_classID);
		etLocation = (EditText) this.findViewById(R.id.et_classlocation);
		btAddSched = (Button) findViewById(R.id.bt_addSched);
		btAddSched.setOnClickListener(this);
		btOk = (Button) findViewById(R.id.ec_okay);
		btOk.setOnClickListener(this);
		lvSchedule = (ListView) this.findViewById(R.id.editClass_lv_schedule);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			classId = extras.getInt("classId");
			Log.v("taskId", classId + "");
		}

		//check if we are editing a class or creating a new class
		if (classId > 0)
		{
			editMode = true;
			//retrieve all the information of the class and put them in their respective fields
			getCurrentClassFromDB(classId);
			etClassId.setText(String.valueOf(classId));
			//class ID must not be editable if we are editing the class
			etClassId.setEnabled(false);
			etClassId.setFocusable(false);
			etClassName.setText(className);
			etStartDate.setText(startDate);
			etEndDate.setText(endDate);
			etLocation.setText(location);
		}
		else
		{
			editMode = false;
			//class ID can be set
			etClassId.setEnabled(true);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		showSchedules();
	}

	private void showSchedules()
	{
		//populate the list view with the added schedules
		lvSchedule = (ListView) this.findViewById(R.id.editClass_lv_schedule);

		ArrayList<String> schedArr = new ArrayList<String>();
		for (ScheduleDS s : scheduleList)
		{
			schedArr.add(s.toString());
		}
		if (schedArr.isEmpty())
		{
			schedArr.add("No schedule has been set for this class.\nPlease add class schedules.");
			lvSchedule.setEnabled(false);
		}
		else
		{
			lvSchedule.setEnabled(true);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, schedArr);

		lvSchedule.setAdapter(adapter);

		//delete the added schedules upon tapping on each schedule
		lvSchedule.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id)
			{
				scheduleList.remove(position);
				//reload the data
				showSchedules();
			}
		});
	}

	public void getCurrentClassFromDB(int classID)
	{
		DBshare dbshare = (DBshare) getApplication();

		DBFunc dbfunc = dbshare.getDbfunc();

		Cursor[] C = dbfunc.displayClassDetail(classID);
		ClassAdapter ca = new ClassAdapter();
		ClassDetail cdtail = ca.adapteClassDetail(C[0], C[1], C[2]);

		className = cdtail.getClassName();
		location = cdtail.getLocation();
		startDate = cdtail.getStartDate();
		endDate = cdtail.getEndDate();

		//add the existing schedule into the current schedule
		scheduleList = new ArrayList<ScheduleDS>();
		ArrayList<String> schedArr = cdtail.getScheds();
		for (String sched : schedArr)
		{
			String[] split1 = sched.split(": ");
			String[] split2 = split1[1].split(" - ");
			scheduleList.add(new ScheduleDS(split1[0], split2[0], split2[1]));
		}
		dbshare.setNoteInfo(cdtail.getNoteInfo());
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		if (id == R.id.bt_addSched)
		{
			Intent intent = new Intent(EditClass.this, AddScheduleActivity.class);
			startActivityForResult(intent, 1);
		}
		else if (id == R.id.ec_okay)
		{
			String classname = etClassName.getText().toString();
			String startDate = etStartDate.getText().toString();
			String endDate = etEndDate.getText().toString();
			String classIdStr = etClassId.getText().toString();
			String classlocation = etLocation.getText().toString();

			//error check the fields, no fields can be empty
			if (classname.isEmpty() || startDate.isEmpty() || endDate.isEmpty()
					|| classIdStr.isEmpty() || classlocation.isEmpty() || scheduleList.isEmpty())
			{
				new AlertDialog.Builder(this).setTitle("Empty Fields")
						.setMessage("Please fill out all the fields.")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.cancel();
							}
						}).show();
			}
			else
			{
				try
				{
					//class ID must be valid and non-negative
					int classId = Integer.parseInt(classIdStr);
					if (classId <= 0)
					{
						throw new NumberFormatException();
					}
					addToDB();
					//return back after we are finished
					finish();
				}
				catch (NumberFormatException e)
				{
					new AlertDialog.Builder(this)
							.setTitle("Invalid Class ID")
							.setMessage(
									"Class ID is not valid. Please use a positive number for class ID.")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.cancel();
								}
							}).show();
				}
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (v.getId() == R.id.et_starttime || v.getId() == R.id.et_endTime)
			{

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View view = View.inflate(this, R.layout.time_picker_dialogue, null);

				final TimePicker timePicker = (android.widget.TimePicker) view
						.findViewById(R.id.time_picker);
				builder.setView(view);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());

				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(Calendar.MINUTE);
				Dialog dialog = builder.create();
				dialog.show();
			}
			else if (v.getId() == R.id.et_startdate || v.getId() == R.id.et_enddate)
			{

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View view = View.inflate(this, R.layout.date_picker_dialogue, null);

				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				builder.setView(view);
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());

				if (v.getId() == R.id.et_startdate)
				{
					final int inType = etStartDate.getInputType();
					etStartDate.setInputType(InputType.TYPE_NULL);
					etStartDate.onTouchEvent(event);
					etStartDate.setInputType(inType);
					etStartDate.setSelection(etStartDate.getText().length());

					builder.setTitle("Please pick a start date");
					builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Util util = new Util();

							String dateString = util.rawDateToString(datePicker.getYear()
									- Util.DATE_YEAR_OFFSET, datePicker.getMonth(),
									datePicker.getDayOfMonth());

							etStartDate.setText(dateString);
							dialog.cancel();
						}
					});
				}
				else
				{
					final int inType = etEndDate.getInputType();
					etEndDate.setInputType(InputType.TYPE_NULL);
					etEndDate.onTouchEvent(event);
					etEndDate.setInputType(inType);
					etEndDate.setSelection(etEndDate.getText().length());

					builder.setTitle("Please pick an end date");
					builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Util util = new Util();

							String dateString = util.rawDateToString(datePicker.getYear()
									- Util.DATE_YEAR_OFFSET, datePicker.getMonth(),
									datePicker.getDayOfMonth());

							etEndDate.setText(dateString);
							dialog.cancel();
						}
					});
				}
				Dialog dialog = builder.create();
				dialog.show();
			}
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
			case (1):
			{
				if (resultCode == Activity.RESULT_OK)
				{
					String whichDay = data.getStringExtra("whichDay");
					String startTime = data.getStringExtra("st");
					String endTime = data.getStringExtra("et");
					ScheduleDS schedule = new ScheduleDS(whichDay, startTime, endTime);
					scheduleList.add(schedule);
					Log.v("preivous", "Weekday:" + whichDay + " StartTime: " + startTime
							+ " EndTime: " + endTime);
					Log.v("Schedule List", scheduleList.toString());
				}
				break;
			}
			default:
				Log.e("Edit Class", "Unexpected request code for onActivityResult: " + requestCode);
				break;
		}
	}

	public void addToDB()
	{
		String classname = etClassName.getText().toString();
		String startDate = etStartDate.getText().toString();
		String endDate = etEndDate.getText().toString();
		String classIdStr = etClassId.getText().toString();
		String classlocation = etLocation.getText().toString();

		try
		{
			int classId = Integer.parseInt(classIdStr);

			DBFunc dbfunc = new DBFunc(EditClass.this);
			CalendarHelper calendarHelper = new CalendarHelper();

			//update the class information
			if (editMode)
			{
				//delete the class schedules from calendar app first then the DB
				ArrayList<Integer> schedIds = (new ClassAdapter()).adapteClassSchedules(dbfunc
						.getScheduleIds(classId));
				//delete all events created by the 
				for (int s : schedIds)
				{
					calendarHelper.deleteEvent(dbfunc.getEventIdFromSchedule(s), mActivity);
					
					dbfunc.deleteSchedule(s);
				}

				//now update the class information
				dbfunc.updateClass(classId, classname, classlocation, startDate, endDate);
			}
			//add new class
			else
			{
				dbfunc.insertClass(classId, classname, classlocation, startDate, endDate);
			}

			//add calendar events here
			Util util = new Util();

			//always insert the new schedule
			if (scheduleList != null)
			{
				Date dateStartDate = util.stringToDate(startDate);
				Date dateEndDate = util.stringToDate(endDate);
				long diff = dateEndDate.getTime() - dateStartDate.getTime();
				//calculate the weeks first, this should not change for all schedules
				int diffWeeks = (int) (diff / (7 * 24 * 60 * 60 * 1000));
				
				for (ScheduleDS s : scheduleList)
				{
					//now check the duration for each schedule, default duration is 1 hour
					dateEndDate.setTime(dateStartDate.getTime());
					int durationMinutes = 60;
					try
					{
						dateStartDate.setHours(Integer.parseInt(s.startTime.split(":")[0]));
						dateStartDate.setMinutes(Integer.parseInt(s.startTime.split(":")[1]));
						dateEndDate.setHours(Integer.parseInt(s.endTime.split(":")[0]));
						dateEndDate.setMinutes(Integer.parseInt(s.endTime.split(":")[1]));
						diff = dateEndDate.getTime() - dateStartDate.getTime();
						durationMinutes = (int) (diff / (60 * 1000));
					}
					catch (NumberFormatException e)
					{
						durationMinutes = 60;
					}
					//retrieve the days from the schedule list, but in a special format
					//just taking the first two characters
					String repeatDayOfWeek = s.day.substring(0, 2);

					long eventId = calendarHelper.setReoccuringEvents(mActivity, dateStartDate,
							durationMinutes, repeatDayOfWeek, diffWeeks, classId + " " + classname, "");
					dbfunc.insertSchedule(classId, s.day, s.startTime, s.endTime, (int)eventId);
				}
			}
		}
		catch (NumberFormatException e)
		{
			Log.e("Edit Class", "Class ID must be a number. Details: " + e.getMessage());
		}
	}


	public class ScheduleDS
	{
		public String day;
		public String startTime;
		public String endTime;


		public ScheduleDS(String day, String startTime, String endTime)
		{
			this.day = day;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public String toString()
		{
			return day + ": " + startTime + " - " + endTime;
		}
	}
}
