package com.moutamid.daiptv.utilis;

import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CustomPagerSnapHelper extends PagerSnapHelper {

    private OnSnapPositionChangeListener snapPositionChangeListener;
    private RecyclerView recyclerView;

    @Override
    public void attachToRecyclerView(RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public void setSnapPositionChangeListener(OnSnapPositionChangeListener listener) {
        snapPositionChangeListener = listener;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private static final int DEBOUNCE_TIMEOUT = 300; // Adjust as needed
        private Handler handler = new Handler();
        private Runnable runnable;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (snapPositionChangeListener != null) {
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                            snapPositionChangeListener.onSnapPositionChange(position);
                        }
                    }
                }
            };

            handler.postDelayed(runnable, DEBOUNCE_TIMEOUT);
        }
    };


    public interface OnSnapPositionChangeListener {
        void onSnapPositionChange(int position);
    }
}
