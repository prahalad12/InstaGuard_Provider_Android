package com.instaguard.provider.Helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.instaguard.provider.Fragments.BankDetailFragment;
import com.instaguard.provider.Fragments.BankDetailsFragmentNew;
import com.instaguard.provider.Fragments.EarningsFragment;
import com.instaguard.provider.Fragments.EditSettingsFragment;

import com.instaguard.provider.Fragments.EmergencyContactsFragment;
import com.instaguard.provider.Fragments.ManageDocumentsFragment;
import com.instaguard.provider.Fragments.ManageViewDocumentsFragment;
import com.instaguard.provider.Fragments.MyAvailabilityFragment;
import com.instaguard.provider.Fragments.MyAvailabilityNewFragment;
import com.instaguard.provider.Fragments.MyAvailabilitySubFragment;
import com.instaguard.provider.Fragments.ManageServiceFragment;
import com.instaguard.provider.Fragments.MyCalenderBookingFragment;
import com.instaguard.provider.Fragments.MyWalletFragment;
import com.instaguard.provider.Fragments.PaymentFragment;
import com.instaguard.provider.Fragments.PayoutFragment;
import com.instaguard.provider.Fragments.SocialSignupFragment;
import com.instaguard.provider.Fragments.StripePaymentFragment;
import com.instaguard.provider.Fragments.SubCategoryFragment;
import com.instaguard.provider.Fragments.SubscriptionPlanFragment;
import com.instaguard.provider.Fragments.SubscriptionPlanHistoryFragment;
import com.instaguard.provider.Fragments.SupportSubFragment;

import com.instaguard.provider.Fragments.ForgotPasswordFragment;
import com.instaguard.provider.Fragments.InviteFriendsFragment;
import com.instaguard.provider.Fragments.LoginFragment;
import com.instaguard.provider.Fragments.RegisterFragment;
import com.instaguard.provider.Fragments.SettingsFragment;
import com.instaguard.provider.Fragments.SupportFragment;
import com.instaguard.provider.Fragments.UserFeedbackFragment;
import com.instaguard.provider.Fragments.WalletTermsFragment;
import com.instaguard.provider.Fragments.YourAnnocementsOffersFragment;
import com.instaguard.provider.Fragments.YourJobsFragment;
import com.instaguard.provider.Fragments.YourWalletTransAllFragment;
import com.instaguard.provider.Fragments.YourWalletTransMoneyInFragment;
import com.instaguard.provider.Fragments.YourWalletTransMoneyOutFragment;
import com.instaguard.provider.Fragments.YourWalletTransactionFragment;
import com.instaguard.provider.R;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static String BASE_NAME = "Insta-Guard";

    /*-********************************************************************************************/
    //Demo
    public static String BASE_CODE = "_Demo";
    public static String BASE_URL = "https://backend.insta-guard.co.uk/";

    public static final String REDIRECT_URI = "https://backend.insta-guard.co.uk/";
    public static final String REDIRECT_URI_LIVE = "https://backend.insta-guard.co.uk/";

    public static String RETROFIT_URL_CALL_CHAT_ACCESS = "https://backend.insta-guard.co.uk/api/v3/";

    //public static String RETROFIT_URL = "http://instaguard.uplogictech.com/api/";
    public static String RETROFIT_URL = "https://backend.insta-guard.co.uk/api/v3/";
    public static String CALENDER_WEB_URL = "http://insta-guardweb.uplogictech.in/calender/driver/";

    /*-********************************************************************************************/
    //Live
