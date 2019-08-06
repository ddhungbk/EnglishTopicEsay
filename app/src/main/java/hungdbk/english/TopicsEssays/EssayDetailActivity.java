package hungdbk.english.TopicsEssays;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Fragment.TopicFragment;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.Vocab;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.util.Calendar;
import java.util.Locale;

import com.naoak.WebviewMarker.TextSelection.TextSelectionSupport;

public class EssayDetailActivity extends AppCompatActivity {

    private TopicHelper tpHelper;
    private EssayHelper esHelper;
    private VocabHelper vbHelper;

    private int topicId = 0;
    private int essayId = 0;
    String mWord = "";

    private int tpOrder;
    private String tpStatus;
    private String detail;
    private int esOrder;
    private String title;
    private String content;
    private String esStatus = VocabKeys.UNCHECKED;
    private String note;

    private JavaScriptInterface jsInterface;

    private MenuItem itSetting;
    private MenuItem itStatus;
    private WebView webView;
    private TextSelectionSupport mTextSelectionSupport;
    private FloatingActionButton fabAction;

    public class JavaScriptInterface {
        @JavascriptInterface
        public void next() {
            nextEssay();
        }

        @JavascriptInterface
        public void back() {
            backEssay();
        }
    }

    public EssayDetailActivity() {
    }

    public void getWidgets() {
        this.webView = (WebView) findViewById(R.id.wvContent);
        fabAction = (FloatingActionButton) findViewById(R.id.fabAction);

        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setDisplayZoomControls(false);
        this.webView.getSettings().setSupportZoom(true);
        this.webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE.CLOSE);
        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.webView.getSettings().setJavaScriptEnabled(true);

