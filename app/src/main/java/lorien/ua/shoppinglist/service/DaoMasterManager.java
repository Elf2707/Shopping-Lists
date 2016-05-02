package lorien.ua.shoppinglist.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ua.lorien.shoppinglist.model.dao.DaoMaster;


/**
 * Created by Elf on 25.03.2016.
 * Dao Master Manager
 */
public class DaoMasterManager {

    public static DaoMaster daoMaster = null;
    private DaoMasterManager(){

    }

    public static DaoMaster getDaoMaster( Context ctx ){
        if( daoMaster == null ) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx.getApplicationContext(), "shopping_list_db", null );
            SQLiteDatabase db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
        }
        return daoMaster;
    }
}
