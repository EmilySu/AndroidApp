package entities;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class NoteAdapter
{

	//adapte the result from sql query to something can be displayed
	public ArrayList<String> adapteNote(Cursor c)
	{
		ArrayList<String> result = new ArrayList<String>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				String noteName = c.getString(c.getColumnIndex("noteName"));
				String notePath = c.getString(c.getColumnIndex("notePATH"));
				String date = c.getString(c.getColumnIndex("date"));
				StringBuffer total = new StringBuffer(noteName);
				total.append(",").append(notePath).append(",").append(date);
				result.add(total.toString());
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}

	//adapte the result from sql query to something can be displayed
	public ArrayList<Integer> adapteNoteIDs(Cursor c)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				result.add(c.getInt(c.getColumnIndex("noteId")));
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}
	
	//adapte the result from sql query to something can be displayed
	public ArrayList<String> adapteNotePaths(Cursor c)
	{
		ArrayList<String> result = new ArrayList<String>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				result.add(c.getString(c.getColumnIndex("notePATH")));
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}

	//display just the categories
	public ArrayList<String> adapteNoteCategories(Cursor c)
	{
		ArrayList<String> result = new ArrayList<String>();
		if (c.moveToFirst())
		{
			c.move(0);
			while (true)
			{
				String category = c.getString(c.getColumnIndex("category"));
				String date = c.getString(c.getColumnIndex("date"));
				StringBuffer total = new StringBuffer(category);
				total.append(",").append(date);
				result.add(total.toString());
				if (c.isLast())
					break;
				c.moveToNext();
			}
		}
		return result;
	}
}