        this.jsInterface = new JavaScriptInterface();
        this.webView.addJavascriptInterface(this.jsInterface, "JsInterface");
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
            public void onClick(View widget) {
                mWord = word;
                vbHelper.insertVocab(vbHelper.getMaxVocabId() + 1, new Vocab(vbHelper.getMaxVocabId() + 1,
                        word, "" + Calendar.getInstance().getTimeInMillis(), ""), VocabKeys.UNCHECKED, topicId);
                // un-highlight
                //spans.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spans.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //tvDetail.setText(spans, TextView.BufferType.SPANNABLE);
                // highlight
                spans.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.BLACK);
                ds.setUnderlineText(false);
            }
        };
    }

    private void showLog(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }

    public void getCurrentTopic() {
        Cursor cursor = this.tpHelper.getTopicById(this.topicId);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.tpOrder = this.tpHelper.getTopicOrder(cursor);
            this.title = this.tpHelper.getTopicTitle(cursor);
            this.detail = this.tpHelper.getTopicDetail(cursor);
            this.tpStatus = this.tpHelper.getTopicStatus(cursor);
        }
        cursor.close();
    }

    private void getCurrentEssay() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progress;

            protected void onPreExecute() {
                super.onPreExecute();
                this.progress = new ProgressDialog(EssayDetailActivity.this);
                this.progress.setMessage("Loading...");
                this.progress.requestWindowFeature(1);
                this.progress.setCanceledOnTouchOutside(false);
                this.progress.show();
            }

            protected Void doInBackground(Void... params) {
                Cursor cursor = esHelper.getEssayById(essayId);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    esOrder = esHelper.getEssayOrder(cursor);
                    content = esHelper.getEssayContent(cursor);
                    note = esHelper.getEssayNote(cursor);
                    esStatus = esHelper.getEssayStatus(cursor);
                    try {
                        InputStreamReader ir = new InputStreamReader(EssayDetailActivity.this.getAssets().open("essay.html"));
                        BufferedReader br = new BufferedReader(ir);
                        StringBuilder htmls = new StringBuilder("");
                        String str = "";
                        while (true) {
                            str = br.readLine();
                            if (str == null) {
                                break;
                            }
                            htmls.append(str + "\n");
                        }
                        ir.close();
                        String html = htmls.toString().trim();
                        content = html.substring(0, html.indexOf("</essay_content>")) + content + html.substring(html.indexOf("</essay_content>") + 16);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                cursor.close();
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                EssayDetailActivity.this.getSupportActionBar().setTitle("English Essay #" + esOrder);
                if (itStatus != null) {
                    updateStatusIcon();
                }

//                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
                try {
                    webView.loadUrl("file:///" + saveContentAsHtml(content));
                } catch (IOException e) {
                }
                this.progress.dismiss();
            }
        }.execute();
    }

    private void writeScripts(String srcFile, String desFile) {
        try {
            InputStreamReader inputStream = new InputStreamReader(EssayDetailActivity.this.getAssets().open(srcFile));
            BufferedReader br = new BufferedReader(inputStream);
            StringBuilder contents = new StringBuilder("");
            String str = "";
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                contents.append(str + "\n");
            }
            inputStream.close();

            String content = contents.toString().trim();
            File file = new File(EssayDetailActivity.this.getFilesDir(), desFile);
            FileOutputStream outputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private String saveContentAsHtml(String content) throws IOException {
        File file = new File(EssayDetailActivity.this.getFilesDir(), "essay_tmp.html");
        FileOutputStream outputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
        outputStream.write(content.getBytes());
        outputStream.close();
        return file.getAbsolutePath();
    }

    private void callGoogleTranslateApps(String word, String fromLang, String toLang) {
        Intent i = new Intent();

        i.setAction(Intent.ACTION_VIEW);
        i.putExtra("key_text_input", word);
        i.putExtra("key_text_output", "");
        i.putExtra("key_language_from", fromLang);
        i.putExtra("key_language_to", toLang);
        i.putExtra("key_suggest_translation", "");
        i.putExtra("key_from_floating_window", false);

        i.setComponent(new ComponentName("com.google.android.apps.translate", "com.google.android.apps.translate.TranslateActivity"));
        startActivity(i);
    }

    private void searchText(String text) {
        webView.findAll(text);
        try {
            for (Method m : WebView.class.getDeclaredMethods()) {
                if (m.getName().equals("setFindIsUp")) {
                    m.setAccessible(true);
                    m.invoke((webView), true);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void unHighlight(String text) {
        try {
            for (Method m : WebView.class.getDeclaredMethods()) {
                if (m.getName().equals("setFindIsUp")) {
                    m.setAccessible(true);
                    m.invoke((webView), false);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void nextEssay() {
        Cursor cursor = esHelper.getArrayEssayOrder(this.topicId);
        if (cursor != null && cursor.getCount() > 0) {
            if (this.esOrder == esHelper.getMaxEssayOrder(this.topicId)) {
                cursor.moveToFirst();
                this.essayId = cursor.getInt(0);
                getCurrentEssay();
            } else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (cursor.getInt(1) > this.esOrder) {
                        this.essayId = cursor.getInt(0);
                        getCurrentEssay();
                        break;
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
    }

    private void backEssay() {
        Cursor cursor = esHelper.getArrayEssayOrder(this.topicId);
        if (cursor != null && cursor.getCount() > 0) {
            if (this.esOrder == 1) {
                cursor.moveToLast();
                this.essayId = cursor.getInt(0);
                getCurrentEssay();
            } else {
                cursor.moveToLast();
                while (!cursor.isBeforeFirst()) {
                    if (cursor.getInt(1) < this.esOrder) {
                        this.essayId = cursor.getInt(0);
                        getCurrentEssay();
                        break;
                    }
                    cursor.moveToPrevious();
                }
            }
        }
        cursor.close();
    }

    private void closeActivity() {
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_essay_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EssayDetailActivity.this.closeActivity();
            }
        });
        getWidgets();
        tpHelper = new TopicHelper(this);
        esHelper = new EssayHelper(this);
        vbHelper = new VocabHelper(this);
        this.topicId = getIntent().getIntExtra(TopicFragment.TOPIC_ID, 0);
        this.essayId = getIntent().getIntExtra(TopicDetailActivity.ESSAY_ID, 0);
        if (this.topicId > 0) {
            getCurrentTopic();
        }
        if (this.essayId > 0) {
            getCurrentEssay();
        }

        mTextSelectionSupport = TextSelectionSupport.support(this, webView);
        mTextSelectionSupport.setSelectionListener(new TextSelectionSupport.SelectionListener() {
            @Override
            public void startSelection() {
            }

            @Override
            public void selectionChanged(String text) {
                mWord = text;
                showLog(text);
                callGoogleTranslateApps(text, "eng", "vie");
            }

            @Override
            public void endSelection() {
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                mTextSelectionSupport.onScaleChanged(oldScale, newScale);
            }
        });
        writeScripts("scripts/android.selection.js", "android.selection.js");
        writeScripts("scripts/jpntext.js", "jpntext.js");
        writeScripts("scripts/jquery-1.8.3.js", "jquery-1.8.3.js");
        writeScripts("scripts/rangy-core.js", "rangy-core.js");
        writeScripts("scripts/rangy-serializer.js", "rangy-serializer.js");
        writeScripts("css/sample.css", "sample.css");

        fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText(mWord);
            }
        });

    }

    public void updateStatusIcon() {
        if (this.esStatus.equalsIgnoreCase(VocabKeys.CHECKED)) {
            this.itStatus.setIcon(R.drawable.ic_menu_checked);
        } else if (this.esStatus.equalsIgnoreCase(VocabKeys.MARKED)) {
            this.itStatus.setIcon(R.drawable.ic_menu_marked);
        } else {
            this.itStatus.setIcon(R.drawable.ic_menu_unchecked);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.essay_detail, menu);
        this.itStatus = menu.findItem(R.id.itStatus);
        this.itSetting = menu.findItem(R.id.itSetting);
        updateStatusIcon();
        return true;
    }

    private void updateStatus() {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                if (esStatus.equalsIgnoreCase(VocabKeys.CHECKED)) {
                    esHelper.updateEssayStatus(essayId, VocabKeys.MARKED);
                } else if (esStatus.equalsIgnoreCase(VocabKeys.MARKED)) {
                    esHelper.updateEssayStatus(essayId, VocabKeys.UNCHECKED);
                } else {
                    esHelper.updateEssayStatus(essayId, VocabKeys.CHECKED);
                }
                Cursor cursor = esHelper.getEssayById(essayId);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    esStatus = esHelper.getEssayStatus(cursor);
                }
                cursor.close();
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
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                EssayDetailActivity.this.updateStatusIcon();
            }
        }.execute();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.itStatus) {
            updateStatus();
            return true;
        } else if (item.getItemId() != R.id.itSetting) {
            return super.onOptionsItemSelected(item);
        } else {
            return true;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(VocabKeys.ESSAY_ID, this.essayId);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.essayId = savedInstanceState.getInt(VocabKeys.ESSAY_ID);
        getCurrentEssay();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
