package lorien.ua.shoppinglist.adapters.choiceable.impl;

import android.os.Bundle;
import android.util.Log;

import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import lorien.ua.shoppinglist.parcelables.ParcelableArrayList;

/**
 * Created by Elf on 01.04.2016.
 * For multi select RecyclerView
 */
public class MultiChoiceMode implements Choiceable {
    private static final String STATE_CHECK_STATES = "checkStates";
    private ParcelableArrayList<Boolean> checkStates = new ParcelableArrayList<>();


    public MultiChoiceMode(int initSize) {
        //create checkStates
        for (int i = 0; i < initSize; i++) {
            addCheckedItem(i, false);
        }
    }

    @Override
    public void setItemState(int position, boolean itemState) {
        //if position not out of range set item state
        if (position < checkStates.size()) {
            checkStates.set(position, itemState);
        }
    }

    @Override
    public boolean isChecked(int position) {
        if (position >= checkStates.size()) return false;

        Boolean result = checkStates.get(position);
        if (result != null) {
            return result;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelable(STATE_CHECK_STATES, checkStates);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        if (state != null && state.getParcelable(STATE_CHECK_STATES) != null) {
            checkStates = state.getParcelable(STATE_CHECK_STATES);
            Log.d(getClass().getSimpleName(), "sssssssssssssssssssssssssssss11111" + checkStates.size() +
            checkStates.get(0).toString() + "|" +
                    checkStates.get(1).toString() + "|" +
                    checkStates.get(2).toString() + "|");
        }
    }

    @Override
    public void addCheckedItem(int position, boolean initValue) {
        //if position out of size add to the end of array else set to the position
        if (position >= checkStates.size()) {
            checkStates.add(initValue);
        }
    }

    @Override
    public void delCheckedItem(int position) {
        if (position < checkStates.size()) checkStates.remove(position);
    }

    @Override
    public void clearCheckedStates() {
        checkStates.clear();
    }


    @Override
    public int getSelectedItemPosition() {
        //Return index of first occurrence true
        for (int i = 0; i < checkStates.size(); i++) {
            if (checkStates.get(i)) {
                return i;
            }
        }

        return -1;
    }
}
