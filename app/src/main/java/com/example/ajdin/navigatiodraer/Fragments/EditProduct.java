package com.example.ajdin.navigatiodraer.Fragments;


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
import com.example.ajdin.navigatiodraer.adapters.ViewPagerAdapter;
import com.example.ajdin.navigatiodraer.adapters.WrapContentHeightViewPager;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.models.Artikli;

import com.example.ajdin.navigatiodraer.models.Slike;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProduct extends Fragment {

    private ImageView ivMovieIcon;
    private TextView tvMovie;
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
        View view = inflater.inflate(R.layout.edit_product, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        list = (ListView)getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        ivMovieIcon = (ImageView)view.findViewById(R.id.ivIcon);
        tvMovie = (TextView)view.findViewById(R.id.tvNazivEdited);
        tvTagline = (TextView)view.findViewById(R.id.tvJedinicaMjereEdited);
        tvYear = (TextView)view.findViewById(R.id.tvCijenaEdited);
        new_price=(EditText)view.findViewById(R.id.new_priceEdited);
        Kolicina=(EditText)view.findViewById(R.id.quantityEdited);
        Kolicina.clearFocus();
        bOrder = (Button)view. findViewById(R.id.bOrderEdited);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarEdited);
        getActivity().setTitle("Detalji proizvoda");




        Bundle bundle = getArguments();
        if(bundle != null){
            movieModel = (Artikli) bundle.getSerializable("productEdit");
            kol = bundle.getString("kolEdit");
            ArrayList<String> slike=new ArrayList<>();
            for (Slike s:movieModel.getSlike()) {
                slike.add(s.getId());

            }
            viewPager = (WrapContentHeightViewPager) view.findViewById(R.id.viewPager2Edited);
            WrapContentHeightViewPager adapter = new WrapContentHeightViewPager(this.getActivity());
            ViewPagerAdapter adapter1=new ViewPagerAdapter(this.getActivity(),slike);
            viewPager.setAdapter(adapter1);

//            // Then later, when you want to display image
//            File file = new File(movieModel.getImageDevice());
//            ivMovieIcon.setImageURI(Uri.parse(file.getAbsolutePath()));
            progressBar.setVisibility(View.GONE);
            tvMovie.setText(movieModel.getNaziv());
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
                        CartFragment fragment=new CartFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("editFragment"))
                                .add(R.id.content_main,fragment,"cart_frag").addToBackStack("cart_frag").commit();
//cijena ""
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
                        CartFragment fragment=new CartFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("editFragment"))
                               .add(R.id.content_main,fragment,"cart_frag").addToBackStack("cart_frag").commit();


                        // ovdje ne moze ici Main
//                        MenuFragment fragment = new MenuFragment();
//                        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                        getActivity().getSupportFragmentManager().popBackStack();
//                        ft.replace(R.id.content_main, fragment);
//                        ft.commit();




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