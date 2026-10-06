package com.instaguard.provider.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.instaguard.provider.Helpers.CustomCheckButton.CheckableButton;
import com.instaguard.provider.Models.AvailabilityDataExtra;
import com.instaguard.provider.Models.AvailabilityDataTimeExtra;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.List;

public class MyAvailabilitySubAdapter extends RecyclerView.Adapter<MyAvailabilitySubAdapter.MyViewHolder> {

    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    private Context mContext;
    private LayoutInflater inflater;
    private List<AvailabilityDataTimeExtra> produkList;
    public static SuccessResponse successResponse;

    public MyAvailabilitySubAdapter(Context context, List<AvailabilityDataTimeExtra> produkList){
        this.mContext = context;
        this.produkList = produkList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyAvailabilitySubAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.adapter_myavailabilitysub, parent, false);

        MyViewHolder holder = new MyViewHolder(rootView);
        return holder;
    }
    public interface SuccessResponse{
        void onSuccess(int text);
    }
    @Override
    public void onBindViewHolder(MyAvailabilitySubAdapter.MyViewHolder holder, int position) {
        holder.checkableBtn.setText(produkList.get(position).text);
        if(produkList.get(position).selected){
            holder.checkableBtn.setChecked(true);
        }
        /*if (isChecked.containsKey(position)){
            //holder.cbProduk.setChecked(isChecked.get(position));
        } else {
           // holder.cbProduk.setChecked(false);
        }*/
        holder.checkableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("slected_time",position);
                if(successResponse!=null)
                    successResponse.onSuccess(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

       // CheckBox cbProduk;
        public static CheckableButton checkableBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkableBtn = (CheckableButton) itemView.findViewById(R.id.myavailability_checkablebutton);


        }
    }
}
