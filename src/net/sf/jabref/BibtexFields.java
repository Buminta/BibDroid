/*  Copyright (C) 2003-2011 Raik Nagel and JabRef contributors
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
//
// function : Handling of bibtex fields.
//            All bibtex-field related stuff should be placed here!
//            Because we can export these informations into additional
//            config files -> simple extension and definition of new fields....
//
// todo     : - handling of identically fields with different names
//              e.g. LCCN = lib-congress
//            - group id for each fields, e.g. standard, jurabib, bio....
//            - add a additional properties functionality into the
//              BibtexSingleField class
//
// modified : r.nagel 25.04.2006
//            export/import of some definition from/to a xml file

package net.sf.jabref ;

import java.util.HashMap;
import java.util.Vector;
import java.util.HashSet;

public class BibtexFields
{
  public static final String KEY_FIELD = "bibtexkey" ;

  // some internal fields
  public static final String
      SEARCH = "__search",
      GROUPSEARCH = "__groupsearch",
      MARKED = "__markedentry",
      OWNER = "owner",
      TIMESTAMP = "timestamp", // it's also definied at the JabRefPreferences class
      ENTRYTYPE = "entrytype",

      // Using this when I have no database open or when I read
      // non bibtex file formats (used by the ImportFormatReader.java)
      DEFAULT_BIBTEXENTRY_ID = "__ID" ;

  public static final String[] DEFAULT_INSPECTION_FIELDS = new String[]
          {"author", "title", "year", KEY_FIELD};


  // singleton instance
  private static final BibtexFields runtime = new BibtexFields() ;

  // contains all bibtex-field objects (BibtexSingleField)
  private HashMap<String, BibtexSingleField> fieldSet;

  // contains all known (and public) bibtex fieldnames
  private String[] PUBLIC_FIELDS = null ;

  private BibtexFields()
  {
    fieldSet = new HashMap<String, BibtexSingleField>();
    BibtexSingleField dummy = null ;

    // FIRST: all standard fields
    // These are the fields that BibTex might want to treat, so these
    // must conform to BibTex rules.
    add( new BibtexSingleField( "address", true ) ) ;
    // An annotation. It is not used by the standard bibliography styles,
    // but may be used by others that produce an annotated bibliography.
    // http://www.ecst.csuchico.edu/~jacobsd/bib/formats/bibtex.html
    add( new BibtexSingleField( "annote", true  ) ) ;
    add( new BibtexSingleField( "author", true ) ) ;
    add( new BibtexSingleField( "booktitle", true ) ) ;
    add( new BibtexSingleField( "chapter", true  ) ) ;
    add( new BibtexSingleField( "crossref", true ) ) ;
    add( new BibtexSingleField( "edition", true  ) ) ;
    add( new BibtexSingleField( "editor", true  ) ) ;
    add( new BibtexSingleField( "howpublished", true  ) ) ;
    add( new BibtexSingleField( "institution", true  ) ) ;

    dummy = new BibtexSingleField( "journal", true ) ;
    dummy.setExtras("journalNames");
    add(dummy) ;
    add( new BibtexSingleField( "key", true ) ) ;
    add( new BibtexSingleField( "month", true ) ) ;
    add( new BibtexSingleField( "note", true  ) ) ;
    add( new BibtexSingleField( "number", true ).setNumeric(true) ) ;
    add( new BibtexSingleField( "organization", true  ) ) ;
    add( new BibtexSingleField( "pages", true ) ) ;
    add( new BibtexSingleField( "publisher", true  ) ) ;
    add( new BibtexSingleField( "school", true  ) ) ;
    add( new BibtexSingleField( "series", true  ) ) ;
    add( new BibtexSingleField( "title", true ) ) ;
    add( new BibtexSingleField( "type", true  ) ) ;
    add( new BibtexSingleField( "language", true  ) ) ;
    add( new BibtexSingleField( "volume", true ).setNumeric(true) ) ;
    add( new BibtexSingleField( "year", true ).setNumeric(true) ) ;

    // some semi-standard fields
    dummy = new BibtexSingleField( KEY_FIELD, true ) ;
    dummy.setPrivate();
    add( dummy ) ;

    dummy = new BibtexSingleField( "doi", true ) ;
    dummy.setExtras("external");
    add(dummy) ;
    add( new BibtexSingleField( "eid", true  ) ) ;

    dummy = new BibtexSingleField( "date", true ) ;
    dummy.setPrivate();
    add( dummy ) ;

    add(new BibtexSingleField("pmid", false).setNumeric(true));

    // additional fields ------------------------------------------------------
    add( new BibtexSingleField( "location", false ) ) ;
    add( new BibtexSingleField( "abstract", false ) ) ;

    dummy =  new BibtexSingleField( "url", false) ;
    dummy.setExtras("external");
    add(dummy) ;

    dummy = new BibtexSingleField( "pdf", false ) ;
    dummy.setExtras("browseDoc");
    add(dummy) ;

    dummy = new BibtexSingleField( "ps", false ) ;
    dummy.setExtras("browseDocZip");
    add(dummy) ;
    add( new BibtexSingleField( "comment", false ) ) ;
    add( new BibtexSingleField( "keywords", false ) ) ;
    //FIELD_EXTRAS.put("keywords", "selector");


    dummy = new BibtexSingleField(Globals.FILE_FIELD, false);
    add(dummy);


    add( new BibtexSingleField( "search", false ) ) ;


    // some internal fields ----------------------------------------------
    dummy = new BibtexSingleField( OWNER, false ) ;
    dummy.setExtras("setOwner");
    dummy.setPrivate();
    add(dummy) ;

    dummy = new BibtexSingleField( TIMESTAMP, false ) ;
    dummy.setExtras("datepicker");
    dummy.setPrivate();
    add(dummy) ;

    dummy =  new BibtexSingleField( ENTRYTYPE, false ) ;
    dummy.setPrivate();
    add(dummy) ;

    dummy =  new BibtexSingleField( SEARCH, false) ;
    dummy.setPrivate();
    dummy.setWriteable(false);
    dummy.setDisplayable(false);
    add(dummy) ;

    dummy =  new BibtexSingleField( GROUPSEARCH, false) ;
    dummy.setPrivate();
    dummy.setWriteable(false);
    dummy.setDisplayable(false);
    add(dummy) ;

    dummy =  new BibtexSingleField( MARKED, false) ;
    dummy.setPrivate();
    dummy.setWriteable(true); // This field must be written to file!
    dummy.setDisplayable(false);
    add(dummy) ;

    // collect all public fields for the PUBLIC_FIELDS array
    Vector<String> pFields = new Vector<String>( fieldSet.size()) ;
    for (BibtexSingleField sField : fieldSet.values()){
      if (sField.isPublic() )
      {
        pFields.add( sField.getFieldName() );
        // or export the complet BibtexSingleField ?
        // BibtexSingleField.toString() { return fieldname ; }
      }
    }

    PUBLIC_FIELDS = pFields.toArray(new String[pFields.size()]);
    // sort the entries
    java.util.Arrays.sort( PUBLIC_FIELDS );

  }

    /**
     * Read the "numericFields" string array from preferences, and activate numeric
     * sorting for all fields listed in the array. If an unknown field name is included,
     * add a field descriptor for the new field.
     */
    public static void setNumericFieldsFromPrefs() {
        String[] numFields = Globals.prefs.getStringArray("numericFields");
        if (numFields == null)
            return;
        // Build a Set of field names for the fields that should be sorted numerically:
        HashSet<String> nF = new HashSet<String>();
        for (int i = 0; i < numFields.length; i++) {
            nF.add(numFields[i]);
        }
        // Look through all registered fields, and activate numeric sorting if necessary:
        for (String fieldName : runtime.fieldSet.keySet()) {
            BibtexSingleField field = runtime.fieldSet.get(fieldName);
            if (!field.isNumeric() && nF.contains(fieldName)) {
                field.setNumeric(nF.contains(fieldName));
            }
            nF.remove(fieldName); // remove, so we clear the set of all standard fields.
        }
        // If there are fields left in nF, these must be non-standard fields. Add descriptors for them:
        for (String fieldName : nF) {
            BibtexSingleField field = new BibtexSingleField(fieldName, false);
            field.setNumeric(true);
            runtime.fieldSet.put(fieldName, field);
        }

    }


  /** insert a field into the internal list */
  private void add( BibtexSingleField field )
  {
    // field == null check
    String key = field.name ;
    fieldSet.put( key, field ) ;
  }

  // --------------------------------------------------------------------------
  //  the "static area"
  // --------------------------------------------------------------------------
  private static final BibtexSingleField getField( String name )
  {
    if (name != null)
    {
      return runtime.fieldSet.get(name.toLowerCase()) ;
    }

    return null ;
  }

  public static String getFieldExtras( String name )
  {
    BibtexSingleField sField = getField( name ) ;
    if (sField != null)
    {
      return sField.getExtras() ;
    }
    return null ;
  }

  // returns an alternative name for the given fieldname
  public static String getFieldDisplayName( String fieldName )
  {
    BibtexSingleField sField = getField( fieldName ) ;
    if (sField != null)
    {
      return sField.getAlternativeDisplayName() ;
    }
    return null ;
  }

  public static boolean isWriteableField( String field )
  {
    BibtexSingleField sField = getField( field ) ;
    if (sField != null)
    {
      return sField.isWriteable() ;
    }
    return true ;
  }

  public static boolean isDisplayableField( String field )
  {
    BibtexSingleField sField = getField( field ) ;
    if (sField != null)
    {
      return sField.isDisplayable() ;
    }
    return true ;
  }

  /**
   * Returns true if the given field is a standard Bibtex field.
   *
   * @param field a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public static boolean isStandardField( String field )
  {
    BibtexSingleField sField = getField( field ) ;
    if (sField != null)
    {
      return sField.isStandard() ;
    }
    return false ;
  }

    public static boolean isNumeric( String field ) {
        BibtexSingleField sField = getField( field ) ;
        if (sField != null)
        {
            return sField.isNumeric() ;
        }
        return false ;
    }

  /** returns an string-array with all fieldnames */
  public static String[] getAllFieldNames()
  {
    return runtime.PUBLIC_FIELDS ;
  }

  /** returns the fieldname of the entry at index t */
  public static String getFieldName( int t )
  {
    return  runtime.PUBLIC_FIELDS[t] ;
  }

  /** returns the number of available fields */
  public static int numberOfPublicFields()
  {
    return runtime.PUBLIC_FIELDS.length ;
  }

  /*
     public static int getPreferredFieldLength(String name) {
     int l = DEFAULT_FIELD_LENGTH;
     Object o = fieldLength.get(name.toLowerCase());
     if (o != null)
     l = ((Integer)o).intValue();
     return l;
     }*/


  // --------------------------------------------------------------------------
  // a container class for all properties of a bibtex-field
  // --------------------------------------------------------------------------
  private static class BibtexSingleField
  {
    private static final int
        STANDARD       = 0x01,  // it is a standard bibtex-field
        PRIVATE        = 0x02,  // internal use, e.g. owner, timestamp
        DISPLAYABLE    = 0x04,  // These fields cannot be shown inside the source editor panel
        WRITEABLE      = 0x08 ; // These fields will not be saved to the .bib file.

    // the fieldname
    private String name ;

    // contains the standard, privat, displayable, writable infos
    // default is: not standard, public, displayable and writable
    private int flag = DISPLAYABLE | WRITEABLE ;

    // a alternative displayname, e.g. used for
    // "citeseercitationcount"="Popularity"
    private String alternativeDisplayName = null ;

    // the extras data
    // fieldExtras contains mappings to tell the EntryEditor to add a specific
    // function to this field, for instance a "browse" button for the "pdf" field.
    private String extras = null ;

    // This value defines whether contents of this field are expected to be
    // numeric values. This can be used to sort e.g. volume numbers correctly:
    private boolean numeric = false;

      // a comma separated list of alternative bibtex-fieldnames, e.g.
    // "LCCN" is the same like "lib-congress"
    // private String otherNames = null ;

    // a Hashmap for a lot of additional "not standard" properties
    // todo: add the handling in a key=value manner
    // private HashMap props = new HashMap() ;

    // some constructors ;-)

    public BibtexSingleField( String fieldName, boolean pStandard )
    {
      name = fieldName ;
      setFlag( pStandard, STANDARD) ;
    }

    private void setFlag( boolean onOff, int flagID)
    {
      if (onOff)  // set the flag
      {
        flag = flag | flagID ;
      }
      else // unset the flag,
      {
        flag = flag & ( 0xff ^ flagID ) ;
      }
    }

    private boolean isSet( int flagID )
    {
      if ( (flag & flagID) == flagID)
        return true ;

      return false ;
    }

    // -----------------------------------------------------------------------
    public boolean isStandard()
    {
      return isSet( STANDARD ) ;
    }

    public void setPrivate()
    {
      flag = flag | PRIVATE ;
    }

    public boolean isPublic()
    {
      return !isSet( PRIVATE ) ;
    }

    public void setDisplayable(boolean value)
    {
      setFlag( value, DISPLAYABLE ) ;
    }

    public boolean isDisplayable()
    {
      return isSet(DISPLAYABLE) ;
    }


    public void setWriteable(boolean value)
    {
      setFlag( value, WRITEABLE ) ;
    }

    public boolean isWriteable()
    {
      return isSet( WRITEABLE ) ;
    }

    public String getAlternativeDisplayName()
    {
      return alternativeDisplayName ;
    }
    // -----------------------------------------------------------------------

    public void setExtras( String pExtras)
    {
      extras = pExtras ;
    }

    // fieldExtras contains mappings to tell the EntryEditor to add a specific
    // function to this field, for instance a "browse" button for the "pdf" field.
    public String getExtras()
    {
      return extras ;
    }

    // -----------------------------------------------------------------------

    public String getFieldName()
    {
      return name ;
    }


      /**
       * Set this field's numeric propery
       * @param numeric true to indicate that this is a numeric field.
       * @return this BibtexSingleField instance. Makes it easier to call this
       *   method on the fly while initializing without using a local variable.
       */
      public BibtexSingleField setNumeric(boolean numeric) {
          this.numeric = numeric;
          return this;
      }

      public boolean isNumeric() {
          return numeric;
      }

  }
}
