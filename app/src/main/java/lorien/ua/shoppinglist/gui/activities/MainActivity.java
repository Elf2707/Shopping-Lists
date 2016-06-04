package lorien.ua.shoppinglist.gui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import lorien.ua.shoppinglist.MyApplication;
import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.events.common.CorrectPositionEvent;
import lorien.ua.shoppinglist.events.item.ItemDeleteEvent;
import lorien.ua.shoppinglist.events.item.ItemDeselectedEvent;
import lorien.ua.shoppinglist.events.item.ItemMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.item.ItemSelectedEvent;
import lorien.ua.shoppinglist.events.list.ListDeleteEvent;
import lorien.ua.shoppinglist.events.list.ListDeselectedEvent;
import lorien.ua.shoppinglist.events.list.ListMarkAsDoUndoEvent;
import lorien.ua.shoppinglist.events.list.ListSelectedEvent;
import ua.lorien.shoppinglist.model.dao.ShoppingList;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback, AppBarLayout.OnOffsetChangedListener {
    public static final String APP_BAR_COLLAPSED_STATE = "APP_BAR_COLLAPSED_STATE";
    public static final String CURRENT_SEL_SHOPPING_LIST = "CURRENT_SEL_LIST";
    public static final String CURRENT_SEL_LIST_ITEM = "CURRENT_SEL_LIST_ITEM";
    public static final String CURRENT_SEL_POSITION = "CURRENT_SEL_POSITION";

    private static final int APP_BAR_EXPAND_OFFSET = -340;

    private ActionMode actionMode = null;
    private ShoppingList selectedShoppingList = null;
    private ShoppingListItem selectedListItem = null;
    private int selectListPosition = -1;
    private boolean isAppBarCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tinting statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.main_appbar);
        appBar.addOnOffsetChangedListener(this);
        appBar.setExpanded(!isAppBarCollapsed, true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListButtonListener(view);
            }
        });

        //Restore data
        if (savedInstanceState != null) {
            selectedShoppingList =
                    (ShoppingList) savedInstanceState.getSerializable(CURRENT_SEL_SHOPPING_LIST);
            selectedListItem =
                    (ShoppingListItem) savedInstanceState.getSerializable(CURRENT_SEL_LIST_ITEM);
            selectListPosition = savedInstanceState.getInt(CURRENT_SEL_POSITION, -1);

            if (selectListPosition != -1) {
                //Where was selected list or item restore action mod
                actionMode = startSupportActionMode(this);
                if (selectedShoppingList != null) {
                    actionMode.setTitle(selectedShoppingList.getName());
                } else if (selectedListItem != null) {
                    actionMode.setTitle(selectedListItem.getName());
                }
            }

            isAppBarCollapsed = savedInstanceState.getBoolean(APP_BAR_COLLAPSED_STATE);
            appBar.setExpanded(!isAppBarCollapsed, true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Preferences.class));
        }

        return true;
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
        if (prefs != null) {
            //Get list layout to make setKeepScreen on
            LinearLayout listLayout = (LinearLayout) findViewById(R.id.main_act_list_layout);
            listLayout.setKeepScreenOn(prefs.getBoolean(MyApplication.PREF_KEEP_SCREEN_ON, false));
        }
    }


    @SuppressWarnings("unused")
    @Subscribe
    public void correctSelPositionEvent(CorrectPositionEvent event) {
        selectListPosition = event.getPosition();
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void onListSelected(ListSelectedEvent event) {
        //Clear item selected if it was not clear by
        //deselect event
        selectedListItem = null;

        selectedShoppingList = event.getShoppingList();
        selectListPosition = event.getPosition();

        //Start action mode
        startActionMode();
        actionMode.setTitle(selectedShoppingList.getName());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onListItemSelected(ItemSelectedEvent event) {
        //Clear list selected if it was not clear by
        //deselect event
        selectedShoppingList = null;

        selectedListItem = event.getItem();
        selectListPosition = event.getPosition();

        //Start action mode
        startActionMode();
        actionMode.setTitle(selectedListItem.getName());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onListDeselected(ListDeselectedEvent event) {
        selectedShoppingList = null;
        selectListPosition = -1;

        //Stop action mode
        stopActionMode();
    }


    @SuppressWarnings("unused")
    @Subscribe
    public void onListItemDeselected(ItemDeselectedEvent event) {
        selectedListItem = null;
        selectListPosition = -1;

        //Stop action mode
        stopActionMode();
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

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_list_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem doneItem = menu.findItem(R.id.main_act_list_mark_done);
        MenuItem addItemItem = menu.findItem(R.id.main_act_list_add_item);

        //Customizing menu depends on List selected or ListItem selected
        if (selectedListItem != null) {
            //customizing fo shopping list item
            addItemItem.setVisible(false);

            if (selectedListItem.getIsDone()) {
                doneItem.setIcon(R.drawable.ic_done_black);
            } else {
                doneItem.setIcon(R.drawable.ic_done_white);
            }
        }

        if (selectedShoppingList != null) {
            //customizing fo shopping list
            addItemItem.setVisible(true);

            if (selectedShoppingList.getIsDone()) {
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
            case R.id.main_act_list_delete:
                AlertDialog doneDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_main_delete_title)
                        .setIcon(R.drawable.ic_warning_black)
                        .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSelectedElement();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null).create();
                doneDialog.show();

                actionMode.finish();
                break;

            case R.id.main_act_list_edit:
                Intent i = null;
                if (selectedShoppingList != null) {
                    i = new Intent(this, EditListActivity.class);
                    i.putExtra(CURRENT_SEL_SHOPPING_LIST, selectedShoppingList);
                }

                if (selectedListItem != null) {
                    i = new Intent(this, EditItemActivity.class);
                    i.putExtra(EditItemActivity.RECEIVE_LIST_ITEM, selectedListItem);
                    i.putExtra(EditItemActivity.RECEIVE_ITEM_POSITION, selectListPosition);
                }

                if (i != null) {
                    i.putExtra(CURRENT_SEL_POSITION, selectListPosition);
                    startActivity(i);
                }
                break;

            case R.id.main_act_list_add_item:
                i = new Intent(this, EditItemActivity.class);
                startActivity(i);
                break;

            case R.id.main_act_list_mark_done:
                if (selectedShoppingList != null) {
                    doneDialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_list_done_title)
                            .setIcon(R.drawable.ic_warning_black)
                            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setListAsDone();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).create();
                    doneDialog.show();
                }

                if (selectedListItem != null) {
                    if (selectedListItem.getIsDone()) {
                        selectedListItem.setIsDone(false);
                    } else {
                        selectedListItem.setIsDone(true);
                    }

                    EventBus.getDefault().post(
                            new ItemMarkAsDoUndoEvent(selectListPosition, selectedListItem, null));
                }

                actionMode.invalidate();
                break;
        }
        return true;
    }

    private void deleteSelectedElement() {
        //Detect that we deleting list or item
        if (selectedListItem != null) {
            //Deleting item
            EventBus.getDefault().post(new ItemDeleteEvent(selectListPosition, selectedListItem));
        } else if (selectedShoppingList != null) {
            EventBus.getDefault().post(new ListDeleteEvent(selectedShoppingList, selectListPosition));
        }
        clearSelectedItems();
    }

    private void setListAsDone() {
        if (selectedShoppingList.getIsDone()) {
            selectedShoppingList.setIsDone(false);
        } else {
            selectedShoppingList.setIsDone(true);
        }

        EventBus.getDefault().post(
                new ListMarkAsDoUndoEvent(selectListPosition, selectedShoppingList));

        actionMode.invalidate();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }

    private void addListButtonListener(View view) {
        Intent i = new Intent(this, EditListActivity.class);
        startActivity(i);
    }

    private void clearSelectedItems() {
        selectedListItem = null;
        selectedShoppingList = null;
        selectListPosition = -1;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_SEL_SHOPPING_LIST, selectedShoppingList);
        outState.putSerializable(CURRENT_SEL_LIST_ITEM, selectedListItem);
        outState.putInt(CURRENT_SEL_POSITION, selectListPosition);
        outState.putBoolean(APP_BAR_COLLAPSED_STATE, isAppBarCollapsed);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset > APP_BAR_EXPAND_OFFSET) {
            //Expanded state
            isAppBarCollapsed = false;
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        } else {
            isAppBarCollapsed = true;
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
}
