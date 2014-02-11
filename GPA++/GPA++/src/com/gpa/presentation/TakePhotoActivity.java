package com.gpa.presentation;


/**
 * @author Jose Davis Nidhin
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gpa.R;

import DBLayout.DBFunc;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class TakePhotoActivity extends Activity
{
	private static final String TAG = "CamTestActivity";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean isRecording = false;
	//private MediaRecorder mMediaRecorder;
	Preview preview;
	Button picture, video;
	Camera camera;
	String path;
	private EditText name;
	private EditText cate;
	private int classId;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_take_photo);
		camera = getCameraInstance();
		if (camera == null)
		{
			Toast.makeText(this, "Cannot access camera at this time.", Toast.LENGTH_LONG).show();
			finish();
		}
		preview = new Preview(this, camera);
		FrameLayout preview1 = (FrameLayout) findViewById(R.id.preview);
		preview1.addView(preview);
		picture = (Button) findViewById(R.id.picture);
		picture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// get an image from the camera
				camera.takePicture(null, null, mPicture);
			}
		});

		Bundle extras = getIntent().getExtras();
		classId = extras.getInt("classId");
	}

	public void setCaptureButtonText(String s)
	{
		video.setText(s);
	}

	public static Camera getCameraInstance()
	{
		Camera c = null;
		try
		{
			c = Camera.open(); // attempt to get a Camera instance
			if (c == null)
			{
				c = Camera.open(Camera.getNumberOfCameras() - 1);
			}
		}
		catch (Exception e)
		{
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback()
	{
		@Override
		public void onPictureTaken(final byte[] data, Camera camera)
		{
			final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null)
			{
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}
			DBshare dbshare = (DBshare) getApplication();
			final DBFunc dbfunc = dbshare.getDbfunc();
			LayoutInflater factory = LayoutInflater.from(TakePhotoActivity.this);
			final View textEntryView = factory.inflate(R.layout.dialog, null);
			//et = new EditText(TakePhotoActivity.this);
			AlertDialog.Builder builder = new AlertDialog.Builder(TakePhotoActivity.this)
					.setTitle("Save your note here").setView(textEntryView)
					.setPositiveButton("Save", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							try
							{
								FileOutputStream fos = new FileOutputStream(pictureFile);
								fos.write(data);
								fos.close();
								name = (EditText) (textEntryView
										.findViewById(R.id.dialogEditTextNoteName));
								cate = (EditText) (textEntryView
										.findViewById(R.id.dialogEditTextCategory));

								String nameString = name.getText().toString();
								String categoryString = cate.getText().toString();

								//use the curent date if user does not enter anything
								if (nameString.equals(""))
								{
									nameString = "Unamed";
								}
								if (categoryString.equals(""))
								{
									categoryString = (new SimpleDateFormat("yyyy/MM/dd")
											.format(new Date())).toString();
								}

								dbfunc.insertNote(classId, nameString, path, categoryString,
										(new SimpleDateFormat("yyyy/MM/dd").format(new Date()))
												.toString());

								finish();
							}
							catch (FileNotFoundException e)
							{
								Log.d(TAG, "File not found: " + e.getMessage());
							}
							catch (IOException e)
							{
								Log.d(TAG, "Error accessing file: " + e.getMessage());
							}
						}
					}).setNegativeButton("Discard", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			resetCam();
		}
	};


	private Uri getOutputMediaFileUri(int type)
	{
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private File getOutputMediaFile(int type)
	{
		File mediaStorage = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		if (!mediaStorage.exists())
		{
			if (!mediaStorage.mkdir())
			{
				Log.d("MyCameraApp", "failed to create directory");
				return null;

			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE)
		{
			path = mediaStorage.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorage.getPath() + File.separator + "IMG_" + timeStamp
					+ ".jpg");
		}
		else if (type == MEDIA_TYPE_VIDEO)
		{
			path = mediaStorage.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorage.getPath() + File.separator + "VID_" + timeStamp
					+ ".mp4");
		}
		else
		{
			return null;
		}

		return mediaFile;
	}

	protected void onPause()
	{
		super.onPause();
		camera.stopPreview();
		//releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		releaseCamera(); // release the camera immediately on pause event
		camera = null;
	}

	private void releaseCamera()
	{
		if (camera != null)
		{
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}

	private void resetCam()
	{
		camera.startPreview();
		//preview.setCamera(camera);
	}

}