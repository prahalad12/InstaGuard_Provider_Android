package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by system7 on 10/11/2017.
 */

public class ProfileData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("social_id")
    @Expose
    private String socialId;
    @SerializedName("social_type")
    @Expose
    private String socialType;
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
    private String birthDate;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("driver_certificate")
    @Expose
    private String driverCertificate;
    @SerializedName("phone_verified")
    @Expose
    private String phoneVerified;
    @SerializedName("email_verified")
    @Expose
    private String emailVerified;
    @SerializedName("service_ids")
    @Expose
    private String serviceIds;
    @SerializedName("availble_slots")
    @Expose
    private String availbleSlots;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("postal_code")
    @Expose
    private String postalCode;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("login_token")
    @Expose
    private String loginToken;
    @SerializedName("reset_pass_token")
    @Expose
    private String resetPassToken;
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
    private String loginStatus;
    @SerializedName("payment_email")
    @Expose
    private String paymentEmail;
    @SerializedName("account_holder_name")
    @Expose
    private String accountHolderName;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("bank_location")
    @Expose
    private String bankLocation;
    @SerializedName("bank_code")
    @Expose
    private String bankCode;
    @SerializedName("bank_account_number")
    @Expose
    private String bankAccountNumber;
    @SerializedName("profile_description")
    @Expose
    private String profileDescription;
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
    private String referralCode;
    @SerializedName("invite_code")
    @Expose
    private String inviteCode;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;


    @SerializedName("driver_paid")
    @Expose
    private Double driverPaid;
    @SerializedName("earn_cash")
    @Expose
    private Double earn_cash;
    @SerializedName("earnings")
    @Expose
    private Double earnings;


    @SerializedName("wallet")
    @Expose
    private Double wallet;
    @SerializedName("earn_card")
    @Expose
    private Double earn_card;
    @SerializedName("admin_paid")
    @Expose
    private Double admin_paid;

    @SerializedName("company_name")
    @Expose
    private String CompanyName;
    @SerializedName("provider_type")
    @Expose
    private String providerType;




    @SerializedName("subscription_id")
    @Expose
    private Object subscriptionId;
    @SerializedName("subscription_status")
    @Expose
    private Object subscriptionStatus;
    @SerializedName("subscription_name")
    @Expose
    private Object subscriptionName;
    @SerializedName("subscription_amount")
    @Expose
    private Object subscriptionAmount;
    @SerializedName("subscription_purchase")
    @Expose
    private Object subscriptionPurchase;
    @SerializedName("subscription_expiry")
    @Expose
    private Object subscriptionExpiry;
    @SerializedName("plan_type")
    @Expose
    private Object planType;
    @SerializedName("free_km")
    @Expose
    private Object freeKm;
    @SerializedName("free_job")
    @Expose
    private Object freeJobRequest;
    @SerializedName("subscription_description")
    @Expose
    private Object subscriptionDescription;
    @SerializedName("period_type")
    @Expose
    private Object periodType;
    @SerializedName("period")
    @Expose
    private Object period;
    @SerializedName("remeaning_job")
    @Expose
    private Object remeaningJob;

    @SerializedName("subscription_code")
    @Expose
    private Object subscriptionCode;
    @SerializedName("maximum_discount_amount")
    @Expose
    private Object maximumDiscountAmount;
    @SerializedName("tow_service_id")
    @Expose
    private Object towServiceId;
    @SerializedName("appointment_service_id")
    @Expose
    private Object appointmentServiceId;
    @SerializedName("subscription_history_id")
    @Expose
    private Integer subscriptionHistoryId;
    @SerializedName("auto_renew")
    @Expose
    private Integer autoRenew;
    @SerializedName("discount_percentage")
    @Expose
    private Integer discountPercentage;
    @SerializedName("free_appointment_services")
    @Expose
    private Object freeAppointmentService;





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

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDriverCertificate() {
        return driverCertificate;
    }

    public void setDriverCertificate(String driverCertificate) {
        this.driverCertificate = driverCertificate;
    }

    public String getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(String phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getResetPassToken() {
        return resetPassToken;
    }

    public void setResetPassToken(String resetPassToken) {
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

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getPaymentEmail() {
        return paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankLocation() {
        return bankLocation;
    }

    public void setBankLocation(String bankLocation) {
        this.bankLocation = bankLocation;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
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

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Double getDriverPaid() {
        return driverPaid;
    }

    public void setDriverPaid(Double driverPaid) {
        this.driverPaid = driverPaid;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public Double getEarnCash() {
        return earn_cash;
    }

    public void setEarnCash(Double earn_cash) {
        this.earn_cash = earn_cash;
    }

    public Double getEarnCard() {
        return earn_card;
    }

    public void setEarn_card(Double earn_card) {
        this.earn_card = earn_card;
    }

    public Double getAdminPaid() {
        return admin_paid;
    }

    public void setAdminPaid(Double admin_paid) {
        this.admin_paid = admin_paid;
    }

    public Double getEarnings() {
        return earnings;
    }

    public void setEarnings(Double earnings) {
        this.earnings = earnings;
    }public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public Object getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Object subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Object getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(Object subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public Object getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(Object subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public Object getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(Object subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public Object getSubscriptionPurchase() {
        return subscriptionPurchase;
    }

    public void setSubscriptionPurchase(Object subscriptionPurchase) {
        this.subscriptionPurchase = subscriptionPurchase;
    }

    public Object getSubscriptionExpiry() {
        return subscriptionExpiry;
    }

    public void setSubscriptionExpiry(Object subscriptionExpiry) {
        this.subscriptionExpiry = subscriptionExpiry;
    }

    public Object getPlanType() {
        return planType;
    }

    public void setPlanType(Object planType) {
        this.planType = planType;
    }

    public Object getFreeKm() {
        return freeKm;
    }

    public void setFreeKm(Object freeKm) {
        this.freeKm = freeKm;
    }

    public Object getFreeJobRequest() {
        return freeJobRequest;
    }

    public void setFreeJobRequest(Integer freeJobRequest) {
        this.freeJobRequest = freeJobRequest;
    }

    public Object getSubscriptionDescription() {
        return subscriptionDescription;
    }

    public void setSubscriptionDescription(Object subscriptionDescription) {
        this.subscriptionDescription = subscriptionDescription;
    }

    public Object getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Object periodType) {
        this.periodType = periodType;
    }

    public Object getPeriod() {
        return period;
    }

    public void setPeriod(Object period) {
        this.period = period;
    }

    public Object getRemeaningJob() {
        return remeaningJob;
    }

    public void setRemeaningJob(Object remeaningJob) {
        this.remeaningJob = remeaningJob;
    }

    public Object getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(Object subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public Object getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(Object maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public Object getTowServiceId() {
        return towServiceId;
    }

    public void setTowServiceId(Object towServiceId) {
        this.towServiceId = towServiceId;
    }

    public Object getAppointmentServiceId() {
        return appointmentServiceId;
    }

    public void setAppointmentServiceId(Object appointmentServiceId) {
        this.appointmentServiceId = appointmentServiceId;
    }

    public Integer getSubscriptionHistoryId() {
        return subscriptionHistoryId;
    }

    public void setSubscriptionHistoryId(Integer subscriptionHistoryId) {
        this.subscriptionHistoryId = subscriptionHistoryId;
    }

    public Integer getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Integer autoRenew) {
        this.autoRenew = autoRenew;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Object getFreeAppointmentService() {
        return freeAppointmentService;
    }

    public void setFreeAppointmentService(Object freeAppointmentService) {
        this.freeAppointmentService = freeAppointmentService;
    }
}