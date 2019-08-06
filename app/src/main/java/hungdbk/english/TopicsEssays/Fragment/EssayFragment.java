package hungdbk.english.TopicsEssays.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.EssayDetailActivity;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Model.EssayKeys;
import hungdbk.english.TopicsEssays.TopicDetailActivity;
import hungdbk.english.TopicsEssays.Adapter.EssayAdapter;
import hungdbk.english.TopicsEssays.R;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

public class EssayFragment extends Fragment implements OnQueryTextListener, OnCloseListener {
    private static FloatingActionButton fabAction;
    private static ImageView ivEmpty;
    private static ListView listEssay;
    private static SearchView searchView;
    private static TextView tvEmpty;
    private EssayAdapter adapter;
    private Cursor cursor;
    private EssayHelper esHelper;
    private String status= VocabKeys.UNCHECKED;

    public EssayFragment() {
    }

    public static EssayFragment newInstance(String status) {
        EssayFragment fragment = new EssayFragment();
        fragment.setStatus(status);
        return fragment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void showLog(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void showEmpty(EssayAdapter adapter, String text) {
        if (adapter == null || adapter.getCount() <= 0) {
            tvEmpty.setText(text);
            ivEmpty.setVisibility(View.VISIBLE);
            return;
        }
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);
    }

    private OnItemClickListener listItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openDetailActivity((int) id);
        }
    };

    public static void displayActionButton(boolean visible) {
        if (visible) {
            fabAction.show();
        } else {
            fabAction.hide();
        }
    }

    public void loadEssayListUseThread() {
        new AsyncTask<Void, Void, Void>(){
            protected void onPreExecute() {
                super.onPreExecute();
                ivEmpty.setVisibility(View.GONE);
            }

            protected Void doInBackground(Void... params) {
                String sort_order = "_order ASC";
                switch (status) {
                    case EssayKeys.UNCHECKED:
                        cursor = esHelper.getEssaysByStatus(VocabKeys.UNCHECKED, sort_order);
                        break;
                    case EssayKeys.MARKED:
                        cursor = esHelper.getEssaysByStatus(VocabKeys.MARKED, sort_order);
                        break;
                    case EssayKeys.CHECKED:
                        cursor = esHelper.getEssaysByStatus(VocabKeys.CHECKED, sort_order);
                        break;
                    case EssayKeys.ALL_ESSAY:
                        cursor = esHelper.getAllEssays(sort_order);
                        break;
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (cursor != null) {
                    adapter = new EssayAdapter(EssayFragment.this.getActivity(), cursor, false);
                    listEssay.setAdapter(adapter);
                }
                showEmpty(adapter, "Empty essays list!");
            }
        }.execute();
    }

    public void loadEssayList() {
        String sort_order = "_order ASC";
        switch (status) {
            case EssayKeys.UNCHECKED:
                cursor = esHelper.getEssaysByStatus(VocabKeys.UNCHECKED, sort_order);
                break;
            case EssayKeys.MARKED:
                cursor = esHelper.getEssaysByStatus(VocabKeys.MARKED, sort_order);
                break;
            case EssayKeys.CHECKED:
                cursor = esHelper.getEssaysByStatus(VocabKeys.CHECKED, sort_order);
                break;
            case EssayKeys.ALL_ESSAY:
                cursor = esHelper.getAllEssays(sort_order);
                break;
        }

        if (this.cursor != null) {
            this.adapter = new EssayAdapter(getActivity(), this.cursor, false);
            listEssay.setAdapter(this.adapter);
        }
        showEmpty(this.adapter, "Empty essays list!");
    }

    private void openDetailActivity(int essayId) {
        Intent intent = new Intent(getActivity(), EssayDetailActivity.class);
        intent.putExtra(TopicDetailActivity.ESSAY_ID, essayId);
        startActivity(intent);
    }

    public boolean onQueryTextSubmit(String query) {
        displayResults("" + query + "*");
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            this.cursor.requery();
            listEssay.setAdapter(this.adapter);
            listEssay.setOnItemClickListener(listItemClick);
            showEmpty(this.adapter, "Empty essays list!");
        } else {
            displayResults(newText);
        }
        return false;
    }

    private void displayResults(String query) {
        EssayHelper essayHelper = new EssayHelper(this.getActivity());
        if (query == null) {
            query = "@@@@";
        }
        Cursor tempModel = essayHelper.searchEssayByInputText(query);
        if (tempModel != null) {
            listEssay.setAdapter(new EssayAdapter(getActivity(), tempModel, false));
            listEssay.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openDetailActivity((int) id);
                }
            });
        }
        showEmpty(this.adapter, "No essays match your search!");
    }

    public void nextRandomEssay() {
        String sort_order = "_order ASC";
        switch (status) {
            case EssayKeys.UNCHECKED:
                this.cursor = esHelper.getRandomEssaysByStatus(5, VocabKeys.UNCHECKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                    break;
            case EssayKeys.MARKED:
                this.cursor = esHelper.getRandomEssaysByStatus(5, VocabKeys.MARKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                    break;
            case EssayKeys.CHECKED:
                this.cursor = esHelper.getRandomEssaysByStatus(5, VocabKeys.CHECKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case EssayKeys.ALL_ESSAY:
                this.cursor = esHelper.getRandomEssaysByStatus(5, EssayKeys.ALL_ESSAY, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(true);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_essay, container, false);
        listEssay = (ListView) rootView.findViewById(R.id.listEssay);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        ivEmpty = (ImageView) rootView.findViewById(R.id.ivEmpty);
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);

        esHelper = new EssayHelper(this.getActivity());
        try {
            loadEssayList();
        } catch (Exception e) {
        }
        listEssay.setOnItemClickListener(listItemClick);
        fabAction = (FloatingActionButton) rootView.findViewById(R.id.fabAction);
        fabAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextRandomEssay();
            }
        });
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.fragment_essay, menu);
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
            this.cursor.requery();
        }
        showEmpty(this.adapter, "Empty essays list!");
    }
}
