package com.att.iamsampleapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Toast;

public class Utils {

	public static int UnreadBG = 0xFFF1F1F1;
	public static int ReadBG = 0xFFFFFFFF;

	private Utils() {
	}

	public static String getContactName(Context context, String phoneNumber) {
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return contactName;
	}

	public static void toastHere(Context ctx, String TAG, String message) {
		Toast toast = Toast.makeText(ctx, "Message : " + message,
				Toast.LENGTH_SHORT);
		toast.show();
	}
}
