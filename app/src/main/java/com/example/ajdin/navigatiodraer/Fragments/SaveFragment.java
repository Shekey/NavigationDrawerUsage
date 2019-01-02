package com.example.ajdin.navigatiodraer.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.ajdin.navigatiodraer.helpers.DatabaseHelper;
import com.example.ajdin.navigatiodraer.services.TimeService;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;
import com.example.ajdin.navigatiodraer.tasks.UploadTaskNapomena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.example.ajdin.navigatiodraer.Fragments.DetailFragment.hideSoftKeyboard;

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
        hideSoftKeyboard(view);

        return view;
    }
    public void Save(String fileName) throws IOException {
        File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "napomene");
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("podaci", Context.MODE_PRIVATE);
        String vlasnik= sharedPreferences.getString("vlasnik","");
        File file = new File(exportDir,vlasnik+"__"+fileName+".txt");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        PrintWriter writer = new PrintWriter( Environment.getExternalStorageDirectory().getAbsolutePath()+"/napomene/"+vlasnik+"__"+fileName+".txt", "UTF-8");
        writer.write(text);
        writer.close();
        ConnectivityManager wifi = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(info.isConnected()){

            new UploadTaskNapomena(DropboxClient.getClient("-moQGOzCYwAAAAAAAAAAZSEoz5K3N_iBvmP9Ns9EelOBx3BlnO5MSDHwbz5js2bK"), file, getActivity().getApplicationContext()).execute();

        }
        else {

            DatabaseHelper db=new DatabaseHelper(getActivity());
            db.addToStack(Environment.getExternalStorageDirectory().getAbsolutePath()+"/napomene/"+vlasnik+"__"+fileName+".txt");
            Intent intent = new Intent(getContext(), TimeService.class);
            getActivity().startService(intent);
        }
    }
}
