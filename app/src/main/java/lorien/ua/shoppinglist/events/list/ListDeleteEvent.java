package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 19.04.2016.
 * delete list event
 */
public class ListDeleteEvent {
    private ShoppingList list = null;
    private int position = -1;

    public ListDeleteEvent(ShoppingList list, int position) {
        this.list = list;
        this.position = position;
    }

    public ShoppingList getList() {
        return list;
    }

    public int getPosition() {
        return position;
    }
}
