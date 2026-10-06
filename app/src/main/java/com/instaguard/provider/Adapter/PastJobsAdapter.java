package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.RatingBar.SimpleRatingBar;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PastJobsData;
import com.instaguard.provider.Models.RatingUserModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;


public class PastJobsAdapter extends RecyclerView.Adapter<PastJobsAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<PastJobsData> orderListModel;
    public static SuccessResponse successResponse;
    PastJobsData listModel;
    int i = 1;
    private SharedPreferences getspCommonCurrency, getspCommonState;
    private String userId, strCurrencySymbol, strLanguageShortName, strLanguage, userAccessToken;
    private SharedPreferences.Editor putSpState;


    private DatabaseReference driverWalletDatabaseBRef;
    private ValueEventListener driverWalletValueEventListener;

    public interface SuccessResponse {
        void onSuccess(HashMap<String, Object> map);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llPastjobsParent;
        CustomButton btnViewDetails;
        CardView cvPastjobsParent;
        CustomTextView txtBookingId, txtBookingDate, txtBookingCategory, txtProviderAddress, txtStatus, txtJobLocation;


        public MyViewHolder(View view) {
            super(view);

            txtBookingId = view.findViewById(R.id.txt_bookingid);
            txtBookingDate = view.findViewById(R.id.txt_booking_date);
            txtBookingCategory = view.findViewById(R.id.txt_booking_category);
            txtJobLocation = view.findViewById(R.id.txt_job_location);
            txtStatus = view.findViewById(R.id.txt_status);
            llPastjobsParent = view.findViewById(R.id.ll_pastjobs_parent);
            cvPastjobsParent = view.findViewById(R.id.cv_pastjobs_parent);


        }
    }

    public PastJobsAdapter(Context mContext, List<PastJobsData> contact) {
        this.mContext = mContext;
        this.orderListModel = contact;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pastjobs, parent, false);
        putSpState = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getspCommonState = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        getspCommonCurrency = mContext.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        strCurrencySymbol = getspCommonCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);


        userId = getspCommonState.getString(Utils.SP_DRIVERID, null);
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        //strCurrencySymbol = getspCommonState.getString(Utils.SP_CURRENCY_SYMBOL, null);

        strLanguage = getspCommonCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCommonCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        listModel = orderListModel.get(position);

        final String dateList;
        String dateListEs = null;
        dateList = listModel.getJobDate(); //"Thu,Jan 30,2020 05:12 AM",
        try {

            String[] separated = dateList.split(",");
            String day = separated[0]; //Thu
            String monthDate = separated[1]; // Jan 30
            String yearTime = separated[2]; // 2020 05:12 AM"

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date12Format = new SimpleDateFormat("yyyy hh:mm a");

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date24Format = new SimpleDateFormat("yyyy HH:mm");

            String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(yearTime)));

            String[] separate2 = monthDate.split(" ");
            String month = separate2[0]; // Jan
            String date = separate2[1]; // 30

            // Log.e("onBindViewHolder: ~~~~~~~~1~~~~~~~", day + "~~" + monthDate + "~~~~" + yearTime + "~~~~");
            // Log.e("onBindViewHolder: ~~~~~~~~2~~~~~~~", "~~" + month + "~~~~" + date);

            //ronBindViewHolder: ~~~~~~~~1~~~~~~~: Thu ~~ Jan 30~~~~2020 05:12 AM~~~~
            //onBindViewHolder: ~~~~~~~~2~~~~~~~: ~~Jan~~~~30

            String dayLabel = "-";
            switch (day) {
                case "Sun":
                case "sun":
                    dayLabel = mContext.getResources().getString(R.string.Sunday_3);
                    break;
                case "Mon":
                case "mon":
                    dayLabel = mContext.getResources().getString(R.string.Monday_3);
                    break;
                case "Tue":
                case "tue":
                    dayLabel = mContext.getResources().getString(R.string.Tuesday_3);
                    break;
                case "Wed":
                case "wed":
                    dayLabel = mContext.getResources().getString(R.string.Wednesday_3);
                    break;
                case "Thu":
                case "thu":
                    dayLabel = mContext.getResources().getString(R.string.Thursday_3);
                    break;
                case "Fri":
                case "fri":
                    dayLabel = mContext.getResources().getString(R.string.Friday_3);
                    break;
                case "Sat":
                case "sat":
                    dayLabel = mContext.getResources().getString(R.string.Saterday_3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + day);
            }

            String monthLabel = "-";
            switch (month) {
                case "Jan":
                case "jan":
                    monthLabel = mContext.getResources().getString(R.string.January_3);
                    break;
                case "Feb":
                case "feb":
                    monthLabel = mContext.getResources().getString(R.string.February_3);
                    break;
                case "Mar":
                case "mar":
                    monthLabel = mContext.getResources().getString(R.string.March_3);
                    break;
                case "Apr":
                case "apr":
                    monthLabel = mContext.getResources().getString(R.string.April_3);
                    break;
                case "May":
                case "may":
                    monthLabel = mContext.getResources().getString(R.string.May_3);
                    break;
                case "Jun":
                case "jun":
                    monthLabel = mContext.getResources().getString(R.string.June_3);
                    break;
                case "Jul":
                case "jul":
                    monthLabel = mContext.getResources().getString(R.string.July_3);
                    break;
                case "Aug":
                case "aug":
                    monthLabel = mContext.getResources().getString(R.string.August_3);
                    break;
                case "Sep":
                case "sep":
                    monthLabel = mContext.getResources().getString(R.string.September_3);
                    break;
                case "Oct":
                case "oct":
                    monthLabel = mContext.getResources().getString(R.string.October_3);
                    break;
                case "Nov":
                case "nov":
                    monthLabel = mContext.getResources().getString(R.string.November_3);
                    break;
                case "Dec":
                case "dec":
                    monthLabel = mContext.getResources().getString(R.string.December_3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + month);
            }

            dateListEs = dayLabel + "," + monthLabel + " " + date + "," + yearTime1;

        } catch (Exception e) {
            e.printStackTrace();
            dateListEs = null;
        }

        if (dateListEs != null) {
            holder.txtBookingDate.setText(dateListEs);
        } else {
            holder.txtBookingDate.setText(dateList);
        }

        Log.i("bind--", "onBindViewHolder-->: " + listModel.getBookingId());
        holder.txtBookingId.setText(String.valueOf(listModel.getBookingId()));

        if (strLanguageShortName.equals("en")) {
            holder.txtBookingCategory.setText(listModel.getCategoryName() + " - " + listModel.getServiceName());
        } else {
            if (!listModel.getCategoryAr().equals(" ")) {
                if (!listModel.getServiceAr().equals(" ")) {
                    holder.txtBookingCategory.setText(listModel.getCategoryAr() + " - " + listModel.getServiceAr());
                } else {
                    holder.txtBookingCategory.setText(listModel.getCategoryName() + " - " + listModel.getServiceName());
                }
            } else {
                holder.txtBookingCategory.setText(listModel.getCategoryName() + " - " + listModel.getServiceName());
            }
        }

        if (listModel.getServiceStatus() == 7) {
            holder.txtStatus.setText(R.string.past_job_finished);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.holo_green_dark));
            holder.cvPastjobsParent.setEnabled(true);
        } else if (listModel.getServiceStatus() == 6) {
            holder.txtStatus.setText(R.string.please_wait_customer_accept_your_completion);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.holo_green_dark));
            holder.cvPastjobsParent.setEnabled(true);
        } else if (listModel.getServiceStatus() == 8) {
            holder.txtStatus.setText(R.string.upcoming_job_cancelled);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.field_text_color_error));
            holder.cvPastjobsParent.setEnabled(false);
        } else if (listModel.getServiceStatus() == 9) {
            holder.txtStatus.setText(R.string.upcoming_job_cancelled_driver);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.field_text_color_error));
            holder.cvPastjobsParent.setEnabled(false);
        } else if (listModel.getServiceStatus() == 14) {
            holder.txtStatus.setText(R.string.upcoming_scheduled_cancel_user);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.field_text_color_error));
            holder.cvPastjobsParent.setEnabled(false);
        } else if (listModel.getServiceStatus() == 15) {
            holder.txtStatus.setText(R.string.upcoming_scheduled_cancel_driver);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.field_text_color_error));
            holder.cvPastjobsParent.setEnabled(false);
        } else {
            holder.txtStatus.setText(R.string.upcoming_job_not_complete);
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.field_text_color_error));
            holder.cvPastjobsParent.setEnabled(false);
        }

        holder.txtJobLocation.setText(String.valueOf(listModel.getCustomerLocation()));


    }

    @Override
    public int getItemCount() {
        return orderListModel.size();
    }
}