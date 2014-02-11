package com.gpa.presentation;


import java.util.ArrayList;

import com.gpa.R;

import entities.NoteAdapter;
import DBLayout.DBFunc;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NotesDetail extends Activity
{
	ArrayList<String> namesPhoto = new ArrayList<String>();
	ArrayList<String> pathsPhoto = new ArrayList<String>();

	ArrayList<String> namesAudio = new ArrayList<String>();
	ArrayList<String> pathsAudio = new ArrayList<String>();


	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_notes_detail);

		Bundle extras = getIntent().getExtras();
		String category = extras.getString("noteCategory");
		int classId = extras.getInt("classId");

		DBshare dbshare = (DBshare) getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayNoteList(classId, category);
		NoteAdapter na = new NoteAdapter();
		ArrayList<String> noteList = new ArrayList<String>();
		noteList = na.adapteNote(c);

		//separate the note names with its file path
		preprocessing(noteList);
		
		ListView lv_image = (ListView) findViewById(R.id.notesDetailNotesListView);
		lv_image.setTextFilterEnabled(true);
		
		//show an empty list message if there are no recorded notes
		if (namesPhoto.isEmpty())
		{
			namesPhoto.add("No recorded visual notes.\nPlease record a visual note.");
			lv_image.setEnabled(false);
		}
		else
		{
			lv_image.setEnabled(true);
		}

		ArrayAdapter<String> adapter_images = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, namesPhoto);

		lv_image.setAdapter(adapter_images);

		//when a note is tapped, display the note
		lv_image.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + pathsPhoto.get(position)), "image/*");
				startActivity(intent);
			}
		});

		//when a note is held, share it
		lv_image.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id)
			{
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/*");

				share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathsPhoto.get(pos)));

				startActivity(Intent.createChooser(share, "Share Visual Note"));
				return true;
			}
		});

		ListView lv_audio = (ListView) findViewById(R.id.editClass_lv_schedule);
		lv_audio.setTextFilterEnabled(true);
		
		//show an empty list message if there are no recorded notes
		if (namesAudio.isEmpty())
		{
			namesAudio.add("No recorded audio notes.\nPlease record an audio note.");
			lv_audio.setEnabled(false);
		}
		else
		{
			lv_audio.setEnabled(true);
		}
		
		ArrayAdapter<String> adapter_audios = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, namesAudio);

		lv_audio.setAdapter(adapter_audios);

		//when a note is tapped, display the note
		lv_audio.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + pathsAudio.get(position)), "audio/*");
				startActivity(intent);
			}
		});

		//when a note is held, share it
		lv_audio.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id)
			{
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("audio/*");

				share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathsAudio.get(pos)));

				startActivity(Intent.createChooser(share, "Share Audio Note"));
				return true;
			}
		});
	}

	/**
	 * Splits the input notes into either audio or picture
	 * 
	 * @param noteList - a list of input string array to be split
	 */
	private void preprocessing(ArrayList<String> noteList)
	{
		for (int i = 0; i < noteList.size(); i++)
		{
			System.out.println(noteList.get(i));
			String[] slist = noteList.get(i).split(",");

			String audioFormat = ".*3gp";
			if (slist[1].matches(audioFormat))
			{
				namesAudio.add(slist[0] + "  " + slist[2]);
				pathsAudio.add(slist[1]);

			}
			else
			{
				namesPhoto.add(slist[0] + "  " + slist[2]);
				pathsPhoto.add(slist[1]);
			}
		}
	}

	//TODO Add intent to go to the view video activity
	//if the button "Open" has been tapped, 
	//Pass the note name into this intent 


	//TODO Add intent to go to the listen audio activity
	//if the button "Open" has been tapped, 
	//Pass the note name into this intent 


	////TODO Add intent to go to the share notes activity
	//if the button "Open" has been tapped, 
	//Pass the note name into this intent 
}
