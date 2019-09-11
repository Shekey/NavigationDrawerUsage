package com.example.ajdin.navigatiodraer.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.MainActivity;
import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.HistoryCustomAdapter;
import com.example.ajdin.navigatiodraer.helpers.CSVWriter;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.CartItem;
import com.example.ajdin.navigatiodraer.helpers.CartItemAdapter;
import com.example.ajdin.navigatiodraer.helpers.Constant;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.PreviewModel;
import com.example.ajdin.navigatiodraer.models.Product;
import com.opencsv.CSVReader;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    ListView lstView;
    private CartItemAdapter cartItemAdapter;

    ArrayList<String> files;
    private ArrayList<PreviewModel> model;
    private HistoryCustomAdapter arrayAdapter;
    private TextView tekst;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name, container, false);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        lstView = view.findViewById(R.id.list_history);
        lstView.setEmptyView(view.findViewById(R.id.emptyElement4));

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            files = getList();


            arrayAdapter = new HistoryCustomAdapter(getActivity(), R.layout.history_custom_row,files);
            lstView.setAdapter(arrayAdapter);

        }

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public static final String TAG = "NAME ACTIVITY";

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (id < 0) {
                    Log.d(TAG, "In MainActivity clicked header");
                    return;
                }
                String path = files.get(position);
                try {
                    model = Read(path);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (model !=null && model.size()>0){

                    PreviewFragment fragment=new PreviewFragment();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("listPreview", model);
                    String ime[] = path.split("[0-9_-]");
                    bundle.putString("naziv_racuna",ime[1]);
                    fragment.setArguments(bundle);

                    fragment.show(getActivity().getSupportFragmentManager(),"dijalog_preview");
                    fragment.setTargetFragment(HistoryFragment.this,1);
                }
                else {
                    //TREBA OVDJE URADITI
                    CartFragment fragment = new CartFragment();
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("path", path);
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    navigationView.setCheckedItem(R.id.nav_korpa);
                    editor.commit();
                    ft.replace(R.id.content_main, fragment,"cart_frag");
                    getActivity().setTitle("Korpa");
                    ft.commit();
                    //DO OVDJE

                }
            }
        });

     lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
         @Override
         public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
             new AlertDialog.Builder(getActivity())
                     .setTitle(getResources().getString(R.string.delete_item))
                     .setMessage(getResources().getString(R.string.delete_item_message))
                     .setPositiveButton(getResources().getString(R.string.da), new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             File file=new File(Environment.getExternalStorageDirectory().toString() + "/racunidevice/"+files.get(i));
                             if(file.exists()){
                                 file.delete();
                                 arrayAdapter.remove(arrayAdapter.getItem(i));
                                 arrayAdapter.notifyDataSetChanged();
                             }
                         }

                     })
                     .setNegativeButton(getResources().getString(R.string.Ne), null)
                     .show();
                             return true;

         }
     });

        return view;
    }
        private ArrayList<String> getList() {

            ArrayList<String> inFiles = new ArrayList<String>();
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
            String path = Environment.getExternalStorageDirectory().toString()+"/racunidevice";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);

            String[] files = directory.list();
            Log.d("Files", "Size: "+ files.length);
            int count=files.length-1;
            while (count>0||count==0){
                if (files[count].equals("Artikli.txt") || files[count].equals("desktop.ini")){
                    if (count == 0) {
                        break;
                    }
                    --count;
                }
                inFiles.add(files[count--]);

            }

            return inFiles;
        }


    private static int daysBetween(Date one, Date two) {
        DateTime d1=new DateTime(one);
        DateTime d2=new DateTime(two);
        int days= Days.daysBetween(d1.toLocalDate(), d2.toLocalDate()).getDays();

        return Math.abs(days);


    }



    public ArrayList<PreviewModel> Read(String path) throws ParseException {
        Artikli products;
        ArrayList<PreviewModel> models=new ArrayList<>();
//        Pattern MY_PATTERN = Pattern.compile("\\-(.*)");
//        Matcher m = MY_PATTERN.matcher(path);
//        String s=new String();
//        while (m.find()) {
//            s = m.group(0);
//            // s now contains "BAR"
//        }
        String datum[] = path.split("[a-zA-Z-]");
        String datumpravi=datum[datum.length - 1];


        DateFormat df = new SimpleDateFormat("dd_MM_yyyy_HHmm");
    Date date=new Date();
    Date date2=df.parse(datumpravi);


        try {
            // BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("myFile.csv"));
            CSVReader csvReader = new CSVReader(new InputStreamReader
                    (new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/racunidevice/" + path), 8192 * 32)), ';');
            try {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                int count = 0;
                String[] line;
                long timeStart = System.nanoTime();
                clearCart();
                while ((line = csvReader.readNext()) != null) {
                    Artikli temp = db.getData(line[0]);

                        if (daysBetween(date, date2) > 1) {
                            if (temp!=null) {
                                models.add(new PreviewModel(temp.getNaziv(), line[2], line[1]));
                            }
                            else {
                                Toast.makeText(getActivity(), "Nažalost, neki artikli nedostaju !", Toast.LENGTH_SHORT).show();
                                models.add(new PreviewModel("---", "0.0", "0.0"));

                            }
                        }

                else {
                    if (line.length == 3) {
                        count++;

                        if (temp != null) {

                            products = new Artikli(temp.getNaziv(), temp.getBarkod(), temp.getId(), temp.getSnizeno(),
                                    temp.getStanje(), temp.getDatum(),
                                    temp.getKategorija(), temp.getJedinica(),
                                    temp.getSlike(), temp.getCijena());
                            Cart cart = CartHelper.getCart();
                            cart.add(products, Double.valueOf(line[1]), line[2]);


                        }
                        if (count >= 150000) {
                            break;
                        }
                    } else {
                        count++;
                        if (temp != null) {
                            BigDecimal decimal = temp.getPrice();
                            products = new Artikli(temp.getNaziv(), temp.getBarkod(), temp.getId(), temp.getSnizeno(),
                                    temp.getStanje(), temp.getDatum(),
                                    temp.getKategorija(), temp.getJedinica(),
                                    temp.getSlike(), temp.getCijena());
                            Cart cart = CartHelper.getCart();
                            cart.add(products, Double.valueOf(line[1]), decimal.toString());


                        }
                        if (count >= 150000) {
                            break;
                        }

                    }
                }
                }


                long timeEnd = System.nanoTime();
                System.out.println("Count: " + count);
                System.out.println("Time: " + (timeEnd - timeStart) * 1.0 / 1000000000 + " sec");


            } catch (IOException e) {
                e.printStackTrace();

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
return models;

    }
    private void clearCart(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        SharedPreferences.Editor spreferencesEditor = sharedPreferences.edit();
        spreferencesEditor.remove("path");
        spreferencesEditor.remove("ime");
        spreferencesEditor.commit();
        Cart cart = CartHelper.getCart();
        cart.clear();
        cartItemAdapter = new CartItemAdapter(getActivity());
        cartItemAdapter.updateCartItems(getCartItems(cart));
        cartItemAdapter.notifyDataSetChanged();



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
