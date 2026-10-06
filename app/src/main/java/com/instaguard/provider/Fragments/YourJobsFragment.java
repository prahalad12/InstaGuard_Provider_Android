package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.facebook.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.NetworkManager.Internal.Util;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class YourJobsFragment extends Fragment {

    View view;
    SharedPreferences SP_Prefs;
    String driverId;
    ImageButton imgbtnBack;
    static TabLayout tabLayout;
    static ViewPager viewPager;
    CustomTextView txtheader;
    //static ViewPagerAdapter adapter;


    AdView adViewFacebook;
    com.google.android.gms.ads.AdView adViewGoogle;

    LinearLayout adContainer;

    static BaseActivity.ViewPagerAdapter adapter;
    BaseActivity baseActivity;

    public YourJobsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.e("driverId~~RideHistoryFragment~~", "");
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yourjobs, container, false);

        SP_Prefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        Log.e("driverId~~RideHistoryFragment~~", driverId);

        initView(view);

        BaseActivity baseActivity = new BaseActivity();
        adContainer = view.findViewById(R.id.banner_container);
        baseActivity.callFacebookAdsCommon(requireActivity(), adViewFacebook, adContainer);

        adViewGoogle = view.findViewById(R.id.adView);
        baseActivity.callGoogleAdsCommon(requireActivity(), adViewGoogle);

        onBackButton(view);

        return view;

    }

    void initView(View view) {

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        txtheader = (CustomTextView) view.findViewById(R.id.txt_signin);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        txtheader.setText(getString(R.string.your_jobs_header));

        tabLayout.setupWithViewPager(viewPager);

        // setupViewPager(viewPager);
        // wrapTabIndicatorToTitle(tabLayout, 30, 30);

        baseActivity = new BaseActivity();

        adapter = new BaseActivity.ViewPagerAdapter(getChildFragmentManager());

        //adapter.addFragment(new YourJobsPendingFragment(), getResources().getString(R.string.yourjobs_pending));

        adapter.addFragment(new YourJobsUpcomingFragment(), getResources().getString(R.string.yourjobs_upcoming));

        adapter.addFragment(new YourJobsPastFragment(), getResources().getString(R.string.yourjobs_past));

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        baseActivity.wrapTabIndicatorToTitle(tabLayout);

        //  viewPager.setPageTransformer(true, new BaseActivity.ZoomOutPageTransformer()); // Zoom-out page transformer

        viewPager.setPageTransformer(true, new BaseActivity.DepthPageTransformer()); // Depth page transformer

        imgbtnBack.setOnClickListener(v -> backButtonFunction());

        if (getArguments() != null) {
            if (Objects.equals(getArguments().getString("main"), "pending")) {
               // selectPage(0);
            } else if (Objects.equals(getArguments().getString("main"), "upcoming")) {
                selectPage(0);
            }
        } else {
            selectPage(0);
        }

    }

    public static void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        viewPager.setCurrentItem(pageIndex);
        adapter.notifyDataSetChanged();
    }

    private void setupViewPager(ViewPager viewPager) {

        /*adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new YourJobsPendingFragment(), getString(R.string.yourjobs_pending));
        adapter.addFragment(new YourJobsUpcomingFragment(), getString(R.string.yourjobs_upcoming));
        adapter.addFragment(new YourJobsPastFragment(), getString(R.string.yourjobs_past));

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/
    }


    @SuppressLint("ObsoleteSdkInt")
    private void setMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        setMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        setMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        setMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);

        }
    }

    public void onBackButton(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    backButtonFunction();
                }
                return true;
            }
        });
    }

    public void backButtonFunction() {

        // removeFragment(Utils.FRAGMENT_YOURJOBS);
        Utils.CURRENT_TAG = null;
        startActivity(new Intent(requireActivity().getApplicationContext(), MainActivity.class));
        requireActivity().overridePendingTransition(R.anim.fade_in_speed, R.anim.fade_out_speed);
        requireActivity().finish();
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }

    @Override
    public void onDestroy() {
        if (adViewFacebook != null) {
            adViewFacebook.destroy();
        }
        if (adViewGoogle != null) {
            adViewGoogle.destroy();
        }
        super.onDestroy();
        Log.e("TAG", "onDestroy: ~~~~~~~~~~~~~~~~~~~");
    }

    @Override
    public void onDestroyView() {
        if (adViewFacebook != null) {
            adViewFacebook.destroy();
        }
        if (adViewGoogle != null) {
            adViewGoogle.destroy();
        }
        super.onDestroyView();
        Log.e("TAG", "onDestroyView: ~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
