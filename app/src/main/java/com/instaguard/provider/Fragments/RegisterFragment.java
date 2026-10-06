package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.CityListAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;

import com.instaguard.provider.Helpers.CountryCode.CountryCodeDialog;
import com.instaguard.provider.Helpers.CountryCode.CountryCodePicker;
import com.instaguard.provider.Helpers.Logger;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utility;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.CitiesListData;
import com.instaguard.provider.Models.CitiesListModel;
import com.instaguard.provider.Models.EmailMobileCheckingModel;
import com.instaguard.provider.Models.RegisterModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static java.lang.System.out;


public class RegisterFragment extends Fragment {

    CustomEditText edtFirstName, edtLastName, edtEmail, edtMobile, edtPassword,edtCity, edtReferralcode, edtCountryCode;
    CustomButton btnConfirmSignup;
    ImageButton imgbtnBack;
    CustomTextView txtAlreadySignin, txtTermsService, txtPrivacyPolicy;
    CountryCodePicker ccp;
    CheckBox cbIAgree;
    Spinner CitySpinner;
    String SelectedCityName;
    SharedPreferences.Editor getState;
    SharedPreferences sprefsGlobalDeviceId;

    ImageButton imgbtnClearFirstName, imgbtnClearLastName, imgbtnClearEmail, imgbtnClearPassword, imgbtnClearMobile, imgbtnClearReflCode;

    String strCountyCode, strMobile, strEmail, strSelectedcity,deviceId, OTP_message, OTP_msg, OTP_Status, OTP_mobilenum, WEB_OTP, StrEmail;
    Dialog OTPDialog;

    View view;

    public String TAG = "RegisterFragment";
    List<CitiesListData> citiesArrayList = new ArrayList<>();
    ArrayList<String> citiesnamesArrayList = new ArrayList<>();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth auth;
    Utility utility;
    String verificationID = "";
    boolean resend = false;
    Dialog dialogBuilder;

    private String strLanguageShortName, strLanguage;
    private SharedPreferences.Editor getStateCurrency;
    private SharedPreferences getspCommonCurrency;


    CustomEditText companyName;
    private AwesomeValidation Validation;

    RadioGroup providerGrp;
    String strProviderType = "individual", strCompanyName = "InstaGuard";

    CityListAdapter adapterCityList;
    RecyclerView rcvCitiesList;
    Dialog dialogCityList;
    CustomEditText searchCity;

    String[] items;
    FrameLayout contentView;

    public RegisterFragment() {
        // Required empty public constructor
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
        view = inflater.inflate(R.layout.fragment_register, container, false);

        auth = FirebaseAuth.getInstance();
        utility = new Utility();


        getState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        sprefsGlobalDeviceId = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = sprefsGlobalDeviceId.getString(Utils.SP_DEVICE_ID, null);

        Log.e(TAG, "token in RegisterFragment--> " + deviceId);
        if (deviceId == null)
            deviceId = "123";

        getStateCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();
        getspCommonCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = getspCommonCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCommonCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        if (strLanguage == null) {
            strLanguage = "English";
            //   strLanguage = "Spanish";
        }

        edtFirstName = (CustomEditText) view.findViewById(R.id.edt_firstname);
        edtLastName = (CustomEditText) view.findViewById(R.id.edt_lastname);
        edtEmail = (CustomEditText) view.findViewById(R.id.edt_email);
        edtCountryCode = (CustomEditText) view.findViewById(R.id.edt_country_code);
        edtMobile = (CustomEditText) view.findViewById(R.id.edt_mobile);
        edtPassword = (CustomEditText) view.findViewById(R.id.edt_password);
        edtCity = (CustomEditText) view.findViewById(R.id.edt_city);
        edtReferralcode = (CustomEditText) view.findViewById(R.id.edt_referralcode);
        cbIAgree = (CheckBox) view.findViewById(R.id.cb_i_agree);
//      CitySpinner = (Spinner) view.findViewById(R.id.select_city);

        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);

        contentView = (FrameLayout) view.findViewById(R.id.frame_signup);
        txtAlreadySignin = (CustomTextView) view.findViewById(R.id.txt_alreadysignin);

        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        btnConfirmSignup = (CustomButton) view.findViewById(R.id.btn_confirmsignup);

