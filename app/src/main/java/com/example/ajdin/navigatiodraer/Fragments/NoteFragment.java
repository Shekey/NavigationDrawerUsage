package com.example.ajdin.navigatiodraer.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ajdin.navigatiodraer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment {


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_note_add_white_24px);
        ListView listView=view.findViewById(R.id.note_list);
        listView.setEmptyView(view.findViewById(R.id.emptyElementNote));

        return view;

    }

}
