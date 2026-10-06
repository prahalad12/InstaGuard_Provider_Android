package com.instaguard.provider.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class PaymentFragment extends Fragment {

    View view;
    SharedPreferences SP_Prefs;
    String driverId;
    ImageButton imgbtnBack;

    public PaymentFragment() {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             Log.e("driverId~~RideHistoryFragment~~", "");
         }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment, container, false);

        SP_Prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = SP_Prefs.getString(Utils.SP_DRIVERID, null);
        Log.e("driverId~~RideHistoryFragment~~", driverId);

        initView(view);

        onBackButton(view);

        return view;

    }

    void initView(View view) {


        imgbtnBack = (ImageButton) view.findViewById(R.id.imgbtn_back);


        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonFunction();
            }
        });

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
      //  MainActivity.frameClickable(false);
        removeFragment(Utils.FRAGMENT_PAYMENT);
    }

    public void removeFragment(String Tag) {
        Utils.CURRENT_TAG = null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Tag)).commit();
    }


}
