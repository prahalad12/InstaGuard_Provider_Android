package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class EndRequestDataModel {

    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("material_fee")
    @Expose
    private String materialFee;
    @SerializedName("misc_charge")
    @Expose
    private String miscCharge;
    @SerializedName("distance")
    @Expose
    private Object distance;
    @SerializedName("driver_discount")
    @Expose
    private String driverDiscount;
    @SerializedName("driver_wallet_balance")
    @Expose
    private Double driverWalletBalance;
    @SerializedName("customer_wallet_balance")
    @Expose
    private Double customerWalletBalance;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMaterialFee() {
        return materialFee;
    }

    public void setMaterialFee(String materialFee) {
        this.materialFee = materialFee;
    }

    public String getMiscCharge() {
        return miscCharge;
    }

    public void setMiscCharge(String miscCharge) {
        this.miscCharge = miscCharge;
    }

    public Object getDistance() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance = distance;
    }

    public String getDriverDiscount() {
        return driverDiscount;
    }

    public void setDriverDiscount(String driverDiscount) {
        this.driverDiscount = driverDiscount;
    }

    public Double getDriverWalletBalance() {
        return driverWalletBalance;
    }

    public void setDriverWalletBalance(Double driverWalletBalance) {
        this.driverWalletBalance = driverWalletBalance;
    }

    public Double getCustomerWalletBalance() {
        return customerWalletBalance;
    }

    public void setCustomerWalletBalance(Double customerWalletBalance) {
        this.customerWalletBalance = customerWalletBalance;
    }

}