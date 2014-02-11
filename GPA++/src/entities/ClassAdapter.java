package entities;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

public class ClassAdapter
{
	public ArrayList<Integer> adapteClassIdList(Cursor c) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(c.moveToFirst()) {
			c.move(0);
			while(true) {
				int classId = c.getInt(c.getColumnIndex("classId"));
				result.add(classId);
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}
	//adapt the result from sql query to something can be displayed
	public ArrayList<Course> adapteClassList(Cursor c)
	{
		ArrayList<Course> result = new ArrayList<Course>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				boolean exist = false;
				Course duplicate = null;
				int classId = c.getInt(c.getColumnIndex("classId"));
				String className = c.getString(c.getColumnIndex("className"));
				Log.v("classAdaptor", className);
				String location = c.getString(c.getColumnIndex("location"));
				String startDate = c.getString(c.getColumnIndex("startDate"));
				String endDate = c.getString(c.getColumnIndex("endDate"));
				String dayOfWeek = c.getString(c.getColumnIndex("dayOfWeek"));
				String startTime = c.getString(c.getColumnIndex("startTime"));
				String duration = c.getString(c.getColumnIndex("duration"));

				for (Course temp : result)
				{
					if (temp.classId == classId)
					{
						exist = true;
						duplicate = temp;
						break;
					}
				}
				if (exist)
				{
					duplicate.dayOfWeek.add(dayOfWeek);
					duplicate.startTime.add(startTime);
					duplicate.duration.add(duration);
				}
				else
				{
					ArrayList<String> dayOfWeekList = new ArrayList<String>();
					ArrayList<String> startTimeList = new ArrayList<String>();
					ArrayList<String> durationList = new ArrayList<String>();
					dayOfWeekList.add(dayOfWeek);
					startTimeList.add(startTime);
					durationList.add(duration);
					System.out.print(className);
					Course newClass = new Course(classId, className, location, startDate, endDate,
							dayOfWeekList, startTimeList, durationList);
					System.out.print(newClass.getName());
					result.add(newClass);
				}
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}
	
	//retrieve a list of class schedule IDs
	public ArrayList<Integer> adapteClassSchedules(Cursor c)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				result.add(c.getInt(c.getColumnIndex("scheduleId")));

				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}

	//first one is classInfo, second one is taskInfo, third one is noteInfo
	public ClassDetail adapteClassDetail(Cursor c1, Cursor c2, Cursor c3)
	{
		List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> classInfo = new ArrayList<String>();
		ArrayList<String> noteInfo = new ArrayList<String>();
		ArrayList<String> taskInfo = new ArrayList<String>();
		if (c1.moveToFirst())
		{
			c1.move(0);

			while (true)
			{
				int classId = c1.getInt(c1.getColumnIndex("classId"));
				String className = c1.getString(c1.getColumnIndex("className"));
				String location = c1.getString(c1.getColumnIndex("location"));
				String startDate = c1.getString(c1.getColumnIndex("startDate"));
				String endDate = c1.getString(c1.getColumnIndex("endDate"));
				String dayOfWeek = c1.getString(c1.getColumnIndex("dayOfWeek"));
				String startTime = c1.getString(c1.getColumnIndex("startTime"));
				//here duration is used as end time
				String endTime = c1.getString(c1.getColumnIndex("duration"));
				String sclassId = "" + classId;
				StringBuffer total = new StringBuffer(sclassId);
				total.append(",").append(className).append(",").append(location).append(",")
						.append(startDate).append(",").append(endDate).append(",")
						.append(dayOfWeek).append(",").append(startTime).append(",")
						.append(endTime);
				classInfo.add(total.toString());
				if (c1.isLast())
					break;
				c1.moveToNext();
			}
		}
		if (c2.moveToFirst())
		{
			c2.move(0);

			while (true)
			{
				String taskName = c2.getString(c2.getColumnIndex("taskName"));
				String dueTime = c2.getString(c2.getColumnIndex("dueTime"));
				taskInfo.add(taskName + dueTime);
				if (c2.isLast())
					break;
				c2.moveToNext();
			}
		}
		if (c3.moveToFirst())
		{
			c3.move(0);

			while (true)
			{
				String category = c3.getString(c3.getColumnIndex("category"));
				System.out.println(category);
				noteInfo.add(category);
				if (c3.isLast())
					break;
				c3.moveToNext();
			}
		}

		ClassDetail classDetail = new ClassDetail(classInfo, noteInfo, taskInfo);

		return classDetail;
	}
	
	
}
