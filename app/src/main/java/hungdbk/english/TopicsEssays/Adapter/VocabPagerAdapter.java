package hungdbk.english.TopicsEssays.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import hungdbk.english.TopicsEssays.DbHandlerActivity;
import hungdbk.english.TopicsEssays.Fragment.VocabFragment;
import hungdbk.english.TopicsEssays.Fragment.VocabPagerFragment;
import hungdbk.english.TopicsEssays.Model.VocabKeys;

public class VocabPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    int tabNum;

    public VocabPagerAdapter(Context context, int tabNum, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = null;
        this.context = context;
        this.tabNum = tabNum;
    }

    public int getCount() {
        return this.tabNum;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case VocabPagerFragment.TAB_UNCHECKED_ID /*0*/:
                return VocabFragment.newInstance(VocabKeys.UNCHECKED);
            case DbHandlerActivity.SAVE_DB_TO_SDCARD /*1*/:
                return VocabFragment.newInstance(VocabKeys.MARKED);
            case DbHandlerActivity.LOAD_DB_FROM_SDCARD /*2*/:
                return VocabFragment.newInstance(VocabKeys.CHECKED);
            case DbHandlerActivity.RESTORE_DB_TO_DEFAULT /*3*/:
                return VocabFragment.newInstance(VocabKeys.ALL_VOCAB);
            default:
                return null;
        }
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case VocabPagerFragment.TAB_UNCHECKED_ID /*0*/:
                return VocabPagerFragment.TAB_UNCHECKED_NAME;
            case DbHandlerActivity.SAVE_DB_TO_SDCARD /*1*/:
                return VocabPagerFragment.TAB_MARKED_NAME;
            case DbHandlerActivity.LOAD_DB_FROM_SDCARD /*2*/:
                return VocabPagerFragment.TAB_CHECKED_NAME;
            case DbHandlerActivity.RESTORE_DB_TO_DEFAULT /*3*/:
                return VocabPagerFragment.TAB_ALL_VOCAB_NAME;
            default:
                return null;
        }
    }
}
