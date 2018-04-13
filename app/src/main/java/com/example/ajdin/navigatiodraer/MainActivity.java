package com.example.ajdin.navigatiodraer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
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

import com.example.ajdin.navigatiodraer.Fragments.CRUDFragment;
import com.example.ajdin.navigatiodraer.Fragments.DetailFragment;
import com.example.ajdin.navigatiodraer.Fragments.EditProduct;
import com.example.ajdin.navigatiodraer.Fragments.HistoryFragment;
import com.example.ajdin.navigatiodraer.Fragments.KupacFragment;
import com.example.ajdin.navigatiodraer.Fragments.MenuFragment;
import com.example.ajdin.navigatiodraer.Fragments.NewproductsFragment;
import com.example.ajdin.navigatiodraer.Fragments.NoteFragment;
import com.example.ajdin.navigatiodraer.Fragments.CartFragment;
import com.example.ajdin.navigatiodraer.Fragments.SnizenjeFragment;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.Constant;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.services.ConnectionService;
import com.example.ajdin.navigatiodraer.services.MyServiceUploading;
import com.example.ajdin.navigatiodraer.services.TimeService;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MAIN" ;
    public ListView lvArtikli;
    private final String URL_TO_HIT = "http://nurexport.com/demo/getJson.php";
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    DatabaseHelper db;
    List<String> mAllValues;
    private ArrayAdapter<String> mAdapter;
    private Context mContext = MainActivity.this;
    SearchView searchView;
    private ProgressDialog dialog;
    public NavigationView navigationView;
    private FloatingActionButton fab;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

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
        SharedPreferences sharedPreferences=getSharedPreferences("podaci", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (sharedPreferences.getString("ime","").isEmpty()) {
            fab.setVisibility(View.VISIBLE);
        }
        else {
            fab.setVisibility(View.INVISIBLE);
        }
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
        setTitle("Svi proizvodi");
        navigationView.setCheckedItem(R.id.nav_proizvodi);
        Intent intent = new Intent(this, TimeService.class);
        startService(intent);


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
            MenuFragment fragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag("first_frag");
            NewproductsFragment fragment1 = (NewproductsFragment) getSupportFragmentManager().findFragmentByTag("new_prod_frag");
            NoteFragment fragment2 = (NoteFragment) getSupportFragmentManager().findFragmentByTag("note_fragment");
            HistoryFragment fragment3 = (HistoryFragment) getSupportFragmentManager().findFragmentByTag("history_frag");
            DetailFragment fragment6 = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detail_fragment");
            CartFragment fragment4 = (CartFragment) getSupportFragmentManager().findFragmentByTag("cart_frag");
            SnizenjeFragment fragment5 = (SnizenjeFragment) getSupportFragmentManager().findFragmentByTag("snizenje_frag");
            EditProduct fragment7 = (EditProduct) getSupportFragmentManager().findFragmentByTag("editFragment");



            getSupportFragmentManager().popBackStack();
            android.support.v4.app.Fragment f= getCurrentFragment();


//              if (fragment4 != null) {
//                if (fragment4.isDetached()) {
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.show(fragment4);
//                    ft.commit();
//                    fab.setVisibility(View.GONE);
//                    navigationView.setCheckedItem(R.id.nav_korpa);
//                    setTitle("Korpa");
//
//                }
//            }
                if (fragment6 != null && f.getTag() != fragment6.getTag()) {

                        if (!fragment6.isVisible()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment6);
                            ft.commit();
                            fab.setVisibility(View.GONE);
                            setTitle("Detalji proizvoda");

                        }
                    }
             else if (fragment7 != null && f.getTag() != fragment7.getTag()) {

                if (!fragment7.isVisible()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.show(fragment7);
                    ft.commit();
                    fab.setVisibility(View.GONE);
                    setTitle("Detalji proizvoda");

                }
            }
                else if (fragment4 != null && f.getTag() != fragment4.getTag()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.show(fragment4);
                    ft.commit();
                    fab.setVisibility(View.INVISIBLE);
                    setTitle("Korpa");
                    navigationView.setCheckedItem(R.id.nav_korpa);

                }

             else if (fragment2 != null && f.getTag() != fragment2.getTag()) {

                        if (!fragment2.isVisible()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment2);
                            ft.commit();
                            fab.setImageResource(R.drawable.ic_note_add_white_24px);
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CRUDFragment fragment = new CRUDFragment();
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_main, fragment, "CRUDFragment").addToBackStack("CRUDFragment");
                                    ft.commit();

//
                                }
                            });
                            setTitle("Napomene ");
                            navigationView.setCheckedItem(R.id.nav_napomene);


                        }

            } else if (fragment3 != null && f.getTag() != fragment3.getTag() ) {

                        if (!fragment3.isVisible()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment3);
                            ft.commit();
                            setTitle("Historija računa");
                            fab.setVisibility(View.GONE);
                            navigationView.setCheckedItem(R.id.nav_history);

                        }

            } else if (fragment != null && f.getTag() != fragment.getTag()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment);
                            ft.commit();
                            fab.setImageResource(R.drawable.dodaj_osobu);
                            SharedPreferences sharedPreferences = getSharedPreferences("podaci", Context.MODE_PRIVATE);
                            if (sharedPreferences.getString("ime", "").isEmpty()) {
                                fab.setVisibility(View.VISIBLE);
                            } else {
                                fab.setVisibility(View.INVISIBLE);
                            }


                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    KupacFragment fragment1 = new KupacFragment();
                                    fragment1.show(getSupportFragmentManager(), "dodavanje_kupca");

