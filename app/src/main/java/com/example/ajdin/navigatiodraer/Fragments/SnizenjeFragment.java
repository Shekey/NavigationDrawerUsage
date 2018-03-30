package com.example.ajdin.navigatiodraer.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapter;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapterDatabase;
import com.example.ajdin.navigatiodraer.adapters.SnizenoAdapter;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.models.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnizenjeFragment extends Fragment implements SearchView.OnQueryTextListener,AdapterView.OnItemSelectedListener {
    public ListView lvArtikli;
    private DatabaseHelper db;
    private ProgressDialog dialog;
    private SearchView editsearch;
    private ArrayList<Product> movieModelList;
    private SnizenoAdapter adapter;
    private List<Product> productList;
    private Parcelable state;
    String[] bankNames={"Abecedno","Obrnuto abecedno","Cijeni opadajuci","Cijeni rastuci"};
    private List<Product> filteredValues;

    @Override
    public void onResume() {
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        getActivity().setTitle("Snizeni proizvodi");
        super.onResume();
    }

    @Override
    public void onPause() {
        state = lvArtikli.onSaveInstanceState();
        super.onPause();
    }


    public SnizenjeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_snizenje, container, false);
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("ime","").isEmpty()) {
            fab.setVisibility(View.VISIBLE);
        }
        else {
            fab.setVisibility(View.INVISIBLE);
        }
        getActivity().setTitle("Snizeni proizvodi");
        fab.setImageResource(R.drawable.dodaj_osobu);
        lvArtikli=view.findViewById(R.id.lvArtikliSnizeno);
        lvArtikli.setEmptyView(view.findViewById(R.id.emptyElementSn));
        Spinner spin = (Spinner)view.findViewById(R.id.simpleSpinnerSnizeno);
        spin.setOnItemSelectedListener(this);
        db=new DatabaseHelper(getContext());
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        editsearch = (SearchView)view.findViewById(R.id.simpleSearchViewSnizeno);
        editsearch.setOnQueryTextListener(this);
        if(!db.isEmpty()){
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Loading. Please wait...");
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext().getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();
            ImageLoader.getInstance().init(config); // Do it on Application start

            lvArtikli = (ListView)view.findViewById(R.id.lvArtikliSnizeno);

            new JSONTaskDatabase().execute();
        }



        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        final List<Product> usedList;
        if (filteredValues==null){
            usedList=new ArrayList<Product>(productList);
        }
        else {
            usedList=new ArrayList<Product>(filteredValues);

        }
        if (i==0){
            Collections.sort(usedList, new Comparator<Product>()
            {
                @Override
                public int compare(Product product, Product t1) {
                    return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                }


            });
        }
        else if(i==1) {
            Collections.sort(usedList, new Comparator<Product>()
            {
                @Override
                public int compare(Product product, Product t1) {
                    return t1.getNaziv().compareToIgnoreCase(product.getNaziv());
                }


            });
        }
        else if(i==2){
            Collections.sort(usedList, new Comparator<Product>()
            {
                @Override
                public int compare(Product p1, Product p2)
                {
                    return p2.getPrice().compareTo(p1.getPrice());
                }



            });

        }
        else {

            Collections.sort(usedList, new Comparator<Product>()
            {
                @Override
                public int compare(Product p1, Product p2)
                {
                    return p1.getPrice().compareTo(p2.getPrice());
                }


            });

        }
        adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, usedList);
        lvArtikli.setAdapter(adapter);
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product movieModel = usedList.get(position); // getting the model
                DetailFragment fragment=new DetailFragment();
                android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("movieModel",movieModel);
                fragment.setArguments(bundle);
                ft.replace(R.id.content_main,fragment);

                ft.addToBackStack("detail_fragment");
                ft.commit();

            }
        });


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        filteredValues = new ArrayList<Product>(productList);
        for (Product value : productList) {
            if (!value.getNaziv().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, filteredValues);

        lvArtikli.setAdapter(adapter);
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product movieModel = filteredValues.get(position); // getting the model
                DetailFragment fragment=new DetailFragment();
                android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("movieModel",movieModel);
                fragment.setArguments(bundle);
                ft.addToBackStack("detail_fragment");
                ft.replace(R.id.content_main,fragment);
                ft.commit();

            }
        });
        return false;
    }
    public class JSONTaskDatabase extends AsyncTask<Void,String, List<Product> > {

        private List<Product> usedList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Product> doInBackground(Void... params) {

            productList = db.getAllSnizeno();
            return productList;

        }

        @Override
        protected void onPostExecute(final List<Product> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                Collections.sort(result, new Comparator<Product>()
                {
                    @Override
                    public int compare(Product product, Product t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                    }


                });

                lvArtikli.setAdapter(adapter);
                if(state != null) {
                    Log.d(TAG, "trying to restore listview state..");
                    lvArtikli.onRestoreInstanceState(state);
                }
                lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Product movieModel = result.get(position); // getting the model
                        DetailFragment fragment=new DetailFragment();
                        android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("movieModel",movieModel);
                        fragment.setArguments(bundle);
                        ft.replace(R.id.content_main,fragment);
                        ft.addToBackStack("detail_fragment");
                        ft.commit();

                    }
                });
            } else {
                Toast.makeText(getContext().getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void resetSearch() {
        adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, productList);
        filteredValues=null;
        lvArtikli.setAdapter(adapter);
    }
}