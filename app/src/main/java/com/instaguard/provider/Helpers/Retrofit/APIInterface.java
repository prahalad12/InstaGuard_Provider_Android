package com.instaguard.provider.Helpers.Retrofit;


import com.instaguard.provider.Models.AcceptRequestModel;
import com.instaguard.provider.Models.AppSettingWebModel;
import com.instaguard.provider.Models.AppSupportModel;
import com.instaguard.provider.Models.AppSupportPagesModel;
import com.instaguard.provider.Models.Availability.NewDateAvailListModel;
import com.instaguard.provider.Models.Availability.UpdateAvailListModel;
import com.instaguard.provider.Models.Availability.ViewAvailListModel;
import com.instaguard.provider.Models.AvailabilityModel;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.BankUpdateModel;
import com.instaguard.provider.Models.CancelJobModel;
import com.instaguard.provider.Models.ChangePasswordModel;
import com.instaguard.provider.Models.ChatAccessModel;
import com.instaguard.provider.Models.CitiesListModel;
import com.instaguard.provider.Models.DeclinePendingModel;
import com.instaguard.provider.Models.DeleteModel;
import com.instaguard.provider.Models.DocumentViewModel_;
import com.instaguard.provider.Models.DocumentsUpload.DocumentListData;
import com.instaguard.provider.Models.DocumentsUpload.DocumentViewModel;
import com.instaguard.provider.Models.DocumentsUpload.ReUploadModel;
import com.instaguard.provider.Models.DocumentsUpload.UploadDocModel;
import com.instaguard.provider.Models.DocumentsUpload.UploadModel;
import com.instaguard.provider.Models.DriverLocationUpdateModel;
import com.instaguard.provider.Models.EmailMobileCheckingModel;
import com.instaguard.provider.Models.EmergencyAddModel;
import com.instaguard.provider.Models.EmergencyDeleteModel;
import com.instaguard.provider.Models.EmergencyViewModel;
import com.instaguard.provider.Models.EndRequestModel;
import com.instaguard.provider.Models.ForgetPassModel;
import com.instaguard.provider.Models.ForgotPasswordModel;
import com.instaguard.provider.Models.GeofencePushNotificationModel;
import com.instaguard.provider.Models.LinkedInUserDetailsModel;
import com.instaguard.provider.Models.LoginResults;
import com.instaguard.provider.Models.LogoutModel;
import com.instaguard.provider.Models.PanicAddAdminModel;
import com.instaguard.provider.Models.PastJobsModel;
import com.instaguard.provider.Models.PaymentUpdateModel;
import com.instaguard.provider.Models.PayoutModel;
import com.instaguard.provider.Models.PayoutTransactionHistoryModel;
import com.instaguard.provider.Models.PendingJobsModel;
import com.instaguard.provider.Models.ProfileModel;
import com.instaguard.provider.Models.ProfilePicModel;
import com.instaguard.provider.Models.ProfileUpdateModel;
import com.instaguard.provider.Models.PushModel;
import com.instaguard.provider.Models.RatingUserModel;
import com.instaguard.provider.Models.ReUploadModel_;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.RegisterModel;
import com.instaguard.provider.Models.Request.SendRequestModel;
import com.instaguard.provider.Models.RequestStatusModel;
import com.instaguard.provider.Models.ResetPasswordModel;
import com.instaguard.provider.Models.RideHistoryModel;
import com.instaguard.provider.Models.StartUpcomingTripModel;
import com.instaguard.provider.Models.StripeEndTModel;
import com.instaguard.provider.Models.Subscription.BuyPlanModel;
import com.instaguard.provider.Models.Subscription.BuyPlanWebHookModel;
import com.instaguard.provider.Models.Subscription.CancelAutoRenewPlanModel;
import com.instaguard.provider.Models.Subscription.CheckPlanModel;
import com.instaguard.provider.Models.Subscription.ListViewSubsModel;
import com.instaguard.provider.Models.Subscription.SubsHistoryModel;
import com.instaguard.provider.Models.Subscription.UpdateSubsModel;
import com.instaguard.provider.Models.Subscription.ViewSubsModel;
import com.instaguard.provider.Models.UpcomingJobsModel;
import com.instaguard.provider.Models.UploadDocModel_;
import com.instaguard.provider.Models.UserFeedbackModel;
import com.instaguard.provider.Models.VehicleCategoryModel;
import com.instaguard.provider.Models.VehicleSubCategoryModel;
import com.instaguard.provider.Models.VerifyMobileModel;
import com.instaguard.provider.Models.VerifyOtpModel;
import com.instaguard.provider.Models.WalletAddMoneyModel;
import com.instaguard.provider.Models.WalletAllModel;
import com.instaguard.provider.Models.SocialLogModel.SocialLoginModel;
import com.instaguard.provider.Models.SocialLogModel.SocialSignupModel;
import com.instaguard.provider.Models.WebResModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by uplogic on 20/04/2018.
 */


