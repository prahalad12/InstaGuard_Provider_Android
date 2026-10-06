package com.instaguard.provider.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.instaguard.provider.Helpers.MutedVideoView;
import com.instaguard.provider.Helpers.NetworkManager.Tovuti;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;

import java.util.concurrent.Executor;

public class LaunchActivity extends AppCompatActivity {

    SharedPreferences getStatePref, spCurrency;
    SharedPreferences.Editor putState, putSpCurrencye;
    String userId, userAccessToken;


    SharedPreferences getStatePrefNew;
    String fingerUserId, fingerStatus, fingerNoValue;
    //String faceUserId, faceStatus, faceNoValue;
    //View ivFacePrint;
    View ivFingerPrint;


    private Tovuti tovuti;
    RelativeLayout rlLowInternet;
    String TAG = "LaunchActivity";
    Boolean loginBoolean;
    MutedVideoView vView;
    Activity activity;

    DatabaseReference driverDistanceDatabaseRef;
    ValueEventListener driverDistanceValueEventListener;
    double distance;

    private FirebaseAuth mAuth;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        activity = LaunchActivity.this;

        putState = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getStatePref = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        spCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        putSpCurrencye = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();

        userId = getStatePref.getString(Utils.SP_DRIVERID, null);
        loginBoolean = getStatePref.getBoolean(Utils.SP_DRIVER_LOGIN, false);
        userAccessToken = getStatePref.getString(Utils.SP_DRIVERID, null);

