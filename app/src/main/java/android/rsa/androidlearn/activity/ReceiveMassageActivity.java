package android.rsa.androidlearn.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.rsa.androidlearn.*;
import android.rsa.androidlearn.utils.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class ReceiveMassageActivity extends AppCompatActivity {

    DatagramSocket serverdS = null;
    DatagramPacket message = null;

    private EditText anotherKey;
    private EditText anotherIp;
    private Button anotherOk;
    private ListView msgList;
    private EditText myMsg;
    private Button mySend;

    private List<Msg> list = new ArrayList<>();
    private byte[] anotherKeyBytes;
    MsgAdapter adapter;
    private ServerSocket serverS;
    private int REC_PORT = 8091;
    int notifyId;

    Thread receiveT;
    Thread receiveUDP;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    break;
                case 1:
                    Msg newms = (Msg) msg.obj;
                    list.add(newms);
                    adapter.notifyDataSetChanged();
                    myMsg.setText("");
                    msgList.setSelection(list.size());

                    myMsg.setText(newms.getMsg());

                    //show ip
                    anotherIp.setText(msg.getData().getString("ip"));

                    //send notification
                    Intent notificationIntent = new Intent(getApplicationContext(), ReceiveMassageActivity.class);
                    notificationIntent.putExtra("notifyId", 1);
                    notificationIntent.putExtra("msg", newms.getMsg());
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("receive message")
                            .setContentText(newms.getMsg())
                            .setShowWhen(true)
                            .setWhen(System.currentTimeMillis())
                            .build();
                    notification.contentIntent = pendingIntent;
                    nm.notify(1, notification);
                    break;
                case -1:
                    String error = (String) msg.obj;
                    Toast.makeText(ReceiveMassageActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_massage);

        myMsg = (EditText) findViewById(R.id.edit_text_msg);

        list.add(new Msg("ex1", Msg.RECEIVE_SIGN));
        list.add(new Msg("ex2", Msg.SEND_SIGN));

        msgList = (ListView) findViewById(R.id.list_view_msg);
        adapter = new MsgAdapter(ReceiveMassageActivity.this, R.layout.msg_item, list);
        msgList.setAdapter(adapter);
        //set item click listener
        msgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Msg temp = list.get(position);
                myMsg.setText(temp.getMsg());
            }
        });
        msgList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Msg temp = list.get(position);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ContentResolver cr = getContentResolver();
                ClipData clipData = ClipData.newPlainText("new", temp.getMsg());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "message has been copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        anotherIp = (EditText) findViewById(R.id.ip_aite);
        mySend = (Button) findViewById(R.id.button_send);
        mySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        SocketCommu.SendMessageByUDP(anotherIp.getText().toString(), myMsg.getText().toString());
                    }
                }.start();

            }
        });

