package com.mq.myvtg.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmt;

public class FrgmtContact extends BaseFrgmt {
    public static FrgmtContact newInstance() {
        return new FrgmtContact();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_contact, container, false);

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.contact);

        contentView
                .findViewById(R.id.btnBack)
                .setOnClickListener(view -> goBack());

        return contentView;
    }

}
