package com.example.ajdin.navigatiodraer.Fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.MainActivity;
import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.MenuAdapter;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapter;
import com.example.ajdin.navigatiodraer.adapters.MovieAdapterDatabase;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.models.Product;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import java.util.List;

import static android.content.ContentValues.TAG;

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
    private ArrayList<Product> movieModelList;
    private MenuAdapter adapter;
    private List<Product> productList;
    String[] bankNames={"Odaberi redoslijed","Abecedno","Obrnuto abecedno","Cijeni opadajuci","Cijeni rastuci"};
    String[] kategoies;
    private List<Product> filteredValues;
    private Parcelable state;
    private Spinner spin2;
    private List<String> lables;

    @Override
    public void onResume() {
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            lvArtikli.onRestoreInstanceState(state);
        }
        super.onResume();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        db=new DatabaseHelper(getContext());
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("ime","").isEmpty()) {
            fab.setVisibility(View.VISIBLE);
        }
        else {
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setImageResource(R.drawable.dodaj_osobu);
        ListView list=(ListView)getActivity().findViewById(R.id.lista);
        list.setVisibility(View.INVISIBLE);
        final Spinner spin = (Spinner)view.findViewById(R.id.simpleSpinner);
        spin2 = (Spinner)view.findViewById(R.id.kategorySpinner);
        spin.setOnItemSelectedListener(this);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (productList == null) {

                } else {

                    if (i == 0) {
                        filteredValues = new ArrayList<Product>(productList);

                    } else {

                        filteredValues=new ArrayList<>(db.getProductsKategoryFiltered(lables.get(i),productList));
                        spin.setSelection(0);
                    }
                    adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredValues);
                    lvArtikli.setAdapter(adapter);
                    if (state != null) {
                        Log.d(TAG, "trying to restore listview state..");
                        lvArtikli.onRestoreInstanceState(state);
                    }
                    final List<Product> finalUsedList = filteredValues;
                    lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Product movieModel = finalUsedList.get(position); // getting the model
                            DetailFragment fragment = new DetailFragment();
                            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            ft.hide(MenuFragment.this);
                            bundle.putSerializable("movieModel", movieModel);
                            fragment.setArguments(bundle);

                            ft.add(R.id.content_main, fragment);
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
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,bankNames);
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
//            dialog = new ProgressDialog(getActivity());
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(false);
//            dialog.setMessage("Loading. Please wait...");
//            // Create default options which will be used for every
//            //  displayImage(...) call if no options will be passed to this method
//            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .build();
//            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext().getApplicationContext())
//                    .defaultDisplayImageOptions(defaultOptions)
//                    .build();
//            ImageLoader.getInstance().init(config); // Do it on Application start
//
//            lvArtikli = (ListView)view.findViewById(R.id.lvMovies);
//            new JSONTask().execute(URL_TO_HIT);

        }else
        {
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

            lvArtikli = (ListView)view.findViewById(R.id.lvMovies);

            new JSONTaskDatabase().execute();
            editsearch = (SearchView)view.findViewById(R.id.simpleSearchView);
            editsearch.setOnQueryTextListener(MenuFragment.this);
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        state = lvArtikli.onSaveInstanceState();
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

        for (Product value : productList) {
            if (!value.getNaziv().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredValues);

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
                ft.show(fragment);
                Bundle bundle=new Bundle();
                bundle.putSerializable("movieModel",movieModel);
                fragment.setArguments(bundle);
                ft.addToBackStack("detail_fragment");
                editsearch.setQuery("", true);
                ft.replace(R.id.content_main,fragment);
                ft.commit();

            }
        });

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (productList == null) {

        } else {


            if (filteredValues==null)
                filteredValues = new ArrayList<Product>(productList);



            if (i == 0) {
                Collections.sort(filteredValues, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());

                    }


                });
            }
            if (i == 1) {
                Collections.sort(filteredValues, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return product.getNaziv().compareToIgnoreCase(t1.getNaziv());
                    }


                });
            } else if (i == 2) {
                Collections.sort(filteredValues, new Comparator<Product>() {
                    @Override
                    public int compare(Product product, Product t1) {
                        return t1.getNaziv().compareToIgnoreCase(product.getNaziv());
                    }


                });
            } else if (i == 3) {
                Collections.sort(filteredValues, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return p2.getPrice().compareTo(p1.getPrice());
                    }


                });

            } else {

                Collections.sort(filteredValues, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return p1.getPrice().compareTo(p2.getPrice());
                    }


                });

            }





            adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredValues);
            lvArtikli.setAdapter(adapter);
            if (state != null) {
                Log.d(TAG, "trying to restore listview state..");
                lvArtikli.onRestoreInstanceState(state);
            }
            final List<Product> finalUsedList = filteredValues;
            lvArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Product movieModel = finalUsedList.get(position); // getting the model
                    DetailFragment fragment = new DetailFragment();
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    ft.hide(MenuFragment.this);
                    bundle.putSerializable("movieModel", movieModel);
                    fragment.setArguments(bundle);
                    ft.add(R.id.content_main, fragment);
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
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spin2.setAdapter(dataAdapter);
    }

    public class JSONTask extends AsyncTask<String,String, List<Product> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Product> doInBackground(String... params) {
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

                movieModelList = new ArrayList<>();

                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    Product movieModel = gson.fromJson(finalObject.toString(), Product.class); // a single line json parsing using Gson

                    movieModelList.add(movieModel);
                }
                db.InsertArtikal(movieModelList);
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
        protected void onPostExecute(final List<Product> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {


                MenuAdapter adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, result);
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
                        ft.commit();

                    }
                });
            } else {
                Toast.makeText(getContext().getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class JSONTaskDatabase extends AsyncTask<Void,String, List<Product> >{

        private List<Product> usedList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Product> doInBackground(Void... params) {

            productList = db.getAll();
            filteredValues = new ArrayList<Product>(productList);
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

                MenuAdapter adapter = new MenuAdapter(getActivity(), R.layout.row_menu, result);

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
                        ft.hide(MenuFragment.this);
                        bundle.putSerializable("movieModel",movieModel);
                        fragment.setArguments(bundle);
                        ft.add(R.id.content_main,fragment).addToBackStack("details");
                        ft.commit();

                    }
                });
            } else {
                Toast.makeText(getContext().getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void populateList() {

        mAllValues = new ArrayList<>();

        mAllValues.add("Afghanistan");
        mAllValues.add("Åland Islands");
        mAllValues.add("Albania");
        mAllValues.add("Algeria");
        mAllValues.add("American Samoa");
        mAllValues.add("AndorrA");
        mAllValues.add("Angola");
        mAllValues.add("Anguilla");
        mAllValues.add("Antarctica");
        mAllValues.add("Antigua and Barbuda");
        mAllValues.add("Argentina");
        mAllValues.add("Armenia");
        mAllValues.add("Aruba");
        mAllValues.add("Australia");
        mAllValues.add("Austria");
        mAllValues.add("Azerbaijan");
        mAllValues.add("Bahamas");
        mAllValues.add("Bahrain");
        mAllValues.add("Bangladesh");
        mAllValues.add("Barbados");
        mAllValues.add("Belarus");
        mAllValues.add("Belgium");
        mAllValues.add("Belize");
        mAllValues.add("Benin");
        mAllValues.add("Bermuda");
        mAllValues.add("Bhutan");
        mAllValues.add("Bolivia");
        mAllValues.add("Bosnia and Herzegovina");
        mAllValues.add("Botswana");
        mAllValues.add("Bouvet Island");
        mAllValues.add("Brazil");
        mAllValues.add("British Indian Ocean Territory");
        mAllValues.add("Brunei Darussalam");
        mAllValues.add("Bulgaria");
        mAllValues.add("Burkina Faso");
        mAllValues.add("Burundi");
        mAllValues.add("Cambodia");
        mAllValues.add("Cameroon");
        mAllValues.add("Canada");
        mAllValues.add("Cape Verde");
        mAllValues.add("Cayman Islands");
        mAllValues.add("Central African Republic");
        mAllValues.add("Chad");
        mAllValues.add("Chile");
        mAllValues.add("China");
        mAllValues.add("Christmas Island");
        mAllValues.add("Cocos (Keeling) Islands");
        mAllValues.add("Colombia");
        mAllValues.add("Comoros");
        mAllValues.add("Congo");
        mAllValues.add("Congo, The Democratic Republic of the");
        mAllValues.add("Cook Islands");
        mAllValues.add("Costa Rica");
        mAllValues.add("Cote D\'Ivoire");
        mAllValues.add("Croatia");
        mAllValues.add("Cuba");
        mAllValues.add("Cyprus");
        mAllValues.add("Czech Republic");
        mAllValues.add("Denmark");
        mAllValues.add("Djibouti");
        mAllValues.add("Dominica");
        mAllValues.add("Dominican Republic");
        mAllValues.add("Ecuador");
        mAllValues.add("Egypt");
        mAllValues.add("El Salvador");
        mAllValues.add("Equatorial Guinea");
        mAllValues.add("Eritrea");
        mAllValues.add("Estonia");
        mAllValues.add("Ethiopia");
        mAllValues.add("Falkland Islands (Malvinas)");
        mAllValues.add("Faroe Islands");
        mAllValues.add("Fiji");
        mAllValues.add("Finland");
        mAllValues.add("France");
        mAllValues.add("French Guiana");
        mAllValues.add("French Polynesia");
        mAllValues.add("French Southern Territories");
        mAllValues.add("Gabon");
        mAllValues.add("Gambia");
        mAllValues.add("Georgia");
        mAllValues.add("Germany");
        mAllValues.add("Ghana");
        mAllValues.add("Gibraltar");
        mAllValues.add("Greece");
        mAllValues.add("Greenland");
        mAllValues.add("Grenada");
        mAllValues.add("Guadeloupe");
        mAllValues.add("Guam");
        mAllValues.add("Guatemala");
        mAllValues.add("Guernsey");
        mAllValues.add("Guinea");
        mAllValues.add("Guinea-Bissau");
        mAllValues.add("Guyana");
        mAllValues.add("Haiti");
        mAllValues.add("Heard Island and Mcdonald Islands");
        mAllValues.add("Holy See (Vatican City State)");
        mAllValues.add("Honduras");
        mAllValues.add("Hong Kong");
        mAllValues.add("Hungary");
        mAllValues.add("Iceland");
        mAllValues.add("India");
        mAllValues.add("Indonesia");
        mAllValues.add("Iran, Islamic Republic Of");
        mAllValues.add("Iraq");
        mAllValues.add("Ireland");
        mAllValues.add("Isle of Man");
        mAllValues.add("Israel");
        mAllValues.add("Italy");
        mAllValues.add("Jamaica");
        mAllValues.add("Japan");
        mAllValues.add("Jersey");
        mAllValues.add("Jordan");
        mAllValues.add("Kazakhstan");
        mAllValues.add("Kenya");
        mAllValues.add("Kiribati");
        mAllValues.add("Korea, Democratic People\'S Republic of");
        mAllValues.add("Korea, Republic of");
        mAllValues.add("Kuwait");
        mAllValues.add("Kyrgyzstan");
        mAllValues.add("Lao People\'S Democratic Republic");
        mAllValues.add("Latvia");
        mAllValues.add("Lebanon");
        mAllValues.add("Lesotho");
        mAllValues.add("Liberia");
        mAllValues.add("Libyan Arab Jamahiriya");
        mAllValues.add("Liechtenstein");
        mAllValues.add("Lithuania");
        mAllValues.add("Luxembourg");
        mAllValues.add("Macao");
        mAllValues.add("Macedonia, The Former Yugoslav Republic of");
        mAllValues.add("Madagascar");
        mAllValues.add("Malawi");
        mAllValues.add("Malaysia");
        mAllValues.add("Maldives");
        mAllValues.add("Mali");
        mAllValues.add("Malta");
        mAllValues.add("Marshall Islands");
        mAllValues.add("Martinique");
        mAllValues.add("Mauritania");
        mAllValues.add("Mauritius");
        mAllValues.add("Mayotte");
        mAllValues.add("Mexico");
        mAllValues.add("Micronesia, Federated States of");
        mAllValues.add("Moldova, Republic of");
        mAllValues.add("Monaco");
        mAllValues.add("Mongolia");
        mAllValues.add("Montserrat");
        mAllValues.add("Morocco");
        mAllValues.add("Mozambique");
        mAllValues.add("Myanmar");
        mAllValues.add("Namibia");
        mAllValues.add("Nauru");
        mAllValues.add("Nepal");
        mAllValues.add("Netherlands");
        mAllValues.add("Netherlands Antilles");
        mAllValues.add("New Caledonia");
        mAllValues.add("New Zealand");
        mAllValues.add("Nicaragua");
        mAllValues.add("Niger");
        mAllValues.add("Nigeria");
        mAllValues.add("Niue");
        mAllValues.add("Norfolk Island");
        mAllValues.add("Northern Mariana Islands");
        mAllValues.add("Norway");
        mAllValues.add("Oman");
        mAllValues.add("Pakistan");
        mAllValues.add("Palau");
        mAllValues.add("Palestinian Territory, Occupied");
        mAllValues.add("Panama");
        mAllValues.add("Papua New Guinea");
        mAllValues.add("Paraguay");
        mAllValues.add("Peru");
        mAllValues.add("Philippines");
        mAllValues.add("Pitcairn");
        mAllValues.add("Poland");
        mAllValues.add("Portugal");
        mAllValues.add("Puerto Rico");
        mAllValues.add("Qatar");
        mAllValues.add("Reunion");
        mAllValues.add("Romania");
        mAllValues.add("Russian Federation");
        mAllValues.add("RWANDA");
        mAllValues.add("Saint Helena");
        mAllValues.add("Saint Kitts and Nevis");
        mAllValues.add("Saint Lucia");
        mAllValues.add("Saint Pierre and Miquelon");
        mAllValues.add("Saint Vincent and the Grenadines");
        mAllValues.add("Samoa");
        mAllValues.add("San Marino");
        mAllValues.add("Sao Tome and Principe");
        mAllValues.add("Saudi Arabia");
        mAllValues.add("Senegal");
        mAllValues.add("Serbia and Montenegro");
        mAllValues.add("Seychelles");
        mAllValues.add("Sierra Leone");
        mAllValues.add("Singapore");
        mAllValues.add("Slovakia");
        mAllValues.add("Slovenia");
        mAllValues.add("Solomon Islands");
        mAllValues.add("Somalia");
        mAllValues.add("South Africa");
        mAllValues.add("South Georgia and the South Sandwich Islands");
        mAllValues.add("Spain");
        mAllValues.add("Sri Lanka");
        mAllValues.add("Sudan");
        mAllValues.add("Suriname");
        mAllValues.add("Svalbard and Jan Mayen");
        mAllValues.add("Swaziland");
        mAllValues.add("Sweden");
        mAllValues.add("Switzerland");
        mAllValues.add("Syrian Arab Republic");
        mAllValues.add("Taiwan, Province of China");
        mAllValues.add("Tajikistan");
        mAllValues.add("Tanzania, United Republic of");
        mAllValues.add("Thailand");
        mAllValues.add("Timor-Leste");
        mAllValues.add("Togo");
        mAllValues.add("Tokelau");
        mAllValues.add("Tonga");
        mAllValues.add("Trinidad and Tobago");
        mAllValues.add("Tunisia");
        mAllValues.add("Turkey");
        mAllValues.add("Turkmenistan");
        mAllValues.add("Turks and Caicos Islands");
        mAllValues.add("Tuvalu");
        mAllValues.add("Uganda");
        mAllValues.add("Ukraine");
        mAllValues.add("United Arab Emirates");
        mAllValues.add("United Kingdom");
        mAllValues.add("United States");
        mAllValues.add("United States Minor Outlying Islands");
        mAllValues.add("Uruguay");
        mAllValues.add("Uzbekistan");
        mAllValues.add("Vanuatu");
        mAllValues.add("Venezuela");
        mAllValues.add("Viet Nam");
        mAllValues.add("Virgin Islands, British");
        mAllValues.add("Virgin Islands, U.S.");
        mAllValues.add("Wallis and Futuna");
        mAllValues.add("Western Sahara");
        mAllValues.add("Yemen");
        mAllValues.add("Zambia");
        mAllValues.add("Zimbabwe");
        adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row, productList);
        lvArtikli.setAdapter(adapter);
    }

    public void resetSearch() {
        filteredValues = new ArrayList<Product>(productList);
        adapter = new MenuAdapter(getContext().getApplicationContext(), R.layout.row_menu, filteredValues);
        lvArtikli.setAdapter(adapter);

    }
}
