package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.BonusAllAdapter;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.RideHistoryData;
import com.instaguard.provider.Models.RideHistoryModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class YourWalletTransAllBonusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    View view;

    SharedPreferences SP_Prefs;

    SharedPreferences.Editor putSpState;
    String userId;
    ImageButton imgbtnBack;
    SharedPreferences spCommon;
    String userAccessToken;
    RecyclerView rvPastJobs;
    RelativeLayout rlEmptyview;

    BonusAllAdapter adapter;

    public String TAG = "OnPastJobsFragment";


    SharedPreferences getspCurrency;
    String strLanguage, strLanguageShortName;

    /*Pagination process */
    private boolean stillavailable = true;
    private int mCurrentPage = 0;
    private boolean loading = true;
    private int visibleItemCount, totalItemCount, lastVisibleItemPosition, firstVisibleItemPosition;

    private RecyclerView.LayoutManager layoutManager;
    private RideHistoryData riderHistoryData;

    private ArrayList<RideHistoryData> eArrayList;
    /*Pagination process */


    public YourWalletTransAllBonusFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static YourWalletTransAllBonusFragment newInstance(String param1, String param2) {
        YourWalletTransAllBonusFragment fragment = new YourWalletTransAllBonusFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yourwallet_all, container, false);

        SP_Prefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        Log.e("userId~~OnGoingJobsFragment~~", userId);

        spCommon = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        putSpState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getspCurrency = requireActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = getspCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        rvPastJobs = (RecyclerView) view.findViewById(R.id.rv_wallet_all);
        rlEmptyview = (RelativeLayout) view.findViewById(R.id.rl_emptyview);

        /*swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);*/
        layoutManager = new LinearLayoutManager(getActivity());
        rvPastJobs.setLayoutManager(layoutManager);

        rvPastJobs.addOnScrollListener(new RecyclerView.OnScrollListener() { //*Pagination process 18-8-2016 Start*//*
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    Log.w(TAG, "onScrolled1: " + visibleItemCount + " " + totalItemCount);
                    firstVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                    lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                    Log.w(TAG, "onScrolled1: " + firstVisibleItemPosition + " " + lastVisibleItemPosition);
                    if (loading) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            loading = false;
                            if (mCurrentPage != 0 && stillavailable) {
                                Log.w("Scroll true", "" + "true");
                                //progressBarLoadMore.setVisibility(View.VISIBLE);
                                getWalletTransactionAllCall("load");
                            } else {
                                Log.w(TAG, "onScrolled: else ~~~");
                            }
                        }
                    } else {
                        Log.w(TAG, "onScrolled: else ~~~");
                    }
                }
            }
        }); //*Pagination process 18-8-2016 End*/


        getWalletTransactionAllCall("home");

        onBackButton(view);

        return view;
    }

    private void getWalletTransactionAllCall(String key) {

        userAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        map.put("language", strLanguageShortName);
        map.put("offset", mCurrentPage);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RideHistoryModel> call2 = apiInterface.doBonusTransactionAll("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<RideHistoryModel>() {
            @Override
            public void onResponse(@NonNull Call<RideHistoryModel> call, @NonNull Response<RideHistoryModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Transactions listed successfully.")) {
                        List<RideHistoryData> getve = Objects.requireNonNull(response.body()).getResult();
                        Log.i(TAG, "onResponse: getUpcomingJobsCall-->" + getve);
                        if (getve.size() > 0) {

                            if (!key.equals("load")) {

                                eArrayList = new ArrayList<>();
                                for (int i = 0; i < getve.size(); i++) {
                                    riderHistoryData = getve.get(i);
                                    eArrayList.add(riderHistoryData);
                                }

                                rlEmptyview.setVisibility(View.GONE);
                                rvPastJobs.setVisibility(View.VISIBLE);
                                adapter = new BonusAllAdapter(getActivity(), eArrayList);
                                LinearLayoutManager horizontalLayoutManager
                                        = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                rvPastJobs.setLayoutManager(horizontalLayoutManager);
                                rvPastJobs.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                hideProgressDialog();

                            } else {

                                ArrayList<RideHistoryData> eArrayList = new ArrayList<>();
                                for (int i = 0; i < getve.size(); i++) {
                                    riderHistoryData = getve.get(i);
                                    eArrayList.add(riderHistoryData);
                                }

                                adapter.updateAdapter(eArrayList);
                                adapter.notifyDataSetChanged();
                            }

                            if (getve.size() == 0 ||
                                    getve.size() < 8) {
                                loading = false;
                                stillavailable = false;
                            } else {
                                mCurrentPage = response.body().getNextPage();
                                loading = true;
                                stillavailable = true;
                            }

                        } else {
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvPastJobs.setVisibility(View.GONE);
                        }

                    } else {
                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvPastJobs.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideHistoryModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                Toast.makeText(getActivity(), "please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("token", userAccessToken);
        Log.e("Logout~~Refresh~~~", "Refresh~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RefreshTokenModel> call2 = apiInterface.doGetRefreshToken(Utils.POST_ACCEPT_CONTENT_TYPE, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<RefreshTokenModel>() {
            @Override
            public void onResponse(@NonNull Call<RefreshTokenModel> call, @NonNull Response<RefreshTokenModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    String refreshToken = Objects.requireNonNull(response.body()).getAccessToken();
                    String refreshTokenType = Objects.requireNonNull(response.body()).getTokenType();
                    String refreshTokenTimeExp = String.valueOf(Objects.requireNonNull(response.body()).getExpiresIn());
                    savesharedpreference(refreshToken, refreshTokenType, refreshTokenTimeExp);
                    if (logout.equalsIgnoreCase("Category")) {
                        getWalletTransactionAllCall("home");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    showTimeOut();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RefreshTokenModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void savesharedpreference(String access_token, String token_type, String exp_time) {

        putSpState.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        putSpState.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        putSpState.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        putSpState.apply();
    }

    public void showTimeOut() {

        Context mContext = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.timeout_title));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getString(R.string.your_session_time_out_content));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        builder.setMessage(spannableStrings);
        builder.setPositiveButton(spannableStringss,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSPmodePrivate();
                    }
                });

        builder.show();
    }

    private void clearSPmodePrivate() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        // deleteCache(MainActivity.this);
        Intent logout = new Intent(getActivity(), HomeActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);
        getActivity().finish();
    }

    /*private void showSnackbar(String s, String key) {


        final TSnackbar snackbar = TSnackbar.make(getActivity().findViewById(android.R.id.content), s, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        (snackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        snackbar.setMaxWidth(FrameLayout.LayoutParams.MATCH_PARENT);

        if (key.equalsIgnoreCase("warning")) {
            snackbarView.setBackgroundColor(Color.RED);
        } else {
            snackbarView.setBackgroundColor(Color.GRAY);
        }

        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.App_Font));
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(font, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        // 27 >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            params.setMargins(0, 100, 0, 0);
        } else {
            // below 23
            params.setMargins(0, 50, 0, 0);
        }
        textView.setLayoutParams(params);
        snackbar.show();

    }*/

    public void showAlertDialogs(String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStrings = new SpannableString(getString(R.string.ok));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setMessage(spannableString).setPositiveButton(spannableStrings,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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
        // MainActivity.frameClickable(false);
//        removeFragment(Utils.FRAGMENT_YOUR_WALLET_TRANSACTION);
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MYWALLET,
                requireActivity(), null,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
    }
}
