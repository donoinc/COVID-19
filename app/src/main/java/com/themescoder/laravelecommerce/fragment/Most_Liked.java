package com.themescoder.laravelecommerce.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
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

import com.themescoder.laravelecommerce.adapters.ProductAdapter;
import com.themescoder.laravelecommerce.app.App;
import com.themescoder.laravelecommerce.constant.ConstantValues;
import com.themescoder.laravelecommerce.models.product_model.GetAllProducts;
import com.themescoder.laravelecommerce.models.product_model.ProductData;
import com.themescoder.laravelecommerce.models.product_model.ProductDetails;
import com.themescoder.laravelecommerce.network.APIClient;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class Most_Liked extends Fragment {

    String customerID;
    Boolean isHeaderVisible;
    Call<ProductData> networkCall;

    TextView emptyRecord, headerText;
    RecyclerView most_liked_recycler;

    ProductAdapter productAdapter;

    List<ProductDetails> mostLikedProductList;

    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_products_horizontal, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
       // noInternetDialog.show();

        // Get the Boolean from Bundle Arguments
        isHeaderVisible = getArguments().getBoolean("isHeaderVisible");

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.products_horizontal_header);
        most_liked_recycler = (RecyclerView) rootView.findViewById(R.id.products_horizontal_recycler);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);
    
        // Check if Header must be Invisible or not
        if (!isHeaderVisible) {
            headerText.setVisibility(View.GONE);
        } else {
            headerText.setText(getString(R.string.most_liked));
        }
    
    
        mostLikedProductList = new ArrayList<>();
    
    
        // RecyclerView has fixed Size
        most_liked_recycler.setHasFixedSize(true);
        most_liked_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    
        // Initialize the ProductAdapter for RecyclerView
        productAdapter = new ProductAdapter(getContext(), mostLikedProductList, true,false);
    
        // Set the Adapter and LayoutManager to the RecyclerView
        most_liked_recycler.setAdapter(productAdapter);
    
    
        // Request for Most Sold Products
        RequestMostLikedProducts();


        return rootView;
    }



    //*********** Adds Products returned from the Server to the MostLikedProductList ********//

    private void addProducts(ProductData productData) {

        // Add Products to mostLikedProductList
        if (productData.getProductData().size() > 0){
        mostLikedProductList.addAll(productData.getProductData());
        } else {
            headerText.setVisibility(View.GONE);
        }
        productAdapter.notifyDataSetChanged();
    }



    //*********** Request all the Products from the Server based on Product's Total Likes ********//

    public void RequestMostLikedProducts() {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setType("most liked");
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);


        networkCall = APIClient.getInstance()
                .getAllProducts
                        (
                                getAllProducts
                        );

        networkCall.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {
                
                if (response.isSuccessful()) {
                    
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Products have been returned. Add Products to the mostLikedProductList
                        addProducts(response.body());
                        emptyRecord.setVisibility(View.GONE);

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Products haven't been returned
                        emptyRecord.setVisibility(View.VISIBLE);
                        if (isHeaderVisible){
                            emptyRecord.setVisibility(View.GONE);
                            headerText.setVisibility(View.GONE);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                if (!call.isCanceled()) {
                    Toast.makeText(App.getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }



    @Override
    public void onPause() {

        // Check if NetworkCall is being executed
        if (networkCall.isExecuted()){
            // Cancel the NetworkCall
            networkCall.cancel();
        }

        super.onPause();
    }

}