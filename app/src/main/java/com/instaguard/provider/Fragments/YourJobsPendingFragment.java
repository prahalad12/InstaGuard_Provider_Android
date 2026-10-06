package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.PendingJobsAdapter;
import com.instaguard.provider.Adapter.UpcomingJobsAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.DeclinePendingModel;
import com.instaguard.provider.Models.PendingJobsData;
import com.instaguard.provider.Models.PendingJobsModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.UpcomingJobsData;
import com.instaguard.provider.Models.UpcomingJobsModel;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.MainActivity.callPendingUpcomingStatus;
import static com.instaguard.provider.Activities.MainActivity.txtPendingCount;
import static com.instaguard.provider.Fragments.YourJobsFragment.selectPage;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

public class YourJobsPendingFragment extends Fragment implements PendingJobsAdapter.SuccessResponse {
    // TODO: Rename parameter arguments, choose names that match

    View view;
    CustomButton PaymentReceive;
    CustomEditText editemail;

    SharedPreferences SP_Prefs;
    SharedPreferences.Editor putSpState;
    String userAccessToken;
    String driverId;
    private AwesomeValidation Validation;

    RecyclerView rvPendingJobs;
    RelativeLayout rlEmptyview;
    RelativeLayout progressBar;

    public String strBookid;

    public String TAG = "YourJobsPendingFragment";
    PendingJobsAdapter adapter;

    CountDownTimer countDownTimerPending;

    Timer timer;


    DatabaseReference driverAvailableDatabaseBRef, driverDistanceDatabaseBRef;
    ValueEventListener driverAvailableValueEventListener, driverDistanceValueEventListener;

