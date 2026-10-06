package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.ProfileModel;
import com.instaguard.provider.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.MainActivity.adminPaid;
import static com.instaguard.provider.Activities.MainActivity.earnCard;
import static com.instaguard.provider.Activities.MainActivity.earnCash;
import static com.instaguard.provider.Activities.MainActivity.earnings;
import static com.instaguard.provider.Activities.MainActivity.payoutBalance;
import static com.instaguard.provider.Activities.MainActivity.siteCommision;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;

public class EarningsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    SharedPreferences SP_Prefs;
    String strCurrencySymbol, driverId;

    SharedPreferences.Editor getUpdateBank;
    SharedPreferences getspCommonCurrency, spUpdateBank;

    String userAccessToken;


    private SwipeRefreshLayout swipeRefreshEarnings;
    private LinearLayout llParenntEarnings;
    private ImageButton imgbtnBack;
    private CustomTextView txtTotalEarningsLabel;
    private CustomTextView txtTotalEarnings;
    private TableRow trTotalCashPayment;
    private CustomTextView txtTotalCashPaymentAmountLabel;
    private CustomTextView txtTotalCashPaymentAmount;
    private TableRow trTotalCardPayment;
    private CustomTextView txtTotalCardPaymentAmountLabel;
    private CustomTextView txtTotalCardPaymentAmount;
    private TableRow trPayoutBalanceCardPayment;
    private CustomTextView txtPayoutAdminBalanceLabel;
    private CustomTextView txtPayoutAdminBalance;
    private TableRow trAdminGivenBalanceAmount;
    private CustomTextView txtAdminGivenAmountLabel;
    private CustomTextView txtAdminGivenAmount;
    private TableRow trAdminSiteCommission;
    private CustomTextView txtAdminSiteCommissionLabel;
    private CustomTextView txtAdminSiteCommission;
    private CustomButton btnViewTransaction;
    private ImageView ivRefresh;
    private ProgressBar progressBar;


    public EarningsFragment() {
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

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_earnings, container, false);

        getUpdateBank = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        spUpdateBank = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);

        getspCommonCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strCurrencySymbol = getspCommonCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);


        driverId = spUpdateBank.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spUpdateBank.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("UserId~~SettingsFragment~~", driverId);
        // validation initalize


        initView(view);

        onBackButton(view);

        return view;

    }

    void initView(View view) {


        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);

        swipeRefreshEarnings = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_earnings);
        llParenntEarnings = (LinearLayout) view.findViewById(R.id.ll_parennt_earnings);
        ivRefresh = (ImageView) view.findViewById(R.id.iv_menu_profile);

        btnViewTransaction = view.findViewById(R.id.btn_viewtransactions);

        trTotalCashPayment = (TableRow) view.findViewById(R.id.tr_total_cash_payment);
        trTotalCardPayment = (TableRow) view.findViewById(R.id.tr_total_card_payment);
        trPayoutBalanceCardPayment = (TableRow) view.findViewById(R.id.tr_payout_balance_card_payment);
        trAdminGivenBalanceAmount = (TableRow) view.findViewById(R.id.tr_admin_given_balance_amount);
        trAdminSiteCommission = (TableRow) view.findViewById(R.id.tr_admin_site_commission);

        txtTotalEarningsLabel = (CustomTextView) view.findViewById(R.id.txt_total_earnings_label);
        txtTotalCashPaymentAmountLabel = (CustomTextView) view.findViewById(R.id.txt_total_cash_payment_amount_label);
        txtTotalCardPaymentAmountLabel = (CustomTextView) view.findViewById(R.id.txt_total_card_payment_amount_label);
        txtPayoutAdminBalanceLabel = (CustomTextView) view.findViewById(R.id.txt_payout_admin_balance_label);
        txtAdminGivenAmountLabel = (CustomTextView) view.findViewById(R.id.txt_admin_given_amount_label);
        txtAdminSiteCommissionLabel = (CustomTextView) view.findViewById(R.id.txt_admin_site_commission_label);

        txtTotalEarnings = (CustomTextView) view.findViewById(R.id.txt_total_earnings);
        txtTotalCashPaymentAmount = (CustomTextView) view.findViewById(R.id.txt_total_cash_payment_amount);
        txtTotalCardPaymentAmount = (CustomTextView) view.findViewById(R.id.txt_total_card_payment_amount);
        txtPayoutAdminBalance = (CustomTextView) view.findViewById(R.id.txt_payout_admin_balance);
        txtAdminGivenAmount = (CustomTextView) view.findViewById(R.id.txt_admin_given_amount);
        txtAdminSiteCommission = (CustomTextView) view.findViewById(R.id.txt_admin_site_commission);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_indeterminate_determinate);

        swipeRefreshEarnings.setOnRefreshListener(this);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });


        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewProfileDetails();
            }
        });


        llParenntEarnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        callTextValues(earnings, txtTotalEarnings, " -- ");
        callTextValues(payoutBalance, txtPayoutAdminBalance, " -- ");
        callTextValues(earnCash, txtTotalCashPaymentAmount, " -- ");
        callTextValues(earnCard, txtTotalCardPaymentAmount, " -- ");
        callTextValues(adminPaid, txtAdminGivenAmount, " -- ");
        callTextValues(siteCommision, txtAdminSiteCommission, " -- ");

        callViewProfileDetails();

        btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("from", "earnings");
                Utils.switchContent(R.id.frame_mainactivity,
                        Utils.FRAGMENT_YOUR_WALLET_TRANSACTION,
                        Objects.requireNonNull(getActivity()), args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });


    }

    private void callViewProfileDetails() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", userAccessToken);
        Log.i("CallViewProfileDetails", "~~~~" + map);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ProfileModel> call2 = apiInterface.doGetprofileDetails(map);
        call2.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    // hideProgressDialog();
                    progressBar.setVisibility(View.INVISIBLE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Profile listed successfully.")) {

                        try {

                            payoutBalance = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getWallet());
                            earnCard = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnCard());
                            adminPaid = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getAdminPaid());

                            earnings = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnings());
                            earnCash = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnCash());
                            siteCommision = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getDriverPaid());

                            callTextValues(earnings, txtTotalEarnings, " -- ");

                            callTextValues(earnCash, txtTotalCashPaymentAmount, " -- ");
                            callTextValues(earnCard, txtTotalCardPaymentAmount, " -- ");

                            callTextValues(payoutBalance, txtPayoutAdminBalance, " -- ");
                            callTextValues(adminPaid, txtAdminGivenAmount, " -- ");

                            callTextValues(siteCommision, txtAdminSiteCommission, " -- ");

                            if (earnCash != null) {
                                if (!earnCash.equals("null")) {
                                    if (!earnCash.equals("")) {
                                        if (!earnCash.equals(" ")) {
                                            if (earnCash.equals("0.0") || earnCash.equals("0")) {
                                                btnViewTransaction.setVisibility(View.GONE);
                                            } else {
                                                btnViewTransaction.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }

                            /*if (payoutBalance != null) {
                                if (!payoutBalance.equals("null")) {
                                    if (!payoutBalance.equals("")) {
                                        navigationBalance.setText(getResources().getString(R.string.total_earnings) + ":- " + earnings);
                                    }else {
                                        navigationBalance.setText(getResources().getString(R.string.total_earnings)+":- 0");
                                    }
                                }else {
                                    navigationBalance.setText(getResources().getString(R.string.total_earnings)+":- 0");
                                }
                            }else {
                                navigationBalance.setText(getResources().getString(R.string.total_earnings)+":- 0");
                            }*/

                            //Total Card payment balance last time show client changes to total earning show in Navigation Balance
                            /*if (payoutBalance != null) {
                                if (!payoutBalance.equals("null")) {
                                    if (!payoutBalance.equals("")) {
                                        navigationBalance.setText(getResources().getString(R.string.payout_balance) + ":- " + payoutBalance);
                                    }else {
                                        navigationBalance.setText(getResources().getString(R.string.payout_balance)+":- 0");
                                    }
                                }else {
                                    navigationBalance.setText(getResources().getString(R.string.payout_balance)+":- 0");
                                }
                            }else {
                                navigationBalance.setText(getResources().getString(R.string.payout_balance)+":- 0");
                            }*/


                            progressBar.setVisibility(View.INVISIBLE);
                            //  hideProgressDialog();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    // hideProgressDialog();
                } else if (response.code() == RESPONSECODE_500) {
                    // hideProgressDialog();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("", "onFailure: !~~~~~~!! 500~~~~~~~");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                //  hideProgressDialog();
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("", "onFailure: !~~~~~~!!" + t.toString());
                call.cancel();
            }
        });


    }


    public String convertCurrencyPrice(String price) {
        if (price != null) {
            if (!price.equals("null")) {
                if (!price.equals("")) {
                    String currencyCommonPrice = getspCommonCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                    Log.i("currencyCommonPrice", "~~~" + currencyCommonPrice);
                    Log.i("price", "~~~" + price);
                    double multiplePrice = Double.parseDouble(currencyCommonPrice) * Double.parseDouble(price);

                    //DecimalFormat twoDForm = new DecimalFormat("#.##"); for Arabic to hide
                    //return String.valueOf(Double.valueOf(twoDForm.format(multiplePrice)));

                    DecimalFormatSymbols symbolsEN_US = DecimalFormatSymbols.getInstance(Locale.US);
                    //return new DecimalFormat("0.0##", symbolsEN_US).format(multiplePrice);
                    return new DecimalFormat("0.0#", symbolsEN_US).format(multiplePrice);

                    //https://stackoverflow.com/questions/7472519/how-to-round-decimal-numbers-in-android
                    //double roundedNumber = DecimalUtils.round(multiplePrice, 2);
                    //return String.valueOf(roundedNumber);
                } else {
                    return "0";
                }
            } else {
                return "0";
            }
        } else {
            return "0";
        }
    }


    @SuppressLint("SetTextI18n")
    private void callTextValues(String value, CustomTextView customTextView, String key) {
        if (value != null) {
            if (!value.equals("null")) {
                if (!value.equals("")) {
                    if (!value.equals("0.0")) {
                        customTextView.setText(strCurrencySymbol + " " + convertCurrencyPrice(value));
                    } else {
                        customTextView.setText(key);
                    }
                } else {
                    customTextView.setText(key);
                }
            } else {
                customTextView.setText(key);
            }
        } else {
            customTextView.setText(key);
        }
    }

    private void onClearViews(CustomEditText customEditText, ImageButton imgBtnClear) {

        customEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    imgBtnClear.setVisibility(View.GONE);
                } else {
                    imgBtnClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgBtnClear.setVisibility(View.GONE);
                } else {
                    imgBtnClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customEditText.setText("");
            }
        });

        customEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("", "onFocusChange: ~~~~" + hasFocus);
                if (!hasFocus) {
                    imgBtnClear.setVisibility(View.GONE);
                } else {
                    imgBtnClear.setVisibility(View.VISIBLE);
                }
            }
        });

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
        hideKeyBoard();
        removeFragment(Utils.FRAGMENT_EARNINGS);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }


    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshEarnings.setRefreshing(false);
        callViewProfileDetails();
    }
}
