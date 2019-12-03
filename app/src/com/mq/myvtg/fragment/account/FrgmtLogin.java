package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mq.myvtg.R;
import com.mq.myvtg.activity.ActivityLogin;
import com.mq.myvtg.activity.ActivityForgotPwd;
import com.mq.myvtg.activity.HomeActivity;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.base.MyVTGApp;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.model.ModelUserLogin;
import com.mq.myvtg.util.AES;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FrgmtLogin extends BaseFrgmt
        implements View.OnClickListener, TextWatcher {

    private EditText inputPhoneNo, inputPwd;
    private ImageButton btnShowPassword;
    private TextView btnForgotPwd, btnSignUp, tvDontHaveAccount, tvTitle, textButtonLogin, tvWebsite, tvHotline;
    private View btnLogin, validatePhoneNumber, validatePassword;
    private LinearLayout footer_container;

    public static FrgmtLogin newInstance() {
        return new FrgmtLogin();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView != null) {
            UIHelper.setOverlayStatusBar(Objects.requireNonNull(getActivity()).getWindow(), true);
            return contentView;
        }
        contentView = inflater.inflate(R.layout.frgmt_login, container, false);
        footer_container = Objects.requireNonNull(getActivity()).findViewById(R.id.footer_container);
        inflater.inflate(R.layout.frgmt_login_footer, footer_container);

        inputPhoneNo = contentView.findViewById(R.id.inputPhoneNo);
        inputPwd = contentView.findViewById(R.id.inputPwd);
        btnShowPassword = contentView.findViewById(R.id.btnShowPassword);
        btnForgotPwd = contentView.findViewById(R.id.btnForgotPwd);
        btnSignUp = contentView.findViewById(R.id.btnSignUp);
        btnLogin = contentView.findViewById(R.id.btnLogin);
        tvDontHaveAccount = contentView.findViewById(R.id.tvDontHaveAccount);
        tvTitle = contentView.findViewById(R.id.tvTitle);
        textButtonLogin = contentView.findViewById(R.id.textButtonLogin);
        validatePassword = contentView.findViewById(R.id.validatePassword);
        validatePhoneNumber = contentView.findViewById(R.id.validatePhoneNumber);
        tvHotline = footer_container.findViewById(R.id.tvHotline);
        tvWebsite = footer_container.findViewById(R.id.tvWebsite);

        setupLoginLayout(contentView);
        setLang(Utils.getLanguage(getActivity()));

        contentView.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            hideSoftKeyboard();
            return false;
        });
        inputPhoneNo.addTextChangedListener(this);
        inputPwd.addTextChangedListener(this);
        tvHotline.setOnClickListener(this);
        tvWebsite.setOnClickListener(this);
        inputPhoneNo.addTextChangedListener(this);

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        login("0972115027", "12345678");
//        login("0886254154", "123456789");
    }

    private void setLang(String lang) {
        ((MyVTGApp) Objects.requireNonNull(getActivity()).getApplication()).changeLang(lang);
    }

    @Override
    public void performChangeLanguage() {
        super.performChangeLanguage();
        inputPhoneNo.setHint(R.string.phone_number);
        inputPwd.setHint(R.string.label_password);
        tvTitle.setText(R.string.login_account);
        btnForgotPwd.setText(R.string.label_forgot_password);
        textButtonLogin.setText(R.string.sign_in);
        tvDontHaveAccount.setText(R.string.label_do_not_have_account);
        btnSignUp.setText(R.string.label_signup);
        tvHotline.setText(R.string.label_hotline);
        tvWebsite.setText(R.string.label_website);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (client != null)
            client.userLogin = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboardDelay();
        footer_container.setVisibility(View.VISIBLE);
        needLogout = false;
    }

    @Override
    public void onPause() {
        footer_container.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    private void setupLoginLayout(View v) {
        for (int id : new int[]{
                R.id.btnLogin,
                R.id.btnForgotPwd,
                R.id.btnSignUp,
                R.id.btnShowPassword
        }) {
            View btn = v.findViewById(id);
            btn.setOnClickListener(this);
        }

        btnShowPassword.setOnClickListener(this);

        inputPwd.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnLogin.performClick();
                return true;
            }
            return false;
        });

        View scrollContainer = v.findViewById(R.id.scrollContainer);
        if (scrollContainer != null)
            scrollContainer.setOnTouchListener((View v12, MotionEvent event) -> {
                hideSoftKeyboard();
                return false;
            });
        contentView.setOnTouchListener((View v13, MotionEvent event) -> {
            hideSoftKeyboard();
            return false;
        });
    }

    private void showDialogFailure(String msg) {
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

    private void login(String username, String password) {
        String encryptedPassword = Const.ENCRYPT_PW_LOGIN ? AES.encrypt(password) : password;
        ((ActivityLogin) Objects.requireNonNull(getActivity())).login(username, encryptedPassword, (boolean success, ModelUserLogin model, String uiMsg) -> {
            if (success) {
                model.username = username;
                model.token = encryptedPassword;
                client.userLogin = model;
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                showDialogFailure(uiMsg);
            }
        });
    }

    @Override
    public void onClick(View view) {
        hideSoftKeyboard();
        switch (view.getId()) {
            case R.id.btnLogin:
                if (validatePhoneNumber() && validatePassword()) {
                    login(inputPhoneNo.getText().toString().trim(), inputPwd.getText().toString());
                }
                break;

            case R.id.btnForgotPwd:
                Intent intentForgotPwd = new Intent(getContext(), ActivityForgotPwd.class);
                startActivityForResult(intentForgotPwd, Const.GO_TO_SIGN_UP);
                break;

            case R.id.btnSignUp:
                goNext(new FrgmtSignUp());
                break;

            case R.id.btnShowPassword:
                if (inputPwd.getInputType() == InputType.TYPE_CLASS_TEXT) {
                    inputPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnShowPassword.setBackground(getResources().getDrawable(R.drawable.ic_eye_lock));
                } else {
                    inputPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    btnShowPassword.setBackground(getResources().getDrawable(R.drawable.ic_eye));
                }
                break;

            case R.id.tvHotline:
                UIHelper.call(getContext(), Const.HOTLINE_NUMBER);
                break;

            case R.id.tvWebsite:
                Utils.openWeb(getContext(), getContext().getString(R.string.url_website));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        View viewFocused = contentView.findFocus();
        if (viewFocused == null) return;

        switch (viewFocused.getId()) {
            case R.id.inputPhoneNo:
                validatePhoneNumber();
                break;
            case R.id.inputPwd:
                validatePassword();
                break;
        }
    }

    @SuppressLint("StringFormatInvalid")
    private boolean validatePhoneNumber() {
        boolean isEmpty = inputPhoneNo.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePhoneNumber.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.phone_number)
                )
        );
        validatePhoneNumber.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validatePassword() {
        boolean isEmpty = inputPwd.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePassword.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.label_password)
                )
        );
        validatePassword.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Const.GO_TO_SIGN_UP) {
            goNext(new FrgmtSignUp());
        }
    }
}
