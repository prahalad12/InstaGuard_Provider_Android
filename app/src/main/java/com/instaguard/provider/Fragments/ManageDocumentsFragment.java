package com.instaguard.provider.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.ManageDocumentAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Helpers.imagezipper.ImageZipper;
import com.instaguard.provider.Models.DocumentViewData_;
import com.instaguard.provider.Models.DocumentViewModel_;
import com.instaguard.provider.Models.ProfileModel;
import com.instaguard.provider.Models.ReUploadModel_;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.UploadDocModel_;
import com.instaguard.provider.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM1;
import static com.instaguard.provider.Fragments.ManageViewDocumentsFragment.ARG_PARAM2;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;
import static java.lang.System.out;

public class ManageDocumentsFragment extends Fragment implements ManageDocumentAdapter.SuccessResponse {

    View view;

    Toolbar toolbarSignin;
    ImageButton imgbtnBack;
    ImageView ivCamera;
    SimpleDraweeView iv_manage_view;
    CustomButton btnSelectDocument, btnUpdateSettings;
    SharedPreferences.Editor putUpdateDocument;
    SharedPreferences getUpdateDocument;

    private static final int CAMERA_IMAGE_REQUEST = 0;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    String fileBinaryData, strProPic;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    public String TAG = "SettingFragment";
    private Uri fileUri;

    RecyclerView rvManageDocument;
    RelativeLayout rlEmptyview;
    CustomEditText edtDocumentName;
    String userId, userDocuPic, userAccessToken;

    DatabaseReference providerApproveStatusDatabaseBRef;
    ValueEventListener providerApproveStatusValueEventListener;
    String strApproveStatus, strDocumentPic;

    ManageDocumentAdapter adapter;

    LinearLayout llViewDocumentLyt;
    CustomTextView txtTitle;

    String doc_id, doc_type, strUploadCountvalue, strClickValue = "0";

    Boolean click = false;
    FrameLayout contentView;

