package hungdbk.english.TopicsEssays.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Model.DrawerID;
import hungdbk.english.TopicsEssays.Adapter.TopicAdapter;
import hungdbk.english.TopicsEssays.Function.TopicHelper;
import hungdbk.english.TopicsEssays.R;
import hungdbk.english.TopicsEssays.Model.SortID;
import hungdbk.english.TopicsEssays.Model.TopicKeys;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.TopicDetailActivity;

public class TopicFragment extends Fragment implements OnQueryTextListener, OnCloseListener {
    public static final String TOPIC_ID = "hungdbk.english.TopicsEssays.TOPIC_ID";
    private static FloatingActionButton fabAction;
    private static ImageView ivEmpty;
    private static ListView listTopic;
    private static SearchView searchView;
    private static TextView tvEmpty;
    private TopicAdapter adapter;
    private Cursor cursor;
    private TopicHelper tpHelper;
    private DrawerID drawerId = DrawerID.TOPICS_LIST;
    private SortID sortId = SortID.ORDER_ASC;
    private SwipeRefreshLayout swRefresh;

    private OnItemClickListener listItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openDetailActivity((int) id);
        }
    };

    public TopicFragment() {
    }

    public static void displayActionButton(boolean visible) {
        if (visible) {
            fabAction.show();
        } else {
            fabAction.hide();
        }
    }

    public void setDrawerId(DrawerID drawerID) {
        this.drawerId = drawerID;
    }

    private void showLog(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void showEmpty(TopicAdapter adapter, String text) {
        if (adapter == null || adapter.getCount() <= 0) {
            tvEmpty.setText(text);
            ivEmpty.setVisibility(View.VISIBLE);
            return;
        }
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);
    }

    public void loadTopicList(final String sort_order) {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progress;

            protected void onPreExecute() {
                super.onPreExecute();
                TopicFragment.ivEmpty.setVisibility(View.GONE);
                this.progress = new ProgressDialog(TopicFragment.this.getActivity());
                this.progress.setIndeterminate(true);
                this.progress.setMessage("Loading...");
                this.progress.setCanceledOnTouchOutside(false);
                this.progress.show();
            }

            protected Void doInBackground(Void... params) {
                switch (drawerId) {
                    case TOPICS_LIST:
                        cursor = tpHelper.getAllTopics(sort_order);
                        break;
                    case COMPLETED:
                        cursor = tpHelper.getTopicsByStatus(TopicKeys.COMPLETED, sort_order);
                        break;
                    case INCOMPLETE:
                        cursor = tpHelper.getTopicsByStatus(TopicKeys.INCOMPLETE, sort_order);
                        break;
                    case UNCHECKED:
                        cursor = tpHelper.getTopicsByStatus(VocabKeys.UNCHECKED, sort_order);
                        break;
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (cursor != null) {
                    adapter = new TopicAdapter(TopicFragment.this.getActivity(), cursor);
                    listTopic.setAdapter(adapter);
                }
                showEmpty(adapter, "Empty topics list!");
                this.progress.dismiss();
            }
        }.execute();
    }

    private void openDetailActivity(int topicId) {
        Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
        intent.putExtra(TOPIC_ID, topicId);
        startActivity(intent);
    }

    public boolean onQueryTextSubmit(String query) {
        displayResults("" + query + "*");
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            this.cursor.requery();
            listTopic.setAdapter(this.adapter);
            listTopic.setOnItemClickListener(this.listItemClick);
            showEmpty(this.adapter, "Empty topics list!");
        } else {
            displayResults(newText);
        }
        return false;
    }

    private void displayResults(String query) {
        TopicHelper topicHelper = new TopicHelper(this.getActivity());
        if (query == null) {
            query = "@@@@";
        }
        Cursor tempModel = topicHelper.searchTopicByInputText(query);
        TopicAdapter tempAdapter = null;
        if (tempModel != null) {
            tempAdapter = new TopicAdapter(getActivity(), tempModel);
            listTopic.setAdapter(tempAdapter);
            listTopic.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TopicFragment.this.openDetailActivity((int) id);
                }
            });
        }
        showEmpty(tempAdapter, "No topics match your search!");
    }

    public void nextRandomTopic(String sort_order) {
        switch (drawerId) {
            case TOPICS_LIST:
                this.cursor = this.tpHelper.getRandomTopics(5, TopicKeys.ALL_TOPIC, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case COMPLETED:
                this.cursor = this.tpHelper.getRandomTopics(5, TopicKeys.COMPLETED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case INCOMPLETE:
                this.cursor = this.tpHelper.getRandomTopics(5, TopicKeys.INCOMPLETE, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case UNCHECKED:
                this.cursor = this.tpHelper.getRandomTopics(5, VocabKeys.UNCHECKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            default:
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void refreshContent() {
        switch (sortId) {
            case NAME_ASC:
                this.sortId = SortID.NAME_DESC;
                break;
            case NAME_DESC:
                this.sortId = SortID.ORDER_ASC;
                break;
            case ORDER_ASC:
                this.sortId = SortID.ORDER_DESC;
                break;
            case ORDER_DESC:
                this.sortId = SortID.NAME_ASC;
                break;
        }
        loadTopicList(this.sortId.getType());
        this.swRefresh.setRefreshing(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        listTopic = (ListView) rootView.findViewById(R.id.listTopic);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        ivEmpty = (ImageView) rootView.findViewById(R.id.ivEmpty);
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);

        tpHelper=new TopicHelper(this.getActivity());
        this.sortId = SortID.ORDER_ASC;
        try {
            loadTopicList(this.sortId.getType());
        } catch (Exception e) {
        }
        fabAction = (FloatingActionButton) rootView.findViewById(R.id.fabAction);
        fabAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerId == DrawerID.TOPICS_LIST || drawerId == DrawerID.COMPLETED
                        || drawerId == DrawerID.INCOMPLETE || drawerId == DrawerID.UNCHECKED) {
                    nextRandomTopic(sortId.getType());
                }
            }
        });
        fabAction.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TopicFragment.this.sortId = SortID.ORDER_ASC;
                TopicFragment.this.loadTopicList(TopicFragment.this.sortId.getType());
                return true;
            }
        });
        listTopic.setOnItemClickListener(this.listItemClick);
        this.swRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swRefresh);
        this.swRefresh.setColorSchemeResources(R.color.Orange, R.color.Green, R.color.Blue);
        this.swRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.fragment_topic, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public boolean onClose() {
        return false;
    }

    public void onResume() {
        super.onResume();
        if (this.cursor != null) {
            cursor.requery();
        }
    }
}
