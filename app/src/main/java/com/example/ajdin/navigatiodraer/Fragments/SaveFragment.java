package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.helpers.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveFragment extends DialogFragment {

    private String text;
    private String textFrom;

    public interface OnSaveClicked{
        void SendTitle(String title);
    }

    private Button button;
    private EditText text_title;
    public OnSaveClicked onSaveClicked;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        onSaveClicked=(OnSaveClicked)getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: "+e.getMessage() );
        }
    }

    public SaveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save, container, false);
        button = view.findViewById(R.id.button);
        text_title = view.findViewById(R.id.text_title);
        text = getArguments().getString("text");
        if (getArguments().getString("putanja")!=null) {
            text = getArguments().getString("text");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text_title!=null && !text.isEmpty()) {
                    try {
                        Save(text_title.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                getDialog().dismiss();
                NoteFragment fragment1=new NoteFragment();
                FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main,fragment1,"NoteFragment").addToBackStack("NoteFragment");
                ft.commit();
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
        writer.println(text);
        writer.close();
    }
    public void SaveFile(String text,String nazivFajla){


        File exportDir = new File(Environment.getExternalStorageDirectory(), "napomene");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        File file = new File(exportDir,nazivFajla+"---"+timeStamp+".txt");


        try
        {
            file.createNewFile();

            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


            String arrStr[] ={text};
            csvWrite.writeNext(arrStr);
            csvWrite.close();


        }
        catch(Exception sqlEx)
        {
            sqlEx.printStackTrace();
            // Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }

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

            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


            String arrStr[] ={text};
            csvWrite.writeNext(arrStr);
            csvWrite.close();

        }
        catch(Exception sqlEx)
        {
            sqlEx.getStackTrace();
        }
    }
}
