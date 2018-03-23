package com.example.ajdin.navigatiodraer.Fragments;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.MainActivity;
import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.helpers.CSVWriter;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.CartItemAdapter;
import com.example.ajdin.navigatiodraer.helpers.Constant;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {


    private TextView tvTotalPrice;
    SharedPreferences sharedPreferences;
    private Button bZavrsi;
    private String ime;
    private Button clear;

    public CartFragment() {
        // Required empty public constructor
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ListView lvProducts = (ListView)view.findViewById(R.id.listCart);
        //  lvProducts.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.cart_header, lvProducts, false));
        //
        if(getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().getActionBar().setDisplayShowHomeEnabled(true);
        }
        final Cart cart = CartHelper.getCart();
        final CartItemAdapter cartItemAdapter = new CartItemAdapter(getActivity());

        tvTotalPrice=(TextView)view.findViewById(R.id.tvTotalPrice);
        cartItemAdapter.updateCartItems(getCartItems(cart));
       tvTotalPrice.setText(String.valueOf(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)+" "+ Constant.CURRENCY));
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        lvProducts.setAdapter(cartItemAdapter);
        lvProducts.setEmptyView(view.findViewById(R.id.emptyElement));
        sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        ime=sharedPreferences.getString("ime","");
        clear = view.findViewById(R.id.clearAll);
        lvProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position<0) {
                    return false;
                }
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.delete_item))
                        .setMessage(getResources().getString(R.string.delete_item_message))
                        .setPositiveButton(getResources().getString(R.string.da), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<CartItem> cartItems = getCartItems(cart);

                                cart.remove(cartItems.get(position).getProduct());
                                cartItems.remove(position);
                                cartItemAdapter.updateCartItems(cartItems);
                                cartItemAdapter.notifyDataSetChanged();
                                tvTotalPrice.setText(String.valueOf(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)+" "+Constant.CURRENCY));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Ne), null)
                        .show();
                return false;
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.zavrsetak_svega))
                        .setMessage(getResources().getString(R.string.cisti_sve))
                        .setPositiveButton(getResources().getString(R.string.da), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor spreferencesEditor = sharedPreferences.edit();
                                spreferencesEditor.clear();
                                spreferencesEditor.commit();
                                cart.clear();
                                cartItemAdapter.updateCartItems(getCartItems(cart));
                                cartItemAdapter.notifyDataSetChanged();
                                tvTotalPrice.setText(String.valueOf(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)+" "+Constant.CURRENCY));

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Ne), null)
                        .show();

            }
        });

        bZavrsi = view.findViewById(R.id.zavrsi);

        bZavrsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartItemAdapter.getCount()==0){
                    Toast.makeText(getActivity(), "Nazalost, niste nista unijeli", Toast.LENGTH_SHORT).show();
                    return;
                }

                ConnectivityManager wifi = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info=wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (info.isConnected()) {
                    if (sharedPreferences.getString("ime", "") != "" && sharedPreferences.getString("ime", "") != null) {
                        String zadropBox = exportDB(getCartItems(cart), cartItemAdapter.getCount(), sharedPreferences.getString("ime", ""));
                        SharedPreferences.Editor spreferencesEditor = sharedPreferences.edit();
                        spreferencesEditor.clear();
                        spreferencesEditor.commit();
                        Toast.makeText(getActivity(), "Uspjesno kreiran racun", Toast.LENGTH_SHORT).show();
                        cart.clear();
                        cartItemAdapter.updateCartItems(getCartItems(cart));
                        cartItemAdapter.notifyDataSetChanged();
                        tvTotalPrice.setText(String.valueOf(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)+" "+Constant.CURRENCY));

                        File file = new File(zadropBox);
                        new UploadTask(DropboxClient.getClient("aLRppJLoiTAAAAAAAAAADkJLNGAbqPzA0hZ_oVvVlEhNiyiYA94B9ndRUrIXxV8G"), file, getActivity().getApplicationContext()).execute();


                    } else {


                        exportDBold(getCartItems(cart), cartItemAdapter.getCount(), sharedPreferences.getString("path", ""));


                        Toast.makeText(getActivity(), "Uspjesno kreiran racun", Toast.LENGTH_SHORT).show();
                        cart.clear();
                        cartItemAdapter.updateCartItems(getCartItems(cart));
                        cartItemAdapter.notifyDataSetChanged();
                        tvTotalPrice.setText(String.valueOf(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP)+" "+Constant.CURRENCY));
