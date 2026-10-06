package com.instaguard.provider.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Adapter.CurrencyAdapter;
import com.instaguard.provider.Adapter.LanguageAdapter;
import com.instaguard.provider.BuildConfig;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Fragments.LoginFragment;
import com.instaguard.provider.Fragments.SocialSignupFragment;
import com.instaguard.provider.Helpers.Localizationactivity.ui.LocalizationActivity;
import com.instaguard.provider.Helpers.NetworkManager.Tovuti;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.SocialNetworks.facebookSignIn.FacebookHelper;
import com.instaguard.provider.Helpers.SocialNetworks.googleAuthSignin.GoogleSignInHelper;
import com.instaguard.provider.Helpers.SocialNetworks.googleSignIn.GooglePlusSignInHelper;
//import com.instaguard.provider.Helpers.SocialNetworks.twitterSignIn.TwitterHelper;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AppSettingCurrencyModel;
import com.instaguard.provider.Models.AppSettingLanguageModel;
import com.instaguard.provider.Models.AppSettingWebModel;
import com.instaguard.provider.Models.LinkedInUserDetailsModel;
import com.instaguard.provider.R;
import com.instaguard.provider.Models.SocialLogModel.SocialLoginModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

@SuppressLint("Registered")
public class HomeActivity extends LocalizationActivity implements GoogleApiClient.OnConnectionFailedListener,
        LoginFragment.SuccessResponseLogin, SocialSignupFragment.SuccessResponseSocialLogin {

    boolean doubleBackToExitPressedOnce = false;
    CustomButton btnSignin, btnSignup, btnLanguage, btnCurrency;
    private Tovuti tovuti;
    RelativeLayout rlLowInternet;
    String TAG = "HomeActivity";
    SharedPreferences.Editor getSpCurrency, getState;
    SharedPreferences spCurrency, sprefsGlobalDeviceId;

    public String deviceId;


    private FacebookHelper mFbHelper;
    private GooglePlusSignInHelper mGHelper;
    private GoogleSignInHelper mGAuthHelper;
//    private TwitterHelper mTwitterHelper;
    ImageButton ivFacebook, ivLinkedIn, ivTwitter, ivGoogle;

    Dialog dialogCurrency, dialogLanguage;
    CustomTextView txtUSD, txtCHF, txtEUR, txtNGS, txtEnglish, txtSwahili, txtFrench, txtSpanish;
    RecyclerView rcvLanguage, rcvCurrency;
    LinearLayout llStaticLanguage, llStaticCurrency;
    CurrencyAdapter adapterCurrency;
    LanguageAdapter adapterLanguage;

    private List<AppSettingLanguageModel> ListModel;
    private List<AppSettingCurrencyModel> ListModelCurrency;

    public DatabaseReference driverSearchDatabaseBRef;
    public ValueEventListener driverSearchValueEventListener;

    private String someVarA, someVarB;
    public String strCurrency, strCurrencySymbol, strLanguage, strLanguageShortName, strServiceStatus;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_L = 199;


    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private GoogleApiClient mGoogleApiClient;
    private static final int OUR_REQUEST_CODE = 49404;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    View viewlocationRequest, viewParentLocReq;
    CustomButton requestAccept;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getState = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getSpCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();
        spCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguage = spCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = spCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);

        System.out.println("Homeview->" + strCurrency);

        sprefsGlobalDeviceId = getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = sprefsGlobalDeviceId.getString(Utils.SP_DEVICE_ID, null);
        if (deviceId == null)
            deviceId = "123";

        LoginFragment.SuccessResponseLogin = this;

      /*  tovuti = Tovuti.from(this)
                .monitor(new Monitor.ConnectivityListener() {
                    @Override
                    public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {

                        String type, speed;
                        if (isConnected) {
                            switch (connectionType) {
                                case -1:
                                    type = "Any";
                                    break;
                                case ConnectivityManager.TYPE_WIFI:
                                    type = "Wifi";
                                    break;
                                case ConnectivityManager.TYPE_MOBILE:
                                    type = "Mobile";
                                    break;
                                default:
                                    type = "Unknown";
                                    break;
                            }

                            if (isFast)
                                speed = "Fast";
                            else if (type.equals("Unknown"))
                                speed = "N/A";
                            else
                                speed = "Slow";
                        } else {
                            type = "None";
                            speed = "N/A";
                        }
                        if (type.equals("None")) {
                            Utils.rlLayoutAnimation(getApplicationContext(),rlLowInternet, Utils.AnimationType.FADE_IN);
                            Log.i(TAG, "onConnectivityChanged:none on internet connnection ");
                        }else{
                            Utils.rlLayoutAnimation(getApplicationContext(),rlLowInternet, Utils.AnimationType.FADE_OUT);
                            Log.i(TAG, "onConnectivityChanged:else on internet connnection ");
                        }
                    }
                });*/

        lytProgressBar = findViewById(R.id.lyt_progressBar_view);


        btnSignin = (CustomButton) findViewById(R.id.btn_signin);
        btnSignup = (CustomButton) findViewById(R.id.btn_signup);
        btnLanguage = (CustomButton) findViewById(R.id.btn_language);
        btnCurrency = (CustomButton) findViewById(R.id.btn_currency);
        //rlLowInternet = (RelativeLayout)findViewById(R.id.rl_lowinternet); vign27

        if (strCurrency != null) {
            btnCurrency.setText(strCurrency);
        }

        if (strLanguage != null) {
            btnLanguage.setText(strLanguage);
        }

        /*Dialog Currency charge*/
        dialogCurrency = new Dialog(HomeActivity.this);
        dialogCurrency.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogCurrency.setContentView(R.layout.dialog_static_currency);
        Objects.requireNonNull(dialogCurrency.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCurrency.setCancelable(true);
        dialogCurrency.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCurrency.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        /*Dialog Currency charge*/
        dialogLanguage = new Dialog(HomeActivity.this);
        dialogLanguage.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogLanguage.setContentView(R.layout.dialog_static_language);
        Objects.requireNonNull(dialogLanguage.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCurrency.setCancelable(true);
        dialogCurrency.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
        lps.copyFrom(dialogLanguage.getWindow().getAttributes());
        lps.width = WindowManager.LayoutParams.MATCH_PARENT;
        lps.height = WindowManager.LayoutParams.WRAP_CONTENT;

        rcvLanguage = (RecyclerView) dialogLanguage.findViewById(R.id.rcv_language);
        llStaticLanguage = (LinearLayout) dialogLanguage.findViewById(R.id.ll_static_language);

        rcvCurrency = (RecyclerView) dialogCurrency.findViewById(R.id.rcv_currency);
        llStaticCurrency = (LinearLayout) dialogCurrency.findViewById(R.id.ll_static_currency);

        txtUSD = (CustomTextView) dialogCurrency.findViewById(R.id.txt_usd);
        txtCHF = (CustomTextView) dialogCurrency.findViewById(R.id.txt_chf);
        txtEUR = (CustomTextView) dialogCurrency.findViewById(R.id.txt_eur);
        txtNGS = (CustomTextView) dialogCurrency.findViewById(R.id.txt_ngs);

        txtEnglish = (CustomTextView) dialogLanguage.findViewById(R.id.txt_english);
        txtSwahili = (CustomTextView) dialogLanguage.findViewById(R.id.txt_swahili);
        txtFrench = (CustomTextView) dialogLanguage.findViewById(R.id.txt_french);
        txtSpanish = (CustomTextView) dialogLanguage.findViewById(R.id.txt_spanish);


        rcvLanguage.addOnItemTouchListener(
                new RecyclerTouchListener(HomeActivity.this, rcvLanguage,
                        new RecyclerTouchListener.OnTouchActionListener() {
                            @SuppressLint({"LongLogTag", "ObsoleteSdkInt"})
                            @Override
                            public void onClick(View view, int position) {
                                Log.i("rvCategory", "onClick: rvCategory-> ");
                                if (position != -1) {
                                  /*  listFeed  = emergencyModelArray.get(position);
                                    callDeleteEmergencyDetails(listFeed.getContact_id());*/
                                    String langId = String.valueOf(ListModel.get(position).getId());
                                    //  String langName = String.valueOf(ListModel.get(position).getLanguageName());
                                    someVarA = String.valueOf(ListModel.get(position).getLanguageName());
                                    Log.e("Language~~Onclick~~", langId + "~~" + someVarA);
                                    dialogLanguage.dismiss();
                                    btnLanguage.setText(someVarA);
                                   /* getSpCurrency.putString(Utils.SP_LANGUAGE,someVarA);
                                    getSpCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME,getLanguage(someVarA));
                                    getSpCurrency.apply();*/

                                    switch (someVarA) {
                                        case "English":
                                            SuccessResponseLogin(someVarA, "en", "US", "normal");
                                            break;
                                        case "Sinhala":
                                            // onSuccessEditProfile(strlangName, "si", "LK", "normal");
                                            //case "Arabic":
                                            SuccessResponseLogin(someVarA, "si", "LK", "normal");
                                            break;

                                        default:
                                            SuccessResponseLogin(someVarA, "en", "US", "normal");
                                            break;
                                    }
                                }

                            }

                            @Override
                            public void onRightSwipe(View view, int position) {
                            }

                            @Override
                            public void onLeftSwipe(View view, int position) {
                            }
                        }));
        rcvCurrency.addOnItemTouchListener(
                new RecyclerTouchListener(HomeActivity.this, rcvCurrency,
                        new RecyclerTouchListener.OnTouchActionListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(View view, int position) {
                                Log.i("rvCategory", "onClick: rvCategory-> ");
                                if (position != -1) {
                                  /*  listFeed  = emergencyModelArray.get(position);
                                    callDeleteEmergencyDetails(listFeed.getContact_id());*/
                                    String curId = String.valueOf(ListModelCurrency.get(position).getId());
                                    String curName = String.valueOf(ListModelCurrency.get(position).getCurrency());
                                    // String curShortName = String.valueOf(ListModelCurrency.get(position).getShortName());
                                    someVarB = String.valueOf(ListModelCurrency.get(position).getShortName());
                                    String curSymbol = String.valueOf(ListModelCurrency.get(position).getSymbol());
                                    Log.e("Language~~Onclick~~", curId + "~~" + curName + "~" + someVarB + "~~" + curSymbol);
                                    dialogCurrency.dismiss();
                                    btnCurrency.setText(someVarB);

                                    getSpCurrency.putString(Utils.SP_CURRENCY, someVarB);
                                    getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, curSymbol);
                                    getSpCurrency.apply();
                                }

                            }

                            @Override
                            public void onRightSwipe(View view, int position) {
                            }

                            @Override
                            public void onLeftSwipe(View view, int position) {
                            }
                        }));

        viewlocationRequest = findViewById(R.id.location_request);
        viewParentLocReq = findViewById(R.id.rl_parent_loc_req);
        requestAccept = findViewById(R.id.loc_req_on);

        OnClick();

        Permissionss();


        getOfflineCurrLangInfo();

        if (isOnline()) {
            callAppSettings();
        }

        //twitter initialization
       /* mTwitterHelper = new TwitterHelper(R.string.twitter_api_key,
                R.string.twitter_secrate_key,
                HomeActivity.this,
                HomeActivity.this);*/

        ivTwitter = findViewById(R.id.btn_twitter_);
        ivLinkedIn = findViewById(R.id.btn_linked_in_);

        ImageButton signInButton = (ImageButton) findViewById(R.id.btn_google_);
        //CustomButton signInButton = (CustomButton) findViewById(R.id.btn_google);
        //SignInButton signInButton_ = (SignInButton) findViewById(R.id.iv_google);
        //signInButton.setStyle(SignInButton.SIZE_ICON_ONLY, SignInButton.SIZE_ICON_ONLY);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton_.setSize(SignInButton.SIZE_WIDE);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.loginButton);
        ImageButton imgFbloginButton = findViewById(R.id.btn_facebook_);

        printKeyHash();

        String serverClientId;

        if (Utils.BASE_CODE.equals("_Demo")) {
            serverClientId = Utils.GOOGLE_CLIENT_ID_DEMO;
        } else if (Utils.BASE_CODE.equals("_Live")) {
            serverClientId = Utils.GOOGLE_CLIENT_ID_LIVE;
        } else {
            serverClientId = Utils.GOOGLE_CLIENT_ID_DEMO;
        }

        if (com.facebook.BuildConfig.DEBUG) {
            System.out.println("serverId==>>>" + serverClientId);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(this /* Context */)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Callback registration
        loginButton.setPermissions(Collections.singletonList(EMAIL));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                //AccessToken accessToken = AccessToken.getCurrentAccessToken();
                String accessToken = loginResult.getAccessToken().getToken();
                saveAccessToken(accessToken);

                if (Utils.BASE_CODE.equals("_Demo")) {
                    System.out.println("successs simple name==>" + loginResult.getClass().getSimpleName());
                    System.out.println("successs name==>" + loginResult.getClass().getName());
                    System.out.println("successs userid==>" + loginResult.getAccessToken().getUserId());
                    System.out.println("successs accessToken==>" + accessToken);
                }

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (jsonObject, response) -> {
                            // Getting FB User Data
                            if (jsonObject != null) {
                                Bundle facebookData = getFacebookData(jsonObject);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(), "login cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull FacebookException exception) {
                // App code
                System.out.println("error==>" + exception.toString());
                Toast.makeText(getApplicationContext(), "login error", Toast.LENGTH_SHORT).show();
            }
        });

        imgFbloginButton.setOnClickListener(view -> loginButton.performClick());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, OUR_REQUEST_CODE);
            }
        });

        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (isLoggedIn()) {
                    // Show the Activity with the logged in user
                    Log.d("LoggedIn?: ", "YES");
                } else {*/
                // Show the Home Activity
                //Log.d("LoggedIn?: ", "NO");
                showProgressDialog_();
                getRequestToken();
                /*}*/
            }
        });

        String state = "linkedin" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
       // String linkedinAuthURLFull = Utils.AUTHURL + "?response_type=code&client_id=" + getResources().getString(R.string.linked_in_api_key) + "&scope=" + Utils.SCOPE + "&state=" + state + "&redirect_uri=" + Utils.REDIRECT_URI;
        String linkedinAuthURLFull;
        String REDIRECT_URI;
        if (com.instaguard.provider.BuildConfig.DEBUG) {
            REDIRECT_URI = Utils.REDIRECT_URI;
        } else {
            REDIRECT_URI = Utils.REDIRECT_URI_LIVE;
        }
        linkedinAuthURLFull = Utils.AUTHURL + "?response_type=code&client_id=" + getResources().getString(R.string.linked_in_api_key)
                + "&scope=" +
                Utils.SCOPE + "&state=" + state + "&redirect_uri=" + REDIRECT_URI;
        ivLinkedIn.setOnClickListener(v -> setupLinkedinWebviewDialog(linkedinAuthURLFull));


    }


    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            //  PackageInfo info = getPackageManager().getPackageInfo("com.roadRescuers.user", PackageManager.GET_SIGNATURES);
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("KeyHash:--", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("Keyhash ==>>" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.SP_LANGUAGE, someVarA);
        outState.putString(Utils.SP_CURRENCY, someVarB);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        someVarA = savedInstanceState.getString(Utils.SP_LANGUAGE);
        someVarB = savedInstanceState.getString(Utils.SP_CURRENCY);

        spCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        someVarA = spCurrency.getString(Utils.SP_LANGUAGE, null);
        someVarB = spCurrency.getString(Utils.SP_CURRENCY, null);

        if (someVarA != null) {
            if (someVarA.equalsIgnoreCase(" ")) {
                btnLanguage.setText("eng");
            } else {
                btnLanguage.setText(someVarA);
            }
        } else {
            btnLanguage.setText("eng");
        }

        if (someVarB != null) {
            if (someVarB.equalsIgnoreCase(" ")) {
                btnCurrency.setText("USD");
            } else {
                btnCurrency.setText(someVarB);
            }
        } else {
            btnCurrency.setText("USD");
        }

    }

    public void printHashKey(Context pContext) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    private void OnClick() {

        viewParentLocReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY, null);
                System.out.println("HomeActivitys-->" + strCurrency);
                if (strCurrencySymbol == null) {
                    getSpCurrency.putString(Utils.SP_CURRENCY, "USD");
                    getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "$");
                    getSpCurrency.apply();
                }
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_SIGNIN,
                        HomeActivity.this, null,
                        Utils.AnimationType.FADE_IN);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY, null);
                if (strCurrencySymbol == null) {
                    getSpCurrency.putString(Utils.SP_CURRENCY, "USD");
                    getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "$");
                    getSpCurrency.apply();
                }
                Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_SIGNUP,
                        HomeActivity.this, null,
                        Utils.AnimationType.FADE_IN);
            }
        });

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this, "Testing", Toast.LENGTH_SHORT).show();
                dialogLanguage.show();
            }
        });

        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(HomeActivity.this, "Testing", Toast.LENGTH_SHORT).show();
                dialogCurrency.show();
            }
        });


        txtUSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCurrency.dismiss();
                btnCurrency.setText("USD");
                getSpCurrency.putString(Utils.SP_CURRENCY, "USD");
                getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "$");
                getSpCurrency.apply();

            }
        });

        txtCHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCurrency.dismiss();
                btnCurrency.setText("CHF");
                getSpCurrency.putString(Utils.SP_CURRENCY, "chf");
                getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "CNF ");
                getSpCurrency.apply();
            }
        });

        txtEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCurrency.dismiss();
                btnCurrency.setText("EUR");
                getSpCurrency.putString(Utils.SP_CURRENCY, "eur");
                getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "£ ");
                getSpCurrency.apply();
            }
        });

        txtNGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCurrency.dismiss();
                btnCurrency.setText("NGN");
                getSpCurrency.putString(Utils.SP_CURRENCY, "ngn");
                getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL, "₦ ");
                getSpCurrency.apply();
            }
        });


        txtEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLanguage.dismiss();
                btnLanguage.setText("English");
                getSpCurrency.putString(Utils.SP_LANGUAGE, "ENGLISH");
                getSpCurrency.apply();

            }
        });

        txtSwahili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLanguage.dismiss();
                btnLanguage.setText("Swahili");
                getSpCurrency.putString(Utils.SP_LANGUAGE, "SWAHILI");
                getSpCurrency.apply();
            }
        });

        txtFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLanguage.dismiss();
                btnLanguage.setText("French");
                getSpCurrency.putString(Utils.SP_LANGUAGE, "FRENCH");
                getSpCurrency.apply();
            }
        });

        txtSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLanguage.dismiss();
                btnLanguage.setText("Spanish");
                getSpCurrency.putString(Utils.SP_LANGUAGE, "SPANISH");
                getSpCurrency.apply();
            }
        });


        /* ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCurrencyPrice(btnCurrency.getText().toString());
                getSpCurrency.putString(Utils.SP_CURRENCY,btnCurrency.getText().toString());
                getSpCurrency.putString(Utils.SP_LANGUAGE,btnLanguage.getText().toString());
                getSpCurrency.putString(Utils.SP_CURRENCY_SYMBOL,getCurrencySymbol(btnCurrency.getText().toString()));
                getSpCurrency.apply();
                mTwitterHelper.performSignIn();
            }
        });*/
    }


    public boolean isOnline() {
        boolean connection = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        if ((activeNetwork != null) && activeNetwork.isConnected()) {
            connection = true;
        }
        Log.i(TAG, "isOnline: " + connection);
        return connection;
    }


    private void getOfflineCurrLangInfo() {
        ListModel = new ArrayList<AppSettingLanguageModel>();
        ListModelCurrency = new ArrayList<AppSettingCurrencyModel>();

        String[] strCurrId = new String[]{"1", "2", "3", "4", "5"};
        String[] strShortName = new String[]{"BHD", "UGX", "CHF", "EUR", "NGN"};
        String[] strSymbol = new String[]{"BD", "UGX", "CHF", "€", "₦"};
        String[] strCurrency = new String[]{"Dollar", "Shilling", "France Currency", "Europe", "Nigerian Currency"};
        String[] strConverionsRatio = new String[]{"10", "Shilling", "France Currency", "Europe", "Nigerian Currency"};

        String[] strLangId = new String[]{"1", "2", "3", "4"};
        String[] strLangName = new String[]{"English", "Swahili", "French Currency", "Spanish"};

        for (int i = 0; i < strLangName.length; i++) {
            AppSettingLanguageModel LangModel = new AppSettingLanguageModel();

            LangModel.setId(Integer.valueOf(strLangId[i]));
            LangModel.setLanguageName(strLangName[i]);

            /*if (i == 0)
                categoryModel.setIsSelected(true);
            else
                categoryModel.setIsSelected(false);*/

            ListModel.add(LangModel);

        }

        for (int i = 0; i < strShortName.length; i++) {
            AppSettingCurrencyModel curcyModel = new AppSettingCurrencyModel();

            curcyModel.setId(Integer.valueOf(strCurrId[i]));
            curcyModel.setShortName(strShortName[i]);
            curcyModel.setCurrency(strCurrency[i]);
            curcyModel.setSymbol(strSymbol[i]);

            /*if (i == 0)
                categoryModel.setIsSelected(true);
            else
                categoryModel.setIsSelected(false);*/

            ListModelCurrency.add(curcyModel);

        }


    }

    private void callAppSettings() {
        showProgressDialog(HomeActivity.this);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AppSettingWebModel> call2 = apiInterface.doGetAppSettings();
        call2.enqueue(new Callback<AppSettingWebModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<AppSettingWebModel> call, @NonNull Response<AppSettingWebModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (response.body().getMessage().equals("App settings listed successfully.")) {
                        hideProgressDialog();

                        String STRIPE_PUB_KEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentPublisherKey();
                        String STRIPE_SECRET_KEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentSecretKey();
                        Log.e(TAG, "onResponse: StripeKeys Admin" + STRIPE_PUB_KEY + "~~" + STRIPE_SECRET_KEY);
                        getState.putString(Utils.SP_COMMON_STRIPE_PUBLISH_KEY, STRIPE_PUB_KEY);
                        getState.putString(Utils.SP_COMMON_STRIPE_SECRET_KEY, STRIPE_SECRET_KEY);
                        getState.apply();

                        List<AppSettingCurrencyModel> currencyTemplates = Objects.requireNonNull(response.body()).getCurrency();

                        if (currencyTemplates.size() > 0) {
                            ListModelCurrency = currencyTemplates;
                            rcvCurrency.setVisibility(View.VISIBLE);
                            llStaticCurrency.setVisibility(View.GONE);
                            adapterCurrency = new CurrencyAdapter(HomeActivity.this, currencyTemplates);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                            rcvCurrency.setLayoutManager(horizontalLayoutManager);
                            rcvCurrency.setAdapter(adapterCurrency);
                            adapterCurrency.notifyDataSetChanged();
                        } else {
                            rcvCurrency.setVisibility(View.GONE);
                            llStaticCurrency.setVisibility(View.VISIBLE);
                        }

                        List<AppSettingLanguageModel> languageTemplates = Objects.requireNonNull(response.body()).getLanguage();
                        if (languageTemplates.size() > 0) {
                            ListModel = languageTemplates;
                            rcvLanguage.setVisibility(View.VISIBLE);
                            llStaticLanguage.setVisibility(View.GONE);
                            adapterLanguage = new LanguageAdapter(HomeActivity.this, languageTemplates);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                            rcvLanguage.setLayoutManager(horizontalLayoutManager);
                            rcvLanguage.setAdapter(adapterLanguage);
                            adapterLanguage.notifyDataSetChanged();
                        } else {
                            rcvLanguage.setVisibility(View.GONE);
                            llStaticLanguage.setVisibility(View.VISIBLE);
                        }

                    } else {
                        hideProgressDialog();
                    }
                } else {
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppSettingWebModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
                Log.e(TAG, "onFailure: ~~~~~~~~~~~" + t.toString());
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.CURRENT_TAG = null;
        /*if (tovuti != null){
            tovuti.stop();
        }*/
//        mGHelper.disconnectApiClient();
    }

    private void Permissionss() {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
//            int hasSMSPermission = checkSelfPermission(android.Manifest.permission.SEND_SMS);
//            int hasSMSPermission1 = checkSelfPermission(android.Manifest.permission.READ_SMS);
//            int hasSMSPermission2 = checkSelfPermission(android.Manifest.permission.RECEIVE_SMS);
            int hasAccessLocation = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasGetAccounts = checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS);
            int hasInternet = checkSelfPermission(android.Manifest.permission.INTERNET);
            int hasAccessNetwork = checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int hasAccounts = checkSelfPermission(android.Manifest.permission.ACCOUNT_MANAGER);
            int hasCamera = checkSelfPermission(android.Manifest.permission.CAMERA);
            int hasVibrate = checkSelfPermission(android.Manifest.permission.VIBRATE);
            int hasReadContacts = checkSelfPermission(android.Manifest.permission.READ_CONTACTS);
            int hasWriteContacts = checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS);
            int hasReadStorage = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWriteStorage = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasWakeLock = checkSelfPermission(android.Manifest.permission.WAKE_LOCK);
            int haswritecalendar = checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR);
            int hasChangeNetwork = checkSelfPermission(android.Manifest.permission.CHANGE_NETWORK_STATE);
            int hasPhoneState = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            int hasvibrate = checkSelfPermission(android.Manifest.permission.VIBRATE);
            List<String> permissions = new ArrayList<String>();
            if (hasReadContacts != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_CONTACTS);
            }
            if (hasReadStorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
          /*  if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
         *//*   if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.SEND_SMS);
            }
            if (hasSMSPermission1 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_SMS);
            }
            if (hasSMSPermission2 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.RECEIVE_SMS);
            }*//*
            if (hasAccessLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }*/
            if (hasGetAccounts != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.GET_ACCOUNTS);
            }
            if (hasInternet != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.INTERNET);
            }
            if (hasAccessNetwork != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (hasAccounts != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCOUNT_MANAGER);
            }
            if (hasChangeNetwork != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.CHANGE_NETWORK_STATE);
            }
            if (hasWakeLock != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.WAKE_LOCK);
            }
            if (hasWriteStorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (hasPhoneState != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_PHONE_STATE);
            }
            if (hasvibrate != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.VIBRATE);
            }
            if (hasCamera != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.CAMERA);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
                checkPermissionforlocation();
            }
        }
    }


    public void checkPermissionforlocation() {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            int hasLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasAccessLocation = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (hasAccessLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!permissions.isEmpty()) {
                viewlocationRequest.setVisibility(View.VISIBLE);

                requestAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewlocationRequest.setVisibility(View.GONE);
                        ActivityCompat.requestPermissions((Activity) HomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                        requestPermissions(permissions.toArray(new String[0]), 1);
                    }
                });

            }
        }
    }

    public String getCurrencySymbol(String lang) {
        switch (lang) {
            case "BHD":
                return "BD";
            case "USD":
                return "$";
            case "EUR":
                return "€ ";
            case "CHF":
                return "CNF ";
            case "NGN":
                return "₦ ";
            default:
                return "$";
        }
    }

    public String getLanguage(String lang) {
        Log.e(TAG, "getLanguage: lang~~" + lang);
        switch (lang) {
            case "English":
                return "en";
            case "Swahili":
                return "sw";
            case "French":
                return "fr";
            case "Spanish":
                return "es";
            default:
                return "en";
        }
    }


    private void checkSocialId(String social_id, String social_type, String fname, String lname, String email) {

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("social_id", social_id);
        map1.put("social_type", social_type);
        map1.put("device_id", deviceId);
        showProgressDialog(HomeActivity.this);
        Log.i("checkSocialId", "~~~~" + map1);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SocialLoginModel> call2 = apiInterface.doGetSocialLoginDetails(map1);

        call2.enqueue(new Callback<SocialLoginModel>() {
            @Override
            public void onResponse(@NonNull Call<SocialLoginModel> call, @NonNull Response<SocialLoginModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    assert response.body() != null;
                    if (response.body().getMsg().equals("New Registration")) {

                        hideProgressDialog();

                        Bundle args = new Bundle();
                        args.putString("social_id", social_id);
                        args.putString("type", social_type);
                        args.putString("fname", fname);
                        args.putString("lname", lname);
                        args.putString("email", email);
                        args.putString("cus_id", String.valueOf(response.body().getSocialLoginDataModel().getId()));
                        // MainActivity.frameClickable(true);
                        Utils.switchContent(R.id.frame_launch_container,
                                Utils.FRAGMENT_SOCIALSIGNUP,
                                HomeActivity.this, args,
                                Utils.AnimationType.FADE_IN);

                    } else if (response.body().getMsg().equalsIgnoreCase("Driver login successful.")) {

                        hideProgressDialog();

                        System.out.println("email===>>>" + response.body().getSocialLoginDataModel().getEmail());
                        if (response.body().getSocialLoginDataModel().getEmail() == null) {
                            if (response.body().getSocialLoginDataModel().getEmail().equals("")) {
                                Bundle args = new Bundle();
                                args.putString("social_id", social_id);
                                args.putString("type", social_type);
                                args.putString("fname", fname);
                                args.putString("lname", lname);
                                args.putString("email", email);
                                args.putString("cus_id", String.valueOf(response.body().getSocialLoginDataModel().getId()));
                                Utils.CURRENT_TAG = null;
                                Utils.switchContent(R.id.frame_launch_container,
                                        Utils.FRAGMENT_SOCIALSIGNUP,
                                        HomeActivity.this, args,
                                        Utils.AnimationType.FADE_IN);
                            } else {
                                showProgressDialog(HomeActivity.this);
                                callCommonFBGOOGLE(social_type, response);
                            }
                        } else {
                            if (response.body().getSocialLoginDataModel().getEmail().equals("")) {
                                Bundle args = new Bundle();
                                args.putString("social_id", social_id);
                                args.putString("type", social_type);
                                args.putString("fname", fname);
                                args.putString("lname", lname);
                                args.putString("email", email);
                                args.putString("cus_id", String.valueOf(response.body().getSocialLoginDataModel().getId()));
                                Utils.CURRENT_TAG = null;
                                Utils.switchContent(R.id.frame_launch_container,
                                        Utils.FRAGMENT_SOCIALSIGNUP,
                                        HomeActivity.this, args,
                                        Utils.AnimationType.FADE_IN);
                            } else {

                                showProgressDialog(HomeActivity.this);
                                callCommonFBGOOGLE(social_type, response);
                            }
                        }

                    } else if (response.body().getMsg().equalsIgnoreCase("Email already exists.")) {
                        hideProgressDialog();
                        Toast.makeText(HomeActivity.this, R.string.register_email_already_exist, Toast.LENGTH_SHORT).show();
                    } else {
                        hideProgressDialog();
                        Toast.makeText(HomeActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SocialLoginModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure:~~~ " + t.toString());
                call.cancel();
                LoginManager.getInstance().logOut();
            }
        });
    }

    private void callCommonFBGOOGLE(String social_type, @NonNull Response<SocialLoginModel> response) {

       /* String access_token = Objects.requireNonNull(response.body()).getAccessToken();
        String token_type = Objects.requireNonNull(response.body()).getTokenType();
        String expires_in = String.valueOf(Objects.requireNonNull(response.body()).getExpiresIn());*/

        String UserId = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getId());
        String Firstname = Objects.requireNonNull(response.body()).getSocialLoginDataModel().getFirstName();
        String Lastname = Objects.requireNonNull(response.body()).getSocialLoginDataModel().getLastName();
        String Email = Objects.requireNonNull(response.body()).getSocialLoginDataModel().getEmail();
        String UserProPic = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getAvatar());
        String Dob = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getBirthDate());
        String State = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getStateId());
        String Country = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getCountryId());
        String Address = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getAddress());
        //   String Currency = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getCurrency());
        String Mobile = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getPhoneNumber());
        String Countrycode = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getCountryCode());
        String PhoneVerified = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getPhoneVerified());
        String EmailVerified = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getEmailVerified());
        String WalletAmount = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getWalletBalance());
        String Referal = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getReferralCode());
        String InviteCode = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getInviteCode());
        String language = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getLanguage());
        String currency = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getCurrency());
        String currencysymbol = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getCurrencySymbol());
        String status = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getStatus());

        strLanguage = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getLanguage());
        strServiceStatus = String.valueOf(Objects.requireNonNull(response.body()).getSocialLoginDataModel().getServiceIds());

        saveInFirebase(UserId, Firstname, Lastname, Email, Countrycode, Mobile, UserProPic, currency, currencysymbol, social_type, status);

        //"status":1,"approv_status":0 -- Active
        //"status":2,"approv_status":0 -- InActive
        savesharedpreference("access_token", "token_type", "expires_in", UserId, Firstname, Lastname, Email, UserProPic, Dob, State,
                Country, Address, Mobile, Countrycode, WalletAmount, Referal, InviteCode, currency, currencysymbol);

        hideProgressDialog();
    }

    private void saveInFirebase(String userId, String firstname, String lastname, String email, String countrycode,
                                String mobile, String userProPic, String currency, String currencysymbol, String social_type, String status) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);

        ref.child(Utils.FIREBASE_PROVIDER_FNAME).setValue(firstname);
        ref.child(Utils.FIREBASE_PROVIDER_LNAME).setValue(lastname);
        ref.child(Utils.FIREBASE_PROVIDER_EMAIL).setValue(email);
        ref.child(Utils.FIREBASE_PROVIDER_CC).setValue(countrycode);
        ref.child(Utils.FIREBASE_PROVIDER_MOBILE).setValue(mobile);
        ref.child(Utils.FIREBASE_PROVIDER_PRO_PIC).setValue(userProPic);

        ref.child("social_type").setValue(social_type);

        //ref.child("wallet", "0");
        //ref.child("status", "0");

        if (status.equals("1")) {
            ref.child("approved").setValue("1");
        } else {
            ref.child("approved").setValue("0");
        }

        if (strLanguage != null) {
            if (!strLanguage.equals("null")) {
                if (!strLanguage.equals("")) {
                    if (!strLanguage.equals(" ")) {
                        ref.child("language").setValue(strLanguage);
                    } else {
                        ref.child("language").setValue("");
                    }
                } else {
                    ref.child("language").setValue("");
                }
            } else {
                ref.child("language").setValue("");
            }
        } else {
            ref.child("language").setValue("");
        }

        if (currency != null) {
            if (!currency.equals("null")) {
                if (!currency.equals("")) {
                    if (!currency.equals(" ")) {
                        if (currencysymbol != null) {
                            if (!currencysymbol.equals("null")) {
                                if (!currencysymbol.equals("")) {
                                    if (!currencysymbol.equals(" ")) {
                                        ref.child("currency").setValue(currency + ":" + currencysymbol);
                                    } else {
                                        ref.child("currency").setValue("");
                                    }
                                } else {
                                    ref.child("currency").setValue("");
                                }
                            } else {
                                ref.child("currency").setValue("");
                            }
                        } else {
                            ref.child("currency").setValue("");
                        }
                    } else {
                        ref.child("currency").setValue("");
                    }
                } else {
                    ref.child("currency").setValue("");
                }
            } else {
                ref.child("currency").setValue("");
            }
        } else {
            ref.child("currency").setValue("");
        }

        if (strServiceStatus != null) {
            if (!strServiceStatus.equalsIgnoreCase("")) {
                ref.child("category").setValue(strServiceStatus);
                Log.e(TAG, "saveInFirebase: ~~~~~Login~~~~" + strServiceStatus);
            } else {
                ref.child("category").setValue("0");
            }
        } else {
            ref.child("category").setValue("0");
        }

      /*  ref.setValue(updates, (databaseError, databaseReference) -> {
            System.out.println("DATA SAVED SUCCESSFULLY");
            if (databaseError != null) {
                System.out.println("DATA SAVED SUCCESSFULLY");
            }
        });*/

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


    private void savesharedpreference(String access_token, String token_type, String exp_time, String userId,
                                      String firstname, String lastname, String email, String userProPic, String dob,
                                      String state, String country, String address, String Mobile, String Countrycode,
                                      String WalletAmount, String referal, String inviteCode, String currency
            , String currencysymbol) {

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN, "1256");
        getState.putString(Utils.SP_DRIVER_TOKENTYPE, "32556");
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN, "access_token");
        getState.putString(Utils.SP_DRIVER_TOKENTYPE, "token_type");
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME, "exp_time");
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
        // getState.putString(Utils.SP_CURRENCY,currency);
        getState.putString(Utils.SP_DRIVER_WALLET_AMT, WalletAmount);
        getState.putString(Utils.SP_DRIVER_REFERAL_CODE, referal);
        getState.putString(Utils.SP_DRIVER_INVITE_CODE, inviteCode);
        getState.apply();

        getState.putString(Utils.SP_CHECKED_REMEMBERME, "false");
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
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("MissingPermission~~~", "currentLocationClick~~MissingPermission");
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION_L);
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
                Intent Signin = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(Signin);
                finish();
                /*Utils.switchContent(R.id.frame_launch_container,
                        Utils.FRAGMENT_MANAGE_SERVICE,
                        HomeActivity.this, args,
                        Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/
            }
        } else {
            Toast.makeText(getApplicationContext(), "Services status empty", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveAccessToken(String token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, String.format("onActivityResult: in HomeActivity~~~~~~~~~%s", data));

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: ~~~~~~~~~~~~" + requestCode);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION_L) {
            callLanguage(strLanguage);
        }
    }


    private void handleSignInResult(@Nullable Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG, "handleSignInResult:" + completedTask.isSuccessful());

        try {
            // Signed in successfully, show authenticated U
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // Signed out, show unauthenticated UI.
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }


    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
    }


    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            System.out.println("email==>>" + account.getEmail());
            Log.i(TAG, "updateUI: get accont details===>" + account.getDisplayName() + "~~~~" + account.getId() + "~~~" + account.getIdToken());

            System.out.println("~~~~" + account.getId() + "google" + account.getDisplayName() + account.getDisplayName() + account.getEmail());

            checkSocialId(account.getId(), "google", account.getDisplayName(), account.getDisplayName(), account.getEmail());

            //checkSocialId(account.getId(), "google", account.getDisplayName(), account.getDisplayName(), account.getEmail());

            signOut();
        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();

        System.out.println("save fb use=>>" + object);

        /*
        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
*/

        String id = null;
        String email = "";
        try {
            id = object.getString("id");
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));

            if (object.has("email")) {
                if (!"null".equals(object.getString("email"))) {
                    //if (object.has("email"))
                    bundle.putString("email", object.getString("email"));
                    email = object.getString("email");
                } else {
                    email = "";
                }
            } else {
                email = "";
            }

            // if (object.has("gender"))
            // bundle.putString("gender", object.getString("gender"));
            //LoginManager.getInstance().logOut();

            System.out.println("~~~~" + id + "facebook" + object.getString("first_name")
                    + object.getString("last_name") + "" + email);

            checkSocialId(id, "facebook", object.getString("first_name"), object.getString("last_name"), email);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return bundle;
    }

    private void callLanguage(String language) {

        if (language == null) {
            if (strLanguage != null) {
                language = strLanguage;
            } else {
                language = "";
            }
        }

        getState.putBoolean(Utils.SP_DRIVER_LOGIN, true);
        getState.apply();

        if (language.equals("")) {
            if (BuildConfig.DEBUG) Log.e(TAG, "savesharedpreference: ");
            SuccessResponseLogin(language, "en", "US", "login");
        } else {
            //Toast.makeText(getActivity(), "Check Language", Toast.LENGTH_SHORT).show();
            switch (language) {
                case "Sinhala":
                    SuccessResponseLogin(language, "si", "LK", "login");
                    break;
                case "English":
                default:
                    SuccessResponseLogin(language, "en", "US", "login");
                    break;
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void SuccessResponseLogin(String langname, String code, String country, String key) {

        callLanguageChange(langname, code, country, key);
    }

    @Override
    public void SuccessResponseSocialLogin_(String langname, String code, String country, String key) {

        callLanguageChange(langname, code, country, key);
    }

    private void callLanguageChange(String langname, String code, String country, String key) {

        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(code + "-" + country);
        // Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setLanguage(code);
            getSpCurrency.putString(Utils.SP_LANGUAGE, langname);
            getSpCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, code);
            getSpCurrency.apply();
        } else {
            setLanguageBelow23(code, country);
            getSpCurrency.putString(Utils.SP_LANGUAGE, langname);
            getSpCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, code);
            getSpCurrency.putString(Utils.SP_LANGUAGE_COUNTRY, country);
            getSpCurrency.apply();
        }

        if (key.equals("login")) {
            Intent news = new Intent(HomeActivity.this, MainActivity.class);
            news.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            news.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(news);
            HomeActivity.this.finish();
        } else {
            // For Home page Language change
            Intent news = new Intent(HomeActivity.this, HomeActivity.class);
            news.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            news.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(news);
            HomeActivity.this.finish();
        }
    }


    private Twitter twitter;
    Dialog twitterDialog;
    AccessToken accToken = null;

    private boolean isLoggedIn() {
        Context context = this;
        String accessToken = context.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, Context.MODE_PRIVATE).getString("oauth_token", "");
        String accessTokenSecret = context.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, Context.MODE_PRIVATE).getString("oauth_token_secret", "");
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setOAuthConsumerKey(getResources().getString(R.string.twitter_api_key))
                .setOAuthConsumerSecret(getResources().getString(R.string.twitter_secrate_key))
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        twitter4j.conf.Configuration config = builder.build();
        TwitterFactory factory = new TwitterFactory(config);
        Twitter twitter = factory.getInstance();
        try {
            twitter.verifyCredentials();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void getRequestToken() {
        new Thread(() -> {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .setDebugEnabled(true)
                    .setOAuthConsumerKey(getResources().getString(R.string.twitter_api_key))
                    .setOAuthConsumerSecret(getResources().getString(R.string.twitter_secrate_key))
                    .setIncludeEmailEnabled(true);
            Configuration config = builder.build();
            TwitterFactory factory = new TwitterFactory(config);
            twitter = factory.getInstance();
            try {
                RequestToken requestToken = null;
                try {
                    requestToken = twitter.getOAuthRequestToken();
                    RequestToken finalRequestToken = requestToken;
                    if (com.instaguard.provider.BuildConfig.DEBUG) {
                        System.out.println("~~~~~finalRequestToken~~~~" + finalRequestToken);
                    }
                    runOnUiThread(() -> setupTwitterWebViewDialog(finalRequestToken.getAuthorizationURL()));
                } catch (TwitterException e) {
                    Log.e("TwitterException: ", "TwitterException-->" + e);
                }
            } catch (IllegalStateException e) {
                Log.e("ERROR: ", "IllegalStateException-->" + e);
            }
        }).start();
    }

    // Show twitter login page in a dialog
    @SuppressLint("SetJavaScriptEnabled")
    private void setupTwitterWebViewDialog(String url) {
        WebSettings settings;
        WebView webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new TwitterWebViewClient());

        webView.clearCache(true);

        webView.clearFormData();

        webView.clearHistory();

        webView.clearSslPreferences();

        webView.loadUrl(url);

        settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUserAgentString("Android Webview");

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        twitterDialog = new Dialog(this);
        twitterDialog.setContentView(webView);
        twitterDialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    // A client to know about WebView navigation
    class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://instaguard.uplogictech.com/")) {
                hideProgressDialog_();
                handleUrl(url);
                twitterDialog.dismiss();
                return true;
            }
            return false;
        }

        // Get the oauth_verifier
        private void handleUrl(String url) {
            Uri uri = Uri.parse(url);
            String oauthVerifier = uri.getQueryParameter("oauth_verifier");
            if (oauthVerifier == null) {
                oauthVerifier = "";
            }
            String finalOauthVerifier = oauthVerifier;
            new Thread(() -> {
                try {
                    accToken = twitter.getOAuthAccessToken(finalOauthVerifier);
                    getUserProfile();
                    twitterDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("~Exception~~printStackTrace~~~~" + e.getMessage());
                    System.out.println("~Exception~~printStackTrace~~~~" + e.getCause());
                    System.out.println("~Exception~~printStackTrace~~~~" + e.getLocalizedMessage());
                }
            }).start();
        }
    }

    public void getUserProfile() {
        new Thread(() -> {
            try {
                User usr = twitter.verifyCredentials();
                //Twitter Id
                String twitterId = String.valueOf(usr.getId());
                //Twitter Handle
                String twitterHandle = usr.getScreenName();
                //Twitter Name
                String twitterName = usr.getName();
                //Twitter Email
                String twitterEmail = usr.getEmail();
                // Twitter Profile Pic URL
                String twitterProfilePic = usr.getProfileImageURLHttps().replace("_normal", "");

                if (com.instaguard.provider.BuildConfig.DEBUG) {
                    Log.d("Twitter Handle: ", twitterHandle);
                    Log.d("Twitter Name: ", twitterName);
                    Log.d("Twitter Email: ", twitterEmail != null ? twitterEmail : "'Request email address from users' on the Twitter dashboard is disabled");
                    Log.d("Twitter Profile URL: ", twitterProfilePic);
                    Log.d("Twitter Id: ", twitterId);
                    // Twitter Access Token
                    Log.d("Twitter Access Token", accToken != null ? accToken.getToken() : "");
                }

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                sharedPref.edit().putString("oauth_token", accToken != null ? accToken.getToken() : "").apply();
                sharedPref.edit().putString("oauth_token_secret", accToken != null ? accToken.getTokenSecret() : "").apply();

                checkSocialId(twitterId, "twitter", twitterName, twitterHandle, twitterEmail);

            } catch (TwitterException e) {
                e.printStackTrace();
                System.out.println("~TwitterException~~printStackTrace~~~~" + e.getStatusCode());
                System.out.println("~TwitterException~~printStackTrace~~~~" + e.getMessage());
            }
        }).start();
    }


    Dialog linkedIndialog;
    String linkedinCode;

    // Show LinkedIn login page in a dialog
    @SuppressLint("SetJavaScriptEnabled")
    public void setupLinkedinWebviewDialog(String url) {
        linkedIndialog = new Dialog(this);
        WebView webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new LinkedInWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        linkedIndialog.setContentView(webView);
        linkedIndialog.show();
    }

    // A client to know about WebView navigations
