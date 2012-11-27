/*  Copyright (C) 2003-2011 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref;



import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Globals {
	public static final String SIGNATURE =
			"This file was created with JabRef";

	public static final String BIBTEX_STRING = "__string";

	public static final String KEY_FIELD = "bibtexkey";

	public static String META_FLAG = "jabref-meta: ";
	public static String META_FLAG_OLD = "bibkeeper-meta: ";
	public static String ENTRYTYPE_FLAG = "jabref-entrytype: ";
	
    public static final String FILE_FIELD = "file";

    public static String JOURNALS_FILE_BUILTIN = "/resource/journalList.txt";
    
    public static final String BACKUP_EXT = ".bak";

    public static final String TYPE_HEADER = "entrytype";

    public static final String COL_DEFINITION_FIELD_SEPARATOR = "/";

	public static ResourceBundle messages, menuTitles, intMessages;

	public static String VERSION, BUILD, BUILD_DATE;

	public static Locale locale;

	public static final String FILETYPE_PREFS_EXT = "_dir", SELECTOR_META_PREFIX = "selector_",
        PROTECTED_FLAG_META = "protectedFlag",
        LAYOUT_PREFIX = "/resource/layout/", MAC = "Mac OS X",
		DOI_LOOKUP_PREFIX = "http://dx.doi.org/", NONE = "_non__",
		ARXIV_LOOKUP_PREFIX = "http://arxiv.org/abs/",
		FORMATTER_PACKAGE = "net.sf.jabref.export.layout.format.";

	public static String[] ENCODINGS, ALL_ENCODINGS = // (String[])
		// Charset.availableCharsets().keySet().toArray(new
		// String[]{});
		new String[] { "ISO8859_1", "UTF8", "UTF-16", "ASCII", "Cp1250", "Cp1251", "Cp1252",
			"Cp1253", "Cp1254", "Cp1257", "SJIS",
            "KOI8_R", // Cyrillic
			"EUC_JP", // Added Japanese encodings.
			"Big5", "Big5_HKSCS", "GBK", "ISO8859_2", "ISO8859_3", "ISO8859_4", "ISO8859_5",
			"ISO8859_6", "ISO8859_7", "ISO8859_8", "ISO8859_9", "ISO8859_13", "ISO8859_15" };
    public static Map<String,String> ENCODING_NAMES_LOOKUP;

    // String array that maps from month number to month string label:
	public static String[] MONTHS = new String[] { "jan", "feb", "mar", "apr", "may", "jun", "jul",
		"aug", "sep", "oct", "nov", "dec" };

	// Map that maps from month string labels to
	public static Map<String, String> MONTH_STRINGS = new HashMap<String, String>();
	static {
		MONTH_STRINGS.put("jan", "January");
		MONTH_STRINGS.put("feb", "February");
		MONTH_STRINGS.put("mar", "March");
		MONTH_STRINGS.put("apr", "April");
		MONTH_STRINGS.put("may", "May");
		MONTH_STRINGS.put("jun", "June");
		MONTH_STRINGS.put("jul", "July");
		MONTH_STRINGS.put("aug", "August");
		MONTH_STRINGS.put("sep", "September");
		MONTH_STRINGS.put("oct", "October");
		MONTH_STRINGS.put("nov", "November");
		MONTH_STRINGS.put("dec", "December");

		// Build list of encodings, by filtering out all that are not supported
		// on this system:
		List<String> encodings = new ArrayList<String>();
		for (int i = 0; i < ALL_ENCODINGS.length; i++) {
			if (Charset.isSupported(ALL_ENCODINGS[i])) {
				encodings.add(ALL_ENCODINGS[i]);
			}
		}
		ENCODINGS = encodings.toArray(new String[0]);
        // Build a map for translating Java encoding names into common encoding names:
        ENCODING_NAMES_LOOKUP = new HashMap<String,String>();
        ENCODING_NAMES_LOOKUP.put("Cp1250", "windows-1250");
        ENCODING_NAMES_LOOKUP.put("Cp1251", "windows-1251");
        ENCODING_NAMES_LOOKUP.put("Cp1252", "windows-1252");
        ENCODING_NAMES_LOOKUP.put("Cp1253", "windows-1253");
        ENCODING_NAMES_LOOKUP.put("Cp1254", "windows-1254");
        ENCODING_NAMES_LOOKUP.put("Cp1257", "windows-1257");
        ENCODING_NAMES_LOOKUP.put("ISO8859_1", "ISO-8859-1");
        ENCODING_NAMES_LOOKUP.put("ISO8859_2", "ISO-8859-2");
        ENCODING_NAMES_LOOKUP.put("ISO8859_3", "ISO-8859-3");
        ENCODING_NAMES_LOOKUP.put("ISO8859_4", "ISO-8859-4");
        ENCODING_NAMES_LOOKUP.put("ISO8859_5", "ISO-8859-5");
        ENCODING_NAMES_LOOKUP.put("ISO8859_6", "ISO-8859-6");
        ENCODING_NAMES_LOOKUP.put("ISO8859_7", "ISO-8859-7");
        ENCODING_NAMES_LOOKUP.put("ISO8859_8", "ISO-8859-8");
        ENCODING_NAMES_LOOKUP.put("ISO8859_9", "ISO-8859-9");
        ENCODING_NAMES_LOOKUP.put("ISO8859_13", "ISO-8859-13");
        ENCODING_NAMES_LOOKUP.put("ISO8859_15", "ISO-8859-15");
        ENCODING_NAMES_LOOKUP.put("KOI8_R", "KOI8-R");
        ENCODING_NAMES_LOOKUP.put("UTF8", "UTF-8");
        ENCODING_NAMES_LOOKUP.put("UTF-16", "UTF-16");
        ENCODING_NAMES_LOOKUP.put("SJIS", "Shift_JIS");
        ENCODING_NAMES_LOOKUP.put("GBK", "GBK");
        ENCODING_NAMES_LOOKUP.put("Big5_HKSCS", "Big5-HKSCS");
        ENCODING_NAMES_LOOKUP.put("Big5", "Big5");
        ENCODING_NAMES_LOOKUP.put("EUC_JP", "EUC-JP");
        ENCODING_NAMES_LOOKUP.put("ASCII", "US-ASCII");
    }

	public static JabRefPreferences prefs = null;

	public static final String NEWLINE = System.getProperty("line.separator");
    public static final int NEWLINE_LENGTH = System.getProperty("line.separator").length();

	public static String lang(String key, String[] params) {
		String translation = null;
		try {
			if (Globals.messages != null) 
				translation = Globals.messages.getString(key.replaceAll(" ", "_"));
		} catch (MissingResourceException ex) {
			//logger("Warning: could not get translation for \"" + key + "\"");
		}
		if (translation == null)
			translation = key;

		if ((translation != null) && (translation.length() != 0)) {
			translation = translation.replaceAll("_", " ");
			StringBuffer sb = new StringBuffer();
			boolean b = false;
			char c;
			for (int i = 0; i < translation.length(); ++i) {
				c = translation.charAt(i);
				if (c == '%') {
					b = true;
				} else {
					if (!b) {
						sb.append(c);
					} else {
						b = false;
						try {
							int index = Integer.parseInt(String.valueOf(c));
							if (params != null && index >= 0 && index <= params.length)
								sb.append(params[index]);
						} catch (NumberFormatException e) {
							// append literally (for quoting) or insert special
							// symbol
							switch (c) {
							case 'c': // colon
								sb.append(':');
								break;
							case 'e': // equal
								sb.append('=');
								break;
							default: // anything else, e.g. %
								sb.append(c);
							}
						}
					}
				}
			}
			return sb.toString();
		}
		return key;
	}

	public static String lang(String key) {
		return lang(key, (String[]) null);
	}

	public static String lang(String key, String s1) {
		return lang(key, new String[] { s1 });
	}

	public static String lang(String key, String s1, String s2) {
		return lang(key, new String[] { s1, s2 });
	}

	public static String lang(String key, String s1, String s2, String s3) {
		return lang(key, new String[] { s1, s2, s3 });
	}

	public static String menuTitle(String key) {
		String translation = null;
		try {
			if (Globals.messages != null) {
				translation = Globals.menuTitles.getString(key.replaceAll(" ", "_"));
			}
		} catch (MissingResourceException ex) {
			translation = key;
		}
		if ((translation != null) && (translation.length() != 0)) {
			return translation.replaceAll("_", " ");
		} else {
			return key;
		}
	}

	public static String getIntegrityMessage(String key) {
		String translation = null;
		try {
			if (Globals.intMessages != null) {
				translation = Globals.intMessages.getString(key);
			}
		} catch (MissingResourceException ex) {
			translation = key;

			// System.err.println("Warning: could not get menu item translation
			// for \""
			// + key + "\"");
		}
		if ((translation != null) && (translation.length() != 0)) {
			return translation;
		} else {
			return key;
		}
	}

    public static String SPECIAL_COMMAND_CHARS = "\"`^~'c=";

	public static HashMap<String, String> HTML_CHARS = new HashMap<String, String>();
	public static HashMap<String, String> HTMLCHARS = new HashMap<String, String>();
	public static HashMap<String, String> XML_CHARS = new HashMap<String, String>();
	public static HashMap<String, String> ASCII2XML_CHARS = new HashMap<String, String>();
	public static HashMap<String, String> UNICODE_CHARS = new HashMap<String, String>();
	public static HashMap<String, String> RTFCHARS = new HashMap<String, String>();
	public static HashMap<String, String> URL_CHARS = new HashMap<String,String>();


	/**
	 * Returns a reg exp pattern in the form (w1) | (w2) | ...
	 * wi are escaped if no regex search is enabled
	 */
	public static Pattern getPatternForWords(ArrayList<String> words) {
		if ((words == null) || (words.isEmpty()) || (words.get(0).isEmpty()))
			return Pattern.compile("");
		
		boolean regExSearch = Globals.prefs.getBoolean("regExpSearch");
		
		// compile the words to a regex in the form (w1) | (w2) | (w3)
		String searchPattern = "(".concat(regExSearch?words.get(0):Pattern.quote(words.get(0))).concat(")");
		for (int i = 1; i < words.size(); i++) {
			searchPattern = searchPattern.concat("|(").concat(regExSearch?words.get(i):Pattern.quote(words.get(i))).concat(")");
		}

		Pattern pattern;
		if (!Globals.prefs.getBoolean("caseSensitiveSearch")) {
			pattern = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(searchPattern);
		}
		
		return pattern;
	}

}
