package com.instaguard.provider.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Activities.BaseActivity.hideProgressDialog;
import static com.instaguard.provider.Activities.BaseActivity.showProgressDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.PayoutModel;
import com.instaguard.provider.R;
import com.instaguard.provider.databinding.FragmentPayoutsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutsFragment extends Fragment {
    FragmentPayoutsBinding binding;
    SharedPreferences spPrefs;
    String userId, walBlc;
    Integer walletBlc, enteredAmt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPayoutsBinding.inflate(inflater, container, false);

        getData();
        onClick();
        return binding.getRoot();
    }
    private void getData() {
        spPrefs = requireActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        userId = spPrefs.getString(Utils.SP_DRIVERID, null);
        System.out.println("Driver id is "+ userId);
        getWalletBlc();
    }

    private void onClick() {
        binding.btnAddMoney.setOnClickListener(view -> {
            String ip = Objects.requireNonNull(binding.edtCustomAmount.getText()).toString();
            System.out.println("entered amount is "+ ip);

            if (!ip.equals("Enter Custom Amount") && !ip.isEmpty()){
                if (!ip.equals("0")) {
                    double blc = Double.parseDouble(walBlc);
//                    walletBlc = Integer.parseInt(blc);
                    enteredAmt = Integer.parseInt(ip);

                    if (binding.edtCustomAmount.getText() == null && binding.edtCustomAmount.getText().toString().isEmpty()) {
                        showToast("Enter Amount");
                    } else if (blc < enteredAmt) {
                        showToast("Insufficient Fund");
                    }else {
                        withDrawl();
                    }

                } else {
                    showToast("Enter Minimum Amount");
                }
            } else {
                showToast("Enter Custom Amount");
            }

        });
    }

    private void withDrawl() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", userId);
        map.put("amount", Objects.requireNonNull(binding.edtCustomAmount.getText()).toString() );

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PayoutModel> call = apiInterface.callPayout(map);
        showProgressDialog(getContext());

        call.enqueue(new Callback<PayoutModel>() {
            @Override
            public void onResponse(@NonNull Call<PayoutModel> call, @NonNull Response<PayoutModel> response) {
                hideProgressDialog();
                if (response.body() != null) {

                    if (response.body().getSuccess()) {
                        showToast(response.body().getMessage());
                        getWalletBlc();
                        binding.edtCustomAmount.setText("");
                        EventBus.getDefault().postSticky("payout added");
                    } else {
                        binding.edtCustomAmount.setText("");
                        showToast(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PayoutModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                System.out.println("with drawl error "+ t.getMessage());
            }
        });
    }

    private void getWalletBlc() {
        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (snapshot.child("wallet").getValue() != null) {
                        walBlc = snapshot.child("wallet").getValue().toString();
                        System.out.println("wallet balance payouts "+ walBlc);
                        binding.blc.setText("£"+walBlc);

                        if (!walBlc.equals("0")) {
                            unRestrictCardClick();
                        } else {
                            restrictCardClick("Add funds to your wallet and proceed with payouts");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("get wallet blc error "+ error.getMessage());
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
    public void onEventListened(HashMap<String, Object> event){
        if (event != null) {
            if (Objects.requireNonNull(event.get("status")).toString().equals("true")){
               String msg = Objects.requireNonNull(event.get("msg")).toString();
               restrictCardClick(msg);
            } else {
                unRestrictCardClick();
            }
        }
    }

    private void restrictCardClick(String msg) {
        binding.paymentStatus.setVisibility(View.VISIBLE);
        binding.paymentStatus.setText(msg);
        binding.btnAddMoney.setEnabled(false);
        binding.edtCustomAmount.setEnabled(false);
        binding.cv.setCardBackgroundColor(requireContext().getColor(R.color.gray_light));
    }

    private void unRestrictCardClick(){
        binding.btnAddMoney.setEnabled(true);
        binding.edtCustomAmount.setEnabled(true);
        binding.paymentStatus.setVisibility(View.GONE);
        binding.cv.setCardBackgroundColor(requireContext().getColor(R.color.white_));
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}