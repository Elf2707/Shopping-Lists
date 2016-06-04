package lorien.ua.shoppinglist.adapters;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.choiceable.impl.SingleChoiceMode;
import lorien.ua.shoppinglist.adapters.refreshable.Refreshable;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.events.item.ItemDeleteEvent;
import lorien.ua.shoppinglist.events.item.ItemMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.item.ItemUpdateEvent;
import lorien.ua.shoppinglist.events.list.ListAddEvent;
import lorien.ua.shoppinglist.events.list.ListDeleteEvent;
import lorien.ua.shoppinglist.events.list.ListDeselectedEvent;
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
        new SaveShoppingListTask().execute(event);
        EventBus.getDefault().removeStickyEvent(ListAddEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void updateListEventListener(ListUpdateEvent event) {
        new UpdateShoppingListTask().execute(event);
        EventBus.getDefault().removeStickyEvent(ListUpdateEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void itemMarkDoUndoListener(ItemMarkAsDoUndoEvent event) {
        ShoppingListItem item = event.getItem();

        //Save item to DB
        shoppingListItemService.add(item);

        ShoppingList list = getListById(item.getShoppingListId());
        int parentPosition = getListParentPosById(list.getId());
        shoppingListService.add(list);

        //Refresh items info
        list.resetItems();
        list.getItems();
        list.setIsDone(list.getDoneItemsCount() == list.getItemsCount());
        shoppingListService.add(list);

        //Refresh interface
        notifyParentItemChanged(parentPosition);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listMarkAsDoneUnEventListener(ListMarkAsDoUndoEvent event) {
        //Save it to DB in new Thread
        new MarkListAndItemsDonUndoneTask().execute(event);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void listDeleteListener(ListDeleteEvent event) {

        //Remove from item from checked list
        delCheckedItem(event.getPosition());
        new DeleteListTask().execute(event);
        EventBus.getDefault().removeStickyEvent(ListDeleteEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void addItemListener(ItemAddEvent event) {
        if (event != null && event.getItem() != null) {
            ShoppingListItem listItem = event.getItem();
            ShoppingList list = getListByChildPos(getSelectedItemPosition());

            if (list != null) {
                listItem.setShoppingListId(list.getId());
                shoppingListItemService.add(listItem);
                //Refresh list items list
                list.resetItems();
                list.getItems();
                notifyChildItemInserted(getListParentPosById(list.getId()), list.getItemsCount() - 1);
            }
        }
        EventBus.getDefault().removeStickyEvent(ItemAddEvent.class);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void updateItemListener(ItemUpdateEvent event) {
        if (event != null && event.getItem() != null) {
            ShoppingListItem listItem = event.getItem();
            ShoppingList list = getListByChildPos(getSelectedItemPosition());

            if (list != null) {
                listItem.setShoppingListId(list.getId());
                shoppingListItemService.add(listItem);

                //Refresh list items list
                shoppingListService.add(list);
                list.resetItems();
                list.getItems();

                ShoppingListItem item = shoppingListItemService.findById(listItem.getId());
                int parentPos = getListParentPosById(list.getId());
                int childPos = getChildItemPosition(list, listItem);

                if (parentPos != -1 && childPos != -1) {
                    notifyChildItemChanged(parentPos, childPos);
                    notifyParentItemChanged(parentPos);
                }
            }
        }
        //Clearing sticky event
        EventBus.getDefault().removeStickyEvent(ItemUpdateEvent.class);
    }

    private int getChildItemPosition(ShoppingList list, ShoppingListItem item) {
        int position = -1;
        List<ShoppingListItem> items = list.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return position;
    }

    private ShoppingList getListByChildPos(int childPos) {
        //Detect it is a list not an item 0 - parent 1 - child
        if (getItemViewType(childPos) == 0) {
            return (ShoppingList) ((ParentWrapper) getListItem(childPos))
                    .getParentListItem();
        }

        int parentPosition = childPos;
        while (parentPosition >= 0 && getItemViewType(--parentPosition) == 1) ;
        if (parentPosition >= 0) {
            return (ShoppingList) ((ParentWrapper) getListItem(parentPosition))
                    .getParentListItem();
        }

        return null;
    }

    private int getListPosByChildPos(int childPos) {
        //Detect it is a list not an item 0 - parent 1 - child
        if (getItemViewType(childPos) == 0) {
            return childPos;
        }

        int parentPosition = childPos;
        while (parentPosition >= 0 && getItemViewType(--parentPosition) == 1) ;

        return parentPosition;
    }

    private ShoppingList getListByAbsPosition(int position) {
        //Detect it is a list not an item 0 - parent 1 - child
        if (getItemViewType(position) != 0) {
            return null;
        }

        ShoppingList list = (ShoppingList) ((ParentWrapper) getListItem(position))
                .getParentListItem();

        return list;
    }

    private int getListParentPosById(Long id) {
        int parentPosition = -1;

        List<ShoppingList> lists = (List<ShoppingList>) getParentItemList();
        for (ShoppingList list : lists) {
            parentPosition++;

            if (list.getId().equals(id)) {
                return parentPosition;
            }
        }

        return parentPosition;
    }

    private ShoppingList getListById(Long id) {
        List<ShoppingList> lists = (List<ShoppingList>) getParentItemList();

        for (ShoppingList list : lists) {
            if (list.getId().equals(id)) {
                return list;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onItemDeleteListener(ItemDeleteEvent event) {
        //Find out child item position in parent items list
        //and parent item position for this child
        ShoppingListItem item = event.getItem();
        int parentPosition = getListParentPosById(item.getShoppingListId());
        ShoppingList list = getListById(item.getShoppingListId());
        int childPosition = getChildItemPosition(list, item);

        //Deselect item
        setItemState(parentPosition, false);

        //remove item from model and notify view
        shoppingListItemService.delete(item.getId());

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
    private class MarkListAndItemsDonUndoneTask extends AsyncTask<ListMarkAsDoUndoEvent, Void, Integer> {

        @Override
        protected Integer doInBackground(ListMarkAsDoUndoEvent... params) {
            ShoppingList list = params[0].getList();

            //save list to db
            shoppingListService.add(list);
            int parentPosition = getListParentPosById(list.getId());
            boolean isDone = list.getIsDone();

            //Mark all items as done/undone and save it to db
            List<ShoppingListItem> items = list.getItems();
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    items.get(i).setIsDone(isDone);
                    shoppingListItemService.add(items.get(i));
                }
            }

            return parentPosition;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0) {
                notifyParentItemChanged(integer);
            }
        }
    }

    //Background task add new List
    private class SaveShoppingListTask extends AsyncTask<ListAddEvent, Void, Integer> {

        @Override
        protected Integer doInBackground(ListAddEvent... params) {
            ShoppingList list = params[0].getShoppingList();

            List parentList = getParentItemList();
            parentList.add(list);

            shoppingListService.add(list);

            int position = parentList.size() - 1;
            //Get generated id and save all elements of a list
            boolean isListDone = true;
            Long listId = list.getId();

            if (listId != null) {
                List<ShoppingListItem> items = list.getItems();
                if (items.size() == 0) {
                    isListDone = false;
                }

                for (ShoppingListItem item : items) {
                    isListDone = isListDone && item.getIsDone();
                    item.setShoppingListId(listId);
                    shoppingListItemService.add(item);
                }
            }

            //Setting up list done/undone
            list.setIsDone(isListDone);

            return position;
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
            int position = getListParentPosById(list.getId());

            List parentsList = getParentItemList();
            parentsList.set(position, list);

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

                //Updating old items
                for (ShoppingListItem item : items) {
                    isListDone = isListDone && item.getIsDone();
                    item.setShoppingListId(listId);
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
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0) {
                //Remove old parent
                notifyParentItemRemoved(integer);
                //Insert new parent
                notifyParentItemInserted(integer);
                //Reselect item in MainActivity to update ActionMode
                EventBus.getDefault().post(new ListSelectedEvent(list, position));
            }
        }
    }

    //Background task delete List
    private class DeleteListTask extends AsyncTask<ListDeleteEvent, Void, Integer> {

        @Override
        protected Integer doInBackground(ListDeleteEvent... params) {
            int position = getListParentPosById(params[0].getList().getId());
            ShoppingList list = params[0].getList();

            List lists = getParentItemList();
            lists.remove(position);

            //Attaching entity to context
            shoppingListService.add(list);

            //Getting list items
            list.resetItems();
            List<ShoppingListItem> items = list.getItems();
            //delete list
            shoppingListService.delete(list.getId());

            //delete items
            for (ShoppingListItem item : items) {
                shoppingListItemService.delete(item.getId());
            }

            return position;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer >= 0) {
                notifyParentItemRemoved(integer);
                EventBus.getDefault().post(new ListDeselectedEvent());
            }
        }

    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }
}
