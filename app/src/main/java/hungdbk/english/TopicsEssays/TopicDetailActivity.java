package hungdbk.english.TopicsEssays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Calendar;
import java.util.Locale;

import hungdbk.english.TopicsEssays.Fragment.TopicFragment;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.Model.EssayKeys;
import hungdbk.english.TopicsEssays.Adapter.EssayAdapter;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.Model.Vocab;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

public class TopicDetailActivity extends AppCompatActivity {
    public static final String ESSAY_ID = "hungdbk.english.TopicEssay.ESSAY_ID";
    public static final String VOCAB_ID = "hungdbk.english.TopicEssay.VOCAB_ID";

    private TopicHelper tpHelper;
    private EssayHelper esHelper;
    private VocabHelper vbHelper;
    private int topicId;
    private Cursor model;
    private EssayAdapter adapter;
    private String mWord;

    private int order;
    private String title;
    private String detail;
    private String status;

    private ScrollView svTopicDetail;
    private ImageView imgTopic;
    private TextView tvTitle;
    private TextView tvDetail;
    private TextView tvSeparate;
    private ListView listEssay;
    private TextView tvEmpty;
    private ImageView ivEmpty;


    public void getWidgets() {
        this.svTopicDetail = (ScrollView) findViewById(R.id.svTopicDetail);
        this.imgTopic = (ImageView) findViewById(R.id.imgTopic);
        this.tvTitle = (TextView) findViewById(R.id.tvTitle);
        this.tvDetail = (TextView) findViewById(R.id.tvDetail);
        this.tvSeparate = (TextView) findViewById(R.id.tvSeparate);
        this.listEssay = (ListView) findViewById(R.id.listEssay);
        this.tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        this.ivEmpty = (ImageView) findViewById(R.id.ivEmpty);
    }

    private void showLog(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }

    private void setClickableText(TextView textView, String text) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(text, BufferType.SPANNABLE);
        Spannable spans = (Spannable) textView.getText();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != -1; end = iterator.next()) {
            String possibleWord = text.substring(start, end);
            if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                spans.setSpan(getClickableSpan(spans, possibleWord, start, end), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = end;
        }
    }

    private ClickableSpan getClickableSpan(final Spannable spans, final String word, final int start, final int end) {
        return new ClickableSpan() {
            @Override
            public void onClick(View view) {
                mWord = word;
                vbHelper.insertVocab(vbHelper.getMaxVocabId() + 1, new Vocab(vbHelper.getMaxVocabId() + 1,
                        word, "" + Calendar.getInstance().getTimeInMillis(), ""), VocabKeys.UNCHECKED, topicId);
                // un-highlight
                //spans.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spans.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //tvDetail.setText(spans, TextView.BufferType.SPANNABLE);
                // highlight
                spans.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvDetail.setText(spans, TextView.BufferType.SPANNABLE);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.GRAY);
                ds.setUnderlineText(false);
            }
        };
    }

    private OnItemClickListener listItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int essayId = (int) id;
            Intent intent = new Intent(TopicDetailActivity.this, EssayDetailActivity.class);
            intent.putExtra(TopicFragment.TOPIC_ID, topicId);
            intent.putExtra(TopicDetailActivity.ESSAY_ID, essayId);
            startActivity(intent);
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            LayoutParams params = listView.getLayoutParams();
            params.height = (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + totalHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
            listView.setFocusable(false);
        }
    }

    public void updateUI(int order, String title, String detail) {
        getSupportActionBar().setTitle("English Topic #" + order);
        imgTopic.setBackgroundResource(R.drawable.pic_001);
        tvTitle.setBackgroundColor(Color.parseColor("#FFFFCC"));
        tvDetail.setBackgroundColor(Color.parseColor("#FFFFCC"));
        tvSeparate.setBackgroundColor(Color.parseColor("#9400D3"));
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);
        setClickableText(tvTitle, title);
        setClickableText(tvDetail, detail);
    }

    private void getCurrentTopic() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progress;

            protected void onPreExecute() {
                super.onPreExecute();
                ivEmpty.setVisibility(View.GONE);
                progress = new ProgressDialog(TopicDetailActivity.this);
                progress.setMessage("Loading...");
                progress.requestWindowFeature(1);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
            }

            protected Void doInBackground(Void... params) {
                Cursor cursor = tpHelper.getTopicById(topicId);
                if (cursor != null && cursor.moveToFirst()) {
                    order = tpHelper.getTopicOrder(cursor);
                    title = tpHelper.getTopicTitle(cursor);
                    detail = tpHelper.getTopicDetail(cursor);
                    status = tpHelper.getTopicStatus(cursor);
                }
                cursor.close();
                if (model != null) {
                    model.close();
                }
                model = esHelper.getEssaysOfTopic(topicId, "_order ASC");
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                updateUI(order, title, detail);
                if (model != null) {
                    adapter = new EssayAdapter(TopicDetailActivity.this, model, true);
                    listEssay.setAdapter(adapter);
                    TopicDetailActivity.this.setListViewHeightBasedOnChildren(listEssay);
                    svTopicDetail.smoothScrollTo(0, 0);
                }
                if (adapter == null || adapter.getCount() <= 0) {
                    tvEmpty.setText("\tEmpty essay list ...");
                    ivEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setText("");
                    ivEmpty.setVisibility(View.GONE);
                }
                this.progress.dismiss();
            }
        }.execute();
    }

    private void refreshEssayList() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progress;

            protected void onPreExecute() {
                super.onPreExecute();
                ivEmpty.setVisibility(View.GONE);
                progress = new ProgressDialog(TopicDetailActivity.this);
                progress.setMessage("Loading...");
                progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
            }

            protected Void doInBackground(Void... params) {
                if (model != null) {
                    model.close();
                }
                model = esHelper.getEssaysOfTopic(topicId, "_order ASC");
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (model != null) {
                    adapter = new EssayAdapter(TopicDetailActivity.this, model, true);
                    adapter.notifyDataSetChanged();
                    listEssay.setAdapter(adapter);
                    TopicDetailActivity.this.setListViewHeightBasedOnChildren(listEssay);
                    svTopicDetail.smoothScrollTo(0, 0);
                }
                if (adapter == null || adapter.getCount() <= 0) {
                    tvEmpty.setText("\tEmpty essay list ...");
                    ivEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setText("");
                    ivEmpty.setVisibility(View.GONE);
                }
                this.progress.dismiss();
            }
        }.execute();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWidgets();
        tpHelper = new TopicHelper(this);
        esHelper = new EssayHelper(this);
        vbHelper = new VocabHelper(this);
        topicId = getIntent().getIntExtra(TopicFragment.TOPIC_ID, 0);
        if (topicId > 0) {
            getCurrentTopic();
        }
        listEssay.setOnItemClickListener(listItemClick);
        tvTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showLog(mWord);
            }
        });
        tvDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog(mWord);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EssayKeys.TOPIC_ID, topicId);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        topicId = savedInstanceState.getInt(EssayKeys.TOPIC_ID);
        getCurrentTopic();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (model != null) {
            model.requery();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
