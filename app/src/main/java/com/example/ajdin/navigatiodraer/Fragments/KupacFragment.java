package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.helpers.CSVWriter;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.CartItemAdapter;
import com.example.ajdin.navigatiodraer.helpers.Constant;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.services.TimeService;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class KupacFragment extends DialogFragment {


    private TextView txt;
    private TextView unos;
    private Cart cart;
    private CartItemAdapter cartItemAdapter;

    public KupacFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kupac, container, false);
        Button btn=view.findViewById(R.id.dodaj_kupca);
        txt = view.findViewById(R.id.txt);
        unos = view.findViewById(R.id.unos);
        txt.setHint("Unesite ime kupca !");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(unos.getText().toString())) {
                    unos.setError("Unesite ime kupca");
                    return;
                } else {

                    SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("ime", unos.getText().toString());
                    editor.commit();
                    android.support.v4.app.Fragment CurrentFragment=getActivity().getSupportFragmentManager().findFragmentById(R.id.content_main);
                    if (CurrentFragment.getTag().equals("cart_frag")){
                        cart = CartHelper.getCart();
                        cartItemAdapter = new CartItemAdapter(getActivity());
                        String imefajla=sharedPreferences.getString("ime", "").replace(" ","");
                        String zadropBox = exportDB(getCartItems(cart), getCartItems(cart).size(), imefajla);
                        SharedPreferences.Editor spreferencesEditor = sharedPreferences.edit();
                        spreferencesEditor.remove("ime");
                        spreferencesEditor.commit();
                        File file = new File(zadropBox);
                        ConnectivityManager wifi = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info=wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (!info.isConnected()) {
                            DatabaseHelper db = new DatabaseHelper(getActivity());
                            db.addToStack(zadropBox);
                            Intent intent = new Intent(getContext(), TimeService.class);
                            getActivity().startService(intent);
                        }
                        else {
                            new UploadTask(DropboxClient.getClient("-moQGOzCYwAAAAAAAAAAZSEoz5K3N_iBvmP9Ns9EelOBx3BlnO5MSDHwbz5js2bK"), file, getActivity().getApplicationContext()).execute();
                        }

                        clearCart();

                    }
                    dismiss();

                }
            }
        });

        return view;
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

        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HHmm").format(Calendar.getInstance().getTime());
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        String vlasnik= sharedPreferences.getString("vlasnik","");
        File file = new File(exportDir,vlasnik+"-"+ imep+"-"+timeStamp+".txt");
        File file2 = new File(exportDir2,vlasnik+"-"+imep+"-"+timeStamp+".txt");

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
        }

        return Environment.getExternalStorageDirectory().toString()+ "/racunidevice/"+
                vlasnik+"-"+imep+"-"+timeStamp+".txt";

    }

    private void clearCart(){
        cart.clear();
        cartItemAdapter.updateCartItems(getCartItems(cart));
        cartItemAdapter.notifyDataSetChanged();


        MenuFragment fragment=new MenuFragment();
        String tag="first_frag";
        NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_proizvodi);
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(getActivity().getSupportFragmentManager().findFragmentByTag("cart_frag"));
        ft.add(R.id.content_main, fragment,tag).addToBackStack(tag);
        ft.commit();

    }

}
