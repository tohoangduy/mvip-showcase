package com.mq.myvtg.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.base.MyVTGApp;
import com.mq.myvtg.dialog.SpinnerDialog;
import com.mq.myvtg.fragment.account.FrgmtLogin;
import com.mq.myvtg.helper.XMLPullParserHandler;
import com.mq.myvtg.model.ModelUserLogin;
import com.mq.myvtg.observer.ObserverManager;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.NetworkUtil;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ActivityLogin extends BaseFrgmtActivity
        implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = ActivityLogin.class.getSimpleName();
    private View btnBack, btnSelectLanguage;
    private SpinnerDialog dialogLanguage;
    private TextView labelLanguage;

    private String noInternetMsg;
    private ActivityLogin.LoginDoneListener loginListener;
    private Client client = Client.getInstance();
    private int retryCount_login = 0;

    public ActivityLogin() {
        contentViewId = R.layout.fragment_activity_logo;
    }

    @Override
    protected Fragment getFirstFragment() {
        return FrgmtLogin.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ObserverManager.register(this, ObserverManager.ObserverId.ACTIVITY_LOGIN);

        // get references
        btnBack = findViewById(R.id.btnBack);
        btnSelectLanguage = findViewById(R.id.btnSelectLanguage);
        labelLanguage = findViewById(R.id.labelLanguage);

        // set listeners
        btnBack.setOnClickListener(this);
        btnSelectLanguage.setOnClickListener(this);

        int selectedLang; // default is khmer language
        String savedLanguage = Utils.getLanguage(this);
        selectedLang = languages.indexOf(savedLanguage);
        if (selectedLang < 0) {
            selectedLang = languages.indexOf(Const.LANGUAGES.KHMER);
        }
        labelLanguage.setText(languages.get(selectedLang));
        dialogLanguage = new SpinnerDialog(this, languages, selectedLang, new SpinnerDialog.DialogListener() {

            @Override
            public void ready(int n) {
                Log.d(TAG, "item selected is " + n);
                labelLanguage.setText(languages.get(n));
                setLang(languages.get(n));
            }

            @Override
            public void cancelled() {
                Log.d(TAG, "popup cancelled");
            }
        });

        noInternetMsg = getString(R.string.notify_network_error);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private void setLang(String lang) {
        ((MyVTGApp) getApplication()).changeLang(lang);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnSelectLanguage:
                if (dialogLanguage != null) {
                    dialogLanguage.show();
                }
                break;
        }
    }

    private boolean isConnectedInternet() {
        return NetworkUtil.isConnected(this);
    }

    @Override
    public void onBackStackChanged() {
        btnBack.setVisibility(isFirstFragment() ? View.INVISIBLE : View.VISIBLE);
    }

    public interface LoginDoneListener {
        void loginDone(boolean success, ModelUserLogin model, String uiMsg);
    }

    public void login(final String username, final String password, final ActivityLogin.LoginDoneListener listener) {
        loginListener = listener;
        if (!isConnectedInternet()) {
            loginDone(false, null, noInternetMsg);
            retryCount_login = 0;
            return;
        }
        UIHelper.showProgress(this);

        client.login(this, username, password, new SoapResponse(this) {
            @Override
            protected void success(String resValue, String uiMsg) {
                retryCount_login = 0;
                ModelUserLogin model;
                if (resValue.isEmpty()) {
                    model = new ModelUserLogin();
                } else {
                    InputStream stream = new ByteArrayInputStream(resValue.getBytes());
                    List<ModelUserLogin> listParse = new XMLPullParserHandler<ModelUserLogin>().parse(stream, ModelUserLogin.class);
                    model = (listParse != null && listParse.size() > 0) ? listParse.get(0) : new ModelUserLogin();
                }
                loginDone(true, model, null);
            }

            @Override
            protected void failed(String reason, final String uiMsg, int statusCode) {
                retryCount_login = 0;
                LogUtil.e(TAG, "login failed. " + reason);
                loginDone(false, null, uiMsg);
            }

            @Override
            public boolean onTimeout() {
                if (retryCount_login >= Client.RETRY_NUM) {
                    UIHelper.showDialogFailure(getApplicationContext(), getString(R.string.cannot_connect_to_server));
                    return false;
                } else {
                    retryCount_login++;
                    login(username, password, listener);
                    return true;
                }
            }
        });
    }

    private void loginDone(boolean success, ModelUserLogin model, final String uiMsg) {
        UIHelper.hideProgress();
        if (!success) {
            if (uiMsg != null) {
                if (uiMsg.equals(noInternetMsg)) {
                    UIHelper.showDialogFailure(this, uiMsg);
                } else {
                    loginListener.loginDone(false, null, uiMsg);
                }

            } else {
                loginListener.loginDone(false, null, null);
            }
        } else {
            ModelUserLogin.saveToCache(this, model);
            client.userLogin = model;
            loginListener.loginDone(true, model, null);
        }
    }
}