    public YourJobsPendingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static YourJobsPendingFragment newInstance(String param1, String param2) {
        YourJobsPendingFragment fragment = new YourJobsPendingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Log.e(TAG, "setUserVisibleHint: ~~1~~~");
            aBoolean = false;
        } else {
            Log.e(TAG, "setUserVisibleHint: ~~2~~~");
        }
    }


    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yourjobs_pending, container, false);
        SP_Prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        putSpState = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("driverId~~YourJobsPastFragment~~", driverId + "--" + userAccessToken);
        PendingJobsAdapter.successResponse = this;

        initView(view);

        Log.e("userId~~PastJobsFragment~~", driverId);
        //  spCommon = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);


        rvPendingJobs = (RecyclerView) view.findViewById(R.id.rv_pendingjobs);
        rlEmptyview = (RelativeLayout) view.findViewById(R.id.rl_emptyview_pending);
        progressBar = (RelativeLayout) view.findViewById(R.id.progressBar);


        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initView(view);

        getDriverOnlineOrOffline();

        onBackButton(view);

        return view;
    }

    void initView(View view) {


        OnClick();
    }

    private void OnClick() {


    }

    //get Driver online offlne status in firebase
    private void getDriverOnlineOrOffline() {

        if (driverId != null) {

            if (driverAvailableDatabaseBRef != null) {
                driverAvailableDatabaseBRef.removeEventListener(driverAvailableValueEventListener);
            }
            driverAvailableDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
            driverAvailableValueEventListener = driverAvailableDatabaseBRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        try {
                            if (dataSnapshot.child("pending").getValue() != null) {

                                Object strPendingcountFB = Objects.requireNonNull(dataSnapshot.child("pending").getValue()).toString();


                                if (strPendingcountFB != null) {

                                    if (!strPendingcountFB.toString().equals("0")) {

                                        getPendingJobsCall();

                                        if (driverAvailableDatabaseBRef != null) {
                                            driverAvailableDatabaseBRef.removeEventListener(driverAvailableValueEventListener);
                                        }

                                    } else {

                                        // Log.e(TAG, "onDataChange: ~~~~aBoolean~~~~~~"+aBoolean);

                                        if (!aBoolean) {
                                            getPendingJobsCall();
                                            aBoolean= true;
                                        }
                                    }
                                } else {
                                    txtPendingCount.setText("0");
                                }
                            } else {

                                progressBar.setVisibility(View.GONE);
                                rlEmptyview.setVisibility(View.VISIBLE);
                                rvPendingJobs.setVisibility(View.GONE);

                                Log.e(TAG, "onDataChange:NULL Pending@upcoming");
                                // getPendingJobsCall();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onDataChange:Exception Pending@upcoming" + e);
                        }

                    }else
                        Log.e(TAG, "onDataChange: ~~~~~5~~~~~~");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onDataChange: ~~~~~6~~~~~~"+databaseError.toString());
                }
            });
        } else {
            try {
                Log.e(TAG, "getDriverOnlineOrOffline: " + "exe");
            } catch (Exception e) {
                Log.e(TAG, "getDriverOnlineOrOffline: ", e);
            }
        }
    }

    private boolean aBoolean = false;
    private boolean bBoolean = false;

    private void getPendingJobsCall() {
        progressBar.setVisibility(View.VISIBLE);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", driverId);
        map.put("mode", "driver");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PendingJobsModel> call2 = apiInterface.doCustomerPendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

        call2.enqueue(new Callback<PendingJobsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<PendingJobsModel> call, @NonNull retrofit2.Response<PendingJobsModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Fetching pending jobs successful.")) {
                        List<PendingJobsData> getve = Objects.requireNonNull(response.body()).getServiceRequest();
                        Log.i(TAG, "onResponse: getPastJobsCall-->" + getve);
                        if (getve.size() > 0) {
                            txtPendingCount.setText(Integer.toString(getve.size()));
                            rlEmptyview.setVisibility(View.GONE);
                            rvPendingJobs.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            adapter = new PendingJobsAdapter(getActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvPendingJobs.setLayoutManager(horizontalLayoutManager);
                            rvPendingJobs.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            //callPendingUpcomingStatus(driverId,"pending",String.valueOf(Integer.toString(getve.size())));
                        } else {
                            txtPendingCount.setText("0");
                            if (!bBoolean) {
                                callPendingUpcomingStatus(driverId, "pending", "0");
                                bBoolean = true;
                            }
                            // callPendingUpcomingStatus(driverId,"pending","0");
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvPendingJobs.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                        }
                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("No records Found.")) {
                        progressBar.setVisibility(View.GONE);
                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvPendingJobs.setVisibility(View.GONE);
                    } else {
                        callRefreshToken("Category");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("Category");
                }
            }

            @Override
            public void onFailure(Call<PendingJobsModel> call, Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptPendingJobs() {
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookid);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DeclinePendingModel> call2 = apiInterface.doAcceptPendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        progressBar.setVisibility(View.VISIBLE);
        call2.enqueue(new Callback<DeclinePendingModel>() {
            @Override
            public void onResponse(@NonNull Call<DeclinePendingModel> call, @NonNull retrofit2.Response<DeclinePendingModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("successfully updated")) {
                        // List<OnGoingJobsData> getve = response.body().getServiceRequest();
                        String getStatus = Objects.requireNonNull(response.body()).getMessage();
                        Log.i(TAG, "onResponse: cancelUpcomingJobs-->" + getStatus);
                        Log.i(TAG, "onResponse: cancelUpcomingJobs bookingid -->" + response.body().getBookingId());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.your_jobs_pending_service_accepte, Toast.LENGTH_SHORT).show();
                        // getPendingJobsCall();
                        selectPage(1);

                    } else {
                        callRefreshToken("accept");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("accept");
                }
            }

            @Override
            public void onFailure(Call<DeclinePendingModel> call, Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void declinePendingJobs() {
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookid);
        map.put("mode", "driver");
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DeclinePendingModel> call2 = apiInterface.doDeclinePendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

        call2.enqueue(new Callback<DeclinePendingModel>() {
            @Override
            public void onResponse(@NonNull Call<DeclinePendingModel> call, @NonNull retrofit2.Response<DeclinePendingModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Cancelled successfully.")) {
                        // List<OnGoingJobsData> getve = response.body().getServiceRequest();
                        String getStatus = response.body().getMessage();
                        Log.i(TAG, "onResponse: cancelUpcomingJobs-->" + getStatus);
                        Log.i(TAG, "onResponse: cancelUpcomingJobs bookingid -->" + response.body().getBookingId());
                        progressBar.setVisibility(View.GONE);
                        getPendingJobsCall();

                    } else {
                        callRefreshToken("cancel");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("cancel");
                }
            }

            @Override
            public void onFailure(Call<DeclinePendingModel> call, Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
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
                        getPendingJobsCall();
                    } else if (logout.equalsIgnoreCase("accept")) {
                        acceptPendingJobs();
                    } else if (logout.equalsIgnoreCase("cancel")) {
                        declinePendingJobs();
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
        // countDownTimerPending.cancel();
        //timer.cancel();
        Log.e(TAG, "run: ~~~~~~~~~~~~~stop~~~~");
        aBoolean = true;
        bBoolean = true;
        removeFragment(Utils.FRAGMENT_YOURJOBS);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }

    @Override
    public void onSuccess(HashMap<String, Object> map) {
        String job = String.valueOf(map.get("job"));
        strBookid = String.valueOf(map.get("book_id"));
        if (job.equals("acceptjob")) {
            Log.i(TAG, "onSuccess: acceptjob--" + strBookid);
            acceptPendingJobs();
        } else if (job.equals("declinejob")) {
            Log.i(TAG, "onSuccess: declinejob--" + strBookid);
            declinePendingJobs();
        }
        //  showChangeLangDialog();


    }

    public void refreshApi() {
        //write the code here to refresh your Api
        Log.i(TAG, "refreshApi: pending fragment");
        getPendingJobsCall();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}