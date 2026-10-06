package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.DocumentViewData_;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;


public class ManageDocumentAdapter extends RecyclerView.Adapter<ManageDocumentAdapter.MyViewHolder> {

    private Context mContext;
    private List<DocumentViewData_> orderListModel;
    public static SuccessResponse successResponse;
    DocumentViewData_ listModel;
    SharedPreferences prefs;
    int i =1;
    public interface SuccessResponse{
        void onSuccess(HashMap<String, Object> map);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

       // public LinearLayout llPastjobsParent;
        public SimpleDraweeView iv_manage_view;
        public CustomEditText edt_document_name;
        public CustomTextView txt_view_details,txt_reject_reason;
        public LinearLayout llRejectDocLyt;


        public MyViewHolder(View view) {
            super(view);


            txt_view_details = (CustomTextView) view.findViewById(R.id.txt_view_details);
            txt_reject_reason = (CustomTextView) view.findViewById(R.id.txt_reject_reason);
            edt_document_name = (CustomEditText) view.findViewById(R.id.edt_document_name);
            iv_manage_view = (SimpleDraweeView) view.findViewById(R.id.iv_manage_views);
            llRejectDocLyt = (LinearLayout) view.findViewById(R.id.ll_reject_lyt);


        }
    }

    public ManageDocumentAdapter(Context mContext, List<DocumentViewData_> contact) {
        this.mContext = mContext;
        this.orderListModel = contact;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_managedocument, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        listModel = orderListModel.get(position);


        //0 - pending 1 - Accept 2 - Reject
        switch (String.valueOf(listModel.getApprovedStatus())) {
            case "0":
                holder.iv_manage_view.setEnabled(true);

                holder.txt_view_details.setText(mContext.getResources().getString(R.string.manage_doc_pending));
                holder.txt_view_details.setEnabled(false);

                holder.llRejectDocLyt.setVisibility(View.GONE);

                break;
            case "1":
                holder.iv_manage_view.setEnabled(true);
               // holder.rlDocuCommonLyt.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorLeftBack));
                holder.txt_view_details.setText(mContext.getResources().getString(R.string.manage_doc_accept));
                holder.txt_view_details.setEnabled(true);

                holder.llRejectDocLyt.setVisibility(View.GONE);
                //colorAccent
                break;
            case "2":
                holder.iv_manage_view.setEnabled(false);
              //  holder.rlDocuCommonLyt.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorcheckinRatingfill));
                holder.txt_view_details.setText(mContext.getResources().getString(R.string.manage_doc_reject_reupload));
                holder.txt_view_details.setEnabled(false);

                holder.llRejectDocLyt.setVisibility(View.VISIBLE);
                holder.txt_reject_reason.setText(String.valueOf(listModel.getReason()));

                //colorcheckinRatingfill
                break;
        }

        holder.edt_document_name.setText(String.valueOf(listModel.getDocumentName()));
        holder.iv_manage_view.setImageURI(Utils.BASE_URL_IMAGE_UPLOAD+listModel.getDocumentProof());

        holder.iv_manage_view.setTag(position);
        holder.iv_manage_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                DocumentViewData_ listModels =orderListModel.get(pos);
                Log.e("~~~~~~~~~~~", "onClick: ~~~~~~~~~~~~"+listModels.getDocument());
                HashMap<String, Object> map = new HashMap<>();
                map.put("doc_name",listModels.getDocumentName());
                map.put("doc_proof", listModels.getDocumentProof());
                map.put("click", "view");
                if(successResponse!=null)
                    successResponse.onSuccess(map);

            }
        });

        holder.llRejectDocLyt.setTag(position);
        holder.llRejectDocLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                DocumentViewData_ listModels =orderListModel.get(pos);
                Log.e("~~~~~~~~~~~", "onClick: ~~~~~~~~~~~~"+listModels.getDocument());
                HashMap<String, Object> map = new HashMap<>();
                map.put("doc_id",listModels.getId());
                map.put("doc_type",listModels.getDocumentName());
                map.put("doc_name",listModels.getDocumentName());
                map.put("doc_proof", listModels.getDocumentProof());
                map.put("click", "imageUpload");
                if(successResponse!=null)
                    successResponse.onSuccess(map);
            }
        });

       // Log.i("bind--", "onBindViewHolder-->: "+listModel.getBookingId());
       // holder.iv_manage_view.setText(String.valueOf(listModel.getBookingId()));
    }
    @Override
    public int getItemCount() {
        return orderListModel.size();
    }

}

