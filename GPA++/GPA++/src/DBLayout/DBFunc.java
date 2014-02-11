package DBLayout;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBFunc {
	private SQLiteDatabase db = null;
	//initialize and build connect with database
	public DBFunc(Context context) {
		SQLiteHelper database = new SQLiteHelper(context);
		db = database.getWritableDatabase();
	}
	
	public Cursor getKey(){
		Cursor c = db.rawQuery("select last_insert_rowid()", null); 
		return c; 
	}
	//execute query on the table class and schedule to retrieve all classes
	public Cursor displayClassList() {
		Cursor c =db.rawQuery("SELECT class.*, schedule.dayOfWeek, schedule.startTime, schedule.duration FROM class, schedule where class.classId = schedule.classId", null);
		return c;
	}
	//execute query on the table to-do-task to retrieve all tasks
	public Cursor displayTaskList() {
		Cursor c =db.rawQuery("SELECT taskId, classId, taskName, dueTime, priority, completionTime FROM task  ORDER BY dueTime", null);
		return c;
	}
	public Cursor displayTaskDetails(int taskId) {
		Cursor c =db.rawQuery("SELECT taskId, classId, taskName, dueTime, priority, completionTime FROM task WHERE taskId = " + taskId, null);
		return c;
	}
	public Cursor displayClassId() {
		Cursor c = db.rawQuery("SELECT classId from class", null);
		return c;
	}
	
	public Cursor displayTaskIdList(){
		Cursor c =db.rawQuery("SELECT taskId FROM task", null);
		return c;
	}
	
	public Cursor displayCategories(int classId){
		Cursor c =db.rawQuery("SELECT DISTINCT category, date FROM note WHERE classID = " + classId, null);
		return c;
	}
	
	
	//execute query on the table note to retrieve all note category
	/*public Cursor displayNoteCategoryList() {
		Cursor c =db.rawQuery("SELECT distinct categorty FROM note", null);
		return c;
	}*/
	//execute query on the table note to retrieve all notes about a specific category
	public Cursor displayNoteList(int classId, String category) {
		Cursor c =db.rawQuery("SELECT * FROM note WHERE category = '" + category + "' and classId = " + classId, null);
		return c;
	}
	public Cursor getAllNotePaths(int classId)
	{
		Cursor c =db.rawQuery("SELECT notePATH FROM note WHERE classId = " + classId, null);
		return c;
	}
    //execute query on the table class, schedule, task and note to retrieve info about a specific class
	public Cursor[] displayClassDetail(int classID) {
		Cursor c2 =db.rawQuery("SELECT task.taskName, task.dueTime FROM class, task WHERE class.classId = " + classID + " and task.classId = " + classID, null);
		Cursor c1 =db.rawQuery("SELECT class.*, schedule.dayOfWeek, schedule.startTime, schedule.duration FROM class, schedule WHERE class.classId = " + classID + " and schedule.classId = " + classID, null);
		Cursor c3 =db.rawQuery("SELECT distinct note.category FROM class, note WHERE class.classId = " + classID + " and note.classId = " + classID, null);
		Cursor[] c = {c1, c2, c3};
		return c;
	}
	public Cursor getScheduleIds(int classID) {
		Cursor c = db.rawQuery("SELECT scheduleId FROM schedule WHERE classId = " + classID, null);
		return c;
	}
	public Cursor getNoteIds(int classID) {
		Cursor c = db.rawQuery("SELECT noteId FROM note WHERE classId = " + classID, null);
		return c;
	}
	
	//insert a new class to class table
	public void insertClass(int classId, String className, String location, String startDate, String endDate) {
		ContentValues cv = new ContentValues();
		cv.put("classId", classId);
		cv.put("className", className);
		cv.put("location", location);
		cv.put("startDate", startDate);
		cv.put("endDate", endDate);	
		db.insert("class", null, cv);
	}
	//insert a new schedule to schedule table
	public void insertSchedule( int classId, String dayOfWeek, String startTime, String endTime, int eventId) {
		ContentValues cv = new ContentValues();
		cv.put("eventId", eventId);
		cv.put("classId", classId);
		cv.put("dayOfWeek", dayOfWeek);
		cv.put("startTime", startTime);
		cv.put("duration", endTime);
		db.insert("schedule", null, cv);
	}
	//insert a new task to table to-do-task table
	public void insertTask(int classId, String taskName, String dueTime, String completionTime, String priority, String type, int eventId) {
		ContentValues cv = new ContentValues();
		cv.put("classId", classId);
		cv.put("taskName", taskName);
		cv.put("dueTime", dueTime);
		cv.put("completionTime", completionTime);
		cv.put("priority", priority);	
		cv.put("type", type);
		cv.put("eventId", eventId);
		db.insert("task", null, cv);
	}
	//insert a new note to table note
	public void insertNote(int classId, String noteName, String notePath, String category, String date) {
		ContentValues cv = new ContentValues();
		cv.put("classId", classId);
		cv.put("noteName", noteName);
		cv.put("notePath", notePath);
		cv.put("category", category);	
		cv.put("date", date);
		db.insert("note", null, cv);
	}
	
	//delete an existing class from class, schedule table, task table and note table
	public void deleteClass(int classId) {
		String[] args = {String.valueOf(classId)};
		db.delete("class", "classId=?", args);
		db.delete("schedule", "classId=?", args);
		db.delete("task", "classId=?", args);
		db.delete("note", "classId=?", args);
	}
	//delete an existing task from to-do-task table
	public void deleteTask(int taskId) {
		String[] args = {String.valueOf(taskId)};
		db.delete("task", "taskId=?", args);
	}
	//delete an existing note from note table
	public void deleteNote(int noteId) {
		String[] args = {String.valueOf(noteId)};
		db.delete("note", "noteId=?", args);
	}
	//delete an existing schedule from schedule table
	public void deleteSchedule(int scheduleId) {
		String[] args = {String.valueOf(scheduleId)};
		db.delete("schedule", "scheduleId=?", args);
	}
	
	
	//update a existing task in the to-do-list table
	public void updateTask(int taskId, int classId, String taskName, String dueTime, String completionTime, String priority) {
		String[] args = {String.valueOf(taskId)};
		ContentValues cv = new ContentValues();
		cv.put("classId", classId);
		cv.put("taskName", taskName);
		cv.put("dueTime", dueTime);
		cv.put("completionTime", completionTime);
		cv.put("priority", priority);	
		db.update("task", cv, "taskId=?", args);
	}
    //update a existing class in the class table
	public void updateClass(int classId, String className, String location, String startDate, String endDate) {
		String[] args = {String.valueOf(classId)};
		ContentValues cv = new ContentValues();
		//cv.put("classId", classId);
		cv.put("className", className);
		cv.put("location", location);
		cv.put("startDate", startDate);
		cv.put("endDate", endDate);	
		db.update("class", cv, "classId=?", args);
	}
	//update a existing schedule in the class schedule
	public void updateschedule(int scheduleId, int classId, String dayOfWeek, String startTime, String duration) {
		String[] args = {String.valueOf(scheduleId)};
		ContentValues cv = new ContentValues();
	    cv.put("classId", classId);
		cv.put("dayOfWeek", dayOfWeek);
		cv.put("startTime", startTime);
		cv.put("duration", duration);
		db.update("schedule", cv, "scheduleId=?", args);
	}
	public void setEventIdForTask(int taskId, int eventId) {
		String[] args = {String.valueOf(taskId)};
		ContentValues cv = new ContentValues();
		cv.put("eventId", eventId);
		db.update("task", cv, "taskId=?", args);
	}
	public void setEventIdForSchedule(int scheduleId, int eventId) {
		String[] args = {String.valueOf(scheduleId)};
		ContentValues cv = new ContentValues();
		cv.put("eventId", eventId);
		db.update("schedule", cv, "scheduleId=?", args);
	}
	public long getEventIdFromTask(int taskId) {
		int eventId = -1;
		Cursor c = db.rawQuery("SELECT eventId FROM task where taskId = " + taskId, null); 
		if(c.moveToFirst()) {
			c.move(0);
			eventId = Integer.parseInt(c.getString(c.getColumnIndex("eventId")));
		}
		return eventId;
	}
	public long getEventIdFromSchedule(int scheduleId) {
		int eventId = -1;
		Cursor c = db.rawQuery("SELECT eventId FROM schedule where scheduleId = " + scheduleId, null); 
		if(c.moveToFirst()) {
			c.move(0);
			eventId = Integer.parseInt(c.getString(c.getColumnIndex("eventId")));
		}
		return eventId;
	}

}
