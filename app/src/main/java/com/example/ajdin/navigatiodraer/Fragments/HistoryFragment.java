package com.example.ajdin.navigatiodraer.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    ListView lstView;

    ArrayList<String> files;
    private ArrayList<PreviewModel> model;
    private HistoryCustomAdapter arrayAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name, container, false);

        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        lstView = (ListView) view.findViewById(R.id.list_history);
        lstView.setEmptyView(view.findViewById(R.id.emptyElement4));


//        if (!tokenExists()){
//
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//
//            startActivity(intent);
//
//        }

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            files = getList();

            //lv_arr = (String[]) listOfStrings.toArray();
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, files) {
//
//                @NonNull
//                @Override
//                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                    View view = super.getView(position, convertView, parent);
//                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//                    text1.setPadding(16, 0, 16, 0);
//                    text2.setPadding(16, 0, 16, 0);
//                    text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//
//                    text1.setAllCaps(true);
//                    text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                    text2.setTextColor(Color.parseColor("#867e7e"));
//                    String ime[] = files.get(position).split("[0-9_-]");
//                    String datum[] = files.get(position).split("[a-zA-Z-]");
//
//                    text2.setText(datum[datum.length - 1]);
//                    text1.setText(ime[0]);
//                    return view;
//
//
//                }
//            };

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
                    bundle.putSerializable("listPreview",(Serializable) model);
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
                    NavigationView navigationView =(NavigationView)getActivity().findViewById(R.id.nav_view);
                    navigationView.setCheckedItem(R.id.nav_korpa);
                    editor.commit();
                    ft.replace(R.id.content_main, fragment);
                    ft.commit();
                    //DO OVDJE

                }
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
        Pattern MY_PATTERN = Pattern.compile("\\-(.*)");
        Matcher m = MY_PATTERN.matcher(path);
        String s=new String();
        while (m.find()) {
            s = m.group(0);
            // s now contains "BAR"
        }

    String input = s, extracted;
        DateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm");
    extracted = input.substring(3,19);
    Date date=new Date();
    Date date2=df.parse(extracted);


        try {
            // BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("myFile.csv"));
            CSVReader csvReader = new CSVReader(new InputStreamReader
                    (new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/racunidevice/" + path), 8192 * 32)), ';');
            try {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                int count = 0;
                String[] line;
                long timeStart = System.nanoTime();
                while ((line = csvReader.readNext()) != null) {
                    Artikli temp = db.getData(line[0]);
                    if (daysBetween(date, date2) > 1) {
                        models.add(new PreviewModel(temp.getNaziv(), line[2], line[1]));
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



}
