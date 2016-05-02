package lorien.ua.shoppinglist.gui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    private void populateViewFromItem(ShoppingListItem item) {
        if (item != null) {

            title.setText((item.getName() != null) ? item.getName() : "");
            description.setText((item.getDescription() != null) ? item.getDescription() : "");
            amount.setText((item.getAmount() != null) ? item.getAmount() : "");
            price.setText((item.getPrice() != null) ? String.valueOf(item.getPrice()) : "");
        }
    }

    public ShoppingListItem getFilledShoppingListItem() {
        if (item == null) {
            item = new ShoppingListItem();
        }

        //Title
        if (title != null && !title.getText().equals("")) {
            item.setName(title.getText().toString());
        } else {
            item.setName(getString(R.string.item_title_unknown));
        }

        //Description
        if (description != null && !description.getText().equals("")) {
            item.setDescription(description.getText().toString());
        } else {
            item.setDescription(getString(R.string.item_description_unknown));
        }

        //Amount
        if (amount != null && !amount.getText().equals("")) {
            item.setAmount(amount.getText().toString());
        } else {
            item.setAmount(getString(R.string.item_amount_unknown));
        }

        //Price
        if (price != null && !price.getText().equals("")) {
            item.setPrice(Double.parseDouble(price.getText().toString()));
        } else {
            item.setPrice(Double.parseDouble(getString(R.string.item_amount_unknown)));
        }

        if (item.getIsDone() == null) {
            item.setIsDone(false);
        }

        return item;
    }
}
