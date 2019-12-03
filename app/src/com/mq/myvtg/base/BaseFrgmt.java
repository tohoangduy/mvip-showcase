package com.mq.myvtg.base;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.activity.ActivityLogin;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.ResponseDefault;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.dialog.DlgListItem;
import com.mq.myvtg.model.ModelDoAction;
import com.mq.myvtg.model.ModelUserLogin;
import com.mq.myvtg.observer.ObserverManager;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.FragmentUtils;
import com.mq.myvtg.util.ImageUtil;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.NetworkUtil;
import com.mq.myvtg.util.SharePref;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;

public class BaseFrgmt extends Fragment {
    protected String TAG;
    protected View contentView;
    protected View headerView;

    protected int loginUserType;
    protected int DIMEN_1DP;

    // Chú ý: biến client là 1 singleton và ko bao giờ null.
    // Tuyệt đối ko được tùy tiện gán client = null linh tinh trong code
    protected Client client = Client.getInstance();

    private EditText searchInput;
    private Animation showSearchBar;
    private Animation hideSearchBar;
    private boolean canBack = false;
    protected boolean needLogout = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        noInternetMsg = getString(R.string.msg_no_internet_connection);
        UIHelper.DECIMAL_DOT = getString(R.string.decimal_dot);
        UIHelper.THOUSAND_DOT = getString(R.string.thousand_dot);
        DIMEN_1DP = getResources().getDimensionPixelSize(R.dimen.dimen1dp);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (client.userLogin == null) {
            ModelUserLogin model = ModelUserLogin.loadFromCache(getActivity());
            if (model != null) {
                client.userLogin = model;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        canBack = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                canBack = true;
            }
        }, 300);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void sendObject(String to, String key, Object data) {
        ((BaseFrgmtActivity) getActivity()).sendObject(getClass().getSimpleName(), to, key, data);
    }

    // to receive data from another Fragment send via Activity
    public void receiveObject(String from, String key, Object data) {
    }

    protected String getTitleString() {
        return null;
    }

    protected boolean isShowBtnBack() {
        return false;
    }

    protected int getIconBtnBack() {
        return 0;
    }

    protected void goBack() {
        hideSoftKeyboard();
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (FragmentUtils.sDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected void backTo(String name) {
        if (getActivity() != null)
            ((BaseFrgmtActivity) getActivity()).popTo(name);
    }

    protected boolean onBackPressed() {
        return !canBack;
    }

    protected void showSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected void goNext(Fragment next) {
        canBack = false;
        hideSoftKeyboard();
        ((BaseFrgmtActivity) getActivity()).goNext(next);
    }

    protected void hideSoftKeyboard(View view) {
        if (view == null) {
            LogUtil.v(TAG, "hideSoftKeyboard but view is null");
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            imm = null;
        }

    }

    protected void hideSoftKeyboard() {
        try {

            hideSoftKeyboard(contentView);
        } catch (Exception e) {

        }
        clearFocusView();
    }

    protected void hideSoftKeyboardDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSoftKeyboard();
            }
        }, 32);

    }

    private String tempPathV24 = "";

    private String getRealPathFromURI(Uri contentURI, int request) {
        String result = "";
        Cursor cursor = null;
        if (contentURI != null) {
            cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath();
            } else {

                if (Build.VERSION.SDK_INT >= 24 && request == Const.REQUEST_CODE_CAMERA) {
                    cursor.moveToFirst();
                    result = Uri.parse(tempPathV24).getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    result = cursor.getString(idx);
                }

                cursor.close();
            }
        }
        Log.e("CAMERA", "path:" + result);
        return result;
    }

    public void requestPermissionIfNeeded(String[] permissions, Runnable runIfGranted, Runnable runIfDenied) {
        BaseFrgmtActivity activity = (BaseFrgmtActivity) getActivity();
        activity.requestPermissionIfNeeded(permissions, runIfGranted, runIfDenied);
        ObserverManager.notifyOne(
                ObserverManager.ObserverId.ACTIVITY_MAIN,
                Const.ObsNotiId.REQUEST_PERMISSIONS,
                permissions,
                runIfGranted,
                runIfDenied);
    }

    protected void logout() {
        //TODO: clear cache?
        String langCurrent = SharePref.getString(getActivity(), Const.KEY_CURRENT_LANG, Const.LANG_EN);
        String avatarKey = Const.KEY_USER_AVATAR + Utils.getCurrentUserName(getActivity());
        String avatarLocal = SharePref.getString(getActivity(), avatarKey, "");
        boolean skipIntro = SharePref.getBoolean(getContext(), Const.KEY_SKIP_INTRO, false);
        SharePref.removeAll(getActivity());
        SharePref.putString(getActivity(), Const.KEY_CURRENT_LANG, langCurrent);
        SharePref.putString(getActivity(), avatarKey, avatarLocal);
        SharePref.putBoolean(getActivity(), Const.KEY_SKIP_INTRO, skipIntro);
        Utils.saveCurrentUserName(getActivity(), client.getIsdn());
        Intent intent = new Intent(getActivity(), ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.KEY_SKIP_SPLASH, true);
        intent.putExtra(Const.KEY_LOG_OUT, true);
        if (isConnectedInternet()) {
            client.cancelAllRequests(true);
            client.logout(getActivity(), new ResponseDefault() {
                @Override
                protected void success(Object model) {
                    // do nothing
                }

                @Override
                protected void failed(String reason, String uiMsg, int statusCode) {
                    LogUtil.e(TAG, "logout error. " + reason);
                }
            });
        } else {
            client.cancelAllRequests(true);
        }
        clearFocusView();
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    protected ImageView onScreenLoadingIndicator;

    protected void showOnScreenLoadingIndicator() {
        if (contentView == null) return;
        if (onScreenLoadingIndicator == null) {
            onScreenLoadingIndicator = contentView.findViewById(R.id.img_loading);
        }
        if (onScreenLoadingIndicator == null) return;
        if (onScreenLoadingIndicator.getVisibility() == View.VISIBLE) {
            return;
        }
        onScreenLoadingIndicator.setVisibility(View.VISIBLE);
        AnimationDrawable animation = (AnimationDrawable) onScreenLoadingIndicator.getDrawable();
        animation.start();
    }

    protected void hideOnScreenLoadingIndicator() {
        if (onScreenLoadingIndicator != null) {
            final AnimationDrawable animation = (AnimationDrawable) onScreenLoadingIndicator.getDrawable();
            if (onScreenLoadingIndicator.getVisibility() != View.VISIBLE) {
                animation.stop();
                onScreenLoadingIndicator.setVisibility(View.GONE);
                return;
            }
            animation.stop();
            onScreenLoadingIndicator.setVisibility(View.GONE);
        }
    }

    public String noInternetMsg = "###";

    protected void showErrorIfNeeded(String uiMsg) {
        if (uiMsg == null || uiMsg.equalsIgnoreCase("null") || uiMsg.trim().length() == 0) {
            return;
        }
        if (uiMsg.equals(noInternetMsg)) {
            showDialogNetworkFailure(uiMsg);
        } else {
            showDialogNetworkFailure(uiMsg);
        }
    }

    protected void showDialogNetworkFailure(String msg) {
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setBtnPositiveColor(CustomDialog.ColorButton.RED)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        dlg.dismiss();
                    }
                })
                .show();
    }

    // Gọi hàm này để thực hiện đăng ký/huỷ đăng ký dịch vụ
    // Hàm sẽ bật dialog hỏi có muốn đăng ký/huỷ đăng ký dịch vụ (tên dịch vụ + giá cước) ko?
    // Click OK thì sẽ call API để thực hiện, click Cancel thì huỷ
    // Chú ý: có 1 số trương hợp click button cancel ko phải là cancel mà thực hiện 1 action khác

    public interface ActionDoneListener {
        void onActionDone(boolean success, Object data);
    }

    private ActionDoneListener actionDoneListener;

    // Xử lý chung cho tất cả các API doActionService
    private int retry_doAction = 0;

    protected void doActionDone(boolean success, ModelDoAction model, String uiMsg) {
        UIHelper.hideProgress();
        if (!success) {
            if (actionDoneListener != null) actionDoneListener.onActionDone(success, model);
            showErrorIfNeeded(uiMsg);
            return;
        }
        if (model != null) {
            model.showUserMsg(getActivity());
        }
        if (actionDoneListener != null) actionDoneListener.onActionDone(success, model);
    }

    private HashMap<String, Integer> retryMap = new HashMap<>();
    private HashMap<String, Boolean> loadingMap = new HashMap<>();

    protected void clearFocusView() {
        try {
            View view = getActivity().getWindow().getCurrentFocus();
            if (view != null)
                view.clearFocus();
        } catch (Exception e) {

        }

    }

    protected boolean isConnectedInternet() {
        return NetworkUtil.isConnected(getActivity());
    }

    protected boolean isConnectedMobileNetwork() {
        return NetworkUtil.isConnectedMobile(getActivity());

    }

    //language
    protected boolean isLanguageChanged = false;

    public void performChangeLanguage() {
        noInternetMsg = getString(R.string.msg_no_internet_connection);
        UIHelper.DECIMAL_DOT = getString(R.string.decimal_dot);
        UIHelper.THOUSAND_DOT = getString(R.string.thousand_dot);
        if (searchInput != null) {
            searchInput.setHint(getString(R.string.label_search));
        }
        isLanguageChanged = true;
    }
}
