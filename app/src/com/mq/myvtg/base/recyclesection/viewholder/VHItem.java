package com.mq.myvtg.base.recyclesection.viewholder;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.mq.myvtg.base.recyclesection.model.Item;

public abstract class VHItem extends RecyclerView.ViewHolder {
    public VHItem(View itemView) {
        super(itemView);
    }

    public abstract void setupView(Item header);
}
