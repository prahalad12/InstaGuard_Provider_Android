package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.BankData;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.BankUpdateModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;

public class BankDetailFragment extends Fragment {

    View view;
    SharedPreferences SP_Prefs;
    String driverId;
    ImageButton imgbtnBack;

    CustomEditText edtPaymentEmail, edtPaymentBankAcName, edtPaymentBankCode, edtPaymentBankAcNo, edtPaymentBankAcLoc, edtPaymentBankName;
    CustomButton btnUpdateBankdetails;

    ImageButton imgbtnClearPaymentEmail, imgbtnClearBankAcName, imgbtnClearBankAcNo, imgbtnClearBankAcLoc, imgbtnClearBankName, imgbtnClearBankCode;

    SharedPreferences.Editor getUpdateBank;
    SharedPreferences spUpdateBank;

    String userAccessToken;

    private AwesomeValidation Validation;


    public BankDetailFragment() {
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
        view = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        getUpdateBank = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        spUpdateBank = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = spUpdateBank.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spUpdateBank.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("UserId~~SettingsFragment~~", driverId);
        // validation initalize
        Validation = new AwesomeValidation(BASIC);


        initView(view);

        onBackButton(view);

        return view;

    }

    void initView(View view) {


        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);

        edtPaymentEmail = (CustomEditText) view.findViewById(R.id.edt_payment_email);
        edtPaymentBankAcName = (CustomEditText) view.findViewById(R.id.edt_payment_bank_ac);
        edtPaymentBankAcNo = (CustomEditText) view.findViewById(R.id.edt_payment_ac_num);
        edtPaymentBankAcLoc = (CustomEditText) view.findViewById(R.id.edt_payment_bank_location);
        edtPaymentBankName = (CustomEditText) view.findViewById(R.id.edt_payment_bank_name);
        edtPaymentBankCode = (CustomEditText) view.findViewById(R.id.edt_payment_code);

        btnUpdateBankdetails = (CustomButton) view.findViewById(R.id.btn_updatesettings);


        imgbtnClearPaymentEmail = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_email);
        imgbtnClearBankAcName = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_bank_ac);
        imgbtnClearBankAcNo = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_ac_num);
        imgbtnClearBankAcLoc = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_bank_location);
        imgbtnClearBankName = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_bank_name);
        imgbtnClearBankCode = (ImageButton) view.findViewById(R.id.imgbtn_clear_payment_code);

        onClearViews(edtPaymentEmail, imgbtnClearPaymentEmail);
        onClearViews(edtPaymentBankAcName, imgbtnClearBankAcName);
        onClearViews(edtPaymentBankAcNo, imgbtnClearBankAcNo);
        onClearViews(edtPaymentBankAcLoc, imgbtnClearBankAcLoc);
        onClearViews(edtPaymentBankName, imgbtnClearBankName);
        onClearViews(edtPaymentBankCode, imgbtnClearBankCode);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

        btnUpdateBankdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Validation.addValidation(getActivity(), R.id.edt_payment_email, Patterns.EMAIL_ADDRESS, R.string.register_error_email);
                //  Validation.addValidation(getActivity(), R.id.edt_payment_bank_ac, "[a-zA-Z\\s]+", R.string.register_error_firstname);
                //  Validation.addValidation(getActivity(), R.id.edt_payment_bank_name, "[a-zA-Z\\s]+", R.string.register_error_firstname);

                if (Validation.validate()) {
                    if (edtPaymentBankAcName.getText().toString().trim().length() == 0) {
                        edtPaymentBankAcName.setError(getString(R.string.error_required));
                    } else if (edtPaymentBankAcNo.getText().toString().trim().length() == 0) {
                        edtPaymentBankAcNo.setError(getString(R.string.error_required));
                    } else if (edtPaymentBankAcLoc.getText().toString().trim().length() == 0) {
                        edtPaymentBankAcLoc.setError(getString(R.string.error_required));
                    } else if (edtPaymentBankName.getText().toString().trim().length() == 0) {
                        edtPaymentBankName.setError(getString(R.string.error_required));
                    } else if (edtPaymentBankCode.getText().toString().trim().length() == 0) {
                        edtPaymentBankCode.setError(getString(R.string.error_required));
                    } else {
                        callUpdateDetails();
                    }
                } else {
                    onBackButton(view);
                }

                /*String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(edtPaymentEmail.getText().toString().trim().length() == 0){
                    edtPaymentEmail.setError(getString(R.string.error_required));
                }else if (edtPaymentEmail.getText().toString().trim().matches(emailPattern)) {
                    edtPaymentEmail.setError(getString(R.string.error_required));
                }*/
            }
        });

