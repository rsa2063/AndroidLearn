package android.rsa.androidlearn.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.rsa.androidlearn.R;
import android.rsa.androidlearn.service.MyTestService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MyTestServiceActivity extends AppCompatActivity implements View.OnClickListener {

    Button button_start_service;
    Button button_stop_service;
    Button button_bind;
    Button button_unbind;
    Button button_count;
    Button button_show_count;
    Button button_call_notify;

    private MyTestService.MyTestBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MyTestService.MyTestBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("TAG", "service run: unbind service");
            binder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test_service);

        button_start_service = (Button) findViewById(R.id.button_start_service);
        button_stop_service = (Button) findViewById(R.id.button_stop_service);
        button_bind = (Button) findViewById(R.id.button_bind);
        button_unbind = (Button) findViewById(R.id.button_unbind);
        button_count = (Button) findViewById(R.id.button_add);
        button_show_count = (Button) findViewById(R.id.button_show_count);
        button_call_notify = (Button) findViewById(R.id.button_notify);

        button_start_service.setOnClickListener(this);
        button_stop_service.setOnClickListener(this);
        button_bind.setOnClickListener(this);
        button_unbind.setOnClickListener(this);
        button_count.setOnClickListener(this);
        button_show_count.setOnClickListener(this);
        button_call_notify.setOnClickListener(this);

        String test = getIntent().getStringExtra("test");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_start_service:
                Intent my_test_start = new Intent(this, MyTestService.class);
                startService(my_test_start);
                break;
            case R.id.button_stop_service:
                Intent my_test_stop = new Intent(this, MyTestService.class);
                stopService(my_test_stop);
                break;
            case R.id.button_bind:
                Intent my_test_start_bind = new Intent(this, MyTestService.class);
                bindService(my_test_start_bind, connection, BIND_AUTO_CREATE);

                break;
            case R.id.button_unbind:
                unbindService(connection);
                binder = null;
                break;
            case R.id.button_add:
                if(binder == null) {
                    Toast.makeText(this, "service not bind", Toast.LENGTH_SHORT).show();
                } else {
                    binder.addCount();
                }
                break;
            case R.id.button_show_count:
                if(binder == null) {
                    Toast.makeText(this, "service not bind", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, String.valueOf(binder.getCount()), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_notify:

                Intent notificationIntent = new Intent(this, ReceiveMassageActivity.class);
                notificationIntent.putExtra("notifyId", 1);
                notificationIntent.putExtra("test", "t");
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("i am title")
                        .setContentText("here is text")
                        .setShowWhen(true)
                        .setWhen(System.currentTimeMillis())
                        .build();
                notification.contentIntent = pendingIntent;
                nm.notify(1, notification);
//                startForeground(1, notification);
                break;
            default:
                break;
        }
    }
}
