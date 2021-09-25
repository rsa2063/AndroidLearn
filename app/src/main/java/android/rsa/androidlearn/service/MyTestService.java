package android.rsa.androidlearn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyTestService extends Service {

    private int count = 0;
    private MyTestBinder binder = new MyTestBinder();

    public MyTestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.w("TAG", "service run: onbind");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.w("TAG", "service run: start service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("TAG", "service run: start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("TAG", "service run: destory service");
    }

    public class MyTestBinder extends Binder {

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}
