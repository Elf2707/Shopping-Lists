package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 25.04.2016.
 * Mark selected List as done
 */
public class ListMarkAsDoUndoEvent {
    private int position = -1;
    private ShoppingList list = null;

    public ListMarkAsDoUndoEvent(int position, ShoppingList list) {
        this.position = position;
        this.list = list;
    }

    public int getPosition() {
        return position;
    }

    public ShoppingList getList() {
        return list;
    }
}
