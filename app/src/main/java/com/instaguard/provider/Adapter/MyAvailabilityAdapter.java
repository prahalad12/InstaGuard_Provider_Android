package com.instaguard.provider.Adapter;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instaguard.provider.Helpers.CustomCheckButton.CheckableButton;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AvailabilityDataExtra;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyAvailabilityAdapter extends RecyclerView.Adapter<MyAvailabilityAdapter.MyViewHolder> {

    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    private FragmentActivity mContext;
    private LayoutInflater inflater;
    private List<AvailabilityDataExtra> produkList;

    private SharedPreferences getspCommonState, getspCommonCurrency;
    private String userId, strLanguageShortName, strCurrencySymbol, userAccessToken;


    public MyAvailabilityAdapter(FragmentActivity context, List<AvailabilityDataExtra> produkList) {
        this.mContext = context;
        this.produkList = produkList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyAvailabilityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.adapter_myavailability, parent, false);

        getspCommonState = mContext.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        getspCommonCurrency = mContext.getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strCurrencySymbol = getspCommonCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguageShortName = getspCommonCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);

        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        MyViewHolder holder = new MyViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAvailabilityAdapter.MyViewHolder holder, int position) {

//String[] dayArray = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saterday"};
//        Sun: الأحد
//        Mon: الأثنين
//        Tue: الثلاثاء
//        Wed: الأربعاء
//        Thu: الخميس
//        Fri: الجمعة
//        Sat: السبت
        switch (produkList.get(position).text) {
            case "Sunday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Sunday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Monday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Monday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Tuesday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Tuesday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Wednesday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Wednesday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Thursday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Thursday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Friday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.February_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            case "Saterday":
                if (strLanguageShortName.equals("en")) {
                    holder.checkableBtn.setText(produkList.get(position).text);
                } else if (strLanguageShortName.equals("si")) {
                    holder.checkableBtn.setText(mContext.getResources().getString(R.string.Saterday_3));
                } else {
                    holder.checkableBtn.setText(produkList.get(position).text);
                }
                break;
            default:
                holder.checkableBtn.setText(produkList.get(position).text);
                break;
        }


        if (produkList.get(position).size > 0) {
            holder.checkableBtn.setChecked(true);
        }
        if (isChecked.containsKey(position)) {
            //holder.cbProduk.setChecked(isChecked.get(position));
        } else {
            // holder.cbProduk.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // CheckBox cbProduk;
        CheckableButton checkableBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            //cbProduk = (CheckBox) itemView.findViewById(R.id.cbProduk);
            checkableBtn = (CheckableButton) itemView.findViewById(R.id.myavailability_checkablebutton);

            checkableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("on", "onClick:-->checkableBtn ");
                   /* String Checked= "Button Unchecked";
                    if(checkableBtn.isChecked()){
                        Checked = "Button Checked";
                    }
                    Toast.makeText(mContext, Checked, Toast.LENGTH_SHORT).show();*/
                   /* Bundle args = new Bundle();
                    args.putString(Utils.B_TIME_LIST,produkList.get(position).text);
                    Utils.switchContent(R.id.frame_mainactivity,
                            Utils.FRAGMENT_MYAVAILABILITY_SUB,
                            mContext, null,
                            Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/
                }
            });

            /*checkableBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked){
                       // KONDISI PADA SAAT CEKLIS
                       Toast.makeText(mContext, "checklist", Toast.LENGTH_SHORT).show();
                   } else {
                       // KONDISI PADA SAAT CEKLIS DIHILANGKAN
                       Toast.makeText(mContext, "unchecklist", Toast.LENGTH_SHORT).show();
                   }
                }
            });*/
        }
    }
}
