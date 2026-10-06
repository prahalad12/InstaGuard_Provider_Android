package com.instaguard.provider.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Adapter.DocumentAdapter;
import com.instaguard.provider.BuildConfig;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Localizationactivity.ui.LocalizationActivity;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Helpers.imagezipper.ImageZipper;
import com.instaguard.provider.Models.AppSettingWebModel;
import com.instaguard.provider.Models.DocumentsUpload.DocumentList;
import com.instaguard.provider.Models.DocumentsUpload.DocumentListData;
import com.instaguard.provider.Models.DocumentsUpload.DocumentViewModel;
import com.instaguard.provider.Models.DocumentsUpload.ReUploadModel;
import com.instaguard.provider.Models.DocumentsUpload.UploadDocModel;
import com.instaguard.provider.Models.DocumentsUpload.UploadModel;
import com.instaguard.provider.R;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Adapter.DocumentAdapter.imgUpdated;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM1;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM2;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM3;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM4;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_501;
import static java.lang.System.out;

public class DocumentActivity extends LocalizationActivity implements DocumentAdapter.SuccessResponse, DocumentAdapter.OnDatePickerClickListener {

    public static FragmentManager manager;

    public String TAG = "DocumentActivity";
    public static Boolean imgUriUpdate = false;

    String strDetails;
    public static boolean imgUploaded = false;

    private SharedPreferences.Editor editor;

    private SharedPreferences prefs;

    private APIInterface apiInterface;

    private Activity activity;

    private BaseActivity baseActivity;

    private String driverId, siaStatus;

    private String userDocuPic;

    private String strFrom;

    private String strDocId;

    private String strId;

    private String strDocName;

    private String strDocNameAr;

    private String token;

    private DocumentAdapter adapter;

    private List<DocumentList> documentLists;

    private String[] items, itemss;

    JSONArray jsonArray;

    private static final int CAMERA_IMAGE_REQUEST = 101;
    private String siaIdToSend, dateToSend, dateStatusToSend;

    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_PDF_REQUEST = 2;

    private Uri fileUri;

    String fileBinaryData;

    String strProPic;

    String picturePath;
    File finalFile;

    DatabaseReference driverStatusCheckinoutDatabaseRef;

    ValueEventListener driverStatusCheckinoutValueEventListener;

    String strDocStatusFB;

    Boolean bDocument = true;

    String strDate;

    String strDateStatus;

    RelativeLayout contentView;

    Spinner spCategory;

    ProgressBar progressBarSpinner;

    CustomTextView txtLabelCategory;

    ImageButton imgBtnBack;

    ImageView imgBtnRefresh;

    RecyclerView rcvDocument;

    RelativeLayout progressBar;

    LinearLayout llNoRecordsFound;

    CustomTextView txtSkip;

    CustomButton btnConfitmDocumetnUpload;

    CheckBox cbIAagreeDocumentsNorms;
    String strGetDocumentNorms = "";
    Dialog dialogDocumentNorms;
    CustomButton btnAgreeDialogDocNorms;
    CustomTextView txtDocumentNorms;
    CustomTextView txtCloseDialogNorms;
    AppCompatButton docSubBtn;
    Integer siaPosition = 0;

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        activity = this;

        manager = ((this)).getSupportFragmentManager();

        DocumentAdapter.successResponse = this;
        DocumentAdapter.datePickerClickListener = this;

        editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString(Utils.SP_DRIVERID, null);
        token = prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        userDocuPic = prefs.getString(Utils.SP_DRIVER_DOC_PIC, null);

        Log.e("driverId~~ManageDocuments~~", driverId);

        out.println("entered in document activity ");

        baseActivity = new BaseActivity();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        jsonArray = new JSONArray();

        contentView = findViewById(R.id.rl_content_view_document_upload);

        spCategory = findViewById(R.id.sp_category_setup);

        progressBarSpinner = findViewById(R.id.progress_bar_spinner);
        txtLabelCategory = findViewById(R.id.txt_label_category);

        imgBtnBack = findViewById(R.id.imgbtn_document_upload);

        imgBtnRefresh = findViewById(R.id.btn_edit_);

        rcvDocument = findViewById(R.id.rcv_document);
        progressBar = findViewById(R.id.rl_progressBar_doc_up);

        llNoRecordsFound = findViewById(R.id.ll_noresult);

        txtSkip = findViewById(R.id.txt_alreadysignin1);
        btnConfitmDocumetnUpload = findViewById(R.id.btn_confirm_document_upload);
        docSubBtn = findViewById(R.id.docSubBtn);


        getDriverStatusFB();

