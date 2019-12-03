package com.mq.myvtg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mq.myvtg.R;
import com.mq.myvtg.model.dto.LstHisScan;
import com.mq.myvtg.util.LogUtil;

import java.util.List;

public class AdapterCustomer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final String DIAMOND = "DIAMOND";
    private final String GOLD = "GOLD";
    private final String SILVER = "SILVER";
    private final String MEMBER = "MEMBER";

    private List<LstHisScan> mDataSet;

    public AdapterCustomer(List<LstHisScan> dataset) {
        mDataSet = dataset;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_info, parent, false);

            return new CustomerVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomerVH) {
            CustomerVH viewHolder = (CustomerVH) holder;
            viewHolder.setDateTime(mDataSet.get(position).insertDate);
            viewHolder.setFullname(mDataSet.get(position).cusName);
            viewHolder.setPhoneNumber(mDataSet.get(position).isdn);
            viewHolder.setRankType(mDataSet.get(position).rank);

        } else if (holder instanceof LoadingVH) {
            LogUtil.d("add loading item");
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class CustomerVH extends RecyclerView.ViewHolder {

        private final View mItemView;

        public CustomerVH(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        public void setRankType(String rank) {
            View rankColor = mItemView.findViewById(R.id.rankColor);
            ImageView rankIcon = mItemView.findViewById(R.id.rankIcon);

            if (rank.equalsIgnoreCase(DIAMOND)) {
                rankColor.setBackgroundColor(
                        rankColor.getResources().getColor(R.color.diamond)
                );
                rankIcon.setImageDrawable(
                        rankIcon.getResources().getDrawable(R.drawable.ic_diamond)
                );
            } else if (rank.equalsIgnoreCase(GOLD)) {
                rankColor.setBackgroundColor(
                        rankColor.getResources().getColor(R.color.gold)
                );
                rankIcon.setImageDrawable(
                        rankIcon.getResources().getDrawable(R.drawable.ic_golden)
                );
            } else if (rank.equalsIgnoreCase(SILVER)) {
                rankColor.setBackgroundColor(
                        rankColor.getResources().getColor(R.color.silver)
                );
                rankIcon.setImageDrawable(
                        rankIcon.getResources().getDrawable(R.drawable.ic_silver)
                );
            } else if (rank.equalsIgnoreCase(MEMBER)) {
                rankColor.setBackgroundColor(
                        rankColor.getResources().getColor(R.color.member)
                );
                rankIcon.setImageDrawable(
                        rankIcon.getResources().getDrawable(R.drawable.ic_member)
                );
            } else {
                LogUtil.e("Rank type not defined");
                rankColor.setBackgroundColor(
                        rankColor.getResources().getColor(R.color.member)
                );
                rankIcon.setImageDrawable(
                        rankIcon.getResources().getDrawable(R.drawable.ic_member)
                );
            }
        }

        public void setFullname(String fullname) {
            TextView tvFullname = mItemView.findViewById(R.id.tvFullname);

            if (fullname == null || fullname.isEmpty()) {
                LogUtil.e("Fullname is null or empty");
                tvFullname.setText("");
            } else {
                tvFullname.setText(fullname);
            }
        }

        public void setPhoneNumber(String phoneNumber) {
            TextView tvPhoneNumber = mItemView.findViewById(R.id.tvPhoneNumber);

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                LogUtil.e("Phone number is null or empty");
                tvPhoneNumber.setText("");
            } else {
                tvPhoneNumber.setText(phoneNumber);
            }
        }

        public void setDateTime(String dateTime) {
            TextView tvDate = mItemView.findViewById(R.id.tvDate);
            TextView tvTime = mItemView.findViewById(R.id.tvTime);

            if (dateTime == null || dateTime.isEmpty()) {
                LogUtil.e("Date time is null or empty");
                tvDate.setText("");
                tvTime.setText("");
            } else {
                String[] strSplitted = dateTime.trim().split(" ");
                String date = strSplitted != null && strSplitted.length > 0 ? strSplitted[0] : "";
                String time = strSplitted != null && strSplitted.length > 1 ? strSplitted[1] : "";

                tvDate.setText(date);
                tvTime.setText(time);
            }
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingVH(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
