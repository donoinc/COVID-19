package com.themescoder.laravelecommerce.fragment;


import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.themescoder.laravelecommerce.R;
import com.themescoder.laravelecommerce.activities.MainActivity;
import com.themescoder.laravelecommerce.constant.ConstantValues;

import am.appwise.components.ni.NoInternetDialog;


public class Thank_You extends Fragment {
    
   // AdView mAdView;
    FrameLayout banner_adView;
    Button order_status_btn, continue_shopping_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thank_you, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
       // noInternetDialog.show();


        // Binding Layout Views
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        order_status_btn = (Button) rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = (Button) rootView.findViewById(R.id.continue_shopping_btn);
    
      /*  if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(getContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
        }*/
        

        // Binding Layout Views
        order_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                // Navigate to My_Orders Fragment
                Fragment fragment = new My_Orders();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                
            }
        });


        // Binding Layout Views
        continue_shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to HomePage Fragment
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });


        return rootView;
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        return super.onOptionsItemSelected(item);
    }
    
}

