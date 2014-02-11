package entities;

import java.util.ArrayList;

public class ClassDetail
{
	ArrayList<String> classInfo = new ArrayList<String>();
	ArrayList<String> noteInfo = new ArrayList<String>();
	ArrayList<String> taskInfo = new ArrayList<String>();

	int classID;
	String className;
	String location;
	String startDate;
	String endDate;


	public ClassDetail(ArrayList<String> classInfo, ArrayList<String> noteInfo,
			ArrayList<String> taskInfo)
	{

		this.classInfo = classInfo;
		this.noteInfo = noteInfo;
		this.taskInfo = taskInfo;
		parseBasicInfo(classInfo);
	}

	public void parseBasicInfo(ArrayList<String> classInfo)
	{
		//Log
		//System.out.println(noteInfo.size());
		if (classInfo == null)
		{
			classID = 0;
			className = "empty";
			location = "empty";
			startDate = "empty";
			endDate = "empty";
		}
		else
		{
			String str = classInfo.get(0);
			String slist[] = str.split(",");
			classID = Integer.parseInt(slist[0]);
			className = slist[1];
			location = slist[2];
			startDate = slist[3];
			endDate = slist[4];
		}
	}

	//this should include weekday and time
	public ArrayList<String> getScheds()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for (String str : classInfo)
		{
			String[] slist = str.split(",");
			String dayOfWeek = slist[5];
			String startTime = slist[6];
			String duration = slist[7];

			//here duration is used as end time
			String result = dayOfWeek + ": " + startTime + " - " + duration + "\n";
			ret.add(result);
		}
		return ret;
	}

	public ArrayList<String> getClassInfo()
	{
		return classInfo;
	}

	public void setClassInfo(ArrayList<String> classInfo)
	{
		this.classInfo = classInfo;
	}

	public ArrayList<String> getNoteInfo()
	{
		return noteInfo;
	}

	public void setNoteInfo(ArrayList<String> noteInfo)
	{
		this.noteInfo = noteInfo;
	}

	public ArrayList<String> getTaskInfo()
	{
		return taskInfo;
	}

	public void setTaskInfo(ArrayList<String> taskInfo)
	{
		this.taskInfo = taskInfo;
	}

	public int getClassID()
	{
		return classID;
	}

	public void setClassID(int classID)
	{
		this.classID = classID;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

}
