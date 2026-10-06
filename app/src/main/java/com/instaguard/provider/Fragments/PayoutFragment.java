package com.instaguard.provider.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instaguard.provider.Adapter.ViewPagerAdapter;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;
import com.instaguard.provider.databinding.FragmentPayoutBinding;

import java.util.Objects;

public class PayoutFragment extends Fragment {
    FragmentPayoutBinding binding;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPayoutBinding.inflate(getLayoutInflater(), container, false);

        viewPagerAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.add(new PayoutsFragment(), "Withdrawal Request");
        viewPagerAdapter.add(new PayoutTransctionsFragment(), "Transaction");

        binding.viewpager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

        binding.imgbtnBack.setOnClickListener(view -> {
            Utils.CURRENT_TAG = null;
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentByTag(Utils.FRAGMENT_PAYOUT))).commit();
        });
        return binding.getRoot();
    }
}