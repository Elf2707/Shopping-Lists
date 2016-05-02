package lorien.ua.shoppinglist.service.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lorien.ua.shoppinglist.service.DaoMasterManager;
import lorien.ua.shoppinglist.service.ShoppingListService;
import ua.lorien.shoppinglist.model.dao.DaoMaster;
import ua.lorien.shoppinglist.model.dao.DaoSession;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListDao;

/**
 * Created by Elf on 25.03.2016.
 */
public class ShoppingListServiceImpl implements ShoppingListService {

    private DaoMaster daoMaster = null;

    public ShoppingListServiceImpl(Context ctx) {
        daoMaster = DaoMasterManager.getDaoMaster(ctx);
    }

    @Override
    public ShoppingList add(ShoppingList shoppinglist) {
        DaoSession session = daoMaster.newSession();
        if (session != null) {
            ShoppingListDao shoppingListDao = session.getShoppingListDao();
            shoppingListDao.insertOrReplace(shoppinglist);
            return shoppinglist;
        }

        return null;
    }

    @Override
    public void delete(Long id) {
        DaoSession session = daoMaster.newSession();
        if (session != null) {
            ShoppingListDao shoppingListDao = session.getShoppingListDao();
            shoppingListDao.deleteByKey(id);
        }
    }

    @Override
    public ShoppingList update(ShoppingList list) {
        return null;
    }

    @Override
    public ShoppingList findByName(String name) {
        return null;
    }

    @Override
    public ShoppingList findById(Long id) {
        return null;
    }

    @Override
    public List<ShoppingList> findAll() {
        DaoSession session = daoMaster.newSession();
        List<ShoppingList> shoppingLists = null;

        if (session != null) {
            ShoppingListDao shoppingListDao = session.getShoppingListDao();
            shoppingLists = shoppingListDao.loadAll();

            //Test for empty list
            if (shoppingLists.size() == 0 ) {
                //Return empty ArrayList because loadAll return Collections.EmptyList
                return new ArrayList<>();
            }
        }

        return shoppingLists;
    }
}
