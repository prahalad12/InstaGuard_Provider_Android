package com.instaguard.provider.Adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.instaguard.provider.Models.PayoutTransactionHistoryModel;
import com.instaguard.provider.R;
import com.instaguard.provider.databinding.LayoutPayoutTransactionBinding;

import java.util.ArrayList;

public class PayoutTransactionAdapter extends RecyclerView.Adapter<PayoutTransactionAdapter.PayoutViewHolder> {
    ArrayList<PayoutTransactionHistoryModel.Result> list;

    public PayoutTransactionAdapter(ArrayList<PayoutTransactionHistoryModel.Result> listData) {
        this.list = listData;
    }

    @NonNull
    @Override
    public PayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_payout_transaction, parent, false);
        return new PayoutViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull PayoutViewHolder holder, int position) {
        System.out.println("payout transaction id is "+ list.get(position).getId());

        String refId = list.get(position).getId().toString();
        String status = list.get(position).getStatus();
        String time = list.get(position).getRequestTime();
        String time_= list.get(position).getPaidTime();
        String date = list.get(position).getRequestDate();
        String date_ = list.get(position).getPaidDate();
        String amt = list.get(position).getAmount().toString();
        String statusDescription = list.get(position).getDescription();

        if (status.equals("Paid")) {
            holder.time.setText(time_);
            holder.date.setText(date_);
        } else {
            holder.time.setText(time);
            holder.date.setText(date);
        }

        holder.refNoShow.setText(refId);
        holder.status.setText(status);
        holder.payoutAmt.setText("£" + amt);
        holder.statusDesc.setText(statusDescription);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PayoutViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView refNoShow, status, time,date, payoutAmt, statusDesc;
        public PayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            refNoShow = itemView.findViewById(R.id.refNoShow);
            status = itemView.findViewById(R.id.status);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            payoutAmt = itemView.findViewById(R.id.payoutAmt);
            statusDesc = itemView.findViewById(R.id.statusDesc);
        }
    }
}
