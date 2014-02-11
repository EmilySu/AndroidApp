package com.gpa.presentation;

import java.util.ArrayList;

import DBLayout.DBFunc;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gpa.R;

import entities.NoteAdapter;

public class ClassHistory extends Activity
{

	//static final String[] images = new String[] { "image1", "image2" };
	//static final String[] videos = new String[] { "video1", "video2" };
	//static final String[] audios = new String[] { "audio1", "audio2" };
	ArrayList<String> categories;
	int classId;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_class_history);

		Bundle extras = getIntent().getExtras();
		classId = extras.getInt("classId");

		DBshare dbshare = (DBshare) getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayCategories(classId);

		categories = (new NoteAdapter()).adapteNoteCategories(c);

		ListView lv_category = (ListView) findViewById(R.id.lv_lectures);
		lv_category.setTextFilterEnabled(true);
		
		//show an empty list message if there are no recorded notes
		if (categories.isEmpty())
		{
			categories.add("No recorded notes in previous lectures,\nPlease record a note.");
			lv_category.setEnabled(false);
		}
		else
		{
			lv_category.setEnabled(true);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categories)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);

				text1.setText(Html.fromHtml(formatCategories(categories.get(position).split(",")[0],
						categories.get(position).split(",")[1])));
				return view;
			}
		};

		lv_category.setAdapter(adapter);
		lv_category.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String category = categories.get(position).split(",")[0];
				Intent intent = new Intent(ClassHistory.this, NotesDetail.class);
				intent.putExtra("noteCategory", category);
				intent.putExtra("classId", classId);
				startActivity(intent);
			}
		});
	}

	public String formatCategories(String category, String date)
	{
		String ret = "<b>" + category + "</b><br/><small>" + date;
		return ret + "</small>";
	}
}
