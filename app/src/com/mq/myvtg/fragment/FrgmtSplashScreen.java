package com.mq.myvtg.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.activity.ActivityBlank;
import com.mq.myvtg.activity.ActivityLogin;
import com.mq.myvtg.activity.HomeActivity;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.SharePref;


public class FrgmtSplashScreen extends Fragment {

    private static final int SPLASH_SHOW_TIME = 5000;
    View containerView;
    boolean mSkipIntro = false;
    private Client client = Client.getInstance();

    /**
     * @return A new instance of fragment FrgmtSplashScreen.
     */
    public static FrgmtSplashScreen newInstance() {
        return new FrgmtSplashScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSkipIntro = SharePref.getBoolean(getContext(), Const.KEY_SKIP_INTRO, false);

        containerView = getActivity().findViewById(R.id.logo);

        new Handler().postDelayed(() -> {
            if (mSkipIntro) {
                Class actClass;
                if (client.userLogin == null
//                        || client.userLogin.isSessionExpired()
                ) {
                    actClass = ActivityLogin.class;
                } else {
                    actClass = HomeActivity.class;
                }

                Intent intent = new Intent(getContext(), actClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                ((ActivityBlank) getActivity()).goNext(FrgmtIntro.newInstance());
            }
        }, SPLASH_SHOW_TIME);

        Animation faceIn = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_show);
        containerView.startAnimation(faceIn);
    }
}
