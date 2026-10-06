package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instaguard.provider.Activities.WebViewActivity;
import com.instaguard.provider.Adapter.BankDetailsAdapter;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.WebResModel;
import com.instaguard.provider.R;
import com.instaguard.provider.databinding.FragmentBankDetailsNewBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsFragmentNew extends Fragment {
    FragmentBankDetailsNewBinding binding;
    SharedPreferences spPrefs;
    String userId;
    ArrayList<BankModel.Datum> bankList = new ArrayList<>();
    BankDetailsAdapter bankDetailsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBankDetailsNewBinding.inflate(inflater, container, false);

        spPrefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);

        System.out.println("Driver Id is " + userId);

        callFB();
        onClick();
        return binding.getRoot();
    }

    private void getBankDetails() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userId);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BankModel> call = apiInterface.doGetBankDetails(map);
        showProgressDialog(getContext());

        call.enqueue(new Callback<BankModel>() {
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {
                if (response.body() != null) {
                    hideProgressDialog();
                    binding.nsv.setRefreshing(false);

                    switch (response.body().getStripeDocumentStatus()) {
                        case 0:
                        case 2:
                            binding.stripeStatus.setVisibility(View.GONE);
                            break;
                        case 1:
                            binding.stripeStatus.setVisibility(View.VISIBLE);
                            binding.stripeStatus.setText(getText(R.string.document_upload));
                            break;
                    }

                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        binding.addBankAccBtn.setText(getText(R.string.update_bank_account));
                        binding.bankDetailsCons.setVisibility(View.VISIBLE);
                        bankList = response.body().getData();

                        bankDetailsAdapter = new BankDetailsAdapter(bankList);
                        binding.bankDetailsRV.setAdapter(bankDetailsAdapter);

                    } else {
                        binding.bankDetailsCons.setVisibility(View.GONE);
                        binding.addBankAccBtn.setText(getText(R.string.add_bank_account));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                System.out.println("get bank details api error "+t.getMessage());
            }
        });
    }

    private void onClick() {

        binding.imgbtnBack.setOnClickListener(view -> {
            Utils.CURRENT_TAG = null;
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Utils.FRAGMENT_BANKDETAIL_NEW))).commit();
        });

        binding.addBankAccBtn.setOnClickListener(view -> moveToWebPage());

        binding.nsv.setOnRefreshListener(this::getBankDetails);
    }

    private void moveToWebPage() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WebResModel> call = apiInterface.createBankAccount(map);
        showProgressDialog(getContext());
        call.enqueue(new Callback<WebResModel>() {
            @Override
            public void onResponse(@NonNull Call<WebResModel> call, @NonNull Response<WebResModel> response) {

                if (response.body() != null && response.body().getStatus().equals("success")) {
                    if (response.body().getUrl() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("webUrl", response.body().getUrl());
                        Intent intent = new Intent(requireActivity(), WebViewActivity.class);
                        intent.putExtras(bundle);
                        hideProgressDialog();
                        startActivity(intent);
                    }
                } else {
                    hideProgressDialog();
                    showToast(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WebResModel> call, @NonNull Throwable t) {
                System.out.println("add bank details api error "+ t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void callFB() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId);
        databaseReference.child("return_status").setValue(0);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBankDetails();
    }
}