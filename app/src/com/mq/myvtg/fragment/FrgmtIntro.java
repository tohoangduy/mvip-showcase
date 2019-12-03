package com.mq.myvtg.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mq.myvtg.R;
import com.mq.myvtg.activity.ActivityLogin;
import com.mq.myvtg.activity.HomeActivity;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.SharePref;

import org.jetbrains.annotations.NotNull;

public class FrgmtIntro extends Fragment
    implements View.OnClickListener
{
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;

    public static FrgmtIntro newInstance() {
        return new FrgmtIntro();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.frgmt_intro, container, false);

        viewPager = contentView.findViewById(R.id.view_pager);
        dotsLayout = contentView.findViewById(R.id.layoutDots);
        btnSkip = contentView.findViewById(R.id.btn_skip);
        btnNext = contentView.findViewById(R.id.btn_next);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.intro_slide1,
                R.layout.intro_slide2,
                R.layout.intro_slide3
        };

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

        return contentView;
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15,0,15,0);
            dots[i].setLayoutParams(params);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
    }

    private void gotoLogin() {
        SharePref.putBoolean(getContext(), Const.KEY_SKIP_INTRO, true);

        Intent intent = new Intent(getContext(), ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.KEY_SKIP_SPLASH, true);
        intent.putExtra(Const.KEY_LOG_OUT, true);
        startActivity(intent);
        getActivity().finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(R.string.invite_toolbar_button_done);
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(R.string.next);
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:
                Activity act = getActivity();
                if (act instanceof HomeActivity) {
                    act.onBackPressed();
                } else {
                    gotoLogin();
                }
                break;
            case R.id.btn_next:
                int nextSlideIdx = viewPager.getCurrentItem() + 1;
                if (nextSlideIdx < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(nextSlideIdx);
                } else {
                    Activity act1 = getActivity();
                    if (act1 instanceof HomeActivity) {
                        act1.onBackPressed();
                    } else {
                        gotoLogin();
                    }
                }
                break;
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {

        MyViewPagerAdapter() {
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
