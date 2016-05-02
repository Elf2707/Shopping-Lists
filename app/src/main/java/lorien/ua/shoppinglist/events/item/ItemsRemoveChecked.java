package lorien.ua.shoppinglist.events.item;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 14.04.2016.
 * Remove all checked list items from the model List
 */
public class ItemsRemoveChecked {
    private ShoppingList shoppingList = null;

    public ItemsRemoveChecked(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }
}
