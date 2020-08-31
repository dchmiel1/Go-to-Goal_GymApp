package com.example.gotogoal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "GymAppDb.db";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DbNames.TABLE_NAME + "(" +
                    DbNames._ID + " INTEGER PRIMARY KEY, " +
                    DbNames.COLUMN_NAME_DATE + " TEXT, " +
                    DbNames.COLUMN_NAME_EXERCISE + " TEXT, " +
                    DbNames.COLUMN_NAME_REPS + " INTEGER, " +
                    DbNames.COLUMN_NAME_KG_ADDED + " REAL, " +
                    DbNames.COLUMN_NAME_ONE_REP + " REAL);";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + DbNames.TABLE_NAME;
    private MainActivity mainActivity;

    public DbHelper(Context c, MainActivity mainActivity){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.mainActivity = mainActivity;
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

    public void insertSet (String exName, int reps, double kgAdded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbNames.COLUMN_NAME_DATE, new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(MainActivity.date));
        values.put(DbNames.COLUMN_NAME_EXERCISE, exName);
        values.put(DbNames.COLUMN_NAME_REPS, reps);
        values.put(DbNames.COLUMN_NAME_KG_ADDED, kgAdded);
        values.put(DbNames.COLUMN_NAME_ONE_REP, calcOneRep(exName, reps, kgAdded));
        db.insert(DbNames.TABLE_NAME, null, values);
    }

    public void insertWeight(double kgs){
        SQLiteDatabase db = this.getWritableDatabase();
        String date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        if(isWeightThatDate(date)) {
            ContentValues values = new ContentValues();
            values.put(DbNames.COLUMN_NAME_DATE, date);
            values.put(DbNames.COLUMN_NAME_EXERCISE, "weight");
            values.put(DbNames.COLUMN_NAME_REPS, 0);
            values.put(DbNames.COLUMN_NAME_KG_ADDED, kgs);
            values.put(DbNames.COLUMN_NAME_ONE_REP, 0);
            db.update(DbNames.TABLE_NAME, values,  "date = ? and exercise = 'weight'", new String[]{date});
        }
        else{
            MainActivity.date = new Date();
            insertSet("weight", 0, kgs);
        }
    }

    public void updateProfileInfo(int sex, String dateOfBirth, int height, double weight, BodyWeightActivity.WhichPicker whichPicker){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(getProfileInfo().getCount() == 0){
            values.put(DbNames.COLUMN_NAME_DATE, dateOfBirth);
            values.put(DbNames.COLUMN_NAME_EXERCISE, "profile_info");
            values.put(DbNames.COLUMN_NAME_REPS, height);
            values.put(DbNames.COLUMN_NAME_KG_ADDED, weight);
            values.put(DbNames.COLUMN_NAME_ONE_REP, sex);
            db.insert(DbNames.TABLE_NAME, null, values);
        }else{
            switch(whichPicker) {
                case SEX:
                    values.put(DbNames.COLUMN_NAME_ONE_REP, sex);
                    break;
                case BIRTHDAY:
                    values.put(DbNames.COLUMN_NAME_DATE, dateOfBirth);
                    break;
                case HEIGHT:
                    values.put(DbNames.COLUMN_NAME_REPS, height);
                    break;
                default:
                    values.put(DbNames.COLUMN_NAME_KG_ADDED, weight);
                    break;
            }
            db.update(DbNames.TABLE_NAME, values, "exercise = ?", new String[] {"profile_info"});
        }
    }

    public void updateSet(int id, int reps, double kgs, String exName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbNames.COLUMN_NAME_REPS, reps);
        values.put(DbNames.COLUMN_NAME_KG_ADDED, kgs);
        values.put(DbNames.COLUMN_NAME_ONE_REP, calcOneRep(exName, reps, kgs));
        db.update(DbNames.TABLE_NAME, values, " _id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getProfileInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + DbNames.TABLE_NAME + " where " + DbNames.COLUMN_NAME_EXERCISE + " = 'profile_info'", null);
    }

    public Cursor getSetsByDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select " + DbNames.COLUMN_NAME_EXERCISE + ", " + DbNames.COLUMN_NAME_KG_ADDED + ", " + DbNames.COLUMN_NAME_REPS +
                            " from " + DbNames.TABLE_NAME +
                            " where " + DbNames.COLUMN_NAME_DATE + " =  '" + MainActivity.dateFormatInDb.format(MainActivity.date) + "' and " + DbNames.COLUMN_NAME_EXERCISE + " != 'weight'", null);
    }

    public Cursor getSetsByDateAndExercise(String date, String exercise){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select " + DbNames.COLUMN_NAME_EXERCISE + ", " + DbNames.COLUMN_NAME_REPS + ", " + DbNames.COLUMN_NAME_KG_ADDED +", "+ DbNames._ID+
                " from " + DbNames.TABLE_NAME + " " +
                "where " + DbNames.COLUMN_NAME_DATE + " =" + "'" + date + "'" + " and " + DbNames.COLUMN_NAME_EXERCISE + " = " + "'" + exercise +"'" + "", null);
    }

    public Cursor getLastWeight(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select " + DbNames.COLUMN_NAME_KG_ADDED + ", " + DbNames.COLUMN_NAME_REPS +
                " from " + DbNames.TABLE_NAME +
                " where " + DbNames.COLUMN_NAME_EXERCISE + " = 'weight' " +
                "order by " + DbNames.COLUMN_NAME_DATE + " DESC", null);
    }

    public double getBestRep(String exName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_KG_ADDED +
                " from " + DbNames.TABLE_NAME +
                " where " + DbNames.COLUMN_NAME_EXERCISE + " = " + exName +
                "order by " + DbNames.COLUMN_NAME_KG_ADDED + " DESC", null);
        if(c.getCount() > 0) {
            c.moveToNext();
            return c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED));
        }
        else
            return -1;
    }

    public double getCalculatedLastOneRep(String exName){
        SQLiteDatabase db = this.getReadableDatabase();
        String lastDate;
        Cursor dateCursor;
        dateCursor = db.rawQuery("select " + DbNames.COLUMN_NAME_DATE +
                " from " + DbNames.TABLE_NAME +
                " where " + DbNames.COLUMN_NAME_EXERCISE + " = " + exName +
                "order by " + DbNames.COLUMN_NAME_DATE + " DESC", null);
        if(dateCursor.getCount() > 0){
            dateCursor.moveToNext();
            lastDate = dateCursor.getString(dateCursor.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE));
        }else{
            return -1;
        }
        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_ONE_REP +
                " from " + DbNames.TABLE_NAME +
                " where " + DbNames.COLUMN_NAME_EXERCISE + " = " + exName + " and " + DbNames.COLUMN_NAME_DATE + " = " + "'" +lastDate+ "'" +
                " order by " + DbNames.COLUMN_NAME_ONE_REP + " DESC", null);
        if(c.getCount() > 0) {
            c.moveToNext();
            return c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_ONE_REP));
        }
        else
            return -1;
    }

    public double getCalculatedBestOneRep(String exName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_ONE_REP +
                " from " + DbNames.TABLE_NAME +
                " where " + DbNames.COLUMN_NAME_EXERCISE + " = " + exName +
                "order by " + DbNames.COLUMN_NAME_ONE_REP + " DESC", null);
        if(c.getCount() > 0) {
            c.moveToNext();
            return c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_ONE_REP));
        }
        else
            return -1;
    }

    public Cursor getWeightData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select " + DbNames.COLUMN_NAME_DATE + ", " + DbNames.COLUMN_NAME_KG_ADDED +
                            " from " + DbNames.TABLE_NAME +
                            " where " + DbNames.COLUMN_NAME_EXERCISE + " = 'weight' " +
                            " order by " + DbNames.COLUMN_NAME_DATE + " ASC", null);
    }

    public Vector<MainActivity.Training> getExerciseHistory(String exercise){
        SQLiteDatabase db = this.getReadableDatabase();
        Vector<MainActivity.Training> trainings = new Vector<>();
        MainActivity.Training training;
        boolean found = false;

        Cursor c = db.rawQuery("select " + DbNames.COLUMN_NAME_DATE + ", " + DbNames.COLUMN_NAME_REPS + ", " + DbNames.COLUMN_NAME_KG_ADDED +
                                    " from " + DbNames.TABLE_NAME +
                                    " where " + DbNames.COLUMN_NAME_EXERCISE + " = '" + exercise + "'" +
                                    " order by " + DbNames.COLUMN_NAME_DATE + " desc ", null);
        while(c.moveToNext()){
            for(int i = 0; i < trainings.size(); i ++)
                if(trainings.elementAt(i).exercise.equals(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE)))){
                    trainings.elementAt(i).reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
                    trainings.elementAt(i).kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
                    found = true;
                }
            if(found){
                found = false;
                continue;
            }
            training = new MainActivity.Training();
            training.exercise = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE));
            training.kgs = new Vector<>();
            training.reps = new Vector<>();
            training.reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
            training.kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
            trainings.add(training);
        }
        return trainings;

    }

    public Cursor getExerciseOneReps(String exercise){
           SQLiteDatabase db = this.getReadableDatabase();
           return db.rawQuery("select " + DbNames.COLUMN_NAME_DATE + ", " + "max(" + DbNames.COLUMN_NAME_ONE_REP + ")" +
                                " from " + DbNames.TABLE_NAME +
                                " where " + DbNames.COLUMN_NAME_EXERCISE + " = '" + exercise + "'" +
                                " group by " + DbNames.COLUMN_NAME_DATE +
                                " order by " + DbNames.COLUMN_NAME_DATE, null);
    }

    public void deleteById (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sets_table",
                "_id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteByDateAndExercise(String exName){
        SQLiteDatabase db = this.getWritableDatabase();
        String date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(MainActivity.date);
        db.delete("sets_table",
                " exercise = ? AND date = ?",
                new String[] { exName, date });
        mainActivity.checkWorkout();
    }

    private boolean isWeightThatDate(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * " +
                " from " + DbNames.TABLE_NAME + " " +
                " where " + DbNames.COLUMN_NAME_DATE + " =" + "'" + date + "' and " + DbNames.COLUMN_NAME_EXERCISE + " = 'weight'", null);
        return c.getCount() > 0;
    }

    public void showAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery( "select * from " + DbNames.TABLE_NAME, null );
        while(c.moveToNext()){
            System.out.println("************");
            System.out.println("ID: " + c.getInt(c.getColumnIndexOrThrow(DbNames._ID)));
            System.out.println("ex: " +c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE)));
            System.out.println("date: " + c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE)));
            System.out.println("kg_added: " + c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
            System.out.println("reps: " + c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)));
            System.out.println("one_rep: " +c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_ONE_REP)));
        }
    }

    private double calcOneRep(String exName, int reps, double kgAdded){
        if(exName.equals("'Pull up'") || exName.equals("'Dip'") && reps <= 20) {
            Cursor c = getLastWeight();
            c.moveToNext();
            return ((kgAdded +c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))) / ((double)MainActivity.multiplier[reps]/100)) - c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED));
        }else if(reps <= MainActivity.multiplier.length-1)
            return kgAdded/((double)MainActivity.multiplier[reps]/100);
        else
            return kgAdded/((double)MainActivity.multiplier[MainActivity.multiplier.length-1]/100);
    }
}
