package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class WalletAllData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("transaction_token")
    @Expose
    private Integer transactionToken;
    @SerializedName("customer_id")
    @Expose
    private Object customerId;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;
    @SerializedName("transaction_amount")
    @Expose
    private Double transactionAmount;
    @SerializedName("transaction_mode")
    @Expose
    private String transactionMode;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("transaction_notes")
    @Expose
    private String transactionNotes;
    @SerializedName("strip_response")
    @Expose
    private String stripResponse;
    @SerializedName("transaction_status")
    @Expose
    private Integer transactionStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("transaction_notes_ar")
    @Expose
    private String transactionNotesAr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionToken() {
        return transactionToken;
    }

    public void setTransactionToken(Integer transactionToken) {
        this.transactionToken = transactionToken;
    }

    public Object getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Object customerId) {
        this.customerId = customerId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTransactionNotes() {
        return transactionNotes;
    }

    public void setTransactionNotes(String transactionNotes) {
        this.transactionNotes = transactionNotes;
    }

    public String getTransactionNotesAr() {
        return transactionNotesAr;
    }

    public void setTransactionNotesAr(String transactionNotesAr) {
        this.transactionNotesAr = transactionNotesAr;
    }

    public String getStripResponse() {
        return stripResponse;
    }

    public void setStripResponse(String stripResponse) {
        this.stripResponse = stripResponse;
    }

    public Integer getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Integer transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}