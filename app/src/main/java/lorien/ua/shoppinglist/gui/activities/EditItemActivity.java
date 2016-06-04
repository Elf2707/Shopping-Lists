package lorien.ua.shoppinglist.gui.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.events.item.ItemAddEvent;
import lorien.ua.shoppinglist.events.item.ItemUpdateEvent;
import lorien.ua.shoppinglist.gui.fragments.ItemEditFragment;
import lorien.ua.shoppinglist.gui.fragments.model.item.ItemModelFragment;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

public class EditItemActivity extends AppCompatActivity {
    public static final String RECEIVE_LIST_ITEM = "ED_ITEM_LIST_ITEM";
    public static final String RECEIVE_ITEM_POSITION = "ED_ITEM_LIST_ITEM_POSITION";

    public static final String LIST_ITEM_MODEL = "LIST_ITEM_MODEL";
    public static final String LIST_ITEM_MAIN_FRAG = "LIST_ITEM_MAIN_FRAG";

    private ItemModelFragment mItemFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tinting systembar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_edit_item);

        if (mItemFragment == null) {
            mItemFragment = new ItemModelFragment();
            getFragmentManager().beginTransaction()
                    .add(mItemFragment, LIST_ITEM_MODEL)
                    .commit();

            //Getting extras
            ShoppingListItem item = (ShoppingListItem) getIntent()
                    .getSerializableExtra(RECEIVE_LIST_ITEM);
            if (item != null) {
                mItemFragment.setShoppingListItem(item);
            }

            int position = getIntent().getIntExtra(RECEIVE_ITEM_POSITION, -1);
            mItemFragment.setPosition(position);

        }


        ItemEditFragment itemEditFragment =
                (ItemEditFragment) getFragmentManager().findFragmentByTag(LIST_ITEM_MAIN_FRAG);

        if (itemEditFragment == null) {
            itemEditFragment = new ItemEditFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_frag_place, itemEditFragment, LIST_ITEM_MAIN_FRAG)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ed_item_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addItemButtonListener(view);
                }
            });
        }

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    private void addItemButtonListener(View view) {
        //Get fragment with data
        ItemEditFragment editFragment = (ItemEditFragment)
                getFragmentManager().findFragmentByTag(LIST_ITEM_MAIN_FRAG);

        if (editFragment != null) {
            //Get list item to the list
            ShoppingListItem item = editFragment.getFilledShoppingListItem();
            if( item == null ){
                Toast.makeText(this, R.string.ed_item_validation, Toast.LENGTH_SHORT).show();
                return;
            }

            if (mItemFragment.isNewItem()) {
                //Fired add event
                EventBus.getDefault().postSticky(
                        new ItemAddEvent(item, mItemFragment.getPosition()));
            } else {
                //Fired update event
                EventBus.getDefault().postSticky(
                        new ItemUpdateEvent(item, mItemFragment.getPosition()));
            }
        }

        finish();
    }
}
