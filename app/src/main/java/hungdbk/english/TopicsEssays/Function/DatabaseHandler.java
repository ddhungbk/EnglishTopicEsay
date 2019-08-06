package hungdbk.english.TopicsEssays.Function;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.DbHandlerActivity;
import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHandler extends AsyncTask<Integer, Integer, Void> {
    private DatabaseHelper dbHelper;
    private TopicHelper tpHelper;
    private EssayHelper esHelper;
    private Activity mainActivity;

    public DatabaseHandler(Activity mainActivity) {
        this.dbHelper = null;
        this.mainActivity = mainActivity;
        this.dbHelper = new DatabaseHelper(mainActivity);
        tpHelper = new TopicHelper(mainActivity);
        esHelper = new EssayHelper(mainActivity);
    }

    private void saveDatabaseToSDCard() {
        Cursor tpModel = this.tpHelper.getAllTopics(VocabKeys.ORDER);
        this.mainActivity.startManagingCursor(tpModel);
        if (tpModel != null && tpModel.getCount() > 0) {
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_01.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_02.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_03.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_04.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_05.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_06.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_07.txt", "", false);
            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_08.txt", "", false);
            int total = tpModel.getCount();
            int progress = 0;
            tpModel.moveToFirst();
            while (!tpModel.isAfterLast()) {
                int tpOrder = this.tpHelper.getTopicOrder(tpModel);
                String title = this.tpHelper.getTopicTitle(tpModel);
                String detail = this.tpHelper.getTopicDetail(tpModel);
                StringBuilder data = new StringBuilder("");
                data.append("Topic " + tpOrder + ".\t" + title.trim() + " @" + detail.trim() + "\n");
                Cursor esModel = esHelper.getEssaysOfTopic(tpOrder, VocabKeys.ORDER);
                this.mainActivity.startManagingCursor(esModel);
                if (esModel != null) {
                    esModel.moveToFirst();
                    while (!esModel.isAfterLast()) {
                        int esOrder = esHelper.getEssayOrder(esModel);
                        String content = esHelper.getEssayContent(esModel).trim();
                        String notes = esHelper.getEssayNote(esModel).trim();
                        data.append("Essay " + esOrder + ": " + content.trim() + "\n");
                        if (notes == null) {
                            data.append("@Notes: \n");
                        } else {
                            data.append("@Notes: " + notes.trim() + "\n");
                        }
                        esModel.moveToNext();
                    }
                }
                esModel.close();
                if (tpOrder <= 25) {
                    AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_01.txt", data.toString(), true);
                } else {
                    if (((tpOrder <= 50 ? 1 : 0) & (tpOrder > 25 ? 1 : 0)) != 0) {
                        AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_02.txt", data.toString(), true);
                    } else {
                        if (((tpOrder <= 75 ? 1 : 0) & (tpOrder > 50 ? 1 : 0)) != 0) {
                            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_03.txt", data.toString(), true);
                        } else {
                            if (((tpOrder <= 100 ? 1 : 0) & (tpOrder > 75 ? 1 : 0)) != 0) {
                                AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_04.txt", data.toString(), true);
                            } else {
                                if (((tpOrder <= 125 ? 1 : 0) & (tpOrder > 100 ? 1 : 0)) != 0) {
                                    AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_05.txt", data.toString(), true);
                                } else {
                                    if (((tpOrder <= 150 ? 1 : 0) & (tpOrder > 125 ? 1 : 0)) != 0) {
                                        AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_06.txt", data.toString(), true);
                                    } else {
                                        if (((tpOrder <= 175 ? 1 : 0) & (tpOrder > 150 ? 1 : 0)) != 0) {
                                            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_07.txt", data.toString(), true);
                                        } else {
                                            AppSystem.writeFileToSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, "data/data_all_08.txt", data.toString(), true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tpModel.moveToNext();
                Integer[] numArr = new Integer[1];
                progress++;
                numArr[0] = Integer.valueOf((int) ((((double) progress) * 100.0d) / ((double) total)));
                publishProgress(numArr);
            }
        }
        tpModel.close();
    }

    public void loadDatabaseFromSDCard() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppSystem.APP_DIRECTORY);
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            ArrayList<File> arrFile = new ArrayList();
            int i = 0;
            while (i < list.length) {
                if (list[i].getName().startsWith("data_all_") && list[i].canRead()) {
                    arrFile.add(list[i]);
                }
                i++;
            }
            if (arrFile.size() > 0) {
                this.dbHelper.deleteDatabase();
                int total = arrFile.size();
                int progress = 0;
                for (i = 0; i < arrFile.size(); i++) {
                    AppSystem.createDatabase(this.mainActivity, dbHelper, AppSystem.buildDatabase(AppSystem.readFileFromSDCard(this.mainActivity, AppSystem.APP_DIRECTORY, ((File) arrFile.get(i)).getName())));
                    Integer[] numArr = new Integer[1];
                    progress++;
                    numArr[0] = Integer.valueOf((int) ((((double) progress) * 100.0d) / ((double) total)));
                    publishProgress(numArr);
                }
            }
        }
    }

    public void restoreDatabaseToDefault() {
        this.dbHelper.deleteDatabase();
        int total = AppSystem.baseDBFileName.length;
        int progress = 0;
        for (String readFileFromAssets : AppSystem.baseDBFileName) {
            AppSystem.createDatabase(this.mainActivity, this.dbHelper, AppSystem.buildDatabase(AppSystem.readFileFromAssets(this.mainActivity, readFileFromAssets)));
            Integer[] numArr = new Integer[1];
            progress++;
            numArr[0] = Integer.valueOf((int) ((((double) progress) * 100.0d) / ((double) total)));
            publishProgress(numArr);
        }
    }

    private void deleteDatabase() {
        this.dbHelper.deleteDatabase();
        publishProgress(new Integer[]{Integer.valueOf(100)});
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(this.mainActivity, "Begin excuting...", Toast.LENGTH_SHORT).show();
    }

    protected Void doInBackground(Integer... params) {
        switch (params[0].intValue()) {
            case DbHandlerActivity.SAVE_DB_TO_SDCARD:
                saveDatabaseToSDCard();
                break;
            case DbHandlerActivity.LOAD_DB_FROM_SDCARD:
                loadDatabaseFromSDCard();
                break;
            case DbHandlerActivity.RESTORE_DB_TO_DEFAULT:
                restoreDatabaseToDefault();
                break;
            case DbHandlerActivity.DELETE_DB:
                deleteDatabase();
                break;
        }
        return null;
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        TextView tvSpeed = (TextView) this.mainActivity.findViewById(R.id.tvSpeed);
        ((ProgressBar) this.mainActivity.findViewById(R.id.pbSpeed)).setProgress(values[0].intValue());
        tvSpeed.setText(values[0] + " %");
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Toast.makeText(this.mainActivity, "Complete!", Toast.LENGTH_SHORT).show();
    }
}
