package com.mq.myvtg.listener;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by nguyen.dang.tho on 6/28/2017.
 */

public abstract class OnScrollToLoadMoreListener extends RecyclerView.OnScrollListener {
    public abstract void onLoadMore();

    private int visibleThreshold = 2;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            onLoadMore();
        }
    }
}
