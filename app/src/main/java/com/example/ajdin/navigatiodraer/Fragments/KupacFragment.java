package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class KupacFragment extends DialogFragment {


    private TextView txt;
    private TextView unos;

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
                    FloatingActionButton fab=(FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setVisibility(View.GONE);
                    dismiss();

                }
            }
        });

        return view;
    }

}
