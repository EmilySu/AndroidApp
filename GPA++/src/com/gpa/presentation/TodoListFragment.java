package com.gpa.presentation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import DBLayout.DBFunc;
import Utility.Util;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.gpa.R;

import entities.TaskAdapter;
import entities.TaskIdAdapter;

public class TodoListFragment extends Fragment
{
	private View rootView;
	ArrayList<Integer> uniqueKeyList = new ArrayList<Integer>();
	ArrayList<String> tasks = new ArrayList<String>();
	int classID;


	public TodoListFragment()
	{
		classID = 0;
	}

	public String[] getTodoTask(ArrayList<Integer> uniqueKeyList, ArrayList<String> tasks)
	{

		DBshare dbshare = (DBshare) getActivity().getApplication();
		DBFunc dbfunc = dbshare.getDbfunc();
		List<String> al = new ArrayList<String>();
		Cursor c = dbfunc.displayTaskList();
		al = (new TaskAdapter()).adapteTaskList(c);
		String[] array = new String[al.size()];
		int j = 0;
		uniqueKeyList.clear();
		tasks.clear();
		for (int i = 0; i < al.size(); i++)
		{
			String[] split = al.get(i).split(",");
			if (!split[5].equals("Unfinished"))
				continue;
			array[j] = "<b>" + split[1] + " " + split[0] + " | Priority: " + split[4] + "</b><br/><small>Due Date:" + split[2]
					+ split[3] + ", " + split[5]+"</small>";
			j++;
			uniqueKeyList.add(Integer.parseInt(split[6]));
			Log.v("split size", split[5]);
			tasks.add(split[0] + "," + split[1] + "," + split[2] + "," + split[3] + "," + split[4]
					+ "," + split[5]);
		}
		for (int i = 0; i < al.size(); i++)
		{
			String[] split = al.get(i).split(",");
			Log.v("split 5", split[5]);
			if (split[5].equals("Unfinished"))
				continue;
			array[j] = "<b>" + split[1] + " " + split[0] + " | Priority: " + split[4] + "</b><br/><small>Due Date:" + split[2]
					+ split[3] + ", Finished on: " + split[5]+"</small>";

			Log.v("debug2",	Arrays.toString(array));
			j++;
			//TODO refine the stuff here

			uniqueKeyList.add(Integer.parseInt(split[6]));

			tasks.add(split[0] + "," + split[1] + "," + split[2] + "," + split[3] + "," + split[4]
					+ "," + split[5]);
		}
		return array;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		//show the todo tasks
		showTasks();

		//set button listener
		Button addButton = (Button) rootView.findViewById(R.id.fragmentToDoaddtodoButton);

		addButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent it = new Intent(getActivity(), EditTask.class);
				//put a task ID of -1 to signify creating a new task
				it.putExtra("taskId", -1);
				startActivity(it);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.com_gpa_presentation_fragment_todo, container, false);

		return rootView;
	}

	/**
	 * Show the to-do task based on the preference of classes
	 * 
	 * @param classId - class ID to base the class preference on
	 */
	public void showTaskBasedOnClass(int classId)
	{
		this.classID = classId;
	}

	/**
	 * Helper method to display the to-do tasks in a listview
	 */
	private void showTasks()
	{
		String[] todoList;
		//no preference
		if (this.classID <= 0)
		{
			todoList = getTodoTask(uniqueKeyList, tasks);
		}
		else
		{
			//get to-do tasks based on class
			todoList = getTodoTask(uniqueKeyList, tasks);
		}

		ListView lv_todoTasks = (ListView) rootView.findViewById(R.id.fragmentToDoListView);
		lv_todoTasks.setTextFilterEnabled(true);

		//show an empty to-do list message if there are no to-do tasks have been set up
		if (todoList.length == 0)
		{
			todoList = new String[1];
			todoList[0] = "No To-do tasks.\nPlease add a to-do task.";
			lv_todoTasks.setEnabled(false);
		}
		else
		{
			lv_todoTasks.setEnabled(true);
		}

		final String[] todoTasks = todoList;

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_checked, todoTasks)
				{
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View view = super.getView(position, convertView, parent);
				CheckedTextView text1 = (CheckedTextView) view.findViewById(android.R.id.text1);

				text1.setText(Html.fromHtml(todoTasks[position]));

				String[] split = todoTasks[position].split(",");
				if (split.length == 2)
				{
					if (split[1].contains("Unfinished"))
					{
						text1.setBackgroundColor(0x00FFFFFF);
						text1.setAlpha(1f);
						text1.setChecked(false);
					}
					else
					{
						text1.setBackgroundColor(0x3F0000FF);
						text1.setAlpha(0.25f);
						text1.setChecked(true);
					}
				}

				return view;
			}
				};

				lv_todoTasks.setAdapter(adapter);

				lv_todoTasks.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView parent, View v, int position, long id)
					{
						DBshare dbshare = (DBshare) getActivity().getApplication();
						DBFunc dbfunc = dbshare.getDbfunc();
						CheckedTextView cv = (CheckedTextView) v.findViewById(android.R.id.text1);
						//toggle the checkmark
						cv.setChecked(!cv.isChecked());

						//dim the checked item
						if (cv.isChecked())
						{
							int taskId = uniqueKeyList.get(position);
							String row = tasks.get(position);
							String[] split = row.split(",");
							Date current = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String c = sdf.format(current);
							dbfunc.updateTask(taskId, Integer.parseInt(split[0]), split[1], split[2] + ","
									+ split[3], c, split[4]);
							//cv.setAlpha(0.25f);
							//highlight

							//cv.setBackgroundColor(0x3F0000FF);
						}
						else
						{
							//no highlight
							int taskId = uniqueKeyList.get(position);
							String row = tasks.get(position);
							String[] split = row.split(",");
							dbfunc.updateTask(taskId, Integer.parseInt(split[0]), split[1], split[2] + ","
									+ split[3], "Unfinished", split[4]);
							//cv.setBackgroundColor(0x00FFFFFF);
							//cv.setAlpha(1f);

						}
						//ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
						//adapter.notifyDataSetChanged();

						showTasks();
					}
				});

				lv_todoTasks.setOnItemLongClickListener(new OnItemLongClickListener()
				{
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id)
					{
						Log.v("long clicked", "pos: " + pos);

						int TASKID = uniqueKeyList.get(pos);

						Intent it = new Intent(getActivity().getApplicationContext(), EditTask.class);
						it.putExtra("taskId", TASKID);
						startActivity(it);

						return true;
					}
				});
	}

	public void highlightItem(int position)
	{
		ListView listView = (ListView) rootView.findViewById(R.id.fragmentToDoListView);
		//animate the second item
		int wantedPosition = position; // Whatever position you're looking for
		int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
		int wantedChild = wantedPosition - firstPosition;
		// Say, first visible position is 8, you want position 10, wantedChild will now be 2
		// So that means your view is child #2 in the ViewGroup:
		if (wantedChild < 0 || wantedChild >= listView.getChildCount())
		{
			return;
		}
		// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
		View wantedView = listView.getChildAt(wantedChild);

		CheckedTextView cv = (CheckedTextView) wantedView.findViewById(android.R.id.text1);

		Util util = new Util();

		util.highlight(cv);
	}
}
