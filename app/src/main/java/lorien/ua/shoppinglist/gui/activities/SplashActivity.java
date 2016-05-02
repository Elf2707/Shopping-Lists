package lorien.ua.shoppinglist.gui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.gui.activities.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed( new Runnable() {

            @Override
            public void run() {
                nextActivity();
                finish();
            }
        }, 2000);
    }

    public void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
