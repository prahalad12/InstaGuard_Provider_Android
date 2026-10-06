package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.UpcomingJobsData;
import com.instaguard.provider.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.System.out;


public class UpcomingJobsAdapter extends RecyclerView.Adapter<UpcomingJobsAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<UpcomingJobsData> orderListModel;
    public static SuccessResponse successResponse;
    UpcomingJobsData listModel;
    SharedPreferences prefs;
    int i = 1;

    private SharedPreferences getspCurrency;
    private String strLanguage, strLanguageShortName;

    public interface SuccessResponse {
        void onSuccess(HashMap<String, Object> map);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llPastjobsParent, llStatusBtnJob, llStatusJob;
        public CustomButton btnStartJobsUpcoming, btnTransferJobsUpcoming, btnCancelJobsUpcoming;
        public CustomTextView txtBookingId, txtBookingDate, txtBookingCategory, txtCustomerName, txtCustomerPhone, txtStatus, txtJobLocation, txtAgentCount,
                getTxtAgentDays,
                txtStartDateTime,
                txtEndDateTime;
        RelativeLayout rlCustomerDetailsLyt;
        ImageButton imgCustomerPhone, imgCustomerMessage;


        public MyViewHolder(View view) {
            super(view);

            txtBookingId = view.findViewById(R.id.txt_bookingid);
            txtBookingDate = view.findViewById(R.id.txt_booking_date);
            txtBookingCategory = view.findViewById(R.id.txt_booking_category);
            txtJobLocation = view.findViewById(R.id.txt_job_location);
            txtStatus = view.findViewById(R.id.txt_status);
            llPastjobsParent = view.findViewById(R.id.ll_pastjobs_parent);

            llStatusJob = view.findViewById(R.id.ll_status);
            llStatusBtnJob = view.findViewById(R.id.ll_status_btnjob);

            btnStartJobsUpcoming = view.findViewById(R.id.btn_startjobs_upcoming);
            btnTransferJobsUpcoming = view.findViewById(R.id.btn_transferjobs_upcoming);
            btnCancelJobsUpcoming = view.findViewById(R.id.btn_canceljobs_upcoming);

            rlCustomerDetailsLyt = view.findViewById(R.id.rl_parent_lyt_provider_details);

            txtCustomerName = view.findViewById(R.id.txt_Driver_name_label);
            txtCustomerPhone = view.findViewById(R.id.txt_Driver_ph_label);

            imgCustomerPhone = view.findViewById(R.id.img_phone);
            imgCustomerMessage = view.findViewById(R.id.img_message);

            txtAgentCount = view.findViewById(R.id.txt_agent_count);
            getTxtAgentDays = view.findViewById(R.id.txt_agent_days);

            txtStartDateTime = view.findViewById(R.id.txt_start_date_time);
            txtEndDateTime = view.findViewById(R.id.txt_end_date_time);

        }
    }

    public UpcomingJobsAdapter(Context mContext, List<UpcomingJobsData> contact) {
        this.mContext = mContext;
        this.orderListModel = contact;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_upcomingjobs, parent, false);

        getspCurrency = mContext.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = getspCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        listModel = orderListModel.get(position);
        //holder.txtBookingDate.setText(listModel.getJobDate());

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

        /*if (strLanguageShortName.equals("en")) {
            holder.txtBookingDate.setText(dateList);
        } else if (strLanguageShortName.equals("es")) {
            */
        if (dateListEs != null) {
            holder.txtBookingDate.setText(dateListEs);
        } else {
            holder.txtBookingDate.setText(dateList);
        }

       /* } else {
            holder.txtBookingDate.setText(dateList);
        }*/


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

        if (listModel.getStartDateTime() != null && !listModel.getStartDateTime().equals("null") && !listModel.getStartDateTime().equals("")) {
            System.out.println("timeStart"+listModel.getStartDateTime());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date12Format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            try {
                String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(listModel.getStartDateTime())));
                out.println("strStartDateTimeFB"+yearTime1);
                holder.txtStartDateTime.setText(yearTime1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.txtStartDateTime.setText("-");
        }
        if (listModel.getEndDateTime() != null && !listModel.getEndDateTime().equals("null") && !listModel.getEndDateTime().equals("")) {
            holder.txtEndDateTime.setText(listModel.getEndDateTime());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date12Format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            try {
                String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(listModel.getEndDateTime())));
                holder.txtEndDateTime.setText(yearTime1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.txtEndDateTime.setText("-");
        }

        holder.txtCustomerName.setText(mContext.getResources().getString(R.string.provider_name) + ":-" + listModel.getCustomerName());
        holder.txtCustomerPhone.setText(mContext.getResources().getString(R.string.provider_phone) + ":-" + listModel.getCustomerPhoneNumber());
        // holder.getTxtAgentDays.setText("Total Days :- "+listModel.getTotalDays());
        // holder.txtAgentCount.setText("Total Agent:- "+listModel.getTotalAgents());
        holder.getTxtAgentDays.setText(mContext.getResources().getString(R.string.total_days) + " :- " +
                listModel.getTotalDays());
        holder.txtAgentCount.setText(mContext.getResources().getString(R.string.total_guard) + ":- " +
                listModel.getTotalAgents());

        if (listModel.getServiceStatus() == 14) {
            holder.txtStatus.setText(R.string.upcoming_job_cancelled);

            holder.llStatusBtnJob.setVisibility(View.GONE); // btn
            holder.llStatusJob.setVisibility(View.VISIBLE); // status

            holder.btnCancelJobsUpcoming.setVisibility(View.GONE);
        } else if (listModel.getServiceStatus() == 15) {

            holder.llStatusBtnJob.setVisibility(View.GONE); // btn
            holder.llStatusJob.setVisibility(View.VISIBLE); // status

            holder.txtStatus.setText(R.string.upcoming_job_cancelled_driver);
            holder.btnCancelJobsUpcoming.setVisibility(View.GONE);
        } else if (listModel.getServiceStatus() == 13) {

            holder.llStatusBtnJob.setVisibility(View.VISIBLE); // btn
            holder.llStatusJob.setVisibility(View.GONE); // status

            holder.txtStatus.setText(R.string.upcoming_job_accepted_driver);
        } else {

            holder.txtStatus.setText(R.string.upcoming_job_not_complete);
            holder.btnCancelJobsUpcoming.setVisibility(View.GONE);
            //holder.btnCancelJobsUpcoming.setVisibility(View.VISIBLE);
        }


        holder.txtJobLocation.setText(String.valueOf(listModel.getCustomerLocation()));

        holder.btnStartJobsUpcoming.setTag(position);
        holder.btnStartJobsUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                UpcomingJobsData listModel = orderListModel.get(pos);

                HashMap<String, Object> map = new HashMap<>();
                map.put("job", "startjob");
                map.put("book_id", listModel.getBookingId());
                if (successResponse != null)
                    successResponse.onSuccess(map);
            }
        });

        holder.btnTransferJobsUpcoming.setTag(position);
        holder.btnTransferJobsUpcoming.setOnClickListener(v -> {
            int pos = (int) v.getTag();
            UpcomingJobsData listModel = orderListModel.get(pos);
            HashMap<String, Object> map = new HashMap<>();
            map.put("job", "transfer");
            map.put("book_id", listModel.getBookingId());
            if (successResponse != null)
                successResponse.onSuccess(map);
        });

        holder.btnCancelJobsUpcoming.setTag(position);
        holder.btnCancelJobsUpcoming.setOnClickListener(v -> {

            int pos = (int) v.getTag();
            UpcomingJobsData listModel = orderListModel.get(pos);

            HashMap<String, Object> map = new HashMap<>();
            map.put("book_id", listModel.getBookingId());
            map.put("job", "canceljob");
            if (successResponse != null)
                successResponse.onSuccess(map);
        });

        holder.imgCustomerPhone.setTag(position);
        holder.imgCustomerPhone.setOnClickListener(v -> {
            int pos = (int) v.getTag();
            UpcomingJobsData listModel = orderListModel.get(pos);

            callDriverTelephonyManager(String.valueOf(listModel.getCustomerPhoneNumber()));
        });

        holder.imgCustomerMessage.setTag(position);
        holder.imgCustomerMessage.setOnClickListener(v -> {
            int pos = (int) v.getTag();
            UpcomingJobsData listModel = orderListModel.get(pos);

            callDriverMessageManager(String.valueOf(listModel.getCustomerName()), String.valueOf(listModel.getCustomerPhoneNumber()));
        });


    }

    @Override
    public int getItemCount() {
        return orderListModel.size();
    }
  /*  public void updateList(List<Contact> list){
        orderListModel = list;
        notifyDataSetChanged();
    }*/


    private void callDriverTelephonyManager(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException ex) {

                Toast.makeText(mContext, R.string.no_is_not_found, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, R.string.no_mobile_num, Toast.LENGTH_SHORT).show();
        }
    }

    private void callDriverMessageManager(String drname, String phoneNumber) {

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            try {
                try {
                    //  startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", drivermobile, null)));
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + phoneNumber));
                    smsIntent.putExtra("sms_body", "Hi " + drname);
                    mContext.startActivity(smsIntent);

                } catch (ActivityNotFoundException e) {
                    // Display some sort of error message here.
                }
            } catch (ActivityNotFoundException ex) {

                Toast.makeText(mContext, R.string.no_is_not_found, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, R.string.no_mobile_num, Toast.LENGTH_SHORT).show();
        }
    }

}

