package com.instaguard.provider.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.R;
import com.instaguard.provider.databinding.LayoutBankCardBinding;

import java.util.ArrayList;

public class BankDetailsAdapter extends RecyclerView.Adapter<BankDetailsAdapter.BankDetailsViewHolder> {

    ArrayList<BankModel.Datum> list;
    public BankDetailsAdapter(ArrayList<BankModel.Datum> bankList) {
        this.list = bankList;
    }

    @NonNull
    @Override
    public BankDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BankDetailsViewHolder(LayoutBankCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BankDetailsViewHolder holder, int position) {
        BankModel.Datum listData = list.get(position);
        holder.layoutBankCardBinding.bankName.setText(listData.getBankName());
        holder.layoutBankCardBinding.bankUserName.setText(listData.getAccountHolderName());
        holder.layoutBankCardBinding.cardNumber.setText(holder.itemView.getContext().getString(R.string.add_bank_account_digit, listData.getLastDigit()));

        if (listData.getIsDefault() == 1) {
            holder.layoutBankCardBinding.status.setVisibility(View.VISIBLE);
            holder.layoutBankCardBinding.status.setText(R.string.default_);
        } else {
            holder.layoutBankCardBinding.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class BankDetailsViewHolder extends RecyclerView.ViewHolder{
        private LayoutBankCardBinding layoutBankCardBinding;
        public BankDetailsViewHolder(LayoutBankCardBinding layoutBankCardBinding) {
            super(layoutBankCardBinding.getRoot());
            this.layoutBankCardBinding = layoutBankCardBinding;
        }
    }
}
