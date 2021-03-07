package com.example.m_vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    String userId = null;

    //Note Your consumer key and secret should be obfuscated in your source code before shipping
    private static final String TWITTER_KEY = "05jtUe1ela6L5rbvq6mk4calL";
    private static final String TWITTER_SECRET = "qP0eM2tQ0a4l65Wf8WlaUF33nfAM3JTqBZoKPUTbFmNnNg9HI8";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView imageView;
    private TextView textView, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashh);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        imageView.startAnimation(animation);
        textView.startAnimation(animation);
        textView2.startAnimation(animation);
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    //User is signed out
                    SharedPreferences sharedPreferences = getSharedPreferences("mypre", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TOKEN",user.getUid()) ;
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                //START_EXCLUDE

                //END_EXCLUDE

            }
        };
        splashMethod(); //This method is responsible for taking the activity to the next screen
    }

    private void splashMethod()
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    mAuth.addAuthStateListener(mAuthListener);
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
