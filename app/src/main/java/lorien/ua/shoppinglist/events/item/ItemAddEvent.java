package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 09.04.2016.
 * Add List item event
 */
public class ItemAddEvent {
    private ShoppingListItem item;
    private int position = -1;
    private boolean isNewItem = false;


    public ItemAddEvent(ShoppingListItem item, int position, boolean isNewItem) {
        this.item = item;
        this.isNewItem = isNewItem;
        this.position = position;
    }

    public ShoppingListItem getItem() {
        return item;
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public int getPosition() {
        return position;
    }
}
