package lorien.ua.shoppinglist.gui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Iterator;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.events.item.ItemDeleteEvent;
import lorien.ua.shoppinglist.events.item.ItemDeselectedEvent;
import lorien.ua.shoppinglist.events.item.ItemMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.item.ItemSelectedEvent;
import lorien.ua.shoppinglist.events.item.ItemsRemoveChecked;
import lorien.ua.shoppinglist.events.list.ListAddEvent;
import lorien.ua.shoppinglist.events.list.ListUpdateEvent;
import lorien.ua.shoppinglist.gui.fragments.ListItemsFragment;
import lorien.ua.shoppinglist.gui.fragments.model.list.ListModelFragment;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

public class EditListActivity extends AppCompatActivity implements ActionMode.Callback {
    public static final String SELECTED_POS = "ED_LIST_SELECTED_POS";
    public static final String MODEL_LIST_FRAGMENT = "LIST_FRAGMENT";
    public static final String ITEMS_OF_LIST = "ITEMS_OF_SHOPPING_LIST";
    public static final String SELECTED_ITEMS_MAP = "ED_LIST_SELECTED_MAP";

    private ListModelFragment mFragment = null;
    private int selectedItemPosition = -1;
    private ShoppingListItem selectedListItem = null;

    private EditText title = null;
    private TextView doneItemsCount = null;
    private TextView leftItemsCount = null;
    private TextView shoppingListTotal = null;
    private ActionMode actionMode = null;

    private HashMap<Integer, ShoppingListItem> selectedItemsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //If model fragment not create do it
        mFragment = (ListModelFragment) getFragmentManager()
                .findFragmentByTag(MODEL_LIST_FRAGMENT);
        if (mFragment == null) {
            mFragment = new ListModelFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(mFragment, MODEL_LIST_FRAGMENT)
                    .commit();

            //Look to the extras from the Intent if where was lis
            ShoppingList list = (ShoppingList) getIntent()
                    .getSerializableExtra(MainActivity.CURRENT_SEL_SHOPPING_LIST);
            int listPosition = getIntent().getIntExtra(MainActivity.CURRENT_SEL_POSITION, -1);
            if (list != null) {
                mFragment.setShoppingList(list);
                mFragment.setPosition(listPosition);
            }
        }

        setContentView(R.layout.activity_edit_list);

        if (savedInstanceState != null) {
            selectedItemPosition = savedInstanceState.getInt(SELECTED_POS, -1);
            selectedItemsMap = (HashMap<Integer, ShoppingListItem>)
                    savedInstanceState.getSerializable(SELECTED_ITEMS_MAP);
            selectedListItem = selectedItemsMap.get(selectedItemPosition);

            if (selectedItemPosition != -1 && selectedListItem != null) {
                actionMode = startSupportActionMode(this);
                actionMode.setTitle(selectedListItem.getName());
            }
        }

