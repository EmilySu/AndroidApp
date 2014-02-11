package com.gpa.presentation;

import java.util.ArrayList;

import DBLayout.DBFunc;
import Utility.Util;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gpa.R;

import entities.ClassAdapter;
import entities.Course;

public class ClassesFragment extends Fragment
{
	private View rootView;


	//[dummy] need retrieve from database

	public ClassesFragment()
	{
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.com_gpa_presentation_fragment_classes, container,
				false);
		return rootView;
	}

	public String[] getClassList()
	{
		DBshare dbshare = (DBshare) getActivity().getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayClassList();
		ClassAdapter ca = new ClassAdapter();
		ArrayList<Course> al = ca.adapteClassList(c);
		//		Toast.makeText(getActivity(), al.get(0).getName(), Toast.LENGTH_LONG).show();
		String[] str = new String[al.size()];

		for (int i = 0; i < al.size(); i++)
		{
			String name = al.get(i).getName();
			int id = al.get(i).getClassId();
			String location = al.get(i).getLocation();
			ArrayList<String> startTime = al.get(i).getStartTime();
			ArrayList<String> durationTime = al.get(i).getDuration();
			ArrayList<String> whichday = al.get(i).getDayOfWeek();
			str[i] = formatClass(name, id, location, startTime, durationTime, whichday);
		}
		return str;
	}

	public String formatClass(String name, int id, String location, ArrayList<String> startTime,
			ArrayList<String> durationTime, ArrayList<String> whichday)
	{

		String ret = "<b>" + id + " " + name + " - " + location + "</b><br/><small>";
		for (int i = 0; i < startTime.size(); i++)
		{
			String day = whichday.get(i);
			String st = startTime.get(i);
			String dur = durationTime.get(i);
			ret += day + ": " + st + " - " + dur + "<br/>";
		}
		return ret + "</small>";
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// rootView = inflater.inflate(R.layout.com_gpa_presentation_fragment_classes, container, false);
		ListView lv_classes = (ListView) rootView.findViewById(R.id.fragmentClassesListview);

		String[] classes = getClassList();
		
		//show an empty class message if there are no classes set up
		if (classes.length == 0)
		{
			classes = new String[1];
			classes[0] = "No classes have been set up.\nPlease add a class.";
			lv_classes.setEnabled(false);
		}
		else
		{
			lv_classes.setEnabled(true);
		}
		final String[] classList = classes;
		
		lv_classes.setTextFilterEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, classList)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);

				text1.setText(Html.fromHtml(classList[position]));
				return view;
			}

		};
		lv_classes.setAdapter(adapter);

		lv_classes.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//show the details of the class
				int[] classIDs = getClassIDs();

				int currentID = classIDs[position];
				Intent intent = new Intent(getActivity().getApplicationContext(),
						ClassDetailActivity.class);
				intent.putExtra("classId", currentID);
				getActivity().startActivityForResult(intent, 1);
			}
		});
		
		lv_classes.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				//edit the details of the class
				int[] classIDs = getClassIDs();

				int currentID = classIDs[position];
				Intent intent = new Intent(getActivity().getApplicationContext(),
						EditClass.class);
				intent.putExtra("classId", currentID);
				getActivity().startActivityForResult(intent, 1);
				return false;
			}
		});

		Button addButton = (Button) rootView.findViewById(R.id.fragmentClassesAddClassesButton);
		//Pass-in-data: -1 for adding new classes
		//next intent: EditClass
		addButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), EditClass.class);
				intent.putExtra("classId", -1);
				startActivity(intent);
			}
		});
	}
	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		if (requestCode == 1)
//		{
//			if (resultCode == Activity.RESULT_OK)
//			{
//				//jump to To-Do list when received
//				int classID = data.getIntExtra("classId", 0);
//				String nextTab = data.getStringExtra("nextTab");
//
//				if (nextTab != null && nextTab.equals("todoList"))
//				{
//					MainActivity mainActivity = (MainActivity) getActivity();
//					final ActionBar actionBar = mainActivity.getActionBar();
//					actionBar.setSelectedNavigationItem(2);
//
//					Util util = new Util();
//					
//					android.support.v4.app.Fragment supportFragment = new Fragment();
//
//					//then get that fragment and call its method to modify its data
//					TodoListFragment todoListFragment = (TodoListFragment) supportFragment.getFragmentManager().findFragmentByTag(
//							util.makeFragmentName(mainActivity.mViewPager.getId(), 2));
//
//					//f.setLabel("Hello There!");
//				}
//			}
//		}
//	}

	public int[] getClassIDs()
	{
		DBshare dbshare = (DBshare) getActivity().getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		Cursor c = dbfunc.displayClassList();
		ClassAdapter ca = new ClassAdapter();
		ArrayList<Course> al = ca.adapteClassList(c);
		//		Toast.makeText(getActivity(), al.get(0).getName(), Toast.LENGTH_LONG).show();
		int[] strid = new int[al.size()];

		for (int i = 0; i < al.size(); i++)
		{

			int tempid = al.get(i).getClassId();
			strid[i] = tempid;
		}

		return strid;

	}

	public void highlightItem(int position)
	{
		ListView listView = (ListView) rootView.findViewById(R.id.fragmentClassesListview);
		//animate the second item
		int wantedPosition = position; // Whatever position you're looking for
		int firstPosition = listView.getFirstVisiblePosition()
				- listView.getHeaderViewsCount(); // This is the same as child #0
		int wantedChild = wantedPosition - firstPosition;
		// Say, first visible position is 8, you want position 10, wantedChild will now be 2
		// So that means your view is child #2 in the ViewGroup:
		if (wantedChild < 0 || wantedChild >= listView.getChildCount())
		{
			return;
		}
		// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
		View wantedView = listView.getChildAt(wantedChild);

		TextView cv = (TextView) wantedView.findViewById(android.R.id.text1);
		
		Util util = new Util();
		
		util.highlight(cv);
	}
	

}