package DBLayout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "mydata.db"; //数据库名称
	private static final int version = 1; //数据库版本

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	public void onCreate(SQLiteDatabase db) {
		String table1 = "create table class(classId  INTEGER , className VARCHAR, location VARCHAR, startDate VARCHAR, endDate VARCHAR);";   
		//  String table2 = "create table schedule(scheduleId INTEGER PRIMARY KEY AUTOINCREMENT , dayOfWeek VARCHAR, startTime VARCHAR, duration VARCHAR);";

		String table2 = "create table schedule(scheduleId INTEGER PRIMARY KEY AUTOINCREMENT , eventId INTEGER, classId INTEGER, dayOfWeek VARCHAR, startTime VARCHAR, duration VARCHAR);";
		String table3 = "create table task(taskId INTEGER PRIMARY KEY AUTOINCREMENT, eventId INTEGER, classId INTEGER, taskName VARCHAR, dueTime VARCHAR, completionTime VARCHAR, priority VARCHAR, type VARCHAR);";
		String table4 = "create table note(noteId INTEGER PRIMARY KEY AUTOINCREMENT, classId INTEGER, noteName VARCHAR, notePATH VARCHAR, category VARCHAR, date VARCHAR);";

		db.execSQL(table1);
		db.execSQL(table2);
		db.execSQL(table3);
		db.execSQL(table4);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
