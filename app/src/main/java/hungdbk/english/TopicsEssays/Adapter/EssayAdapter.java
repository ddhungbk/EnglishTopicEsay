package hungdbk.english.TopicsEssays.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hungdbk.english.TopicsEssays.Function.AppSystem;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

public class EssayAdapter extends CursorAdapter {
    private Activity activity;
    private TopicHelper tpHelper;
    private EssayHelper esHelper;
    private VocabHelper vbHelper;
    private boolean listType;

    public EssayAdapter(Context context, Cursor c, boolean listType) {
        super(context, c);
        this.activity = (Activity) context;
        tpHelper = new TopicHelper(context);
        esHelper = new EssayHelper(context);
        vbHelper = new VocabHelper(context);
        this.listType = listType;
    }

    public View newView(Context context, Cursor c, ViewGroup parent) {
        View row = this.activity.getLayoutInflater().inflate(R.layout.row_essay, parent, false);
        row.setTag(new EssayHolder(row));
        return row;
    }

    public void bindView(View row, Context context, Cursor cursor) {
        ((EssayHolder) row.getTag()).displayRow(cursor, tpHelper, esHelper, vbHelper, this.listType);
    }
}
class EssayHolder {
    private TextView tvContent;
    private TextView tvInfo;
    private TextView tvOrder;
    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvTitle;

    public EssayHolder(View row) {
        this.tvOrder = (TextView) row.findViewById(R.id.tvOrder);
        this.tvTitle = (TextView) row.findViewById(R.id.tvTitle);
        this.tvInfo = (TextView) row.findViewById(R.id.tvInfo);
        this.tvTime = (TextView) row.findViewById(R.id.tvTime);
        this.tvContent = (TextView) row.findViewById(R.id.tvContent);
        this.tvStatus = (TextView) row.findViewById(R.id.tvStatus);
    }

    void displayRow(final Cursor cursor, final TopicHelper tpHelper, final EssayHelper esHelper, final VocabHelper vbHelper, boolean listType) {
        int topicId = esHelper.getEssayTopicId(cursor);
        if (listType) {
            this.tvOrder.setText("");
            this.tvOrder.setBackgroundResource(AppSystem.arrIcons[topicId]);
            this.tvTitle.setText("Essay #" + esHelper.getEssayOrder(cursor));
            this.tvInfo.setText("" + vbHelper.getNumberOfVocabs(esHelper.getEssayId(cursor), VocabKeys.UNCHECKED) + "/" + vbHelper.getNumberOfVocabs(esHelper.getEssayId(cursor)) + " new words");
            if (esHelper.getEssayTime(cursor) == null || esHelper.getEssayTime(cursor).equalsIgnoreCase("brand new!")) {
                this.tvTime.setText("" + esHelper.getEssayTime(cursor));
            } else {
                Date date = new Date();
                date.setTime(Long.parseLong(esHelper.getEssayTime(cursor)));
                this.tvTime.setText("" + new SimpleDateFormat("MMM d, yy  hh:mm:ss a").format(date));
            }
        }
        if (!listType) {
            this.tvOrder.setText("" + esHelper.getEssayId(cursor));
            this.tvOrder.setBackgroundResource(AppSystem.arrIcons[topicId]);
            Cursor cTopic = tpHelper.getTopicById(topicId);
            if (cTopic == null || !cTopic.moveToFirst()) {
                this.tvTitle.setText("Essay #" + esHelper.getEssayOrder(cursor));
                this.tvInfo.setText("Topic " + topicId + ", Essay " + esHelper.getEssayOrder(cursor));
            } else {
                this.tvTitle.setText("" + cTopic.getString(3));
                this.tvInfo.setText("Topic " + topicId + ", Essay " + esHelper.getEssayOrder(cursor));
            }
            if (esHelper.getEssayTime(cursor) == null || esHelper.getEssayTime(cursor).equalsIgnoreCase("brand new!")) {
                this.tvTime.setText("" + esHelper.getEssayTime(cursor));
            } else {
                Date date = new Date();
                date.setTime(Long.parseLong(esHelper.getEssayTime(cursor)));
                this.tvTime.setText("" + new SimpleDateFormat("MMM d, yyyy hh:mm:ss a").format(date));
            }
            cTopic.close();
        }
        this.tvContent.setText("" + esHelper.getEssayContent(cursor).substring(3, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION).replace("<p>", "").replace("</p>", ""));
        String status = esHelper.getEssayStatus(cursor);
        if (status.equals(VocabKeys.CHECKED)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_checked);
        } else if (status.equals(VocabKeys.MARKED)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_marked);
        } else {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_unchecked);
        }
        this.tvStatus.setTag(R.id.tvStatus, Integer.valueOf(cursor.getPosition()));
        this.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPosition(((Integer)v.getTag(R.id.tvStatus)).intValue() )){
                    String time = "" + Calendar.getInstance().getTimeInMillis();
                    String status = esHelper.getEssayStatus(cursor);
                    if (status.equalsIgnoreCase(VocabKeys.CHECKED)) {
                        status = VocabKeys.MARKED;
                    } else if (status.equalsIgnoreCase(VocabKeys.MARKED)) {
                        status = VocabKeys.UNCHECKED;
                    } else {
                        status = VocabKeys.CHECKED;
                    }
                    esHelper.updateEssayByTime(esHelper.getEssayId(cursor), time);
                    esHelper.updateEssayStatus(esHelper.getEssayId(cursor), status);
                    int topicId = esHelper.getEssayTopicId(cursor);
                    int total = esHelper.getNumberOfEssays(topicId);
                    int checked = esHelper.getNumberOfEssays(topicId, VocabKeys.CHECKED);
                    int marked = esHelper.getNumberOfEssays(topicId, VocabKeys.MARKED);
                    if (checked == total) {
                         tpHelper.updateTopic(topicId, TopicKeys.COMPLETED);
                    } else if (checked > 0 || marked > 0) {
                        tpHelper.updateTopic(topicId, TopicKeys.INCOMPLETE);
                    } else {
                        tpHelper.updateTopic(topicId, VocabKeys.UNCHECKED);
                    }
                }
                cursor.requery();
            }
        });
    }
}
