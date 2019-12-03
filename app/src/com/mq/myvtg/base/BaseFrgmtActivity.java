package com.mq.myvtg.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mq.myvtg.R;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.observer.ObserverManager;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.UIHelper;

import java.util.Arrays;
import java.util.List;

public abstract class BaseFrgmtActivity extends FragmentActivity implements ObserverManager.ObserverObj {
    protected String TAG;
    protected int contentViewId = -1;
    public List<String> languages;

    public BaseFrgmtActivity() {
        TAG = getClass().getSimpleName();
    }

    protected abstract Fragment getFirstFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentViewId == -1 ? R.layout.fragment_activity : contentViewId);

        UIHelper.setOverlayStatusBar(this.getWindow(), true);
        languages = Arrays.asList(
                Const.LANGUAGES.ENLISH,
                Const.LANGUAGES.KHMER
        );

        Fragment fragment = getFirstFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private long mLastClickTime = 0;

    public void goNext(Fragment fragment) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public void popTo(String name) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack(name, 0);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fr = getCurrentFragment();
        if (fr instanceof BaseFrgmt && ((BaseFrgmt) fr).onBackPressed()) {
            // already handle in fragment
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            String msg = getString(R.string.confirm_quit_app);
            UIHelper.askYesNo(this, msg, () -> finish());
        }
    }

    public boolean isFirstFragment() {
        try {
            return getSupportFragmentManager().getBackStackEntryCount() == 1;
        } catch (Exception ignored) {

        }
        return false;
    }

    public void sendObject(String fromName, String toName, String key, Object data) {
        Fragment target = getSupportFragmentManager().findFragmentByTag(toName);
        if (target instanceof BaseFrgmt) {
            ((BaseFrgmt) target).receiveObject(fromName, key, data);
        }
    }

    @Override
    public void notification(Object... params) {
        if (params == null || params.length <= 0 || !(params[0] instanceof Integer)) {
            return;
        }
        switch ((Integer) params[0]) {
            case Const.ObsNotiId.CHANGE_LANGUAGE:
                ((MyVTGApp) getApplication()).updateResourceLocale(this);
                Fragment fr = getCurrentFragment();
                if (fr instanceof BaseFrgmt) {
                    ((BaseFrgmt) fr).performChangeLanguage();
                }
                break;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    // For request permission
    /////////////////////////////////////////////////////////////////////////////////

    private static int REQUEST_CODE_PERMISSION = 1000;
    private SparseArray<ModelRequestPermission> requestPermissions = new SparseArray<>();

    class ModelRequestPermission {
        public String[] permissions;
        Runnable runIfGranted;
        Runnable runIfDenied;
    }

    protected void requestPermissionIfNeeded(String[] permissions, Runnable runIfGranted, Runnable runIfDenied) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean granted = true;
            for (String permission : permissions) {
                int pem = ActivityCompat.checkSelfPermission(this, permission);
                if (pem != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                runIfGranted.run();
            } else {
                REQUEST_CODE_PERMISSION += 1;
                ModelRequestPermission model = new ModelRequestPermission();
                model.permissions = permissions;
                model.runIfGranted = runIfGranted;
                model.runIfDenied = runIfDenied;
                requestPermissions.put(REQUEST_CODE_PERMISSION, model);
                requestPermissions(permissions, REQUEST_CODE_PERMISSION);
            }
        } else {
            runIfGranted.run();
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        LogUtil.d(TAG, "onRequestPermissionsResult");
        ModelRequestPermission model = requestPermissions.get(requestCode);
        if (model != null) {
            requestPermissions.delete(requestCode);
            boolean granted = model.permissions.length == permissions.length && permissions.length == grantResults.length;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                if (model.runIfGranted != null) model.runIfGranted.run();
            } else {
                if (model.runIfDenied != null) model.runIfDenied.run();
            }
        }
    }
}
