package com.instaguard.provider.Activities;

import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.databinding.ActivityWebViewBinding;

import java.net.URLDecoder;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {
    ActivityWebViewBinding webViewBinding;
    SharedPreferences spPrefs;
    String url, userId;
    ValueCallback<Uri[]> vc;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webViewBinding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(webViewBinding.getRoot());

        spPrefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("webUrl");
            try {
                String afterDecode = URLDecoder.decode(url, "UTF-8");
                webViewBinding.web.loadUrl(Objects.requireNonNull(afterDecode));
                webViewBinding.web.getSettings().setJavaScriptEnabled(true);
                webViewBinding.web.getSettings().setAllowFileAccessFromFileURLs(true);
                webViewBinding.web.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webViewBinding.web.getSettings().setAllowFileAccess(true);;

               /* webViewBinding.web.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            request.grant(request.getResources());
                        }
                    }

                });*/

                webViewBinding.web.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            request.grant(request.getResources());
                        }
                    }

                    @Override
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                        WebViewActivity.this.filePathCallback = filePathCallback;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_REQUEST_CODE);
                        return true;
                    }
                });

                webViewBinding.web.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return false;
                    }
                });

            } catch (Throwable e) {
                System.out.println("web view error "+ e.getMessage());
            }
        }

        callFB();
        onClick();
    }

    private void callFB() { // for return back to bank details fragment (after bank details upload)

        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Object returnStatus = snapshot.child("return_status").getValue();
                        if (Objects.requireNonNull(returnStatus).toString().equals("1")) {
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("call fb error "+ error.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println("getting data from firebase exception "+ e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (filePathCallback == null) {
                return;
            }
            Uri[] results = null;
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        } else {
            Toast.makeText(this, "File selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClick() {
        webViewBinding.imgbtnBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
    }
}