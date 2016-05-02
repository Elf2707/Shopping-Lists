package lorien.ua.shoppinglist.gui.fragments.model.list;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 12.04.2016.
 * Shopping List model fragment
 */
public class ListModelFragment extends Fragment {
    private int position = -1;
    private ShoppingList shoppingList = null;

    public ListModelFragment() {
        this.shoppingList = new ShoppingList();
        shoppingList.setIsDone(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
