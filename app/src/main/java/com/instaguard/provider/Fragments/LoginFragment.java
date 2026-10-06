package com.instaguard.provider.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.BuildConfig;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.LoginModel;
import com.instaguard.provider.Models.LoginResults;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.instaguard.provider.Activities.BaseActivity.callClickAction;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;


public class LoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;

    private Toolbar toolbarSignin;
    private ImageButton imgbtnBack, imgbtnClearEmail, imgbtnClearPwd;

    private CustomEditText edtEmail, edtPassword;
    private ImageView ivFacebook, ivTwitter, ivGoogle;
    private CustomButton btnConfirmsignin;
    private CustomTextView txtSignup, txtForgotpassword;
    private AwesomeValidation Validation;

    private SharedPreferences.Editor getState, getStateRemember, getSpCurrency;

    public String TAG = "LoginFragment";
    private String deviceId;

    private String spRememberChecked;
    private SharedPreferences sprefs, sprefsRemember, sprefsGlobalDeviceId, sprefsCurrency;
    private CheckBox cbRememberMe;
    private boolean boolRememberChecked = false;

    public DatabaseReference driverSearchDatabaseBRef;
    public ValueEventListener driverSearchValueEventListener;
    public String strCurrency;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private String strServiceStatus, strLanguage;

    public static SuccessResponseLogin SuccessResponseLogin;
    FrameLayout contentView;

    Locale locale;

    public interface SuccessResponseLogin {
        void SuccessResponseLogin(String langname, String code, String country, String key);
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        view = inflater.inflate(R.layout.fragment_login, container, false);

        getState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getStateRemember = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME_REMEMBER, MODE_PRIVATE).edit();
        getSpCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();

        sprefsGlobalDeviceId = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = sprefsGlobalDeviceId.getString(Utils.SP_DEVICE_ID, null);

        sprefsCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strCurrency = sprefsCurrency.getString(Utils.SP_CURRENCY, null);
        System.out.println("LoginFrag-->" + strCurrency);

        sprefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        sprefsRemember = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME_REMEMBER, MODE_PRIVATE);

        spRememberChecked = sprefsRemember.getString(Utils.SP_CHECKED_REMEMBERME, null);
        // SharedPreferences pref = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        // deviceId = pref.getString(Utils.SP_DEVICE_ID, null);
        if (spRememberChecked == null) {
            spRememberChecked = "null";
        } else {
            Log.i(TAG, "onCreateView: spRemeberme-->" + spRememberChecked);
        }

        //  SharedPreferences pref = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        // deviceId = pref.getString(Utils.SP_DEVICE_ID, null);
        Log.i(TAG, "token in singinpage--> " + deviceId);
        if (deviceId == null)
            deviceId = "123";

        toolbarSignin = (Toolbar) view.findViewById(R.id.toolbar_signin);
        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);

        imgbtnClearEmail = (ImageButton) view.findViewById(R.id.imgbtn_clear_email);
        imgbtnClearPwd = (ImageButton) view.findViewById(R.id.imgbtn_clear_password);

        ivFacebook = (ImageView) view.findViewById(R.id.iv_facebook);
        ivTwitter = (ImageView) view.findViewById(R.id.iv_twitter);
        ivGoogle = (ImageView) view.findViewById(R.id.iv_google);

        edtEmail = (CustomEditText) view.findViewById(R.id.edt_email);
        edtPassword = (CustomEditText) view.findViewById(R.id.edt_password);
        contentView = (FrameLayout) view.findViewById(R.id.frame_signin);

        btnConfirmsignin = (CustomButton) view.findViewById(R.id.btn_confirmsignin);

        txtForgotpassword = (CustomTextView) view.findViewById(R.id.txt_forgotpassword);
        txtSignup = (CustomTextView) view.findViewById(R.id.txt_createanewaccount);

        cbRememberMe = (CheckBox) view.findViewById(R.id.cb_rememberme);

        if (spRememberChecked.equals("true")) {
            edtEmail.setText(sprefsRemember.getString(Utils.SP_DRIVER_EMAIL_REMEMBERME, null));
            // edtPassword.setText(sprefsRemember.getString(Utils.SP_DRIVER_PASS_REMEMBERME, null));
            cbRememberMe.setChecked(true);
            boolRememberChecked = true;
            imgbtnClearEmail.setVisibility(View.VISIBLE);
            imgbtnClearPwd.setVisibility(View.GONE);
        } else {
            imgbtnClearEmail.setVisibility(View.GONE);
            imgbtnClearPwd.setVisibility(View.GONE);
        }

        // validation initalize
        Validation = new AwesomeValidation(BASIC);


        imgbtnClearEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEmail.setText("");
            }
        });

        imgbtnClearPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPassword.setText("");
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    imgbtnClearEmail.setVisibility(View.GONE);
                } else {
                    imgbtnClearEmail.setVisibility(View.VISIBLE);
                }
            }
        });
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    imgbtnClearPwd.setVisibility(View.GONE);
                } else {
                    imgbtnClearPwd.setVisibility(View.VISIBLE);
                }
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    imgbtnClearEmail.setVisibility(View.GONE);
                } else {
                    imgbtnClearEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgbtnClearEmail.setVisibility(View.GONE);
                } else {
                    imgbtnClearEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    imgbtnClearPwd.setVisibility(View.GONE);
                } else {
                    imgbtnClearPwd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgbtnClearPwd.setVisibility(View.GONE);
                } else {
                    imgbtnClearPwd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnConfirmsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                ValidationForEditText();
            }
        });

        txtForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_FORGOTPASSWORD,
                        getActivity(), null,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });

        //  CallPaymentDetails();

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_SIGNUP,
                        getActivity(), null,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });
        cbRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolRememberChecked = isChecked;
                if (!isChecked) {
                    getStateRemember.putString(Utils.SP_DRIVER_EMAIL_REMEMBERME, "");
                    getStateRemember.putString(Utils.SP_DRIVER_PASS_REMEMBERME, "");
                    getStateRemember.putString(Utils.SP_CHECKED_REMEMBERME, "false");
                    getStateRemember.apply();
                }

            }
        });

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                backButtonFunction();
            }
        });
        searchCurrencyPrice(strCurrency); //vign27
        onBackButton(view);
        return view;
    }


    //validation controller
    private void ValidationForEditText() {
        // Validation.addValidation(getActivity(), R.id.edt_password, "(?=.*[a-z]).{8,}", R.string.log_in_error_password);
        if (isValidMobile(Objects.requireNonNull(edtEmail.getText()).toString())) {// Phone number is valid
            Log.i(TAG, "ValidationForEditText: valid mobile");
            callLogin("mobile");
            callClickAction(btnConfirmsignin, true);
        } else if (isEmailValid(edtEmail.getText().toString())) {//Email is valid
            Log.i(TAG, "ValidationForEditText: valid email");
            callLogin("email");
            callClickAction(btnConfirmsignin, true);
        } else {// Not valid email or phone number
            Log.i(TAG, "ValidationForEditText: valid else");
            edtEmail.setError(getString(R.string.log_in_error_email));
//            showSnackbar(getString(R.string.error_all_fields_required), "Field");
            BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.error_all_fields_required), "Field");
            callClickAction(btnConfirmsignin, true);
           /*if (Validation.validate()) {
            } else {
                onBackButton(view);
            }*/
        }
    }

    public void callLogin(String type) {
        //if (Validation.validate()) {
        if (Objects.requireNonNull(edtPassword.getText()).toString().isEmpty()) {
//            showSnackbar(getString(R.string.error_all_fields_required), "Field");
            BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.error_all_fields_required), "Field");
            callClickAction(btnConfirmsignin, true);
        } else {
            callClickAction(btnConfirmsignin, true);
            //  onBackButton(view);
            CallLoginDetails(type);
        }
    }


    private void CallLoginDetails(String type) {
        HashMap<String, Object> map1 = new HashMap<>();
        /*if (type.equals("mobile")) {
            map1.put("phone", edtEmail.getText().toString());
            map1.put("email", "");
            map1.put("password", edtPassword.getText().toString());
            map1.put("device_id", deviceId);
        } else {
            map1.put("phone", "");
            map1.put("email", edtEmail.getText().toString().toLowerCase());
            map1.put("password", edtPassword.getText().toString());
            map1.put("device_id", deviceId);
        }*/
        Log.i(TAG, "CallLoginDetails: deviceid-->" + deviceId);
        map1.put("email", edtEmail.getText().toString().toLowerCase());
        map1.put("password", edtPassword.getText().toString());
        map1.put("device_token", deviceId);

        Log.i("CallLoginDetails", "~~~~" + map1);
        showProgressDialog(getActivity());
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LoginResults> call2 = apiInterface.doGetLoginDetails(map1);
        call2.enqueue(new Callback<LoginResults>() {
            @Override
            public void onResponse(@NonNull Call<LoginResults> call, @NonNull Response<LoginResults> response) {
                hideProgressDialog();
                Log.i(TAG, "onResponse: resonse code--> " + response.code());
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (response.body() != null && response.body().getMessage().equals("loggedin successfully")) {

                        if (response.body().getResult() != null) {

                            String access_token = Objects.requireNonNull(response.body().getResult()).getAccessToken();
                            String token_type = Objects.requireNonNull(response.body().getResult()).getTokenType();
                            String expires_in = String.valueOf(Objects.requireNonNull(response.body().getResult()).getExpiresIn());
                            String UserId = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getId());
                            String Firstname = Objects.requireNonNull(response.body().getResult()).getDriverData().getFirstName();
                            String Lastname = Objects.requireNonNull(response.body().getResult()).getDriverData().getLastName();
                            String Email = Objects.requireNonNull(response.body().getResult()).getDriverData().getEmail();
                            String UserProPic = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getAvatar());
                            String Dob = Objects.requireNonNull(response.body().getResult()).getDriverData().getBirthDate();
                            String State = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getStateId());
                            String Country = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getCountryId());
                            String Address = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getAddress());
                            String Mobile = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getPhoneNumber());
                            String Countrycode = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getCountryCode());
                            String WalletAmount = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getWalletBalance());
                            String Referal = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getReferralCode());
                            String InviteCode = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getInviteCode());
                            String currency = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getCurrency());
                            String currencysymbol = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getCurrencySymbol());

                            strLanguage = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getLanguage());
                            strServiceStatus = String.valueOf(Objects.requireNonNull(response.body().getResult()).getDriverData().getServiceIds());

                            savesharedpreference(access_token, token_type, expires_in, UserId, Firstname, Lastname, Email, UserProPic, Dob, State,
                                    Country, Address, Mobile, Countrycode, WalletAmount, Referal, InviteCode, currency, currencysymbol
                                    , edtPassword.getText().toString().trim());

                        } else {
//                            showSnackbar(getString(R.string.login_Invalid_credentials), "warning");
                            BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.login_Invalid_credentials), "warning");
                        }
                    } else {
                        if (response.body() != null) {
//                            showSnackbar(response.body().getMessage(), "warning");
                            BaseActivity.showSnackbar(requireActivity(),contentView,response.body().getMessage(), "warning");
                        }
                    }

                } else if (response.code() == RESPONSECODE_401) {
//                    showSnackbar("401 " + getString(R.string.login_Invalid_credentials), "warning");
                    BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.login_Invalid_credentials), "warning");
                } else {
//                    showSnackbar("500 Error" + getString(R.string.login_Invalid_credentials), "warning");
                    BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.login_Invalid_credentials), "warning");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResults> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                call.cancel();
