package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_401;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.BaseActivity;
import com.instaguard.provider.Activities.HomeActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Adapter.MapCategoryAdapter;
import com.instaguard.provider.CustomViews.CustomButton;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.Helpers.RecyclerTouchListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.AvailabilityModel;
import com.instaguard.provider.Models.CategoryModel;
import com.instaguard.provider.Models.RefreshTokenModel;
import com.instaguard.provider.Models.VehicleCategoryData;
import com.instaguard.provider.Models.VehicleCategoryModel;
import com.instaguard.provider.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageServiceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    SharedPreferences spCommon, spCurrency;
    SharedPreferences.Editor getRequestState, getManageState;

    String strLanguage, strCurrency, strLanguageShortName, strCurrencySymbol;

    String driverId, driverAccessToken;
    ImageButton imgbtnBack;
    ImageView ivRefresh;
    RelativeLayout rlParentViewMS;
    SwipeRefreshLayout swipeRefreshLayout;
    CustomButton btnBackToLogin;

    /*Category Start*/

    ArrayList<CategoryModel> categoryModelArray;
    private CategoryModel listFeed;
    RecyclerView rvCategory;
    MapCategoryAdapter mapCategoryAdapter;
    /*Category End*/

    public String TAG = "ManageServiceFragment";
    private String strSelectedServiceIds;


    private DatabaseReference providerApproveStatusDatabaseBRef;
    private ValueEventListener providerApproveStatusValueEventListener;
    private String strCategoryStatus, strApproveStatus, strPageKey;


    public ManageServiceFragment() {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strPageKey = getArguments().getString("key");
            Log.e("Manage Service strPageKey~~", strPageKey);
        }

    }

    @SuppressLint({"LongLogTag", "CommitPrefEdits"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_sevice, container, false);

        getManageState = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        getRequestState = requireActivity().getSharedPreferences(Utils.MY_PREFS_REQUEST_STATE, MODE_PRIVATE).edit();


        spCommon = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = spCommon.getString(Utils.SP_DRIVERID, null);
        driverAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        spCurrency = requireActivity().getSharedPreferences(Utils.PREFS_NAME_CURRENCY, MODE_PRIVATE);
        strLanguage = spCurrency.getString(Utils.SP_LANGUAGE, null);
        strLanguageShortName = spCurrency.getString(Utils.SP_LANGUAGE_SHORT_NAME, null);
        strCurrency = spCurrency.getString(Utils.SP_CURRENCY, null);
        strCurrencySymbol = spCurrency.getString(Utils.SP_CURRENCY_SYMBOL, null);

        Log.e(TAG, "onCreateView: strLanguageShortName~~" + strLanguageShortName);

        if (strLanguageShortName == null) {
            strLanguageShortName = "en";
        }

        Log.e("driverId~~RideHistoryFragment~~", driverId);

        initView(view);

        onRefresh();

        onBackButton(view);

        return view;

    }

    void initView(View view) {

        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);
        btnBackToLogin =  view.findViewById(R.id.btn_back_to_login);

        rlParentViewMS = view.findViewById(R.id.rl_parent_view_manage_service);
        ivRefresh = view.findViewById(R.id.iv_menu_profile);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_manage_service);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (strPageKey.equalsIgnoreCase("register")) {
            imgbtnBack.setImageResource(R.drawable.ic_home_white);
            btnBackToLogin.setVisibility(View.VISIBLE);
            btnBackToLogin.setText(getResources().getString(R.string.back_login));
        } else if (strPageKey.equalsIgnoreCase("login")) {
            imgbtnBack.setImageResource(R.drawable.ic_home_white);
            btnBackToLogin.setVisibility(View.VISIBLE);
            btnBackToLogin.setText(getResources().getString(R.string.back_dash));
        } else {
            btnBackToLogin.setVisibility(View.GONE);
            imgbtnBack.setImageResource(R.drawable.ic_back_whitearrow);
        }

        rlParentViewMS.setOnClickListener(v -> {

        });
        imgbtnBack.setOnClickListener(v -> backButtonFunction());

        btnBackToLogin.setOnClickListener(v -> imgbtnBack.performClick());

        ivRefresh.setOnClickListener(view1 -> onRefresh());

        categoryModelArray = new ArrayList<>();
        mapCategoryAdapter = new MapCategoryAdapter(categoryModelArray, getActivity());
        rvCategory = (RecyclerView) view.findViewById(R.id.rv_manage_service);
        rvCategory.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCategory.setLayoutManager(linearLayoutManager);
        rvCategory.setAdapter(mapCategoryAdapter);

    }

    private void getOnlineCategoryInfo() {

        driverAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);

        if (categoryModelArray != null)
            categoryModelArray.clear();
        HashMap<String, Object> map = new HashMap<>();
        //  map.put("token", userAccessToken);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<VehicleCategoryModel> call2 = apiInterface.doGetVehiclecategoryUpdateDetails("Bearer " + driverAccessToken, Utils.POST_ACCEPT_CONTENT_TYPE, Utils.POST_ACCEPT_CONTENT_TYPE, map);
        showProgressDialog(getActivity());
        call2.enqueue(new Callback<VehicleCategoryModel>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<VehicleCategoryModel> call, @NonNull retrofit2.Response<VehicleCategoryModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("categories listed successfully.")) {
                        List<VehicleCategoryData> getve = Objects.requireNonNull(response.body()).getVehicleCategoryData();
                        String[] items = new String[getve.size()];
                        Log.i("itemsitems", Arrays.toString(items));
                        for (int i = 0; i < getve.size(); i++) {
                            CategoryModel categoryModel = new CategoryModel();

                            if (strLanguageShortName.equals("si")) {
                                categoryModel.setCategoryName(getve.get(i).getCategoryNameSpanish());
                            } else {
                                categoryModel.setCategoryName(getve.get(i).getCategoryName());
                            }
                            categoryModel.setCategoryId(String.valueOf(getve.get(i).getId()));
                            categoryModel.setCategoryImage(getve.get(i).getCategoryImage());

                            categoryModel.setIsSelected(i == 0);

                            categoryModelArray.add(categoryModel);

                            items[i] = String.valueOf(getve.get(i));
                        }
                    } else {
                        //callRefreshToken("Category");
                    }
                    hideProgressDialog();

                    mapCategoryAdapter.notifyDataSetChanged();

                } else if (response.code() == RESPONSECODE_401) {
                    //callRefreshToken("Category");
                }
            }

            @Override
            public void onFailure(@NonNull Call<VehicleCategoryModel> call, @NonNull Throwable t) {
                Log.i("errror", "errr" + t);
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void getSelectedServices() {

        driverAccessToken = spCommon.getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", driverId);
        map.put("token", "123");
        Log.i(TAG, "getSelectedServices" + map);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AvailabilityModel> call2 = apiInterface.doGetAvailable(map);
        // showProgressDialog(getActivity());
        call2.enqueue(new Callback<AvailabilityModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AvailabilityModel> call, @NonNull Response<AvailabilityModel> response) {
                if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("Driver Services listed successfully.")) {
                        if (Objects.requireNonNull(response.body()).getAvailabilityData().getServiceIds() != null) {
                            //  Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                            strSelectedServiceIds = String.valueOf(Objects.requireNonNull(response.body()).getAvailabilityData().getServiceIds());
                            Log.i(TAG, "onResponse: getSelectedServices-->" + strSelectedServiceIds);
                        } else {
//                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //callRefreshToken("viewprofile");
                    }
                    // hideProgressDialog();
                } else if (response.code() == RESPONSECODE_401) {
                    // callRefreshToken("viewprofile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AvailabilityModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Mangage serivce-->>" + t.toString());
                call.cancel();
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    //get Approve Status in firebase
    private void getProviderApproveStatusAmount() {

        if (providerApproveStatusDatabaseBRef != null) {
            providerApproveStatusDatabaseBRef.removeEventListener(providerApproveStatusValueEventListener);
        }
        providerApproveStatusDatabaseBRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId);
        providerApproveStatusValueEventListener = providerApproveStatusDatabaseBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    try {

                        if (dataSnapshot.child("category").getValue() != null) {
                            strCategoryStatus = dataSnapshot.child("category").getValue().toString();
                        } else {
                            strCategoryStatus = "0";
                        }

                        if (dataSnapshot.child("approved").getValue() != null) {
                            strApproveStatus = dataSnapshot.child("approved").getValue().toString();
                        } else {
                            strApproveStatus = "0";
                        }

                        if (strApproveStatus.equals("1")) {

                            rvCategory.addOnItemTouchListener(
                                    new RecyclerTouchListener(getActivity(), rvCategory,
                                            new RecyclerTouchListener.OnTouchActionListener() {
                                                @SuppressLint("LongLogTag")
                                                @Override
                                                public void onClick(View view, int position) {
                                                    Log.i("rvCategory", "onClick: rvCategory-> ");
                                                    if (position != -1) {

                                                        /*listFeed = categoryModelArray.get(position);
                                                        mapCategoryAdapter.selected(position);
                                                        if (strPageKey.equalsIgnoreCase("register")) {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_launch_container,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                                        } else if (strPageKey.equalsIgnoreCase("login")) {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_launch_container,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                                        } else {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_mainactivity,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);
                                                        }
                                                        getRequestState.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                        getRequestState.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                        getRequestState.apply();*/

                                                        Toast.makeText(requireActivity(), getResources().getString(R.string.once_the_admin_has_approved_can_t_the_managed_service_be_changed), Toast.LENGTH_SHORT).show();

                                                    }

                                                }

                                                @Override
                                                public void onRightSwipe(View view, int position) {
                                                }

                                                @Override
                                                public void onLeftSwipe(View view, int position) {
                                                }
                                            }));

                        } else {

                            rvCategory.addOnItemTouchListener(
                                    new RecyclerTouchListener(getActivity(), rvCategory,
                                            new RecyclerTouchListener.OnTouchActionListener() {
                                                @SuppressLint("LongLogTag")
                                                @Override
                                                public void onClick(View view, int position) {
                                                    Log.i("rvCategory", "onClick: rvCategory-> ");
                                                    if (position != -1) {
                                                        //setProgresscount(position);
                                                        listFeed = categoryModelArray.get(position);
                                                        Log.e("Category_getCategoryName~~", listFeed.getCategoryName());
                                                        Log.e("Category_getId~~", listFeed.getCategoryId());
                                                        mapCategoryAdapter.selected(position);


                                                        if (strPageKey.equalsIgnoreCase("register")) {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_launch_container,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                                                        } else if (strPageKey.equalsIgnoreCase("login")) {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_launch_container,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                                                        } else {
                                                            Bundle args = new Bundle();
                                                            args.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                            args.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                            args.putString(Utils.B_SELECTED_CATEGORY, strSelectedServiceIds);
                                                            args.putString("key", strPageKey);

                                                            Utils.switchContent(R.id.frame_mainactivity,
                                                                    Utils.FRAGMENT_SUBCATEGORY,
                                                                    requireActivity(), args,
                                                                    Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                                                        }

                                                        getRequestState.putString(Utils.B_CATEGORY_ID, listFeed.getCategoryId());
                                                        getRequestState.putString(Utils.B_CATEGORY_NAME, listFeed.getCategoryName());
                                                        getRequestState.apply();

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

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onDataChange: getProviderApproveStatusAmount~~~" + e.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError);
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

        if (strPageKey.equalsIgnoreCase("register")) {
            if (strCategoryStatus != null) {
                if (!strCategoryStatus.equalsIgnoreCase("0")) {

                    getManageState.putBoolean(Utils.SP_DRIVER_LOGIN, false);
                    getManageState.apply();

                    Utils.switchContent(R.id.frame_launch_container,
                            Utils.FRAGMENT_SIGNIN,
                            getActivity(), null,
                            Utils.AnimationType.SLIDE_IN_SLIDE_OUT);

                } else {
                    callAlertDialog();
                }
            } else {
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }

        } else if (strPageKey.equalsIgnoreCase("login")) {
            if (strCategoryStatus != null) {
                if (!strCategoryStatus.equalsIgnoreCase("0")) {

                    //removeFragment(Utils.FRAGMENT_MANAGE_SERVICE);

                    getManageState.putBoolean(Utils.SP_DRIVER_LOGIN, true);
                    getManageState.apply();

                    Intent Signin = new Intent(getActivity(), MainActivity.class);
                    startActivity(Signin);
                    requireActivity().finish();

                } else {
                    callAlertDialog();
                }
            } else {
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }

        } else {
            removeFragment(Utils.FRAGMENT_MANAGE_SERVICE);
        }
    }

    private void callAlertDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(),
                getString(R.string.App_Font));
        BaseActivity.CustomTFSpan tfSpan = new BaseActivity.CustomTFSpan(tf);
        SpannableString spannableString = new SpannableString(getString(R.string.oops_select_any_manage_service));
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableStrings = new SpannableString(getString(R.string.ok));
        spannableStrings.setSpan(tfSpan, 0, spannableStrings.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(spannableString).setPositiveButton(spannableStrings,
                (dialog, which) -> dialog.dismiss()).show();
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Tag))).commit();
    }

    public void showTimeOut() {
        Context mContext = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);

        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(),
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
                (dialog, which) -> clearSPmodePrivate());

        builder.show();
    }

    private void clearSPmodePrivate() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        // deleteCache(MainActivity.this);
        Intent logout = new Intent(getActivity(), HomeActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);
        requireActivity().finish();
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);

        getProviderApproveStatusAmount();

        getSelectedServices();

        getOnlineCategoryInfo();

    }
}
