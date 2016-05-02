package lorien.ua.shoppinglist.events.list;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 30.03.2016.
 * Fires then new shopping list adds to the database
 */
public class ListAddEvent {
    private ShoppingList shoppingList;
    private int position = -1;

    public ListAddEvent(ShoppingList shoppingList, int position){
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