/*    public static String BASE_CODE = "_Live";
    public static String BASE_URL = "https://backend.insta-guard.co.uk/";

    public static final String REDIRECT_URI = "https://backend.insta-guard.co.uk/";

    public static String RETROFIT_URL_CALL_CHAT_ACCESS = "https://backend.insta-guard.co.uk/api/v3/";

    public static final String REDIRECT_URI_LIVE = "https://backend.insta-guard.co.uk/";

    //public static String RETROFIT_URL = "https://backend.insta-guard.co.uk/api/";
    public static String RETROFIT_URL = "https://backend.insta-guard.co.uk/api/v3/";

    *//*public static String CALENDER_WEB_URL = "https://backend.insta-guard.co.uk/calender/driver/";*//*
        public static String CALENDER_WEB_URL = "https://www.insta-guard.co.uk/calender/driver/";*/


    /*-********************************************************************************************/

    public static String com_stripe_publishable_key = "pk_test_WDP0fwVbtyJzXdGucvd901EJ";

    public static String BASE_URL_CATEGORY_IMAGE = BASE_URL + "uploads/";
    public static String BASE_URL_IMAGE = BASE_URL;
    public static String BASE_URL_IMAGE_UPLOAD = BASE_URL + "uploads/";
    public static String BASE_URL_ADMIN = BASE_URL + "admin";

    /*********************************************************************************************/

    public static final String MY_PREFS_NAME = BASE_NAME + BASE_CODE + "_Driver";

    public static final String SP_DRIVERID = MY_PREFS_NAME + "_userId";

    public static final String MY_PREFS_NAME_NEW = MY_PREFS_NAME + "_face_finger";

    /*********************************************************************************************/

    // Google sign in web Credentials
    public static final String GOOGLE_CLIENT_ID_DEMO = "729845483920-5dl7k3olfk690ap0a4f46kaec57q7cjl.apps.googleusercontent.com";
    public static final String GOOGLE_CLIENT_ID_LIVE = "729845483920-2s9fu4uo6h99k3ee0pda46ek5svldgrn.apps.googleusercontent.com";

    /*-********************************************************************************************/

    public static String LINKED_IN_URL = "https://api.linkedin.com/v3/";
    public static final String AUTHURL = "https://www.linkedin.com/oauth/v3/authorization";
    public static final String TOKENURL = "https://www.linkedin.com/oauth/v3/accessToken";

    public static final String SCOPE = "openid%20profile%20email";
    //public static final String SCOPE = "r_liteprofile%20r_emailaddress";
    //    const scope = ['email', 'profile', "openid"]

    /*********************************************************************************************/

    public static final String MY_PREFS_NAME_REMEMBER = MY_PREFS_NAME + "_rememberme";

    public static final String MY_PREFS_REQUEST_STATE = MY_PREFS_NAME + "_Request_State";

    public static final String MY_PREFS_FB_CATEGORY_TRIPSTATE = MY_PREFS_NAME + "_category_tripstate";

    public static final String GLOBAL_STORED_ID = MY_PREFS_NAME + "_global_stored";


    //Retrofit Header path
    public static String POST_ACCEPT_CONTENT_TYPE = "application/json";

    /*************************** GEOFENCE ********************************/

    public static String CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION";
    public static int CUSTOM_REQUEST_CODE_GEOFENCE = 1001;
    public static int GEOFENCE_RADIUS_IN_METERS = 50;
    public static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 90 * 1000; //90 seconds Long.parseLong(strRequestCount) * 1000

    /*************************** DELETE_ACCOUNT ********************************/
    public static int DELETE_ACCOUNT_CALL_INTERVAL = 10 * 1000; //10 seconds

    public static final String SP_DRIVER_DELETE_ACCOUNT = MY_PREFS_NAME + "_delete";

    /*************************** CALL CHAT identity - toIdentity ********************************/

    public static String identity = MY_PREFS_NAME + "_identity";
    public static String toIdentity = MY_PREFS_NAME + "_toIdentity";
    public static String mIdentity = MY_PREFS_NAME + "_mIdentity";

    /*********************************************************************************************/

    //Face & Finger
    public static final String spFingerUserId = MY_PREFS_NAME + "_fingeruserid";
    public static final String spFingerStatus = MY_PREFS_NAME + "_fingerstatus";
    public static final String spFingerNoStatus = MY_PREFS_NAME + "_fingerno";

    public static final String spFaceUserId = MY_PREFS_NAME + "_faceuserid";
    public static final String spFaceStatus = MY_PREFS_NAME + "_facestatus";
    public static final String spFaceNoStatus = MY_PREFS_NAME + "_faceno";

    /*********************************************************************************************/

    //Firebase
    public static String FIREBASE_PROVIDER_HEADER = "providers";
    public static String FIREBASE_USER_HEADER = "users";

    public static String FIREBASE_TRIP_HEADER = "trips";

    public static String FIREBASE_SERVICE_STATUS = "service_status";
    public static String FIREBASE_ACCEPT_STATUS = "accept_status";

    public static String FIREBASE_BACKUP_REQUEST = "backup_request";
    public static String FIREBASE_TRANSFER_REQUEST = "transfer_request";

    public static String FIREBASE_PROVIDER_FNAME = "fname";
    public static String FIREBASE_PROVIDER_LNAME = "lname";
    public static String FIREBASE_PROVIDER_EMAIL = "email";
    public static String FIREBASE_PROVIDER_CC = "cc";
    public static String FIREBASE_PROVIDER_MOBILE = "mobile";
    public static String FIREBASE_PROVIDER_PRO_PIC = "pro_pic";
    public static String FIREBASE_PROVIDER_CITY_NAME = "city";

    public static String FIREBASE_NOTIFICATIONS_HEADER = "Notifications";
    public static String FIREBASE_OFFERS_HEADER = "Offers";
    public static String FIREBASE_ANNOUNCEMENTS_HEADER = "Announcements";
    public static String FIREBASE_OFFER_USER_HEADER = "user";
    public static String FIREBASE_OFFER_PROVIDER_HEADER = "provider";

    /*********************************************************************************************/

    /* Sharedpreferences */

    //Admin controll OTP status verification
    public static final String SP_OTP_STATUS = "otp_status";

    //Device token for firebase instance notification states
    public static final String SP_DEVICETOKEN = "deviceToken";
    public static final String SAMPLE_PROFILE_PIC = "https://www.cars.ro/images/photo_galleries/large/2954/driver-4.jpg";
    public static final String MAP_REQUEST_GREEN_PIN = BASE_URL_ADMIN + "assets/images/ic_greenpin_32.png";

    public static final String SP_DRIVER_ACCESSTOKEN = "accessToken";
    public static final String SP_DRIVER_TOKENTYPE = "tokenType";
    public static final String SP_DRIVER_TOKENEXPTIME = "tokenExpTime";


    public static final String SP_DRIVER_LOGIN = MY_PREFS_NAME + "_boolean";
    public static final String SP_LANGUAGE = MY_PREFS_NAME + "_language";
    public static final String SP_LANGUAGE_SHORT_NAME = MY_PREFS_NAME + "_language_short_name";
    public static final String SP_LANGUAGE_COUNTRY = MY_PREFS_NAME + "_language_country_name";
    public static final String SP_CURRENCY = MY_PREFS_NAME + "_currency";
    public static final String SP_CURRENCY_SYMBOL = MY_PREFS_NAME + "_currency_symbol";
    public static final String SP_CATEGORY = MY_PREFS_NAME + "_category";

    public static final String PREFS_NAME_CURRENCY = MY_PREFS_NAME + "_Currecny_All";
    public static final String SP_CURRENCY_COMMON_VALUES = MY_PREFS_NAME + "_currency_common_ratio";

    public static final String SP_DRIVER_FNAME = MY_PREFS_NAME + "_userfname";
    public static final String SP_DRIVER_LNAME = MY_PREFS_NAME + "_userlname";
    public static final String SP_DRIVER_EMAIL = MY_PREFS_NAME + "_useremail";
    public static final String SP_DRIVER_PASSWORD = MY_PREFS_NAME + "_userpassword";
    public static final String SP_DRIVER_MOBILE = MY_PREFS_NAME + "_usermobile";
    public static final String SP_DRIVER_COUNTRY_CODE = MY_PREFS_NAME + "_usermobileCC";
    public static final String SP_DRIVER_DOB = MY_PREFS_NAME + "_userdob";
    public static final String SP_DRIVER_PRO_PIC = MY_PREFS_NAME + "_userProPic";
    public static final String SP_DRIVER_DOC_PIC = MY_PREFS_NAME + "_userDOCPic";
    public static final String SP_DRIVER_STATE = MY_PREFS_NAME + "_userstate";
    public static final String SP_DRIVER_COUNTRY = MY_PREFS_NAME + "_usercountry";
    public static final String SP_DRIVER_ADDRESS = MY_PREFS_NAME + "_useraddress";
    public static final String SP_DRIVER_JOINDATETIME = MY_PREFS_NAME + "_userjoindatetime";
    public static final String SP_DRIVER_WALLET_AMT = MY_PREFS_NAME + "_driver_wallet";
    public static final String SP_DRIVER_BONUS_AMT = MY_PREFS_NAME + "_driver_wallet_bonus";
    public static final String SP_DRIVER_WALLET_AMT_MIN = MY_PREFS_NAME + "_driverwalletAmtMin";
    public static final String SP_USER_SOCIAL_LOGIN_TYPE = MY_PREFS_NAME + "_sociallogintype";

    public static final String SP_COMMON_STRIPE_PUBLISH_KEY = MY_PREFS_NAME + "_STRIPE_PUBLISH_KEY";
    public static final String SP_COMMON_STRIPE_SECRET_KEY = MY_PREFS_NAME + "_STRIPE_SECRET_KEY";

    public static final String SP_COMMON_REQUEST_TIME_DELAY_ACCEPT = MY_PREFS_NAME + "_REQUEST_TIME_DELAY_ACCEPT";
    public static final String SP_COMMON_DISTANCE_UNIT = MY_PREFS_NAME + "_DistanceUnit";
    public static final String SP_COMMON_SUBSCRIPTION_PAGE = MY_PREFS_NAME + "_getGuardSubscriptionPage";

    public static final String SP_COMMON_TOWK_CHAT_URL = MY_PREFS_NAME + "_TOWK_CHAT_URL";
    public static final String SP_COMMON_TOWK_PROPERTY_URL = MY_PREFS_NAME + "_TOWK_PROPERTY_URL";
    public static final String SP_COMMON_TOWK_PROPERTY_ID = MY_PREFS_NAME + "_TOWK_PROPERTY_ID";

    public static final String SP_DRIVER_EMAIL_REMEMBERME = MY_PREFS_NAME + "_email_remember";
    public static final String SP_DRIVER_PASS_REMEMBERME = MY_PREFS_NAME + "_pass_remember";
    public static final String SP_CHECKED_REMEMBERME = MY_PREFS_NAME + "_checked_remember";

    public static final String SP_DRIVER_REFERAL_CODE = MY_PREFS_NAME + "_userRegReferalCode";
    public static final String SP_DRIVER_INVITE_CODE = MY_PREFS_NAME + "_userSendInviteCode";

    public static final String SP_MAP_TRIPSTATE_DISTANCE = MY_PREFS_NAME + "_distance";
    public static final String SP_MAP_TRIPSTATE_ADDITIONAL_CHARGE_LAST_CLICK = "_ADDITIONAL_CHARGE_LAST_CLICK";


    public static final String SP_CURRENT_BOOKING_ID = "trip_BOOKING_ID";
    public static final String SP_MAP_TRIPSTATE = "trip_state";

    public static final String SP_MAP_TRIPSTATE_DISTANCE_INT = MY_PREFS_NAME + "_trip_distance_p";

    public static final String SP_FB_REQUEST_PAYMENT_MODE = "payment_mode";
    public static final String SP_FB_REQUEST_STRIPE_TOKEN = "stripe_token";
    public static final String SP_FB_CATEGORY_ID_TRIPSTATE = "category_id";
    public static final String SP_FB_CATEGORY_NAME_TRIPSTATE = "category_name";
    public static final String SP_FB_CATEGORY_SUB_ID_TRIPSTATE = "sub_category_id";
    public static final String SP_FB_CATEGORY_SUB_NAME_TRIPSTATE = "sub_category_name";
    public static final String SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE = "Accept_pickup_lat";
    public static final String SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE = "Accept_pickup_lng";

    public static final String SP_FB_ACCEPT_DROP_LAT_TRIPSTATE = "Accept_DROP_lat";
    public static final String SP_FB_ACCEPT_DROP_LNG_TRIPSTATE = "Accept_DROP_lng";

    public static final String SP_FB_ACCEPT_DROP_LOCATION = "Accept_drop_loc";
    public static final String SP_FB_ACCEPT_DROP_LOCATION_ADDED = "Accept_drop_loc_boolean";

    public static final String SP_NAVIGATION = MY_PREFS_NAME + "_Navigation";

    public static final String SP_DEVICE_NOTIFY_TYPE = MY_PREFS_NAME + "_regid_notify";



    public static final String SP_USER_SUBSCRIPTION_CODE = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE";
    public static final String SP_USER_SUBSCRIPTION_CODE_ID = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE_ID";
    public static final String SP_USER_SUBSCRIPTION_CODE_DISCOUNT_PERCENTAGE = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE_PeRCENTAge";
    public static final String SP_USER_SUBSCRIPTION_REMAINING_COUNT = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE_REMAINING_COUNT";
    public static final String SP_USER_SUBSCRIPTION_SERVICE_IDS = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE_SERVICE_IDS";
    public static final String SP_USER_SUBSCRIPTION_APPOINTMENT_SERVICE_IDS = MY_PREFS_NAME + "_USER_SUBSCRIPTION_CODE_APPOINTMENT_SERVICE_IDS";
    public static final String SP_USER_SUBSCRIPTION_FREE_APPOINTMENT_SERVICE = MY_PREFS_NAME + "_USER_SUBSCRIPTION_FREE_APPOINTMENT_SERVICE";

    /*********************************************************************************************/

    //Category Bundle
    public static final String B_CATEGORY_NAME = MY_PREFS_NAME + "_category_name";
    public static final String B_CATEGORY_ID = MY_PREFS_NAME + "_category_id";
    public static final String B_SELECTED_CATEGORY = MY_PREFS_NAME + "_selected_category";


    public static final String B_WALLET_RECHARGE_AMOUNT = MY_PREFS_NAME + "_RechargeAmount";
    public static final String B_WALLET_PAGE = MY_PREFS_NAME + "_page";

    public static final String B_CURRENT_LAT = MY_PREFS_NAME + "_current_lat";
    public static final String B_CURRENT_LNG = MY_PREFS_NAME + "_current_lng";

    public static final String B_SUB_CATEGORY_NAME = MY_PREFS_NAME + "_sub_category_name";
    public static final String B_SUB_CATEGORY_ID = MY_PREFS_NAME + "_sub_category_id";

    //Bundle
    public static final String B_DRIVERID = "_userId";
    public static final String B_SUPPORT = "_support";
    public static final String B_SUPPORT_STATIC_DYNAMIC = "_support_static_dynamic";
    public static final String B_SUPPORT_STATIC_DYNAMIC_PAGE = "_support_static_dynamic_page";
    public static final String B_SUPPORT_FROM = "_support_static_dynamic_page_from";
    public static final String B_ABOUTUS = "_aboutus";
    public static final String B_TERMS = "_terms";
    public static final String B_PRIVACY = "_privacy";
    public static final String B_CONTACTUS = "_contactus";
    public static final String B_FAQ = "_faq";

    /*********************************************************************************************/

    //Fcm
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //   public static final String SHARED_PREF = "ah_firebase";
    public static final String SP_DEVICE_ID = "regid";


    public static final String B_TIME_LIST = "time_list";
    public static final String B_DAY_LIST = "day_list";

    //Map utils
    public static final int MAP_DEFAULT_ZOOM = 15;
    public static final int MAP_ONTRIP_ZOOM = 15;

    //Polyline color
    public static final int POLYLINE_COLOR_ACCEPTTRIP = 0xFF000000;
    public static final int POLYLINE_COLOR_STARTTRIP = 0xFF3B5998;

    public static final long SET_INTERVAL = 2000; //2 Seconds
    public static final long SET_FASTESTINTERVAL = 3000; //3 Seconds
    public static final int REQUEST_CHECK_SETTINGS = 0x2;
    public static final long SET_MIN_DISTANCE = 1; // 10 meters


    public static int GOOGLE_DIRECTION_CALL_INTERVAL = 60 * 1500; //1.5 minutes
    public static Double currentSpeedDouble = 6.0;

    // Splash screen timer
    public static boolean MAP_ISSHOWING = false;

    public static int COUNT_TIMER_DISTANCE_DURATION = 10;
    public static int COUNT_TIMER_DISTANCE_DURATIONACCEPT = 15;

    public static int countDownTimeLimit = 30;

    public static int RESPONSECODE_200 = 200;
    public static int RESPONSECODE_201 = 201;
    public static int RESPONSECODE_401 = 401;
    public static int RESPONSECODE_500 = 500;
    public static int RESPONSECODE_501 = 501;


    //Fragment transitions
    public static String CURRENT_TAG = null;

    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    public static final String FRAGMENT_SIGNIN = "LoginFragment";
    public static final String FRAGMENT_SIGNUP = "RegisterFragment";
    public static final String FRAGMENT_FORGOTPASSWORD = "ForgotPasswordFragment";
    public static final String FRAGMENT_RESETPASSWORD = "ResetPasswordFragment";
    public static final String FRAGMENT_RIDEHISTORY = "RideHistoryFragment";
    public static final String FRAGMENT_BILLHISTORY = "BillHistoryFragment";
    public static final String FRAGMENT_SETTINGS = "SettingsFragment";
    public static final String FRAGMENT_EDITSETTINGS = "EditSettingsFragment";
    public static final String FRAGMENT_PAYMENT = "PaymentFragment";
    public static final String FRAGMENT_MANAGEDOCUMENT = "ManageDocumentsFragment";
    public static final String FRAGMENT_MANAGE_VIEW_DOCUMENT = "ManageViewDocumentsFragment";
    public static final String FRAGMENT_MYAVAILABILITY_NEW = "MyAvailabilityNewFragment";

    public static final String FRAGMENT_INVITEFRIENDS = "InviteFriendsFragment";
    public static final String FRAGMENT_SUPPORT = "SupportFragment";
    public static final String FRAGMENT_SUPPORT_SUBPAGES = "SupportSubFragment";
    public static final String FRAGMENT_MYAVAILABILITY = "MyAvailabilityFragment";
    public static final String FRAGMENT_MYAVAILABILITY_SUB = "MyAvailabilitySubFragment";
    public static final String FRAGMENT_WALLET_TERMS = "WalletTermsFragment";

    public static final String FRAGMENT_YOURJOBS = "YourJobsFragment";
    public static final String FRAGMENT_BANKDETAIL = "BankDetailFragment";
    public static final String FRAGMENT_BANKDETAIL_NEW = "BankDetailFragmentNew";
    public static final String FRAGMENT_PAYOUT = "payoutFragment";

    public static final String FRAGMENT_MYWALLET = "MyWalletFragment";

    public static final String FRAGMENT_EMERGENCY_CONTACTS = "EmergencyContactsFragment";
    public static final String FRAGMENT_USERFEEDBACK = "UserFeedbackFragment";
    public static final String FRAGMENT_MANAGE_SERVICE = "ManageServiceFragment";
    public static final String FRAGMENT_SUBCATEGORY = "SubCategoryFragment";

    public static final String FRAGMENT_STRIPE_PAYMENT = "StripePaymentFragment";

    public static final String FRAGMENT_YOUR_WALLET_TRANSACTION = "YourWalletTransactionFragment";

    private static final String FRAGMENT_YOUR_WALLET_TRANSACTION_ALL = "YourWalletTransAllFragment";
    private static final String FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_IN = "YourWalletTransMoneyInFragment";
    private static final String FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_OUT = "YourWalletTransMoneyOutFragment";


    public static final String FRAGMENT_YOUR_OFFERS_ANNOCEMENTS = "YourAnnocementsOffersFragment";
    private static final String FRAGMENT_YOUR_OFFERS = "YourOffersFragment";
    private static final String FRAGMENT_YOUR_ANNOCEMENTS = "YourAnnocementFragment";

    public static final String FRAGMENT_EARNINGS = "EarningsFragment";
    public static final String FRAGMENT_SOCIALSIGNUP = "SocialSignupFragment";

    public static final String FRAGMENT_MY_CALENDER_BOOKING = "MyCalenderBookingFragment";

    public static final String FRAGMENT_SUBSCRIPTION_PLAN = "SubscriptionPlanFragment";
    public static final String FRAGMENT_SUBSCRIPTION_PLAN_HISTORY = "SubscriptionPlanHistoryFragment";

    /*********************************************************************************************/

    public static final String LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate";
    public static final String INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate";


    //Geocoder
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.instaguard.provider";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_DATA = PACKAGE_NAME + ".RESULT_DATA";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String LOCATION_DATA_EXTRA_TYPE = PACKAGE_NAME + ".LOCATION_DATA_EXTRA_TYPE";
    public static final String GEOCODER_TYPE = "GEOCODER";

    //Availablility
    public static String[] dayArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saterday"};
    public static String[] timeSlotArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    public static String[] timeArray = {"12-01 AM", "01-02 AM", "02-03 AM", "03-04 AM", "04-05 AM", "05-06 AM"
            , "06-07 AM", "07-08 AM", "08-09 AM", "09-10 AM", "10-11 AM", "11-12 PM", "12-01 PM", "01-02 PM", "02-03 PM"
            , "03-04 PM", "04-05 PM", "05-06 PM", "06-07 PM", "07-08 PM", "08-09 PM", "09-10 PM", "10-11 PM", "11-12 PM"};


    public static String[] languageIds = {"1", "2", "3", "4"};
    public static String[] languageStringIds = {"English", "Swahili", "French", "Spanish"};

    //Fragment Trasitions
    public static void switchContent(int id, String TAG,
                                     FragmentActivity baseActivity, Bundle args, AnimationType transitionStyle) {

        Fragment fragmentToReplace = null;
        FragmentManager fragmentManager = baseActivity
                .getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // If our current fragment is null, or the new fragment is different, we
        // need to change our current fragment
        Log.i(TAG, "switchContent:" + CURRENT_TAG + "--------" + TAG);
        if (CURRENT_TAG == null || !TAG.equals(CURRENT_TAG)) {

            if (transitionStyle != null) {
                switch (transitionStyle) {
                    case SLIDE_DOWN:
                        // Exit from down
                        transaction.setCustomAnimations(R.anim.slide_up,
                                R.anim.slide_down);

                        break;
                    case SLIDE_UP:
                        // Enter from Up
                        transaction.setCustomAnimations(R.anim.slide_in_up,
                                R.anim.slide_out_up);
                        break;
                    case SLIDE_LEFT:
                        // Enter from left
                        transaction.setCustomAnimations(R.anim.slide_left,
                                R.anim.slide_out_left);
                        break;
                    // Enter from right
                    case SLIDE_RIGHT:
                        transaction.setCustomAnimations(R.anim.slide_right,
                                R.anim.slide_out_right);
                        break;
                    case FADE_IN:
                        transaction.setCustomAnimations(R.anim.fade_in,
                                R.anim.fade_out);
                    case FADE_OUT:
                        transaction.setCustomAnimations(R.anim.fade_in,
                                R.anim.donot_move);
                        break;
                    case SLIDE_IN_SLIDE_OUT:
                        transaction.setCustomAnimations(R.anim.slide_left,
                                R.anim.slide_out_left);
                        break;
                    case SLIDE_IN_SLIDE_OUT_RIGHT:
                        transaction.setCustomAnimations(R.anim.slide_right,
                                R.anim.slide_out_right);
                        break;
                    default:
                        break;
                }
            }

            // Try to find the fragment we are switching to
            Fragment fragment = fragmentManager.findFragmentByTag(TAG);

            // If the new fragment can't be found in the manager, create a new
            // one
            if (fragment == null) {
                System.out.println("if utils");
                switch (TAG) {
                    case FRAGMENT_SIGNIN:
                        fragmentToReplace = new LoginFragment();
                        break;
                    case FRAGMENT_SIGNUP:
                        fragmentToReplace = new RegisterFragment();
                        break;
                    case FRAGMENT_FORGOTPASSWORD:
                        fragmentToReplace = new ForgotPasswordFragment();
                        break;
                    case FRAGMENT_INVITEFRIENDS:
                        fragmentToReplace = new InviteFriendsFragment();
                        break;
                    case FRAGMENT_SUPPORT:
                        fragmentToReplace = new SupportFragment();
                        break;
                    case FRAGMENT_SETTINGS:
                        fragmentToReplace = new SettingsFragment();
                        break;
                    case FRAGMENT_EDITSETTINGS:
                        fragmentToReplace = new EditSettingsFragment();
                        break;
                    case FRAGMENT_SUPPORT_SUBPAGES:
                        fragmentToReplace = new SupportSubFragment();
                        break;
                    case FRAGMENT_MYWALLET:
                        fragmentToReplace = new MyWalletFragment();
                        break;
                    case FRAGMENT_WALLET_TERMS:
                        fragmentToReplace = new WalletTermsFragment();
                        break;
                    case FRAGMENT_YOURJOBS:
                        fragmentToReplace = new YourJobsFragment();
                        break;
                    case FRAGMENT_BANKDETAIL:
                        fragmentToReplace = new BankDetailFragment();
                        break;
                    case FRAGMENT_BANKDETAIL_NEW:
                        fragmentToReplace = new BankDetailsFragmentNew();
                        break;
                    case FRAGMENT_PAYOUT:
                        fragmentToReplace = new PayoutFragment();
                        break;
                    case FRAGMENT_EMERGENCY_CONTACTS:
                        fragmentToReplace = new EmergencyContactsFragment();
                        break;
                    case FRAGMENT_USERFEEDBACK:
                        fragmentToReplace = new UserFeedbackFragment();
                        break;
                    case FRAGMENT_PAYMENT:
                        fragmentToReplace = new PaymentFragment();
                        break;
                    case FRAGMENT_MYAVAILABILITY:
                        fragmentToReplace = new MyAvailabilityFragment();
                        break;
                    case FRAGMENT_MYAVAILABILITY_SUB:
                        fragmentToReplace = new MyAvailabilitySubFragment();
                        break;
                    case FRAGMENT_MANAGE_SERVICE:
                        fragmentToReplace = new ManageServiceFragment();
                        break;
                    case FRAGMENT_STRIPE_PAYMENT:
                        fragmentToReplace = new StripePaymentFragment();
                        break;
                    case FRAGMENT_SUBCATEGORY:
                        fragmentToReplace = new SubCategoryFragment();
                        break;
                    case FRAGMENT_MANAGEDOCUMENT:
                        fragmentToReplace = new ManageDocumentsFragment();
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION:
                        fragmentToReplace = new YourWalletTransactionFragment();
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_ALL:
                        fragmentToReplace = new YourWalletTransAllFragment();
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_IN:
                        fragmentToReplace = new YourWalletTransMoneyInFragment();
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_OUT:
                        fragmentToReplace = new YourWalletTransMoneyOutFragment();
                        break;
                    case FRAGMENT_MANAGE_VIEW_DOCUMENT:
                        fragmentToReplace = new ManageViewDocumentsFragment();
                        break;
                    case FRAGMENT_YOUR_OFFERS_ANNOCEMENTS:
                        fragmentToReplace = new YourAnnocementsOffersFragment();
                        break;
                    case FRAGMENT_EARNINGS:
                        fragmentToReplace = new EarningsFragment();
                        break;
                    case FRAGMENT_SOCIALSIGNUP:
                        fragmentToReplace = new SocialSignupFragment();
                        break;
                    case FRAGMENT_MYAVAILABILITY_NEW:
                        fragmentToReplace = new MyAvailabilityNewFragment();
                        break;
                    case FRAGMENT_MY_CALENDER_BOOKING:
                        fragmentToReplace = new MyCalenderBookingFragment();
                        break;
                    case FRAGMENT_SUBSCRIPTION_PLAN:
                        fragmentToReplace = new SubscriptionPlanFragment();
                        break;
                    case FRAGMENT_SUBSCRIPTION_PLAN_HISTORY:
                        fragmentToReplace = new SubscriptionPlanHistoryFragment();
                        break;
                }
            } else {
                System.out.println("else utils");
                switch (TAG) {
                    case FRAGMENT_SIGNIN:
                        fragmentToReplace = (LoginFragment) fragment;
                        break;
                    case FRAGMENT_SIGNUP:
                        fragmentToReplace = (RegisterFragment) fragment;
                        break;
                    case FRAGMENT_FORGOTPASSWORD:
                        fragmentToReplace = (ForgotPasswordFragment) fragment;
                        break;
                    case FRAGMENT_INVITEFRIENDS:
                        fragmentToReplace = (InviteFriendsFragment) fragment;
                        break;
                    case FRAGMENT_SUPPORT:
                        fragmentToReplace = (SupportFragment) fragment;
                        break;
                    case FRAGMENT_SETTINGS:
                        fragmentToReplace = (SettingsFragment) fragment;
                        break;
                    case FRAGMENT_EDITSETTINGS:
                        fragmentToReplace = (EditSettingsFragment) fragment;
                        break;
                    case FRAGMENT_SUPPORT_SUBPAGES:
                        fragmentToReplace = (SupportSubFragment) fragment;
                        break;
                    case FRAGMENT_MYWALLET:
                        fragmentToReplace = (MyWalletFragment) fragment;
                        break;
                    case FRAGMENT_WALLET_TERMS:
                        fragmentToReplace = (WalletTermsFragment) fragment;
                        break;
                    case FRAGMENT_YOURJOBS:
                        fragmentToReplace = (YourJobsFragment) fragment;
                        break;
                    case FRAGMENT_BANKDETAIL:
                        fragmentToReplace = (BankDetailFragment) fragment;
                        break;
                    case FRAGMENT_EMERGENCY_CONTACTS:
                        fragmentToReplace = (EmergencyContactsFragment) fragment;
                        break;
                    case FRAGMENT_USERFEEDBACK:
                        fragmentToReplace = (UserFeedbackFragment) fragment;
                        break;
                    case FRAGMENT_PAYMENT:
                        fragmentToReplace = (PaymentFragment) fragment;
                        break;
                    case FRAGMENT_MYAVAILABILITY:
                        fragmentToReplace = (MyAvailabilityFragment) fragment;
                        break;
                    case FRAGMENT_MYAVAILABILITY_SUB:
                        fragmentToReplace = (MyAvailabilitySubFragment) fragment;
                        break;
                    case FRAGMENT_MANAGE_SERVICE:
                        fragmentToReplace = (ManageServiceFragment) fragment;
                        break;
                    case FRAGMENT_STRIPE_PAYMENT:
                        fragmentToReplace = (StripePaymentFragment) fragment;
                        break;
                    case FRAGMENT_SUBCATEGORY:
                        fragmentToReplace = (SubCategoryFragment) fragment;
                        break;
                    case FRAGMENT_MANAGEDOCUMENT:
                        fragmentToReplace = (ManageDocumentsFragment) fragment;
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION:
                        fragmentToReplace = (YourWalletTransactionFragment) fragment;
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_ALL:
                        fragmentToReplace = (YourWalletTransAllFragment) fragment;
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_IN:
                        fragmentToReplace = (YourWalletTransMoneyInFragment) fragment;
                        break;
                    case FRAGMENT_YOUR_WALLET_TRANSACTION_MONEY_OUT:
                        fragmentToReplace = (YourWalletTransMoneyOutFragment) fragment;
                        break;
                    case FRAGMENT_MANAGE_VIEW_DOCUMENT:
                        fragmentToReplace = (ManageViewDocumentsFragment) fragment;
                        break;
                    case FRAGMENT_YOUR_OFFERS_ANNOCEMENTS:
                        fragmentToReplace = (YourAnnocementsOffersFragment) fragment;
                        break;
                    case FRAGMENT_EARNINGS:
                        fragmentToReplace = (EarningsFragment) fragment;
                        break;
                    case FRAGMENT_SOCIALSIGNUP:
                        fragmentToReplace = (SocialSignupFragment) fragment;
                        break;
                    case FRAGMENT_MYAVAILABILITY_NEW:
                        fragmentToReplace = (MyAvailabilityNewFragment) fragment;
                        break;
                    case FRAGMENT_MY_CALENDER_BOOKING:
                        fragmentToReplace = (MyCalenderBookingFragment) fragment;
                        break;
                    case FRAGMENT_SUBSCRIPTION_PLAN:
                        fragmentToReplace = (SubscriptionPlanFragment) fragment;
                        break;
                    case FRAGMENT_SUBSCRIPTION_PLAN_HISTORY:
                        fragmentToReplace = (SubscriptionPlanHistoryFragment) fragment;
                        break;
                }
            }
            CURRENT_TAG = TAG;
            if (args != null)
                fragmentToReplace.setArguments(args);
            // Replace our current fragment with the one we are changing to
            transaction.replace(id, fragmentToReplace, TAG);
            transaction.commit();
        } else {
            // Do nothing since we are already on the fragment being changed to

        }
    }

    public static void rlLayoutAnimation(Context context, RelativeLayout layout, AnimationType transitionStyle) {
        Animation startAnimation;
        switch (transitionStyle) {
            case FADE_IN:
                layout.setVisibility(View.VISIBLE);
                startAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                layout.startAnimation(startAnimation);
                break;
            case FADE_OUT:
                startAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                layout.startAnimation(startAnimation);
                layout.setVisibility(View.GONE);
                break;
        }
    }

    public enum AnimationType {
        SLIDE_LEFT, SLIDE_RIGHT, SLIDE_UP, SLIDE_DOWN, FADE_IN, SLIDE_IN_SLIDE_OUT, SLIDE_IN_SLIDE_OUT_RIGHT, FADE_OUT
    }
}
