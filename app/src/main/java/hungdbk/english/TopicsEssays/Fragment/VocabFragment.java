package hungdbk.english.TopicsEssays.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Adapter.VocabAdapter;
import hungdbk.english.TopicsEssays.EssayDetailActivity;
import hungdbk.english.TopicsEssays.Function.EssayHelper;
import hungdbk.english.TopicsEssays.Function.VocabHelper;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.TopicDetailActivity;
import hungdbk.english.TopicsEssays.R;

public class VocabFragment extends Fragment implements OnQueryTextListener, OnCloseListener {
    private static ImageView ivEmpty;
    private static ListView listVocab;
    private static SearchView searchView;
    private static TextView tvEmpty;
    private VocabAdapter adapter;
    private Cursor cursor;
    private VocabHelper vbHelper;

    private String status = VocabKeys.UNCHECKED;

    private OnItemClickListener listItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VocabFragment.this.openDetailActivity((int) id);
        }
    };

    public VocabFragment() {
    }

    public static VocabFragment newInstance(String status) {
        VocabFragment fragment = new VocabFragment();
        fragment.setStatus(status);
        return fragment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void loadVocabList() {
        String sort_order = "_order ASC";
        switch (status) {
            case VocabKeys.UNCHECKED:
                this.cursor = vbHelper.getVocabsByStatus(VocabKeys.UNCHECKED, sort_order);
                break;
            case VocabKeys.MARKED:
                this.cursor = vbHelper.getVocabsByStatus(VocabKeys.MARKED, sort_order);
                break;
            case VocabKeys.CHECKED:
                this.cursor = vbHelper.getVocabsByStatus(VocabKeys.CHECKED, sort_order);
                break;
            case VocabKeys.ALL_VOCAB:
                this.cursor = vbHelper.getAllVocabs(sort_order);
                break;
        }

        if (this.cursor != null) {
            this.adapter = new VocabAdapter(getActivity(), this.cursor, this.vbHelper);
            listVocab.setAdapter(this.adapter);
        }
        showEmpty(this.adapter, "Empty words list!");
    }

    private void openDetailActivity(int vocabId) {
        Intent intent = new Intent(getActivity(), EssayDetailActivity.class);
        intent.putExtra(TopicDetailActivity.VOCAB_ID, vocabId);
        startActivity(intent);
    }

    private void showLog(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void showEmpty(VocabAdapter adapter, String text) {
        if (adapter == null || adapter.getCount() <= 0) {
            tvEmpty.setText(text);
            ivEmpty.setVisibility(View.VISIBLE);
            return;
        }
        tvEmpty.setText("");
        ivEmpty.setVisibility(View.GONE);
    }

    public boolean onQueryTextSubmit(String query) {
        displayResults("" + query + "*");
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            this.cursor.requery();
            listVocab.setAdapter(this.adapter);
            listVocab.setOnItemClickListener(this.listItemClick);
            showEmpty(this.adapter, "Empty words list!");
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
        VocabAdapter tempAdapter = null;
        if (tempModel != null) {
            tempAdapter = new VocabAdapter(getActivity(), tempModel, this.vbHelper);
            listVocab.setAdapter(tempAdapter);
            listVocab.setOnItemClickListener(listItemClick);
        }
        showEmpty(tempAdapter, "No words match your search!");
    }

    public void nextRandomVocab() {
        String sort_order = "_order ASC";
        switch (status) {
            case VocabKeys.UNCHECKED:
                this.cursor = vbHelper.getRandomVocabsByStatus(10, VocabKeys.UNCHECKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case VocabKeys.MARKED:
                this.cursor = vbHelper.getRandomVocabsByStatus(10, VocabKeys.MARKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case VocabKeys.CHECKED:
                this.cursor = vbHelper.getRandomVocabsByStatus(10, VocabKeys.CHECKED, sort_order);
                this.adapter.changeCursor(this.cursor);
                break;
            case VocabKeys.ALL_VOCAB:
                this.cursor = vbHelper.getRandomVocabsByStatus(10, VocabKeys.ALL_VOCAB, sort_order);
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
        View rootView = inflater.inflate(R.layout.fragment_vocab, container, false);
        listVocab = (ListView) rootView.findViewById(R.id.listVocab);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        ivEmpty = (ImageView) rootView.findViewById(R.id.ivEmpty);
        vbHelper = new VocabHelper(this.getActivity());
        try {
            loadVocabList();
        } catch (Exception e) {
        }
        listVocab.setOnItemClickListener(this.listItemClick);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.fragment_vocab, menu);
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
        showEmpty(this.adapter, "Empty words list!");
    }
}
