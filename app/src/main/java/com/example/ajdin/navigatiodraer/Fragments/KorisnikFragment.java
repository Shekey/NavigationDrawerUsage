package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KorisnikFragment extends Fragment {


    private EditText textView;
    private EditText licencaedit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button snimiLicencu;

    public KorisnikFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_korisnik, container, false);
        Button button=view.findViewById(R.id.butonsnimi);
        snimiLicencu = view.findViewById(R.id.snimiLicencu);
        textView = view.findViewById(R.id.editText);
        licencaedit = view.findViewById(R.id.licencaedit);
        sharedPreferences = getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
      String vlasnik=sharedPreferences.getString("vlasnik","");
      String licenca=sharedPreferences.getString("licenca","");
      if (!vlasnik.equals("")){
          textView.setText(vlasnik);
      }
        if (!licenca.equals("")){
            licencaedit.setText(licenca);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(textView.getText().toString())) {
                    textView.setError("Unesite ime kupca");
                    return;
                }
                else {

                    editor.putString("vlasnik", textView.getText().toString());
                    editor.commit();

                }
            }
        });
        snimiLicencu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(licencaedit.getText().toString())) {
                    licencaedit.setError("Unesite licencu");
                    return;
                }
                else {

                    editor.putString("licenca", licencaedit.getText().toString());
                    editor.commit();
                    MenuFragment fragment = new MenuFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, fragment,"first_frag").addToBackStack("first_frag");
                    ft.commit();

                }
            }
        });


        return view;
    }

}
