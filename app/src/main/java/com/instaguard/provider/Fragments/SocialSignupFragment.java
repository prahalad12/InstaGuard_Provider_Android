package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;

import static java.lang.System.out;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Adapter.CityListAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.CountryCode.CountryCodeDialog;
import com.instaguard.provider.Helpers.CountryCode.CountryCodePicker;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.CitiesListData;
import com.instaguard.provider.Models.CitiesListModel;
import com.instaguard.provider.Models.SocialLogModel.SocialSignupModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SocialSignupFragment extends Fragment {

    CustomEditText edtFirstName, edtLastName, edtEmail, edtMobile, edtCountryCode;
    ImageView imgbtnBack;
    CustomButton btnConfirmSignup;
    CustomTextView txtAlreadySignin, txtAlreadySignin1;
    CountryCodePicker ccp;

    SharedPreferences.Editor getSpCurrency, getStatePrefs;
    SharedPreferences spCurrency;
    public DatabaseReference driverSearchDatabaseBRef;
    public ValueEventListener driverSearchValueEventListener;

    private String someVarA, someVarB;
    public String strCurrency, strCurrencySymbol, strServiceStatus, strLanguage, strLanguageShortName;


    public static String TAG = "SocialSignupFragment";

    String strCountyCode, strMobile, strEmail, strSelectedCity, OTP_message, OTP_msg, OTP_Status, OTP_mobilenum, WEB_OTP, StrEmail;
    Dialog OTPDialog;

    View view;

    String deviceId;
    String referralCode;


    ImageButton imgbtnClearFirstName, imgbtnClearLastName, imgbtnClearEmail, imgbtnClearPassword, imgbtnClearMobile, imgbtnClearCompany, imgbtnClearReflCode;

    View lytSuccess;
    CustomTextView txtSuccessTitle;
    CustomButton btnSuccessOkay;
    RelativeLayout rlParentSuccess;

    CustomEditText edtReferralCode;


    APIInterface apiInterface;

    CheckBox cbIAgree;
    String privacyPolicyLink;
    String privacyPolicyText;
    String termsConditionsLink;
    String termsConditionsText;
    //Terms&Condition

    CustomTextView txtPrivacyPolicy;
    CustomTextView txtTermsService;

    CustomEditText companyName;
    private AwesomeValidation Validation;

    RadioGroup providerGrp;

    String strProviderType = "individual", strCompanyName = "InstaGuard";

    CityListAdapter adapterCityList;
    RecyclerView rcvCitiesList;
    Dialog dialogCityList;
    CustomEditText searchCity, edtCity;
    List<CitiesListData> citiesArrayList = new ArrayList<>();
    ArrayList<String> citiesnamesArrayList = new ArrayList<>();
    public static SuccessResponseSocialLogin SuccessResponseSocialLogin_;
    Locale locale;
    FrameLayout contentView;

    public interface SuccessResponseSocialLogin {
        void SuccessResponseSocialLogin_(String langname, String code, String country, String key);
    }

    public SocialSignupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    String socialId, fName, lName, type, custId, email;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_signup_social, container, false);

        getStatePrefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        SharedPreferences pref = requireActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = pref.getString(Utils.SP_DEVICE_ID, null);
        Log.i(TAG, "token in singinpage--> " + deviceId);
        if (deviceId == null)
            deviceId = "123";

        getSpCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();
        spCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguage = spCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = spCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtFirstName = view.findViewById(R.id.edt_firstname);
        edtLastName = view.findViewById(R.id.edt_lastname);
        edtEmail = view.findViewById(R.id.edt_email);
        edtCountryCode = view.findViewById(R.id.edt_country_code);
        edtMobile = view.findViewById(R.id.edt_mobile);
        edtReferralCode = view.findViewById(R.id.edt_referralcode);

        cbIAgree = (CheckBox) view.findViewById(R.id.checkbox);
        contentView = (FrameLayout) view.findViewById(R.id.frame_signup_social);

        txtPrivacyPolicy = view.findViewById(R.id.txt_i_agree_ll);
        txtTermsService = view.findViewById(R.id.txt_i_agree_llll);

        ccp = view.findViewById(R.id.ccp);

        imgbtnClearFirstName = (ImageButton) view.findViewById(R.id.imgbtn_clear_first_name);
        imgbtnClearLastName = (ImageButton) view.findViewById(R.id.imgbtn_clear_last_name);
        imgbtnClearEmail = (ImageButton) view.findViewById(R.id.imgbtn_clear_email);
        imgbtnClearPassword = (ImageButton) view.findViewById(R.id.imgbtn_clear_pwd);
        imgbtnClearMobile = (ImageButton) view.findViewById(R.id.imgbtn_clear_mobile);
        imgbtnClearReflCode = (ImageButton) view.findViewById(R.id.imgbtn_clear_referralcode);
        imgbtnClearCompany = (ImageButton) view.findViewById(R.id.imgbtn_clear_company_name);
        edtCity = (CustomEditText) view.findViewById(R.id.edt_city);

        imgbtnBack = (ImageView) view.findViewById(R.id.imgbtn_back);
        btnConfirmSignup = (CustomButton) view.findViewById(R.id.btn_confirmsignup);

        providerGrp = view.findViewById(R.id.provider_type);
        companyName = view.findViewById(R.id.edt_company_name);


        if (getArguments() != null) {
            socialId = getArguments().getString("social_id");
            fName = getArguments().getString("fname");
            lName = getArguments().getString("lname");
            type = getArguments().getString("type");
            custId = getArguments().getString("cus_id");
            email = getArguments().getString("email");

            System.out.println("inside social");
        }

        edtFirstName.setText(fName.replaceAll("_", " "));
        edtLastName.setText(lName.replaceAll("_", " "));
        if (email != null) {
            if (!email.equals("")) {
                edtEmail.setText(email);
                edtEmail.setEnabled(false);
                edtEmail.setFocusable(false);
                edtEmail.setFocusableInTouchMode(false);
                edtEmail.setClickable(false);
            } else {
                edtEmail.setEnabled(true);
                edtEmail.setFocusable(true);
                edtEmail.setFocusableInTouchMode(true);
                edtEmail.setClickable(true);
            }
        } else {
            edtEmail.setEnabled(true);
            edtEmail.setFocusable(true);
            edtEmail.setFocusableInTouchMode(true);
            edtEmail.setClickable(true);
        }
        // validation initalize
        Validation = new AwesomeValidation(BASIC);


        OnClick();

        onBackButton(view);

        GetCitiesList();

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


    private void OnClick() {

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


       /* txtPrivacyPolicy.setOnClickListener(view -> {
            if (privacyPolicyLink != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(privacyPolicyLink));
                    intent.setPackage("com.android.chrome");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // chrome app not install
                }
            } else {
                String urls = Utils.PRIVACY_URL;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urls));
                    intent.setPackage("com.android.chrome");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // chrome app not install
                }
            }
        });

        txtTermsService.setOnClickListener(view -> {
            if (termsConditionsLink != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(termsConditionsLink));
                    intent.setPackage("com.android.chrome");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // chrome app not install
                }
            } else {
                String urls = Utils.TERMS_SERVICE_URL;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urls));
                    intent.setPackage("com.android.chrome");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // chrome app not install
                }
                // Toast.makeText(getApplicationContext(), "Terms of service", Toast.LENGTH_SHORT).show();
            }
        });*/


        txtPrivacyPolicy.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString(Utils.B_SUPPORT_FROM,"register_social");
            Utils.switchContent(R.id.frame_signup_social,
                    Utils.FRAGMENT_SUPPORT,
                    requireActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        });

        txtTermsService.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString(Utils.B_SUPPORT_FROM,"register_social");
            Utils.switchContent(R.id.frame_signup_social,
                    Utils.FRAGMENT_SUPPORT,
                    requireActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        });


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

                hideKeyBoard();
                ValidationForEditText();
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


    //validation controller
    private void ValidationForEditText() {
        strSelectedCity = Objects.requireNonNull(edtCity.getText()).toString();

        Validation.addValidation(getActivity(), R.id.edt_firstname, "[a-zA-Z\\s]+", R.string.register_error_firstname);
        Validation.addValidation(getActivity(), R.id.edt_lastname, "[a-zA-Z\\s]+", R.string.register_error_lastname);
        Validation.addValidation(getActivity(), R.id.edt_mobile, RegexTemplate.TELEPHONE, R.string.register_error_enter_validnum);
        Validation.addValidation(getActivity(), R.id.edt_email, Patterns.EMAIL_ADDRESS, R.string.register_error_email);

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
                btnConfirmSignup.setEnabled(true);
//                showSnackbar(getString(R.string.error_all_fields_required), "Field");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.error_all_fields_required), "Field");
            } else if (strProviderType == null) {
//                showSnackbar(getString(R.string.select_individual_company), "warning");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.select_individual_company), "warning");
                btnConfirmSignup.setEnabled(true);
            }else if  (strSelectedCity.equals("Select Your Town")||strSelectedCity.isEmpty()) {
                edtCity.setError(getString(R.string.select_town));
                btnConfirmSignup.setEnabled(true);
            } else if ((strProviderType.equals("company") && strCompanyName == null) || (strProviderType.equals("company") && strCompanyName.equals(""))) {
//                showSnackbar(getString(R.string.enter_company_name), "warning");
                BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.enter_company_name), "warning");
                btnConfirmSignup.setEnabled(true);
            } else {
                strEmail = edtEmail.getText().toString();
                strCountyCode = edtCountryCode.getText().toString().trim();
                strMobile = edtMobile.getText().toString().trim();
                strEmail = strEmail.replaceAll("%40", "");
                strEmail = strEmail.replaceAll("%20", "");
                CallRegisterDetails(strCountyCode, strMobile);
            }
        } else {
            onBackButton(view);
        }
    }

    private boolean validateSelectItems(AppCompatSpinner view, String key) {

        if (view == null) {
            return true;
        } else if (Objects.equals(view, "null")) {
            return true;
        } else if (view.getSelectedItem() == null) {
            return true;
        } else if (view.getSelectedItem().equals("null")) {
            return true;
        } else return view.getSelectedItem().equals(key);
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
                    System.err.println(e);
                }

            }
            return true;
        }

    }

    private boolean validateCountryCode() {

        if (edtCountryCode.getText().toString().trim().isEmpty()) {
            edtCountryCode.setError(getString(R.string.register_error_enter_country_code));
            return false;
        } else if (edtCountryCode.getText().toString().equals("CC")) {
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


    private void CallRegisterDetails(String strCountyCode, String mobNumber) {

        String strfname = edtFirstName.getText().toString().trim();
        String strlname = edtLastName.getText().toString().trim();
        String strfnameupperString = strfname.substring(0, 1).toUpperCase() + strfname.substring(1);
        String strlnameupperString = strlname.substring(0, 1).toUpperCase() + strlname.substring(1);

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("driver_id", custId);
        map1.put("email", edtEmail.getText().toString());
        map1.put("country_code", strCountyCode);
        map1.put("phone_number", mobNumber);
        map1.put("device_id", deviceId);
        map1.put("name", edtFirstName.getText().toString());
        map1.put("last_name", edtLastName.getText().toString());
        map1.put("referral_code", edtReferralCode.getText().toString().trim());
        map1.put("provider_type", strProviderType);
        map1.put("company_name", strCompanyName);
        map1.put("city", strSelectedCity);

        Log.i("CallRegisterDetails", "~~~~" + map1);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SocialSignupModel> call2 = apiInterface.doSocialLoginUpdate(map1);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<SocialSignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SocialSignupModel> call, @NonNull Response<SocialSignupModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    assert response.body() != null;
                    if (response.body().getMsg().equalsIgnoreCase("Driver Details")) {

                        callCommonFBGOOGLE(type, response);

                        // saveInFirebase(UserId, Firstname, Lastname, Email, Countrycode, Mobile, UserProPic);
                        // removeRememberme();

                        /*Utils.switchContent(R.id.frame_launch_container,
                                Utils.FRAGMENT_SIGNIN,
                                getActivity(), null,
                                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/

                    } else if (response.body().getMsg().equalsIgnoreCase("Email already exists.")) {
                        Toast.makeText(requireActivity(), R.string.register_email_already_exist, Toast.LENGTH_SHORT).show();
                    } else {
                        //showSnackbar(response.body().getMsg(), "warning");
                        Toast.makeText(requireActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SocialSignupModel> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure:~~~ " + t.toString());
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    private void callCommonFBGOOGLE(String social_type, @NonNull Response<SocialSignupModel> response) {

        /*String access_token = Objects.requireNonNull(response.body()).getAccessToken();
        String token_type = Objects.requireNonNull(response.body()).getTokenType();
        String expires_in = String.valueOf(Objects.requireNonNull(response.body()).getExpiresIn());*/

        String UserId = String.valueOf(Objects.requireNonNull(response.body()).getResult().getId());
        String Firstname = Objects.requireNonNull(response.body()).getResult().getFirstName();
        String Lastname = Objects.requireNonNull(response.body()).getResult().getLastName();
        String Email = Objects.requireNonNull(response.body()).getResult().getEmail();
        String UserProPic = String.valueOf(Objects.requireNonNull(response.body()).getResult().getAvatar());

        String Dob = String.valueOf(Objects.requireNonNull(response.body()).getResult().getBirthDate());
        String State = String.valueOf(Objects.requireNonNull(response.body()).getResult().getStateId());
        String Country = String.valueOf(Objects.requireNonNull(response.body()).getResult().getCountryId());
        String Address = String.valueOf(Objects.requireNonNull(response.body()).getResult().getAddress());
        //   String Currency = String.valueOf(Objects.requireNonNull(response.body()).getResult().getCurrency());

        String Mobile = String.valueOf(Objects.requireNonNull(response.body()).getResult().getPhoneNumber());
        String Countrycode = String.valueOf(Objects.requireNonNull(response.body()).getResult().getCountryCode());

        String PhoneVerified = String.valueOf(Objects.requireNonNull(response.body()).getResult().getPhoneVerified());
        String EmailVerified = String.valueOf(Objects.requireNonNull(response.body()).getResult().getEmailVerified());
        String WalletAmount = String.valueOf(Objects.requireNonNull(response.body()).getResult().getWalletBalance());
        String Referal = String.valueOf(Objects.requireNonNull(response.body()).getResult().getReferralCode());
        String InviteCode = String.valueOf(Objects.requireNonNull(response.body()).getResult().getInviteCode());

        String currency = String.valueOf(Objects.requireNonNull(response.body()).getResult().getCurrency());

        String currencysymbol = String.valueOf(Objects.requireNonNull(response.body()).getResult().getCurrency());

        strLanguage = String.valueOf(Objects.requireNonNull(response.body()).getResult().getLanguage());
        strServiceStatus = String.valueOf(Objects.requireNonNull(response.body()).getResult().getServiceIds());

        saveInFirebase(UserId, Firstname, Lastname, Email, Countrycode, Mobile, UserProPic, social_type);

        savesharedpreference("access_token", "token_type", "expires_in", UserId, Firstname, Lastname, Email, UserProPic, Dob, State,
                Country, Address, Mobile, Countrycode, WalletAmount, Referal, InviteCode, currency, currencysymbol);


        hideProgressDialog();
    }

    private void savesharedpreference(String access_token, String token_type, String exp_time, String userId,
                                      String firstname, String lastname, String email, String userProPic, String dob,
                                      String state, String country, String address, String Mobile, String Countrycode,
                                      String WalletAmount, String referal, String inviteCode, String currency
            , String currencysymbol) {

        getStatePrefs.putString(Utils.SP_DRIVER_ACCESSTOKEN, "access_token");
        getStatePrefs.putString(Utils.SP_DRIVER_TOKENTYPE, "token_type");
        getStatePrefs.putString(Utils.SP_DRIVER_TOKENEXPTIME, "exp_time");
        getStatePrefs.putString(Utils.SP_DRIVERID, userId);
        getStatePrefs.putString(Utils.SP_DRIVER_FNAME, firstname);
        getStatePrefs.putString(Utils.SP_DRIVER_LNAME, lastname);
        getStatePrefs.putString(Utils.SP_DRIVER_EMAIL, email);
        getStatePrefs.putString(Utils.SP_DRIVER_PRO_PIC, userProPic);
        getStatePrefs.putString(Utils.SP_DRIVER_DOB, dob);
        getStatePrefs.putString(Utils.SP_DRIVER_STATE, state);
        getStatePrefs.putString(Utils.SP_DRIVER_COUNTRY, country);
        getStatePrefs.putString(Utils.SP_DRIVER_ADDRESS, address);
        getStatePrefs.putString(Utils.SP_DRIVER_MOBILE, Mobile);
        getStatePrefs.putString(Utils.SP_DRIVER_COUNTRY_CODE, Countrycode);
        // getState.putString(Utils.SP_CURRENCY,currency);
        getStatePrefs.putString(Utils.SP_DRIVER_WALLET_AMT, WalletAmount);
        getStatePrefs.putString(Utils.SP_DRIVER_REFERAL_CODE, referal);
        getStatePrefs.putString(Utils.SP_DRIVER_INVITE_CODE, inviteCode);
        getStatePrefs.apply();

        // getState.putString(Utils.SP_CHECKED_REMEMBERME, "false");

        /* if(boolRememberChecked){
            getStateRemember.putString(Utils.SP_DRIVER_EMAIL_REMEMBERME,edtEmail.getText().toString());
            getStateRemember.putString(Utils.SP_DRIVER_PASS_REMEMBERME,edtPassword.getText().toString());
            getStateRemember.putString(Utils.SP_CHECKED_REMEMBERME, String.valueOf(boolRememberChecked));
            getStateRemember.apply();
        }*/

        if (currency.equals("")) {
            strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
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
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("MissingPermission~~~", "currentLocationClick~~MissingPermission");
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    } else {
                        callLangauge(strLanguage);
                    }
                } else {
                    callLangauge(strLanguage);
                }
            } else {

                getStatePrefs.putBoolean(Utils.SP_DRIVER_LOGIN, false);
                getStatePrefs.apply();

                if (strLanguage.equals("")) {
                    getSpCurrency.putString(Utils.SP_LANGUAGE, "English");
                    getSpCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, "en");
                    getSpCurrency.putString(Utils.SP_LANGUAGE_COUNTRY, "EN");
                    getSpCurrency.apply();
                }

                Bundle args = new Bundle();
                args.putString("key", "login");
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_SIGNIN,
                        requireActivity(), args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                /*Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_MANAGE_SERVICE,
                        getActivity(), args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/

            }
        } else {
            Toast.makeText(getActivity(), "Services status empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveInFirebase(String userId, String firstname, String lastname, String email,
                                String countrycode, String mobile, String userProPic, String social_type) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(Utils.FIREBASE_PROVIDER_FNAME, firstname);
        updates.put(Utils.FIREBASE_PROVIDER_LNAME, lastname);
        updates.put(Utils.FIREBASE_PROVIDER_EMAIL, email);
        updates.put(Utils.FIREBASE_PROVIDER_CC, countrycode);
        updates.put(Utils.FIREBASE_PROVIDER_MOBILE, mobile);
        updates.put(Utils.FIREBASE_PROVIDER_PRO_PIC, userProPic);
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

        updates.put("social_type", social_type);

        ref.setValue(updates, (databaseError, databaseReference) -> {
            System.out.println("DATA SAVED SUCCESSFULLY");
            if (databaseError != null) {
                System.out.println("DATA SAVED SUCCESSFULLY");
            }
        });

        DatabaseReference refs = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER)
                .child(userId).child(Utils.FIREBASE_TRIP_HEADER);
        Map<String, Object> updatess = new HashMap<String, Object>();
        updatess.put(Utils.FIREBASE_SERVICE_STATUS, "0");

        refs.setValue(updatess, (databaseError, databaseReference) -> {
            System.out.println("DATA SAVED SUCCESSFULLY");
            if (databaseError != null) {
                System.out.println("DATA SAVED SUCCESSFULLY");
            }
        });
    }


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


    public void backButtonFunction() {
        removeFragment(Utils.FRAGMENT_SOCIALSIGNUP);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: ~~~~~~~~~~~~" + requestCode);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            callLangauge(strLanguage);
        }
    }

    private void callLangauge(String language) {

        getStatePrefs.putBoolean(Utils.SP_DRIVER_LOGIN, true);
        getStatePrefs.apply();

        out.println("language is "+ language);
        if (language.equals("")) {
            if (BuildConfig.DEBUG) Log.e(TAG, "savesharedpreference: ");
            SuccessResponseSocialLogin_.SuccessResponseSocialLogin_(language, "en", "US", "login");
            out.println("entered in language is empty");
        } else {
            // Toast.makeText(getActivity(), "Check Languages", Toast.LENGTH_SHORT).show();
            out.println("entered in language is not empty " + language);
            switch (language) {
                case "English":
                default:
                    SuccessResponseSocialLogin_.SuccessResponseSocialLogin_(language, "en", "US", "login");
                    break;
                case "Sinhala":
                    SuccessResponseSocialLogin_.SuccessResponseSocialLogin_(language, "si", "LK", "login");
                    break;
            }
        }
    }

    public void searchCurrencyPrice(String Currency) {
        Log.i(TAG, "searchCurrencyPrice: in homeactivity-->" + Currency);
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
