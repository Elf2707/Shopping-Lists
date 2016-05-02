package lorien.ua.shoppinglist.adapters;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.choiceable.impl.SingleChoiceMode;
import lorien.ua.shoppinglist.adapters.refreshable.Refreshable;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.events.item.ItemDeleteEvent;
import lorien.ua.shoppinglist.events.item.ItemMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.list.ListAddEvent;
import lorien.ua.shoppinglist.events.list.ListDeleteEvent;
import lorien.ua.shoppinglist.events.list.ListMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.list.ListSelectedEvent;
import lorien.ua.shoppinglist.events.list.ListUpdateEvent;
import lorien.ua.shoppinglist.holders.ItemHolder;
import lorien.ua.shoppinglist.holders.ListHolder;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import lorien.ua.shoppinglist.service.ShoppingListService;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 21.03.2016.
 * DataBase GreenDAO adapter for main RecyclerView
 */
public class DbShoppingListAdapterExpandable extends
        ExpandableSingleChoiceAdapter<ListHolder, ItemHolder> implements Refreshable {

    //Db Shopping List Service
    private ShoppingListService shoppingListService = null;
    private ShoppingListItemService shoppingListItemService = null;

    public DbShoppingListAdapterExpandable(ShoppingListService shoppingListService,
                                           ShoppingListItemService shoppingListItemService,
                                           List<ShoppingList> shoppingLists) {
        super(new SingleChoiceMode(), shoppingLists);

        this.shoppingListService = shoppingListService;
        this.shoppingListItemService = shoppingListItemService;
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void addListEventListener(ListAddEvent event) {
        Log.d(getClass().getSimpleName(), "sssssssssssssssssssssssssssssssssAdd");
        new SaveShoppingListTask().execute(event);
        EventBus.getDefault().removeStickyEvent(ListAddEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void updateListEventListener(ListUpdateEvent event) {
        Log.d(getClass().getSimpleName(), "sssssssssssssssssssssssssssssssssUpdate");
        new UpdateShoppingListTask().execute(event);
        EventBus.getDefault().removeStickyEvent(ListUpdateEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void itemMarkDoUndoListener(ItemMarkAsDoUndoEvent event) {
        int position = event.getPosition();
        int childPosition = event.getPosition();


        ShoppingListItem item = event.getItem();

        //Save it to DB
        shoppingListItemService.add(item);

        //find parent item position to update
        //interface 0 - parent list, 1 - child item
        while ((position >= 0) && getItemViewType(--position) != 0) {
        }

        ShoppingList list = (ShoppingList) getParentItemList().get(position);

        //Refresh items info
        list.resetItems();
        list.getItems();
        list.setIsDone(list.getDoneItemsCount() == list.getItemsCount());
        shoppingListService.add(list);

        //Refresh interface
        //child
        notifyItemChanged(childPosition);
        //parent
        notifyParentItemChanged(position);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listMarkAsDoneUnEventListener(ListMarkAsDoUndoEvent event) {
        //Save it to DB in new Thread
        new MarkListAndItemsDonUndoneTask().execute(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listDeleteListener(ListDeleteEvent event) {
        int delPosition = event.getPosition();

        ShoppingList delShoppingList = (ShoppingList) getParentItemList().get(delPosition);

        //Remove from db
        shoppingListService.delete(delShoppingList.getId());

        //Remove from Parent list
        getParentItemList().remove(delPosition);

        //Remove from item from checked list
        delCheckedItem(delPosition);

        notifyParentItemRemoved(delPosition);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void addItemToList(ItemAddEvent event) {
        if (event != null && event.getItem() != null) {
            ShoppingListItem listItem = event.getItem();
            ShoppingList list = (ShoppingList) getParentItemList().get(getSelectedItemPosition());

            listItem.setShoppingListId(list.getId());
            shoppingListItemService.add(listItem);
            //Refresh list items list
            list.resetItems();
            list.getItems();

            notifyChildItemInserted(getSelectedItemPosition(), list.getItemsCount() - 1);

            //Clearing sticky event
            EventBus.getDefault().removeStickyEvent(ItemAddEvent.class);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onItemDeleteListener(ItemDeleteEvent event) {
        //Find out child item position in parent items list
        //and parent item position for this child
        int childPosition = 0;
        int parentPosition = event.getPosition();

        //Deselect item
        setItemState(parentPosition, false);

        //0 - parent list, 1 - child item
        while (getItemViewType(--parentPosition) != 0) {
            childPosition++;
        }

        //remove item from model and notify view
        shoppingListItemService.delete(event.getItem().getId());
        ShoppingList list = (ShoppingList) getParentItemList().get(parentPosition);
        list.resetItems();
        list.getItems();
        notifyChildItemRemoved(parentPosition, childPosition);
    }

    @Override
    public ListHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {

        return new ListHolder(this, LayoutInflater.from(parentViewGroup.getContext())
                .inflate(R.layout.list_row, parentViewGroup, false));
    }

    @Override
    public ItemHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        return new ItemHolder(this, LayoutInflater.from(childViewGroup.getContext())
                .inflate(R.layout.child_row, childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(ListHolder parentViewHolder, int position,
                                       ParentListItem parentListItem) {
        if (parentListItem != null && parentListItem instanceof ShoppingList) {
            parentViewHolder.bindModel((ShoppingList) parentListItem);
        }
    }

    @Override
    public void onBindChildViewHolder(ItemHolder childViewHolder, int position, Object listItem) {
        if (listItem != null && listItem instanceof ShoppingListItem) {
            childViewHolder.bindModel((ShoppingListItem) listItem);
        }
    }

    //Background thread for marking all items of list don andone
    private class MarkListAndItemsDonUndoneTask extends AsyncTask<ListMarkAsDoUndoEvent, Integer, Void> {

        @Override
        protected Void doInBackground(ListMarkAsDoUndoEvent... params) {
            ShoppingList list = params[0].getList();
            int position = params[0].getPosition();
            boolean isDone = list.getIsDone();

            //save list to db
            shoppingListService.add(list);
            publishProgress(new Integer(position));

            //Mark all items as done/undone and save it to db
            List<ShoppingListItem> items = list.getItems();
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    items.get(i).setIsDone(isDone);
                    shoppingListItemService.add(items.get(i));
                    publishProgress(new Integer[]{position, i});
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //what we should update parent view or child view
            if (values.length > 1) {
                notifyChildItemChanged(values[0], values[1]);
            } else {
                notifyParentItemChanged(values[0]);
            }
        }
    }

    //Background task add new List
    private class SaveShoppingListTask extends AsyncTask<ListAddEvent, Integer, Integer> {

        @Override
        protected Integer doInBackground(ListAddEvent... params) {
            ShoppingList list = params[0].getShoppingList();

            List parentList = getParentItemList();
            parentList.add(list);

            int position = parentList.size() - 1;

            shoppingListService.add(list);

            //Get generated id and save all elements of a list
            boolean isListDone = true;
            Long listId = list.getId();

            if (listId != null) {
                List<ShoppingListItem> items = list.getItems();
                if (items.size() == 0) {
                    isListDone = false;
                }

                ShoppingListItem item;
                for (int i = 0; i < items.size(); i++) {
                    item = items.get(i);
                    isListDone = isListDone && item.getIsDone();

                    //Detect if it is new item just add third parameter 1
                    //to detect notifyUpdate or notifyInsert to call
                    if (item.getShoppingListId() == null) {
                        item.setShoppingListId(listId);
                        publishProgress(new Integer[]{position, i, 1});
                    } else {
                        //Item already exists
                        publishProgress(new Integer[]{position, i});
                    }
                    shoppingListItemService.add(item);
                }
            }

            //Setting up list done/undone
            list.setIsDone(isListDone);

            return position;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            //When it is first child first update parent
            if (values[0] >= 0) {
                if (values[1] == 0) {
                    notifyParentItemInserted(values[0]);
                }

                if (values.length == 3) {
                    notifyChildItemInserted(values[0], values[1]);
                } else {
                    notifyChildItemChanged(values[0], values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0) {
                notifyParentItemInserted(integer);
            }
        }
    }

    private class UpdateShoppingListTask extends AsyncTask<ListUpdateEvent, Integer, Integer> {
        private ShoppingList list = null;
        private int position = -1;

        @Override
        protected Integer doInBackground(ListUpdateEvent... params) {
            ShoppingList list = params[0].getShoppingList();
            int position = params[0].getPosition();

            List parentList = getParentItemList();
            parentList.set(position, list);

            shoppingListService.add(list);

            this.list = list;
            this.position = position;

            //Get generated id and save all elements of a list
            boolean isListDone = true;
            Long listId = list.getId();
            if (listId != null) {
                List<ShoppingListItem> items = list.getItems();
                if (items.size() == 0) {
                    isListDone = false;
                }

                ShoppingListItem item;
                for (int i = 0; i < items.size(); i++) {
                    item = items.get(i);
                    isListDone = isListDone && item.getIsDone();
                    item.setShoppingListId(listId);
                    shoppingListItemService.add(item);
                    publishProgress(new Integer[]{position, i});
                }
            }

            //Seting up list done/undone
            list.setIsDone(isListDone);

            return position;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            //When it is first child first update parent
            if (values[0] >= 0) {
                if (values[1] == 0) {
                   // notifyParentItemChanged(values[0]);
                }

                notifyChildItemChanged(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0) {
                notifyParentItemChanged(position);
                //Reselect item in MainActivity to update ActionMode
                EventBus.getDefault().post(new ListSelectedEvent(list, position));
            }
        }
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }
}
