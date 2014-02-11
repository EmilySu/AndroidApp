package com.gpa.presentation;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DBLayout.DBFunc;
import Utility.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gpa.R;

import entities.CalendarHelper;
import entities.ClassAdapter;
import entities.TaskAdapter;

public class EditTask extends Activity implements OnTouchListener
{
	private static final String[] priority = {"high", "medium", "low"}; 
	private Activity mActivity;
	private EditText etdueDate;
	private EditText etTaskName;
	private Spinner etClassId;
	private Spinner etPrio;

	int classId;
	int taskId;
	String dueDate;
	String prio;
	String taskName;
	boolean editMode;

	DBFunc db;


	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_edit_task);

		mActivity = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			taskId = extras.getInt("taskId");
			Log.v("taskId", taskId + "");
		}

		//check if we are editing a task
		if (taskId < 0)
		{
			editMode = false;
		}
		else
		{
			editMode = true;
		}

		//store the UI elements for later use
		etdueDate = (EditText) findViewById(R.id.et_task_due_date);
		etTaskName = (EditText) findViewById(R.id.editTaskNameEditText);
		etClassId = (Spinner) findViewById(R.id.editTaskClassSpinner);
		etPrio = (Spinner) findViewById(R.id.editTaskPrioritySpinner);
		
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priority);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etPrio.setAdapter(adapter1);
		DBshare dbshare = (DBshare) getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c1 = dbfunc.displayClassId();
		ArrayList<Integer> classIds = (new ClassAdapter()).adapteClassIdList(c1);
		if(classIds.size() == 0) {
			new AlertDialog.Builder(EditTask.this).setTitle("Warning").setMessage("There are no class existing in GPA++")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					finish();
				}
			}).show();
			
		}
		final String[] classIdArray = new String[classIds.size()];
		for(int i = 0; i < classIds.size(); i ++) {
			classIdArray[i] = classIds.get(i).toString();
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classIdArray);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etClassId.setAdapter(adapter2);
		


		//if we are editing a task, then fill up the fields with existing data
		if (editMode)
		{

			Cursor c = dbfunc.displayTaskDetails(taskId);
			
			//get the only task entry
			String existingTaskStr = (new TaskAdapter()).adapteTaskList(c).get(0);
			
			//there should be at least 4 data from each task
			if (!existingTaskStr.isEmpty() && existingTaskStr.split(",").length >= 4)
			{
				for(int i = 0; i < classIdArray.length; i ++) {
					if(classIdArray[i].equals(existingTaskStr.split(",")[0])) {
						etClassId.setSelection(i, true);
					}
				}
				
				
				taskName = existingTaskStr.split(",")[1];
				etTaskName.setText(taskName);
				dueDate = existingTaskStr.split(",")[2] + "," + existingTaskStr.split(",")[3];
				etdueDate.setText(dueDate);
				prio = existingTaskStr.split(",")[4];
				if(prio.equals("high"))
					etPrio.setSelection(0, true);
				else if(prio.equals("medium"))
					etPrio.setSelection(1, true);
				else if(prio.equals("low"))
					etPrio.setSelection(2, true);
			}
		}

		//allow the due date to be set by a date and time picker dialog
		etdueDate.setOnTouchListener(this);

		Button bt_addtask = (Button) findViewById(R.id.bt_addtask);
		bt_addtask.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				taskName = etTaskName.getText().toString();
				dueDate = etdueDate.getText().toString();
				prio = etPrio.getSelectedItem().toString();
				if(taskName.isEmpty() || dueDate.isEmpty()) {
					new AlertDialog.Builder(EditTask.this).setTitle("Warning").setMessage("Please fill out empty blank")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
					
				}
				
				else {
				//for converting dates
				Util util = new Util();

				try
				{
					classId = Integer.parseInt(etClassId.getSelectedItem().toString());
				}
				catch (NumberFormatException e)
				{
					classId = 0;
				}
				//null check for the user input fields
				if (taskName.isEmpty())
				{
					taskName = "Unamed To-do Task";
					if (classId != 0)
					{
						taskName += " for class " + classId;
					}
				}
				if (dueDate.isEmpty())
				{
					//use tomorrow as the due date
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DATE, 1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dueDate = util.dateToString(c.getTime());
				}

				if (prio.isEmpty())
				{
					//use low as default priority
					prio = "Low";
				}

				List<String> al = new ArrayList<String>();

				DBshare dbshare = (DBshare) getApplication();
				DBFunc dbfunc = dbshare.getDbfunc();
				Log.v("editText", "taskname=" + taskName);

				CalendarHelper calendarHelper = new CalendarHelper();
				
				//then we simply delete the old calendar event and create a new one
				if (editMode)
				{
					long eventId = dbfunc.getEventIdFromTask(taskId);
					calendarHelper.deleteEvent(eventId, mActivity);
					//add the new event and retrieve a new event ID
					eventId = calendarHelper.setOneTimeEvent(mActivity,
							util.stringToDateTime(dueDate), taskName, taskName + " is a " + prio
									+ " priority task for class " + classId);
					dbfunc.updateTask(taskId, classId, taskName, dueDate, "Unfinished", prio);
					dbfunc.setEventIdForTask(taskId, (int)eventId);
				}
				else
				{
					//also insert the task into the calendar app for setting reminders
					long eventId = calendarHelper.setOneTimeEvent(mActivity,
							util.stringToDateTime(dueDate), taskName, taskName + " is a " + prio
									+ " priority task for class " + classId);

					dbfunc.insertTask(classId, taskName, dueDate, "Unfinished", prio, "type",
							(int) eventId);
				}

				finish();
			}
			}
		});
		
		Button bt_deleteTask = (Button) findViewById(R.id.bt_deletetask);
		bt_deleteTask.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				DBshare dbshare = (DBshare) getApplication();
				DBFunc dbfunc = dbshare.getDbfunc();
				
				//also needs to delete the calendar app, must do this before
				//deleting the actual data in the DB
				long eventId = dbfunc.getEventIdFromTask(taskId);
				CalendarHelper calendarHelper = new CalendarHelper();
				calendarHelper.deleteEvent(eventId, mActivity);

				dbfunc.deleteTask(taskId);

				Toast.makeText(getBaseContext(), "Deleted to-do task: " + taskName,
						Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}

	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.date_time_dialog, null);
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.due_date_picker);
			final TimePicker timePicker = (android.widget.TimePicker) view
					.findViewById(R.id.due_time_picker);
			builder.setView(view);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), null);

			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(Calendar.MINUTE);


			final int inType = etdueDate.getInputType();
			etdueDate.setInputType(InputType.TYPE_NULL);
			etdueDate.onTouchEvent(event);
			etdueDate.setInputType(inType);
			etdueDate.setSelection(etdueDate.getText().length());

			builder.setTitle("Select due date");
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Util util = new Util();

					//Note: month is 0 based for some reason
					String dateString = util.rawDateToString(datePicker.getYear()
							- Util.DATE_YEAR_OFFSET, datePicker.getMonth(),
							datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
							timePicker.getCurrentMinute());

					etdueDate.setText(dateString);
					dialog.cancel();
				}
			});

			Dialog dialog = builder.create();
			dialog.show();
		}


		return true;
	}
}
