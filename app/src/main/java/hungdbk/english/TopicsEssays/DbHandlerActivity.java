package hungdbk.english.TopicsEssays;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import hungdbk.english.TopicsEssays.Function.DatabaseHandler;
import hungdbk.english.TopicsEssays.R;

public class DbHandlerActivity extends AppCompatActivity {
    public static final int DELETE_DB = 4;
    public static final int LOAD_DB_FROM_SDCARD = 2;
    public static final int RESTORE_DB_TO_DEFAULT = 3;
    public static final int SAVE_DB_TO_SDCARD = 1;
    private Button btDeleteDB;
    private Button btLoadDBFromSDCard;
    private Button btRestoreDBToDefault;
    private Button btSaveDBToSDCard;
    private DatabaseHandler dbHandler;

    private void getWidgets() {
        this.btSaveDBToSDCard = (Button) findViewById(R.id.btSaveDBToSDCard);
        this.btLoadDBFromSDCard = (Button) findViewById(R.id.btLoadDBFromSDCard);
        this.btRestoreDBToDefault = (Button) findViewById(R.id.btRestoreDBToDefault);
        this.btDeleteDB = (Button) findViewById(R.id.btDeleteDB);
    }

    public void createNewHandler() {
        this.dbHandler = new DatabaseHandler(this);
    }

    private void saveDatabaseToSDCard() {
        Builder alert = new Builder(this, 32);
        alert.setTitle("Save?");
        alert.setMessage("Are you sure you want to save database to SDCard?\nAll the database files will be saved at mnt/sdcard/EnglishTopicsEssays/...");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewHandler();
                dbHandler.execute(Integer.valueOf(DbHandlerActivity.SAVE_DB_TO_SDCARD));
            }
        });
        alert.setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }

    private void loadDatabaseFromSDCard() {
        Builder alert = new Builder(this, 32);
        alert.setTitle("Load?");
        alert.setMessage("Are you sure you want to load database from SDCard?\nMake sure that all the database files have placed in mnt/sdcard/EnglishTopicsEssays/...");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewHandler();
                dbHandler.execute(Integer.valueOf(DbHandlerActivity.LOAD_DB_FROM_SDCARD));
            }
        });
        alert.setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }

    private void restoreDatabaseToDefault() {
        Builder alert = new Builder(this, 32);
        alert.setTitle("Restore?");
        alert.setMessage("Are you sure you want to restore database to default?\nEverything has saved will be removed and can't restore again...");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewHandler();
                dbHandler.execute(Integer.valueOf(DbHandlerActivity.RESTORE_DB_TO_DEFAULT));
            }
        });
        alert.setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }

    private void deleteDatabase() {
        Builder alert = new Builder(this, 32);
        alert.setTitle("Delete?");
        alert.setMessage("Are you sure you want to delete all the topics and all the essays?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewHandler();
                dbHandler.execute(Integer.valueOf(DbHandlerActivity.DELETE_DB));
            }
        });
        alert.setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_db_handler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        getWidgets();
        this.btSaveDBToSDCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbHandlerActivity.this.saveDatabaseToSDCard();
            }
        });
        this.btLoadDBFromSDCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbHandlerActivity.this.loadDatabaseFromSDCard();
            }
        });
        this.btRestoreDBToDefault.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbHandlerActivity.this.restoreDatabaseToDefault();
            }
        });
        this.btDeleteDB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbHandlerActivity.this.deleteDatabase();
            }
        });
    }

    private void closeActivity() {
        finish();
    }
}
