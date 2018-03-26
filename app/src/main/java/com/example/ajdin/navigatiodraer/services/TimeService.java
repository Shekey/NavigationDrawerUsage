package com.example.ajdin.navigatiodraer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;

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
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    DatabaseHelper db=new DatabaseHelper(TimeService.this);
                    ArrayList<String> listaStack=db.getAllStacked();
                    if (listaStack.size()!=0){
                        for (String s: listaStack) {
                            File file = new File(s);
                            new UploadTask(DropboxClient.getClient("aLRppJLoiTAAAAAAAAAADkJLNGAbqPzA0hZ_oVvVlEhNiyiYA94B9ndRUrIXxV8G"), file, TimeService.this).execute();

                        }
                    }
                    // display toast


                    Toast.makeText(getApplicationContext(), getDateTime(),
                            Toast.LENGTH_SHORT).show();
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