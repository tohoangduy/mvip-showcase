package com.mq.myvtg.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.base.MyVTGApp;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.dialog.SpinnerDialog;
import com.mq.myvtg.fragment.account.FrgmtChangePwd;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.Utils;

import java.util.Arrays;
import java.util.List;


public class FrgmtSetting extends BaseFrgmt implements View.OnClickListener {

    private View btnChangeLanguage, btnHelp, btnContact, btnAbout, btnSignOut, btnChangePwd;
    private TextView tvSelectedLanguage;

    private SpinnerDialog dialogLanguage;

    public static FrgmtSetting newInstance() {
        return new FrgmtSetting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_setting, container, false);

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.setting);

        // get references
        View btnBack = contentView.findViewById(R.id.btnBack);
        btnChangeLanguage = contentView.findViewById(R.id.btnChangeLanguage);
        btnHelp = contentView.findViewById(R.id.btnHelp);
        btnContact = contentView.findViewById(R.id.btnContact);
        btnAbout = contentView.findViewById(R.id.btnAbout);
        btnSignOut = contentView.findViewById(R.id.btnSignOut);
        tvSelectedLanguage = contentView.findViewById(R.id.tvSelectedLanguage);
        btnChangePwd = contentView.findViewById(R.id.btnChangePwd);

        // register listeners
        btnChangePwd.setOnClickListener(this);
        btnChangeLanguage.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        List<String> languages = ((BaseFrgmtActivity)getActivity()).languages;
        int selectedLang; // default is khmer language
        String savedLanguage = Utils.getLanguage(getContext());
        selectedLang = languages.indexOf(savedLanguage);
        if (selectedLang < 0) {
            selectedLang = languages.indexOf(Const.LANGUAGES.KHMER);
        }
        tvSelectedLanguage.setText(languages.get(selectedLang));
        dialogLanguage = new SpinnerDialog(getContext(), languages, selectedLang, new SpinnerDialog.DialogListener() {

            @Override
            public void ready(int n) {
                Log.d(TAG, "item selected is " + n);
                tvSelectedLanguage.setText(languages.get(n));
                setLang(languages.get(n));
            }

            @Override
            public void cancelled() {
                Log.d(TAG, "popup cancelled");
            }
        });

        return contentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangeLanguage:
                if (dialogLanguage != null) {
                    dialogLanguage.show();
                }
                break;
            case R.id.btnHelp:
                goNext(FrgmtIntro.newInstance());
                break;
            case R.id.btnContact:
                goNext(FrgmtContact.newInstance());
                break;
            case R.id.btnAbout:
                Utils.openWeb(getContext(), getContext().getString(R.string.url_website));
                break;
            case R.id.btnSignOut:
                doSignOut();
                break;
            case R.id.btnBack:
                goBack();
                break;
            case R.id.btnChangePwd:
                goNext(FrgmtChangePwd.newInstance());
                break;
        }
    }

    private void doSignOut() {
        new CustomDialog(getActivity())
                .hideHeader()
                .setMess(getString(R.string.msg_confirm_logout))
                .setButtonNegative(getString(R.string.cancel), null)
                .setButtonPositive(getString(R.string.label_yes), null)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        dlg.dismiss();
                        logout();
                    }
                })
                .show();
    }

    @Override
    public void performChangeLanguage() {
        super.performChangeLanguage();
        List<Integer> textViewIds = Arrays.asList(
                R.id.tvTitle,
                R.id.tvLanguage,
                R.id.tvHelp,
                R.id.tvContact,
                R.id.tvAbout,
                R.id.tvSignOut
        );
        List<Integer> stringIds = Arrays.asList(
                R.string.setting,
                R.string.language,
                R.string.help,
                R.string.contact,
                R.string.about,
                R.string.sign_out
        );

        for (int i = 0; i < textViewIds.size(); ++i) {
            TextView textView = contentView.findViewById(textViewIds.get(i));
            textView.setText(stringIds.get(i));
        }
    }

    private void setLang(String lang) {
        ((MyVTGApp) getActivity().getApplication()).changeLang(lang);
    }
}
