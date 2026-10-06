package com.instaguard.provider.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.CategoryModel;
import com.instaguard.provider.Models.CategorySubModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapCategorySubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CategorySubModel> categoryArrayList;
    private static int selected_pos = -1;
    private Context context;
    public static SuccessResponse successResponse;
    public String TAG="MapCategorySubAdapter";
    public MapCategorySubAdapter( Context context,ArrayList<CategorySubModel> categoryArrayList) {
        this.categoryArrayList=categoryArrayList;
        this.context=context;
    }
    public interface SuccessResponse{
        void onSuccess(int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mapsubcategory, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        Log.e("current position   "+position,"selected position   "+selected_pos);

        if (categoryArrayList.get(position).getSubCategoryImage()!=null){
            if (!categoryArrayList.get(position).getSubCategoryImage().equals("null")){
                if (!categoryArrayList.get(position).getSubCategoryImage().equals("")){
                    myViewHolder.imgCategory.setImageURI(Utils.BASE_URL_CATEGORY_IMAGE + categoryArrayList.get(position).getSubCategoryImage());
                }
            }
        }


        String tempCategoryName = categoryArrayList.get(position).getSubCategoryName();
        tempCategoryName = tempCategoryName.replaceAll("_", " ");
        myViewHolder.txtCategoryName.setText(tempCategoryName);
        myViewHolder.chkSubCategory.setChecked(categoryArrayList.get(position).getiIsSelected());


        myViewHolder.chkSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: "+categoryArrayList.get(position).getSubCategoryId());
                if(successResponse!=null)
                    successResponse.onSuccess(Integer.parseInt(categoryArrayList.get(position).getSubCategoryId()));
            }
        });

    }
    public void selected(int position){
        Log.i("selected", "selected: ");
        selected_pos = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory1;
        SimpleDraweeView imgCategory;
        CustomTextView txtCategoryName;
        CheckBox chkSubCategory;

        MyViewHolder(View itemView) {
            super(itemView);
            imgCategory =  itemView.findViewById(R.id.img_category);
            imgCategory1 = (ImageView) itemView.findViewById(R.id.img_category1);
            txtCategoryName = (CustomTextView) itemView.findViewById(R.id.txt_category_name);
            chkSubCategory = (CheckBox) itemView.findViewById(R.id.chk_sub_category);
        }
    }

}