        try {

            Intent intent = getIntent();
            strFrom = intent.getStringExtra("from");

            cbIAagreeDocumentsNorms = findViewById(R.id.cb_i_agree_documents_norms);

            //InternetConnection(this, rlLowInternet);

            dialogDocumentNorms = new Dialog(DocumentActivity.this, R.style.AppTheme);
            dialogDocumentNorms.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialogDocumentNorms.setContentView(R.layout.dialog_document_norms);
            //Objects.requireNonNull(dialogDocumentNorms.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogDocumentNorms.setCancelable(true);
            dialogDocumentNorms.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialogDocumentNorms.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            txtCloseDialogNorms = dialogDocumentNorms.findViewById(R.id.txt_select_currency);
            txtDocumentNorms = dialogDocumentNorms.findViewById(R.id.txt_select_);
            btnAgreeDialogDocNorms = dialogDocumentNorms.findViewById(R.id.btn_confirm_document_upload);

            Call<AppSettingWebModel> call2 = apiInterface.doGetAppSettings();
            call2.enqueue(new Callback<AppSettingWebModel>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(@NonNull Call<AppSettingWebModel> call, @NonNull Response<AppSettingWebModel> response) {
                    hideProgressDialog();
                    if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                        Object getDocumentNorms = Objects.requireNonNull(response.body()).getAppSetting().getDocumentNorms();
                        if (getDocumentNorms != null && !String.valueOf(getDocumentNorms).equals("null")
                                && !String.valueOf(getDocumentNorms).equals("")) {
                            strGetDocumentNorms = getDocumentNorms.toString();

                            try {
                                txtDocumentNorms.setText(Html.fromHtml(strGetDocumentNorms));
                                Linkify.addLinks(txtDocumentNorms, Linkify.ALL);
                                txtDocumentNorms.setLinkTextColor(Color.parseColor("#FF0000"));

                            } catch (Exception e) {
                                e.printStackTrace();
                                txtDocumentNorms.setText(HtmlCompat.fromHtml(strGetDocumentNorms, HtmlCompat.FROM_HTML_MODE_LEGACY));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppSettingWebModel> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        //InternetConnection(this, rlLowInternet);

        onClicks();

    }

    private void onClicks() {

        txtSkip.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            overridePendingTransition(R.anim.fade_in_speed, R.anim.fade_out_speed);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        imgBtnRefresh.setOnClickListener(view -> {
            getDriverStatusFB();
        });

        progressBar.setOnClickListener(view -> {
        });

        imgBtnBack.setOnClickListener(view -> onBackPressed());

        btnConfitmDocumetnUpload.setOnClickListener(view -> onBackPressed());

        cbIAagreeDocumentsNorms.setOnClickListener(view -> {
            if (strGetDocumentNorms != null && !strGetDocumentNorms.equals("null") && !strGetDocumentNorms.equals("")) {
                if (dialogDocumentNorms != null && !dialogDocumentNorms.isShowing()) {
                    dialogDocumentNorms.show();
                }
            } else {
                if (cbIAagreeDocumentsNorms.isChecked()) {
                    cbIAagreeDocumentsNorms.setChecked(false);
                }
            }
        });

        btnAgreeDialogDocNorms.setOnClickListener(view -> {
            if (dialogDocumentNorms != null && dialogDocumentNorms.isShowing()) {
                dialogDocumentNorms.dismiss();
            }
            if (!cbIAagreeDocumentsNorms.isChecked()) {
                cbIAagreeDocumentsNorms.setChecked(true);
            }
        });

        txtCloseDialogNorms.setOnClickListener(view -> {
            if (dialogDocumentNorms != null && dialogDocumentNorms.isShowing()) {
                dialogDocumentNorms.dismiss();
            }
            if (cbIAagreeDocumentsNorms.isChecked()) {
                cbIAagreeDocumentsNorms.setChecked(false);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void callImageUpload() {

        if (cbIAagreeDocumentsNorms.isChecked()) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_upload_photo);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimationLRS);

            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);

            final ImageButton imgbtn_back = dialog.findViewById(R.id.imgbtn_back);
            final CustomButton btnphoto = dialog.findViewById(R.id.btntake_photo);
            final CustomButton btnpdf = dialog.findViewById(R.id.btnchoose_photo);

            btnphoto.setText("Choose Photo");
            btnpdf.setText("Upload PDF");

            imgbtn_back.setOnClickListener(v -> {
                editor.putString("pdf", null);
                editor.putString("fileUri", null);
                editor.apply();
                dialog.dismiss();
            });

            dialog.findViewById(R.id.btntake_photo).setOnClickListener(v -> {
                dialog.dismiss();
                callImage();
            });

            dialog.findViewById(R.id.btnchoose_photo).setOnClickListener(v -> {
                dialog.dismiss();
                pdfIntent();
            });
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

        } else {
            Toast.makeText(activity, getResources().getString(R.string.kindly_read_the_document_norms), Toast.LENGTH_SHORT).show();
        }
    }

    private void callImage() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_upload_photo);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimationLRS);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        final ImageButton imgbtn_back = dialog.findViewById(R.id.imgbtn_back);

        imgbtn_back.setOnClickListener(v -> {

            editor.putString("pdf", null);
            editor.putString("fileUri", null);
            editor.apply();

            dialog.dismiss();
        });

        dialog.findViewById(R.id.btntake_photo).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (DocumentActivity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && DocumentActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && DocumentActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss();
                    cameraIntent();
                } else {
                    dialog.cancel();
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
                }
            } else {
                dialog.dismiss();
                cameraIntent();
            }
        });

        dialog.findViewById(R.id.btnchoose_photo).setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (DocumentActivity.this.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss();
                    galleryIntent();
                } else {
                    dialog.cancel();
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 10);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (DocumentActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                        galleryIntent();
                    } else {
                        dialog.cancel();
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
                    }
                } else {
                    dialog.dismiss();
                    galleryIntent();
                }
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }


    @SuppressLint("IntentReset")
    private void pdfIntent() {
         /*Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);*/
        Intent intent = new Intent();
        //intent.setType("image/*");
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "select"), PICK_PDF_REQUEST);
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        fileUri = DocumentActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Log.e(TAG, "cameraIntent:fileUri~~~~ " + fileUri);

        editor.putString("photo", String.valueOf(fileUri));
        editor.apply();

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
    }

    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);

    }

    @SuppressLint("Range")
    public String getImagePath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = DocumentActivity.this.getContentResolver().query(uri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = DocumentActivity.this.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            assert cursor != null;
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            System.out.println("path111:" + path);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getPathOfImage(Uri uri) {

        String wholeID = DocumentsContract.getDocumentId(uri);
        System.out.println("WholeId:" + wholeID);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = DocumentActivity.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
        String filePath = "";
        assert cursor != null;
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        System.out.println("File Path1:" + filePath);
        cursor.close();
        return filePath;
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            @SuppressLint("Recycle")
            Cursor cursor = DocumentActivity.this.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    public String getExtension(String fileName) {
        String encoded;
        try {
            encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = fileName;
        }

        return MimeTypeMap.getFileExtensionFromUrl(encoded).toLowerCase();
    }

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(url));
        }

       /* ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String types = mime.getExtensionFromMimeType(cR.getType(Uri.parse(url)));
        out.println("~~~~getMimeType~~~~types~"+types);*/
        out.println("~~~~getMimeType~~~~~" + url + "~~~~" + type);
        return type;
    }

    private String getRealPathFromURI1(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.i("encodedImage", encodedImage);
        return encodedImage;
    }


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivity Result----", requestCode + String.valueOf(resultCode) + data);
        if (resultCode == RESULT_OK) {

            imgUploaded = true;

            if (requestCode == CAMERA_IMAGE_REQUEST) {  // img taken from camera

                Log.i(TAG, "onActivityResult: insideer-->" + prefs.getString("photo", null));

                fileUri = Uri.parse(prefs.getString("photo", null));
                if (fileUri != null) {
                    if (data != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            final Uri data1 = data.getData();
                            if (data1 != null) {
                                if (!data.equals("null")) {
                                    finalFile = new File(Objects.requireNonNull(data1.getPath()));
                                    picturePath = data1.getPath();
                                } else {
                                    picturePath = getRealPathFromURI(fileUri);
                                    finalFile = new File(picturePath);
                                }
                            } else {
                                picturePath = getRealPathFromURI(fileUri);
                                Log.i(TAG, "onActivityResult: insideer-->" + picturePath);
                                finalFile = new File(picturePath);
                            }

                        } else {
                            // in android version lower than M your method must work
                            picturePath = getRealPathFromURI(fileUri);
                            Log.i(TAG, "onActivityResult: insideer-->" + picturePath);
                            finalFile = new File(picturePath);
                        }
                    } else {
                        picturePath = getRealPathFromURI(fileUri);
                        Log.i(TAG, "onActivityResult: insideer-->" + picturePath);
                        finalFile = new File(picturePath);
                    }


                    //   File imageZipperFile=new ImageZipper(getActivity()).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToFile(finalFile);
                    Bitmap photo = null;
                    try {
                        photo = new ImageZipper(this).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToBitmap(finalFile);
                        fileBinaryData = getStringImage(photo);
                        Random r = new Random();
                        int i1 = r.nextInt(80 - 65) + 65;
                        //strProPic = String.valueOf(i1) + ".png";
                        try {
                            ExifInterface exifObject = new ExifInterface(picturePath);
                            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                            //Bitmap imageRotate = rotateBitmap(photo, orientation);
                            //iv_manage_view.setImageURI(fileUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String ss = prefs.getString("siaStatus", "");
                        out.println("sia status is " + ss + " ~~~ " + siaPosition);
                        out.println("image path " + finalFile.getAbsolutePath());

                        documentLists.get(siaPosition).setSelectedImg(fileUri);
                        adapter.onSelect(siaPosition,fileUri , ss);

                        /*if (!ss.equals("disable")) {
                            adapter.onSelect(siaPosition, fileUri);
                        } else {
                            callImageUploads(fileBinaryData, "image");
                        }*/


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (requestCode == PICK_PDF_REQUEST && data != null && data.getData() != null) { // pdf take from gallery
                Uri selectedImage = data.getData();

                // String filePath = FilePath.getPath(this, selectedImage);
                fileBinaryData = getStringPdf(selectedImage);

                out.println("sia status is " + siaPosition);

                String ss = prefs.getString("siaStatus", "");
                documentLists.get(siaPosition).setSelectedImg(fileUri);
                adapter.onSelect(siaPosition,fileUri , ss);
/*
                if (!siaStatus.equals("disable")) {
                    adapter.onSelect(siaPosition, fileUri);
                } else {
                    callImageUploads(fileBinaryData, "pdf");
                }*/

            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) { // img take from gallery img
                String picturePath = null;
                Bitmap photo = null;
                Uri selectedImage = data.getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final Uri data1 = data.getData();
                    System.out.println("data1-->" + data1);


                    if (data1 != null) {
                        if (!data.equals("null")) {
                            finalFile = new File(Objects.requireNonNull(data1.getPath()));
                            picturePath = data1.getPath();
                        } else {
                            picturePath = getRealPathFromURI(fileUri);
                            finalFile = new File(picturePath);
                        }


                    } else {
                        picturePath = getRealPathFromURI(fileUri);
                        Log.i(TAG, "onActivityResult: insideer-->" + picturePath);
                        finalFile = new File(picturePath);
                    }
                    //  picturePath = getImagePath(data1);
                    System.out.println("mSelectedFissslssePath res~~~~~~~~~" + picturePath);
                } else {
                    if (Build.VERSION.SDK_INT >= 19) {
                        if (selectedImage != null && !selectedImage.toString().equals("null")) {
                            System.out.println("greater 19:" + "kitkat");
                            picturePath = getRealPathFromURI1(selectedImage);
                            System.out.println("mSelectedFissslssePath res" + picturePath);

                        } else {
                            System.out.println("greater 19:" + "not kitkat");
                            picturePath = getPathOfImage(selectedImage);
                            System.out.println("mSelectedFissslePath res" + picturePath);
                        }
                    }
                }

                assert picturePath != null;
                File finalFile = new File(picturePath);
                try {
                    AssetFileDescriptor parcelFileDescriptor = getContentResolver().openAssetFileDescriptor(selectedImage, "r", null);

                    FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                    File fileDescriptor = new File(getCacheDir(), finalFile.getName());
                    FileOutputStream outputStream = new FileOutputStream(fileDescriptor);
                    IOUtils.copyStream(inputStream, outputStream);

                    photo = new ImageZipper(this).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToBitmap(fileDescriptor);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri filePath = data.getData();
                assert photo != null;
                fileBinaryData = getStringImage(photo);
                strProPic = finalFile.getName();

                if (strProPic.equals("")) {
                    Random r = new Random();
                    int i1 = r.nextInt(80 - 65) + 65;
                    strProPic = String.valueOf(i1) + ".png";
                }

                out.println("sia status is " + siaPosition);

                String ss = prefs.getString("siaStatus", "");

                documentLists.get(siaPosition).setSelectedImg(filePath);
                out.println("img pick from gallery "+ filePath);
                adapter.onSelect(siaPosition,filePath , ss);

                /*if (!siaStatus.equals("disable")) {
                    adapter.onSelect(siaPosition, filePath);
                } else {
                    callImageUploads(fileBinaryData, "image");
                }*/

                //remove sikka
               /* if (bDocument){
                    callImageUploadPathDetails(fileBinaryData, strProPic);
                }
                else
                {
                    callReUploadDetails(fileBinaryData, strProPic);
                }

                editor.putString(strDocId, String.valueOf(filePath));
                editor.apply();
                adapter.notifyDataSetChanged();*/


            } else {
                Toast.makeText(getApplicationContext(), R.string.pic_failed_error,
                        Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            imgUpdated = false;
            imgUploaded = false;
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "onRequestPermissionsResult: -->" + requestCode + "~~~~" +
                Arrays.toString(permissions) + "~~~" + Arrays.toString(grantResults));

        if (requestCode <= 5) {
            // if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                Toast.makeText(DocumentActivity.this, "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode >= 7) {
            //if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pdfIntent();
            } else {
                Toast.makeText(DocumentActivity.this, "Please provide permission to use gallery", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 10) {
            //if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(DocumentActivity.this, "Please provide media and photos permission to use gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: -->" + requestCode + "~~~~" + Arrays.toString(permissions) + "~~~" + Arrays.toString(grantResults));
        if (requestCode <= 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                Toast.makeText(DocumentActivity.this, R.string.provide_camera_permission, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode >= 7) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pdfIntent();
            } else {
                Toast.makeText(DocumentActivity.this, R.string.provide_gallery_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void callAppSettings() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("type", "driver");
        map.put("mode", "api");

        progressBar.setVisibility(View.VISIBLE);

        Call<DocumentListData> call2 = apiInterface.doCallViewManageDocument(map);

        call2.enqueue(new Callback<DocumentListData>() {

            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<DocumentListData> call, @NonNull Response<DocumentListData> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    //{"message":"No records Found."}
                    //"message":"Documents listed successfully."

                    if (response.body().getMessage().equals("Documents listed successfully.")) {

                        documentLists = Objects.requireNonNull(response.body()).getData();

                        if (documentLists.size() > 0) {

                            progressBar.setVisibility(View.GONE);
                            rcvDocument.setVisibility(View.VISIBLE);
                            btnConfitmDocumetnUpload.setVisibility(View.VISIBLE);
                            llNoRecordsFound.setVisibility(View.GONE);

                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            rcvDocument.setLayoutManager(horizontalLayoutManager);

                            adapter = new DocumentAdapter(getApplicationContext(), baseActivity, documentLists, "appsetting", bDocument);
                            rcvDocument.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else {

                            Toast.makeText(activity, getString(R.string.document_empty), Toast.LENGTH_SHORT).show();
                            rcvDocument.setVisibility(View.GONE);
                            llNoRecordsFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        rcvDocument.setVisibility(View.GONE);
                        llNoRecordsFound.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                if (response.code() == RESPONSECODE_500 || response.code() == RESPONSECODE_501 || response.code() == RESPONSECODE_401) {

                    if (response.body().getMessage() != null)
                        //  baseActivity.callDialogViews(DocumentActivity.this, map, response.body().getMessage());
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<DocumentListData> call, @NonNull Throwable t) {
                call.cancel();
                if (BuildConfig.DEBUG) Log.e(TAG, "onFailure: ~~~~~" + t.toString());
                Toast.makeText(activity, t.toString(), Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                rcvDocument.setVisibility(View.GONE);
                //  btnConfitmDocumetnUpload.setVisibility(View.GONE);
                llNoRecordsFound.setVisibility(View.VISIBLE);

            }
        });

    }

    private void callViewDocument() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);

        progressBar.setVisibility(View.VISIBLE);
        Call<DocumentViewModel> call2 = apiInterface.doDocumentViewDetails(map);

        call2.enqueue(new Callback<DocumentViewModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<DocumentViewModel> call, @NonNull Response<DocumentViewModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (response.body() != null && response.body().getMessage().equals("success")) {

                        documentLists = Objects.requireNonNull(response.body()).getResult();

                        if (documentLists.size() > 0) {

                            progressBar.setVisibility(View.GONE);
                            rcvDocument.setVisibility(View.VISIBLE);
                            btnConfitmDocumetnUpload.setVisibility(View.VISIBLE);
                            llNoRecordsFound.setVisibility(View.GONE);
                            adapter = new DocumentAdapter(getApplicationContext(), baseActivity, documentLists, "viewdocument", bDocument);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            rcvDocument.setLayoutManager(horizontalLayoutManager);
                            rcvDocument.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (!cbIAagreeDocumentsNorms.isChecked()) {
                                cbIAagreeDocumentsNorms.setChecked(true);
                                cbIAagreeDocumentsNorms.setClickable(false);
                                cbIAagreeDocumentsNorms.setFocusable(false);
                                cbIAagreeDocumentsNorms.setFocusableInTouchMode(false);
                                cbIAagreeDocumentsNorms.setEnabled(false);
                            }

                        } else {

                            //  baseActivity.showSnackbar(getApplicationContext(), contentView, getString(R.string.document_empty), "f");
                            Toast.makeText(activity, getString(R.string.document_empty), Toast.LENGTH_SHORT).show();
                            rcvDocument.setVisibility(View.GONE);
                            // btnConfitmDocumetnUpload.setVisibility(View.GONE);
                            llNoRecordsFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
                if (response.code() == RESPONSECODE_500 || response.code() == RESPONSECODE_501 || response.code() == RESPONSECODE_401) {

                    if (response.body().getMessage() != null)
                        // baseActivity.callDialogViews(DocumentActivity.this, map, response.body().getMessage());
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<DocumentViewModel> call, @NonNull Throwable t) {
                call.cancel();
                if (BuildConfig.DEBUG) Log.e(TAG, "onFailure: ~~~~~" + t.toString());
                // baseActivity.callretrofitFailure(getApplicationContext(), contentView, t.toString());
                Toast.makeText(activity, t.toString(), Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                rcvDocument.setVisibility(View.GONE);
                // btnConfitmDocumetnUpload.setVisibility(View.GONE);
                llNoRecordsFound.setVisibility(View.VISIBLE);

            }
        });
    }

    private void callImageUploads(String fileBinaryData, String type) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("image", fileBinaryData);
        map.put("type", type);

        Log.e("callImageUpload", "~~~" + map);

        Call<UploadModel> call2 = apiInterface.doImageUploadPathDetails(map);
        progressBar.setVisibility(View.VISIBLE);

        call2.enqueue(new Callback<UploadModel>() {
            @Override
            public void onResponse(@NonNull Call<UploadModel> call, @NonNull Response<UploadModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("image updated successfully")) {

                        imgUriUpdate = false;

                        System.out.println("date-->" + strDate);

                        if (strDate == null) {
                            strDate = prefs.getString("date", "");
                        }

                        if (bDocument == null) {
                            bDocument = prefs.getBoolean("bDoc", true);
                        }

                        if (strDateStatus == null) {
                            strDateStatus = prefs.getString("date_status", "0");
                        }

                        if (bDocument) {
                            if (strDateStatus.equals("1")) {
                                editor.putString(driverId + strDocId, response.body().getResult());
                                editor.putString("date" + driverId + strDocId, strDate);
                                editor.apply();
                                adapter.notifyDataSetChanged();
                            } else {
                                callImageUploadPathDetails(response.body().getResult(), strDate, type);
                            }
                        } else {
                            callReUploadDetails(response.body().getResult(), strDate, type);
                           /* if (strDateStatus.equals("1")) {
                                editor.putString(driverId + strId + strDocId, response.body().getResult());
                                editor.apply();
                                adapter.notifyDataSetChanged();
                            } else {
                                callReUploadDetails(response.body().getResult(), strDate, type);
                            }*/
                        }

                        Log.e(TAG, "onResponse: ~~~~~~~~~~~");
                    } else {
                        Toast.makeText(activity, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    System.out.println("``````");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadModel> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    //callImageUploa: ~~~{driver_id=10, document=uploads/DriverProof/1611840534.jpg, expiry_date=, document_type_ar=شهادة السجل التجاري,
    // document_id=6, document_type=Commercial Registration Certificate}

    //{driver_id=10, document=uploads/DriverProof/1611840623.jpg, expiry_date=, id=6, document_id=1}

    //{driver_id=12, document=uploads/DriverProof/1611843235.jpg, expiry_date=, document_type_ar=شهادة السجل التجاري,
    // document_id=1, document_type=Commercial Registration Certificate}

    //{driver_id=12, document=uploads/DriverProof/1611843283.jpg, expiry_date=, document_type_ar=شهادة السجل التجاري,
    // document_id=8, document_type=Commercial Registration Certificate}

    private void callImageUploadPathDetails(String fileBinaryData, String date, String type) {

        // strDocId = String.valueOf(map.get("id"));
        if (strDocId == null) {
            strDocId = prefs.getString("id", null);
        }

        if (strDocName == null) {
            strDocName = prefs.getString("documentname", "");
        }

        if (strDocNameAr == null) {
            strDocNameAr = prefs.getString("documentname_ar", null);
        }

        if (date == null) {
            date = prefs.getString("date", "");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("document", fileBinaryData);
        map.put("document_id", strDocId);
        map.put("document_type", strDocName);
        map.put("document_type_ar", strDocName);
//        map.put("document_type_ar", strDocNameAr);
        map.put("driver_id", driverId);
        map.put("type", type);
        map.put("expiry_date", dateToSend);
        map.put("expiry_status", dateStatusToSend);

        if (!siaStatus.equals("disable")) {
            map.put("licence_number", siaIdToSend);
        }

        Log.e("t", "callImageUploadPathDetails" + "~~~" + map);

        Call<UploadDocModel> call2 = apiInterface.doDocumentUploadDetails(map);

        progressBar.setVisibility(View.VISIBLE);

        String finalDate = date;

        call2.enqueue(new Callback<UploadDocModel>() {

            @Override
            public void onResponse(@NonNull Call<UploadDocModel> call, @NonNull Response<UploadDocModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Successfully updated.")) {

                        //remove sikka
                        // editor.putString(strDocId, String.valueOf(fileUri));
                        editor.putString(driverId + strDocId, response.body().getUploadDocData().getDocument());
                        editor.putString("date" + driverId + strDocId, finalDate);
                        editor.putString("type" + driverId + strDocId, type);
                        editor.apply();

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        } else {
                            callViewDocument();
                        }

                        Log.e(TAG, "onResponse: ~~~~~~~~~~~");
                    } else {
                        Toast.makeText(activity, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    System.out.println("``````");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadDocModel> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private void callReUploadDetails(String fileBinaryData, String date, String type) {

        // strId = String.valueOf(map.get("id"));
        // strDocId = String.valueOf(map.get("document"));

        if (strDocId == null) {
            strDocId = prefs.getString("document", null);
        }

        if (strId == null) {
            strId = prefs.getString("id", null);
        }

        if (strDocName == null) {
            strDocName = prefs.getString("documentname", "");
        }

        if (strDocNameAr == null) {
            strDocNameAr = prefs.getString("documentname_ar", null);
        }

        if (date == null) {
            date = prefs.getString("date", "");
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("document", fileBinaryData);
        map.put("driver_id", driverId);

//        map.put("expiry_date", date);
        map.put("document_id", strDocId);
        map.put("id", strId);
        map.put("type", type);
        map.put("expiry_date", dateToSend);
        map.put("expiry_status", dateStatusToSend);

        if (!siaStatus.equals("disable")) {
            map.put("licence_number", siaIdToSend);
        }

        Log.e("callReUploadDetails", "~~~" + map);
        Call<ReUploadModel> call2 = apiInterface.doDocumentReUploadDetails(map);
        progressBar.setVisibility(View.VISIBLE);
        call2.enqueue(new Callback<ReUploadModel>() {
            @Override
            public void onResponse(@NonNull Call<ReUploadModel> call, @NonNull Response<ReUploadModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Successfully updated.")) {

                        //remove sikka

                      /*  editor.putString(strDocId, String.valueOf(fileUri));
                        editor.apply();*/
                        //adapter.notifyDataSetChanged();

                        callViewDocument();


                        Log.e(TAG, "onResponse: ~~~~~~~~~~~");
                    } else {
                        Toast.makeText(activity, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    System.out.println("``````");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReUploadModel> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private void getDriverStatusFB() {

        hideProgressDialog();

      /*  if (driverStatusCheckinoutDatabaseRef != null) {
            driverStatusCheckinoutDatabaseRef.removeEventListener(driverStatusCheckinoutValueEventListener);
        }*/
        driverStatusCheckinoutDatabaseRef = FirebaseDatabase.getInstance().getReference().child("documents_providers").child(driverId);


        driverStatusCheckinoutValueEventListener = driverStatusCheckinoutDatabaseRef.orderByValue().limitToLast(10).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imgUriUpdate = false;

                if (dataSnapshot.child("Document_status").getValue() != null) {
                    strDocStatusFB = String.valueOf(dataSnapshot.child("Document_status").getValue());
                    out.println("status-->" + strDocStatusFB);
                    if (strDocStatusFB.equals("1")) {
                        bDocument = false;
                        callViewDocument();
                    } else {
                        bDocument = true;
                        callAppSettings();
                    }
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("documents_providers").child(driverId).child("Document_status").setValue("0");
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getDriverCheckinchoutFB~Error~", String.valueOf(databaseError));
            }
        });

    }

    @Override
    public void onBackPressed() {
        imgUriUpdate = false;
        out.println("document back btn pressed "+ imgUriUpdate);

        if (strFrom != null) {
            if (strFrom.equals("login")) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                overridePendingTransition(R.anim.fade_in_speed, R.anim.fade_out_speed);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (strFrom.equals("vehicle")) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                overridePendingTransition(R.anim.fade_in_speed, R.anim.fade_out_speed);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            overridePendingTransition(R.anim.fade_in_speed, R.anim.fade_out_speed);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgUriUpdate = false;
        out.println("Document activity "+ imgUriUpdate);
    }

    @Override
    public void onSuccess(HashMap<String, Object> map) {
        strDetails = String.valueOf(map.get("name"));
        if (strDetails.equals("update")) {
            callViewDocument();
        } else {
            strDate = String.valueOf(map.get("date"));
            strDateStatus = String.valueOf(map.get("date_status"));
            siaStatus = String.valueOf(map.get("onClickedSiaStatus"));
            editor.putString("siaStatus", siaStatus);
            siaPosition = Integer.valueOf(Objects.requireNonNull(map.get("onClickedPosition")).toString());
            siaIdToSend = String.valueOf(map.get("licence_number"));
            dateToSend = String.valueOf(map.get("expiry_date"));
            dateStatusToSend = String.valueOf(map.get("expiry_status"));
            String checkSia = String.valueOf(map.get("siaUpdate"));


            System.out.println("strDates123--->" + strDate);
            System.out.println("sia position --->" + siaPosition);
            System.out.println("onClickedSiaStatus 3--->" + siaStatus);

            if (bDocument) {
                out.println("document status " + bDocument);

                String langId = String.valueOf(map.get("status"));

                if (langId.equals("1")) {
                    strDocId = String.valueOf(map.get("id"));
                    strDocName = String.valueOf(map.get("documentname"));
                    strDocNameAr = String.valueOf(map.get("documentname_ar"));

                    if (checkSia.equals("true")) {
                        callImageUploads(fileBinaryData, "image");
                    } else {
                        callImageUpload();
                    }

                } else {
                    //baseActivity.showSnackbar(getApplicationContext(), contentView, getString(R.string.document_inactive_state), "f");
                    Toast.makeText(activity, getString(R.string.document_inactive_state), Toast.LENGTH_SHORT).show();
                }

            } else {

                String langId = String.valueOf(map.get("approvedstatus"));

                out.println("lang Id " + langId);

                strId = String.valueOf(map.get("id"));
                strDocId = String.valueOf(map.get("document"));
                strDocName = String.valueOf(map.get("documentname"));
                strDocNameAr = String.valueOf(map.get("documentname_ar"));

                //Log.e(TAG, "onSuccess: ~~~~~~"+strId+"~~~~"+strDocId+"~~~~~");
                //0 - pending 1 - Accept 2 - Reject
                switch (langId) {
                    case "0":
                    case "2":
                        if (!imgUploaded) {  // 1st time img upload 2nd it call submit
                            imgUploaded = true;
                            callImageUpload();
                        } else {
                            imgUploaded = false;
                            callImageUploads(fileBinaryData, "image");
                        }
                       /* if (checkSia.equals("true")) {
                            callImageUploads(fileBinaryData, "image");
                        } else {
                            callImageUpload();
                        }*/
                        break;
                    case "3":
                        if (!imgUploaded) {  // 1st time img upload 2nd it call submit
                            imgUploaded = true;
                            callImageUpload();
                        } else {
                            imgUploaded = false;
                            callImageUploads(fileBinaryData, "image");
                        }
                        break;
                    case "1":
                        // goToView(strDocName,strDocNameAr,strDocId);
                        break;
                }

                /*if (!langId.equals("1")) {
                    strId = String.valueOf(map.get("id"));
                    strDocId = String.valueOf(map.get("document"));
                    strDocName = String.valueOf(map.get("documentname"));
                    strDocNameAr = String.valueOf(map.get("documentname_ar"));
                    callImageUpload();
                } else {
                    // baseActivity.showSnackbar(getApplicationContext(), contentView, getString(R.string.document_inactive_state), "f");
                }*/
            }
        }
    }

    private void goToView(String doc_name, String doc_name_ar, String doc_proof) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, doc_proof);
        args.putString(ARG_PARAM2, doc_name);
        args.putString(ARG_PARAM3, doc_name_ar);
        args.putString(ARG_PARAM4, "activity");
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MANAGE_VIEW_DOCUMENT, DocumentActivity.this, args,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

    public String getStringPdf(Uri filepath) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            inputStream = getContentResolver().openInputStream(filepath);

            byte[] buffer = new byte[1024];
            byteArrayOutputStream = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        byte[] pdfByteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(pdfByteArray, Base64.DEFAULT);
    }

    @Override
    public void onDatePickerClick(int position, String siaNo) {
        openDatePicker(position, siaNo);
        out.println("selected position is "+ position + " ~~ " + siaNo);
    }

    private void openDatePicker(int position, String siaNo) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DocumentActivity.this, (datePicker, year1, monthOfYear, dayOfMonth) ->
        {
            String curDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            out.println("selected date "+ curDate);

            documentLists.get(position).setExpiryDate(curDate);
            documentLists.get(position).setLicenseNo(siaNo);

            out.println("date selected is "+ documentLists.get(position).getSelected_date() + " ~~ " + documentLists.get(position).getLicenseNo());

            /*editor.putString("imgUpload", "true");
            editor.apply();*/

            adapter.onDateSelected(position, documentLists);
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
}