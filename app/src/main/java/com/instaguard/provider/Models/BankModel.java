package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by system7 on 10/11/2017.
 */

public class BankModel {

        @SerializedName("stripe_document_status")
        @Expose
        private Integer stripeDocumentStatus;
        @SerializedName("data")
        @Expose
        private ArrayList<Datum> data;
        @SerializedName("message")
        @Expose
        private String message;

        public Integer getStripeDocumentStatus() {
            return stripeDocumentStatus;
        }

        public void setStripeDocumentStatus(Integer stripeDocumentStatus) {
            this.stripeDocumentStatus = stripeDocumentStatus;
        }

        public ArrayList<Datum> getData() {
            return data;
        }

        public void setData(ArrayList<Datum> data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    public class Datum {

        @SerializedName("bank_name")
        @Expose
        private String bankName;
        @SerializedName("account_holder_name")
        @Expose
        private String accountHolderName;
        @SerializedName("routing_number")
        @Expose
        private String routingNumber;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("last_digit")
        @Expose
        private String lastDigit;
        @SerializedName("bank_status")
        @Expose
        private String bankStatus;
        @SerializedName("is_default")
        @Expose
        private Integer isDefault;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountHolderName() {
            return accountHolderName;
        }

        public void setAccountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
        }

        public String getRoutingNumber() {
            return routingNumber;
        }

        public void setRoutingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getLastDigit() {
            return lastDigit;
        }

        public void setLastDigit(String lastDigit) {
            this.lastDigit = lastDigit;
        }

        public String getBankStatus() {
            return bankStatus;
        }

        public void setBankStatus(String bankStatus) {
            this.bankStatus = bankStatus;
        }

        public Integer getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(Integer isDefault) {
            this.isDefault = isDefault;
        }

    }

}