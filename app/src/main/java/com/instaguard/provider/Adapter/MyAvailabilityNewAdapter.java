package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.instaguard.provider.Helpers.CustomCheckButton.CheckableButton;
import com.instaguard.provider.Models.AvailabilityDataTimeExtra;
import com.instaguard.provider.R;
import java.util.HashMap;
import java.util.List;

public class MyAvailabilityNewAdapter extends RecyclerView.Adapter<MyAvailabilityNewAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final List<AvailabilityDataTimeExtra> produkList;
    public static SuccessResponse successResponse;

    public MyAvailabilityNewAdapter(Context context, List<AvailabilityDataTimeExtra> produkList){
        this.produkList = produkList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyAvailabilityNewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.adapter_myavailability_new, parent, false);
        return new MyViewHolder(rootView);
    }
    public interface SuccessResponse{
        void onSuccess(int text);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAvailabilityNewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyViewHolder.checkableBtn.setText(produkList.get(position).text);
        if(produkList.get(position).selected){
            MyViewHolder.checkableBtn.setChecked(true);
        }
        MyViewHolder.checkableBtn.setOnClickListener(view -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("slected_time",position);
            if(successResponse!=null)
                successResponse.onSuccess(position);
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public static CheckableButton checkableBtn;
        public MyViewHolder(View itemView) {
            super(itemView);
            checkableBtn = itemView.findViewById(R.id.myavailability_checkablebutton);
        }
    }
}
