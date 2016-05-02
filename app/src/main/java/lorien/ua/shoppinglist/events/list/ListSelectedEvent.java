package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 19.04.2016.
 * List selected
 */
public class ListSelectedEvent {
    private ShoppingList shoppingList;
    private int position;

    public ListSelectedEvent(ShoppingList shoppingList, int position) {
        this.shoppingList = shoppingList;
        this.position = position;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public int getPosition() {
        return position;
    }
}
