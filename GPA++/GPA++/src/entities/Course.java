package entities;

import java.util.ArrayList;

public class Course {
	int classId;
	String className;
	String location;
	String startDate;
	String endDate;
	ArrayList<String> dayOfWeek;
	ArrayList<String> startTime;
	ArrayList<String> duration;
	
	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ArrayList<String> getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(ArrayList<String> dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public ArrayList<String> getStartTime() {
		return startTime;
	}

	public void setStartTime(ArrayList<String> startTime) {
		this.startTime = startTime;
	}

	public ArrayList<String> getDuration() {
		return duration;
	}

	public void setDuration(ArrayList<String> duration) {
		this.duration = duration;
	}

	public String getName() {
		return className;
	}
	/*public Course() {
		dayOfWeek = new ArrayList<String>();
		startTime = new ArrayList<String>();
		duration = new ArrayList<String>();
	}*/
	
	public Course(int classId, String className, String location, String startDate, String endDate, ArrayList<String> dayOfWeek, ArrayList<String> startTime, ArrayList<String> duration) {
		this.classId = classId;
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.duration = duration;
		this.className =className;
	}

}
