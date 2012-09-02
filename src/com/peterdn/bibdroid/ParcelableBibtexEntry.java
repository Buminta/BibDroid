package com.peterdn.bibdroid;

import java.util.ArrayList;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableBibtexEntry implements Parcelable {

	private BibtexEntry _entry;
		
	public ParcelableBibtexEntry(BibtexEntry entry) {
		_entry = entry;
	}
	
	public BibtexEntry getBibtexEntry() {
		return _entry;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(_entry.getId());
		dest.writeString(_entry.getType().getName());
		ArrayList<String> fields = new ArrayList<String>(_entry.getAllFields());
		dest.writeInt(fields.size());
		dest.writeStringList(fields);
		for (String field : fields) {
			dest.writeString(_entry.getField(field));
		}
	}

	public static final Parcelable.Creator<ParcelableBibtexEntry> CREATOR = new Parcelable.Creator<ParcelableBibtexEntry>() {
		@Override
		public ParcelableBibtexEntry createFromParcel(Parcel source) {
			return new ParcelableBibtexEntry(source);
		}

		@Override
		public ParcelableBibtexEntry[] newArray(int size) {
			return new ParcelableBibtexEntry[size];
		}
	};
	
	private ParcelableBibtexEntry(Parcel in) {
		_entry = new BibtexEntry(in.readString());
		_entry.setType(BibtexEntryType.getType(in.readString()));
		ArrayList<String> fields = new ArrayList<String>(in.readInt()); 
		in.readStringList(fields);
		for (String field : fields) {
			_entry.setField(field, in.readString());
		}
	}
}
