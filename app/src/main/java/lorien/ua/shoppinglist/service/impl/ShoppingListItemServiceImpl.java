package lorien.ua.shoppinglist.service.impl;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lorien.ua.shoppinglist.service.DaoMasterManager;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import ua.lorien.shoppinglist.model.dao.DaoMaster;
import ua.lorien.shoppinglist.model.dao.DaoSession;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListDao;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;
import ua.lorien.shoppinglist.model.dao.ShoppingListItemDao;

/**
 * Created by Elf on 25.03.2016.
 * Service for Shopping List Items
 */
public class ShoppingListItemServiceImpl implements ShoppingListItemService {

    private DaoMaster daoMaster = null;

    public ShoppingListItemServiceImpl(Context ctx) {
        daoMaster = DaoMasterManager.getDaoMaster(ctx);
    }

    @Override
    public ShoppingListItem add(ShoppingListItem listItem) {
        DaoSession session = daoMaster.newSession();
        if (session != null) {
            ShoppingListItemDao shoppingListItemDao = session.getShoppingListItemDao();
            shoppingListItemDao.insertOrReplace(listItem);
            return listItem;
        }

        return null;
    }

    @Override
    public void delete(Long id) {
        DaoSession session = daoMaster.newSession();
        if (session != null) {
            ShoppingListItemDao shoppingListItemDao = session.getShoppingListItemDao();
            shoppingListItemDao.deleteByKey(id);
        }
    }

    @Override
    public ShoppingListItem update(ShoppingListItem listTem ) {
        return null;
    }

    @Override
    public ShoppingListItem findByName(String name) {
        return null;
    }

    @Override
    public ShoppingListItem findById(Long id) {
        return null;
    }

    @Override
    public List<ShoppingListItem> findAll() {
        DaoSession session = daoMaster.newSession();
        List<ShoppingListItem> shoppingListItems = null;

        if (session != null) {
            ShoppingListItemDao shoppingListItemsDao = session.getShoppingListItemDao();
            shoppingListItems = shoppingListItemsDao.loadAll();

            //Test for empty list
            if (shoppingListItems.size() == 0) {
                //Return empty ArrayList because loadAll return Collections.EmptyList
                return new ArrayList<>();
            }
        }

        return shoppingListItems;
    }

    @Override
    public List<ShoppingListItem> findAllByListId(Long id) {
        DaoSession session = daoMaster.newSession();
        List<ShoppingListItem> shoppingListItems = new ArrayList<>();

        if (session != null) {
            ShoppingListItemDao shoppingListItemsDao = session.getShoppingListItemDao();

            //Copy elements to ArrayList<ShoppingListItem> from abstract list
            List<ShoppingListItem> allItems = shoppingListItemsDao._queryShoppingList_Items(id);
            shoppingListItems.addAll(allItems);
        }

        return shoppingListItems;
    }
}
