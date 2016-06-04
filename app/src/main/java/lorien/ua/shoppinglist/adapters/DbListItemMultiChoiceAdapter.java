package lorien.ua.shoppinglist.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import lorien.ua.shoppinglist.adapters.choiceable.impl.MultiChoiceMode;
import lorien.ua.shoppinglist.adapters.refreshable.Refreshable;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.events.item.ItemDeleteEvent;
import lorien.ua.shoppinglist.events.item.ItemDeselectedEvent;
import lorien.ua.shoppinglist.events.item.ItemMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.item.ItemUpdateEvent;
import lorien.ua.shoppinglist.events.item.ItemsRemoveChecked;
import lorien.ua.shoppinglist.events.list.ListRefreshEvent;
import lorien.ua.shoppinglist.holders.ItemHolder;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 08.04.2016.
 * Adapter for Recycler View on Edit shopping list activity
 */
public class DbListItemMultiChoiceAdapter extends RecyclerView.Adapter<ItemHolder>
        implements Choiceable, Refreshable {
    private Choiceable choiceMode = null;
    private List<ShoppingListItem> listItems = new ArrayList<>();
    private ShoppingListItemService itemsService = null;

    public DbListItemMultiChoiceAdapter(ShoppingListItemService itemsService, List<ShoppingListItem> listItems) {
        int itemsCount = 0;

        if (listItems != null) {
            this.listItems.addAll(listItems);
            itemsCount = listItems.size();
        }

        this.choiceMode = new MultiChoiceMode(itemsCount);
        this.itemsService = itemsService;
    }

    public List<ShoppingListItem> getAllItems() {
        return listItems;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(this, LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bindModel(listItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        }
        return 0;
    }

    @Override
    public int getSelectedItemPosition() {
        return choiceMode.getSelectedItemPosition();
    }

    public void setItemState(int position, boolean initValue) {
        choiceMode.setItemState(position, initValue);
    }

    public boolean isChecked(int position) {
        return choiceMode.isChecked(position);
    }

    public void addCheckedItem(int position, boolean initValue) {
        choiceMode.addCheckedItem(position, initValue);
    }

    @Override
    public void delCheckedItem(int position) {
        choiceMode.delCheckedItem(position);
    }

    @Override
    public void clearCheckedStates() {
        choiceMode.clearCheckedStates();
    }

    public void onSaveInstanceState(Bundle state) {
        choiceMode.onSaveInstanceState(state);
    }

    public void onRestoreInstanceState(Bundle state) {
        choiceMode.onRestoreInstanceState(state);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void addItemListener(ItemAddEvent event) {
        if (event != null && event.getItem() != null) {
            choiceMode.addCheckedItem(listItems.size(), false);
            listItems.add(event.getItem());
            notifyItemInserted(listItems.size() - 1);
        }

        EventBus.getDefault().post(new ListRefreshEvent());
        EventBus.getDefault().removeStickyEvent(ItemAddEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void updateListItemListener(ItemUpdateEvent event) {
        if (event != null && event.getItem() != null) {
            listItems.set(event.getPosition(), event.getItem());
            notifyItemChanged(event.getPosition());
        }

        EventBus.getDefault().removeStickyEvent(ItemUpdateEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void deleteAllSelItemsListener(ItemsRemoveChecked event) {
        ShoppingList shoppingList = event.getShoppingList();
        //Delete checked shopping list
        int i = 0;
        while (i < listItems.size()) {
            if (isChecked(i)) {
                listItems.remove(i);

                //Remove from modeling shopping list
                if (shoppingList != null) shoppingList.removeItem(i);

                //Remove from item from checked list
                delCheckedItem(i);
                notifyItemRemoved(i);
            } else {
                //Go to the next element
                i++;
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void deleteItemListener(ItemDeleteEvent event) {
        ShoppingListItem item = event.getItem();
        int position = event.getPosition();

        //Delete checked shopping list
        delCheckedItem(position);
        listItems.remove(position);
        itemsService.delete(item.getId());

        notifyItemRemoved(position);
        EventBus.getDefault().post(new ItemDeselectedEvent(event.getPosition()));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void itemMarkDoUndoListener(ItemMarkAsDoUndoEvent event) {
        int position = event.getPosition();

        //We do not save it to db
        //till user press save whole list button
        //Refresh interface child item
        notifyItemChanged(position);
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }
}
