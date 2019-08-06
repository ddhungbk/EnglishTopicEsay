package hungdbk.english.TopicsEssays.Function;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

import hungdbk.english.TopicsEssays.BuildConfig;
import hungdbk.english.TopicsEssays.Model.EssayKeys;
import hungdbk.english.TopicsEssays.Model.Vocab;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

public class VocabHelper extends  DatabaseHelper{

    public VocabHelper(Context context){
        super(context);
    }

    public Cursor getAllVocabs(String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " ORDER BY " + filter, null);
    }

    public Cursor getVocabsOfTopic(int essayId, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + VocabKeys.ESSAY_ID + "=" + Integer.toString(essayId) + " ORDER BY " + filter, null);
    }

    public Cursor getVocabById(int id) {
        String[] args = new String[]{Integer.toString(id)};
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE docid=?", args);
    }

    public Cursor getVocabByKey(String field, String key) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + field + "='" + key + "'", null);
    }

    public Cursor getVocabsByStatus(String status, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + VocabKeys.STATUS + " MATCH '" + status + "' ORDER BY " + filter, null);
    }

    public Cursor getVocabsByStatus(int essayId, String status, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + VocabKeys.ESSAY_ID + " = " + Integer.toString(essayId) + " AND " + VocabKeys.STATUS + " MATCH '" + status + "' ORDER BY " + filter, null);
    }

    public Cursor getRandomVocabsByStatus(int number, String status, String filter) {
        String sql;
        if (status == EssayKeys.ALL_ESSAY) {
            sql = "SELECT docid FROM tbVocab ORDER BY docid;";
        } else {
            sql = "SELECT docid FROM tbVocab WHERE status MATCH '" + status + "' ORDER BY docid" + ";";
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
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE docid IN(" + query + ") ORDER BY " + filter, null);
    }

    public Cursor getRandomVocabsByStatus(int essayId, int number, String status, String filter) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT docid FROM tbVocab WHERE status MATCH '" + status + "' AND " + VocabKeys.ESSAY_ID + "=" + Integer.toString(essayId) + " ORDER BY docid", null);
        ArrayList<Integer> result = AppSystem.getRandomArray(cursor, number);
        cursor.close();
        if (result.size() <= 0) {
            return null;
        }
        String query = "'" + result.get(0) + "'";
        for (int i = 1; i < result.size(); i++) {
            query = query + ",'" + result.get(i) + "'";
        }
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE docid IN(" + query + ") ORDER BY " + filter, null);
    }

    public Cursor searchVocabByInputText(String inputText) throws SQLException {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + TB_VOCAB + " MATCH '" + inputText + "*" + "';", null);
    }

    public Cursor searchVocabByInputText(int essayId, String inputText) throws SQLException {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + VOCAB_FIELDS + " FROM " + TB_VOCAB + " WHERE " + TB_VOCAB + " MATCH '" + inputText + "*" + "' AND " + VocabKeys.ESSAY_ID + "=" + Integer.toString(essayId) + ";", null);
    }

    public Cursor getArrayVocabOrder(int essayId) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, _order FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + " ORDER BY " + VocabKeys.ORDER + ";", null);
    }

    public int getMaxVocabId() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT docid FROM tbVocab WHERE docid=(SELECT MAX(docid) FROM tbVocab)", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getMaxVocabOrder(int essayId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbVocab WHERE _order=(SELECT MAX(_order) FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + ")", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getNextVocabOrder(int essayId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + " ORDER BY " + VocabKeys.ORDER + BuildConfig.FLAVOR, null);
        int next = AppSystem.getNextOrder(cursor);
        cursor.close();
        return next;
    }

    public boolean isVocabOrderExisted(int essayId, int order) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + " ORDER BY " + VocabKeys.ORDER + BuildConfig.FLAVOR, null);
        boolean isExisted = AppSystem.isExistedOrder(cursor, order);
        cursor.close();
        return isExisted;
    }

    public int getNumberOfVocabs() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbVocab", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfVocabs(String status) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbVocab WHERE status MATCH '" + status + "';", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfVocabs(int essayId) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + ";", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfVocabs(int essayId, String status) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbVocab WHERE essayId = " + Integer.toString(essayId) + " AND " + VocabKeys.STATUS + " MATCH '" + status + "';", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public long insertVocab(int id, Vocab vocab, String status, int essayId) {
        ContentValues cv = new ContentValues();
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(vocab.getOrder()));
        cv.put(VocabKeys.WORD, vocab.getWord());
        cv.put(VocabKeys.TIME, vocab.getTime());
        cv.put(VocabKeys.NOTE, vocab.getNote());
        cv.put(VocabKeys.STATUS, status);
        cv.put(VocabKeys.ESSAY_ID, Integer.valueOf(essayId));
        return sqlHelper.getWritableDatabase().insert(TB_VOCAB, null, cv);
    }

    public int updateVocab(int docid, int id, Vocab vocab, String status) {
        ContentValues cv = new ContentValues();
        cv.put("docid", Integer.valueOf(docid));
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(vocab.getOrder()));
        cv.put(VocabKeys.WORD, vocab.getWord());
        cv.put(VocabKeys.TIME, vocab.getTime());
        cv.put(VocabKeys.NOTE, vocab.getNote());
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_VOCAB, cv, "docid=?", new String[]{Integer.toString(docid)});
    }

    public void updateVocabsByStatus(String status) {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbVocab SET status='" + status + "';");
    }

    public void updateVocabsByStatus(int essayId, String status) {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbVocab SET status='" + status + "' WHERE " + VocabKeys.ESSAY_ID + "=" + Integer.toString(essayId) + ";");
    }

    public int updateVocabStatus(int docid, String status) {
        ContentValues cv = new ContentValues();
        String[] args = new String[]{Integer.toString(docid)};
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_VOCAB, cv, "docid=?", args);
    }

    public int deleteVocabById(int docid) {
        return sqlHelper.getReadableDatabase().delete(TB_VOCAB, "docid=?", new String[]{Integer.toString(docid)});
    }

    public void deleteVocabsOfTopic(int essayId) {
        sqlHelper.getReadableDatabase().execSQL("DELETE FROM tbVocab WHERE essayId = " + Integer.toString(essayId));
    }

    public int getVocabId(Cursor cursor) {
        return cursor.getInt(VocabKeys.ID_INDEX);
    }

    public int getVocabOrder(Cursor cursor) {
        return cursor.getInt(VocabKeys.ORDER_INDEX);
    }

    public String getVocabWord(Cursor cursor) {
        return cursor.getString(VocabKeys.WORD_INDEX);
    }

    public String getVocabTime(Cursor cursor) {
        return cursor.getString(VocabKeys.TIME_INDEX);
    }

    public String getVocabNote(Cursor cursor) {
        return cursor.getString(VocabKeys.NOTE_INDEX);
    }

    public String getVocabStatus(Cursor cursor) {
        return cursor.getString(VocabKeys.STATUS_INDEX);
    }

    public int getVocabEssayId(Cursor cursor) {
        return Integer.parseInt(cursor.getString(VocabKeys.ESSAY_ID_INDEX));
    }

}
