package com.example.ajdin.navigatiodraer.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.ViewPagerAdapter;
import com.example.ajdin.navigatiodraer.adapters.WrapContentHeightViewPager;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.CartItemAdapter;
import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.example.ajdin.navigatiodraer.models.Artikli;

import com.example.ajdin.navigatiodraer.models.Slike;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.ajdin.navigatiodraer.Fragments.DetailFragment.hideSoftKeyboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProduct extends Fragment {

    private ImageView ivMovieIcon;
    private TextView tvMovie;
    private TextView tvStanje;
    private TextView tvTagline;
    private TextView tvYear;
    EditText new_price;
    EditText Kolicina;
    private ProgressBar progressBar;
    private Button bOrder;
    private Artikli movieModel;
    private ListView list;
    private Parcelable state;
    private WrapContentHeightViewPager viewPager;
    private String kol;
    private CartItemAdapter cartItemAdapter;
    Cart cart;

    public EditProduct() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        state = list.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (state!=null){
            list.onRestoreInstanceState(state);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.edit_product, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        list = getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        ivMovieIcon = view.findViewById(R.id.ivIcon);
        tvMovie = view.findViewById(R.id.tvNazivEdited);
        tvStanje = view.findViewById(R.id.tvStanjeDetaljiEdited);
        tvTagline = view.findViewById(R.id.tvJedinicaMjereEdited);
        tvYear = view.findViewById(R.id.tvCijenaEdited);
        new_price= view.findViewById(R.id.new_priceEdited);
        Kolicina= view.findViewById(R.id.quantityEdited);
        Kolicina.clearFocus();
        bOrder = view. findViewById(R.id.bOrderEdited);
        progressBar = view.findViewById(R.id.progressBarEdited);
        getActivity().setTitle("Detalji proizvoda");
        cart = CartHelper.getCart();
        cartItemAdapter = new CartItemAdapter(getActivity());



        Bundle bundle = getArguments();
        if(bundle != null){
            movieModel = (Artikli) bundle.getSerializable("productEdit");
            kol = bundle.getString("kolEdit");
            ArrayList<String> slike=new ArrayList<>();
            for (Slike s:movieModel.getSlike()) {
                slike.add(s.getId());

            }
            viewPager = view.findViewById(R.id.viewPager2Edited);
            WrapContentHeightViewPager adapter = new WrapContentHeightViewPager(this.getActivity());
            ViewPagerAdapter adapter1=new ViewPagerAdapter(this.getActivity(),slike);
            viewPager.setAdapter(adapter1);

            progressBar.setVisibility(View.GONE);
            tvMovie.setText(movieModel.getNaziv());
            tvStanje.setText(movieModel.getStanje());
            tvTagline.setText(movieModel.getKategorija());
            tvYear.setText("Cijena: " + movieModel.getCijena()+ " KM");
            Kolicina.setText(kol);


        }
        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = CartHelper.getCart();
                // Log.d(TAG, "Adding product: " + product.getName());
                if (Kolicina.getText().toString().matches("^[0-9]\\d*(\\.\\d+)?$")) {//unesena kolicina
                    if (new_price.getText().toString().trim().matches("")) {
                        cart.remove(movieModel);
                        cart.add(movieModel, Double.valueOf(Kolicina.getText().toString()), "");
                        hideSoftKeyboard(view);
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.popBackStack();
                        CartFragment fragment=new CartFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment,"cart_frag").addToBackStack("cart_frag").commit();
                        getActivity().setTitle("Korpa");


                    }

                    else {
                        if (!new_price.getText().toString().matches("^[0-9]\\d*(\\.\\d+)?$")) {
                            new_price.setText("");
                            Toast.makeText(getActivity(), "Niste unijeli dobar format cijene,unosi se sa '.' ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        cart.remove(movieModel);
                        movieModel.setCijena(new_price.getText().toString());
                        cart.add(movieModel, Double.valueOf(Kolicina.getText().toString()), new_price.getText().toString());
                        BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(new_price.getText().toString()));
                        hideSoftKeyboard(view);
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.popBackStack();
                        manager.popBackStack();
                        CartFragment fragment=new CartFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment,"cart_frag").addToBackStack("cart_frag").commit();
                        getActivity().setTitle("Korpa");

                    }
                } else {
                    Kolicina.setText("");
                    Toast.makeText(getActivity(), " Unesite kolicinu", Toast.LENGTH_SHORT).show();
                    Kolicina.requestFocus();
                    return;



                }

            }
        });

        return view;

    }
    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItems = new ArrayList<CartItem>();

        LinkedHashMap<Saleable, Double> itemMap = cart.getItemWithQuantity();

        for (Map.Entry<Saleable, Double> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Artikli) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }

        return cartItems;
    }
}