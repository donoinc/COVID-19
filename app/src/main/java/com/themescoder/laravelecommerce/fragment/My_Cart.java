package com.themescoder.laravelecommerce.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.themescoder.laravelecommerce.R;
import com.themescoder.laravelecommerce.activities.Login;
import com.themescoder.laravelecommerce.activities.MainActivity;
import com.themescoder.laravelecommerce.adapters.CartItemsAdapter;
import com.themescoder.laravelecommerce.constant.ConstantValues;
import com.themescoder.laravelecommerce.databases.User_Cart_DB;
import com.themescoder.laravelecommerce.models.cart_model.CartProduct;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;


public class My_Cart extends Fragment {

    public TextView cart_total_price;

    RecyclerView cart_items_recycler;
    LinearLayout cart_view, cart_view_empty;
    Button cart_checkout_btn, continue_shopping_btn;

    CartItemsAdapter cartItemsAdapter;
    User_Cart_DB user_cart_db = new User_Cart_DB();

    List<CartProduct> cartItemsList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_cart, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        setHasOptionsMenu(true);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionCart));

        // Get the List of Cart Items from the Local Databases User_Cart_DB
        cartItemsList = user_cart_db.getCartItems();


        // Binding Layout Views
        cart_view = (LinearLayout) rootView.findViewById(R.id.cart_view);
        cart_total_price = (TextView) rootView.findViewById(R.id.cart_total_price);
        cart_checkout_btn = (Button) rootView.findViewById(R.id.cart_checkout_btn);
        cart_items_recycler = (RecyclerView) rootView.findViewById(R.id.cart_items_recycler);
        cart_view_empty = (LinearLayout) rootView.findViewById(R.id.cart_view_empty);
        continue_shopping_btn = (Button) rootView.findViewById(R.id.continue_shopping_btn);


        // Change the Visibility of cart_view and cart_view_empty LinearLayout based on CartItemsList's Size
        if (cartItemsList.size() != 0) {
            cart_view.setVisibility(View.VISIBLE);
            cart_view_empty.setVisibility(View.GONE);
        } else {
            cart_view.setVisibility(View.GONE);
            cart_view_empty.setVisibility(View.VISIBLE);
        }



        // Initialize the AddressListAdapter for RecyclerView
        cartItemsAdapter = new CartItemsAdapter(getContext(), cartItemsList, My_Cart.this);

        // Set the Adapter and LayoutManager to the RecyclerView
        cart_items_recycler.setAdapter(cartItemsAdapter);
        cart_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));


        // Show the Cart's Total Price with the help of static method of CartItemsAdapter
        cartItemsAdapter.setCartTotal();

        
        cartItemsAdapter.notifyDataSetChanged();



        // Handle Click event of continue_shopping_btn Button
        continue_shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSubFragment", false);
    
                // Navigate to Products Fragment
                Fragment fragment = new Products();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionCart)).commit();

            }
        });
        
        // Handle Click event of cart_checkout_btn Button
        cart_checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConstantValues.MAINTENANCE_MODE != null) {
                    if (ConstantValues.MAINTENANCE_MODE.equalsIgnoreCase("Maintenance"))
                        showDialog(ConstantValues.MAINTENANCE_TEXT);
                    else {
                        // Check if cartItemsList isn't empty
                        if (cartItemsList.size() != 0) {

                            // Check if User is Logged-In
                            if (ConstantValues.IS_USER_LOGGED_IN) {

                                // Navigate to Shipping_Address Fragment
                                Fragment fragment = new Shipping_Address();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                                        .addToBackStack(getString(R.string.actionCart)).commit();

                            } else {
                                // Navigate to Login Activity
                                Intent i = new Intent(getContext(), Login.class);
                                getContext().startActivity(i);
                                ((MainActivity) getContext()).finish();
                                ((MainActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                            }
                        }

                    }
                }
            }
        });


        return rootView;
    }



    //*********** Change the Layout View of My_Cart Fragment based on Cart Items ********//

    public void updateCartView(int cartListSize) {

        // Check if Cart has some Items
        if (cartListSize != 0) {
            cart_view.setVisibility(View.VISIBLE);
            cart_view_empty.setVisibility(View.GONE);
        } else {
            cart_view.setVisibility(View.GONE);
            cart_view_empty.setVisibility(View.VISIBLE);
        }
    }
    
    
    //*********** Used to Share the App with Others ********//
    
    private void showDialog(String str) {
        
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.dialog_maintenance, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        
        final TextView dialog_message = (TextView) dialogView.findViewById(R.id.maintenanceText);
        final Button dialog_button_positive = (Button) dialogView.findViewById(R.id.dialog_button);
      
        dialog_message.setText(str);
       
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        
        
        dialog_button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               alertDialog.dismiss();
            }
        });
    }


    //*********** Static method to Add the given Item to User's Cart ********//

    public static void AddCartItem(CartProduct cartProduct) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.addCartItem
                (
                        cartProduct
                );
    }



    //*********** Static method to Get the Cart Product based on product_id ********//

    public static CartProduct GetCartProduct(int product_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();

        CartProduct cartProduct = user_cart_db.getCartProduct
                (
                        product_id
                );

        return cartProduct;
    }



    //*********** Static method to Update the given Item in User's Cart ********//

    public static void UpdateCartItem(CartProduct cartProduct) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.updateCartItem
                (
                        cartProduct
                );
    }



    //*********** Static method to Delete the given Item from User's Cart ********//

    public static void DeleteCartItem(int cart_item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.deleteCartItem
                (
                        cart_item_id
                );
    }



    //*********** Static method to Clear User's Cart ********//

    public static void ClearCart() {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        user_cart_db.clearCart();
    }



    //*********** Static method to get total number of Items in User's Cart ********//

    public static int getCartSize() {
        int cartSize = 0;
        
        User_Cart_DB user_cart_db = new User_Cart_DB();
        List<CartProduct> cartItems = user_cart_db.getCartItems();
        
        for (int i=0;  i<cartItems.size();  i++) {
            cartSize += cartItems.get(i).getCustomersBasketProduct().getCustomersBasketQuantity();
        }
        
        return cartSize;
    }


    //*********** Static method to check if the given Product is already in User's Cart ********//

    public static boolean checkCartHasProduct(int cart_item_id) {
        User_Cart_DB user_cart_db = new User_Cart_DB();
        return user_cart_db.getCartItemsIDs().contains(cart_item_id);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        cartItem.setVisible(false);
        searchItem.setVisible(true);
    }
    
}

