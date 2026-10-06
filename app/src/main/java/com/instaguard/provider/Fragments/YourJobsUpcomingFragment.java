package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.checkStringEmptyZero;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.setVisibility;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Activities.MainActivity.callPendingUpcomingStatus;
import static com.instaguard.provider.Activities.MainActivity.txtUpcomingCount;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.UpcomingJobsAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.DeclinePendingModel;
import com.instaguard.provider.Models.Request.SendRequestModel;
import com.instaguard.provider.Models.StartUpcomingTripModel;
import com.instaguard.provider.Models.UpcomingJobsData;
import com.instaguard.provider.Models.UpcomingJobsModel;
import com.instaguard.provider.R;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class YourJobsUpcomingFragment extends Fragment implements UpcomingJobsAdapter.SuccessResponse {
    // TODO: Rename parameter arguments, choose names that match

    View view;
    SharedPreferences SP_Prefs;
    SharedPreferences.Editor putStateFBCategory, putSpState;
    String userAccessToken;
    String driverId;

    RecyclerView rvUpcomingJobs;
    RelativeLayout rlEmptyview;
    RelativeLayout progressBar;


    String strBookid;

    public String TAG = "YourJobsUpcomingFragment";
    UpcomingJobsAdapter adapter;


    DatabaseReference driverAvailableDatabaseBRef;
    ValueEventListener driverAvailableValueEventListener;

    APIInterface apiInterface;


    private View dialogRequesting;
    private View llParentLytBackupGuardRequestAccepted;
    private View rlParentLytRequest;
    View btnOkayBackupGuardRequestAccepted;
    private CustomTextView txtCountTimer;
    private CustomTextView btnCancelRequest;
    RippleBackground rippleBackgroundRequesting;
    CountDownTimer countDownTimerSOSBackupRequest;


    public YourJobsUpcomingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static YourJobsUpcomingFragment newInstance(String param1, String param2) {
        YourJobsUpcomingFragment fragment = new YourJobsUpcomingFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yourjobs_upcoming, container, false);
        SP_Prefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        putSpState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        putStateFBCategory = requireActivity().getSharedPreferences(Utils.MY_PREFS_FB_CATEGORY_TRIPSTATE, MODE_PRIVATE).edit();

        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("driverId~~YourJobsPastFragment~~", driverId + "--" + userAccessToken);

        UpcomingJobsAdapter.successResponse = this;

        apiInterface = APIClient.getClient().create(APIInterface.class);

        initView(view);

        getDriverOnlineOrOffline();

        getUpcomingJobsCall();

        callRequestTimePeriod();

        OnClick();

        onBackButton(view);

        return view;
    }

    private void callCancelBooking(String key) {

        setVisibility(dialogRequesting, false);
        setVisibility(llParentLytBackupGuardRequestAccepted, false);

        if (countDownTimerSOSBackupRequest != null) {
            countDownTimerSOSBackupRequest.cancel();
        }
        if (rippleBackgroundRequesting != null) {
            rippleBackgroundRequesting.stopRippleAnimation();
        }

        removeProviderBackupStatus(key);

        getUpcomingJobsCall();
    }


    void initView(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        rvUpcomingJobs = view.findViewById(R.id.rv_upcomingjobs);
        rlEmptyview = view.findViewById(R.id.rl_emptyview_upcoming);

        /*Requesting*/
        dialogRequesting = view.findViewById(R.id.lyt_dialog_requesting);
        rlParentLytRequest = view.findViewById(R.id.rl_parent_lyt_request);
        llParentLytBackupGuardRequestAccepted = view.findViewById(R.id.ll_parent_lyt_backup_guard_accepted);
        btnOkayBackupGuardRequestAccepted = view.findViewById(R.id.btn_okay_backup_guard_accepted);

        txtCountTimer = view.findViewById(R.id.txt_count_timer);
        rippleBackgroundRequesting = view.findViewById(R.id.rippleBackground);
        btnCancelRequest = view.findViewById(R.id.btn_cancel_request);
        /*Requesting*/


    }

    private void OnClick() {

        btnCancelRequest.setOnClickListener(view1 -> callCancelBooking("2"));

        btnOkayBackupGuardRequestAccepted.setOnClickListener(view -> callCancelBooking("0"));

        rlParentLytRequest.setOnClickListener(v -> {

        });
        progressBar.setOnClickListener(v -> {

        });

    }

    private boolean aBoolean = false;
    private boolean bBoolean = false;

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

                            if (dataSnapshot.child("upcoming").getValue() != null) {
                                Object strPendingcountFB = Objects.requireNonNull(dataSnapshot.child("upcoming").getValue()).toString();
                                if (strPendingcountFB != null) {
                                    if (!strPendingcountFB.toString().equals("0")) {
                                        //getUpcomingJobsCall();
                                    } else {
                                        if (!aBoolean) {
                                            //getUpcomingJobsCall();
                                            aBoolean = true;
                                        }
                                    }
                                } else {
                                    txtUpcomingCount.setText("0");
                                    rlEmptyview.setVisibility(View.VISIBLE);
                                    rvUpcomingJobs.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            } else {
                                Log.e(TAG, "onDataChange:NULL Pending@upcoming");
                            }

                            String transfer_request = checkStringEmptyZero(dataSnapshot, Utils.FIREBASE_TRANSFER_REQUEST);
                            switch (transfer_request) {
                                case "yes":

                                    setVisibility(dialogRequesting, true);
                                    setVisibility(llParentLytBackupGuardRequestAccepted, true);

                                    if (countDownTimerSOSBackupRequest != null) {
                                        countDownTimerSOSBackupRequest.cancel();
                                    }

                                    if (rippleBackgroundRequesting != null) {
                                        rippleBackgroundRequesting.stopRippleAnimation();
                                    }

                                    break;
                                case "1":
                                    break;
                                case "0":
                                default:
                                    setVisibility(dialogRequesting, false);
                                    setVisibility(llParentLytBackupGuardRequestAccepted, false);

                                    if (countDownTimerSOSBackupRequest != null) {
                                        countDownTimerSOSBackupRequest.cancel();
                                    }

                                    if (rippleBackgroundRequesting != null) {
                                        rippleBackgroundRequesting.stopRippleAnimation();
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onDataChange:Exception Pending@upcoming" + e);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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


    private void getUpcomingJobsCall() {

        progressBar.setVisibility(View.VISIBLE);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", driverId);
        map.put("mode", "driver");

        Call<UpcomingJobsModel> call2 = apiInterface.doCustomerUpcomingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

        call2.enqueue(new Callback<UpcomingJobsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<UpcomingJobsModel> call, @NonNull retrofit2.Response<UpcomingJobsModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Fetching upcoming jobs successful.")) {
                        List<UpcomingJobsData> getve = Objects.requireNonNull(response.body()).getServiceRequest();
                        Log.i(TAG, "onResponse: getPastJobsCall-->" + getve);
                        if (getve.size() > 0) {

                            txtUpcomingCount.setText(Integer.toString(getve.size()));
                            rlEmptyview.setVisibility(View.GONE);
                            rvUpcomingJobs.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            adapter = new UpcomingJobsAdapter(requireActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvUpcomingJobs.setLayoutManager(horizontalLayoutManager);
                            rvUpcomingJobs.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            //  callPendingUpcomingStatus(driverId,"upcoming",String.valueOf(Integer.toString(getve.size())));

                        } else {

                            txtUpcomingCount.setText("0");
                            if (!bBoolean) {
                                callPendingUpcomingStatus(driverId, "pending", "0");
                                bBoolean = true;
                            }
                            //callPendingUpcomingStatus(driverId,"upcoming","0");

                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvUpcomingJobs.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("No records Found.")) {

                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvUpcomingJobs.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        //callRefreshToken("Category");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("Category");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpcomingJobsModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startUpcomingJobs() {

        progressBar.setVisibility(View.VISIBLE);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookid);
        map.put("driver_id",driverId);
        map.put("mode", "accept");

        Call<StartUpcomingTripModel> call2 = apiInterface.doStartUpcomingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<StartUpcomingTripModel>() {
            @Override
            public void onResponse(@NonNull Call<StartUpcomingTripModel> call, @NonNull retrofit2.Response<StartUpcomingTripModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {

                        String getStatus = Objects.requireNonNull(response.body()).getMessage();
                        String getPaymentMode = Objects.requireNonNull(response.body()).getServiceRequest().getPaymentType();
                        Log.i(TAG, "onResponse: cancelUpcomingJobs-->" + getStatus);
                        Log.i(TAG, "onResponse: cancelUpcomingJobs bookingid -->" + Objects.requireNonNull(response.body()).getServiceRequest().getId());

                        putStateFBCategory.putString(Utils.SP_FB_REQUEST_PAYMENT_MODE, getPaymentMode);
                        putStateFBCategory.apply();
                        progressBar.setVisibility(View.GONE);

                        Intent Signin = new Intent(requireActivity(), MainActivity.class);
                        startActivity(Signin);

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("You should not start before request date")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "You should not start before request date", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        //callRefreshToken("start");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("start");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StartUpcomingTripModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cancelUpcomingJobs() {

        progressBar.setVisibility(View.VISIBLE);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookid);
        map.put("driver_id",driverId);
        map.put("mode", "driver");

        Call<DeclinePendingModel> call2 = apiInterface.doDeclinePendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<DeclinePendingModel>() {
            @Override
            public void onResponse(@NonNull Call<DeclinePendingModel> call, @NonNull retrofit2.Response<DeclinePendingModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Cancelled successfully.")) {
                        String getStatus = Objects.requireNonNull(response.body()).getMessage();
                        Log.i(TAG, "onResponse: cancelUpcomingJobs-->" + getStatus);
                        Log.i(TAG, "onResponse: cancelUpcomingJobs bookingid -->" + Objects.requireNonNull(response.body()).getBookingId());
                        progressBar.setVisibility(View.GONE);
                        getUpcomingJobsCall();
                    } else {
                        //callRefreshToken("cancel");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("cancel");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeclinePendingModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onBackButton(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_BACK) {
                backButtonFunction();
            }
            return true;
        });
    }

    public void backButtonFunction() {
        // MainActivity.frameClickable(false);
        aBoolean = true;
        bBoolean = true;
        removeFragment(Utils.FRAGMENT_YOURJOBS);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
    }

    @Override
    public void onSuccess(HashMap<String, Object> map) {
        String job = String.valueOf(map.get("job"));
        strBookid = String.valueOf(map.get("book_id"));
        switch (job) {
            case "startjob":
                Log.i(TAG, "onSuccess: startjob");
                startUpcomingJobs();
                break;
            case "canceljob":
                Log.i(TAG, "onSuccess: canceljob");
                cancelUpcomingJobs();
                break;
            case "transfer":
                Log.i(TAG, "onSuccess: transfer");
                transferJobs(strBookid);
                break;
        }
    }

    private void transferJobs(String strBookid) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);

        SpannableString spannableStringst = new SpannableString(getString(R.string.transfer_job));
        spannableStringst.setSpan(tfSpan, 0, spannableStringst.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getString(R.string.are_you_sure_want_transfer_job));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.yes));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringsss = new SpannableString(getString(R.string.no));
        spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(spannableStringst);

        builder.setMessage(spannableStrings);

        builder.setPositiveButton(spannableStringss, (dialog, which) -> callRequestBooking(strBookid));
        builder.setNegativeButton(spannableStringsss, (dialog, which) -> dialog.dismiss());

        builder.show();

    }

    private void callRequestTimePeriod() {

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        String strRequestCount = SP_Prefs.getString(Utils.SP_COMMON_REQUEST_TIME_DELAY_ACCEPT, String.valueOf(Utils.countDownTimeLimit));
        Log.e(TAG, "callRequestTimePeriod: ~~~~~~~~~~~~~~~~~~~~" + strRequestCount);

        //count down timer
        countDownTimerSOSBackupRequest = new CountDownTimer(Long.parseLong(strRequestCount) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txtCountTimer.setText("00:" + millisUntilFinished / 1000);
                // timer = Integer.valueOf(String.valueOf(millisUntilFinished));
            }

            public void onFinish() {
                new CountDownTimer(300, 100) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            Log.i("Inside timer count", "finish");

                            setVisibility(dialogRequesting, false);
                            setVisibility(llParentLytBackupGuardRequestAccepted, false);

                            if (countDownTimerSOSBackupRequest != null) {
                                countDownTimerSOSBackupRequest.cancel();
                            }

                            if (rippleBackgroundRequesting != null) {
                                rippleBackgroundRequesting.stopRippleAnimation();
                            }

                            removeProviderBackupStatus("3");

                            showAlertDialogTimeOut();

                        } catch (Exception e) {
                            e.printStackTrace();
                            //TODO: Fill in exception
                        }
                    }
                }.start();
            }
        };
    }

    public void removeProviderBackupStatus(String key) {
        Log.e(TAG, "removeCustomerAcceptStatus: upcoming--" + key);
        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).
                child(driverId).child(Utils.FIREBASE_TRANSFER_REQUEST).setValue(key);
    }

    public void showAlertDialogTimeOut() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        //SpannableString spannableString = new SpannableString(getString(R.string.my_wallet_add_money_btn_text));
        //spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getResources().getString(R.string.request_time_out));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setMessage(spannableStrings);

        builder.setPositiveButton(spannableStringss, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void callRequestBooking(String strBookingId) {

        HashMap<String, Object> map = new HashMap<>();

        /*mode:request
        booking_id
        driver_id*/

        map.put("mode", "request");
        map.put("booking_id", strBookingId);
        map.put("driver_id", driverId);
        map.put("transfer_status", "1");

        Log.e("callRequestBooking", "~~~~" + map);

        showProgressDialog(requireActivity());

        Call<SendRequestModel> call2 = apiInterface.doBackupRequestSendDetails(map);
        call2.enqueue(new Callback<SendRequestModel>() {
            @Override
            public void onResponse(@NonNull Call<SendRequestModel> call, @NonNull Response<SendRequestModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {

                        removeProviderBackupStatus("1");

                        if (countDownTimerSOSBackupRequest != null) {
                            countDownTimerSOSBackupRequest.start();
                        }

                        setVisibility(dialogRequesting, true);
                        setVisibility(llParentLytBackupGuardRequestAccepted, false);

                        if (rippleBackgroundRequesting != null) {
                            rippleBackgroundRequesting.startRippleAnimation();
                        }

                        String strbookingId = response.body().getServiceRequest().getBookingId().toString();
                        calltimeout(strbookingId, driverId);

                        if (!strbookingId.equals("null") && !strbookingId.equals("")) {
                            btnCancelRequest.setVisibility(View.VISIBLE);
                        } else {
                            btnCancelRequest.setVisibility(View.INVISIBLE);
                        }

                        //callClickAction(btnSOSBackupRequiredJobsInProgress, true);

                    } else {

                        btnCancelRequest.setVisibility(View.INVISIBLE);

                        Toast.makeText(requireActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        hideProgressDialog();

                        setVisibility(dialogRequesting, false);
                        setVisibility(llParentLytBackupGuardRequestAccepted, false);

                        if (countDownTimerSOSBackupRequest != null) {
                            countDownTimerSOSBackupRequest.cancel();
                        }

                        if (rippleBackgroundRequesting != null) {
                            rippleBackgroundRequesting.stopRippleAnimation();
                        }

                        //callClickAction(btnSOSBackupRequiredJobsInProgress, true);
                    }

                } else {
                    //callClickAction(btnSOSBackupRequiredJobsInProgress, true);
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SendRequestModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
                System.out.println("~~~onFailure~~" + t);
                //callClickAction(btnSOSBackupRequiredJobsInProgress, true);
            }
        });

    }


    private void calltimeout(String strbookingId, String driverId) {
        ArrayList<String> list = new ArrayList<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strbookingId);
        map.put("driver_id", driverId);
        map.put("type", "app");
        map.put("transfer_status", "1");
        map.put("reference_id", list);

        Log.e("calltimeout", "~~~~" + map);

        Call<BankModel> call2 = apiInterface.docallTimeout(map);
        call2.enqueue(new Callback<BankModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Log.e(TAG, "onResponse:Time out success ");
                    } else {
                        Log.e(TAG, "onResponse:Time out failure ");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });

    }

}