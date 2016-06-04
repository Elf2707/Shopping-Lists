package lorien.ua.shoppinglist.gui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.events.item.ItemResetDataEvent;
import lorien.ua.shoppinglist.gui.activities.EditItemActivity;
import lorien.ua.shoppinglist.gui.fragments.model.item.ItemModelFragment;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 09.04.2016.
 * List item edit fragment
 */
public class ItemEditFragment extends Fragment {

    EditText title = null;
    EditText description = null;
    EditText amount = null;
    EditText price = null;
    CheckBox done = null;
    ShoppingListItem item = null;

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.item_fragment, container, false);

        title = (EditText) result.findViewById(R.id.list_item_title);
        description = (EditText) result.findViewById(R.id.list_item_description);
        amount = (EditText) result.findViewById(R.id.list_item_amount);
        price = (EditText) result.findViewById(R.id.list_item_price);
        done = (CheckBox) result.findViewById(R.id.list_item_done);

        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Find model fragment and populate
        //interface if fragment exists
        ItemModelFragment itemModelFragment = (ItemModelFragment) getFragmentManager()
                .findFragmentByTag(EditItemActivity.LIST_ITEM_MODEL);
        if (itemModelFragment != null) {
            populateViewFromItem(itemModelFragment.getShoppingListItem());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void resetAllFilledData(ItemResetDataEvent event) {
        title.setText("");
        description.setText("");
        amount.setText("");
        price.setText("");
        done.setChecked(false);
    }

    private void populateViewFromItem(ShoppingListItem item) {
        if (item != null) {
            this.item = item;
            title.setText((item.getName() != null) ? item.getName() : "");
            description.setText((item.getDescription() != null) ? item.getDescription() : "");
            amount.setText((item.getAmount() != null) ? item.getAmount() : "");
            price.setText((item.getPrice() != null) ? String.valueOf(item.getPrice()) : "");
            done.setChecked(item.getIsDone() != null ? item.getIsDone() : false);
        }
    }

    public ShoppingListItem getFilledShoppingListItem() {
        if (item == null) {
            item = new ShoppingListItem();
        }

        //Title
        if (title != null && title.getText().length() > 0) {
            item.setName(title.getText().toString());
        } else {
            item.setName(getString(R.string.item_title_unknown));
        }

        //Description
        if (description != null && description.getText().length() > 0) {
            item.setDescription(description.getText().toString());
        } else {
            item.setDescription(getString(R.string.item_description_unknown));
        }

        //Amount
        if (amount != null && amount.getText().length() > 0) {
            item.setAmount(amount.getText().toString());
        } else {
            item.setAmount(getString(R.string.item_amount_unknown));
        }

        //Price
        if (price != null && price.getText().length() > 0) {
            item.setPrice(Double.parseDouble(price.getText().toString()));
        } else {
            Log.d(getClass().getSimpleName(), "ssssssssssssssssssssssssssssssssssssss111111");
        }

        if (done.isChecked()) {
            item.setIsDone(true);
        } else {
            item.setIsDone(false);
        }

        return item;
    }
}