//
                                }
                            });
                            setTitle("Svi proizvodi");
                            navigationView.setCheckedItem(R.id.nav_proizvodi);


                        }


           else if (fragment1 != null && f.getTag() != fragment1.getTag()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment1);
                            ft.commit();
                            fab.setImageResource(R.drawable.dodaj_osobu);
                            SharedPreferences sharedPreferences = getSharedPreferences("podaci", Context.MODE_PRIVATE);
                            if (sharedPreferences.getString("ime", "").isEmpty()) {
                                fab.setVisibility(View.VISIBLE);
                            } else {
                                fab.setVisibility(View.INVISIBLE);
                            }


                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    KupacFragment fragment1 = new KupacFragment();
                                    fragment1.show(getSupportFragmentManager(), "dodavanje_kupca");

//
                                }
                            });
                            setTitle("Novi proizvodi");
                            navigationView.setCheckedItem(R.id.nav_novi_proizvodi);

                        }




            else if (fragment5 != null && f.getTag() != fragment5.getTag()) {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(fragment5);
                            ft.commit();
                            fab.setImageResource(R.drawable.dodaj_osobu);
                            SharedPreferences sharedPreferences = getSharedPreferences("podaci", Context.MODE_PRIVATE);
                            if (sharedPreferences.getString("ime", "").isEmpty()) {
                                fab.setVisibility(View.VISIBLE);
                            } else {
                                fab.setVisibility(View.INVISIBLE);
                            }


                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    KupacFragment fragment1 = new KupacFragment();
                                    fragment1.show(getSupportFragmentManager(), "dodavanje_kupca");

//
                                }
                            });
                            setTitle("Snizeni proizvodi");
                            navigationView.setCheckedItem(R.id.nav_snizeno);

                        }





    }

        else {
            new AlertDialog.Builder(this)
                    .setMessage("Da li ste sigurni da želite izici?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();



        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


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
            ConnectivityManager wifi = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info.isConnected()) {
                new JSONTask().execute(URL_TO_HIT);
            }


        }
        if (id == R.id.nav_cart) {
            CartFragment fragment=new CartFragment();
            String  tag="cart_frag";

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            android.support.v4.app.Fragment CurrentFragment= getSupportFragmentManager().findFragmentById(R.id.content_main);
            ft.add(R.id.content_main, fragment, tag);
            ft.hide(CurrentFragment);
            navigationView.setCheckedItem(R.id.nav_korpa);
            ft.addToBackStack(CurrentFragment.getTag());
            setTitle("Korpa");
            ft.hide(CurrentFragment);
            ft.commit();
            // do something with f


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
            setTitle("Korpa");
            tag="cart_frag";
            // Handle the camera action
        } else if (id == R.id.nav_proizvodi) {
            fragment = new MenuFragment();
            setTitle("Svi proizvodi");
            tag="first_frag";

        } else if (id == R.id.nav_history) {
            fragment=new HistoryFragment();
            setTitle("Historija računa");

            tag="history_frag";
        }

        else if (id == R.id.nav_kupac) {
            final SharedPreferences sharedPreferences=getSharedPreferences("podaci", Context.MODE_PRIVATE);
            String ime=sharedPreferences.getString("ime","");
            if (!ime.isEmpty()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.brisanje_kupca))
                        .setMessage(getResources().getString(R.string.delete_kupac))
                        .setPositiveButton(getResources().getString(R.string.da), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences.edit().clear().commit();
                                fab.setVisibility(View.VISIBLE);

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Ne), null)
                        .show();

            }else {
                KupacFragment fragment1=new KupacFragment();
                fragment1.show(getSupportFragmentManager(),"Mydialog");
                tag="dialog_kupac";
            }




        } else if (id == R.id.nav_snizeno) {
            fragment = new SnizenjeFragment();
            setTitle("Sniženi proizvodi");
            tag="snizenje_frag";

        } else if (id == R.id.nav_novi_proizvodi) {
            fragment = new NewproductsFragment();
            setTitle("Novi proizvodi");
            tag="new_prod_frag";

        } else {
            fragment=new NoteFragment();
            setTitle("Napomene");
            tag="note_fragment";

        }

        if (fragment != null) {

            android.support.v4.app.Fragment CurrentFragment= getSupportFragmentManager().findFragmentById(R.id.content_main);
            // do something with f

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.content_main, fragment,tag);
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

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
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
                ArrayList<Artikli> movieModelList1 = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    Artikli movieModel = gson.fromJson(finalObject.toString(), Artikli.class); // a single line json parsing using Gson

                    movieModelList1.add(movieModel);
                    db.replaceSlike(movieModel.getSlike());

                }

                db.replace1(movieModelList1);



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
            ft.replace(R.id.content_main, fragment,"first_frag");
            ft.commit();

        }



        public void resetSearch() {
            mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
            lvArtikli.setAdapter(mAdapter);
        }
    }
}