public interface APIInterface {

    //Login Response
    @FormUrlEncoded
    @POST("auth/logind?")
    Call<LoginResults> doGetLoginDetails(@FieldMap HashMap<String, Object> map1);

    //Register Response
    @FormUrlEncoded
    @POST("auth/registerDriver?")
    Call<RegisterModel> doGetRegisterDetails(@FieldMap HashMap<String, Object> map1);

    //Mobile email checking Response
    @FormUrlEncoded
    @POST("emailAvailable?")
    Call<EmailMobileCheckingModel> doGetEmailMobileChecking(@FieldMap HashMap<String, Object> map1);

    //Panic save db Response
    @FormUrlEncoded
    @POST("savepanic?")
    Call<PanicAddAdminModel> doUpdateAddPanic(@FieldMap HashMap<String, Object> map1);


    //Logout  Response
    @FormUrlEncoded
    @POST("auth/logoutd?")
    Call<LogoutModel> doGetLogoutDetails(@FieldMap HashMap<String, Object> map1);

    //Refresh Token  Response
    @FormUrlEncoded
    @POST("auth/refreshd?")
    Call<RefreshTokenModel> doGetRefreshToken(@Header("Accept") String accept, @Header("Content-Type") String content_type, @FieldMap HashMap<String, Object> map1);

    //SocialLog
    @FormUrlEncoded
    @POST("auth/SocialLogDriver?")
    Call<SocialLoginModel> doGetSocialLoginDetailsdd(@FieldMap HashMap<String, Object> map1);


    //Social
    @FormUrlEncoded
    @POST("driver/socialAdd")
    Call<SocialLoginModel> doGetSocialLoginDetails(@FieldMap HashMap<String, Object> map1);

    //Social Register Response
    @FormUrlEncoded
    @POST("driver/socialUpdate")
    Call<SocialSignupModel> doSocialLoginUpdate(@FieldMap HashMap<String, Object> map1);


    @FormUrlEncoded
    @POST("forgot_password?")
    Call<ForgotPasswordModel> doUpdateForgotPassword(@FieldMap HashMap<String, Object> map1);

    //Verify Mobile response
    @FormUrlEncoded
    @POST("verifyMobile?")
    Call<VerifyMobileModel> doUpdateverifyMobile(@FieldMap HashMap<String, Object> map1);

    //Update Password
    @FormUrlEncoded
    @POST("updatePassword?")
    Call<ResetPasswordModel> doUpdateResetPassword(@FieldMap HashMap<String, Object> map1);

    //Verify Otp
    @FormUrlEncoded
    @POST("verifyotp")
    Call<VerifyOtpModel> doVerifyOtp(@FieldMap HashMap<String, Object> map1);

    //Forgot Password
    @FormUrlEncoded
    @POST("forgetpass")
    Call<ForgetPassModel> doForgotPassword(@FieldMap HashMap<String, Object> map1);

    //Profile View  Response
    @FormUrlEncoded 
    @POST("profileViewDriver?")
    Call<ProfileModel> doGetprofileDetails(@FieldMap HashMap<String, Object> map1);

    //Profile Update  Response
    @FormUrlEncoded
    @POST("updateprofileDriver?")
    Call<ProfileUpdateModel> doGetprofileUpdateDetails(@FieldMap HashMap<String, Object> map1);

