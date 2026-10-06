package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.DocumentsUpload.DocumentList;
import com.instaguard.provider.Models.DocumentsUpload.ReUploadModel;
import com.instaguard.provider.Models.DocumentsUpload.UploadDocModel;
import com.instaguard.provider.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.DocumentActivity.imgUploaded;
import static com.instaguard.provider.Activities.DocumentActivity.imgUriUpdate;
import static com.instaguard.provider.Activities.DocumentActivity.manager;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyViewHolder> {

    private Context mContext;
    private String type;
    private Boolean bDocument = true;
    public static Boolean imgUpdated = false;
    private List<DocumentList> orderListModel;
    private BaseActivity baseActivity;

    private static int selected_pos, datePos;
    private Uri imgUri;
    private String driverId;
    private boolean isCallFrom = false;
    public static String selectedDate;

    private SharedPreferences getspCommonCurrency, prefs, getData;
    private String strLanguage, strCurrencySymbol, strLanguageShortName;
    private SharedPreferences.Editor editor, setData;
    public static SuccessResponse successResponse;
    public static OnDatePickerClickListener datePickerClickListener;
    String strType, strImage, strViewImage;
    DatePickerDialog datePicker;
    private APIInterface apiInterface;

    HashMap<String, Object> map = new HashMap<>();

    public interface SuccessResponse {
        void onSuccess(HashMap<String, Object> map);
    }

    public interface OnDatePickerClickListener {
        void onDatePickerClick(int position, String siaNo);
    }

    public void onDateSelected(int position, List<DocumentList> date) {
        datePos = position;
        orderListModel = date;
        notifyDataSetChanged();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlParentLyt;
        CustomTextView txtProofName, txtStatus, txtReason;
        CustomEditText edtExpDate;
        CustomButton imgOk;
        WebView ivIdProof;
        SimpleDraweeView ivIdProofImage;
        AppCompatEditText siaIdNo;
        LinearLayout sidField, expiryDateLyt;
        Button siaUploadBtn;
        AppCompatImageView btnExpiryDate;
        AppCompatEditText selectedExpiryDate;
        AppCompatImageView expiryDateBtn;

        MyViewHolder(View view) {
            super(view);

            txtProofName = view.findViewById(R.id.txt_idproof);
            txtStatus = view.findViewById(R.id.txt_status);
            txtReason = view.findViewById(R.id.txt_reason);
            rlParentLyt = view.findViewById(R.id.rl_id_proof_lyt);
            ivIdProof = view.findViewById(R.id.img_idproof_signup);
            ivIdProofImage = view.findViewById(R.id.img_idproof_image);
            edtExpDate = view.findViewById(R.id.edt_date);
            imgOk = view.findViewById(R.id.img_ok);
            siaIdNo = view.findViewById(R.id.siaIdNo);
            sidField = view.findViewById(R.id.sidField);
            expiryDateLyt = view.findViewById(R.id.expiry_date_lyt);
            siaUploadBtn = view.findViewById(R.id.siaUploadBtn);
            ivIdProof.getSettings().setJavaScriptEnabled(true);
//            expiryDateBtn = view.findViewById(R.id.btn_Select_date);
            selectedExpiryDate = view.findViewById(R.id.expiry_date);

        }
    }

    public DocumentAdapter(Context mContext, BaseActivity mBaseActivity, List<DocumentList> contact, String type, Boolean bDocument) {
        this.mContext = mContext;
        this.orderListModel = contact;
        this.type = type;
        this.bDocument = bDocument;
        baseActivity = mBaseActivity;
    }

    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_document_upload_view, parent, false);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        editor = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString(Utils.SP_DRIVERID, null);

        getspCommonCurrency = mContext.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        getData = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        setData = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        strCurrencySymbol = getspCommonCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguage = getspCommonCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCommonCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        DocumentList listModel = orderListModel.get(position);

        if (strLanguageShortName.equals("en")) {
            if (listModel.getDocumentPriority() != null && !String.valueOf(listModel.getDocumentPriority()).equals("null")
                    && !String.valueOf(listModel.getDocumentPriority()).equals("")) {
                if (String.valueOf(listModel.getDocumentPriority()).equals("mandatory")) {
                    callSetText(holder.txtProofName, "*" + listModel.getDocumentName());
                } else if (String.valueOf(listModel.getDocumentPriority()).equals("Optional")) {
                    callSetText(holder.txtProofName, listModel.getDocumentName());
                } else {
                    callSetText(holder.txtProofName, listModel.getDocumentName());
                }
            } else {
                callSetText(holder.txtProofName, listModel.getDocumentName());
            }
        } else {
            if (listModel.getDocumentNameAr() != null && listModel.getDocumentNameAr().equals("null") &&
                    !listModel.getDocumentNameAr().equals(" ") && !listModel.getDocumentNameAr().equals("")) {
                callSetText(holder.txtProofName, listModel.getDocumentNameAr());
            } else {
                callSetText(holder.txtProofName, listModel.getDocumentName());
            }
        }

        if (type.equals("viewdocument")) {

            String strStatus = String.valueOf(listModel.getExpiryStatus());

            if (strStatus.equals("1")) {
//                holder.edtExpDate.setVisibility(View.VISIBLE);
//                holder.imgOk.setVisibility(View.VISIBLE);

                strViewImage = prefs.getString(driverId + listModel.getId() + listModel.getDocument(), null);

                System.out.println("viewimage-->" + strViewImage);
                if (listModel.getDocumentProof().contains("jpg")) {
                    holder.ivIdProof.loadDataWithBaseURL(null, "<html><head></head><body><table style=\"width:100%; height:100%;\"><tr><td style=\"vertical-align:middle;\"><img src=\"" + Utils.BASE_URL + listModel.getDocumentProof() + "\"></td></tr></table></body></html>", "html/css", "utf-8", null);
                } else {
                    if (listModel.getDocumentProof() != null) {
                        if (strViewImage != null) {

                            //  holder.ivIdProof.setImageURI(Utils.BASE_URL + strViewImage);
                            holder.ivIdProof.loadUrl("https://docs.google.com/gview?embedded=true&url=" + strViewImage);
                        } else {
                            // holder.ivIdProof.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                            holder.ivIdProof.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Utils.BASE_URL + listModel.getDocumentProof());
                        }
                    }
                }
                String strViewDate = prefs.getString("date" + driverId + listModel.getDocument(), null);
                String strDate = String.valueOf(listModel.getExpiryDate());
                if (strViewDate != null) {
                    holder.edtExpDate.setText(strViewDate);
                } else {
                    holder.edtExpDate.setText(strDate);
                }


                if (listModel.getApprovedStatus() == 1) {
                    holder.imgOk.setVisibility(View.GONE);
                } /*else {
                    holder.imgOk.setVisibility(View.VISIBLE);
                }*/

            } else {
                System.out.println("viewimage1-->" + listModel.getDocumentProof());

//                0 - pending,1-accept,2-reject,3-not upload

                if (listModel.getApprovedStatus() != 3)
                    holder.edtExpDate.setVisibility(View.GONE);
                holder.imgOk.setVisibility(View.GONE);

                //holder.ivIdProof.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                if (listModel.getDocumentProof() != null && !listModel.getDocumentProof().equals("")) {
                    if (listModel.getDocumentProof().contains(".jpg")) {
                        holder.edtExpDate.setVisibility(View.GONE);
                        holder.imgOk.setVisibility(View.GONE);
                        holder.ivIdProofImage.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                        holder.ivIdProof.setVisibility(View.INVISIBLE);
                        holder.ivIdProofImage.setVisibility(View.VISIBLE);
                        // holder.ivIdProof.loadDataWithBaseURL(Utils.BASE_URL, "<html><head></head><body><table style=\"width:100%; height:100%;\"><tr><td style=\"vertical-align:middle;\"><img src=\"" +  listModel.getDocumentProof() + "\"></td></tr></table></body></html>", "html/css", "utf-8", null);
                    } else {
                        //holder.ivIdProof.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                        holder.ivIdProof.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Utils.BASE_URL + listModel.getDocumentProof());
                        holder.ivIdProof.setVisibility(View.VISIBLE);
                        holder.ivIdProofImage.setVisibility(View.GONE);
                    }
                }
                // holder.ivIdProof.loadUrl("https://docs.google.com/gview?embedded=true&url="+Utils.BASE_URL + listModel.getDocumentProof());

            }

            if (listModel.getApprovedStatus() == 2 || listModel.getApprovedStatus() == 4) {
                holder.txtStatus.setVisibility(View.VISIBLE);
                holder.txtReason.setVisibility(View.VISIBLE);

                callSetText(holder.txtStatus, mContext.getResources().getString(R.string.manage_doc_reject_reupload));
                callSetText(holder.txtReason, listModel.getReason());

                holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.holo_red_dark));
                holder.txtReason.setBackgroundColor(mContext.getResources().getColor(R.color.holo_red_dark));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.txtReason.setTextColor(mContext.getResources().getColor(R.color.white));

            } else if (listModel.getApprovedStatus() == 1) {

                holder.txtStatus.setVisibility(View.VISIBLE);
                holder.txtReason.setVisibility(View.GONE);
                holder.siaUploadBtn.setVisibility(View.GONE);
                holder.selectedExpiryDate.setEnabled(false);

                callSetText(holder.txtStatus, mContext.getResources().getString(R.string.manage_doc_accept));

                holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));
                holder.txtReason.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.txtReason.setTextColor(mContext.getResources().getColor(R.color.white));

            } else if (listModel.getApprovedStatus() == 3) {

                holder.txtStatus.setVisibility(View.GONE);
                holder.txtReason.setVisibility(View.GONE);

                callSetText(holder.txtStatus, mContext.getResources().getString(R.string.manage_doc_accept));

                holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));
                holder.txtReason.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.txtReason.setTextColor(mContext.getResources().getColor(R.color.white));

            } else if ( listModel.getApprovedStatus() == 0 && listModel.getExpiryStatus() == 0 ) {
                holder.txtStatus.setVisibility(View.VISIBLE);
                holder.txtReason.setVisibility(View.VISIBLE);
                holder.selectedExpiryDate.setEnabled(true);

                callSetText(holder.txtStatus, mContext.getResources().getString(R.string.document_expired));

                holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.holo_red_dark));
                holder.txtReason.setBackgroundColor(mContext.getResources().getColor(R.color.holo_red_dark));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.txtReason.setTextColor(mContext.getResources().getColor(R.color.white));
            }else {

                holder.txtStatus.setVisibility(View.VISIBLE);
                holder.txtReason.setVisibility(View.GONE);
                holder.siaUploadBtn.setVisibility(View.GONE);
                holder.selectedExpiryDate.setEnabled(false);
                callSetText(holder.txtStatus, mContext.getResources().getString(R.string.manage_doc_pending));

                holder.rlParentLyt.setEnabled(false);
                holder.txtStatus.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_dark));
                holder.txtReason.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_dark));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.txtReason.setTextColor(mContext.getResources().getColor(R.color.white));

            }

            if (listModel.getLicenseNumberStatus() != null && !listModel.getLicenseNumberStatus().isEmpty()
                    && !listModel.getLicenseNumberStatus().equals("disable")) {
                holder.sidField.setVisibility(View.VISIBLE);

                if (!listModel.getLicenseNo().isEmpty() && !imgUpdated) {
                    imgUpdated = true;
                    holder.siaIdNo.setText(listModel.getLicenseNo());
                    holder.siaIdNo.setFocusable(false);
                    holder.siaIdNo.setClickable(true);
                } else {
                    imgUpdated = false;
                    if (listModel.getLicenseNumberStatus().equals("enable")) {
                        if (!listModel.getLicenseNo().isEmpty()) {
                            holder.siaIdNo.setText(listModel.getLicenseNo());
                        } else {
                            holder.siaIdNo.setText("");
                        }
                    }
                    holder.siaIdNo.setFocusable(true);
                    holder.siaIdNo.setFocusableInTouchMode(true);
                    holder.siaIdNo.setClickable(true);
                }

                System.out.println("expiry status "+ listModel.getExpiryStatus());

                if (listModel.getDocumentProof() != null && !listModel.getDocumentProof().isEmpty() && listModel.getExpiryStatus() != 0 && listModel.getApprovedStatus() != 2) {
                    holder.ivIdProofImage.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                    System.out.println("img set _>>1 " + listModel.getSelectedImg());
                } else if (imgUri != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getSelectedImg());
                    System.out.println("img _>>1 " + listModel.getSelectedImg());
                }

            } else {
                holder.sidField.setVisibility(View.GONE);
                if (listModel.getDocumentProof() != null && !listModel.getDocumentProof().isEmpty() ) {
                    holder.ivIdProofImage.setImageURI(Utils.BASE_URL + listModel.getDocumentProof());
                    System.out.println("img set _>>2 " + Utils.BASE_URL + listModel.getDocumentProof());
                } else if (imgUri != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getSelectedImg());
                    System.out.println("img _>>2 " + listModel.getSelectedImg());
                }
            }

        } else {

            holder.txtStatus.setVisibility(View.GONE);
            holder.txtReason.setVisibility(View.GONE);

            strImage = prefs.getString(driverId + listModel.getId(), null);
            strType = prefs.getString("type" + driverId + listModel.getId(), null);
            System.out.println("~~~~~strImage~~~~~~~~" + strImage);
            System.out.println("~~~~~strType~~~~~~~~" + strType);

            if (strImage != null && strType != null) {
                if (!strType.equals("null") && !strType.equals("") && strType.equals("image")) {
                    if (listModel.getDocumentProof() == null) {
                        holder.ivIdProofImage.setImageURI(Utils.BASE_URL + strImage);
                        holder.ivIdProof.setVisibility(View.INVISIBLE);
                        holder.ivIdProofImage.setVisibility(View.VISIBLE);
                    }
                } else if (!strType.equals("null") && !strType.equals("") && strType.equals("pdf")) {
                    if (listModel.getDocumentProof() == null) {
                        holder.ivIdProof.setVisibility(View.VISIBLE);
                        holder.ivIdProofImage.setVisibility(View.INVISIBLE);
                        holder.ivIdProof.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Utils.BASE_URL + strImage);
                    }
                }
            }

            String strStatus = String.valueOf(listModel.getExpiryStatus());
            if (strStatus.equals("1")) {
                /*holder.edtExpDate.setVisibility(View.VISIBLE);
                holder.imgOk.setVisibility(View.VISIBLE);*/
                String strDate = prefs.getString("date" + driverId + listModel.getId(), null);
                if (strDate != null) {
                    if (!strDate.equals("null")) {
                        if (!strDate.equals("")) {
                            holder.edtExpDate.setText(strDate);
                        }
                    }
                }

            } else {
                holder.edtExpDate.setVisibility(View.GONE);
                holder.imgOk.setVisibility(View.GONE);
            }

            if (listModel.getLicenseNumberStatus() != null && !listModel.getLicenseNumberStatus().isEmpty() && !listModel.getLicenseNumberStatus().equals("disable")) {
                holder.sidField.setVisibility(View.VISIBLE);

                if (listModel.getDocumentProof() != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getDocumentProof());
                    System.out.println("img set _>>3 " + listModel.getSelectedImg());
                } else if (imgUri != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getSelectedImg());
                    System.out.println("img _>>3 " + listModel.getSelectedImg());
                }
            } else {
                holder.sidField.setVisibility(View.GONE);

                if (listModel.getDocumentProof() != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getDocumentProof());
                    System.out.println("img set _>>4 " + listModel.getSelectedImg());
                } else if (imgUri != null) {
                    holder.ivIdProofImage.setImageURI(listModel.getSelectedImg());
                    System.out.println("img _>>4 " + listModel.getSelectedImg());
                }
            }

        }

        if (listModel.getExpiryDate() != null && !listModel.getExpiryDate().isEmpty()) {
            holder.selectedExpiryDate.setText(listModel.getExpiryDate());
        }

        String isDatePicked = getData.getString("imgUpload", null);

        System.out.println("is date picked " + isDatePicked);


        holder.siaUploadBtn.setOnClickListener(view -> {
            System.out.println("sia number value is " + Objects.requireNonNull(holder.siaIdNo.getText()));

            if (imgUri == null) {
                imgUriUpdate = false;
                Toast.makeText(mContext, "IMAGE REQUIRED", Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(holder.selectedExpiryDate.getText()).toString().isEmpty()) {
                Toast.makeText(mContext, "DATE REQUIRED", Toast.LENGTH_SHORT).show();
            } else {

                String sia = holder.siaIdNo.getText().toString();
                String date = holder.selectedExpiryDate.getText().toString();
                System.out.println("sia id is " + sia + " ~~~ " + date);

                if (!listModel.getLicenseNumberStatus().equals("disable")) {

                    if (Objects.requireNonNull(holder.siaIdNo.getText()).toString().isEmpty() || holder.siaIdNo.getText().toString().length() > 16
                            || Objects.requireNonNull(holder.siaIdNo.getText()).toString().equals(holder.itemView.getContext().getString(R.string.enter_sia_number))) {

                        Toast.makeText(mContext, "SIA ID REQUIRED", Toast.LENGTH_SHORT).show();

                    } else {
                        if (successResponse != null) {
                            map.put("expiry_date", date);
                            map.put("expiry_status", "1");
                            map.put("licence_number", sia);
                            map.put("siaUpdate", "true");
                            successResponse.onSuccess(map);
                            setData.clear();
                        }
                    }
                } else {
                    if (successResponse != null) {
                        map.put("expiry_date", date);
                        map.put("expiry_status", "1");
                        map.put("siaUpdate", "true");
                        successResponse.onSuccess(map);
                        setData.clear();
                    }
                }
            }
        });

        holder.selectedExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (datePickerClickListener != null) {
                    String siaNo = Objects.requireNonNull(holder.siaIdNo.getText()).toString();
                    System.out.println("selected sia number is " + siaNo);
                    datePickerClickListener.onDatePickerClick(position, siaNo);
                }
            }
        });
        holder.imgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strDocName = String.valueOf(listModel.getDocumentName());
                String strDocId = String.valueOf(listModel.getId());


                if (type.equals("viewdocument")) {
                    String strValue;
                    String strId = String.valueOf(listModel.getDocument());
                    strViewImage = prefs.getString(driverId + String.valueOf(listModel.getId()) + String.valueOf(listModel.getDocument()), null);
                    if (strViewImage != null) {
                        strValue = strViewImage;
                    } else {
                        strValue = listModel.getDocumentProof();
                    }

                    if (strValue == null) {
                        Toast.makeText(mContext, "Select " + strDocName + " image", Toast.LENGTH_SHORT).show();
                    } else if (holder.edtExpDate.getText().toString().equals("Expiry Date") ||
                            holder.edtExpDate.getText().toString().equals("null") ||
                            holder.edtExpDate.getText().toString().equals("")) {
                        Toast.makeText(mContext, "Select expiry date", Toast.LENGTH_SHORT).show();
                    } else {
                        // callImageUploadPathDetails(strImage, holder.edtExpDate.getText().toString(),strDocId,strDocName);
                        callReUploadDetails(strValue, holder.edtExpDate.getText().toString(), strId, strDocId);
                    }

                } else {
                    System.out.println("date--" + holder.edtExpDate.getText().toString());
                    strImage = prefs.getString(driverId + strDocId, null);
                    if (strImage == null) {
                        Toast.makeText(mContext, "Select " + strDocName + " image", Toast.LENGTH_SHORT).show();
                    } else if (holder.edtExpDate.getText().toString().equals("Expiry Date") ||
                            holder.edtExpDate.getText().toString().equals("null") ||
                            holder.edtExpDate.getText().toString().isEmpty()) {
                        Toast.makeText(mContext, "Select expiry date", Toast.LENGTH_SHORT).show();
                    } else {
                        callImageUploadPathDetails(strImage, holder.edtExpDate.getText().toString(), strDocId, strDocName);
                    }

                }

            }
        });

        holder.rlParentLyt.setOnClickListener(view -> {
            if (!imgUriUpdate) {
                imgUploaded = false;
                holder.siaIdNo.setFocusable(true);
                holder.siaIdNo.setClickable(true);

                map.put("status", String.valueOf(listModel.getStatus()));
                map.put("approvedstatus", String.valueOf(listModel.getApprovedStatus()));

                map.put("id", String.valueOf(listModel.getId()));
                map.put("documentname", String.valueOf(listModel.getDocumentName()));
                map.put("documentname_ar", String.valueOf(listModel.getDocumentNameAr()));

                map.put("document", String.valueOf(listModel.getDocument()));

                map.put("date", holder.edtExpDate.getText().toString());

                map.put("date_status", String.valueOf(listModel.getExpiryStatus()));
                map.put("name", "normal");

                System.out.println("onClickedSiaStatus " + listModel.getLicenseNumberStatus());

                if (!listModel.getLicenseNumberStatus().equals("disable")) {
                    System.out.println("onClickedSiaStatus 1" + listModel.getLicenseNumberStatus());
                    map.put("onClickedSiaStatus", "enable");
                    map.put("onClickedPosition", position);
                } else {
                    System.out.println("onClickedSiaStatus 2" + listModel.getLicenseNumberStatus().equals("disable"));
                    map.put("onClickedSiaStatus", "disable");
                    map.put("onClickedPosition", position);
                }

                if (type.equals("viewdocument")) {
                    editor.putString("id", String.valueOf(listModel.getDocument()));
                    System.out.println("id-view->" + listModel.getDocument());
                } else {
                    editor.putString("id", String.valueOf(listModel.getId()));
                    System.out.println("id-->" + listModel.getId());
                }

                editor.putString("name", "normal");

                editor.putBoolean("bDoc", bDocument);

                editor.putString("date_status", String.valueOf(listModel.getExpiryStatus()));
                editor.putString("date", holder.edtExpDate.getText().toString());


                editor.putString("documentname", String.valueOf(listModel.getDocumentName()));

                if (listModel.getDocumentNameAr() == null ||
                        String.valueOf(listModel.getDocumentNameAr()).isEmpty()
                        || String.valueOf(listModel.getDocumentNameAr()).equals("null")) {
                    editor.putString("documentname_ar", String.valueOf(listModel.getDocumentName()));
                } else {
                    editor.putString("documentname_ar", String.valueOf(listModel.getDocumentNameAr()));
                }

                String strViewImage = String.valueOf(listModel.getDocument());

                System.out.println("viewimage-->" + strViewImage);

                if (strViewImage.equals("null")) {
                    editor.putString("document", strViewImage);
                } else {
                    editor.putString("document", String.valueOf(listModel.getDocument()));
                    //editor.putString("document", String.valueOf(listModel.getDocumentProof()));
                }

                editor.apply();

                if (successResponse != null)
                    successResponse.onSuccess(map);
            } else
                Toast.makeText(mContext, "complete current document upload", Toast.LENGTH_SHORT).show();
        });

        holder.edtExpDate.setTag(position);
        holder.edtExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if (datePicker != null) {
                    datePicker.dismiss();
                    Calendar cur_calender = Calendar.getInstance();
                    datePicker = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    long date_ship_millis = calendar.getTimeInMillis();

                                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    String selectedDate = newFormat.format(new Date(date_ship_millis));
                                    holder.edtExpDate.setText(selectedDate);
                                    if (type.equals("viewdocument")) {
                                        editor.putString("date" + driverId + String.valueOf(listModel.getDocument()), selectedDate);
                                        editor.apply();
                                    } else {
                                        editor.putString("date" + driverId + String.valueOf(listModel.getId()), selectedDate);
                                        editor.apply();
                                    }

                                    System.out.println("date-->" + selectedDate);
                                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date1;
                                    try {
                                        date1 = dateformat.parse(selectedDate);
                                        DateFormat dayFormate = new SimpleDateFormat("EEEE", Locale.US);
                                        String dayFromDate = dayFormate.format(date1);

                                        String selectedDay = dayFromDate;

                                        Log.i("day text-->", "onDateSet: " + dayFromDate);

                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            },
                            cur_calender.get(Calendar.YEAR),
                            cur_calender.get(Calendar.MONTH),
                            cur_calender.get(Calendar.DAY_OF_MONTH)
                    );
                    datePicker.setThemeDark(false);
                    datePicker.setVersion(DatePickerDialog.Version.VERSION_1);
                    datePicker.setLocale(Locale.US);
                    datePicker.setAccentColor(mContext.getResources().getColor(R.color.colorPrimary));

                    Calendar minDate = Calendar.getInstance();
                    datePicker.setMinDate(minDate);
                    datePicker.show(manager, "Datepickerdialog");
                } else {
                    // dialogDatePickerLight();
                    Calendar cur_calender = Calendar.getInstance();
                    datePicker = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    long date_ship_millis = calendar.getTimeInMillis();

                                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    String selectedDate = newFormat.format(new Date(date_ship_millis));
                                    holder.edtExpDate.setText(selectedDate);
                                    if (type.equals("viewdocument")) {
                                        editor.putString("date" + driverId + String.valueOf(listModel.getDocument()), selectedDate);
                                        editor.apply();
                                    } else {
                                        editor.putString("date" + driverId + String.valueOf(listModel.getId()), selectedDate);
                                        editor.apply();
                                    }

                                    @SuppressLint("SimpleDateFormat")
                                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date1;
                                    try {
                                        date1 = dateformat.parse(selectedDate);
                                        DateFormat dayFormate = new SimpleDateFormat("EEEE", Locale.US);
                                        String dayFromDate = dayFormate.format(date1);

                                        String selectedDay = dayFromDate;

                                        Log.i("day text-->", "onDateSet: " + dayFromDate);

                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            },
                            cur_calender.get(Calendar.YEAR),
                            cur_calender.get(Calendar.MONTH),
                            cur_calender.get(Calendar.DAY_OF_MONTH)
                    );
                    datePicker.setThemeDark(false);
                    datePicker.setVersion(DatePickerDialog.Version.VERSION_1);
                    datePicker.setLocale(Locale.US);
                    datePicker.setAccentColor(mContext.getResources().getColor(R.color.colorPrimary));

                    Calendar minDate = Calendar.getInstance();
                    datePicker.setMinDate(minDate);
                    datePicker.show(manager, "Datepickerdialog");
                }

            }
        });

    }

    public void onSelect(int position, Uri imgUrl, String isFrom) {
        selected_pos = position;
        imgUri = imgUrl;
        imgUriUpdate = false;

        notifyDataSetChanged();
    }

    private void callSetText(CustomTextView holder, String string) {
        holder.setText(string);
    }

    @Override
    public int getItemCount() {
        return orderListModel.size();
    }

    private void callImageUploadPathDetails(String fileBinaryData, String date, String strDocId, String strDocName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("document", fileBinaryData);
        map.put("expiry_date", date);
        map.put("document_id", strDocId);
        map.put("document_type", strDocName);
        map.put("driver_id", driverId);
        if (fileBinaryData.contains("jpg")) {
            map.put("type", "image");
        } else {
            map.put("type", "pdf");
        }
        Log.e("callImageUploa", "~~~" + map);
        Call<UploadDocModel> call2 = apiInterface.doDocumentUploadDetails(map);
        //progressBar.setVisibility(View.VISIBLE);
        call2.enqueue(new Callback<UploadDocModel>() {
            @Override
            public void onResponse(@NonNull Call<UploadDocModel> call, @NonNull Response<UploadDocModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    //   progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Successfully updated.")) {

                        //remove sikka
                        // editor.putString(strDocId, String.valueOf(fileUri));
                     /*   editor.putString(driverId+strDocId, response.body().getUploadDocData().getDocument());
                        editor.putString("date"+driverId+strDocId, date);
                        editor.apply();*/

                        notifyDataSetChanged();
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        // adapter.notifyDataSetChanged();

                        Log.e("DocumentAdapter-", "onResponse: ~~~~~~~~~~~");
                    } else {
                        Toast.makeText(mContext, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    System.out.println("``````");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadDocModel> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                // progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private void callReUploadDetails(String fileBinaryData, String date, String strDocId, String strId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("document", fileBinaryData);
        map.put("expiry_date", date);
        map.put("document_id", strDocId);
        map.put("id", strId);
        map.put("driver_id", driverId);
        Log.e("callReUploadDetails", "~~~" + map);
        Call<ReUploadModel> call2 = apiInterface.doDocumentReUploadDetails(map);
        //  progressBar.setVisibility(View.VISIBLE);
        call2.enqueue(new Callback<ReUploadModel>() {
            @Override
            public void onResponse(@NonNull Call<ReUploadModel> call, @NonNull Response<ReUploadModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    // progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Successfully updated.")) {

                        //remove sikka

                        //   callViewDocument();
                        editor.putString(driverId + strId + strDocId, null);
                        editor.apply();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", "update");

                        editor.putString("name", "update");
                        editor.apply();

                        if (successResponse != null)
                            successResponse.onSuccess(map);


                        Log.e("DocumentAdapter", "onResponse: ~~~~~~~~~~~");
                    } else {
                        Toast.makeText(mContext, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    System.out.println("``````");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReUploadModel> call, @NonNull Throwable t) {
                Log.e("callonFailure~~~", String.valueOf(t));
                //progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

}

