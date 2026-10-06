package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.ManageDocumentAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.CountryCode.CountryCodePicker;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class ManageViewDocumentsFragment extends Fragment {

    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    public static final String ARG_PARAM3 = "param3";
    public static final String ARG_PARAM4 = "param4";

    View view;

    Toolbar toolbarSignin;
    ImageButton imgbtnBack;
    ImageView ivMenuProfile,ivViewDocuments;
    CustomTextView txtTitle;
    CustomEditText edtFname,edtLname,edtEmail,edtMobile,edtLanguage,edtCurrency,edtCountryCode;
    SimpleDraweeView iv_manage_view;
    private AwesomeValidation Validation;
    CustomButton btnSelectDocument,btnUpdateSettings;
    SharedPreferences.Editor putUpdateDocument;
    SharedPreferences getUpdateDocument;
    String strfname,strlname,stremail,strMobile,strCountyCode;
    CountryCodePicker ccp;

    private static final int CAMERA_IMAGE_REQUEST = 101;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    File fi;
    Bitmap bitmap;
    String fileBinaryData, strProPic;
    private String imageName,imagee;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private String userChoosenTask;
    public String TAG="SettingFragment";
    private Uri fileUri;
    Uri selectedImageUri;

    RecyclerView rvManageDocument;
    RelativeLayout rlEmptyview;

    String userId, userDocuPic,userAccessToken;

    DatabaseReference providerApproveStatusDatabaseBRef;
    ValueEventListener providerApproveStatusValueEventListener;
    String strViewDocumentsName,strViewDocuments;

    ManageDocumentAdapter adapter;

    public ManageViewDocumentsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static com.instaguard.provider.Fragments.ManageViewDocumentsFragment newInstance(String param1, String param2) {
        com.instaguard.provider.Fragments.ManageViewDocumentsFragment fragment = new com.instaguard.provider.Fragments.ManageViewDocumentsFragment();
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
            strViewDocuments = getArguments().getString(ARG_PARAM1);
            strViewDocumentsName = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"CommitPrefEdits", "LongLogTag"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_view_document, container, false);

        putUpdateDocument = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        Fresco.initialize(getActivity());
        getUpdateDocument = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = getUpdateDocument.getString(Utils.SP_DRIVERID, null);
        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        userDocuPic = getUpdateDocument.getString(Utils.SP_DRIVER_DOC_PIC, null);
        Log.e("UserId~~ManageDocumentsFragment~~",userId);

         imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_backs);

        ivViewDocuments = (ImageView) view.findViewById(R.id.iv_view_documents);
        txtTitle = (CustomTextView) view.findViewById(R.id.textView);

        txtTitle.setText(strViewDocumentsName);

        if (strViewDocuments != null) {
            if (!strViewDocuments.isEmpty()) {
                // imageUser.setImageURI("http://" + category_id);
                Picasso.get().load(Utils.BASE_URL_IMAGE_UPLOAD +strViewDocuments)
                        .placeholder(R.mipmap.doc_off)
                        .error(R.mipmap.doc_off)
                        .into(ivViewDocuments);
            }
        }

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

        onBackButton(view);
        return view;
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

    public void backButtonFunction()
    {
       // removeFragment(Utils.FRAGMENT_MANAGE_VIEW_DOCUMENT);
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MANAGEDOCUMENT, Objects.requireNonNull(getActivity()),null,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }

    public void showTimeOut() {
        Context mContext = getActivity() ;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.AppCompatAlertDialogStyle);
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

}
