package com.example.ajdin.navigatiodraer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by ajdin on 24.3.2018..
 */

public class MyServiceUploading extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service is destroyed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isInternetAvailable("8.8.8.8", 53, 1000);

            return START_STICKY;


    }
    public void isInternetAvailable(final String address, final int port, final int timeoutMs) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress(address, port);
                    sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
                    sock.close();
                    File file = new File(Environment.getExternalStorageDirectory().toString()+ "/racunidevice/"+
                            "aaa---25 03 2018 16:48"+".txt");
                    new UploadTask(DropboxClient.getClient("aLRppJLoiTAAAAAAAAAADkJLNGAbqPzA0hZ_oVvVlEhNiyiYA94B9ndRUrIXxV8G"), file, MyServiceUploading.this).execute();


                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        });

        thread.start();

    }

}
