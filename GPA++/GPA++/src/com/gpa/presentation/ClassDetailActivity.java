package com.gpa.presentation;

import java.io.File;
import java.util.ArrayList;

import DBLayout.DBFunc;
import Utility.Widget;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gpa.R;

import entities.CalendarHelper;
import entities.ClassAdapter;
import entities.ClassDetail;
import entities.NoteAdapter;

public class ClassDetailActivity extends Activity
{
	Widget widget;
	private Activity mActivity;
	private TextView tx_classname;
	private TextView tx_classid;
	private TextView tx_location;
	private TextView tx_schedInfo;

	int classID;
	String className;
	String location;
	String scheds;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_class_detail);

		mActivity = this;
		tx_classname = (TextView) findViewById(R.id.tx_cd_classname);
		tx_classid = (TextView) findViewById(R.id.tx_cd_classid);
		tx_location = (TextView) findViewById(R.id.tx_cd_location);
		tx_schedInfo = (TextView) findViewById(R.id.tx_cd_schedInfo);

		Bundle extras = getIntent().getExtras();
		this.classID = 0;
		if (extras != null)
		{
			this.classID = extras.getInt("classId");
		}

		getCurrentClassFromDB(this.classID);

		tx_classid.setText(String.valueOf(this.classID));
		tx_classname.setText(className);
		tx_location.setText(location);
		tx_schedInfo.setText(scheds);

		Button bt_deleteClass = (Button) findViewById(R.id.bt_deleteClass);
		Button bt_record = (Button) findViewById(R.id.classDetailsAudioRecordButton);
		Button bt_history = (Button) findViewById(R.id.classDetailsClassHistoryButton);
		Button bt_location = (Button) findViewById(R.id.classDetailsFindLocation);
		Button bt_picture = (Button) findViewById(R.id.classDetailsVisualRecordButton);
		Button bt_todo = (Button) findViewById(R.id.classDetailsTodoListButton);

		bt_todo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.putExtra("classId", classID);
				intent.putExtra("nextTab", "todoList");
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		bt_picture.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent locationIntent = new Intent(ClassDetailActivity.this,
						TakePhotoActivity.class);
				locationIntent.putExtra("classId", classID);
				startActivity(locationIntent);
			}
		});
		bt_history.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent historyIntent = new Intent(ClassDetailActivity.this, ClassHistory.class);
				historyIntent.putExtra("classId", classID);
				startActivity(historyIntent);
			}
		});
		bt_record.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent recordIntent = new Intent(ClassDetailActivity.this, RecordActivity.class);
				recordIntent.putExtra("classId", classID);
				startActivity(recordIntent);
			}
		});
		bt_location.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String uri = "geo:0,0?q=" + location;
				startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		});
		bt_deleteClass.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				deleteClassFromDB(classID);
				Toast.makeText(mActivity, "class " + classID + " " + className + " is deleted.",
						Toast.LENGTH_LONG).show();
				finish();
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
		//		Toast.makeText(getActivity(), al.get(0).getName(), Toast.LENGTH_LONG).show();

		this.classID = cdtail.getClassID();
		className = cdtail.getClassName();
		location = cdtail.getLocation();
		ArrayList<String> classScheds = cdtail.getScheds();
		//format the schedule, get rid of the [] from the arrayList toString method
		scheds = classScheds.toString().replace("[", "").replace("]", "").replace(", ", "");
		dbshare.setNoteInfo(cdtail.getNoteInfo());
	}

	public void deleteClassFromDB(int classId)
	{
		DBshare dbshare = (DBshare) mActivity.getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		CalendarHelper calendarHelper = new CalendarHelper();

		//delete the schedules from the calendar app first
		//then delete the schedule from the DB
		ArrayList<Integer> schedIds = (new ClassAdapter()).adapteClassSchedules(dbfunc
				.getScheduleIds(classId));
		for (int s : schedIds)
		{
			calendarHelper.deleteEvent(dbfunc.getEventIdFromSchedule(s), mActivity);
			
			dbfunc.deleteSchedule(s);
		}

		//then delete all the notes from the SD card
		Cursor c = dbfunc.getAllNotePaths(classId);
		NoteAdapter na = new NoteAdapter();
		ArrayList<String> notePaths = new ArrayList<String>();
		notePaths = na.adapteNotePaths(c);

		for (String p : notePaths)
		{
			new File(p).delete();
		}

		//then delete all the notes from the DB
		ArrayList<Integer> noteIds = na.adapteNoteIDs(dbfunc.getNoteIds(classId));
		for (int n : noteIds)
		{
			dbfunc.deleteNote(n);
		}

		//finally we can delete this class from the DB
		dbfunc.deleteClass(classId);
	}
}
