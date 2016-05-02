package lorien.ua.shoppinglist.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lorien.ua.shoppinglist.MyApplication;
import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.DbShoppingListAdapterExpandable;
import lorien.ua.shoppinglist.itemdecorators.RowItemDecorator;
import lorien.ua.shoppinglist.itemdeviders.RowOffsetDevider;
import lorien.ua.shoppinglist.service.ShoppingListItemService;
import lorien.ua.shoppinglist.service.ShoppingListService;
import ua.lorien.shoppinglist.model.dao.ShoppingList;


/**
 * List of shopping lists
 */
public class ListOfListsFragment extends Fragment {

    private RecyclerView listOfListsView = null;
    private DbShoppingListAdapterExpandable adapter = null;

    //Methods for fragment
    public ListOfListsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        MyApplication app = (MyApplication) getActivity().getApplication();
        ShoppingListService shoppingListService = app.getShoppingListService();
        ShoppingListItemService shoppingListItemService = app.getShoppingListItemService();

        List<ShoppingList> existentLists = shoppingListService.findAll();
        //Load all lists elements
        for (ShoppingList list : existentLists) {
            List l = list.getItems();
        }

        //Test for empty list findAll return Collections.EMTY_LIST
        //So we replace it with ArrayList
        existentLists = (existentLists.size() == 0) ? new ArrayList<ShoppingList>() : existentLists;
        adapter = new DbShoppingListAdapterExpandable(shoppingListService,
                shoppingListItemService, existentLists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.list_of_lists_fragment, container, false);

        listOfListsView = (RecyclerView) result.findViewById(R.id.listOfLists);
        listOfListsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfListsView.addItemDecoration(new RowItemDecorator(getActivity(), R.drawable.row_divider));
        listOfListsView.addItemDecoration(new RowOffsetDevider(2));
        listOfListsView.setAdapter(adapter);

        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.main_fab);
        fab.attachToRecyclerView(listOfListsView);
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
}
