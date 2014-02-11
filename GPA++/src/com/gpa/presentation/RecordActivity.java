package com.gpa.presentation;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import DBLayout.DBFunc;

import com.gpa.R;

public class RecordActivity extends Activity
{

	private MediaRecorder recorder;
	private String path;
	private TextView tv;
	private EditText name;
	private EditText cate;
	private int classId;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_record);

		tv = (TextView) findViewById(R.id.textViewClassHistory);

		Button startBtn = (Button) findViewById(R.id.bgnBtn);
		Button endBtn = (Button) findViewById(R.id.stpBtn);

		Bundle extras = getIntent().getExtras();
		classId = extras.getInt("classId");

		startBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				try
				{
					beginRecording();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		endBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				try
				{
					stopRecording();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void beginRecording() throws Exception
	{
		killMediaRecorder();
		path = AudioRecordFile();
		File outFile = new File(path);
		if (outFile.exists())
		{
			outFile.delete();
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);
		recorder.prepare();
		tv.setText("Recording");
		recorder.start();
	}

	private void stopRecording() throws Exception
	{
		DBshare dbshare = (DBshare) getApplication();
		final DBFunc dbfunc = dbshare.getDbfunc();
		if (recorder != null)
		{
			recorder.stop();
			tv.setText("");
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.dialog, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this)
					.setTitle("Save your note here").setView(textEntryView)
					.setPositiveButton("Save", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							//value is the class Id 
							name = (EditText) (textEntryView
									.findViewById(R.id.dialogEditTextNoteName));
							cate = (EditText) (textEntryView
									.findViewById(R.id.dialogEditTextCategory));
							String nameString = name.getText().toString();
							String categoryString = cate.getText().toString();
							
							//use the curent date if user does not enter anything
							if (nameString.equals("")){
								nameString = "Unamed";
							}
							if (categoryString.equals("")){
								categoryString = (new SimpleDateFormat("yyyy/MM/dd")
								.format(new Date())).toString();
							}
							
							dbfunc.insertNote(classId, nameString, path, categoryString, (new SimpleDateFormat("yyyy/MM/dd")
									.format(new Date())).toString());
							
							finish();
						}
					}).setNegativeButton("Discard", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							File file = new File(path);
							if (file.exists())
							{
								file.delete();
							}
							dialog.cancel();
							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();

		}

	}

	private void killMediaRecorder()
	{
		if (recorder != null)
		{
			recorder.release();
		}
	}

	public String AudioRecordFile()
	{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + timeStamp
				+ ".3gp";
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		killMediaRecorder();

	}
}
