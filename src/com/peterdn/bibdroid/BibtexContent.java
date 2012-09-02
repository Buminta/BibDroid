package com.peterdn.bibdroid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.imports.BibtexParser;

public class BibtexContent {

	private BibtexDatabase _bib;
	
	public List<BibtexEntry> entries;
	
	public BibtexContent(String filename) throws FileNotFoundException, IOException {
		FileReader reader = new FileReader(filename);
		BibtexParser parser = new BibtexParser(reader);

		_bib = parser.parse().getDatabase();
		
		reader.close();
		
		entries = new ArrayList<BibtexEntry>(_bib.getEntries());
	}
	
	
}
