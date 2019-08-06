package hungdbk.english.TopicsEssays.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Function.AppSystem;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

public class TopicAdapter extends CursorAdapter {
    private Activity activity;
    private TopicHelper tpHelper;
    private EssayHelper esHelper;

    public TopicAdapter(Context context, Cursor c) {
        super(context, c);
        this.activity = (Activity) context;
        tpHelper = new TopicHelper(context);
        esHelper = new EssayHelper(context);
    }

    public void bindView(View row, Context context, Cursor cursor) {
        ((TopicHolder) row.getTag()).displayRow(cursor, tpHelper, esHelper);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View row = this.activity.getLayoutInflater().inflate(R.layout.row_topic, parent, false);
        row.setTag(new TopicHolder(row));
        return row;
    }
}

class TopicHolder {
    private TextView tvDetail;
    private TextView tvOrder;
    private TextView tvProgress;
    private TextView tvStatus;
    private TextView tvTitle;

    public TopicHolder(View row) {
        this.tvOrder = (TextView) row.findViewById(R.id.tvOrder);
        this.tvTitle = (TextView) row.findViewById(R.id.tvTitle);
        this.tvProgress = (TextView) row.findViewById(R.id.tvProgress);
        this.tvDetail = (TextView) row.findViewById(R.id.tvDetail);
        this.tvStatus = (TextView) row.findViewById(R.id.tvStatus);
    }

    public void displayRow(Cursor cursor,  TopicHelper tpHelper, EssayHelper esHelper) {
        this.tvOrder.setText("" + tpHelper.getTopicOrder(cursor));
        this.tvOrder.setBackgroundResource(AppSystem.arrIcons[tpHelper.getTopicOrder(cursor)]);
        this.tvTitle.setText(tpHelper.getTopicTitle(cursor));
        int topicId = tpHelper.getTopicId(cursor);
        int total = esHelper.getNumberOfEssays(topicId);
        int checked = esHelper.getNumberOfEssays(topicId, VocabKeys.CHECKED);
        int marked = esHelper.getNumberOfEssays(topicId, VocabKeys.MARKED);
        this.tvProgress.setText("" + checked + "/" + total + " essays" + (marked > 0 ? ", " + marked + " marked " : ""));
        this.tvDetail.setText(tpHelper.getTopicDetail(cursor));
        String status = tpHelper.getTopicStatus(cursor);
        if (status.equals(TopicKeys.COMPLETED)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_checked);
        } else if (status.equals(TopicKeys.INCOMPLETE)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_marked);
        } else {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_unchecked);
        }
    }
}
