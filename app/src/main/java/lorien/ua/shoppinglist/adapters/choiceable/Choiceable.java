package lorien.ua.shoppinglist.adapters.choiceable;

import android.os.Bundle;

/**
 * Created by Elf on 01.04.2016.
 * For allow checking row in recyclerview
 */
public interface Choiceable {
    void setItemState( int position, boolean itemState );

    boolean isChecked(int position);

    void onSaveInstanceState(Bundle state);

    void onRestoreInstanceState(Bundle state);

    void addCheckedItem( int position, boolean initValue );

    void delCheckedItem( int position );

    void clearCheckedStates();

    int getSelectedItemPosition();

}
