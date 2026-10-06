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

import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PendingJobsData;
import com.instaguard.provider.Models.UpcomingJobsData;
import com.instaguard.provider.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class PendingJobsAdapter extends RecyclerView.Adapter<PendingJobsAdapter.MyViewHolder> {

    private Context mContext;
    private List<PendingJobsData> orderListModel;
    public static SuccessResponse successResponse;
    PendingJobsData listModel;
    SharedPreferences prefs;
    int i =1;

    private SharedPreferences getspCurrency;
    private String strLanguage,strLanguageShortName;


    public interface SuccessResponse{
        void onSuccess(HashMap<String, Object> map);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llPastjobsParent;
        public CustomButton btnViewDetails,btnAcceptJobsUpcoming,btnDeclineJobsUpcoming;
        public CustomTextView txtBookingId,txtBookingDate,txtBookingCategory,txtProviderAddress,txtStatus,txtJobLocation,txtAgentCount,getTxtAgentDays;


        public MyViewHolder(View view) {
            super(view);

            txtBookingId = (CustomTextView) view.findViewById(R.id.txt_bookingid);
            txtBookingDate = (CustomTextView) view.findViewById(R.id.txt_booking_date);
            txtBookingCategory = (CustomTextView) view.findViewById(R.id.txt_booking_category);
            txtJobLocation = (CustomTextView) view.findViewById(R.id.txt_job_location);
           // txtStatus = (CustomTextView) view.findViewById(R.id.txt_status);
            llPastjobsParent = (LinearLayout) view.findViewById(R.id.ll_pastjobs_parent);

            btnAcceptJobsUpcoming = (CustomButton) view.findViewById(R.id.btn_acceptjobs_pending);
            btnDeclineJobsUpcoming = (CustomButton) view.findViewById(R.id.btn_declinejobs_pending);
            txtAgentCount= (CustomTextView) view.findViewById(R.id.txt_agent_count);
            getTxtAgentDays= (CustomTextView) view.findViewById(R.id.txt_agent_days);
        }
    }

    public PendingJobsAdapter(Context mContext, List<PendingJobsData> contact) {
        this.mContext = mContext;
        this.orderListModel = contact;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pendingjobs, parent, false);

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
            */if (dateListEs != null) {
            holder.txtBookingDate.setText(dateListEs);
        } else {
            holder.txtBookingDate.setText(dateList);
        }

       /* } else {
            holder.txtBookingDate.setText(dateList);
        }*/

        Log.i("bind--", "onBindViewHolder-->: "+listModel.getBookingId());
        holder.txtBookingId.setText(String.valueOf(listModel.getBookingId()));

        if (strLanguageShortName.equals("en")){
            holder.txtBookingCategory.setText(listModel.getCategoryName()+" - "+listModel.getServiceName());
        }else {
            if (!listModel.getCategoryAr().equals(" ")){
                if (!listModel.getServiceAr().equals(" ")){
                    holder.txtBookingCategory.setText(listModel.getCategoryAr()+" - "+listModel.getServiceAr());
                }else {
                    holder.txtBookingCategory.setText(listModel.getCategoryName()+" - "+listModel.getServiceName());
                }
            }else {
                holder.txtBookingCategory.setText(listModel.getCategoryName()+" - "+listModel.getServiceName());
            }
        }
        holder.getTxtAgentDays.setText(mContext.getResources().getString(R.string.total_days)+" :- "+listModel.getTotalDays());
        holder.txtAgentCount.setText(mContext.getResources().getString(R.string.total_guard)+":- "+listModel.getTotalAgents());
        if(listModel.getServiceStatus()== 6 ){
           // holder.txtStatus.setText("Finished");
        }else{
          //  holder.txtStatus.setText("Not Completed");
        }

        holder.txtJobLocation.setText(String.valueOf(listModel.getCustomerLocation()));

        holder.btnAcceptJobsUpcoming.setTag(position);
        holder.btnAcceptJobsUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (int) v.getTag();
                PendingJobsData listModel = orderListModel.get(pos);

                HashMap<String, Object> map = new HashMap<>();
                map.put("id", "hai");
                map.put("job", "acceptjob");
                map.put("book_id", listModel.getBookingId());

                if(successResponse!=null)
                    successResponse.onSuccess(map);
            }
        });

        holder.btnDeclineJobsUpcoming.setTag(position);
        holder.btnDeclineJobsUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                PendingJobsData listModel = orderListModel.get(pos);

                HashMap<String, Object> map = new HashMap<>();
                map.put("id", "hai");
                map.put("job", "declinejob");
                map.put("book_id", listModel.getBookingId());

                if(successResponse!=null)
                    successResponse.onSuccess(map);
            }
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
}

