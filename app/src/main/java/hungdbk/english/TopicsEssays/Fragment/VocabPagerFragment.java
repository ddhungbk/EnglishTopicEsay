package hungdbk.english.TopicsEssays.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hungdbk.english.TopicsEssays.Adapter.VocabPagerAdapter;
import hungdbk.english.TopicsEssays.R;

public class VocabPagerFragment extends Fragment {
    public static final int NUM_ITEM = 4;
    public static final int TAB_ALL_VOCAB_ID = 3;
    public static final String TAB_ALL_VOCAB_NAME = "ALL";
    public static final int TAB_CHECKED_ID = 2;
    public static final String TAB_CHECKED_NAME = "CHECKED";
    public static final int TAB_MARKED_ID = 1;
    public static final String TAB_MARKED_NAME = "MARKED";
    public static final int TAB_UNCHECKED_ID = 0;
    public static final String TAB_UNCHECKED_NAME = "UNCHECKED";

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager_vocabs, container, false);
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(TAB_UNCHECKED_NAME));
        tabLayout.addTab(tabLayout.newTab().setText(TAB_MARKED_NAME));
        tabLayout.addTab(tabLayout.newTab().setText(TAB_CHECKED_NAME));
        tabLayout.addTab(tabLayout.newTab().setText(TAB_ALL_VOCAB_NAME));
        tabLayout.setTabGravity(TAB_UNCHECKED_ID);
        final ViewPager viewPager = (ViewPager) root.findViewById(R.id.pager);
        viewPager.setAdapter(new VocabPagerAdapter(getActivity(), NUM_ITEM, getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
        return root;
    }
}
