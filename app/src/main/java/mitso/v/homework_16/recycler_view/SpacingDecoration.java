package mitso.v.homework_16.recycler_view;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int         mHorizontalSpacing = 0;
    private int         mVerticalSpacing = 0;
    private boolean     mIncludeEdge = false;

    public SpacingDecoration(int hSpacing, int vSpacing, boolean includeEdge) {

        mHorizontalSpacing = hSpacing;
        mVerticalSpacing = vSpacing;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = layoutManager.getSpanCount();
        int column = position % spanCount;
        getGridItemOffsets(outRect, position, column, spanCount);
    }

    private void getGridItemOffsets(Rect outRect, int position, int column, int spanCount) {

        if (mIncludeEdge) {
            outRect.left = mHorizontalSpacing * (spanCount - column) / spanCount;
            outRect.right = mHorizontalSpacing * (column + 1) / spanCount;
            if (position < spanCount)
                outRect.top = mVerticalSpacing;
            outRect.bottom = mVerticalSpacing;
        } else {
            outRect.left = mHorizontalSpacing * column / spanCount;
            outRect.right = mHorizontalSpacing * (spanCount - 1 - column) / spanCount;
            if (position >= spanCount)
                outRect.top = mVerticalSpacing;
        }
    }
}