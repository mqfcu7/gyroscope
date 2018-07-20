package com.mqfcu7.jiangmeilan.gyroscope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "com.mqfcu7.jiangmeilan.gyroscope";
    private static final String TABLE_QUESTION_NAME = "question";
    private static final String TABLE_HISTORY_NAME = "history";
    private static final String TABLE_SETTING_NAME = "setting";
    private static final String TABLE_GAME_NAME = "game";

    private static final int DATABASE_VERSION = 1;

    public static class GyroscopeData {
        public String title;
        public int sectionsNum;
        public float[] sectionsAngle;
        public String[] sectionsName = null;
        public float arrowAngle;
        public int selectedSection = Gyroscope.INVALID_SELECTED_SECTION;
        public long time;
        public String location;
    }

    public static class GameData {
        public float arrowAngle;
        public int score;
    }

    private Context mContext;

    private abstract class QuestionColumns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String SECTIONS_NUM = "sections_num";
        public static final String SECTIONS_ANGLE = "sections_angle";
        public static final String SECTIONS_NAME = "sections_name";
    }

    private abstract class HistoryColumns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String SECTIONS_NUM = "sections_num";
        public static final String SECTIONS_ANGLE = "sections_angle";
        public static final String SECTIONS_NAME = "sections_name";
        public static final String ARROW_ANGLE = "arrow_angle";
        public static final String SELECTED_SECTION = "selected_section";
        public static final String TIME = "time";
        public static final String LOCATION = "location";
    }

    private abstract class SettingColumns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String SECTIONS_NUM = "sections_num";
        public static final String SECTIONS_ANGLE = "sections_angle";
        public static final String SECTIONS_NAME = "sections_name";
        public static final String ARROW_ANGLE = "arrow_angle";
    }

    private abstract class GameColumns implements BaseColumns {
        public static final String ARROW_ANGLE = "arrow_angle";
        public static final String SCORE = "score";
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createQuestionTable(db);
        createHistoryTable(db);
        createSettingTable(db);
        createGameTable(db);
    }

    private void createQuestionTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_QUESTION_NAME + " ("
                + QuestionColumns._ID + " integer primary key,"
                + QuestionColumns.TITLE + " text,"
                + QuestionColumns.SECTIONS_NUM + " integer,"
                + QuestionColumns.SECTIONS_ANGLE + " text,"
                + QuestionColumns.SECTIONS_NAME + " text"
                + ");");

        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(0,'轮到谁',6,'60,60,60,60,60,60','1,2,3,4,5,6');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(1,'喜不喜欢',10,'60,60,60,60,60,60','喜欢,不喜欢,喜欢,不喜欢,喜欢,不喜欢,喜欢,不喜欢,喜欢,不喜欢');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(2,'ABCD',4,'90,90,90,90','A,B,C,D');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(3,'待会吃什么',8,'45,45,45,45,45,45,45,45','火锅,日料,川菜,港式,快餐,烤肉,东南亚菜,西餐');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(4,'颜色',6,'60,60,60,60,60,60','红,黄,蓝,绿,灰,紫');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(5,'学习',8,'45,45,45,45,45,45,45,45','语文,数学,英语,政治,物理,历史,生物,地理');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(6,'喝什么',9,'40,40,40,40,40,40,40,40,40','西瓜汁,玉米汁,橙汁,茶,可乐,雪碧,柠檬水,酸梅汁,啤酒');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(7,'看哪部电影',5,'72,72,72,72,72','我不是药神,邪不压正,动物世界,新大头儿子,超人总动员');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(8,'去哪玩',6,'60,60,60,60,60,60','密室逃脱,桌游,唱歌,电影,桑拿,酒吧');");
        db.execSQL("insert into " + TABLE_QUESTION_NAME + " values(9,'酒吧',9,'40,40,40,40,40,40,40,40,40','选异性拥抱,下家唱,异性交杯酒,PASS,喝半杯,上家唱,全女士半杯,亲异性,全男士半杯');");
    }

    private void createHistoryTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_HISTORY_NAME + " ("
                + HistoryColumns._ID + " integer primary key,"
                + QuestionColumns.TITLE + " text,"
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
                + SettingColumns.TITLE + " text,"
                + SettingColumns.SECTIONS_NUM + " integer,"
                + SettingColumns.SECTIONS_ANGLE + " text,"
                + SettingColumns.SECTIONS_NAME + " text,"
                + SettingColumns.ARROW_ANGLE + " real"
                + ");");

        db.execSQL("insert into " + TABLE_SETTING_NAME + " values(0,'轮到谁',6,'60,60,60,60,60,60','1,2,3,4,5,6',290);");
    }

    private void createGameTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_GAME_NAME + " ("
                + GameColumns._ID + " integer primary key,"
                + GameColumns.ARROW_ANGLE + " real,"
                + GameColumns.SCORE + " integer"
                + ");");

        db.execSQL("insert into " + TABLE_GAME_NAME + " values(0,290,1000);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getQuestionTitle() {
        ArrayList<String> result = new ArrayList<>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_QUESTION_NAME);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext()) {
                String title = c.getString(c.getColumnIndex(QuestionColumns.TITLE));
                result.add(title);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public GyroscopeData getQuestionRecord(String title) {
        GyroscopeData result = null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_QUESTION_NAME);
        qb.appendWhere(QuestionColumns.TITLE + "='" + title + "'");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);

            if (c.moveToFirst()) {
                result = new GyroscopeData();
                result.title = title;
                result.sectionsNum = c.getInt(c.getColumnIndex(QuestionColumns.SECTIONS_NUM));
                result.sectionsAngle = parseSectionsAngle(c.getString(c.getColumnIndex(QuestionColumns.SECTIONS_ANGLE)));
                result.sectionsName = parseSectionsName(c.getString(c.getColumnIndex(QuestionColumns.SECTIONS_NAME)));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    public void updateQuestionRecord(int sectionsNum, float[] sectionsAngle, String[] sectionsName) {
        ContentValues values = new ContentValues();
        values.put(QuestionColumns.SECTIONS_NUM, sectionsNum);
        values.put(QuestionColumns.SECTIONS_ANGLE, serializeSectionAngle(sectionsAngle));
        values.put(QuestionColumns.SECTIONS_NAME, serializeSectionName(sectionsName));

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_QUESTION_NAME, values, QuestionColumns._ID + "=0", null);
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
                result.title = c.getString(c.getColumnIndex(SettingColumns.TITLE));
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

    public void updateSettingData(String title,
                                  int sectionsNum,
                                  float[] sectionsAngle,
                                  String[] sectionsName,
                                  float arrowAngle,
                                  boolean isFromSetting) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        if (isFromSetting) {
            values.put(SettingColumns.SECTIONS_NUM, sectionsNum);
            values.put(SettingColumns.SECTIONS_ANGLE, serializeSectionAngle(sectionsAngle));
            values.put(SettingColumns.SECTIONS_NAME, serializeSectionName(sectionsName));
            db.update(TABLE_SETTING_NAME, values, SettingColumns._ID + "=0", null);
            updateQuestionRecord(sectionsNum, sectionsAngle, sectionsName);
        } else {
            if (title != "") {
                values.put(SettingColumns.TITLE, title);
            }
            if (sectionsNum != Integer.MAX_VALUE) {
                values.put(SettingColumns.SECTIONS_NUM, sectionsNum);
            }
            if (sectionsAngle != null) {
                values.put(SettingColumns.SECTIONS_ANGLE, serializeSectionAngle(sectionsAngle));
            }
            if (sectionsName != null && sectionsName.length == sectionsNum) {
                values.put(SettingColumns.SECTIONS_NAME, serializeSectionName(sectionsName));
            }
            if (arrowAngle != Integer.MAX_VALUE) {
                values.put(SettingColumns.ARROW_ANGLE, arrowAngle);
            }
            db.update(TABLE_SETTING_NAME, values, SettingColumns._ID + "=0", null);
        }
    }

    public void saveGyroscope(GyroscopeData data) {
        ContentValues values = new ContentValues();
        values.put(HistoryColumns.TITLE, data.title);
        values.put(HistoryColumns.SECTIONS_NUM, data.sectionsNum);
        values.put(HistoryColumns.SECTIONS_ANGLE, serializeSectionAngle(data.sectionsAngle));
        values.put(HistoryColumns.SECTIONS_NAME, serializeSectionName(data.sectionsName));
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
                data.title = c.getString(c.getColumnIndex(HistoryColumns.TITLE));
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

    public GameData getGameData() {
        GameData result = null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_GAME_NAME);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result = new GameData();
                result.arrowAngle = c.getFloat(c.getColumnIndex(GameColumns.ARROW_ANGLE));
                result.score = c.getInt(c.getColumnIndex(GameColumns.SCORE));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    public void updateGameData(float arrowAngle, int score) {
        ContentValues values = new ContentValues();
        values.put(GameColumns.ARROW_ANGLE, arrowAngle);
        if (score != Integer.MAX_VALUE) {
            values.put(GameColumns.SCORE, score);
        }

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_GAME_NAME, values, GameColumns._ID + "=0", null);
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

    private String serializeSectionName(String[] names) {
        if (names == null) {
            return "";
        }
        String result = "";
        for (int i = 0; i < names.length; ++ i) {
            result += names[i];
            if (i < names.length - 1) {
                result += ",";
            }
        }
        return result;
    }
}
