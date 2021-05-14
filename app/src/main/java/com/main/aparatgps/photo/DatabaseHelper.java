package com.main.aparatgps.photo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{
    public String TABLE_NAME = "FavouritePhotos";
    public String TABLE_COLUMN_ABSOLUTE_PATH = "Absolute_Path";

    private String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+BaseColumns._ID+" INTEGER PRIMARY KEY, "+TABLE_COLUMN_ABSOLUTE_PATH+" TEXT NOT NULL)";

    public DatabaseHelper(@Nullable Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}