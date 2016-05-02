package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 27.04.2016.
 * Delete List Item Event
 */
public class ItemDeleteEvent {
    int position = -1;
    ShoppingListItem item = null;

    public ItemDeleteEvent(int position, ShoppingListItem item) {
        this.position = position;
        this.item = item;
    }

    public int getPosition() {
        return position;
    }

    public ShoppingListItem getItem() {
        return item;
    }
}
