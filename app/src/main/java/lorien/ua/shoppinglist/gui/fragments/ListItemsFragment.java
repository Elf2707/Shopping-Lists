package lorien.ua.shoppinglist.gui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lorien.ua.shoppinglist.MyApplication;
import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.DbListItemMultiChoiceAdapter;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.gui.activities.EditListActivity;
import lorien.ua.shoppinglist.gui.fragments.model.list.ListModelFragment;
import lorien.ua.shoppinglist.itemdecorators.RowItemDecorator;
import lorien.ua.shoppinglist.itemdeviders.RowOffsetDevider;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 07.04.2016.
 * Items of Shopping List
 */
public class ListItemsFragment extends Fragment {
    private DbListItemMultiChoiceAdapter adapter = null;
    private RecyclerView itemsListRecyclerView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication app = (MyApplication) getActivity().getApplication();
        ShoppingListItemService shoppingListItemService = app.getShoppingListItemService();

        ListModelFragment mFragment = (ListModelFragment) getActivity()
                .getFragmentManager()
                .findFragmentByTag(EditListActivity.MODEL_LIST_FRAGMENT);

        List<ShoppingListItem> itemsList = null;
        if ((mFragment != null) && (mFragment.getShoppingList().getId() != null)) {
            itemsList = mFragment.getShoppingList().getItems();
        }

        this.adapter = new DbListItemMultiChoiceAdapter(shoppingListItemService, itemsList);

        if (savedInstanceState != null) {
            adapter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.items_of_list_fragment, container, false);
        itemsListRecyclerView = (RecyclerView) result.findViewById(R.id.listOfLists);
        itemsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemsListRecyclerView.addItemDecoration(new RowItemDecorator(getActivity(), R.drawable.row_divider));
        itemsListRecyclerView.addItemDecoration(new RowOffsetDevider(2));
        itemsListRecyclerView.setAdapter(adapter);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.ed_list_fab);

        fab.attachToRecyclerView(itemsListRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(adapter);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(adapter);
    }

    @Override
    public void onStop() {
        //Remove sticky ItemAddEvent if it was not consume
        EventBus.getDefault().removeStickyEvent(ItemAddEvent.class);
        super.onStop();
    }
}
