package lorien.ua.shoppinglist.adapters;

import android.os.Bundle;
import android.util.Log;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 01.04.2016.
 * Multichoice implementation
 */
public abstract class ExpandableSingleChoiceAdapter<T extends ParentViewHolder, S extends ChildViewHolder>
        extends ExpandableRecyclerAdapter<T, S> implements Choiceable {
    private final Choiceable choiceMode;

    public ExpandableSingleChoiceAdapter(Choiceable choiceMode, List<ShoppingList> parentItemList) {
        super(parentItemList);
        this.choiceMode = choiceMode;
    }

    public void setItemState(int position, boolean initValue) {
        choiceMode.setItemState(position, initValue);
    }

    public boolean isChecked(int position) {
        return choiceMode.isChecked(position);
    }

    public void clearCheckedStates() {
        choiceMode.clearCheckedStates();
    }

    public void addCheckedItem(int position, boolean initValue) {
        choiceMode.addCheckedItem(position, initValue);
    }

    public void onSaveInstanceState(Bundle state) {
        choiceMode.onSaveInstanceState(state);
    }

    public void onRestoreInstanceState(Bundle state) {
        choiceMode.onRestoreInstanceState(state);
    }

    @Override
    public void delCheckedItem(int position) {
        choiceMode.delCheckedItem(position);
    }

    @Override
    public int getSelectedItemPosition() {
        return choiceMode.getSelectedItemPosition();
    }
}