    //Profile Photo Update  Response
    @FormUrlEncoded
    @POST("picuploadDriver?")
    Call<ProfilePicModel> doGetprofilePhotoDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Vehicle Category Response
    @FormUrlEncoded
    @POST("CategoriesDriver?")
    Call<VehicleCategoryModel> doGetVehiclecategoryUpdateDetails(@Header("Authorization") String token, @Header("Accept") String accept, @Header("Content-Type") String content_type, @FieldMap HashMap<String, Object> map1);

    //Vehicle Category Response
    @FormUrlEncoded
    @POST("AllServicesDriver?")
    Call<VehicleCategoryModel> doGetVehiclecategoryAllServicesDriver(@Header("Authorization") String token, @Header("Accept") String accept, @Header("Content-Type") String content_type, @FieldMap HashMap<String, Object> map1);

    //Vehicle Category Response id(send) & token
    @FormUrlEncoded
    @POST("ServiceDriver?")
    Call<VehicleSubCategoryModel> doGetVehiclecategoryServicesDriver(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Update Vehicle Selected Category
    @FormUrlEncoded
    @POST("ServiceSelection?")
    Call<AvailabilityModel> doUpdateVehicleCategory(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);


    //Profile Update  Response
    @FormUrlEncoded
    @POST("checkin?")
    Call<DriverLocationUpdateModel> doDriverUpdateLocation(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);

    //Available View  Response
    @FormUrlEncoded
    @POST("ServiceSelectionGet?")
    Call<AvailabilityModel> doGetAvailable(@FieldMap HashMap<String, Object> map1);

    //Update Available
    @FormUrlEncoded
    @POST("ServiceSelection?")
    Call<AvailabilityModel> doUpdateAvailable(@FieldMap HashMap<String, Object> map1);

    //ADD Emergency Response
    @FormUrlEncoded
    @POST("emergencyContactDriver?")
    Call<EmergencyAddModel> doAddemergencyContactCustomer(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Vuew Emergency Response
    @FormUrlEncoded
    @POST("emergencyContactGETDriver?")
    Call<EmergencyViewModel> doGetViewEmergencyDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Vuew Emergency Response
    @FormUrlEncoded
    @POST("emergencyContactDeleteDriver?")
    Call<EmergencyDeleteModel> doGetDeleteEmergencyDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Bank View  Response
    @FormUrlEncoded
    @POST("bankDetailsGet?")
    Call<BankModel> doGetBankDetails(@FieldMap HashMap<String, Object> map);

    //Bank Update  Response
    @FormUrlEncoded
    @POST("bankDetailsUpdate?")
    Call<BankUpdateModel> doBankUpdateDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);


    //Request ACCEPT Response
    @FormUrlEncoded
    @POST("requestService?")
    Call<AcceptRequestModel> doAcceptRequestDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);

