package com.instaguard.provider.Adapter;

import static java.lang.System.out;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.R;

import java.util.ArrayList;

public class CityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> filteredCitiesList;

    public CityListAdapter(FragmentActivity activity, ArrayList<String> citiesnamesArrayList) {
        this.filteredCitiesList = new ArrayList<>(citiesnamesArrayList);
    }


    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_citylist, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CityListAdapter.MyViewHolder myViewHolder = (CityListAdapter.MyViewHolder) holder;

        out.println("print list get cities -->" + filteredCitiesList.get(position));
        String getName = String.valueOf(filteredCitiesList.get(position));

        myViewHolder.tvCityName.setText(getName);
        myViewHolder.itemView.setTag(position);

        myViewHolder.itemView.setOnClickListener(v -> {
            int pos = (int) v.getTag();
            String listModels = filteredCitiesList.get(pos);
            out.println("selected city pos~~~>>>" + listModels);
        });
    }

    public int getItemCount() {
        return this.filteredCitiesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(ArrayList<String> filteredNames) {
        this.filteredCitiesList = new ArrayList<>(filteredNames);
        notifyDataSetChanged();
    }
    public ArrayList<String> getFilteredCitiesList() {
        return filteredCitiesList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName;

        MyViewHolder(View itemView) {
            super(itemView);
            tvCityName = (CustomTextView) itemView.findViewById(R.id.cityName);
        }
    }
}
