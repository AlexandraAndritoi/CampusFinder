package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.upt.ac.campusfinderapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SavedPlacesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText mSearchBar;
    private Button mSearchButton;
    private RecyclerView mSavedPlacesList;

    public SavedPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(com.upt.ac.campusfinderapp.R.layout.fragment_saved_places, container, false);

        mSearchBar = view.findViewById(com.upt.ac.campusfinderapp.R.id.saved_places_search_bar);
        mSearchButton = view.findViewById(com.upt.ac.campusfinderapp.R.id.search_saved_place_button);
        mSavedPlacesList = view.findViewById(com.upt.ac.campusfinderapp.R.id.saved_places_list);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
