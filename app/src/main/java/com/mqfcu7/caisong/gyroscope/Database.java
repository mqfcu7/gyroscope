package com.mqfcu7.caisong.gyroscope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gyroscope";
    private static final String TABLE_HISTORY_NAME = "history";
    private static final String TABLE_SETTING_NAME = "setting";

    private static final int DATABASE_VERSION = 1;

    public class GyroscopeData {
        public int sectionsNum;
        public float[] sectionsAngle;
        public String[] sectionsName;
        public int arrowAngle;
        public int selectedSection;
        public int time;
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
                + HistoryColumns.ARROW_ANGLE + " integer,"
                + HistoryColumns.SELECTED_SECTION + " integer,"
                + HistoryColumns.TIME + " integer,"
                + HistoryColumns.LOCATION + " text"
                + ");");
    }

    private void createSettingTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_SETTING_NAME + " ("
                + SettingColumns._ID + " integer primary key,"
                + SettingColumns.SECTIONS_NUM + " integer,"
                + SettingColumns.SECTIONS_ANGLE + " text,"
                + SettingColumns.SECTIONS_NAME + " text,"
                + SettingColumns.ARROW_ANGLE + " integer"
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
                result.arrowAngle = c.getInt(c.getColumnIndex(SettingColumns.ARROW_ANGLE));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    public void updateSettingData(int sectionsNum, float[] sectionsAngle, int arrowAngle) {
        ContentValues values = new ContentValues();
        if (sectionsNum != Integer.MAX_VALUE) {
            values.put(SettingColumns.SECTIONS_NUM, sectionsNum);
        }
        if (sectionsAngle != null) {
            String angles = "";
            for (int i = 0; i < sectionsNum; ++ i) {
                angles += String.valueOf(sectionsAngle[i]);
                if (i < sectionsNum - 1) {
                    angles += ",";
                }
            }
            values.put(SettingColumns.SECTIONS_ANGLE, angles);
        }
        if (arrowAngle != Integer.MAX_VALUE) {
            values.put(SettingColumns.ARROW_ANGLE, arrowAngle);
        }

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SETTING_NAME, values, SettingColumns._ID + "=0", null);
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
        return data.split(",");
    }
}
