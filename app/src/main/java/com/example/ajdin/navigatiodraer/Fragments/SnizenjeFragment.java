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
import com.example.ajdin.navigatiodraer.adapters.MenuAdapter;
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
    private List<Product> filteredKategory;
    private List<Product> filteredAll;
    private List<String> lables;
    private int odabraniSort;
    private int odabranaKategorija;
    private String textGeteR;
    private Spinner spin;
    private Spinner spin2;

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
        spin = (Spinner)view.findViewById(R.id.simpleSpinnerSnizeno);
        spin2 = (Spinner)view.findViewById(R.id.kategorySpinnerSnizeno);
        spin.setOnItemSelectedListener(this);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (productList == null) {

                } else {


                    odabranaKategorija = i;
                    if (i == 0) {
                        if (filteredAll==null) {
                            filteredAll = new ArrayList<Product>(productList);
                        }
                        else {
                            filteredAll = new ArrayList<Product>(productList);

                        }


                    } else {

                        filteredKategory = new ArrayList<>(db.getProductsKategoryFiltered(lables.get(i), productList));

                        if (textGeteR != null) {
                            for (Product p : filteredKategory) {
                                if (!p.getNaziv().toLowerCase().contains(textGeteR)) {
                                    filteredKategory.remove(p);
                                }
                            }

                        }

                        filteredAll=new ArrayList<>(filteredKategory);
                    }

                    adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, filteredAll);
                    lvArtikli.setAdapter(adapter);
                    if (state != null) {
                        Log.d(TAG, "trying to restore listview state..");
                        lvArtikli.onRestoreInstanceState(state);
                    }
                    final List<Product> finalUsedList = filteredAll;
                    lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Product movieModel = finalUsedList.get(position); // getting the model
                            DetailFragment fragment = new DetailFragment();
                            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            ft.hide(SnizenjeFragment.this);
                            bundle.putSerializable("movieModel", movieModel);
                            fragment.setArguments(bundle);

                            ft.add(R.id.content_main, fragment,"detail_fragment");
//                editsearch.setQuery("", false);
                            ft.addToBackStack("detail_fragment");
                            ft.commit();

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //do ovdje ubaceno

        db=new DatabaseHelper(getContext());
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        loadSpinnerData();
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
        if (productList == null) {

        } else {


            if (filteredAll==null)
                filteredAll = new ArrayList<Product>(productList);


            odabraniSort = i;
            if (i == 0) {

                Collections.sort(filteredAll, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());

                    }


                });
            }
            if (i == 1) {
                Collections.sort(filteredAll, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                    }


                });
            } else if (i == 2) {
                Collections.sort(filteredAll, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return t1.getNaziv().compareToIgnoreCase(product.getNaziv());
                    }


                });
            } else if (i == 3) {
                Collections.sort(filteredAll, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return p2.getPrice().compareTo(p1.getPrice());
                    }


                });

            } else {

                Collections.sort(filteredAll, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return p1.getPrice().compareTo(p2.getPrice());
                    }


                });

            }





            adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, filteredAll);
            lvArtikli.setAdapter(adapter);
            if (state != null) {
                Log.d(TAG, "trying to restore listview state..");
                lvArtikli.onRestoreInstanceState(state);
            }
            final List<Product> finalUsedList = filteredAll;
            lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Product movieModel = finalUsedList.get(position); // getting the model
                    DetailFragment fragment = new DetailFragment();
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    ft.hide(SnizenjeFragment.this);
                    ArrayList<String> slike=new ArrayList<>();
                    slike.add(movieModel.getImageDevice());
                    slike.add(movieModel.getImageDevice());
                    bundle.putStringArrayList("listaSlike",slike);
                    bundle.putSerializable("movieModel", movieModel);
                    fragment.setArguments(bundle);
                    ft.add(R.id.content_main, fragment,"detail_fragment");
//                editsearch.setQuery("", false);
                    ft.addToBackStack("detail_fragment");
                    ft.commit();

                }
            });
        }


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

        textGeteR = newText;
        filteredValues = new ArrayList<Product>(productList);
        for (Product value : productList) {
            if (value.getNaziv().toLowerCase().contains(newText.toLowerCase())) {
                if (odabranaKategorija > 0) {
                    if (!value.getKategorija().equals(lables.get(odabranaKategorija))) {
                        filteredValues.remove(value);
                    }
                }
            }
            else {
                filteredValues.remove(value);
            }
        }

//            if (odabranaKategorija > 0) {
//                for (Product p : filteredValues) {
//                    if (!p.getKategorija().equals(lables.get(odabranaKategorija))) {
//                        filteredValues.remove(p);
//                    }
//                }
//            }
        filteredAll = new ArrayList<>(filteredValues);



        adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, filteredAll);

        lvArtikli.setAdapter(adapter);
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product movieModel = filteredAll.get(position); // getting the model
                DetailFragment fragment=new DetailFragment();
                android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                ft.hide(SnizenjeFragment.this);
                Bundle bundle=new Bundle();
                ArrayList<String> slike=new ArrayList<>();
                slike.add(movieModel.getImageDevice());
                slike.add(movieModel.getImageDevice());
                bundle.putStringArrayList("listaSlike",slike);
                bundle.putSerializable("movieModel",movieModel);
                fragment.setArguments(bundle);
                ft.addToBackStack("detail_fragment");
                editsearch.setQuery("", true);
                ft.add(R.id.content_main,fragment,"detail_fragment");
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
                        ArrayList<String> slike=new ArrayList<>();
                        slike.add(movieModel.getImageDevice());
                        slike.add(movieModel.getImageDevice());
                        bundle.putStringArrayList("listaSlike",slike);
                        bundle.putSerializable("movieModel",movieModel);
                        fragment.setArguments(bundle);
                        ft.hide(SnizenjeFragment.this);
                        ft.add(R.id.content_main,fragment,"detail_fragment");
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
        if (filteredKategory!=null)
            filteredAll = new ArrayList<Product>(filteredKategory);
        else{
            filteredAll=new ArrayList<>(productList);
        }
        textGeteR=null;
        adapter = new SnizenoAdapter(getContext().getApplicationContext(), R.layout.row_snizeno, filteredAll);
        lvArtikli.setAdapter(adapter);
        spin.setSelection(0);
    }
    private void loadSpinnerData() {
        // database handler

        // Spinner Drop down elements
        lables = db.getAllLabels();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spin2.setAdapter(dataAdapter);
    }
}