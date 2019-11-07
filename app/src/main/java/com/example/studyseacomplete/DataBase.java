package com.example.studyseacomplete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final String studySeaName = "studySEA";
    private static final int start=1;
    private static final String studySeaItems = "assignment";
    private static final String studySeaItemName = "assignmentName";

    public DataBase(Context context) {
        super(context, studySeaName, null, start);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryName = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s  TEXT NOT NULL);", studySeaItems,studySeaItemName);
        db.execSQL(queryName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String queryName2 = String.format("DELETE TABLE IF EXISTS %s)", studySeaItems);
        db.execSQL(queryName2);
        onCreate(db);
    }

    public void insertData(String task){
        SQLiteDatabase databaseStudySEA = this.getWritableDatabase();
        ContentValues databaseValues = new ContentValues();
        databaseValues.put(studySeaItemName, task);
        databaseStudySEA.insertWithOnConflict(studySeaItems, null,databaseValues, SQLiteDatabase.CONFLICT_REPLACE);
        databaseStudySEA.close();
    }

    public void deleteData(String task){
        SQLiteDatabase databaseStudySEA = this.getWritableDatabase();
        databaseStudySEA.delete(studySeaItems, studySeaItemName + " = ?", new String[]{task});
        databaseStudySEA.close();

    }

    public ArrayList<String> getAllTasks(){
        ArrayList<String> alltasks = new ArrayList<>();
        SQLiteDatabase databaseStudySEA = this.getReadableDatabase();
        Cursor cursordatabaseStudySEA = databaseStudySEA.query(studySeaItems, new String[]{studySeaItemName}, null,null,null,null,null);
        while (cursordatabaseStudySEA.moveToNext()){
            int index = cursordatabaseStudySEA.getColumnIndex(studySeaItemName);
            alltasks.add(cursordatabaseStudySEA.getString(index));
        }
        cursordatabaseStudySEA.close();
        databaseStudySEA.close();
        return alltasks;
    }
}