//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    intent.putExtra("pathFile",Environment.getExternalStorageDirectory().toString()+"/racunidevice/"+sharedPreferences.getString("path",""));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                        String putanja = Environment.getExternalStorageDirectory().toString() + "/racunidevice/" + sharedPreferences.getString("path", "");
                        File file = new File(putanja);
                        new UploadTask(DropboxClient.getClient("aLRppJLoiTAAAAAAAAAADkJLNGAbqPzA0hZ_oVvVlEhNiyiYA94B9ndRUrIXxV8G"), file,getActivity(). getApplicationContext()).execute();
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return;


                    }
                }
                else {
                    Toast.makeText(getActivity(), "Morate ukljuciti WIFI", Toast.LENGTH_SHORT).show();

                }
                //  helper.InsertIntoRacun(cartItemAdapter, cartItemAdapter.getCount());




//                SharedPreferences shared=getSharedPreferences("path",Context.MODE_PRIVATE);
//                shared.edit().clear();
//                shared.edit().commit();




            }
        });

        return view;
    }
    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItems = new ArrayList<CartItem>();


        Map<Saleable, Double> itemMap = cart.getItemWithQuantity();


        for (Map.Entry<Saleable, Double> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Product) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }


        return cartItems;
    }

    private String exportDB(List<CartItem> items, int size, String imep) {

        File dbFile=getActivity().getDatabasePath("NUR.db");
        DatabaseHelper dbhelper = new DatabaseHelper(getActivity().getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "racuni");
        File exportDir2 = new File(Environment.getExternalStorageDirectory(), "racunidevice");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        if (!exportDir2.exists())
        {
            exportDir2.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("dd MM yyyy HH:mm").format(Calendar.getInstance().getTime());

        File file = new File(exportDir,imep+"---"+timeStamp+".txt");
        File file2 = new File(exportDir2,imep+"---"+timeStamp+".txt");

        try
        {
            file.createNewFile();
            file2.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            CSVWriter csvWrite2 = new CSVWriter(new FileWriter(file2));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM Artikli",null);

            for (int i=0;i<size;i++){

                String arrStr[] ={items.get(i).getProduct().getBarkod(), String.valueOf(items.get(i).getQuantity()), String.valueOf(items.get(i).getProduct().getPrice())};
                csvWrite.writeNext(arrStr);
                csvWrite2.writeNext(arrStr);


            }

            csvWrite.close();
            csvWrite2.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            sqlEx.printStackTrace();
            // Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
        return Environment.getExternalStorageDirectory().toString()+ "/racunidevice/"+
                imep+"---"+timeStamp+".txt";

    }

    private void exportDBold(List<CartItem> cartItems, int count, String pathfile) {

        File dbFile=getActivity().getDatabasePath("NUR.db");
        DatabaseHelper dbhelper = new DatabaseHelper(getActivity().getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "racuni");
        File exportDir2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "racunidevice");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        if (!exportDir2.exists())
        {
            exportDir2.mkdirs();
        }


        File file = new File(exportDir,pathfile);

        if (file.delete()){
            Toast.makeText(getActivity(),"izbrisan fajl", Toast.LENGTH_SHORT);
        }
        File file2 = new File(exportDir,pathfile);
        File file3 = new File(exportDir2,pathfile);

        try
        {
            file2.createNewFile();
            file3.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file2));
            CSVWriter csvWrite2 = new CSVWriter(new FileWriter(file3));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            // Cursor curCSV = db.rawQuery("SELECT * FROM Artikli",null);

            for (int i=0;i<count;i++){

                String arrStr[] ={cartItems.get(i).getProduct().getBarkod(), String.valueOf(cartItems.get(i).getQuantity()), String.valueOf(cartItems.get(i).getProduct().getPrice())};

                csvWrite.writeNext(arrStr);
                csvWrite2.writeNext(arrStr);

            }

            csvWrite.close();
            csvWrite2.close();

        }
        catch(Exception sqlEx)
        {
            sqlEx.getStackTrace();
        }


    }

}
