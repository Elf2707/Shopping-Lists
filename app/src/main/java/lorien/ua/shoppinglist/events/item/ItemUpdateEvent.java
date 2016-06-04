package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 03.05.2016.
 * Update event for update child item
 */
public class ItemUpdateEvent {
    private ShoppingListItem item = null;
    private int position = -1;

    public ItemUpdateEvent(ShoppingListItem item, int position) {
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
