package com.mq.myvtg.util;

import android.view.animation.AlphaAnimation;

public class AlphaVisibility {

    public static AlphaAnimation invisible(){
        final AlphaAnimation alphaTR = new AlphaAnimation(0.5F, 0.5F);
        alphaTR.setDuration(0);
        alphaTR.setFillAfter(true);
        return alphaTR;
    }

    public static AlphaAnimation visible(){
        final AlphaAnimation alphaVB = new AlphaAnimation(1, 1);
        alphaVB.setDuration(0);
        alphaVB.setFillAfter(true);
        return alphaVB;
    }
}
