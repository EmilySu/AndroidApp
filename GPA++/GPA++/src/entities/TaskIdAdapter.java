package entities;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class TaskIdAdapter {
	public List<Integer> adapteTaskList(Cursor c) {
		List<Integer> result = new ArrayList<Integer>();
		if(c.moveToFirst()) {
			c.move(0);	
			 while(true){
				int taskId = c.getInt(c.getColumnIndex("taskId")); 
				
				result.add(taskId);
				if(c.isLast())
					break;
				c.moveToNext();
	         }
		}
		return result;
	}

}
