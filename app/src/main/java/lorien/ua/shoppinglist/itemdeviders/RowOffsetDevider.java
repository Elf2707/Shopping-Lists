package lorien.ua.shoppinglist.itemdeviders;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Elf on 23.03.2016.
 */
public class RowOffsetDevider extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public RowOffsetDevider(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpaceHeight;
        }
        outRect.bottom = mVerticalSpaceHeight;
    }
}
