package com.mqfcu7.caisong.gyroscope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gyroscope";
    private static final String TABLE_HISTORY_NAME = "history";
    private static final String TABLE_SETTING_NAME = "setting";

    private static final int DATABASE_VERSION = 1;

    public static class GyroscopeData {
        public int sectionsNum;
        public float[] sectionsAngle;
        public String[] sectionsName;
        public float arrowAngle;
        public int selectedSection = Gyroscope.INVALID_SELECTED_SECTION;
        public long time;
        public String location;
    }

    private Context mContext;

    private abstract class HistoryColumns implements BaseColumns {
        public static final String SECTIONS_NUM = "sections_num";
        public static final String SECTIONS_ANGLE = "sections_angle";
        public static final String SECTIONS_NAME = "sections_name";
        public static final String ARROW_ANGLE = "arrow_angle";
        public static final String SELECTED_SECTION = "selected_section";
        public static final String TIME = "time";
        public static final String LOCATION = "location";
    }

    private abstract class SettingColumns implements BaseColumns {
        public static final String SECTIONS_NUM = "sections_num";
        public static final String SECTIONS_ANGLE = "sections_angle";
        public static final String SECTIONS_NAME = "sections_name";
        public static final String ARROW_ANGLE = "arrow_angle";
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHistoryTable(db);
        createSettingTable(db);
    }

    private void createHistoryTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_HISTORY_NAME + " ("
                + HistoryColumns._ID + " integer primary key,"
                + HistoryColumns.SECTIONS_NUM + " integer,"
                + HistoryColumns.SECTIONS_ANGLE + " text,"
                + HistoryColumns.SECTIONS_NAME + " text,"
                + HistoryColumns.ARROW_ANGLE + " real,"
                + HistoryColumns.SELECTED_SECTION + " integer,"
                + HistoryColumns.TIME + " int,"
                + HistoryColumns.LOCATION + " text"
                + ");");
    }

    private void createSettingTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_SETTING_NAME + " ("
                + SettingColumns._ID + " integer primary key,"
                + SettingColumns.SECTIONS_NUM + " integer,"
                + SettingColumns.SECTIONS_ANGLE + " text,"
                + SettingColumns.SECTIONS_NAME + " text,"
                + SettingColumns.ARROW_ANGLE + " real"
                + ");");

        db.execSQL("insert into " + TABLE_SETTING_NAME + " values(0,6,'60,60,60,60,60,60','',290);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public GyroscopeData getSettingData() {
        GyroscopeData result = null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_SETTING_NAME);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result = new GyroscopeData();
                result.sectionsNum = c.getInt(c.getColumnIndex(SettingColumns.SECTIONS_NUM));
                result.sectionsAngle = parseSectionsAngle(c.getString(c.getColumnIndex(SettingColumns.SECTIONS_ANGLE)));
                result.sectionsName = parseSectionsName(c.getString(c.getColumnIndex(SettingColumns.SECTIONS_NAME)));
                result.arrowAngle = c.getFloat(c.getColumnIndex(SettingColumns.ARROW_ANGLE));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    public void updateSettingData(int sectionsNum, float[] sectionsAngle, float arrowAngle) {
        ContentValues values = new ContentValues();
        if (sectionsNum != Integer.MAX_VALUE) {
            values.put(SettingColumns.SECTIONS_NUM, sectionsNum);
        }
        if (sectionsAngle != null) {
            values.put(SettingColumns.SECTIONS_ANGLE, serializeSectionAngle(sectionsAngle));
        }
        if (arrowAngle != Integer.MAX_VALUE) {
            values.put(SettingColumns.ARROW_ANGLE, arrowAngle);
        }

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SETTING_NAME, values, SettingColumns._ID + "=0", null);
    }

    public void saveGyroscope(GyroscopeData data) {
        ContentValues values = new ContentValues();
        values.put(HistoryColumns.SECTIONS_NUM, data.sectionsNum);
        values.put(HistoryColumns.SECTIONS_ANGLE, serializeSectionAngle(data.sectionsAngle));
        values.put(HistoryColumns.ARROW_ANGLE, data.arrowAngle);
        values.put(HistoryColumns.SELECTED_SECTION, data.selectedSection);
        values.put(HistoryColumns.TIME, data.time);
        values.put(HistoryColumns.LOCATION, data.location);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HISTORY_NAME, HistoryColumns._ID, values);
    }

    public List<GyroscopeData> getAllHistoryGyroscope() {
        List<GyroscopeData> result = new ArrayList<>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_HISTORY_NAME);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext()) {
                GyroscopeData data = new GyroscopeData();
                data.sectionsNum = c.getInt(c.getColumnIndex(HistoryColumns.SECTIONS_NUM));
                data.sectionsAngle = parseSectionsAngle(c.getString(c.getColumnIndex(HistoryColumns.SECTIONS_ANGLE)));
                data.sectionsName = parseSectionsName(c.getString(c.getColumnIndex(HistoryColumns.SECTIONS_NAME)));
                data.arrowAngle = c.getFloat(c.getColumnIndex(HistoryColumns.ARROW_ANGLE));
                data.selectedSection = c.getInt(c.getColumnIndex(HistoryColumns.SELECTED_SECTION));
                data.time = c.getLong(c.getColumnIndex(HistoryColumns.TIME));
                data.location = c.getString(c.getColumnIndex(HistoryColumns.LOCATION));
                result.add(0, data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    private float[] parseSectionsAngle(String data) {
        String[] items = data.split(",");
        float[] angles = new float[items.length];
        for (int i = 0; i < items.length; ++ i) {
            angles[i] = Float.valueOf(items[0]);
        }
        return angles;
    }

    private String[] parseSectionsName(String data) {
        if (data == null) {
            return null;
        }
        return data.split(",");
    }

    private String serializeSectionAngle(float[] angles) {
        String result = "";
         for (int i = 0; i < angles.length; ++ i) {
            result += String.valueOf(angles[i]);
            if (i < angles.length - 1) {
                result += ",";
            }
        }
        return result;
    }
}
