package hungdbk.english.TopicsEssays.Function;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.Model.Topic;

public class TopicHelper extends DatabaseHelper {

    public TopicHelper(Context context) {
        super(context);
    }

    public Cursor getAllTopics(String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid , " + TOPIC_FIELDS + " FROM " + TB_TOPIC + " ORDER BY " + filter, null);
    }

    public Cursor getTopicById(int id) {
        String[] args = new String[]{Integer.toString(id)};
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + TOPIC_FIELDS + " FROM " + TB_TOPIC + " WHERE docid=?", args);
    }

    public Cursor getTopicsByStatus(String status, String filter) {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + TOPIC_FIELDS + " FROM " + TB_TOPIC + " WHERE " + VocabKeys.STATUS + " MATCH '" + status + "' ORDER BY " + filter + ";", null);
    }

    public Cursor getRandomTopics(int number, String status, String filter) {
        String sql;
        if (status == TopicKeys.ALL_TOPIC) {
            sql = "SELECT docid FROM tbTopic ORDER BY docid;";
        } else {
            sql = "SELECT docid FROM tbTopic WHERE status MATCH '" + status + "' ORDER BY docid" + ";";
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
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + TOPIC_FIELDS + " FROM " + TB_TOPIC + " WHERE docid IN(" + query + ") ORDER BY " + filter, null);
    }

    public Cursor searchTopicByInputText(String inputText) throws SQLException {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, " + TOPIC_FIELDS + " FROM " + TB_TOPIC + " WHERE " + TB_TOPIC + " MATCH '" + inputText + "*" + "' ;", null);
    }

    public Cursor getArrayOfTopicOrders() {
        return sqlHelper.getReadableDatabase().rawQuery("SELECT docid, _order FROM tbTopic ORDER BY _order", null);
    }

    public int getMaxTopicId() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT docid FROM tbTopic WHERE docid=(SELECT MAX(docid) FROM tbTopic);", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getMaxTopicOrder() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbTopic WHERE _order = (SELECT MAX(_order) FROM tbTopic)", null);
        int max = 0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public int getNextTopicOrder() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbTopic ORDER BY _order", null);
        int next = AppSystem.getNextOrder(cursor);
        cursor.close();
        return next;
    }

    public boolean isTopicOrderExisted(int order) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT _order FROM tbTopic ORDER BY _order", null);
        boolean isExisted = AppSystem.isExistedOrder(cursor, order);
        cursor.close();
        return isExisted;
    }

    public int getNumberOfTopics() {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbTopic", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getNumberOfTopics(String status) {
        Cursor cursor = sqlHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM tbTopic WHERE status MATCH '" + status + "';", null);
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public long insertTopic(int id, Topic topic, String status) {
        ContentValues cv = new ContentValues();
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(topic.getOrder()));
        cv.put(TopicKeys.TITLE, topic.getTitle());
        cv.put(TopicKeys.DETAIL, topic.getDetail());
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().insert(TB_TOPIC, null, cv);
    }

    public int updateTopic(int docid, int id, Topic topic, String status) {
        ContentValues cv = new ContentValues();
        cv.put(VocabKeys.ID, Integer.valueOf(id));
        cv.put(VocabKeys.ORDER, Integer.valueOf(topic.getOrder()));
        cv.put(TopicKeys.TITLE, topic.getTitle());
        cv.put(TopicKeys.DETAIL, topic.getDetail());
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_TOPIC, cv, "docid=?", new String[]{Integer.toString(docid)});
    }

    public int updateTopic(int docid, String title, String detail) {
        ContentValues cv = new ContentValues();
        cv.put(TopicKeys.TITLE, title);
        cv.put(TopicKeys.DETAIL, detail);
        return sqlHelper.getWritableDatabase().update(TB_TOPIC, cv, "docid=?", new String[]{Integer.toString(docid)});
    }

    public void updateTopics(String status) {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbTopic SET status='" + status + "';");
    }

    public int updateTopic(int docid, String status) {
        ContentValues cv = new ContentValues();
        cv.put(VocabKeys.STATUS, status);
        return sqlHelper.getWritableDatabase().update(TB_TOPIC, cv, "docid=?", new String[]{Integer.toString(docid)});
    }

    public void toUpperCase() {
        sqlHelper.getWritableDatabase().execSQL("UPDATE tbTopic SET title = UPPER(title)");
    }

    public int deleteTopic(int id) {
        //deleteEssaysOfTopic(id);
        return sqlHelper.getWritableDatabase().delete(TB_TOPIC, "docid=?", new String[]{Integer.toString(id)});
    }

    public int getTopicId(Cursor cursor) {
        return cursor.getInt(TopicKeys.ID_INDEX);
    }

    public int getTopicOrder(Cursor cursor) {
        return cursor.getInt(TopicKeys.ORDER_INDEX);
    }

    public String getTopicTitle(Cursor cursor) {
        return cursor.getString(TopicKeys.TITLE_INDEX);
    }

    public String getTopicDetail(Cursor cursor) {
        return cursor.getString(TopicKeys.DETAIL_INDEX);
    }

    public String getTopicStatus(Cursor cursor) {
        return cursor.getString(TopicKeys.STATUS_INDEX);
    }
}
