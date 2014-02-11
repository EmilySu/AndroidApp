package com.gpa.presentation;

import java.util.Locale;

import DBLayout.DBFunc;
import DBLayout.SQLiteHelper;
import Utility.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.gpa.R;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	public final static int SUMMARY_TAB_ID = 0;
	public final static int CLASSES_TAB_ID = 1;
	public final static int TODOLIST_TAB_ID = 2;
	public final static int CALENDAR_TAB_ID = 3;

	public void removeAll()
	{
	    // db.delete(String tableName, String whereClause, String[] whereArgs);
	    // If whereClause is null, it will delete all rows.
		
		SQLiteHelper database = new SQLiteHelper(this);
		SQLiteDatabase db = database.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS schedule");
		db.execSQL("DROP TABLE IF EXISTS class");
		db.execSQL("DROP TABLE IF EXISTS task");
		db.execSQL("DROP TABLE IF EXISTS note");
		
		String table1 = "create table class(classId  INTEGER , className VARCHAR, location VARCHAR, startDate VARCHAR, endDate VARCHAR);";   
		String table2 = "create table schedule(scheduleId INTEGER PRIMARY KEY AUTOINCREMENT , eventId INTEGER, classId INTEGER, dayOfWeek VARCHAR, startTime VARCHAR, duration VARCHAR);";
		String table3 = "create table task(taskId INTEGER PRIMARY KEY AUTOINCREMENT, eventId INTEGER, classId INTEGER, taskName VARCHAR, dueTime VARCHAR, completionTime VARCHAR, priority VARCHAR, type VARCHAR);";
		String table4 = "create table note(noteId INTEGER PRIMARY KEY AUTOINCREMENT, classId INTEGER, noteName VARCHAR, notePATH VARCHAR, category VARCHAR, date VARCHAR);";

		db.execSQL(table1);
		db.execSQL(table2);
		db.execSQL(table3);
		db.execSQL(table4);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_gpa_presentation_activity_main);
		
		//use this to refresh database
		//removeAll(); 
		
		DBshare dbshare = (DBshare) getApplicationContext();

		DBFunc dbfunc = new DBFunc(this);
		dbshare.setDbfunc(dbfunc);

		//initial db 
		DBFunc db = new DBFunc(this.getBaseContext());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			switch (position)
			{
				case 0:
					Fragment fragment0 = new TodayFragment();
					return fragment0;

				case 1:
					Fragment fragment1 = new ClassesFragment();

					return fragment1;
				case 2:
					Fragment fragment2 = new TodoListFragment();
					return fragment2;
			}
			return null;
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			String currTab = "";
			switch (position)
			{
				case 0:
					currTab = "Summary";
					return currTab.toUpperCase(l);
				case 1:
					currTab = "Classes";
					return currTab.toUpperCase(l);
				case 2:
					currTab = "To-do List";
					return currTab.toUpperCase(l);
			}
			return null;
		}
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				//jump to To-Do list when received
				int classID = data.getIntExtra("classId", 0);
				String nextTab = data.getStringExtra("nextTab");

				if (nextTab != null && nextTab.equals("todoList"))
				{
					final ActionBar actionBar = this.getActionBar();
					actionBar.setSelectedNavigationItem(TODOLIST_TAB_ID);

					Util util = new Util();
					
					
					//then get that fragment and call its method to modify its data
					TodoListFragment todoListFragment = (TodoListFragment) getSupportFragmentManager().findFragmentByTag(
							util.makeFragmentName(this.mViewPager.getId(), TODOLIST_TAB_ID));

					todoListFragment.showTaskBasedOnClass(classID);
				}
			}
		}
	}
}
