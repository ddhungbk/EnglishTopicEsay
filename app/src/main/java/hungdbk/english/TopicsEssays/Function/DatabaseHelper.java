package hungdbk.english.TopicsEssays.Function;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;

import hungdbk.english.TopicsEssays.Model.EssayKeys;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper {

    private static final String DATABASE_NAME = "EnglishTopicEssay.db";
    private static final int SCHEMA_VERSION = 5;
    public static final String TB_ESSAY = "tbEssay";
    public static final String TB_TOPIC = "tbTopic";
    public static final String TB_VOCAB = "tbVocab";
    public static final String TB_VERSION = "tbVersion";

    private final Context context;
    public SQLiteHelper sqlHelper;
    private String PACKAGE_NAME;
    private String DATABASE_PATH;
    public String TOPIC_FIELDS;
    public String ESSAY_FIELDS;
    public String VOCAB_FIELDS;

    class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context) {
            super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.SCHEMA_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            if (!DatabaseHelper.this.checkExistedTable(db, DatabaseHelper.TB_TOPIC)) {
                String sqlTopic = "CREATE VIRTUAL TABLE tbTopic USING FTS3(%s INTEGER, %s INTEGER, " +
                        "%s TEXT, %s TEXT, %s TEXT DEFAULT 'unchecked');";
                Object[] objArr = new Object[]{TopicKeys.ID, TopicKeys.ORDER, TopicKeys.TITLE,
                        TopicKeys.DETAIL, TopicKeys.STATUS};
                db.execSQL(String.format(sqlTopic, objArr));
            }
            if (!DatabaseHelper.this.checkExistedTable(db, DatabaseHelper.TB_ESSAY)) {
                String sqlEssay = "CREATE VIRTUAL TABLE tbEssay USING FTS3(%s INTEGER, %s INTEGER, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT DEFAULT 'unchecked', " +
                        "%s INTEGER NOT NULL CONSTRAINT %s REFERENCES tbTopic(docid) ON DELETE CASCADE);";
                db.execSQL(String.format(sqlEssay, new Object[]{EssayKeys.ID, EssayKeys.ORDER,
                        EssayKeys.CONTENT, EssayKeys.TIME, EssayKeys.NOTE, EssayKeys.STATUS,
                        EssayKeys.TOPIC_ID, EssayKeys.TOPIC_ID}));
            }
            if (!DatabaseHelper.this.checkExistedTable(db, DatabaseHelper.TB_VOCAB)) {
                String sqlVocab = "CREATE VIRTUAL TABLE tbVocab USING FTS3(%s INTEGER, %s INTEGER, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT DEFAULT 'unchecked', " +
                        "%s INTEGER NOT NULL CONSTRAINT %s REFERENCES tbEssay(docid) ON DELETE CASCADE);";
                db.execSQL(String.format(sqlVocab, new Object[]{VocabKeys.ID, VocabKeys.ORDER, VocabKeys.WORD,
                        VocabKeys.TIME, VocabKeys.NOTE, VocabKeys.STATUS, VocabKeys.ESSAY_ID, VocabKeys.ESSAY_ID}));
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tbTopic");
            db.execSQL("DROP TABLE IF EXISTS tbEssay");
            db.execSQL("DROP TABLE IF EXISTS tbVocab");
            onCreate(db);
        }

        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }
    }

    public DatabaseHelper(Context context) {
        this.TOPIC_FIELDS = TopicKeys.ID + ", " + TopicKeys.ORDER + ", " + TopicKeys.TITLE + ", "
                + TopicKeys.DETAIL + ", "  + TopicKeys.STATUS;
        this.ESSAY_FIELDS = EssayKeys.ID + ", " + EssayKeys.ORDER + ", " + EssayKeys.CONTENT + ", "
                + EssayKeys.TIME + ", " + EssayKeys.NOTE + ", " + EssayKeys.STATUS + ", " + EssayKeys.TOPIC_ID;
        this.VOCAB_FIELDS = VocabKeys.ID + ", " + VocabKeys.ORDER + ", " + VocabKeys.WORD + ", "
                + VocabKeys.TIME + ", " + VocabKeys.NOTE + ", " + VocabKeys.STATUS + ", " + VocabKeys.ESSAY_ID;
        this.context = context;
        this.sqlHelper = new SQLiteHelper(context);
        PACKAGE_NAME = context.getPackageName();
        DATABASE_PATH = "data/data/" + PACKAGE_NAME + "/databases/" + DATABASE_NAME;
    }

    public void close() {
        if (this.sqlHelper != null) {
            this.sqlHelper.close();
        }
    }

    public void copyDatabase() throws IOException {
        Log.d("APP", "Start copy...");
        InputStream input = this.context.getAssets().open(DATABASE_NAME);
        OutputStream output = new FileOutputStream(DATABASE_PATH);
        Log.d("APP", "copying");
        byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        while (true) {
            int length = input.read(buffer);
            if (length > 0) {
                output.write(buffer, 0, length);
            } else {
                output.flush();
                Log.d("APP", "Finish copy");
                output.close();
                input.close();
                return;
            }
        }
    }

    public void openDatabase() {
        if (checkExistedDatabase()) {
            Log.d("APP", "Db has existed");
            return;
        }
        this.sqlHelper.getReadableDatabase();
        try {
            Log.d("APP", "Copying database...");
            copyDatabase();
            Log.d("APP", "Copy done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH, null, 1);
        Cursor cursor = db.rawQuery("SELECT version_id FROM dbVersion", null);
        cursor.moveToFirst();
        int v = cursor.getInt(0);
        db.close();
        return v;
    }

    public void deleteDatabase() {
        try {
            SQLiteDatabase db = this.sqlHelper.getReadableDatabase();
            db.execSQL("DROP TABLE tbTopic");
            db.execSQL("DROP TABLE tbEssay");
            db.execSQL("DROP TABLE tbVocab");
            this.sqlHelper.onCreate(db);
        } catch (SQLException e) {
            Log.d("ERROR", e.toString());
        }
    }

    public boolean checkExistedTable(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean checkExistedDatabase() {
        if (new File(DATABASE_PATH).exists()) {
            return true;
        }
        return false;
    }

    public boolean removeDatabase() {
        return new File(DATABASE_PATH).delete();
    }

    public SQLiteHelper getSqlHelper() {
        return sqlHelper;
    }


}
