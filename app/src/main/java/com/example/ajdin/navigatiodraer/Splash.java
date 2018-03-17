package com.example.ajdin.navigatiodraer;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = findViewById(R.id.tv_welcome);
        imageView = findViewById(R.id.iv_welcome);
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        textView.startAnimation(myanim);
        imageView.startAnimation(myanim);
        final Intent i=new Intent(this,MainActivity.class);
        Thread timer =new Thread(){
            public void run(){
                try{
                sleep(3000);

                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
