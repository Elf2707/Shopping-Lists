package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 26.04.2016.
 * List item selected event
 */
public class ItemSelectedEvent {
    private ShoppingListItem item = null;
    private int position = -1;

    public ItemSelectedEvent(ShoppingListItem item, int position) {
        this.item = item;
        this.position = position;
    }

    public ShoppingListItem getItem() {
        return item;
    }

    public int getPosition() {
        return position;
    }
}
