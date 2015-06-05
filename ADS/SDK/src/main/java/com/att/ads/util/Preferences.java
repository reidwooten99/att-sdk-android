package com.att.ads.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This Class used to maintain the shared preferences usages in single place.
 * Which is having different types of data storage such as String, int, boolean,
 * long. This class is final.
 * 
 * @author ATT
 */

public final class Preferences {

	private SharedPreferences prefs;

	/**
	 * This constructor Initialize and hold the contents of the preferences file
	 * 'name', returning a SharedPreferences through which you can retrieve and
	 * modify its values. Only one instance of the SharedPreferences object is
	 * returned to any callers for the same name, meaning they will see each
	 * other's edits as soon as they are made. *
	 * 
	 * @param context
	 */
	public Preferences(Context context) {
		prefs = context.getSharedPreferences("ADS_API", Context.MODE_PRIVATE);
	}

	/**
	 * Set a integer value in the preferences by given key. 
	 */
	public boolean setInt(String key, int value) {
		Editor editor = prefs.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * Set a long value in the preferences by given key. 
	 */
	public boolean setLong(String key, long value) {
		Editor editor = prefs.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * Set a String value in the preferences by given key. 
	 */
	public boolean setString(String key, String value) {
		Editor editor = prefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * Set a boolean value in the preferences by given key. 
	 */
	public boolean setBoolean(String key, Boolean value) {
		Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
.	 * Retrieve a boolean value from the preferences by given key.
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	/**
	 * Retrieve a integer value from the preferences by given key.
	 */
	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	/**
	 * Retrieve a long value from the preferences by given key.
	 */
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	/**
	 * Retrieve a String value from the preferences by given key.
	 */
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	/**
	 * Check the value availability in the preferences by given key.
	 */
	public boolean containsValue(String key) {
		return prefs.contains(key);
	}

}
