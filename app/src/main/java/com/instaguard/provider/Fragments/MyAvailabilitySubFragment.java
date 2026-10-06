package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidadvance.topsnackbar.TSnackbar;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.MyAvailabilitySubAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AvailabilityDataTimeExtra;
import com.instaguard.provider.Models.AvailabilityModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;


public class MyAvailabilitySubFragment extends Fragment implements MyAvailabilitySubAdapter.SuccessResponse {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;

    ImageButton imgbtnBack;

    CustomTextView txtMyavailabilitySub;
    CustomButton btnUpdateAvailable;
    RecyclerView rvMyAvailablity;
    private List<String> timeArray;

    private List<Integer> Monday;
    private List<Integer> Tuesday;
    private List<Integer> Wednesday;
    private List<Integer> Thursday;
    private List<Integer> Friday;
    private List<Integer> Saterday;
    private List<Integer> Sunday;

    int[] arrayMonday;
    int[] arrayTuesday;
    int[] arrayWednesday;
    int[] arrayThursday;
    int[] arrayFriday;
    int[] arraySaterday;
    int[] arraySunday;

    int numberOfColumns = 3;

    SharedPreferences.Editor getState;
    SharedPreferences spPrefs;

    String userId,userAccessToken,userProfPic;
    public String TAG = "MyAvailabilitySubFragment";
    String deviceId;
    String strTimeArrayList,strDayArrayList;
    MyAvailabilitySubAdapter adapter;
    public String availableSlots;

    public MyAvailabilitySubFragment() {
        // Required empty public constructor
    }

    public List<AvailabilityDataTimeExtra> availabilityDataTimeExtra;
    String strMondaySelectedIds,strTuesdaySelectedIds,strWednesdaySelectedIds,strThursdaySelectedIds,strFridaySelectedIds,strSaterdaySelectedIds,strSundaySelectedIds;
    // TODO: Rename and change types and number of parameters
    public static MyAvailabilitySubFragment newInstance(String param1, String param2) {
        MyAvailabilitySubFragment fragment = new MyAvailabilitySubFragment();
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

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_myavailabilitysub, container, false);

        getState = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        SharedPreferences pref = getActivity().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE);
        deviceId = pref.getString(Utils.SP_DEVICE_ID, null);
        MyAvailabilitySubAdapter.successResponse = this;

        spPrefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        if(getArguments()!=null) {
            strTimeArrayList = getArguments().getString(Utils.B_TIME_LIST);
            strDayArrayList = getArguments().getString(Utils.B_DAY_LIST);
            Log.i(TAG, "strArrayList inMyAvailabilitySubFragment--> "+strTimeArrayList+"~~~"+strDayArrayList);

        }
        Log.i(TAG, "token inMyAvailabilitySubFragment--> "+deviceId);

        txtMyavailabilitySub = (CustomTextView) view.findViewById(R.id.txt_myavailabilitysub);
        btnUpdateAvailable = (CustomButton) view.findViewById(R.id.btn_updateavailable);
        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        rvMyAvailablity = (RecyclerView) view.findViewById(R.id.rv_myavailablity);


        /*for (int i = 0; i < Utils.timeArray.length; i++){
            timeArray.add(Utils.timeArray[i]);
        }*/

