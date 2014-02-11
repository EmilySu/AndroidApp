package com.gpa.presentation;

import java.util.ArrayList;

import DBLayout.DBFunc;
import android.app.Application;

public class DBshare extends Application {
	
	private DBFunc dbfunc; 
	private ArrayList<String> noteInfo =  new ArrayList<String>(); 

	public DBFunc getDbfunc() {
		return dbfunc;
	}

	public void setDbfunc(DBFunc dbfunc) {
		this.dbfunc = dbfunc;
	}
	

	public ArrayList<String> getNoteInfo() {
		return noteInfo;
	}

	public void setNoteInfo(ArrayList<String> notIn) {
		for(String i : notIn)
			noteInfo.add(i);
		//System.out.println(noteInfo.size());
	}


}
