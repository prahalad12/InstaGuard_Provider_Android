package com.instaguard.provider.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.RatingBar.SimpleRatingBar;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PastJobsData;
import com.instaguard.provider.Models.UserFeedbackDataModel;
import com.instaguard.provider.Models.UserFeedbackModel;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;


public class UserFeedbackAdapter extends RecyclerView.Adapter<UserFeedbackAdapter.MyViewHolder> {

    private Context mContext;
    private List<UserFeedbackDataModel> orderListModel;
    public static SuccessResponse successResponse;
    private UserFeedbackDataModel listModel;
    SharedPreferences prefs;
    int i =1;
    public interface SuccessResponse{
        void onSuccess(HashMap<String, Object> map);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView txt_customer_name_info,txt_time;
        public SimpleDraweeView img_customer_info_pro_pic;
        public SimpleRatingBar ratingbar_userfeedback;
        public LinearLayout ll_feedback_parent;


        MyViewHolder(View view) {
            super(view);


           // ll_feedback_parent = (LinearLayout) view.findViewById(R.id.ll_feedback_parent);
            txt_customer_name_info = (CustomTextView) view.findViewById(R.id.txt_customer_name_info);
            txt_time = (CustomTextView) view.findViewById(R.id.txt_time);
            img_customer_info_pro_pic = (SimpleDraweeView) view.findViewById(R.id.img_customer_info_pro_pic);
            ratingbar_userfeedback = (SimpleRatingBar) view.findViewById(R.id.ratingbar_userfeedback);


            ll_feedback_parent = (LinearLayout) view.findViewById(R.id.ll_feedback_parent);
            for (int i = 0; i < ll_feedback_parent.getChildCount(); i++) {
                View child = ll_feedback_parent.getChildAt(i);
                child.setEnabled(false);
            }

          /*  ll_feedback_parent.setEnabled(false);
            ll_feedback_parent.setFocusableInTouchMode(false);*/

        }
    }

    public UserFeedbackAdapter(Context mContext, List<UserFeedbackDataModel> contact) {
        this.mContext = mContext;
        this.orderListModel = contact;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_userfeedback, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        listModel = orderListModel.get(position);

        holder.txt_customer_name_info.setText(listModel.getName());
        holder.ratingbar_userfeedback.setRating(listModel.getRating());

        holder.ratingbar_userfeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        holder.ratingbar_userfeedback.setFocusable(false);

//        holder.txt_time.setText(listModel.getUpdatedAt());

        holder.txt_time.setText(listModel.getFeedback());


        try {
            holder.img_customer_info_pro_pic.setImageURI(Utils.BASE_URL + listModel.getAvatar());
            int color = mContext.getResources().getColor(R.color.colorPrimary);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setBorder(color, 1.0f);
            roundingParams.setRoundAsCircle(true);
            holder.img_customer_info_pro_pic.getHierarchy().setRoundingParams(roundingParams);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    @Override
    public int getItemCount() {
        return orderListModel.size();
    }
  /*  public void updateList(List<Contact> list){
        orderListModel = list;
        notifyDataSetChanged();
    }*/
}

