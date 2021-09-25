package android.rsa.androidlearn.activity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.rsa.androidlearn.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class DownloadManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputUrl;
    private Button buttonDownload;
    private Button buttonGetDownloadInfo;

    private DownloadManager manager;
    private int downloadId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        inputUrl = (EditText) findViewById(R.id.inputUrl);
        buttonDownload = (Button)findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(this);
        buttonGetDownloadInfo = (Button)findViewById(R.id.button_get_download_info);
        buttonGetDownloadInfo.setOnClickListener(this);
        Button buttonRemoveDownload = (Button)findViewById(R.id.button_remove_download);
        buttonRemoveDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonDownload:
                manager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(inputUrl.getText().toString()));
                String filename = inputUrl.getText().toString().substring(inputUrl.getText().toString().lastIndexOf("/") + 1,
                        inputUrl.getText().toString().length());
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("downloadind something");
                Log.w("TAG", Uri.fromFile(Environment.getExternalStorageDirectory()).getPath());
                downloadId = (int)manager.enqueue(request);
                break;
            case R.id.button_get_download_info:
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
                Cursor cursor = manager.query(query);
                if(cursor != null) {
                    while(cursor.moveToNext()) {
                        Log.w("TAG", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                        Log.w("TAG", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
                        Log.w("TAG", cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    }
                }
                break;
            case R.id.button_remove_download:
                manager.remove(downloadId);
                break;
            default:
                break;
        }
    }
}
