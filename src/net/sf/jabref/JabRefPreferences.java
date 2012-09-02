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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;




public class JabRefPreferences {
    public String WRAPPED_USERNAME, MARKING_WITH_NUMBER_PATTERN;

    Preferences prefs;
    public HashMap<String, Object> defaults = new HashMap<String, Object>();
    public HashMap<String, String>
        keyBinds = new HashMap<String, String>(),
        defKeyBinds = new HashMap<String, String>();
    private HashSet<String> putBracesAroundCapitalsFields = new HashSet<String>(4);
    private HashSet<String> nonWrappableFields = new HashSet<String>(5);
    
    // The following field is used as a global variable during the export of a database.
    // By setting this field to the path of the database's default file directory, formatters
    // that should resolve external file paths can access this field. This is an ugly hack
    // to solve the problem of formatters not having access to any context except for the
    // string to be formatted and possible formatter arguments.
    public String[] fileDirForDatabase = null;

    // Similarly to the previous variable, this is a global that can be used during
    // the export of a database if the database filename should be output. If a database
    // is tied to a file on disk, this variable is set to that file before export starts:
    public File databaseFile = null;

    // The following field is used as a global variable during the export of a database.
    // It is used to hold custom name formatters defined by a custom export filter.
    // It is set before the export starts:
    public HashMap<String,String> customExportNameFormatters = null;

    // The only instance of this class:
    private static JabRefPreferences singleton = null;

    public static JabRefPreferences getInstance() {
		if (singleton == null)
			singleton = new JabRefPreferences();
		return singleton;
	}

