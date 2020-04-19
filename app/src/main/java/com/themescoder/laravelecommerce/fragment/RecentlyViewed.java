package com.themescoder.laravelecommerce.fragment;


import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.themescoder.laravelecommerce.R;

import java.util.ArrayList;
import java.util.List;

import com.themescoder.laravelecommerce.adapters.ProductAdapterRemovable;
import com.themescoder.laravelecommerce.constant.ConstantValues;
import com.themescoder.laravelecommerce.databases.User_Recents_DB;
import com.themescoder.laravelecommerce.models.product_model.GetAllProducts;
import com.themescoder.laravelecommerce.models.product_model.ProductData;
import com.themescoder.laravelecommerce.models.product_model.ProductDetails;
import com.themescoder.laravelecommerce.network.APIClient;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class RecentlyViewed extends Fragment {

    String customerID;

    TextView emptyRecord, headerText;
    RecyclerView recents_recycler;

    ProductAdapterRemovable productAdapter;
    User_Recents_DB recents_db = new User_Recents_DB();

    ArrayList<Integer> recents;
    List<ProductDetails> recentViewedList;
    ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_products_horizontal, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
       // noInternetDialog.show();

        recents = new ArrayList<>();
        recentViewedList  = new ArrayList<>();

        // Get the List of RecentlyViewed Product's IDs from the Local Databases User_Recents_DB
        recents = recents_db.getUserRecents();

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.products_horizontal_header);
        recents_recycler = (RecyclerView) rootView.findViewById(R.id.products_horizontal_recycler);
        progressBar = rootView.findViewById(R.id.progressBar);


        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);

        // Set text of Header
        headerText.setText(getString(R.string.recentlyViewed));
    
    
        // Initialize the ProductAdapterRemovable and LayoutManager for RecyclerView
        productAdapter = new ProductAdapterRemovable(getContext(), recentViewedList, true, true, headerText);
        recents_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set the Adapter and LayoutManager to the RecyclerView
        recents_recycler.setAdapter(productAdapter);
        

        // Check if the recents List isn't empty
        if (recents.size() < 1 ) {
            headerText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        else {
            headerText.setVisibility(View.VISIBLE);
            for (int i=0;  i<recents.size();  i++) {
                // Request current Product's Details
                RequestProductDetails(recents.get(i));
            }
        }


        return rootView;
    }



    //*********** Adds Products returned from the Server to the RecentViewedList ********//

    private void addRecentProducts(ProductData productData) {

        // Add Products to recentViewedList
        if (productData.getProductData().size() > 0 ) {
            recentViewedList.add(productData.getProductData().get(0));
        }

        // Notify the Adapter
        productAdapter.notifyDataSetChanged();
    }



    //*********** Request the Product's Details from the Server based on Product's ID ********//

    public void RequestProductDetails(final int products_id) {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setProductsId(String.valueOf(products_id));
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);


        Call<ProductData> call = APIClient.getInstance()
                .getAllProducts
                        (
                                getAllProducts
                        );

        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {
                
                if (response.isSuccessful()) {
                    
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Product's Details has been returned.
                        // Add Product to the recentViewedList
                        addRecentProducts(response.body());

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Product's Details haven't been returned.
                        // Call the method to process some implementations
                        addRecentProducts(response.body());

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                Toast.makeText(getActivity(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        
    }

}

