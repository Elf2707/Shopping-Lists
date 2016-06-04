package lorien.ua.shoppinglist;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import lorien.ua.shoppinglist.service.DaoMasterManager;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import lorien.ua.shoppinglist.service.ShoppingListService;
import lorien.ua.shoppinglist.service.impl.ShoppingListItemServiceImpl;
import lorien.ua.shoppinglist.service.impl.ShoppingListServiceImpl;
import ua.lorien.shoppinglist.model.dao.DaoMaster;
import ua.lorien.shoppinglist.model.dao.DaoSession;

/**
 * Created by Elf on 30.03.2016.
 * My application class for holding application level singletons
 */
public class MyApplication extends Application {
    public static final String PREF_KEEP_SCREEN_ON="keepScreenOn";

    //Services for working with dbs
    private ShoppingListService shoppingListService = null;
    private ShoppingListItemService shoppingListItemService = null;
    private SharedPreferences prefs = null;

    public MyApplication() {
    }

    //Shopping list service singleton
    public ShoppingListService getShoppingListService() {
        if (shoppingListService == null) {
            shoppingListService = new ShoppingListServiceImpl(this);
        }

        return shoppingListService;
    }

    //Shopping list items service singleton
    public ShoppingListItemService getShoppingListItemService() {
        if (shoppingListItemService == null) {
            shoppingListItemService = new ShoppingListItemServiceImpl(this);
        }

        return shoppingListItemService;
    }


    @Override
    public void onTerminate() {
        closeDb();
        super.onTerminate();
    }


    private void closeDb(){
        DaoSession session = DaoMasterManager.getDaoMaster(this).newSession();
        session.getDatabase().close();
    }

    synchronized public SharedPreferences getPrefs(){
        return prefs;
    }

    //Preferences loader
    private class LoadPrefsThread extends Thread {
        @Override
        public void run() {
            synchronized (this){
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
        }
    }
}
