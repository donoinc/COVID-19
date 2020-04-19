package com.themescoder.laravelecommerce.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themescoder.laravelecommerce.activities.MainActivity;
import com.themescoder.laravelecommerce.R;

import java.util.ArrayList;
import java.util.List;

import com.themescoder.laravelecommerce.adapters.CategoryListAdapter_5;
import com.themescoder.laravelecommerce.app.App;
import com.themescoder.laravelecommerce.models.category_model.CategoryDetails;

import am.appwise.components.ni.NoInternetDialog;


public class SubCategories_5 extends Fragment {

    int parentCategoryID;
    Boolean isHeaderVisible;

    TextView emptyText, headerText;
    RecyclerView category_recycler;

    CategoryListAdapter_5 categoryListAdapter;

    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> subCategoriesList;
    CardView header_card;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories, container, false);
    
        // Get CategoryID from Bundle arguments
        parentCategoryID = getArguments().getInt("CategoryID");
    
        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString("CategoryName", getString(R.string.actionCategory)));

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

    
        
        // Get CategoriesList from ApplicationContext
        allCategoriesList = new ArrayList<>();
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();


        // Binding Layout Views
        emptyText = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.categories_header);
        header_card = rootView.findViewById(R.id.header_card);
        category_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);


        // Hide some of the Views
        header_card.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);


        subCategoriesList = new ArrayList<>();

        for (int i=0;  i<allCategoriesList.size();  i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase(String.valueOf(parentCategoryID))) {
                subCategoriesList.add(allCategoriesList.get(i));
            }
        }
    
    
        // Initialize the CategoryListAdapter and LayoutManager for RecyclerView
        categoryListAdapter = new CategoryListAdapter_5(getContext(), subCategoriesList, true);
        category_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    
        // Set the Adapter to the RecyclerView
        category_recycler.setAdapter(categoryListAdapter);

        
        categoryListAdapter.notifyDataSetChanged();


        return rootView;
    }

}

