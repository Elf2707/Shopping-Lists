package lorien.ua.shoppinglist.adapters.choiceable.impl;

import android.os.Bundle;

import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import lorien.ua.shoppinglist.parcelables.ParcelableArrayList;

/**
 * Created by Elf on 19.04.2016.
 * Only one item can be choose at a time
 */
public class SingleChoiceMode implements Choiceable {
    private static final String STATE_CHECKED_POSITION = "checkPosition";
    private int position = -1;

    @Override
    public void setItemState(int position, boolean itemState) {
        if( itemState ){
            this.position = position;
        } else {
            this.position = -1;
        }
    }

    @Override
    public boolean isChecked(int position) {
        return this.position == position;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(STATE_CHECKED_POSITION, position);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        if( state != null ){
            position = state.getInt(STATE_CHECKED_POSITION, -1);
        }
    }

    @Override
    public void addCheckedItem(int position, boolean initValue) {
        this.position = position;
    }

    @Override
    public void delCheckedItem(int position) {
        if( this.position == position ){
            this.position = -1;
        }
    }

    @Override
    public void clearCheckedStates() {
        this.position = -1;
    }

    @Override
    public int getSelectedItemPosition() {
        return position;
    }
}
