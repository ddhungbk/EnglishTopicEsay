package hungdbk.english.TopicsEssays.Model;

import android.support.v4.app.NotificationCompat.WearableExtender;

import hungdbk.english.TopicsEssays.DbHandlerActivity;
import hungdbk.english.TopicsEssays.Fragment.VocabPagerFragment;

public enum SortID {
    NAME_ASC(0),
    NAME_DESC(1),
    ORDER_ASC(2),
    ORDER_DESC(3),
    TIME_ASC(4),
    TIME_DESC(5);
    
    private final int value;

    private SortID(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getType() {
        switch (this.value) {
            case VocabPagerFragment.TAB_UNCHECKED_ID /*0*/:
                return "title ASC";
            case DbHandlerActivity.SAVE_DB_TO_SDCARD /*1*/:
                return "title DESC";
            case DbHandlerActivity.LOAD_DB_FROM_SDCARD /*2*/:
                return "_order ASC";
            case DbHandlerActivity.RESTORE_DB_TO_DEFAULT /*3*/:
                return "_order DESC";
            case DbHandlerActivity.DELETE_DB /*4*/:
                return "time ASC";
            case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                return "time DESC";
            default:
                return "_order ASC";
        }
    }
}
