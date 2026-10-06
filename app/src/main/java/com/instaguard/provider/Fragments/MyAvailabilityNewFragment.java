package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Activities.MainActivity.txtUpdateAvailabilitySetupOnline;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.instaguard.provider.Adapter.MyAvailabilityNewAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.Availability.NewDateAvailListModel;
import com.instaguard.provider.Models.Availability.UpdateAvailListModel;
import com.instaguard.provider.Models.Availability.ViewAvailListModel;
import com.instaguard.provider.Models.AvailabilityDataTimeExtra;
import com.instaguard.provider.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyAvailabilityNewFragment extends Fragment implements MyAvailabilityNewAdapter.SuccessResponse {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;

    ImageButton imgbtnBack;

    CustomTextView txtMyavailabilitySub;
    CustomTextView txtHeaderDate;
    CustomButton btnUpdateAvailable;
    RecyclerView rvMyAvailablity;
    CheckBox ivCheck;
//    HorizontalCalendarView horzCalendarView;
    CustomButton btnSelectDate;
//    HorizontalCalendar horizontalCalendar;

    private List<String> timeArray;
    int[] arraySunday;

    int numberOfColumns = 4;

    SharedPreferences.Editor getState;
    SharedPreferences spPrefs;

    String userId, userAccessToken;
    public String TAG = "MyAvailabilityNewFragment";
    String deviceId;
    String strTimeArrayList;
    MyAvailabilityNewAdapter adapter;
    public String availableSlots;

    public MyAvailabilityNewFragment() {
        // Required empty public constructor
    }

    public List<AvailabilityDataTimeExtra> availabilityDataTimeExtra;
    String strSundaySelectedIds;

    // TODO: Rename and change types and number of parameters
    public static MyAvailabilityNewFragment newInstance(String param1, String param2) {
        MyAvailabilityNewFragment fragment = new MyAvailabilityNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_myavailability_new, container, false);

        getState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        SharedPreferences pref = requireActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = pref.getString(Utils.SP_DEVICE_ID, null);
        MyAvailabilityNewAdapter.successResponse = this;

        spPrefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        if (getArguments() != null) {
            strTimeArrayList = getArguments().getString(Utils.B_TIME_LIST);
            //strDayArrayList = getArguments().getString(Utils.B_DAY_LIST);
            Log.i(TAG, "strArrayList inMyAvailabilitySubFragment--> " + strTimeArrayList + "~~~" + "strDayArrayList");
        }

        initViews(view);

       /* try {

            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            SimpleDateFormat sdf_ = callCurrentDate_init();

            Calendar startDate = Calendar.getInstance();
            //startDate.add(Calendar.MONTH, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.MONTH, 1);

            // Add events to specific dates
            Calendar eventDate = Calendar.getInstance();
            eventDate.add(Calendar.DAY_OF_MONTH, 2); // for example, add an event to the date 2 days from now
            //HorizontalCalendarEvent event = new HorizontalCalendarEvent("Event 1", eventDate);
            //horizontalCalendar.addEvent(event);

           *//*CalenderEvent calenderEvent = findViewById(R.id.calender_event);
            Event event = new Event(eventDate.getTimeInMillis(), "Test");
            // to set desire day time milli second in first parameter
            //or you set color for each event
            Event event = new Event(eventDate.getTimeInMillis(), "Test",Color.RED);
            calenderEvent.addEvent(event);*//*

            CalendarEventsPredicate eventsPredicate = new CalendarEventsPredicate() {
                final Random rnd = new Random();

                @Override
                public List<CalendarEvent> events(Calendar date) {
                    *//*List<CalendarEvent> events = new ArrayList<>();
                    int count = rnd.nextInt(1);
                    for (int i = 0; i <= count; i++) {
                        events.add(new CalendarEvent(
                                Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)),
                                "event"));
                    }
                    return events;*//*
                    return null;
                }
            };
            // Default Date set to Today.
            final Calendar defaultSelectedDate = Calendar.getInstance();

            horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(7)
                    .addEvents(eventsPredicate)
                    *//*.defaultSelectedDate(defaultSelectedDate)*//*
                    .build();
            horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                @Override
                public void onDateSelected(Calendar date, int position) {

                    System.out.println("~~~~~position~~~~~"+position);

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy , EEE", Locale.getDefault());
                    // firebaseretrievedata(sdf.format(date.getTime()));
                    System.out.println("~~~~onDateSelected~~~1~~~" + sdf.format(date.getTime()));

                    selectedDate = sdf.format(date.getTime());
                    selectedDateApi = sdf_.format(date.getTime());
                    selectedDateApiCurrent = sdf_.format(date.getTime());

                    btnSelectDate.setText(selectedDate);
                    txtHeaderDate.setText(selectedDateApi);

                    CallViewAvailableDetails("1", selectedDateApi);

                    // Handle date selection
                    //List<HorizontalCalendarEvent> events = horizontalCalendar.getEvents(date);
                    // Handle events for the selected date
                }
            });

            horizontalCalendar.goToday(true);

            view.findViewById(R.id.txt_today).setOnClickListener(view -> {
                horizontalCalendar.goToday(true);
                System.out.println("~~~txt_today~~~");
                callCurrentDate_init();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        onClickViews();

        onBackButton(view);

        return view;

    }

    private SimpleDateFormat callCurrentDate_init() {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedDate = sdf.format(new Date());
        selectedDateApi = sdf_.format(new Date());
        selectedDateApiCurrent = sdf_.format(new Date());

        System.out.println("~~currentDateandTime~~~~>>>" + selectedDate);
        System.out.println("~~currentDateandTime_~~~~>>>" + selectedDateApi);

        txtHeaderDate.setText(selectedDateApi);

        SimpleDateFormat newFormat_date = new SimpleDateFormat("dd", Locale.US);
        SimpleDateFormat newFormat_month = new SimpleDateFormat("MMM", Locale.US);
        SimpleDateFormat newFormat_year = new SimpleDateFormat("yyyy", Locale.US);
        System.out.println(TAG + "dialogDatePickerLight: ~~~~~~" +
                newFormat_date.format(new Date()) + "~~~~~" +
                newFormat_month.format(new Date()) + "~~~~~" +
                newFormat_year.format(new Date()));

        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date1;
        try {
            date1 = dateformat.parse(selectedDate);
            DateFormat dayFormate = new SimpleDateFormat("EEEE", Locale.US);
            String dayFromDate = null;
            if (date1 != null) {
                dayFromDate = dayFormate.format(date1);
            }

            selectedDay = dayFromDate;

            System.out.println(TAG + "day text-->----------:: " + dayFromDate);
            System.out.println(TAG + "day text-->onDateSet: " + dayFromDate);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // btnSelectDate.setText(selectedDay + " " + selectedDate);
        CallViewAvailableDetails("1", selectedDateApi);
        return sdf_;
    }

    private void initViews(View view) {

        txtMyavailabilitySub = view.findViewById(R.id.txt_myavailabilitysub);
        txtHeaderDate = view.findViewById(R.id.txt_header_date);
        btnUpdateAvailable = view.findViewById(R.id.btn_updateavailable);
        imgbtnBack = view.findViewById(R.id.imgbtn_back);
        rvMyAvailablity = view.findViewById(R.id.rv_myavailablity);

        btnSelectDate = view.findViewById(R.id.btn_select_date);
        ivCheck = view.findViewById(R.id.iv_checkbox);
//        horzCalendarView = view.findViewById(R.id.calendarView);

        rvMyAvailablity.setLayoutManager(new GridLayoutManager(requireActivity(), numberOfColumns));

        timeArray = new ArrayList<>();
        timeArray.addAll(Arrays.asList(Utils.timeArray));
    }

    private void onClickViews() {

        imgbtnBack.setOnClickListener(v -> backButtonFunction());

        btnSelectDate.setOnClickListener(v -> dialogDatePickerLight());

        btnUpdateAvailable.setOnClickListener(v -> {

            strSundaySelectedIds = returnWithBoxes(strSundaySelectedIds);
            availableSlots = strSundaySelectedIds;
            Log.i(TAG, "onClick: btnUpdateAvailable--" + txtHeaderDate.getText().toString());
            Log.i(TAG, "onClick: btnUpdateAvailable--" + availableSlots);

            CallUpdateAvailableDetails();
        });

        ivCheck.setOnClickListener(view -> {
            if (ivCheck.isChecked()) {
                availabilityDataTimeExtra = new ArrayList<>();

                arraySunday = new int[timeArray.size()];
                for (int i = 0; i < timeArray.size(); i++) {
                    arraySunday[i] = (i + 1);
                }
                strSundaySelectedIds = Arrays.toString(arraySunday);

                for (int i = 0; i < timeArray.size(); i++) {
                    availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));
                    adapter = new MyAvailabilityNewAdapter(requireActivity(), availabilityDataTimeExtra);
                    rvMyAvailablity.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            } else {
                availabilityDataTimeExtra = new ArrayList<>();
                strSundaySelectedIds = null;
                for (int i = 0; i < timeArray.size(); i++) {
                    availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                    adapter = new MyAvailabilityNewAdapter(requireActivity(), availabilityDataTimeExtra);
                    rvMyAvailablity.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public String returnWithBoxes(String data) {
        if (data == null) {
            data = "[]";
        } else {
            data = data;
        }
        return data;
    }

    private void CallUpdateAvailableDetails() {

        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        map.put("slot", availableSlots);
        map.put("date", txtHeaderDate.getText().toString());

        Log.i(TAG, "CallUpdateAvailableDetails" + map);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UpdateAvailListModel> call2 = apiInterface.doUpdateNewAvailable(map);
        showProgressDialog(requireActivity());
        call2.enqueue(new Callback<UpdateAvailListModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<UpdateAvailListModel> call, @NonNull Response<UpdateAvailListModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Slot added successfully.")
                            || Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Slot upddated successfully.")) {

                        hideProgressDialog();
                        Toast.makeText(requireActivity(), getString(R.string.myavailablitysub_update_success), Toast.LENGTH_SHORT).show();

                        if (response.body().getResult().getDate().equals(selectedDateApiCurrent))
                            /*if (txtHeaderDate.getText().toString().equals(selectedDateApiCurrent))*/ {
                            if (response.body().getResult().getSlot() != null) {
                                if (response.body().getResult().getSlot().size() > 0) {
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
                        Toast.makeText(requireActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateAvailListModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });

    }

    private void CallViewAvailableDetails(String from, String date) {

        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        map.put("date", date);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<NewDateAvailListModel> call2 = apiInterface.doGetNewAvailable(map);

        showProgressDialog(requireActivity());

        call2.enqueue(new Callback<NewDateAvailListModel>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<NewDateAvailListModel> call, @NonNull Response<NewDateAvailListModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {

                        if (response.body().getResult().getSlot() != null) {

                            Log.e(TAG, "onResponse: ~~~~~~~~~~~~~~~~~~~~~If~~~~");

                            availabilityDataTimeExtra = new ArrayList<>();

                            if (response.body().getResult().getSlot() != null) {
                                List<String> sunday = response.body().getResult().getSlot();
                                arraySunday = new int[sunday.size()];
                                for (int i = 0; i < sunday.size(); i++) {
                                    arraySunday[i] = Integer.parseInt(sunday.get(i));
                                }
                                strSundaySelectedIds = Arrays.toString(arraySunday);
                            }


                            for (int i = 0; i < timeArray.size(); i++) {
                                if (contains(arraySunday, Integer.parseInt(Utils.timeSlotArray[i]))) {
                                    availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));
                                } else {
                                    availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                }
                                adapter = new MyAvailabilityNewAdapter(requireActivity(), availabilityDataTimeExtra);
                                rvMyAvailablity.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }

                            if (timeArray.size() == arraySunday.length) {
                                ivCheck.setChecked(true);
                            } else {
                                ivCheck.setChecked(false);
                            }

                            if (from.equals("1")) {
                                txtUpdateAvailabilitySetupOnline.setVisibility(View.GONE);
                            }

                        } else {

                            if (from.equals("1")) {
                                txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                            }

                            arraySunday = new int[0];

                            Log.e(TAG, "onResponse: ~~~~~~~~~~~~~~~~~~~~~Else~~~~");
                            availabilityDataTimeExtra = new ArrayList<>();
                            for (int i = 0; i < timeArray.size(); i++) {
                                availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                            }
                            adapter = new MyAvailabilityNewAdapter(requireActivity(), availabilityDataTimeExtra);
                            rvMyAvailablity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            ivCheck.setChecked(false);
                        }

                    } else {

                        if (from.equals("1")) {
                            txtUpdateAvailabilitySetupOnline.setVisibility(View.VISIBLE);
                        }
                        arraySunday = new int[0];

                        Log.e(TAG, "onResponse: ~~~~~~~~~~~~~~~~~~~~~Else~~~~");
                        availabilityDataTimeExtra = new ArrayList<>();
                        for (int i = 0; i < timeArray.size(); i++) {
                            availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                        }
                        adapter = new MyAvailabilityNewAdapter(requireActivity(), availabilityDataTimeExtra);
                        rvMyAvailablity.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        ivCheck.setChecked(false);
                    }

                    hideProgressDialog();

                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("view");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewDateAvailListModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: AvailabilityModel~~~~~>Sub" + t.toString());
                call.cancel();
            }
        });


    }

    private void CallViewDriverAvailableDetails() {

        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<ViewAvailListModel> call2 = apiInterface.doGetNewViewDriverAvailable(map);

        showProgressDialog(requireActivity());

        call2.enqueue(new Callback<ViewAvailListModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ViewAvailListModel> call, @NonNull Response<ViewAvailListModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver slots listed successfully.")) {


                    } else {
                        //callRefreshToken("view");
                    }
                    hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("view");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewAvailListModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: AvailabilityModel~~~~~>Sub" + t.toString());
                call.cancel();
            }
        });


    }

    /*
     * Removing element from array without using Collection class
     */
    public static int[] removeIntArr(int[] numbers, int target) {
        int count = 0;

        // loop over array to count number of target values.
        // this required to calculate length of new array
        for (int number : numbers) {
            if (number == target) {
                count++;
            }
        }

        // if original array doesn't contain number to removed
        // return same array
        if (count == 0) {
            return numbers;
        }

        int[] result = new int[numbers.length - count];
        int index = 0;
        for (int value : numbers) {
            if (value != target) {
                result[index] = value;
                index++;
            }
        }
        numbers = null; // make original array eligible for GC
        return result;
    }

    public static boolean contains(int[] arr, int item) {
        for (int n : arr) {
            if (item == n) {
                return true;
            }
        }
        return false;
    }

    static int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    public static int[] addOneIntToArray(int[] initialArray, int newValue) {

        int[] newArray = new int[initialArray.length + 1];
        for (int index = 0; index < initialArray.length; index++) {
            Log.i("hai", "addOneIntToArray: arrray" + initialArray[index]);
            newArray[index] = initialArray[index];
        }

        newArray[newArray.length - 1] = newValue;
        Log.i("jao", "addOneIntToArray: --" + Arrays.toString(newArray));
        return newArray;
    }


    /*******************************Get Firebase States********************************************/

    public void onBackButton(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_BACK) {
                backButtonFunction();
            }
            return true;
        });
    }

    public void backButtonFunction() {
        removeFragment(Utils.FRAGMENT_MYAVAILABILITY_NEW);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    //Inteface called
    public void onSuccess(int position) {
        Log.i(TAG, "onSuccess: interface called in myavailability sub fragment-->" + position);
        AddArrayList(position);
    }

    public void AddArrayList(int position) {
        //AddAndRemoveArrayPosition(arrayMonday,position+1);
        int positionInc = position + 1;
        Log.i(TAG, "onSuccess: position-->" + positionInc + "~~~");
        if (strSundaySelectedIds != null) {
            if (arraySunday != null) {
                if (contains(arraySunday, positionInc)) {
                    Log.i(TAG, "onSuccess: if");
                    // categoryModel.setIsSelected(true);
                    ivCheck.setChecked(false);
                    arraySunday = removeIntArr(arraySunday, positionInc);
                    Log.i(TAG, "onSuccess: " + Arrays.toString(arraySunday));
                } else {
                    Log.i(TAG, "onSuccess: else arrayMonday.length--" + arraySunday.length);
                    arraySunday = addOneIntToArray(arraySunday, positionInc);
                    if (timeArray.size() == arraySunday.length) {
                        ivCheck.setChecked(true);
                    }
                }
            } else {
                Log.i(TAG, "onSuccess: else else");
                arraySunday = new int[0];
                arraySunday = addElement(arraySunday, positionInc);
            }
        } else {
            Log.i(TAG, "onSuccess: else else else");
            arraySunday = new int[0];
            arraySunday = addElement(arraySunday, positionInc);
        }
        strSundaySelectedIds = Arrays.toString(arraySunday);
        Log.i(TAG, "onSuccess: in the onsuccess-->" + Arrays.toString(arraySunday));
    }


    int selectedHour;
    String selectedDay;
    String selectedDate, selectedDateApi, selectedDateApiCurrent = "yyyy-MM-dd", selectedTime;

    private void dialogDatePickerLight() {
        Calendar cur_calender = Calendar.getInstance();
        @SuppressLint("SetTextI18n") DatePickerDialog datePicker = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    long date_ship_millis = calendar.getTimeInMillis();

                    SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    SimpleDateFormat newFormat_date = new SimpleDateFormat("dd", Locale.US);
                    SimpleDateFormat newFormat_month = new SimpleDateFormat("MMM", Locale.US);
                    SimpleDateFormat newFormat_year = new SimpleDateFormat("yyyy", Locale.US);
                    selectedDate = newFormat.format(new Date(date_ship_millis));


                    SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    selectedDateApi = sdf_.format(new Date(date_ship_millis));
                    //selectedDateApiCurrent = sdf_.format(new Date(date_ship_millis));

                    System.out.println(TAG + "dialogDatePickerLight: ~~~~~~" +
                            newFormat_date.format(new Date(date_ship_millis)) + "~~~~~" +
                            newFormat_month.format(new Date(date_ship_millis)) + "~~~~~" +
                            newFormat_year.format(new Date(date_ship_millis)));

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1;
                    try {
                        date1 = dateformat.parse(selectedDate);
                        DateFormat dayFormate = new SimpleDateFormat("EEEE", Locale.US);
                        String dayFromDate = dayFormate.format(date1);

                        selectedDay = dayFromDate;

                        System.out.println("day text-->----------:: " + dayFromDate);
                        System.out.println("day text-->onDateSet: " + dayFromDate);

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    System.out.println("~~currentDateandTime~~~~>>>" + selectedDate);
                    System.out.println("~~currentDateandTime_~~~~>>>" + selectedDateApi);
                    System.out.println("~~currentDateandTime_~~c~~>>>" + selectedDateApiCurrent);

                    txtHeaderDate.setText(selectedDateApi);

                    btnSelectDate.setText(selectedDay + " " + selectedDate);

                    CallViewAvailableDetails("2", selectedDateApi);


                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setLocale(Locale.US);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(requireActivity().getSupportFragmentManager(), "Datepickerdialog");
    }

}
