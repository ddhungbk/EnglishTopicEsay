package hungdbk.english.TopicsEssays.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.R;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

public class VocabAdapter extends CursorAdapter {
    private Activity activity;
    private VocabHelper vbHelper;

    public VocabAdapter(Context context, Cursor c, VocabHelper vbHelper) {
        super(context, c);
        this.activity = (Activity) context;
        this.vbHelper = vbHelper;
    }

    public void bindView(View row, Context context, Cursor cursor) {
        ((VocabHolder) row.getTag()).displayRow(cursor, this.vbHelper);
    }

    public View newView(Context context, Cursor c, ViewGroup parent) {
        View row = this.activity.getLayoutInflater().inflate(R.layout.row_vocab, parent, false);
        row.setTag(new VocabHolder(row));
        return row;
    }
}
class VocabHolder {
    private TextView tvInfo;
    private TextView tvStatus;
    private TextView tvWord;

    public VocabHolder(View row) {
        this.tvWord = (TextView) row.findViewById(R.id.tvRowVocabWord);
        this.tvInfo = (TextView) row.findViewById(R.id.tvRowVocabInfo);
        this.tvStatus = (TextView) row.findViewById(R.id.tvRowVocabStatus);
    }

    void displayRow(final Cursor cursor, final VocabHelper vbHelper) {
        this.tvWord.setText("" + vbHelper.getVocabWord(cursor));
        Date date = new Date();
        date.setTime(Long.parseLong(vbHelper.getVocabTime(cursor)));
        this.tvInfo.setText("" + new SimpleDateFormat("MMM d, yyyy hh:mm:ss a").format(date));
        String status = vbHelper.getVocabStatus(cursor);
        if (status.equals(VocabKeys.CHECKED)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_checked);
        } else if (status.equals(VocabKeys.MARKED)) {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_marked);
        } else {
            this.tvStatus.setBackgroundResource(R.drawable.ic_button_unchecked);
        }
        this.tvStatus.setTag(R.id.tvRowVocabStatus, Integer.valueOf(cursor.getPosition()));
        this.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPosition(((Integer)v.getTag(R.id.tvRowVocabStatus)).intValue())) {
                    String status = vbHelper.getVocabStatus(cursor);
                    if (status.equalsIgnoreCase(VocabKeys.CHECKED)) {
                        status = VocabKeys.MARKED;
                    } else if (status.equalsIgnoreCase(VocabKeys.MARKED)) {
                        status = VocabKeys.UNCHECKED;
                    } else {
                        status = VocabKeys.CHECKED;
                    }
                    vbHelper.updateVocabStatus(vbHelper.getVocabId(cursor), status);
                }
                cursor.requery();
            }
        });
    }
}
