package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 01.05.2016.
 * Refresh a view in some position
 */
public class ListViewRefresh {
    private ShoppingList shoppingList = null;
    private int position = -1;

    public ListViewRefresh(ShoppingList shoppingList, int position) {
        this.position = position;
        this.shoppingList = shoppingList;
    }

    public int getPosition() {
        return position;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }
}
