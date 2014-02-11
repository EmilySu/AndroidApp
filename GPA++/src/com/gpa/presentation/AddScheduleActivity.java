package com.gpa.presentation;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.gpa.R;

public class AddScheduleActivity extends Activity implements OnTouchListener, OnClickListener,
		OnItemSelectedListener
{
	private EditText etStartTime;
	private EditText etEndTime;
	private Button btShedOkay;
	private String whichDay;
	private String startTime;
	private String endTime;
	private String[] weekdays = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday", "Sunday" };

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.bt_schedOkay)
		{
			getInputData();
			
			//error check the fields
			if (whichDay.isEmpty() || startTime.isEmpty() || endTime.isEmpty())
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
				Intent output = new Intent();
				output.putExtra("whichDay", whichDay);
				output.putExtra("st", startTime);
				output.putExtra("et", endTime);
				setResult(Activity.RESULT_OK, output);
				finish();
			}
		}
	}

	public void getInputData()
	{
		startTime = etStartTime.getText().toString();
		endTime = etEndTime.getText().toString();
	}

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

				if (v.getId() == R.id.et_starttime)
				{
					final int inType = etStartTime.getInputType();
					etStartTime.setInputType(InputType.TYPE_NULL);
					etStartTime.onTouchEvent(event);
					etStartTime.setInputType(inType);
					etStartTime.setSelection(etStartTime.getText().length());

					builder.setTitle("please pick a start time");
					builder.setPositiveButton("set", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							StringBuffer sb = new StringBuffer();
							sb.append(timePicker.getCurrentHour()).append(":")
									.append(timePicker.getCurrentMinute());
							
							etStartTime.setText(sb);
							dialog.cancel();
						}
					});
				}
				else
				{
					final int inType = etEndTime.getInputType();
					etEndTime.setInputType(InputType.TYPE_NULL);
					etEndTime.onTouchEvent(event);
					etEndTime.setInputType(inType);
					etEndTime.setSelection(etEndTime.getText().length());

					builder.setTitle("please pick an end time");
					builder.setPositiveButton("set", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							StringBuffer sb = new StringBuffer();
							sb.append(timePicker.getCurrentHour()).append(":")
									.append(timePicker.getCurrentMinute());
							etEndTime.setText(sb);
							dialog.cancel();
						}
					});

				}
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
				Dialog dialog = builder.create();
				dialog.show();
			}
		}
		return true;
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3)
	{
		Spinner spinner = (Spinner) parent;
		if (spinner.getId() == R.id.spin_whichday)
		{
			whichDay = weekdays[pos];
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_add_schedule);

		etStartTime = (EditText) this.findViewById(R.id.et_starttime);
		etEndTime = (EditText) this.findViewById(R.id.et_endTime);
		btShedOkay = (Button) findViewById(R.id.bt_schedOkay);
		etStartTime.setOnTouchListener(this);
		etEndTime.setOnTouchListener(this);
		btShedOkay.setOnClickListener(this);

		Spinner spin_whichday = (Spinner) findViewById(R.id.spin_whichday);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter_month = ArrayAdapter.createFromResource(this,
				R.array.whichDay, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spin_whichday.setAdapter(adapter_month);
		spin_whichday.setOnItemSelectedListener(this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{

	}
}