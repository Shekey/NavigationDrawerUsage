package com.example.ajdin.navigatiodraer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.Fragments.KupacFragment;
import com.example.ajdin.navigatiodraer.Fragments.MenuFragment;
import com.example.ajdin.navigatiodraer.Fragments.NewproductsFragment;
import com.example.ajdin.navigatiodraer.Fragments.NoteFragment;
import com.example.ajdin.navigatiodraer.Fragments.CartFragment;
import com.example.ajdin.navigatiodraer.Fragments.SnizenjeFragment;
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
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public ListView lvArtikli;
    private final String URL_TO_HIT = "http://192.168.1.103:80/artikli/getJson.php";
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    DatabaseHelper db;
    List<String> mAllValues;
    private ArrayAdapter<String> mAdapter;
    private Context mContext = MainActivity.this;
    SearchView searchView;
    private ProgressDialog dialog;
    private NavigationView navigationView;


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET}, 1);
        lvArtikli = (ListView) findViewById(R.id.lista);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start


        db = new DatabaseHelper(MainActivity.this);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Ajdin");
        arrayList.add("Husan");
        arrayList.add("Amira");
        arrayList.add("Seval");
//        adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList);
//        lvArtikli.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.dodaj_osobu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KupacFragment fragment1=new KupacFragment();
                fragment1.show(getSupportFragmentManager(),"dodavanje_kupca");

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final MenuFragment fragment = new MenuFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_main, fragment,"first_frag");
        ft.commit();
        navigationView.setCheckedItem(R.id.nav_proizvodi);

    }

    public android.support.v4.app.Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.content_main);
    }
    public android.support.v4.app.Fragment getVisibleFragment(){
        android.support.v4.app.FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<android.support.v4.app.Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(android.support.v4.app.Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (count != 0) {
            MenuFragment fragment  =(MenuFragment)getSupportFragmentManager().findFragmentByTag("first_frag");
            NewproductsFragment fragment1  =(NewproductsFragment)getSupportFragmentManager().findFragmentByTag("new_prod_frag");

            getSupportFragmentManager().popBackStack();
            if (fragment!=null){
            if (fragment.isVisible()){
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                ft.show(fragment);
                ft.commit();
                navigationView.setCheckedItem(R.id.nav_proizvodi);

            }
            }
            else if(fragment1!=null){
            if (fragment1.isVisible()){
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                ft.show(fragment1);
                ft.commit();
                navigationView.setCheckedItem(R.id.nav_novi_proizvodi);

            }
              }
        } else {
            super.onBackPressed();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem item=menu.findItem(R.id.action_search);
//        SearchView searchView=(SearchView)item.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                adapter.getFilter().filter(s);
//                return  false;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_sync) {
            new JSONTask().execute(URL_TO_HIT);

        }
        if (id == R.id.nav_cart) {
            CartFragment fragment=new CartFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.content_main, fragment).addToBackStack("korpa_nav");
            ft.commit();
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        android.support.v4.app.Fragment fragment = null;
        int id = item.getItemId();
        String tag="";

        if (id == R.id.nav_korpa) {
            fragment = new CartFragment();
            // Handle the camera action
        } else if (id == R.id.nav_proizvodi) {
            fragment = new MenuFragment();
            tag="menu_frag";

        } else if (id == R.id.nav_kupac) {

            KupacFragment fragment1=new KupacFragment();
            fragment1.show(getSupportFragmentManager(),"Mydialog");
            tag="dialog_kupac";

        } else if (id == R.id.nav_snizeno) {
            fragment = new SnizenjeFragment();

        } else if (id == R.id.nav_novi_proizvodi) {
            fragment = new NewproductsFragment();
            tag="new_prod_frag";

        } else {
            fragment=new NoteFragment();
            tag="note_fragment";

        }

        if (fragment != null) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment,tag).addToBackStack(tag);
            ft.commit();

        }
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
drawer.postDelayed(new Runnable() {
    @Override
    public void run() {
        drawer.closeDrawer(GravityCompat.START);
    }
        },300);

        return true;
    }

    public class JSONTask extends AsyncTask<String, String, Void> implements com.example.ajdin.navigatiodraer.JSONTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("artikli");

                ArrayList<Product> movieModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    Product movieModel = gson.fromJson(finalObject.toString(), Product.class); // a single line json parsing using Gson

                    movieModelList.add(movieModel);
                }

                db.replace(movieModelList);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            dialog.dismiss();
            MenuFragment fragment = new MenuFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();

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


            mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
            lvArtikli.setAdapter(mAdapter);
        }

        public void resetSearch() {
            mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
            lvArtikli.setAdapter(mAdapter);
        }
    }
}
