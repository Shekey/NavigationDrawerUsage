package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.CustomSwipeAdapter;
import com.example.ajdin.navigatiodraer.adapters.ViewPagerAdapter;
import com.example.ajdin.navigatiodraer.adapters.WrapContentHeightViewPager;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.OnSwipeTouchListener;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;
import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private ImageView ivMovieIcon;
    private TextView tvMovie;
    private TextView tvTagline;
    private TextView tvYear;
    private TextView tvStanje;
    EditText new_price;
    EditText Kolicina;
    private ProgressBar progressBar;
    private Button bOrder;
    private Artikli movieModel;
    private ArrayList<Artikli> movieModelLista;
    private int pozicija;
    private ListView list;
    private Parcelable state;
    private WrapContentHeightViewPager viewPager;
    private byte[] bytes;
    private String s2;
    private Artikli movieModelnext;

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
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        list = (ListView)getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        ivMovieIcon = (ImageView)view.findViewById(R.id.ivIcon);
        tvMovie = (TextView)view.findViewById(R.id.tvNaziv);
        tvTagline = (TextView)view.findViewById(R.id.tvJedinicaMjere);
        tvYear = (TextView)view.findViewById(R.id.tvCijena);
        tvStanje = (TextView)view.findViewById(R.id.tvStanjeDetalji);
        new_price=(EditText)view.findViewById(R.id.new_price);
        Kolicina=(EditText)view.findViewById(R.id.quantity);
        Kolicina.clearFocus();
        bOrder = (Button)view. findViewById(R.id.bOrder);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        getActivity().setTitle("Detalji proizvoda");
        String [] images;



        Bundle bundle = getArguments();
        if(bundle != null){
            movieModel = (Artikli) bundle.getSerializable("movieModel");
            movieModelLista = (ArrayList<Artikli>) bundle.getSerializable("lista");
            pozicija=bundle.getInt("pozicija");
            ArrayList<String> list21=bundle.getStringArrayList("listaSlike");
            viewPager = (WrapContentHeightViewPager) view.findViewById(R.id.viewPager2);
            WrapContentHeightViewPager adapter = new WrapContentHeightViewPager(this.getActivity());
            ViewPagerAdapter adapter1=new ViewPagerAdapter(this.getActivity(),list21);
            TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(viewPager, true);
            viewPager.setAdapter(adapter1);

//            // Then later, when you want to display image
//            File file = new File(movieModel.getImageDevice());
//            ivMovieIcon.setImageURI(Uri.parse(file.getAbsolutePath()));
            progressBar.setVisibility(View.GONE);
            //tvMovie.setText(movieModel.getNaziv());

            tvMovie.setText(movieModel.getNaziv());
            tvTagline.setText(movieModel.getKategorija());
            tvYear.setText("Cijena: " + movieModel.getCijena()+ " KM");
            tvStanje.setText("Na stanju: " +movieModel.getStanje());


        }
        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = CartHelper.getCart();
                // Log.d(TAG, "Adding product: " + product.getName());
                if (Kolicina.getText().toString().matches("^[0-9]\\d*(\\.[1-9])?$")) {
                    if (Double.valueOf(Kolicina.getText().toString()) > 0.0) {


                        if (new_price.getText().toString().trim().matches("")) {
                            cart.add(movieModel, Double.valueOf(Kolicina.getText().toString()), "");
                            hideSoftKeyboard(view);
                            FragmentTransaction ftt =getActivity().getSupportFragmentManager().beginTransaction();
                            ftt.remove(DetailFragment.this).commit();
                            getActivity().onBackPressed();
                            return;
//cijena ""
                        }
                        else {
                            if (!new_price.getText().toString().matches("^[0-9]\\d*(\\.[1-9])?$")) {
                                new_price.setText("");
                                Toast.makeText(getActivity(), "Unesite cijenu veću od 0.0 ", Toast.LENGTH_LONG).show();
                                return;
                            }


                        else if (Double.valueOf(new_price.getText().toString()) < 0) {
                                new_price.setText("");
                                Toast.makeText(getActivity(), "Unesite cijenu veću od 0 ", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else {
                                Artikli pr = new Artikli(movieModel.getNaziv(), movieModel.getBarkod(), movieModel.getId(), movieModel.getSnizeno(), movieModel.getStanje(), movieModel.getDatum(), movieModel.getKategorija(), movieModel.getJedinica(), movieModel.getSlike(), new_price.getText().toString());
                                cart.add(pr, Double.valueOf(Kolicina.getText().toString()), new_price.getText().toString());
                                BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(new_price.getText().toString()));
                                hideSoftKeyboard(view);
                                FragmentTransaction ftt =getActivity().getSupportFragmentManager().beginTransaction();
                                ftt.remove(DetailFragment.this).commit();
                                getActivity().onBackPressed();
                                return;
                            }

                        }

                            //  Product pr=new Product(movieModel.getName(),1,movieModel.getBarkod(),movieModel.getJM(),movieModel.getKategorija(),new_price.getText().toString(),movieModel.getImageDevice(),movieModel.getImageDevice(),movieModel.getSnizeno(),movieModel.getDatum_kreiranja());

                        }
                    else {
                        Kolicina.setText("");
                        Toast.makeText(getActivity(), " Unesite kolicinu vecu od 0", Toast.LENGTH_LONG).show();
                        Kolicina.requestFocus();
                        return;



                    }

            } else {
                    Kolicina.setText("");
                    Toast.makeText(getActivity(), " Unesite kolicinu", Toast.LENGTH_LONG).show();
                    Kolicina.requestFocus();
                    return;



                }







            }
        });
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                NextImage();

                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                PreviousImage();


                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return view;

    }
    public static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public void NextImage(){
        if (movieModelLista.size()>0){
            DetailFragment fragment=new DetailFragment();
            android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
            Bundle bundle=new Bundle();
            movieModelnext=movieModelLista.get(pozicija+1);
            ArrayList<String> slike=new ArrayList<>();
            for (Slike s:movieModelnext.getSlike()) {
                slike.add(s.getId());

            }
            ArrayList<Artikli> lista=new ArrayList<>(movieModelLista);
            bundle.putStringArrayList("listaSlike",slike);
            bundle.putSerializable("lista",lista);
            bundle.putInt("pozicija",pozicija+1);
            bundle.putSerializable("movieModel",movieModelnext);
            fragment.setArguments(bundle);
            ft.remove(DetailFragment.this).add(R.id.content_main,fragment,"detail_fragment");
            ft.commit();
        }
    }
    public void PreviousImage(){
        if (movieModelLista.size()>0){
            DetailFragment fragment=new DetailFragment();
            android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
            Bundle bundle=new Bundle();
            movieModelnext=movieModelLista.get(pozicija-1);
            ArrayList<String> slike=new ArrayList<>();
            for (Slike s:movieModelnext.getSlike()) {
                slike.add(s.getId());

            }
            ArrayList<Artikli> lista=new ArrayList<>(movieModelLista);
            bundle.putStringArrayList("listaSlike",slike);
            bundle.putSerializable("lista",lista);
            bundle.putInt("pozicija",pozicija-1);
            bundle.putSerializable("movieModel",movieModelnext);
            fragment.setArguments(bundle);
            ft.remove(DetailFragment.this).add(R.id.content_main,fragment,"detail_fragment");
            ft.commit();
        }
    }

}