package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 09.04.2016.
 * Add List item event
 */
public class ItemAddEvent {
    private ShoppingListItem item;
    private int position = -1;


    public ItemAddEvent(ShoppingListItem item, int position) {
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
