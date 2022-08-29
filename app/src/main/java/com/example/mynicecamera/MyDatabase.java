package com.example.mynicecamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {
    private static final String FILES_TABLE ="FILES_TABLE" ;
    public static final String FILE_NOTE = "FILE_NOTE";
    public static final String FILE_DATE = "FILE_DATE";
    public static final String FILE_ADDRESS = "FILE_ADDRESS";
    public static final String ID = "ID";
    private static final String ADDRESS_LATITUDE = "ADDRESS_LATITUDE";
    private static final String ADDRESS_LONGITUDE ="ADDRESS_LONGITUDE" ;
    private static final String FILE_PATH ="FILE_PATH";


    public MyDatabase(@Nullable Context context) {
        super(context, "my_media_file.db",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + FILES_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FILE_ADDRESS + " TEXT, " +FILE_DATE +" TEXT, "+ ADDRESS_LATITUDE +" REAL, "+ ADDRESS_LONGITUDE + " REAL, "+ FILE_NOTE +" TEXT, "+ FILE_PATH +" TEXT " + ")";
        db.execSQL(createTable);
    }

    public boolean addOne(MediaFile myFile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FILE_ADDRESS,myFile.getAddress());
        cv.put(FILE_DATE,myFile.getDate());
        cv.put(ADDRESS_LATITUDE,myFile.getLatitude());
        cv.put(ADDRESS_LONGITUDE,myFile.getLongitude());
        cv.put(FILE_NOTE,myFile.getNote());
        cv.put(FILE_PATH,myFile.getPhotoPath());
        long insert = db.insert(FILES_TABLE, null, cv);
        db.close();

        if (insert == -1) return  false;
        else return true;
    }

    public List<MediaFile> getThePics(){
        List<MediaFile> fileList = new ArrayList<>();
        String query = "SELECT * FROM "+ FILES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                int fileId = cursor.getInt(0);
                String fileAddress= cursor.getString(1);
                String fileDate = cursor.getString(2);
                Double latitude = cursor.getDouble(3);
                Double longitude = cursor.getDouble(4);
                String fileNote = cursor.getString(5);
                String filePath = cursor.getString(6);
                MediaFile newMediaFile = new MediaFile(fileDate,fileAddress,latitude,longitude,fileNote,filePath,fileId);
                fileList.add(newMediaFile);
            }while (cursor.moveToNext());
        }
        else{}

        cursor.close();
        db.close();
        return fileList;
    }
    public boolean deleteOne(MediaFile myFile){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+ FILES_TABLE +" WHERE "+ID+" = "+ myFile.getId();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) return true;
        else return false;

    }
    public boolean onUpdate(MediaFile myFile,int id){
        //Ανανέωση γεγονότος
        SQLiteDatabase db = this.getWritableDatabase();
        String query  = "UPDATE "+ FILES_TABLE+" SET "+FILE_ADDRESS+ " = "+ "'"+myFile.getAddress()+"'"+", "+
                FILE_DATE+ " = "+ "'"+myFile.getDate()+"'"+", "+ADDRESS_LATITUDE+ " = "+ "'"+myFile.getLatitude()+"'"+
                ", "+ADDRESS_LONGITUDE+ " = "+ "'"+myFile.getLongitude()+"'"+", "+FILE_NOTE+ " = "+ "'"+myFile.getNote()+"'" + ", "+FILE_PATH+ " = "+ "'"+myFile.getPhotoPath()+"'"+
                " WHERE "+ID+" = "+id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) return true;
        else return false;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
