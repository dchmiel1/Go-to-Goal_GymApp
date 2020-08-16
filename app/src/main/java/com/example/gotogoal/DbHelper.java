package com.example.gotogoal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GymAppDb.db";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DbNames.TABLE_NAME + "(" +
                    DbNames._ID + " INTEGER PRIMARY KEY, " +
                    DbNames.COLUMN_NAME_DATE + " TEXT, " +
                    DbNames.COLUMN_NAME_EXERCISE + " TEXT, " +
                    DbNames.COLUMN_NAME_REPS + " INTEGER, " +
                    DbNames.COLUMN_NAME_KG_ADDED + " REAL);";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + DbNames.TABLE_NAME;

    public DbHelper(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    public boolean insertSet (String exName, int reps, double kgAdded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbNames.COLUMN_NAME_DATE, new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(MainActivity.date));
        values.put(DbNames.COLUMN_NAME_EXERCISE, exName);
        values.put(DbNames.COLUMN_NAME_REPS, reps);
        values.put(DbNames.COLUMN_NAME_KG_ADDED, kgAdded);
        db.insert(DbNames.TABLE_NAME, null, values);
        return true;
    }

    public Cursor getById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DbNames.TABLE_NAME + " where _id="+id+"", null );
        return res;
    }

    public Cursor getExercisesByDate(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select distinct " + DbNames.COLUMN_NAME_EXERCISE +
                                    " from " + DbNames.TABLE_NAME +
                                    " where date =" + "'" + date + "'" + " and " + DbNames.COLUMN_NAME_EXERCISE + " != 'weight'"
                                    , null);
        return c;
    }

    public Cursor getSetsByDateAndExercise(String date, String exercise){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_EXERCISE + ", " + DbNames.COLUMN_NAME_REPS + ", " + DbNames.COLUMN_NAME_KG_ADDED +
                                " from " + DbNames.TABLE_NAME + " " +
                                "where " + DbNames.COLUMN_NAME_DATE + " =" + "'" + date + "'" + " and " + DbNames.COLUMN_NAME_EXERCISE + " = " + "'" + exercise +"'" + "", null);
        return c;
    }

    public void showAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbNames.TABLE_NAME+"", null);
        while(res.moveToNext()) {
            System.out.println("id: " + res.getLong(res.getColumnIndexOrThrow(DbNames._ID)));
            System.out.println("date: " +res.getString(res.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE)));
            System.out.println("exerciseName: " + res.getString(res.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE)));
            System.out.println("reps: " +res.getString(res.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)));
            System.out.println("kgAdded: " +res.getString(res.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
            System.out.println("");
        }
    }

    public void deleteSet (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sets_table",
                "_id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void insertOrUpdateWeight(double kgs, int cms){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String date = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(MainActivity.date);
        values.put(DbNames.COLUMN_NAME_DATE, date);
        values.put(DbNames.COLUMN_NAME_EXERCISE, "weight");
        values.put(DbNames.COLUMN_NAME_REPS, cms);
        values.put(DbNames.COLUMN_NAME_KG_ADDED, kgs);
        if(isWeightThatDate(date)) {
            db.update(DbNames.TABLE_NAME, values,  "date = ? and exercise = 'weight'", new String[]{date});
        }
        else{
            insertSet("weight", cms, kgs);
        }
    }

    private boolean isWeightThatDate(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * " +
                                    " from " + DbNames.TABLE_NAME + " " +
                                    " where " + DbNames.COLUMN_NAME_DATE + " =" + "'" + date + "' and " + DbNames.COLUMN_NAME_EXERCISE + " = 'weight'", null);
        System.out.println(c.getCount() > 0);
        return c.getCount() > 0;
    }

    public Cursor findLastWeight(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_KG_ADDED + ", " + DbNames.COLUMN_NAME_REPS +
                                " from " + DbNames.TABLE_NAME + " where " + DbNames.COLUMN_NAME_EXERCISE + " = 'weight' ", null);
        return c;
    }
}