//        callViewBankDetails();
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


    private void callUpdateDetails() {
        userAccessToken = spUpdateBank.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("bank_email", edtPaymentEmail.getText().toString().trim());
        map.put("account_holder_name", edtPaymentBankAcName.getText().toString().trim());
        map.put("bank_name", edtPaymentBankName.getText().toString().trim());
        map.put("bank_location", edtPaymentBankAcLoc.getText().toString().trim());
        map.put("bank_code", edtPaymentBankCode.getText().toString().trim());
        map.put("bank_account_number", edtPaymentBankAcNo.getText().toString().trim());
        Log.i("callBankUpdateDetails", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BankUpdateModel> call2 = apiInterface.doBankUpdateDetails("Bearer " + userAccessToken, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<BankUpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<BankUpdateModel> call, @NonNull Response<BankUpdateModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver bank details updated  successfully.")) {
                        showUpdateAlertDialog(getActivity(), getString(R.string.editsettings_update_successfully));
                    } else {
                        Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                        // callRefreshToken("edit");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("edit");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankUpdateModel> call, @NonNull Throwable t) {
                // CommonUtil.showRetrofitError(LoginActivity.this, error);
                hideProgressDialog();
                Log.e("~~~", "onFailure: ~~~~~~~" + t.toString());
                call.cancel();
            }
        });
    }

    /*private void callViewBankDetails() {
        userAccessToken = spUpdateBank.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", userAccessToken);
        Log.i("callViewBankDetails", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BankModel> call2 = apiInterface.doGetBankDetails("Bearer " + userAccessToken, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<BankModel>() {
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("bank details listed successfully.")) {

                        List<BankData> getve = Objects.requireNonNull(response.body()).getBankData();
                        String[] items = new String[getve.size()];
                        Log.i("itemsitems", "" + Arrays.toString(items));
                        for (int i = 0; i < getve.size(); i++) {

                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getAccountHolderName() != null) {
                                edtPaymentBankAcName.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getAccountHolderName());
                            } else {
                                edtPaymentBankAcName.setHint(R.string.bankdetails_bank_account_holder_name_hint);
                            }
                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getBankAccountNumber() != null) {
                                edtPaymentBankAcNo.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getBankAccountNumber());
                            } else {
                                edtPaymentBankAcNo.setHint(R.string.bankdetails_account_number_hint);
                            }
                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getBankCode() != null) {
                                edtPaymentBankCode.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getBankCode());
                            } else {
                                edtPaymentBankCode.setHint(R.string.bankdetails_bic_swift_code_hint);
                            }
                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getBankLocation() != null) {
                                edtPaymentBankAcLoc.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getBankLocation());
                            } else {
                                edtPaymentBankAcLoc.setHint(R.string.bankdetails_bank_location_hint);
                            }
                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getBankName() != null) {
                                edtPaymentBankName.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getBankName());
                            } else {
                                edtPaymentBankName.setHint(R.string.bankdetails_bank_name_hint);
                            }
                            if (Objects.requireNonNull(response.body()).getBankData().get(i).getPaymentEmail() != null) {
                                edtPaymentEmail.setText(Objects.requireNonNull(response.body()).getBankData().get(i).getPaymentEmail());
                            } else {
                                edtPaymentEmail.setHint(R.string.bankdetails_payment_email_hint);
                            }
                        }

                    } else {
                        Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
//                        callRefreshToken("view");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("view");
                } else if (response.code() == RESPONSECODE_500) {
                    callRefreshToken("view");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });

    }*/

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
                    if (logout.equalsIgnoreCase("view")) {
//                        callViewBankDetails();
                    } else if (logout.equalsIgnoreCase("edit")) {
                        callUpdateDetails();
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

        getUpdateBank.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        getUpdateBank.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        getUpdateBank.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        getUpdateBank.apply();
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

    private void showUpdateAlertDialog(Activity self, String message) {

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
                    }
                }).show();
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
        removeFragment(Utils.FRAGMENT_BANKDETAIL);
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

}
