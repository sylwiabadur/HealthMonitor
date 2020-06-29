package pl.edu.pwr.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "step_distance_date_speed_table";
    private String COL1="ID";
    private String COL2="steps";
    private String COL3="distance";
    private String COL4="date";
    private String COL5="speed";

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL2 + " INTEGER," + COL3 + " REAL, " + COL4 + " DATE, "+ COL5 + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int steps, double distance, String date, double speed)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, steps);
        contentValues.put(COL3, df.format(distance));
        contentValues.put(COL4, date);
        contentValues.put(COL5, df.format(speed));

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else return true;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor sortByDate()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL4 + " DESC ;";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor sortBySteps()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL2 + " DESC ;";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getLastId()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT max(" + COL1 + ") FROM " + TABLE_NAME + ";";
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