    // The constructor is made private to enforce this as a singleton class:
    private JabRefPreferences() {        
        prefs = Preferences.userNodeForPackage(JabRefPreferences.class);
        
        defaults.put("useDefaultLookAndFeel", Boolean.TRUE);
        defaults.put("lyxpipe", System.getProperty("user.home")+File.separator+".lyx/lyxpipe");
        defaults.put("vim", "vim");
        defaults.put("vimServer", "vim");
        defaults.put("posX", Integer.valueOf(0));
        defaults.put("posY", Integer.valueOf(0));
        defaults.put("sizeX", Integer.valueOf(840));
        defaults.put("sizeY", Integer.valueOf(680));
        defaults.put("windowMaximised", Boolean.FALSE);
        defaults.put("previewPanelHeight", 200);
        defaults.put("entryEditorHeight", 400);
        defaults.put("tableColorCodesOn", Boolean.TRUE);
        defaults.put("namesAsIs", Boolean.FALSE);
        defaults.put("namesFf", Boolean.FALSE);
        defaults.put("namesLf", Boolean.FALSE);
        defaults.put("namesNatbib", Boolean.TRUE);
        defaults.put("abbrAuthorNames", Boolean.TRUE);
        defaults.put("namesLastOnly", Boolean.TRUE);
        defaults.put("language", "en");
        defaults.put("showShort", Boolean.TRUE);
        defaults.put("priSort", "author");
        defaults.put("priDescending", Boolean.FALSE);
        defaults.put("priBinary", Boolean.FALSE);
        defaults.put("secSort", "year");
        defaults.put("secDescending", Boolean.TRUE);
        defaults.put("terSort", "author");
        defaults.put("terDescending", Boolean.FALSE);
        defaults.put("columnNames", "entrytype;author;title;year;journal;owner;timestamp;bibtexkey");
        defaults.put("columnWidths","75;280;400;60;100;100;100;100");
        defaults.put("xmpPrivacyFilters", "pdf;timestamp;keywords;owner;note;review");
        defaults.put("useXmpPrivacyFilter", Boolean.FALSE);
        defaults.put("workingDirectory", System.getProperty("user.home"));
        defaults.put("exportWorkingDirectory", System.getProperty("user.home"));
        defaults.put("importWorkingDirectory", System.getProperty("user.home"));
        defaults.put("fileWorkingDirectory", System.getProperty("user.home"));
        defaults.put("autoOpenForm", Boolean.TRUE);
        defaults.put("entryTypeFormHeightFactor", Integer.valueOf(1));
        defaults.put("entryTypeFormWidth", Integer.valueOf(1));
        defaults.put("backup", Boolean.TRUE);
        defaults.put("openLastEdited", Boolean.TRUE);
        defaults.put("lastEdited", null);
        defaults.put("stringsPosX", Integer.valueOf(0));
        defaults.put("stringsPosY", Integer.valueOf(0));
        defaults.put("stringsSizeX", Integer.valueOf(600));
        defaults.put("stringsSizeY", Integer.valueOf(400));
        defaults.put("defaultShowSource", Boolean.FALSE);
        defaults.put("showSource", Boolean.TRUE);
        defaults.put("defaultAutoSort", Boolean.FALSE);
        defaults.put("enableSourceEditing", Boolean.TRUE);
        defaults.put("caseSensitiveSearch", Boolean.FALSE);
        defaults.put("searchReq", Boolean.TRUE);
        defaults.put("searchOpt", Boolean.TRUE);
        defaults.put("searchGen", Boolean.TRUE);
        defaults.put("searchAll", Boolean.FALSE);
        defaults.put("incrementS", Boolean.FALSE);
        defaults.put("searchAutoComplete", Boolean.TRUE);
        defaults.put("saveInStandardOrder", Boolean.TRUE);
        defaults.put("saveInOriginalOrder", Boolean.FALSE);
        defaults.put("exportInStandardOrder", Boolean.TRUE);
        defaults.put("exportInOriginalOrder", Boolean.FALSE);
        defaults.put("selectS", Boolean.FALSE);
        defaults.put("regExpSearch", Boolean.TRUE);
        defaults.put("highLightWords", Boolean.TRUE);
        defaults.put("searchPanePosX", Integer.valueOf(0));
        defaults.put("searchPanePosY", Integer.valueOf(0));
        defaults.put("autoComplete", Boolean.TRUE);
        defaults.put("autoCompleteFields", "author;editor;title;journal;publisher;keywords;crossref");
        defaults.put("autoCompFF", Boolean.FALSE);
        defaults.put("autoCompLF", Boolean.FALSE);
        defaults.put("groupSelectorVisible", Boolean.TRUE);
        defaults.put("groupFloatSelections", Boolean.TRUE);
        defaults.put("groupIntersectSelections", Boolean.TRUE);
        defaults.put("groupInvertSelections", Boolean.FALSE);
        defaults.put("groupShowOverlapping", Boolean.FALSE);
        defaults.put("groupSelectMatches", Boolean.FALSE);
        defaults.put("groupsDefaultField", "keywords");
        defaults.put("groupShowIcons", Boolean.TRUE);
        defaults.put("groupShowDynamic", Boolean.TRUE);
        defaults.put("groupExpandTree", Boolean.TRUE);
        defaults.put("groupAutoShow", Boolean.TRUE);
        defaults.put("groupAutoHide", Boolean.TRUE);
        defaults.put("autoAssignGroup", Boolean.TRUE);
        defaults.put("groupKeywordSeparator", ", ");
        defaults.put("highlightGroupsMatchingAny", Boolean.FALSE);
        defaults.put("highlightGroupsMatchingAll", Boolean.FALSE);
        defaults.put("searchPanelVisible", Boolean.FALSE);
        defaults.put("defaultEncoding", System.getProperty("file.encoding"));
        defaults.put("groupsVisibleRows", Integer.valueOf(8));
        defaults.put("defaultOwner", System.getProperty("user.name"));
        defaults.put("preserveFieldFormatting", Boolean.FALSE);
        defaults.put("memoryStickMode", Boolean.FALSE);
        defaults.put("renameOnMoveFileToFileDir", Boolean.TRUE);

    // The general fields stuff is made obsolete by the CUSTOM_TAB_... entries.
        defaults.put("generalFields", "crossref;keywords;file;doi;url;urldate;"+
                     "pdf;comment;owner");

        defaults.put("useCustomIconTheme", Boolean.FALSE);
        defaults.put("customIconThemeFile", "/home/alver/div/crystaltheme_16/Icons.properties");

        //defaults.put("recentFiles", "/home/alver/Documents/bibk_dok/hovedbase.bib");
        defaults.put("historySize", Integer.valueOf(8));
        defaults.put("fontSize", Integer.valueOf(12));
        defaults.put("overrideDefaultFonts", Boolean.FALSE);
        defaults.put("menuFontFamily", "Times");
        defaults.put("menuFontSize", Integer.valueOf(11));
        // Main table color settings:
        defaults.put("tableBackground", "255:255:255");
        defaults.put("tableReqFieldBackground", "230:235:255");
        defaults.put("tableOptFieldBackground", "230:255:230");
        defaults.put("tableText", "0:0:0");
        defaults.put("gridColor", "210:210:210");
        defaults.put("grayedOutBackground", "210:210:210");
        defaults.put("grayedOutText", "40:40:40");
        defaults.put("veryGrayedOutBackground", "180:180:180");
        defaults.put("veryGrayedOutText", "40:40:40");
        defaults.put("markedEntryBackground0", "255:255:180");
        defaults.put("markedEntryBackground1", "255:220:180");
        defaults.put("markedEntryBackground2", "255:180:160");
        defaults.put("markedEntryBackground3", "255:120:120");
        defaults.put("markedEntryBackground4", "255:75:75");
        defaults.put("markedEntryBackground5", "220:255:220");
        defaults.put("validFieldBackgroundColor", "255:255:255");
        defaults.put("invalidFieldBackgroundColor", "255:0:0");
        defaults.put("activeFieldEditorBackgroundColor", "220:220:255");
        defaults.put("fieldEditorTextColor", "0:0:0");

        defaults.put("incompleteEntryBackground", "250:175:175");

        defaults.put("antialias", Boolean.FALSE);
        defaults.put("ctrlClick", Boolean.FALSE);
        defaults.put("disableOnMultipleSelection", Boolean.FALSE);
        defaults.put("pdfColumn", Boolean.FALSE);
        defaults.put("urlColumn", Boolean.TRUE);
        defaults.put("fileColumn", Boolean.TRUE);
        defaults.put("arxivColumn", Boolean.FALSE);
        defaults.put("useOwner", Boolean.TRUE);
        defaults.put("overwriteOwner", Boolean.FALSE);
        defaults.put("allowTableEditing", Boolean.FALSE);
        defaults.put("dialogWarningForDuplicateKey", Boolean.TRUE);
        defaults.put("dialogWarningForEmptyKey", Boolean.TRUE);
        defaults.put("displayKeyWarningDialogAtStartup", Boolean.TRUE);
        defaults.put("avoidOverwritingKey", Boolean.FALSE);
        defaults.put("warnBeforeOverwritingKey", Boolean.TRUE);
        defaults.put("confirmDelete", Boolean.TRUE);
        defaults.put("grayOutNonHits", Boolean.TRUE);
        defaults.put("floatSearch", Boolean.TRUE);
        defaults.put("showSearchInDialog", Boolean.FALSE);
        defaults.put("searchAllBases", Boolean.FALSE);
        defaults.put("defaultLabelPattern", "[auth][year]");
        defaults.put("previewEnabled", Boolean.TRUE);
        defaults.put("activePreview", 0);
        defaults.put("preview0", "<font face=\"arial\">"
                     +"<b><i>\\bibtextype</i><a name=\"\\bibtexkey\">\\begin{bibtexkey} (\\bibtexkey)</a>"
                     +"\\end{bibtexkey}</b><br>__NEWLINE__"
                     +"\\begin{author} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\author}<BR>\\end{author}__NEWLINE__"
                     +"\\begin{editor} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\editor} "
                     +"<i>(\\format[IfPlural(Eds.,Ed.)]{\\editor})</i><BR>\\end{editor}__NEWLINE__"
                     +"\\begin{title} \\format[HTMLChars]{\\title} \\end{title}<BR>__NEWLINE__"
                     +"\\begin{chapter} \\format[HTMLChars]{\\chapter}<BR>\\end{chapter}__NEWLINE__"
                     +"\\begin{journal} <em>\\format[HTMLChars]{\\journal}, </em>\\end{journal}__NEWLINE__"
                     // Include the booktitle field for @inproceedings, @proceedings, etc.
                     +"\\begin{booktitle} <em>\\format[HTMLChars]{\\booktitle}, </em>\\end{booktitle}__NEWLINE__"
                     +"\\begin{school} <em>\\format[HTMLChars]{\\school}, </em>\\end{school}__NEWLINE__"
                     +"\\begin{institution} <em>\\format[HTMLChars]{\\institution}, </em>\\end{institution}__NEWLINE__"
                     +"\\begin{publisher} <em>\\format[HTMLChars]{\\publisher}, </em>\\end{publisher}__NEWLINE__"
                     +"\\begin{year}<b>\\year</b>\\end{year}\\begin{volume}<i>, \\volume</i>\\end{volume}"
                     +"\\begin{pages}, \\format[FormatPagesForHTML]{\\pages} \\end{pages}__NEWLINE__"
                     +"\\begin{abstract}<BR><BR><b>Abstract: </b> \\format[HTMLChars]{\\abstract} \\end{abstract}__NEWLINE__"
                     +"\\begin{review}<BR><BR><b>Review: </b> \\format[HTMLChars]{\\review} \\end{review}"
                     +"</dd>__NEWLINE__<p></p></font>");
        defaults.put("preview1", "<font face=\"arial\">"
                     +"<b><i>\\bibtextype</i><a name=\"\\bibtexkey\">\\begin{bibtexkey} (\\bibtexkey)</a>"
                     +"\\end{bibtexkey}</b><br>__NEWLINE__"
                     +"\\begin{author} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\author}<BR>\\end{author}__NEWLINE__"
                     +"\\begin{editor} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\editor} "
                     +"<i>(\\format[IfPlural(Eds.,Ed.)]{\\editor})</i><BR>\\end{editor}__NEWLINE__"
                     +"\\begin{title} \\format[HTMLChars]{\\title} \\end{title}<BR>__NEWLINE__"
                     +"\\begin{chapter} \\format[HTMLChars]{\\chapter}<BR>\\end{chapter}__NEWLINE__"
                     +"\\begin{journal} <em>\\format[HTMLChars]{\\journal}, </em>\\end{journal}__NEWLINE__"
                     // Include the booktitle field for @inproceedings, @proceedings, etc.
                     +"\\begin{booktitle} <em>\\format[HTMLChars]{\\booktitle}, </em>\\end{booktitle}__NEWLINE__"
                     +"\\begin{school} <em>\\format[HTMLChars]{\\school}, </em>\\end{school}__NEWLINE__"
                     +"\\begin{institution} <em>\\format[HTMLChars]{\\institution}, </em>\\end{institution}__NEWLINE__"
                     +"\\begin{publisher} <em>\\format[HTMLChars]{\\publisher}, </em>\\end{publisher}__NEWLINE__"
                     +"\\begin{year}<b>\\year</b>\\end{year}\\begin{volume}<i>, \\volume</i>\\end{volume}"
                     +"\\begin{pages}, \\format[FormatPagesForHTML]{\\pages} \\end{pages}"
                     +"</dd>__NEWLINE__<p></p></font>");


        // TODO: Currently not possible to edit this setting:
        defaults.put("previewPrintButton", Boolean.FALSE);
        defaults.put("autoDoubleBraces", Boolean.FALSE);
        defaults.put("doNotResolveStringsFor", "url");
        defaults.put("resolveStringsAllFields", Boolean.FALSE);
        defaults.put("putBracesAroundCapitals","");//"title;journal;booktitle;review;abstract");
        defaults.put("nonWrappableFields", "pdf;ps;url;doi;file");
        defaults.put("useImportInspectionDialog", Boolean.TRUE);
        defaults.put("useImportInspectionDialogForSingle", Boolean.TRUE);
        defaults.put("generateKeysAfterInspection", Boolean.TRUE);
        defaults.put("markImportedEntries", Boolean.TRUE);
        defaults.put("unmarkAllEntriesBeforeImporting", Boolean.TRUE);
        defaults.put("warnAboutDuplicatesInInspection", Boolean.TRUE);
        defaults.put("useTimeStamp", Boolean.TRUE);
        defaults.put("overwriteTimeStamp", Boolean.FALSE);
        defaults.put("timeStampFormat", "yyyy.MM.dd");
//        defaults.put("timeStampField", "timestamp");
        defaults.put("timeStampField", BibtexFields.TIMESTAMP);
        defaults.put("generateKeysBeforeSaving", Boolean.FALSE);

        defaults.put("useRemoteServer", Boolean.FALSE);
        defaults.put("remoteServerPort", Integer.valueOf(6050));

        defaults.put("personalJournalList", null);
        defaults.put("externalJournalLists", null);
        defaults.put("citeCommand", "cite"); // obsoleted by the app-specific ones
        defaults.put("citeCommandVim", "\\cite");
        defaults.put("citeCommandEmacs", "\\cite");
        defaults.put("citeCommandWinEdt", "\\cite");
        defaults.put("citeCommandLed", "\\cite");
        defaults.put("floatMarkedEntries", Boolean.TRUE);

        defaults.put("useNativeFileDialogOnMac", Boolean.FALSE);
        defaults.put("filechooserDisableRename", Boolean.TRUE);

        defaults.put("lastUsedExport", null);
        defaults.put("sidePaneWidth", Integer.valueOf(-1));

        defaults.put("importInspectionDialogWidth", Integer.valueOf(650));
        defaults.put("importInspectionDialogHeight", Integer.valueOf(650));
        defaults.put("searchDialogWidth", Integer.valueOf(650));
        defaults.put("searchDialogHeight", Integer.valueOf(500));
        defaults.put("showFileLinksUpgradeWarning", Boolean.TRUE);
        defaults.put("autolinkExactKeyOnly", Boolean.TRUE);
        defaults.put("numericFields", "mittnum;author");
        defaults.put("runAutomaticFileSearch", Boolean.FALSE);
        defaults.put("useLockFiles", Boolean.TRUE);
        defaults.put("autoSave", Boolean.TRUE);
        defaults.put("autoSaveInterval", 5);
        defaults.put("promptBeforeUsingAutosave", Boolean.TRUE);
        defaults.put("deletePlugins", "");
        defaults.put("enforceLegalBibtexKey", Boolean.TRUE);
        defaults.put("biblatexMode", Boolean.FALSE);
        defaults.put("keyGenFirstLetterA", Boolean.TRUE);
        defaults.put("keyGenAlwaysAddLetter", Boolean.FALSE);
        defaults.put(JabRefPreferences.EMAIL_SUBJECT, Globals.lang("References"));
        defaults.put(JabRefPreferences.OPEN_FOLDERS_OF_ATTACHED_FILES, Boolean.FALSE);
        defaults.put("allowFileAutoOpenBrowse", Boolean.TRUE);
        defaults.put("webSearchVisible", Boolean.FALSE);
        defaults.put("selectedFetcherIndex", 0);
        defaults.put("bibLocationAsFileDir", Boolean.TRUE);
        defaults.put("bibLocAsPrimaryDir", Boolean.FALSE);
        defaults.put("dbConnectServerType", "MySQL");
        defaults.put("dbConnectHostname", "localhost");
        defaults.put("dbConnectDatabase", "jabref");
        defaults.put("dbConnectUsername", "root");
        //defaults.put("lastAutodetectedImport", "");

        //defaults.put("autoRemoveExactDuplicates", Boolean.FALSE);
        //defaults.put("confirmAutoRemoveExactDuplicates", Boolean.TRUE);
        
        //defaults.put("tempDir", System.getProperty("java.io.tmpdir"));
        //Util.pr(System.getProperty("java.io.tempdir"));

        //defaults.put("keyPattern", new LabelPattern(KEY_PATTERN));
        
        restoreKeyBindings();

        //defaults.put("oooWarning", Boolean.TRUE);
        updateSpecialFieldHandling();
        WRAPPED_USERNAME = "["+get("defaultOwner")+"]";
        MARKING_WITH_NUMBER_PATTERN = "\\["+get("defaultOwner")+":(\\d+)\\]";

        String defaultExpression = "**/.*[bibtexkey].*\\\\.[extension]";
        defaults.put(DEFAULT_REG_EXP_SEARCH_EXPRESSION_KEY, defaultExpression);
        defaults.put(REG_EXP_SEARCH_EXPRESSION_KEY, defaultExpression);
        defaults.put(USE_REG_EXP_SEARCH_KEY, Boolean.FALSE);
        defaults.put("useIEEEAbrv", Boolean.TRUE);
    }
    
