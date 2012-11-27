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

// created by : Morten O. Alver 2003

package net.sf.jabref;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.SaveSession;

/**
 * utility functions
 */
public class Util {

	final static NumberFormat idFormat;

    public static Pattern remoteLinkPattern = Pattern.compile("[a-z]+://.*");

    public static int MARK_COLOR_LEVELS = 6,
            MAX_MARKING_LEVEL = MARK_COLOR_LEVELS-1,
            IMPORT_MARK_LEVEL = MARK_COLOR_LEVELS;
    public static Pattern markNumberPattern = Pattern.compile(Globals.prefs.MARKING_WITH_NUMBER_PATTERN);


	static {
		idFormat = NumberFormat.getInstance();
		idFormat.setMinimumIntegerDigits(8);
		idFormat.setGroupingUsed(false);
	}

	public static int getMinimumIntegerDigits(){
		return idFormat.getMinimumIntegerDigits();
	}

	public static void bool(boolean b) {
		if (b)
			System.out.println("true");
		else
			System.out.println("false");
	}

	public static void pr(String s) {
		System.out.println(s);
	}

	public static void pr_(String s) {
		System.out.print(s);
	}

	public static String nCase(String s) {
		// Make first character of String uppercase, and the
		// rest lowercase.
		if (s.length() > 1)
			return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
		else
			return s.toUpperCase();

	}

	public static String checkName(String s) {
		// Append '.bib' to the string unless it ends with that.
		if (s.length() < 4 || !s.substring(s.length() - 4).equalsIgnoreCase(".bib")) {
			return s + ".bib";
		}
		return s;
	}

	private static int idCounter = 0;

	public synchronized static String createNeutralId() {
		return idFormat.format(idCounter++);
	}