// For API 21 and above
    public class LinkedInWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
        ) {
            String REDIRECT_URI;
            if (com.instaguard.provider.BuildConfig.DEBUG) {
                REDIRECT_URI = Utils.REDIRECT_URI;
            } else {
                REDIRECT_URI = Utils.REDIRECT_URI_LIVE;
            }
            if (request.getUrl().toString().startsWith(REDIRECT_URI)) {
                handleUrl(request.getUrl().toString());
                // Close the dialog after getting the authorization code
                if (request.getUrl().toString().contains("?code=")) {
                    linkedIndialog.dismiss();
                }
                return true;
            }
            return false;
        }

        // For API 19 and below
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String REDIRECT_URI;
            if (com.instaguard.provider.BuildConfig.DEBUG) {
                REDIRECT_URI = Utils.REDIRECT_URI;
            } else {
                REDIRECT_URI = Utils.REDIRECT_URI_LIVE;
            }
            if (url.startsWith(REDIRECT_URI)) {
                handleUrl(url);
                // Close the dialog after getting the authorization code
                if (url.contains("?code=")) {
                    linkedIndialog.dismiss();
                }
                return true;
            }
            return false;
        }

        // Check webview url for access token code or error
        private void handleUrl(String url) {
            Uri uri = Uri.parse(url);
            if (url.contains("code")) {
                linkedinCode = uri.getQueryParameter("code") != null ? uri.getQueryParameter("code") : "";
                linkedInRequestForAccessToken();
            } else if (url.contains("error")) {
                String error = uri.getQueryParameter("error") != null ? uri.getQueryParameter("error") : "";
                Log.e("Error: ", error);
            }
        }
    }


    public void linkedInRequestForAccessToken() {
        new Thread(() -> {
            String REDIRECT_URI;
            if (com.instaguard.provider.BuildConfig.DEBUG) {
                REDIRECT_URI = Utils.REDIRECT_URI;
            } else {
                REDIRECT_URI = Utils.REDIRECT_URI_LIVE;
            }
            String grantType = "authorization_code";
            String postParams = "grant_type=" + grantType + "&code=" + linkedinCode + "&redirect_uri=" + REDIRECT_URI + "&client_id=" + getResources().getString(R.string.linked_in_api_key) + "&client_secret=" + getResources().getString(R.string.linked_in_secrate_key);
            try {
                URL url = new URL(Utils.TOKENURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(postParams);
                outputStreamWriter.flush();
                String response = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    response = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())).lines().collect(Collectors.joining("\n"));
                }
                JSONObject jsonObject = null;
                if (response != null) {
                    jsonObject = new JSONObject(response);
                }
                String accessToken = jsonObject.getString("access_token");
                System.out.println("accessToken is: " + accessToken);
                int expiresIn = jsonObject.getInt("expires_in");
                System.out.println("expiresIn is: " + expiresIn);

                callFetchLinkedInUserDetails(accessToken);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void callFetchLinkedInUserDetails(String accessToken) {

        APIInterface apiInterface = APIClient.getClient_linked(Utils.LINKED_IN_URL).create(APIInterface.class);
        Call<LinkedInUserDetailsModel> call2 = apiInterface.doGetLinkedInUserInfo("Bearer " + accessToken);
        call2.enqueue(new Callback<LinkedInUserDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<LinkedInUserDetailsModel> call, @NonNull Response<LinkedInUserDetailsModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    String getSub = "";
                    String email = "";
                    String getName = "";
                    String getGivenName = "";

                    if (response.body() != null) {

                        getSub = response.body().getSub();
                        email = response.body().getEmail();
                        getName = response.body().getName();
                        getGivenName = response.body().getGivenName();

                        if (com.instaguard.provider.BuildConfig.DEBUG) {
                            System.out.println("~~~getName~~~" + getName);
                            System.out.println("~~~getGivenName~~~" + getGivenName);
                            System.out.println("~~~email~~~" + email);
                            System.out.println("~~~getSub~~~" + getSub);
                        }

                        getSub = getSub != null ? getSub : "";
                        getName = getName != null ? getName : "";
                        email = email != null ? email : "";
                        getGivenName = getGivenName != null ? getGivenName : "";

                        checkSocialId(getSub, "linkedin", getName, getGivenName, email);

                    } else {
                        hideProgressDialog_();
                        Toast.makeText(HomeActivity.this, "LinkedIn User info error,Try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideProgressDialog_();
                    Toast.makeText(HomeActivity.this, "LinkedIn User 500 error,Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LinkedInUserDetailsModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog_();
                Log.e(TAG, "onFailure: ~~~~~~~~~~~" + t);
                Toast.makeText(HomeActivity.this, "LinkedIn User onFailure error,Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    View lytProgressBar;

    private void hideProgressDialog_() {
        if (lytProgressBar.getVisibility() == View.VISIBLE) {
            lytProgressBar.setVisibility(View.GONE);
        }
    }

    private void showProgressDialog_() {
        if (lytProgressBar.getVisibility() == View.GONE) {
            lytProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
