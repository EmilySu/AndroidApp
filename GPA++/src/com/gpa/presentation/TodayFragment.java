package com.gpa.presentation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import DBLayout.DBFunc;
import Utility.Util;
import android.app.ActionBar;
import android.database.Cursor;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gpa.R;

import entities.ClassAdapter;
import entities.Course;
import entities.TaskAdapter;


public class TodayFragment extends Fragment
{
	//[dummy] need retrieve from database
	private View rootView;
	//static final String[] classesToday = new String[] { "18641 JavaSmartPhone", "18648 R-T Embedded" };
	//static final String[] upc = new String[] { "18641 quiz3", "18648 quiz4" };
	Calendar rightNow;
	HashMap<String, Integer> map = new HashMap<String, Integer>();

	public TodayFragment()
	{
	}

	//get upcoming two day class
	public String[] getUpcomingClassList(int dayOfWeek)
	{
		DBshare dbshare = (DBshare) getActivity().getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayClassList();
		ClassAdapter ca = new ClassAdapter();

		ArrayList<Course> al = ca.adapteClassList(c);
		//		String[] str = new String[al.size()] ; 

		ArrayList<String> ret = new ArrayList<String>();

		for (int i = 0; i < al.size(); i++)
		{
			String name = al.get(i).getName();
			int id = al.get(i).getClassId();
			String location = al.get(i).getLocation();

			ArrayList<String> startTime = al.get(i).getStartTime();
			ArrayList<String> durationTime = al.get(i).getDuration();
			ArrayList<String> dayOfWeekList = al.get(i).getDayOfWeek();
			//ArrayList<String> whichday = getRecentList(dayOfWeekList, dayOfWeek); 

			String formatedString = formatClass(name, id, location, startTime, durationTime,
					dayOfWeekList, dayOfWeek);
			if (formatedString != null)
				ret.add(formatedString);

		}

		String[] arr = ret.toArray(new String[ret.size()]);
		return arr;
	}

	public ArrayList<String> getRecentList(ArrayList<String> dayOfWeekList, int dayOfWeek)
	{
		ArrayList<String> ret = new ArrayList<String>();
		if (dayOfWeek == 6)
			dayOfWeek = 5; //saturday and sunday corner case

		for (String str : dayOfWeekList)
		{
			int dayid = map.get(str);
			if (dayid == (dayOfWeek) % 5 || dayid == (dayOfWeek + 1) % 5)
				ret.add(str);
		}
		return ret;
	}