    public static final String DEFAULT_REG_EXP_SEARCH_EXPRESSION_KEY = "defaultRegExpSearchExpression";
    public static final String REG_EXP_SEARCH_EXPRESSION_KEY = "regExpSearchExpression";
    public static final String USE_REG_EXP_SEARCH_KEY = "useRegExpSearch";

	public static final String EMAIL_SUBJECT = "emailSubject";
	public static final String OPEN_FOLDERS_OF_ATTACHED_FILES = "openFoldersOfAttachedFiles";


	public boolean putBracesAroundCapitals(String fieldName) {
        return putBracesAroundCapitalsFields.contains(fieldName);
    }

    public void updateSpecialFieldHandling() {
        putBracesAroundCapitalsFields.clear();
        String fieldString = get("putBracesAroundCapitals");
        if (fieldString.length() > 0) {
            String[] fields = fieldString.split(";");
            for (int i=0; i<fields.length; i++)
                putBracesAroundCapitalsFields.add(fields[i].trim());
        }
        nonWrappableFields.clear();
        fieldString = get("nonWrappableFields");
        if (fieldString.length() > 0) {
            String[] fields = fieldString.split(";");
            for (int i=0; i<fields.length; i++)
                nonWrappableFields.add(fields[i].trim());
        }

    }

