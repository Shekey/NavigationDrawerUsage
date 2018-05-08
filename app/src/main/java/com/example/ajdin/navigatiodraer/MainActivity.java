package com.example.ajdin.navigatiodraer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.example.ajdin.navigatiodraer.Fragments.KorisnikFragment;
import com.example.ajdin.navigatiodraer.Fragments.KupacFragment;
import com.example.ajdin.navigatiodraer.Fragments.MenuFragment;
import com.example.ajdin.navigatiodraer.Fragments.NewproductsFragment;
import com.example.ajdin.navigatiodraer.Fragments.NoteFragment;
import com.example.ajdin.navigatiodraer.Fragments.CartFragment;
import com.example.ajdin.navigatiodraer.Fragments.SnizenjeFragment;

import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.CartItemAdapter;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;

import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;
import com.example.ajdin.navigatiodraer.services.TimeService;
import com.google.gson.Gson;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MAIN" ;
    public ListView lvArtikli;


    private final String BASE_URL = "http://nurexport.com/demo/";
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    DatabaseHelper db;
    int brojac=0;
    int brojacZavrsenih=0;
    List<String> mAllValues;
    ArrayList<Slike> sveSlike=new ArrayList<>();
    ArrayList<String> URLLIST=new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private Context mContext = MainActivity.this;
    SearchView searchView;
    private ProgressDialog dialog;
    public NavigationView navigationView;
    private FloatingActionButton fab;
    ArrayList<Slike> slikeURL=new ArrayList<>();
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private int sizeSlike;
    private ArrayList<Slike> downloadList;
    private CartItemAdapter cartItemAdapter;
    private String url_to_hit;
    private String macAdresa;

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

//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheInMemory(false)
//                .cacheOnDisk(false)
//                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this)
//                .defaultDisplayImageOptions(defaultOptions)
//                .build();
//        ImageLoader.getInstance().init(config); // Do it on Application start


        db = new DatabaseHelper(MainActivity.this);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Ajdin");
        arrayList.add("Husan");
        arrayList.add("Amira");
        arrayList.add("Seval");
