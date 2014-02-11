package entities;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class TaskAdapter {
	//adapte the result from sql query to something can be displayed
	public ArrayList<String> adapteTaskList(Cursor c) {
		ArrayList<String> result = new ArrayList<String>();
		if(c.moveToFirst()) {
			c.move(0);	
			 while(true){
				int taskId = c.getInt(c.getColumnIndex("taskId"));
				int classId = c.getInt(c.getColumnIndex("classId"));
				String taskName = c.getString(c.getColumnIndex("taskName"));
				String dueTime = c.getString(c.getColumnIndex("dueTime"));
				String priority = c.getString(c.getColumnIndex("priority"));
				String completionTime = c.getString(c.getColumnIndex("completionTime"));
				String sclassId = ""+classId; 
				StringBuffer total = new StringBuffer(sclassId);//careful! string buffer initial int is about capacity!
				total.append(",").append(taskName).append(",").append(dueTime).append(",").append(priority).append(",").append(completionTime).append(",").append(taskId);
				result.add(total.toString());
				if(c.isLast())
					break;
				c.moveToNext();
	         }
		}
		return result;
	}
}
