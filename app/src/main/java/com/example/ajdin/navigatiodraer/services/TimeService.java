package com.example.ajdin.navigatiodraer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;
import com.example.ajdin.navigatiodraer.tasks.UploadTaskNapomena;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ajdin on 27.3.2018..
 */

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 3000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private ArrayList<String> listaStack;
    private DatabaseHelper db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        db = new DatabaseHelper(TimeService.this);
        listaStack = db.getAllStacked();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        // schedule task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    ConnectivityManager wifi = (ConnectivityManager)TimeService.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info=wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    listaStack = db.getAllStacked();
                    if (listaStack.size()!=0) {
                        if (info.isConnected()) {
                                for (String s : listaStack) {
                                    File file = new File(s);
                                    if (s.contains("napomene")){
                                        new UploadTaskNapomena(DropboxClient.getClient("-moQGOzCYwAAAAAAAAAAYt6hUOPRHKC2L9vZXuVkEVxJa7qRo8gWN38fMX_PfC53"), file, TimeService.this).execute();

                                    }
                                    else {
                                        new UploadTask(DropboxClient.getClient("-moQGOzCYwAAAAAAAAAAYt6hUOPRHKC2L9vZXuVkEVxJa7qRo8gWN38fMX_PfC53"), file, TimeService.this).execute();
                                    }

                                }
                            }

                        // display toast
                    }
                    else {
                        mTimer.cancel();
                        stopSelf();

                    }



//                    Toast.makeText(getApplicationContext(), getDateTime(),
//                            Toast.LENGTH_SHORT).show();
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }
}