//        adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList);
//        lvArtikli.setAdapter(adapter);
        SharedPreferences sharedPreferences=getSharedPreferences("podaci", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("ime").commit();
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        if (sharedPreferences.getString("ime","").isEmpty()) {
//            fab.setVisibility(View.VISIBLE);
//        }
//        else {
//            fab.setVisibility(View.INVISIBLE);
//        }
        fab.setImageResource(R.drawable.ic_note_add_white_24px);
        fab.setVisibility(View.INVISIBLE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                KupacFragment fragment1=new KupacFragment();
////                fragment1.show(getSupportFragmentManager(),"dodavanje_kupca");
//
//            }
//        });

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
            KorisnikFragment fragment8 = (KorisnikFragment) getSupportFragmentManager().findFragmentByTag("postavke");

            android.support.v4.app.Fragment f= getCurrentFragment();
            if (f!=null) {
                if (f.getTag().equals("detail_fragment") && f.isVisible()) {

                    FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
                    ftt.remove(f).commit();
                }
            }
            getSupportFragmentManager().popBackStackImmediate();

            f=getCurrentFragment();


                if (fragment6 != null && f.getTag().equals(fragment6.getTag())) {

                            setTitle("Detalji proizvoda");

                    }
             else if (fragment7 != null && f.getTag().equals(fragment7.getTag())) {

                if (!fragment7.isVisible()) {
                    setTitle("Detalji proizvoda");

                }
            }
                else if (fragment4 != null && f.getTag().equals(fragment4.getTag())) {


                    setTitle("Korpa");
                    navigationView.setCheckedItem(R.id.nav_korpa);

                }
                else if (fragment != null &&f.getTag().equals(fragment.getTag())) {
//
                    setTitle("Svi proizvodi");
                    navigationView.setCheckedItem(R.id.nav_proizvodi);


                }
                else if (fragment1 != null &&  f.getTag().equals(fragment1.getTag())) {

//
                    setTitle("Novi proizvodi");
                    navigationView.setCheckedItem(R.id.nav_novi_proizvodi);

                }
                else if (fragment8 != null &&  f.getTag().equals(fragment8.getTag())) {

//
                    setTitle("Postavke");
                    navigationView.setCheckedItem(R.id.komercijalista);

                }




                else if (fragment5 != null && f.getTag().equals(fragment5.getTag())) {

                    setTitle("Snizeni proizvodi");
                    navigationView.setCheckedItem(R.id.nav_snizeno);

                }




             else if (fragment2 != null && f.getTag().equals(fragment2.getTag())) {



                           fab.setImageResource(R.drawable.ic_note_add_white_24px);
                           fab.setVisibility(View.VISIBLE);
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




            } else if (fragment3 != null && f.getTag().equals(fragment3.getTag()) ) {



                            setTitle("Historija računa");
//                            fab.setVisibility(View.GONE);
                            navigationView.setCheckedItem(R.id.nav_history);



            }






    }

        else {
            new AlertDialog.Builder(this)
                    .setMessage("Da li ste sigurni da želite izici?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearCart();
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();



        }

    }
    private void clearCart(){
        SharedPreferences sharedPreferences=getSharedPreferences("podaci", Context.MODE_PRIVATE);
        SharedPreferences.Editor spreferencesEditor = sharedPreferences.edit();
        spreferencesEditor.remove("ime");
        spreferencesEditor.remove("path");
        spreferencesEditor.commit();
        Cart cart = CartHelper.getCart();
        cart.clear();
        cartItemAdapter = new CartItemAdapter(MainActivity.this);
        cartItemAdapter.updateCartItems(getCartItems(cart));
        cartItemAdapter.notifyDataSetChanged();



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
                final SharedPreferences sharedPreferences=getSharedPreferences("podaci", Context.MODE_PRIVATE);
                String licenca=sharedPreferences.getString("licenca","");
                int Ispravna=sharedPreferences.getInt("ispravnost",0);
                if (licenca.equals("")){
                    Toast.makeText(mContext, "Neispravna licenca", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (Ispravna==0){
                    macAdresa = getMacAddress(MainActivity.this);
                    url_to_hit = "http://nurexport.com/demo/getJsonFake.php?id="+licenca+"&mac="+macAdresa;
                }
                else {
                    url_to_hit = "http://nurexport.com/demo/getJson.php";

                }
                new JSONTask().execute(url_to_hit);
            }
            else{
                Toast.makeText(mContext, "Uključite konekciju !", Toast.LENGTH_LONG).show();
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
            ft.commit();
            // do something with f


        }
        if (id == R.id.nav_All) {
            android.support.v4.app.Fragment CurrentFragment= getSupportFragmentManager().findFragmentById(R.id.content_main);
            String  tag="first_frag";
            MenuFragment fragment=new MenuFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment, tag).addToBackStack(tag).commit();
            navigationView.setCheckedItem(R.id.nav_proizvodi);
            setTitle("Svi proizvodi");



        }
        if (id == R.id.nav_remove) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.detele_cart))
                    .setMessage(getResources().getString(R.string.detele_korpe))
                    .setPositiveButton(getResources().getString(R.string.da), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.support.v4.app.Fragment CurrentFragment= getSupportFragmentManager().findFragmentById(R.id.content_main);

                            if (CurrentFragment.getTag().equals("cart_frag")){
                                CartFragment fragment=new CartFragment();
                                String  tag="cart_frag";

                                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_main, fragment, tag).addToBackStack("cart_frag");
                                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                                manager.popBackStack();

                                ft.commit();
                                setTitle("Korpa");
                            }
                           clearCart();

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.Ne), null)
                    .show();
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
            setTitle("Historija kupovina");

            tag="history_frag";
        }
        else if (id == R.id.komercijalista) {
            fragment = new KorisnikFragment();
            setTitle("Postavke");
            tag="postavke";
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
                                sharedPreferences.edit().remove("ime").commit();
//                                fab.setVisibility(View.VISIBLE);

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

        }
        else if (id == R.id.izlaz) {
            new AlertDialog.Builder(this)
                    .setMessage("Da li ste sigurni da želite izici?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearCart();
                            finish();
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();

        }
        else {
            fragment=new NoteFragment();
            setTitle("Napomene");
            tag="note_fragment";

        }

        if (fragment != null) {

            android.support.v4.app.Fragment CurrentFragment= getSupportFragmentManager().findFragmentById(R.id.content_main);
            // do something with f

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.remove(CurrentFragment);
                ft.add(R.id.content_main, fragment,tag).addToBackStack(tag);
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
            boolean ispravnost=true;
            SharedPreferences sharedPreferences;
            SharedPreferences.Editor editor;
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


                connection.disconnect();

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
                    Artikli movieModel = gson.fromJson(finalObject.toString(), Artikli.class);// a single line json parsing using Gson
                    if(movieModel.getNaziv().equals("Neispravna licenca"))
                    {
                        ispravnost=false;
                        db.deleteAll();
                    }
                    movieModelList1.add(movieModel);
                    List<Slike> listaSlike=movieModel.getSlike();
                    if (listaSlike!=null) {
                        for (Slike s:movieModel.getSlike()) {
                            slikeURL.add(s);

//                            slikeURL.add(BASE_URL+uri.toString());
//                            DownloadImageFromPath(BASE_URL+uri.toString(),s.getId()+".jpg");

//                            downloadThroughManager(s.getImage(),s.getId(),MainActivity.this);
//                            brojac++;
                            Log.d(TAG, "doInBackground: "+String.valueOf(brojac));

                        }



                    }
//                    for (Slike s:movieModel.getSlike()) {
//                        String kopija=s.getImage();
//                        URI uri = new URI(kopija.replace(" ", "%20"));
//                        if (brojac>=100){
//                            sveSlike.add(s);
//                        }
//                        else {
//                            downloadImage(s.getId(), uri.toString());
//                            brojac++;
//                            Log.d(TAG, "doInBackground: " + uri.toString() +" -" +String.valueOf(brojac));
//                        }
//
//                    }

                }
                downloadList = db.replaceSlike(slikeURL);
                for (Slike ss: downloadList) {
                    String kopija=ss.getImage();
                    URI uri = new URI(kopija.replace(" ", "%20"));
                    if(isFileExists(ss.getId()+".jpg")==false) {
                        downloadImageURL(ss.getId(), uri.toString());
                    }

                }

//                String kopija="upload/04-16-2018_0435pmdetergenti.jpg";
//                URI uri = new URI(kopija.replace(" ", "%20"));
//                Log.d(TAG, "doInBackground: "+uri.toString());
//                downloadImageURL("146",uri.toString());
////
//
//                kopija="upload/Bitmap in DETERĐENT-MARAMICE-2.jpg";
//                uri = new URI(kopija.replace(" ", "%20"));
//                Log.d(TAG, "doInBackground: "+uri.toString());
//                downloadImageURL("147",uri.toString());
////
//                kopija="upload/04-12-2018_0638amBitmap in DETERĐENT-MARAMICE-3ghgfh.jpg";
//                uri = new URI(kopija.replace(" ", "%20"));
//                Log.d(TAG, "doInBackground: "+uri.toString());
//
//                downloadImageURL("148",uri.toString());



//                Log.d(TAG, "doInBackground: "uri.toString());

//                int index = 0;
//                for (String ruta : slikeURL) {
//                    Picasso.with(MainActivity.this)
//                            .load(ruta)
//                            .into(new IndexTarget(index));
//                    index++;
//                }



//                List<Bitmap> yourimages = new DownloadImageTask().execute(URLLIST).get();
                db.replace1(movieModelList1);
        if (ispravnost){

            sharedPreferences = getSharedPreferences("podaci", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putInt("ispravnost", 1);
        }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
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
//            for (Slike s:sveSlike) {
//                String kopija=s.getImage();
//                try {
//                    URI uri = new URI(kopija.replace(" ", "%20"));
//                    downloadImage(s.getId(),s.getImage());
//                    brojac++;
//                    Log.d(TAG, "doInBackground: " + uri.toString() + " - "+String.valueOf(brojac));
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                    MenuFragment fragment = new MenuFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(getSupportFragmentManager().findFragmentByTag("first_frag")).add(R.id.content_main, fragment,"first_frag").addToBackStack("first_frag");//bio replace
                    ft.commit();
                }
            }, 60000);





        }



        public void resetSearch() {
            mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
            lvArtikli.setAdapter(mAdapter);
        }

    }
    public String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }
    public void DownloadImageFromPath(String path,String filenamejpg){
        InputStream in =null;
        Bitmap bmp=null;
        int responseCode = -1;
        int br=0;
        try{

            URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                FileOutputStream outStream = null;

                File dir=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES,
                        File.separator + "YourFolderName/"+filenamejpg);
                String fileName = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES+"/YourFolderName/"+filenamejpg;
                File outFile = new File(fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                br++;
                Log.d(TAG, "DownloadImageFromPath: "+String.valueOf(br));
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        catch(Exception ex){
            Log.e("Exception",ex.toString());
        }
    }
    class IndexTarget implements Target {

        private final int mIndex;

        public IndexTarget(int index){
            this.mIndex = index;
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() +"/img"+mIndex+".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
     public void downloadImageURL(String fileName ,String imagePath){
        String filename = fileName+".jpg";
        String downloadUrlOfImage = "http://nurexport.com/demo/"+imagePath;

        File direct =new File(Environment.DIRECTORY_PICTURES,
                        File.separator + "YourFolderName" + File.separator);


        if (!direct.exists()) {
            direct.mkdir();
            Log.d("DOWNLOADING IMAGE", "dir created for first time");
        }


            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            File.separator + "YourFolderName" + File.separator + filename);


            long id = dm.enqueue(request);



    }
    private boolean isFileExists(String filename){
        String path=Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES+File.separator + "YourFolderName" + File.separator+filename;
        File file=new File(path);
        if (file.isFile()){
            return true;
        }
        else {
            return false;
        }


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