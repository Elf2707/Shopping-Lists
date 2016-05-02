package lorien.ua.shoppinglist;

import android.app.Application;

import lorien.ua.shoppinglist.service.ShoppingListItemService;
import lorien.ua.shoppinglist.service.ShoppingListService;
import lorien.ua.shoppinglist.service.impl.ShoppingListItemServiceImpl;
import lorien.ua.shoppinglist.service.impl.ShoppingListServiceImpl;

/**
 * Created by Elf on 30.03.2016.
 * My application class for holding application level singletons
 */
public class MyApplication extends Application {
    //Services for working with dbs
    private ShoppingListService shoppingListService = null;
    private ShoppingListItemService shoppingListItemService = null;

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
}
