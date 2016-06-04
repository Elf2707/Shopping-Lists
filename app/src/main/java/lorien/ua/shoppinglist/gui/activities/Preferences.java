package lorien.ua.shoppinglist.gui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import lorien.ua.shoppinglist.R;

/**
 * Created by Elf on 06.05.2016.
 * Preferences activity
 */
public class Preferences extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(getFragmentManager().findFragmentById(android.R.id.content) == null){
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new DisplayPrefs())
                    .commit();
        }
    }

    public static class DisplayPrefs extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_display);
        }
    }
}
