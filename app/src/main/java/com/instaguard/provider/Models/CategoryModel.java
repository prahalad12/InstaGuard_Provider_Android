package com.instaguard.provider.Models;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    private String id;
    public String getId(){
        return id;
    }
    public void setId(String cabId){
        this.id = cabId;
    }

    private boolean is_selected;
    public boolean getiIsSelected(){
        return is_selected;
    }
    public void setIsSelected(boolean is_selected){
        this.is_selected = is_selected;
    }

    private String categoryId;
    public String getCategoryId(){
        return categoryId;
    }
    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
    }

    private String categoryName;
    public String getCategoryName(){
        return categoryName;
    }
    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    private String categoryImage;
    public String getCategoryImage(){
        return categoryImage;
    }
    public void setCategoryImage(String categoryImage){
        this.categoryImage = categoryImage;
    }

    private String catId;
    public String getCatId(){
        return catId;
    }
    public void setCatId(String catId){
        this.catId = catId;
    }



    private String priceKm;
    public String getPriceKm(){
        return priceKm;
    }
    public void setPriceKm(String priceKm){
        this.priceKm = priceKm;
    }

    private String minFare;
    public String getMinFare(){
        return minFare;
    }
    public void setMinFare(String minFare){
        this.minFare = minFare;
    }

}
