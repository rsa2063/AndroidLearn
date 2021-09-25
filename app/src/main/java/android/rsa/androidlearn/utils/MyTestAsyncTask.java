package android.rsa.androidlearn.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangbin on 2016/10/30.
 */

public class MyTestAsyncTask extends AsyncTask<Object, Integer, String> {

    static int nowCount = 0;
    Context context;

    public MyTestAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {
        publishProgress(0);
        return "current thread toString is " + Thread.currentThread().toString();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        nowCount++;
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, s + "\ncurrent thread toString is " + Thread.currentThread().toString()
                + "\nnowCount is " + nowCount,
                Toast.LENGTH_SHORT).show();
        Log.w("tag", s + "\ncurrent thread toString is " + Thread.currentThread().toString());
        super.onPostExecute(s);
    }
}
