package hungdbk.english.TopicsEssays.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Fragment.EssayFragment;
import hungdbk.english.TopicsEssays.Fragment.EssayPagerFragment;
import hungdbk.english.TopicsEssays.Fragment.VocabPagerFragment;
import hungdbk.english.TopicsEssays.Model.EssayKeys;

public class EssayPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    int tabNum;

    public EssayPagerAdapter(Context context, int tabNum, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = null;
        this.context = context;
        this.tabNum = tabNum;
    }

    private void showLog(String text) {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show();
    }

    public int getCount() {
        return this.tabNum;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case EssayPagerFragment.TAB_UNCHECKED_ID:
                showLog("Unchecked");
                return EssayFragment.newInstance(EssayKeys.UNCHECKED);
            case EssayPagerFragment.TAB_MARKED_ID:
                showLog("Marked");
                return EssayFragment.newInstance(EssayKeys.MARKED);
            case EssayPagerFragment.TAB_CHECKED_ID:
                showLog("Checked");
                return EssayFragment.newInstance(EssayKeys.CHECKED);
            case EssayPagerFragment.TAB_ALL_ESSAY_ID:
                showLog("All");
                return EssayFragment.newInstance(EssayKeys.ALL_ESSAY);
            default:
                return null;
        }
    }

    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case EssayPagerFragment.TAB_UNCHECKED_ID:
                return VocabPagerFragment.TAB_UNCHECKED_NAME;
            case EssayPagerFragment.TAB_MARKED_ID:
                return VocabPagerFragment.TAB_MARKED_NAME;
            case EssayPagerFragment.TAB_CHECKED_ID:
                return VocabPagerFragment.TAB_CHECKED_NAME;
            case EssayPagerFragment.TAB_ALL_ESSAY_ID:
                return VocabPagerFragment.TAB_ALL_VOCAB_NAME;
            default:
                return null;
        }
    }
}