//        rvMyAvailablity.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvMyAvailablity.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
       // rvMyAvailablity.setAdapter(new MyAvailabilitySubAdapter(getActivity(), timeArray));

        timeArray = new ArrayList<>();
        Monday = new ArrayList<>();
        Tuesday = new ArrayList<>();
        Wednesday = new ArrayList<>();
        Thursday = new ArrayList<>();
        Friday = new ArrayList<>();
        Saterday = new ArrayList<>();
        Sunday = new ArrayList<>();

        timeArray.clear();
        timeArray.addAll(Arrays.asList(Utils.timeArray));


        CallViewAvailableDetails();


        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                backButtonFunction();
            }
        });
        btnUpdateAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strMondaySelectedIds = returnWithBoxes (strMondaySelectedIds);
                strTuesdaySelectedIds = returnWithBoxes (strTuesdaySelectedIds);
                strWednesdaySelectedIds = returnWithBoxes (strWednesdaySelectedIds);
                strThursdaySelectedIds = returnWithBoxes (strThursdaySelectedIds);
                strFridaySelectedIds = returnWithBoxes (strFridaySelectedIds);
                strSaterdaySelectedIds = returnWithBoxes (strSaterdaySelectedIds);
                strSundaySelectedIds = returnWithBoxes (strSundaySelectedIds);
                availableSlots = "{"+"\"Monday\""+":"+ strMondaySelectedIds +","+"\"Tuesday\""+":"+ strTuesdaySelectedIds+","+"\"Wednesday\""+":"+ strWednesdaySelectedIds+","+"\"Thursday\""+":"
                        + strThursdaySelectedIds+","+"\"Friday\""+":"+ strFridaySelectedIds+","+"\"Saturday\""+":"+ strSaterdaySelectedIds+","+"\"Sunday\""+":"+ strSundaySelectedIds+"}";
                Log.i(TAG, "onClick: btnUpdateAvailable--"+availableSlots);

                CallUpdateAvailableDetails();
            }
        });



        onBackButton(view);
        return view;

    }
    public String returnWithBoxes(String data){
        if(data == null){
            data = "[]";
        }else{
            data = data;
        }
        return data;
    }
    private void CallUpdateAvailableDetails() {

        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
       // map.put("availble_slots", "{\"Monday\":[1,2,6],\"Tuesday\":[3,7],\"Wednesday\":[3,7],\"Thursday\":[3,7],\"Friday\":[3,7],\"Saterday\":[3,7],\"Sunday\":[3,7]}");
        map.put("availble_slots", availableSlots);
        map.put("token",userAccessToken );
        Log.i(TAG, "CallViewAvailableDetails" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AvailabilityModel> call2 = apiInterface.doUpdateAvailable(map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<AvailabilityModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AvailabilityModel> call, @NonNull Response<AvailabilityModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver Services updated successfully.")) {
                      if(response.body().getAvailabilityData().getAvailbleSlots()!=null){
                         Toast.makeText(getActivity(), getString(R.string.myavailablitysub_update_success), Toast.LENGTH_SHORT).show();
                          String json = String.valueOf(response.body().getAvailabilityData().getAvailbleSlots());
                          backButtonFunction();
                          try {
                              JSONObject obj = new JSONObject(json);
                              Log.d("My App", obj.toString());
                              Log.d("phonetype value ", obj.getString("Monday"));
                              for(int i=0;i<obj.length();i++){

                              }


                          } catch (Throwable tx) {
                              Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                          }
                      }else{
                          Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                      }

                    }else {
                        callRefreshToken("update");
                    }
                    hideProgressDialog();
                }else if (response.code() == RESPONSECODE_401){
                    callRefreshToken("update");
                }
            }
            @Override
            public void onFailure(@NonNull Call<AvailabilityModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                call.cancel();
            }
        });


    }

    private void CallViewAvailableDetails() {


        userAccessToken = spPrefs.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("token",userAccessToken );
        Log.i(TAG, "CallViewAvailableDetails" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AvailabilityModel> call2 = apiInterface.doGetAvailable(map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<AvailabilityModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AvailabilityModel> call, @NonNull Response<AvailabilityModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver Services listed successfully.")) {
                        if(response.body().getAvailabilityData().getAvailbleSlots()!=null){

                            availabilityDataTimeExtra = new ArrayList<>();

                            // Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                            //List<Integer> monday =  response.body().getAvailabilityData().getAvailbleSlots().getMonday();
                            //Log.i(TAG, "onResponse: in the list-->"+dayList.get(0).toString());

                            if(response.body().getAvailabilityData().getAvailbleSlots().getMonday()!= null){
                                List<Integer> monday = response.body().getAvailabilityData().getAvailbleSlots().getMonday();
                                arrayMonday = new int[monday.size()];
                                for(int i = 0; i < monday.size(); i++){
                                    arrayMonday[i] = monday.get(i);
                                }
                                strMondaySelectedIds = Arrays.toString(arrayMonday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getTuesday()!= null){
                                List<Integer> tuesday = response.body().getAvailabilityData().getAvailbleSlots().getTuesday();
                                arrayTuesday = new int[tuesday.size()];
                                for(int i = 0; i < tuesday.size(); i++){
                                    arrayTuesday[i] = tuesday.get(i);}
                                strTuesdaySelectedIds = Arrays.toString(arrayTuesday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getWednesday()!= null){
                                Log.i(TAG, "onResponse: getWednesday-->");
                                List<Integer> wednesday = response.body().getAvailabilityData().getAvailbleSlots().getWednesday();
                                arrayWednesday = new int[wednesday.size()];
                                for(int i = 0; i < wednesday.size(); i++){
                                    arrayWednesday[i] = wednesday.get(i);}
                                strWednesdaySelectedIds = Arrays.toString(arrayWednesday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getThursday()!= null){
                                List<Integer> thursday = response.body().getAvailabilityData().getAvailbleSlots().getThursday();
                                arrayThursday = new int[thursday.size()];
                                for(int i = 0; i < thursday.size(); i++){
                                    arrayThursday[i] = thursday.get(i);}
                                strThursdaySelectedIds = Arrays.toString(arrayThursday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getFriday()!= null){
                                List<Integer> friday = response.body().getAvailabilityData().getAvailbleSlots().getFriday();
                                arrayFriday = new int[friday.size()];
                                for(int i = 0; i < friday.size(); i++){
                                    arrayFriday[i] = friday.get(i);}
                                strFridaySelectedIds = Arrays.toString(arrayFriday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getSaterday()!= null){
                                List<Integer> saterday = response.body().getAvailabilityData().getAvailbleSlots().getSaterday();
                                arraySaterday = new int[saterday.size()];
                                for(int i = 0; i < saterday.size(); i++){
                                    arraySaterday[i] = saterday.get(i);}
                                strSaterdaySelectedIds = Arrays.toString(arraySaterday);
                            }
                            if(response.body().getAvailabilityData().getAvailbleSlots().getSunday()!= null){
                                List<Integer> sunday = response.body().getAvailabilityData().getAvailbleSlots().getSunday();
                                arraySunday = new int[sunday.size()];
                                for(int i = 0; i < sunday.size(); i++){
                                    arraySunday[i] = sunday.get(i);}
                                strSundaySelectedIds = Arrays.toString(arraySunday);
                            }

                            if(strDayArrayList.equals("1")){
                                for(int i = 0; i < timeArray.size(); i++){
                                   if(contains(arrayMonday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                    availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                       availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                   }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            if(strDayArrayList.equals("2")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arrayTuesday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if(strDayArrayList.equals("3")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arrayWednesday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if(strDayArrayList.equals("4")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arrayThursday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if(strDayArrayList.equals("5")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arrayFriday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            if(strDayArrayList.equals("6")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arraySaterday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if(strDayArrayList.equals("0")){
                                for(int i = 0; i < timeArray.size(); i++){
                                    if(contains(arraySunday,Integer.parseInt(Utils.timeSlotArray[i]))){
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], true));}
                                    else{
                                        availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                                    }
                                    adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                                    rvMyAvailablity.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } else{
                            Log.e(TAG, "onResponse: ~~~~~~~~~~~~~~~~~~~~~Else~~~~");
                            availabilityDataTimeExtra = new ArrayList<>();
                            for(int i = 0; i < timeArray.size(); i++) {
                                availabilityDataTimeExtra.add(new AvailabilityDataTimeExtra(Utils.timeArray[i], false));
                            }
                            adapter = new MyAvailabilitySubAdapter(getActivity(),availabilityDataTimeExtra);
                            rvMyAvailablity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                    }else {
                        //callRefreshToken("view");
                    }
                    hideProgressDialog();
                }else if (response.code() == RESPONSECODE_401){
                   // callRefreshToken("view");
                }
            }
            @Override
            public void onFailure(@NonNull Call<AvailabilityModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: AvailabilityModel~~~~~>Sub"+t.toString());
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
        for (int number: numbers) {
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
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
    public static int[] addOneIntToArray(int[] initialArray , int newValue) {

        int[] newArray = new int[initialArray.length + 1];
        for (int index = 0; index < initialArray.length; index++) {
            Log.i("hai", "addOneIntToArray: arrray"+initialArray[index]);
            newArray[index] = initialArray[index];
        }

        newArray[newArray.length - 1] = newValue;
        Log.i("jao", "addOneIntToArray: --"+ Arrays.toString(newArray));
        return newArray;
    }

    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("token",userAccessToken );
        Log.e("Logout~~Refresh~~~", "Refresh~~~" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RefreshTokenModel> call2 = apiInterface.doGetRefreshToken(Utils.POST_ACCEPT_CONTENT_TYPE,Utils.POST_ACCEPT_CONTENT_TYPE,map);
        call2.enqueue(new Callback<RefreshTokenModel>() {
            @Override
            public void onResponse(@NonNull Call<RefreshTokenModel> call, @NonNull Response<RefreshTokenModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    String refreshToken = Objects.requireNonNull(response.body()).getAccessToken();
                    String refreshTokenType = Objects.requireNonNull(response.body()).getTokenType();
                    String refreshTokenTimeExp = String.valueOf(Objects.requireNonNull(response.body()).getExpiresIn());
                    savesharedpreference(refreshToken,refreshTokenType,refreshTokenTimeExp);
                    if (logout.equalsIgnoreCase("update")){
                        CallUpdateAvailableDetails();
                    }else if (logout.equalsIgnoreCase("view")){
                        CallViewAvailableDetails();
                    }
                }else if (response.code() == RESPONSECODE_401){
                    showTimeOut();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RefreshTokenModel> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void savesharedpreference(String access_token,String token_type,String exp_time) {

        getState.putString(Utils.SP_DRIVER_ACCESSTOKEN,access_token);
        getState.putString(Utils.SP_DRIVER_TOKENTYPE,token_type);
        getState.putString(Utils.SP_DRIVER_TOKENEXPTIME,exp_time);
        getState.apply();
    }
    /*******************************Get Firebase States********************************************/

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
        //removeFragment(Utils.FRAGMENT_MYAVAILABILITY);
        Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MYAVAILABILITY,
                getActivity(), null,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }

    /*private void showSnackbar(String s, String key) {

        final TSnackbar snackbar = TSnackbar.make(getActivity().findViewById(android.R.id.content), s, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        (snackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        snackbar.setMaxWidth(FrameLayout.LayoutParams.MATCH_PARENT);

        if (key.equalsIgnoreCase("warning")) {
            snackbarView.setBackgroundColor(Color.RED);
        } else {
            snackbarView.setBackgroundColor(Color.GRAY);
        }
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.App_Font));
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(font, Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0, 30, 0, 0);
        textView.setLayoutParams(params);
        snackbar.show();
    }*/

    public void hideKeyBoard()
    {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void showTimeOut() {
        Context mContext = getActivity() ;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.App_Font));

        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.timeout_title));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStrings = new SpannableString(getString(R.string.your_session_time_out_content));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableStringss = new SpannableString(getString(R.string.ok));
        spannableStringss.setSpan(tfSpan, 0, spannableStringss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        builder.setMessage(spannableStrings);
        builder.setPositiveButton(spannableStringss,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSPmodePrivate();
                    }
                });

        builder.show();
    }

    private void clearSPmodePrivate() {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        // deleteCache(MainActivity.this);
        Intent logout = new Intent(getActivity(), HomeActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);
        getActivity().finish();
    }

    //Inteface called
    public void onSuccess(int position) {
        Log.i(TAG, "onSuccess: interface called in myavailability sub fragment-->"+position);
        AddArrayList(position);
    }

    public void AddArrayList(int position){
        if(strDayArrayList.equals("1")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strMondaySelectedIds !=null) {
                if(arrayMonday!=null){
                    if (contains(arrayMonday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arrayMonday = removeIntArr(arrayMonday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arrayMonday));
                    } else {
                        Log.i(TAG, "onSuccess: else arrayMonday.length--"+arrayMonday.length);
                        arrayMonday = addOneIntToArray(arrayMonday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arrayMonday = new int[0];
                    arrayMonday = addElement(arrayMonday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arrayMonday = new int[0];
                arrayMonday = addElement(arrayMonday, positionInc);
            }
            strMondaySelectedIds = Arrays.toString(arrayMonday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arrayMonday));
        }
        else if(strDayArrayList.equals("2")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strTuesdaySelectedIds!=null) {
                if(arrayTuesday!=null){
                    if (contains(arrayTuesday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arrayTuesday = removeIntArr(arrayTuesday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arrayTuesday));
                    } else {
                        Log.i(TAG, "onSuccess: else arrayTuesday.length--"+arrayTuesday.length);
                        arrayTuesday = addOneIntToArray(arrayTuesday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arrayTuesday = new int[0];
                    arrayTuesday = addElement(arrayTuesday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arrayTuesday = new int[0];
                arrayTuesday = addElement(arrayTuesday, positionInc);
            }
            strTuesdaySelectedIds = Arrays.toString(arrayTuesday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arrayTuesday));
        }
        else if(strDayArrayList.equals("3")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strWednesdaySelectedIds!=null) {
                if(arrayWednesday!=null){
                    if (contains(arrayWednesday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arrayWednesday = removeIntArr(arrayWednesday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arrayWednesday));
                    } else {
                        Log.i(TAG, "onSuccess: else arrayWednesday.length--"+arrayWednesday.length);
                        arrayWednesday = addOneIntToArray(arrayWednesday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arrayWednesday = new int[0];
                    arrayWednesday = addElement(arrayWednesday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arrayWednesday = new int[0];
                arrayWednesday = addElement(arrayWednesday, positionInc);
            }
            strWednesdaySelectedIds = Arrays.toString(arrayWednesday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arrayWednesday));
        }
        else if(strDayArrayList.equals("4")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strThursdaySelectedIds!=null) {
                if(arrayThursday!=null){
                    if (contains(arrayThursday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arrayThursday = removeIntArr(arrayThursday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arrayThursday));
                    } else {
                        Log.i(TAG, "onSuccess: else arrayThursday.length--"+arrayThursday.length);
                        arrayThursday = addOneIntToArray(arrayThursday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arrayThursday = new int[0];
                    arrayThursday = addElement(arrayThursday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arrayThursday = new int[0];
                arrayThursday = addElement(arrayThursday, positionInc);
            }
            strThursdaySelectedIds = Arrays.toString(arrayThursday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arrayThursday));
        }
        else if(strDayArrayList.equals("5")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strFridaySelectedIds!=null) {
                if(arrayFriday!=null){
                    if (contains(arrayFriday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arrayFriday = removeIntArr(arrayFriday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arrayFriday));
                    } else {
                        Log.i(TAG, "onSuccess: else arrayFriday.length--"+arrayFriday.length);
                        arrayFriday = addOneIntToArray(arrayFriday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arrayFriday = new int[0];
                    arrayFriday = addElement(arrayFriday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arrayFriday = new int[0];
                arrayFriday = addElement(arrayFriday, positionInc);
            }
            strFridaySelectedIds = Arrays.toString(arrayFriday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arrayFriday));
        }
        else if(strDayArrayList.equals("6")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strSaterdaySelectedIds!=null) {
                if(arraySaterday!=null){
                    if (contains(arraySaterday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arraySaterday = removeIntArr(arraySaterday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arraySaterday));
                    } else {
                        Log.i(TAG, "onSuccess: else arraySaterday.length--"+arraySaterday.length);
                        arraySaterday = addOneIntToArray(arraySaterday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arraySaterday = new int[0];
                    arraySaterday = addElement(arraySaterday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arraySaterday = new int[0];
                arraySaterday = addElement(arraySaterday, positionInc);
            }
            strSaterdaySelectedIds = Arrays.toString(arraySaterday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arraySaterday));
        }
        else if(strDayArrayList.equals("0")){
            //AddAndRemoveArrayPosition(arrayMonday,position+1);
            int positionInc = position+1;
            Log.i(TAG, "onSuccess: position-->"+positionInc+"~~~");
            if(strSundaySelectedIds!=null) {
                if(arraySunday!=null){
                    if (contains(arraySunday, positionInc)) {
                        Log.i(TAG, "onSuccess: if");
                        // categoryModel.setIsSelected(true);
                        arraySunday = removeIntArr(arraySunday, positionInc);
                        Log.i(TAG, "onSuccess: " + Arrays.toString(arraySunday));
                    } else {
                        Log.i(TAG, "onSuccess: else arraySunday.length--"+arraySunday.length);
                        arraySunday = addOneIntToArray(arraySunday, positionInc);
                    }
                } else {
                    Log.i(TAG, "onSuccess: else else");
                    arraySunday = new int[0];
                    arraySunday = addElement(arraySunday, positionInc);
                }} else {
                Log.i(TAG, "onSuccess: else else else");
                arraySunday = new int[0];
                arraySunday = addElement(arraySunday, positionInc);
            }
            strSundaySelectedIds = Arrays.toString(arraySunday);
            Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arraySunday));
        }
    }

}