    /**
     * Check whether a key is set (differently from null).
     * @param key The key to check.
     * @return true if the key is set, false otherwise.
     */
    public boolean hasKey(String key) {
        return prefs.get(key, null) != null;
    }

    public String get(String key) {
        return prefs.get(key, (String)defaults.get(key));
    }

    public String get(String key, String def) {
        return prefs.get(key, def);
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, getBooleanDefault(key));
    }
    
    public boolean getBooleanDefault(String key){
        return ((Boolean)defaults.get(key)).booleanValue();
    }

    public double getDouble(String key) {
        return prefs.getDouble(key, getDoubleDefault(key));
    }
    
    public double getDoubleDefault(String key){
        return ((Double)defaults.get(key)).doubleValue();
    }

    public int getInt(String key) {
        return prefs.getInt(key, getIntDefault(key));
    }

    public int getIntDefault(String key) {
        return ((Integer)defaults.get(key)).intValue();
    }
    
    public byte[] getByteArray(String key) {
        return prefs.getByteArray(key, getByteArrayDefault(key));
    }

    public byte[] getByteArrayDefault(String key){
        return (byte[])defaults.get(key);   
    }
    
    public void put(String key, String value) {
        prefs.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    public void putDouble(String key, double value) {
        prefs.putDouble(key, value);
    }

    public void putInt(String key, int value) {
        prefs.putInt(key, value);
    }

    public void putByteArray(String key, byte[] value) {
        prefs.putByteArray(key, value);
    }

    public void remove(String key) {
        prefs.remove(key);
    }

    /**
     * Puts a string array into the Preferences, by linking its elements
     * with ';' into a single string. Escape characters make the process
     * transparent even if strings contain ';'.
     */
    public void putStringArray(String key, String[] value) {
        if (value == null) {
            remove(key);
            return;
        }

        if (value.length > 0) {
            StringBuffer linked = new StringBuffer();
            for (int i=0; i<value.length-1; i++) {
                linked.append(makeEscape(value[i]));
                linked.append(';');
            }
            linked.append(makeEscape(value[value.length-1]));
            put(key, linked.toString());
        } else {
            put(key, "");
        }
    }

    /**
     * Returns a String[] containing the chosen columns.
     */
    public String[] getStringArray(String key) {
        String names = get(key);
        if (names == null)
            return null;

        StringReader rd = new StringReader(names);
        Vector<String> arr = new Vector<String>();
        String rs;
        try {
            while ((rs = getNextUnit(rd)) != null) {
                arr.add(rs);
            }
        } catch (IOException ex) {}
        String[] res = new String[arr.size()];
        for (int i=0; i<res.length; i++)
            res[i] = arr.elementAt(i);

        return res;
    }
    
    /**
     * Set the default value for a key. This is useful for plugins that need to
     * add default values for the prefs keys they use.
     * @param key The preferences key.
     * @param value The default value.
     */
    public void putDefaultValue(String key, Object value) {
        defaults.put(key, value);
    }

    /**
     * Returns the HashMap containing all key bindings.
     */
    public HashMap<String, String> getKeyBindings() {
        return keyBinds;
    }

    /**
     * Returns the HashMap containing default key bindings.
     */
    public HashMap<String, String> getDefaultKeys() {
        return defKeyBinds;
    }

    /**
     * Stores new key bindings into Preferences, provided they
     * actually differ from the old ones.
     */
    public void setNewKeyBindings(HashMap<String, String> newBindings) {
        if (!newBindings.equals(keyBinds)) {
            // This confirms that the bindings have actually changed.
            String[] bindNames = new String[newBindings.size()],
                bindings = new String[newBindings.size()];
            int index = 0;
            for (Iterator<String> i=newBindings.keySet().iterator();
                 i.hasNext();) {
                String nm = i.next();
                String bnd = newBindings.get(nm);
                bindNames[index] = nm;
                bindings[index] = bnd;
                index++;
            }
            putStringArray("bindNames", bindNames);
            putStringArray("bindings", bindings);
            keyBinds = newBindings;
        }
    }

    private void restoreKeyBindings() {
        // Define default keybindings.
        defineDefaultKeyBindings();

        // First read the bindings, and their names.
        String[] bindNames = getStringArray("bindNames"),
            bindings = getStringArray("bindings");

        // Then set up the key bindings HashMap.
        if ((bindNames == null) || (bindings == null)
            || (bindNames.length != bindings.length)) {
            // Nothing defined in Preferences, or something is wrong.
            setDefaultKeyBindings();
            return;
        }

        for (int i=0; i<bindNames.length; i++)
            keyBinds.put(bindNames[i], bindings[i]);
    }

    private void setDefaultKeyBindings() {
        keyBinds = defKeyBinds;
    }

    private void defineDefaultKeyBindings() {
        defKeyBinds.put("Push to application","ctrl L");
      defKeyBinds.put("Push to LyX","ctrl L");
      defKeyBinds.put("Push to WinEdt","ctrl shift W");
        defKeyBinds.put("Quit JabRef", "ctrl Q");
        defKeyBinds.put("Open database", "ctrl O");
        defKeyBinds.put("Save database", "ctrl S");
        defKeyBinds.put("Save database as ...", "ctrl shift S");
        defKeyBinds.put("Save all", "ctrl alt S");
        defKeyBinds.put("Close database", "ctrl W");
        defKeyBinds.put("New entry", "ctrl N");
        defKeyBinds.put("Cut", "ctrl X");
        defKeyBinds.put("Copy", "ctrl C");
        defKeyBinds.put("Paste", "ctrl V");
        defKeyBinds.put("Undo", "ctrl Z");
        defKeyBinds.put("Redo", "ctrl Y");
        defKeyBinds.put("Help", "F1");
        defKeyBinds.put("New article", "ctrl shift A");
        defKeyBinds.put("New book", "ctrl shift B");
        defKeyBinds.put("New phdthesis", "ctrl shift T");
        defKeyBinds.put("New inbook", "ctrl shift I");
        defKeyBinds.put("New mastersthesis", "ctrl shift M");
        defKeyBinds.put("New proceedings", "ctrl shift P");
        defKeyBinds.put("New unpublished", "ctrl shift U");
        defKeyBinds.put("Edit strings", "ctrl T");
        defKeyBinds.put("Edit preamble", "ctrl P");
        defKeyBinds.put("Select all", "ctrl A");
        defKeyBinds.put("Toggle groups interface", "ctrl shift G");
        defKeyBinds.put("Autogenerate BibTeX keys", "ctrl G");
        defKeyBinds.put("Search", "ctrl F");
        defKeyBinds.put("Incremental search", "ctrl shift F");
        defKeyBinds.put("Repeat incremental search", "ctrl shift F");
        defKeyBinds.put("Close dialog", "ESCAPE");
        defKeyBinds.put("Close entry editor", "ESCAPE");
        defKeyBinds.put("Close preamble editor", "ESCAPE");
        defKeyBinds.put("Back, help dialog", "LEFT");
        defKeyBinds.put("Forward, help dialog", "RIGHT");
        defKeyBinds.put("Preamble editor, store changes", "alt S");
        defKeyBinds.put("Clear search", "ESCAPE");
        defKeyBinds.put("Entry editor, next panel", "ctrl TAB");//"ctrl PLUS");//"shift Right");
        defKeyBinds.put("Entry editor, previous panel", "ctrl shift TAB");//"ctrl MINUS");
        defKeyBinds.put("Entry editor, next panel 2", "ctrl PLUS");//"ctrl PLUS");//"shift Right");
        defKeyBinds.put("Entry editor, previous panel 2", "ctrl MINUS");//"ctrl MINUS");
        defKeyBinds.put("Entry editor, next entry", "ctrl shift DOWN");
        defKeyBinds.put("Entry editor, previous entry", "ctrl shift UP");
        defKeyBinds.put("Entry editor, store field", "alt S");
        defKeyBinds.put("String dialog, add string", "ctrl N");
        defKeyBinds.put("String dialog, remove string", "shift DELETE");
        defKeyBinds.put("String dialog, move string up", "ctrl UP");
        defKeyBinds.put("String dialog, move string down", "ctrl DOWN");
        defKeyBinds.put("Save session", "F11");
        defKeyBinds.put("Load session", "F12");
        defKeyBinds.put("Copy \\cite{BibTeX key}", "ctrl K");
        defKeyBinds.put("Copy BibTeX key", "ctrl shift K");
        defKeyBinds.put("Copy BibTeX key and title", "ctrl shift alt K");
        defKeyBinds.put("Next tab", "ctrl PAGE_DOWN");
        defKeyBinds.put("Previous tab", "ctrl PAGE_UP");
        defKeyBinds.put("Replace string", "ctrl R");
        defKeyBinds.put("Delete", "DELETE");
        defKeyBinds.put("Open file", "F4");
        defKeyBinds.put("Open PDF or PS", "shift F5");
        defKeyBinds.put("Open URL or DOI", "F3");
        defKeyBinds.put("Open SPIRES entry", "ctrl F3");
        defKeyBinds.put("Toggle entry preview", "ctrl F9");
        defKeyBinds.put("Switch preview layout", "F9");
        defKeyBinds.put("Edit entry", "ctrl E");
        defKeyBinds.put("Mark entries", "ctrl M");
        defKeyBinds.put("Unmark entries", "ctrl shift M");
        defKeyBinds.put("Fetch Medline", "F5");
        defKeyBinds.put("Search ScienceDirect", "ctrl F5");
        defKeyBinds.put("Search ADS", "ctrl shift F6");
        defKeyBinds.put("New from plain text", "ctrl shift N");
        defKeyBinds.put("Synchronize files", "ctrl F4");
        defKeyBinds.put("Synchronize PDF", "shift F4");
        defKeyBinds.put("Synchronize PS", "ctrl shift F4");
        defKeyBinds.put("Focus entry table", "ctrl shift E");

        defKeyBinds.put("Abbreviate", "ctrl alt A");
        defKeyBinds.put("Unabbreviate", "ctrl alt shift A");
        defKeyBinds.put("Search IEEEXplore", "alt F8");
        defKeyBinds.put("Search ACM Portal", "ctrl shift F8");
        defKeyBinds.put("Fetch ArXiv.org", "shift F8");
        defKeyBinds.put("Search JSTOR", "shift F9");
        defKeyBinds.put("Write XMP", "ctrl F4");
        defKeyBinds.put("New file link", "ctrl N");
        defKeyBinds.put("Fetch SPIRES", "ctrl F8");
        defKeyBinds.put("Fetch INSPIRE", "ctrl F2");
        defKeyBinds.put("Back", "alt LEFT");
        defKeyBinds.put("Forward", "alt RIGHT");
        defKeyBinds.put("Import into current database", "ctrl I");
        defKeyBinds.put("Import into new database", "ctrl alt I");
        defKeyBinds.put("Increase table font size", "ctrl PLUS");
        defKeyBinds.put("Decrease table font size", "ctrl MINUS");
        defKeyBinds.put("Automatically link files", "alt F");
        defKeyBinds.put("Resolve duplicate BibTeX keys", "ctrl shift D");
        defKeyBinds.put("Refresh OO", "ctrl alt O");
        defKeyBinds.put("File list editor, move entry up", "ctrl UP");
        defKeyBinds.put("File list editor, move entry down", "ctrl DOWN");
        defKeyBinds.put("Minimize to system tray", "ctrl alt W");
    }

    private String getNextUnit(Reader data) throws IOException {
        int c;
        boolean escape = false, done = false;
        StringBuffer res = new StringBuffer();
        while (!done && ((c = data.read()) != -1)) {
            if (c == '\\') {
                if (!escape)
                    escape = true;
                else {
                    escape = false;
                    res.append('\\');
                }
            } else {
                if (c == ';') {
                    if (!escape)
                        done = true;
                    else
                        res.append(';');
                } else {
                    res.append((char)c);
                }
                escape = false;
            }
        }
        if (res.length() > 0)
            return res.toString();
        else
            return null;
    }

    private String makeEscape(String s) {
        StringBuffer sb = new StringBuffer();
        int c;
        for (int i=0; i<s.length(); i++) {
            c = s.charAt(i);
            if ((c == '\\') || (c == ';'))
                sb.append('\\');
            sb.append((char)c);
        }
        return sb.toString();
    }
    
    /**
     * Removes all entries keyed by prefix+number, where number
     * is equal to or higher than the given number.
     * @param number or higher.
     */
    public void purgeSeries(String prefix, int number) {
        while (get(prefix+number) != null) {
            remove(prefix+number);
            number++;
        }
    }

    /**
     * Exports Preferences to an XML file.
     *
     * @param filename String File to export to
     */
    public void exportPreferences(String filename) throws IOException {
      File f = new File(filename);
      OutputStream os = new FileOutputStream(f);
      try {
        prefs.exportSubtree(os);
      } catch (BackingStoreException ex) {
        throw new IOException(ex.getMessage());
      }
    }

      /**
       * Imports Preferences from an XML file.
       *
       * @param filename String File to import from
       */
      public void importPreferences(String filename) throws IOException {
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        try {
          Preferences.importPreferences(is);
        } catch (InvalidPreferencesFormatException ex) {
          throw new IOException(ex.getMessage());
        }
      }

    /**
     * Determines whether the given field should be written without any sort of wrapping.
     * @param fieldName The field name.
     * @return true if the field should not be wrapped.
     */
    public boolean isNonWrappableField(String fieldName) {
        return nonWrappableFields.contains(fieldName);
    }
}
