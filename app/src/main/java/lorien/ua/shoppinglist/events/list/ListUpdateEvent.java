package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 28.04.2016.
 * Update existing Shopping List
 */
public class ListUpdateEvent {
    private ShoppingList shoppingList = null;
    private int position = -1;

    public ListUpdateEvent(ShoppingList shoppingList, int position) {
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
