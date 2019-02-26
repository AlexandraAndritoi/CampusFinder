package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.dao.SavedPlaceDAO;
import com.upt.ac.campusfinderapp.model.SavedPlace;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SavedPlacesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<SavedPlace, SavedPlacesViewHolder> adapter;

    private EditText mSearchText;
    private ImageButton mSearchButton;
    private RecyclerView mSavedPlacesList;

//    private SavedPlaceDAO savedPlaceDAO;

    public SavedPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
//        firebaseSavedPlaceSearch();
    }

    @Override
    public void onStop() {
        if(adapter != null) {
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("savedplace");
        FirebaseRecyclerOptions<SavedPlace> options =
                new FirebaseRecyclerOptions.Builder<SavedPlace>()
                        .setQuery(mDatabaseReference, SavedPlace.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<SavedPlace, SavedPlacesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SavedPlacesViewHolder holder, int position, @NonNull SavedPlace model) {
                holder.displaySavedPlace(model.getName());
            }

            @NonNull
            @Override
            public SavedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.saved_places_list_layout, viewGroup, false);

                return new SavedPlacesViewHolder(view);
            }
        };
        adapter.startListening();
//        savedPlaceDAO = new SavedPlaceDAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(com.upt.ac.campusfinderapp.R.layout.fragment_saved_places, container, false);
//        mSearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                firebaseSavedPlaceUserSearch(mSearchText.getText().toString());
////                firebaseUserSearch(mSearchText.getText().toString());
//            }
//        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchText = view.findViewById(com.upt.ac.campusfinderapp.R.id.saved_places_search_bar);
        mSearchButton = view.findViewById(com.upt.ac.campusfinderapp.R.id.search_saved_place_button);
        mSavedPlacesList = view.findViewById(com.upt.ac.campusfinderapp.R.id.saved_places_list);
        mSavedPlacesList.setHasFixedSize(true);
        mSavedPlacesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSavedPlacesList.setAdapter(adapter);
    }

    //    private void firebaseUserSearch(String string) {
//
//        Query query = mDatabaseReference.child("01").startAt(string).endAt(string + "\uf8ff");
//
//        FirebaseRecyclerOptions<SavedPlace> options =
//                new FirebaseRecyclerOptions.Builder<SavedPlace>()
//                        .setQuery(query, SavedPlace.class)
////                        .setLifecycleOwner(lifecycleOwner)
//                        .build();
//
//        FirebaseRecyclerAdapter<SavedPlace, SavedPlacesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SavedPlace, SavedPlacesViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull SavedPlacesViewHolder holder, int position, @NonNull SavedPlace model) {
//                holder.displaySavedPlace(model.getName());
//            }
//
//            @NonNull
//            @Override
//            public SavedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.saved_places_list_layout, viewGroup, false);
//
//                return new SavedPlacesViewHolder(view);
//            }
//        };
//    }

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

//    private void firebaseSavedPlaceSearch(){
//        savedPlaceDAO.firebaseSavedPlaceSearch(getViewLifecycleOwner(), mSavedPlacesList);
//    }
//
//    private void firebaseSavedPlaceUserSearch(String savedPlaceName){
//        savedPlaceDAO.firebaseSavedPlaceUserSearch(getViewLifecycleOwner(), mSavedPlacesList, savedPlaceName);
//    }
}