    //Request Response
    @FormUrlEncoded
    @POST("requestService?")
    Call<RequestStatusModel> doGetRequestStatusDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);

    //End Request Response
    @FormUrlEncoded
    @POST("requestService?")
    Call<EndRequestModel> doGetEndRequestStatusDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);


    //Past Jobs Response
    @FormUrlEncoded
    @POST("pastjobs?")
    Call<PastJobsModel> doCustomerPastJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Upcoming Jobs Response
    @FormUrlEncoded
    @POST("upcomingJobs?")
    Call<UpcomingJobsModel> doCustomerUpcomingJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Pending Jobs Response
    @FormUrlEncoded
    @POST("pendingJobs?")
    Call<PendingJobsModel> doCustomerPendingJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);


    //Vuew Emergency Response
    @FormUrlEncoded
    @POST("driverAddMoney?")
    Call<WalletAddMoneyModel> doUpdateWalletAmount(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);


    //Document Photo Update  Response
    @FormUrlEncoded
    @POST("documentUpload?")
    Call<UploadDocModel_> doUPdateDocumentDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Re Document Photo Update  Response
    @FormUrlEncoded
    @POST("proofreupload?")
    Call<ReUploadModel_> doReupdateDocumentDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);


    //CancelJobs Update  Response
    @FormUrlEncoded
    @POST("cancelJobs?")
    Call<CancelJobModel> doCancelJobDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Decline Pending Jobs Update  Response
    @FormUrlEncoded
    @POST("scheduledcancelJobs?")
    Call<DeclinePendingModel> doDeclinePendingJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Accept Pending Jobs Update  Response
    @FormUrlEncoded
    @POST("scheduleJobAccept?")
    Call<DeclinePendingModel> doAcceptPendingJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //Start Upcoming Jobs Update  Response
    @FormUrlEncoded
    @POST("scheduletripStart?")
    Call<StartUpcomingTripModel> doStartUpcomingJobs(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //CancelJobs Update  Response
    @FormUrlEncoded
    @POST("DriverFeedbackStore?")
    Call<RatingUserModel> doUpdateRatingUser(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //CancelJobs Update  Response
    @POST("appsetting")
    Call<AppSettingWebModel> doGetAppSettings();

    //Upcoming Jobs Response
    @FormUrlEncoded
    @POST("DriverFeedback?")
    Call<UserFeedbackModel> doGetUserFeedback(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);


    //Support Response id(send) & token
    @FormUrlEncoded
    @POST("pages?")
    Call<AppSupportPagesModel> doGetSupportDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Support sub page Response id(send) & token
    @FormUrlEncoded
    @POST("Support?")
    Call<AppSupportModel> doGetAppSupportDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);


    //Profile Photo Update  Response
    @FormUrlEncoded
    @POST("auth/passwordChangeDriver?")
    Call<ChangePasswordModel> doUpdateChangePassword(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Transaction Jobs Response
    @FormUrlEncoded
    @POST("TransactionDriver?")
    Call<WalletAllModel> doWalletTransactionAll(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    //StripUpdateEndTrip Update  Response
    @FormUrlEncoded
    @POST("stripePayment?")
    Call<StripeEndTModel> doStripUpdateEndTrip(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("notifysend?")
    Call<PushModel> doNotifySend(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Profile view  Response
    @FormUrlEncoded
    @POST("viewdocument")
    Call<DocumentViewModel_> doGetDocumentsviewDetails(@Header("Authorization") String token, @FieldMap HashMap<String, Object> map1);

    //Document Upload Path Response
    @FormUrlEncoded
    @POST("imageupload_e")
    Call<UploadModel> doImageUploadPathDetails(@FieldMap HashMap<String, Object> map1);

    //Document Upload Path Response
    @FormUrlEncoded
    @POST("document_update_e")
    Call<UploadDocModel> doDocumentUploadDetails(@FieldMap HashMap<String, Object> map1);

    //Document Upload Path Response
    @FormUrlEncoded
    @POST("proofreupload_e")
    Call<ReUploadModel> doDocumentReUploadDetails(@FieldMap HashMap<String, Object> map1);

    //Document Upload Path Response
    @FormUrlEncoded
    @POST("viewdocument_e")
    Call<DocumentViewModel> doDocumentViewDetails(@FieldMap HashMap<String, Object> map1);

    //Document Upload Path Response
    @FormUrlEncoded
    @POST("manage_document")
    Call<DocumentListData> doCallViewManageDocument(@FieldMap HashMap<String, Object> map1);


    //Cash Payment Update  Response
    @FormUrlEncoded
    @POST("cashPayment")
    Call<PaymentUpdateModel> doUpdatePaymentDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);

    //Linked In Response
    @GET("userinfo")
    Call<LinkedInUserDetailsModel> doGetLinkedInUserInfo(@Header("Authorization") String token);


    //Delete Account Response
    @FormUrlEncoded
    @POST("delete_account_request")
    Call<DeleteModel> doGetDeleteDetails(@FieldMap HashMap<String, Object> map1);


    //Delete Account Confirm  Response
    @FormUrlEncoded
    @POST("delete_account_confirm")
    Call<DeleteModel> doGetDeleteConfirmDetails(@FieldMap HashMap<String, Object> map1);


    //Remove Firebase Driver after accept
    @FormUrlEncoded
    @POST("removeDriver")
    Call<BankModel> docallRemoveDriver(@FieldMap HashMap<String, Object> map);

    //New Available with Date View  Response
    @FormUrlEncoded
    @POST("viewDriverSlots")
    Call<ViewAvailListModel> doGetNewViewDriverAvailable(@FieldMap HashMap<String, Object> map1);

    //New Available with Date View  Response
    @FormUrlEncoded
    @POST("viewDriverDateSlot")
    Call<NewDateAvailListModel> doGetNewAvailable(@FieldMap HashMap<String, Object> map1);

    //Update Available with Date View  Response
    @FormUrlEncoded
    @POST("updateDriverSlot")
    Call<UpdateAvailListModel> doUpdateNewAvailable(@FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("volunteerTimeout")
    Call<BankModel> docallTimeout(@FieldMap HashMap<String, Object> map1);

    //Volunteer Trip Request send Response
    @FormUrlEncoded
    @POST("VolunteerTrip?")
    Call<SendRequestModel> doBackupRequestSendDetails(@FieldMap HashMap<String, Object> map1);

    //Backup Request ACCEPT Response
    @FormUrlEncoded
    @POST("VolunteerTrip")
    Call<AcceptRequestModel> doAcceptBackupRequestDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);

    //Backup Request Response
    @FormUrlEncoded
    @POST("VolunteerTrip")
    Call<RequestStatusModel> doGetBackupRequestStatusDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map);


    //CancelJobs Update  Response
    @FormUrlEncoded
    @POST("VolunteerTrip")
    Call<CancelJobModel> doCancelBackupJobDetails(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);



    //ChatAccessToken
    @GET("chatAccessToken")
    Call<ChatAccessModel> doChatAccessToken(@Query("device") String device,
                                            @Query("identity") String identity);

    //CallAccessToken
    @GET("accesstoken")
    Call<ChatAccessModel> doCallAccessToken(@Query("device") String device,
                                            @Query("identity") String identity);

    @FormUrlEncoded
    @POST("message_notify")
    Call<BankModel> addSendMessageNotify(@Header("Authorization") String key, @FieldMap HashMap<String, Object> map1);


    //Transaction Jobs Response
    @FormUrlEncoded
    @POST("BonusTransactionList?")
    Call<RideHistoryModel> doBonusTransactionAll(@Header("Authorization") String token, @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);



    @FormUrlEncoded
    @POST("subscriptionGetList")
    Call<ViewSubsModel> doGetsubscriptionGetList(@Header("Authorization") String token,
                                                 @Header("Accept") String accept, @FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("subscriptionList")
    Call<ListViewSubsModel> doGetsubscriptionList(@FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("buySubscription")
    Call<UpdateSubsModel> doUpdatesubscriptionList(@FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("subscription_history")
    Call<SubsHistoryModel> doSubscriptionHistory(@FieldMap HashMap<String, Object> map1);

    //Disable Auto renew BuyPlan Response
    @FormUrlEncoded
    @POST("updateAutoRenew?")
    Call<CancelAutoRenewPlanModel> doUpdateAutoRenew(@FieldMap HashMap<String, Object> map1);

    //Check Subscription Sub category  Response
    @FormUrlEncoded
    @POST("checkService?")
    Call<CheckPlanModel> doCheckSubscriptionSubCategory(@FieldMap HashMap<String, Object> map1);


    //CheckSubscription BuyPlan Response
    @FormUrlEncoded
    @POST("createCheckoutSession?")
    Call<BuyPlanWebHookModel> doCreateCheckoutSessionSubscription(@FieldMap HashMap<String, Object> map1);

    //CheckSubscription BuyPlan Response
    @FormUrlEncoded
    @POST("checkSubscription?")
    Call<BuyPlanModel> doCheckSubscription(@FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("geofensPushNotification")
    Call<GeofencePushNotificationModel> doSendPushNotifyGeofence(@Header("Authorization") String key, @FieldMap HashMap<String, Object> map1);

    @FormUrlEncoded
    @POST("stripe/create_account")
    Call<WebResModel> createBankAccount(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("driverPayoutRequest")
    Call<PayoutModel> callPayout(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("payout_history")
    Call<PayoutTransactionHistoryModel> callPayoutTransaction(@FieldMap HashMap<String, Object> map);

    @GET("cities")
    Call<CitiesListModel> callcitieslist(@Query("device") String device);
}