//                showSnackbar("onFailure" + t.toString(), "warning");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.login_Invalid_credentials), "warning");

            }
        });
    }


    private void savesharedpreference(String access_token, String token_type, String exp_time, String userId,
                                      String firstname, String lastname, String email, String userProPic, String dob,
                                      String state, String country, String address, String Mobile, String Countrycode,
                                      String WalletAmount, String referal, String inviteCode, String currency
            , String currencysymbol, String password) {

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        getState.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);

        getState.putString(Utils.SP_DRIVERID, userId);

        getState.putString(Utils.SP_DRIVER_FNAME, firstname);
        getState.putString(Utils.SP_DRIVER_LNAME, lastname);
        getState.putString(Utils.SP_DRIVER_EMAIL, email);
        getState.putString(Utils.SP_DRIVER_PASSWORD, password);
        getState.putString(Utils.SP_DRIVER_PRO_PIC, userProPic);
        getState.putString(Utils.SP_DRIVER_DOB, dob);
        getState.putString(Utils.SP_DRIVER_STATE, state);
        getState.putString(Utils.SP_DRIVER_COUNTRY, country);
        getState.putString(Utils.SP_DRIVER_ADDRESS, address);
        getState.putString(Utils.SP_DRIVER_MOBILE, Mobile);
        getState.putString(Utils.SP_DRIVER_COUNTRY_CODE, Countrycode);
        // getState.putString(Utils.SP_CURRENCY,currency);
        getState.putString(Utils.SP_DRIVER_WALLET_AMT, WalletAmount);
        getState.putString(Utils.SP_DRIVER_REFERAL_CODE, referal);
        getState.putString(Utils.SP_DRIVER_INVITE_CODE, inviteCode);
        getState.putString(Utils.SP_DRIVER_DELETE_ACCOUNT, "0");
        getState.apply();

        getState.putString(Utils.SP_CHECKED_REMEMBERME, "false");
        if (boolRememberChecked) {
            getStateRemember.putString(Utils.SP_DRIVER_EMAIL_REMEMBERME, edtEmail.getText().toString());
            getStateRemember.putString(Utils.SP_DRIVER_PASS_REMEMBERME, edtPassword.getText().toString());
            getStateRemember.putString(Utils.SP_CHECKED_REMEMBERME, String.valueOf(boolRememberChecked));
            getStateRemember.apply();
        }
        if (currency.equals("")) {
            strCurrency = sprefsCurrency.getString(Utils.SP_CURRENCY, null);
            Log.i(TAG, "savesharedpreference: if currency-->" + strCurrency);
            searchCurrencyPrice(strCurrency);
        } else {
            Log.i(TAG, "savesharedpreference: else");
            getSpCurrency.putString(Utils.SP_CURRENCY, currency);
            getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, currencysymbol);
            searchCurrencyPrice(currency);
            getSpCurrency.apply();
        }
        if (strServiceStatus != null) {
            if (!strServiceStatus.equalsIgnoreCase("")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("MissingPermission~~~", "currentLocationClick~~MissingPermission");
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                        btnConfirmsignin.setEnabled(true);
                    } else {
                        callLanguage(strLanguage);
                    }
                } else {
                    callLanguage(strLanguage);
                }
            } else {

                if (strLanguage.equals("")) {
                    getSpCurrency.putString(Utils.SP_LANGUAGE, "English");
                    getSpCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, "en");
                    getSpCurrency.putString(Utils.SP_LANGUAGE_COUNTRY, "EN");
                    getSpCurrency.apply();
                }

                Bundle args = new Bundle();
                args.putString("key", "login");
                Intent Signin = new Intent(getActivity(), MainActivity.class);
                startActivity(Signin);
                requireActivity().finish();
                /*Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_MANAGE_SERVICE,
                        requireActivity(), args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/
            }
        } else {
            Toast.makeText(getActivity(), "Services status empty", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: ~~~~~~~~~~~~" + requestCode);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            callLanguage(strLanguage);
        }
    }

    private void callLanguage(String language) {

        getState.putBoolean(Utils.SP_DRIVER_LOGIN, true);
        getState.apply();

        if (language.equals("")) {
            if (BuildConfig.DEBUG) Log.e(TAG, "savesharedpreference: ");
            if (SuccessResponseLogin != null)
                SuccessResponseLogin.SuccessResponseLogin(language, "en", "US", "login");
        } else {
            switch (language) {
                case "Sinhala":
                    if (SuccessResponseLogin != null)
                        SuccessResponseLogin.SuccessResponseLogin(language, "si", "LK", "login");
                    break;
                case "English":
                default:
                    if (SuccessResponseLogin != null)
                        SuccessResponseLogin.SuccessResponseLogin(language, "en", "US", "login");
                    break;
            }
        }
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
        removeFragment(Utils.FRAGMENT_SIGNIN);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
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

    public static boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 9 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                // txtPhone.setError("Not Valid Number");
            } else {
                check = android.util.Patterns.PHONE.matcher(phone).matches();
            }
        } else {
            check = false;
        }
        return check;
    }

    public static boolean isEmailValid(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        return check;
    }

    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void searchCurrencyPrice(String Currency) {

        if (driverSearchDatabaseBRef != null) {
            driverSearchDatabaseBRef.removeEventListener(driverSearchValueEventListener);
        }
        driverSearchDatabaseBRef = FirebaseDatabase.getInstance().getReference().child("currency").child(Currency);
        driverSearchValueEventListener = driverSearchDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    Object currencyPriceObj = dataSnapshot.getValue();
                    if (currencyPriceObj != null) {
                        Log.i(TAG, "onDataChange: in search currency-->" + currencyPriceObj.toString());
                        ;
                        getSpCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, currencyPriceObj.toString());
                        getSpCurrency.apply();
                    } else {
                        getSpCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                        getSpCurrency.apply();
                    }
                    if (driverSearchDatabaseBRef != null) {
                        driverSearchDatabaseBRef.removeEventListener(driverSearchValueEventListener);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: error occur " + databaseError);
                getSpCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                getSpCurrency.apply();

            }
        });
    }
}