    public ManageDocumentsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static com.instaguard.provider.Fragments.ManageDocumentsFragment newInstance(String param1, String param2) {
        com.instaguard.provider.Fragments.ManageDocumentsFragment fragment = new com.instaguard.provider.Fragments.ManageDocumentsFragment();
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

    @SuppressLint({"CommitPrefEdits", "LongLogTag"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_document, container, false);

        out.println("entered in document fragment ");

        putUpdateDocument = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        //Fresco.initialize(getActivity());
        getUpdateDocument = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = getUpdateDocument.getString(Utils.SP_DRIVERID, null);
        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        userDocuPic = getUpdateDocument.getString(Utils.SP_DRIVER_DOC_PIC, null);
        Log.e("UserId~~ManageDocumentsFragment~~", userId);

        toolbarSignin = view.findViewById(R.id.toolbar_setting);
        imgbtnBack = view.findViewById(R.id.imgbtn_back);
        iv_manage_view = view.findViewById(R.id.iv_manage_view);

        btnUpdateSettings = view.findViewById(R.id.btn_updatesettings);
        btnSelectDocument = view.findViewById(R.id.btn_select_document);

        ivCamera = view.findViewById(R.id.iv_camera);
        contentView = view.findViewById(R.id.frame_editprofile);

        rvManageDocument = view.findViewById(R.id.rv_manage_document);
        rlEmptyview = view.findViewById(R.id.rl_emptyview);

        edtDocumentName = view.findViewById(R.id.edt_document_name);

        llViewDocumentLyt = view.findViewById(R.id.ll_view_document_lyt);
        txtTitle = view.findViewById(R.id.txt_title);

        rvManageDocument.setNestedScrollingEnabled(false);

        ManageDocumentAdapter.successResponse = this;

        btnUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CallUpdateProfilepicDetails(fileBinaryData, strProPic);
            }
        });

        btnSelectDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (strUploadCountvalue!=null) {
                    if (Integer.parseInt(strUploadCountvalue) >= 10) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.reached_upload_limit), Toast.LENGTH_SHORT).show();
                    } else {
                        String edtDocum = edtDocumentName.getText().toString().trim();
                        if (edtDocum.length() == 0) {
                            edtDocumentName.setError(getResources().getString(R.string.error_required));
                        } else {
                            strClickValue = "1";
                            showCustomDialog();
                        }
                    }
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


        //  CallViewProfileDetails();
        callViewDocumentsDetails();

        iv_manage_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strDocumentPic != null) {
                    Log.e("userDocuPic~~~", strDocumentPic);
                    //goToView(doc_name, doc_proof);
                } else {
                    Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });

        llViewDocumentLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                if (rvManageDocument.getVisibility() == View.GONE) {
                    rvManageDocument.setVisibility(View.VISIBLE);
//                    ViewAnimation.expand(rvManageDocument);
                    txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black, 0);
                } else {
//                    ViewAnimation.collapse(rvManageDocument);
                    rvManageDocument.setVisibility(View.GONE);
                    txtTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black, 0);
                }
            }
        });

        onBackButton(view);

        return view;
    }


    @SuppressLint("LongLogTag")
    private void callViewDocumentsDetails() {
        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        Log.i("callViewDocumentsDetails", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DocumentViewModel_> call2 = apiInterface.doGetDocumentsviewDetails(userAccessToken, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<DocumentViewModel_>() {
            @Override
            public void onResponse(@NonNull Call<DocumentViewModel_> call, @NonNull Response<DocumentViewModel_> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (response.body().getMessage().equals("success")) {
                        String rejectStatus = String.valueOf(response.body().getRejectStatus());

                        List<DocumentViewData_> getve = Objects.requireNonNull(response.body()).getResult();
                        Log.i("sizxx", "onResponse: getve-->" + getve.size());
                        strUploadCountvalue = String.valueOf(getve.size());
                        hideProgressDialog();
                        if (getve.size() > 0) {
                            llViewDocumentLyt.setVisibility(View.VISIBLE);
                            rlEmptyview.setVisibility(View.GONE);
                            //  rvManageDocument.setVisibility(View.VISIBLE);
                            adapter = new ManageDocumentAdapter(getActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvManageDocument.setLayoutManager(horizontalLayoutManager);
                            rvManageDocument.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            hideProgressDialog();
                        } else {
                            llViewDocumentLyt.setVisibility(View.GONE);
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvManageDocument.setVisibility(View.GONE);
                        }

                    } else {
                        hideProgressDialog();
                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvManageDocument.setVisibility(View.GONE);
                        llViewDocumentLyt.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DocumentViewModel_> call, Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure:callViewDocumentsDetails~~~~~~~~~" + t);
                call.cancel();
            }
        });


    }

    private void CallViewProfileDetails() {
        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("token", userAccessToken);
        Log.i("CallViewProfileDetails", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ProfileModel> call2 = apiInterface.doGetprofileDetails(map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Profile listed successfully.")) {

                        try {
                            strDocumentPic = Objects.requireNonNull(response.body()).getProfileData().getDriverCertificate();
                            strDocumentPic = strDocumentPic.replaceAll(" ", "%20");
                            if (strDocumentPic != null) {
                                Log.e("userDocuPic~~~", strDocumentPic);
                                iv_manage_view.setImageURI(Utils.BASE_URL_IMAGE_UPLOAD + strDocumentPic);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                      /*  List<String> getve = response.body().getProfileData().getDriverCertificate();
                        if(getve!=null){
                        if (getve.size() > 0) {
                            rlEmptyview.setVisibility(View.GONE);
                            rvManageDocument.setVisibility(View.VISIBLE);
                            adapter = new ManageDocumentAdapter(getActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvManageDocument.setLayoutManager(horizontalLayoutManager);
                            rvManageDocument.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvManageDocument.setVisibility(View.GONE);
                        }} else {
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvManageDocument.setVisibility(View.GONE);
                        }
*/

                    } else {
                        callRefreshToken("viewprofile");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshToken("viewprofile");
                } else if (response.code() == RESPONSECODE_500) {
                    callRefreshToken("viewprofile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivity Result----", String.valueOf(requestCode) + resultCode + data);
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_IMAGE_REQUEST) {

                Log.i(TAG, "onActivityResult: insideer-->" + getUpdateDocument.getString("file", null));

                fileUri = Uri.parse(getUpdateDocument.getString("file", null));
                // Log.i(TAG, "onActivityResult: insideer-->" + fileUri);

                if (fileUri != null) {
                    Uri uri = fileUri;
                    String picturePath = getRealPathFromURI(uri);
                    Log.i(TAG, "onActivityResult: insideer-->" + picturePath);
                    File finalFile = new File(picturePath);
                    //   File imageZipperFile=new ImageZipper(getActivity()).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToFile(finalFile);
                    Bitmap photo = null;
                    try {
                        photo = new ImageZipper(getActivity()).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToBitmap(finalFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileBinaryData = getStringImage(photo);
                    Random r = new Random();
                    int i1 = r.nextInt(80 - 65) + 65;
                    strProPic = i1 + ".png";
                    try {
                        ExifInterface exifObject = new ExifInterface(picturePath);
                        int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Bitmap imageRotate = rotateBitmap(photo, orientation);
                        iv_manage_view.setImageURI(fileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (strClickValue.equals("1")) {
                        CallUpdateProfilepicDetails(fileBinaryData, strProPic);
                    } else if (strClickValue.equals("2")) {
                        callReUpdateProfilepic(fileBinaryData, strProPic);
                    }

                }
               /* }else{
                    // in android version lower than M your method must work
                }*/

            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                String picturePath = null;
                Bitmap photo = null;
                Uri selectedImage = data.getData();
                if (Build.VERSION.SDK_INT >= 19) {
                    if (selectedImage != null && !selectedImage.toString().equals("null")) {
                        System.out.println("greater 19:" + "kitkat");
//                        picturePath = getImagePath(selectedImage);
                        picturePath = getRealPathFromURI1(selectedImage);
                        System.out.println("mSelectedFissslssePath res" + picturePath);
                    } else {
                        System.out.println("greater 19:" + "not kitkat");
                        picturePath = getPathOfImage(selectedImage);
                        System.out.println("mSelectedFissslePath res" + picturePath);
                    }
                }
                File finalFile = new File(picturePath);
                try {
                    photo = new ImageZipper(getActivity()).setQuality(10).setMaxWidth(200).setMaxHeight(200).compressToBitmap(finalFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                filePath = data.getData();

                fileBinaryData = getStringImage(photo);
                strProPic = finalFile.getName();
                if (strProPic.equals("")) {
                    Random r = new Random();
                    int i1 = r.nextInt(80 - 65) + 65;
                    strProPic = i1 + ".png";
                }

                if (strClickValue.equals("1")) {
                    CallUpdateProfilepicDetails(fileBinaryData, strProPic);
                } else if (strClickValue.equals("2")) {
                    callReUpdateProfilepic(fileBinaryData, strProPic);
                }

            } else {
                Toast.makeText(getActivity(), R.string.pic_failed_error,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }


    public String getImagePath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            System.out.println("path111:" + path);
            cursor.close();
        } catch (Exception e) {

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

        Cursor cursor = getActivity().getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

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
            Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(requireActivity());
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
        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strClickValue = "0";
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btntake_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                        cameraIntent();
                    } else {
                        dialog.cancel();
                        getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
                    }
                } else {
                    dialog.dismiss();
                    cameraIntent();

                }
            }
        });

        dialog.findViewById(R.id.btnchoose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                        galleryIntent();
                    } else {
                        dialog.cancel();
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 6);
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

    private void showNormalAlertDialog(Activity self, String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(self,R.style.AppCompatAlertDialogStyle);
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

    private void galleryIntent() {
        /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_IMAGE_REQUEST);*/
        @SuppressLint("IntentReset")
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image File name");
        fileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        Log.e(TAG, "cameraIntent: ~~~~~~~~~~~~~~~~~~" + fileUri);
        putUpdateDocument.putString("file", String.valueOf(fileUri));
        putUpdateDocument.apply();

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);

    }

    /**
     * Create a File for saving an image
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");

        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        /**Create a media file name*/
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                Toast.makeText(getActivity(), "Please provide permission to use camera", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 6) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(getActivity(), "Please provide permission to use gallery", Toast.LENGTH_SHORT).show();
            }
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


    public String getImgPath(Uri uri) {
        String result;
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            Log.i("resultmeeee", result);
            cursor.close();

        }
        return result;

    }


    private void showFileChooser() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.gallery_not_try_again, Toast.LENGTH_LONG).show();
            }
        } else {
            checkPermissionGallery();
        }
    }

    public void checkPermissionGallery() {

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.Permission_necessary);
                    alertBuilder.setMessage(R.string.gallery_access_permission);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

    public void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.Permission_necessary);
                    alertBuilder.setMessage(R.string.camera_permission);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_IMAGE_REQUEST);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_IMAGE_REQUEST);
                }
            }
        }

    }


    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
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
                    if (logout.equalsIgnoreCase("viewprofile")) {
                        CallViewProfileDetails();
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

    @SuppressLint("LongLogTag")
    private void CallUpdateProfilepicDetails(String fileBinaryData, String strProPic) {

        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("file_binary_data", fileBinaryData);
        map.put("certificate", strProPic);
        map.put("proof_name", edtDocumentName.getText().toString().trim());

        Log.e("CallUpdateProfilepicDetails", "~~~" + map);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UploadDocModel_> call2 = apiInterface.doUPdateDocumentDetails("Bearer " + userAccessToken, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<UploadDocModel_>() {
            @Override
            public void onResponse(@NonNull Call<UploadDocModel_> call, @NonNull Response<UploadDocModel_> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("driver Certificate updated successfully.")) {

//                        String profileResponse = Objects.requireNonNull(response.body()).getUploadDocData().getDriverCertificate();
//                        profileResponse = profileResponse.replaceAll(" ","%20");
//                        Log.e("profileResponse",profileResponse);
//
//                        if (selectedImageUri == null) {
//                            imagee = "";
//                            Log.i("ifffff", "ifffffffffff");
//                        } else {
//                            imagee = selectedImageUri.toString();
//                            Log.i("imageeeee", "" + imagee);
//                        }
                       /* String val1 = response.body().getUploadDocData();

                        final GsonBuilder gsonBuilder = new GsonBuilder();
                        final Gson gson = gsonBuilder.create();
asdf
                        TestCase[] testCase = gson.fromJson(val1, TestCase[].class);*/

                        edtDocumentName.setText("");

                        callViewDocumentsDetails();
                        // iv_manage_view.setImageURI(Utils.BASE_URL+profileResponse);

                        showProfileImageUpdateAlertDialog(getActivity(), getString(R.string.edit_settings_image_update_successfully));

                        //once check this state update shareprefer for user details

                    } else {
//                        showSnackbar(getString(R.string.update_not_successfully), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.update_not_successfully), "warning");
                    }

                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshTokens(fileBinaryData, strProPic, "Profile_pic");
                }

            }

            @Override
            public void onFailure(@NonNull Call<UploadDocModel_> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                hideProgressDialog();
                call.cancel();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void callReUpdateProfilepic(String fileBinaryData, String strProPic) {

//        Log.e("callReUpdateProfilepic", "~~~" + userId+"~~~~~"+doc_id+"~~~~~"+doc_type+"~~~~~");

        userAccessToken = getUpdateDocument.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        map.put("id", doc_id);
        map.put("type", doc_type);
        map.put("document_proof", fileBinaryData);
        map.put("certificate", strProPic);
        //  map.put("proof_name", edtDocumentName.getText().toString().trim());

        Log.e("callReUpdateProfilepic", "~~~" + map);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ReUploadModel_> call2 = apiInterface.doReupdateDocumentDetails("Bearer " + userAccessToken, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<ReUploadModel_>() {
            @Override
            public void onResponse(@NonNull Call<ReUploadModel_> call, @NonNull Response<ReUploadModel_> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {

                        callViewDocumentsDetails();

                        showProfileImageUpdateAlertDialog(getActivity(), getString(R.string.edit_settings_image_update_successfully));

                    } else {
//                        showSnackbar(getString(R.string.update_not_successfully), "warning");
                        BaseActivity.showSnackbar(requireActivity(),contentView,getString(R.string.update_not_successfully), "warning");
                    }

                } else if (response.code() == RESPONSECODE_401) {
                    callRefreshTokens(fileBinaryData, strProPic, "Profile_pic");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ReUploadModel_> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                hideProgressDialog();
                call.cancel();
            }
        });

    }

    private void callRefreshTokens(String fileBinaryData, String strProPic, String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
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
                    if (logout.equalsIgnoreCase("Profile_pic")) {
                        CallUpdateProfilepicDetails(fileBinaryData, strProPic);
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

    public void showProfileImageUpdateAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(),
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
                        putUpdateDocument.putString(Utils.SP_DRIVER_DOC_PIC, "null");
                        putUpdateDocument.apply();

                    }
                }).show();
    }


    /*******************************Get Firebase States********************************************/

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
        removeFragment(Utils.FRAGMENT_MANAGEDOCUMENT);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
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
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.App_Font));
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(font, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }*/

    public void hideKeyBoard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void savesharedpreference(String access_token, String token_type, String exp_time) {

        putUpdateDocument.putString(Utils.SP_DRIVER_ACCESSTOKEN, access_token);
        putUpdateDocument.putString(Utils.SP_DRIVER_TOKENTYPE, token_type);
        putUpdateDocument.putString(Utils.SP_DRIVER_TOKENEXPTIME, exp_time);
        putUpdateDocument.apply();
    }


    public void showTimeOut() {
        Context mContext = getActivity();

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
        SharedPreferences preferences = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
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

    //get Approve Status in firebase
    private void getProviderApproveStatusAmount() {
        if (providerApproveStatusDatabaseBRef != null) {
            providerApproveStatusDatabaseBRef.removeEventListener(providerApproveStatusValueEventListener);
        }
        providerApproveStatusDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
        providerApproveStatusValueEventListener = providerApproveStatusDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Object approveStatusObj = dataSnapshot.child("approved").getValue();

                    if (approveStatusObj != null) {
                        strApproveStatus = approveStatusObj.toString();
                    } else {
                        strApproveStatus = "0";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String getRealPathFromURI1(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onSuccess(HashMap<String, Object> map) {
        String click = String.valueOf(map.get("click"));
        String doc_name = String.valueOf(map.get("doc_name"));
        String doc_proof = String.valueOf(map.get("doc_proof"));
        doc_id = String.valueOf(map.get("doc_id"));
        doc_type = String.valueOf(map.get("doc_type"));

        if (click.equals("view")) {
            goToView(doc_name, doc_proof);
        } else if (click.equals("imageUpload")) {
            strClickValue = "2";
            showCustomDialog();
        }
    }

    private void goToView(String doc_name, String doc_proof) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, doc_proof);
        args.putString(ARG_PARAM2, doc_name);
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MANAGE_VIEW_DOCUMENT, requireActivity(), args,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

}
