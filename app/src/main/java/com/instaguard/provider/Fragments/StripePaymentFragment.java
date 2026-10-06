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
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.BuildConfig;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.Helpers.Creditcard.fields.CreditCardModule;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.NetworkManager.Internal.Util;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AppSettingSmsTemplate;
import com.instaguard.provider.Models.AppSettingWebModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.WalletAddMoneyModel;
import com.instaguard.provider.R;
/*import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;*/

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;

public class StripePaymentFragment extends Fragment {

    View view;
    String driverId, userAccessToken;
    ImageButton imgbtnBack;

    CreditCardModule creditCardModule;
    //CardInputWidget creditCardModuleStripe;
   // Card cardToSave;

    CustomButton btnPayStripe;
    ImageView backButton;
    String creditCardNumber, creditCardMonth, creditCardYear, creditCardCvv, stripeTokenID, message;
    private int STRIPE_REQUEST_CODE = 5;
    String STRIPE_SECRETKEY, STRIPE_PUBLISHKEY, strWalletRechargeAmt, strWalletPage;

    SharedPreferences.Editor putUpdateStripeWallet;
    SharedPreferences getUpdateStripeWallet;


    private String TAG = "StripePaymentFragment";

    public StripePaymentFragment() {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strWalletRechargeAmt = getArguments().getString(Utils.B_WALLET_RECHARGE_AMOUNT);
            strWalletPage = getArguments().getString(Utils.B_WALLET_PAGE);
            Log.e(TAG, "Recharge Amount & page: " + strWalletRechargeAmt + " " + strWalletPage);
        }
    }

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stripe_payment, container, false);

        putUpdateStripeWallet = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getUpdateStripeWallet = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = getUpdateStripeWallet.getString(Utils.SP_DRIVERID, null);
        userAccessToken = getUpdateStripeWallet.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        STRIPE_PUBLISHKEY = getUpdateStripeWallet.getString(Utils.SP_COMMON_STRIPE_PUBLISH_KEY, null);
        STRIPE_SECRETKEY = getUpdateStripeWallet.getString(Utils.SP_COMMON_STRIPE_SECRET_KEY, null);

        Log.e("driverId~~StripePaymentFragment~~", driverId);

        initView(view);

        view.findViewById(R.id.rl_parent_lyt_stripe).setOnClickListener(view1 -> {});

        onBackButton(view);

        return view;

    }

    void initView(View view) {


        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);


        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

        if (STRIPE_PUBLISHKEY == null) {
            if (BuildConfig.DEBUG) {
                STRIPE_PUBLISHKEY = Utils.com_stripe_publishable_key;
            }
            /*else {
                STRIPE_PUBLISHKEY = Utils.com_stripe_publishable_key;
            }*/
            callAppSettings();
        }

        /*if (STRIPE_PUBLISHKEY == null) {
            STRIPE_PUBLISHKEY = Utils.com_stripe_publishable_key;
            callAppSettings();
        }*/

        creditCardModule = (CreditCardModule) view.findViewById(R.id.credit_card_module);
        //creditCardModuleStripe = (CardInputWidget) view.findViewById(R.id.credit_card_module_stripe);

        btnPayStripe = (CustomButton) view.findViewById(R.id.pay_button);

        btnPayStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callClickAction(btnPayStripe, false);
                //stripePayment();
            }
        });
    }

    private void callClickAction(View view, Boolean b) {
        view.setEnabled(b);
        view.setFocusableInTouchMode(b);
        view.setFocusable(b);
        view.setClickable(b);
    }

    /*@SuppressLint("LongLogTag")
    private void stripePayment() {
        BaseActivity.showProgressDialog(getActivity());
        // Log.e("Stripe Key inside payment~~~ ",STRIPE_PUBLISHKEY);
        cardToSave = creditCardModuleStripe.getCard();

        if (cardToSave == null) {
            BaseActivity.hideProgressDialog();
            Toast.makeText(getActivity(), R.string.invalid_card_details, Toast.LENGTH_SHORT).show();
            callClickAction(btnPayStripe, true);
            return;

        } else {

        *//*creditCardNumber = creditCardModule.getCreditCardNumberEditField().getRawCreditCardNumber();
        creditCardCvv = creditCardModule.getCreditCardController().getCVV();
        creditCardYear = creditCardModule.getCreditCardController().getExpirationDate().toString();

        Card card = new Card((creditCardNumber),
                Integer.valueOf(creditCardMonth),
                Integer.valueOf(creditCardYear),
                creditCardCvv
        );*//*

            final Stripe stripe = new Stripe(Objects.requireNonNull(getActivity()));
            stripe.createToken(cardToSave, STRIPE_PUBLISHKEY, new TokenCallback() {
                public void onSuccess(@NonNull Token token) {
                    // TODO: Send Token information to your backend to initiate a charge
                    stripeTokenID = token.getId();
                    // Log.e("TokenID==>",token.getId());
                    BaseActivity.hideProgressDialog();
                    callWalletupdatewithStripe();
                }

                public void onError(@NonNull Exception error) {
                    callClickAction(btnPayStripe, true);
                    BaseActivity.hideProgressDialog();
                    Log.d("Stripe error", error.getLocalizedMessage());
                    // Toast.makeText(getActivity(), R.string.stripe_error_msg, Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }*/

   /* @SuppressLint("LongLogTag")
    private void callWalletupdatewithStripe() {
        userAccessToken = getUpdateStripeWallet.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("driver_id", driverId);
        map1.put("transaction_amount", strWalletRechargeAmt);
        map1.put("transaction_mode", "card");
        map1.put("transaction_token", stripeTokenID);
        map1.put("card_number", cardToSave.getNumber());

        Log.i("callWalletupdatewithStripe", "~~~~" + map1);

        BaseActivity.showProgressDialog(getActivity());
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WalletAddMoneyModel> call2 = apiInterface.doUpdateWalletAmount("Bearer " + userAccessToken, map1);
        call2.enqueue(new Callback<WalletAddMoneyModel>() {
            @Override
            public void onResponse(@NonNull Call<WalletAddMoneyModel> call, @NonNull Response<WalletAddMoneyModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Money added in wallet successfully.")) {

                        callClickAction(btnPayStripe, true);

                        String walletAmount = String.valueOf(Objects.requireNonNull(response.body()).getWalletBalance());

                        double roundedNumber = DecimalUtils.round(response.body().getWalletBalance(), 2);

                        putUpdateStripeWallet.putString(Utils.SP_DRIVER_WALLET_AMT, String.valueOf(roundedNumber));
                        putUpdateStripeWallet.apply();

                        BaseActivity.hideProgressDialog();
                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                        refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue(roundedNumber);

                        hideKeyBoard();

                        //MainActivity.frameClickable(true);
                        Bundle args = new Bundle();
                        args.putString(Utils.B_WALLET_RECHARGE_AMOUNT, walletAmount);
                        args.putString(Utils.B_WALLET_PAGE, "wallet");
                        Utils.switchContent(R.id.frame_mainactivity,
                                Utils.FRAGMENT_MYWALLET,
                                Objects.requireNonNull(getActivity()), args,
                                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Credit Amount should be grater than 50Cents")) {

                        callClickAction(btnPayStripe, true);

                        Toast.makeText(getActivity(), getResources().getString(R.string.please_recharge_valid_amount), Toast.LENGTH_SHORT).show();

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Amount must be at least $0.50 usd")) {

                        callClickAction(btnPayStripe, true);

                        Toast.makeText(getActivity(), getResources().getString(R.string.please_recharge_valid_amount), Toast.LENGTH_SHORT).show();

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Amount must be at least $0.50 USD")) {

                        callClickAction(btnPayStripe, true);

                        Toast.makeText(getActivity(), getResources().getString(R.string.please_recharge_valid_amount), Toast.LENGTH_SHORT).show();

                    } else {

                        callClickAction(btnPayStripe, true);

                        Toast.makeText(getActivity(), Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    BaseActivity.hideProgressDialog();

                } else if (response.code() == RESPONSECODE_401) {

                    BaseActivity.hideProgressDialog();

                    callClickAction(btnPayStripe, true);

                    callRefreshToken();

                } else if (response.code() == RESPONSECODE_500) {

                    BaseActivity.hideProgressDialog();
                    Log.i(TAG, "onResponse: success");

                    callClickAction(btnPayStripe, true);

                    Toast.makeText(getActivity(), "Response Error 500", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletAddMoneyModel> call, @NonNull Throwable t) {
                call.cancel();
                callClickAction(btnPayStripe, true);
                Log.e(TAG, "onFailure: ~~~~~" + t.toString());
                BaseActivity.hideProgressDialog();
            }
        });
    }*/

    private void callRefreshToken() {
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
                    if ("wallet".equalsIgnoreCase("wallet")) {
                       // callWalletupdatewithStripe();
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

        putUpdateStripeWallet.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        putUpdateStripeWallet.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        putUpdateStripeWallet.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        putUpdateStripeWallet.apply();
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
        hideKeyBoard();
       // MainActivity.frameClickable(false);
        // removeFragment(Utils.FRAGMENT_STRIPE_PAYMENT);
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MYWALLET,
                Objects.requireNonNull(getActivity()), null,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }


    private void callAppSettings() {

        /*if(appSettingArray!=null)
            appSettingArray.clear();*/

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AppSettingWebModel> call2 = apiInterface.doGetAppSettings();
        call2.enqueue(new Callback<AppSettingWebModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<AppSettingWebModel> call, @NonNull Response<AppSettingWebModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    STRIPE_PUBLISHKEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentPublisherKey();
                    STRIPE_SECRETKEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentSecretKey();
                    Log.e(TAG, "onResponse: StripeKeys Admin" + STRIPE_PUBLISHKEY + "~~" + STRIPE_SECRETKEY);
                    putUpdateStripeWallet.putString(Utils.SP_COMMON_STRIPE_PUBLISH_KEY, STRIPE_PUBLISHKEY);
                    putUpdateStripeWallet.putString(Utils.SP_COMMON_STRIPE_SECRET_KEY, STRIPE_SECRETKEY);
                    putUpdateStripeWallet.apply();

                }
            }

            @Override
            public void onFailure(@NonNull Call<AppSettingWebModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }


}
