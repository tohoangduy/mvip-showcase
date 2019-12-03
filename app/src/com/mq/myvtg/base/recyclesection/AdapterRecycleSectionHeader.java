package com.mq.myvtg.base.recyclesection;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mq.myvtg.base.recyclesection.model.Header;
import com.mq.myvtg.base.recyclesection.model.Item;
import com.mq.myvtg.base.recyclesection.viewholder.VHHeader;
import com.mq.myvtg.base.recyclesection.viewholder.VHItem;

import java.util.ArrayList;
import java.util.List;

public abstract class AdapterRecycleSectionHeader extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private List<Header> data;

    public void addData(List<Header> list) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        if (data != null) data.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return createVHForHeader(parent);
        } else {
            return createVHForItem(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            Header h = getHeader(position);
            ((VHHeader) holder).setupView(h);
        } else {
            Item i = getItem(position);
            ((VHItem) holder).setupView(i);
        }
    }

    protected abstract VHHeader createVHForHeader(ViewGroup parent);

    protected abstract VHItem createVHForItem(ViewGroup parent);

    @Override
    public int getItemCount() {
        int count = 0;
        if (data == null) {
            return count;
        }
        for (Header s : data) {
            count += s.items.size();
        }
        return count + data.size();
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (int i = 0; i < data.size(); i++) {
            if (position == count) {
                return VIEW_TYPE_HEADER;
            }
            count += data.get(i).items.size() + 1;
            if (position < count) {
                break;
            }
        }
        return VIEW_TYPE_ITEM;
    }

    private Header getHeader(int position) {
        int count = 0;
        int i;
        for (i = 0; i < data.size(); i++) {
            if (position == count) {
                return data.get(i);
            }
            count += data.get(i).items.size() + 1;
            if (position < count) {
                break;
            }
        }
        return data.get(i);
    }

    private Item getItem(int position) {
        int count = 0, lastCount = 0;
        int i = 0;
        count += data.get(i).items.size() + 1;
        for (i = 1; i <= data.size(); i++) {

            if (position < count) {
                Header s = data.get(i - 1);
                int offset = position - lastCount - 1;
                Item item = s.items.get(offset);
                item.isLast = offset == (s.items.size() - 1);
                return item;
            }
            lastCount = count;
            count += data.get(i).items.size() + 1;
        }
        return null;
    }
}
