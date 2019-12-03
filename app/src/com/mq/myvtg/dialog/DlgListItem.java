package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.mq.myvtg.R;

import java.util.List;

public class DlgListItem extends Dialog {
    private List<Model> data;
    private OnItemSelectListener listener;
    private boolean multiSelect = false;

    public interface OnItemSelectListener {
        void onItemSelect(DlgListItem dlg, int position);
    }

    public DlgListItem(Context context, List<Model> data, OnItemSelectListener listener) {
        super(context);
        this.data = data;
        this.listener = listener;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_list_item);
        setCancelable(true);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(new Adapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelect(DlgListItem.this, position);
            }
        });
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    class Adapter extends BaseAdapter {
        private LayoutInflater inflater;

        public Adapter() {
            inflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.cell_dlg_list_item, parent, false);
                holder.text = view.findViewById(R.id.text);
                holder.image = view.findViewById(R.id.image);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            Model model = data.get(position);
            if (model.imgResId == 0) {
                holder.image.setVisibility(View.GONE);
            } else {
                holder.image.setVisibility(View.VISIBLE);
                holder.image.setImageResource(model.imgResId);
            }
            holder.text.setText(model.text);
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public static class Model {
        String text;
        int imgResId;
        public Model(String text, int imgResId) {
            this.text = text;
            this.imgResId = imgResId;
        }

        public String getText() {
            return text;
        }
    }
}
