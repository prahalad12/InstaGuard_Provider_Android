package com.instaguard.provider.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.RideHistoryData;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BonusAllAdapter extends RecyclerView.Adapter<BonusAllAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<RideHistoryData> dataList_;

    public static SuccessResponse successResponse;
    SharedPreferences prefs;
    int i = 1;
    private SharedPreferences getspCommonState, getspCommonCurrency;
    private String userId, strLanguageShortName, strCurrencySymbol, userAccessToken;
    private SharedPreferences.Editor putSpState;


    public interface SuccessResponse {
        void onSuccess(HashMap<String, Object> map);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView txtTransactionBalance, txtTransactionDate, txtTransactionBookingid, txtTransactionMode;
        ImageView iv_credit_debit_img;
        LinearLayout ll_adapter_wallet;

        MyViewHolder(View view) {
            super(view);

            ll_adapter_wallet = (LinearLayout) view.findViewById(R.id.ll_adapter_wallet);
            iv_credit_debit_img = (ImageView) view.findViewById(R.id.iv_credit_debit_img);

            txtTransactionBalance = (CustomTextView) view.findViewById(R.id.txt_transaction_balance);
            txtTransactionBookingid = (CustomTextView) view.findViewById(R.id.txt_transaction_bookingid);
            txtTransactionDate = (CustomTextView) view.findViewById(R.id.txt_transaction_date);
            txtTransactionMode = (CustomTextView) view.findViewById(R.id.txt_transaction_mode);


        }
    }

    public BonusAllAdapter(Context mContext, ArrayList<RideHistoryData> contact) {
        this.mContext = mContext;
        this.dataList_ = contact;
    }

    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_bonus_history, parent, false);

        putSpState = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getspCommonState = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        getspCommonCurrency = mContext.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        userId = getspCommonState.getString(Utils.SP_DRIVERID, null);
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        strCurrencySymbol = getspCommonCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguageShortName = getspCommonCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);

        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        RideHistoryData listModel = dataList_.get(position);

        String upperString = "";
        String upperStringAmt = "0";

        if (listModel.getJobId() != null && !listModel.getJobId().equals("") && !listModel.getJobId().equals("null")) {
            upperString = "Bonus received from Job Id" + " :- " + listModel.getJobId();
        }
        holder.txtTransactionBookingid.setText(upperString);


        final String dateList;
        String dateListEs = null;
        // dateList = listModel.getTransactionDate(); //"Thu,Jan 30,2020 05:12 AM",
        dateList = listModel.getCreatedAt(); //"2020-03-10 10:03:42",
        /*try {

            String[] separated = dateList.split(",");
            String day = separated[0]; //Thu
            String monthDate = separated[1]; // Jan 30
            String yearTime = separated[2]; // 2020 05:12 AM"

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

            dateListEs = dayLabel + "," + monthLabel + " " + date + "," + yearTime;

        } catch (Exception e) {
            e.printStackTrace();
            dateListEs = null;
        }*/

        holder.txtTransactionDate.setText(dateList);

        if (listModel.getJobId() != null && !listModel.getJobId().equals("") && !listModel.getJobId().equals("null")) {
            upperStringAmt = String.valueOf(listModel.getBonusAmount());
        }
        holder.txtTransactionBalance.setText(strCurrencySymbol + " " + convertCurrencyPrice(upperStringAmt));

        /*switch (listModel.getTransactionType()) {
            case "debit":
                holder.iv_credit_debit_img.setImageResource(R.mipmap.ic_debit);

                break;
            case "debit ":
                holder.iv_credit_debit_img.setImageResource(R.mipmap.ic_debit);

                break;
            case "Debit":
                holder.iv_credit_debit_img.setImageResource(R.mipmap.ic_debit);

                break;
            case "credit":
                holder.iv_credit_debit_img.setImageResource(R.mipmap.ic_credit);
                break;
            default:
                holder.iv_credit_debit_img.setImageResource(R.mipmap.ic_close_detail);
                break;
        }*/

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<RideHistoryData> eArrayList) {
        dataList_.addAll(eArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList_ == null ? 0 : dataList_.size();
    }


    /*  public void updateList(List<Contact> list){
          orderListModel = list;
          notifyDataSetChanged();
      }*/
    public String convertCurrencyPrice(String price) {
        String currencyCommonPrice = getspCommonCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
        double multiplePrice = Double.parseDouble(currencyCommonPrice) * Double.parseDouble(price);
        double roundedNumber = DecimalUtils.round(multiplePrice, 2);
        return String.valueOf(roundedNumber);
    }
}

