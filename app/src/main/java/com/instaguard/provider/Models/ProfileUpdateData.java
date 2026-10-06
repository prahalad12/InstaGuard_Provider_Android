package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by system7 on 10/11/2017.
 */

public class ProfileUpdateData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("social_id")
    @Expose
    private Object socialId;
    @SerializedName("social_type")
    @Expose
    private Object socialType;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("birth_date")
    @Expose
    private Object birthDate;
    @SerializedName("gender")
    @Expose
    private Object gender;
    @SerializedName("driver_certificate")
    @Expose
    private String driverCertificate;
    @SerializedName("phone_verified")
    @Expose
    private Object phoneVerified;
    @SerializedName("email_verified")
    @Expose
    private Object emailVerified;
    @SerializedName("service_ids")
    @Expose
    private String serviceIds;
    @SerializedName("availble_slots")
    @Expose
    private String availbleSlots;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("state_id")
    @Expose
    private Object stateId;
    @SerializedName("country_id")
    @Expose
    private Object countryId;
    @SerializedName("postal_code")
    @Expose
    private Object postalCode;
    @SerializedName("device_type")
    @Expose
    private Object deviceType;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("login_token")
    @Expose
    private Object loginToken;
    @SerializedName("reset_pass_token")
    @Expose
    private Object resetPassToken;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("service_description")
    @Expose
    private String serviceDescription;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("wallet_balance")
    @Expose
    private Double walletBalance;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("login_status")
    @Expose
    private Integer loginStatus;
    @SerializedName("payment_email")
    @Expose
    private Object paymentEmail;
    @SerializedName("account_holder_name")
    @Expose
    private Object accountHolderName;
    @SerializedName("bank_name")
    @Expose
    private Object bankName;
    @SerializedName("bank_location")
    @Expose
    private Object bankLocation;
    @SerializedName("bank_code")
    @Expose
    private Object bankCode;
    @SerializedName("bank_account_number")
    @Expose
    private Object bankAccountNumber;
    @SerializedName("profile_description")
    @Expose
    private Object profileDescription;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("referral_code")
    @Expose
    private Object referralCode;
    @SerializedName("invite_code")
    @Expose
    private String inviteCode;
    @SerializedName("salt")
    @Expose
    private Object salt;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getSocialId() {
        return socialId;
    }

    public void setSocialId(Object socialId) {
        this.socialId = socialId;
    }

    public Object getSocialType() {
        return socialType;
    }

    public void setSocialType(Object socialType) {
        this.socialType = socialType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Object getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Object birthDate) {
        this.birthDate = birthDate;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    public String getDriverCertificate() {
        return driverCertificate;
    }

    public void setDriverCertificate(String driverCertificate) {
        this.driverCertificate = driverCertificate;
    }

    public Object getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Object phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Object getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Object emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getAvailbleSlots() {
        return availbleSlots;
    }

    public void setAvailbleSlots(String availbleSlots) {
        this.availbleSlots = availbleSlots;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getStateId() {
        return stateId;
    }

    public void setStateId(Object stateId) {
        this.stateId = stateId;
    }

    public Object getCountryId() {
        return countryId;
    }

    public void setCountryId(Object countryId) {
        this.countryId = countryId;
    }

    public Object getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Object postalCode) {
        this.postalCode = postalCode;
    }

    public Object getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Object deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Object getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(Object loginToken) {
        this.loginToken = loginToken;
    }

    public Object getResetPassToken() {
        return resetPassToken;
    }

    public void setResetPassToken(Object resetPassToken) {
        this.resetPassToken = resetPassToken;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Object getPaymentEmail() {
        return paymentEmail;
    }

    public void setPaymentEmail(Object paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public Object getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(Object accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Object getBankName() {
        return bankName;
    }

    public void setBankName(Object bankName) {
        this.bankName = bankName;
    }

    public Object getBankLocation() {
        return bankLocation;
    }

    public void setBankLocation(Object bankLocation) {
        this.bankLocation = bankLocation;
    }

    public Object getBankCode() {
        return bankCode;
    }

    public void setBankCode(Object bankCode) {
        this.bankCode = bankCode;
    }

    public Object getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(Object bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Object getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(Object profileDescription) {
        this.profileDescription = profileDescription;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Object getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(Object referralCode) {
        this.referralCode = referralCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Object getSalt() {
        return salt;
    }

    public void setSalt(Object salt) {
        this.salt = salt;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

}