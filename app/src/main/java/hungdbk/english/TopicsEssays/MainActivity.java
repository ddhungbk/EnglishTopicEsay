package hungdbk.english.TopicsEssays;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Function.DatabaseHelper;
import hungdbk.english.TopicsEssays.Model.DrawerID;
import hungdbk.english.TopicsEssays.Fragment.EssayPagerFragment;
import hungdbk.english.TopicsEssays.Fragment.TopicFragment;
import hungdbk.english.TopicsEssays.Fragment.VocabPagerFragment;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private TopicHelper tpHelper;
    private EssayHelper esHelper;
    private VocabHelper vbHelper;
    private DrawerID drawerId;
    private boolean newSelected;

    private TopicFragment topicFragment;
    private VocabPagerFragment vocabFragment;
    private EssayPagerFragment essayFragment;

    private DrawerLayout drawer;
    private NavigationView navView;

    public MainActivity() {
    }

    private void showLog(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }

    private void checkToCopyDatabase() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLog("Loading data...");
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                dbHelper.openDatabase();
                showLog("Completed!");
            }
        });
    }

    private void switchFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (drawerId) {
            case TOPICS_LIST:
            case COMPLETED:
            case INCOMPLETE:
            case UNCHECKED:
                topicFragment = new TopicFragment();
                topicFragment.setDrawerId(drawerId);
                transaction.replace(R.id.content_frame_main, topicFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case ESSAYS_LIST:
                essayFragment = new EssayPagerFragment();
                transaction.replace(R.id.content_frame_main, essayFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case VOCABS_LIST:
                vocabFragment = new VocabPagerFragment();
                transaction.replace(R.id.content_frame_main, vocabFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
        }
    }

    private void checkItemSelected(int id) {
        if (id == DrawerID.TOPICS_LIST.getValue()) {
            if (drawerId != DrawerID.TOPICS_LIST) {
                drawerId = DrawerID.TOPICS_LIST;
                newSelected = true;
                getSupportActionBar().setTitle("English Topics & Essays");
            }
        } else if (id == DrawerID.COMPLETED.getValue()) {
            if (drawerId != DrawerID.COMPLETED) {
                drawerId = DrawerID.COMPLETED;
                newSelected = true;
                getSupportActionBar().setTitle("Completed Topics");
            }
        } else if (id == DrawerID.INCOMPLETE.getValue()) {
            if (drawerId != DrawerID.INCOMPLETE) {
                drawerId = DrawerID.INCOMPLETE;
                this.newSelected = true;
                getSupportActionBar().setTitle("Incomplete Topics");
            }


        } else if (id == DrawerID.UNCHECKED.getValue()) {
            if (drawerId != DrawerID.UNCHECKED) {
                drawerId = DrawerID.UNCHECKED;
                newSelected = true;
                getSupportActionBar().setTitle("Unchecked Topics");
            }
        } else if (id == DrawerID.ESSAYS_LIST.getValue()) {
            if (drawerId != DrawerID.ESSAYS_LIST) {
                drawerId = DrawerID.ESSAYS_LIST;
                newSelected = true;
                getSupportActionBar().setTitle("Essays List");
            }
        } else if (id == DrawerID.VOCABS_LIST.getValue()) {
            if (drawerId != DrawerID.VOCABS_LIST) {
                drawerId = DrawerID.VOCABS_LIST;
                newSelected = true;
                getSupportActionBar().setTitle("Vocabularies");
            }
        } else if (id == DrawerID.OPTIONS.getValue()) {
            showLog("Options");
        } else if (id == DrawerID.SETTINGS.getValue()) {
            showLog("Settings");
        }
    }

    private void setNavItemCount(@IdRes int itemId, String count) {
        ((TextView) navView.getMenu().findItem(itemId).getActionView()).setText(count);
    }

    private void updateProgress() {
        setNavItemCount(R.id.nav_topics_list, "" + tpHelper.getNumberOfTopics());
        setNavItemCount(R.id.nav_completed, "" + tpHelper.getNumberOfTopics(TopicKeys.COMPLETED));
        setNavItemCount(R.id.nav_incomplete, "" + tpHelper.getNumberOfTopics(TopicKeys.INCOMPLETE));
        setNavItemCount(R.id.nav_unchecked, "" + tpHelper.getNumberOfTopics(VocabKeys.UNCHECKED));
        setNavItemCount(R.id.nav_essays_list, "" + esHelper.getNumberOfEssays(VocabKeys.CHECKED) + "/" + esHelper.getNumberOfEssays());
        setNavItemCount(R.id.nav_vocabs_list, "" + vbHelper.getNumberOfVocabs(VocabKeys.CHECKED) + "/" + vbHelper.getNumberOfVocabs());
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                newSelected = false;
                TopicFragment.displayActionButton(false);
                updateProgress();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                TopicFragment.displayActionButton(true);
                if (newSelected) {
                    switchFragments();
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        checkToCopyDatabase();
        tpHelper = new TopicHelper(this);
        esHelper = new EssayHelper(this);
        vbHelper = new VocabHelper(this);

        drawerId = DrawerID.TOPICS_LIST;
        newSelected = false;
        if (savedInstanceState == null) {
            switchFragments();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawerId != DrawerID.TOPICS_LIST) {
            navView.setCheckedItem(DrawerID.TOPICS_LIST.getValue());
            getSupportActionBar().setTitle("English Topics & Essays");
            drawerId = DrawerID.TOPICS_LIST;
            switchFragments();
        } else {
            new Builder(this).setTitle("Exit?").setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Process.killProcess(Process.myPid());
                        }
                    }).create().show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawer != null) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("drawerId", drawerId);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        drawerId = (DrawerID) savedInstanceState.getSerializable("drawerId");
        switchFragments();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        checkItemSelected(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
