package com.example.goals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "goals";
    public static final String TABLE_NAME = "goals_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "ITEM1";
    public static final String COL3 = "PARENT";


    public DatabaseHelper(Context context){super(context,DATABASE_NAME,null,1);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ITEM1 TEXT, " + "PARENT INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String item1){
     SQLiteDatabase db = this.getWritableDatabase();
     ContentValues contentValues = new ContentValues();
     contentValues.put(COL2, item1);

     long result = db.insert(TABLE_NAME, null, contentValues);

     if (result == -1){
         return false;
     }
     else{
         return true;
     }
    }

    public boolean addSubData(String item1, int parent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item1);
        contentValues.put(COL3, parent);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM goals_data WHERE PARENT = 0", null);
        return data;
    }

    public Cursor getListContentsByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM goals_data WHERE PARENT = " + id, null);
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + "*" + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = " + "'" + name + "'" + ";";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public void DeleteDataByName(String name){
        Cursor cursor = getItemID(name);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                COL1 + " = " + id + ";";
        db.execSQL(query);
    }

    public void DeleteSubDataByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM goals_data");
    }

    public boolean Recursion(Cursor data){
        SQLiteDatabase db = this.getWritableDatabase();
        if(data.getCount() <= 0){
            return false;
        }
        else{
            if(data.getCount() == 1){
                data.moveToFirst();
                int id = data.getInt(0);
                String query1 = "DELETE FROM goals_data WHERE ID = " + id;
                db.execSQL(query1);
                String query2 = "SELECT * FROM goals_data WHERE PARENT = " + id;
                Cursor subdata = db.rawQuery(query2,null);
                Recursion(subdata);
                return false;
            }
            else{
                while (data.moveToNext()){
                    int id = data.getInt(0);
                    String query1 = "DELETE FROM goals_data WHERE ID = " + id;
                    db.execSQL(query1);
                    String query2 = "SELECT * FROM goals_data WHERE PARENT = " + id;
                    Cursor subdata = db.rawQuery(query2,null);
                    Recursion(subdata);
                }
            }
            return true;
        }
    }

    public long ColumnCount(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM goals_data";
        Cursor data = db.rawQuery(query,null);
        long count = DatabaseUtils.queryNumEntries(db, "goals_data");
        return count;
    }
}
