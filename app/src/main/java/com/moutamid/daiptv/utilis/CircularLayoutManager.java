package com.moutamid.daiptv.utilis;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CircularLayoutManager extends LinearLayoutManager {

    public CircularLayoutManager(Context context) {
        super(context, HORIZONTAL, false);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
        handleCircularScroll(dx, recycler);
        return scrolled;
    }

    private void handleCircularScroll(int dx, RecyclerView.Recycler recycler) {
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            int position = getPosition(child);

            if (dx > 0 && position == getItemCount() - 1) {
                // Reached the end, reposition the first item to the end
                int left = getDecoratedRight(child);
                int right = left + getDecoratedMeasuredWidth(child);
                layoutDecorated(child, left, getPaddingTop(), right, getPaddingTop() + getDecoratedMeasuredHeight(child));
            } else if (dx < 0 && position == 0) {
                // Reached the beginning, reposition the last item to the start
                int right = getDecoratedLeft(child);
                int left = right - getDecoratedMeasuredWidth(child);
                layoutDecorated(child, left, getPaddingTop(), right, getPaddingTop() + getDecoratedMeasuredHeight(child));
            }
        }
    }

    @Override
    public int getItemCount() {
        // Return a large value to create a circular effect
        return Integer.MAX_VALUE;
    }
}

