package de.master.kd.epic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.master.kd.epic.navigation.NavigationActivity;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

             new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent navigation = new Intent(SplashScreen.this, NavigationActivity.class);
                startActivity(navigation);
                finish();;
            }
        }, SPLASH_TIME_OUT);
    }
}
