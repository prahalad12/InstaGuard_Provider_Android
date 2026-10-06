package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.PastJobsAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.RatingBar.SimpleRatingBar;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.GroupDriverDatum;
import com.instaguard.provider.Models.PastJobsData;
import com.instaguard.provider.Models.PastJobsModel;
import com.instaguard.provider.Models.RatingUserModel;
import com.instaguard.provider.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.convertCurrencyPrice;
import static com.instaguard.provider.Activities.BaseActivity.convertHoursToDaysHoursMinutes;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.setVisibility;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;


public class YourJobsPastFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    static CustomEditText edtDob;
    View view;

    SharedPreferences.Editor putSpState;
    ImageButton imgbtnBack;
    SharedPreferences spCommon;
    String userAccessToken;
    RecyclerView rvPastJobs;
    RelativeLayout rlEmptyview;
    ProgressBar progressBar;
    List<PastJobsData> getve;

    PastJobsAdapter adapter;
    public String TAG = "OnPastJobsFragment";

    SharedPreferences SP_Prefs;
    String driverId;
    private AwesomeValidation Validation;


    SharedPreferences getspCurrency;
    String strLanguage, strCurrencySymbol, strLanguageShortName;


    private Dialog dialogShowTripDetail;
    private SimpleDraweeView ivprofpicPastjobdetail;
    private CustomTextView txt_drivername_pastjob_detail;
    private CustomTextView txtDayFeeLabel;
    private CustomTextView txtAgentFeeLabel;
    private CustomTextView txt_booking_pastjob_detail;
    private CustomTextView txt_job_request_date_time;
    private CustomTextView txt_pickup_pastjob_detail;
    private CustomTextView txt_category_past_job_detail;
    private CustomTextView txt_paymentmode;
    private CustomTextView txt_trip_status;
    private ImageView imgbtn_back;
    private CustomTextView txt_base_amount_trip_summary;
    private CustomEditText txt_current_charge;
    private CustomEditText txt_material_fee;
    private CustomEditText txt_misc_fee;
    private CustomEditText txt_discount_driver;
    private CustomTextView txt_final_total;
    private CustomTextView txt_your_final_total;
    private CustomEditText edt_comment_pastjob_detail;
    private CustomEditText txtAgentCharge;
    private CustomEditText txtDayCharge;
    private CustomButton btn_rating_payment;
    private CardView cv_rating_lyt;
    private LinearLayout ll_current_charge_lyt;
    private LinearLayout ll_material_fee_lyt;
    private LinearLayout ll_misc_fee_lyt;
    private LinearLayout ll_discount_lyt;
    private LinearLayout ll_final_total_lyt;
    private SimpleRatingBar ratingBar_pastjob_detail;
    private SimpleRatingBar ratingBar_Driver_pastjob_detail;
    private SimpleRatingBar ratingBar_Trip_pastjob_detail;

    CustomTextView txtNoofHrsTripSummary;
    CustomTextView txtPricePerHrsLabelTripSummary;
    CustomTextView txtPricePerHrsTripSummary;
    CustomTextView txtVatAmountTripSummary;

    CustomTextView txtExtendNoofHrsTripSummary;
    TableRow trExtendNoofHrsTripSummary;
    View progressBarView;


    public YourJobsPastFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static YourJobsPastFragment newInstance(String param1, String param2) {
        YourJobsPastFragment fragment = new YourJobsPastFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yourjobs_past, container, false);

        SP_Prefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        putSpState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        getspCurrency = requireActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = getspCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = getspCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        strCurrencySymbol = getspCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);
        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        Log.e("userId~~PastJobsFragment~~", driverId);

        initviews();

        getPastJobsCall();

        OnClick();

        onBackButton(view);

        return view;
    }

    private void initviews() {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        rvPastJobs = (RecyclerView) view.findViewById(R.id.rv_pastjobs);
        rlEmptyview = (RelativeLayout) view.findViewById(R.id.rl_emptyview);

        //Dialog Show trip details
        dialogShowTripDetail = new Dialog(requireActivity(), R.style.AppTheme);
        dialogShowTripDetail.setContentView(R.layout.layout_pastjob_details);
        dialogShowTripDetail.setCancelable(false);

        imgbtn_back = (ImageView) dialogShowTripDetail.findViewById(R.id.imgbtn_back);

        ivprofpicPastjobdetail = (SimpleDraweeView) dialogShowTripDetail.findViewById(R.id.iv_prof_pic_pastjob_detail);
        txt_drivername_pastjob_detail = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_drivername_pastjob_detail);
        txt_booking_pastjob_detail = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_booking_pastjob_detail);
        txt_job_request_date_time = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_job_request_date_time);
        txt_pickup_pastjob_detail = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_pickup_pastjob_detail);
        txt_category_past_job_detail = (CustomTextView) dialogShowTripDetail.findViewById(R.id.category_past_job_detail);

        txt_paymentmode = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_paymentmode);
        txt_trip_status = (CustomTextView) dialogShowTripDetail.findViewById(R.id.txt_trip_status);

        txt_base_amount_trip_summary = dialogShowTripDetail.findViewById(R.id.txt_base_amount_trip_summary);
        txt_current_charge = (CustomEditText) dialogShowTripDetail.findViewById(R.id.txt_current_charge);
        txt_material_fee = (CustomEditText) dialogShowTripDetail.findViewById(R.id.edt_material_fee);
        txt_misc_fee = (CustomEditText) dialogShowTripDetail.findViewById(R.id.edt_misc_fee);
        txt_discount_driver = (CustomEditText) dialogShowTripDetail.findViewById(R.id.edt_discount_driver);
        txt_final_total = dialogShowTripDetail.findViewById(R.id.txt_final_total);
        txt_your_final_total = dialogShowTripDetail.findViewById(R.id.txt_your_final_total);
        edt_comment_pastjob_detail = (CustomEditText) dialogShowTripDetail.findViewById(R.id.edt_comment_pastjob_detail);

        btn_rating_payment = (CustomButton) dialogShowTripDetail.findViewById(R.id.btn_rating_payment);

        cv_rating_lyt = (CardView) dialogShowTripDetail.findViewById(R.id.cv_rating_lyt);

        ll_current_charge_lyt = (LinearLayout) dialogShowTripDetail.findViewById(R.id.ll_current_charge_lyt);
        ll_material_fee_lyt = (LinearLayout) dialogShowTripDetail.findViewById(R.id.ll_material_fee_lyt);
        ll_misc_fee_lyt = (LinearLayout) dialogShowTripDetail.findViewById(R.id.ll_misc_fee_lyt);
        ll_discount_lyt = (LinearLayout) dialogShowTripDetail.findViewById(R.id.ll_discount_lyt);
        ll_final_total_lyt = (LinearLayout) dialogShowTripDetail.findViewById(R.id.ll_final_total_lyt);

        ratingBar_pastjob_detail = (SimpleRatingBar) dialogShowTripDetail.findViewById(R.id.ratingBar_pastjob_detail);
        ratingBar_Driver_pastjob_detail = (SimpleRatingBar) dialogShowTripDetail.findViewById(R.id.ratingBar);
        ratingBar_Trip_pastjob_detail = (SimpleRatingBar) dialogShowTripDetail.findViewById(R.id.ratingBar_trip);

        txtAgentCharge = dialogShowTripDetail.findViewById(R.id.txt_agent_charge);
        txtAgentFeeLabel = dialogShowTripDetail.findViewById(R.id.txt_agent_fee_label);
        txtDayFeeLabel = dialogShowTripDetail.findViewById(R.id.txt_day_fee_label);
        txtDayCharge = dialogShowTripDetail.findViewById(R.id.txt_day_charge);

        txtNoofHrsTripSummary = dialogShowTripDetail.findViewById(R.id.txt_no_of_hrs_trip_summary);
        txtPricePerHrsLabelTripSummary = dialogShowTripDetail.findViewById(R.id.txt_price_per_hrs_trip_summary_label);
        txtPricePerHrsTripSummary = dialogShowTripDetail.findViewById(R.id.txt_price_per_hrs_trip_summary);
        txtVatAmountTripSummary = dialogShowTripDetail.findViewById(R.id.txt_vat_amount_trip_summary);
        trExtendNoofHrsTripSummary = dialogShowTripDetail.findViewById(R.id.tr_extra_no_of_hrs_trip_summary);
        txtExtendNoofHrsTripSummary = dialogShowTripDetail.findViewById(R.id.txt_extra_no_of_hrs_trip_summary);

        progressBarView = dialogShowTripDetail.findViewById(R.id.progressBar);
    }

    private void OnClick() {

        rvPastJobs.addOnItemTouchListener(
                new RecyclerTouchListener(requireActivity(), rvPastJobs,
                        new RecyclerTouchListener.OnTouchActionListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(View view, int position) {
                                Log.i("rvSubCategory", "onClick: rvSubCategory-> ");
                                if (position != -1) {
                                    PastJobsData listModel = getve.get(position);
                                    if (listModel.getServiceStatus() == 6) {
                                        Toast.makeText(requireActivity(), getResources().getString(R.string.please_wait_customer_accept_your_completion),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (listModel.getServiceStatus() == 8) {
                                        Toast.makeText(requireActivity(), getResources().getString(R.string.upcoming_job_cancelled),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (listModel.getServiceStatus() == 9) {
                                        Toast.makeText(requireActivity(), getResources().getString(R.string.upcoming_job_cancelled_driver),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (listModel.getServiceStatus() == 14) {
                                        Toast.makeText(requireActivity(), getResources().getString(R.string.upcoming_scheduled_cancel_user),
                                                Toast.LENGTH_SHORT).show();
                                    } else if (listModel.getServiceStatus() == 15) {
                                        Toast.makeText(requireActivity(), getResources().getString(R.string.upcoming_scheduled_cancel_driver),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    // Show Past Trip summary was hidden (as per discussion)
                                    /*else {
                                        *//*Toast.makeText(requireActivity(), getResources().getString(R.string.upcoming_job_not_complete),
                                                Toast.LENGTH_SHORT).show();*//*

                                        showDialogTripDetails(listModel);
                                    }*/
                                }
                            }

                            @Override
                            public void onRightSwipe(View view, int position) {
                            }

                            @Override
                            public void onLeftSwipe(View view, int position) {
                            }
                        }));

    }

    private void getPastJobsCall() {

        progressBar.setVisibility(View.VISIBLE);
        userAccessToken = SP_Prefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", driverId);
        map.put("mode", "driver");
        map.put("language", strLanguageShortName);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PastJobsModel> call2 = apiInterface.doCustomerPastJobs("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        // showProgressDialog(getActivity());
        call2.enqueue(new Callback<PastJobsModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<PastJobsModel> call, @NonNull retrofit2.Response<PastJobsModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Fetching past jobs successful.")) {
                        // List<OnGoingJobsData> getve = response.body().getServiceRequest();
                        getve = response.body().getServiceRequest();
                        Log.i(TAG, "onResponse: getPastJobsCall-->" + getve);
                        if (getve.size() > 0) {
                            rlEmptyview.setVisibility(View.GONE);
                            rvPastJobs.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            adapter = new PastJobsAdapter(getActivity(), getve);
                            LinearLayoutManager horizontalLayoutManager
                                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            rvPastJobs.setLayoutManager(horizontalLayoutManager);
                            rvPastJobs.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            rlEmptyview.setVisibility(View.VISIBLE);
                            rvPastJobs.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        rlEmptyview.setVisibility(View.VISIBLE);
                        rvPastJobs.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("Category");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PastJobsModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void onBackButton(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    backButtonFunction();
                }
                return true;
            }
        });
    }

    public void backButtonFunction() {
        // MainActivity.frameClickable(false);
        removeFragment(Utils.FRAGMENT_YOURJOBS);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
    }

    @SuppressLint("SetTextI18n")
    private void showDialogTripDetails(PastJobsData listModel) {

        try {

            String bookingId = String.valueOf(listModel.getBookingId());
            Integer serviceStatus = listModel.getServiceStatus();
            String customerName = listModel.getCustomerName();
            String customer_avatar = listModel.getCustomerAvatar();
            String jobDate = listModel.getJobDate();
            String getUserid = String.valueOf(listModel.getCustomerId());
            String paymentType = String.valueOf(listModel.getPaymentType());
            String totalAmount = String.valueOf(listModel.getTotalAmount());
            String rating_status = String.valueOf(listModel.getRatingStatus());
            float rating = listModel.getRating();

            String strVatAmountsFB = String.valueOf(listModel.getVatAmount());
            String strTotalHrsFB = String.valueOf(listModel.getTotalHrs());
            String stPricePerHrsFB = String.valueOf(listModel.getPricePerHr());
            String strExtendedStatusFB = String.valueOf(listModel.getExtendedStatus());
            String strExtendedHrsFB = String.valueOf(listModel.getExtendedHrs());
            String strExtendedVatFB = String.valueOf(listModel.getExtendedVat());
            String strExtendedAmountFB = String.valueOf(listModel.getExtendedAmount());
            String strSubLevelAmountFB = String.valueOf(listModel.getSubLevelAmount());
            List<GroupDriverDatum> groupDriverData = listModel.getGroupDriverData();

            ivprofpicPastjobdetail.setImageURI(Utils.BASE_URL + customer_avatar);
            int color = getResources().getColor(R.color.colorPrimary);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setBorder(color, 1.0f);
            roundingParams.setRoundAsCircle(true);
            ivprofpicPastjobdetail.getHierarchy().setRoundingParams(roundingParams);

            txt_drivername_pastjob_detail.setText(customerName);

            ratingBar_Trip_pastjob_detail.setRating(rating);

            txt_booking_pastjob_detail.setText(bookingId);

            final String dateList;
            String dateListEs = null;
            dateList = jobDate; //"Thu,Jan 30,2020 05:12 AM",
            try {

                String[] separated = dateList.split(",");
                String day = separated[0]; //Thu
                String monthDate = separated[1]; // Jan 30
                String yearTime = separated[2]; // 2020 05:12 AM"

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat date12Format = new SimpleDateFormat("yyyy hh:mm a");

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat date24Format = new SimpleDateFormat("yyyy HH:mm");

                String yearTime1 = date24Format.format(Objects.requireNonNull(date12Format.parse(yearTime)));

                String[] separate2 = monthDate.split(" ");
                String month = separate2[0]; // Jan
                String date = separate2[1]; // 30

                // Log.e("onBindViewHolder: ~~~~~~~~1~~~~~~~", day + "~~" + monthDate + "~~~~" + yearTime + "~~~~");
                // Log.e("onBindViewHolder: ~~~~~~~~2~~~~~~~", "~~" + month + "~~~~" + date);

                //ronBindViewHolder: ~~~~~~~~1~~~~~~~: Thu ~~ Jan 30~~~~2020 05:12 AM~~~~
                //onBindViewHolder: ~~~~~~~~2~~~~~~~: ~~Jan~~~~30

                String dayLabel = "-";
                switch (day) {
                    case "Sun":
                    case "sun":
                        dayLabel = getResources().getString(R.string.Sunday_3);
                        break;
                    case "Mon":
                    case "mon":
                        dayLabel = getResources().getString(R.string.Monday_3);
                        break;
                    case "Tue":
                    case "tue":
                        dayLabel = getResources().getString(R.string.Tuesday_3);
                        break;
                    case "Wed":
                    case "wed":
                        dayLabel = getResources().getString(R.string.Wednesday_3);
                        break;
                    case "Thu":
                    case "thu":
                        dayLabel = getResources().getString(R.string.Thursday_3);
                        break;
                    case "Fri":
                    case "fri":
                        dayLabel = getResources().getString(R.string.Friday_3);
                        break;
                    case "Sat":
                    case "sat":
                        dayLabel = getResources().getString(R.string.Saterday_3);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + day);
                }

                String monthLabel = "-";
                switch (month) {
                    case "Jan":
                    case "jan":
                        monthLabel = getResources().getString(R.string.January_3);
                        break;
                    case "Feb":
                    case "feb":
                        monthLabel = getResources().getString(R.string.February_3);
                        break;
                    case "Mar":
                    case "mar":
                        monthLabel = getResources().getString(R.string.March_3);
                        break;
                    case "Apr":
                    case "apr":
                        monthLabel = getResources().getString(R.string.April_3);
                        break;
                    case "May":
                    case "may":
                        monthLabel = getResources().getString(R.string.May_3);
                        break;
                    case "Jun":
                    case "jun":
                        monthLabel = getResources().getString(R.string.June_3);
                        break;
                    case "Jul":
                    case "jul":
                        monthLabel = getResources().getString(R.string.July_3);
                        break;
                    case "Aug":
                    case "aug":
                        monthLabel = getResources().getString(R.string.August_3);
                        break;
                    case "Sep":
                    case "sep":
                        monthLabel = getResources().getString(R.string.September_3);
                        break;
                    case "Oct":
                    case "oct":
                        monthLabel = getResources().getString(R.string.October_3);
                        break;
                    case "Nov":
                    case "nov":
                        monthLabel = getResources().getString(R.string.November_3);
                        break;
                    case "Dec":
                    case "dec":
                        monthLabel = getResources().getString(R.string.December_3);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + month);
                }

                dateListEs = dayLabel + "," + monthLabel + " " + date + "," + yearTime1;

            } catch (Exception e) {
                e.printStackTrace();
                dateListEs = null;
            }

            if (dateListEs != null) {
                txt_job_request_date_time.setText(dateListEs);
            } else {
                txt_job_request_date_time.setText(jobDate);

            }

            txt_category_past_job_detail.setText(listModel.getCategoryName() + "-" + listModel.getServiceName());

            txt_pickup_pastjob_detail.setText(String.valueOf(listModel.getCustomerLocation()));


            if (serviceStatus == 7) {
                txt_trip_status.setText(getString(R.string.your_job_was_successfully_completed));
            } else if (serviceStatus == 6) {
                txt_trip_status.setText(getString(R.string.please_wait_customer_accept_your_completion));
            } else if (serviceStatus == 8) {
                txt_trip_status.setText(getString(R.string.upcoming_job_cancelled));
            } else if (serviceStatus == 9) {
                txt_trip_status.setText(getString(R.string.upcoming_job_cancelled_driver));
            } else if (serviceStatus == 14) {
                txt_trip_status.setText(getString(R.string.upcoming_scheduled_cancel_user));
            } else if (serviceStatus == 15) {
                txt_trip_status.setText(getString(R.string.upcoming_scheduled_cancel_driver));
            } else {
                txt_trip_status.setText(getString(R.string.dialog_pastjob_not_finish_trip));
            }

            if (!strSubLevelAmountFB.equals("0")) {
                txt_base_amount_trip_summary.setText(strCurrencySymbol + " " + convertCurrencyPrice(requireActivity(), strSubLevelAmountFB));
            } else {
                txt_base_amount_trip_summary.setText(strCurrencySymbol + " ");
            }

            if (totalAmount.equalsIgnoreCase("0")) {
                ll_final_total_lyt.setVisibility(View.GONE);
            } else {

                ll_final_total_lyt.setVisibility(View.VISIBLE);

                double faret = Double.parseDouble(totalAmount);
                if (faret <= 0.0) {
                    faret = 0.0;
                }

                if (!strVatAmountsFB.equals("0")) {
                    if (strExtendedStatusFB.equals("1")) {
                        double fare = Double.parseDouble(strVatAmountsFB) + Double.parseDouble(strExtendedVatFB);
                        if (fare <= 0.0) {
                            fare = 0.0;
                        }
                        txt_final_total.setText(strCurrencySymbol + " " + convertCurrencyPrice(requireActivity(), String.valueOf(faret + fare)));
                    } else {
                        double fared = Double.parseDouble(strVatAmountsFB) + faret;
                        if (fared <= 0.0) {
                            fared = 0.0;
                        }
                        txt_final_total.setText(strCurrencySymbol + " " + convertCurrencyPrice(requireActivity(), String.valueOf(faret + fared)));
                    }
                } else {
                    if (strExtendedStatusFB.equals("1")) {
                        txt_final_total.setText(strCurrencySymbol + " " + convertCurrencyPrice(requireActivity(), String.valueOf(faret)));
                    } else {
                        txt_final_total.setText(strCurrencySymbol + " " + convertCurrencyPrice(requireActivity(), totalAmount));
                    }
                }

                if (groupDriverData != null && groupDriverData.size() > 0) {
                    double farediv = Double.parseDouble(totalAmount) / groupDriverData.size();
                    if (farediv <= 0.0) {
                        farediv = 0.0;
                    }
                    txt_your_final_total.setText(strCurrencySymbol + "" + convertCurrencyPrice(requireActivity(), String.valueOf(farediv)));
                } else {
                    txt_your_final_total.setText("-");
                }

            }

            if (rating_status.equalsIgnoreCase("Yes")) {
                cv_rating_lyt.setVisibility(View.GONE);
            } else {
                edt_comment_pastjob_detail.setHint(getString(R.string.dialog_enter_your_reason_hint));
                ratingBar_pastjob_detail.setRating(0);
                cv_rating_lyt.setVisibility(View.VISIBLE);
            }

            if (!strVatAmountsFB.equals("0")) {
                if (strExtendedStatusFB.equals("1")) {
                    double fare = Double.parseDouble(strVatAmountsFB) + Double.parseDouble(strExtendedVatFB);
                    if (fare <= 0.0) {
                        fare = 0.0;
                    }
                    txtVatAmountTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(requireActivity(), String.valueOf(fare)));
                } else {
                    txtVatAmountTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(requireActivity(), strVatAmountsFB));
                }
            } else {
                txtVatAmountTripSummary.setText("");
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

                String label = "( " + getResources().getString(R.string.price_per_hrs) + " = " + strCurrencySymbol + "" + convertCurrencyPrice(requireActivity(), stPricePerHrsFB) + " * " + hrs_ + " " + getResources().getString(R.string.hrs) + " )";
                txtPricePerHrsLabelTripSummary.setText(label);

                double fare = Double.parseDouble(stPricePerHrsFB) * Double.parseDouble(hrs_);
                if (fare <= 0.0) {
                    fare = 0.0;
                }
                txtPricePerHrsTripSummary.setText(strCurrencySymbol + "" + convertCurrencyPrice(requireActivity(), String.valueOf(fare)));
            } else {
                txtPricePerHrsLabelTripSummary.setText(getResources().getString(R.string.price_per_hrs));
                txtPricePerHrsTripSummary.setText("");
            }

            if (!paymentType.equals("null") && !paymentType.isEmpty() && !paymentType.equals(" "))
                switch (paymentType) {
                    case "card":
                    case "stripe":
                        txt_paymentmode.setText(getResources().getString(R.string.paid_with) + " " + getResources().getString(R.string.select_payment_pay_online));
                        break;
                    case "wallet":
                        txt_paymentmode.setText(getResources().getString(R.string.paid_with_wallet));
                        break;
                    case "stripe_gpay":
                        txt_paymentmode.setText(getResources().getString(R.string.paid_with_google_pay));
                        break;
                    case "paypal":
                        txt_paymentmode.setText(getResources().getString(R.string.paid_with_paypal));
                        break;
                    default:
                        txt_paymentmode.setText(paymentType);
                        break;
                }
            else {
                txt_paymentmode.setText("");
            }


            btn_rating_payment.setOnClickListener(v -> {

                if (edt_comment_pastjob_detail.getText().toString().trim().length() == 0) {
                    edt_comment_pastjob_detail.setError(getString(R.string.error_required));
                } else {
                    callUpdateRating(edt_comment_pastjob_detail.getText().toString().trim(),
                            ratingBar_pastjob_detail.getRating(),
                            bookingId, driverId, getUserid);
                }
            });

            imgbtn_back.setOnClickListener(v -> dialogShowTripDetail.dismiss());

            if (dialogShowTripDetail != null && !dialogShowTripDetail.isShowing()) {
                dialogShowTripDetail.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("LongLogTag")
    private void callUpdateRating(String trim, float rating, String strBookingId, String driverid, String getUserid) {

        String ratings = String.valueOf(rating);

        HashMap<String, Object> map = new HashMap<>();
        map.put("from_id", driverid);   //driverid
        map.put("job_id", strBookingId);
        map.put("to_id", getUserid);  //Userid
        map.put("rating", ratings);
        map.put("status", "0");
        map.put("feedback", trim);
        Log.e("callUpdateRating adapter", "~~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RatingUserModel> call2 = apiInterface.doUpdateRatingUser("Bearer " + userAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        setVisibility(progressBarView, true);
        call2.enqueue(new Callback<RatingUserModel>() {
            @Override
            public void onResponse(@NonNull Call<RatingUserModel> call, @NonNull Response<RatingUserModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    setVisibility(progressBarView, false);
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Feedback received")) {
                        Toast.makeText(requireActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                        setVisibility(cv_rating_lyt, false);
                        if (dialogShowTripDetail != null && dialogShowTripDetail.isShowing()) {
                            dialogShowTripDetail.show();
                        }
                        getPastJobsCall();
                    } else {
                        setVisibility(progressBarView, false);
                        Toast.makeText(requireActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setVisibility(progressBarView, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RatingUserModel> call, @NonNull Throwable t) {
                call.cancel();
                setVisibility(progressBarView, false);
            }
        });
    }


}