        String strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY, null);
        if (strCurrencySymbol == null) {
            putSpCurrencye.putString(Utils.SP_CURRENCY, "USD");
            putSpCurrencye.putString(Utils.SP_CURRENCY_SYMBOL, "$");
            putSpCurrencye.apply();
        }

        System.out.println("strCurrencySymbol----->" + strCurrencySymbol);

        getStatePrefNew = getSharedPreferences(Utils.MY_PREFS_NAME_NEW, MODE_PRIVATE);

        fingerUserId = getStatePrefNew.getString(Utils.spFingerUserId, null);
        fingerStatus = getStatePrefNew.getString(Utils.spFingerStatus, null);
        fingerNoValue = getStatePrefNew.getString(Utils.spFingerNoStatus, null);

        //faceUserId = getStatePrefNew.getString(Utils.spFaceUserId, null);
        //faceStatus = getStatePrefNew.getString(Utils.spFaceStatus, null);
        //faceNoValue = getStatePrefNew.getString(Utils.spFaceNoStatus, null);


        ivFingerPrint = findViewById(R.id._rl_face_finger_lyt);
        //ivFacePrint = findViewById(R.id.iv_face_print);

        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    System.out.println("refreshedToken =token=>" + token);

                    SharedPreferences.Editor getState = getApplicationContext().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE).edit();
                    getState.putString(Utils.SP_DEVICE_ID, token);
                    getState.apply();
                });

        checkDeviceCanAuthenticateWithBiometrics();

        enableFingerPrint("home");

        ivFingerPrint.setOnClickListener(v -> enableFingerPrint("finger"));

        //ivFacePrint.setOnClickListener(v -> enableFingerPrint("face"));

    }


    private void callApp(int SPLASH_DISPLAY_LENGTH,String key) {

        new Handler().postDelayed(() -> {
            if (loginBoolean) {
                if (userId != null) {
                    Log.e("USERId~~Splash~~~.", userId);
                    if (driverDistanceDatabaseRef != null) {
                        driverDistanceDatabaseRef.removeEventListener(driverDistanceValueEventListener);
                    }

                    driverDistanceDatabaseRef = FirebaseDatabase.getInstance().getReference().child("providers");
                    driverDistanceDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userId).getValue() != null) {
                                Log.e("if USERId~~Splash~~~~~~~~.", userId);

                                try {
                                    String values = "1";
                                    if (getIntent().getExtras() != null) {
                                        for (String key : getIntent().getExtras().keySet()) {
                                            Object value = getIntent().getExtras().get(key);
                                            Log.d(TAG, "Key: " + key + " Value: " + value);
                                            assert value != null;
                                            switch (value.toString()) {
                                                case "Offers":
                                                    Log.e(TAG, "onCreate: Inside offers~~~");
                                                    values = "2";
                                                    break;
                                                case "Announcements":
                                                    Log.e(TAG, "onCreate: Inside Announcements~~~");
                                                    values = "3";
                                                    break;
                                                case "pending":
                                                    Log.e(TAG, "onCreate: Inside pending~~~");
                                                    values = "4";
                                                    break;
                                                case "Reminder":
                                                    Log.e(TAG, "onCreate: Inside reminder~~~");
                                                    values = "5";
                                                    break;
                                            }
                                        }

                                        switch (values) {
                                            case "2":
                                                callDistanceCheck(dataSnapshot, "2");
                                                break;
                                            case "3":
                                                callDistanceCheck(dataSnapshot, "3");
                                                break;
                                            case "4":
                                                callDistanceCheck(dataSnapshot, "4");
                                                break;
                                            case "5":
                                                callDistanceCheck(dataSnapshot, "5");
                                                break;
                                            default:
                                                callDistanceCheck(dataSnapshot, "1");
                                                break;
                                        }
                                    } else {
                                        callDistanceCheck(dataSnapshot, "1");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "onDataChange: ~~~~~~~~~" + e.toString());
                                }


                            } else {
                                Log.e(" else USERId~~Splash~~~~~~~~.", userId);
                                LaunchActivity.this.startActivity(new Intent(LaunchActivity.this.getApplicationContext(), HomeActivity.class));
                                LaunchActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
                                LaunchActivity.this.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(TAG, "onCancelled: -->" + databaseError);
                        }
                    });

                } else {
                    LaunchActivity.this.startActivity(new Intent(LaunchActivity.this.getApplicationContext(), HomeActivity.class));
                    LaunchActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
                    LaunchActivity.this.finish();
                }
            } else {
                LaunchActivity.this.startActivity(new Intent(LaunchActivity.this.getApplicationContext(), HomeActivity.class));
                LaunchActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
                LaunchActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.CURRENT_TAG = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void callDistanceCheck(DataSnapshot dataSnapshot, String key) {


        if (dataSnapshot.child(userId).child("distance").getValue() != null) {
            distance = Double.parseDouble(String.valueOf(dataSnapshot.child(userId).child("distance").getValue()));

            if (dataSnapshot.child(userId).child("est_distance").getValue() == null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("providers").child(userId).child("est_distance").setValue("0");
            }
            if (dataSnapshot.child(userId).child("est_duration").getValue() == null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("providers").child(userId).child("est_duration").setValue("0");
            }


            // Log.e("DRIVERId~~distance~~~.", String.valueOf(distance));
            Intent nes = new Intent(LaunchActivity.this.getApplicationContext(), MainActivity.class);
            switch (key) {
                case "2":
                    nes.putExtra("Notify", "Offers");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "3":
                    nes.putExtra("Notify", "Announcements");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "4":
                    nes.putExtra("Notify", "Pending");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "5":
                    nes.putExtra("Notify", "Reminder");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            nes.putExtra("distance", String.valueOf(distance));
            LaunchActivity.this.startActivity(nes);
            //  LaunchActivity.this.startActivity(new Intent(LaunchActivity.this.getApplicationContext(), DriverMainActivity.class));
            LaunchActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
            LaunchActivity.this.finish();

        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("providers").child(userId).child("distance").setValue("0");
            ref.child("providers").child(userId).child("est_distance").setValue("0");
            ref.child("providers").child(userId).child("est_duration").setValue("0");


            Intent nes = new Intent(LaunchActivity.this.getApplicationContext(), MainActivity.class);
            nes.putExtra("distance", "0");
            switch (key) {
                case "2":
                    nes.putExtra("Notify", "Offers");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "3":
                    nes.putExtra("Notify", "Announcements");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "4":
                    nes.putExtra("Notify", "Pending");
                    nes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    nes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            LaunchActivity.this.startActivity(nes);

            //  LaunchActivity.this.startActivity(new Intent(LaunchActivity.this.getApplicationContext(), DriverMainActivity.class));
            LaunchActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
            LaunchActivity.this.finish();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                System.out.println("-----not null----");
            } else {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(LaunchActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInAnonymously:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                                    // Toast.makeText(LaunchActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }


    public void enableFingerPrint(String key) {
        switch (key) {
            case "home":

                if (fingerNoValue == null) {
                    if (fingerUserId != null) {
                        if (fingerStatus != null) {
                            if (fingerStatus.equals("1")) {
                                ivFingerPrint.setVisibility(View.VISIBLE);

                                /*if (faceStatus != null) {
                                    if (faceStatus.equals("1")) {
                                        ivFacePrint.setVisibility(View.VISIBLE);
                                    } else {
                                        ivFacePrint.setVisibility(View.GONE);
                                    }
                                } else {
                                    ivFacePrint.setVisibility(View.GONE);
                                }*/

                            } else {
                                ivFingerPrint.setVisibility(View.GONE);
                                /*if (faceStatus != null) {
                                    if (faceStatus.equals("1")) {
                                        ivFacePrint.setVisibility(View.VISIBLE);
                                    } else {
                                        ivFacePrint.setVisibility(View.GONE);
                                        callApp(1500, "4");
                                    }
                                } else {
                                    ivFacePrint.setVisibility(View.GONE);
                                    callApp(1500, "5");
                                }*/

                                callApp(1500, "4");
                            }
                        } else {
                            ivFingerPrint.setVisibility(View.GONE);
                            /*if (faceStatus != null) {
                                if (faceStatus.equals("1")) {
                                    ivFacePrint.setVisibility(View.VISIBLE);
                                } else {
                                    ivFacePrint.setVisibility(View.GONE);
                                    callApp(1500, "4");
                                }
                            } else {
                                ivFacePrint.setVisibility(View.GONE);
                                callApp(1500, "5");
                            }*/

                            callApp(1500, "4");
                        }
                    } else {
                        ivFingerPrint.setVisibility(View.GONE);
                       /* if (faceStatus != null) {
                            if (faceStatus.equals("1")) {
                                ivFacePrint.setVisibility(View.VISIBLE);
                            } else {
                                ivFacePrint.setVisibility(View.GONE);
                                callApp(1500, "4");
                            }
                        } else {
                            ivFacePrint.setVisibility(View.GONE);
                            callApp(1500, "5");
                        }*/

                        callApp(1500, "4");
                    }
                }


                break;

            case "face":
                /*if (faceStatus != null) {
                    if (faceStatus.equals("1")) {
                        Toast.makeText(activity, " Face Login", Toast.LENGTH_SHORT).show();
                        faceRecognitionState();
                    } else {
                        Toast.makeText(activity, "No Face Login", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "No Face Login", Toast.LENGTH_SHORT).show();
                }*/
                break;


            case "finger":
                if (fingerNoValue == null) {
                    if (fingerUserId != null) {
                        if (fingerStatus != null) {
                            if (fingerStatus.equals("1")) {
                                if (fingerBoolean) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                        if (CheckKeyGaurd()) {
                                            biometricPrompt.authenticate(promptInfoKey);
                                        } else {
                                            setUpDeviceLockInAPIBelow23Intent();
                                        }
                                    } else {
                                        if (CheckKeyGaurd()) {
                                            biometricPrompt.authenticate(promptInfoKey);
                                        } else {
                                            System.out.println("Else!!0");
                                            biometricPrompt.authenticate(promptInfo);
                                        }
                                    }
                                } else {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                        if (CheckKeyGaurd()) {
                                            biometricPrompt.authenticate(promptInfoKey);
                                        } else {
                                            // setUpDeviceLockInAPIBelow23Intent();
                                            System.out.println("Else~~~~1");
                                            callApp(1500, "6");
                                        }
                                    } else {
                                        if (CheckKeyGaurd()) {
                                            biometricPrompt.authenticate(promptInfoKey);
                                        } else {
                                            // setUpDeviceLockInAPIBelow23Intent();
                                            System.out.println("Else~~~2");
                                            callApp(1500, "7");
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "No Finger Login", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "No Finger Login", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "No Finger Login", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                Toast.makeText(activity, "No Login", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /*-*******************************************************************************/

    boolean fingerBoolean = false;
    KeyguardManager keyguardManager;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt.PromptInfo promptInfoKey;
    private BiometricManager biometricManager;
    int RC_BIOMETRICS_ENROLL = 10;
    int RC_DEVICE_CREDENTIAL_ENROLL = 18;

    private void setUpDeviceLockInAPIBelow23Intent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivityForResult(enrollIntent, RC_DEVICE_CREDENTIAL_ENROLL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return new Intent(Settings.ACTION_SECURITY_SETTINGS);
    }

    private void checkDeviceCanAuthenticateWithBiometrics() {

        biometricManager = BiometricManager.from(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (CheckKeyGaurd()) {

                switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Log.e("MY_APP_TAG", "No biometric features available on this device.");
                    case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                        Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        fingerBoolean = false;
                        Log.e("MY_APP_TAG", "No Biometric features are currently unavailable.");
                        break;
                }

            } else {
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.~~~1~~");
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                    case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                        fingerBoolean = false;
                        Log.e("MY_APP_TAG", "No Biometric features are currently unavailable.~~1~~");
                        break;
                }
            }

        } else {
            if (CheckKeyGaurd()) {

                switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Log.e("MY_APP_TAG", "No biometric features available on this device.");
                    case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                        Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        fingerBoolean = false;
                        Log.e("MY_APP_TAG", "No Biometric features are currently unavailable.");
                        break;
                }

            } else {
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Log.d("MY_APP_TAG", "App can authenticate using biometrics.~~~2~~");
                        fingerBoolean = true;
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                    case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                        fingerBoolean = false;
                        Log.e("MY_APP_TAG", "No Biometric features are currently unavailable.~~2~~");
                        break;
                }
            }
        }

        init();

    }

    private void init() {

        executor = ContextCompat.getMainExecutor(activity);
        biometricPrompt = new BiometricPrompt(LaunchActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                /*Toast.makeText(activity,
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();*/
                Toast.makeText(activity,
                                "Authentication error: " + getErrorMessage(errorCode, errString), Toast.LENGTH_SHORT)
                        .show();
                //setUpdateFingerPrintStatus("0");

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(activity,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                //setUpdateFingerPrintStatus("1");
                ivFingerPrint.setVisibility(View.GONE);

                callApp(100, "8");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(activity, "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
                // setUpdateFingerPrintStatus("0");
            }
        });

        /*-****************If Split this content message for crediental com.bharathvishal.androidbiometricauthentication.Activities*/

        try {

            String titleKey = getResources().getString(R.string.PASSWORD_PIN_AUTHENTICATION);
            String subtitleKey = getResources().getString(R.string.PASSWORD_PIN_AUTHENTICATION_SUBTITLE);
            String descriptionKey = getResources().getString(R.string.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION);

            String title = getResources().getString(R.string.BIOMETRIC_AUTHENTICATION);
            String subtitle = getResources().getString(R.string.BIOMETRIC_AUTHENTICATION_SUBTITLE);
            String description = getResources().getString(R.string.BIOMETRIC_AUTHENTICATION_DESCRIPTION);
            String Cancel = getResources().getString(R.string.BIOMETRIC_AUTHENTICATION_CANCEL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                int authFlag = BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.BIOMETRIC_WEAK
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

                promptInfoKey = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(titleKey)
                        .setSubtitle(subtitleKey)
                        .setDescription(descriptionKey)
                        .setConfirmationRequired(true)
                        .setAllowedAuthenticators(authFlag)
                        .build();
            } else {
                promptInfoKey = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(titleKey)
                        .setSubtitle(subtitleKey)
                        .setDescription(descriptionKey)
                        .setConfirmationRequired(true)
                        .setDeviceCredentialAllowed(true)
                        .build();
            }

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setConfirmationRequired(true)
                    .setNegativeButtonText(Cancel)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "FP Ex Try again -- " + e, Toast.LENGTH_SHORT).show();
        }
    }


    private String getErrorMessage(int errorCode, CharSequence errString) {
        String label;
        switch (errorCode) {
            case BiometricPrompt.ERROR_NEGATIVE_BUTTON:
                label = activity.getResources().getString(R.string.message_user_app_authentication);
                break;
            case BiometricPrompt.ERROR_HW_UNAVAILABLE:
                label = activity.getResources().getString(R.string.error_hw_unavailable);
                break;
            case BiometricPrompt.ERROR_UNABLE_TO_PROCESS:
                label = activity.getResources().getString(R.string.error_unable_to_process);
                break;
            case BiometricPrompt.ERROR_TIMEOUT:
                label = activity.getResources().getString(R.string.error_time_out);
                break;
            case BiometricPrompt.ERROR_NO_SPACE:
                label = activity.getResources().getString(R.string.error_no_space);
                break;
            case BiometricPrompt.ERROR_CANCELED:
                label = activity.getResources().getString(R.string.error_canceled);
                break;
            case BiometricPrompt.ERROR_LOCKOUT:
                label = activity.getResources().getString(R.string.error_lockout);
                break;
            case BiometricPrompt.ERROR_VENDOR:
                label = activity.getResources().getString(R.string.error_vendor);
                break;
            case BiometricPrompt.ERROR_LOCKOUT_PERMANENT:
                label = activity.getResources().getString(R.string.error_lockout_permanent);
                break;
            case BiometricPrompt.ERROR_USER_CANCELED:
                label = activity.getResources().getString(R.string.error_user_canceled);
                break;
            case BiometricPrompt.ERROR_NO_BIOMETRICS:
                //checkAPILevelAndProceed();
                label = activity.getResources().getString(R.string.error_no_biometrics);
                break;
            case BiometricPrompt.ERROR_HW_NOT_PRESENT:
                label = activity.getResources().getString(R.string.error_hw_not_present);
                break;
            case BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL:
                //callBiometricEntrollIntent();
                label = activity.getResources().getString(R.string.error_no_device_credentials);
                break;
            case BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED:
                label = activity.getResources().getString(R.string.error_security_update_required);
                break;
            default:
                label = errString.toString();
                break;
        }

        return label;
    }

    private Boolean CheckKeyGaurd() {
        keyguardManager = (KeyguardManager) activity.getSystemService(KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardSecure();
    }

    /*-*******************************************************************************/


}
