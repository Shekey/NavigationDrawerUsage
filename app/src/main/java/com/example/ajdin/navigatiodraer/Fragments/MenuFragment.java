package com.example.ajdin.navigatiodraer.Fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.MainActivity;
import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.MenuAdapter;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapter;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapterDatabase;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.ajdin.navigatiodraer.Fragments.DetailFragment.hideSoftKeyboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements SearchView.OnQueryTextListener,AdapterView.OnItemSelectedListener {
    public ListView lvArtikli;
    DatabaseHelper db;
    private final String URL_TO_HIT = "http://192.168.1.103:80/artikli/getJson.php";
    private ProgressDialog dialog;
    private SearchView editsearch;
    List<String> mAllValues;
    private ArrayAdapter<String> mAdapter;
    private Context mContext=getActivity();
    private ArrayList<Artikli> movieModelList;
    private MenuAdapter adapter;
    private List<Artikli> productList;
    private List<Artikli> productList1;
    String[] bankNames={"Odaberi redoslijed","Abecedno","Obrnuto abecedno","Cijeni opadajuci","Cijeni rastuci"};
    String[] kategoies;
    private List<Artikli> filteredValues;
    private List<Artikli> filteredValues1;
    private List<Artikli> filteredKategory;
    private List<Artikli> filteredKategory1;
    private List<Artikli> filteredAll;
    private List<Artikli> filteredAll1;
    private Parcelable state;
    private Spinner spin2;
    private List<String> lables;
    private int odabraniSort;
    private int odabranaKategorija;
    private Spinner spin;
    private String textGeteR;
    private FloatingActionButton fab;
    private Bundle savedState = null;
    private Fragment mContent;
    private TextView brojrez;

    @Override
    public void onResume() {
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        getActivity().setTitle("Svi proizvodi");
        if (!getUserVisibleHint())
        {
            return;
        }


//        fab.setImageResource(R.drawable.dodaj_osobu);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KupacFragment fragment1=new KupacFragment();
//                fragment1.show(getActivity().getSupportFragmentManager(),"dodavanje_kupca");
//
////
//            }
//        });
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            onResume();
        }
    }

    @Override
    public void onPause() {
        state = lvArtikli.onSaveInstanceState();

        super.onPause();
    }

    public MenuFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        db=new DatabaseHelper(getContext());
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);

        getActivity().setTitle("Svi proizvodi");
        ListView list=(ListView)getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        spin = (Spinner)view.findViewById(R.id.simpleSpinner);
        spin2 = (Spinner)view.findViewById(R.id.kategorySpinner);
        spin.setOnItemSelectedListener(this);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (productList == null) {

                } else {


                    odabranaKategorija = i;
                    if (i == 0) {
                        if (filteredAll==null) {
                            filteredAll = new ArrayList<Artikli>(productList);
                        }
                        else {
                            filteredAll = new ArrayList<Artikli>(productList);

                        }


                    } else {

                        filteredKategory = new ArrayList<>(db.getProductsKategoryFiltered1(lables.get(i), productList));

                        if (textGeteR != null) {
                            for (Artikli p : filteredKategory) {
                                if (!p.getNaziv().toLowerCase().contains(textGeteR)) {
                                    filteredKategory.remove(p);
                                }
                            }

                        }

                        filteredAll=new ArrayList<>(filteredKategory);
                    }
                    editsearch.clearFocus();
                    adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredAll);
                    lvArtikli.setAdapter(adapter);
                    brojrez.setText(+filteredAll.size()+" rezultata");
                    if (savedInstanceState != null) {
                        Log.d(TAG, "trying to restore listview state..");
                        lvArtikli.onRestoreInstanceState(state);
                    }
                    final List<Artikli> finalUsedList = filteredAll;
                    lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Artikli movieModel = finalUsedList.get(position); // getting the model
                            DetailFragment fragment = new DetailFragment();
                            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            ft.hide(MenuFragment.this);
                            bundle.putSerializable("movieModel", movieModel);

                            ArrayList<String> slike=new ArrayList<>();
                            for (Slike s:movieModel.getSlike()) {
                                slike.add(s.getId());

                            }
                            ArrayList<Artikli> lista=new ArrayList<>(filteredAll);
                            bundle.putStringArrayList("listaSlike",slike);
                            bundle.putSerializable("lista",lista);
                            bundle.putInt("pozicija",position);
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


//Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,bankNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(18);
                return view;
            }
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextSize(18);
                return v;
            }
        };

//        ArrayAdapter aa2 = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,li);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        loadSpinnerData();
//        spin2.setAdapter(aa2);

        if(db.isEmpty()){
            lvArtikli = (ListView)view.findViewById(R.id.lvMovies);
            lvArtikli.setEmptyView(view.findViewById(R.id.emptyElementMenu));

        }else
        {
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Loading. Please wait...");

            lvArtikli = (ListView)view.findViewById(R.id.lvMovies);

            new JSONTaskDatabase().execute();
            editsearch = (SearchView)view.findViewById(R.id.simpleSearchView);
            brojrez = (TextView)view.findViewById(R.id.brojRezulata);
            editsearch.setOnQueryTextListener(MenuFragment.this);
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


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
        filteredValues = new ArrayList<Artikli>(productList);
        for (Artikli value : productList) {
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

        filteredAll = new ArrayList<>(filteredValues);



        adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredAll);

        lvArtikli.setAdapter(adapter);
        brojrez.setText(+filteredAll.size()+" rezultata");
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artikli movieModel = filteredAll.get(position); // getting the model
                DetailFragment fragment=new DetailFragment();
                android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                ft.hide(MenuFragment.this);
                Bundle bundle=new Bundle();

                ArrayList<String> slike=new ArrayList<>();
                for (Slike s:movieModel.getSlike()) {
                    slike.add(s.getId());

                }
                ArrayList<Artikli> lista=new ArrayList<>(filteredAll);
                bundle.putStringArrayList("listaSlike",slike);
                bundle.putSerializable("lista",lista);
                bundle.putInt("pozicija",position);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (productList == null) {

        } else {


            if (filteredAll==null)
                filteredAll = new ArrayList<Artikli>(productList);


            odabraniSort = i;
            if (i == 0) {

                Collections.sort(filteredAll, new Comparator<Artikli>() {
                    @Override
                    public int compare(Artikli product, Artikli t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());

                    }


                });
            }
            if (i == 1) {
                Collections.sort(filteredAll, new Comparator<Artikli>() {
                    @Override
                    public int compare(Artikli product, Artikli t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                    }


                });
            } else if (i == 2) {
                Collections.sort(filteredAll, new Comparator<Artikli>() {
                    @Override
                    public int compare(Artikli product, Artikli t1) {
                        return t1.getNaziv().compareToIgnoreCase(product.getNaziv());
                    }


                });
            } else if (i == 3) {
                Collections.sort(filteredAll, new Comparator<Artikli>() {
                    @Override
                    public int compare(Artikli p1, Artikli p2) {
                        return p2.getPrice().compareTo(p1.getPrice());
                    }


                });

            } else {

                Collections.sort(filteredAll, new Comparator<Artikli>() {
                    @Override
                    public int compare(Artikli p1, Artikli p2) {
                        return p1.getPrice().compareTo(p2.getPrice());
                    }


                });

            }
           editsearch.clearFocus();




            adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredAll);
            lvArtikli.setAdapter(adapter);
            brojrez.setText(+filteredAll.size()+" rezultata");
            if (state != null) {
                Log.d(TAG, "trying to restore listview state..");
                lvArtikli.onRestoreInstanceState(state);
            }
            final List<Artikli> finalUsedList = filteredAll;
            lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Artikli movieModel = finalUsedList.get(position); // getting the model
                    DetailFragment fragment = new DetailFragment();
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    ft.hide(MenuFragment.this);
                    ArrayList<String> slike=new ArrayList<>();
                    for (Slike s:movieModel.getSlike()) {
                        slike.add(s.getId());

                    }


                    ArrayList<Artikli> lista=new ArrayList<>(finalUsedList);
                    bundle.putStringArrayList("listaSlike",slike);
                    bundle.putSerializable("lista",lista);
                    bundle.putInt("pozicija",position);
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
    private void loadSpinnerData() {
        // database handler

        // Spinner Drop down elements
        lables = db.getAllLabels();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, lables){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(18);
                return view;
            }
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextSize(18);
                return v;
            }
        };

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spin2.setAdapter(dataAdapter);
    }

    public class JSONTask extends AsyncTask<String,String, List<Artikli> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Artikli> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("artikli");

                movieModelList = new ArrayList<Artikli>();

                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    Artikli movieModel = gson.fromJson(finalObject.toString(), Artikli.class); // a single line json parsing using Gson

                    movieModelList.add(movieModel);
                }
                db.InsertArtikal1(movieModelList);
                return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final List<Artikli> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {


                MenuAdapter adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, result);
                lvArtikli.setAdapter(adapter);
                 brojrez.setText(+result.size()+" rezultata");
                if(state != null) {
                    Log.d(TAG, "trying to restore listview state..");
                    lvArtikli.onRestoreInstanceState(state);
                }

                lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Artikli movieModel = result.get(position); // getting the model
                        DetailFragment fragment=new DetailFragment();
                        android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle=new Bundle();
                        ft.hide(MenuFragment.this);
                        bundle.putSerializable("movieModel",movieModel);
                        fragment.setArguments(bundle);
                        ft.add(R.id.content_main,fragment,"detail_fragment");
                        ft.commit();

                    }
                });
            } else {
                Toast.makeText(getContext().getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class JSONTaskDatabase extends AsyncTask<Void,String, List<Artikli> >{

        private List<Product> usedList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Artikli> doInBackground(Void... params) {

            productList = db.getAll1();
            filteredValues = new ArrayList<Artikli>(productList);
            return productList;

        }

        @Override
        protected void onPostExecute(final List<Artikli> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                Collections.sort(result, new Comparator<Artikli>()
                {
                    @Override
                    public int compare(Artikli product, Artikli t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                    }


                });

                MenuAdapter adapter = new MenuAdapter(getActivity(), R.layout.row_menu, result);

                lvArtikli.setAdapter(adapter);
                brojrez.setText(+result.size()+" rezultata");
                if(state != null) {
                    Log.d(TAG, "trying to restore listview state..");
                    lvArtikli.onRestoreInstanceState(state);
                }

                lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Artikli movieModel = result.get(position); // getting the model
                        DetailFragment fragment=new DetailFragment();
                        android.support.v4.app.FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle=new Bundle();
                        ft.hide(MenuFragment.this);
                        ArrayList<String> slike=new ArrayList<>();
                        for (Slike s:movieModel.getSlike()) {
                            slike.add(s.getId());

                        }
                        ArrayList<Artikli> lista=new ArrayList<>(result);
                        bundle.putStringArrayList("listaSlike",slike);
                        bundle.putSerializable("lista",lista);
                        bundle.putInt("pozicija",position);
                        bundle.putSerializable("movieModel",movieModel);

                        fragment.setArguments(bundle);
                        ft.add(R.id.content_main,fragment,"detail_fragment").addToBackStack("details");
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
            filteredAll = new ArrayList<Artikli>(filteredKategory);
        else if (productList!=null){
            filteredAll=new ArrayList<Artikli>(productList);
        }



        textGeteR=null;
        adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredAll);
        if (filteredAll==null) {
            lvArtikli.setEmptyView(getView().findViewById(R.id.emptyElementMenu));
        }
        else {
            lvArtikli.setAdapter(adapter);
            brojrez.setText(+filteredAll.size() + " rezultata");
            spin.setSelection(0);
            editsearch.clearFocus();
        }


    }

}