	/**
	 * This method translates a field or string from Bibtex notation, with
	 * possibly text contained in " " or { }, and string references,
	 * concatenated by '#' characters, into Bibkeeper notation, where string
	 * references are enclosed in a pair of '#' characters.
	 */
	public static String parseField(String content) {
		
		if (content.length() == 0)
			return content;
		
		String[] strings = content.split("#");
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < strings.length; i++){
			String s = strings[i].trim();
			if (s.length() > 0){
				char c = s.charAt(0);
				// String reference or not?
				if (c == '{' || c == '"'){
					result.append(shaveString(strings[i]));	
				} else {
					// This part should normally be a string reference, but if it's
					// a pure number, it is not.
					String s2 = shaveString(s);
					try {
						Integer.parseInt(s2);
						// If there's no exception, it's a number.
						result.append(s2);
					} catch (NumberFormatException ex) {
						// otherwise append with hashes...
						result.append('#').append(s2).append('#');
					}
				}
			}
		}
		return result.toString();
	}

	/**
	 * Will return the publication date of the given bibtex entry in conformance
	 * to ISO 8601, i.e. either YYYY or YYYY-MM.
	 * 
	 * @param entry
	 * @return will return the publication date of the entry or null if no year
	 *         was found.
	 */
	public static String getPublicationDate(BibtexEntry entry) {

		Object o = entry.getField("year");
		if (o == null)
			return null;

		String year = toFourDigitYear(o.toString());

		o = entry.getField("month");
		if (o != null) {
			int month = Util.getMonthNumber(o.toString());
			if (month != -1) {
				return year + "-" + (month + 1 < 10 ? "0" : "") + (month + 1);
			}
		}
		return year;
	}

	public static String shaveString(String s) {
		// returns the string, after shaving off whitespace at the beginning
		// and end, and removing (at most) one pair of braces or " surrounding
		// it.
		if (s == null)
			return null;
		char ch, ch2;
		int beg = 0, end = s.length();
		// We start out assuming nothing will be removed.
		boolean begok = false, endok = false;
		while (!begok) {
			if (beg < s.length()) {
				ch = s.charAt(beg);
				if (Character.isWhitespace(ch))
					beg++;
				else
					begok = true;
			} else
				begok = true;

		}
		while (!endok) {
			if (end > beg + 1) {
				ch = s.charAt(end - 1);
				if (Character.isWhitespace(ch))
					end--;
				else
					endok = true;
			} else
				endok = true;
		}

		if (end > beg + 1) {
			ch = s.charAt(beg);
			ch2 = s.charAt(end - 1);
			if (((ch == '{') && (ch2 == '}')) || ((ch == '"') && (ch2 == '"'))) {
				beg++;
				end--;
			}
		}
		s = s.substring(beg, end);
		return s;
	}

	/**
	 * This method returns a String similar to the one passed in, except that it
	 * is molded into a form that is acceptable for bibtex.
	 * 
	 * Watch-out that the returned string might be of length 0 afterwards.
	 * 
	 * @param key
	 *            mayBeNull
	 */
	public static String checkLegalKey(String key) {
		if (key == null)
			return null;
        if (!Globals.prefs.getBoolean("enforceLegalBibtexKey")) {
            // User doesn't want us to enforce legal characters. We must still look
            // for whitespace and some characters such as commas, since these would
            // interfere with parsing:
            StringBuilder newKey = new StringBuilder();
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                if (!Character.isWhitespace(c) && (c != '{') && (c != '\\') && (c != '"')
                    && (c != '}') && (c != ','))
                    newKey.append(c);
            }
            return newKey.toString();

        }
		StringBuilder newKey = new StringBuilder();
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!Character.isWhitespace(c) && (c != '#') && (c != '{') && (c != '\\') && (c != '"')
				&& (c != '}') && (c != '~') && (c != ',') && (c != '^') && (c != '\''))
				newKey.append(c);
		}

		// Replace non-english characters like umlauts etc. with a sensible
		// letter or letter combination that bibtex can accept.
		String newKeyS = replaceSpecialCharacters(newKey.toString());

		return newKeyS;
	}

	/**
	 * Replace non-english characters like umlauts etc. with a sensible letter
	 * or letter combination that bibtex can accept. The basis for replacement
	 * is the HashMap GLobals.UNICODE_CHARS.
	 */
	public static String replaceSpecialCharacters(String s) {
		for (Map.Entry<String, String> chrAndReplace : Globals.UNICODE_CHARS.entrySet()){
			s = s.replaceAll(chrAndReplace.getKey(), chrAndReplace.getValue());
		}
		return s;
	}

	static public String _wrap2(String in, int wrapAmount) {
		// The following line cuts out all whitespace and replaces them with
		// single
		// spaces:
		// in = in.replaceAll("[ ]+"," ").replaceAll("[\\t]+"," ");
		// StringBuffer out = new StringBuffer(in);
		StringBuffer out = new StringBuffer(in.replaceAll("[ \\t\\r]+", " "));

		int p = in.length() - wrapAmount;
		int lastInserted = -1;
		while (p > 0) {
			p = out.lastIndexOf(" ", p);
			if (p <= 0 || p <= 20)
				break;
			int lbreak = out.indexOf("\n", p);
			System.out.println(lbreak + " " + lastInserted);
			if ((lbreak > p) && ((lastInserted >= 0) && (lbreak < lastInserted))) {
				p = lbreak - wrapAmount;
			} else {
				out.insert(p, "\n\t");
				lastInserted = p;
				p -= wrapAmount;
			}
		}
		return out.toString();
	}

	static public String wrap2(String in, int wrapAmount) {
		return net.sf.jabref.imports.FieldContentParser.wrap(in, wrapAmount);
	}

	static public String __wrap2(String in, int wrapAmount) {
		// The following line cuts out all whitespace except line breaks, and
		// replaces
		// with single spaces. Line breaks are padded with a tab character:
		StringBuffer out = new StringBuffer(in.replaceAll("[ \\t\\r]+", " "));

		int p = 0;
		// int lastInserted = -1;
		while (p < out.length()) {
			int q = out.indexOf(" ", p + wrapAmount);
			if ((q < 0) || (q >= out.length()))
				break;
			int lbreak = out.indexOf("\n", p);
			// System.out.println(lbreak);
			if ((lbreak > p) && (lbreak < q)) {
				p = lbreak + 1;
				int piv = lbreak + 1;
				if ((out.length() > piv) && !(out.charAt(piv) == '\t'))
					out.insert(piv, "\n\t");

			} else {
				// System.out.println(q+" "+out.length());
				out.deleteCharAt(q);
				out.insert(q, "\n\t");
				p = q + 1;
			}
		}
		return out.toString();// .replaceAll("\n", "\n\t");
	}

	/**
	 * Takes a String array and returns a string with the array's elements
	 * delimited by a certain String.
	 * 
	 * @param strs
	 *            String array to convert.
	 * @param delimiter
	 *            String to use as delimiter.
	 * @return Delimited String.
	 */
	public static String stringArrayToDelimited(String[] strs, String delimiter) {
		if ((strs == null) || (strs.length == 0))
			return "";
		if (strs.length == 1)
			return strs[0];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length - 1; i++) {
			sb.append(strs[i]);
			sb.append(delimiter);
		}
		sb.append(strs[strs.length - 1]);
		return sb.toString();
	}

	/**
	 * Takes a delimited string, splits it and returns
	 * 
	 * @param names
	 *            a <code>String</code> value
	 * @return a <code>String[]</code> value
	 */
	public static String[] delimToStringArray(String names, String delimiter) {
		if (names == null)
			return null;
		return names.split(delimiter);
	}

    /**
	 * Make sure an URL is "portable", in that it doesn't contain bad characters
	 * that break the open command in some OSes.
	 * 
	 * A call to this method will also remove \\url{} enclosings and clean doi links.
	 * 
	 * Old Version can be found in CVS version 114 of Util.java.
	 * 
	 * @param link
	 *            The URL to sanitize.
	 * @return Sanitized URL
	 */
	public static String sanitizeUrl(String link) {

	    // First check if it is enclosed in \\url{}. If so, remove
        // the wrapper.
        if (link.startsWith("\\url{") && link.endsWith("}"))
            link = link.substring(5, link.length() - 1);

        if (link.matches("^doi:/*.*")){
            // Remove 'doi:'
            link = link.replaceFirst("^doi:/*", "");
            link = Globals.DOI_LOOKUP_PREFIX + link;
        }
        
        /*
         * Poor man's DOI detection
         * 
         * Fixes
         * https://sourceforge.net/tracker/index.php?func=detail&aid=1709449&group_id=92314&atid=600306
         */
        if (link.startsWith("10.")) {
            link = Globals.DOI_LOOKUP_PREFIX + link;
        }
	    
		link = link.replaceAll("\\+", "%2B");

		try {
			link = URLDecoder.decode(link, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		/**
		 * Fix for: [ 1574773 ] sanitizeUrl() breaks ftp:// and file:///
		 * 
		 * http://sourceforge.net/tracker/index.php?func=detail&aid=1574773&group_id=92314&atid=600306
		 */
		try {
			return new URI(null, link, null).toASCIIString();
		} catch (URISyntaxException e) {
			return link;
		}
	}

	public static Map<BibtexEntry, List<File>> findAssociatedFiles(Collection<BibtexEntry> entries, Collection<String> extensions, Collection<File> directories){
		HashMap<BibtexEntry, List<File>> result = new HashMap<BibtexEntry, List<File>>();
	
		// First scan directories
		Set<File> filesWithExtension = findFiles(extensions, directories);
		
		// Initialize Result-Set
		for (BibtexEntry entry : entries){
			result.put(entry, new ArrayList<File>());
		}

        boolean exactOnly = Globals.prefs.getBoolean("autolinkExactKeyOnly");
        // Now look for keys
		nextFile:
		for (File file : filesWithExtension){
			
			String name = file.getName();
            int dot = name.lastIndexOf('.');
            // First, look for exact matches:
            for (BibtexEntry entry : entries){
                String citeKey = entry.getCiteKey();
                if ((citeKey != null) && (citeKey.length() > 0)) {
                    if (dot > 0) {
                        if (name.substring(0, dot).equals(citeKey)) {
                            result.get(entry).add(file);
                            continue nextFile;
                        }
                    }
                }
            }
            // If we get here, we didn't find any exact matches. If non-exact
            // matches are allowed, try to find one:
            if (!exactOnly) {
                for (BibtexEntry entry : entries){
                    String citeKey = entry.getCiteKey();
                    if ((citeKey != null) && (citeKey.length() > 0)) {
                        if (name.startsWith(citeKey)){
                            result.get(entry).add(file);
                            continue nextFile;
                        }
                    }
                }
            }
		}
		
		return result;
	}
	
	public static Set<File> findFiles(Collection<String> extensions, Collection<File> directories) {
		Set<File> result = new HashSet<File>();
		
		for (File directory : directories){
			result.addAll(findFiles(extensions, directory));
		}
		
		return result;
	}

	private static Collection<? extends File> findFiles(Collection<String> extensions, File directory) {
		Set<File> result = new HashSet<File>();
		
		File[] children = directory.listFiles();
		if (children == null)
			return result; // No permission?

		for (File child : children){
			if (child.isDirectory()) {
				result.addAll(findFiles(extensions, child));
			} else {
				
				String extension = getFileExtension(child);
					
				if (extension != null){
					if (extensions.contains(extension)){
						result.add(child);
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Returns the extension of a file or null if the file does not have one (no . in name).
	 * 
	 * @param file
	 * 
	 * @return The extension, trimmed and in lowercase.
	 */
	public static String getFileExtension(File file) {
		String name = file.getName();
		int pos = name.lastIndexOf('.');
		String extension = ((pos >= 0) && (pos < name.length() - 1)) ? name.substring(pos + 1)
			.trim().toLowerCase() : null;
		return extension;
	}

	/**
	 * Removes optional square brackets from the string s
	 *
	 * @param s
	 * @return
	 */
	public static String stripBrackets(String s) {
		int beginIndex = (s.length() > 0 && s.charAt(0) == '[' ? 1 : 0);
		int endIndex = (s.endsWith("]") ? s.length() - 1 : s.length());
		return s.substring(beginIndex, endIndex);
	}

	public static ArrayList<String[]> parseMethodsCalls(String calls) throws RuntimeException {

		ArrayList<String[]> result = new ArrayList<String[]>();

		char[] c = calls.toCharArray();

		int i = 0;

		while (i < c.length) {

			int start = i;
			if (Character.isJavaIdentifierStart(c[i])) {
				i++;
				while (i < c.length && (Character.isJavaIdentifierPart(c[i]) || c[i] == '.')) {
					i++;
				}
				if (i < c.length && c[i] == '(') {

					String method = calls.substring(start, i);

					// Skip the brace
					i++;

					if (i < c.length){
						if (c[i] == '"'){
							// Parameter is in format "xxx"

							// Skip "
							i++;

							int startParam = i;
							i++;
		                    boolean escaped = false;
							while (i + 1 < c.length &&
                                    !(!escaped && c[i] == '"' && c[i + 1] == ')')) {
                                if (c[i] == '\\') {
                                    escaped = !escaped;
                                }
                                else
                                    escaped = false;
                                i++;

                            }

							String param = calls.substring(startParam, i);
		
							result.add(new String[] { method, param });
						} else {
							// Parameter is in format xxx

							int startParam = i;

							while (i < c.length && c[i] != ')') {
								i++;
							}

							String param = calls.substring(startParam, i);

							result.add(new String[] { method, param });


						}
					} else {
						// Incorrecly terminated open brace
						result.add(new String[] { method });
					}
				} else {
					String method = calls.substring(start, i);
					result.add(new String[] { method });
				}
			}
			i++;
		}

		return result;
	}


	static Pattern squareBracketsPattern = Pattern.compile("\\[.*?\\]");

	/**
	 * Concatenate all strings in the array from index 'from' to 'to' (excluding
	 * to) with the given separator.
	 * 
	 * Example:
	 * 
	 * String[] s = "ab/cd/ed".split("/"); join(s, "\\", 0, s.length) ->
	 * "ab\\cd\\ed"
	 * 
	 * @param strings
	 * @param separator
	 * @param from
	 * @param to
	 *            Excluding strings[to]
	 * @return
	 */
	public static String join(String[] strings, String separator, int from, int to) {
		if (strings.length == 0 || from >= to)
			return "";
		
		from = Math.max(from, 0);
		to = Math.min(strings.length, to);

		StringBuffer sb = new StringBuffer();
		for (int i = from; i < to - 1; i++) {
			sb.append(strings[i]).append(separator);
		}
		return sb.append(strings[to - 1]).toString();
	}

	/**
	 * Copies a file.
	 * 
	 * @param source
	 *            File Source file
	 * @param dest
	 *            File Destination file
	 * @param deleteIfExists
	 *            boolean Determines whether the copy goes on even if the file
	 *            exists.
	 * @throws IOException
	 * @return boolean Whether the copy succeeded, or was stopped due to the
	 *         file already existing.
	 */
	public static boolean copyFile(File source, File dest, boolean deleteIfExists)
		throws IOException {

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			// Check if the file already exists.
			if (dest.exists()) {
				if (!deleteIfExists)
					return false;
				// else dest.delete();
			}

			in = new BufferedInputStream(new FileInputStream(source));
			out = new BufferedOutputStream(new FileOutputStream(dest));
			int el;
			// int tell = 0;
			while ((el = in.read()) >= 0) {
				out.write(el);
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (in != null)
				in.close();
		}
		return true;
	}

	/**
	 * This method is called at startup, and makes necessary adaptations to
	 * preferences for users from an earlier version of Jabref.
	 */
	public static void performCompatibilityUpdate() {

		// Make sure "abstract" is not in General fields, because
		// Jabref 1.55 moves the abstract to its own tab.
		String genFields = Globals.prefs.get("generalFields");
		// pr(genFields+"\t"+genFields.indexOf("abstract"));
		if (genFields.indexOf("abstract") >= 0) {
			// pr(genFields+"\t"+genFields.indexOf("abstract"));
			String newGen;
			if (genFields.equals("abstract"))
				newGen = "";
			else if (genFields.indexOf(";abstract;") >= 0) {
				newGen = genFields.replaceAll(";abstract;", ";");
			} else if (genFields.indexOf("abstract;") == 0) {
				newGen = genFields.replaceAll("abstract;", "");
			} else if (genFields.indexOf(";abstract") == genFields.length() - 9) {
				newGen = genFields.replaceAll(";abstract", "");
			} else
				newGen = genFields;
			// pr(newGen);
			Globals.prefs.put("generalFields", newGen);
		}

	}

    // -------------------------------------------------------------------------------

	/**
	 * extends the filename with a default Extension, if no Extension '.x' could
	 * be found
	 */
	public static String getCorrectFileName(String orgName, String defaultExtension) {
		if (orgName == null)
			return "";

		String back = orgName;
		int t = orgName.indexOf(".", 1); // hidden files Linux/Unix (?)
		if (t < 1)
			back = back + "." + defaultExtension;

		return back;
	}

	/**
	 * Quotes each and every character, e.g. '!' as &#33;. Used for verbatim
	 * display of arbitrary strings that may contain HTML entities.
	 */
	public static String quoteForHTML(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			sb.append("&#" + (int) s.charAt(i) + ";");
		}
		return sb.toString();
	}

	public static String quote(String s, String specials, char quoteChar) {
		return quote(s, specials, quoteChar, 0);
	}

	/**
	 * Quote special characters.
	 * 
	 * @param s
	 *            The String which may contain special characters.
	 * @param specials
	 *            A String containing all special characters except the quoting
	 *            character itself, which is automatically quoted.
	 * @param quoteChar
	 *            The quoting character.
	 * @param linewrap
	 *            The number of characters after which a linebreak is inserted
	 *            (this linebreak is undone by unquote()). Set to 0 to disable.
	 * @return A String with every special character (including the quoting
	 *         character itself) quoted.
	 */
	public static String quote(String s, String specials, char quoteChar, int linewrap) {
		StringBuffer sb = new StringBuffer();
		char c;
		int linelength = 0;
		boolean isSpecial;
		for (int i = 0; i < s.length(); ++i) {
			c = s.charAt(i);
			isSpecial = specials.indexOf(c) >= 0 || c == quoteChar;
			// linebreak?
			if (linewrap > 0
				&& (++linelength >= linewrap || (isSpecial && linelength >= linewrap - 1))) {
				sb.append(quoteChar);
				sb.append('\n');
				linelength = 0;
			}
			if (isSpecial) {
				sb.append(quoteChar);
				++linelength;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Unquote special characters.
	 * 
	 * @param s
	 *            The String which may contain quoted special characters.
	 * @param quoteChar
	 *            The quoting character.
	 * @return A String with all quoted characters unquoted.
	 */
	public static String unquote(String s, char quoteChar) {
		StringBuffer sb = new StringBuffer();
		char c;
		boolean quoted = false;
		for (int i = 0; i < s.length(); ++i) {
			c = s.charAt(i);
			if (quoted) { // append literally...
				if (c != '\n') // ...unless newline
					sb.append(c);
				quoted = false;
			} else if (c != quoteChar) {
				sb.append(c);
			} else { // quote char
				quoted = true;
			}
		}
		return sb.toString();
	}

	/**
	 * Quote all regular expression meta characters in s, in order to search for
	 * s literally.
	 */
	public static String quoteMeta(String s) {
		// work around a bug: trailing backslashes have to be quoted
		// individually
		int i = s.length() - 1;
		StringBuffer bs = new StringBuffer("");
		while ((i >= 0) && (s.charAt(i) == '\\')) {
			--i;
			bs.append("\\\\");
		}
		s = s.substring(0, i + 1);
		return "\\Q" + s.replaceAll("\\\\E", "\\\\E\\\\\\\\E\\\\Q") + "\\E" + bs.toString();
	}

	/*
	 * This method "tidies" up e.g. a keyword string, by alphabetizing the words
	 * and removing all duplicates.
	 */
	public static String sortWordsAndRemoveDuplicates(String text) {

		String[] words = text.split(", ");
		SortedSet<String> set = new TreeSet<String>();
		for (int i = 0; i < words.length; i++)
			set.add(words[i]);
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> i = set.iterator(); i.hasNext();) {
			sb.append(i.next());
			sb.append(", ");
		}
		if (sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		String result = sb.toString();
		return result.length() > 2 ? result : "";
	}

	// ========================================================
	// lot of abreviations in medline
	// PKC etc convert to {PKC} ...
	// ========================================================
	static Pattern titleCapitalPattern = Pattern.compile("[A-Z]+");

	/**
	 * Wrap all uppercase letters, or sequences of uppercase letters, in curly
	 * braces. Ignore letters within a pair of # character, as these are part of
	 * a string label that should not be modified.
	 * 
	 * @param s
	 *            The string to modify.
	 * @return The resulting string after wrapping capitals.
	 */
	public static String putBracesAroundCapitals(String s) {

		boolean inString = false, isBracing = false, escaped = false;
		int inBrace = 0;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			// Update variables based on special characters:
			int c = s.charAt(i);
			if (c == '{')
				inBrace++;
			else if (c == '}')
				inBrace--;
			else if (!escaped && (c == '#'))
				inString = !inString;

			// See if we should start bracing:
			if ((inBrace == 0) && !isBracing && !inString && Character.isLetter((char) c)
				&& Character.isUpperCase((char) c)) {

				buf.append('{');
				isBracing = true;
			}

			// See if we should close a brace set:
			if (isBracing && !(Character.isLetter((char) c) && Character.isUpperCase((char) c))) {

				buf.append('}');
				isBracing = false;
			}

			// Add the current character:
			buf.append((char) c);

			// Check if we are entering an escape sequence:
			if ((c == '\\') && !escaped)
				escaped = true;
			else
				escaped = false;

		}
		// Check if we have an unclosed brace:
		if (isBracing)
			buf.append('}');

		return buf.toString();

		/*
		 * if (s.length() == 0) return s; // Protect against ArrayIndexOutOf....
		 * StringBuffer buf = new StringBuffer();
		 * 
		 * Matcher mcr = titleCapitalPattern.matcher(s.substring(1)); while
		 * (mcr.find()) { String replaceStr = mcr.group();
		 * mcr.appendReplacement(buf, "{" + replaceStr + "}"); }
		 * mcr.appendTail(buf); return s.substring(0, 1) + buf.toString();
		 */
	}

	static Pattern bracedTitleCapitalPattern = Pattern.compile("\\{[A-Z]+\\}");

	/**
	 * This method looks for occurences of capital letters enclosed in an
	 * arbitrary number of pairs of braces, e.g. "{AB}" or "{{T}}". All of these
	 * pairs of braces are removed.
	 * 
	 * @param s
	 *            The String to analyze.
	 * @return A new String with braces removed.
	 */
	public static String removeBracesAroundCapitals(String s) {
		String previous = s;
		while ((s = removeSingleBracesAroundCapitals(s)).length() < previous.length()) {
			previous = s;
		}
		return s;
	}

	/**
	 * This method looks for occurences of capital letters enclosed in one pair
	 * of braces, e.g. "{AB}". All these are replaced by only the capitals in
	 * between the braces.
	 * 
	 * @param s
	 *            The String to analyze.
	 * @return A new String with braces removed.
	 */
	public static String removeSingleBracesAroundCapitals(String s) {
		Matcher mcr = bracedTitleCapitalPattern.matcher(s);
		StringBuffer buf = new StringBuffer();
		while (mcr.find()) {
			String replaceStr = mcr.group();
			mcr.appendReplacement(buf, replaceStr.substring(1, replaceStr.length() - 1));
		}
		mcr.appendTail(buf);
		return buf.toString();
	}

	public static String wrapHTML(String s, final int lineWidth) {
		StringBuffer sb = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(s);
		int charsLeft = lineWidth;
		while (tok.hasMoreTokens()) {
			String word = tok.nextToken();
			if (charsLeft == lineWidth) { // fresh line
				sb.append(word);
				charsLeft -= word.length();
				if (charsLeft <= 0) {
					sb.append("<br>\n");
					charsLeft = lineWidth;
				}
			} else { // continue previous line
				if (charsLeft < word.length() + 1) {
					sb.append("<br>\n");
					sb.append(word);
					if (word.length() >= lineWidth - 1) {
						sb.append("<br>\n");
						charsLeft = lineWidth;
					} else {
						sb.append(' ');
						charsLeft = lineWidth - word.length() - 1;
					}
				} else {
					sb.append(' ').append(word);
					charsLeft -= word.length() + 1;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Make a list of supported character encodings that can encode all
	 * characters in the given String.
	 * 
	 * @param characters
	 *            A String of characters that should be supported by the
	 *            encodings.
	 * @return A List of character encodings
	 */
	public static List<String> findEncodingsForString(String characters) {
		List<String> encodings = new ArrayList<String>();
		for (int i = 0; i < Globals.ENCODINGS.length; i++) {
			CharsetEncoder encoder = Charset.forName(Globals.ENCODINGS[i]).newEncoder();
			if (encoder.canEncode(characters))
				encodings.add(Globals.ENCODINGS[i]);
		}
		return encodings;
	}

	/**
	 * Will convert a two digit year using the following scheme (describe at
	 * http://www.filemaker.com/help/02-Adding%20and%20view18.html):
	 * 
	 * If a two digit year is encountered they are matched against the last 69
	 * years and future 30 years.
	 * 
	 * For instance if it is the year 1992 then entering 23 is taken to be 1923
	 * but if you enter 23 in 1993 then it will evaluate to 2023.
	 * 
	 * @param year
	 *            The year to convert to 4 digits.
	 * @return
	 */
	public static String toFourDigitYear(String year) {
		if (thisYear == 0) {
			thisYear = Calendar.getInstance().get(Calendar.YEAR);
		}
		return toFourDigitYear(year, thisYear);
	}

	public static int thisYear;

	/**
	 * Will convert a two digit year using the following scheme (describe at
	 * http://www.filemaker.com/help/02-Adding%20and%20view18.html):
	 * 
	 * If a two digit year is encountered they are matched against the last 69
	 * years and future 30 years.
	 * 
	 * For instance if it is the year 1992 then entering 23 is taken to be 1923
	 * but if you enter 23 in 1993 then it will evaluate to 2023.
	 * 
	 * @param year
	 *            The year to convert to 4 digits.
	 * @return
	 */
	public static String toFourDigitYear(String year, int thisYear) {
		if (year.length() != 2)
			return year;
		try {
			int thisYearTwoDigits = thisYear % 100;
			int thisCentury = thisYear - thisYearTwoDigits;

			int yearNumber = Integer.parseInt(year);

			if (yearNumber == thisYearTwoDigits) {
				return String.valueOf(thisYear);
			}
			// 20 , 90
			// 99 > 30
			if ((yearNumber + 100 - thisYearTwoDigits) % 100 > 30) {
				if (yearNumber < thisYearTwoDigits) {
					return String.valueOf(thisCentury + yearNumber);
				} else {
					return String.valueOf(thisCentury - 100 + yearNumber);
				}
			} else {
				if (yearNumber < thisYearTwoDigits) {
					return String.valueOf(thisCentury + 100 + yearNumber);
				} else {
					return String.valueOf(thisCentury + yearNumber);
				}
			}
		} catch (NumberFormatException e) {
			return year;
		}
	}

	/**
	 * Will return an integer indicating the month of the entry from 0 to 11.
	 * 
	 * -1 signals a unknown month string.
	 * 
	 * This method accepts three types of months given:
	 *  - Single and Double Digit months from 1 to 12 (01 to 12)
	 *  - 3 Digit BibTex strings (jan, feb, mar...)
	 *  - Full English Month identifiers.
	 * 
	 * @param month
	 * @return
	 */
	public static int getMonthNumber(String month) {

		month = month.replaceAll("#", "").toLowerCase();

		for (int i = 0; i < Globals.MONTHS.length; i++) {
			if (month.startsWith(Globals.MONTHS[i])) {
				return i;
			}
		}

		try {
			return Integer.parseInt(month) - 1;
		} catch (NumberFormatException e) {
		}
		return -1;
	}


    /**
     * Encodes a two-dimensional String array into a single string, using ':' and
     * ';' as separators. The characters ':' and ';' are escaped with '\'.
     * @param values The String array.
     * @return The encoded String.
     */
    public static String encodeStringArray(String[][] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(encodeStringArray(values[i]));
            if (i < values.length-1)
                sb.append(';');
        }
        return sb.toString();
    }

    /**
     * Encodes a String array into a single string, using ':' as separator.
     * The characters ':' and ';' are escaped with '\'.
     * @param entry The String array.
     * @return The encoded String.
     */
    public static String encodeStringArray(String[] entry) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entry.length; i++) {
            sb.append(encodeString(entry[i]));
            if (i < entry.length-1)
                sb.append(':');

        }
        return sb.toString();
    }

    /**
     * Decodes an encoded double String array back into array form. The array
     * is assumed to be square, and delimited by the characters ';' (first dim) and
     * ':' (second dim).
     * @param value The encoded String to be decoded.
     * @return The decoded String array.
     */
    public static String[][] decodeStringDoubleArray(String value) {
        ArrayList<ArrayList<String>> newList = new ArrayList<ArrayList<String>>();
        StringBuilder sb = new StringBuilder();
        ArrayList<String> thisEntry = new ArrayList<String>();
        boolean escaped = false;
        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if (!escaped && (c == '\\')) {
                escaped = true;
                continue;
            }
            else if (!escaped && (c == ':')) {
                thisEntry.add(sb.toString());
                sb = new StringBuilder();
            }
            else if (!escaped && (c == ';')) {
                thisEntry.add(sb.toString());
                sb = new StringBuilder();
                newList.add(thisEntry);
                thisEntry = new ArrayList<String>();
            }
            else sb.append(c);
            escaped = false;
        }
        if (sb.length() > 0)
            thisEntry.add(sb.toString());
        if (thisEntry.size() > 0)
            newList.add(thisEntry);

        // Convert to String[][]:
        String[][] res = new String[newList.size()][];
        for (int i = 0; i < res.length; i++) {
            res[i] = new String[newList.get(i).size()];
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = newList.get(i).get(j);
            }
        }
        return res;
    }

    private static String encodeString(String s) {
        if (s == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if ((c == ';') || (c == ':') || (c == '\\'))
                sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    /**
	 * Static equals that can also return the right result when one of the
	 * objects is null.
	 * 
	 * @param one
	 *            The object whose equals method is called if the first is not
	 *            null.
	 * @param two
	 *            The object passed to the first one if the first is not null.
	 * @return <code>one == null ? two == null : one.equals(two);</code>
	 */
	public static boolean equals(Object one, Object two) {
		return one == null ? two == null : one.equals(two);
	}

	/**
	 * Returns the given string but with the first character turned into an
	 * upper case character.
	 * 
	 * Example: testTest becomes TestTest
	 * 
	 * @param string
	 *            The string to change the first character to upper case to.
	 * @return A string has the first character turned to upper case and the
	 *         rest unchanged from the given one.
	 */
	public static String toUpperFirstLetter(String string){
		if (string == null)
			throw new IllegalArgumentException();
		
		if (string.length() == 0)
			return string;
		
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

    /**
     * Build a String array containing all those elements of all that are not
     * in subset.
     * @param all The array of all values.
     * @param subset The subset of values.
     * @return The remainder that is not part of the subset.
     */
    public static String[] getRemainder(String[] all, String[] subset) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < all.length; i++) {
            boolean found = false;
            inner: for (int j = 0; j < subset.length; j++) {
                if (subset[j].equals(all[i])) {
                    found = true;
                    break inner;
                }
            }
            if (!found) al.add(all[i]);
        }
        return al.toArray(new String[al.size()]);
    }

    public static <T> T[] concat(T[] first, T[] second) {
	T[] result = Arrays.copyOf(first, first.length + second.length);
	System.arraycopy(second, 0, result, first.length, second.length);
	return result;
    }
    

    /**
     * This method checks whether there is a lock file for the given file. If
     * there is, it waits for 500 ms. This is repeated until the lock is gone
     * or we have waited the maximum number of times.
     *
     * @param file The file to check the lock for.
     * @param maxWaitCount The maximum number of times to wait.
     * @return true if the lock file is gone, false if it is still there.
     */
    public static boolean waitForFileLock(File file, int maxWaitCount) {
        // Check if the file is locked by another JabRef user:
        int lockCheckCount = 0;
        while (Util.hasLockFile(file)) {

            if (lockCheckCount++ == maxWaitCount) {
                return false;
            }
            try { Thread.sleep(500); } catch (InterruptedException ex) {}
        }
        return true;
    }

    /**
     * Check whether a lock file exists for this file.
     * @param file The file to check.
     * @return true if a lock file exists, false otherwise.
     */
    public static boolean hasLockFile(File file) {
        File lock = new File(file.getPath()+ SaveSession.LOCKFILE_SUFFIX);
        return lock.exists();
    }
}
