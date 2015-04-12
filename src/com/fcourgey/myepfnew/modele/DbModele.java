package com.fcourgey.myepfnew.modele;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbModele extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "db.db";
    
    // Inner class that defines the table contents
    public static abstract class Colonne implements BaseColumns {
        public static final String TABLE_NAME = "DEVOIRS";
        public static final String DATE_ID = "DATE_ID"; // date style "2015-03-13T13:45", fait office d'id
        public static final String DEVOIRS = "DEVOIRS"; // 
    }
    
    private static SQLiteDatabase db;
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + Colonne.TABLE_NAME + " (" +
        /*Colonne._ID + " INTEGER PRIMARY KEY," +*/
        Colonne.DATE_ID + TEXT_TYPE + " PRIMARY KEY " + COMMA_SEP +
        Colonne.DEVOIRS + TEXT_TYPE +
        " )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + Colonne.TABLE_NAME;

    public DbModele(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(db == null){
        	db = getWritableDatabase();
        }
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public long inserer(ContentValues v){
    	return db.insertWithOnConflict (Colonne.TABLE_NAME, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public String getDevoir(String dateId) throws Exception{
    	String selectQuery = "SELECT * FROM "+Colonne.TABLE_NAME+" WHERE "+Colonne.DATE_ID+"=?";
    	Cursor c = db.rawQuery(selectQuery, new String[] { dateId });
    	
    	if (c == null)
    		throw new Exception();
    	
    	c.moveToFirst();
    	return c.getString(c.getColumnIndex(Colonne.DEVOIRS));
    }
	public int supprimer(String dateId) {
		return db.delete(Colonne.TABLE_NAME, Colonne.DATE_ID+"=?", new String[] { dateId });
	}
}
