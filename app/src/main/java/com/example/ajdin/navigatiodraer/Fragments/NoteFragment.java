package com.example.ajdin.navigatiodraer.Fragments;


import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment implements SaveFragment.OnSaveClicked {

    String naslov;
    ArrayList<String> files;
    String text;
    private FloatingActionButton fab;

    public NoteFragment() {
        // Required empty public constructor
    }
    @Override
    public void SendTitle(String title) {
        naslov=title;
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
        fab.setImageResource(R.drawable.ic_note_add_white_24px);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CRUDFragment fragment=new CRUDFragment();
                FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main,fragment,"CRUDFragment").addToBackStack("CRUDFragment");
                ft.commit();

//
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        files=getList();
        fab.setImageResource(R.drawable.ic_note_add_white_24px);
        ListView listView=view.findViewById(R.id.note_list);
        listView.setEmptyView(view.findViewById(R.id.emptyElementNote));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                // do whatever you want with this text view
                textView.setTextSize(27);

                return view;
            }
        };
        listView.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                    text = Readfile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CRUDFragment fragment=new CRUDFragment();
                FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putString("text_from",text);
                bundle.putString("putanja",path);
                fragment.setArguments(bundle);
                ft.replace(R.id.content_main,fragment,"CRUDFragment").addToBackStack("CRUDFragment");
                ft.commit();

            }
        });

        return view;

    }
    private ArrayList<String> getList() {

        ArrayList<String> inFiles = new ArrayList<String>();
        File exportDir = new File(Environment.getExternalStorageDirectory(), "napomene");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
            String path = Environment.getExternalStorageDirectory().toString() + "/napomene";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);

            String[] files = directory.list();
            Log.d("Files", "Size: " + files.length);
            int count = files.length - 1;
            while (count > 0 || count == 0) {

                inFiles.add(files[count--]);

            }



        return inFiles;
    }
    public String Readfile(String path) throws IOException {

        CSVReader csvReader = new CSVReader(new InputStreamReader
                (new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/napomene/"+path), 8192 * 32)));
        String[] line;
        String text="";
        long timeStart = System.nanoTime();
        while((line = csvReader.readNext()) != null) {
           text+=line[0];

        }
        return text;
    }
}
