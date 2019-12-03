package com.mq.myvtg.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.FontHelper;

import static com.mq.myvtg.util.Const.FONT_PHET;

public class TVRobotoThin extends AppCompatTextView {
    private static final String FONT_NAME = Const.FONT_ROBOTO_THIN;

    public TVRobotoThin(Context context) {
        super(context);
        init();
    }

    public TVRobotoThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TVRobotoThin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT <= Const.MIN_SDK_TO_CHANGE_FONT) {
            Typeface typeface = FontHelper.getInstance().getTypeFace(getContext(), FONT_PHET);
            setTypeface(typeface);
        } else {
            Typeface typeface = FontHelper.getInstance().getTypeFace(getContext(), FONT_NAME);
            setTypeface(typeface);
        }
    }
}
