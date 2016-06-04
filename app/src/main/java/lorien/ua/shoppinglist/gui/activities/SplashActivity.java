package lorien.ua.shoppinglist.gui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.gui.activities.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Setting custom font for the title
        TextView title = (TextView) findViewById(R.id.appTitle);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Lemon-Regular.ttf");
        title.setTypeface(font);

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
