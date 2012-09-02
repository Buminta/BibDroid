package com.peterdn.bibdroid;

import net.sf.jabref.BibtexEntry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EntryDetailFragment extends Fragment {

    BibtexEntry mItem;

    public EntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParcelableBibtexEntry parcel = getArguments().getParcelable(null);
        mItem = parcel.getBibtexEntry();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.entry_detail)).setText(mItem.toString());
        }
        return rootView;
    }
}