        //Put items_of_list_fragment to the right place
        ListItemsFragment listItems = new ListItemsFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.list_items, listItems, ITEMS_OF_LIST).commit();

        title = (EditText) findViewById(R.id.shopping_list_name);
        doneItemsCount = (TextView) findViewById(R.id.shopping_list_items_done);
        leftItemsCount = (TextView) findViewById(R.id.shopping_list_items_left);
        shoppingListTotal = (TextView) findViewById(R.id.shopping_list_total);

        populateGuiFromModelFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ed_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingList list = mFragment.getShoppingList();
                //Get title from UI
                if (list.getId() == null) {
                    list.setIsDone(false);
                }
                list.setName(title.getText().toString());

                if (list.getId() == null) {
                    EventBus.getDefault().postSticky(new ListAddEvent(list, mFragment.getPosition()));
                } else {
                    EventBus.getDefault().postSticky(new ListUpdateEvent(list, mFragment.getPosition()));
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.ed_list_del_items:
                delItemButtonListener();
                return true;

            case R.id.ed_list_add_item:
                addItemButtonListener();
                return true;


            case R.id.ed_list_del_list:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SELECTED_ITEMS_MAP, selectedItemsMap);
        outState.putInt(SELECTED_POS, selectedItemPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        //Refresh the list on main activity
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    private void populateGuiFromModelFragment() {
        ShoppingList shoppingList = mFragment.getShoppingList();
        if (shoppingList.getName() != null) {
            title.setText(shoppingList.getName());
        }

        int doneItems = shoppingList.getDoneItemsCount();
        doneItemsCount.setText(String.valueOf(doneItems));
        leftItemsCount.setText(String.valueOf(shoppingList.getItemsCount() - doneItems));
        shoppingListTotal.setText(String.valueOf(shoppingList.getTotalPrice()));
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void addListItem(ItemAddEvent event) {
        if (event != null && event.getItem() != null) {
            if (event.isNewItem()) {
                mFragment.getShoppingList().addItem(event.getItem());
            } else {
                mFragment.getShoppingList().setItem(event.getPosition(), event.getItem());
            }
        }
    }

    private void delItemButtonListener() {
        EventBus.getDefault().post(new ItemsRemoveChecked(mFragment.getShoppingList()));
    }

    private void addItemButtonListener() {
        Intent i = new Intent(this, EditItemActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_actions, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem doneItem = menu.findItem(R.id.items_frgmt_item_done);

        //Customizing menu depends on List selected or ListItem selected
        if (selectedListItem != null) {

            //customizing fo shopping list item
            if (selectedListItem.getIsDone()) {
                doneItem.setIcon(R.drawable.ic_done_black);
            } else {
                doneItem.setIcon(R.drawable.ic_done_white);
            }
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.items_frgmt_item_delete:
                //Detect that we deleting list or item
                if (selectedListItem != null) {
                    //Deleting item
                    EventBus.getDefault().post(new ItemDeleteEvent(selectedItemPosition, selectedListItem));
                }

                selectedItemPosition = -1;
                selectedListItem = null;
                actionMode.finish();
                break;

            case R.id.items_frgmt_item_edit:
                Intent i;
                if (selectedListItem != null) {
                    i = new Intent(this, EditItemActivity.class);
                    i.putExtra(EditItemActivity.RECEIV_LIST_ITEM, selectedListItem);
                    i.putExtra(EditItemActivity.RECEIV_ITEM_POSITION, selectedItemPosition);
                    startActivity(i);
                }
                break;

            case R.id.items_frgmt_item_done:
                if (selectedListItem != null) {
                    if (selectedListItem.getIsDone()) {
                        selectedListItem.setIsDone(false);
                    } else {
                        selectedListItem.setIsDone(true);
                    }

                    EventBus.getDefault().post(
                            new ItemMarkAsDoUndoEvent(selectedItemPosition,
                                    selectedListItem, mFragment.getShoppingList()));
                }

                actionMode.invalidate();
                break;
        }

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onListItemSelected(ItemSelectedEvent event) {
        selectedListItem = event.getItem();
        selectedItemPosition = event.getPosition();

        selectedItemsMap.put(selectedItemPosition, selectedListItem);

        //Start action mode in case if where was not more then one
        //item selected
        if (selectedItemsMap.size() == 1) {
            startActionMode();
            actionMode.setTitle(selectedListItem.getName());
        } else {
            if (actionMode != null) {
                actionMode.finish();
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onListItemDeselected(ItemDeselectedEvent event) {
        selectedItemPosition = event.getPosition();

        selectedItemsMap.remove(selectedItemPosition);

        //Start action mode in case if where was not more then one
        //item selected
        if (selectedItemsMap.size() == 1) {
            Iterator<Integer> iterator = selectedItemsMap.keySet().iterator();
            selectedItemPosition = iterator.next();
            selectedListItem = selectedItemsMap.get(selectedItemPosition);

            startActionMode();
            actionMode.setTitle(selectedListItem.getName());
        } else {
            stopActionMode();
        }
    }

    private void startActionMode() {
        //Start action mode
        if (actionMode == null) {
            actionMode = startSupportActionMode(this);
        } else {
            actionMode.invalidate();
        }
    }

    private void stopActionMode() {
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }
}
