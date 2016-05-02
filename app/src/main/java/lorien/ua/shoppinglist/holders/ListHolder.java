package lorien.ua.shoppinglist.holders;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import org.greenrobot.eventbus.EventBus;

import lorien.ua.shoppinglist.R;
import lorien.ua.shoppinglist.adapters.choiceable.Choiceable;
import lorien.ua.shoppinglist.adapters.refreshable.Refreshable;
import lorien.ua.shoppinglist.events.common.CorrectPositionEvent;
import lorien.ua.shoppinglist.events.item.ItemDeselectedEvent;
import lorien.ua.shoppinglist.events.item.ItemSelectedEvent;
import lorien.ua.shoppinglist.events.list.ListDeselectedEvent;
import lorien.ua.shoppinglist.events.list.ListSelectedEvent;
import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 21.03.2016.
 * List holder
 */
public class ListHolder extends ParentViewHolder implements View.OnClickListener {
    private TextView bought = null;
    private TextView total = null;
    private TextView title = null;
    private ImageView expandIcon = null;
    private RelativeLayout rowLayout = null;
    private ShoppingList shoppingList = null;
    private Choiceable choiceableAdapter = null;


    public ListHolder(final Choiceable choiceableAdapter, View row) {
        super(row);

        bought = (TextView) row.findViewById(R.id.list_row_bought);
        total = (TextView) row.findViewById(R.id.list_row_total);
        title = (TextView) row.findViewById(R.id.list_row_title);
        expandIcon = (ImageView) row.findViewById(R.id.list_row_expandicon);
        rowLayout = (RelativeLayout) row.findViewById(R.id.list_row_layout);

        row.setOnClickListener(this);

        //Making ripple effect starts at position of touch
        row.setOnTouchListener(new View.OnTouchListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.findViewById(R.id.list_row_main_layout)
                        .getBackground()
                        .setHotspot(event.getX(), event.getY());
                return false;
            }
        });

        this.choiceableAdapter = choiceableAdapter;

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCollapseRow(v);
            }
        });
    }

    public void bindModel(ShoppingList shoppingList) {
        String template;
        this.shoppingList = shoppingList;
        template = bought.getContext().getString(R.string.row_bought);
        bought.setText(String.format(template, shoppingList.getDoneItemsCount(), shoppingList.getItemsCount()));

        total.setText(String.valueOf(shoppingList.getTotalPrice()));

        if (isExpanded()) {
            expandIcon.setImageResource(R.drawable.ic_collapse_white);
        } else {
            expandIcon.setImageResource(R.drawable.ic_expand_white);
        }

        title.setText(shoppingList.getName());

        //Shopping list done/not done
        if (shoppingList.getIsDone()) {
            rowLayout.setBackgroundColor(rowLayout.getResources().getColor(R.color.itemDoneBack));
        } else {
            rowLayout.setBackgroundColor(rowLayout.getResources().getColor(R.color.itemNotDoneBack));
        }

        //Shopping list select/not select
        boolean checkedState = choiceableAdapter.isChecked(getAdapterPosition());
        if (checkedState) {
            rowLayout.setBackgroundColor(rowLayout.getResources().getColor(R.color.itemSelect));
        }
    }

    private void expandCollapseRow(View row) {
        int selectedPosition = choiceableAdapter.getSelectedItemPosition();
        int expandPosition = getAdapterPosition();

        //Correct selectable position if we need it
        if (selectedPosition > expandPosition) {
            int listItemsCount = shoppingList.getItemsCount();

            int correctPosition = -1;
            if (isExpanded()) {
                //Will collapse
                //If select position inside collapsing item deselect it
                //else just correct position
                if (selectedPosition <= expandPosition + listItemsCount) {
                    choiceableAdapter.setItemState(selectedPosition, false);
                    EventBus.getDefault().post(new ItemDeselectedEvent(selectedPosition));
                } else {
                    correctPosition = selectedPosition - listItemsCount;
                    choiceableAdapter.setItemState(correctPosition, true);
                }
            } else {
                //Will expand
                correctPosition = selectedPosition + listItemsCount;
                choiceableAdapter.setItemState(correctPosition, true);
            }
            EventBus.getDefault().post(new CorrectPositionEvent(correctPosition));
        }

        //Do expand
        ListHolder.super.onClick(row);
        if (isExpanded()) {
            expandIcon.setImageResource(R.drawable.ic_collapse_white);
        } else {
            expandIcon.setImageResource(R.drawable.ic_expand_white);
        }
    }

    //Select/deselect shopping list
    @Override
    public void onClick(View v) {
        int oldPosition = choiceableAdapter.getSelectedItemPosition();
        int position = getAdapterPosition();

        //Send deselect message only if we want deselect currently
        //selected item
        if (choiceableAdapter.isChecked(position)) {
            choiceableAdapter.setItemState(position, false);
            EventBus.getDefault().post(new ListDeselectedEvent());
        } else {
            //replace one sel item to another
            choiceableAdapter.setItemState(position, true);
            EventBus.getDefault().post(new ListSelectedEvent(shoppingList, position));
        }

        //Refresh old and new items to update selection
        if (oldPosition != -1) {
            ((Refreshable) choiceableAdapter).refreshItem(oldPosition);
        }
        ((Refreshable) choiceableAdapter).refreshItem(position);
    }
}