	public String formatClass(String name, int id, String location, ArrayList<String> startTime,
			ArrayList<String> durationTime, ArrayList<String> whichday, int dayOfWeek)
	{
		if (dayOfWeek == 6)
			dayOfWeek = 5; //saturday and sunday corner case
		String ret = "<b>" + id + " " + name + " - " + location + "</b><br/><small>";
		Boolean nonAvailiable = true;
		for (int i = 0; i < startTime.size(); i++)
		{

			Log.v("which day", whichday.toString());
			String day = whichday.get(i);
			Log.v("debug1",day); 

			int dayid = map.get(day);
			//int dayid = 2;
			Log.v("which int", dayid + "");

			if (dayid == (dayOfWeek) % 5 || dayid == (dayOfWeek + 1) % 5)
			{
				nonAvailiable = false;
				String st = startTime.get(i);
				String dur = durationTime.get(i);
				ret += day + "," + st + "," + dur + " hours" + " " + "<br/>";
			}
		}
		Log.v("isokay", nonAvailiable + "");
		if(nonAvailiable == false) {
		String[] sp = ret.split(",");
		Log.v("split", sp[1]);
		}
		return (nonAvailiable == true) ? null : ret + "</small>";
	}
	public void sort(String[] classToday) {
		for(int j = 0; j < classToday.length - 1; j ++) {
			for(int i = classToday.length - 1; i > j; i --) {
				String[] split1 = classToday[i].split(",");
				String[] split11 = split1[1].split(":");
				String[] split2 = classToday[i - 1].split(",");
				String[] split21 = split2[1].split(":");
				if(Integer.valueOf(split11[0]) < Integer.valueOf(split21[0])) {
					String temp = classToday[i];
					classToday[i] = classToday[i - 1];
					classToday[i - 1] = temp;
				}
					
			}
		}
	}
	public ArrayList<String> getUpcomingTask() {
		DBshare dbshare = (DBshare) getActivity().getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayTaskList();
		TaskAdapter tA = new TaskAdapter();
		ArrayList<String> result = tA.adapteTaskList(c);
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < result.size(); i ++) {
			String[] split = result.get(i).split(",");
			if(!split[5].equals("Unfinished"))
				continue;
			ret.add("<b>" + split[1] + " " + split[0] + " | Priority: " + split[4] + "</b><br/>Due Date: " + split[2] + split[3]);
		}
		return ret;
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.com_gpa_presentation_fragment_today, container, false);
		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		map = new HashMap<String, Integer>();

		map.put("Monday", 0);
		map.put("Tuesday", 1);
		//map.put("Tusday", 1);
		map.put("Wednesday", 2);
		map.put("Thursday", 3);
		map.put("Friday", 4);

		rightNow = Calendar.getInstance();
		int dayOfWeek = rightNow.get(rightNow.DAY_OF_WEEK) - 2;

		Log.v("right now", dayOfWeek + "");

		final String[] classesToday = getUpcomingClassList(dayOfWeek);
		sort(classesToday);
		ListView lv_classes = (ListView) rootView
				.findViewById(R.id.fragmentTodayClassTodayListview);
		lv_classes.setTextFilterEnabled(true);

		//format the texts using HTML
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, classesToday)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);

				text1.setText(Html.fromHtml(classesToday[position]));
				return view;
			}
		};

		
	
		lv_classes.setAdapter(adapter);

		//hightlight the selected classes
		lv_classes.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id)
			{

				final ActionBar actionBar = getActivity().getActionBar();
				MainActivity currActivity = (MainActivity) getActivity();
				Util util = new Util();

				//jump to the tab first
				actionBar.setSelectedNavigationItem(MainActivity.CLASSES_TAB_ID);
				//then get that fragment and call its method to modify its data
				ClassesFragment fragment = (ClassesFragment) getFragmentManager()
						.findFragmentByTag(
								util.makeFragmentName(currActivity.mViewPager.getId(),
										MainActivity.CLASSES_TAB_ID));
				fragment.highlightItem(position);
			}
		});

		final String[] upc = getUpcomingTask().toArray(new String[getUpcomingTask().size()]);
		if(upc.length == 1 && upc[0] == null)
			upc[0] = "No unfinished to-do task now";
				//getUpcomingClassList(dayOfWeek);
		//Log.v("upc size", upc[0]);
		//upcoming todo tasks
		ListView lv_upc = (ListView) rootView.findViewById(R.id.fragmentTodayUpcomingListview);
		lv_upc.setTextFilterEnabled(true);
		ArrayAdapter<String> adapter_upc = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, upc)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);

				text1.setText(Html.fromHtml(upc[position]));
				return view;
			}
		};

		lv_upc.setAdapter(adapter_upc);

		lv_upc.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id)
			{

				final ActionBar actionBar = getActivity().getActionBar();

				MainActivity currActivity = (MainActivity) getActivity();
				Util util = new Util();

				//jump to the tab first
				actionBar.setSelectedNavigationItem(MainActivity.TODOLIST_TAB_ID);

				//then get that fragment and call its method to modify its data
				TodoListFragment fragment = (TodoListFragment) getFragmentManager()
						.findFragmentByTag(
								util.makeFragmentName(currActivity.mViewPager.getId(),
										MainActivity.TODOLIST_TAB_ID));
				fragment.highlightItem(position);
			}
		});

		Button calendarButton = (Button) rootView.findViewById(R.id.buttonSummaryCalendar);

		calendarButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				viewCalendar();
			}
		});
	}

	private void viewCalendar()
	{
		long startMillis = System.currentTimeMillis();

		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");

		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
		startActivity(intent);
	}
}