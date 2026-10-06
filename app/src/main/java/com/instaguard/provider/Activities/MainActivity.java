package com.instaguard.provider.Activities;

import static com.instaguard.provider.Activities.BaseActivity.CalculationByDistance;
import static com.instaguard.provider.Activities.BaseActivity.callClickAction;
import static com.instaguard.provider.Activities.BaseActivity.checkStringEmpty;
import static com.instaguard.provider.Activities.BaseActivity.checkStringEmptyDecimalZero;
import static com.instaguard.provider.Activities.BaseActivity.checkStringEmptyZero;
import static com.instaguard.provider.Activities.BaseActivity.convertCurrencyDividePrice;
import static com.instaguard.provider.Activities.BaseActivity.convertCurrencyDivideWithoutRoundPrice;
import static com.instaguard.provider.Activities.BaseActivity.convertCurrencyPrice;
import static com.instaguard.provider.Activities.BaseActivity.convertDistanceUnit;
import static com.instaguard.provider.Activities.BaseActivity.convertHoursToDaysHoursMinutes;
import static com.instaguard.provider.Activities.BaseActivity.convertKmsToMiles;
import static com.instaguard.provider.Activities.BaseActivity.getDecimalFormattedString;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.meterDistanceBetweenPoints;
import static com.instaguard.provider.Activities.BaseActivity.setVisibility;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Fragments.EditSettingsFragment.loadSharedPreferencesLogList;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_500;
import static com.instaguard.provider.Helpers.Utils.RETROFIT_URL_CALL_CHAT_ACCESS;
import static com.instaguard.provider.Helpers.Utils.mIdentity;
import static com.instaguard.provider.Helpers.Utils.toIdentity;
import static java.lang.Double.parseDouble;
import static java.lang.System.out;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adriangl.overlayhelper.OverlayHelper;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.facebook.BuildConfig;
import com.facebook.ads.AdView;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.CustomKeysAndValues;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.instaguard.provider.Adapter.CurrencyAdapter;
import com.instaguard.provider.Adapter.LanguageAdapter;
import com.instaguard.provider.Adapter.TripFirebaseBackupGuardListAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomSwitch.ToggleSwitch;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.CustomViews.CustomTypefaceSpan;
import com.instaguard.provider.FingerPrint.Activity.SettingsFaceFingerscreen;
import com.instaguard.provider.Fragments.EditSettingsFragment;
import com.instaguard.provider.Helpers.CLocation;
import com.instaguard.provider.Helpers.CustomDialog.DialogUtils;
import com.instaguard.provider.Helpers.DecimalUtils;
import com.instaguard.provider.Helpers.DistanceMatrix.DistanceApiClient;
import com.instaguard.provider.Helpers.DistanceMatrix.RestUtil;
import com.instaguard.provider.Helpers.DistanceMatrix.models.DistanceResponse;
import com.instaguard.provider.Helpers.DistanceMatrix.models.Element;
import com.instaguard.provider.Helpers.FloatingWidgetView.FloatingViewManager;
import com.instaguard.provider.Helpers.GeoFirecoder.GeoFire;
import com.instaguard.provider.Helpers.Geocoder.FetchAddressIntentService;
import com.instaguard.provider.Helpers.Localizationactivity.ui.LocalizationActivity;
import com.instaguard.provider.Helpers.PermissionUtils;
import com.instaguard.provider.Helpers.RatingBar.SimpleRatingBar;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Services.FloatingWidgetService;
import com.instaguard.provider.Helpers.Services.RequestServices;
import com.instaguard.provider.Helpers.Services.WidgetService;
import com.instaguard.provider.Helpers.SoundPoolManager;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AcceptRequestModel;
import com.instaguard.provider.Models.AppSettingCurrencyModel;
import com.instaguard.provider.Models.AppSettingLanguageModel;
import com.instaguard.provider.Models.AppSettingModel;
import com.instaguard.provider.Models.AppSettingSmsTemplate;
import com.instaguard.provider.Models.AppSettingWebModel;
import com.instaguard.provider.Models.Availability.NewDateAvailListModel;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.CancelJobModel;
import com.instaguard.provider.Models.DeclinePendingModel;
import com.instaguard.provider.Models.ChatAccessModel;
import com.instaguard.provider.Models.DriverLocationUpdateModel;
import com.instaguard.provider.Models.EmergencyViewDataModel;
import com.instaguard.provider.Models.EmergencyViewModel;
import com.instaguard.provider.Models.EndRequestModel;
import com.instaguard.provider.Models.LogoutModel;
import com.instaguard.provider.Models.PanicAddAdminModel;
import com.instaguard.provider.Models.PaymentUpdateModel;
import com.instaguard.provider.Models.PendingJobsData;
import com.instaguard.provider.Models.PendingJobsModel;
import com.instaguard.provider.Models.ProfileModel;
import com.instaguard.provider.Models.ProfileUpdateModel;
import com.instaguard.provider.Models.PushModel;
import com.instaguard.provider.Models.RatingUserModel;
import com.instaguard.provider.Models.Request.SendRequestModel;
import com.instaguard.provider.Models.RequestStatusModel;
import com.instaguard.provider.Models.StripeEndTModel;
import com.instaguard.provider.Models.TripsFirebaseBackupGuardListModel;
import com.instaguard.provider.Models.UpcomingJobsData;
import com.instaguard.provider.Models.UpcomingJobsModel;
import com.instaguard.provider.R;
import com.kyleduo.switchbutton.SwitchButton;
import com.skyfishjy.library.RippleBackground;
import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationListener;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.ConversationsClientListener;
import com.twilio.conversations.Message;
import com.twilio.conversations.Participant;
import com.twilio.conversations.StatusListener;
import com.twilio.conversations.User;
import com.twilio.util.ErrorInfo;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends LocalizationActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener, GoogleApiClient.OnConnectionFailedListener,
        ValueEventListener, DirectionCallback, EditSettingsFragment.SuccessResponseEditProfile, GoogleMap.OnMapLongClickListener {

    boolean doubleBackToExitPressedOnce = false;
    DecimalFormat format = new DecimalFormat("0.#");
    /*Navigation Drawer start*/
    @SuppressLint("StaticFieldLeak")
    static DrawerLayout drawerLayout;
    NavigationView navigationView;
    CustomTextView txtVersion, navigationName, navigationWalletAmount, navigationCustomerId;
    public static SimpleDraweeView navigationProfileImage;
    public static CustomTextView navigationSubsPlan;
    String mUserId;
    SimpleRatingBar navRbUserRating;
    ImageView navigationSetting;
    RelativeLayout navigationLogoutDriver;
    /*Navigation Drawer End*/

    AdView adViewFacebook;
    com.google.android.gms.ads.AdView adViewGoogle;
    LinearLayout adContainer;

    static String TAG = "MainActivity";
    String reqStatus = "0";

    private GoogleMap mMap;
    public SupportMapFragment mapFragment;

    //Geofire
    GeoFire geoFire;
    public LatLng currentLatlng;

    /*Layout Details*/ View lytRequestReceive;
    View lytOnlineOffline;
    View lytJobInprogress;
    View lytSMSalert;
    View lytCardPayment;
    View lytCashPayment;
    /*Layout Details*/

    CustomButton btnSmsAlert;
    CustomTextView txtSmsAlertContent;

    /*Online Offline start*/ CustomTextView txtDrivernameOnline, txtOnlineOffline, txtUpdateLanguageSetupOnline, txtUpdateCurrencySetupOnline;

    public static CustomTextView txtPendingCount, txtUpcomingCount, txtUpdateAvailabilitySetupOnline, txtSubscriptionPlanSetupLabel;
    RelativeLayout rlPendingArea, rlUpcomingArea;
    SwitchButton switchOnlineOffline;
    Boolean booleanSwitchOnlineOffline = false, doSwitchBtnCall = false;
    public static SimpleDraweeView ivProfpicOnline;
    LinearLayout llNotapproved, llGettingLocation;

    DatabaseReference driverStatusCheckinoutDatabaseRef;

    ValueEventListener driverStatusCheckinoutValueEventListener;
    String strDocStatusFB, strDocReviewStatusFB, strExpiryDateFB;


    /*Online Offline end*/

    /*Request Screen*/ int timer;
    Boolean timerStart = false;
    CountDownTimer countDownTimerNormalReceivedRequest;
    CountDownTimer countDownTimerSOSBackupRequest;

    CustomTextView txtUserNameRequest, txtSpecialInstrRequest, txtDropLocationRequest, txtDropLocationRequestLabel, txtPickupLocationRequest, txtCategoryNameRequest, txtEmergencyMsgRequest, txtTripTypeRequest, txtJobTypeRequest, txtTimerCount, txtCount, txtDays;
    SimpleRatingBar ratingBarRequest;
    View trStartDateTimeRequest, trEndDateTimeRequest;
    CustomTextView txtStartDateTimeRequest, txtEndDateTimeRequest;

    View llBottomUserDetailsRequest;
    CustomButton btnDeclineRequest, btnAcceptRequest;

    /*PlayAlertTone*/
    private static MediaPlayer mp;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    ArrayList<AppSettingModel> appSettingArray;
    String smsTemplateSubject, smsTemplateContent;
    AlertDialog.Builder builder;

    /*Request Screen*/

    /*Trip Status*/ PopupMenu popup;
    String strMainCategoryid;
    String strMainCategoryidAr;
    String strMainCategoryname;
    String strPaymentMode;
    String strSubCategoryid;
    String strSubCategoryname;
    String struserIdFB;
    String struserNameFB;
    String strMainServiceFB;
    String strPaymodeFB;
    String strSripeTokenFB;
    String strPickupLatFB;
    String strPickupLngFB;
    String strPromoCodeValueFB;
    String strPromoCodeFB;
    String strSpecialInstFB;
    String strSubServiceFB;
    String strDistanceFB_EndJob = "0";
    String strLevelTypeFB;
    String strMainUnitPriceFB;
    String strVisitPriceFB;
    String strBasePriceFB;
    String strdistanceFB;
    String strDropLngFB;
    String strDropLatFB;
    String strDropLocationFB;
    String strPickupLocationFB;
    String struserRatingFB;
    String strBookingId;
    String strAcceptTimeFB;
    String strArriveTimeFB;
    String strBeginJobTimeFB;
    String strStartTimeFB;
    String strCancelServiceStatusFB;
    String strCreatedIdFB;
    String strUpdatedFB;
    String struserEmailFB;
    String struserFnameFB;
    String struserLnameFB;
    String struserMobileFB;
    String strdriverEmailFB;
    String strdriverFnameFB;
    String strdriverIdFB;
    String strdriverLnameFB;
    String strdriverMobileFB;
    String strJobDateFB;
    String strJobNumberFB;
    String strModeFB;
    String strPaymentTypeFB;
    String strSiteCommissionFB;
    String strStripIdFB;
    String strTotalAmountFB;
    String strUserProfPicFB;
    String strDriverProfPicFB;
    String strUnitPriceFB;
    String strMaterialChargeFB;
    String strMiscChargeFB;
    String strSubLevelAmountFB;
    String strAmountTypeFB;
    String strPricePerKMFB;
    String strTotalDistanceFB;
    String strDriverDiscountFB;
    String strCashStatusFB;
    String strCount;
    String strDays;
    String strCompanyName;
    String strPriceperAgentFB;
    String strCustomerBookingIdFB;
    String strPriceperDayFB;
    String strTripTypeFB;
    String strBackupVolunteerStatusFB;
    String strTransferJobStatusFB;
    String strRequestedDriverIdFB;
    String strRequestedDriverNameFB;
    String strRequestedDriverAvatarFB;
    String strEmergencyMsgFB;
    String strStartDateTimeFB;
    String strEndDateTimeFB;

    String strTotalAmountGuardFB;
    String strGuardPercentageFB;
    String strGuardPercentageAmountFB;
    String strGuardAmountFB;


    String AcceptPickupLat, AcceptPickupLng;
    String strSubscriptionStatusFB, strTotalHrsFB, stPricePerHrsFB, strVatAmountsFB;

    String strExtendedHrsFB;
    String strExtendedAmountFB;
    String strExtendedVatFB;
    String strExtendedStatusFB;

    String strDeducationAmountFB;
    String strDeducationStatusFB;
    String strBonusAmountFB;
    String strBonusStatusFB;

    LatLng AcceptPickupLatLng, AcceptDropLatLng;
    /*Trip Status*/

    /*Job In progress start*/ View rlMapJobinprogress;
    View rlJobinprogress;
    View btnNavigationJobinprogress;

    //Retry Accept & cancel & begin trip
    RelativeLayout rlUserCancelRetryTop, rlUserCancelRetryCancelLyt;
    CustomTextView tvAcceptTripCancelRetry, tvRetry, tvCancelTripRetry;


    ImageView ivMenuJobinprogress, ivAlertJobinprogress, ivAlertMain;
    SimpleDraweeView ivuserProfpicJobinprogress;
    SimpleRatingBar ratingbarJobinprogress;
    CustomTextView txtDroplocMapjobinprogress, txtPickuplocMapjobinprogress, txtHintJobinprogress, txtTitleJobinprogres, txtUsernameJobinprogress, txtPickupJobinprogress, txtCompanyJobinprogress, txtCountJobinprogress, txtDaysJobinprogress;
    View rlUserDetailsLytJobinprogress;
    LinearLayout llTripviewone, llTripviewtwo, llTripviewthree, llTripviewfour;
    CustomTextView txtAcceptone, txtAccepttwo, txtAcceptthree, txtAcceptfour;
    CustomTextView txtAccepttimeone, txtAccepttimetwo, txtAccepttimethree, txtAccepttimefour;
    CustomTextView txtMinagoone, txtMinagotwo, txtMinagothree, txtMinagofour;
    LinearLayout llBottomSlidetripHeader;
    LinearLayout btnStarttrip, btnArrivetrip, btnBeginjobtrip, btnEndjobtrip;
    View llUserCallMessageJobinprogress;
    View ivCallJobinprogress;
    View ivMessageJobinprogress;
    View btnCancelJobsInProgress;
    View btnSOSBackupRequiredJobsInProgress;

    View rlBackupGuardListParentLytJobsInProgress;
    RecyclerView rcvGuardDetailsList;

    View rlBackupRequestedGuardDetailsParentLytJobsInProgress;
    CustomTextView txtBackupRequestedGuardLabelJIP;
    CustomTextView txtBackupRequestedGuardNameJIP;
    SimpleRatingBar ratingBarBackupRequestedGuardJIP;
    SimpleDraweeView ivBackupRequestedGuardProfPicJIP;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    Dialog dialogCancelJobinfo, dialogCustomerinfo, dialogShowNavigation;

    LinearLayout llNavigationBtnheader;
    CustomButton btnInAppMap, btnWazeMap, btnGoogleMap;


    View llExtendJobsInProgressDetailsLyt;
    CustomTextView txtExtendHrsJobsInProgress;
    CustomTextView txtExtendHrsAmountJobsInProgress;
    CustomTextView txtExtendHrsVATAmountJobsInProgress;

    /*Job In progress end*/

    /*Payment Screen Additional charge*/
    //Dialog dialogAdditionalEndCharge;
    View dialogAdditionalEndCharge, llParentLytAdditionalCharge;
    ImageView ivCloseAdditionalCharge;
    CustomTextView txtCurrentChargelabel, txtDistance$LabelAdditionalCharge, txtDistanceAdditionalCharge, txtPriceperKmAdditionalCharge, txtMaterialFeelabel, txtMiscFeelabel, txtDiscountDriverlabel, txtFinalTotallabel, txtFinalTotal$, txtCurrentCharge, txtFinalTotal, txtDiscountdriverlabel$, txtMiscfeelabel$, txtMaterialfeelabel$, txtCurrentchargelabel$, txtAgentFeeLabel, txtDayFeeLabel, txtAgentchargelabel$, txtDaychargelabel$, txtDaychargeLabel, txtAgentchargeLabel;


    CustomEditText edtMaterialFee;
    CustomEditText edtMiscFee;
    CustomEditText edtDiscountDriver;
    CustomEditText txtAgentCharge;
    CustomEditText txtDayCharge;
    CustomEditText txtBaseCharge;
    CustomEditText txtadditionalAgentCost;
    CustomEditText txtAdditionalDayCharge;

    CustomTextView txtNoofHrsTripSummary;
    CustomTextView txtPricePerHrsLabelTripSummary;
    CustomTextView txtPricePerHrsTripSummary;
    CustomTextView txtVatAmountTripSummary;
    CustomTextView txtCommissionAmountTripSummary, txtTotalTripSummary;

    CustomTextView txtSubscribtionStatusTripSummary;
    CustomTextView txtExtendNoofHrsTripSummary;
    TableRow trExtendNoofHrsTripSummary;

    CustomButton btnSkipAddcharge, btnSubmitAddcharge, btnRatingpayment;
    SimpleRatingBar ratingbarUserTripsummary;
    CustomEditText edtCommentTripsummary;
    LinearLayout llDistanceAdditionalCharge;
    /*Payment Screen Additional charge*/

    /*Trip Summary*/ View dialogTripSummary;
    // Dialog dialogTripSummary;
    LinearLayout llParentLytTripSummary;
    CustomButton btnCollectPaymentTripSummary;
    LinearLayout llCurrentChargelyt, llMaterialFeelyt, llMiscFeelyt, llDiscountlyt, llPickupDropLyt, llDistanceLytTripSummary, llFinalTotallyt;
    CustomTextView txtFinalTotalHeaderTripSummary, txtJobDateTripSummary, txtDiscountTripSummary, txt, txtPaymentTypeTripSummary, txtDistanceLabel, txtDropLocTripSummaryLabel, txtDropLocTripSummary, txtPickupLocTripSummary, txPromoCodeTripSummary, txtCurrentChargeLabelTripSummary, txtCategoryTripSummary;
    CustomEditText txtFinalTotalTripSummary, txtCurrentChargeTripSummary, txtMaterialFeeTripSummary, txtMiscFeeTripSummary, txtDistance, txtDiscountDriverTripSummary;
    CardView cvRating;

    View trDeductionAmountLytTripSummary, trBonusAmountLytTripSummary;
    CustomTextView txtDeductionAmountTripSummary, txtBonusAmountTripSummary;

    /*Trip Summary*/


    //Geocoder and Fused locatoin
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    Dialog myDialog;

    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    public static Location locFusedLocation;
    public Marker markerCurrentLocationMan;
    public boolean beginMoveCamera = true;

    CustomTextView addressTxtView;

    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    String driverId, userAccessToken, tokenExpTime, currencyCommonPrice, strLanguageShortName, strLanguage, strCurrencySymbol, strCurrency, strDriverProPic, strOnlineOfflineStatus = "0", strDriverfname, deleteMyAccount, strDeleteStatus, strWalletAmt, strfinaltotalamt, strUserWalletAmt, strDriverlname;
    double roundedNumber;

    /*Shared Preference*/
    static SharedPreferences getspCommonState;
    SharedPreferences getspFBCategory;
    SharedPreferences spTripState;
    SharedPreferences spCurrency;

    /*Shared Preference Editor */
    static SharedPreferences.Editor getStateCurrency;
    SharedPreferences.Editor spEditTripState, putCommonState;
    SharedPreferences.Editor putStateFBCategory;

    String strTripstate;

    DatabaseReference tripStatusDatabaseRef;
    DatabaseReference customerWalletDatabaseBRef;
    DatabaseReference driverEstDistanceDurationDatabaseBRef;
    DatabaseReference driverAvailableDatabaseBRef;
    DatabaseReference driverAvailableDatabaseBRef_;
    DatabaseReference driverDistanceDatabaseBRef;
    ValueEventListener tripStatusValueEventListener;
    ValueEventListener customerWalletValueEventListener;
    ValueEventListener driverEstDistanceDurationValueEventListener;
    ValueEventListener driverAvailableValueEventListener;
    ValueEventListener driverAvailableValueEventListener_;
    ValueEventListener driverDistanceValueEventListener;

    public static DatabaseReference driverSearchDatabaseBRef, driverSearchDatabaseBRefNew;
    public static ValueEventListener driverSearchValueEventListener, driverSearchValueEventListenerNew;

    List<String> mobileNumbers = new ArrayList<>();
    String minWalletAmount;

    public static FrameLayout frameMap;

    Switch switchmTraffic;
    BitmapDescriptor bitmapDescriptor, bitmapDescriptorMan;
    Polyline directionPolyline;
    Marker driverCarMarker = null;
    Marker markerPickupDropAccept;
    LatLng curPos = new LatLng(0, 0), prevLatLng = new LatLng(0, 0);
    float previousBearing = 0;
    Location currentLocation;
    List<Step> step;

    Location lStart, lEnd;
    double distance;
    double speed;
    static long startTime, endTime;
    String strDistanceBegin, strDestAddress, strDistanceEndTrip;
    static int p = 1;

    /*CountDownTimer countDownTimerDistanceDuration;
    CountDownTimer countDownTimerDistanceDurationAccept;*/ String strEstDuration, strEstDistance;


    private static final int CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE = 101;

    ToggleSwitch switchLiveTrackJobinProgress;

    Animation slideEndtoStart, slideStarttoEnd;

    /*Currency Language*/ Dialog dialogCurrency, dialogLanguage;
    RecyclerView rcvLanguage, rcvCurrency;
    LinearLayout llStaticLanguage, llStaticCurrency;
    CurrencyAdapter adapterCurrency;
    LanguageAdapter adapterLanguage;

    private List<AppSettingLanguageModel> ListModel;
    private List<AppSettingCurrencyModel> ListModelCurrency;
    List<AppSettingCurrencyModel> getCurrencyList = null;


    APIInterface apiInterface;

    /*Card Payment*/ View lytCardPaymentTitleMsg;
    CustomTextView txtCardPaymentMsg, txtCardPaymentTitle;
    CustomTextView btnCardPayment;
    CustomTextView btnGOTTHECASH;


    /*Cash Payment*/ RelativeLayout rlParentPaymentConfirmation;
    CustomButton btnYesPaymentConfirmation, btnNoPaymentConfirmation;
    ImageView ivClosePaymentConfirmation;
    CustomTextView txtCashStatusTripSummary;

    /*Background Service*/
    private OverlayHelper overlayHelper;
    private Intent intentFloating;
    private boolean show = false;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    Intent intentRequest;
    private Intent intentWidget;
    View viewParentLocReq, viewlocationRequest, llDocApproved;
    CustomButton requestAccept;
    CustomTextView txtBookingIdCommon;
    CustomTextView txtDocApproved;

    private View dialogRequesting;
    private View llParentLytBackupGuardRequestAccepted;
    private CustomTextView txtCountTimer;
    private CustomTextView btnCancelRequest;
    RippleBackground rippleBackgroundRequesting;

    BaseActivity baseActivity;

    @SuppressLint({"CommitPrefEdits", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_main);
            Log.e(TAG, "onCreate: " + "MainActivity");

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            callSharedPreferences();

            baseActivity = new BaseActivity();

            apiInterface = APIClient.getClient().create(APIInterface.class);

            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            EditSettingsFragment.successResponseEditProfile = this;

            initViews();

            mResultReceiver = new AddressResultReceiver(new Handler());

            updateValuesFromBundle(savedInstanceState);

            showGpsDailog();

            initGoogleAPIClient();//Init Google API Client

            checkPermissions();//Check Permission

            registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));   //Register location button receiver

            Utils.MAP_ISSHOWING = true;

            startIntentServiceRequest();

            if (driverId != null) {
                navigationCustomerId.setText(getResources().getString(R.string.id_) + " :- UK" + driverId);
            } else {
                navigationCustomerId.setVisibility(View.GONE);
                //   navigationCustomerId.setText(getString(R.string.customer_id));
            }

            if (isGpsEnable()) {
                callLocationListener();
                fusedLocationCalled();
            }

            if (isOnline()) {

                //                baseActivity.setUserId("U# "+driverId);
//                FirebaseCrashlytics.getInstance().setUserId("crashed user 0001");
                CustomKeysAndValues keysAndValues = new CustomKeysAndValues.Builder().putString("user", "user "+driverId).build();
                FirebaseCrashlytics.getInstance().setCustomKeys(keysAndValues);

                geofireIntailize();

                emptyCurrentLatLng();

                callAppSettings();

                getPendingJobsCall();

                getUpcomingJobsCall();

                getDriverOnlineOrOffline("home", driverId, reqStatus);

                getTripStatusListener();

                callCheckBackground();

                callGeoFencingInitViews();

                overlayHelper = new OverlayHelper(MainActivity.this, new OverlayHelper.OverlayPermissionChangedListener() {
                    @Override
                    public void onOverlayPermissionCancelled() {
                        Toast.makeText(MainActivity.this, "Draw overlay permissions request canceled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onOverlayPermissionGranted() {
                        Toast.makeText(MainActivity.this, "Draw overlay permissions request granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onOverlayPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Draw overlay permissions request denied", Toast.LENGTH_SHORT).show();
                    }
                });

                overlayHelper.startWatching();


                /*String selectedDate, selectedDateApi, selectedTime;
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                selectedDate = sdf.format(new Date());
                selectedDateApi = sdf_.format(new Date());

                System.out.println("~~currentDateandTime~~~~>>>" + selectedDate);
                System.out.println("~~currentDateandTime_~~~~>>>" + selectedDateApi);

                callViewAvailableDetails(selectedDateApi);*/

            } else {
                Toast.makeText(MainActivity.this, "Internet is slow or disconnect!", Toast.LENGTH_SHORT).show();
            }

            onClick();

            callSwitchLiveTrackJobinprogress();

            callTwilioSetup();

            callTwilioChatSetup();

            try {
                //Font in Menu Items.
                applyFontInMenuItems();

                Intent i = getIntent();
                if (i.getExtras() != null) {
                    String distances = i.getStringExtra("distance");
                    String Notify = i.getStringExtra("Notify");

                   /* if (distances == null) {
                        distances = "0";
                    }
                    distance = Double.parseDouble(distances);*/

                    String strDistanceEndTrip_sp = spTripState.getString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");

                    if (distances == null || distances.equals("") || distances.equals("null")) {
                        distances = "0";
                    }

                    distance = Math.max(Double.parseDouble(strDistanceEndTrip_sp), Double.parseDouble(distances));

                    Log.e(TAG, "onCreate: distance~~~~~~~~~~~~~~~~~~~~~~~> " + distance + "~~~" + distances);

                    if (Notify != null) {
                        switch (Notify) {
                            case "Offers": {
                                Bundle args = new Bundle();
                                args.putString("via", "Offers");
                                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOUR_OFFERS_ANNOCEMENTS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                break;
                            }
                            case "Announcements": {
                                Bundle args = new Bundle();
                                args.putString("via", "Announcements");
                                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOUR_OFFERS_ANNOCEMENTS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                break;
                            }
                            case "Pending": {
                                Bundle args = new Bundle();
                                args.putString("main", "pending");
                                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOURJOBS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                break;
                            }
                            case "Reminder": {
                                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MY_CALENDER_BOOKING,
                                        MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                break;
                            }
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Exception");
                }

                BaseActivity baseActivity = new BaseActivity();
                adContainer = findViewById(R.id.banner_container);
                baseActivity.callFacebookAdsCommon(MainActivity.this, adViewFacebook, adContainer);

                adViewGoogle = findViewById(R.id.adView);
                baseActivity.callGoogleAdsCommon(MainActivity.this, adViewGoogle);

                PackageManager manager = MainActivity.this.getPackageManager();
                PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
                String version = info.versionName;
                //Log.e("userid ~~ version ~~", version);
                txtVersion.setText("v" + version);

            } catch (Exception e) {
                Log.i(TAG, "onCreate: exception-->" + e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void initViews() {

        frameMap = (FrameLayout) findViewById(R.id.frame_mainactivity);

        lytOnlineOffline = (View) findViewById(R.id.lyt_online_offline);
        lytRequestReceive = (View) findViewById(R.id.lyt_request_receive);
        lytJobInprogress = (View) findViewById(R.id.lyt_job_inprogress);
        lytSMSalert = (View) findViewById(R.id.lyt_sms_alert);
        lytCardPayment = (View) findViewById(R.id.lyt_card_payment);
        lytCashPayment = (View) findViewById(R.id.lyt_cash_payment);
        dialogAdditionalEndCharge = findViewById(R.id.lyt_addtional_charge);
        dialogTripSummary = findViewById(R.id.lyt_trip_summary);

        //  bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker);
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_guard_marker);
        bitmapDescriptorMan = BitmapDescriptorFactory.fromResource(R.drawable.ic_center_blue_marker);

        switchmTraffic = findViewById(R.id.switchm_traffic);


        /*Navigation Drawer intialize*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        try {
            navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        View header = navigationView.getHeaderView(0);
        navigationName = (CustomTextView) header.findViewById(R.id.txt_nav_user_name);
        navigationWalletAmount = (CustomTextView) header.findViewById(R.id.txt_nav_walletbalance);
        navigationCustomerId = (CustomTextView) header.findViewById(R.id.txt_nav_user_id);
        navigationProfileImage = (SimpleDraweeView) header.findViewById(R.id.img_nav_profileimage);
        navRbUserRating = (SimpleRatingBar) header.findViewById(R.id.rb_nav_user_rating);
        navigationSetting = (ImageView) header.findViewById(R.id.img_nav_setting);
        navigationSubsPlan = header.findViewById(R.id.txt_subscribed_user);

        navigationView.setNavigationItemSelectedListener(this);
        navigationLogoutDriver = (RelativeLayout) findViewById(R.id.rl_signout_customer);
        txtVersion = (CustomTextView) findViewById(R.id.txt_version);

        addressTxtView = (CustomTextView) findViewById(R.id.addressTxtView);

        txtSmsAlertContent = (CustomTextView) findViewById(R.id.txt_sms);
        btnSmsAlert = (CustomButton) findViewById(R.id.btn_ok_sms);

        if (strDriverfname != null) {
            String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
            navigationName.setText(upperString);
        } else {
            navigationName.setText(getResources().getString(R.string.app_name));
        }

        if (strDriverProPic != null) {
            navigationProfileImage.setImageURI(Utils.BASE_URL + strDriverProPic);
            int color = getResources().getColor(R.color.colorPrimary);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setBorder(color, 1.0f);
            roundingParams.setRoundAsCircle(true);
            navigationProfileImage.getHierarchy().setRoundingParams(roundingParams);
        }

        /*Navigation Drawer end*/

        switchOnlineOffline = (SwitchButton) findViewById(R.id.switch_online_offline);
        txtOnlineOffline = (CustomTextView) findViewById(R.id.txt_onlineoffline);
        txtDrivernameOnline = (CustomTextView) findViewById(R.id.txt_drivername_online);
        txtPendingCount = (CustomTextView) findViewById(R.id.txt_pending_count);
        txtUpcomingCount = (CustomTextView) findViewById(R.id.txt_upcoming_count);
        ivProfpicOnline = (SimpleDraweeView) findViewById(R.id.iv_prof_pic_online);

        txtUpdateCurrencySetupOnline = (CustomTextView) findViewById(R.id.txt_update_currency_setup);
        txtUpdateLanguageSetupOnline = (CustomTextView) findViewById(R.id.txt_update_language_setup);

        txtUpdateAvailabilitySetupOnline = (CustomTextView) findViewById(R.id.txt_update_avialbility_setup);
        txtSubscriptionPlanSetupLabel = (CustomTextView) findViewById(R.id.update_subscription_setup);

        txtUpdateAvailabilitySetupOnline.setText(getResources().getString(R.string.myavailablitysub_header) + " ?");

        llGettingLocation = (LinearLayout) findViewById(R.id.ll_getting_location);
        llNotapproved = (LinearLayout) findViewById(R.id.ll_not_approved);

        rlPendingArea = (RelativeLayout) findViewById(R.id.rl_pending_area);
        rlUpcomingArea = (RelativeLayout) findViewById(R.id.rl_upcoming_area);

        if (strDriverfname != null) {
            String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
            txtDrivernameOnline.setText(upperString);
        } else {
            txtDrivernameOnline.setText(getResources().getString(R.string.app_name));
        }

        try {
            if (strDriverProPic != null) {
                ivProfpicOnline.setImageURI(Utils.BASE_URL + strDriverProPic);
                int color = getResources().getColor(R.color.colorPrimary);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(color, 1.0f);
                roundingParams.setRoundAsCircle(true);
                ivProfpicOnline.getHierarchy().setRoundingParams(roundingParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Request Screen start*/
        txtTimerCount = (CustomTextView) findViewById(R.id.txt_timer_count);

        txtJobTypeRequest = (CustomTextView) findViewById(R.id.txt_requestType);
        txtCategoryNameRequest = (CustomTextView) findViewById(R.id.txt_categoryName);
        txtPickupLocationRequest = (CustomTextView) findViewById(R.id.tx_pickup_loc_req);

        txtDropLocationRequestLabel = (CustomTextView) findViewById(R.id.txt_drop_req);
        txtDropLocationRequest = (CustomTextView) findViewById(R.id.tx_drop_loc_req);

        txtSpecialInstrRequest = (CustomTextView) findViewById(R.id.tx_special_instr);
        txtUserNameRequest = (CustomTextView) findViewById(R.id.txt_User_name);
        txtCount = (CustomTextView) findViewById(R.id.tx_count);
        txtDays = (CustomTextView) findViewById(R.id.tx_days);
        ratingBarRequest = (SimpleRatingBar) findViewById(R.id.ratingBar_request);

        btnDeclineRequest = (CustomButton) findViewById(R.id.btn_decline);
        btnAcceptRequest = (CustomButton) findViewById(R.id.btn_accept);

        txtTripTypeRequest = findViewById(R.id.txt_trip_type_request);
        txtEmergencyMsgRequest = findViewById(R.id.txt_emergency_msg);
        llBottomUserDetailsRequest = findViewById(R.id.ll_bottom_user_detail);


        trStartDateTimeRequest = findViewById(R.id.tr_start_date_time);
        trEndDateTimeRequest = findViewById(R.id.tr_end_date_time);
        txtStartDateTimeRequest = findViewById(R.id.txt_start_date_time);
        txtEndDateTimeRequest = findViewById(R.id.txt_end_date_time);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        ratingBarRequest.setOnTouchListener((v, event) -> true);

        callRequestTimePeriod();

        /*Request Screen end*/

        /*Job In progress start */
        switchLiveTrackJobinProgress = findViewById(R.id.switch_container_livetrack_jobinprogress);
        // slide-up animation
        slideEndtoStart = AnimationUtils.loadAnimation(this, R.anim.fade_out_speed);
        slideStarttoEnd = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        rlMapJobinprogress = findViewById(R.id.rl_map_job_inprogress);
        rlJobinprogress = findViewById(R.id.rl_job_inprogress);

        rlUserCancelRetryTop = (RelativeLayout) findViewById(R.id.rl_user_cancel_retry_top);
        rlUserCancelRetryCancelLyt = (RelativeLayout) findViewById(R.id.rl_user_retry_cancel);
        tvAcceptTripCancelRetry = (CustomTextView) findViewById(R.id.txt_accept_trip_cancel_retry);
        tvRetry = (CustomTextView) findViewById(R.id.tv_retry_user_cancel);
        tvCancelTripRetry = (CustomTextView) findViewById(R.id.tv_retry_user_cancel_trip);

        btnNavigationJobinprogress = findViewById(R.id.rl_btn_navigation_header);

        llBottomSlidetripHeader = (LinearLayout) findViewById(R.id.ll_btn_slide_trip);

        llNavigationBtnheader = (LinearLayout) findViewById(R.id.ll_navigation_btn_header);

        llTripviewone = (LinearLayout) findViewById(R.id.ll_trip_one);
        llTripviewtwo = (LinearLayout) findViewById(R.id.ll_trip_two);
        llTripviewthree = (LinearLayout) findViewById(R.id.ll_trip_three);
        llTripviewfour = (LinearLayout) findViewById(R.id.ll_trip_four);

        btnStarttrip = findViewById(R.id.ll_start_trip);
        btnArrivetrip = findViewById(R.id.ll_arrive_trip);
        btnBeginjobtrip = findViewById(R.id.ll_Begin_job_trip);
        btnEndjobtrip = findViewById(R.id.ll_end_job_trip);
        btnCancelJobsInProgress = findViewById(R.id.btn_call_jobs_job_inprogress);
        btnSOSBackupRequiredJobsInProgress = findViewById(R.id.btn_sos_backup_required_job_inprogress);

        ivMenuJobinprogress = (ImageView) findViewById(R.id.iv_menu_job_inprogress);
        ivAlertJobinprogress = (ImageView) findViewById(R.id.iv_alert_job_inprogress);
        ivAlertMain = (ImageView) findViewById(R.id.iv_alert);
        ivuserProfpicJobinprogress = (SimpleDraweeView) findViewById(R.id.iv_user_prof_pic_job_inprogress);
        ratingbarJobinprogress = (SimpleRatingBar) findViewById(R.id.ratingbar_job_inprogress);

        txtPickuplocMapjobinprogress = (CustomTextView) findViewById(R.id.txt_Pickup_loc);
        txtDroplocMapjobinprogress = (CustomTextView) findViewById(R.id.txt_drop_loc);
        txtHintJobinprogress = (CustomTextView) findViewById(R.id.txt_hint_job_inprogress);
        txtTitleJobinprogres = (CustomTextView) findViewById(R.id.txt_job_inprogres_title);

        rlUserDetailsLytJobinprogress = findViewById(R.id.rl_parent_lyt_user_details_jobinprogress);
        txtUsernameJobinprogress = (CustomTextView) findViewById(R.id.txt_username_job_inprogress);
        txtPickupJobinprogress = (CustomTextView) findViewById(R.id.txt_pickup_job_inprogress);
        llUserCallMessageJobinprogress = findViewById(R.id.ll_call_msg_lyt);

        ivCallJobinprogress = findViewById(R.id.img_phone);
        ivMessageJobinprogress = findViewById(R.id.img_message);


        txtCompanyJobinprogress = (CustomTextView) findViewById(R.id.txt_company_name);
        txtCountJobinprogress = (CustomTextView) findViewById(R.id.txt_job_count);
        txtDaysJobinprogress = (CustomTextView) findViewById(R.id.txt_job_days);

        txtAcceptone = (CustomTextView) findViewById(R.id.txt_accept_one);
        txtAccepttwo = (CustomTextView) findViewById(R.id.txt_accept_two);
        txtAcceptthree = (CustomTextView) findViewById(R.id.txt_accept_three);
        txtAcceptfour = (CustomTextView) findViewById(R.id.txt_accept_four);

        txtAccepttimeone = (CustomTextView) findViewById(R.id.txt_accept_time_one);
        txtAccepttimetwo = (CustomTextView) findViewById(R.id.txt_accept_time_two);
        txtAccepttimethree = (CustomTextView) findViewById(R.id.txt_accept_time_three);
        txtAccepttimefour = (CustomTextView) findViewById(R.id.txt_accept_time_four);

        txtMinagoone = (CustomTextView) findViewById(R.id.txt_min_ago_one);
        txtMinagotwo = (CustomTextView) findViewById(R.id.txt_min_ago_two);
        txtMinagothree = (CustomTextView) findViewById(R.id.txt_min_ago_three);
        txtMinagofour = (CustomTextView) findViewById(R.id.txt_min_ago_four);

        /*Extend Hours*/
        llExtendJobsInProgressDetailsLyt = findViewById(R.id.ll_extra_hours_job_inprogress_lyt);
        txtExtendHrsJobsInProgress = findViewById(R.id.txt_extra_no_of_hrs);
        txtExtendHrsAmountJobsInProgress = findViewById(R.id.txt_extend_no_of_hrs_amount);
        txtExtendHrsVATAmountJobsInProgress = findViewById(R.id.txt_extend_vat_amount);
        /*Extend Hours*/

        lytCardPaymentTitleMsg = findViewById(R.id.tab_host);
        txtCardPaymentTitle = findViewById(R.id.txt_card_payment_title);
        txtCardPaymentMsg = findViewById(R.id.txt_card_payment_msg);
        btnCardPayment = findViewById(R.id.btn_card_payment);
        btnGOTTHECASH = findViewById(R.id.txt_cash_label);


        /*Dialog Additional charge*/
        llParentLytAdditionalCharge = findViewById(R.id.ll_parent_lyt_AdditionalCharge);

        txtCurrentChargelabel = (CustomTextView) findViewById(R.id.txt_current_charge_label_add_charge);
        txtMaterialFeelabel = (CustomTextView) findViewById(R.id.txt_material_fee_label_add_charge);
        txtMiscFeelabel = (CustomTextView) findViewById(R.id.txt_misc_fee_label_add_charge);
        txtDiscountDriverlabel = (CustomTextView) findViewById(R.id.txt_discount_driver_label_add_charge);
        txtFinalTotallabel = (CustomTextView) findViewById(R.id.txt_final_total_label_add_charge);

        txtDistance$LabelAdditionalCharge = (CustomTextView) findViewById(R.id.txt_distance_label_km_add_charge);
        txtDistanceAdditionalCharge = (CustomTextView) findViewById(R.id.txt_distance_charge_add_charge);
        txtPriceperKmAdditionalCharge = (CustomTextView) findViewById(R.id.txt_distance_priceper_km_add_charge);

        llDistanceAdditionalCharge = (LinearLayout) findViewById(R.id.ll_distance_lyt_add_charge);

        txtCurrentCharge = (CustomTextView) findViewById(R.id.txt_current_charge_add_charge);
        txtFinalTotal$ = (CustomTextView) findViewById(R.id.txt_final_total_dollar_add_charge);
        txtFinalTotal = (CustomTextView) findViewById(R.id.txt_final_total_add_charge);

        edtMaterialFee = (CustomEditText) findViewById(R.id.edt_material_fee_add_charge);
        edtMiscFee = (CustomEditText) findViewById(R.id.edt_misc_fee_add_charge);
        edtDiscountDriver = (CustomEditText) findViewById(R.id.edt_discount_driver_add_charge);

        txtCurrentchargelabel$ = (CustomTextView) findViewById(R.id.txt_current_charge_label_1_add_charge);
        txtMaterialfeelabel$ = (CustomTextView) findViewById(R.id.txt_material_fee_label_1_add_charge);
        txtMiscfeelabel$ = (CustomTextView) findViewById(R.id.txt_misc_fee_label_1_add_charge);
        txtDiscountdriverlabel$ = (CustomTextView) findViewById(R.id.txt_discount_driver_label_1_add_charge);

        txtadditionalAgentCost = dialogAdditionalEndCharge.findViewById(R.id.txt_additional_agent_charge);
        txtAdditionalDayCharge = dialogAdditionalEndCharge.findViewById(R.id.txt_additional_day_charge);

        txtAgentchargeLabel = dialogAdditionalEndCharge.findViewById(R.id.txt_agent_charge_label);
        txtDaychargeLabel = dialogAdditionalEndCharge.findViewById(R.id.txt_day_charge_label);

        txtAgentchargelabel$ = dialogAdditionalEndCharge.findViewById(R.id.txt_agent_charge_label_1);
        txtDaychargelabel$ = dialogAdditionalEndCharge.findViewById(R.id.txt_day_charge_label_1);

        btnSkipAddcharge = (CustomButton) findViewById(R.id.btn_skip_additonal_charge);
        btnSubmitAddcharge = (CustomButton) findViewById(R.id.btn_submit_additonal_charge);

        ivCloseAdditionalCharge = (ImageView) findViewById(R.id.iv_cancel_add_charge);
        /*Job In progress end*/


        /*Fare Summary Dialog view */
        llParentLytTripSummary = findViewById(R.id.ll_parent_lyt_trip_summary);

        btnCollectPaymentTripSummary = (CustomButton) findViewById(R.id.btn_Collect_payments);

        ratingbarUserTripsummary = (SimpleRatingBar) findViewById(R.id.ratingbar_trip_summary);
        edtCommentTripsummary = (CustomEditText) findViewById(R.id.edt_comment_trip_summary);
        btnRatingpayment = (CustomButton) findViewById(R.id.btn_rating_payment);
        cvRating = (CardView) findViewById(R.id.cv_rating);

        llCurrentChargelyt = (LinearLayout) findViewById(R.id.ll_current_charge_lyt);
        llMaterialFeelyt = (LinearLayout) findViewById(R.id.ll_material_fee_lyt);
        llMiscFeelyt = (LinearLayout) findViewById(R.id.ll_misc_fee_lyt);
        llDiscountlyt = (LinearLayout) findViewById(R.id.ll_discount_lyt);
        llFinalTotallyt = (LinearLayout) findViewById(R.id.ll_final_total_lyt);

        txtFinalTotalHeaderTripSummary = (CustomTextView) findViewById(R.id.txt_total_fare);
        txtJobDateTripSummary = (CustomTextView) findViewById(R.id.txt_date_trip_summary);
        txtDiscountTripSummary = (CustomTextView) findViewById(R.id.txt_discount_trip_summary);
        txtPaymentTypeTripSummary = (CustomTextView) findViewById(R.id.txt_payment_type_trip_summary);
        txtCategoryTripSummary = (CustomTextView) findViewById(R.id.category_trip_summary);
        txtCurrentChargeLabelTripSummary = (CustomTextView) findViewById(R.id.txt_current_charge_label);
        txPromoCodeTripSummary = (CustomTextView) findViewById(R.id.tx_promo_code);

        txtFinalTotalTripSummary = (CustomEditText) findViewById(R.id.txt_final_total);
        txtCurrentChargeTripSummary = (CustomEditText) findViewById(R.id.txt_current_charge);
        txtMaterialFeeTripSummary = (CustomEditText) findViewById(R.id.edt_material_fee);
        txtMiscFeeTripSummary = (CustomEditText) findViewById(R.id.edt_misc_fee);
        txtDiscountDriverTripSummary = (CustomEditText) findViewById(R.id.edt_discount_driver);

        llDistanceLytTripSummary = (LinearLayout) findViewById(R.id.ll_distance_lyt);

        llPickupDropLyt = (LinearLayout) findViewById(R.id.ll_pickup_drop_lyt);
        txtPickupLocTripSummary = (CustomTextView) findViewById(R.id.tx_pickup_loc);
        txtDropLocTripSummary = (CustomTextView) findViewById(R.id.tx_drop_loc);
        txtDropLocTripSummaryLabel = (CustomTextView) findViewById(R.id.txt_drop);

        txtDistanceLabel = (CustomTextView) findViewById(R.id.txt_distance_driver_label);
        txtDistance = (CustomEditText) findViewById(R.id.edt_distance_driver);

        trDeductionAmountLytTripSummary = findViewById(R.id.tr_deduction_fare_lyt);
        txtDeductionAmountTripSummary = findViewById(R.id.txt_deduction_amount_trip_summary);

        trBonusAmountLytTripSummary = findViewById(R.id.tr_bonus_fare_lyt);
        txtBonusAmountTripSummary = findViewById(R.id.txt_bonus_amount_trip_summary);

        /*Fare Summary Dialog view */


        rlParentPaymentConfirmation = findViewById(R.id.rl_parent_payment_cnf);
        btnYesPaymentConfirmation = findViewById(R.id.btn_ok_payment_cnf);
        btnNoPaymentConfirmation = findViewById(R.id.btn_cancel_payment_cnf);
        ivClosePaymentConfirmation = findViewById(R.id.iv_close_payment_cnf);
        txtCashStatusTripSummary = findViewById(R.id.generalCommentTxt);
        txtAgentFeeLabel = findViewById(R.id.txt_agent_fee_label);
        txtDayFeeLabel = findViewById(R.id.txt_day_fee_label);
        txtAgentCharge = findViewById(R.id.txt_agent_charge);
        txtDayCharge = findViewById(R.id.txt_day_charge);
        txtBaseCharge = findViewById(R.id.txt_base_charge);


        /*Hours Changes*/
        txtNoofHrsTripSummary = findViewById(R.id.txt_no_of_hrs_trip_summary);
        txtPricePerHrsLabelTripSummary = findViewById(R.id.txt_price_per_hrs_trip_summary_label);
        txtPricePerHrsTripSummary = findViewById(R.id.txt_price_per_hrs_trip_summary);
        txtVatAmountTripSummary = findViewById(R.id.txt_vat_amount_trip_summary);
        trExtendNoofHrsTripSummary = findViewById(R.id.tr_extra_no_of_hrs_trip_summary);
        txtExtendNoofHrsTripSummary = findViewById(R.id.txt_extra_no_of_hrs_trip_summary);
        /*Hours Changes*/

        txtSubscribtionStatusTripSummary = findViewById(R.id.txt_subscribed_user_trip_summary);
        txtCommissionAmountTripSummary = findViewById(R.id.txt_commission_amount_trip_summary);
        txtTotalTripSummary = findViewById(R.id.txt_total_amount_trip_summary);

        builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);

        viewlocationRequest = findViewById(R.id.location_request);
        llDocApproved = findViewById(R.id.ll_doc_approved);
        viewParentLocReq = findViewById(R.id.rl_parent_loc_req);
        requestAccept = findViewById(R.id.loc_req_on);
        txtDocApproved = findViewById(R.id.txt_doc_approved);

        txtBookingIdCommon = findViewById(R.id.txt_bookingid);

        viewParentLocReq.setOnClickListener(view -> {
        });

        /*Volenters SOS Backup Required*/
        dialogRequesting = findViewById(R.id.lyt_dialog_requesting);
        View rlParentLytRequest = findViewById(R.id.rl_parent_lyt_request);
        llParentLytBackupGuardRequestAccepted = findViewById(R.id.ll_parent_lyt_backup_guard_accepted);
        View btnOkayBackupGuardRequestAccepted = findViewById(R.id.btn_okay_backup_guard_accepted);

        txtCountTimer = findViewById(R.id.txt_count_timer);
        rippleBackgroundRequesting = findViewById(R.id.rippleBackground);
        btnCancelRequest = findViewById(R.id.btn_cancel_request);

        rlBackupGuardListParentLytJobsInProgress = findViewById(R.id.ll_backup_guard_list_view_lyt);
        rcvGuardDetailsList = findViewById(R.id.rcv_guard_details_list);


        rlBackupRequestedGuardDetailsParentLytJobsInProgress = findViewById(R.id.rl_backup_requested_guard_details_lyt);
        txtBackupRequestedGuardLabelJIP = findViewById(R.id.txt_backup_requested_guard_details_job_inprogress);
        txtBackupRequestedGuardNameJIP = findViewById(R.id.txt_backup_requested_guard__job_inprogress);
        ratingBarBackupRequestedGuardJIP = findViewById(R.id.backup_requested_guard_ratingbar_job_inprogress);
        ivBackupRequestedGuardProfPicJIP = findViewById(R.id.iv_backup_requested_guard__prof_pic_job_inprogress);

        ratingBarBackupRequestedGuardJIP.setOnTouchListener((v, event) -> true);

        llParentLytBackupGuardRequestAccepted.setOnClickListener(view -> {
        });
        rlParentLytRequest.setOnClickListener(view -> {
        });
        btnCancelRequest.setOnClickListener(view1 -> callCancelBooking("2"));

        btnOkayBackupGuardRequestAccepted.setOnClickListener(view -> callCancelBooking("0"));


        // countdown for trip
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100); //You can manage the time of the blink with this parameter
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);

        String strRequestCount = getspCommonState.getString(Utils.SP_COMMON_REQUEST_TIME_DELAY_ACCEPT, String.valueOf(Utils.countDownTimeLimit));
        Log.e(TAG, "callRequestTimePeriod: ~~~~~~~~~~~~~~~~~~~~" + strRequestCount);

        //count down timer
        countDownTimerNormalReceivedRequest = new CountDownTimer(Long.parseLong(strRequestCount) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                String timeRemaining = getString(R.string.countdown_timer, minutes, seconds);
                txtTimerCount.setText(timeRemaining);

                timer = Integer.parseInt(String.valueOf(millisUntilFinished / 1000));
                if (timerStart) {
                    if (timer <= 10) {
                        timerStart = false;
                        Log.e(TAG, "onTick: Timer~~~ " + timer);
                        txtTimerCount.startAnimation(anim);
                    }
                }
            }

            public void onFinish() {
                Log.i(TAG, "onFinish: called ");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER);
                databaseReference.child(Utils.FIREBASE_SERVICE_STATUS).setValue("0");

                removeRequestDetailsFirebase();

                Log.i(TAG, "onFinish: ");
                txtTimerCount.setText("00:00");
                stopPlaying();
                switch (reqStatus) {
                    case "0":

                        lytOnlineOffline.setVisibility(View.VISIBLE);
                        lytRequestReceive.setVisibility(View.GONE);
                        lytJobInprogress.setVisibility(View.GONE);
                        break;
                    case "1":
                        lytOnlineOffline.setVisibility(View.GONE);
                        lytRequestReceive.setVisibility(View.VISIBLE);
                        lytJobInprogress.setVisibility(View.GONE);
                        break;
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                        lytOnlineOffline.setVisibility(View.GONE);
                        lytRequestReceive.setVisibility(View.GONE);
                        lytJobInprogress.setVisibility(View.VISIBLE);
                        break;
                }

                countDownTimerNormalReceivedRequest.cancel();
                //removeRequestDetailsFirebase();
                Log.i("Inside timer count", "finish at 0");
            }
        };

        countDownTimerSOSBackupRequest = new CountDownTimer(Long.parseLong(strRequestCount) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txtCountTimer.setText("00:" + millisUntilFinished / 1000);
                // timer = Integer.valueOf(String.valueOf(millisUntilFinished));
            }

            public void onFinish() {
                new CountDownTimer(300, 100) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            Log.i("Inside timer count", "finish");

                            setVisibility(dialogRequesting, false);
                            setVisibility(llParentLytBackupGuardRequestAccepted, false);

                            if (countDownTimerSOSBackupRequest != null) {
                                countDownTimerSOSBackupRequest.cancel();
                            }

                            if (rippleBackgroundRequesting != null) {
                                rippleBackgroundRequesting.stopRippleAnimation();
                            }

                            removeProviderBackupStatus("3");

                            showAlertDialogTimeOut();

                        } catch (Exception e) {
                            e.printStackTrace();
                            //TODO: Fill in exception
                        }
                    }
                }.start();
            }
        };

    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

        switchmTraffic.setOnCheckedChangeListener((compoundButton, b) -> updateTraffic());

        /*Dialog Currency charge*/
        dialogLanguage = new Dialog(MainActivity.this);
        dialogLanguage.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogLanguage.setContentView(R.layout.dialog_static_language);
        Objects.requireNonNull(dialogLanguage.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLanguage.setCancelable(true);
        dialogLanguage.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
        lps.copyFrom(dialogLanguage.getWindow().getAttributes());
        lps.width = WindowManager.LayoutParams.MATCH_PARENT;
        lps.height = WindowManager.LayoutParams.WRAP_CONTENT;

        rcvLanguage = (RecyclerView) dialogLanguage.findViewById(R.id.rcv_language);
        llStaticLanguage = (LinearLayout) dialogLanguage.findViewById(R.id.ll_static_language);

        rcvLanguage.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, rcvLanguage, new RecyclerTouchListener.OnTouchActionListener() {
            @SuppressLint({"LongLogTag", "ObsoleteSdkInt"})
            @Override
            public void onClick(View view, int position) {
                Log.i("rcvLanguage", "onClick: rcvLanguage-> ");
                if (position != -1) {
                                  /*  listFeed  = emergencyModelArray.get(position);
                                    callDeleteEmergencyDetails(listFeed.getContact_id());*/
                    String langId = String.valueOf(ListModel.get(position).getId());
                    String someVarA = String.valueOf(ListModel.get(position).getLanguageName());
                    String someVarB = String.valueOf(ListModel.get(position).getLanguageCode());
                    Log.e("Language~~Onclick~~", langId + "~~" + someVarA);
                    dialogLanguage.dismiss();

                    CallUpdateDetails(langId, someVarA, someVarB, "langDialog");

                }
            }

            @Override
            public void onRightSwipe(View view, int position) {
            }

            @Override
            public void onLeftSwipe(View view, int position) {
            }
        }));


        /*Dialog Currency charge*/
        dialogCurrency = new Dialog(MainActivity.this);
        dialogCurrency.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogCurrency.setContentView(R.layout.dialog_static_currency);
        Objects.requireNonNull(dialogCurrency.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCurrency.setCancelable(true);
        dialogCurrency.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCurrency.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        rcvCurrency = (RecyclerView) dialogCurrency.findViewById(R.id.rcv_currency);
        llStaticCurrency = (LinearLayout) dialogCurrency.findViewById(R.id.ll_static_currency);

        rcvCurrency.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, rcvCurrency, new RecyclerTouchListener.OnTouchActionListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view, int position) {
                Log.i("rvCategory", "onClick: rvCategory-> ");
                if (position != -1) {

                    String curId = String.valueOf(ListModelCurrency.get(position).getId());
                    String curName = String.valueOf(ListModelCurrency.get(position).getCurrency());
                    String curShortName = String.valueOf(ListModelCurrency.get(position).getShortName());
                    String curSymbol = String.valueOf(ListModelCurrency.get(position).getSymbol());
                    Log.e("Language~~Onclick~~", curId + "~~" + curName + "~" + curShortName + "~~" + curSymbol);
                    dialogCurrency.dismiss();
                    //edtCurrency.setText(curShortName);
                    callCurrencyUpdate(curId, curShortName, "update");
                }

            }

            @Override
            public void onRightSwipe(View view, int position) {
            }

            @Override
            public void onLeftSwipe(View view, int position) {
            }
        }));

        navigationLogoutDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });

        navigationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawerMethod();
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });

        txtUpdateCurrencySetupOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCurrency != null) {
                    dialogCurrency.show();
                }
            }
        });

        txtUpdateLanguageSetupOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogLanguage != null) {
                    dialogLanguage.show();
                }
            }
        });

        txtUpdateAvailabilitySetupOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MYAVAILABILITY_NEW, MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });

        btnSmsAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lytSMSalert.setVisibility(View.GONE);
            }
        });


        switchOnlineOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.i(TAG, "onCheckedChanged: check-->" + isChecked);
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("MissingPermission~~~", "currentLocationClick~~MissingPermission");
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    viewlocationRequest.setVisibility(View.VISIBLE);

                    requestAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            out.println("checked in");
                            viewlocationRequest.setVisibility(View.GONE);
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    });

                    //   btnEndjobtrip.setEnabled(true);

                } else {
                    Log.e("locCurrentLocation~~", "Checkinstate``` ~~" + currentLocation);
                    if (currentLocation != null) {
                        if (isGpsEnable()) {
                            aboolean = false;
                            CallUpdateDriverOnlineOfflineStatus("1", String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));
                        } else {
                            updateDriverLocationFirebase(0.0, 0.0, null);
                            updateDriverAvailableStatus("0");
                        }
                    } else {
                        locationEnableDialogMessage();
                    }
                }
            } else {
                out.println("checked out");
                if(!doSwitchBtnCall) {
                    CallUpdateDriverOnlineOfflineStatus("0", "0.0", "0.0"); // check out driver
                }
            }
        });


        ivAlertJobinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewEmergencyDetails();
            }
        });

        ivAlertMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callViewEmergencyDetails();

            }
        });


        tvCancelTripRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCancelJobinfos();
            }
        });

        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*DatabaseReference requestAcceptrDatabaseBRef;
                requestAcceptrDatabaseBRef = FirebaseDatabase.getInstance().getReference().child("providers").child(strdriverIdFB).child(Utils.FIREBASE_TRIP_HEADER);
                requestAcceptrDatabaseBRef.child("request_status").setValue("3");

                DatabaseReference requestAcceptrDatabaseBRefs;
                requestAcceptrDatabaseBRefs = FirebaseDatabase.getInstance().getReference().child("booking_trips").child(strBookingId);
                requestAcceptrDatabaseBRefs.child("request_status").setValue("3");*/

                // rlUserCancelRetryTop.setVisibility(View.GONE);
                // rlUserCancelRetryCancelLyt.setVisibility(View.GONE);
            }
        });

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimerNormalReceivedRequest.cancel();
                callClickAction(btnAcceptRequest, false);
                stopPlaying();
                stopPlaying();
                callAcceptRequest();
            }
        });

        btnDeclineRequest.setOnClickListener(v -> {

            countDownTimerNormalReceivedRequest.cancel();
            callClickAction(btnAcceptRequest, true);
            callClickAction(btnDeclineRequest, true);

            //callNotifySend(struserIdFB, driverId);

            stopPlaying();
            stopPlaying();

            /*DatabaseReference databaseReferences = FirebaseDatabase.getInstance().getReference();
            databaseReferences.child(Utils.FIREBASE_USER_HEADER).child(struserIdFB).child(Utils.FIREBASE_TRIP_HEADER).child(Utils.FIREBASE_ACCEPT_STATUS).setValue("1");*/

            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER).child(Utils.FIREBASE_SERVICE_STATUS).setValue("0");

            removeRequestDetailsFirebase();

            lytJobInprogress.setVisibility(View.GONE);
            lytOnlineOffline.setVisibility(View.VISIBLE);
            lytRequestReceive.setVisibility(View.GONE);

            if (strTransferJobStatusFB.equals("0") && strBackupVolunteerStatusFB.equals("0")) {
                callRemoveDriver();
            }

        });

        btnStarttrip.setOnClickListener(this::callClickView_Ontheway);

        btnArrivetrip.setOnClickListener(this::callClickView_Arrive);

        btnNavigationJobinprogress.setOnClickListener(v -> dialogShowNavigations());

        btnBeginjobtrip.setOnClickListener(this::callClickView_Begin);

        btnEndjobtrip.setOnClickListener(this::callClickView_End);

        btnCancelJobsInProgress.setOnClickListener(view -> dialogCancelJobinfos());

        btnRatingpayment.setOnClickListener(v -> {
            if (edtCommentTripsummary.getText().toString().trim().length() == 0) {
                edtCommentTripsummary.setError(getString(R.string.error_required));
            } else {
                callUpdateRating();
            }
        });

        llParentLytAdditionalCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        llParentLytTripSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnCollectPaymentTripSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (driverId != null && !driverId.isEmpty()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("providers").child(driverId).child("distance").setValue("0");
                        ref.child("providers").child(driverId).child("est_distance").setValue("0");
                        ref.child("providers").child(driverId).child("est_duration").setValue("0");
                        ref.child("providers").child(driverId).child("trips").child("service_status").setValue("0");
                    }

                    SharedPreferences preferences = getSharedPreferences(Utils.MY_PREFS_FB_CATEGORY_TRIPSTATE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();


                    distance = 0;

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                    spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    strDistanceEndTrip = "0";

                    if (dialogTripSummary != null) {
                        /*if (dialogTripSummary.isShowing()) {
                            dialogTripSummary.dismiss();
                        }*/

                        if (dialogTripSummary.getVisibility() == View.VISIBLE) {
                            dialogTripSummary.setVisibility(View.GONE);
                        }
                    }

                    lytOnlineOffline.setVisibility(View.VISIBLE);
                    lytJobInprogress.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.GONE);

                    removeRequestDetailsFirebase();

                    callViewProfileDetails();

                    Utils.CURRENT_TAG = null;

                    callStopGeoFencing();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rlPendingArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("main", "pending");
                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOURJOBS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });
        rlUpcomingArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("main", "upcoming");
                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOURJOBS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
            }
        });

        ratingbarJobinprogress.setOnTouchListener((v, event) -> true);

        btnCardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strPaymentTypeFB.equals("card")) {
                    callEndtrip("card");
                } else if (strPaymentTypeFB.equals("paypal")) {
                    callEndtrip("paypal");
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.check_payment_type), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGOTTHECASH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setCancelable(false);

                Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

                BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
                SpannableString spannableString = new SpannableString(getResources().getString(R.string.got_cash));
                spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStrings = new SpannableString(getString(R.string.are_you_sure_want_got_the_cash_for_this_job));
                spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStringss = new SpannableString(getString(R.string.yes));
                spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStringsss = new SpannableString(getString(R.string.no));
                spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setTitle(spannableString);

                builder.setMessage(spannableStrings);

                builder.setPositiveButton(spannableStringss, (dialog, which) -> {

                    DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                    refs.child("booking_trips").child(strBookingId).child("cash_status").setValue("0");
                    refs.child("booking_trips").child(strBookingId).child("card_status").setValue("0");
                    refs.child("booking_trips").child(strBookingId).child("paypal_status").setValue("0");
                    refs.child("booking_trips").child(strBookingId).child("payment_type").setValue("cash");
                    refs.child("booking_trips").child(strBookingId).child("paymode").setValue("cash");
                    refs.child("booking_trips").child(strBookingId).child("service_status").setValue("5.2");

                    refs.child("providers").child(driverId).child("trips").child("cash_status").setValue("0");
                    refs.child("providers").child(driverId).child("trips").child("card_status").setValue("0");
                    refs.child("providers").child(driverId).child("trips").child("paypal_status").setValue("0");
                    refs.child("providers").child(driverId).child("trips").child("payment_type").setValue("cash");
                    refs.child("providers").child(driverId).child("trips").child("paymode").setValue("cash");
                    refs.child("providers").child(driverId).child("trips").child("service_status").setValue("5.2");

                            /*payment_type - cash
                            cash_status -1
                            card_status -0
                            service_status - 5.2*/

                    strPaymentTypeFB = "cash";

                    String from = spTripState.getString(Utils.SP_MAP_TRIPSTATE_ADDITIONAL_CHARGE_LAST_CLICK, "skip");
                    finalAmountResult(from);

                });

                builder.setNegativeButton(spannableStringsss, (dialog, which) -> dialog.dismiss());

                builder.show();

            }
        });

        rlParentPaymentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnYesPaymentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCashonHandPayment();
            }
        });

        btnNoPaymentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updateCashonHandPayment("no");
            }
        });

        ivClosePaymentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                refs.child("booking_trips").child(strBookingId).child("cash_status").setValue("2");
                refs.child("providers").child(driverId).child("trips").child("cash_status").setValue("2");
            }
        });

        btnSOSBackupRequiredJobsInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callClickAction(btnSOSBackupRequiredJobsInProgress, false);

                removeProviderBackupStatus("0");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setCancelable(false);

                Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

                BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);

                SpannableString spannableStringst = new SpannableString(getString(R.string.backup_required));
                spannableStringst.setSpan(tfSpan, 0, spannableStringst.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStrings = new SpannableString(getString(R.string.are_you_sure_want_sos_backup_required));
                spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStringss = new SpannableString(getString(R.string.yes));
                spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString spannableStringsss = new SpannableString(getString(R.string.no));
                spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setTitle(spannableStringst);

                builder.setMessage(spannableStrings);

                builder.setPositiveButton(spannableStringss, (dialog, which) -> {
                    callRequestBooking();
                });
                builder.setNegativeButton(spannableStringsss, (dialog, which) -> {
                    dialog.dismiss();
                    callClickAction(btnSOSBackupRequiredJobsInProgress, true);
                });

                builder.show();
            }
        });

        ivCallJobinprogress.setOnClickListener(view -> {
            callClickAction(ivCallJobinprogress, false);
            if (!checkPermissionForMicrophone()) {
                requestPermissionForMicrophone();
                callClickAction(ivCallJobinprogress, true);
            } else {
                toIdentity = "InstaGuard_user" + struserIdFB;
                if (accessToken != null) {
                    params.put("to", toIdentity);
                    ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken).params(params).build();
                    activeCall = Voice.connect(MainActivity.this, connectOptions, callListener);
                    setCallUI();
                    callClickAction(ivCallJobinprogress, true);
                } else {
                    callClickAction(ivCallJobinprogress, true);
                    retrieveAccessToken();
                }
                callClickAction(ivCallJobinprogress, true);
            }
        });

        ivMessageJobinprogress.setOnClickListener(view -> {
            callClickAction(ivMessageJobinprogress, false);
            retrieveAccessTokenfromServer();
        });
    }

    private void callSwitchLiveTrackJobinprogress() {

        switchLiveTrackJobinProgress.setCheckedTogglePosition(0);
        Log.e("Inside ", "infirststate" + switchLiveTrackJobinProgress.getCheckedTogglePosition());

        switchLiveTrackJobinProgress.setOnToggleSwitchChangeListener((position, isChecked) -> {

            Log.e(TAG, "onToggleSwitchChangeListener: " + position + " ~~~" + isChecked);

            if (position == 0) {
                Log.e(TAG, "onToggleSwitchChangeListener: Login ~ 000");
                if (rlJobinprogress.getVisibility() == View.VISIBLE) {
                    rlJobinprogress.setVisibility(View.GONE);
                    // scrollviewJobInprogress.startAnimation(slideEndtoStart);
                }
                if (rlMapJobinprogress.getVisibility() == View.GONE) {
                    rlMapJobinprogress.setVisibility(View.VISIBLE);
                    // scrollviewJobInprogress.startAnimation(slideEndtoStart);
                }

            } else {
                Log.e(TAG, "onToggleSwitchChangeListener: Signup ~ 111");
                if (rlJobinprogress.getVisibility() == View.GONE) {
                    rlJobinprogress.setVisibility(View.VISIBLE);
                    // scrollviewJobInprogress.startAnimation(slideEndtoStart);
                }
                if (rlMapJobinprogress.getVisibility() == View.VISIBLE) {
                    rlMapJobinprogress.setVisibility(View.GONE);
                    // scrollviewJobInprogress.startAnimation(slideEndtoStart);
                }

            }
        });
    }

    /*-****************************************************
     *
     * Navigation Drawer
     *
     * ****************************************************/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_myprofile) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_SETTINGS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_myavailability) {
            //  Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MYAVAILABILITY_NEW,
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MYAVAILABILITY,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_manageservices) {
            Utils.CURRENT_TAG = null;
            Bundle args = new Bundle();
            args.putString("key", "main");
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MANAGE_SERVICE,
                    MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_managedocument) {
            Intent i = new Intent(MainActivity.this, DocumentActivity.class);
            i.putExtra("from", "vehicle");
            startActivity(i);
        } else if (id == R.id.nav_yourjobs) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOURJOBS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_bankdetails) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_BANKDETAIL_NEW,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_userPayouts) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_PAYOUT,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_mywallet) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MYWALLET,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_emergencycontact) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_EMERGENCY_CONTACTS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_userfeedback) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_USERFEEDBACK,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_invitefriends) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_INVITEFRIENDS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_support) {
            Utils.CURRENT_TAG = null;
            Bundle args = new Bundle();
            args.putString(Utils.B_SUPPORT_FROM, "main");
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_SUPPORT,
                    MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_notification) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOUR_OFFERS_ANNOCEMENTS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_payout) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_EARNINGS,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_language) {
            if (dialogLanguage != null) {
                dialogLanguage.show();
            }
        } else if (id == R.id.nav_currency) {
            if (dialogCurrency != null) {
                dialogCurrency.show();
            }
        } else if (id == R.id.menu_finger) {
            try {
                Utils.CURRENT_TAG = null;
                Intent i = new Intent(getApplicationContext(), SettingsFaceFingerscreen.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //  finish();
            } catch (Exception e) {
                Log.e(TAG, "onNavigationItemSelected: " + e.toString());
                e.printStackTrace();
            }
        } else if (id == R.id.nav_subscription_plans) {
            Bundle argss = new Bundle();
            argss.putString("from", "main");
            Utils.switchContent(R.id.frame_mainactivity,
                    Utils.FRAGMENT_SUBSCRIPTION_PLAN,
                    MainActivity.this, argss,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        } else if (id == R.id.nav_my_event_booking) {
            Utils.CURRENT_TAG = null;
            Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_MY_CALENDER_BOOKING,
                    MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public static void openDrawerMethod() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawerMethod() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void menubuttonClick(View view) {
        openDrawerMethod();
    }

    public static void frameClickable(Boolean bool) {
        frameMap.setClickable(bool);
    }

    /*-*************************************************
     *
     * Common
     *
     * *************************************************/

    public boolean isOnline() {
        boolean connection = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        if ((activeNetwork != null) && activeNetwork.isConnected()) {
            connection = true;
        }
        Log.i(TAG, "isOnline: " + connection);
        return connection;
    }

    private void applyFontInMenuItems() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {

        Typeface font = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.App_Font));
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this).enableAutoManage(this, 0, this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

    }


    private void callRequestTimePeriod() {
    }

    /*-*************************************************
     *
     * SharedPreferences
     *
     * *************************************************/

    @SuppressLint("CommitPrefEdits")
    private void callSharedPreferences() {


        putCommonState = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        putStateFBCategory = getSharedPreferences(Utils.MY_PREFS_FB_CATEGORY_TRIPSTATE, MODE_PRIVATE).edit();
        spCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);

        getStateCurrency = getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE).edit();

        getspCommonState = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = getspCommonState.getString(Utils.SP_DRIVERID, null);

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        tokenExpTime = getspCommonState.getString(Utils.SP_DRIVER_TOKENEXPTIME, null);
        strDriverProPic = getspCommonState.getString(Utils.SP_DRIVER_PRO_PIC, null);
        strDriverfname = getspCommonState.getString(Utils.SP_DRIVER_FNAME, null);
        strDriverlname = getspCommonState.getString(Utils.SP_DRIVER_LNAME, null);
        strWalletAmt = getspCommonState.getString(Utils.SP_DRIVER_WALLET_AMT, null);
/*        strCurrency = getspCommonState.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = getspCommonState.getString(Utils.SP_CURRENCY_SYMBOL, null);*/

        currencyCommonPrice = spCurrency.getString(Utils.SP_CURRENCY_COMMON_VALUES, null);
        Log.i(TAG, "convertCurrencyPrice: Main -->" + currencyCommonPrice);

        getspFBCategory = getSharedPreferences(Utils.MY_PREFS_FB_CATEGORY_TRIPSTATE, MODE_PRIVATE);

        strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        strLanguage = spCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = spCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        searchCurrencyPrice(strCurrency);

        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        spEditTripState = getSharedPreferences(Utils.SP_MAP_TRIPSTATE, MODE_PRIVATE).edit();
        spTripState = getSharedPreferences(Utils.SP_MAP_TRIPSTATE, MODE_PRIVATE);

        strTripstate = spTripState.getString(Utils.SP_MAP_TRIPSTATE, null);
        if (strTripstate == null) {
            strTripstate = "Normalstate";
        }
        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
        spEditTripState.apply();

        getCurrencyList = loadSharedPreferencesLogList(MainActivity.this);


    }

    private void getCategoriesFirebase(String strMainServiceFB, String strSubServiceFB, String strPickupLatFB, String strPickupLngFB, String strPaymodeFB, String strStripeToken, String strDropLocationFB, String strDropLatFB, String strDropLngFB, String strdistanceFB) {

        Log.e("strMainServiceFB~~~", strMainServiceFB);
        Log.e("strSubServiceFB~~~", strSubServiceFB);

        String[] separated = strMainServiceFB.split(":");
        String MainCategoryid = separated[0];
        String MainCategoryname = separated[1];

        String[] subseparated = strSubServiceFB.split(":");
        String SubCategoryid = subseparated[0];
        String SubCategoryname = subseparated[1];

        Log.e("MainCategoryid~~~", MainCategoryid);
        Log.e("SubCategoryid~~~", SubCategoryid);

        putStateFBCategory.putString(Utils.SP_FB_REQUEST_PAYMENT_MODE, strPaymodeFB);
        putStateFBCategory.putString(Utils.SP_FB_REQUEST_STRIPE_TOKEN, strStripeToken);
        putStateFBCategory.putString(Utils.SP_FB_CATEGORY_ID_TRIPSTATE, MainCategoryid);
        putStateFBCategory.putString(Utils.SP_FB_CATEGORY_NAME_TRIPSTATE, MainCategoryname);
        putStateFBCategory.putString(Utils.SP_FB_CATEGORY_SUB_ID_TRIPSTATE, SubCategoryid);
        putStateFBCategory.putString(Utils.SP_FB_CATEGORY_SUB_NAME_TRIPSTATE, SubCategoryname);

        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, strPickupLatFB);
        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, strPickupLngFB);

        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_DROP_LAT_TRIPSTATE, strDropLatFB);
        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_DROP_LNG_TRIPSTATE, strDropLngFB);

        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_DROP_LOCATION, strDropLocationFB);
        putStateFBCategory.putBoolean(Utils.SP_FB_ACCEPT_DROP_LOCATION_ADDED, false);

        putStateFBCategory.apply();

    }

    /*-*************************************************
     *
     * Location Permission
     *
     * *************************************************/

    /* Check Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else {
                //  showSettingDialog();
                locationEnableDialogMessage();
            }
        } else {
            //   showSettingDialog();
            locationEnableDialogMessage();
        }

    }

    /* Show Popup to access User Permission  */
    private void requestLocationPermission() {
        viewlocationRequest.setVisibility(View.VISIBLE);

        requestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewlocationRequest.setVisibility(View.GONE);
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
                }
            }
        });

    }

    private void callCheckBackground() {
        int backgroundLocation = 0;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (backgroundLocation != PackageManager.PERMISSION_GRANTED) {
                System.out.println("if callCheckBackground checkGpsPermissions");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..
                out.println("~~~~>>>>>> We do not have the permission");
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (com.instaguard.provider.BuildConfig.DEBUG) {
                    Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
                }

                AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                out.println("~~~~AcceptPickupLat~~~~" + AcceptPickupLat);
                out.println("~~~~AcceptPickupLng~~~~" + AcceptPickupLng);
                if (AcceptPickupLat != null) {
                    AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
                    handleMapLongClick(AcceptPickupLatLng);
                }
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is necessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), R.string.Permission_granted, Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.Permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showGpsDailog() {
        String title = getString(R.string.mainactivity_gps_enable_dialog_title);
        String message = getString(R.string.mainactivity_gps_enable_dialog_message);
        String negativeButton = getString(R.string.mainactivity_gps_enable_dialog_negativebutton);
        String positiveButton = getString(R.string.mainactivity_gps_enable_dialog_positivebutton);

        myDialog = DialogUtils.createDialogMessage(this, title, message, negativeButton, positiveButton, false, new DialogUtils.DialogListener() {
            @Override
            public void onPositiveButton() {
                myDialog.dismiss();
                        /*finish();
                        System.exit(0);*/
            }

            @Override
            public void onNegativeButton() {
                myDialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        myDialog.setCancelable(false);
    }

    protected void locationEnableDialogMessage() {

        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
    }

    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // Make an action or refresh an already managed state.
                // Log.i(TAG, "onReceive: mGpsSwitchStateReceiver");
                if (isGpsEnable()) {
                    // Toast.makeText(context, "GPS is Enable!", Toast.LENGTH_LONG).show();
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    callLocationListener();
                    // Toast.makeText(context, "GPS is Enable!", Toast.LENGTH_LONG).show();

                } else {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    if (myDialog != null && !myDialog.isShowing()) {
                        myDialog.show();
                        emptyCurrentLatLng();
                        updateDriverLocationFirebase(0.0, 0.0, null);
                        updateDriverAvailableStatus("0");
                    }
                    //  Toast.makeText(context, "GPS is disable!", Toast.LENGTH_LONG).show();
                }

            }
        }
    };

    public Boolean isGpsEnable() {
        final LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public void gpsState() {
        if (isGpsEnable()) {
            // Toast.makeText(getApplicationContext(), "GPS is Enable!", Toast.LENGTH_LONG).show();
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
        } else {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            if (myDialog != null && !myDialog.isShowing()) {
                myDialog.show();
                emptyCurrentLatLng();
                updateDriverLocationFirebase(0.0, 0.0, null);
                updateDriverAvailableStatus("0");
            }
            // Toast.makeText(getApplicationContext(), "GPS is disable!", Toast.LENGTH_LONG).show();
        }
    }

    public void callLocationListener() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MissingPermission~~~", "MissingPermission");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return;
        } else {
            // Permission has already been granted
            Log.e("MissingPermission~~~", "Success");
            LocationManager locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, Utils.SET_INTERVAL, Utils.SET_MIN_DISTANCE, locationListener);
        }
    }

    /*Geocoder and Fusedlocation End*/
    public void moveCamera(Double lat, Double lng) {
        LatLng coordinates = new LatLng(lat, lng);
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(coordinates, Utils.MAP_DEFAULT_ZOOM);
        mMap.moveCamera(current);
    }

    public void currentLocationClick(View view) {
        Log.i(TAG, "currentLocationClick: " + locFusedLocation);
        if (locFusedLocation != null) {
            Log.i(TAG, "currentLocationClick: " + locFusedLocation.getLatitude() + " " + locFusedLocation.getLongitude());
            LatLng latLng = new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)                              // Sets the center of the map to current location
                    .zoom(Utils.MAP_DEFAULT_ZOOM).tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Toast.makeText(this, getString(R.string.mainactivity_getting_location), Toast.LENGTH_SHORT).show();
        }

    }

    public void geofireIntailize() {
        /*Geo fire driver location state checking updation */
        if (driverId != null && !driverId.isEmpty() && !driverId.trim().matches("")) {
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location"));
        }
    }

    FusedLocationProviderClient mFusedLocationClient;
    boolean aboolean = true;

    public void fusedLocationCalled() {
        // Log.e(TAG, " ~~~~~~~~~fusedLocationCalled~~~~~~~~");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // Log.e(TAG, " ~~~~~~~~~fusedLocationCalled~~~~~Missing~~~");

            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
            if (location != null) {
                try {

                    // Log.e(TAG, "onReceive: ~~~~~~~~~mLocationUpdated~~~~~~~~");
                    // Log.i(TAG, "onReceive: Lat:" + location.getLatitude() + "  Lon:  " + location.getLongitude() + "`````" + location.getBearing());

                    locFusedLocation = location;
                    currentLocation = location;

                    // Log.i(TAG, "onReceive: locFusedLocation:" + locFusedLocation.getLatitude() + "  locFusedLocation:  " + locFusedLocation.getLongitude() + "``locFusedLocation```" + locFusedLocation.getBearing());
                    // Log.i(TAG, "onReceive: locFusedLocation:" + currentLocation.getLatitude() + "  locFusedLocation:  " + currentLocation.getLongitude() + "``locFusedLocation```" + locFusedLocation.getBearing());

                    startIntentService(locFusedLocation, "normal");

                    driverCarMarker(location);

                    if (beginMoveCamera) {
                        moveCamera(location.getLatitude(), location.getLongitude());
                        beginMoveCamera = false;
                    }

                    if (booleanSwitchOnlineOffline) {
                        //Driver Tracking location Firebase
                        //   Log.d(TAG, "CallUpdateDriverOnlineOfflineStatus-----reqStatus-------" + reqStatus);
                        switch (reqStatus) {
                            case "0":
                            case "7":
                            case "8":
                            case "9":
                            case "10":
                                if (aboolean) {
                                    aboolean = false; //CallUpdateDriverOnlineOfflineStatus("1", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), locFusedLocation);
                                    updateDriverAvailableStatus("1");
                                }
                                break;
                            case "1":
                            case "2":
                            case "3":
                            case "4":
                            case "5":
                            case "5.1":
                            case "6":
                                updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), locFusedLocation);
                                break;
                            default:
                                if (aboolean) {
                                    aboolean = false;
                                    CallUpdateDriverOnlineOfflineStatus("1", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), locFusedLocation);
                                    updateDriverAvailableStatus("1");
                                }
                                break;
                        }
                    } else {
                        //  Log.e(TAG, "onReceive: ~~~~~ booleanSwitchOnlineOffline = false");
                        switch (reqStatus) {
                            case "0":
                            case "1":
                            case "7":
                            case "8":
                            case "9":
                            case "10":
                                break;
                            case "2":
                            case "3":
                            case "4":
                            case "5":
                            case "5.1":
                            case "6":
                                updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), locFusedLocation);
                                break;
                            default:
                                if (aboolean) {
                                    aboolean = false;
                                    CallUpdateDriverOnlineOfflineStatus("1", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), locFusedLocation);
                                    updateDriverAvailableStatus("1");
                                }
                                break;
                        }
                    }

                } catch (Exception e) {
                    //   Log.e(TAG, "onReceive: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+e.toString()); Check this error later/
                }
            } else {
                // Log.e(TAG, " ~~~~~~~~~fusedLocationCalled~~~~~Null~~~");
            }
        });
    }

    public void callClickView_Ontheway(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);

        SpannableString spannableStrings = new SpannableString(getString(R.string.map_dialog_start_driver_location));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringsss = new SpannableString(getString(R.string.cancel));
        spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // builder.setTitle(spannableString);

        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(spannableStrings);

        builder.setPositiveButton(spannableStringss, (dialog, which) -> callRequestServicesStatus("3"));
        builder.setNegativeButton(spannableStringsss, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public void callClickView_Arrive(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);

        SpannableString spannableStrings = new SpannableString(getString(R.string.map_dialog_driver_arrived));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringsss = new SpannableString(getString(R.string.cancel));
        spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(spannableStrings);

        builder.setPositiveButton(spannableStringss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callRequestServicesStatus("4");
            }
        });
        builder.setNegativeButton(spannableStringsss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    public void callClickView_Begin(View view) {

        if (strDropLocationFB.equals("NoDrop")) {
            strDistanceBegin = "totaldistancend";
            MainActivity.p = 1;
            distance = 0;
            spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
            spEditTripState.commit();
        } else {
            distance = 0;
            strDistanceBegin = "distancebegin";
            MainActivity.p = 0;
            spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 0);
            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
            spEditTripState.commit();
        }

        callRequestServicesStatus("5");

        AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
        AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);

        out.println("~~~~AcceptPickupLat~~~~" + AcceptPickupLat);
        out.println("~~~~AcceptPickupLng~~~~" + AcceptPickupLng);

        if (AcceptPickupLat != null) {
            AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
            if (Build.VERSION.SDK_INT >= 29) {
                //We need background permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    handleMapLongClick(AcceptPickupLatLng);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        //We show a dialog and ask for permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    }
                }
            } else {
                handleMapLongClick(AcceptPickupLatLng);
            }
        }

    }

    public void callClickView_End(View view) {

        if (strBackupVolunteerStatusFB.equals("0")) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                viewlocationRequest.setVisibility(View.VISIBLE);
                requestAccept.setOnClickListener(v -> {
                    viewlocationRequest.setVisibility(View.GONE);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                });
            } else {
                if (currentLocation != null) {
                    // AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                    // AccptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                    //  String pickup = AcceptPickupLat + "," + AcceptPickupLng;
                    // String drop = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                    aBoolean = true;
                    //callGoogleDistanceQueryAPI("endTrip");
                    Log.i(TAG, "onClick: btnEndjobtrip");
                    strDistanceBegin = "totaldistancend";
                    MainActivity.p = 1;
                    //countDownTimerDistanceDuration.cancel();
                    //countDownTimerDistanceDurationAccept.cancel();
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Endtrip");
                    spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                    spEditTripState.apply();

                    putStateFBCategory.putString(Utils.SP_NAVIGATION, null);
                    putStateFBCategory.apply();

                    out.println("paytment type is " + strPaymentTypeFB);

                    switch (strPaymentTypeFB) {
                        case "stripe":
                        case "card":
                        case "stripe_invoice":
                            callEndtrip("stripe");
                            break;
                        case "paypal":
                            callEndtrip("paypal");
                            break;
                        case "stripe_gpay":
                            callEndtrip("stripe_gpay");
                            break;
                        case "wallet":
                            callEndtrip("wallet");
                            break;
                        default:
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.check_payment_type), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    locationEnableDialogMessage();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.please_wait_requested_guard_will_end_the_job), Toast.LENGTH_SHORT).show();
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                CLocation myLocation = new CLocation(location, true);
                updateSpeed(myLocation);
                locFusedLocation = location;
                currentLocation = location;
                update_UI_location(location);
                driverCarMarker(location);
            } else {
                update_UI_location(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    /*-****************************************************
     *
     * API
     *
     * ****************************************************/
    Integer getGuardSubscriptionPage = 0;

    private void callAppSettings() {

        /*if(appSettingArray!=null)
            appSettingArray.clear();*/

        Call<AppSettingWebModel> call2 = apiInterface.doGetAppSettings();
        call2.enqueue(new Callback<AppSettingWebModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<AppSettingWebModel> call, @NonNull Response<AppSettingWebModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    String STRIPE_PUB_KEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentPublisherKey();
                    String STRIPE_SECRET_KEY = Objects.requireNonNull(response.body()).getAppSetting().getPaymentSecretKey();
                    String requestTime = String.valueOf(Objects.requireNonNull(response.body()).getAppSetting().getTimedelayAccept());
                    getGuardSubscriptionPage = Objects.requireNonNull(response.body()).getAppSetting().getGuardSubscription();

                    if (BuildConfig.DEBUG) {
                        //Log.e(TAG, "onResponse: StripeKeys Admin" + STRIPE_PUB_KEY + "~~" + STRIPE_SECRET_KEY);
                        Log.e(TAG, "onResponse: Distance Limit" + response.body().getAppSetting().getDistanceLimit());
                    }

                    String strDistanceUnit = response.body().getAppSetting().getDistanceUnit(); //miles

                    putCommonState.putInt(Utils.SP_COMMON_SUBSCRIPTION_PAGE, getGuardSubscriptionPage);
                    putCommonState.putString(Utils.SP_COMMON_DISTANCE_UNIT, strDistanceUnit);
                    putCommonState.putString(Utils.SP_COMMON_REQUEST_TIME_DELAY_ACCEPT, requestTime);

                    putCommonState.putString(Utils.SP_COMMON_STRIPE_PUBLISH_KEY, STRIPE_PUB_KEY);
                    putCommonState.putString(Utils.SP_COMMON_STRIPE_SECRET_KEY, STRIPE_SECRET_KEY);
                    putCommonState.apply();

                    minWalletAmount = String.valueOf(Objects.requireNonNull(response.body()).getAppSetting().getWalletMinimum());

                    List<AppSettingSmsTemplate> smsTemplates = Objects.requireNonNull(response.body()).getSmsTemplates();
                    for (int i = 0; i < smsTemplates.size(); i++) {
                        smsTemplateSubject = smsTemplates.get(i).getSmsSubject();
                        smsTemplateContent = smsTemplates.get(i).getSmsContent();
                        if (BuildConfig.DEBUG) {
                            Log.e("getSmsSubject()~~", smsTemplates.get(i).getSmsSubject());
                            Log.e("getSmsContent()~~", smsTemplates.get(i).getSmsContent());
                        }
                    }

                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "onResponse: walletamount-->" + minWalletAmount);
                    }

                    List<AppSettingLanguageModel> languageTemplates = Objects.requireNonNull(response.body()).getLanguage();
                    if (languageTemplates.size() > 0) {
                        ListModel = languageTemplates;
                        rcvLanguage.setVisibility(View.VISIBLE);
                        llStaticLanguage.setVisibility(View.GONE);
                        adapterLanguage = new LanguageAdapter(MainActivity.this, languageTemplates);
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                        rcvLanguage.setLayoutManager(horizontalLayoutManager);
                        rcvLanguage.setAdapter(adapterLanguage);
                        adapterLanguage.notifyDataSetChanged();
                    } else {
                        rcvLanguage.setVisibility(View.GONE);
                        llStaticLanguage.setVisibility(View.VISIBLE);
                    }

                    List<AppSettingCurrencyModel> currencyTemplates = Objects.requireNonNull(response.body()).getCurrency();
                    if (currencyTemplates.size() > 0) {
                        ListModelCurrency = currencyTemplates;
                        rcvCurrency.setVisibility(View.VISIBLE);
                        llStaticCurrency.setVisibility(View.GONE);
                        adapterCurrency = new CurrencyAdapter(MainActivity.this, currencyTemplates);
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                        rcvCurrency.setLayoutManager(horizontalLayoutManager);
                        rcvCurrency.setAdapter(adapterCurrency);
                        adapterCurrency.notifyDataSetChanged();
                    } else {
                        rcvCurrency.setVisibility(View.GONE);
                        llStaticCurrency.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppSettingWebModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });

        callViewProfileDetails();

    }

    public static String payoutBalance, earnings, earnCash, earnCard, adminPaid, siteCommision;

    private void callViewProfileDetails() {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", userAccessToken);
        Log.i("CallViewProfileDetails", "~~~~" + map);
        Call<ProfileModel> call2 = apiInterface.doGetprofileDetails(map);
        call2.enqueue(new Callback<ProfileModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Profile listed successfully.")) {

                        try {

                            earnings = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnings());

                            payoutBalance = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getWallet());
                            earnCash = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnCash());
                            earnCard = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getEarnCard());
                            adminPaid = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getAdminPaid());
                            siteCommision = String.valueOf(Objects.requireNonNull(response.body()).getProfileData().getDriverPaid());


                            String rating = String.valueOf(Objects.requireNonNull(response.body()).getRating());
                            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("rating").setValue(rating);

                            Integer apiStatus = response.body().getProfileData().getStatus();
                            String approvedValue = (apiStatus != null && apiStatus == 1) ? "1" : "0";
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("approved").setValue(approvedValue);

                            String getProfPic = Objects.requireNonNull(response.body()).getProfileData().getAvatar();
                            putCommonState.putString(Utils.SP_DRIVER_PRO_PIC, getProfPic);
                            putCommonState.apply();

                            String strDriverfname = response.body().getProfileData().getFirstName();

                            if (strDriverfname != null) {
                                String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
                                txtDrivernameOnline.setText(upperString);
                            } else {
                                txtDrivernameOnline.setText(getResources().getString(R.string.app_name));
                            }

                            if (strDriverfname != null) {
                                String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
                                navigationName.setText(upperString);
                            } else {
                                navigationName.setText(getResources().getString(R.string.app_name));
                            }

                            String strDriverProPic = Objects.requireNonNull(response.body()).getProfileData().getAvatar();
                            strDriverProPic = strDriverProPic.replaceAll(" ", "%20");

                            if (strDriverProPic != null) {
                                navigationProfileImage.setImageURI(Utils.BASE_URL + strDriverProPic);
                                ivProfpicOnline.setImageURI(Utils.BASE_URL + strDriverProPic);

                                int color = getResources().getColor(R.color.colorPrimary);
                                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                                roundingParams.setBorder(color, 1.0f);
                                roundingParams.setRoundAsCircle(true);
                                navigationProfileImage.getHierarchy().setRoundingParams(roundingParams);
                                ivProfpicOnline.getHierarchy().setRoundingParams(roundingParams);

                            }

                            if (response.body().getProfileData().getWalletBalance() != null) {
                                Log.i(TAG, "onResponse: profile details-->" + response.body().getProfileData().getCurrency());
                                if (!String.valueOf(response.body().getProfileData().getWalletBalance()).equals("null")) {
                                    if (!String.valueOf(response.body().getProfileData().getWalletBalance()).equals("")) {
                                        double roundedNumber = DecimalUtils.round(Double.parseDouble(String.valueOf(response.body().getProfileData().getWalletBalance())), 2);
                                        putCommonState.putString(Utils.SP_DRIVER_WALLET_AMT, String.valueOf(roundedNumber));
                                        putCommonState.apply();
                                        hideProgressDialog();
                                        refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue(roundedNumber);
                                        System.out.println("wallet blc main activity " + roundedNumber);

                                    } else
                                        refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue("0");
                                } else
                                    refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue("0");
                            } else
                                refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("wallet").setValue("0");

                            double roundedNumbergetBonusEarning = 0.0;
                            Object getBonusEarning = String.valueOf(Objects.requireNonNull(response.body()).getBonusEarning());
                            if (!String.valueOf(getBonusEarning).equals("null") && !String.valueOf(getBonusEarning).equals("") && !String.valueOf(getBonusEarning).equals(" ")) {
                                roundedNumbergetBonusEarning = DecimalUtils.round(Double.parseDouble(String.valueOf(getBonusEarning)), 2);
                                putCommonState.putString(Utils.SP_DRIVER_BONUS_AMT, String.valueOf(roundedNumbergetBonusEarning));
                                putCommonState.apply();
                                hideProgressDialog();
                            } else {
                                putCommonState.putString(Utils.SP_DRIVER_BONUS_AMT, String.valueOf(roundedNumbergetBonusEarning));
                                putCommonState.apply();
                            }


                            hideProgressDialog();

                            boolean aBoolean;
                            if (response.body().getProfileData().getCurrency() != null) {
                                Log.i(TAG, "onResponse: profile details-->" + response.body().getProfileData().getCurrency());
                                if (!response.body().getProfileData().getCurrency().isEmpty()) {
                                    searchCurrencyPrice(response.body().getProfileData().getCurrency());
                                    aBoolean = false;
                                } else {
                                    aBoolean = true;
                                }
                            } else {
                                Log.i(TAG, "onResponse: profile details else-->");
                                aBoolean = true;
                            }

                            if (response.body().getProfileData().getLanguage() != null && !response.body().getProfileData().getLanguage().equals("null") && !response.body().getProfileData().getLanguage().equals("")) {
                                txtUpdateLanguageSetupOnline.setVisibility(View.GONE);
                            } else {
                                //txtUpdateLanguageSetupOnline.setVisibility(View.VISIBLE);
                                txtUpdateLanguageSetupOnline.setVisibility(View.GONE);
                                CallUpdateDetails("1", "English", "en", "view");
                            }

                            if (aBoolean) {
                                // txtUpdateCurrencySetupOnline.setVisibility(View.VISIBLE);
                                txtUpdateCurrencySetupOnline.setVisibility(View.GONE);
                                if (ListModelCurrency != null && ListModelCurrency.size() > 0) {
                                    String curShortName = String.valueOf(ListModelCurrency.get(0).getShortName());
                                    String curId = String.valueOf(ListModelCurrency.get(0).getId());
                                    callCurrencyUpdate(curId, curShortName, "view");
                                } else {
                                    callCurrencyUpdate("1", "GBP", "view");
                                }
                            } else {
                                txtUpdateCurrencySetupOnline.setVisibility(View.GONE);
                            }


                            String strSubscriptionId = String.valueOf(Objects.requireNonNull(response.body()).
                                    getProfileData().getSubscriptionId());
                            System.out.println("~~~~strSubscriptionId~~~~" + strSubscriptionId);

                            String strSubscriptionStatus = String.valueOf(Objects.requireNonNull(response.body()).
                                    getProfileData().getSubscriptionStatus());
                            System.out.println("~~~~strSubscriptionStatus~~~~" + strSubscriptionStatus);

                            String strSubscriptionName = String.valueOf(Objects.requireNonNull(response.body()).
                                    getProfileData().getSubscriptionName());

                            //refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("remaining_job").setValue("0");

                            putCommonState.putString(Utils.SP_USER_SUBSCRIPTION_CODE, strSubscriptionName);
                            putCommonState.putString(Utils.SP_USER_SUBSCRIPTION_CODE_ID, strSubscriptionId);
                            putCommonState.apply();

                            MenuItem nav_subscription_plans = navigationView.getMenu().findItem(R.id.nav_subscription_plans);
                            out.println("~~~~~SUBSCRIPTION_PAGE~~~>> " + getspCommonState.getInt(Utils.SP_COMMON_SUBSCRIPTION_PAGE, 0));

                            if (getGuardSubscriptionPage == 1) {
                                extracted(nav_subscription_plans, strSubscriptionStatus);
                            } else {
                                if (getspCommonState.getInt(Utils.SP_COMMON_SUBSCRIPTION_PAGE, 0) == 1) {
                                    extracted(nav_subscription_plans, strSubscriptionStatus);
                                } else {
                                    nav_subscription_plans.setVisible(false);
                                    navigationSubsPlan.setVisibility(View.GONE);
                                    txtSubscriptionPlanSetupLabel.setVisibility(View.GONE);
                                }
                            }

                            hideProgressDialog();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        //callRefreshToken("viewprofile");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("viewprofile");
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_500) {
                    ////callRefreshToken("viewprofile");
                    hideProgressDialog();
                }
            }

            private void extracted(MenuItem nav_subscription_plans, String strSubscriptionStatus) {
                nav_subscription_plans.setVisible(true);
                out.println("~~~~~subscription_status~~~" + strSubscriptionStatus);
                if (!strSubscriptionStatus.equals("null") && !strSubscriptionStatus.equals("")) {
                    switch (strSubscriptionStatus) {
                        case "0": //Default
                        case "0.0": //Default

                            nav_subscription_plans.setTitle(getResources().getString(R.string.nav_mymembership_not));

                            // txtSubscriptionPlanSetupLabel.setVisibility(View.VISIBLE);
//                            txtSubscriptionPlanSetupLabel.setVisibility(View.INVISIBLE);
//                            txtSubscriptionPlanSetupLabel.setText(getResources().getString(R.string.your_subs_plan_choose));
//                            txtSubscriptionPlanSetupLabel.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
//                            txtSubscriptionPlanSetupLabel.setTextColor(getResources().getColor(R.color.colorPrimary));

                            /*navigationSubsPlan.setVisibility(View.GONE);
                            navigationSubsPlan.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_very_light)));
                            navigationSubsPlan.setText(getResources().getString(R.string.plan_choose));*/

                            break;
                        case "1": //Active
                        case "1.0": //Active

                            nav_subscription_plans.setTitle(getResources().getString(R.string.nav_mymembership));

                            //txtSubscriptionPlanSetupLabel.setVisibility(View.GONE);
                            txtSubscriptionPlanSetupLabel.setVisibility(View.INVISIBLE);
                            txtSubscriptionPlanSetupLabel.setText(getResources().getString(R.string.your_subs_plan_active));
                            txtSubscriptionPlanSetupLabel.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_green_dark)));
                            txtSubscriptionPlanSetupLabel.setTextColor(getResources().getColor(R.color.white));

                            navigationSubsPlan.setVisibility(View.VISIBLE);
                            navigationSubsPlan.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_green_dark)));
                            navigationSubsPlan.setText(getResources().getString(R.string.plan_active));

                            break;
                        case "2": //Expired
                        case "2.0": //Expired

                            nav_subscription_plans.setTitle(getResources().getString(R.string.nav_mymembership_not));

                            txtSubscriptionPlanSetupLabel.setVisibility(View.VISIBLE);
                            txtSubscriptionPlanSetupLabel.setText(getResources().getString(R.string.your_subs_plan_expiry));
                            txtSubscriptionPlanSetupLabel.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_red_dark)));
                            txtSubscriptionPlanSetupLabel.setTextColor(getResources().getColor(R.color.white));

                            navigationSubsPlan.setVisibility(View.VISIBLE);
                            navigationSubsPlan.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.holo_red_dark)));
                            navigationSubsPlan.setText(getResources().getString(R.string.plan_expired));

                            break;
                    }
                } else {
                    nav_subscription_plans.setTitle(getResources().getString(R.string.nav_mymembership_not));
                    navigationSubsPlan.setVisibility(View.GONE);
                    txtSubscriptionPlanSetupLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: !~~~~~~!!" + t.toString());
                call.cancel();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void callViewEmergencyDetails() {

        if (mobileNumbers != null) {
            mobileNumbers.clear();
        }
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        Log.i("callViewEmergencyDetails", "~~~~" + map);
        Call<EmergencyViewModel> call2 = apiInterface.doGetViewEmergencyDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(this);
        call2.enqueue(new Callback<EmergencyViewModel>() {
            @SuppressLint({"SetTextI18n", "LongLogTag"})
            @Override
            public void onResponse(@NonNull Call<EmergencyViewModel> call, @NonNull Response<EmergencyViewModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("driver emergency contact listed successfully.")) {
                        hideProgressDialog();

                        List<EmergencyViewDataModel> getve = Objects.requireNonNull(response.body()).getEmergencyViewDataModel();
                        Log.e(TAG, "onResponse: " + getve.size());

                        /* String[] months = {"+91 88701 46888", "+919789354285", "+91 97860 52373"};
                            ArrayList<String>  monthNames = new ArrayList<String>(Arrays.asList(months));*/

                        for (int i = 0; i < getve.size(); i++) {
                            mobileNumbers.add(getve.get(i).getPhoneNumber().replaceAll(" ", ""));
                        }

                        String locmessage;
                        if (locFusedLocation == null) {
                            locmessage = " HELP!";
                        } else {
                            locmessage = "https://maps.google.com/maps?q=loc:" + locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude();
                        }

                        String message = smsTemplateContent + " " + locmessage;

                        callPanicAddAdmin(message);

                        Log.i(TAG, "callViewEmergencyDetails: ~~~~~~~~1~~~~" + mobileNumbers);
                        Log.i(TAG, "callViewEmergencyDetails: ~~~~~~~~2~~~~" + String.valueOf(mobileNumbers).replaceAll("\\[", "").replaceAll("\\]", ""));

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + String.valueOf(mobileNumbers).replaceAll("\\[", "").replaceAll("\\]", "")));
                        smsIntent.putExtra("sms_body", message);
                        startActivity(smsIntent);

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("No records Found.")) {
                        Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_EMERGENCY_CONTACTS, MainActivity.this, null, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                        hideProgressDialog();

                    }

                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {

                    //callRefreshToken("viewemergency");
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmergencyViewModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    private void getPendingJobsCall() {
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", driverId);
        map.put("mode", "driver");
        map.put("language", strLanguageShortName);
        Call<PendingJobsModel> call2 = apiInterface.doCustomerPendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<PendingJobsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<PendingJobsModel> call, @NonNull retrofit2.Response<PendingJobsModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Fetching pending jobs successful.")) {
                        List<PendingJobsData> getve = Objects.requireNonNull(response.body()).getServiceRequest();
                        Log.i(TAG, "onResponse: getPendingJobsCall-->" + getve.size());
                        if (getve.size() > 0) {
                            txtPendingCount.setText(Integer.toString(getve.size()));
                            // callPendingUpcomingStatus(driverId,"pending",String.valueOf(Integer.toString(getve.size())));
                        } else {
                            txtPendingCount.setText("0");
                            callPendingUpcomingStatus(driverId, "pending", "0");
                        }
                    } else {
                        //callRefreshToken("pending");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("pending");
                }
            }

            @Override
            public void onFailure(Call<PendingJobsModel> call, Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
            }
        });

    }

    private void getUpcomingJobsCall() {
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", driverId);
        map.put("mode", "driver");
        map.put("language", strLanguageShortName);
        Call<UpcomingJobsModel> call2 = apiInterface.doCustomerUpcomingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<UpcomingJobsModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<UpcomingJobsModel> call, @NonNull retrofit2.Response<UpcomingJobsModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Fetching upcoming jobs successful.")) {
                        List<UpcomingJobsData> getve = Objects.requireNonNull(response.body()).getServiceRequest();
                        Log.i(TAG, "onResponse: getPastJobsCall-->" + getve);
                        for (int i = 0; i < getve.size(); i++) {
                        }
                        if (getve.size() > 0) {
                            txtUpcomingCount.setText(Integer.toString(getve.size()));
                            // callPendingUpcomingStatus(driverId,"upcoming",String.valueOf(Integer.toString(getve.size())));

                        } else {
                            txtUpcomingCount.setText("0");
                            callPendingUpcomingStatus(driverId, "upcoming", "0");
                        }
                    } else {
                        //callRefreshToken("upcoming");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("upcoming");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpcomingJobsModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
            }
        });

    }

    private void callViewAvailableDetails(String date) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        map.put("date", date);

        Call<NewDateAvailListModel> call2 = apiInterface.doGetNewAvailable(map);

        call2.enqueue(new Callback<NewDateAvailListModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<NewDateAvailListModel> call, @NonNull Response<NewDateAvailListModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        if (response.body().getResult().getSlot() != null) {
                            List<String> sunday = response.body().getResult().getSlot();
                            if (sunday != null) {
                                if (sunday.size() > 0) {
                                    txtUpdateAvailabilitySetupOnline.setVisibility(View.GONE);
                                } else {
                                    txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                                }
                            } else {
                                txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                            }
                        } else {
                            txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                        }
                    } else {
                        txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtUpdateAvailabilitySetupOnline.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewDateAvailListModel> call, @NonNull Throwable t) {
                out.println("onFailure: AvailabilityModel~~~~~>Sub" + t.toString());
                call.cancel();
                txtUpdateAvailabilitySetupOnline.setVisibility(View.GONE);
            }
        });
    }

    private void callPanicAddAdmin(String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        map.put("mode", "driver");
        map.put("location_link", message);
        // showProgressDialog(MainActivity.this);
        Log.i("callPanicAddAdmin", "callPanicAddAdmin~~~" + map);
        Call<PanicAddAdminModel> call2 = apiInterface.doUpdateAddPanic(map);
        call2.enqueue(new Callback<PanicAddAdminModel>() {
            @Override
            public void onResponse(@NonNull Call<PanicAddAdminModel> call, @NonNull Response<PanicAddAdminModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Panic Call details updated successfully.")) {
                        hideProgressDialog();
                        String panicAddDate = Objects.requireNonNull(response.body()).getPanicAddAdminDataModel().getPanicDate();
                        String panicAddId = String.valueOf(Objects.requireNonNull(response.body()).getPanicAddAdminDataModel().getId());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PanicAddAdminModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void CallUpdateDriverOnlineOfflineStatus(String status, String lat, String lng) {
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("current_lat", lat);
        map.put("current_lon", lng);
        map.put("status", status);
        map.put("language", strLanguageShortName);
        // map.put("token", userAccessToken);
        Log.i("CallUpdateDetails", "~~~~" + map);
        Call<DriverLocationUpdateModel> call2 = apiInterface.doDriverUpdateLocation("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
         showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<DriverLocationUpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<DriverLocationUpdateModel> call, @NonNull Response<DriverLocationUpdateModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (response.body() != null) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(driverId);
                        Map<String, Object> taskMap = new HashMap<String, Object>();

                        if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("driver check-in successful.")) {
                            switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
                            booleanSwitchOnlineOffline = true;
                            txtOnlineOffline.setText(getString(R.string.mainactivity_online_txt));
                            doSwitchBtnCall = false;
                            updateDriverLocationFirebase(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation);
                            updateDriverAvailableStatus("1");

                            taskMap.put("status", "1");
                            databaseReference.updateChildren(taskMap);
                        } else if (response.body().getMessage().equals("Select city before checkIn")) {
                            checkInDenied(response.body().getMessage());
                        } else if (response.body().getMessage().equals("Please upload an expiry date for the document")) {
                            checkInDenied(response.body().getMessage());
                        } else if (response.body().getMessage().equals("driver check-out successful.")) {
                            doSwitchBtnCall = false;
                            emptyCurrentLatLng();
                            switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                            booleanSwitchOnlineOffline = false;
                            switchOnlineOffline.setChecked(false);
                            txtOnlineOffline.setText(getString(R.string.mainactivity_offline_txt));
                            updateDriverLocationFirebase(0.0, 0.0, null);
                            updateDriverAvailableStatus("0");

                            databaseReference.child("status").setValue("0");
                        } else {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            doSwitchBtnCall = false;
                            emptyCurrentLatLng();
                            switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                            booleanSwitchOnlineOffline = false;
                            switchOnlineOffline.setChecked(false);
                            txtOnlineOffline.setText(getString(R.string.mainactivity_offline_txt));
                            updateDriverLocationFirebase(0.0, 0.0, null);
                            updateDriverAvailableStatus("0");

                            taskMap.put("status", "0");
                            databaseReference.updateChildren(taskMap);
                        }
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    doSwitchBtnCall = false;
                    //callRefreshTokenOnline(status, lat, lng);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverLocationUpdateModel> call, @NonNull Throwable t) {
                // CommonUtil.showRetrofitError(LoginActivity.this, error);
                hideProgressDialog();
                doSwitchBtnCall = false;
                call.cancel();
            }
        });
    }

    private void checkInDenied(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg);
        builder.setTitle("Alert !");
        builder.setCancelable(false);

        if(msg.equals("Please upload an expiry date for the document")) {
            builder.setMessage("Please upload the expiry date for your SIA badge document.");
        } else {
            builder.setMessage("Please provide the specified city.");
        }

        builder.setPositiveButton("Ok", (dialog, which) -> {
            // When the user click yes button then app will close
            doSwitchBtnCall = true;
            switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
            txtOnlineOffline.setText(getString(R.string.mainactivity_offline_txt));
            switchOnlineOffline.setChecked(false);
            dialog.dismiss();
        });

        builder.show();
    }

    @SuppressLint("LongLogTag")
    private void callNotifySend(String userId, String driverId) {

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("customer_id", userId);
        map1.put("driver_id", driverId);
        map1.put("mode", "reject");
        map1.put("language", strLanguageShortName);
        Log.i("callForgotPasswordDetail~~", "~~~~" + map1);
        Call<PushModel> call2 = apiInterface.doNotifySend("", map1);
        call2.enqueue(new Callback<PushModel>() {
            @Override
            public void onResponse(@NonNull Call<PushModel> call, @NonNull Response<PushModel> response) {
                hideProgressDialog();
                Log.i(TAG, "onResponse: resonse code--> " + response.code());
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("notification send successfully")) {
                        // Toast.makeText(getActivity(),Objects.requireNonNull(response.body()).getMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PushModel> call, Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    private void callCurrencyUpdate(String curId, String curShortName, String from) {
        // userAccessToken = spUpdateProfile.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("currency", curShortName);
        map.put("token", "");
        Log.i("CallUpdateDetails", "~~~~" + map);
        Call<ProfileUpdateModel> call2 = apiInterface.doGetprofileUpdateDetails(map);
        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<ProfileUpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateModel> call, @NonNull Response<ProfileUpdateModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver updated successfully.")) {
                        String strCurrencySymbol = Objects.requireNonNull(response.body()).getProfileUpdateData().getCurrencySymbol();
                        String strCurrenc = Objects.requireNonNull(response.body()).getProfileUpdateData().getCurrency();

                        searchCurrencyPrice(strCurrenc.toString());

                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                        refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("currency").setValue(strCurrenc + ":" + strCurrencySymbol);
                        getStateCurrency.putString(Utils.SP_CURRENCY, strCurrenc);
                        getStateCurrency.putString(Utils.SP_CURRENCY_SYMBOL, strCurrencySymbol);
                        getStateCurrency.apply();

                        if (!from.equals("view")) {
                            MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MainActivity.class));
                            MainActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            MainActivity.this.finish();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: editprofile ~~~~~--->>>> " + t.toString());
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    private void CallUpdateDetails(String strlang, String strlangName, String strlangCode, String key) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("language", strlang);
        Log.i("CallUpdateDetails", "~~~~" + map);

        if (!key.equals("view")) {
            showProgressDialog(MainActivity.this);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ProfileUpdateModel> call2 = apiInterface.doGetprofileUpdateDetails(map);
        call2.enqueue(new Callback<ProfileUpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateModel> call, @NonNull Response<ProfileUpdateModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver updated successfully.")) {

                        try {

                            String getChangeLang = Objects.requireNonNull(response.body()).getProfileUpdateData().getLanguage();

                            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child("language").setValue(getChangeLang);

                            if (key.equals("view")) {

                                getStateCurrency.putString(Utils.SP_LANGUAGE, strlangName);
                                getStateCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, "en");
                                getStateCurrency.putString(Utils.SP_LANGUAGE_COUNTRY, "US");
                                getStateCurrency.apply();

                            } else if (key.equals("langDialog")) {

                                hideProgressDialog();

                                if (strlangCode != null) {
                                    if (!strlangCode.equals("null")) {
                                        if (!strlangCode.equals("")) {
                                            callSwitch();
                                        } else {
                                            callSwitch();
                                        }
                                    } else {
                                        callSwitch();
                                    }
                                } else {
                                    callSwitch();
                                }

                                hideProgressDialog();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    hideProgressDialog();
                }
            }

            private void callSwitch() {
                switch (strlangName) {
                    case "Sinhala":
                        onSuccessEditProfile(strlangName, "si", "LK", "normal");
                        break;
                    case "Finnish":
                        onSuccessEditProfile(strlangName, "fi", "FI", "normal");
                        break;
                    case "Arabic":
                    case "عربى":
                    case "عربى-Arabic":
                    case "عربى - Arabic":
                    case "Ar":
                    case "ar":
                        onSuccessEditProfile(strlangName, "ar", "AE", "normal");
                        break;
                    case "English":
                    default:
                        onSuccessEditProfile(strlangName, "en", "US", "normal");
                        break;
                }

            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    Boolean aBooleanSomethingAcceptStatus = true;

    private void callAcceptRequest() {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        strMainCategoryname = getspFBCategory.getString(Utils.SP_FB_CATEGORY_NAME_TRIPSTATE, null);
        strMainCategoryid = getspFBCategory.getString(Utils.SP_FB_CATEGORY_ID_TRIPSTATE, null);
        strSubCategoryname = getspFBCategory.getString(Utils.SP_FB_CATEGORY_SUB_NAME_TRIPSTATE, null);
        strSubCategoryid = getspFBCategory.getString(Utils.SP_FB_CATEGORY_SUB_ID_TRIPSTATE, null);
        strPaymentMode = getspFBCategory.getString(Utils.SP_FB_REQUEST_PAYMENT_MODE, null);
        strSripeTokenFB = getspFBCategory.getString(Utils.SP_FB_REQUEST_STRIPE_TOKEN, null);

        if (strDropLatFB == null) {
            strDropLatFB = "0.0";
        }
        if (strDropLngFB == null) {
            strDropLngFB = "0.0";
        }
        if (strDropLocationFB == null) {
            strDropLocationFB = "NoDrop";
        }

        HashMap<String, Object> map = new HashMap<>();
        Call<AcceptRequestModel> call2;

        if (strBackupVolunteerStatusFB.equals("0")) {

            if (strTransferJobStatusFB.equals("0")) {

                map.put("mode", "accept");
                map.put("customer_id", struserIdFB);
                map.put("driver_id", driverId);
                map.put("customer_location", strPickupLocationFB);
                map.put("service_id", strSubCategoryid);
                map.put("category", strMainCategoryid);
                map.put("customer_lat", strPickupLatFB);
                map.put("customer_lon", strPickupLngFB);

                map.put("drop_lat", strDropLatFB);
                map.put("drop_lon", strDropLngFB);
                map.put("drop_location", strDropLocationFB);
                map.put("payment_type", strPaymentMode);
                map.put("total_agent", strCount);
                map.put("total_days", strDays);

                if (strPaymentMode.equals("wallet")) {

                } else if (strPaymentMode.equals("card")) {
                    map.put("stripe_token", strSripeTokenFB);
                    map.put("card_number", "123");
                }

                map.put("promocode", strPromoCodeFB);
                map.put("special_instruction", strSpecialInstFB);

                map.put("language", strLanguageShortName);

                map.put("customer_booking_id", strCustomerBookingIdFB);

                if (strTripTypeFB.equals("2")) {
                    map.put("booking_id", strBookingId);
                    map.put("trip_type", "2");
                    map.put("start_date_time", strStartDateTimeFB);
                    map.put("end_date_time", strEndDateTimeFB);
                }

                out.println("callAcceptRequest status 0");

                call2 = apiInterface.doAcceptRequestDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

            } else {

                map.put("mode", "accept");
                map.put("booking_id", strBookingId);
                map.put("from_driver_id", strRequestedDriverIdFB);
                map.put("to_driver_id", driverId);
                map.put("transfer_status", "1");

                call2 = apiInterface.doAcceptBackupRequestDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

            }

        } else {

            /*mode:accept
            booking_id
            from_driver_id ( requested driver id )
            to_driver_id ( who accept driver id)*/

            map.put("mode", "accept");
            map.put("booking_id", strBookingId);
            map.put("from_driver_id", strRequestedDriverIdFB);
            map.put("to_driver_id", driverId);

            call2 = apiInterface.doAcceptBackupRequestDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

        }


        Log.e("callAcceptRequest", "~~~~" + map);

        out.println("from driver id is " + strRequestedDriverIdFB);


        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<AcceptRequestModel>() {
            @Override
            public void onResponse(@NonNull Call<AcceptRequestModel> call, @NonNull Response<AcceptRequestModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {
                        stopPlaying();
                        if (countDownTimerNormalReceivedRequest != null) {
                            countDownTimerNormalReceivedRequest.cancel();
                        }
                        if (strBackupVolunteerStatusFB.equals("0")) {
                            if (strTripTypeFB.equals("2")) {
                                setVisibility(lytJobInprogress, false);
                                setVisibility(lytRequestReceive, false);
                                setVisibility(lytOnlineOffline, true);
                                Utils.CURRENT_TAG = null;
                                Bundle args = new Bundle();
                                args.putString("main", "upcoming");
                                Utils.switchContent(R.id.frame_mainactivity, Utils.FRAGMENT_YOURJOBS, MainActivity.this, args, Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                            } else {
                                setVisibility(lytJobInprogress, true);
                                setVisibility(lytRequestReceive, false);
                                setVisibility(lytOnlineOffline, false);
                            }
                        } else {
                            setVisibility(lytJobInprogress, true);
                            setVisibility(lytRequestReceive, false);
                            setVisibility(lytOnlineOffline, false);
                        }

                        callClickAction(btnAcceptRequest, true);

                        strBookingId = String.valueOf(Objects.requireNonNull(response.body()).getAcceptRequestDataModel().getBookingId());
                        out.println("acceptReqBookingId" + strBookingId);

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("something went wrong in accept.")) {

                        callClickAction(btnAcceptRequest, true);

                        out.println("acceptReqBookingId--1" + strBookingId);
                        hideProgressDialog();
                        strBookingId = String.valueOf(Objects.requireNonNull(response.body()).getAcceptRequestDataModel().getBookingId());

                        if (aBooleanSomethingAcceptStatus) {
                            aBooleanSomethingAcceptStatus = false;
                            callAcceptRequest();
                        } else {
                            hideProgressDialog();
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        hideProgressDialog();
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    callClickAction(btnAcceptRequest, true);
                } else {
                    callClickAction(btnAcceptRequest, true);
                    hideProgressDialog();
                    Toast.makeText(MainActivity.this, "500-->Error", Toast.LENGTH_LONG).show();
                    out.println("callAcceptRequest calling");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AcceptRequestModel> call, @NonNull Throwable t) {
                callClickAction(btnAcceptRequest, true);
                call.cancel();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void callDeclineScheduledRequest() {
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookingId);
        map.put("mode", "driver");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<DeclinePendingModel> call2 = apiInterface.doDeclinePendingJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        Log.e(TAG, "callDeclineScheduledRequest: " + map);
        call2.enqueue(new Callback<DeclinePendingModel>() {
            @Override
            public void onResponse(@NonNull Call<DeclinePendingModel> call, @NonNull Response<DeclinePendingModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    Log.i(TAG, "callDeclineScheduledRequest onResponse: " + Objects.requireNonNull(response.body()).getMessage());
                } else {
                    Log.e(TAG, "callDeclineScheduledRequest error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeclinePendingModel> call, @NonNull Throwable t) {
                call.cancel();
                Log.e(TAG, "callDeclineScheduledRequest onFailure: " + t);
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void callRequestServicesStatus(String status) {
        Log.e("callRequestServicesStatus~~", status);
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        Call<RequestStatusModel> call2;

        if (strBackupVolunteerStatusFB.equals("0")) {
            if (status.equalsIgnoreCase("3")) {
                map.put("mode", "start_driver");
            } else if (status.equalsIgnoreCase("4")) {
                map.put("mode", "reached");
            } else if (status.equalsIgnoreCase("5")) {
                map.put("mode", "start_service");
            }

            if (strTransferJobStatusFB.equals("1")) {
                out.println("bookingIdInStatus1-" + strBookingId);
                map.put("booking_id", strBookingId);
                map.put("from_driver_id", strRequestedDriverIdFB);
                map.put("to_driver_id", driverId);
                map.put("transfer_status", "1");
                call2 = apiInterface.doGetBackupRequestStatusDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
            } else {
                out.println("bookingIdInStatus-1-" + strBookingId);
                map.put("driver_id", driverId);
                map.put("booking_id", strBookingId);
                map.put("language", strLanguageShortName);
                call2 = apiInterface.doGetRequestStatusDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
            }
        } else {
            if (status.equalsIgnoreCase("3")) {
                map.put("mode", "start_driver");
            } else if (status.equalsIgnoreCase("4")) {
                map.put("mode", "reached");
            } else if (status.equalsIgnoreCase("5")) {
                map.put("mode", "start_service");
            }

            out.println("bookingIdInStatus3" + strBookingId);
            map.put("booking_id", strBookingId);
            map.put("from_driver_id", strRequestedDriverIdFB);
            map.put("to_driver_id", driverId);
            call2 = apiInterface.doGetBackupRequestStatusDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        }

        Log.e("callAcceptRequest", "~~~~" + map);

        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<RequestStatusModel>() {
            @Override
            public void onResponse(@NonNull Call<RequestStatusModel> call, @NonNull Response<RequestStatusModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {

                        strBookingId = String.valueOf(Objects.requireNonNull(response.body()).getRequestStatusData().getBookingId());

                        if (status.equalsIgnoreCase("3")) {
                            btnStarttrip.setVisibility(View.GONE);
                            btnArrivetrip.setVisibility(View.VISIBLE);

                            /*DatabaseReference requestAcceptrDatabaseBRef;
                            requestAcceptrDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER);
                            requestAcceptrDatabaseBRef.child("request_status").setValue("0");

                            DatabaseReference requestAcceptrDatabaseBRefs;
                            requestAcceptrDatabaseBRefs = FirebaseDatabase.getInstance().getReference().child("booking_trips").child(strBookingId);
                            requestAcceptrDatabaseBRefs.child("request_status").setValue("0");*/

                        } else if (status.equalsIgnoreCase("4")) {
                            btnStarttrip.setVisibility(View.GONE);
                            btnArrivetrip.setVisibility(View.GONE);
                            btnBeginjobtrip.setVisibility(View.VISIBLE);
                        } else if (status.equalsIgnoreCase("5")) {
                            btnStarttrip.setVisibility(View.GONE);
                            btnArrivetrip.setVisibility(View.GONE);
                            btnBeginjobtrip.setVisibility(View.GONE);
                            btnEndjobtrip.setVisibility(View.VISIBLE);
                        } else if (status.equalsIgnoreCase("6")) {
                            lytJobInprogress.setVisibility(View.GONE);
                            lytRequestReceive.setVisibility(View.GONE);
                            lytOnlineOffline.setVisibility(View.VISIBLE);
                        }

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Failure")) {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //callRefreshTokenRequest(status);
                    hideProgressDialog();
                    Toast.makeText(MainActivity.this, "500-->Error", Toast.LENGTH_LONG).show();
                    out.println("callRequestServicesStatus calling");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestStatusModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
                Toast.makeText(MainActivity.this, "onFailure-" + t, Toast.LENGTH_LONG).show();
                out.println("api call failure " + t.getMessage());
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void callEndtrip(String key) {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        strDestAddress = getspFBCategory.getString(Utils.SP_FB_ACCEPT_DROP_LOCATION, null);

        out.println("end trip destination " + strDestAddress);

        String get_Lat = String.valueOf(currentLocation.getLatitude());
        String get_Long = String.valueOf(currentLocation.getLongitude());

        Log.e("LOCATIONCHANGE--Endtrip--->>>", get_Lat + "Longtitude  " + get_Long + "-strDestAddress----->>>>>" + strDestAddress);

        String strMaterialFees = edtMaterialFee.getText().toString().trim();
        String strMiscFees = edtMiscFee.getText().toString().trim();
        String strDiscoDriver = edtDiscountDriver.getText().toString().trim();

        strMaterialFees = strMaterialFees.replaceAll(",", "");
        strMiscFees = strMiscFees.replaceAll(",", "");
        strDiscoDriver = strDiscoDriver.replaceAll(",", "");


        if (strMaterialFees.length() == 0) {
            strMaterialFees = "0.0";
        } else {
            strMaterialFees = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strMaterialFees);
        }

        if (strMiscFees.length() == 0) {
            strMiscFees = "0.0";
        } else {
            strMiscFees = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strMiscFees);
        }

        if (strDiscoDriver.length() == 0) {
            strDiscoDriver = "0.0";
        } else {
            strDiscoDriver = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strDiscoDriver);
        }


        if (strDropLatFB == null) {
            strDropLatFB = "0.0";
        }
        if (strDropLngFB == null) {
            strDropLngFB = "0.0";
        }
        if (strDropLocationFB == null) {
            strDropLocationFB = "NoDrop";
        }

        String strDistanceEndTrip_sp = spTripState.getString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");

        if (strDistanceFB_EndJob == null) {
            strDistanceFB_EndJob = "0";
        }

        String strFinalDistance;
        if (Double.parseDouble(strDistanceEndTrip_sp) > Double.parseDouble(strDistanceFB_EndJob)) {
            strFinalDistance = strDistanceEndTrip_sp;
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "callEndtripDetails: Final Distance SP~~~~~" + strFinalDistance);
            }
        } else {
            strFinalDistance = strDistanceFB_EndJob;
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "callEndtripDetails: Final Distance FB~~~~~" + strFinalDistance);
            }
        }

        String cnvt_strDistanceEndTrip_;

        if (strAmountTypeFB.equals("3")) {
            if (convertDistanceUnit(MainActivity.this).equals("mile")) {
                cnvt_strDistanceEndTrip_ = String.valueOf(convertKmsToMiles(MainActivity.this, Double.valueOf(strFinalDistance)));
            } else {
                cnvt_strDistanceEndTrip_ = strFinalDistance;
            }

        } else {
            cnvt_strDistanceEndTrip_ = "0";
        }

        Log.e(TAG, "callEndtrip:distance~~~~ final~~convert KM Miles~~" + cnvt_strDistanceEndTrip_);

        HashMap<String, Object> map = new HashMap<>();
        map.put("mode", "finish_service");
        map.put("driver_id", driverId);
        map.put("booking_id", strBookingId);
        map.put("customer_id", struserIdFB);

        map.put("material_fee", strMaterialFees);
        map.put("misc_charge", strMiscFees);
        map.put("driver_discount", strDiscoDriver);

        if (strDropLocationFB.equals("NoDrop")) {
            map.put("distance", "0");
            map.put("drop_location", "NoDrop");
        } else {
            map.put("distance", cnvt_strDistanceEndTrip_);
            map.put("drop_location", strDestAddress);
        }

        map.put("drop_lat", get_Lat);
        map.put("drop_lon", get_Long);

        if (key.equalsIgnoreCase("both")) {
            strfinaltotalamt = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strfinaltotalamt);
            map.put("payment_type", key);
            map.put("wallet_amount", strUserWalletAmt);
            map.put("total_amount", strfinaltotalamt);
            map.put("customer_id", struserIdFB);
        } else if (key.equalsIgnoreCase("cash")) {
            // strfinaltotalamt = convertCurrencyDivideWithoutRoundPrice(MainActivity.this,strfinaltotalamt);
            map.put("payment_type", key);
            map.put("customer_id", struserIdFB);
        } else {
            map.put("payment_type", key);
        }

        if (!strPromoCodeFB.equals("")) {
            map.put("promocode", strPromoCodeFB);
        }

        map.put("total_agent", strCount);
        map.put("total_days", strDays);

        map.put("language", strLanguageShortName);

        Log.e("callEndtrip~", "~~~~" + map);

        Call<EndRequestModel> call2 = apiInterface.doGetEndRequestStatusDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<EndRequestModel>() {
            @Override
            public void onResponse(@NonNull Call<EndRequestModel> call, @NonNull Response<EndRequestModel> response) {

                hideProgressDialog();

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    hideProgressDialog();

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {
                        // dialogAdditionalEndCharge.dismiss();

                        putStateFBCategory.putBoolean(Utils.SP_FB_ACCEPT_DROP_LOCATION_ADDED, false);
                        putStateFBCategory.apply();

                        if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                            dialogAdditionalEndCharge.setVisibility(View.GONE);
                        }
                        lytJobInprogress.setVisibility(View.VISIBLE);

                        strBookingId = String.valueOf(Objects.requireNonNull(response.body()).getEndRequestDataModel().getBookingId());

                        String strDriverWalletAmt = String.valueOf(Objects.requireNonNull(response.body()).getEndRequestDataModel().getDriverWalletBalance());
                        putCommonState.putString(Utils.SP_DRIVER_WALLET_AMT, strDriverWalletAmt);
                        putCommonState.apply();


                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request status updated successfully.")) {

                        //dialogAdditionalEndCharge.dismiss();
                        if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                            dialogAdditionalEndCharge.setVisibility(View.GONE);
                        }

                        lytJobInprogress.setVisibility(View.VISIBLE);

                        strBookingId = String.valueOf(Objects.requireNonNull(response.body()).getEndRequestDataModel().getBookingId());

                        String strDriverWalletAmt = String.valueOf(Objects.requireNonNull(response.body()).getEndRequestDataModel().getDriverWalletBalance());
                        putCommonState.putString(Utils.SP_DRIVER_WALLET_AMT, strDriverWalletAmt);
                        putCommonState.apply();

                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Failure")) {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshTokenFinalAmount(key);
                } else {
                    Toast.makeText(MainActivity.this, "Error - 500", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EndRequestModel> call, @NonNull Throwable t) {
                call.cancel();
                Toast.makeText(MainActivity.this, "Error - 500 - onFailure - " + t.toString(), Toast.LENGTH_SHORT).show();
                out.println("end service api error " + t.getMessage());
            }
        });
    }

    private void updateCashonHandPayment() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookingId);
        map.put("driver_id", driverId);
        map.put("customer_id", struserIdFB);

        Log.i("updateCashonHandPayment", String.valueOf(map));
        showProgressDialog(MainActivity.this);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PaymentUpdateModel> call2 = apiInterface.doUpdatePaymentDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        call2.enqueue(new Callback<PaymentUpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<PaymentUpdateModel> call, @NonNull Response<PaymentUpdateModel> response) {

                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {

                        // if ("yes".equals("yes")){
                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                        refs.child("booking_trips").child(strBookingId).child("cash_status").setValue("1");
                        refs.child("providers").child(driverId).child("trips").child("cash_status").setValue("1");
                       /* }else {
                            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                            refs.child("booking_trips").child(strBookingId).child("cash_status").setValue("0");
                            refs.child("providers").child(driverId).child("trips").child("cash_status").setValue("0");
                        }*/

                    } else {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentUpdateModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void callStripeUpdate(String from) {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        String strMaterialFees = edtMaterialFee.getText().toString().trim();
        String strMiscFees = edtMiscFee.getText().toString().trim();
        String strDiscoDriver = edtDiscountDriver.getText().toString().trim();

        if (strMaterialFees.length() == 0) {
            strMaterialFees = "0.0";
        } else {
            strMaterialFees = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strMaterialFees);
        }
        if (strMiscFees.length() == 0) {
            strMiscFees = "0.0";
        } else {
            strMiscFees = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strMiscFees);
        }
        if (strDiscoDriver.length() == 0) {
            strDiscoDriver = "0.0";
        } else {
            strDiscoDriver = convertCurrencyDivideWithoutRoundPrice(MainActivity.this, strDiscoDriver);
        }

        String strDistanceEndTrip_sp = spTripState.getString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");

        if (strDistanceFB_EndJob == null) {
            strDistanceFB_EndJob = "0";
        }

        String strFinalDistance;
        if (Double.parseDouble(strDistanceEndTrip_sp) > Double.parseDouble(strDistanceFB_EndJob)) {
            strFinalDistance = strDistanceEndTrip_sp;
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "callEndtripDetails: Final Distance SP~~~~~" + strFinalDistance);
            }
        } else {
            strFinalDistance = strDistanceFB_EndJob;
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "callEndtripDetails: Final Distance FB~~~~~" + strFinalDistance);
            }
        }

        String cnvt_strDistanceEndTrip_;
        if (convertDistanceUnit(MainActivity.this).equals("mile")) {
            cnvt_strDistanceEndTrip_ = String.valueOf(convertKmsToMiles(MainActivity.this, Double.valueOf(strFinalDistance)));
        } else {
            cnvt_strDistanceEndTrip_ = strFinalDistance;
        }

        Log.e(TAG, "callStripeUpdate:distance~~~~ final~~convert KM Miles~~" + cnvt_strDistanceEndTrip_);

        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strBookingId);
        map.put("amount", strfinaltotalamt);
        map.put("driver_id", driverId);
        map.put("customer_id", struserIdFB);
        map.put("mode", "0");

        map.put("material_fee", strMaterialFees);
        map.put("misc_charge", strMiscFees);
        map.put("driver_discount", strDiscoDriver);
        if (strDropLocationFB.equals("NoDrop")) {
            map.put("distance", "0");
        } else {
            map.put("distance", cnvt_strDistanceEndTrip_);
        }

        map.put("type", from);

        map.put("language", strLanguageShortName);

        Log.e("callStripeUpdate", "~~~~~~~~" + map);

        Call<StripeEndTModel> call2 = apiInterface.doStripUpdateEndTrip("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(this);
        call2.enqueue(new Callback<StripeEndTModel>() {
            @Override
            public void onResponse(@NonNull Call<StripeEndTModel> call, @NonNull Response<StripeEndTModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Money debited successfully.") || Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        hideProgressDialog();
                        // callEndtrip("card");
                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                        if (from.equals("card")) {
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(strdriverIdFB).child("trips").child("card_status").setValue("2");
                            refs.child("booking_trips").child(strBookingId).child("card_status").setValue("2");
                        } else {
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(strdriverIdFB).child("trips").child("paypal_status").setValue("2");
                            refs.child("booking_trips").child(strBookingId).child("paypal_status").setValue("2");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: " + response.body().getMessage());
                    }
                } else if (response.code() == RESPONSECODE_500) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StripeEndTModel> call, @NonNull Throwable t) {
                // CommonUtil.showRetrofitError(LoginActivity.this, error);
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    private void callUpdateRating() {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        String strcmtTripsummary = edtCommentTripsummary.getText().toString().trim();
        String rating = String.valueOf(ratingbarUserTripsummary.getRating());

        HashMap<String, Object> map = new HashMap<>();
        map.put("from_id", driverId);
        map.put("job_id", strBookingId);
        map.put("to_id", struserIdFB);
        map.put("rating", rating);
        map.put("status", "0");
        map.put("feedback", strcmtTripsummary);
        Log.e("callAcceptRequest", "~~~~" + map);
        Call<RatingUserModel> call2 = apiInterface.doUpdateRatingUser("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<RatingUserModel>() {
            @Override
            public void onResponse(@NonNull Call<RatingUserModel> call, @NonNull Response<RatingUserModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Feedback received")) {
                        Toast.makeText(MainActivity.this, R.string.rating_update, Toast.LENGTH_SHORT).show();

                        cvRating.setVisibility(View.GONE);

                        DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                        refs.child("providers").child(strdriverIdFB).child("trips").child("rating_status").setValue("1");

                    } else {
                        Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("rating");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RatingUserModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void callCancelJobUserUpdate(String strreason, String strcmt) {

        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        Call<CancelJobModel> call2;

        if (strBackupVolunteerStatusFB.equals("0")) {
            if (strTransferJobStatusFB.equals("1")) {
                map.put("transfer_status", "1");
                map.put("mode", "cancel");
                map.put("booking_id", strBookingId);
                map.put("from_driver_id", strRequestedDriverIdFB);
                map.put("to_driver_id", driverId);

                call2 = apiInterface.doCancelBackupJobDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);

            } else {
                map.put("booking_id", strBookingId);
                map.put("driver_id", driverId);
                map.put("reason", strreason + "-" + strcmt);
                map.put("mode", "driver");
                map.put("language", strLanguageShortName);

                call2 = apiInterface.doCancelJobDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
            }
        } else {
            map.put("mode", "cancel");
            map.put("booking_id", strBookingId);
            map.put("from_driver_id", strRequestedDriverIdFB);
            map.put("to_driver_id", driverId);

            call2 = apiInterface.doCancelBackupJobDetails("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        }


        Log.i("callCancelJobUserUpdate", "~~~~" + map);

        showProgressDialog(this);
        call2.enqueue(new Callback<CancelJobModel>() {
            @Override
            public void onResponse(@NonNull Call<CancelJobModel> call, @NonNull Response<CancelJobModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Cancelled successfully.")) {

                        if (dialogCancelJobinfo != null && dialogCancelJobinfo.isShowing()) {
                            dialogCancelJobinfo.dismiss();
                        }

                        if (strBackupVolunteerStatusFB.equals("0")) {
                            DatabaseReference requestAcceptrDatabaseBRef;
                            requestAcceptrDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_USER_HEADER).child(struserIdFB).child(Utils.FIREBASE_TRIP_HEADER);
                            requestAcceptrDatabaseBRef.child(Utils.FIREBASE_ACCEPT_STATUS).setValue("0");
                        }

                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CancelJobModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });
    }

    /*-****************************************************
     *
     * Firebase
     *
     * ****************************************************/
    //get Driver online offlne status in firebase
    private void getDriverOnlineOrOffline(String from, String driverId, String reqStatus) {

        if (driverId != null) {
            if (driverAvailableDatabaseBRef != null) {
                driverAvailableDatabaseBRef.removeEventListener(driverAvailableValueEventListener);
            }
            driverAvailableDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
            driverAvailableValueEventListener = driverAvailableDatabaseBRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        strOnlineOfflineStatus = dataSnapshot.child("status").getValue() != null ? dataSnapshot.child("status").getValue().toString() : "0";
                        String approved = dataSnapshot.child("approved").getValue() != null ? dataSnapshot.child("approved").getValue().toString() : "0";
                        String pro_pic = dataSnapshot.child("pro_pic").getValue() != null ? dataSnapshot.child("pro_pic").getValue().toString() : "";
                        strDriverfname = dataSnapshot.child("fname").getValue() != null ? dataSnapshot.child("fname").getValue().toString() : "";

                        Object currencyObj = dataSnapshot.child("currency").getValue();
                        Object walletAmountObj = dataSnapshot.child("wallet").getValue();

                        if (strDriverfname != null && !strDriverfname.isEmpty()) {
                            String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
                            txtDrivernameOnline.setText(upperString);
                        } else {
                            txtDrivernameOnline.setText(getResources().getString(R.string.app_name));
                        }

                        if (strDriverfname != null && !strDriverfname.isEmpty()) {
                            String upperString = strDriverfname.substring(0, 1).toUpperCase() + strDriverfname.substring(1);
                            navigationName.setText(upperString);
                        } else {
                            navigationName.setText(getResources().getString(R.string.app_name));
                        }

                        try {
                            if (currencyObj != null) {
                                if (!currencyObj.toString().equals("")) {
                                    String strSplitCurrency = currencyObj.toString();
                                    String[] splitStr = strSplitCurrency.split(":");
                                    strCurrency = splitStr[0];
                                    strCurrencySymbol = splitStr[1];
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (walletAmountObj != null) {
                            strWalletAmt = walletAmountObj.toString();
                        } else {
                            strWalletAmt = "0";
                        }
                        Log.i(TAG, "onDataChange in walletAmount: -->" + strWalletAmt);
                        Log.i(TAG, "onDataChange in getDriverOnlineOrOffline: -->" + strOnlineOfflineStatus);

                        if (approved.equalsIgnoreCase("0") || approved.equalsIgnoreCase("2")) {

                            txtOnlineOffline.setText(getString(R.string.mainactivity_offline_txt));
                            switchOnlineOffline.setChecked(false);
                            switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                            switchOnlineOffline.setEnabled(false);
                            llGettingLocation.setVisibility(View.GONE);
                            llNotapproved.setVisibility(View.VISIBLE);

                            getDriverStatusFB();

                        } else {

                            llGettingLocation.setVisibility(View.VISIBLE);
                            llNotapproved.setVisibility(View.GONE);
                            switchOnlineOffline.setEnabled(true);
                            llDocApproved.setVisibility(View.GONE);
                            if (strOnlineOfflineStatus.equals("0")) {
                                txtOnlineOffline.setText(getString(R.string.mainactivity_offline_txt));
                                switchOnlineOffline.setChecked(false);
                                switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                                booleanSwitchOnlineOffline = false;

                            } else {
                                txtOnlineOffline.setText(getString(R.string.mainactivity_online_txt));
                                switchOnlineOffline.setChecked(true);
                                switchOnlineOffline.setBackColor(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
                                booleanSwitchOnlineOffline = true;
                            }
                        }

                        Object rating = dataSnapshot.child("rating").getValue();
                        if (rating != null) {
                            navRbUserRating.setRating(Float.parseFloat(rating.toString()));
                        } else {
                            navRbUserRating.setRating(0);
                        }

                        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);

                        if (strWalletAmt != null) {
                            if (!strWalletAmt.equals("0") || !strWalletAmt.equals(0)) {
                                navigationWalletAmount.setText(getString(R.string.nav_wallet_bal_text) + " " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strWalletAmt));
                            } else {
                                navigationWalletAmount.setText(getString(R.string.nav_wallet_bal_text) + " " + strCurrencySymbol + " " + strWalletAmt);
                            }
                            //navigationWalletAmount.setText(getString(R.string.nav_wallet_bal_text) + " " + strCurrencySymbol +" "+ convertCurrencyPrice(MainActivity.this,strWalletAmt));
                            //   navigationWalletAmount.setText(getString(R.string.nav_wallet_bal_text) + " " + strCurrencySymbol+ strWalletAmt);
                        } else {
                            navigationWalletAmount.setText(getString(R.string.nav_wallet_bal_text) + " " + strCurrencySymbol + " " + "0");
                        }

                        putCommonState.putString(Utils.SP_DRIVER_WALLET_AMT_MIN, strWalletAmt);
                        putCommonState.apply();

                        try {

                            //Delete Account
                            if (dataSnapshot.child("delete_status").getValue() != null) {
                                strDeleteStatus = dataSnapshot.child("delete_status").getValue().toString();
                            } else {
                                strDeleteStatus = "0";
                            }
                            System.out.println("strDeleteStatus-->" + strDeleteStatus);
                            deleteMyAccount = getspCommonState.getString(Utils.SP_DRIVER_DELETE_ACCOUNT, "0");
                            System.out.println("deleteMyAccount-->" + deleteMyAccount);
                            if (strDeleteStatus.equals("1")) {
                                if (deleteMyAccount.equals("0")) {
                                    Toast.makeText(MainActivity.this, getString(R.string.account_deleted_msg), Toast.LENGTH_LONG).show();
                                    clearSPmodePrivate();
                                }
                            }
                            //Delete Account


                            if (dataSnapshot.child("pending").getValue() != null || dataSnapshot.child("upcoming").getValue() != null) {
                                Object strPendingcountFB = Objects.requireNonNull(dataSnapshot.child("pending").getValue()).toString();
                                Object strUpcomingcountFB = Objects.requireNonNull(dataSnapshot.child("upcoming").getValue()).toString();
                                if (strPendingcountFB != null) {
                                    if (!strPendingcountFB.toString().equals("0")) {
                                        txtPendingCount.setText(strPendingcountFB.toString());
                                    }
                                } else {
                                    txtPendingCount.setText("0");
                                }
                                if (strUpcomingcountFB != null) {
                                    if (!strUpcomingcountFB.toString().equals("0")) {
                                        txtUpcomingCount.setText(strUpcomingcountFB.toString());
                                    }
                                } else {
                                    txtUpcomingCount.setText("0");
                                }
                            } else {
                                Log.e(TAG, "onDataChange:NULL Pending@upcoming");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onDataChange:Exception Pending@upcoming" + e);
                        }

                        /*Back up request for your driver id checking*/
                        String backup_request = checkStringEmptyZero(dataSnapshot, Utils.FIREBASE_BACKUP_REQUEST);
                        switch (backup_request) {
                            case "yes":

                                setVisibility(dialogRequesting, true);
                                setVisibility(llParentLytBackupGuardRequestAccepted, true);

                                if (countDownTimerSOSBackupRequest != null) {
                                    countDownTimerSOSBackupRequest.cancel();
                                }

                                if (rippleBackgroundRequesting != null) {
                                    rippleBackgroundRequesting.stopRippleAnimation();
                                }

                                break;
                            case "1":
                                break;
                            case "0":
                            default:
                                setVisibility(dialogRequesting, false);
                                setVisibility(llParentLytBackupGuardRequestAccepted, false);

                                if (countDownTimerSOSBackupRequest != null) {
                                    countDownTimerSOSBackupRequest.cancel();
                                }

                                if (rippleBackgroundRequesting != null) {
                                    rippleBackgroundRequesting.stopRippleAnimation();
                                }
                                break;
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            try {
                Log.e(TAG, "getDriverOnlineOrOffline: " + "exe");
            } catch (Exception e) {
                Log.e(TAG, "getDriverOnlineOrOffline: ", e);
            }
        }
    }

    private void getDriverOnlineOrOffline_(String from, String driverId, String reqStatus) {

        if (driverId != null) {
            if (driverAvailableDatabaseBRef_ != null) {
                driverAvailableDatabaseBRef_.removeEventListener(driverAvailableValueEventListener_);
            }
            driverAvailableDatabaseBRef_ = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
            driverAvailableValueEventListener_ = driverAvailableDatabaseBRef_.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        Object rating = dataSnapshot.child("rating").getValue();
                        if (rating != null) {
                            ratingBarBackupRequestedGuardJIP.setRating(Float.parseFloat(rating.toString()));
                        } else {
                            ratingBarBackupRequestedGuardJIP.setRating(0);
                        }

                        if (from.equals("backup")) {
                            /*Back up request for your requested driver id checking*/
                            String backup_request = checkStringEmptyZero(dataSnapshot, Utils.FIREBASE_BACKUP_REQUEST);
                            switch (backup_request) {
                                case "1":  // Guard Requesting

                                    if (reqStatus.equals("1")) {

                                        lytOnlineOffline.setVisibility(View.GONE);
                                        lytRequestReceive.setVisibility(View.VISIBLE);
                                        if (countDownTimerNormalReceivedRequest != null) {
                                            countDownTimerNormalReceivedRequest.start();
                                        }
                                        timerStart = true;
                                        playRequestTone();
                                    }

                                    break;

                                case "2":  // Guard Cancelled the request

                                    callClearStage(driverId);
                                    callClearFirebase("222", driverId);

                                    Toast.makeText(MainActivity.this, "Guard Cancelled the request.", Toast.LENGTH_SHORT).show();

                                    if (driverAvailableDatabaseBRef_ != null) {
                                        driverAvailableDatabaseBRef_.removeEventListener(driverAvailableValueEventListener_);
                                    }

                                    break;

                                case "3":  // Guard timeout Cancelled the request

                                    callClearStage(driverId);
                                    callClearFirebase("333", driverId);

                                    Toast.makeText(MainActivity.this, "Guard request time out.", Toast.LENGTH_SHORT).show();

                                    if (driverAvailableDatabaseBRef_ != null) {
                                        driverAvailableDatabaseBRef_.removeEventListener(driverAvailableValueEventListener_);
                                    }

                                    break;
                                // case "yes": no need

                                    /*setVisibility(dialogRequesting, true);
                                    setVisibility(llParentLytBackupGuardRequestAccepted, true);

                                    if (countDownTimerSOSBackupRequest != null) {
                                        countDownTimerSOSBackupRequest.cancel();
                                    }

                                    if (rippleBackgroundRequesting != null) {
                                        rippleBackgroundRequesting.stopRippleAnimation();
                                    }*/

                                //   break;
                                case "0":
                                default:
                                    setVisibility(dialogRequesting, false);
                                    setVisibility(llParentLytBackupGuardRequestAccepted, false);

                                    if (countDownTimerSOSBackupRequest != null) {
                                        countDownTimerSOSBackupRequest.cancel();
                                    }

                                    if (rippleBackgroundRequesting != null) {
                                        rippleBackgroundRequesting.stopRippleAnimation();
                                    }
                                    break;
                            }
                        } else {
                            /*Transfer request for your requested driver id checking*/
                            String transfer_request = checkStringEmptyZero(dataSnapshot, Utils.FIREBASE_TRANSFER_REQUEST);
                            switch (transfer_request) {
                                case "1":  // Guard Requesting
                                    if (reqStatus.equals("1")) {

                                        lytOnlineOffline.setVisibility(View.GONE);
                                        lytRequestReceive.setVisibility(View.VISIBLE);
                                        if (countDownTimerNormalReceivedRequest != null) {
                                            countDownTimerNormalReceivedRequest.start();
                                        }
                                        timerStart = true;
                                        playRequestTone();
                                    }
                                    break;
                                case "2":  // Guard Cancelled the request
                                    callClearStage(driverId);
                                    callClearFirebase("222", driverId);

                                    Toast.makeText(MainActivity.this, "Guard Cancelled the request.", Toast.LENGTH_SHORT).show();

                                    if (driverAvailableDatabaseBRef_ != null) {
                                        driverAvailableDatabaseBRef_.removeEventListener(driverAvailableValueEventListener_);
                                    }
                                    break;
                                case "3":  // Guard timeout Cancelled the request

                                    callClearStage(driverId);
                                    callClearFirebase("333", driverId);

                                    Toast.makeText(MainActivity.this, "Guard request time out.", Toast.LENGTH_SHORT).show();

                                    if (driverAvailableDatabaseBRef_ != null) {
                                        driverAvailableDatabaseBRef_.removeEventListener(driverAvailableValueEventListener_);
                                    }
                                    break;
                                case "yes":
                                /*setVisibility(dialogRequesting, true);
                                setVisibility(llParentLytBackupGuardRequestAccepted, true);
                                if (countDownTimerSOSBackupRequest != null) {
                                    countDownTimerSOSBackupRequest.cancel();
                                }
                                if (rippleBackgroundRequesting != null) {
                                    rippleBackgroundRequesting.stopRippleAnimation();
                                }*/
                                    break;
                                case "0":
                                    break;
                                default:
                                /*setVisibility(dialogRequesting, false);
                                setVisibility(llParentLytBackupGuardRequestAccepted, false);
                                if (countDownTimerSOSBackupRequest != null) {
                                    countDownTimerSOSBackupRequest.cancel();
                                }
                                if (rippleBackgroundRequesting != null) {
                                    rippleBackgroundRequesting.stopRippleAnimation();
                                }*/
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            try {
                Log.e(TAG, "getDriverOnlineOrOffline: " + "exe");
            } catch (Exception e) {
                Log.e(TAG, "getDriverOnlineOrOffline: ", e);
            }
        }
    }

    private void getDriverStatusFB() {

        hideProgressDialog();

        if (driverStatusCheckinoutDatabaseRef != null) {
            driverStatusCheckinoutDatabaseRef.removeEventListener(driverStatusCheckinoutValueEventListener);
        }
        driverStatusCheckinoutDatabaseRef = FirebaseDatabase.getInstance().getReference().child("documents_providers").child(driverId);
        driverStatusCheckinoutValueEventListener = driverStatusCheckinoutDatabaseRef.orderByValue().limitToLast(10).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Document_status").getValue() != null) {
                    strDocStatusFB = String.valueOf(dataSnapshot.child("Document_status").getValue());
                    strDocReviewStatusFB = String.valueOf(dataSnapshot.child("Document_review_status").getValue());
                    strExpiryDateFB = String.valueOf(dataSnapshot.child("expiry_status").getValue());
                    out.println("status-->" + strDocStatusFB + " ~~~ " + strExpiryDateFB);
                    if (strDocStatusFB.equals("1")) {
                        if (strDocReviewStatusFB.equals("0")) {
                            llDocApproved.setVisibility(View.VISIBLE);
                            txtDocApproved.setText(getResources().getString(R.string.your_document_verification_is_under_review));
                        } else if (strDocReviewStatusFB.equals("2")) {
                            llDocApproved.setVisibility(View.VISIBLE);
                            txtDocApproved.setText(getResources().getString(R.string.your_document_is_rejected));
                        } else if (strDocReviewStatusFB.equals("3")) {
                            llDocApproved.setVisibility(View.VISIBLE);
                            txtDocApproved.setText(R.string.your_document_has_expired_please_reupload_it);
                        } else if (strDocReviewStatusFB.equals("4")) {
                            llDocApproved.setVisibility(View.VISIBLE);
                            txtDocApproved.setText(R.string.your_expired_date_please_reupload_it);
                        } else {
                            llDocApproved.setVisibility(View.GONE);
                        }

                    } else {
                        txtDocApproved.setText(getResources().getString(R.string.no_documents_uploaded));
                        llDocApproved.setVisibility(View.VISIBLE);
                    }
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("documents_providers").child(driverId).child("Document_status").setValue("0");
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getDriverCheckinchoutFB~Error~", String.valueOf(databaseError));
            }
        });

    }

    //get wallet amount in firebase
    private void getCustomerWalletAmount(String rstatus) {

        if (!rstatus.equals("0") && !rstatus.equals("8") && !rstatus.equals("9")) {

            if (customerWalletDatabaseBRef != null) {
                customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
            }

            customerWalletDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_USER_HEADER).child(struserIdFB);
            customerWalletValueEventListener = customerWalletDatabaseBRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        Object walletAmountObj = dataSnapshot.child("wallet").getValue();
                        if (walletAmountObj != null) {
                            strUserWalletAmt = walletAmountObj.toString();
                        } else {
                            strUserWalletAmt = "0";
                        }

                        Object rating = dataSnapshot.child("rating").getValue();
                        if (rating != null) {
                            ratingbarJobinprogress.setRating(Float.parseFloat(rating.toString()));
                            ratingBarRequest.setRating(Float.parseFloat(rating.toString()));
                        } else {
                            ratingbarJobinprogress.setRating(0);
                            ratingBarRequest.setRating(0);
                        }

                        if (rstatus.equals("1") && !strBackupVolunteerStatusFB.equals("1")) {

                            if (strTripTypeFB.equals("1")) {

                                if (dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).getValue() != null) {

                                    if (dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).child("accept_status").getValue() != null) {

                                        if (dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).child("accept_status").getValue() != null) {

                                            String accept_status = Objects.requireNonNull(dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).child("accept_status").getValue()).toString();

                                            out.println("~~~~~~accept_status~~~~~~~" + accept_status);

                                            switch (accept_status) {
                                            /*case "0":

                                                callClearStage();
                                                callClearFirebase("000");

                                                if (customerWalletDatabaseBRef != null) {
                                                    customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                                                }

                                                break;*/
                                                case "1":  // Customer Requesting

                                                    if (reqStatus.equals("1")) {

                                                        lytOnlineOffline.setVisibility(View.GONE);
                                                        lytRequestReceive.setVisibility(View.VISIBLE);
                                                        countDownTimerNormalReceivedRequest.start();
                                                        timerStart = true;
                                                        playRequestTone();
                                                    }

                                                    break;

                                                case "2":  // Customer Cancelled the request

                                                    callClearStage(driverId);
                                                    callClearFirebase("222", driverId);

                                                    Toast.makeText(MainActivity.this, "Customer Cancelled the service.", Toast.LENGTH_SHORT).show();

                                                    if (customerWalletDatabaseBRef != null) {
                                                        customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                                                    }

                                                    break;

                                                case "3":  // Customer timeout Cancelled the request

                                                    callClearStage(driverId);
                                                    callClearFirebase("333", driverId);

                                                    Toast.makeText(MainActivity.this, "Customer request time out.", Toast.LENGTH_SHORT).show();

                                                    if (customerWalletDatabaseBRef != null) {
                                                        customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                                                    }

                                                    break;
                                                case "yes_": //No need for this project

                                                    if (dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).child("booking_id").getValue() != null) {

                                                        String booking_id = Objects.requireNonNull(dataSnapshot.child(Utils.FIREBASE_TRIP_HEADER).child("booking_id").getValue()).toString();

                                                        if (!booking_id.equals("0")) {

                                                            try {
                                                                FirebaseDatabase.getInstance().getReference().child("booking_trips").child(booking_id).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        if (dataSnapshot.child("driver_id").getValue() != null) {

                                                                            String strdriverIdFBCusFrom = Objects.requireNonNull(dataSnapshot.child("driver_id").getValue()).toString();

                                                                            if (!Objects.equals(strdriverIdFBCusFrom, driverId)) {

                                                                                callClearStage(driverId);
                                                                                callClearFirebase("yes", driverId);

                                                                                if (customerWalletDatabaseBRef != null) {
                                                                                    customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                        out.println("~~~error~~~" + error.toString());
                                                                    }
                                                                });
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                }

                            } else {
                                if (customerWalletDatabaseBRef != null) {
                                    customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                                }
                            }

                        } else {
                            if (customerWalletDatabaseBRef != null) {
                                customerWalletDatabaseBRef.removeEventListener(customerWalletValueEventListener);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ~~~~" + databaseError.toString());

                }
            });

            if (rstatus.equals("5") || rstatus.equals("5.1") || rstatus.equals("end")) {
                //getAppSettingsFB();
            }

        } else {
            out.println("~~~~listoner~request~status~~~~" + reqStatus + "~~~~" + rstatus);
        }
    }

    public void updateDriverLocationFirebase(Double lat, Double lng, Location locCurrentLocation) {
        Location localLocation = new Location("");
        if (locCurrentLocation != null) {
            localLocation = locCurrentLocation;
        } else {
            localLocation.setLatitude(0.0d);
            localLocation.setLongitude(0.0d);
        }
        Log.i(TAG, "updateDriverLocationFirebase: userid-->" + driverId);
        if (driverId != null && !driverId.isEmpty() && !driverId.trim().matches("")) {
            geoFire.setLocation(driverId, new GeoLocation(lat, lng), localLocation, new GeoFire.CompletionListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Log.i("There was an error saving the location to GeoFire: ", String.valueOf(lat + lng));
                    } else {
                        Log.i("Online Location saved ", "on server successfully!");
                    }
                }
            });
        }
    }

    public void updateDriverAvailableStatus(String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("providers").child(driverId);
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("status", status);
        databaseReference.updateChildren(taskMap);
    }

    public void emptyCurrentLatLng() {
        currentLatlng = new LatLng(0.0, 0.0);
    }

    public void removeRequestDetailsFirebase() {

        DatabaseReference refs = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER);
        Map<String, Object> updatess = new HashMap<>();
        updatess.put(Utils.FIREBASE_SERVICE_STATUS, "0");

        refs.setValue(updatess, (databaseError, databaseReference) -> {
            System.out.println("DATA SAVED SUCCESSFULLY");
            if (databaseError != null) {
                System.out.println("DATA SAVED SUCCESSFULLY");
            }
        });

        SharedPreferences preferences = getSharedPreferences(Utils.MY_PREFS_FB_CATEGORY_TRIPSTATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

    }

    public static void callPendingUpcomingStatus(String driverId, String key, String value) {

        DatabaseReference requestAcceptrDatabaseBRef;
        requestAcceptrDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
        requestAcceptrDatabaseBRef.child(key).setValue(value);

    }

    /*-****************************************************
     *
     * Firebase & update Location updateUI
     *
     * ****************************************************/
    private void update_UI_location(Location location) {

        Location currentLocation;
        try {

            currentLocation = location;

            // System.out.println("update_UI_location: ~~~~~~~~~~~~~~~~~~~~" + location.getLatitude() + "~~~~~" + location.getLongitude() + "~~~status~~~~" + reqStatus);

            if (booleanSwitchOnlineOffline) {
                //Driver Tracking location Firebase
                //Log.d(TAG, "CallUpdateDriverOnlineOfflineStatus-----reqStatus-------" + reqStatus);
                switch (reqStatus) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "10":
                    case "0":
                    default:
                        updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), currentLocation);
                        break;
                }

                // Log.e(TAG, "strDistanceBegin~~~~~~~~: " + strDistanceBegin);

                if (strDistanceBegin != null) {
                    Log.e(TAG, "onLocationChanged: " + strDistanceBegin);
                    if (strDistanceBegin.matches("distancebegin")) {
                        if (lStart == null) {
                            lStart = currentLocation;
                            lEnd = currentLocation;
                        } else lEnd = currentLocation;

                        //Calling the method below updates the  live values of distance and speed to the TextViews.
                        if (parseDouble(strCurrentSpeed) >= Utils.currentSpeedDouble) {
                            // System.out.println("value of updateUI==>>> 1");
                            updateUI();
                        } else {
                            System.out.println("value of updateUI==>>> 2");
                        }

                        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
                        speed = currentLocation.getSpeed() * 18 / 5;
                        // Log.e(TAG, "onLocationChanged1: Speed-->" + speed);
                        //Toast.makeText(Map_Activity.this, "trip started!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Log.e(TAG, "strDistanceBegin~~~~2else~~~~: " + strDistanceBegin);
                }

            } else {
                //Log.e(TAG, "booleanSwitchOnlineOffline~~~~2else~~~~: " + strDistanceBegin);
                switch (reqStatus) {
                    case "0":
                    case "1":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "10":
                    default:
                        break;
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "5.1":
                        // Log.e(TAG, "strDistanceBegin~~~~~~~~: " + strDistanceBegin);
                        updateDriverLocationFirebase(location.getLatitude(), location.getLongitude(), currentLocation);

                        if (strDistanceBegin != null) {
                            //Log.e(TAG, "onLocationChanged: " + strDistanceBegin);
                            if (strDistanceBegin.matches("distancebegin")) {
                                if (lStart == null) {
                                    lStart = currentLocation;
                                    lEnd = currentLocation;
                                } else lEnd = currentLocation;

                                //Calling the method below updates the  live values of distance and speed to the TextViews.
                                if (parseDouble(strCurrentSpeed) >= Utils.currentSpeedDouble) {
                                    System.out.println("value of updateUI==>>> 1");
                                    updateUI();
                                } else {
                                    System.out.println("value of updateUI==>>> 2");
                                }

                                //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
                                speed = currentLocation.getSpeed() * 18 / 5;
                                //Log.e(TAG, "onLocationChanged1: Speed-->" + speed);
                                //Toast.makeText(Map_Activity.this, "trip started!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Log.e(TAG, "strDistanceBegin~~~~2else~~~~: " + strDistanceBegin);
                        }
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String strDistanceEndTrips = "0";

    private void updateUI() {

        p = spTripState.getInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 0);
        System.out.println("value of p==>>>" + p);
        if (MainActivity.p == 0) {

            double maxAccuracy = speed * 2;
            double minAccuracy = 25;
            if (maxAccuracy < minAccuracy) {
                maxAccuracy = minAccuracy;
            }

            if (lStart != null && lEnd.getAccuracy() < maxAccuracy && lEnd.getTime() > lStart.getTime()) {

                MainActivity.endTime = System.currentTimeMillis();
                long diff = MainActivity.endTime - MainActivity.startTime;
                diff = TimeUnit.MILLISECONDS.toSeconds(diff);

                //  distance = distance + (lStart.distanceTo(lEnd) * 1.04 / 1000.00);
                distance = distance + (meterDistanceBetweenPoints(lStart.getLatitude(), lStart.getLongitude(), lEnd.getLatitude(), lEnd.getLongitude()) * 1.04 / 1000.00);

                if (speed > 0.0) {
                    Log.e(TAG, "updateUI~~~~~1~~~distance~: " + distance);
//                    System.out.println("Speedd===>" + new android.icu.text.DecimalFormat("0.00").format(speed) + "km/hr");
                } else {
                    System.out.println("else Speedd===>");
                }

                lStart = lEnd;
            }

            if (distance > 0) {
                //https://stackoverflow.com/questions/39183447/use-decimalformat-with-localization
                DecimalFormatSymbols symbolsEN_US = DecimalFormatSymbols.getInstance(Locale.US);
                //  Log.e(TAG, "updateUI~~~~~3~~~distance~: " + distance);
                strDistanceEndTrip = new DecimalFormat("0.0##", symbolsEN_US).format(distance);
//                Log.e(TAG, "TOTAL DISTANCE+++>" + strDistanceEndTrip);
                strDistanceEndTrip = strDistanceEndTrip.replaceAll(",", ".");
//                Log.e(TAG, "TOTAL DISTANCE replace+++>" + strDistanceEndTrip);

                if (driverId != null && !driverId.isEmpty()) {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("providers").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("distance").getValue() != null) {
                                    strDistanceEndTrips = Objects.requireNonNull(dataSnapshot.child("distance").getValue()).toString();
                                } else {
                                    strDistanceEndTrips = "0";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: ~~~~" + databaseError.toString());
                                strDistanceEndTrips = "0";
                            }
                        });

                        if (parseDouble(strDistanceEndTrips) < parseDouble(strDistanceEndTrip)) {
                            System.out.println("Inside distance ---->> FB--->> ");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("providers").child(driverId).child("distance").setValue(strDistanceEndTrip);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                strDistanceEndTrip = String.valueOf(distance);
                try {
                    if (driverId != null && !driverId.isEmpty()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("providers").child(driverId).child("distance").setValue(strDistanceEndTrip);
                            }
                        }, 50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, strDistanceEndTrip);
            spEditTripState.commit();
        }
    }


    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if (location != null) {
            location.setUseMetricunits(true);
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "miles/hour";
                 /*if (spRefs.getString(Utils.SP_MAP_TRIP_STATE, null).equals("Accepttrip")) {
                directionPolyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 3, Utils.POLYLINE_COLOR_ACCEPTTRIP));
            } else if (spRefs.getString(Utils.SP_MAP_TRIP_STATE, null).equals("Starttrip")) {
                directionPolyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 3, Utils.POLYLINE_COLOR_STARTTRIP));
            }*/
        if (true) {
            strUnits = "meters/second";
        }

        //Log.i(TAG, "updateSpeed: current speed--->" + strCurrentSpeed);
    }

    /*-****************************************************
     *
     * Location Marker & Countdown Timer Distance Duration
     *
     * ****************************************************/
    public void driverCarMarker(Location location) {

        // Log.i(TAG, "driverCarMarker: ---------in bearing>" + location.getBearing());
        curPos = new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude());

        if (driverCarMarker != null) {
            // Log.i(TAG, "driverCarMarker: driverCarMarker not null-->" + driverCarMarker);
            // Log.i(TAG, "driverCarMarker:  strEstDuration-->" + strEstDuration + "strEstDistance" + strEstDistance);
            driverCarMarker.remove();
            //driverCarMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude())).icon(bitmapDescriptor).flat(true));
            if (strEstDuration != null && strEstDistance != null) {
                driverCarMarker = mMap.addMarker(new MarkerOptions().position(curPos).title(strEstDistance + " , " + strEstDuration).icon(bitmapDescriptor).flat(true));

            } else {
                driverCarMarker = mMap.addMarker(new MarkerOptions().position(curPos).icon(bitmapDescriptor).flat(true));

            }
            driverCarMarker.setAnchor(0.5f, 0.5f);
        }
        if (driverCarMarker == null) {
            // Log.i(TAG, "driverCarMarker: is null");
            //driverCarMarker.remove();
            if (strEstDuration != null && strEstDistance != null) {
                driverCarMarker = mMap.addMarker(new MarkerOptions().icon(bitmapDescriptor).title(strEstDistance + " , " + strEstDuration).position(new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude())).flat(true));
                driverCarMarker.setAnchor(0.5f, 0.5f);
                driverCarMarker.setRotation(location.getBearing());

            } else {

                driverCarMarker = mMap.addMarker(new MarkerOptions().icon(bitmapDescriptor).position(new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude())).flat(true));
                driverCarMarker.setAnchor(0.5f, 0.5f);
                driverCarMarker.setRotation(location.getBearing());

            }

        } else {
            //  System.out.println("enter the bearing " + location.getBearing());
            if (location.getBearing() != 0.0) {
                previousBearing = location.getBearing();
            }
            if (prevLatLng != new LatLng(0, 0)) {

                if (!(curPos.equals(prevLatLng))) {

                    double[] startValues = new double[]{prevLatLng.latitude, prevLatLng.longitude};

                    double[] endValues = new double[]{curPos.latitude, curPos.longitude};

                    //  System.out.println("Start location===>" + startValues[0] + "  " + startValues[1]);
                    //   System.out.println("end location===>" + endValues[0] + "  " + endValues[1]);

                    //  System.out.println("inside locationchange bearing" + location.getBearing());
                    baseActivity.animateMarkerTo(driverCarMarker, startValues, endValues, location.getBearing(), mMap, locFusedLocation);

                } else {
                    //  System.out.println("outside locationchange bearing" + location.getBearing());
                    if (location.getBearing() == 0.0) driverCarMarker.setRotation(previousBearing);
                    else driverCarMarker.setRotation(location.getBearing());
                    // currentLocMarker.setRotation(mCurrentLocation.getBearing());
                }
            } else {
                driverCarMarker.setPosition(curPos);
                driverCarMarker.setRotation(location.getBearing());
            }

            prevLatLng = new LatLng(curPos.latitude, curPos.longitude);
        }

    }

    /*-****************************************************
     *
     * Override
     *
     * ****************************************************/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: called");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else {
                gpsState();
            }
        } else {
            gpsState();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        callTripStatusFB(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: ~~~~" + databaseError.toString());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        updateTraffic();

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult()res:", Integer.toString(resultCode));
        Log.d("onActivityResult()req:", Integer.toString(requestCode));

        switch (requestCode) {

            case CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE:

                checkOverlayPermission();

                break;
        }
    }

    /*-****************************************************
     *
     * Widget & Services - Common & OnTrip views
     *
     * ****************************************************/

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Utils.RESULT_DATA_KEY);
            String gettingType = resultData.getString(Utils.RESULT_DATA);
            Log.i(TAG, "onReceiveResult: gettingType-->" + gettingType);
            //displayAddressOutput();
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Utils.SUCCESS_RESULT) {
                //    showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            //mAddressRequested = false;
            // updateUIWidgets();
        }
    }

    public void displayAddressOutput() {
        Log.i(TAG, "onReceiveResult: geocoder-->" + mAddressOutput);
        addressTxtView.setText(mAddressOutput);
    }

    private void startIntentService(Location location, String type) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Utils.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Utils.LOCATION_DATA_EXTRA, location);
        intent.putExtra(Utils.GEOCODER_TYPE, type);
        Log.i(TAG, "startIntentService: type---->" + type);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                boolean mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
                Log.i(TAG, "updateValuesFromBundle:mAddressRequested -->" + mAddressRequested);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                Log.i(TAG, "updateValuesFromBundle:mAddressOutput -->" + mAddressOutput);
                displayAddressOutput();

            }
        }
    }

    private void startIntentServiceRequest() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        intentRequest = new Intent(this, RequestServices.class);
        startService(intentRequest);
    }

    public void startWidgetService() {

        Intent intent = new Intent(getApplication(), WidgetService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openRouteNavigator();
            }
        }, 500);
    }

    public void checkOverlayPermission() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startFloatingViewService(MainActivity.this, true);
            //startWidgetService();
        }
        //Check if it can be displayed on other applications
        else if (Settings.canDrawOverlays(this)) {
            startFloatingViewService(MainActivity.this, true);
            // startWidgetService();
        } else if (overlayHelper != null && overlayHelper.canDrawOverlays()) {
            startFloatingViewService(MainActivity.this, true);
        }
        //Display overlay permissions
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.alert);
            builder.setMessage(R.string.aler_overlay_application);
            // builder.setCancelable(false);
            builder.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
                            startActivityForResult(intent, CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                    }, 200);
                }
            });

            /*builder.setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openRouteNavigator();
                        }
                    });*/

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.show();

        }
    }

    public void openRouteNavigator() {

        String selectedDrawable = getspFBCategory.getString(Utils.SP_NAVIGATION, null);

        if (selectedDrawable != null) {

            switch (selectedDrawable) {
                case "gmap":

                    callGooglemapDetails();
                    break;
                case "inapp":

                    try {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro));
                        if (!success) {
                            Log.e(TAG, "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Can't find style. Error: ", e);
                    }

                    break;
                default:

                    callWazemapDetails();
                    break;
            }
        } else {

            callWazemapDetails();

            /*try {
                String uri = "waze://?ll="+routeLat+", "+routeLng+"=yes";
                Intent waze =new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                waze.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(waze);

            } catch (ActivityNotFoundException e) {
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("App Not Found");
                builder.setMessage("For Navigate you need waze app");
                // builder.setCancelable(false);
                builder.setPositiveButton("Go to playstore",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent =
                                        new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                                startActivity(intent);
                            }
                        });
                builder.show();
            }*/
        }
    }

    public void checkOverlayPermissionNew() {
        // checkOverlayPermission();
        // API22
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            /*if (lytOverLayWidget.getVisibility() == View.VISIBLE) {
                lytOverLayWidget.setVisibility(View.GONE);
            }*/
            startFloatingViewService(MainActivity.this, false);
            return;
        } else if (Settings.canDrawOverlays(MainActivity.this)) {
            /*if (lytOverLayWidget.getVisibility() == View.VISIBLE) {
                lytOverLayWidget.setVisibility(View.GONE);
            }*/
            Log.e(TAG, "checkOverlayPermissionNew: ");
            startFloatingViewService(MainActivity.this, false);
            return;
        } else {

        }

        if (overlayHelper.canDrawOverlays()) {
            Log.e(TAG, "onCreate: ~~~~~~~~~true``````````````~~~");
           /* if (lytOverLayWidget.getVisibility() == View.VISIBLE) {
                lytOverLayWidget.setVisibility(View.GONE);
            }*/
            startFloatingViewService(MainActivity.this, false);
            //  switchOverlayWidget.setChecked(true);
            return;
        }
    }

    private void startFloatingViewService(Activity activity, boolean isCustomFloatingView) {
        // *** You must follow these rules when obtain the cutout(FloatingViewManager.findCutoutSafeArea) ***
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 1. 'windowLayoutInDisplayCutoutMode' do not be set to 'never'
            if (activity.getWindow().getAttributes().layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
                throw new RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to 'never'");
            }
        }

        // launch service
        final Class<? extends Service> service;

        final String key;

        if (isCustomFloatingView) {

            service = WidgetService.class;
            key = WidgetService.EXTRA_CUTOUT_SAFE_AREA;

            intentWidget = new Intent(activity, service);
            intentWidget.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intentWidget);

            Log.e(TAG, "startFloatingViewService: ~~~" + getspFBCategory.getString(Utils.SP_NAVIGATION, null));

            if (getspFBCategory.getString(Utils.SP_NAVIGATION, null) != null) {
                if (Objects.equals(getspFBCategory.getString(Utils.SP_NAVIGATION, null), "gmap") || Objects.equals(getspFBCategory.getString(Utils.SP_NAVIGATION, null), "waze") || Objects.equals(getspFBCategory.getString(Utils.SP_NAVIGATION, null), "inapp")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openRouteNavigator();
                        }
                    }, 500);
                }
            }

        } else {
            service = FloatingWidgetService.class;
            key = FloatingWidgetService.EXTRA_CUTOUT_SAFE_AREA;

            intentFloating = new Intent(activity, service);
            intentFloating.putExtra(key, FloatingViewManager.findCutoutSafeArea(activity));
            // ContextCompat.startForegroundService(activity, intentFloating);

            Context context = getApplicationContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                new Handler().postDelayed(() -> {
                    try {
                        ContextCompat.startForegroundService(activity, intentFloating);
                    } catch (ForegroundServiceStartNotAllowedException e) {
                        e.printStackTrace();
                        try {
                            context.startForegroundService(intentFloating);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }, 500);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    new Handler().postDelayed(() -> {
                        try {
                            context.startForegroundService(intentFloating);
                        } catch (Exception e) {
                            e.printStackTrace();
                            context.startForegroundService(intentFloating);
                        }
                    }, 500);
                }
            }

        }
    }

    private void stopIntentServiceRequest() {

        /*if (intentRequest != null) {
            stopService(intentRequest);
        }*/

        if (intentFloating != null) {
            stopService(intentFloating);
        }

        if (intentWidget != null) {
            stopService(intentWidget);
        }
    }
    /*-****************************************************
     *
     * OnTrip JobInprogress Views
     *
     * ****************************************************/

    public void getTripStatusListener() {

        if (driverId != null) {
            if (tripStatusDatabaseRef != null) {
                tripStatusDatabaseRef.removeEventListener(tripStatusValueEventListener);
            }
            tripStatusDatabaseRef = /*new GeoFire(*/
                    FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER)
                            .child(driverId).child(Utils.FIREBASE_TRIP_HEADER).getRef()
            /*).getDatabaseReference()*/;
            tripStatusValueEventListener = tripStatusDatabaseRef.addValueEventListener(this);
        } else {
            try {
                Log.e(TAG, "getTripStatus_Listener: " + "exe");
            } catch (Exception e) {
                Log.e(TAG, "getTripStatus_Listener: ", e);
            }
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "UseCompatTextViewDrawableApis"})
    private void callTripStatusFB(DataSnapshot dataSnapshot) {

        Object reqStatusObj = dataSnapshot.child(Utils.FIREBASE_SERVICE_STATUS).getValue();

        Log.i("reqStatusObj-->", String.valueOf(reqStatusObj));

        if (reqStatusObj != null) {
            reqStatus = String.valueOf(reqStatusObj);
            switch (reqStatus) {

                case "0":
                    stopPlaying();

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    //countDownTimerDistanceDuration.cancel();
                    //countDownTimerDistanceDurationAccept.cancel();

                    strTripstate = "Normalstate";
                    /*lytOnlineOffline.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.VISIBLE);*/

                    lytOnlineOffline.setVisibility(View.VISIBLE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.GONE);
                    lytCardPayment.setVisibility(View.GONE);
                    lytCardPaymentTitleMsg.setVisibility(View.GONE);

                    countDownTimerNormalReceivedRequest.cancel();
                    countDownTimerSOSBackupRequest.cancel();

                    callClickAction(btnAcceptRequest, true);
                    callClickAction(btnDeclineRequest, true);

                    stopRepeatingTask();

                    callStopGeoFencing();

                    break;
                case "1":

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Request");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    lytOnlineOffline.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.VISIBLE);
                    countDownTimerNormalReceivedRequest.start();
                    timerStart = true;

                    playRequestTone();

                    struserIdFB = checkStringEmpty(dataSnapshot, "customer_id");
                    struserNameFB = checkStringEmpty(dataSnapshot, "customer_name");

                    strMainServiceFB = checkStringEmpty(dataSnapshot, "main_service");
                    strSubServiceFB = checkStringEmpty(dataSnapshot, "sub_service");

                    strPaymodeFB = Objects.requireNonNull(dataSnapshot.child("paymode").getValue()).toString();
                    strPickupLatFB = Objects.requireNonNull(dataSnapshot.child("pickup_lat").getValue()).toString();
                    strPickupLngFB = Objects.requireNonNull(dataSnapshot.child("pickup_lng").getValue()).toString();
                    strPromoCodeFB = Objects.requireNonNull(dataSnapshot.child("promocode").getValue()).toString();
                    strSpecialInstFB = Objects.requireNonNull(dataSnapshot.child("special_ins").getValue()).toString();
                    strPickupLocationFB = Objects.requireNonNull(dataSnapshot.child("user_pickup").getValue()).toString();
                    struserRatingFB = Objects.requireNonNull(dataSnapshot.child("user_rating").getValue()).toString();

                    strDropLocationFB = Objects.requireNonNull(dataSnapshot.child("user_drop").getValue()).toString();
                    strDropLatFB = Objects.requireNonNull(dataSnapshot.child("drop_lat").getValue()).toString();
                    strDropLngFB = Objects.requireNonNull(dataSnapshot.child("drop_lon").getValue()).toString();
                    strdistanceFB = Objects.requireNonNull(dataSnapshot.child("distance").getValue()).toString();

                    strBasePriceFB = Objects.requireNonNull(dataSnapshot.child("base_fare").getValue()).toString();

                    if (dataSnapshot.child("trip_type").getValue() != null) {
                        strTripTypeFB = Objects.requireNonNull(dataSnapshot.child("trip_type").getValue()).toString();
                    } else {
                        strTripTypeFB = "1";
                    }

                    if (dataSnapshot.child("start_date_time").getValue() != null) {
                        strStartDateTimeFB = Objects.requireNonNull(dataSnapshot.child("start_date_time").getValue()).toString();
                        out.println("strStartDateTimeFB" + strStartDateTimeFB);

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat date12Format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        try {
                            String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(strStartDateTimeFB)));
                            out.println("strStartDateTimeFB" + yearTime1);
                            strStartDateTimeFB = yearTime1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        strStartDateTimeFB = "";
                    }

                    if (dataSnapshot.child("end_date_time").getValue() != null) {
                        strEndDateTimeFB = Objects.requireNonNull(dataSnapshot.child("end_date_time").getValue()).toString();

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat date12Format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        try {
                            String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(strEndDateTimeFB)));
                            out.println("strStartDateTimeFB" + yearTime1);
                            strEndDateTimeFB = yearTime1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        strEndDateTimeFB = "";
                    }

                    if (dataSnapshot.child("volunteer_status").getValue() != null) {
                        strBackupVolunteerStatusFB = Objects.requireNonNull(dataSnapshot.child("volunteer_status").getValue()).toString();
                    } else {
                        strBackupVolunteerStatusFB = "0";
                    }

                    if (dataSnapshot.child("transfer_status").getValue() != null) {
                        strTransferJobStatusFB = Objects.requireNonNull(dataSnapshot.child("transfer_status").getValue()).toString();
                    } else {
                        strTransferJobStatusFB = "0";
                    }

                    if (dataSnapshot.child("customer_booking_id").getValue() != null) {
                        strCustomerBookingIdFB = Objects.requireNonNull(dataSnapshot.child("customer_booking_id").getValue()).toString();
                    } else {
                        strCustomerBookingIdFB = "0";
                    }

                    final String strMainServiceArFB, strSubServiceArFB;
                    if (dataSnapshot.child("main_service_ar").getValue() != null) {
                        strMainServiceArFB = Objects.requireNonNull(dataSnapshot.child("main_service_ar").getValue()).toString();
                    } else {
                        strMainServiceArFB = "";
                    }

                    if (dataSnapshot.child("sub_service_ar").getValue() != null) {
                        strSubServiceArFB = Objects.requireNonNull(dataSnapshot.child("sub_service_ar").getValue()).toString();
                    } else {
                        strSubServiceArFB = "";
                    }

                    if (dataSnapshot.child("total_agent").getValue() != null && !dataSnapshot.child("total_agent").getValue().toString().equals("")) {
                        strCount = Objects.requireNonNull(dataSnapshot.child("total_agent").getValue()).toString();
                        txtCount.setText(strCount);
                    } else {
                        strCount = "";

                    }
                    if (dataSnapshot.child("total_days").getValue() != null && !dataSnapshot.child("total_days").getValue().toString().equals("")) {
                        strDays = Objects.requireNonNull(dataSnapshot.child("total_days").getValue()).toString();
                        txtDays.setText(strDays);
                    } else {
                        strDays = "";
                    }

                    if (strPaymodeFB.equals("card")) {
                        strSripeTokenFB = Objects.requireNonNull(dataSnapshot.child("stripe_token").getValue()).toString();
                        // strSripeCardNumFB = Objects.requireNonNull(dataSnapshot.child("card_number").getValue()).toString();
                    }

                    if (strPaymentTypeFB != null && strPaymentTypeFB.equals("stripe_invoice")) {
                        strSripeTokenFB = "Stripe";
                    } else if (strPaymentTypeFB != null && strPaymentTypeFB.equals("apple")) {
                        strSripeTokenFB = "Apple";
                    } else if (strPaymentTypeFB != null && strPaymentTypeFB.equals("stripe_gpay")) {
                        strSripeTokenFB = "GPay";
                    }

                    getCategoriesFirebase(strMainServiceFB, strSubServiceFB, strPickupLatFB, strPickupLngFB, strPaymodeFB, strSripeTokenFB, strDropLocationFB, strDropLatFB, strDropLngFB, strdistanceFB);

                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    try {
                        String[] separated = struserNameFB.split(":");
                        String strfname = separated[0];
                        //txtUserNameRequest.setText(strfname);
                        String strlname = separated[1];
                        txtUserNameRequest.setText(strfname + " " + strlname);


                        Log.i(TAG, "onDataChange: mainservice in request --" + strSubServiceFB + "~~~~~~~" + strMainServiceFB);

                        String[] separated_ = strSubServiceFB.split(":");
                        String strsubserviceno = separated_[0];
                        String strsubservicename = separated_[1];
                        if (strLanguageShortName.equals("en")) {
                            txtCategoryNameRequest.setText(strsubservicename);
                        } else {
                            if (!strSubServiceArFB.equals("")) {
                                txtCategoryNameRequest.setText(strSubServiceArFB);
                            } else {
                                txtCategoryNameRequest.setText(strsubservicename);
                            }
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "onDataChange: exception in status 1-->" + e);
                        txtCategoryNameRequest.setText(strSubServiceFB);
                    }
                    try {
                        String[] separated = strMainServiceFB.split(":");
                        String strsubserviceno = separated[0];
                        //txtCategoryNameRequest.setText(strfname);
                        String strsubservicename = separated[1];

                        if (strLanguageShortName.equals("en")) {
                            txtJobTypeRequest.setText(strsubservicename);
                        } else {
                            if (!strMainServiceArFB.equals("")) {
                                txtJobTypeRequest.setText(strMainServiceArFB);
                            } else {
                                txtJobTypeRequest.setText(strsubservicename);
                            }
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "onDataChange: exception in status 1-->" + e);
                        txtJobTypeRequest.setText(strMainServiceFB);
                    }

                    txtPickupLocationRequest.setText(strPickupLocationFB.replaceAll("%20", "").replaceAll("%40", ""));
                    txtSpecialInstrRequest.setText(strSpecialInstFB.replaceAll("%20", "").replaceAll("%40", ""));

                    if (strDropLocationFB.equals("NoDrop")) {
                        txtDropLocationRequestLabel.setVisibility(View.GONE);
                        txtDropLocationRequest.setVisibility(View.GONE);

                    } else {
                        txtDropLocationRequestLabel.setVisibility(View.VISIBLE);
                        txtDropLocationRequest.setVisibility(View.VISIBLE);
                        txtDropLocationRequest.setText(strDropLocationFB.replaceAll("%20", "").replaceAll("%40", ""));
                    }

                    strEmergencyMsgFB = checkStringEmpty(dataSnapshot, "emergency_msg");

                    txtEmergencyMsgRequest.setText(strEmergencyMsgFB.replaceAll("%20", "").replaceAll("%40", ""));

                    callClickAction(btnAcceptRequest, true);
                    callClickAction(btnDeclineRequest, true);

                    getCustomerWalletAmount("1");

                    if (strBackupVolunteerStatusFB.equals("1")) {

                        strBookingId = checkStringEmpty(dataSnapshot, "booking_id");
                        strRequestedDriverIdFB = checkStringEmpty(dataSnapshot, "requested_driver_id");
                        strRequestedDriverNameFB = checkStringEmpty(dataSnapshot, "requested_driver_name");
                        strRequestedDriverAvatarFB = checkStringEmpty(dataSnapshot, "requested_driver_avatar");

                        setVisibility(trStartDateTimeRequest, false);
                        setVisibility(trEndDateTimeRequest, false);

                        setVisibility(rlUserDetailsLytJobinprogress, false);
                        setVisibility(llBottomUserDetailsRequest, false);
                        txtTripTypeRequest.setText(getResources().getString(R.string.backup_required_request));

                        if (!strRequestedDriverIdFB.equals("")) {
                            getDriverOnlineOrOffline_("backup", strRequestedDriverIdFB, reqStatus);
                        }

                    } else {

                        strBookingId = checkStringEmpty(dataSnapshot, "booking_id");
                        strRequestedDriverIdFB = checkStringEmpty(dataSnapshot, "requested_driver_id");

                        setVisibility(rlUserDetailsLytJobinprogress, true);
                        setVisibility(llBottomUserDetailsRequest, true);

                        if (strTransferJobStatusFB.equals("1")) {
                            txtTripTypeRequest.setText(getResources().getString(R.string.transfer_job_request));
                            if (strTripTypeFB.equals("1")) {
                                setVisibility(trStartDateTimeRequest, false);
                                setVisibility(trEndDateTimeRequest, false);
                            } else {
                                setVisibility(trStartDateTimeRequest, true);
                                setVisibility(trEndDateTimeRequest, true);
                                txtStartDateTimeRequest.setText(strStartDateTimeFB);
                                txtEndDateTimeRequest.setText(strEndDateTimeFB);
                            }

                            if (!strRequestedDriverIdFB.equals("")) {
                                getDriverOnlineOrOffline_("transfer", strRequestedDriverIdFB, reqStatus);
                            }

                        } else {
                            if (strTripTypeFB.equals("1")) {
                                txtTripTypeRequest.setText(getResources().getString(R.string.sos_request));
                                setVisibility(trStartDateTimeRequest, false);
                                setVisibility(trEndDateTimeRequest, false);
                            } else {
                                setVisibility(trStartDateTimeRequest, true);
                                setVisibility(trEndDateTimeRequest, true);
                                txtStartDateTimeRequest.setText(strStartDateTimeFB);
                                txtEndDateTimeRequest.setText(strEndDateTimeFB);
                                txtTripTypeRequest.setText(getResources().getString(R.string.schedule_request));
                            }
                        }

                    }

                    hideProgressDialog();

                    break;
                case "2":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");
                    strBackupVolunteerStatusFB = checkStringEmptyZero(dataSnapshot, "volunteer_status");
                    strTransferJobStatusFB = checkStringEmptyZero(dataSnapshot, "transfer_status");

                    stopPlaying();
                    stopPlaying();
                    countDownTimerNormalReceivedRequest.cancel();

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Accepttrip");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    //  generateNotification(MainActivity.this, getString(R.string.map_status_driver_accept_request));

                    callGeneralDetails(dataSnapshot);

                    callGeneralLytViews("2");

                    getDriverEstDistanceDuration(strdriverIdFB);

                    getCustomerWalletAmount("2");

                    hideProgressDialog();

                    break;
                case "3":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "ArriveTrips");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    callGeneralDetails(dataSnapshot);


                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    callGeneralLytViews("3");

                    getDriverEstDistanceDuration(strdriverIdFB);

                    getCustomerWalletAmount("3");

                    hideProgressDialog();
                    break;
                case "4":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "ArriveTrip");
                    spEditTripState.commit();

                    callGeneralDetails(dataSnapshot);

                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    callGeneralLytViews("4");

                    /*String request_status = checkStringEmpty(dataSnapshot, "request_status");
                    if (!request_status.equals("")) {
                        switch (request_status) {
                            case "1":
                                btnBeginjobtrip.setEnabled(true);
                                rlUserCancelRetryTop.setVisibility(View.VISIBLE);
                                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);
                                tvAcceptTripCancelRetry.setText(getResources().getString(R.string.user_accept_your_trip_amp_slide_to_begin_trip));
                                tvAcceptTripCancelRetry.setBackgroundColor(getResources().getColor(R.color.text_color_success));
                                break;
                            case "2":
                                btnBeginjobtrip.setEnabled(false);
                                rlUserCancelRetryTop.setVisibility(View.VISIBLE);
                                rlUserCancelRetryCancelLyt.setVisibility(View.VISIBLE);
                                tvAcceptTripCancelRetry.setText(getResources().getString(R.string.user_cancel_to_begin_trip));
                                tvAcceptTripCancelRetry.setBackgroundColor(getResources().getColor(R.color.text_color_error));
                                break;
                            case "3":
                            case "0":
                                btnBeginjobtrip.setEnabled(false);
                                rlUserCancelRetryTop.setVisibility(View.VISIBLE);
                                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);
                                tvAcceptTripCancelRetry.setText(getResources().getString(R.string.please_wait_for_user_confirm_to_begin_trip));
                                tvAcceptTripCancelRetry.setBackgroundColor(getResources().getColor(R.color.black));
                                break;
                            default:
                                rlUserCancelRetryTop.setVisibility(View.GONE);
                                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        DatabaseReference requestAcceptrDatabaseBRefs;
                        requestAcceptrDatabaseBRefs = FirebaseDatabase.getInstance().getReference().child("booking_trips").child(strBookingId);
                        requestAcceptrDatabaseBRefs.child("request_status").setValue("0");
                    }*/

                    startRepeatingTask();

                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.apply();

                    getCustomerWalletAmount("4");

                    hideProgressDialog();

                    break;
                case "5":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Starttrip");
                    spEditTripState.commit();

                    callGeneralDetails(dataSnapshot);

                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    callGeneralLytViews("5");

                    //startRepeatingTask();

                    getDriverEstDistanceDuration(strdriverIdFB);

                    getCustomerWalletAmount("5");

                    getDriverDistanceFB("5");

                    if (strDropLocationFB.equals("NoDrop")) {
                        strDistanceBegin = "totaldistancend";
                        MainActivity.p = 1;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    } else {
                        strDistanceBegin = "distancebegin";
                        MainActivity.p = 0;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 0);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    }

                    out.println("drop location is " + strDropLocationFB);

                    hideProgressDialog();

                    stopRepeatingTask();

                    break;

                case "5.1":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Endtrip");
                    spEditTripState.commit();

                    callGeneralDetails(dataSnapshot);

                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    callGeneralLytViews("5.1");

                    String strCardStatus = checkStringEmpty(dataSnapshot, "card_status");
                    if (!strCardStatus.equals("")) {
                        switch (strCardStatus) {
                            case "0":
                                lytCardPayment.setVisibility(View.GONE);
                                lytCardPaymentTitleMsg.setVisibility(View.GONE);
                                btnEndjobtrip.setVisibility(View.VISIBLE);
                                btnGOTTHECASH.setVisibility(View.GONE);

                                break;
                            case "1":
                                lytCardPayment.setVisibility(View.VISIBLE);
                                lytCardPaymentTitleMsg.setVisibility(View.VISIBLE);

                                lytCardPaymentTitleMsg.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

                                txtCardPaymentTitle.setText(getResources().getString(R.string.user_paid_with_current_job));
                                txtCardPaymentMsg.setText(getResources().getString(R.string.click_to_finish_services));

                                btnCardPayment.setBackground(getResources().getDrawable(R.drawable.bg_rect_filled_colorprimary));
                                btnCardPayment.setTextColor(getResources().getColor(R.color.white));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    btnCardPayment.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                }
                                btnCardPayment.setEnabled(true);
                                btnEndjobtrip.setVisibility(View.INVISIBLE);

                                btnGOTTHECASH.setVisibility(View.INVISIBLE);

                                break;
                            case "2":
                                lytCardPayment.setVisibility(View.VISIBLE);
                                lytCardPaymentTitleMsg.setVisibility(View.VISIBLE);

                                lytCardPaymentTitleMsg.setBackgroundColor(getResources().getColor(R.color.holo_red_light));

                                txtCardPaymentTitle.setText(getResources().getString(R.string.wait_a_moment));
                                txtCardPaymentMsg.setText(getResources().getString(R.string.please_wait_user_can_able_to_pay_with_card_on_current_job));
                                btnCardPayment.setBackground(getResources().getDrawable(R.drawable.bg_rect_border_fill));
                                btnCardPayment.setTextColor(getResources().getColor(R.color.colorPrimary));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    btnCardPayment.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                }
                                btnCardPayment.setEnabled(false);
                                btnEndjobtrip.setVisibility(View.VISIBLE);

                                // btnGOTTHECASH.setVisibility(View.VISIBLE); Need Enable
                                btnGOTTHECASH.setVisibility(View.INVISIBLE);

                                break;
                            default:
                                lytCardPayment.setVisibility(View.GONE);
                                lytCardPaymentTitleMsg.setVisibility(View.GONE);
                                btnEndjobtrip.setVisibility(View.GONE);
                                btnGOTTHECASH.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        lytCardPayment.setVisibility(View.GONE);
                        btnEndjobtrip.setVisibility(View.VISIBLE);
                        btnGOTTHECASH.setVisibility(View.GONE);
                    }

                    startRepeatingTask();

                    getCustomerWalletAmount("5.1");

                    getDriverDistanceFB("5.1");

                    if (strDropLocationFB.equals("NoDrop")) {
                        strDistanceBegin = "totaldistancend";
                        MainActivity.p = 1;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    } else {
                        strDistanceBegin = "distancebegin";
                        MainActivity.p = 0;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 0);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    }


                    hideProgressDialog();

                    break;
                case "5.3": //PayPal

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Endtrip");
                    spEditTripState.commit();

                    callGeneralDetails(dataSnapshot);

                    AcceptDropLatLng = new LatLng(parseDouble(dataSnapshot.child("drop_lat").getValue().toString()), parseDouble(dataSnapshot.child("drop_lon").getValue().toString()));

                    callGeneralLytViews("5.3");

                    String strPayPalStatus = checkStringEmpty(dataSnapshot, "paypal_status");
                    if (!strPayPalStatus.equals("")) {
                        switch (strPayPalStatus) {
                            case "0":
                                lytCardPayment.setVisibility(View.GONE);
                                lytCardPaymentTitleMsg.setVisibility(View.GONE);
                                btnEndjobtrip.setVisibility(View.VISIBLE);
                                btnGOTTHECASH.setVisibility(View.GONE);

                                break;
                            case "1":
                                lytCardPayment.setVisibility(View.VISIBLE);
                                lytCardPaymentTitleMsg.setVisibility(View.VISIBLE);

                                lytCardPaymentTitleMsg.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

                                txtCardPaymentTitle.setText(getResources().getString(R.string.user_paid_with_current_job));
                                txtCardPaymentMsg.setText(getResources().getString(R.string.click_to_finish_services));

                                btnCardPayment.setBackground(getResources().getDrawable(R.drawable.bg_rect_filled_colorprimary));
                                btnCardPayment.setTextColor(getResources().getColor(R.color.white));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    btnCardPayment.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                }
                                btnCardPayment.setEnabled(true);
                                btnEndjobtrip.setVisibility(View.INVISIBLE);

                                btnGOTTHECASH.setVisibility(View.INVISIBLE);

                                break;
                            case "2":
                                lytCardPayment.setVisibility(View.VISIBLE);
                                lytCardPaymentTitleMsg.setVisibility(View.VISIBLE);

                                lytCardPaymentTitleMsg.setBackgroundColor(getResources().getColor(R.color.holo_red_light));

                                txtCardPaymentTitle.setText(getResources().getString(R.string.wait_a_moment));
                                txtCardPaymentMsg.setText(getResources().getString(R.string.please_wait_user_can_able_to_pay_with_card_on_current_job));

                                btnCardPayment.setBackground(getResources().getDrawable(R.drawable.bg_rect_border_fill));
                                btnCardPayment.setTextColor(getResources().getColor(R.color.colorPrimary));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    btnCardPayment.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                }
                                btnCardPayment.setEnabled(false);

                                btnEndjobtrip.setVisibility(View.VISIBLE);

                                // btnGOTTHECASH.setVisibility(View.VISIBLE); Need Enable
                                btnGOTTHECASH.setVisibility(View.INVISIBLE);

                                break;
                            default:
                                lytCardPayment.setVisibility(View.GONE);
                                lytCardPaymentTitleMsg.setVisibility(View.GONE);
                                btnEndjobtrip.setVisibility(View.GONE);
                                btnGOTTHECASH.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        lytCardPayment.setVisibility(View.GONE);
                        btnEndjobtrip.setVisibility(View.VISIBLE);
                        btnGOTTHECASH.setVisibility(View.GONE);
                    }

                    startRepeatingTask();

                    getCustomerWalletAmount("5.3");

                    getDriverDistanceFB("5.3");

                    if (strDropLocationFB.equals("NoDrop")) {
                        strDistanceBegin = "totaldistancend";
                        MainActivity.p = 1;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    } else {
                        strDistanceBegin = "distancebegin";
                        MainActivity.p = 0;
                        spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 0);
                        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                        spEditTripState.commit();
                    }


                    hideProgressDialog();

                    break;
                case "5.2": //CashPayment
                case "6":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Endtrip");
                    spEditTripState.commit();

                    lytOnlineOffline.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.VISIBLE);
                    lytCardPayment.setVisibility(View.GONE);
                    lytCardPaymentTitleMsg.setVisibility(View.GONE);

                    if (dataSnapshot.child("booking_id").getValue() != null) {
                        if (dataSnapshot.child("booking_id").getValue().toString() != null) {

                            callGeneralDetails(dataSnapshot);

                            strCashStatusFB = checkStringEmptyZero(dataSnapshot, "cash_status");



                            /*if (strPaymentTypeFB != null) {
                                if (!strPaymentTypeFB.equals("null")) {
                                    if (!strPaymentTypeFB.equals("")) {

                                        if (strPaymentTypeFB.equals("cash")) {
                                            switch (strCashStatusFB) {
                                                case "0":
                                                    txtCashStatusTripSummary.setVisibility(View.VISIBLE);
                                                    txtCashStatusTripSummary.setBackgroundColor(getResources().getColor(R.color.black));
                                                    txtCashStatusTripSummary.setText(getResources().getString(R.string.trip_summary_please_wait_provider_confirm_to_cash_payment));
                                                    lytCashPayment.setVisibility(View.VISIBLE);

                                                    callClickAction(txtCashStatusTripSummary, false);

                                                    callGoToHomeButtonAction(false);

                                                    break;
                                                case "1":
                                                    txtCashStatusTripSummary.setVisibility(View.VISIBLE);
                                                    txtCashStatusTripSummary.setBackgroundColor(getResources().getColor(R.color.holo_green_dark));
                                                    txtCashStatusTripSummary.setText(getResources().getString(R.string.trip_summary_cash_payment_received));
                                                    lytCashPayment.setVisibility(View.GONE);

                                                    callClickAction(txtCashStatusTripSummary, false);

                                                    callGoToHomeButtonAction(true);

                                                    break;
                                                case "2":
                                                    txtCashStatusTripSummary.setVisibility(View.VISIBLE);
                                                    txtCashStatusTripSummary.setBackgroundColor(getResources().getColor(R.color.holo_red_dark));
                                                    txtCashStatusTripSummary.setText(getResources().getString(R.string.trip_summary_please_collect_cash_payment_from_user_side));
                                                    lytCashPayment.setVisibility(View.GONE);

                                                    callClickAction(txtCashStatusTripSummary, true);

                                                    callGoToHomeButtonAction(false);

                                                    txtCashStatusTripSummary.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                                                            refs.child("booking_trips").child(strBookingId).child("cash_status").setValue("0");
                                                            refs.child("providers").child(strdriverIdFB).child("trips").child("cash_status").setValue("0");
                                                        }
                                                    });

                                                    break;
                                            }
                                        } else {
                                            callClickAction(txtCashStatusTripSummary, true);
                                            callGoToHomeButtonAction(true);
                                            lytCashPayment.setVisibility(View.GONE);
                                            txtCashStatusTripSummary.setVisibility(View.GONE);
                                        }

                                    } else {
                                        callGoToHomeButtonAction(true);
                                        lytCashPayment.setVisibility(View.GONE);
                                        txtCashStatusTripSummary.setVisibility(View.GONE);
                                    }
                                } else {
                                    callGoToHomeButtonAction(true);
                                    lytCashPayment.setVisibility(View.GONE);
                                    txtCashStatusTripSummary.setVisibility(View.GONE);
                                }
                            } else {
                                callGoToHomeButtonAction(true);
                                lytCashPayment.setVisibility(View.GONE);
                                txtCashStatusTripSummary.setVisibility(View.GONE);
                            }*/

                            txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                            Double totalAgentAmount = Double.parseDouble(strCount) * Double.parseDouble(strPriceperAgentFB);
                            txtAgentFeeLabel.setText("(" + strCount + " X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperAgentFB) + ") " + getResources().getString(R.string.guard_count_x_price_per_guard));
                            txtDayFeeLabel.setText("(" + strCount + " X " + strDays + "X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperDayFB) + ") " + getResources().getString(R.string.guard_count_x_number_of_days_x_price_per_day));

                           /*strBasePriceFB = Objects.requireNonNull(dataSnapshot.child("base_fare").getValue()).toString();
                            txtBaseCharge.setText(convertCurrencyPrice(MainActivity.this,strBasePriceFB));*/
                            Double totalDayAmount = Double.parseDouble(strCount) * Double.parseDouble(strDays) * Double.parseDouble(strPriceperDayFB);

                            txtAgentCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, totalAgentAmount.toString()));

                            txtDayCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, totalDayAmount.toString()));
                            //  txtDayFeeLabel.setText("("+strCount+" * "+strDays+"*"+strCurrencySymbol+" "+strPriceperDayFB+")");
                            getCustomerWalletAmount("6");

                            getDriverDistanceFB("6");

                            //countDownTimerDistanceDuration.cancel();
                            //countDownTimerDistanceDurationAccept.cancel();

                            strDistanceBegin = "totaldistancend";
                            spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                            spEditTripState.commit();
                            distance = 0;


                            if (strBackupVolunteerStatusFB.equals("0")) {

                                txtCashStatusTripSummary.setVisibility(View.VISIBLE);
                                txtCashStatusTripSummary.setBackgroundColor(getResources().getColor(R.color.black));
                                txtCashStatusTripSummary.setText(getResources().getString(R.string.please_wait_customer_accept_your_completion));
                                callGoToHomeButtonAction(false);

                                showTripSummaryDialog(dataSnapshot);

                            } else {

                                lytOnlineOffline.setVisibility(View.VISIBLE);
                                lytRequestReceive.setVisibility(View.GONE);
                                lytJobInprogress.setVisibility(View.GONE);
                                Toast.makeText(this, R.string.completed_your_job, Toast.LENGTH_SHORT).show();

                                removeRequestDetailsFirebase();

                                Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(mainActivityIntent);
                                finish();

                            }


                            /*End Trip*/
                            hideProgressDialog();

                            stopRepeatingTask();

                            out.println("drop location is " + strDropLocationFB);

                        } else {
                            strBookingId = "";
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("providers").child(driverId).child("trips").child("service_status").setValue("0");
                        }
                    } else {
                        strBookingId = "";
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("providers").child(driverId).child("trips").child("service_status").setValue("0");
                    }

                    break;
                case "7":
                    /*Payment Completed*/
                    hideProgressDialog();

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Endtrip");
                    spEditTripState.commit();

                    lytOnlineOffline.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.VISIBLE);
                    lytCardPayment.setVisibility(View.GONE);
                    lytCardPaymentTitleMsg.setVisibility(View.GONE);

                    if (dataSnapshot.child("booking_id").getValue() != null) {
                        if (dataSnapshot.child("booking_id").getValue().toString() != null) {

                            callGeneralDetails(dataSnapshot);

                            strCashStatusFB = checkStringEmptyZero(dataSnapshot, "cash_status");

                            callGoToHomeButtonAction(true);
                            txtCashStatusTripSummary.setVisibility(View.GONE);
                            lytCashPayment.setVisibility(View.GONE);

                            txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                            double totalAgentAmount = Double.parseDouble(strCount) * Double.parseDouble(strPriceperAgentFB);
                            txtAgentFeeLabel.setText("(" + strCount + " X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperAgentFB) + ") " + getResources().getString(R.string.guard_count_x_price_per_guard));
                            txtDayFeeLabel.setText("(" + strCount + " X " + strDays + "X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperDayFB) + ") " + getResources().getString(R.string.guard_count_x_number_of_days_x_price_per_day));

                           /* strBasePriceFB = Objects.requireNonNull(dataSnapshot.child("base_fare").getValue()).toString();
                            txtBaseCharge.setText(convertCurrencyPrice(MainActivity.this,strBasePriceFB));*/
                            double totalDayAmount = Double.parseDouble(strCount) * Double.parseDouble(strDays) * Double.parseDouble(strPriceperDayFB);

                            txtAgentCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, Double.toString(totalAgentAmount)));

                            txtDayCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, Double.toString(totalDayAmount)));
                            //  txtDayFeeLabel.setText("("+strCount+" * "+strDays+"*"+strCurrencySymbol+" "+strPriceperDayFB+")");
                            getCustomerWalletAmount("7");

                            getDriverDistanceFB("7");

                            showTripSummaryDialog(dataSnapshot);

                            //countDownTimerDistanceDuration.cancel();
                            //countDownTimerDistanceDurationAccept.cancel();

                            strDistanceBegin = "totaldistancend";
                            spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                            spEditTripState.commit();
                            distance = 0;

                            /*End Trip*/
                            hideProgressDialog();

                            stopRepeatingTask();

                        } else {
                            strBookingId = "";
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("providers").child(driverId).child("trips").child("service_status").setValue("0");
                        }
                    } else {
                        strBookingId = "";
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("providers").child(driverId).child("trips").child("service_status").setValue("0");
                    }
                    //countDownTimerDistanceDuration.cancel();
                    //countDownTimerDistanceDurationAccept.cancel();
                    break;
                case "8":

                    strDistanceBegin = "totaldistancend";

                    distance = 0;

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                    spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    lytOnlineOffline.setVisibility(View.VISIBLE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.map_status_user_cancel_request, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();

                    callStopGeoFencing();

                    removeRequestDetailsFirebase();

                    Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();

                    break;
                case "9":
                    strDistanceBegin = "totaldistancend";

                    distance = 0;
                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "");
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                    spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();

                    lytOnlineOffline.setVisibility(View.VISIBLE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.GONE);
                    hideProgressDialog();
                    removeRequestDetailsFirebase();
                    callStopGeoFencing();

                    break;
                case "10":

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);

                    lytOnlineOffline.setVisibility(View.GONE);
                    lytRequestReceive.setVisibility(View.GONE);
                    lytJobInprogress.setVisibility(View.VISIBLE);
                    lytCardPayment.setVisibility(View.GONE);
                    lytCardPaymentTitleMsg.setVisibility(View.GONE);

                    callGeneralDetails(dataSnapshot);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                    showTripSummaryDialog(dataSnapshot);

                    showUserBalanceAlertDialog();

                    strDistanceBegin = "totaldistancend";

                    //countDownTimerDistanceDuration.cancel();
                    //countDownTimerDistanceDurationAccept.cancel();

                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE, "Normalstate");
                    spEditTripState.putInt(Utils.SP_MAP_TRIPSTATE_DISTANCE_INT, 1);
                    spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
                    spEditTripState.commit();
                    distance = 0;

                    /*End Trip*/
                    hideProgressDialog();

                    stopRepeatingTask();

                    break;
                default:

                    strBookingId = checkStringEmpty(dataSnapshot, "booking_id");
                    spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, strBookingId);

                    getCustomerWalletAmount("0");

                    callClearStage(driverId);
                    callClearFirebase("0", driverId);

                    hideProgressDialog();

                    callStopGeoFencing();

                    break;
            }
        } else {
            hideProgressDialog();
            spEditTripState.putString(Utils.SP_CURRENT_BOOKING_ID, "").apply();
        }
    }


    @SuppressLint("SetTextI18n")
    private void callGeneralDetails(DataSnapshot dataSnapshot) {


        strBookingId = checkStringEmpty(dataSnapshot, "booking_id");

        strMainCategoryid = checkStringEmpty(dataSnapshot, "category");
        strSubCategoryid = checkStringEmpty(dataSnapshot, "service_id");

        strCancelServiceStatusFB = checkStringEmpty(dataSnapshot, "cancel_service_status");

        strAcceptTimeFB = checkStringEmpty(dataSnapshot, "accept_time");
        strStartTimeFB = checkStringEmpty(dataSnapshot, "start_time");
        strArriveTimeFB = checkStringEmpty(dataSnapshot, "reach_time");
        strBeginJobTimeFB = checkStringEmpty(dataSnapshot, "service_start_time");


        strCreatedIdFB = checkStringEmpty(dataSnapshot, "created_at");
        strUpdatedFB = checkStringEmpty(dataSnapshot, "updated_at");

        struserIdFB = checkStringEmpty(dataSnapshot, "customer_id");
        struserEmailFB = checkStringEmpty(dataSnapshot, "customer_email");
        struserFnameFB = checkStringEmpty(dataSnapshot, "customer_first_name");
        struserLnameFB = checkStringEmpty(dataSnapshot, "customer_last_name");
        struserMobileFB = checkStringEmpty(dataSnapshot, "customer_phone_number");
        strUserProfPicFB = checkStringEmpty(dataSnapshot, "customer_avatar");

        strPickupLocationFB = checkStringEmpty(dataSnapshot, "customer_location");
        strPickupLatFB = checkStringEmpty(dataSnapshot, "customer_lat");
        strPickupLngFB = checkStringEmpty(dataSnapshot, "customer_lon");

        strDropLocationFB = checkStringEmpty(dataSnapshot, "drop_location");

        strdriverIdFB = checkStringEmpty(dataSnapshot, "driver_id");
        strdriverEmailFB = checkStringEmpty(dataSnapshot, "driver_email");
        strdriverFnameFB = checkStringEmpty(dataSnapshot, "driver_first_name");
        strdriverLnameFB = checkStringEmpty(dataSnapshot, "driver_last_name");
        strdriverMobileFB = checkStringEmpty(dataSnapshot, "driver_phone_number");
        strDriverProfPicFB = checkStringEmpty(dataSnapshot, "driver_avatar");

        strJobDateFB = checkStringEmpty(dataSnapshot, "job_date");
        strJobNumberFB = checkStringEmpty(dataSnapshot, "job_number");
        strModeFB = checkStringEmpty(dataSnapshot, "mode");

        strPaymentTypeFB = checkStringEmpty(dataSnapshot, "payment_type");
        out.println("payment type is " + strPaymentTypeFB);
        strSiteCommissionFB = checkStringEmpty(dataSnapshot, "site_commission");

        strStripIdFB = checkStringEmpty(dataSnapshot, "strip_id");
        strPromoCodeFB = checkStringEmpty(dataSnapshot, "promocode");
        strPromoCodeValueFB = checkStringEmpty(dataSnapshot, "promo_value");

        strMainCategoryidAr = checkStringEmpty(dataSnapshot, "category_ar");
        if (strMainCategoryidAr.equals("")) {
            strMainCategoryidAr = strMainCategoryname;
        }

        //strTotalAmountFB = checkStringEmptyZero(dataSnapshot, "total_amount");
        strTotalAmountFB = checkStringEmptyZero(dataSnapshot, "job_amount_per_guard");

        strUnitPriceFB = checkStringEmptyZero(dataSnapshot, "unit_price");
        strVisitPriceFB = checkStringEmptyZero(dataSnapshot, "visit_fare");
        strMainUnitPriceFB = checkStringEmptyZero(dataSnapshot, "level_amount");
        strLevelTypeFB = checkStringEmptyZero(dataSnapshot, "level_type");

        strPricePerKMFB = checkStringEmptyZero(dataSnapshot, "price_per_km");
        strAmountTypeFB = checkStringEmptyZero(dataSnapshot, "amount_type");
        strSubLevelAmountFB = checkStringEmptyZero(dataSnapshot, "sub_level_amount");
        strTotalDistanceFB = checkStringEmptyZero(dataSnapshot, "total_distance");

        strMaterialChargeFB = checkStringEmptyDecimalZero(dataSnapshot, "extra_charge");
        strMiscChargeFB = checkStringEmptyDecimalZero(dataSnapshot, "misc_charge");
        strDriverDiscountFB = checkStringEmptyDecimalZero(dataSnapshot, "driver_discount");

        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, strPickupLatFB);
        putStateFBCategory.putString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, strPickupLngFB);
        putStateFBCategory.apply();

        strPriceperAgentFB = Objects.requireNonNull(dataSnapshot.child("price_per_agent").getValue()).toString();
        strPriceperDayFB = Objects.requireNonNull(dataSnapshot.child("price_per_day").getValue()).toString();

        if (dataSnapshot.child("customer_booking_id").getValue() != null) {
            strCustomerBookingIdFB = Objects.requireNonNull(dataSnapshot.child("customer_booking_id").getValue()).toString();
        } else {
            strCustomerBookingIdFB = "0";
        }

        if (dataSnapshot.child("total_agent").getValue() != null && !dataSnapshot.child("total_agent").getValue().toString().equals("")) {
            strCount = Objects.requireNonNull(dataSnapshot.child("total_agent").getValue()).toString();
            txtCount.setText(strCount);
        } else {
            strCount = "";
        }

        if (dataSnapshot.child("total_days").getValue() != null && !dataSnapshot.child("total_days").getValue().toString().equals("")) {
            strDays = Objects.requireNonNull(dataSnapshot.child("total_days").getValue()).toString();
            txtDays.setText(strDays);
        } else {
            strDays = "";
        }

        if (dataSnapshot.child("total_agent").getValue() != null && !dataSnapshot.child("total_agent").getValue().toString().equals("")) {
            strCount = Objects.requireNonNull(dataSnapshot.child("total_agent").getValue()).toString();
            txtCountJobinprogress.setText(getResources().getText(R.string.no_of_guards) + " :- " + strCount);
        } else {
            strCount = "";
        }

        if (dataSnapshot.child("total_days").getValue() != null && !dataSnapshot.child("total_days").getValue().toString().equals("")) {
            strDays = Objects.requireNonNull(dataSnapshot.child("total_days").getValue()).toString();
            txtDaysJobinprogress.setText("No of Days :- " + strDays);
        } else {
            strDays = "";
        }

        if (dataSnapshot.child("company_name").getValue() != null) {
            strCompanyName = Objects.requireNonNull(dataSnapshot.child("company_name").getValue()).toString();
        } else {
            strCompanyName = "Individual";
        }

        strSubscriptionStatusFB = checkStringEmptyZero(dataSnapshot, "subscription_status");

        strTotalHrsFB = checkStringEmptyZero(dataSnapshot, "total_hrs");
        stPricePerHrsFB = checkStringEmptyZero(dataSnapshot, "price_per_hr");
        strVatAmountsFB = checkStringEmptyZero(dataSnapshot, "vat_amount");

        strExtendedHrsFB = checkStringEmptyZero(dataSnapshot, "extended_hrs");
        strExtendedAmountFB = checkStringEmptyZero(dataSnapshot, "extended_amount");
        strExtendedVatFB = checkStringEmptyZero(dataSnapshot, "extended_vat");
        strExtendedStatusFB = checkStringEmptyZero(dataSnapshot, "extended_status");

        strDeducationAmountFB = checkStringEmptyZero(dataSnapshot, "deducation_amount");
        strDeducationStatusFB = checkStringEmptyZero(dataSnapshot, "deducation_status");
        strBonusAmountFB = checkStringEmptyZero(dataSnapshot, "bonus_amount");
        strBonusStatusFB = checkStringEmptyZero(dataSnapshot, "bonus_status");

        strBackupVolunteerStatusFB = checkStringEmptyZero(dataSnapshot, "volunteer_status");
        strTransferJobStatusFB = checkStringEmptyZero(dataSnapshot, "transfer_status");

        if (strBackupVolunteerStatusFB.equals("1")) {
            strBookingId = checkStringEmpty(dataSnapshot, "booking_id");
            strRequestedDriverIdFB = checkStringEmpty(dataSnapshot, "requested_driver_id");
            strRequestedDriverNameFB = checkStringEmpty(dataSnapshot, "requested_driver_name");
            strRequestedDriverAvatarFB = checkStringEmpty(dataSnapshot, "requested_driver_avatar");
        }

        strTotalAmountGuardFB = checkStringEmptyZero(dataSnapshot, "total_amount_guard");
        strGuardPercentageFB = checkStringEmptyZero(dataSnapshot, "guard_percentage");
        strGuardPercentageAmountFB = checkStringEmptyZero(dataSnapshot, "guard_percentage_amount");
        strGuardAmountFB = checkStringEmptyZero(dataSnapshot, "guard_amount");

        if (dataSnapshot.child("volunteers").getValue() != null) {
            try {
                viewTimelineFBStatus(dataSnapshot);

            } catch (Exception e) {
                e.printStackTrace();
                out.println("~~~~volunteers~~~" + e);
            }
        } else {
            setVisibility(btnSOSBackupRequiredJobsInProgress, false);
            // setVisibility(btnSOSBackupRequiredJobsInProgress, true);
            setVisibility(rlBackupGuardListParentLytJobsInProgress, false);
        }

    }

    @SuppressLint("SetTextI18n")
    private void callGeneralLytViews(String key) {

        try {

            if (strUserProfPicFB != null) {
                ivCallImage.setImageURI(Utils.BASE_URL_IMAGE_UPLOAD + strUserProfPicFB);
                int color = getResources().getColor(R.color.colorPrimary);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(color, 1.0f);
                roundingParams.setRoundAsCircle(true);
                ivCallImage.getHierarchy().setRoundingParams(roundingParams);
            }

            txtCallName.setText(struserFnameFB + " " + struserLnameFB);


            if (strUserProfPicFB != null) {
                ivuserProfpicJobinprogress.setImageURI(Utils.BASE_URL_IMAGE_UPLOAD + strUserProfPicFB);
                int color = getResources().getColor(R.color.colorPrimary);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(color, 1.0f);
                roundingParams.setRoundAsCircle(true);
                ivuserProfpicJobinprogress.getHierarchy().setRoundingParams(roundingParams);
            }

            txtPickupJobinprogress.setText(strPickupLocationFB);

            txtUsernameJobinprogress.setText(struserFnameFB);

            double totalAgentAmount = parseDouble(strCount) * parseDouble(strPriceperAgentFB);
            txtAgentCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, String.valueOf(totalAgentAmount)));
            txtAgentFeeLabel.setText("(" + strCount + " *" + strCurrencySymbol + " " + strPriceperAgentFB + ")");
            double totalDayAmount = parseDouble(strCount) * parseDouble(strDays) * parseDouble(strPriceperDayFB);
            txtDayCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, String.valueOf(totalDayAmount)));

            txtAgentFeeLabel.setText("(" + strCount + " X" + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperAgentFB) + ") " + getResources().getString(R.string.guard_count_x_price_per_guard));
            txtDayFeeLabel.setText("(" + strCount + " * " + strDays + "*" + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperDayFB) + ") " + getResources().getString(R.string.guard_count_x_number_of_days_x_price_per_day));

            txtBookingIdCommon.setText(strBookingId);
            txtTitleJobinprogres.setText(getResources().getString(R.string.booking_id) + "# " + strBookingId);

            txtUsernameJobinprogress.setText(struserFnameFB + " " + struserLnameFB);
            txtPickupJobinprogress.setText(strPickupLocationFB);
            txtCompanyJobinprogress.setText(strCompanyName);

            if (strExtendedStatusFB.equals("1")) {
                setVisibility(lytCardPayment, false);
                setVisibility(llExtendJobsInProgressDetailsLyt, true);
                if (!strExtendedHrsFB.equals("0")) {
                    txtExtendHrsJobsInProgress.setText(convertHoursToDaysHoursMinutes(strExtendedHrsFB));
                } else {
                    txtExtendHrsJobsInProgress.setText("-");
                }
                if (!strExtendedAmountFB.equals("0")) {
                    txtExtendHrsAmountJobsInProgress.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strExtendedAmountFB));
                } else {
                    txtExtendHrsAmountJobsInProgress.setText("-");
                }
                if (!strExtendedVatFB.equals("0")) {
                    txtExtendHrsVATAmountJobsInProgress.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strExtendedVatFB));
                } else {
                    txtExtendHrsVATAmountJobsInProgress.setText("-");
                }
            } else {
                setVisibility(llExtendJobsInProgressDetailsLyt, false);
            }

            if (strBackupVolunteerStatusFB.equals("1")) {
                setVisibility(rlUserDetailsLytJobinprogress, false);
                setVisibility(rlBackupRequestedGuardDetailsParentLytJobsInProgress, true);
                if (llUserCallMessageJobinprogress.getVisibility() == View.VISIBLE) {
                    llUserCallMessageJobinprogress.setVisibility(View.INVISIBLE);
                }
                setVisibility(btnSOSBackupRequiredJobsInProgress, false);
                setVisibility(rlBackupGuardListParentLytJobsInProgress, false);

                txtBackupRequestedGuardNameJIP.setText(strRequestedDriverNameFB.replaceAll(":", " "));
                ivBackupRequestedGuardProfPicJIP.setImageURI(Utils.BASE_URL + strRequestedDriverAvatarFB);
            } else {
                setVisibility(rlUserDetailsLytJobinprogress, true);
                setVisibility(rlBackupRequestedGuardDetailsParentLytJobsInProgress, false);
                setVisibility(llUserCallMessageJobinprogress, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (key) {
            case "0":
            case "1":
                break;
            case "2":

                lytOnlineOffline.setVisibility(View.GONE);
                lytRequestReceive.setVisibility(View.GONE);
                lytJobInprogress.setVisibility(View.VISIBLE);
                rlMapJobinprogress.setVisibility(View.VISIBLE);
                rlJobinprogress.setVisibility(View.GONE);
                lytCardPayment.setVisibility(View.GONE);
                lytCardPaymentTitleMsg.setVisibility(View.GONE);

                //btnCancelJobsInProgress.setVisibility(View.VISIBLE);
                btnCancelJobsInProgress.setVisibility(View.GONE);

                llTripviewone.setVisibility(View.VISIBLE);
                llTripviewtwo.setVisibility(View.GONE);
                llTripviewthree.setVisibility(View.GONE);
                llTripviewfour.setVisibility(View.GONE);

                btnStarttrip.setVisibility(View.VISIBLE);
                btnArrivetrip.setVisibility(View.GONE);
                btnBeginjobtrip.setVisibility(View.GONE);
                btnEndjobtrip.setVisibility(View.GONE);

                txtAccepttimeone.setText(strAcceptTimeFB);

                if (strLanguageShortName.equals("en")) {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                } else {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                }

                if (!strDropLocationFB.equals("NoDrop")) {
                    //countDownTimerDistanceDurationAccept.start();
                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);
                    txtDroplocMapjobinprogress.setText(getResources().getString(R.string.drop_off_location) + ":- " + strDropLocationFB);
                } else {
                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                }


                if (strPickupLatFB != null && strPickupLngFB != null) {
                    AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                    AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                    out.println("sdsdfs" + AcceptPickupLat + "long" + AcceptPickupLng);
                    AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
                    out.println("sdsdfs AcceptPickupLatLng" + AcceptPickupLatLng);
                    startRepeatingTask();
                } else {
                    AcceptPickupLat = String.valueOf(0.0);
                    AcceptPickupLng = String.valueOf(0.0);
                    AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
                    startRepeatingTask();
                }

                break;
            case "3":

                lytOnlineOffline.setVisibility(View.GONE);
                lytRequestReceive.setVisibility(View.GONE);
                lytJobInprogress.setVisibility(View.VISIBLE);
                rlMapJobinprogress.setVisibility(View.VISIBLE);
                rlJobinprogress.setVisibility(View.GONE);

                btnCancelJobsInProgress.setVisibility(View.GONE);
                //btnCancelJobsInProgress.setVisibility(View.VISIBLE);

                llTripviewone.setVisibility(View.VISIBLE);
                llTripviewtwo.setVisibility(View.VISIBLE);
                llTripviewthree.setVisibility(View.GONE);
                llTripviewfour.setVisibility(View.GONE);

                btnStarttrip.setVisibility(View.GONE);
                btnArrivetrip.setVisibility(View.VISIBLE);
                btnBeginjobtrip.setVisibility(View.GONE);
                btnEndjobtrip.setVisibility(View.GONE);


                rlUserCancelRetryTop.setVisibility(View.GONE);
                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);

                txtAccepttimeone.setText(strAcceptTimeFB);
                txtAccepttimetwo.setText(strStartTimeFB);

                lytCardPayment.setVisibility(View.GONE);
                lytCardPaymentTitleMsg.setVisibility(View.GONE);

                if (strLanguageShortName.equals("en")) {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                } else {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                }


                if (!strDropLocationFB.equals("NoDrop")) {
                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);
                    txtDroplocMapjobinprogress.setText(getResources().getString(R.string.drop_off_location) + ":- " + strDropLocationFB);
                } else {
                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                }

                AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));

                if (AcceptPickupLat != null && AcceptPickupLng != null) {
                    AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                    AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                    AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
                    startRepeatingTask();
                } else {
                    AcceptPickupLat = String.valueOf(0.0);
                    AcceptPickupLng = String.valueOf(0.0);
                    AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));
                    startRepeatingTask();
                }

                break;
            case "4":


                lytOnlineOffline.setVisibility(View.GONE);
                lytRequestReceive.setVisibility(View.GONE);
                lytJobInprogress.setVisibility(View.VISIBLE);

                lytCardPayment.setVisibility(View.GONE);
                lytCardPaymentTitleMsg.setVisibility(View.GONE);

                switchLiveTrackJobinProgress.setCheckedTogglePosition(1);
                rlMapJobinprogress.setVisibility(View.GONE);
                rlJobinprogress.setVisibility(View.VISIBLE);

                btnCancelJobsInProgress.setVisibility(View.GONE);
                //btnCancelJobsInProgress.setVisibility(View.VISIBLE);

                llTripviewone.setVisibility(View.VISIBLE);
                llTripviewtwo.setVisibility(View.VISIBLE);
                llTripviewthree.setVisibility(View.VISIBLE);
                llTripviewfour.setVisibility(View.GONE);

                txtAccepttimeone.setText(strAcceptTimeFB);
                txtAccepttimetwo.setText(strStartTimeFB);
                txtAccepttimethree.setText(strArriveTimeFB);

                if (!strDropLocationFB.equals("NoDrop")) {
                    llNavigationBtnheader.setVisibility(View.GONE);
                    txtDroplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);
                    txtDroplocMapjobinprogress.setText(getResources().getString(R.string.drop_off_location) + ":- " + strDropLocationFB);
                } else {
                    llNavigationBtnheader.setVisibility(View.GONE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                }

                if (strLanguageShortName.equals("en")) {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                } else {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                }

                btnStarttrip.setVisibility(View.GONE);
                btnArrivetrip.setVisibility(View.GONE);
                btnBeginjobtrip.setVisibility(View.VISIBLE);
                btnEndjobtrip.setVisibility(View.GONE);

                //btnBeginjobtrip.setEnabled(false);
                //rlUserCancelRetryTop.setVisibility(View.VISIBLE);
                rlUserCancelRetryTop.setVisibility(View.GONE);

                break;
            case "5":

                lytCardPayment.setVisibility(View.GONE);
                lytCardPaymentTitleMsg.setVisibility(View.GONE);

                rlUserCancelRetryTop.setVisibility(View.GONE);
                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);

                lytOnlineOffline.setVisibility(View.GONE);
                lytRequestReceive.setVisibility(View.GONE);
                lytJobInprogress.setVisibility(View.VISIBLE);

                rlMapJobinprogress.setVisibility(View.GONE);
                rlJobinprogress.setVisibility(View.VISIBLE);

                llTripviewone.setVisibility(View.VISIBLE);
                llTripviewtwo.setVisibility(View.VISIBLE);
                llTripviewthree.setVisibility(View.VISIBLE);
                llTripviewfour.setVisibility(View.VISIBLE);

                btnCancelJobsInProgress.setVisibility(View.GONE);

                btnStarttrip.setVisibility(View.GONE);
                btnArrivetrip.setVisibility(View.GONE);
                btnBeginjobtrip.setVisibility(View.GONE);
                btnEndjobtrip.setVisibility(View.VISIBLE);

                txtAccepttimeone.setText(strAcceptTimeFB);
                txtAccepttimetwo.setText(strStartTimeFB);
                txtAccepttimethree.setText(strArriveTimeFB);
                txtAccepttimefour.setText(strBeginJobTimeFB);

                if (strLanguageShortName.equals("en")) {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                    txtAcceptfour.setText(getString(R.string.map_status_driver_start_job));
                } else {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                    txtAcceptfour.setText(getString(R.string.map_status_driver_start_job));
                }

                if (!strDropLocationFB.equals("NoDrop")) {
                    //countDownTimerDistanceDurationAccept.cancel();
                    //countDownTimerDistanceDuration.start();

                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setVisibility(View.GONE);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);
                    txtDroplocMapjobinprogress.setText(getResources().getString(R.string.drop_off_location) + ":- " + strDropLocationFB);
                } else {
                    llNavigationBtnheader.setVisibility(View.GONE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                }

                break;
            case "5.1":

                rlUserCancelRetryTop.setVisibility(View.GONE);
                rlUserCancelRetryCancelLyt.setVisibility(View.GONE);

                lytOnlineOffline.setVisibility(View.GONE);
                lytRequestReceive.setVisibility(View.GONE);
                lytJobInprogress.setVisibility(View.VISIBLE);

                rlMapJobinprogress.setVisibility(View.GONE);
                rlJobinprogress.setVisibility(View.VISIBLE);

                llTripviewone.setVisibility(View.VISIBLE);
                llTripviewtwo.setVisibility(View.VISIBLE);
                llTripviewthree.setVisibility(View.VISIBLE);
                llTripviewfour.setVisibility(View.VISIBLE);

                btnCancelJobsInProgress.setVisibility(View.GONE);
                btnStarttrip.setVisibility(View.GONE);
                btnArrivetrip.setVisibility(View.GONE);
                btnBeginjobtrip.setVisibility(View.GONE);

                txtAccepttimeone.setText(strAcceptTimeFB);
                txtAccepttimetwo.setText(strStartTimeFB);
                txtAccepttimethree.setText(strArriveTimeFB);
                txtAccepttimefour.setText(strBeginJobTimeFB);

                if (strLanguageShortName.equals("en")) {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                    txtAcceptfour.setText(getString(R.string.map_status_driver_start_job));
                } else {
                    txtAcceptone.setText(getString(R.string.map_status_driver_accept_request));
                    txtAccepttwo.setText(getString(R.string.map_status_driver_start_loc));
                    txtAcceptthree.setText(getString(R.string.map_status_driver_arrive));
                    txtAcceptfour.setText(getString(R.string.map_status_driver_start_job));
                }

                if (!strDropLocationFB.equals("NoDrop")) {
                    //countDownTimerDistanceDurationAccept.cancel();
                    //countDownTimerDistanceDuration.start();

                    llNavigationBtnheader.setVisibility(View.VISIBLE);
                    txtDroplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setVisibility(View.GONE);

                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);
                    txtDroplocMapjobinprogress.setText(getResources().getString(R.string.drop_off_location) + ":- " + strDropLocationFB);
                } else {
                    llNavigationBtnheader.setVisibility(View.GONE);
                    txtDroplocMapjobinprogress.setVisibility(View.GONE);
                    txtPickuplocMapjobinprogress.setVisibility(View.VISIBLE);
                    txtPickuplocMapjobinprogress.setText(getResources().getString(R.string.job_location) + ":- " + strPickupLocationFB);

                }
                break;
            case "5.2":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
                break;
        }

    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void callGoToHomeButtonAction(boolean b) {
        try {
            //9B9B9B
            if (b) {
                btnCollectPaymentTripSummary.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            } else {
                btnCollectPaymentTripSummary.setBackgroundTintList(getResources().getColorStateList(R.color.gray_black));
            }
            btnCollectPaymentTripSummary.setEnabled(b);
            btnCollectPaymentTripSummary.setClickable(b);
            btnCollectPaymentTripSummary.setFocusable(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUserBalanceAlertDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        Typeface tf = Typeface.createFromAsset(Objects.requireNonNull(MainActivity.this).getAssets(), getString(R.string.App_Font));
        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.dialog_balance_amount_content_msg));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStrings = new SpannableString(getString(R.string.ok));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(spannableString).setPositiveButton(spannableStrings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                           /* DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
                            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(userId).child(Utils.FIREBASE_PROVIDER_PRO_PIC).setValue(profilepic);*/

            }
        }).show();


    }

    @SuppressLint("SetTextI18n")
    private void showTripSummaryDialog(DataSnapshot dataSnapshot) {

        if (dataSnapshot.child("rating_status").getValue() != null) {
            String strRatingStatusFB = Objects.requireNonNull(dataSnapshot.child("rating_status").getValue()).toString();
            Log.e(TAG, "strRatingStatusFB~~: " + strRatingStatusFB);
            if (strRatingStatusFB != null) {
                if (strRatingStatusFB.equalsIgnoreCase("0")) {
                    cvRating.setVisibility(View.VISIBLE);
                    ratingbarUserTripsummary.setRating(0);
                    edtCommentTripsummary.setText("");
                } else if (strRatingStatusFB.equalsIgnoreCase("1")) {
                    cvRating.setVisibility(View.GONE);
                    ratingbarUserTripsummary.setRating(0);
                    edtCommentTripsummary.setText("");
                } else {
                    cvRating.setVisibility(View.VISIBLE);
                    ratingbarUserTripsummary.setRating(0);
                    edtCommentTripsummary.setText("");
                }
            } else cvRating.setVisibility(View.GONE);
        } else {
            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
            refs.child("providers").child(strdriverIdFB).child("trips").child("rating_status").setValue("0");
        }

        if (strPromoCodeFB.equals("")) {
            txPromoCodeTripSummary.setVisibility(View.GONE);
        } else {
            txPromoCodeTripSummary.setVisibility(View.VISIBLE);
            txPromoCodeTripSummary.setText(getResources().getString(R.string.promo_code_apply) + " : " + strPromoCodeFB + " " + strPromoCodeValueFB + "%");
        }

        txtJobDateTripSummary.setText(strJobDateFB);
        txtCategoryTripSummary.setText(strMainCategoryid);

        switch (strPaymentTypeFB) {
            case "stripe_invoice":
            case "stripe":
                txtPaymentTypeTripSummary.setText("Stripe");
                break;
            case "stripe_gpay":
                txtPaymentTypeTripSummary.setText("GPay");
                break;
            case "apple":
                txtPaymentTypeTripSummary.setText("Apple");
                break;
            case "wallet":
                txtPaymentTypeTripSummary.setText("Wallet");
                break;
            case "paypal":
                txtPaymentTypeTripSummary.setText("Paypal");
                break;
        }

        if (strPickupLocationFB != null) {
            txtPickupLocTripSummary.setText(strPickupLocationFB);
        }

        if (!strDropLocationFB.equals("NoDrop")) {
            txtDropLocTripSummary.setText(strDropLocationFB);

            if (strTotalDistanceFB != null) {

                if (!strTotalDistanceFB.equals("0")) {

                    if (strAmountTypeFB.equals("3")) {

                        double fare = Double.parseDouble(strPricePerKMFB) * Double.parseDouble(strTotalDistanceFB);

                        if (fare > 0.0) {
                            llDistanceLytTripSummary.setVisibility(View.VISIBLE);
                        } else {
                            llDistanceLytTripSummary.setVisibility(View.GONE);
                        }

                        if (convertDistanceUnit(MainActivity.this).equals("mile")) {
                            txtDistanceLabel.setText("( " + getResources().getString(R.string.additional_charge_price_per_mile) + " " + convertCurrencyPrice(MainActivity.this, strPricePerKMFB) + " * " + strTotalDistanceFB + " Mile )");
                            txtDistance.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));
                        } else if (convertDistanceUnit(MainActivity.this).equals("km")) {
                            txtDistanceLabel.setText("( " + getResources().getString(R.string.additional_charge_price_per_km) + " " + convertCurrencyPrice(MainActivity.this, strPricePerKMFB) + " * " + strTotalDistanceFB + " KM )");
                            txtDistance.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));
                        }

                    } else {
                        llDistanceLytTripSummary.setVisibility(View.GONE);
                    }
                }
            }

        } else {
            llDistanceLytTripSummary.setVisibility(View.GONE);
            txtDropLocTripSummary.setVisibility(View.GONE);
            txtDropLocTripSummaryLabel.setVisibility(View.GONE);
        }

        if (strMaterialChargeFB.equals("0.0")) {
            llMaterialFeelyt.setVisibility(View.GONE);
        } else {
            llMaterialFeelyt.setVisibility(View.VISIBLE);
            txtMaterialFeeTripSummary.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strMaterialChargeFB));
        }

        if (strMiscChargeFB.equals("0.0")) {
            llMiscFeelyt.setVisibility(View.GONE);
        } else {
            llMiscFeelyt.setVisibility(View.VISIBLE);
            txtMiscFeeTripSummary.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strMiscChargeFB));
        }

        if (strDriverDiscountFB.equals("0.0")) {
            llDiscountlyt.setVisibility(View.GONE);
        } else {
            llDiscountlyt.setVisibility(View.VISIBLE);
            txtDiscountDriverTripSummary.setText("-" + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strDriverDiscountFB));
            txtDiscountTripSummary.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strDriverDiscountFB));
        }

        if (strTotalAmountFB != null && !strTotalAmountFB.equals("null")) {
            txtFinalTotalHeaderTripSummary.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strTotalAmountFB));
            txtFinalTotalTripSummary.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strTotalAmountFB));
        }

        if (!strVatAmountsFB.equals("0")) {
            txtVatAmountTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(MainActivity.this, strVatAmountsFB));
        } else {
            txtVatAmountTripSummary.setText("");
        }

        if (!strGuardPercentageAmountFB.equals("0")) {
            txtCommissionAmountTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(MainActivity.this, strGuardPercentageAmountFB));
        } else {
            txtCommissionAmountTripSummary.setText("");
        }

        if (!strTotalAmountFB.equals("0")) {
            txtTotalTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(MainActivity.this, strTotalAmountFB));
        } else {
            txtTotalTripSummary.setText("");
        }

        if (!strTotalHrsFB.equals("0")) {
            // txtNoofHrsTripSummary.setText(strTotalHrsFB + " " + getResources().getString(R.string.hrs));
            txtNoofHrsTripSummary.setText(convertHoursToDaysHoursMinutes(strTotalHrsFB));
        } else {
            txtNoofHrsTripSummary.setText("");
        }

        if (strExtendedStatusFB.equals("1")) {
            setVisibility(trExtendNoofHrsTripSummary, true);
            if (!strExtendedHrsFB.equals("0")) {
                txtExtendNoofHrsTripSummary.setText(convertHoursToDaysHoursMinutes(strExtendedHrsFB));
            } else {
                txtExtendNoofHrsTripSummary.setText("-");
            }
        } else {
            setVisibility(trExtendNoofHrsTripSummary, false);
        }

        if (!strTotalHrsFB.equals("0") && !stPricePerHrsFB.equals("0")) {

            String hrs_;
            if (strExtendedStatusFB.equals("1")) {
                hrs_ = String.valueOf(Double.parseDouble(strTotalHrsFB) + Double.parseDouble(strExtendedHrsFB));
            } else {
                hrs_ = strTotalHrsFB;
            }

            String label = "( " + getResources().getString(R.string.price_per_hrs) + " = " + strCurrencySymbol + "" + convertCurrencyPrice(MainActivity.this, stPricePerHrsFB) + " * " + hrs_ + " " + getResources().getString(R.string.hrs) + " )";
            txtPricePerHrsLabelTripSummary.setText(label);

            double fare = Double.parseDouble(stPricePerHrsFB) * Double.parseDouble(hrs_);
            if (fare <= 0.0) {
                fare = 0.0;
            }
            txtPricePerHrsTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));
        } else {
            txtPricePerHrsLabelTripSummary.setText(getResources().getString(R.string.price_per_hrs));
            txtPricePerHrsTripSummary.setText("");
        }

        if (strSubscriptionStatusFB.equals("1")) {
            //  setVisibility(txtSubscribtionStatusTripSummary, true);
            setVisibility(txtSubscribtionStatusTripSummary, false);
        } else {
            setVisibility(txtSubscribtionStatusTripSummary, false);
        }

        if (!strDeducationStatusFB.equals("0")) {
            if (!strDeducationAmountFB.equals("0")) {
                setVisibility(trDeductionAmountLytTripSummary, true);
                txtDeductionAmountTripSummary.setText(strCurrencySymbol + " " +
                        convertCurrencyPrice(MainActivity.this, strDeducationAmountFB));
            } else {
                setVisibility(trDeductionAmountLytTripSummary, false);
            }
        } else {
            setVisibility(trDeductionAmountLytTripSummary, false);
        }

        if (!strBonusStatusFB.equals("0")) {
            if (!strBonusAmountFB.equals("0")) {
                setVisibility(trBonusAmountLytTripSummary, true);
                txtBonusAmountTripSummary.setText(strCurrencySymbol + " " +
                        convertCurrencyPrice(MainActivity.this, strBonusAmountFB));
            } else {
                setVisibility(trBonusAmountLytTripSummary, false);
            }
        } else {
            setVisibility(trBonusAmountLytTripSummary, false);
        }

        if (dialogTripSummary.getVisibility() == View.GONE) {
            dialogTripSummary.setVisibility(View.VISIBLE);
        }

    }

    Boolean aBoolean = true;

    private void getDriverDistanceFB(String key) {

        if (driverId != null) {
            if (driverDistanceDatabaseBRef != null) {
                driverDistanceDatabaseBRef.removeEventListener(driverDistanceValueEventListener);
            }
            driverDistanceDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
            driverDistanceValueEventListener = driverDistanceDatabaseBRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("distance").getValue() != null) {
                        strDistanceFB_EndJob = Objects.requireNonNull(dataSnapshot.child("distance").getValue()).toString();
                    } else {
                        strDistanceFB_EndJob = "0";
                    }

                    if (key.equals("endTrip")) {
                        // callEndJobServices();
                        //callSurgeStatusOnTrip();
                        if (aBoolean) {
                            callEndJobServices();
                            aBoolean = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ~~~~~~" + databaseError.toString());
                    if (key.equals("endTrip")) {
                        //callEndJobServices();
                        //callSurgeStatusOnTrip();
                        if (aBoolean) {
                            callEndJobServices();
                            aBoolean = false;
                        }
                    }
                }
            });
        } else {
            try {
                Log.e(TAG, "getDriverOnlineOrOffline: " + "exe");
            } catch (Exception e) {
                Log.e(TAG, "getDriverOnlineOrOffline: ", e);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void callEndJobServices() {

        getCustomerWalletAmount("end");

        if (strDistanceFB_EndJob == null) {
            strDistanceFB_EndJob = "0";
        }

        String strDistanceEndTrip_sp = spTripState.getString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");

        String strFinalDistance;
        if (Double.parseDouble(strDistanceEndTrip_sp) > Double.parseDouble(strDistanceFB_EndJob)) {
            strFinalDistance = strDistanceEndTrip_sp;
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "callEndJobServices: Final Distance SP~~~~~" + strFinalDistance);
            }
        } else {
            strFinalDistance = strDistanceFB_EndJob;
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "callEndJobServices: Final Distance FB~~~~~" + strFinalDistance);
            }
        }


        String cnvt_strDistanceEndTrip_;
        if (convertDistanceUnit(MainActivity.this).equals("mile")) {
            cnvt_strDistanceEndTrip_ = String.valueOf(convertKmsToMiles(MainActivity.this, Double.valueOf(strFinalDistance)));
        } else {
            cnvt_strDistanceEndTrip_ = strFinalDistance;
        }

        /*if (Double.parseDouble(cnvt_strDistanceEndTrip_) > Double.parseDouble(strMileageLimitFB)) {
        } else {
        }*/

        //getDriverDistanceFB(); // Some time distance not update in End API So add that function.

        if (strLevelTypeFB.equals("0")) {

            if (strAmountTypeFB.equals("3")) {

                if (!strVisitPriceFB.equals("0")) {
                    DecimalFormat f1 = new DecimalFormat("##.##");
                    double fares = (Double.parseDouble(strSubLevelAmountFB) + Double.parseDouble(strVisitPriceFB));
                    txtCurrentChargelabel.setText(getResources().getString(R.string.additional_charge_current_charger) + "(" + convertCurrencyPrice(MainActivity.this, strSubLevelAmountFB) + ")" + "+" + getResources().getString(R.string.additional_charge_visit_price) + "(" + convertCurrencyPrice(MainActivity.this, strVisitPriceFB) + ")");
                    txtCurrentCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fares)));

                } else {
                    txtCurrentCharge.setText(convertCurrencyPrice(MainActivity.this, strUnitPriceFB));
                }

                if (strDropLocationFB.equals("NoDrop")) {
                    llDistanceAdditionalCharge.setVisibility(View.GONE);
                    txtDistanceAdditionalCharge.setText("0");
                    txtDistance$LabelAdditionalCharge.setText(strCurrencySymbol);
                } else {
                    Log.e(TAG, "callEndJobServices: dist~~~~ " + strFinalDistance + "~~~~PPKM~~~~~" + strPricePerKMFB);
                    if (!strFinalDistance.equals("0")) {

                        llDistanceAdditionalCharge.setVisibility(View.VISIBLE);

                        if (convertDistanceUnit(MainActivity.this).equals("mile")) {
                            DecimalFormat f = new DecimalFormat("##.##");
                            double fare = Double.parseDouble(strPricePerKMFB) * convertKmsToMiles(MainActivity.this, Double.valueOf(strFinalDistance));
                            txtPriceperKmAdditionalCharge.setText("( " + getResources().getString(R.string.additional_charge_price_per_mile) + " " + convertCurrencyPrice(MainActivity.this, strPricePerKMFB) + " * " + convertKmsToMiles(MainActivity.this, Double.valueOf(strFinalDistance)) + " Mile ) =" /*+ strCurrencySymbol
                                    + " " + convertCurrencyPrice(MainActivity.this,String.valueOf(fare))*/);
                            txtDistanceAdditionalCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));

                        } else if (convertDistanceUnit(MainActivity.this).equals("km")) {
                            DecimalFormat f = new DecimalFormat("##.##");
                            double fare = Double.parseDouble(strPricePerKMFB) * Double.parseDouble(strFinalDistance);

                            txtPriceperKmAdditionalCharge.setText(getResources().getString(R.string.additional_charge_price_per_km) + "= " + convertCurrencyPrice(MainActivity.this, strPricePerKMFB) + " * " + strFinalDistance + "KM");
                            txtDistanceAdditionalCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));
                        }
                        txtDistance$LabelAdditionalCharge.setText(strCurrencySymbol);
                    } else {
                        llDistanceAdditionalCharge.setVisibility(View.GONE);
                    }
                }

            } else {

                /* if (strDropLocationFB.equals("NoDrop")) {*/
                llDistanceAdditionalCharge.setVisibility(View.GONE);
                txtDistanceAdditionalCharge.setText("0");
                txtDistance$LabelAdditionalCharge.setText(strCurrencySymbol);

                if (!strVisitPriceFB.equals("0")) {
                    DecimalFormat f1 = new DecimalFormat("##.##");
                    double fares = (Double.parseDouble(strSubLevelAmountFB) + Double.parseDouble(strVisitPriceFB));
                    txtCurrentChargelabel.setText(getResources().getString(R.string.additional_charge_current_charger) + "(" + convertCurrencyPrice(MainActivity.this, strSubLevelAmountFB) + ")" + "+" + getResources().getString(R.string.additional_charge_visit_price) + "(" + convertCurrencyPrice(MainActivity.this, strVisitPriceFB) + ")");
                    txtCurrentCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fares)));

                } else {
                    txtCurrentCharge.setText(convertCurrencyPrice(MainActivity.this, strUnitPriceFB));

                }
            }

        } else {

            if (strVisitPriceFB.equals("0")) {
                txtCurrentChargelabel.setText(getResources().getString(R.string.additional_charge_current_charger) + "(" + convertCurrencyPrice(MainActivity.this, strUnitPriceFB) + ")" + "*" + strMainCategoryid + " charge" + "(" + strMainUnitPriceFB + ")");
                txtCurrentChargelabel.setTextSize(14.0f);

            } else {
                txtCurrentChargelabel.setText(getResources().getString(R.string.additional_charge_current_charger) + "(" + convertCurrencyPrice(MainActivity.this, strUnitPriceFB) + ")" + "*" + strMainCategoryid + " charge" + "(" + strMainUnitPriceFB + ") + " + getResources().getString(R.string.additional_charge_visit_price) + "(" + strVisitPriceFB + ")");
                txtCurrentChargelabel.setTextSize(14.0f);

            }
            //android:text="Current charge(10) * Level charge(10) + visit charge(5)"-->

            DecimalFormat f1 = new DecimalFormat("##.##");
            double fares = ((Double.parseDouble(strUnitPriceFB) * Double.parseDouble(strMainUnitPriceFB)) + Double.parseDouble(strVisitPriceFB));
            Log.e(TAG, "callEndJobServices:fares~~ ~~~" + fares);
            txtCurrentCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fares)));

            if (strDropLocationFB.equals("NoDrop")) {
                llDistanceAdditionalCharge.setVisibility(View.GONE);
                txtDistanceAdditionalCharge.setText("0");
                txtDistance$LabelAdditionalCharge.setText(strCurrencySymbol);

            } else {

                Log.e(TAG, "callEndJobServices: dist~~~~ " + strFinalDistance + strPricePerKMFB);
                if (!strFinalDistance.equals("0")) {

                    llDistanceAdditionalCharge.setVisibility(View.VISIBLE);

                    DecimalFormat f = new DecimalFormat("##.##");
                    double fare = Double.parseDouble(strPricePerKMFB) * Double.parseDouble(strFinalDistance);

                    txtPriceperKmAdditionalCharge.setText(getResources().getString(R.string.additional_charge_price_per_km) + "= " + convertCurrencyPrice(MainActivity.this, strPricePerKMFB) + " * " + strFinalDistance + "KM =");
                    txtDistanceAdditionalCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(fare)));

                    txtDistance$LabelAdditionalCharge.setText(strCurrencySymbol);
                } else {
                    llDistanceAdditionalCharge.setVisibility(View.GONE);
                }

            }
        }


        double totalAgentAmount = Double.parseDouble(strCount) * Double.parseDouble(strPriceperAgentFB);
        txtadditionalAgentCost.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(totalAgentAmount)));

        double totalDayAmount = Double.parseDouble(strCount) * Double.parseDouble(strDays) * Double.parseDouble(strPriceperDayFB);
        txtAdditionalDayCharge.setText(convertCurrencyPrice(MainActivity.this, String.valueOf(totalDayAmount)));

        //strTotalAmountFB = String.valueOf(Double.parseDouble(strTotalAmountFB) + totalAgentAmount + totalDayAmount);
        //txtFinalTotal.setText(convertCurrencyPrice(MainActivity.this,strTotalAmountFB));

        txtAgentchargeLabel.setText("(" + strCount + " X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperAgentFB) + ") " + getResources().getString(R.string.guard_count_x_price_per_guard));
        txtDaychargeLabel.setText("(" + strCount + " X " + strDays + "X " + strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this, strPriceperDayFB) + ") " + getResources().getString(R.string.guard_count_x_number_of_days_x_price_per_day));

       /* Double totalAgentAmount = Double.parseDouble(strCount) * Double.parseDouble(strPriceperAgentFB);
        Double totalDayAmount = Double.parseDouble(strCount) * Double.parseDouble(strDays) * Double.parseDouble(strPriceperDayFB);
        txtAgentCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this,totalAgentAmount.toString()));
        txtDayCharge.setText(strCurrencySymbol + " " + convertCurrencyPrice(MainActivity.this,totalDayAmount.toString()));*/

        txtDaychargelabel$.setText(strCurrencySymbol);
        txtAgentchargelabel$.setText(strCurrencySymbol);

        edtMaterialFee.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("LongLogTag")
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint({"LongLogTag", "SetTextI18n"})
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                int cursorPosition = edtMaterialFee.getSelectionEnd();
                String originalStr = edtMaterialFee.getText().toString();

                //To restrict only two digits after decimal place
                edtMaterialFee.setFilters(new InputFilter[]{new BaseActivity.EditTextValueFilter(Integer.parseInt(String.valueOf(2)))});

                try {
                    edtMaterialFee.removeTextChangedListener(this);
                    String value = edtMaterialFee.getText().toString();

                    if (value != null && !value.equals("")) {
                        if (value.startsWith(".")) {
                            edtMaterialFee.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            edtMaterialFee.setText("");
                        }
                        String str = edtMaterialFee.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            edtMaterialFee.setText(getDecimalFormattedString(str));

                        int diff = edtMaterialFee.getText().toString().length() - originalStr.length();
                        edtMaterialFee.setSelection(cursorPosition + diff);
                    }

                    edtMaterialFee.addTextChangedListener(this);

                    if (strDropLocationFB.equals("NoDrop")) {
                        txtFinalTotal.setText(baseActivity.addNumbers(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    } else {
                        txtFinalTotal.setText(baseActivity.addNumbersDistance(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtDistanceAdditionalCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();

                    edtMaterialFee.addTextChangedListener(this);
                }
            }
        });

        edtMiscFee.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @SuppressLint("LongLogTag")
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint({"LongLogTag", "SetTextI18n"})
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                int cursorPosition = edtMiscFee.getSelectionEnd();
                String originalStr = edtMiscFee.getText().toString();

                //To restrict only two digits after decimal place
                edtMiscFee.setFilters(new InputFilter[]{new BaseActivity.EditTextValueFilter(Integer.parseInt(String.valueOf(2)))});

                try {
                    edtMiscFee.removeTextChangedListener(this);
                    String value = edtMiscFee.getText().toString();

                    if (value != null && !value.equals("")) {
                        if (value.startsWith(".")) {
                            edtMiscFee.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            edtMiscFee.setText("");
                        }
                        String str = edtMiscFee.getText().toString().replaceAll(",", "");
                        if (!value.equals("")) edtMiscFee.setText(getDecimalFormattedString(str));

                        int diff = edtMiscFee.getText().toString().length() - originalStr.length();
                        edtMiscFee.setSelection(cursorPosition + diff);
                    }

                    edtMiscFee.addTextChangedListener(this);

                    if (strDropLocationFB.equals("NoDrop")) {
                        txtFinalTotal.setText(baseActivity.addNumbers(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    } else {
                        txtFinalTotal.setText(baseActivity.addNumbersDistance(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtDistanceAdditionalCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();

                    edtMiscFee.addTextChangedListener(this);
                }
            }
        });

        edtDiscountDriver.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("LongLogTag")
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint({"LongLogTag", "SetTextI18n"})
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int cursorPosition = edtDiscountDriver.getSelectionEnd();
                String originalStr = edtDiscountDriver.getText().toString();

                //To restrict only two digits after decimal place
                edtDiscountDriver.setFilters(new InputFilter[]{new BaseActivity.EditTextValueFilter(Integer.parseInt(String.valueOf(2)))});

                try {
                    edtDiscountDriver.removeTextChangedListener(this);
                    String value = edtDiscountDriver.getText().toString();

                    if (value != null && !value.equals("")) {
                        if (value.startsWith(".")) {
                            edtDiscountDriver.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            edtDiscountDriver.setText("");
                        }
                        String str = edtDiscountDriver.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            edtDiscountDriver.setText(getDecimalFormattedString(str));

                        int diff = edtDiscountDriver.getText().toString().length() - originalStr.length();
                        edtDiscountDriver.setSelection(cursorPosition + diff);
                    }

                    edtDiscountDriver.addTextChangedListener(this);

                    if (strDropLocationFB.equals("NoDrop")) {
                        txtFinalTotal.setText(baseActivity.addNumbers(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    } else {
                        txtFinalTotal.setText(baseActivity.addNumbersDistance(edtMaterialFee, edtMiscFee, edtDiscountDriver, txtCurrentCharge, txtDistanceAdditionalCharge, txtadditionalAgentCost, txtAdditionalDayCharge));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    edtDiscountDriver.addTextChangedListener(this);
                }
            }
        });

        ivCloseAdditionalCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (dialogAdditionalEndCharge.isShowing()) {
                    dialogAdditionalEndCharge.dismiss();
                }*/
                aBoolean = true;
                if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                    dialogAdditionalEndCharge.setVisibility(View.GONE);
                }
            }
        });

        btnSkipAddcharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtDiscountDriver.getText()).length() > 0) {
                    if (Double.parseDouble(convertCurrencyDividePrice(MainActivity.this, edtDiscountDriver.getText().toString())) >= Double.parseDouble(convertCurrencyPrice(MainActivity.this, strUnitPriceFB))) {
                        Toast.makeText(MainActivity.this, R.string.please_enter_valid_discount, Toast.LENGTH_SHORT).show();
                    } else {
                        finalAmountResult("skip");
                    }
                } else {
                    finalAmountResult("skip");
                }
            }
        });

        btnSubmitAddcharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(edtMaterialFee.getText()).length() == 0) {
                    //edtMaterialFee.setError(getResources().getString(R.string.error_required));
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.additional_charge_material_fee) + " " + getResources().getString(R.string.error_required), Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(edtMiscFee.getText()).length() == 0) {
                    // edtMiscFee.setError(getResources().getString(R.string.error_required));
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.additional_charge_misc_fee) + " " + getResources().getString(R.string.error_required), Toast.LENGTH_SHORT).show();

                }/*else if (edtDiscountDriver.getText().length() == 0) {
                    edtDiscountDriver.setError(getResources().getString(R.string.error_required));
                } */ else {
                    if (Objects.requireNonNull(edtDiscountDriver.getText()).length() > 0) {
                        if (Double.parseDouble(convertCurrencyDividePrice(MainActivity.this, edtDiscountDriver.getText().toString().replaceAll(",", ""))) >= Double.parseDouble(convertCurrencyPrice(MainActivity.this, strUnitPriceFB))) {
                            Toast.makeText(MainActivity.this, R.string.please_enter_valid_discount, Toast.LENGTH_SHORT).show();
                        } else {
                            finalAmountResult("submit");
                        }
                    } else {
                        finalAmountResult("submit");
                    }
                }
            }
        });

        if (strCurrencySymbol != null) {
            txtCurrentchargelabel$.setText(strCurrencySymbol);
            txtMaterialfeelabel$.setText(strCurrencySymbol);
            txtMiscfeelabel$.setText(strCurrencySymbol);
            txtDiscountdriverlabel$.setText(strCurrencySymbol);
            txtFinalTotal$.setText(strCurrencySymbol);
        } else {
            txtCurrentchargelabel$.setText("$");
            txtMaterialfeelabel$.setText("$");
            txtMiscfeelabel$.setText("$");
            txtDiscountdriverlabel$.setText("$");
            txtFinalTotal$.setText("$");
        }

        edtMaterialFee.setText("");
        edtMiscFee.setText("");
        edtDiscountDriver.setText("");
        // edtMileageFee.setText("");

        if (dialogAdditionalEndCharge.getVisibility() == View.GONE) {
            dialogAdditionalEndCharge.setVisibility(View.VISIBLE);
        }

    }

    private void finalAmountResult(String from) {

        System.out.println("~~~~~~from~~~~~" + from);
        spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_ADDITIONAL_CHARGE_LAST_CLICK, from);
        spEditTripState.apply();

        strfinaltotalamt = txtFinalTotal.getText().toString().trim();

        if (strUserWalletAmt == null) {
            strUserWalletAmt = "0";
        }

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "finalAmountResult: ~finaltotalamt-" + Double.parseDouble(strfinaltotalamt));
            Log.e(TAG, "~strfinaltotalamt-" + strfinaltotalamt);
            Log.e(TAG, "finalAmountResult: ~strUserWalletAmt-" + Double.parseDouble(strUserWalletAmt));
            Log.e(TAG, "finalAmountResult: ~strPaymentTypeFB-" + strPaymentTypeFB);
        }

        switch (strPaymentTypeFB) {
            case "wallet":

                if (Double.parseDouble(convertCurrencyDividePrice(MainActivity.this, strfinaltotalamt)) > Double.parseDouble(strUserWalletAmt)) {

                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "finalAmountResult: ~Inside compare-~~# " + (Double.parseDouble(strfinaltotalamt) - Double.parseDouble(strUserWalletAmt)));
                    }

                    if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                        dialogAdditionalEndCharge.setVisibility(View.GONE);
                    }

                    callEndtrip("both");
                } else {

                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "finalAmountResult: ~Outside compare-");
                    }

                    if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                        dialogAdditionalEndCharge.setVisibility(View.GONE);
                    }

                    callEndtrip("wallet");
                }

                break;
            case "cash":

                if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                    dialogAdditionalEndCharge.setVisibility(View.GONE);
                }
                callEndtrip("cash");

                break;
            default:

                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "finalAmountResult: ~default compare-");
                }

                if (dialogAdditionalEndCharge.getVisibility() == View.VISIBLE) {
                    dialogAdditionalEndCharge.setVisibility(View.GONE);
                }

                if (strPaymentTypeFB.equals("card")) {
                    callStripeUpdate("card");
                } else if (strPaymentTypeFB.equals("paypal")) {
                    callStripeUpdate("paypal");
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.check_payment_type), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void callGooglemapDetails() {

        // PackageManager packageManager = getPackageManager();
        // ApplicationInfo applicationInfo = null;

        String PACKAGE_NAME = "com.google.android.apps.maps";
        Intent mapIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);

        AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
        AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
        AcceptPickupLatLng = new LatLng(Double.parseDouble(AcceptPickupLat), Double.parseDouble(AcceptPickupLng));
//        System.out.println("google map AcceptPickupLatLng==>" + AcceptPickupLatLng.latitude + "lng" + AcceptPickupLatLng.longitude);
//        System.out.println("google map AcceptDropLatLng==>" + AcceptDropLatLng.latitude + "lng" + AcceptDropLatLng.longitude);

        if (mapIntent != null) {

            // startActivity(intent);
            switch (spTripState.getString(Utils.SP_MAP_TRIPSTATE, null)) {
                case "Accepttrip":
                case "ArriveTrips":
                    if (locFusedLocation != null) {
                        try {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + AcceptPickupLatLng.latitude + "," + AcceptPickupLatLng.longitude + "&mode=d");
                            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage(PACKAGE_NAME);
                            startActivity(mapIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "Starttrip":
                    if (AcceptDropLatLng.latitude != 0.0) {
                        try {

                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + AcceptDropLatLng.latitude + "," + AcceptDropLatLng.longitude + "&mode=d");
                            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage(PACKAGE_NAME);
                            startActivity(mapIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude() + "&mode=driving"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));

            // Toast.makeText(getApplicationContext(),"Kindly install Paypulse Application for further transaction.",Toast.LENGTH_SHORT).show();
        }
    }

    private void callWazemapDetails() {

        AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
        AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
        AcceptPickupLatLng = new LatLng(Double.parseDouble(AcceptPickupLat), Double.parseDouble(AcceptPickupLng));

        String PACKAGE_NAME = "com.waze";
        Intent wazeIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);

        if (wazeIntent != null) {

            switch (spTripState.getString(Utils.SP_MAP_TRIPSTATE, null)) {
                case "Accepttrip":
                case "ArriveTrips":
                    if (locFusedLocation != null) {
                        try {
                            @SuppressLint("DefaultLocale") final String url = String.format("waze://?ll=%f,%f&navigate=yes", AcceptPickupLatLng.latitude, AcceptPickupLatLng.longitude);
                            wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            Log.e(TAG, "callWazemapDetails: " + url);
                            startActivity(wazeIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "Starttrip":
                    if (AcceptDropLatLng.latitude != 0.0) {
                        try {
                            @SuppressLint("DefaultLocale") final String url = String.format("waze://?ll=%f,%f&navigate=yes", AcceptDropLatLng.latitude, AcceptDropLatLng.longitude);
                            wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(wazeIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            @SuppressLint("DefaultLocale") final String url = String.format("waze://?ll=%f,%f&navigate=yes", locFusedLocation.getLatitude(), locFusedLocation.getLongitude());
                            wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(wazeIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));
        }
    }

    private void getDriverEstDistanceDuration(String key) {

//        if (driverEstDistanceDurationDatabaseBRef != null) {
//            driverEstDistanceDurationDatabaseBRef.removeEventListener(driverEstDistanceDurationValueEventListener);
//        }
//        driverEstDistanceDurationDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(key);
//        driverEstDistanceDurationValueEventListener = driverEstDistanceDurationDatabaseBRef.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//
//                    Object est_distance = dataSnapshot.child("est_distance").getValue();
//                    Object est_duration = dataSnapshot.child("est_duration").getValue();
//
//                    if (est_distance != null) {
//                        strEstDistance = String.valueOf(est_distance);
//                        Log.e(TAG, "onDataChange: strEstDistance~~~>>" + strEstDistance);
//                    } else {
//                        strEstDistance = "Loading";
//                    }
//
//                    if (est_duration != null) {
//                        strEstDuration = String.valueOf(est_duration);
//                        Log.e(TAG, "onDataChange: strEstDuration~~~>>" + strEstDuration);
//                    } else {
//                        strEstDuration = "Loading";
//                    }
//
//                    if (driverCarMarker != null) {
//                        driverCarMarker.setTitle(strEstDistance + " , " + strEstDuration);
//                    }
//
//
//                    if (!strDropLocationFB.equals("NoDrop")) {
//                        if (driverCarMarker != null) {
//                            driverCarMarker.showInfoWindow();
//                        }
//                    } else {
//                        Log.e(TAG, "onDirectionSuccessPlaceMarker: Distance NO``");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled: ~~~~" + databaseError.toString());
//            }
//        });

    }

    /***************************************
     *
     * * Dialog customer info
     *
     *--************************************* */
    @SuppressLint("SetTextI18n")
    private void dialogCancelJobinfos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        Typeface tf = Typeface.createFromAsset(getAssets(), getString(R.string.App_Font));
        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.map_cancel_job_title));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStrings = new SpannableString(getString(R.string.map_cancel_job_label));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStringss = new SpannableString(getString(R.string.no));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStringsss = new SpannableString(getString(R.string.yes));
        spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);
        builder.setMessage(spannableStrings);
        builder.setPositiveButton(spannableStringss, (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton(spannableStringsss, (dialog, which) -> {
            dialog.dismiss();
            callCancelJobUserUpdate("", "");
        });
        builder.show();

        /*
        dialogCancelJobinfo = new Dialog(MainActivity.this);
        dialogCancelJobinfo.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogCancelJobinfo.setContentView(R.layout.dialog_cancel_job);
        Objects.requireNonNull(dialogCancelJobinfo.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogCancelJobinfo.setCancelable(true);
        dialogCancelJobinfo.getWindow().setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCancelJobinfo.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        CustomTextView txtContinue = (CustomTextView) dialogCancelJobinfo.findViewById(R.id.txt_continue);
        CustomTextView txtCancel = (CustomTextView) dialogCancelJobinfo.findViewById(R.id.txt_cancel);
        CustomEditText edtReason = (CustomEditText) dialogCancelJobinfo.findViewById(R.id.edt_reason);
        CustomEditText edtComment = (CustomEditText) dialogCancelJobinfo.findViewById(R.id.edt_comment);

        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here I want to dismiss it after SetBackground() method
                dialogCancelJobinfo.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // here I want to dismiss it after SetBackground() method
                String strreason = edtReason.getText().toString().trim();
                String strcmt = edtComment.getText().toString().trim();

                if (strreason.length() == 0 && strcmt.length() == 0) {
                    edtReason.setError(getString(R.string.error_cancel_job_enter_reason));
                    edtComment.setError(getString(R.string.error_cancelc_job_enter_commt));
                } else {
                    dialogCancelJobinfo.dismiss();
                    callCancelJobUserUpdate(strreason, strcmt);
                }

            }
        });

        dialogCancelJobinfo.show();
        dialogCancelJobinfo.getWindow().setAttributes(lp);*/
    }

    public void dialogShowNavigations() {

        dialogShowNavigation = new Dialog(MainActivity.this);
        dialogShowNavigation.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogShowNavigation.setContentView(R.layout.dialog_show_navigation);
        Objects.requireNonNull(dialogShowNavigation.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogShowNavigation.setCancelable(true);
        Objects.requireNonNull(dialogShowNavigation.getWindow()).setWindowAnimations(R.style.DialogAnimationLR);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogShowNavigation.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageButton imgbtn_back = (ImageButton) dialogShowNavigation.findViewById(R.id.imgbtn_back);
        btnGoogleMap = (CustomButton) dialogShowNavigation.findViewById(R.id.btn_google);
        btnWazeMap = (CustomButton) dialogShowNavigation.findViewById(R.id.btn_waze);
        btnInAppMap = (CustomButton) dialogShowNavigation.findViewById(R.id.btn_inapp);


        btnGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putStateFBCategory.putString(Utils.SP_NAVIGATION, "gmap");
                putStateFBCategory.apply();
                dialogShowNavigation.dismiss();
//                openRouteNavigator();
                checkOverlayPermission();
            }
        });

        btnWazeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putStateFBCategory.putString(Utils.SP_NAVIGATION, "waze");
                putStateFBCategory.apply();
                dialogShowNavigation.dismiss();
//                openRouteNavigator();
                checkOverlayPermission();
            }
        });

        btnInAppMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putStateFBCategory.putString(Utils.SP_NAVIGATION, "inapp");
                putStateFBCategory.apply();
                dialogShowNavigation.dismiss();
                // checkOverlayPermission();
            }
        });


        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowNavigation.dismiss();
            }
        });


        dialogShowNavigation.show();
    }

    /***************************************
     *
     * * playmusic info
     *
     *--************************************* */

    public void playRequestTone() {
        Log.i(TAG, "playRequestTone: ");
        if (mp == null) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.uber_beep_sound);
            mp.start();
            mp.setLooping(true);
        }
    }

    private void stopPlaying() {
        Log.i(TAG, "stopPlaying: ");

        if (mp != null) {
            mp.stop();
            // mp.reset();
            mp.release();
            mp = null;
        }
    }

    /*-*******************************************
     *
     * *Logout
     *
     *-********************************************/
    public void showLogoutAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.logout));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getString(R.string.are_u_sure_logout));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.yes));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringsss = new SpannableString(getString(R.string.no));
        spannableStringsss.setSpan(tfSpan, 0, spannableStringsss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableString);

        builder.setMessage(spannableStrings);
        builder.setPositiveButton(spannableStringss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateDriverLocationFirebase(0.0, 0.0, null);
                CallLogout(dialog);
            }
        });
        builder.setNegativeButton(spannableStringsss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });


        builder.show();
    }

    private void CallLogout(DialogInterface dialog) {
        userAccessToken = getspCommonState.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", userAccessToken);
        Log.i("CallLogout", "CallLogout~~~" + map);
        showProgressDialog(MainActivity.this);
        Call<LogoutModel> call2 = apiInterface.doGetLogoutDetails(map);
        call2.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(@NonNull Call<LogoutModel> call, @NonNull Response<LogoutModel> response) {
                hideProgressDialog();
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Successfully logged out")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
                        Map<String, Object> taskMap1 = new HashMap<String, Object>();
                        taskMap1.put("status", "0");
                        databaseReference.updateChildren(taskMap1);
                        clearSPmodePrivate();
                    } else {
                        //callRefreshToken("Logout");
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("Logout");
                } else if (response.code() == RESPONSECODE_500) {
                    clearSPmodePrivate();
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<LogoutModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void clearSPmodePrivate() {
        Log.i(TAG, "clearSPmodePrivate: called");
        SharedPreferences preferences = getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        //deleteCache(MainActivity.this);
        Intent logout = new Intent(MainActivity.this, HomeActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);
        finish();

    }

    /*-*******************************************
     *
     * *Google Direction & API Query check
     *
     *-********************************************/

    DatabaseReference DriverLocDatabaseRef;
    ValueEventListener DriverLocValueEventListener;
    String drivBearing = "0";
    float getBearing = 0.0f;
    float DriverCurrentBearing = 0.0f;
    LatLng DriverCurrentLatLng;
    private final Map<String, List<MarkerInfo>> markerInfoMap = new HashMap<>();
    private final Map<Marker, Float> markerRotationMap = new HashMap<>(); // To keep track of marker rotations

    public void callGoogleDirection() {

        try {

            /*if (directionPolyline != null) {
                Log.i(TAG, "ondirection: polyline change");
                directionPolyline.remove();
            }*/

            //if (strGroupDriverIdFB.contains(",")) {
            // String[] numbers = strGroupDriverIdFB.split(",");
            if (tripsFirebaseBackupGuardListModels != null && tripsFirebaseBackupGuardListModels.size() > 0) {
                for (int i = 0; i < tripsFirebaseBackupGuardListModels.size(); i++) {
                    // Perform some operation on each character
                    String strdriverIdFB = tripsFirebaseBackupGuardListModels.get(i).getDriver_id();

                    // Example: Convert the string to an integer
                    int intValue = Integer.parseInt(strdriverIdFB);

                    // Print the result or perform other operations
                    System.out.println("Number at index -->" + i + ": " + intValue);
                    System.out.println("Number at index ==>" + i + ": " + strdriverIdFB);

                    callDirectionPolyline(strdriverIdFB, reqStatus);
                }
            }
            /*} else {
                callDirectionPolyline(strGroupDriverIdFB, key);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void callDirectionPolyline(String strGroupDriverIdFB, String key) {


        DriverLocDatabaseRef = FirebaseDatabase.getInstance().getReference().child("drivers_location")
                .child(strGroupDriverIdFB);
        DriverLocValueEventListener = DriverLocDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                List<MarkerInfo> markerInfoList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (dataSnapshot.child("bearing").getValue() != null)
                        drivBearing = Objects.requireNonNull(dataSnapshot.child("bearing").getValue()).toString(); //get bearing child
                    else
                        drivBearing = "0";

                    String status = Objects.requireNonNull(dataSnapshot.child("l").getValue()).toString(); //get location child
                    System.out.println("Status==>" + status);

                    if (isFloat(drivBearing)) {
                        getBearing = Float.parseFloat(drivBearing);
                    } else {
                        getBearing = (float) Integer.parseInt(drivBearing);
                    }

                    String[] lat1ong = status.split(",");

                    System.out.println("length of the latlong==>" + lat1ong.length);

                    String latitude = lat1ong[0];
                    String longitude = lat1ong[1];

                    String latreplace = latitude.replaceAll("\\[", "");
                    String longreplace = longitude.replaceAll("\\]", "");

                    double templat = Double.parseDouble(latreplace);
                    double templng = Double.parseDouble(longreplace);

                    DriverCurrentLatLng = new LatLng(templat, templng);
                    DriverCurrentBearing = getBearing;

                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "onDataChange: drivercurrent latlng--->" + DriverCurrentLatLng + "~~~Status~~~");
                        Log.i(TAG, "onDataChange in onkeyentered-->: " + DriverCurrentBearing);
                    }

                    // Retrieve latitude and longitude from Firebase snapshot
                    //double latitude = snapshot.child("latitude").getValue(Double.class);
                    //double longitude = snapshot.child("longitude").getValue(Double.class);
                    // float rotation = snapshot.child("bearing").getValue(Float.class); // Added rotation value
                    // LatLng position = new LatLng(latitude, longitude);

                    MarkerInfo markerInfo = new MarkerInfo(DriverCurrentLatLng, DriverCurrentBearing);
                    markerInfoList.add(markerInfo);
                }

                // Update marker info map
                markerInfoMap.put(strGroupDriverIdFB, markerInfoList);

                // Update markers on the map
                updateMarkersOnMap(key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                out.println("~~onCancelled~~~");
            }
        });


    }


    private void updateMarkersOnMap(String key) {
        mMap.clear(); // Clear previous markers
        // Add new markers to the map for each child node
        for (List<MarkerInfo> markerInfoList : markerInfoMap.values()) {
            for (MarkerInfo markerInfo : markerInfoList) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(markerInfo.getPosition())
                        .rotation(markerInfo.getRotation()); // Set rotation for the marker
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptor).flat(true));
                markerRotationMap.put(marker, markerInfo.getRotation()); // Store marker rotation
                //callPolylineUpdate_(markerInfo.getPosition());
            }
        }
        // Animate marker rotation
        animateMarkerRotation();
        //callPolylineUpdate(strGroupDriverIdFB, key);
    }

    private void animateMarkerRotation() {
        for (final Marker marker : markerRotationMap.keySet()) {
            final float currentRotation = marker.getRotation();
            final float targetRotation = markerRotationMap.get(marker);
            ValueAnimator animator = ValueAnimator.ofFloat(currentRotation, targetRotation);
            animator.setDuration(1000); // Animation duration in milliseconds
            animator.addUpdateListener(animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                marker.setRotation(animatedValue);
            });
            animator.start();
        }
    }

    // Custom class to hold marker information
    private static class MarkerInfo {
        private LatLng position;
        private float rotation;

        public MarkerInfo(LatLng position, float rotation) {
            this.position = position;
            this.rotation = rotation;
        }

        public LatLng getPosition() {
            return position;
        }

        public float getRotation() {
            return rotation;
        }
    }

    List<LatLng> alLatLng = new ArrayList<>();


    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        //  mMap.setTrafficEnabled(switchmTraffic.isChecked());
        if (switchmTraffic.isChecked()) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void callPolylineUpdate_(LatLng position) {
        try {
            if (!checkReady()) {
                return;
            }

            LatLng start = new LatLng(Double.parseDouble(strPickupLatFB), Double.parseDouble(strPickupLngFB));
            LatLng end = new LatLng(9.919033517164335, 78.10867445411951);

            LatLng start1 = new LatLng(9.916781557264631, 78.09853573563835);
            LatLng end1 = new LatLng(9.906019382081157, 78.11066918071194);
            // LatLng end = position;

            /*double cLat = ((start.latitude + end.latitude) / 2);
            double cLon = ((start.longitude + end.longitude) / 2);

            //add skew and arcHeight to move the midPoint
            if(Math.abs(start.longitude - end.longitude) < 0.0001){
                cLon -= 0.0195;
            } else {
                cLat += 0.0195;
            }

            double tDelta = 1.0/50;
            for (double t = 0; t <= 1.0; t+=tDelta) {
                double oneMinusT = (1.0-t);
                double t2 = Math.pow(t, 2);
                double lon = oneMinusT * oneMinusT * start.longitude
                        + 2 * oneMinusT * t * cLon
                        + t2 * end.longitude;
                double lat = oneMinusT * oneMinusT * start.latitude
                        + 2 * oneMinusT * t * cLat
                        + t2 * end.latitude;

                alLatLng.add(new LatLng(lat, lon));
            }*/

            /*out.println("~~~~~~~alLatLng~~~~~~~~"+alLatLng.size());
            if (alLatLng != null) {
                // draw polyline
                PolylineOptions line = new PolylineOptions();
                line.width(5f);
                line.color(Color.RED);
                line.addAll(alLatLng);
                mMap.addPolyline(line);
            }*/

            List<LatLng> mergedList = new ArrayList<>();
            mergedList.add(start);
            mergedList.add(end);
            mergedList.add(start1);
            mergedList.add(end1);

            //MapAnimator.getInstance().animateRoute(mMap, mergedList);

            //MapUtils.showCurvedPolyline(mMap, new LatLng(Double.parseDouble(strPickupLatFB), Double.parseDouble(strPickupLngFB)),position, 0.5, Utils.POLYLINE_COLOR_ACCEPTTRIP);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callGoogleDirection(String key) {

        System.out.println("inside callGoogleDirection");
        try {

            if (directionPolyline != null) {
                Log.i(TAG, "onDataChange: Starttrip directionPolyline if ");
                directionPolyline.remove();
            }

            if (driverId != null && !driverId.isEmpty() && !driverId.trim().matches("")) {
                // driverCarMarker();
                System.out.println("inside callGoogleDirection==>" + driverId);
                if (spTripState != null) {

                    AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                    AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                    AcceptPickupLatLng = new LatLng(Double.parseDouble(AcceptPickupLat), Double.parseDouble(AcceptPickupLng));
                    System.out.println("sdsdfs" + AcceptPickupLat + "long" + AcceptPickupLng);
                    System.out.println(" spTripState google direction==>" + spTripState.getString(Utils.SP_MAP_TRIPSTATE, null));
                    System.out.println(" spTripState google direction==AcceptPickupLatLng >" + AcceptPickupLatLng);

                    switch (key) {
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                            if (directionPolyline != null) {
                                Log.i(TAG, "onDataChange: Accepttrip directionPolyline");
                                directionPolyline.remove();
                            }

                            if (AcceptPickupLatLng != null) {
                                if (locFusedLocation != null) {
                                    try {
                                        if (directionPolyline != null) {
                                            Log.i(TAG, "onDataChange: Accepttrip directionPolyline");
                                            directionPolyline.remove();
                                        }
                                        GoogleDirection.withServerKey(BaseActivity.getGoogleApiKey(MainActivity.this)).from(new LatLng(locFusedLocation.getLatitude(), locFusedLocation.getLongitude())).to(AcceptPickupLatLng).avoid(AvoidType.FERRIES).avoid(AvoidType.INDOOR).transportMode(TransportMode.DRIVING).transitMode(TransitMode.BUS).alternativeRoute(true).execute(new DirectionCallback() {
                                            @Override
                                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                                if (direction != null) {
                                                    if (direction.isOK()) {
                                                        Log.i(TAG, "onDirectionSuccess: is OK");
                                                        directionFirebaseUpdate(direction);
                                                        Route route = direction.getRouteList().get(0);
                                                        List<Leg> leg = direction.getRouteList().get(0).getLegList();
                                                        step = leg.get(0).getStepList();
                                                        onDirectionSuccessPlaceMarker(direction, key);

                                                        if (directionPolyline != null) {
                                                            Log.i(TAG, "onDirectionSuccess: directionPolyline");
                                                            directionPolyline.remove();
                                                        }

                                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();

                                                        switch (key) {
                                                            case "2":
                                                            case "3":
                                                            case "4":
                                                            case "5":
                                                                directionPolyline = mMap.addPolyline(DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 3, Utils.POLYLINE_COLOR_ACCEPTTRIP));
                                                                break;
                                                        }
                                                    } else {
                                                        Log.i(TAG, "onDirectionSuccess: not OK");
                                                        onDirectionSuccessPlaceMarker(direction, key);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onDirectionFailure(Throwable t) {

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Log.i(TAG, "callGoogleDirection: not get AcceptPickupLatLng");
                            }
                            break;
                    }
                }
            }

        } catch (Exception e) {
            Log.i(TAG, "callGoogleDirection: exception-->" + e);
        }
    }

    public void onDirectionSuccessPlaceMarker(Direction direction, String key) {
        try {
            List<Leg> leg = direction.getRouteList().get(0).getLegList();
            List<Step> step = leg.get(0).getStepList();
            List<Leg> legDistance = direction.getRouteList().get(0).getLegList();

            System.out.println("distance==>>" + legDistance.get(0).getDistance().getText());
            System.out.println("duration==>>" + legDistance.get(0).getDuration().getText());
            System.out.println("distance==1>>" + step.get(0).getDistance().getText());
            System.out.println("duration==1>>" + step.get(0).getDuration().getText());

            if (legDistance.get(0).getDistance().getText() != null) {
                strEstDistance = String.valueOf(legDistance.get(0).getDistance().getText());
                Log.e(TAG, "onDataChange: strEstDistance~~~>>" + strEstDistance);
            } else {
                strEstDistance = "Loading";
            }

            if (legDistance.get(0).getDuration().getText() != null) {
                strEstDuration = String.valueOf(legDistance.get(0).getDuration().getText());
                Log.e(TAG, "onDataChange: strEstDuration~~~>>" + strEstDuration);
            } else {
                strEstDuration = "Loading";
            }

            if (driverCarMarker != null) {
                driverCarMarker.setTitle(strEstDistance + " , " + strEstDuration);
            }

            Log.e(TAG, "onDirectionSuccessPlaceMarker: ------------------------------------>>>>>>" + locFusedLocation);
            Log.e(TAG, "onDirectionSuccess: -----strEstDistance------------strEstDuration--------------->>>>>>" + strEstDistance + "----" + strEstDuration);
            switch (key) {
                case "2":
                case "3":
                case "4":

                    if (!strDropLocationFB.equals("NoDrop")) {
                        if (driverCarMarker != null) {
                            driverCarMarker.showInfoWindow();
                        }
                    } else {
                        Log.e(TAG, "onDirectionSuccessPlaceMarker: Distance NO``");
                    }

                    if (markerPickupDropAccept != null) markerPickupDropAccept.remove();
                    /*if (strPickupLatFB != null) {
                        markerPickupDropAccept = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(strPickupLatFB), Double.parseDouble(strPickupLngFB))).icon(bitmapDescriptorMan));
                    }*/
                    // markerPickupDropAccept = mMap.addMarker(new MarkerOptions().position(AcceptPickupLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_green_pin)));
                    break;

                case "5":
                case "5.1":
                case "5.3":

                    if (!strDropLocationFB.equals("NoDrop")) {
                        if (driverCarMarker != null) {
                            driverCarMarker.showInfoWindow();
                        }
                    } else {
                        Log.e(TAG, "onDirectionSuccessPlaceMarker: Distance NO``");
                    }
                    if (AcceptDropLatLng != null) {
                        if (markerPickupDropAccept != null) markerPickupDropAccept.remove();

                        if (markerCurrentLocationMan != null) markerCurrentLocationMan.remove();

                        markerCurrentLocationMan = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(strPickupLatFB), Double.parseDouble(strPickupLngFB))).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_green_pin)));

                        markerPickupDropAccept = mMap.addMarker(new MarkerOptions().position(AcceptDropLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_red_pin)));
                    } else {
                        Toast.makeText(this, "Drop location not mentioned", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler();
    private boolean boolDirection = false;
    public String strCurrentSpeed = "0.0";

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            //doSomething();
            if (reqStatus != null) {
                if (!reqStatus.equals("null")) {
                    if (!reqStatus.equals("")) {
                        switch (reqStatus) {
                            case "2":
                                callGoogleDirection("2");
                                break;
                            case "3":
                                callGoogleDirection("3");
                                break;
                            case "4":
                                callGoogleDirection("4");
                                break;
                            case "5":
                                callGoogleDirection("5");
                                break;
                            /*case "5.1":
                                callGoogleDirection("5.1");
                                break;
                            case "5.3":
                                callGoogleDirection("5.3");
                                break;*/
                        }
                    }
                }
            }
            boolDirection = true;
            mHandler.postDelayed(mHandlerTask, Utils.GOOGLE_DIRECTION_CALL_INTERVAL);
        }
    };

    void startRepeatingTask() {
        Log.i(TAG, "startRepeatingTask: ");
        if (mHandlerTask != null) mHandlerTask.run();
    }

    void stopRepeatingTask() {
        Log.i(TAG, "stopRepeatingTask: ");
        if (mHandlerTask != null) mHandler.removeCallbacks(mHandlerTask);
    }

    public void directionFirebaseUpdate(Direction direction) {
        Gson gson = new Gson();
        String json = gson.toJson(direction);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("direction").child(driverId).child("direction").setValue(json);
//        ref.child("drivers_location").child(driverId).child("direction").setValue(json); // Not working
//        ref.child("booking_trips").child(strBookingId).child("direction").setValue(json); // Not working
//        ref.child("providers").child(driverId).child("direction").setValue(json); // Not working
    }

    String pickup, drop;

    private void callGoogleDistanceQueryAPI(String key) {

        try {

            System.out.println("~~~~key~" + key);

            switch (key) {

                case "accept":
                case "onTrip":
                default:
                    callGoogleApiDistance(key);
                    break;
                case "endTrip":

                    if (BuildConfig.DEBUG) {
                        System.out.println("~~~DROP_LOCATION_ADDED~--->>>>>" + getspFBCategory.getBoolean(Utils.SP_FB_ACCEPT_DROP_LOCATION_ADDED, false));
                    }
                    if (getspFBCategory.getBoolean(Utils.SP_FB_ACCEPT_DROP_LOCATION_ADDED, false)) {
                        if (BuildConfig.DEBUG) {
                            System.out.println("DropLocation -- true --->>>>>" + getspFBCategory.getString(Utils.SP_FB_ACCEPT_DROP_LOCATION, null));
                        }
                        getDriverDistanceFB("endTrip");
                    } else {
                        callGoogleApiDistance(key);
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "callGoogleDistanceQueryAPI: ~~~~~" + e.toString());
            Toast.makeText(MainActivity.this, "API Query Exception --- " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void callGoogleApiDistance(String key) {

        // http://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        switch (key) {

            case "accept":

                if (locFusedLocation != null) {

                    if (AcceptPickupLat != null && AcceptPickupLng != null) {

                        AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                        AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                        AcceptPickupLatLng = new LatLng(Double.parseDouble(AcceptPickupLat), Double.parseDouble(AcceptPickupLng));

                        System.out.println("google map AcceptPickupLatLng 123==>" + AcceptPickupLatLng.latitude + "lng" + AcceptPickupLatLng.longitude);

                        Log.e(TAG, "case ETA1: " + locFusedLocation.getLatitude() + " " + locFusedLocation.getLongitude());
                        Log.e(TAG, "case ETA1: " + AcceptPickupLatLng.latitude + "," + AcceptPickupLatLng.longitude);

                        pickup = locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude();
                        drop = AcceptPickupLatLng.latitude + "," + AcceptPickupLatLng.longitude;

                    /*mapQuery.put("origins", pickup);
                    mapQuery.put("destinations", drop);*/
                    }

                } else {
                    ref.child("providers").child(driverId).child("est_distance").setValue("");
                    ref.child("providers").child(driverId).child("est_duration").setValue("");
                }

                break;
            case "onTrip":

                if (locFusedLocation != null) {
                    if (AcceptDropLatLng != null) {

                        pickup = locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude();
                        drop = AcceptDropLatLng.latitude + "," + AcceptDropLatLng.longitude;

                        Log.e(TAG, "case ETA1: " + locFusedLocation.getLatitude() + " " + locFusedLocation.getLongitude());
                        Log.e(TAG, "case ETA1: " + AcceptDropLatLng.latitude + "," + AcceptDropLatLng.longitude);

                        /*mapQuery.put("origins", pickup);
                        mapQuery.put("destinations", drop);*/

                    } else {
                        ref.child("providers").child(driverId).child("est_distance").setValue("");
                        ref.child("providers").child(driverId).child("est_duration").setValue("");
                    }

                } else {
                    ref.child("providers").child(driverId).child("est_distance").setValue("");
                    ref.child("providers").child(driverId).child("est_duration").setValue("");
                }

                break;
            case "endTrip":

                AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);

                pickup = AcceptPickupLat + "," + AcceptPickupLng;
                drop = currentLocation.getLatitude() + "," + currentLocation.getLongitude();

                /*mapQuery.put("origins", pickup);
                mapQuery.put("destinations", drop);*/

                showProgressDialog(MainActivity.this);

                break;
            default:

                if (locFusedLocation != null) {
                    pickup = locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude();
                    drop = locFusedLocation.getLatitude() + "," + locFusedLocation.getLongitude();
                } else {
                    pickup = "";
                    drop = "";
                }

                break;
        }

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "metric");
        mapQuery.put("key", BaseActivity.getGoogleApiKey(MainActivity.this));

        mapQuery.put("origins", pickup);
        mapQuery.put("destinations", drop);

        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);

        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<DistanceResponse> call, @NonNull Response<DistanceResponse> response) {

                String status = Objects.requireNonNull(response.body()).getStatus();

                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "status~~~~: " + status);
                }

                // getAddressFromLatLngEndTrip(locCurrentLocation.getLatitude(), locCurrentLocation.getLongitude());
                //OVER_QUERY_LIMIT
                //REQUEST_DENIED
                //INVALID_REQUEST
                //OVER_DAILY_LIMIT

                if (status.equals("OK")) {

                    Element element = response.body().getRows().get(0).getElements().get(0);

                    switch (key) {
                        case "accept":

                            strEstDistance = element.getDistance().getText();
                            strEstDuration = element.getDuration().getText();

                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "onResponse: accept~~~~~~" + strEstDuration + "~~~~@!!~~~" + strEstDistance);
                            }

                            ref.child("providers").child(driverId).child("est_distance").setValue(strEstDistance);
                            ref.child("providers").child(driverId).child("est_duration").setValue(strEstDuration);

                            //countDownTimerDistanceDurationAccept.start();

                            break;
                        case "onTrip":

                            strEstDistance = element.getDistance().getText();
                            strEstDuration = element.getDuration().getText();

                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "onResponse: onTrip~~~~~~" + strEstDuration + "~~~~@!!~~~" + strEstDistance);
                            }

                            ref.child("providers").child(driverId).child("est_distance").setValue(strEstDistance);
                            ref.child("providers").child(driverId).child("est_duration").setValue(strEstDuration);

                            //countDownTimerDistanceDuration.start();

                            break;
                        case "endTrip":

                            if (currentLocation != null) {

                                hideProgressDialog();
                                strDestAddress = response.body().getDestinationAddresses().get(0);

                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "dest_address~~~~: " + strDestAddress);
                                    Log.e(TAG, "strDistanceEndTrip~~~~: " + strDistanceEndTrip);
                                }

                                putStateFBCategory.putString(Utils.SP_FB_ACCEPT_DROP_LOCATION, strDestAddress);
                                putStateFBCategory.putBoolean(Utils.SP_FB_ACCEPT_DROP_LOCATION_ADDED, true);
                                putStateFBCategory.apply();

                                getDriverDistanceFB("endTrip");

                            } else {
                                hideProgressDialog();
                                Toast.makeText(MainActivity.this, R.string.mainactivity_getting_location, Toast.LENGTH_SHORT).show();
                            }

                            break;
                        default:

                            hideProgressDialog();
                            break;
                    }


                } else {

                    switch (key) {

                        case "accept":
                        case "onTrip":

                            //Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();

                            ref.child("providers").child(driverId).child("est_distance").setValue("");
                            ref.child("providers").child(driverId).child("est_duration").setValue("");

                            break;

                        case "endTrip":

                            hideProgressDialog();

                            Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();

                            String strCustomerPickuplat, strCustomerPickuplng, strCustomerdrop;

                            strCustomerPickuplat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, null);
                            strCustomerPickuplng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, null);
                            strCustomerdrop = getspFBCategory.getString(Utils.SP_FB_ACCEPT_DROP_LOCATION, null);


                            LatLng pickup = new LatLng(Double.parseDouble(strCustomerPickuplat), Double.parseDouble(strCustomerPickuplng));
                            LatLng drop = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            DecimalFormat df = new DecimalFormat("###.##");
                            strDistanceEndTrip = df.format(CalculationByDistance(pickup, drop));

                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "Endtrip-PickupLat: " + strCustomerPickuplat);
                                Log.e(TAG, "Endtrip-PickupLng" + strCustomerPickuplng);
                                Log.e(TAG, "strDistanceEndTrip~~~2~~" + strDistanceEndTrip);
                            }

                            hideProgressDialog();

                            if (strCustomerdrop != null) {
                                if (!strCustomerdrop.equals("")) {
                                    getDriverDistanceFB("endTrip");
                                } else {
                                    getDriverDistanceFB("endTrip");
                                }
                            } else {
                                getDriverDistanceFB("endTrip");
                            }

                            break;
                        default:

                            hideProgressDialog();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(MainActivity.this, "API Query Error --- " + t.toString(), Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }

    /*-*******************************************
     *
     * *Currency Conversion & Details
     *
     *-********************************************/
    public static void searchCurrencyPrice(String Currency) {

        if (driverSearchDatabaseBRef != null) {
            driverSearchDatabaseBRef.removeEventListener(driverSearchValueEventListener);
        }
        driverSearchDatabaseBRef = FirebaseDatabase.getInstance().getReference().child("currency").child(Currency);
        driverSearchValueEventListener = driverSearchDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    Object currencyPriceObj = dataSnapshot.getValue();
                    if (currencyPriceObj != null) {
                        Log.i(TAG, "onDataChange: in search currency-->" + currencyPriceObj.toString());
                        getStateCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, currencyPriceObj.toString());
                        getStateCurrency.apply();
                    } else {
                        getStateCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                        getStateCurrency.apply();
                    }

                } else {
                    getStateCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                    getStateCurrency.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: error occur " + databaseError);
                getStateCurrency.putString(Utils.SP_CURRENCY_COMMON_VALUES, "1");
                getStateCurrency.apply();
            }
        });

    }

    @SuppressLint("ObsoleteSdkInt")
    public void onSuccessEditProfile(String langname, String code, String country, String key) {

        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(code + "-" + country);
        // Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setLanguage(code);
            getStateCurrency.putString(Utils.SP_LANGUAGE, langname);
            getStateCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, code);
            getStateCurrency.apply();
        } else {
            setLanguageBelow23(code, country);
            getStateCurrency.putString(Utils.SP_LANGUAGE, langname);
            getStateCurrency.putString(Utils.SP_LANGUAGE_SHORT_NAME, code);
            getStateCurrency.putString(Utils.SP_LANGUAGE_COUNTRY, country);
            getStateCurrency.apply();
        }

       /* Intent intent = getIntent();
        finish();
        startActivity(intent);*/

       /* Intent news = new Intent(MainActivity.this,LaunchActivity.class);
        news.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        news.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //news.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(news);
        MainActivity.this.finish();*/

        if (mMap != null) {
            mMap.clear();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // MainActivity.this.recreate();
            MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MainActivity.class));
            MainActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            MainActivity.this.finish();
        } else {
            MainActivity.this.finish();
            MainActivity.this.startActivity(MainActivity.this.getIntent());
            // MainActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /*-*******************************************
     *
     * *Convert Kilometer Km to Miles
     *
     *-********************************************/


    @Override
    public void onResume() {
        Log.e(TAG, "onResume: --------------------reqStatus-------");
        super.onResume();
        Utils.MAP_ISSHOWING = true;
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: --------------------reqStatus-------");
        super.onPause();
        if (reqStatus != null) {
            if (!reqStatus.equals("null")) {
                if (!reqStatus.equals("")) {
                    switch (reqStatus) {
                        case "0":
                        case "1":
                        default:
                            stopIntentServiceRequest();
                            System.out.println("0 state onpause!!~~~");
                            break;
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "5.1":
                        case "5.2":
                        case "5.3":
                        case "6":
                        case "6.1":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                checkOverlayPermissionNew();
                            }
                            break;
                    }
                }
            }
        }
        Utils.MAP_ISSHOWING = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                show = true;
                return;
            } else /*if (overlayHelper != null) {*/
                if (overlayHelper != null && overlayHelper.canDrawOverlays()) {
                    show = true;
                    return;
                    /*}*/
                } else {
                    builder.setTitle(getResources().getString(R.string.alert));
                    builder.setMessage(getResources().getString(R.string.aler_overlay_application));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getResources().getString(R.string.turn_on), (dialog, which) -> {
                        dialog.dismiss();
                        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivityForResult(intent, CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE);
                        show = false;
                    });

                    builder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                        dialog.dismiss();
                        show = false;
                        startFloatingViewService(MainActivity.this, false);
                    });

                    builder.setNeutralButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

                    Log.e(TAG, "onStart:~~~~~~~~~~~~~~~~ " + show);

                    if (builder != null) {
                        if (!show) {
                            builder.show();
                            show = true;
                        }
                    }
                }
        }
        Utils.MAP_ISSHOWING = true;
        Log.e(TAG, "onStart: --------------------------->>>");
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: --------------------reqStatus-------");
        super.onStop();
        Utils.MAP_ISSHOWING = false;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.e(TAG, "onDestroy: ~~~");

        try {

            if (adViewFacebook != null) {
                adViewFacebook.destroy();
            }

            if (adViewGoogle != null) {
                adViewGoogle.destroy();
            }

            stopIntentServiceRequest();

            unregisterReceiver(mGpsSwitchStateReceiver);
            Utils.MAP_ISSHOWING = false;
            Utils.CURRENT_TAG = null;

            if (!MainActivity.this.isFinishing() && !builder.show().isShowing()) {
                builder.setOnDismissListener(DialogInterface::dismiss);
            }

            if (driverAvailableDatabaseBRef != null) {
                driverAvailableDatabaseBRef.removeEventListener(driverAvailableValueEventListener);
            }
            if (tripStatusDatabaseRef != null) {
                tripStatusDatabaseRef.removeEventListener(tripStatusValueEventListener);
            }
            if (driverSearchDatabaseBRef != null) {
                driverSearchDatabaseBRef.removeEventListener(driverSearchValueEventListener);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish: called");
        /*if (dialogTripSummary != null) {
            Log.i(TAG, "finish: trip summary called dismiss");
            dialogTripSummary.dismiss();
        }*/
        super.finish();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void callClearStage(String requested_driverId) {

        // if (driverId.equals(requested_driverId)) {
        if (!reqStatus.equals("2") && !reqStatus.equals("3") && !reqStatus.equals("4") && !reqStatus.equals("5") && !reqStatus.equals("6") && !reqStatus.equals("7")) {

            spEditTripState.putString(Utils.SP_MAP_TRIPSTATE_DISTANCE, "0");
            spEditTripState.apply();

            //countDownTimerDistanceDuration.cancel();
            //countDownTimerDistanceDurationAccept.cancel();
            strTripstate = "Normalstate";
            lytOnlineOffline.setVisibility(View.VISIBLE);
            lytRequestReceive.setVisibility(View.GONE);
            lytJobInprogress.setVisibility(View.GONE);
            lytCardPayment.setVisibility(View.GONE);
            lytCardPaymentTitleMsg.setVisibility(View.GONE);

            countDownTimerNormalReceivedRequest.cancel();

            stopRepeatingTask();
        }
    }

    private void callClearFirebase(String key, String requested_driverId) {

        // if (driverId.equals(requested_driverId)) {
        if (!reqStatus.equals("2") && !reqStatus.equals("3") && !reqStatus.equals("4") && !reqStatus.equals("5") && !reqStatus.equals("6") && !reqStatus.equals("7")) {

            stopPlaying();
            stopPlaying();

            DatabaseReference refs = FirebaseDatabase.getInstance().getReference();
            refs.child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER).child(Utils.FIREBASE_SERVICE_STATUS).setValue(key);

            // removeRequestProviderDetailsFirebase();

            stopRepeatingTask();
        }

    }

    private void callRemoveDriver() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strCustomerBookingIdFB);
        map.put("driver_id", driverId);
        Log.e("strbookingId", "~~~~" + map);
        Call<BankModel> call2 = apiInterface.docallRemoveDriver(map);
        call2.enqueue(new Callback<BankModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Log.e(TAG, "onResponse:Time out success ");
                    } else {
                        Log.e(TAG, "onResponse:Time out failure ");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });

    }

    private void callCancelBooking(String key) {

        setVisibility(dialogRequesting, false);
        setVisibility(llParentLytBackupGuardRequestAccepted, false);

        if (countDownTimerSOSBackupRequest != null) {
            countDownTimerSOSBackupRequest.cancel();
        }
        if (rippleBackgroundRequesting != null) {
            rippleBackgroundRequesting.stopRippleAnimation();
        }

        removeProviderBackupStatus(key);
    }

    public void removeProviderBackupStatus(String key) {
        Log.e(TAG, "removeCustomerAcceptStatus: " + key);
        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).
                child(driverId).child(Utils.FIREBASE_BACKUP_REQUEST).setValue(key);
    }

    public void showAlertDialogTimeOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(), getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        //SpannableString spannableString = new SpannableString(getString(R.string.my_wallet_add_money_btn_text));
        //spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getResources().getString(R.string.request_time_out));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setMessage(spannableStrings);

        builder.setPositiveButton(spannableStringss, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void callRequestBooking() {

        HashMap<String, Object> map = new HashMap<>();

        /*mode:request
        booking_id
        driver_id*/

        map.put("mode", "request");
        map.put("booking_id", strBookingId);
        map.put("driver_id", driverId);

        Log.e("callRequestBooking", "~~~~" + map);

        showProgressDialog(MainActivity.this);

        Call<SendRequestModel> call2 = apiInterface.doBackupRequestSendDetails(map);
        call2.enqueue(new Callback<SendRequestModel>() {
            @Override
            public void onResponse(@NonNull Call<SendRequestModel> call, @NonNull Response<SendRequestModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Request saved successfully.")) {

                        removeProviderBackupStatus("1");

                        if (countDownTimerSOSBackupRequest != null) {
                            countDownTimerSOSBackupRequest.start();
                        }

                        setVisibility(dialogRequesting, true);
                        setVisibility(llParentLytBackupGuardRequestAccepted, false);

                        if (rippleBackgroundRequesting != null) {
                            rippleBackgroundRequesting.startRippleAnimation();
                        }

                        String strbookingId = response.body().getServiceRequest().getBookingId().toString();
                        calltimeout(strbookingId, driverId);

                        if (!strbookingId.equals("null") && !strbookingId.equals("")) {
                            btnCancelRequest.setVisibility(View.VISIBLE);
                        } else {
                            btnCancelRequest.setVisibility(View.INVISIBLE);
                        }

                        callClickAction(btnSOSBackupRequiredJobsInProgress, true);

                    } else {

                        btnCancelRequest.setVisibility(View.INVISIBLE);

                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        hideProgressDialog();

                        setVisibility(dialogRequesting, false);
                        setVisibility(llParentLytBackupGuardRequestAccepted, false);

                        if (countDownTimerSOSBackupRequest != null) {
                            countDownTimerSOSBackupRequest.cancel();
                        }

                        if (rippleBackgroundRequesting != null) {
                            rippleBackgroundRequesting.stopRippleAnimation();
                        }

                        callClickAction(btnSOSBackupRequiredJobsInProgress, true);
                    }

                } else {
                    callClickAction(btnSOSBackupRequiredJobsInProgress, true);
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SendRequestModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
                System.out.println("~~~onFailure~~" + t);
                callClickAction(btnSOSBackupRequiredJobsInProgress, true);
            }
        });

    }

    private void calltimeout(String strbookingId, String driverId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("booking_id", strbookingId);
        map.put("driver_id", driverId);
        map.put("type", "app");
        Log.e("calltimeout", "~~~~" + map);
        Call<BankModel> call2 = apiInterface.docallTimeout(map);
        call2.enqueue(new Callback<BankModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Log.e(TAG, "onResponse:Time out success ");
                    } else {
                        Log.e(TAG, "onResponse:Time out failure ");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });

    }


    List<TripsFirebaseBackupGuardListModel> tripsFirebaseBackupGuardListModels = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    private void viewTimelineFBStatus(@NonNull DataSnapshot dataSnapshot) {

        Log.e(TAG, "viewTimelineFBStatus: query-------drivers--------dataSnapshot------>>>");
        Log.e(TAG, "onBindViewHolder: Inside firebase -------->>>>");

        if (tripsFirebaseBackupGuardListModels.size() > 0) {
            tripsFirebaseBackupGuardListModels.clear();
        }

        DataSnapshot listDataSnapshot = dataSnapshot.child("volunteers");

        for (DataSnapshot ds : listDataSnapshot.getChildren()) {

            TripsFirebaseBackupGuardListModel d = new TripsFirebaseBackupGuardListModel(MainActivity.this);
            //strExtendedStatusFB = checkStringEmptyZero(dataSnapshot, "booking_id");
            d.setBooking_id(checkStringEmptyZero(ds, "booking_id"));
            d.setDriver_id(checkStringEmptyZero(ds, "driver_id"));
            d.setDriver_first(checkStringEmpty(ds, "driver_first"));
            d.setDriver_last(checkStringEmpty(ds, "driver_last"));
            d.setDriver_mobile(checkStringEmpty(ds, "driver_mobile"));
            d.setDriver_avatar(checkStringEmpty(ds, "driver_avatar"));

            d.setStatus(checkStringEmptyZero(ds, "status"));
            d.setTrip_status(checkStringEmptyZero(ds, "trip_status"));

            d.setAccept_time(checkStringEmpty(ds, "accept_time"));
            d.setStart_time(checkStringEmpty(ds, "start_time"));
            d.setReach_time(checkStringEmpty(ds, "reach_time"));
            d.setService_start_time(checkStringEmpty(ds, "service_start_time"));
            d.setRating_status("0");

            if (Integer.parseInt(checkStringEmptyZero(ds, "trip_status")) == 7
                    || Integer.parseInt(checkStringEmptyZero(ds, "trip_status")) == 8
                    || Integer.parseInt(checkStringEmptyZero(ds, "trip_status")) == 9
                    || Integer.parseInt(checkStringEmptyZero(ds, "trip_status")) == 13) {
                setVisibility(rcvGuardDetailsList, false);
            } else {
                tripsFirebaseBackupGuardListModels.add(d);
            }

            System.out.println("getFirebaseTripsList() tripListFirebaseModel.size==>" + tripsFirebaseBackupGuardListModels.size());

        }

        if (tripsFirebaseBackupGuardListModels != null && tripsFirebaseBackupGuardListModels.size() > 0) {

            setVisibility(btnSOSBackupRequiredJobsInProgress, false);
            setVisibility(rlBackupGuardListParentLytJobsInProgress, true);

            TripFirebaseBackupGuardListAdapter tripFirebaseGuardListAdapter = new TripFirebaseBackupGuardListAdapter(MainActivity.this, tripsFirebaseBackupGuardListModels);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            rcvGuardDetailsList.setLayoutManager(linearLayoutManager);
            rcvGuardDetailsList.setAdapter(tripFirebaseGuardListAdapter);
            tripFirebaseGuardListAdapter.notifyDataSetChanged();

            callGoogleDirection();

        } else {
            setVisibility(btnSOSBackupRequiredJobsInProgress, false);
            //setVisibility(btnSOSBackupRequiredJobsInProgress, true);
            setVisibility(rlBackupGuardListParentLytJobsInProgress, false);
        }

    }


    /*-*******************************
    Twillio call Start
    **********************************/
    public static boolean CurrentlyRunningChat = false;
    private View coordinatorLayout;
    private FloatingActionButton callActionFab;
    private FloatingActionButton hangupActionFab;
    private FloatingActionButton loudspeakerActionFab;
    private FloatingActionButton holdActionFab;
    private FloatingActionButton muteActionFab;
    private Chronometer chronometer;
    private SoundPoolManager soundPoolManager;

    private View lytCall;
    private SimpleDraweeView ivCallImage;
    private CustomTextView txtCallName;

    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL";
    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";

    private NotificationManager notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private com.twilio.voice.Call activeCall;
    private int activeCallNotificationId;

    RegistrationListener registrationListener = registrationListener();
    com.twilio.voice.Call.Listener callListener = callListener();

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int SNACKBAR_DURATION = 4000;

    private String accessToken;
    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;

    private boolean isReceiverRegistered = false;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    HashMap<String, String> params = new HashMap<>();

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    /*Twillio Start End*/

    @SuppressLint("SetTextI18n")
    private void callTwilioSetup() {

        /*Twillio Start*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Utils.identity = "dofloo" + driverId;

        lytCall = findViewById(R.id.lyt_call);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        callActionFab = findViewById(R.id.call_action_fab);
        hangupActionFab = findViewById(R.id.hangup_action_fab);
        loudspeakerActionFab = findViewById(R.id.loudspeaker_action_fab);
        holdActionFab = findViewById(R.id.hold_action_fab);
        muteActionFab = findViewById(R.id.mute_action_fab);
        chronometer = findViewById(R.id.chronometer);

        ivCallImage = findViewById(R.id.iv_call_image);
        txtCallName = findViewById(R.id.txt_call_name);

        //callActionFab.setOnClickListener(callActionFabClickListener());
        hangupActionFab.setOnClickListener(hangupActionFabClickListener());
        loudspeakerActionFab.setOnClickListener(loudspeakerActionFabClickListener());
        holdActionFab.setOnClickListener(holdActionFabClickListener());
        muteActionFab.setOnClickListener(muteActionFabClickListener());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        soundPoolManager = SoundPoolManager.getInstance(this);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this Activity.
         */
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();

        /*
         * Needed for setting/abandoning audio focus during a call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Setup the UI
         */
        resetUI();

        /*
         * Displays a call dialog if the intent contains a call invite
         */
        handleIncomingCallIntent(getIntent());

        /*
         * Ensure the microphone permission is enabled
         */
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        } else {
            retrieveAccessToken();
        }


        coordinatorLayout.setOnClickListener(view -> {

        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
            }

            @Override
            public void onError(@NonNull RegistrationException error, @NonNull String accessToken, @NonNull String fcmToken) {
//                @SuppressLint("DefaultLocale")
//                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
//                Toast.makeText(MainActivity_annotation.this, message, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private com.twilio.voice.Call.Listener callListener() {
        return new com.twilio.voice.Call.Listener() {

            @Override
            public void onRinging(@NonNull com.twilio.voice.Call call) {
                Log.d(TAG, "Ringing");
            }

            @Override
            public void onConnectFailure(@NonNull com.twilio.voice.Call call, @NonNull CallException error) {
                setAudioFocus(false);
                String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                out.println("voice call test -->>" + message);
                //Snackbar.make(coordinatorLayout, message, SNACKBAR_DURATION).show();
                resetUI();
            }

            @Override
            public void onConnected(@NonNull com.twilio.voice.Call call) {
                setAudioFocus(true);
                activeCall = call;
                //    call.getState().name();
                Log.e(" callgetState", "callgetState");
                out.println("test call" + call.getState().name());
                chronometer.start();
                //   sasdsd
            }

            @Override
            public void onReconnecting(@NonNull com.twilio.voice.Call call, @NonNull CallException callException) {
            }

            @Override
            public void onReconnected(@NonNull com.twilio.voice.Call call) {
            }

            @Override
            public void onDisconnected(@NonNull com.twilio.voice.Call call, CallException error) {
                setAudioFocus(false);
                if (error != null) {
                    String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Call ended", Toast.LENGTH_LONG).show();
                }
                resetUI();
            }
        };
    }

    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {
        callActionFab.hide();
        hangupActionFab.show();
        // holdActionFab.show();
        muteActionFab.show();
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        lytCall.setVisibility(View.VISIBLE);

        chronometer.setOnChronometerTickListener(chronometer -> {
            // TODO Auto-generated method stub
            String currentTime = chronometer.getText().toString();
            // System.out.println("time-->"+currentTime);
            if (currentTime.equals("02:00")) //put time according to you
            {
                soundPoolManager.playDisconnect();
                resetUI();
                disconnect();
            }
        });

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
            //wakeLock.acquire(10*60*1000L /*10 minutes*/); // New suspend
        }
    }

    /*
     * Reset UI elements
     */
    @SuppressLint({"ObsoleteSdkInt", "UseCompatLoadingForDrawables"})
    private void resetUI() {
        //callActionFab.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp, getApplicationContext().getTheme()));
            muteActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_white_24dp, getApplicationContext().getTheme()));
            holdActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp, getApplicationContext().getTheme()));
        } else {
            loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
            muteActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
            holdActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
        }

        // muteActionFab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_white_24dp));
        holdActionFab.hide();
        holdActionFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        muteActionFab.hide();
        hangupActionFab.hide();
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
        //  holdActionFab.setBackgroundResource(R.drawable.ic_volume_up_white_24dp);
        lytCall.setVisibility(View.GONE);

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        activeCallInvite.accept(this, callListener);
        notificationManager.cancel(activeCallNotificationId);
    }

    /*
     * Disconnect from Call
     */
    private void disconnect() {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void audioOption() {
        if (activeCall != null) {
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_phonelink_ring_white_24dp, getApplicationContext().getTheme()));
                } else {
                    loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_phonelink_ring_white_24dp));
                }

            } else {
                audioManager.setSpeakerphoneOn(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp, getApplicationContext().getTheme()));
                } else {
                    loudspeakerActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
                }

            }
        }
    }

    private void hold() {
        if (activeCall != null) {
            boolean hold = !activeCall.isOnHold();
            activeCall.hold(hold);

            // Set fab as pressed when call is on hold
            ColorStateList holdActionBarCsl = hold ? ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)) : ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent));
            holdActionFab.setBackgroundTintList(holdActionBarCsl);
        }
    }

    private void mute() {
        if (activeCall != null) {

            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
            if (mute) {
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_white_off_24dp));
            } else {
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_white_24dp));
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT).setAudioAttributes(playbackAttributes).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener(i -> {
                    }).build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    int focusRequestResult = audioManager.requestAudioFocus(focusChange -> {
                    }, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }

                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }

    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Microphone permissions needed. Please allow in your application settings.", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void retrieveAccessToken() {

        if (BuildConfig.DEBUG) {
            String tokenURL = RETROFIT_URL_CALL_CHAT_ACCESS + "?identity=" + Utils.identity + "&device=" + "android_driver";
            System.out.println("~~tokenURL~~~" + tokenURL);
        }
        APIInterface apiInterface = APIClient.getClient_chat().create(APIInterface.class);
        Call<ChatAccessModel> call2 = apiInterface.doCallAccessToken("android_driver", Utils.identity);
        showProgressDialog(MainActivity.this);
        call2.enqueue(new Callback<ChatAccessModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ChatAccessModel> call, @NonNull Response<ChatAccessModel> response) {
                hideProgressDialog();
                if (response.body() != null) {
                    String accessToken = response.body().getToken();
                    MainActivity.this.accessToken = accessToken;
                    registerForCallInvites();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatAccessModel> call, @NonNull Throwable t) {
                call.cancel();
                hideProgressDialog();
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                new MyAsyncTask1(MainActivity.this).execute();
            }
        });

    }

    public static AlertDialog createIncomingCallDialog(Context context, DialogInterface.OnClickListener answerCallClickListener, DialogInterface.OnClickListener cancelClickListener) {

        Typeface tf = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);

        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.twilio_incoming_call));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString accept = new SpannableString(context.getResources().getString(R.string.twilio_accept));
        accept.setSpan(tfSpan, 0, accept.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString reject = new SpannableString(context.getResources().getString(R.string.twilio_reject));
        reject.setSpan(tfSpan, 0, reject.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString message = new SpannableString(context.getResources().getString(R.string.twilio_user_is_calling));
        message.setSpan(tfSpan, 0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);

        alertDialogBuilder.setTitle(spannableString);

        alertDialogBuilder.setPositiveButton(accept, answerCallClickListener);
        alertDialogBuilder.setNegativeButton(reject, cancelClickListener);
        //   alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setOnCancelListener(DialogInterface::dismiss);


        return alertDialogBuilder.create();
    }

    private void handleIncomingCallIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_INCOMING_CALL:
                    activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);
                    if (activeCallInvite != null) {
                        soundPoolManager.playRinging();
                        alertDialog = createIncomingCallDialog(MainActivity.this, answerCallClickListener(), cancelCallClickListener());
                        alertDialog.show();
                        activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);
                    } else {
                        System.out.println("else calling");
                    }
                    break;
                case ACTION_CANCEL_CALL:
                    if (alertDialog != null && alertDialog.isShowing()) {
                        soundPoolManager.stopRinging();
                        alertDialog.cancel();
                    }
                    break;
                case ACTION_FCM_TOKEN:
                    retrieveAccessToken();
                    break;
            }
        }
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return (dialog, which) -> {
            soundPoolManager.stopRinging();
            answer();
            setCallUI();
            alertDialog.dismiss();
        };
    }

    private DialogInterface.OnClickListener cancelCallClickListener() {
        return (dialogInterface, i) -> {
            soundPoolManager.stopRinging();
            if (activeCallInvite != null) {
                activeCallInvite.reject(MainActivity.this);
                notificationManager.cancel(activeCallNotificationId);
            }
            alertDialog.dismiss();
        };
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_INCOMING_CALL);
            intentFilter.addAction(ACTION_CANCEL_CALL);
            intentFilter.addAction(ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_CALL) || action.equals(ACTION_CANCEL_CALL)) {
                /*
                 * Handle the incoming or cancelled call invite
                 */
                handleIncomingCallIntent(intent);
            }
        }
    }

    private void registerForCallInvites() {
        /*final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
            System.out.println("accessToken=>" + accessToken);
            Voice.register(accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
        }*/

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();

            System.out.println("refreshedToken~~registerForCallInvites=>" + token);
            System.out.println("accessToken=>" + accessToken);

            Voice.register(accessToken, Voice.RegistrationChannel.FCM, token, registrationListener);

                    /*SharedPreferences.Editor getState = getApplicationContext().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE).edit();
                    getState.putString(Utils.SP_DEVICE_ID, token);
                    getState.apply();*/

            // Log and toast
                    /*String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/
        });
    }

    /*private View.OnClickListener callActionFabClickListener() {
        return v -> {
            toIdentity = "InstaGuard_user" + customerId;
            params.put("to", toIdentity);
            //params.put("bookingId", strBookingId);
            ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                    .params(params)
                    .build();
            activeCall = Voice.connect(MainActivity.this, connectOptions, callListener);
            setCallUI();
        };
    }*/

    private View.OnClickListener hangupActionFabClickListener() {
        return v -> {
            soundPoolManager.playDisconnect();
            resetUI();
            disconnect();
        };
    }

    private View.OnClickListener loudspeakerActionFabClickListener() {
        return v -> audioOption();
    }


    private View.OnClickListener holdActionFabClickListener() {
        return v -> hold();
    }

    private View.OnClickListener muteActionFabClickListener() {
        return v -> mute();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsyncTask1 extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private final MainActivity activity;

        MyAsyncTask1(MainActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        private final Context context;

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("loading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog != null && dialog.isShowing()) {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (success) {
                System.out.print("success");
            } else {
                System.out.print("failure");
            }
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {
                // ... processing ...
                String url = RETROFIT_URL_CALL_CHAT_ACCESS + "?identity=" + Utils.identity + "&device=" + "android_driver";
                URL yahoo = null;
                try {
                    yahoo = new URL(url);
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            System.out.println("TokenSuccess" + inputLine);
                            MainActivity.this.accessToken = inputLine;
                            registerForCallInvites();

                        }
                        in.close();

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                // getOnlineCategoryInfo();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /*Twillio call End*/


    private String userID = "";

    private RecyclerView mMessagesRecyclerView;
    private MessagesAdapter mMessagesAdapter;
    private final ArrayList<Message> mMessages = new ArrayList<>();

    private CustomEditText mWriteMessageEditText;
    //private Button mSendChatMessageButton;
    private ImageView mSendChatMessageButton;
    private CustomTextView empty;
    private ConversationsClient mConversationsClient;
    private Conversation mGeneralChannel;
    ImageView imgBack;
    String sendDateTime;
    String datetime = "";

    RelativeLayout loading;
    View parentView;
    View lytChat;
    String accessTokenChat;
    String DEFAULT_CHANNEL_NAME = "";

    private void callTwilioChatSetup() {

        lytChat = findViewById(R.id.lyt_chat);

        imgBack = findViewById(R.id.img_back);
        parentView = findViewById(R.id.rl_coordinate_lyt);
        loading = findViewById(R.id.rl_progress_bar_lyt);

        loading.setVisibility(View.VISIBLE);

        // DEFAULT_CHANNEL_NAME = "Channel:" + customerId + "," + driverId + "," + bookingId;
        Log.e(TAG, "onCreate: DEFAULT_CHANNEL_NAME" + DEFAULT_CHANNEL_NAME);

        mMessagesRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mMessagesRecyclerView.setLayoutManager(layoutManager);

        mMessagesAdapter = new MessagesAdapter();
        mMessagesRecyclerView.setAdapter(mMessagesAdapter);

        mWriteMessageEditText = findViewById(R.id.writeMessageEditText);
        mSendChatMessageButton = (ImageView) findViewById(R.id.sendChatMessageButton);
        View rl_parent_lyt_chat = findViewById(R.id.rl_parent_lyt_chat);
        empty = findViewById(R.id.empty);

        rl_parent_lyt_chat.setOnClickListener(v -> baseActivity.hideKeyBoard(MainActivity.this));

        SharedPreferences.Editor getState_ = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        getState_.putInt("chat_click", 0);
        getState_.apply();

        imgBack.setOnClickListener(v -> {

            getState_.putInt("chat_click", 1);
            getState_.apply();

            if (lytChat.getVisibility() == View.VISIBLE) {

                // baseActivity.hideKeyBoard(MapsActivity_annotation.this);

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                lytChat.setVisibility(View.GONE);
            }

        });

        mSendChatMessageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                mSendChatMessageButton.setEnabled(false);
                if (mGeneralChannel != null) {
                    String messageBody = mWriteMessageEditText.getText().toString();
                    Calendar c = Calendar.getInstance();
                    //   SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        df = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
                    }

                    sendDateTime = df.format(c.getTime());
                    if (mWriteMessageEditText.getText().toString().trim().length() <= 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.chat_field_empty), Toast.LENGTH_LONG).show();
                        mSendChatMessageButton.setEnabled(true);
                    } else if (mWriteMessageEditText.getText().toString().equals("") || mWriteMessageEditText.length() < 1 || mWriteMessageEditText.getText().toString().equals(" ")) {
                        Toast.makeText(MainActivity.this, getString(R.string.chat_field_empty), Toast.LENGTH_SHORT).show();
                        mSendChatMessageButton.setEnabled(true);
                    } else {

                        Log.d(TAG, "Message created");

                        mGeneralChannel.prepareMessage().setBody(messageBody).buildAndSend(new CallbackListener() {
                            @Override
                            public void onSuccess(Object result) {
                                runOnUiThread(() -> {
                                    Log.e(TAG, "run: msg" + result.toString());
                                    // need to modify user interface elements on the UI thread
                                    mWriteMessageEditText.setText("");
                                    mSendChatMessageButton.setEnabled(true);
                                });
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                CallbackListener.super.onError(errorInfo);
                                Log.e(TAG, "Error sending message: " + errorInfo.getMessage());
                            }
                        });

                    }
                } else {
                    Log.d(TAG, "onClick: else mGeneralChannel ");
                }
            }
        });

        System.out.println("~~PermissionUtils~~~" + PermissionUtils.checkPermission_Notification(MainActivity.this));


        if (PermissionUtils.checkPermission_Notification(MainActivity.this).equals("1")) {
            // success
            if (NotificationManagerCompat.from(MainActivity.this).areNotificationsEnabled()) {
                // Toast.makeText(this, "Notification is on for this Application", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Notification is off for this Application", Toast.LENGTH_SHORT).show();
                displayNeverAskAgainDialog();
            }
        } else if (PermissionUtils.checkPermission_Notification(MainActivity.this).equals("2")) {
            if (!aBoolean) {
                displayNeverAskAgainDialog();
            }
        } else {
            checkAndRequestPermissions(MainActivity.this);
        }

    }

    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mMessageTime, mMessageDate, rightUser;
            TextView mMessageTextView, mMessageTextView1;
            TextView mMessageTime1, mMessageDate1, leftUser;
            View right, left;

            ViewHolder(View view) {
                super(view);
                right = view.findViewById(R.id.right);
                mMessageTextView = (TextView) view.findViewById(R.id.btn_message_boddy);
                mMessageTime = (TextView) view.findViewById(R.id.txt_message_time);
                rightUser = (TextView) view.findViewById(R.id.txt_message_time_you);
                // mMessageDate = (TextView) view.findViewById(R.id.txt_message_date);

                left = view.findViewById(R.id.left);
                leftUser = view.findViewById(R.id.txt_message_time_from_customer);
                mMessageTextView1 = (TextView) view.findViewById(R.id.btn_message_boddy1);
                mMessageTime1 = (TextView) view.findViewById(R.id.txt_message_time1);
                // mMessageDate1 = (TextView) view.findViewById(R.id.txt_message_date1);

            }
        }

        MessagesAdapter() {
        }

        @NonNull
        @Override
        public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View messageTextView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_text_view, parent, false);

            /*TextView messageTextView = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_text_view, parent, false);*/
            return new MessagesAdapter.ViewHolder(messageTextView);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {

            Message message = mMessages.get(position);
            String messageTexts = String.format(message.getBody());

            //    String messageTime = String.format(String.valueOf(message.getTimeStampAsDate()));
            Log.d(TAG, "Buddy id : " + driverId);
            Log.d(TAG, "User id : " + struserIdFB);
            Log.d(TAG, "Author Name : " + message.getAuthor());
            Log.d(TAG, "date created : " + message.getDateCreated());
            Log.d(TAG, "date created : " + message.getDateCreatedAsDate());

            Log.d(TAG, "date created : messageTexts--> " + messageTexts);

            message.getAuthor();
            message.getDateCreatedAsDate();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            //    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat outputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                outputFormat = new SimpleDateFormat("MM/dd/YYYY hh:mm a");
            }
            Date date = null;

            try {
                date = inputFormat.parse(message.getDateCreatedAsDate().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String formattedDate = outputFormat.format(date);
            Log.d(TAG, "date format=====>>>>>>>>>" + formattedDate);
            out.println("driverMIden::" + mIdentity);

            if (message.getAuthor().contains(mIdentity)) {

                holder.right.setVisibility(View.VISIBLE);
                holder.left.setVisibility(View.GONE);

                holder.rightUser.setVisibility(View.GONE);
                holder.leftUser.setVisibility(View.GONE);

                holder.mMessageTextView.setText(messageTexts);
                holder.mMessageTime.setText(formattedDate);

                //holder.mMessageDate.setText(messageDate);
            } else if (driverId.contains(userID)) {

                holder.right.setVisibility(View.GONE);
                holder.left.setVisibility(View.VISIBLE);

                holder.rightUser.setVisibility(View.GONE);
                holder.leftUser.setVisibility(View.GONE);

                holder.mMessageTextView1.setText(messageTexts);
                holder.mMessageTime1.setText(formattedDate);

                //holder.mMessageDate1.setText(messageDate);
            } else {
                Log.d(TAG, "userId is driverId else ===>>>>>" + userID);
            }
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }

    private void retrieveAccessTokenfromServer(/*String from*/) {

        out.println("~~retrieveAccessTokenfromServer~~count~~>> " + getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).getInt("chat_click", 0) + " <<<<");
        if (getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).getInt("chat_click", 0) == 1) {
            /*if (!from.equals("home")) {
                if (lytChat.getVisibility() == View.GONE) {
                    lytChat.setVisibility(View.VISIBLE);
                }
            }else {
                if (lytChat.getVisibility() == View.VISIBLE) {
                    lytChat.setVisibility(View.GONE);
                }
            }*/

            callClickAction(ivMessageJobinprogress, true);

            if (lytChat.getVisibility() == View.GONE) {
                lytChat.setVisibility(View.VISIBLE);
            }

        } else {

            /*if (!from.equals("home")) {
                if (lytChat.getVisibility() == View.GONE) {
                    lytChat.setVisibility(View.VISIBLE);
                }
            } else {*/
            DEFAULT_CHANNEL_NAME = "Channel:" + struserIdFB + "," + driverId + "," + strBookingId;
            mIdentity = "driver" + driverId + "InstaGuard";
            mUserId = "customer" + driverId + "InstaGuard";

            APIInterface apiInterface = APIClient.getClient_chat().create(APIInterface.class);
            Call<ChatAccessModel> call2 = apiInterface.doChatAccessToken(driverId, mIdentity);
            showProgressDialog(MainActivity.this);
            call2.enqueue(new Callback<ChatAccessModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ChatAccessModel> call, @NonNull Response<ChatAccessModel> response) {
                    hideProgressDialog();
                    if (response.body() != null) {
                        String accessToken = response.body().getToken();
                        out.println("accessToken::" + accessToken);
                        setTitle("Chat");
                        ConversationsClient.Properties props = ConversationsClient.Properties.newBuilder().createProperties();
                        ConversationsClient.create(MainActivity.this, accessToken, props, mConversationsClientCallback);

                        Log.e(TAG, "onCreate: DEFAULT_CHANNEL_NAME" + DEFAULT_CHANNEL_NAME);

                        callClickAction(ivMessageJobinprogress, true);
                        // if (!from.equals("home")) {
                        if (lytChat.getVisibility() == View.GONE) {
                            lytChat.setVisibility(View.VISIBLE);
                        }
                        //}
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ChatAccessModel> call, @NonNull Throwable t) {
                    call.cancel();
                    callClickAction(ivMessageJobinprogress, true);
                    hideProgressDialog();
                    loading.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, R.string.error_retrieving_access_token, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    //2nd
    private final CallbackListener<ConversationsClient> mConversationsClientCallback = new CallbackListener<ConversationsClient>() {
        @Override
        public void onSuccess(ConversationsClient conversationsClient) {
            mConversationsClient = conversationsClient;
            conversationsClient.addListener(mConversationsClientListener);
            Log.d(TAG, "Success creating Twilio Conversations Client" + conversationsClient.getMyConversations());
            Log.d(TAG, "Success creating Twilio Conversations Client" + conversationsClient.getMyConversations().size());
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            Log.e(TAG, "Error creating Twilio Conversations Client: " + errorInfo.getMessage());
        }
    };

    // 3rd
    private final ConversationsClientListener mConversationsClientListener = new ConversationsClientListener() { // 4th
        @Override
        public void onClientSynchronization(ConversationsClient.SynchronizationStatus synchronizationStatus) {
            if (synchronizationStatus == ConversationsClient.SynchronizationStatus.COMPLETED) {
                loadChannels();
            }
        }

        @Override
        public void onNewMessageNotification(String conversationSid, String messageSid, long messageIndex) {

        }

        @Override
        public void onAddedToConversationNotification(String conversationSid) {

        }

        @Override
        public void onRemovedFromConversationNotification(String conversationSid) {

        }

        @Override
        public void onNotificationSubscribed() {

        }

        @Override
        public void onNotificationFailed(ErrorInfo errorInfo) {

        }

        @Override
        public void onConnectionStateChange(ConversationsClient.ConnectionState state) {

        }

        @Override
        public void onTokenExpired() {
        }

        @Override
        public void onTokenAboutToExpire() {
        }

        @Override
        public void onConversationAdded(Conversation conversation) {
        }

        @Override
        public void onConversationUpdated(Conversation conversation, Conversation.UpdateReason reason) {
        }

        @Override
        public void onConversationDeleted(Conversation conversation) {
        }

        @Override
        public void onConversationSynchronizationChange(Conversation conversation) {
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
        }

        @Override
        public void onUserUpdated(User user, User.UpdateReason reason) {
        }

        @Override
        public void onUserSubscribed(User user) {
        }

        @Override
        public void onUserUnsubscribed(User user) {
        }
    };

    // 4th
    private void loadChannels() {
        Log.d(TAG, "loadChannels-->" + DEFAULT_CHANNEL_NAME);

        mConversationsClient.getConversation(DEFAULT_CHANNEL_NAME, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                Log.i(TAG, "onSuccess: load channel---->" + conversation.getStatus() + "~~~" + conversation.getFriendlyName() + "~~~" + conversation.getUniqueName());

                if (conversation != null) {
                    if (conversation.getStatus() == Conversation.ConversationStatus.JOINED
                        /*|| conversation.getStatus() == Conversation.ConversationStatus.NOT_PARTICIPATING*/) {
                        Log.d(TAG, "Already Exists in Conversation: " + DEFAULT_CHANNEL_NAME);
                        mGeneralChannel = conversation;
                        out.println("mGeneral Channel " + conversation.getSid() + "~~~" + conversation.getStatus());

                        conversation.addListener(mDefaultChannelListener);

                        if (conversation.getSynchronizationStatus() == Conversation.SynchronizationStatus.ALL) {
                            loadPreviousMessages(conversation);
                        }

                        out.println("Join Conversation Listened " + conversation.getStatus() + " " + conversation.getStatus().JOINED);
                    } else {
                        Log.d(TAG, "Joining Conversation: " + DEFAULT_CHANNEL_NAME);
                        joinChannel(conversation);
                    }
                } else {
                    Log.i(TAG, "onSuccess: create channel");
                    createchannel();
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error retrieving channel: " + errorInfo.getMessage());
                createchannel();
            }

        });

    }

    // 5th
    private final ConversationListener mDefaultChannelListener = new ConversationListener() {
        @Override
        public void onMessageAdded(final Message message) {
            Log.d(TAG, "Message added" + message);
            runOnUiThread(() -> {
                // need to modify user interface elements on the UI thread
                mSendChatMessageButton.setEnabled(true);
                mMessages.add(message);
                out.println("Added Msg " + message.toString());
                mMessagesAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);

                loading.setVisibility(View.GONE);
                if (mMessages.size() > 0) {
                    empty.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            Log.i(TAG, "onMessageUpdated: ---" + message.toString());
        }

        @Override
        public void onMessageDeleted(Message message) {
            Log.d(TAG, "Message deleted");
        }

        @Override
        public void onParticipantAdded(Participant participant) {
        }

        @Override
        public void onParticipantUpdated(Participant participant, Participant.UpdateReason reason) {
        }

        @Override
        public void onParticipantDeleted(Participant participant) {
        }

        @Override
        public void onTypingStarted(Conversation channel, Participant member) {
        }

        @Override
        public void onTypingEnded(Conversation channel, Participant member) {
        }

        @Override
        public void onSynchronizationChanged(Conversation mGeneralChannel) {

            Log.d(TAG, "synchronation list channel " + mGeneralChannel.getStatus());
            String status = String.valueOf(mGeneralChannel.getStatus());
            String msg = String.valueOf(mGeneralChannel);
            out.println("channel sid " + mGeneralChannel.getSid());
            out.println("channel fName " + mGeneralChannel.getFriendlyName());
            Log.d(TAG, "synchronation status channel " + status);
            Log.d(TAG, "synchronation msg channel " + msg);

            loading.setVisibility(View.GONE);
        }
    };

    // 6th
    private void loadPreviousMessages(final Conversation conversation) {
        conversation.getLastMessages(100, new CallbackListener<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                Log.d(TAG, "onSuccess: message list " + messages);
                mMessages.addAll(messages);
                mMessagesAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
                loading.setVisibility(View.GONE);
                if (mMessages.size() > 0) {
                    empty.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                CallbackListener.super.onError(errorInfo);
                out.println("load Msg data error " + errorInfo.toString());
            }
        });
    }

    // 7th
    private void joinChannel(Conversation conversation) {

        Log.d(TAG, "Joining Conversation getUniqueName: " + "~~~~" + conversation.getStatus() + "~~" + conversation.getUniqueName() + "~~~~~~" + DEFAULT_CHANNEL_NAME);
        Log.d(TAG, "Joining Conversation getUniqueName: " + "~~~" + conversation);
        Log.d(TAG, "channel Id from join Conversation " + "~~~" + conversation.getSid().toString());

        // usual flow
        if (conversation.getStatus() == Conversation.ConversationStatus.JOINED) {
            // already in the channel, load the messages
            mGeneralChannel.addListener(mDefaultChannelListener);
            out.println("Join Conversation Listened " + conversation.getStatus() + " " + Conversation.ConversationStatus.JOINED);
        } else {

            //  Log.i(TAG, "joinChannel: get memeners-->"+channel.getMembers().toString());
            // join the channel
            conversation.join(new StatusListener() {
                @Override
                public void onSuccess() {

                    mGeneralChannel = conversation;
                    out.println("mGeneralChannel " + mGeneralChannel);
                    mGeneralChannel.setUniqueName(DEFAULT_CHANNEL_NAME, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Joined default channel" + conversation);
                            mGeneralChannel.addListener(mDefaultChannelListener);
                        }
                    });

                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    Log.e(TAG, "Error joining channel: " + errorInfo.getMessage());

                    mGeneralChannel = conversation;
                    out.println("mGeneralChannelError " + mGeneralChannel);
                    mGeneralChannel.setUniqueName(DEFAULT_CHANNEL_NAME, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Joined default channel" + conversation);
                            mGeneralChannel.addListener(mDefaultChannelListener);

                            conversation.getLastMessages(100, new CallbackListener<List<Message>>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onSuccess(List<Message> messages) {
                                    Log.d(TAG, "onSuccess: message list " + messages.toString());
                                    mMessages.addAll(messages);
                                    mMessagesAdapter.notifyDataSetChanged();
                                    mMessagesRecyclerView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
                                    //hideProgressDialog();
                                    loading.setVisibility(View.GONE);
                                    if (mMessages.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    // 8th
    private void createchannel() {
        Log.d(TAG, "Creating Conversation: " + DEFAULT_CHANNEL_NAME);
        mConversationsClient.createConversation(DEFAULT_CHANNEL_NAME, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) {
                    Log.d(TAG, "Joining Conversation in Create channel : " + DEFAULT_CHANNEL_NAME);
                    Log.d(TAG, "dispatcher id : " + struserIdFB);

                    conversation.setUniqueName(DEFAULT_CHANNEL_NAME, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            out.println("twilio user unique name was set");
                        }
                    });
                    conversation.addParticipantByIdentity(mUserId, null, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            // Participant added successfully
                            Log.d("Twilio", "Participant added: " + mUserId);
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            StatusListener.super.onError(errorInfo);
                            out.println("create channel error " + errorInfo.toString());
                        }
                    });

                    joinNewConv(conversation);

                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error creating conversation: " + errorInfo.getMessage());
            }
        });
    }

    //9th
    private void joinNewConv(final Conversation conversation) {
        Log.d(TAG, "Joining Conversation: " + conversation.getUniqueName());
        out.println("new conversion satus " + conversation.getStatus());

        if (conversation.getStatus() == Conversation.ConversationStatus.JOINED) {
            DEFAULT_CHANNEL_NAME = "Channel:" + struserIdFB + "," + driverId + "," + strBookingId;
            mIdentity = "driver" + driverId + "InstaGuard";
            mUserId = "customer" + driverId + "InstaGuard";

            mGeneralChannel = conversation;
            Log.d(TAG, "Already joined default conversation");
            conversation.addListener(mDefaultChannelListener);
            return;
        }


        conversation.join(new StatusListener() {
            @Override
            public void onSuccess() {
                mGeneralChannel = conversation;
                Log.d(TAG, "Joined default conversation");
                conversation.addListener(mDefaultChannelListener);
                loadPreviousMessages(conversation);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "Error joining conversation: " + errorInfo.getMessage());
            }
        });
    }

    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS_N = 0x26;

    private void checkAndRequestPermissions(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int notificationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS);
            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS_N);
        }
    }

    private void displayNeverAskAgainDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Typeface tf = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableStrings = new SpannableString(getResources().getString(R.string.permit_manually));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getResources().getString(R.string.cancel));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringssd = new SpannableString(getResources().getString(R.string.ok));
        spannableStringssd.setSpan(tfSpan, 0, spannableStringssd.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableString = new SpannableString(getString(R.string.permit_manually_msg));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(spannableStrings);

        builder.setMessage(spannableString);

        builder.setCancelable(false);

        builder.setPositiveButton(spannableStringssd, (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton(spannableStringss, null);
        builder.show();
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Typeface tf = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableStrings = new SpannableString(getResources().getString(R.string.permit_manually));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringssd = new SpannableString(getResources().getString(R.string.ok));
        spannableStringssd.setSpan(tfSpan, 0, spannableStringssd.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getResources().getString(R.string.cancel));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // SpannableString spannableString = new SpannableString(getString(R.string.permit_manually_msg));
        SpannableString spannableString = new SpannableString(getString(R.string.permit_manually_msg_nec));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        new AlertDialog.Builder(this).setTitle(spannableStrings).setMessage(spannableString).setPositiveButton(spannableStringssd, okListener).setNegativeButton(spannableStringss, okListener).create().show();
    }


    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    GeofenceBroadcastReceiver geofenceBroadcastReceiver;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    private void callGeoFencingInitViews() {

        try {

            geofencingClient = LocationServices.getGeofencingClient(MainActivity.this);
            geofenceHelper = new GeofenceHelper(MainActivity.this);

            /*

            AcceptPickupLat = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LAT_TRIPSTATE, "9.921860583985165");
            AcceptPickupLng = getspFBCategory.getString(Utils.SP_FB_ACCEPT_PICKUP_LNG_TRIPSTATE, "78.09497182081704");
            AcceptPickupLatLng = new LatLng(parseDouble(AcceptPickupLat), parseDouble(AcceptPickupLng));

            out.println("~~~~AcceptPickupLat~~~~" + AcceptPickupLat);
            out.println("~~~~AcceptPickupLng~~~~" + AcceptPickupLng);

            findViewById(R.id.txt_check_geofence).setOnClickListener(view -> {
                if (Build.VERSION.SDK_INT >= 29) {
                    //We need background permission
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        handleMapLongClick(AcceptPickupLatLng);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            //We show a dialog and ask for permission
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        }
                    }
                } else {
                    handleMapLongClick(AcceptPickupLatLng);
                }
            });*/

            /* geofencingClient = LocationServices.getGeofencingClient(MainActivity.this);
            callCreateGeoFencing();
            new Handler().postDelayed(this::callRegisterGeofence, 1000);*/

        } catch (Exception e) {
            e.printStackTrace();
            out.println("~~~callGeoFencingInitViews~~Exception~~~>" + e);
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    private void handleMapLongClick(LatLng acceptPickupLatLng) {

        geofenceBroadcastReceiver = new GeofenceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.CUSTOM_INTENT_GEOFENCE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(geofenceBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(geofenceBroadcastReceiver, intentFilter);
        }

        addGeofence(acceptPickupLatLng, Utils.GEOFENCE_RADIUS_IN_METERS);
        //addCircle(acceptPickupLatLng, Utils.GEOFENCE_RADIUS_IN_METERS);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        out.println("~~~~~~~latLng~````~~~" + latLng);
        /*if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            handleMapLongClick(latLng);
        }*/
    }

    private void addCircle(LatLng latLng, int radius) {
        if (mMap != null) {
            mMap.clear();
            out.println("~~~~addGeofence~~~CircleOptions~" + latLng);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(radius);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(64, 255, 0, 0));
            circleOptions.strokeWidth(4);
            mMap.addCircle(circleOptions);
        }
    }

    private void addGeofence(LatLng latLng, int radius) {

        out.println("~~~~addGeofence~~~~" + latLng);
        out.println("~~~~addGeofence~~~radius~" + radius);

        Geofence geofence = geofenceHelper.getGeofence(driverId, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Geofence Added..."))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
                    Log.d(TAG, "onFailure: " + errorMessage);
                });
    }

    private void callStopGeoFencing() {

        try {
            if (geofenceHelper != null) {
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent(MainActivity.this);
                geofencingClient.removeGeofences(pendingIntent)
                        .addOnSuccessListener(this, aVoid -> {
                            // Geofences removed
                            out.println("~~~``geofencingClient~~~removeGeofences~");
                        })
                        .addOnFailureListener(this, e -> {
                            // Failed to remove geofences
                            out.println("~~~``geofencingClient~~~removeGeofences~" + e);
                        });
            }

            if (geofenceBroadcastReceiver != null) {
                unregisterReceiver(geofenceBroadcastReceiver);
                geofenceBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
