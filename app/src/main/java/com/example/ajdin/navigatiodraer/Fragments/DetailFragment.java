package com.example.ajdin.navigatiodraer.Fragments;


import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.models.Product;
import com.google.gson.Gson;

import java.io.File;
import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private ImageView ivMovieIcon;
    private TextView tvMovie;
    private TextView tvTagline;
    private TextView tvYear;
    EditText new_price;
    EditText Kolicina;
    private ProgressBar progressBar;
    private Button bOrder;
    private Product movieModel;
    private ListView list;
    private Parcelable state;

    public DetailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        list = (ListView)getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        ivMovieIcon = (ImageView)view.findViewById(R.id.ivIcon);
        tvMovie = (TextView)view.findViewById(R.id.tvNaziv);
        tvTagline = (TextView)view.findViewById(R.id.tvJedinicaMjere);
        tvYear = (TextView)view.findViewById(R.id.tvCijena);
        new_price=(EditText)view.findViewById(R.id.new_price);
        Kolicina=(EditText)view.findViewById(R.id.quantity);
        Kolicina.clearFocus();
        bOrder = (Button)view. findViewById(R.id.bOrder);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        Bundle bundle = getArguments();
        if(bundle != null){
            movieModel = (Product) bundle.getSerializable("movieModel");

            // Then later, when you want to display image
            File file = new File(movieModel.getImageDevice());
            ivMovieIcon.setImageURI(Uri.parse(file.getAbsolutePath()));
            progressBar.setVisibility(View.GONE);

            tvMovie.setText(movieModel.getNaziv());
            tvTagline.setText(movieModel.getBarkod());
            tvYear.setText("Cijena: " + movieModel.getCijena());


        }
        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = CartHelper.getCart();
                // Log.d(TAG, "Adding product: " + product.getName());
                if (Kolicina.getText().toString().matches("^[0-9]\\d*(\\.\\d+)?$")) {//unesena kolicina
                    if (new_price.getText().toString().trim().matches("")) { //nema cijene
                        cart.add(movieModel, Double.valueOf(Kolicina.getText().toString()) , "");//cijena ""
                        MenuFragment fragment = new MenuFragment();
                        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_main, fragment);
                        ft.commit();

                    } else {
                        if (!new_price.getText().toString().matches("^[0-9]\\d*(\\.\\d+)?$")) {
                            new_price.setText("");
                            Toast.makeText(getActivity(), "Niste unijeli dobar format cijene,unosi se sa '.' ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        movieModel.setCijena(new_price.getText().toString());
                        cart.add(movieModel, Double.valueOf(Kolicina.getText().toString()), new_price.getText().toString());
                        BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(new_price.getText().toString()));


                        // ovdje ne moze ici Main
                        MenuFragment fragment = new MenuFragment();
                        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        getActivity().getSupportFragmentManager().popBackStack();
                        ft.replace(R.id.content_main, fragment);
                        ft.commit();

                        return;
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

}
