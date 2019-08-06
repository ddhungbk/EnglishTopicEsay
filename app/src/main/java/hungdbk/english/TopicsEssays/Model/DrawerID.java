package hungdbk.english.TopicsEssays.Model;

import hungdbk.english.TopicsEssays.R;

public enum DrawerID {
    TOPICS_LIST(R.id.nav_topics_list),
    COMPLETED(R.id.nav_completed),
    INCOMPLETE(R.id.nav_incomplete),
    UNCHECKED(R.id.nav_unchecked),
    ESSAYS_LIST(R.id.nav_essays_list),
    VOCABS_LIST(R.id.nav_vocabs_list),
    OPTIONS(R.id.nav_options),
    SETTINGS(R.id.nav_settings);
    
    private final int value;

    private DrawerID(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public DrawerID getId() {
        switch (this.value) {

            case R.id.nav_topics_list:
                return TOPICS_LIST;
            case R.id.nav_completed:
                return COMPLETED;
            case R.id.nav_incomplete:
                return INCOMPLETE;
            case R.id.nav_unchecked:
                return UNCHECKED;
            case R.id.nav_essays_list:
                return ESSAYS_LIST;
            case R.id.nav_vocabs_list:
                return VOCABS_LIST;
            case R.id.nav_options:
                return OPTIONS;
            case R.id.nav_settings:
                return SETTINGS;
            default:
                return TOPICS_LIST;
        }
    }
}
