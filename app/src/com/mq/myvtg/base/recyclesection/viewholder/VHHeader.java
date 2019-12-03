package com.mq.myvtg.base.recyclesection.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mq.myvtg.base.recyclesection.model.Header;

public abstract class VHHeader extends RecyclerView.ViewHolder {
    public VHHeader(View itemView) {
        super(itemView);
    }

    public abstract void setupView(Header header);
}
