package com.instaguard.provider.Models;

import java.io.Serializable;

public class CategorySubModel implements Serializable {

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

    private String SubCategoryId;
    public String getSubCategoryId(){
        return SubCategoryId;
    }
    public void setSubCategoryId(String SubCategoryId){
        this.SubCategoryId = SubCategoryId;
    }

    private String SubCategoryName;
    public String getSubCategoryName(){
        return SubCategoryName;
    }
    public void setSubCategoryName(String SubCategoryName){
        this.SubCategoryName = SubCategoryName;
    }

    private String SubCategoryImage;
    public String getSubCategoryImage(){
        return SubCategoryImage;
    }
    public void setSubCategoryImage(String SubCategoryImage){
        this.SubCategoryImage = SubCategoryImage;
    }

    private String SubMainCategoryId;
    public String getSubMainCategoryId(){
        return SubMainCategoryId;
    }
    public void setSubMainCategoryId(String SubMainCategoryId){
        this.SubMainCategoryId = SubMainCategoryId;
    }


    private String SubCategoryBasefare;
    public String getSubCategoryBasefare(){
        return SubCategoryBasefare;
    }
    public void setSubCategoryBasefare(String SubCategoryBasefare){
        this.SubCategoryBasefare = SubCategoryBasefare;
    }

    private String SubCategoryAdminComm;
    public String getSubCategoryAdminComm(){
        return SubCategoryAdminComm;
    }
    public void setSubCategoryAdminComm(String SubCategoryAdminComm){
        this.SubCategoryAdminComm = SubCategoryAdminComm;
    }

    private String subSetSelected;
    public String getSetSelected(){
        return subSetSelected;
    }
    public void setSetSelected(String subSetSelected){
        this.subSetSelected = subSetSelected;
    }
}
