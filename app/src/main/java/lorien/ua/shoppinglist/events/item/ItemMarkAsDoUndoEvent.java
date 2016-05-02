package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 25.04.2016.
 * Mark selected item as done
 */
public class ItemMarkAsDoUndoEvent {
    private int position = -1;
    private ShoppingListItem item = null;
    private ShoppingList parentList = null;

    public ItemMarkAsDoUndoEvent(int position, ShoppingListItem item, ShoppingList parentList) {
        this.position = position;
        this.item = item;
        this.parentList = parentList;
    }

    public int getPosition() {
        return position;
    }

    public ShoppingListItem getItem() {
        return item;
    }

    public ShoppingList getParentList() {
        return parentList;
    }
}
