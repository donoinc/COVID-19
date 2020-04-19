package com.themescoder.laravelecommerce.fragment;


import androidx.annotation.Nullable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.themescoder.laravelecommerce.activities.MainActivity;
import com.themescoder.laravelecommerce.R;

import java.util.ArrayList;
import java.util.List;

import com.themescoder.laravelecommerce.adapters.OrdersListAdapter;
import com.themescoder.laravelecommerce.constant.ConstantValues;
import com.themescoder.laravelecommerce.customs.DialogLoader;
import com.themescoder.laravelecommerce.models.order_model.OrderData;
import com.themescoder.laravelecommerce.models.order_model.OrderDetails;
import com.themescoder.laravelecommerce.network.APIClient;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class My_Orders extends Fragment {

    View rootView;
    String customerID;
    
   // AdView mAdView;
    TextView emptyRecord;
    FrameLayout banner_adView;
    RecyclerView orders_recycler;

    DialogLoader dialogLoader;
    OrdersListAdapter ordersListAdapter;

    List<OrderDetails> ordersList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_orders, container, false);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionOrders));

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        
        Log.d("customerID",customerID);
        
        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record);
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        orders_recycler = (RecyclerView) rootView.findViewById(R.id.orders_recycler);
    
    
      /*  if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(getContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
        }
        */
        

        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);


        dialogLoader = new DialogLoader(getContext());


        // Request for User's Orders
        RequestMyOrders();


        return rootView;
    }



    //*********** Adds Orders returned from the Server to the OrdersList ********//

    private void addOrders(OrderData orderData) {

        // Add Orders to ordersList from the List of OrderData
        ordersList = orderData.getData();


        // Initialize the OrdersListAdapter for RecyclerView
        ordersListAdapter = new OrdersListAdapter(getContext(), customerID, ordersList,this);
        // Set the Adapter and LayoutManager to the RecyclerView
        orders_recycler.setAdapter(ordersListAdapter);
        orders_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        
        ordersListAdapter.notifyDataSetChanged();
    }


    //*********** Request User's Orders from the Server ********//

    public void RequestMyOrders() {

        dialogLoader.showProgressDialog();

        Call<OrderData> call = APIClient.getInstance()
                .getOrders
                        (
                                customerID,
                                ConstantValues.LANGUAGE_ID,
                                ConstantValues.CURRENCY_CODE
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {

                String str = new Gson().toJson(response.body());

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        // Orders have been returned. Add Orders to the ordersList
                        addOrders(response.body());
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });
    }
    
    
    //*********** Request User's cancel order ********//
    
    public void RequestMyOrdersCancel(int orderID) {
        
        dialogLoader.showProgressDialog();
        
        Call<OrderData> call = APIClient.getInstance()
                .updatestatus
                        (
                                customerID,
                                orderID
                        );
        
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {
                
                dialogLoader.hideProgressDialog();
                
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        // Orders status has been changed. Add Orders again to the ordersList
                        RequestMyOrders();
                        
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        
                    }
                    else {
                        // Unable to get Success status
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    
   
}

