package com.themescoder.laravelecommerce.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.themescoder.laravelecommerce.activities.MainActivity;
import com.themescoder.laravelecommerce.R;

import java.util.ArrayList;
import java.util.List;

import com.themescoder.laravelecommerce.app.App;
import com.themescoder.laravelecommerce.adapters.CategoryListAdapter_2;
import com.themescoder.laravelecommerce.constant.ConstantValues;
import com.themescoder.laravelecommerce.models.category_model.CategoryDetails;

import am.appwise.components.ni.NoInternetDialog;


public class HomePage_5 extends Fragment  {

    RecyclerView category_recycler;

    CategoryListAdapter_2 categoryListAdapter;

    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> mainCategoriesList;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage_5, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_HEADER);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        allCategoriesList = new ArrayList<>();

        // Get CategoriesList from ApplicationContext
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();
        

        // Binding Layout Views
        category_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);
    
      /*  if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize InterstitialAd
            final InterstitialAd mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(ConstantValues.AD_UNIT_ID_INTERSTITIAL);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    mInterstitialAd.show();
                }
            });
        }*/
        

        mainCategoriesList= new ArrayList<>();

        for (int i=0;  i<allCategoriesList.size();  i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase("0")) {
                mainCategoriesList.add(allCategoriesList.get(i));
            }
        }


        // Initialize the CategoryListAdapter for RecyclerView
        categoryListAdapter = new CategoryListAdapter_2(getContext(), mainCategoriesList, false);

        // Set the Adapter and LayoutManager to the RecyclerView
        category_recycler.setAdapter(categoryListAdapter);
        category_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryListAdapter.notifyDataSetChanged();


        return rootView;

    }

}