        imgbtnClearFirstName = (ImageButton) view.findViewById(R.id.imgbtn_clear_first_name);
        imgbtnClearLastName = (ImageButton) view.findViewById(R.id.imgbtn_clear_last_name);
        imgbtnClearEmail = (ImageButton) view.findViewById(R.id.imgbtn_clear_email);
        imgbtnClearPassword = (ImageButton) view.findViewById(R.id.imgbtn_clear_pwd);
        imgbtnClearMobile = (ImageButton) view.findViewById(R.id.imgbtn_clear_mobile);
        imgbtnClearReflCode = (ImageButton) view.findViewById(R.id.imgbtn_clear_referralcode);

        providerGrp = view.findViewById(R.id.provider_type);

        companyName = view.findViewById(R.id.edt_company_name);

        txtPrivacyPolicy = view.findViewById(R.id.txt_i_agree_ll);
        txtTermsService = view.findViewById(R.id.txt_i_agree_llll);

        /*Dialog cityList initalize*/
        cityDialog();

        rcvCitiesList = dialogCityList.findViewById(R.id.cityListRV);

        searchCity = (CustomEditText)dialogCityList.findViewById(R.id.searchInput);

        searchCity.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                // Call filterQuery on text change
                filterQuery(editable.toString());
            }
        });


        // validation initalize
        Validation = new AwesomeValidation(BASIC);

        OnClick();

        onClearViews(edtFirstName, imgbtnClearFirstName);
        onClearViews(edtLastName, imgbtnClearLastName);
        onClearViews(edtEmail, imgbtnClearEmail);
        onClearViews(edtMobile, imgbtnClearMobile);
        onClearViews(edtPassword, imgbtnClearPassword);
        onClearViews(edtReferralcode, imgbtnClearReflCode);

        //GetCountryZipCode();

        GetCitiesList();

        onBackButton(view);

        return view;
    }

    private void cityDialog() {
        dialogCityList = new Dialog(requireActivity());
        dialogCityList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCityList.setContentView(R.layout.dialog_citylist);
        Objects.requireNonNull(dialogCityList.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCityList.setCancelable(true);

        WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
        lps.copyFrom(dialogCityList.getWindow().getAttributes());
        lps.width = WindowManager.LayoutParams.MATCH_PARENT;
        lps.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterQuery(String text) {
        try {
            if (citiesArrayList != null && !citiesArrayList.isEmpty()) {
                ArrayList<String> filteredNames = new ArrayList<>();

                for (CitiesListData s : citiesArrayList) {
                    if (s.getCityName().toLowerCase().startsWith(text.toLowerCase())) {
                        filteredNames.add(s.getCityName());
                    }
                }

                adapterCityList.setFilter(filteredNames);

                adapterCityList.notifyDataSetChanged();

                if (!filteredNames.isEmpty()) {
                    System.out.println("Filtered city names: " + filteredNames);
                } else {
                    System.out.println("No cities found for the query: " + text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetCitiesList() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CitiesListModel> call2 = apiInterface.callcitieslist("");
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<CitiesListModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<CitiesListModel> call, @NonNull Response<CitiesListModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();

                    if (response.body() != null && response.body().getCities() != null) {
                        citiesArrayList = response.body().getCities();

                        citiesnamesArrayList.clear();

                        for (int i = 0; i < citiesArrayList.size(); i++) {
                            citiesnamesArrayList.add(citiesArrayList.get(i).getCityName());
                            }


                        adapterCityList = new CityListAdapter(getActivity(), citiesnamesArrayList);
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        rcvCitiesList.setLayoutManager(horizontalLayoutManager);
                        rcvCitiesList.setAdapter(adapterCityList);
                        adapterCityList.notifyDataSetChanged();



                    } else {
                        out.println("Error: response body or cities list is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<CitiesListModel> call, Throwable t) {
                call.cancel();
                hideProgressDialog();
            }
        });
    }

    private void GetCountryZipCode() {

                String CountryZipCode = "";
                Locale locales = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

//        Log.e(TAG, "onCreateView: ~~~~~~3~~~~~~~~"+locales);
//        Log.e(TAG, "onCreateView: ~~~~~~4~~~~~~~~"+locales.getDisplayName());
//        Log.e(TAG, "onCreateView: ~~~~~~5~~~~~~~~"+locales.getLanguage());
                Log.e(TAG, "onCreateView: ~~~~~~6~~~~~~~~" + locales.getCountry());
                Log.e(TAG, "onCreateView: ~~~~~~7~~~~~~~~" + locales.getDisplayCountry());

                String[] rl = getActivity().getResources().getStringArray(R.array.CountryCodes);

                for (int i = 0; i < rl.length; i++) {
                    String[] g = rl[i].split(",");
                    if (g[1].trim().equals(locales.getCountry().trim())) {
                        CountryZipCode = g[0];
                        break;
                    }
                }

        edtCountryCode.setText(CountryZipCode);

        Log.e(TAG, "GetCountryZipCode: ~~~~~CountryZipCode~~~~~~" + CountryZipCode);
    }

    private void OnClick() {

        if (strLanguage != null) {
            if (strLanguage.equals("Sinhala")) {
                ccp.changeLanguage(CountryCodePicker.Language.SINHALA);
            } else {
                ccp.changeLanguage(CountryCodePicker.Language.ENGLISH);
            }
        } else {
            ccp.changeLanguage(CountryCodePicker.Language.ENGLISH);
        }

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                edtCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
            }
        });

        edtCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountryCodeDialog.openCountryCodeDialog(ccp);
            }
        });

        btnConfirmSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirmSignup.setEnabled(false);

                hideKeyBoard();
               /* Intent login = new Intent(getActivity(), MainActivity.class);
                startActivity(login);*/
                ValidationForEditText();
            }
        });

        txtAlreadySignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_SIGNIN,
                        getActivity(), null,
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
        providerGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id = radioGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.radio_individual:
                        companyName.setVisibility(View.GONE);
                        strCompanyName = "";
                        strProviderType = "individual";
                        break;
                    case R.id.radio_company:

                        //companyName.setText("");
                        companyName.setVisibility(View.VISIBLE);
                        strProviderType = "company";

                        Log.e(TAG, "ValidationForEditText: " + strCompanyName);
                        break;
                }
            }
        });
        companyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                strCompanyName = companyName.getText().toString().trim();
            }
        });


        txtPrivacyPolicy.setText(getResources().getString(R.string.privacy));
        txtTermsService.setText(getResources().getString(R.string.terms_of_service));

        SpannableString ss = new SpannableString(txtPrivacyPolicy.getText().toString());

        ClickableSpan clickableTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // show toast here
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);

            }
        };
        ss.setSpan(clickableTerms, 0, txtPrivacyPolicy.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtPrivacyPolicy.setText(ss);
        txtPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        txtPrivacyPolicy.setHighlightColor(Color.TRANSPARENT);

        SpannableString sss = new SpannableString(txtTermsService.getText().toString());

        ClickableSpan clickableTermss = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // show toast here
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);

            }
        };
        sss.setSpan(clickableTermss, 0, txtTermsService.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtTermsService.setText(sss);
        txtTermsService.setMovementMethod(LinkMovementMethod.getInstance());
        txtTermsService.setHighlightColor(Color.TRANSPARENT);


        txtPrivacyPolicy.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString(Utils.B_SUPPORT_FROM, "register");
            Utils.switchContent(R.id.frame_signup,
                    Utils.FRAGMENT_SUPPORT,
                    requireActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        });
        txtTermsService.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString(Utils.B_SUPPORT_FROM, "register");
            Utils.switchContent(R.id.frame_signup,
                    Utils.FRAGMENT_SUPPORT,
                    requireActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        });


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
                Log.e(TAG, "onFocusChange: ~~~~" + hasFocus);
                if (!hasFocus) {
                    imgBtnClear.setVisibility(View.GONE);
                } else {
                    imgBtnClear.setVisibility(View.VISIBLE);
                }
            }
        });

        rcvCitiesList.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(), rcvCitiesList,
                        new RecyclerTouchListener.OnTouchActionListener() {
                            @SuppressLint({"LongLogTag", "NewApi"})
                            @Override
                            public void onClick(View view, int position) {
                                out.println("Onclick~~~>>>rcvCitiesList");

                                if (position != -1) {
                                    String CityName = String.valueOf(adapterCityList.getFilteredCitiesList().get(position));
                                    out.println("SelectedCityName~~~>>>>" + CityName);
                                    dialogCityList.dismiss();
                                    edtCity.setText(CityName);
                                    searchCity.setText("");
                                }
                            }

                            @Override
                            public void onRightSwipe(View view, int position) { }

                            @Override
                            public void onLeftSwipe(View view, int position) { }
                        }
                )
        );
        edtCity.setOnClickListener(view ->
                dialogCityList.show());


    }

    //validation controller
    private void ValidationForEditText() {

        strSelectedcity = Objects.requireNonNull(edtCity.getText()).toString();
        out.println("Selectedcity ~~~>>>"+strSelectedcity);

        // String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        //String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}";
        String regexPassword = "(?=.*[a-z])(?=.*[0-9]).{8,}";

        Validation.addValidation(getActivity(), R.id.edt_firstname, "[a-zA-Z\\s]+", R.string.register_error_firstname);
        Validation.addValidation(getActivity(), R.id.edt_lastname, "[a-zA-Z\\s]+", R.string.register_error_lastname);
        Validation.addValidation(getActivity(), R.id.edt_mobile, RegexTemplate.TELEPHONE, R.string.register_error_enter_validnum);
        Validation.addValidation(getActivity(), R.id.edt_email, Patterns.EMAIL_ADDRESS, R.string.register_error_email);
        // Validation.addValidation(getActivity(), R.id.edt_password, "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}", R.string.error_password);
        Validation.addValidation(getActivity(), R.id.edt_password, regexPassword, R.string.log_in_error_password);
        if (Validation.validate()) {
            if (!validatePhone()) {
//                showSnackbar(getString(R.string.error_all_fields_required), "Field");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.error_all_fields_required), "Field");
                btnConfirmSignup.setEnabled(true);

            } else if (!validateCountryCode()) {
//                showSnackbar(getString(R.string.error_all_fields_required), "Field");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.error_all_fields_required), "Field");
                btnConfirmSignup.setEnabled(true);

            } else if (!validateUsing_libphonenumber()) {
                edtMobile.setError(getString(R.string.register_error_invalid_mobile_number));
                btnConfirmSignup.setEnabled(true);

            }else if  (strSelectedcity.equals("Select Your Town")||strSelectedcity.isEmpty()) {
                edtCity.setError(getString(R.string.select_town));
                btnConfirmSignup.setEnabled(true);
            }

            else {
                strEmail = edtEmail.getText().toString();
                strCountyCode = edtCountryCode.getText().toString().trim();
                strMobile = edtMobile.getText().toString().trim();
                strEmail = strEmail.replaceAll("%40", "");
                strEmail = strEmail.replaceAll("%20", "");

//              callMobileEmailchecking(strCountyCode, strMobile, strEmail);
                if (cbIAgree.isChecked()) {
                    callMobileEmailChecking(strCountyCode, strMobile, strEmail);
                   /* showProgressDialog(getActivity());
                    sentVerificationCode(strCountyCode, strMobile,strEmail);  //vignesh*/
                } else {
                    btnConfirmSignup.setEnabled(true);
//                    showSnackbar(getString(R.string.register_accept_terms_conditions), "warning");
                    BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.register_accept_terms_conditions), "warning");
                }
            }

        } else {
            btnConfirmSignup.setEnabled(true);
            onBackButton(view);
            // Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private boolean validateUsing_libphonenumber() {
        if (edtMobile.getText().toString().length() <= 1) {
            return false;
        } else if (edtMobile.getText().toString().substring(0, 1).matches("0")) {
            edtMobile.setError(getString(R.string.register_error_enter_validnum));
            return false;
        } else {
            strCountyCode = edtCountryCode.getText().toString();
            strMobile = edtMobile.getText().toString();
            if (validatePhone() && validateCountryCode()) {
                System.out.println("CountryCode==>" + strCountyCode);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    strCountyCode = strCountyCode.replace("+", "");
                }
                System.out.println("SDK_VERSION==>" + Build.VERSION.SDK_INT);
                System.out.println("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
                System.out.println("CountryCode1==>" + strCountyCode);
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(strCountyCode));
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                    phoneNumber = phoneNumberUtil.parse(strMobile, isoCode);
                    boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                    if (isValid) {
                        String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                        return true;
                    } else {
                        edtMobile.setError(getString(R.string.register_error_enter_a_valid_mobile_number));
                        return false;
                    }
                } catch (NumberParseException e) {
                    System.err.println("validation exception "+e.getMessage());
                }

            }
            return true;
        }

    }

    private boolean validateCountryCode() {

        if (edtCountryCode.getText().toString().trim().isEmpty()) {
            edtCountryCode.setError(getString(R.string.register_error_enter_country_code));
            return false;
        } else if (edtCountryCode.getText().toString().equals("Country")) {
            edtCountryCode.setError(getString(R.string.register_error_enter_country_code));
            return false;
        } else {
            edtCountryCode.setError(null);
        }
        return true;
    }

    private boolean validatePhone() {

        if (edtMobile.getText().toString().trim().isEmpty()) {
            edtMobile.setError(getString(R.string.register_error_enter_mobile_number));
            return false;
        } else if (edtCountryCode.getText().toString().trim().isEmpty()) {
            return false;
        } else if (!edtMobile.getText().toString().trim().isEmpty()) {
            if (edtMobile.getText().toString().substring(0, 1).matches("0")) {
                edtMobile.setError(getString(R.string.register_error_enter_validnum));
                return false;
            } else {
                int maxLengthofEditText = 15;
                edtMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                edtMobile.setError(null);
            }
            return true;
        }

        return true;
    }


    private void callMobileEmailChecking(String WithCC, String mobileNumber, String strEmail) {
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("phone_number", mobileNumber);
        map1.put("email", strEmail);
        map1.put("mode", "driver");
        Log.i("callMobileEmailChecking", "~~~~" + map1);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<EmailMobileCheckingModel> call2 = apiInterface.doGetEmailMobileChecking(map1);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<EmailMobileCheckingModel>() {
            @Override
            public void onResponse(@NonNull Call<EmailMobileCheckingModel> call, @NonNull Response<EmailMobileCheckingModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Email or Phone Number not exists in our db.")) {
                        // showProgressDialog(getActivity());
                        //  sentVerificationCode(WithCC, mobileNumber,strEmail);
                        CallRegisterDetails(WithCC, mobileNumber, strEmail,strSelectedcity);

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Phone Number already exists.")) {
                        btnConfirmSignup.setEnabled(true);
                        hideProgressDialog();
//                        showSnackbar(getString(R.string.register_mobile_already_exist), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.register_mobile_already_exist), "warning");

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Email already exists.")) {
                        btnConfirmSignup.setEnabled(true);
                        hideProgressDialog();
//                        showSnackbar(getString(R.string.register_email_already_exist), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.register_email_already_exist), "warning");
                    } else {
                        hideProgressDialog();
                        btnConfirmSignup.setEnabled(true);
//                        showSnackbar(getString(R.string.register_email_mobile_already_exist), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.register_email_mobile_already_exist), "warning");
                    }
                } else {
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmailMobileCheckingModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
            }
        });
    }

    private void sentVerificationCode(String strCCode, String phone, String strEmail) {
        Log.e("sentVerificationCode~~", strCCode + phone);
        String phoneNo = strCCode + phone;
        Logger.printDebug("codeeee___" + phoneNo);
        callback_verificvation(strCCode, phone, strEmail);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo, 60, TimeUnit.SECONDS, requireActivity(), mCallbacks);
    }

    private void callback_verificvation(String strCCode, String phone, String strEmail) {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Logger.printDebug("Verification_complete");
                Toast.makeText(getActivity(), "Your mobile number already verified", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
                btnConfirmSignup.setEnabled(true);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Logger.printDebug(e.getMessage());
                System.out.println("enter the exeption otp verfication" + e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Utility.showToastMessage(getContext(), "failed Sending OTP_InvalidRequest");
                    btnConfirmSignup.setEnabled(true);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Utility.showToastMessage(getContext(), "failed Sending OTP_ManyRequest");
                    btnConfirmSignup.setEnabled(true);
                } else {
                    Utility.showToastMessage(getContext(), "verification message Sending failed..");
                    btnConfirmSignup.setEnabled(true);
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Logger.printDebug("code send");
                Logger.printDebug(verificationId);
                verificationID = verificationId;
                if (!resend) {
                    showVerifyOTPDialog(strCCode, phone, strEmail);
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void showVerifyOTPDialog(String strCCode, String phone, String strEmail) {

        dialogBuilder = new Dialog(requireActivity(), R.style.AppTheme);
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogBuilder.setContentView(R.layout.layout_account_verification);
        dialogBuilder.setCancelable(true);
        Objects.requireNonNull(dialogBuilder.getWindow()).setWindowAnimations(R.style.DialogAnimationLR);


        CustomEditText etOTP = (CustomEditText) dialogBuilder.findViewById(R.id.edt_otp);
        CustomTextView btnVerify = (CustomTextView) dialogBuilder.findViewById(R.id.btn_confirmsignupotp);
        ImageView ivCancel = (ImageView) dialogBuilder.findViewById(R.id.imgbtn_back);
        final TextView etResend = (TextView) dialogBuilder.findViewById(R.id.txt_resendotp);

        final TextView txt_mobilenum = (TextView) dialogBuilder.findViewById(R.id.txt_mobilenum);

        txt_mobilenum.setText(strCCode + phone);

        new CountDownTimer(30000, 1000) {

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                etResend.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                etResend.setEnabled(false);
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                etResend.setText("RESEND CODE");
                etResend.setEnabled(true);
                //etResend.setBackgroundColor(Color.rgb(55,103,228));
            }
        }.start();

        dialogBuilder.setCancelable(false);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // boolSignupBtnClick = false;
                btnConfirmSignup.setEnabled(true);
                dialogBuilder.dismiss();
            }
        });

        etResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resend = true;
                new CountDownTimer(30000, 1000) {

                    @SuppressLint({"DefaultLocale", "SetTextI18n"})
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        etResend.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                        etResend.setEnabled(false);
                    }

                    public void onFinish() {
                        etResend.setText("RESEND CODE");
                        etResend.setEnabled(true);
                        //etResend.setBackgroundColor(Color.rgb(55,103,228));
                    }
                }.start();
                sentVerificationCode(strCCode, phone, strEmail);
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("verificationID--" + verificationID);
                System.out.println("etOTP--" + etOTP.getText().toString().trim());
                if (etOTP.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Verification Code", Toast.LENGTH_SHORT).show();
                } else {
                    dialogBuilder.dismiss();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, etOTP.getText().toString().trim());
                    System.out.println("credential--" + credential);
                    verifyOTP(credential);
                }
            }
        });
        hideProgressDialog();
        dialogBuilder.show();
    }

    //firebase verify otp
    private void verifyOTP(PhoneAuthCredential credential) {
        System.out.println("credential----" + credential);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logger.printDebug("signInWithCredential:success");
                            resend = false;
                            CallRegisterDetails(strCountyCode, strMobile, strEmail,strSelectedcity);  //vignesh
                        } else {
                            // Sign in failed, display a message and update the UI
                            btnConfirmSignup.setEnabled(true);
                            Logger.printDebug(Objects.requireNonNull(task.getException()).toString());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Logger.printDebug("Invalid");
                                System.out.println("yayini" + task.getException().getMessage());
                                Utility.showToastMessage(getActivity(), task.getException().getMessage());
                            }
                        }
                    }
                });
    }


    private void CallRegisterDetails(String WithCC, String mobileNumber, String strEmail,String selectedCityName) {

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("first_name", edtFirstName.getText().toString());
        map1.put("last_name", edtLastName.getText().toString());
        map1.put("email", strEmail);
        map1.put("phone_number", mobileNumber);
        map1.put("country_code", WithCC);
        map1.put("password", edtPassword.getText().toString());
        map1.put("device_token", deviceId);
        map1.put("referral_code", edtReferralcode.getText().toString());
        map1.put("provider_type", strProviderType);
        map1.put("company_name", strCompanyName);
        map1.put("city", strSelectedcity);
        Log.i("CallRegisterDetails", "~~~~" + map1);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegisterModel> call2 = apiInterface.doGetRegisterDetails(map1);

        call2.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModel> call, @NonNull Response<RegisterModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Registered Successfully")) {

                        String UserId = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getId());
                        String Firstname = Objects.requireNonNull(response.body()).getRegisterData().getFirstName();
                        String Lastname = Objects.requireNonNull(response.body()).getRegisterData().getLastName();
                        String Email = Objects.requireNonNull(response.body()).getRegisterData().getEmail();
                        String UserProPic = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getAvatar());
                        String Mobile = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getPhoneNumber());
                        String Countrycode = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getCountryCode());

                        /*String Dob = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getBirthDate());
                        String State = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getStateId());
                        String Country = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getCountryId());
                        String Address = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getAddress());
                        String Currency = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getCurrency());
                        String PhoneVerified = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getPhoneVerified());
                        String EmailVerified = String.valueOf(Objects.requireNonNull(response.body()).getRegisterData().getEmailVerified());*/

                        getState.putString(Utils.SP_DRIVERID, UserId);
                        getState.putBoolean(Utils.SP_DRIVER_LOGIN, false);
                        getState.apply();

                        saveInFirebase(UserId, Firstname, Lastname, Email, Countrycode, Mobile, UserProPic,selectedCityName);

                        hideProgressDialog();

                        showProfileUpdateAlertDialog(getActivity(), getResources().getString(R.string.register_successfully));

                    } else {
                        hideProgressDialog();
                        btnConfirmSignup.setEnabled(true);
//                        showSnackbar(response.body().getMessage(), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,response.body().getMessage(), "warning");
                    }
                } else {
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
            }
        });
    }

    private void showProfileUpdateAlertDialog(Activity self, String message) {

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
                (dialog, which) -> {
                    dialog.dismiss();
                    Bundle args = new Bundle();
                    args.putString("key", "register");
                    Utils.switchContent(R.id.frame_launch_container,
                            Utils.FRAGMENT_SIGNIN,
                            requireActivity(), args,
                            Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                }).show();
    }


    private void savesharedpreference(String access_token, String token_type, String exp_time, String userId, String firstname, String lastname, String email, String userProPic, String dob, String state, String country, String address, String currency, String Mobile, String Countrycode) {

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        getState.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        getState.putString(Utils.SP_DRIVERID, userId);
        getState.putString(Utils.SP_DRIVER_FNAME, firstname);
        getState.putString(Utils.SP_DRIVER_LNAME, lastname);
        getState.putString(Utils.SP_DRIVER_EMAIL, email);
        getState.putString(Utils.SP_DRIVER_PRO_PIC, userProPic);
        getState.putString(Utils.SP_DRIVER_DOB, dob);
        getState.putString(Utils.SP_DRIVER_STATE, state);
        getState.putString(Utils.SP_DRIVER_COUNTRY, country);
        getState.putString(Utils.SP_DRIVER_ADDRESS, address);
        getState.putString(Utils.SP_DRIVER_MOBILE, Mobile);
        getState.putString(Utils.SP_DRIVER_COUNTRY_CODE, Countrycode);
        getState.putString(Utils.SP_CURRENCY, currency);
        getState.apply();

        Intent Signin = new Intent(getActivity(), MainActivity.class);
        startActivity(Signin);
        requireActivity().finish();
    }

    private void saveInFirebase(String userId, String firstname, String lastname, String email, String countrycode, String mobile, String userProPic,String selectedCityName) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(Utils.FIREBASE_PROVIDER_FNAME, firstname);
        updates.put(Utils.FIREBASE_PROVIDER_LNAME, lastname);
        updates.put(Utils.FIREBASE_PROVIDER_EMAIL, email);
        updates.put(Utils.FIREBASE_PROVIDER_CC, countrycode);
        updates.put(Utils.FIREBASE_PROVIDER_MOBILE, mobile);
        updates.put(Utils.FIREBASE_PROVIDER_PRO_PIC, userProPic);
        updates.put(Utils.FIREBASE_PROVIDER_CITY_NAME, selectedCityName);
        updates.put("language", "");
        updates.put("currency", "");
        updates.put("wallet", "0");
        updates.put("category", "0");
        updates.put("status", "0");
        updates.put("approved", "0");
        updates.put("pending", "0");
        updates.put("upcoming", "0");
        updates.put("est_distance", "0");
        updates.put("est_duration", "0");
        updates.put("distance", "0");

        ref.setValue(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("DATA SAVED SUCCESSFULLY");
                if (databaseError != null) {
                    System.out.println("DATA SAVED SUCCESSFULLY");
                }
            }
        });

        DatabaseReference refs = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId).child(Utils.FIREBASE_TRIP_HEADER);
        Map<String, Object> updatess = new HashMap<String, Object>();
        updatess.put(Utils.FIREBASE_SERVICE_STATUS, "0");

        refs.setValue(updatess, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("DATA SAVED SUCCESSFULLY");
                if (databaseError != null) {
                    System.out.println("DATA SAVED SUCCESSFULLY");
                }
            }
        });

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

    public void onBackButton(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("Onbackpressed", String.valueOf(event));
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("Onbackpressed~~~", String.valueOf(event));
                    backButtonFunction();
                }
                return true;
            }
        });
    }

    public void backButtonFunction() {
        removeFragment(Utils.FRAGMENT_SIGNUP);
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
