package android.rsa.androidlearn.activity;

import android.content.Intent;
import android.rsa.androidlearn.R;
import android.rsa.androidlearn.service.MyTestService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_receive_message;
    Button button_my_test_service;
    Button button_async_task;
    Button buttonDownloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_receive_message = (Button) findViewById(R.id.button_receive_message);
        button_receive_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ReceiveMassageActivity.class);
                startActivity(intent);
            }
        });
        button_my_test_service = (Button) findViewById(R.id.button_my_test_servive);
        button_my_test_service.setOnClickListener(this);

        button_async_task = (Button) findViewById(R.id.button_asynctask);
        button_async_task.setOnClickListener(this);

        buttonDownloadFile = (Button)findViewById(R.id.button_download_manager);
        buttonDownloadFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_my_test_servive:
                Intent intent=new Intent(getApplicationContext(), MyTestServiceActivity.class);
                intent.putExtra("test", "t");
                startActivity(intent);
                break;
            case R.id.button_asynctask:
                new android.rsa.androidlearn.utils.MyTestAsyncTask(this).execute(0);
                break;
            case R.id.button_download_manager:
                Intent intentDownload = new Intent(getApplicationContext(), DownloadManagerActivity.class);
                startActivity(intentDownload);
                break;
            default:
                break;
        }
    }
}
