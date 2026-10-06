package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.facebook.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.MyAvailabilityAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.Helpers.PermissionUtils;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AvailabilityDataExtra;
import com.instaguard.provider.Models.AvailabilityModel;
import com.instaguard.provider.Models.AvailbleSlots_Sub;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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


public class MyAvailabilityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;

    ImageButton imgbtnBack;

    ImageView ivMenuProfile;
    RecyclerView rvMyAvailablity;
    private List<String> dayList;
    int numberOfColumns = 3;

    AdView adView;
    LinearLayout adContainer;

    MyAvailabilityAdapter adapter;
    List<AvailbleSlots_Sub> results;

    SharedPreferences.Editor getState;
    SharedPreferences spPrefs;
    public List<AvailabilityDataExtra> availabilityDataExtra;
    public AvailabilityDataExtra availabilityListField;

    String userId, userAccessToken, userProfPic;
    public String TAG = "MyAvailabilityFragment";
    String deviceId;

    DatabaseReference providerApproveStatusDatabaseBRef;
    ValueEventListener providerApproveStatusValueEventListener;
    String strApproveStatus;

    public MyAvailabilityFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyAvailabilityFragment newInstance(String param1, String param2) {
        MyAvailabilityFragment fragment = new MyAvailabilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_myavailability, container, false);

        getState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        SharedPreferences pref = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = pref.getString(Utils.SP_DEVICE_ID, null);

        spPrefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        userProfPic = spPrefs.getString(Utils.SP_DRIVER_PRO_PIC, null);
        Log.i(TAG, "token in My Availability Fragment--> " + deviceId);
        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        rvMyAvailablity = (RecyclerView) view.findViewById(R.id.rv_myavailablity);


        getProviderApproveStatusAmount();

        dayList = new ArrayList<>();
        availabilityDataExtra = new ArrayList<>();
        for (int i = 0; i < Utils.dayArray.length; i++) {
            dayList.add(Utils.dayArray[i]);
        }
        rvMyAvailablity.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyAvailablity.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));

        BaseActivity baseActivity = new BaseActivity();
        adContainer = view.findViewById(R.id.banner_container);
        baseActivity.callFacebookAdsCommon(requireActivity(), adView, adContainer);


        imgbtnBack.setOnClickListener(v -> {
            hideKeyBoard();
            backButtonFunction();
        });

        CallViewAvailableDetails();

        onBackButton(view);

        return view;
    }


    private void CallViewAvailableDetails() {


        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("token", userAccessToken);
        Log.i(TAG, "CallViewAvailableDetails" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AvailabilityModel> call2 = apiInterface.doGetAvailable(map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<AvailabilityModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AvailabilityModel> call, @NonNull Response<AvailabilityModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    Log.e(TAG, "onResponse:CallViewAvailableDetails------------------>>>>>>>> " + response.body().getMessage());
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver Services listed successfully.")) {
                        if (Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots() != null) {
                            // Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                            //List<Integer> monday =  response.body().getAvailabilityData().getAvailbleSlots().getMonday();
                            Log.i(TAG, "onResponse: in the list-->" + dayList.get(0).toString());

                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(1), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getMonday().size(), response.body().getAvailabilityData().getAvailbleSlots().getMonday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(2), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getTuesday().size(), response.body().getAvailabilityData().getAvailbleSlots().getTuesday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(3), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getWednesday().size(), response.body().getAvailabilityData().getAvailbleSlots().getWednesday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(4), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getThursday().size(), response.body().getAvailabilityData().getAvailbleSlots().getThursday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(5), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getFriday().size(), response.body().getAvailabilityData().getAvailbleSlots().getFriday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(6), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getSaterday().size(), response.body().getAvailabilityData().getAvailbleSlots().getSaterday()));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(0), Objects.requireNonNull(response.body()).getAvailabilityData().getAvailbleSlots().getSunday().size(), response.body().getAvailabilityData().getAvailbleSlots().getSunday()));

                            adapter = new MyAvailabilityAdapter(getActivity(), availabilityDataExtra);
                            rvMyAvailablity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        } else {
                            //  Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onResponse: in the list-->" + dayList.get(0).toString());
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(1), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(2), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(3), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(4), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(5), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(6), 0, null));
                            availabilityDataExtra.add(new AvailabilityDataExtra(dayList.get(0), 0, null));

                            adapter = new MyAvailabilityAdapter(getActivity(), availabilityDataExtra);
                            rvMyAvailablity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        //callRefreshToken("viewprofile");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("viewprofile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AvailabilityModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: AvailabilityModel--Main```--->>" + t.toString());
                call.cancel();
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
                    if (logout.equalsIgnoreCase("viewprofile")) {
                        CallViewAvailableDetails();
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

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        getState.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        getState.apply();
    }


    //get Approve Status in firebase
    private void getProviderApproveStatusAmount() {
        if (providerApproveStatusDatabaseBRef != null) {
            providerApproveStatusDatabaseBRef.removeEventListener(providerApproveStatusValueEventListener);
        }
        providerApproveStatusDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
        providerApproveStatusValueEventListener = providerApproveStatusDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    strApproveStatus = Objects.requireNonNull(dataSnapshot.child("approved").getValue()).toString();
                    if (strApproveStatus.equals("1")) {

                        rvMyAvailablity.addOnItemTouchListener(
                                new RecyclerTouchListener(getActivity(), rvMyAvailablity,
                                        new RecyclerTouchListener.OnTouchActionListener() {
                                            @SuppressLint("LongLogTag")
                                            @Override
                                            public void onClick(View view, int position) {
                                                Log.i("rvCategory", "onClick: rvCategory-> ");
                                                if (position != -1) {
                                                    //setProgresscount(position);
                                                    availabilityListField = availabilityDataExtra.get(position);
                                                    Log.e("text", availabilityDataExtra.get(position).text);
                                                    Log.e("name list-->", String.valueOf(availabilityDataExtra.get(position).list));
                                                    Bundle args = new Bundle();

                                                    String strTimeArray;
                                                    if (availabilityDataExtra.get(position).list != null) {
                                                        strTimeArray = Arrays.toString(availabilityDataExtra.get(position).list.toArray());
                                                    } else {
                                                        strTimeArray = null;
                                                    }
                                                    Log.i(TAG, "onClick: array in str-->" + strTimeArray);

                                                    String pos = String.valueOf(position + 1);

                                                    if (pos.equals("7")) {
                                                        pos = "0";
                                                    }
                                                    Log.i(TAG, "onClick: array in position-->" + pos);
                                                    //hide the details
                                                    //args.put(availabilityDataExtra);
                                                    args.putString(Utils.B_DAY_LIST, pos);
                                                    args.putString(Utils.B_TIME_LIST, strTimeArray);
                                                    Utils.switchContent(R.id.frame_mainactivity,
                                                            Utils.FRAGMENT_MYAVAILABILITY_SUB,
                                                            getActivity(), args,
                                                            Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                                }
                                            }

                                            @Override
                                            public void onRightSwipe(View view, int position) {
                                            }

                                            @Override
                                            public void onLeftSwipe(View view, int position) {
                                            }
                                        }));

                    } else {
                        showNormalAlertDialog(getActivity(), getString(R.string.oops_sorry_msg_choose_availabilty));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        if (adView!=null){
            adView.destroy();
        }
        super.onDestroyView();
        if (providerApproveStatusDatabaseBRef != null) {
            providerApproveStatusDatabaseBRef.removeEventListener(providerApproveStatusValueEventListener);
        }
    }

    @Override
    public void onDestroy() {
        if (adView!=null){
            adView.destroy();
        }
        super.onDestroy();
        if (providerApproveStatusDatabaseBRef != null) {
            providerApproveStatusDatabaseBRef.removeEventListener(providerApproveStatusValueEventListener);
        }
    }

    /*******************************Get Firebase States********************************************/

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
        removeFragment(Utils.FRAGMENT_MYAVAILABILITY);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
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
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }*/

    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showNormalAlertDialog(Activity self, String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setCancelable(false);
        Typeface tf = Typeface.createFromAsset(self.getAssets(),
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
                        dialog.dismiss();
                        backButtonFunction();
                    }
                }).show();
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
}
