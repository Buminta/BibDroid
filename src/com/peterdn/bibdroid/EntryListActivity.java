package com.peterdn.bibdroid;


import net.sf.jabref.BibtexEntry;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class EntryListActivity extends FragmentActivity
        implements EntryListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        if (findViewById(R.id.entry_detail_container) != null) {
            mTwoPane = true;
            ((EntryListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.entry_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(BibtexEntry entry) {
        ParcelableBibtexEntry parcelableEntry = new ParcelableBibtexEntry(entry);

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(null, parcelableEntry);
            EntryDetailFragment fragment = new EntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.entry_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, EntryDetailActivity.class);
            detailIntent.putExtra(null, parcelableEntry);
            startActivity(detailIntent);
        }
    }
}
