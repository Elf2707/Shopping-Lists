package lorien.ua.shoppinglist.gui.fragments.model.item;

import android.app.Fragment;
import android.os.Bundle;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 29.04.2016.
 * List item model fragment
 */
public class ItemModelFragment extends Fragment {
    private ShoppingListItem shoppingListItem = null;
    private int position = -1;
    private boolean isNewItem = true;

    public ItemModelFragment() {
        this.shoppingListItem = new ShoppingListItem();
        shoppingListItem.setIsDone(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ShoppingListItem getShoppingListItem() {
        return shoppingListItem;
    }

    public void setShoppingListItem(ShoppingListItem shoppingListItem) {
        this.shoppingListItem = shoppingListItem;
        isNewItem = false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isNewItem() {
        return isNewItem;
    }
}
