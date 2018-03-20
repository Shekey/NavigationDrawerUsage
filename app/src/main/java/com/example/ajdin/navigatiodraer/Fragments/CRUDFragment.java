package com.example.ajdin.navigatiodraer.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.helpers.CSVWriter;
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CRUDFragment extends Fragment implements SaveFragment.OnSaveClicked{


    private EditText editText1;
    private Button save_note;
    String naslov;
    String putanja;
    private String textFrom;

    public CRUDFragment() {
        // Required empty public constructor
    }
    @Override
    public void SendTitle(String title) {
    naslov=title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crud, container, false);
        editText1 = view.findViewById(R.id.EditText1);
        save_note = view.findViewById(R.id.save_note);
        if (getArguments()!=null) {
            textFrom = getArguments().getString("text_from");
            putanja = getArguments().getString("putanja");
            editText1.setText(textFrom);

        }
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        save_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText1.getText().toString().isEmpty()){
                  getActivity().getSupportFragmentManager().popBackStack();
                }
                else if (putanja!=null) {
                    SaveFileFromPath(editText1.getText().toString(),putanja);
                    NoteFragment fragment1=new NoteFragment();
                    FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main,fragment1,"NoteFragment").addToBackStack("NoteFragment");
                    ft.commit();

                }
                else {
                    SaveFragment fragment=new SaveFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("text",editText1.getText().toString());
                    fragment.setArguments(bundle);
                    fragment.show(getActivity().getSupportFragmentManager(),"save");
                    fragment.setTargetFragment(CRUDFragment.this,1);


                }

            }
        });


        return view;
    }
    public void Save(String fileName) throws IOException {
        File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "napomene");
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        File file = new File(exportDir,fileName+"//"+timeStamp+".txt");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        PrintWriter writer = new PrintWriter(Environment.getExternalStorageDirectory().getAbsolutePath()+"/napomene/"+fileName+".txt", "UTF-8");
        writer.println(editText1.getText().toString());
        writer.close();
    }
    public void SaveFileFromPath(String text,String pathfile){


        File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "napomene");

        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }



        File file = new File(exportDir,pathfile);

        if (file.delete()){
            Toast.makeText(getActivity(),"Uspjesno uredje fajl", Toast.LENGTH_SHORT);
        }


        try
        {
            file.createNewFile();

            PrintWriter writer = new PrintWriter(Environment.getExternalStorageDirectory().getAbsolutePath()+"/napomene/"+pathfile, "UTF-8");
            writer.write(editText1.getText().toString());
            writer.close();

        }
        catch(Exception sqlEx)
        {
            sqlEx.getStackTrace();
        }
    }
    public void Readfile(String path) throws IOException {

        CSVReader csvReader = new CSVReader(new InputStreamReader
                (new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/napoemene/"+path), 8192 * 32)));
        String[] line;
        long timeStart = System.nanoTime();
        while((line = csvReader.readNext()) != null) {
            editText1.setText(line.toString());

        }
    }



}
