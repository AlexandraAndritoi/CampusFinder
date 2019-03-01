package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.model.SavedPlace;

import java.util.ArrayList;

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
    private ArrayList<SavedPlace> savedPlaces;

    private EditText mSearchText;
    private RecyclerView mSavedPlacesRecyclerView;

    public SavedPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
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
        savedPlaces = new ArrayList<>();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.upt.ac.campusfinderapp.R.layout.fragment_saved_places, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchText = view.findViewById(R.id.saved_places_edit_text);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firebaseSearch(s.toString());
            }
        });

        mSavedPlacesRecyclerView = view.findViewById(R.id.saved_places_recycler_view);
        mSavedPlacesRecyclerView.setHasFixedSize(true);
        mSavedPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSavedPlacesRecyclerView.setAdapter(adapter);
    }

    private void firebaseSearch(String string) {
        Query query = mDatabaseReference.orderByChild("name").startAt(string).endAt(string + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    savedPlaces.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        SavedPlace savedPlace = ds.getValue(SavedPlace.class);
                        savedPlaces.add(savedPlace);
                    }

                    SavedPlaceRecyclerAdapter savedPlaceRecyclerAdapter = new SavedPlaceRecyclerAdapter(savedPlaces);
                    mSavedPlacesRecyclerView.setAdapter(savedPlaceRecyclerAdapter);
                    savedPlaceRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
