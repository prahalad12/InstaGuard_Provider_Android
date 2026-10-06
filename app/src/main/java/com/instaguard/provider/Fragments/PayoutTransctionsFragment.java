package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.instaguard.provider.Adapter.PayoutTransactionAdapter;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PayoutTransactionHistoryModel;
import com.instaguard.provider.databinding.FragmentPayoutTransctionsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutTransctionsFragment extends Fragment {
    FragmentPayoutTransctionsBinding binding;
    PayoutTransactionAdapter adapter;
    String userId;
    SharedPreferences spPrefs;
    ArrayList<PayoutTransactionHistoryModel.Result> listData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPayoutTransctionsBinding.inflate(getLayoutInflater(), container, false);

        spPrefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
        listData = new ArrayList<>();

        getData();

        binding.listRF.setOnRefreshListener(this::getData);
        return binding.getRoot();
    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PayoutTransactionHistoryModel> call = apiInterface.callPayoutTransaction(map);
        showProgressDialog(getContext());

        call.enqueue(new Callback<PayoutTransactionHistoryModel>() {
            @Override
            public void onResponse(@NonNull Call<PayoutTransactionHistoryModel> call, @NonNull Response<PayoutTransactionHistoryModel> response) {
                hideProgressDialog();
                binding.listRF.setRefreshing(false);

                if (response.body() != null) {

                    if (response.body().getCurrentStatus() != null){
                        HashMap<String, Object> map2 = new HashMap<>();
                        map2.put("status", response.body().getCurrentStatus());
                        map2.put("msg", response.body().getPayoutMsg());
                        EventBus.getDefault().postSticky(map2);
                    }

                    if (!response.body().getResult().isEmpty()) {
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.listRv.setVisibility(View.VISIBLE);

                        listData = response.body().getResult();
                        System.out.println("payout transaction list "+ listData);

                        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        binding.listRv.setLayoutManager(llm);

                        adapter = new PayoutTransactionAdapter(listData);
                        binding.listRv.setAdapter(adapter);

                    } else {
                        binding.listRv.setVisibility(View.GONE);
                        binding.noDataFound.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PayoutTransactionHistoryModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                System.out.println("payout history getting error "+ t.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event) {
        if (event.equalsIgnoreCase("payout added")) {
            getData();
        }
    }
}