package com.examples.Services.MusicService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHandler {
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String KIND = "kind";
	public static final String NUMBER = "number";
	public static final String CURRENT = "current";
	public static final String TABLE_NAME = "myTable";
	public static final String DATA_BASE_NAME = "myDataBase";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_CREATE = "create table myTable (date text not null,time text not null,kind text not null,number text not null,current text not null);";

	//create a helper instance
	DataBaseHelper dbHelper;
	//create a context variable
	Context context;
	//create a SQLite instance
	SQLiteDatabase db;
	
	public DataHandler(Context context){
		//assign the context and database helper in the constructor
		this.context = context;
		dbHelper = new DataBaseHelper(context);
	}
	
	private static class DataBaseHelper extends SQLiteOpenHelper{
		
		public DataBaseHelper(Context context) {
			super(context,DATA_BASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
			//create the table
			db.execSQL(TABLE_CREATE);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//execute the query on upgrade
			db.execSQL("DROP TABLE IF EXISTS myTable");
			onCreate(db);
		}
		
	}
	
	public DataHandler open(){
		//open the database
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		//close the database
		dbHelper.close();
	}
	
	public long insertData(String date,String time,String kind,String number,String current){
		ContentValues content = new ContentValues();
		//insert the values in the content
		content.put(DATE, date);
		content.put(TIME, time);
		content.put(KIND, kind);
		content.put(NUMBER, number);
		content.put(CURRENT, current);
		return db.insertOrThrow(TABLE_NAME, null, content);
	}
	
	public Cursor returnData(){
		//execute the query for select
		return db.query(TABLE_NAME, new String[]{DATE,TIME,KIND,NUMBER,CURRENT},null,null,null,null,null);
	}
	
	public void deleteData(){
		//delete all the records from the table
		db.execSQL(" delete from "+ TABLE_NAME);
	}

}
