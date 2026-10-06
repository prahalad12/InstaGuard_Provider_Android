package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Adapter.TransactionAllAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.ProfileModel;
import com.instaguard.provider.Models.WalletAllData;
import com.instaguard.provider.Models.WalletAllModel;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyWalletFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String TAG = "MyWalletFragment";
    View view;
    Toolbar toolbarSignin;
    ImageButton imgbtnBack;
    CustomEditText edtRechargeAmt;
    CustomButton btnViewTransaction, btnAddMoney;
    CustomTextView txtYourBalance, txtBonusAmount, txtWalletAmount, txtAddMoney, txtAddMoneyTag, txtAddmoneyOne, txtAddmoneyTwo, txtAddmoneyThree, txtPolicyline, txtTermsPolicy;
    SharedPreferences.Editor putUpdateWallet;
    SharedPreferences getspCurrency, getUpdateWallet;
    String driverId, strCurrencySymbol, strWalletAmt, strWalletRechargeAmt;

    DatabaseReference driverAvailableDatabaseBRef;
    ValueEventListener driverAvailableValueEventListener;
    String deviceId;

    RecyclerView rvPastJobs;
    RelativeLayout rlEmptyview;

    TransactionAllAdapter adapter;

    private AwesomeValidation Validation;

    public MyWalletFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static com.instaguard.provider.Fragments.MyWalletFragment newInstance(String param1, String param2) {
        com.instaguard.provider.Fragments.MyWalletFragment fragment = new com.instaguard.provider.Fragments.MyWalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strWalletRechargeAmt = getArguments().getString(Utils.B_WALLET_RECHARGE_AMOUNT);
            String strWalletPage = getArguments().getString(Utils.B_WALLET_PAGE);
            Log.e(TAG, "Recharge Amount & page: " + strWalletRechargeAmt + " " + strWalletPage);
        }
    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false);

        putUpdateWallet = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getUpdateWallet = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        getspCurrency = requireActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        driverId = getUpdateWallet.getString(Utils.SP_DRIVERID, null);
        strWalletAmt = getUpdateWallet.getString(Utils.SP_DRIVER_WALLET_AMT, null);

        strCurrencySymbol = getspCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);

        Log.e(TAG, "strWalletAmt~~ " + strWalletAmt);

        toolbarSignin = (Toolbar) view.findViewById(R.id.toolbar_wallet);
        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);


        txtYourBalance = (CustomTextView) view.findViewById(R.id.txt_yourbalance);
        txtWalletAmount = (CustomTextView) view.findViewById(R.id.txt_walletamount);
        txtBonusAmount = (CustomTextView) view.findViewById(R.id.txt_walletamount_bonus);
        txtAddMoney = (CustomTextView) view.findViewById(R.id.txt_addmoney);
        txtAddMoneyTag = (CustomTextView) view.findViewById(R.id.txt_addmoney_tag);

        txtAddmoneyOne = (CustomTextView) view.findViewById(R.id.txt_addmoney_one);
        txtAddmoneyTwo = (CustomTextView) view.findViewById(R.id.txt_addmoney_two);
        txtAddmoneyThree = (CustomTextView) view.findViewById(R.id.txt_addmoney_three);

        txtPolicyline = (CustomTextView) view.findViewById(R.id.txt_policy);
        txtTermsPolicy = (CustomTextView) view.findViewById(R.id.txt_terms);

        edtRechargeAmt = (CustomEditText) view.findViewById(R.id.edt_recharge_amt);

        btnViewTransaction = (CustomButton) view.findViewById(R.id.btn_viewtransactions);
        btnAddMoney = (CustomButton) view.findViewById(R.id.btn_addmoney);

        view.findViewById(R.id.rl_parent_lyt_wallet).setOnClickListener(view1 -> {
        });

        rvPastJobs = (RecyclerView) view.findViewById(R.id.rv_wallet_all);
        rlEmptyview = (RelativeLayout) view.findViewById(R.id.rl_emptyview);

        // validation initalize
        Validation = new AwesomeValidation(BASIC);
        txtPolicyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchContent(R.id.frame_mainactivity,
                        Utils.FRAGMENT_SUPPORT,
                        requireActivity(), null,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });

        btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("from", "wallet");
                //  args.putString(Utils.B_WALLET_RECHARGE_AMOUNT, edtRechargeAmount);
                //  args.putString(Utils.B_WALLET_PAGE, "wallet");
                Utils.switchContent(R.id.frame_mainactivity,
                        Utils.FRAGMENT_YOUR_WALLET_TRANSACTION,
                        requireActivity(), args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });


        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                backButtonFunction();
            }
        });


        txtAddmoneyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRechargeAmt.setText("300");
                /*txtAddmoneyOne.setTextColor(getContext().getResources().getColor(R.color.black));
                txtAddmoneyTwo.setTextColor(getContext().getResources().getColor(R.color.white));
                txtAddmoneyThree.setTextColor(getContext().getResources().getColor(R.color.white));*/
                txtAddmoneyOne.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border_color));
                txtAddmoneyTwo.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
                txtAddmoneyThree.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
            }
        });
        txtAddmoneyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRechargeAmt.setText("500");
               /* txtAddmoneyOne.setTextColor(getContext().getResources().getColor(R.color.white));
                txtAddmoneyTwo.setTextColor(getContext().getResources().getColor(R.color.black));
                txtAddmoneyThree.setTextColor(getContext().getResources().getColor(R.color.white));*/
                txtAddmoneyOne.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
                txtAddmoneyTwo.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border_color));
                txtAddmoneyThree.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
            }
        });
        txtAddmoneyThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRechargeAmt.setText("1000");
               /* txtAddmoneyOne.setTextColor(getContext().getResources().getColor(R.color.white));
                txtAddmoneyTwo.setTextColor(getContext().getResources().getColor(R.color.white));
                txtAddmoneyThree.setTextColor(getContext().getResources().getColor(R.color.black));*/
                txtAddmoneyOne.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
                txtAddmoneyTwo.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border));
                txtAddmoneyThree.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_wallet_border_color));
            }
        });

        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String edtRechargeAmount = edtRechargeAmt.getText().toString().trim();

                if (edtRechargeAmount.length() == 0) {
                    edtRechargeAmt.setError(getString(R.string.wallet_enter_rchrg_amount));
                } else {

                    //600 > 500
                    // Log.e(TAG, "Balance differernce: " + convertCurrencyPrice(strWalletBalAmt) + "~~" + convertCurrencyDividePrice(edtRechargeAmount));
                    hideKeyBoard();
                    if (strCurrencySymbol != null) {
                        if (strCurrencySymbol.equals("$")) {

                            Log.i(TAG, "onClick: in the edtRechargeAmount-->" + edtRechargeAmount);
                            Log.i(TAG, "onClick: in the btnAddmoney-->" + convertCurrencyDividePrice(edtRechargeAmount));

                            double value = Double.parseDouble(edtRechargeAmount) / Double.parseDouble(strUsdValue);
                            double roundedNumber = DecimalUtils.round(value, 2);

                            System.out.println("onClick: ~~~~~~~~~~~~" + String.valueOf(roundedNumber));

                            //  Log.e(TAG, "onClick: ~~~If~~~1111111");

                            if (roundedNumber > 0.50) {
                                // Log.e(TAG, "onClick: ~~~If~~~");

                                //MainActivity.frameClickable(true);
                                Bundle args = new Bundle();
                                args.putString(Utils.B_WALLET_RECHARGE_AMOUNT, convertCurrencyDividePrice(edtRechargeAmount));
                                args.putString(Utils.B_WALLET_PAGE, "wallet");
                                Utils.switchContent(R.id.frame_mainactivity,
                                        Utils.FRAGMENT_STRIPE_PAYMENT,
                                        requireActivity(), args,
                                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                            } else {

                                Toast.makeText(requireActivity(), getResources().getString(R.string.amt_must_greater_than), Toast.LENGTH_SHORT).show();
                                // Log.e(TAG, "onClick: ~~~Else~~~");
                            }

                        } else {

                            Log.i(TAG, "onClick: in the edtRechargeAmount-->" + edtRechargeAmount);
                            Log.i(TAG, "onClick: in the btnAddmoney-->" + convertCurrencyDividePrice(edtRechargeAmount));

                            double value = Double.parseDouble(convertCurrencyDividePrice(edtRechargeAmount)) * Double.parseDouble(strUsdValue);
                            double roundedNumber = DecimalUtils.round(value, 2);

                            //  System.out.println("onClick: ~~~~~~~~~~~~" + String.valueOf(roundedNumber));


                            //  Log.e(TAG, "onClick: ~~~If~~~122211");

                            if (roundedNumber > 0.50) {
                                //  Log.e(TAG, "onClick: ~~~If~~~");

                                // MainActivity.frameClickable(true);
                                Bundle args = new Bundle();
                                args.putString(Utils.B_WALLET_RECHARGE_AMOUNT, convertCurrencyDividePrice(edtRechargeAmount));
                                args.putString(Utils.B_WALLET_PAGE, "wallet");
                                Utils.switchContent(R.id.frame_mainactivity,
                                        Utils.FRAGMENT_STRIPE_PAYMENT,
                                        requireActivity(), args,
                                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                            } else {

                                Toast.makeText(requireActivity(), getResources().getString(R.string.amt_must_greater_than), Toast.LENGTH_SHORT).show();
                                //  Log.e(TAG, "onClick: ~~~Else~~~");
                            }
                        }

                    }
                }
            }
        });

        //getWalletTransactionAllCall();

        //getDriverWalletAmount();

        //callUSDCurrencyValue();

        callViewProfileDetails();

        onBackButton(view);

        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    /*******************************Get Firebase States********************************************/

    public void onBackButton(View view) {
        // MainActivity.frameClickable(false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    backButtonFunction();
                    hideKeyBoard();
                }
                return true;
            }
        });
    }

    public void backButtonFunction() {
        hideKeyBoard();
        removeFragment(Utils.FRAGMENT_MYWALLET);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }

    /*private void showSnackbar(String s, String key) {

        final TSnackbar snackbar = TSnackbar.make(requireActivity().findViewById(android.R.id.content), s, TSnackbar.LENGTH_LONG);
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
        Typeface font = Typeface.createFromAsset(requireActivity().getAssets(), getString(R.string.App_Font));
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(font, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }*/

    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    //get Driver online offlne status in firebase
    private void getDriverWalletAmount() {

        if (driverAvailableDatabaseBRef != null) {
            driverAvailableDatabaseBRef.removeEventListener(driverAvailableValueEventListener);
        }
        driverAvailableDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
        driverAvailableValueEventListener = driverAvailableDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Object walletAmountObj = dataSnapshot.child("wallet").getValue();
                    if (walletAmountObj != null) {
                        strWalletAmt = walletAmountObj.toString();
                    } else {
                        strWalletAmt = "0";
                    }

                    if (strWalletAmt != null) {
                        //txtWalletAmount.setText(strCurrencySymbol + strWalletAmt);
                        txtWalletAmount.setText(strCurrencySymbol + " " + convertCurrencyPrice(strWalletAmt));
                    } else {
                        txtWalletAmount.setText(strCurrencySymbol + "0");
                    }
                    putUpdateWallet.putString(Utils.SP_DRIVER_WALLET_AMT_MIN, strWalletAmt);
                    putUpdateWallet.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public String convertCurrencyPrice(String price) {
        String currencyCommonPrice = getspCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, null);
        Double multiplePrice = Double.parseDouble(currencyCommonPrice) * Double.parseDouble(price);
        double roundedNumber = DecimalUtils.round(multiplePrice, 2);
        return String.valueOf(roundedNumber);
    }

    public String convertCurrencyDividePrice(String price) {
        String currencyCommonPrice = getspCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, null);
        Double multiplePrice = Double.parseDouble(price) / Double.parseDouble(currencyCommonPrice);
        double roundedNumber = DecimalUtils.round(multiplePrice, 2);
        return String.valueOf(roundedNumber);
    }

    DatabaseReference currencyDatabaseBRef;
    ValueEventListener currencyValueEventListener;
    String strUsdValue = "1";

    private void callUSDCurrencyValue() {

        if (currencyDatabaseBRef != null) {
            currencyDatabaseBRef.removeEventListener(currencyValueEventListener);
        }
        currencyDatabaseBRef = FirebaseDatabase.getInstance().getReference();
        currencyValueEventListener = currencyDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("currency").getValue() != null) {
                    if (dataSnapshot.child("currency").child("USD").getValue() != null) {
                        strUsdValue = dataSnapshot.child("currency").child("USD").getValue().toString();
                    } else if (dataSnapshot.child("currency").child("usd").getValue() != null) {
                        strUsdValue = dataSnapshot.child("currency").child("usd").getValue().toString();
                    } else {
                        strUsdValue = "1";
                    }
                } else {
                    strUsdValue = "1";
                }

                System.out.println("onDataChange: ~~~~~~~USD currency~~~~" + strUsdValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("", "onCancelled: error occur " + databaseError);
                strUsdValue = "1";
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getWalletTransactionAllCall() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        map.put("language", "strLanguageShortName");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WalletAllModel> call2 = apiInterface.doWalletTransactionAll("Bearer " + "123", Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(requireActivity());
        call2.enqueue(new Callback<WalletAllModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<WalletAllModel> call, @NonNull Response<WalletAllModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Transactions listed successfully.")) {
                        List<WalletAllData> getve = Objects.requireNonNull(response.body()).getWalletAllData();
                        Log.i(TAG, "onResponse: getUpcomingJobsCall-->" + getve);
                        if (getve.size() > 0) {

                            rlEmptyview.setVisibility(View.GONE);
                            rvPastJobs.setVisibility(View.VISIBLE);
                            adapter = new TransactionAllAdapter(requireActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvPastJobs.setLayoutManager(horizontalLayoutManager);
                            rvPastJobs.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            hideProgressDialog();

                        } else {
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvPastJobs.setVisibility(View.GONE);
                        }
                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("No records Found.")) {
                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvPastJobs.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(requireActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletAllModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                Toast.makeText(requireActivity(), "please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callViewProfileDetails() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", "123");
        Log.i("CallViewProfileDetails", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ProfileModel> call2 = apiInterface.doGetprofileDetails(map);
        call2.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Profile listed successfully.")) {

                        try {
                            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();

                            Object getBonusEarning = String.valueOf(Objects.requireNonNull(response.body()).getBonusEarning());

                            double roundedNumber = 0.0;
                            double roundedNumbergetBonusEarning = 0.0;
                            if (response.body().getProfileData().getWalletBalance() != null &&
                                    !String.valueOf(response.body().getProfileData().getWalletBalance()).equals("null")
                                    && !String.valueOf(response.body().getProfileData().getWalletBalance()).equals("")) {
                                roundedNumber = DecimalUtils.round(Double.parseDouble(String.valueOf(response.body().getProfileData().getWalletBalance())), 2);
                                putUpdateWallet.putString(Utils.SP_DRIVER_WALLET_AMT, String.valueOf(roundedNumber));
                                putUpdateWallet.apply();
                                hideProgressDialog();
                            }

                            if (!String.valueOf(getBonusEarning).equals("null") && !String.valueOf(getBonusEarning).equals("") && !String.valueOf(getBonusEarning).equals(" ")) {
                                roundedNumbergetBonusEarning = DecimalUtils.round(Double.parseDouble(String.valueOf(getBonusEarning)), 2);
                                putUpdateWallet.putString(Utils.SP_DRIVER_BONUS_AMT, String.valueOf(roundedNumbergetBonusEarning));
                                putUpdateWallet.apply();
                                hideProgressDialog();
                            } else {
                                putUpdateWallet.putString(Utils.SP_DRIVER_BONUS_AMT, String.valueOf(roundedNumbergetBonusEarning));
                                putUpdateWallet.apply();
                            }

                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue(String.valueOf(roundedNumber));

                            txtWalletAmount.setText(strCurrencySymbol + " " + convertCurrencyPrice(String.valueOf(roundedNumber)));
                            txtBonusAmount.setText(strCurrencySymbol + " " + convertCurrencyPrice(String.valueOf(roundedNumbergetBonusEarning)));

                            hideProgressDialog();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        //callRefreshToken("viewprofile");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("viewprofile");
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_500) {
                    ////callRefreshToken("viewprofile");
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: !~~~~~~!!" + t.toString());
                call.cancel();
            }
        });

    }

}
