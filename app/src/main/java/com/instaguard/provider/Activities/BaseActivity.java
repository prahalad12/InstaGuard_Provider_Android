package com.instaguard.provider.Activities;

import static java.lang.Double.parseDouble;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.DigitsKeyListener;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.BuildConfig;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Firebase;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.Localizationactivity.ui.LocalizationActivity;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@SuppressLint("Registered")
public class BaseActivity extends LocalizationActivity {

    private static AlertDialog singleButtonDialog;
    private static Dialog progressDialog;

    public static void showProgressDialog(Context context) {
        try {
            /*if (BuildConfig.DEBUG)
                Log.e( "onFinish: ~~~~","~~~~Inside show 0~~~~");*/
            if (progressDialog == null) {
                progressDialog = new Dialog
                        (context, R.style.AppTheme_ProgressIndicator);
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(context).inflate(R.layout.layout_progressbar_view, null);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorprogresstransparent);
                // progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                progressDialog.setContentView(view);
                //progressDialog.setCancelable(false);
                /*if (BuildConfig.DEBUG)
                    Log.e( "onFinish: ~~~~","~~~~Inside show 1 ~~~");*/
            }
            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
           /* if (BuildConfig.DEBUG)
                Log.e( "onFinish: ~~~~",e.toString());*/
            //TODO: Fill in exception
        }
    }

    public static void hideProgressDialog() {
        /*if (BuildConfig.DEBUG)
            Log.e( "onFinish: ~~~~","~~~~0~~~~");*/
        new CountDownTimer(600, 100) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    /*if (BuildConfig.DEBUG)
                        Log.e( "onFinish: ~~~~","~~~~1~~~~");*/
                    if (progressDialog != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    progressDialog = null;

                } catch (Exception e) {
                    e.printStackTrace();
                 /* if (BuildConfig.DEBUG)
                      Log.e( "onFinish: ~~~~",e.toString());*/
                    //TODO: Fill in exception
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    public static class CustomTFSpan extends TypefaceSpan {

        private Typeface typeface;

        public CustomTFSpan(Typeface typeface) {
            super("");
            this.typeface = typeface;
        }

        private static void applyTypeFace(Paint paint, Typeface tf) {
            paint.setTypeface(tf);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyTypeFace(ds, typeface);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyTypeFace(paint, typeface);
        }
    }

    // Custom method to get screen width in dp/dip using Context object
    public static int getScreenWidthInDPs(Context context) {
        /*
            DisplayMetrics
                A structure describing general information about a display,
                such as its size, density, and font scaling.
        */
        DisplayMetrics dm = new DisplayMetrics();

        /*
            WindowManager
                The interface that apps use to talk to the window manager.
                Use Context.getSystemService(Context.WINDOW_SERVICE) to get one of these.
        */

        /*
            public abstract Object getSystemService (String name)
                Return the handle to a system-level service by name. The class of the returned
                object varies by the requested name. Currently available names are:

                WINDOW_SERVICE ("window")
                    The top-level window manager in which you can place custom windows.
                    The returned object is a WindowManager.
        */

        /*
            public abstract Display getDefaultDisplay ()

                Returns the Display upon which this WindowManager instance will create new windows.

                Returns
                The display that this window manager is managing.
        */

        /*
            public void getMetrics (DisplayMetrics outMetrics)
                Gets display metrics that describe the size and density of this display.
                The size is adjusted based on the current rotation of the display.

                Parameters
                outMetrics A DisplayMetrics object to receive the metrics.
        */
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }

    // Custom method to get screen height in dp/dip using Context object
    public static int getScreenHeightInDPs(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        /*
            In this example code we converted the float value
            to nearest whole integer number. But, you can get the actual height in dp
            by removing the Math.round method. Then, it will return a float value, you should
            also make the necessary changes.
        */

        /*
            public int heightPixels
                The absolute height of the display in pixels.

            public float density
             The logical density of the display.
        */
        int heightInDP = Math.round(dm.heightPixels / dm.density);
        return heightInDP;
    }

    public static String getGoogleApiKey(Context context_) {

        Object value_;

        PackageManager manager_ = context_.getPackageManager();
        PackageInfo info_ = null;
        try {
            info_ = manager_.getPackageInfo(context_.getPackageName(), PackageManager.GET_META_DATA);
            value_ = info_.applicationInfo.metaData.get("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            value_ = "";
            //value_ = BuildConfig.API_KEY;
        }
        return value_.toString();
    }


    /**************************************************************************************
     * Get decimal formated string to include comma seperator to decimal number
     *
     * @param value
     *
     **************************************************************************************/
    public static String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            StringBuilder str3 = new StringBuilder();
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = new StringBuilder(".");
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3.append(".").append(str2);
                    return str3.toString();
                }
                if (i == 3) {
                    str3.insert(0, ",");
                    i = 0;
                }
                str3.insert(0, str1.charAt(k));
                i++;
            }
        }
        return "";
    }

    /****************************************************************************************
     *
     * Restrict digits after decimal point value as per currency
     *
     ****************************************************************************************/
    static class EditTextValueFilter extends DigitsKeyListener {

        private final int digits;

        public EditTextValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
        }
    }

    public static class MySpinnerAdapter extends ArrayAdapter<String> {
        Typeface font;
        private final Context context;

        public MySpinnerAdapter(Context context_, int resource, String[] items) {
            super(context_, resource, items);
            context = context_;
            font = Typeface.createFromAsset(context_.getAssets(), context_.getString(R.string.App_Font));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            view.setTextColor(context.getResources().getColor(R.color.black));
            return view;
        }

        @NonNull
        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            /*if(position %2 == 0){
                view.setBackground(context.getResources().getDrawable(R.color.colorLeftBack));
            } else {
                view.setBackground(context.getResources().getDrawable(R.color.colorRightBack));
            }*/
            return view;
        }
    }


    public void callGoogleAdsCommon(Context context, com.google.android.gms.ads.AdView adView) {
        /*try {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void callFacebookAdsCommon(Context context, AdView adView, LinearLayout adContainer) {
        /*try {
            if (AudienceNetworkAds.isInitialized(context)) {
                //return;
                System.out.println("~~~~isInAdsProcess~");
            } else {
                System.out.println("~~~~isInAdsProcess~not~~~");
            }

            if (BuildConfig.DEBUG) {
                AdSettings.setTestMode(true);
                //AdSettings.addTestDevice("190165ba-eb58-4bd0-8d29-103a45e7e85d");
            }

            if (BuildConfig.DEBUG) {
                adView = new AdView(context, "IMG_16_9_LINK#1400850877185021_1400851810518261", AdSize.BANNER_HEIGHT_50);
            } else {
                adView = new AdView(context, "1400850877185021_1400851810518261", AdSize.BANNER_HEIGHT_50);
            }

            if (BuildConfig.DEBUG) {
                System.out.println("~~~~adView~~~~" + adView.getPlacementId());
                System.out.println("~~~~adView~~~~" + adView.isAdInvalidated());
            }
            adContainer.setVisibility(View.VISIBLE);
            adContainer.addView(adView);
            // Request an ad
            adView.loadAd();

        } catch (Exception e) {
            e.printStackTrace();
            adContainer.setVisibility(View.INVISIBLE);
        }*/

    }


    /*TabLayout Fragment Adapter*/
    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
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

    @SuppressLint("ObsoleteSdkInt")
    public void setMargin(ViewGroup.MarginLayoutParams layoutParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(50);
            layoutParams.setMarginEnd(50);
        } else {
            layoutParams.leftMargin = 50;
            layoutParams.rightMargin = 50;
        }
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout) {
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
                        setMargin(layoutParams);
                    } else if (i == childCount - 1) {
                        // right
                        setMargin(layoutParams);
                    } else {
                        // internal
                        setMargin(layoutParams);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }


    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    public void animateMarkerTo(final Marker marker, double[] startValues, double[] endValues, final float bearing, GoogleMap mMap, Location locFusedLocation) {

        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new BaseActivity.DoubleArrayEvaluator(), startValues, endValues);
        latLngAnimator.setDuration(1000);
        latLngAnimator.setInterpolator(new DecelerateInterpolator());
        latLngAnimator.addUpdateListener(animation -> {
            double[] animatedValue = (double[]) animation.getAnimatedValue();
            marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
        });
        latLngAnimator.start();
        marker.setRotation(bearing);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(Utils.MAP_ONTRIP_ZOOM)      // Sets the zoom
                //.bearing(getBearing)            // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static class DoubleArrayEvaluator implements TypeEvaluator<double[]> {

        private double[] mArray;

        /**
         * Create a DoubleArrayEvaluator that does not reuse the animated value. Care must be taken
         * when using this option because on every evaluation a new <code>double[]</code> will be
         * allocated.
         *
         * @see #DoubleArrayEvaluator(double[])
         */
        public DoubleArrayEvaluator() {
        }

        /**
         * Create a DoubleArrayEvaluator that reuses <code>reuseArray</code> for every evaluate() call.
         * Caution must be taken to ensure that the value returned from
         * {@link ValueAnimator#getAnimatedValue()} is not cached, modified, or
         * used across threads. The value will be modified on each <code>evaluate()</code> call.
         *
         * @param reuseArray The array to modify and return from <code>evaluate</code>.
         */
        public DoubleArrayEvaluator(double[] reuseArray) {
            mArray = reuseArray;
        }

        /**
         * Interpolates the value at each index by the fraction. If
         * {@link #DoubleArrayEvaluator(double[])} was used to construct this object,
         * <code>reuseArray</code> will be returned, otherwise a new <code>double[]</code>
         * will be returned.
         *
         * @param fraction   The fraction from the starting to the ending values
         * @param startValue The start value.
         * @param endValue   The end value.
         * @return A <code>double[]</code> where each element is an interpolation between
         * the same index in startValue and endValue.
         */
        @Override
        public double[] evaluate(float fraction, double[] startValue, double[] endValue) {
            double[] array = mArray;
            if (array == null) {
                array = new double[startValue.length];
            }

            for (int i = 0; i < array.length; i++) {
                double start = startValue[i];
                double end = endValue[i];
                array[i] = start + (fraction * (end - start));
            }
            return array;
        }
    }


    public static class DepthPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
    /*TabLayout Fragment Adapter*/

    public String addNumbers(CustomEditText edtMaterialFee, CustomEditText edtMiscFee, CustomEditText edtDiscountDriver,
                              CustomTextView txtCurrentCharge, CustomEditText txtadditionalAgentCost, CustomEditText txtAdditionalDayCharge) {

        double number1, number2, number3, number4, number5, number6;

        if (!edtMaterialFee.getText().toString().replaceAll(",", "").equals("") && !edtMaterialFee.getText().toString().equals("") && edtMaterialFee.getText().length() > 0) {
            number1 = Double.parseDouble(edtMaterialFee.getText().toString().replaceAll(",", ""));
            //Log.e("number1~~", String.valueOf(number1));
        } else {
            number1 = 0;
        }
        if (!edtMiscFee.getText().toString().replaceAll(",", "").equals("") && !edtMiscFee.getText().toString().equals("") && edtMiscFee.getText().length() > 0) {
            number2 = Double.parseDouble(edtMiscFee.getText().toString().replaceAll(",", ""));
            //Log.e("number2~~", String.valueOf(number2));
        } else {
            number2 = 0.0;
        }
        if (!edtDiscountDriver.getText().toString().replaceAll(",", "").equals("") && !edtDiscountDriver.getText().toString().equals("") && edtDiscountDriver.getText().length() > 0) {
            number3 = Double.parseDouble(edtDiscountDriver.getText().toString().replaceAll(",", ""));
            //Log.e("number3~~", String.valueOf(number3));
        } else {
            number3 = 0.0;
        }

        if (!txtCurrentCharge.getText().toString().replaceAll(",", "").equals("") && !txtCurrentCharge.getText().toString().equals("") && txtCurrentCharge.getText().length() > 0) {
            number4 = /*Math.round(*/Double.parseDouble(txtCurrentCharge.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number3));
        } else {
            number4 = 0.0;
        }

        if (!txtadditionalAgentCost.getText().toString().replaceAll(",", "").equals("") && !txtadditionalAgentCost.getText().toString().equals("") && txtadditionalAgentCost.getText().length() > 0) {
            number5 = /*Math.round(*/Double.parseDouble(txtadditionalAgentCost.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number3));
        } else {
            number5 = 0.0;
        }

        if (!txtAdditionalDayCharge.getText().toString().replaceAll(",", "").equals("") && !txtAdditionalDayCharge.getText().toString().equals("") && txtAdditionalDayCharge.getText().length() > 0) {
            number6 = /*Math.round(*/Double.parseDouble(txtAdditionalDayCharge.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number3));
        } else {
            number6 = 0.0;
        }

        double value = parseDouble(String.valueOf(number4 + number1 + number2 + number5 + number6 - number3));

        System.out.println("Total--->>" + (int) DecimalUtils.round(value, 2));

        //return Integer.toString(number4 + number1 + number2 - number3);
        return Integer.toString((int) DecimalUtils.round(value, 2));

    }

    public String addNumbersDistance(CustomEditText edtMaterialFee, CustomEditText edtMiscFee, CustomEditText edtDiscountDriver,
                                      CustomTextView txtCurrentCharge,CustomTextView txtDistanceAdditionalCharge, CustomEditText txtadditionalAgentCost,
                                      CustomEditText txtAdditionalDayCharge) {

        double number1, number2, number3, number4;
        double number5;
        double number6;
        double number7;

        if (!edtMaterialFee.getText().toString().replaceAll(",", "").equals("") && !edtMaterialFee.getText().toString().equals("") && edtMaterialFee.getText().length() > 0) {
            number1 = Double.parseDouble(edtMaterialFee.getText().toString().replaceAll(",", ""));
            // Log.e("number1~~", String.valueOf(number1));
        } else {
            number1 = 0.0;
        }
        if (!edtMiscFee.getText().toString().replaceAll(",", "").equals("") && !edtMiscFee.getText().toString().equals("") && edtMiscFee.getText().length() > 0) {
            number2 = Double.parseDouble(edtMiscFee.getText().toString().replaceAll(",", ""));
            // Log.e("number2~~", String.valueOf(number2));
        } else {
            number2 = 0.0;
        }
        if (!edtDiscountDriver.getText().toString().replaceAll(",", "").equals("") && !edtDiscountDriver.getText().toString().equals("") && edtDiscountDriver.getText().length() > 0) {
            number3 = Double.parseDouble(edtDiscountDriver.getText().toString().replaceAll(",", ""));
            // Log.e("number3~~", String.valueOf(number3));
        } else {
            number3 = 0.0;
        }

        if (!txtCurrentCharge.getText().toString().replaceAll(",", "").equals("")
                && !txtCurrentCharge.getText().toString().equals("") && txtCurrentCharge.getText().length() > 0) {
            number4 = /*Math.round(*/Double.parseDouble(txtCurrentCharge.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number4));
        } else {
            number4 = 0.0;
        }

        if (!txtDistanceAdditionalCharge.getText().toString().replaceAll(",", "").equals("")
                && !txtDistanceAdditionalCharge.getText().toString().equals("") && txtDistanceAdditionalCharge.getText().length() > 0) {
            // number5 = (int) Math.round(Double.parseDouble(txtDistanceAdditionalCharge.getText().toString()));
            // number5 = Math.round(Double.parseDouble(txtDistanceAdditionalCharge.getText().toString()));
            number5 = Double.parseDouble(txtDistanceAdditionalCharge.getText().toString().replaceAll(",", ""));
            // Log.e("number5~~", String.valueOf(number5));
        } else {
            number5 = 0.0;
        }


        if (!txtadditionalAgentCost.getText().toString().replaceAll(",", "").equals("") && !txtadditionalAgentCost.getText().toString().equals("") && txtadditionalAgentCost.getText().length() > 0) {
            number7 = /*Math.round(*/Double.parseDouble(txtadditionalAgentCost.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number3));
        } else {
            number7 = 0.0;
        }

        if (!txtAdditionalDayCharge.getText().toString().replaceAll(",", "").equals("") && !txtAdditionalDayCharge.getText().toString().equals("") && txtAdditionalDayCharge.getText().length() > 0) {
            number6 = /*Math.round(*/Double.parseDouble(txtAdditionalDayCharge.getText().toString().replaceAll(",", "")/*)*/);
            //Log.e("number4~~", String.valueOf(number3));
        } else {
            number6 = 0.0;
        }

        double value = parseDouble(String.valueOf(number6 + number7 + number5 + number4 + number1 + number2 - number3));

        // System.out.println("Total--number->>" + number6 + number5 + number4 + number1 + number2 + number3);

        System.out.println("Total--->>" + DecimalUtils.round(value, 2));

        // return String.valueOf((number5 + number4 + number1 + number2 - number3));
        return String.valueOf(DecimalUtils.round(value, 2));
    }


    @SuppressLint("DefaultLocale")
    public static String convertCurrencyPrice(Context context_, String price) {
        try {
            if (price != null) {
                if (!price.equals("null")) {
                    if (!price.equals("")) {
                        String currencyCommonPrice = context_.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE)
                                .getString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                        if (BuildConfig.DEBUG) {
                            Log.i("currencyCommonPrice", "~~~" + currencyCommonPrice);
                            Log.i("price", "~~~" + price);
                        }
                        double multiplePrice = Double.parseDouble(currencyCommonPrice) * Double.parseDouble(price);
                        double roundedNumber = DecimalUtils.round(multiplePrice, 2);
                        if (BuildConfig.DEBUG) {
                            Log.i("price", "~~String~~" + roundedNumber);
                            Log.i("price", "~~String~~" + String.format("%.2f", roundedNumber));
                        }

                        int roundedValue = (int) Math.round(roundedNumber);
                        if (roundedValue == roundedNumber) {
                            System.out.println("Value is rounded to " + roundedValue);
                            return String.valueOf(roundedValue);
                        } else if (roundedNumber == roundedNumber) {
                            System.out.println("Value is " + roundedNumber);
                            return String.format("%.2f", roundedNumber);
                        } else {
                            return String.format("%.2f", roundedNumber);
                        }

                        //return String.valueOf(roundedNumber);
                        //---? correct return String.format("%.2f", roundedNumber);
                    } else {
                        return "0";
                    }
                } else {
                    return "0";
                }
            } else {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG)
                Log.e(e.toString(), "convertCurrencyPrice: ~~~~~");
            return "0";
        }
    }

    public static String convertHoursToDaysHoursMinutes(String hours_) {

        try {

            // Convert double to long
            long hours = (long) Double.parseDouble(hours_);
            //long hours = Long.parseLong(longValue);

            long days = TimeUnit.HOURS.toDays(hours);
            long remainingHours = hours % 24;

            if (com.instaguard.provider.BuildConfig.DEBUG) {
                System.out.println("~~~~convertHoursToDays~~~~" + days);
                System.out.println("~~~~convertHoursToMinutes~~~~" + remainingHours);
            }

            StringBuilder resultStringBuilder = new StringBuilder();

            if (days > 0) {
                resultStringBuilder.append(days).append(" days ");
            } else {
                resultStringBuilder.append("");
            }

            if (remainingHours > 0) {
                if (remainingHours == 1) {
                    resultStringBuilder.append(remainingHours).append(" hr");
                } else {
                    resultStringBuilder.append(remainingHours).append(" hrs");
                }
            } else {
                resultStringBuilder.append("");
            }

            return resultStringBuilder.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            if (Double.parseDouble(hours_) > 0.0) {
                if (Double.parseDouble(hours_) == 1.0) {
                    return hours_ + " hr";
                } else {
                    return hours_ + " hrs";
                }
            } else {
                return hours_ + " hrs";
            }
        }
    }

    public static Double convertKmsToMiles(Context context, Double kms) {
        double miles = 0.0;
        if (convertDistanceUnit(context).equals("mile")) {
            miles = 0.621371 * kms;
        } else if (convertDistanceUnit(context).equals("km")) {
            miles = kms;
        }
        return DecimalUtils.round(miles, 3);
    }

    public static String convertDistanceUnit(Context context) {

        String strDistanceUnit = context.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).getString(Utils.SP_COMMON_DISTANCE_UNIT, null);
        String miles;
        if (strDistanceUnit != null) {
            if (!strDistanceUnit.equals("null")) {
                if (!strDistanceUnit.equals("")) {
                    if (!strDistanceUnit.equals(" ")) {
                        switch (strDistanceUnit) {
                            case "miles":
                            case "Miles":
                            case "mile":
                            case "Mile":
                                miles = "mile";
                                break;
                            case "KM":
                            case "km":
                            case "kilometers":
                            case "kilometer":
                            case "Kilometer":
                            case "Kilometers":
                            default:
                                miles = "km";
                                break;
                        }
                    } else {
                        miles = "km";
                    }
                } else {
                    miles = "km";
                }
            } else {
                miles = "km";
            }
        } else {
            miles = "km";
        }
        return miles;
    }

    public static String convertCurrencyDividePrice(Context context, String price) {
        price = price.replaceAll(",", "");
        double roundedNumber = 0;
        if (!price.equals("null")) {
            if (!price.equals("")) {
                String currencyCommonPrice = context.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).getString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                double multiplePrice = Double.parseDouble(price) / Double.parseDouble(currencyCommonPrice);
                roundedNumber = DecimalUtils.round(multiplePrice, 2);
            }
        }
        return String.valueOf(roundedNumber);
    }

    public static String convertCurrencyDivideWithoutRoundPrice(Context context, String price) {
        /*String currencyCommonPrice = spCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, null);
        Double multiplePrice Double.parseDouble(price) / Double.parseDouble(currencyCommonPrice);
        return String.valueOf(multiplePrice);*/

        price = price.replaceAll(",", "");
        double multiplePrice = 0;
        if (!price.equals("null")) {
            if (!price.equals("")) {
                String currencyCommonPrice = context.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).getString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                multiplePrice = Double.parseDouble(price) / Double.parseDouble(currencyCommonPrice);
                // roundedNumber = DecimalUtils.round(multiplePrice, 2);
            }
        }
        return String.valueOf(multiplePrice);
    }

    public static float meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat_b - lat_a);
        double dLng = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        System.out.println("**********this is distance calculation**********" + dist);
        return dist;
    }

    //get distance between two location
    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        System.out.println("Lat---" + StartP + "Long---" + EndP);
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;

        double km = valueResult / 1;

        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));

        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public static void callClickAction(View view, Boolean b) {
        view.setEnabled(b);
        view.setFocusableInTouchMode(b);
        view.setFocusable(b);
        view.setClickable(b);
        if (!b) {
            view.setAlpha(0.6f);
        } else {
            view.setAlpha(1f);
        }
    }

    public static String checkStringEmpty(DataSnapshot data_, String key) {
        String strFrom;
        if (data_.child(key).getValue() != null) {
            strFrom = Objects.requireNonNull(data_.child(key).getValue()).toString();
        } else {
            strFrom = "";
        }
        return strFrom;
    }

    public static String checkStringEmptyZero(DataSnapshot data_, String key) {
        String strFrom;
        if (data_.child(key).getValue() != null) {
            strFrom = Objects.requireNonNull(data_.child(key).getValue()).toString();
        } else {
            strFrom = "0";
        }
        return strFrom;
    }

    public static String checkStringEmptyDecimalZero(DataSnapshot data_, String key) {
        String strFrom;
        if (data_.child(key).getValue() != null) {
            strFrom = Objects.requireNonNull(data_.child(key).getValue()).toString();
        } else {
            strFrom = "0.0";
        }
        return strFrom;
    }

    public static void setVisibility(View view, boolean visibility) {
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    public void hideKeyBoard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserId(String userId) {
        FirebaseCrashlytics.getInstance().setUserId(userId);
    }
    public static void showSnackbar(Context context, View view, String s, String key) {

        Snackbar snackbars = Snackbar.make(view, s, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarViews = snackbars.getView();
        TextView textViews = snackbarViews.findViewById(com.google.android.material.R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.App_Font));
        textViews.setTypeface(font, Typeface.BOLD);

        if (key.equalsIgnoreCase("warning")) {
            snackbarViews.setBackgroundColor(Color.RED);
        } else if (key.equalsIgnoreCase("Field")){
            snackbarViews.setBackgroundColor(Color.GRAY);
        } else if (key.equalsIgnoreCase("other")){
            snackbarViews.setBackgroundColor(Color.GREEN);
        }

        View snackbarView = snackbars.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.height = 150;
        snackbarView.setLayoutParams(params);

        snackbars.show();
    }
}
