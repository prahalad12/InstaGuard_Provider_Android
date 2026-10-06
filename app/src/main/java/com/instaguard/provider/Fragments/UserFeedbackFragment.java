package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.PastJobsAdapter;
import com.instaguard.provider.Adapter.UserFeedbackAdapter;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PastJobsData;
import com.instaguard.provider.Models.PastJobsModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.UserFeedbackDataModel;
import com.instaguard.provider.Models.UserFeedbackModel;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

public class UserFeedbackFragment extends Fragment {

    View view;
    SharedPreferences SP_Prefs;
    String userAccessToken,driverId;
    ImageButton imgbtnBack;
    RecyclerView rv_user_feedback;
    CustomTextView txt_no_data_found;
    UserFeedbackAdapter adapter;

    public UserFeedbackFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_userfeedback, container, false);

        SP_Prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("driverId~~UserFeedbackFragment~~", driverId);

        initView(view);

        onBackButton(view);

        getUserFeedback();

        return view;
    }

    void initView(View view) {


        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        rv_user_feedback = (RecyclerView) view.findViewById(R.id.rv_user_feedback);
        txt_no_data_found = (CustomTextView) view.findViewById(R.id.txt_no_data_found);


        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

    }
    private void getUserFeedback() {
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserFeedbackModel> call2 = apiInterface.doGetUserFeedback("Bearer " + userAccessToken,  Utils.POST_ACCEPT_CONTENT_TYPE,map);
         showProgressDialog(getActivity());
        call2.enqueue(new Callback<UserFeedbackModel>() {
            @Override
            public void onResponse(@NonNull Call<UserFeedbackModel> call, @NonNull retrofit2.Response<UserFeedbackModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("feedback listed successfully.")) {
                        // List<OnGoingJobsData> getve = response.body().getServiceRequest();
                        List<UserFeedbackDataModel> getve = Objects.requireNonNull(response.body()).getUserFeedbackDataModel();
                        Log.i("sizxx", "onResponse: getve-->"+getve.size());
                        if(getve.size() > 0) {
                            txt_no_data_found.setVisibility(View.GONE);
                            rv_user_feedback.setVisibility(View.VISIBLE);
                            adapter = new UserFeedbackAdapter(getActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rv_user_feedback.setLayoutManager(horizontalLayoutManager);
                            rv_user_feedback.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            hideProgressDialog();
                        }else{
                            txt_no_data_found.setVisibility(View.VISIBLE);
                            rv_user_feedback.setVisibility(View.GONE);
                        }
                    } else {
                       // callRefreshToken("Category");
                        hideProgressDialog();
                        txt_no_data_found.setVisibility(View.VISIBLE);
                        rv_user_feedback.setVisibility(View.GONE);
                    }
                }else if (response.code() == RESPONSECODE_401){
                    callRefreshToken("Category");
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserFeedbackModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token",userAccessToken );
        Log.e("Logout~~Refresh~~~", "Refresh~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RefreshTokenModel> call2 = apiInterface.doGetRefreshToken(Utils.POST_ACCEPT_CONTENT_TYPE,Utils.POST_ACCEPT_CONTENT_TYPE,map);
        call2.enqueue(new Callback<RefreshTokenModel>() {
            @Override
            public void onResponse(@NonNull Call<RefreshTokenModel> call, @NonNull Response<RefreshTokenModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    String refreshToken = Objects.requireNonNull(response.body()).getAccessToken();
                    String refreshTokenType = Objects.requireNonNull(response.body()).getTokenType();
                    String refreshTokenTimeExp = String.valueOf(Objects.requireNonNull(response.body()).getExpiresIn());
                    savesharedpreference(refreshToken,refreshTokenType,refreshTokenTimeExp);
                    if (logout.equalsIgnoreCase("Category")){
                        //getPastJobsCall();
                    }
                }else if (response.code() == RESPONSECODE_401){
                    showTimeOut();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RefreshTokenModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }
    private void savesharedpreference(String access_token,String token_type,String exp_time) {
/*
        putSpState.putString(Utils.SP_DRIVER_ACCESSTOKEN,access_token);
        putSpState.putString(Utils.SP_DRIVER_TOKENTYPE,token_type);
        putSpState.putString(Utils.SP_DRIVER_TOKENEXPTIME,exp_time);
        putSpState.apply();*/
    }
    public void showTimeOut() {
        Context mContext = getActivity() ;

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
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

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
      //  MainActivity.frameClickable(false);
        removeFragment(Utils.FRAGMENT_USERFEEDBACK);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }


}
