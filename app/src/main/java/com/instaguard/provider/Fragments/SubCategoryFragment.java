package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Adapter.MapCategorySubAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AvailabilityModel;
import com.instaguard.provider.Models.CategorySubModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.VehicleSubCategoryData;
import com.instaguard.provider.Models.VehicleSubCategoryModel;
import com.instaguard.provider.R;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class SubCategoryFragment extends Fragment implements MapCategorySubAdapter.SuccessResponse{

    View view;
    SharedPreferences spCommon;
    SharedPreferences.Editor getUpdateProfile,getRequestState;

    SharedPreferences spCurrency;

    String strLanguage,strCurrency,strLanguageShortName,strCurrencySymbol;


    String userAccessToken, driverId,categoryId,categoryName;
    ImageButton imgbtnBack;

    CustomButton btnBookNow,btnBookLater;

    int n = 9;//Total Category
    String[] strCategoryName = new String[n];
    String[] strCategoryId = new String[n];
    String[] strCategoryImage = new String[n];
    String[] strPriceKm = new String[n];
    String[] strMinFare = new String[n];
    ArrayList<CategorySubModel> categoryModelArray;
    private CategorySubModel listFeed;
    RecyclerView rvSubCategory;
    RadioGroup rgCategory;
    String getSubcatName,getSubcatId,getSubcatMainId;
    MapCategorySubAdapter mapCategorySubAdapter;
    CustomButton btnUpdateServices;
    RelativeLayout rlParentView;

    String strPageKey,strSelectedServiceIds;
    int[] arraySelectedCategory;

    String TAG = "SubCategoryFragment";
    public SubCategoryFragment() {
        // Required empty public constructor
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(Utils.B_CATEGORY_ID);
            categoryName = getArguments().getString(Utils.B_CATEGORY_NAME);
            strSelectedServiceIds = getArguments().getString(Utils.B_SELECTED_CATEGORY);
            strPageKey = getArguments().getString("key");

            MapCategorySubAdapter.successResponse = this;
            Log.e("SubCategoryFragment~~~", categoryId+"~~"+categoryName+"~~"+strSelectedServiceIds+"~~"+strPageKey);

        }
    }

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_sub_sevice, container, false);

        getUpdateProfile = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getRequestState = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_REQUEST_STATE, MODE_PRIVATE).edit();

        spCommon = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = spCommon.getString(Utils.SP_DRIVERID, null);
        userAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        Log.e("driverId~~RideHistoryFragment~~", driverId);

        spCurrency = getActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = spCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = spCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);

        Log.e(TAG, "onCreateView: strLanguageShortName~~"+strLanguageShortName);

        if (strLanguageShortName == null){
            strLanguageShortName = "en";
        }


        if (strSelectedServiceIds != null) {
            if (!strSelectedServiceIds.equals("")){
                String[] parts = strSelectedServiceIds.split(",");
                arraySelectedCategory = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    arraySelectedCategory[i] = Integer.parseInt(parts[i].replaceAll(" ", ""));
                }
            }
        }else {
            Log.e(TAG, "onCreateView: ~~~~~~strSelectedServiceIds~~~~~~~~~~~"+strSelectedServiceIds);
        }

        initView(view);

        onBackButton(view);

        return view;

    }

    void initView(View view) {

        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        //   btnBookNow = (CustomButton) view.findViewById(R.id.btn_booknow);
        btnBookLater = (CustomButton) view.findViewById(R.id.btn_booklater);
        btnUpdateServices = (CustomButton) view.findViewById(R.id.btn_update_services);
        rlParentView = view.findViewById(R.id.rl_parent_view_manage_sub);

        // rgCategory = (RadioGroup) view.findViewById(R.id.rg_selcet_category);

        rvSubCategory = (RecyclerView)view. findViewById(R.id.rv_manage_sub_service);
        rvSubCategory.setVisibility(View.VISIBLE);

        if (strPageKey.equalsIgnoreCase("register")) {
            btnUpdateServices.setVisibility(View.VISIBLE);
        }else if (strPageKey.equalsIgnoreCase("login")) {
            btnUpdateServices.setVisibility(View.VISIBLE);
        } else {
            btnUpdateServices.setVisibility(View.VISIBLE);
        }

        onClick();

        getOnlineSubCategoryInfo();

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

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    private void onClick() {
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

        rlParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnUpdateServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e(TAG, "onClick: ~~~~~~~~~~~~~~"+strSelectedServiceIds);
                    if (strSelectedServiceIds!=null){
                        updateSelectedSubCategory();
                    }else {
                        Toast.makeText(getActivity(), "Please select service.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onClick: ~~~~~~"+e.toString());
                }
            }
        });

        categoryModelArray = new ArrayList<CategorySubModel>();
        mapCategorySubAdapter = new MapCategorySubAdapter(getActivity(),categoryModelArray );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSubCategory.setLayoutManager(linearLayoutManager);
        rvSubCategory.setAdapter(mapCategorySubAdapter);

        /*rvSubCategory.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(), rvSubCategory,
                        new RecyclerTouchListener.OnTouchActionListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(View view, int position) {
                                Log.i("rvSubCategory", "onClick: rvSubCategory-> ");
                                if(position != -1){
                                    //setProgresscount(position);
                                    listFeed  = categoryModelArray.get(position);
                                    getSubcatName = listFeed.getSubCategoryName();
                                    getSubcatId =  listFeed.getSubCategoryId();
                                    getSubcatMainId = listFeed.getSubMainCategoryId();
                                    Log.e("Category_getCategoryName~~",getSubcatName);
                                    Log.e("Category_getId~~",getSubcatId);
                                    Log.e("Category_getMainId~~",getSubcatMainId);
                                    //  mapCategorySubAdapter.selected(position);
                                    getRequestState.putString(Utils.B_SUB_CATEGORY_ID,listFeed.getSubCategoryId());
                                    getRequestState.putString(Utils.B_SUB_CATEGORY_NAME,listFeed.getSubCategoryName());
                                    getRequestState.apply();
                                }

                            }
                            @Override
                            public void onRightSwipe(View view, int position) {
                            }
                            @Override
                            public void onLeftSwipe(View view, int position) {
                            }
                        }));*/
    }

    private void getOnlineSubCategoryInfo() {

        userAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        if(categoryModelArray!=null)
            categoryModelArray.clear();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", categoryId);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<VehicleSubCategoryModel> call2 = apiInterface.doGetVehiclecategoryServicesDriver("Bearer "+userAccessToken,map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<VehicleSubCategoryModel>() {
            @Override
            public void onResponse(@NonNull Call<VehicleSubCategoryModel> call, @NonNull retrofit2.Response<VehicleSubCategoryModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    hideProgressDialog();
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("categories services listed successfully.")) {
                        List<VehicleSubCategoryData> getve = Objects.requireNonNull(response.body()).getVehicleSubCategoryData();
                        String[] items = new String[getve.size()];
                        Log.i("itemsitems", "" + Arrays.toString(items));
                        for (int i = 0; i < getve.size(); i++) {
                            CategorySubModel categoryModel = new CategorySubModel();

                            if (strLanguageShortName.equals("ar")){
                                categoryModel.setSubCategoryName(getve.get(i).getServiceNameSpanish());
                            }else {
                                categoryModel.setSubCategoryName(getve.get(i).getServiceName());
                            }

                            categoryModel.setSubCategoryId(String.valueOf(getve.get(i).getId()));
                            categoryModel.setSubCategoryImage(getve.get(i).getServiceImage());
                            categoryModel.setSubCategoryBasefare(String.valueOf(getve.get(i).getBaseFare()));
                            categoryModel.setSubCategoryAdminComm(String.valueOf(getve.get(i).getAdminCommission()));
                            categoryModel.setSubMainCategoryId(String.valueOf(getve.get(i).getId()));
                            categoryModel.setSubCategoryImage(String.valueOf(getve.get(i).getServiceImage()));

                            Log.i(TAG, "onResponse: getOnlineSubCategoryInfo-->" + Arrays.toString(arraySelectedCategory) + "~~~" + getve.get(i).getId());
                            if (arraySelectedCategory != null) {
                                if (contains(arraySelectedCategory, getve.get(i).getId())) {
                                    categoryModel.setIsSelected(true);
                                } else {
                                    categoryModel.setIsSelected(false);
                                }
                            } else {
                                categoryModel.setIsSelected(false);
                            }

                            categoryModelArray.add(categoryModel);

                            items[i] = String.valueOf(getve.get(i));

                            /*if (strLanguageShortName.equals("es")){
                                strCategoryName[i] = getve.get(i).getServiceNameSpanish();
                            }else {
                                strCategoryName[i] = getve.get(i).getServiceName();
                            }

                            strCategoryId[i] = String.valueOf(getve.get(i).getId());
                            strCategoryImage[i] = getve.get(i).getServiceImage();
                            //  strMinFare[i] = String.valueOf(getve.get(i).getCategoryId());


                            Log.i("strCategoryId", "strCategoryId" + strCategoryId[i]);
                            Log.i("strCategoryName", "strCategoryName" + strCategoryName[i]);
                            Log.i("strCategoryImage", "strCategoryImage" + strCategoryImage[i]);
                            // Log.i("strPriceKm", "strPriceKm" + strPriceKm[i]);
                            // Log.i("strMinFare", "strMinFare" + strMinFare[i]);*/
                        }
                    } else if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("No records Found.")){
                        Toast.makeText(getActivity(), R.string.no_result_found, Toast.LENGTH_SHORT).show();
                    } else {
                        callRefreshToken("Category");
                    }
                    mapCategorySubAdapter.notifyDataSetChanged();
                }else if (response.code() == RESPONSECODE_401){
                    callRefreshToken("Category");
                }
            }
            @Override
            public void onFailure(@NonNull Call<VehicleSubCategoryModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                call.cancel();
            }
        });

    }
    private void updateSelectedSubCategory() {

        String removeArrayBox = strSelectedServiceIds.replaceAll("]","").replaceAll("\\[","").replaceAll(" ","");
        userAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("service_ids", removeArrayBox);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AvailabilityModel> call2 = apiInterface.doUpdateVehicleCategory("Bearer "+userAccessToken,map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<AvailabilityModel>() {
            @Override
            public void onResponse(@NonNull Call<AvailabilityModel> call, @NonNull Response<AvailabilityModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {

                    Log.e(TAG, "onFailure: updateSelectedSubCategory---->"+response.body().getMessage());

                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver Services updated successfully.")) {
                        if(response.body().getAvailabilityData().getServiceIds()!=null){
                            // Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                            String servicesIds = String.valueOf(response.body().getAvailabilityData().getServiceIds());
                            Log.i(TAG, "onResponse: UpdateSelectedSubCategory-->"+servicesIds);
                            try {
                                updateDriverCategoryFirebase(servicesIds);
                                backButtonFunction();
                            } catch (Throwable tx) {
                                // Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                            }
                        }else{
//                            Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        // callRefreshToken("viewprofile");
                    }
                    hideProgressDialog();
                }else if (response.code() == RESPONSECODE_401){
                    // callRefreshToken("viewprofile");
                }
            }
            @Override
            public void onFailure(@NonNull Call<AvailabilityModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: updateSelectedSubCategory---->"+t.toString());
                call.cancel();
            }
        });

    }
    public void updateDriverCategoryFirebase(String categoryId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("providers").child(driverId);
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("category", categoryId);
        databaseReference.updateChildren(taskMap);
    }
    public static boolean contains(int[] arr, int item) {
        for (int n : arr) {
            if (item == n) {
                return true;
            }
        }
        return false;
    }
    private void callRefreshToken(String logout) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
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
                    if (logout.equalsIgnoreCase("Category")){
                        getOnlineSubCategoryInfo();
                    }else if (logout.equalsIgnoreCase("viewprofile")){
                        updateSelectedSubCategory();
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

        getUpdateProfile.putString(Utils.SP_DRIVER_ACCESSTOKEN,access_token);
        getUpdateProfile.putString(Utils.SP_DRIVER_TOKENTYPE,token_type);
        getUpdateProfile.putString(Utils.SP_DRIVER_TOKENEXPTIME,exp_time);
        getUpdateProfile.apply();
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
    @Override
    public void onDestroy() {
        super.onDestroy();

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

       /* Utils.switchContent(R.id.frame_mainactivity,
                Utils.FRAGMENT_MANAGE_SERVICE,
                getActivity(), null,
                Utils.AnimationType.SLIDE_IN_SLIDE_OUT);*/

        if (strPageKey.equalsIgnoreCase("register")){
            Bundle args = new Bundle();
            args.putString("key",strPageKey);
            Utils.switchContent(R.id.frame_launch_container,
                    Utils.FRAGMENT_MANAGE_SERVICE,
                    getActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        }else if (strPageKey.equalsIgnoreCase("login")){
            Bundle args = new Bundle();
            args.putString("key",strPageKey);
            Utils.switchContent(R.id.frame_launch_container,
                    Utils.FRAGMENT_MANAGE_SERVICE,
                    getActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        }else {
            Bundle args = new Bundle();
            args.putString("key",strPageKey);
            Utils.switchContent(R.id.frame_mainactivity,
                    Utils.FRAGMENT_MANAGE_SERVICE,
                    getActivity(), args,
                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
        }

    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }


    @Override
    public void onSuccess(int position) {
        Log.i(TAG, "onSuccess: position-->"+position+"~~~");
        if(strSelectedServiceIds!=null) {
            if(arraySelectedCategory!=null){
                if (contains(arraySelectedCategory, position)) {
                    Log.i(TAG, "onSuccess: if");
                    // categoryModel.setIsSelected(true);
                    arraySelectedCategory = removeIntArr(arraySelectedCategory, position);
                    Log.i(TAG, "onSuccess: " + Arrays.toString(arraySelectedCategory));
                } else {
                    Log.i(TAG, "onSuccess: else arraySelectedCategory.length--"+arraySelectedCategory.length);
                    // arraySelectedCategory = new int[arraySelectedCategory.length+1];
                    arraySelectedCategory = addOneIntToArray(arraySelectedCategory, position);

               /* int add[] = new int[arraySelectedCategory.length+1];
                for(int i=0;i<add.length;i++){
                    if(i==add.length-1){
                        add[i]=position;
                    }else{
                        add[i]=arraySelectedCategory[i];
                    }
                    System.out.println("storedvaues-->+"+add[i]);
                }*/
                    // arraySelectedCategory = ArrayUtils.add(series, 3);
                }
            } else {
                Log.i(TAG, "onSuccess: else else");
//            String[] parts = strSelectedServiceIds.split(",");
                arraySelectedCategory = new int[0];
                arraySelectedCategory = addElement(arraySelectedCategory, position);
            }} else {
            Log.i(TAG, "onSuccess: else else else");
//            String[] parts = strSelectedServiceIds.split(",");
            arraySelectedCategory = new int[0];
            arraySelectedCategory = addElement(arraySelectedCategory, position);
        }
        strSelectedServiceIds = Arrays.toString(arraySelectedCategory);
        Log.i(TAG, "onSuccess: in the onsuccess-->"+ Arrays.toString(arraySelectedCategory));
    }
    public static int[] addOneIntToArray(int[] initialArray , int newValue) {

        int[] newArray = new int[initialArray.length + 1];
        for (int index = 0; index < initialArray.length; index++) {
            Log.i("hai", "addOneIntToArray: arrray"+initialArray[index]);
            newArray[index] = initialArray[index];
        }

        newArray[newArray.length - 1] = newValue;
        Log.i("jao", "addOneIntToArray: --"+Arrays.toString(newArray));
        return newArray;
    }
}