//        receiveT = new Thread(new ReceiveThread());
//        receiveT.start();
        try {
//            Toast.makeText(ReceiveMassageActivity.this, "your ip is " + InetAddress.getLocalHost().getHostAddress()
//                    , Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            ;
        }


        receiveUDP = new Thread(new UDPReceiveThread());
        receiveUDP.start();

        Intent intentFromOther = getIntent();

        if((notifyId = intentFromOther.getIntExtra("notifyId", -1)) != -1) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(notifyId);
            String message = intentFromOther.getStringExtra("msg");
            list.add(new Msg(message, Msg.RECEIVE_SIGN));
            adapter.notifyDataSetChanged();
        }
        msgList.requestFocus();
        hideSoftKeyboard();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiveT != null) {
            receiveT.interrupt();
        }
        if(receiveUDP != null) {
            receiveUDP.interrupt();
        }
        try {
            serverS.close();
            if(serverdS != null) {
                serverdS.disconnect();
                serverdS.close();
            }
        } catch(Exception e) {
            e.getMessage();
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ReceiveMassage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    //receive mesage by tcp socket
    class ReceiveThread implements Runnable {           //监听8091端口

        @Override
        public void run() {
            Socket s = null;

            try {
                serverS = new ServerSocket(REC_PORT);
            } catch(Exception e) {
                e.printStackTrace();
            }

            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Log.d("TAG", "run: start listening");
                    s = serverS.accept();
                    Log.d("TAG", "run: receive message");
                    new Thread(new chatOne(s)).start();
                } catch(Exception e) {
                    e.getMessage();
                }
            }
        }
    }


    class chatOne implements Runnable {   //受到请求 处理请求
        Socket so = null;

        public chatOne(Socket s) {
            so = s;
        }

        @Override
        public void run() {
            try {

                BufferedInputStream in = new BufferedInputStream(so.getInputStream());
                int size = in.read();
                byte[] content = new byte[size];
                if(in.read(content, 0, size) != -1) {
                    Log.d("TAG", "run: receive message " + new String(content, "utf-8"));
                    Msg newMsg = new Msg(new String(content, "utf-8"), Msg.RECEIVE_SIGN);
                    Message m = new Message();
                    m.obj = newMsg;
                    m.what = 1;
                    handler.sendMessage(m);
                }
            } catch(Exception e) {
                e.printStackTrace();
                //Log.d("sockettest", e.getMessage());
            }
        }
    }

    //receive message by udp socket
    class UDPReceiveThread implements Runnable {           //监听8091端口

        @Override
        public void run() {

            try {


                message = new DatagramPacket(new byte[256], 256);
                serverdS = new DatagramSocket(null);
                serverdS.setReuseAddress(true);
                serverdS.bind(new InetSocketAddress(REC_PORT));

//                serverdS = new DatagramSocket(REC_PORT);

                //show your ip
//                InetAddress.getLocalHost().getHostAddress();
                Message showLocalIp = new Message();
                showLocalIp.obj = InetAddress.getLocalHost().getHostAddress();
                showLocalIp.what = -1;
                handler.sendMessage(showLocalIp);

                //get wifi ip
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                Message showLocalIp1 = new Message();
                showLocalIp1.obj = "wifi ip is " + String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                showLocalIp1.what = -1;
                handler.sendMessage(showLocalIp1);

                getIpAddress();
                getAllIpAddress();

                Log.w("TAG", "run: start listening");
            } catch(Exception e) {
                Log.w("TAG", e.getMessage());
                e.printStackTrace();
            }
            while(!Thread.currentThread().isInterrupted()) {

                if(serverdS == null) break;

                Log.w("TAG", "servers not null");

                try {
                    serverdS.receive(message);
                    Log.w("TAG", "run: receive message form " + message.getAddress().getHostName()
                            + " : " + message.getAddress().getHostAddress());
                    byte[] data = new byte[message.getData()[0]];
                    System.arraycopy(message.getData(), 1, data, 0, data.length);

                    Msg newMsg = new Msg(new String(data, "utf-8"), Msg.RECEIVE_SIGN);
                    Message m = new Message();
                    m.obj = newMsg;
                    Bundle bundleIp = new Bundle();
                    bundleIp.putString("ip", message.getAddress().getHostAddress());
                    m.setData(bundleIp);
                    m.what = 1;
                    handler.sendMessage(m);
                } catch(Exception e) {
                    Log.w("TAG", e.getMessage());
                }
            }
        }
    }

    //from stackflowstack
    public void hideSoftKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String getIpAddress() throws Exception{
        try {
            for(Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface)en.nextElement();
                for(Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddress = inetAddress.getHostAddress().toString();
                        Log.w("TAG", "LAN IP address is " + ipAddress);
                        Log.w("TAG", "Host IP address is " + InetAddress.getLocalHost().toString());
                        return ipAddress;
                    }
                }
            }
        } catch(SocketException ex) {
            Log.w("TAG", ex.toString());
        }
        return null;
    }

    public static String getAllIpAddress() throws Exception{
        try {
            for(Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface)en.nextElement();
                for(Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                        String ipAddress = inetAddress.getHostAddress().toString();
                        Log.w("TAG", "IP address is " + ipAddress);
                }
            }
        } catch(SocketException ex) {
            Log.w("TAG", ex.toString());
        }
        return null;
    }
}
