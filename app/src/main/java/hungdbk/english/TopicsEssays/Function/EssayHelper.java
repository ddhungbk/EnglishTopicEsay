package hungdbk.english.TopicsEssays.Function;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

import hungdbk.english.TopicsEssays.BuildConfig;
import hungdbk.english.TopicsEssays.Model.Essay;
import hungdbk.english.TopicsEssays.Model.EssayKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

public class EssayHelper extends DatabaseHelper {

    public EssayHelper(Context context){
        super(context);
    }

    public Cursor getAllEssays(String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " ORDER BY " + filter, null);
    }

    public Cursor getEssaysOfTopic(int topicId, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + EssayKeys.TOPIC_ID + "=" + Integer.toString(topicId) + " ORDER BY " + filter, null);
    }

    public Cursor getEssayById(int id) {
        String[] args = new String[]{Integer.toString(id)};
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE docid=?", args);
    }

    public Cursor getEssayByKey(String field, String key) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + field + "='" + key + "'", null);
    }

    public Cursor getEssaysByStatus(String status, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + VocabKeys.STATUS + " MATCH '" + status + "' ORDER BY " + filter, null);
    }

    public Cursor getEssaysByStatus(int topicId, String status, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + EssayKeys.TOPIC_ID + " = " + Integer.toString(topicId) + " AND " + VocabKeys.STATUS + " MATCH '" + status + "' ORDER BY " + filter, null);
    }

    public Cursor getRandomEssaysByStatus(int number, String status, String filter) {
        String sql;
        if (status == EssayKeys.ALL_ESSAY) {
            sql = "SELECT docid FROM tbEssay ORDER BY docid;";
        } else {
            sql = "SELECT docid FROM tbEssay WHERE status MATCH '" + status + "' ORDER BY docid" + ";";
        }
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery(sql, null);
        ArrayList<Integer> result = AppSystem.getRandomArray(cursor, number);
        cursor.close();
        if (result.size() <= 0) {
            return null;
        }
        String query = "'" + result.get(0) + "'";
        for (int i = 1; i < result.size(); i++) {
            query = query + ",'" + result.get(i) + "'";
        }
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE docid IN(" + query + ") ORDER BY " + filter, null);
    }

    public Cursor getRandomEssaysByStatus(int topicId, int number, String status, String filter) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT docid FROM tbEssay WHERE status MATCH '" + status + "' AND " + EssayKeys.TOPIC_ID + "=" + Integer.toString(topicId) + " ORDER BY docid", null);
        ArrayList<Integer> result = AppSystem.getRandomArray(cursor, number);
        cursor.close();
        if (result.size() <= 0) {
            return null;
        }
        String query = "'" + result.get(0) + "'";
        for (int i = 1; i < result.size(); i++) {
            query = query + ",'" + result.get(i) + "'";
        }
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE docid IN(" + query + ") ORDER BY " + filter, null);
    }

    public Cursor searchEssayByInputText(String inputText) throws SQLException {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + TB_ESSAY + " MATCH '" + inputText + "*" + "';", null);
    }

    public Cursor searchEssayByInputText(int topicId, String inputText) throws SQLException {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + ESSAY_FIELDS + " FROM " + TB_ESSAY + " WHERE " + TB_ESSAY + " MATCH '" + inputText + "*" + "' AND " + EssayKeys.TOPIC_ID + "=" + Integer.toString(topicId) + ";", null);
    }

    public Cursor getArrayEssayOrder(int topicId) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, _order FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + " ORDER BY " + VocabKeys.ORDER + ";", null);
    }

    public int getMaxEssayId() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT docid FROM tbEssay WHERE docid=(SELECT MAX(docid) FROM tbEssay)", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getMaxEssayOrder(int topicId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbEssay WHERE _order=(SELECT MAX(_order) FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + ")", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getNextEssayOrder(int topicId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + " ORDER BY " + VocabKeys.ORDER + BuildConfig.FLAVOR, null);
        int next = AppSystem.getNextOrder(cursor);
        cursor.close();
        return next;
    }

    public boolean isEssayOrderExisted(int topicId, int order) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + " ORDER BY " + VocabKeys.ORDER + BuildConfig.FLAVOR, null);
        boolean isExisted = AppSystem.isExistedOrder(cursor, order);
        cursor.close();
        return isExisted;
    }

    public int getNumberOfEssays() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbEssay", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfEssays(String status) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbEssay WHERE status MATCH '" + status + "';", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfEssays(int topicId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + ";", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfEssays(int topicId, String status) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbEssay WHERE topicId = " + Integer.toString(topicId) + " AND " + VocabKeys.STATUS + " MATCH '" + status + "';", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public long insertEssay(int id, Essay essay, String status, int topicId) {
        ContentValues cv = new ContentValues();
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(essay.getOrder()));
        cv.put(EssayKeys.CONTENT, essay.getContent());
        cv.put(VocabKeys.TIME, essay.getTime());
        cv.put(VocabKeys.NOTE, essay.getNote());
        cv.put(VocabKeys.STATUS, status);
        cv.put(EssayKeys.TOPIC_ID, Integer.valueOf(topicId));
        return sqlHelper.getWritableDatabase().insert(TB_ESSAY, null, cv);
    }

    public int updateEssay(int docid, int id, Essay essay, String status) {
        ContentValues cv = new ContentValues();
        cv.put("docid", Integer.valueOf(docid));
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(essay.getOrder()));
        cv.put(EssayKeys.CONTENT, essay.getContent());
        cv.put(VocabKeys.TIME, essay.getTime());
        cv.put(VocabKeys.NOTE, essay.getNote());
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_ESSAY, cv, "docid=?", new String[]{Integer.toString(docid)});
    }

    public int updateEssayByTime(int docid, String time) {
        ContentValues cv = new ContentValues();
        String[] args = new String[]{Integer.toString(docid)};
        cv.put(VocabKeys.TIME, time);
        return sqlHelper.getWritableDatabase().update(TB_ESSAY, cv, "docid=?", args);
    }

    public void updateEssaysByStatus(String status) {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbEssay SET status='" + status + "';");
    }

    public void updateEssaysByStatus(int topicId, String status) {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbEssay SET status='" + status + "' WHERE " + EssayKeys.TOPIC_ID + "=" + Integer.toString(topicId) + ";");
    }

    public int updateEssayStatus(int docid, String status) {
        ContentValues cv = new ContentValues();
        String[] args = new String[]{Integer.toString(docid)};
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_ESSAY, cv, "docid=?", args);
    }

    public int deleteEssayById(int docid) {
        return sqlHelper.getReadableDatabase().delete(TB_ESSAY, "docid=?", new String[]{Integer.toString(docid)});
    }

    public void deleteEssaysOfTopic(int topicId) {
        sqlHelper.getReadableDatabase().execSQL("DELETE FROM tbEssay WHERE topicId = " + Integer.toString(topicId));
    }

    public int getEssayId(Cursor cursor) {
        return cursor.getInt(EssayKeys.ID_INDEX);
    }

    public int getEssayOrder(Cursor cursor) {
        return cursor.getInt(EssayKeys.ORDER_INDEX);
    }

    public String getEssayContent(Cursor cursor) {
        return cursor.getString(EssayKeys.CONTENT_INDEX);
    }

    public String getEssayTime(Cursor cursor) {
        return cursor.getString(EssayKeys.TIME_INDEX);
    }

    public String getEssayNote(Cursor cursor) {
        return cursor.getString(EssayKeys.NOTE_INDEX);
    }

    public String getEssayStatus(Cursor cursor) {
        return cursor.getString(EssayKeys.STATUS_INDEX);
    }

    public int getEssayTopicId(Cursor cursor) {
        return Integer.parseInt(cursor.getString(EssayKeys.TOPIC_ID_INDEX));
    }

}
