package com.example.ajdin.navigatiodraer.Fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.adapters.PreviewAdapter;
import com.example.ajdin.navigatiodraer.helpers.Cart;
import com.example.ajdin.navigatiodraer.helpers.CartHelper;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.models.PreviewModel;
import com.example.ajdin.navigatiodraer.models.Product;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ajdin on 28.3.2018..
 */

public class PreviewFragment extends DialogFragment {


    private TextView tekst;

    public PreviewFragment() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_preview, container, false);
        ListView preview=view.findViewById(R.id.preview);
        tekst = (TextView) view.findViewById(R.id.tekst);
        Bundle bundle=getArguments();
        if (bundle!=null){
            ArrayList<PreviewModel> models=(ArrayList)bundle.getSerializable("listPreview");
            PreviewAdapter adapter = new PreviewAdapter(getContext().getApplicationContext(), R.layout.row_preview, models);

            preview.setAdapter(adapter);

        }


        return view;
    }
//    String input = dat, extracted;
//    extracted = input.substring(3,16);
//    Date date = new Date();
//    DateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
//    Date date1 = df.parse(dat);


}