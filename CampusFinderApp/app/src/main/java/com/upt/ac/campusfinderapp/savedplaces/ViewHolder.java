package com.upt.ac.campusfinderapp.savedplaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.upt.ac.campusfinderapp.savedplaces.dummy.DummyContent;

public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public DummyContent.DummyItem mItem;

    public ViewHolder(View view) {
        super(view);
        mView = view;
        mIdView = view.findViewById(com.upt.ac.campusfinderapp.R.id.item_number);
        mContentView = view.findViewById(com.upt.ac.campusfinderapp.R.id.content);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mContentView.getText() + "'";
    }
}