package pl.edu.pwr.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationDataBaseHelper extends SQLiteOpenHelper
{
    private static final String TABLE_NAME = "date_location_table";
    private String COL1="ID";
    private String COL2="longitude";
    private String COL3="latitude";
    private String COL4="date";
    private String COL5="params_id";


    public LocationDataBaseHelper(@Nullable Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " DATE, " + COL5 + " INTEGER )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String longitude, String latitude, String date, String params_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, longitude);
        contentValues.put(COL3, latitude);
        contentValues.put(COL4, date);
        contentValues.put(COL5, params_id);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else return true;
    }


    public Cursor getDataWhere(String params_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL5 + " = " + params_id;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
