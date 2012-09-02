package com.peterdn.bibdroid;


import java.io.FileReader;

import java.util.Collection;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.ParserResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.util.Log;


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
        
        try {
			FileReader in = new FileReader("/sdcard/refs.bib");
	        BibtexParser parser = new BibtexParser(in);
	        ParserResult result = parser.parse();
	        BibtexDatabase database = result.getDatabase();
	        Collection<BibtexEntry> entries = database.getEntries();
	        Log.i("BibDroid", ((Integer)entries.size()).toString());
	        for (BibtexEntry entry : entries) {
	        	String key = entry.getCiteKey();
	        	Log.i("BibDroid", key);
	        }
	        in.close();
		} catch (Exception e) {
			Log.e("BibDroid", e.toString());
		}
        
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(EntryDetailFragment.ARG_ITEM_ID, id);
            EntryDetailFragment fragment = new EntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.entry_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, EntryDetailActivity.class);
            detailIntent.putExtra(EntryDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
