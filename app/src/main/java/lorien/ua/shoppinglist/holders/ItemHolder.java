package lorien.ua.shoppinglist.holders;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import org.greenrobot.eventbus.EventBus;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import lorien.ua.shoppinglist.adapters.refreshable.Refreshable;
import lorien.ua.shoppinglist.events.item.ItemDeselectedEvent;
import lorien.ua.shoppinglist.events.item.ItemSelectedEvent;
import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 23.03.2016.
 * List item view holder
 */
public class ItemHolder extends ChildViewHolder implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    private static int MAX_DESCRIPIION_LEN = 14;

    private TextView name = null;
    private TextView description = null;
    private String fullDescription = null;
    private TextView amount = null;
    private TextView price = null;
    private ImageView cart = null;
    private RelativeLayout listItemLayout = null;
    private Choiceable choiceableAdapter = null;

    private ShoppingListItem item = null;


    public ItemHolder(Choiceable choicebleAdapter, View row) {
        super(row);

        name = (TextView) row.findViewById(R.id.child_row_name);
        description = (TextView) row.findViewById(R.id.child_row_description);
        amount = (TextView) row.findViewById(R.id.child_row_amount);
        price = (TextView) row.findViewById(R.id.child_row_price);
        listItemLayout = (RelativeLayout) row.findViewById(R.id.child_row_layout);
        cart = (ImageView) row.findViewById(R.id.child_row_cart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Making ripple effect starts at position of touch
            row.setOnTouchListener(new View.OnTouchListener() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.findViewById(R.id.child_row_main_layout)
                            .getBackground()
                            .setHotspot(event.getX(), event.getY());
                    return false;
                }
            });
        }

        row.setOnClickListener(this);

        this.choiceableAdapter = choicebleAdapter;
    }

    public void bindModel(ShoppingListItem item) {
        this.item = item;

        name.setText(item.getName());
        amount.setText(item.getAmount());

        //If description too long cut it to 40 letters and will show it
        //as a hint by click
        if (item.getDescription().length() > MAX_DESCRIPIION_LEN) {
            fullDescription = item.getDescription();
            //3 - "..."
            description.setText(item.getDescription().substring(0, MAX_DESCRIPIION_LEN - 3) + "...");
            description.setOnClickListener(this);
        } else {
            description.setText(item.getDescription());
        }
        price.setText(String.valueOf(item.getPrice()));

        //Shopping list done/not done
        if (item.getIsDone()) {
            listItemLayout.setBackgroundColor(listItemLayout.getResources().getColor(R.color.itemDoneBack));
            cart.setImageResource(R.drawable.ic_remove_cart_white);
        } else {
            listItemLayout.setBackgroundColor(listItemLayout.getResources().getColor(R.color.itemNotDoneBack));
            cart.setImageResource(R.drawable.ic_cart_white);
        }

        //Shopping list select/not select
        boolean checkedState = choiceableAdapter.isChecked(getAdapterPosition());
        if (checkedState) {
            listItemLayout.setBackgroundColor(listItemLayout.getResources().getColor(R.color.itemSelect));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        choiceableAdapter.setItemState(getAdapterPosition(), isChecked);
    }

    @Override
    public void onClick(View v) {
        if (v == description) {
            Toast.makeText(v.getContext(), fullDescription, Toast.LENGTH_SHORT).show();
            return;
        }


        int oldPosition = choiceableAdapter.getSelectedItemPosition();
        int position = getAdapterPosition();

        //Send deselect message only if we want deselect currently
        //selected item
        if (choiceableAdapter.isChecked(position)) {
            choiceableAdapter.setItemState(position, false);
            EventBus.getDefault().post(new ItemDeselectedEvent(position));
        } else {
            //replace one sel item to another
            choiceableAdapter.setItemState(position, true);
            EventBus.getDefault().post(new ItemSelectedEvent(item, position));
        }

        //Refresh old and new items to update selection
        if (oldPosition != -1) {
            ((Refreshable) choiceableAdapter).refreshItem(oldPosition);
        }
        ((Refreshable) choiceableAdapter).refreshItem(position);
    }
}
