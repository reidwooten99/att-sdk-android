package com.att.iamsampleapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.MessageContent;
import com.att.api.oauth.OAuthToken;

public class MMSContent extends Activity {
	   
	private static final String TAG = "MMS Attachment Activity";
	String[] mmsContentName, mmsContentType, mmsContentUrl, mmsType;
	OAuthToken token;
	IAMManager iamManager;
	MessageContent msg;
	ListView MessageContentListView;
	ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mmscontent);

		Bundle ext = getIntent().getExtras();
		mmsContentName = (String[]) ext.get("MMSContentName");
		mmsContentType = (String[]) ext.get("MMSContentName");
		mmsContentUrl = (String[]) ext.get("MMSContentUrl");
		// mmsType = (String[]) ext.get("MMSType")
		token = new OAuthToken(Config.token, OAuthToken.NO_EXPIRATION,
				Config.refreshToken);

		String attachments = "";
		for (int n = 0; n < mmsContentName.length; n++) {
			if (n != 0 && !(mmsContentName[n - 1].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(", ");
			if (!(mmsContentName[n].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(mmsContentName[n]);

			iamManager = new IAMManager(Config.fqdn, token,
					new getMessageContentListener());
			String mmsContentDetails[] = mmsContentUrl[n].split("/");
			iamManager.GetMessageContent(
					mmsContentDetails[mmsContentDetails.length - 3],
					mmsContentDetails[mmsContentDetails.length - 1]);
		}
		TextView attachmentFiles = (TextView) findViewById(R.id.attachmentsFileName);
		attachmentFiles.setText(attachments.concat("."));
		
		MessageContentListView = (ListView) findViewById(R.id.messagecontentList);
		adapter=new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            listItems);
		MessageContentListView.setAdapter(adapter);
		
		MessageContentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View View, int position,
					long id) {
				// TODO Auto-generated method stub
				String fileName = listItems.get(position);
				String rootPath = Environment.getExternalStorageDirectory().getPath();				
				String dirPath = rootPath + "/InAppMessagingDownloads/";
				String filePath = dirPath + fileName;
				String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
    			MimeTypeMap mType =  MimeTypeMap.getSingleton();
    			String mimetype = mType.getMimeTypeFromExtension(extension.toLowerCase());
    			
    			if (mimetype.contains("video")) {
    				Uri uri = Uri.parse("content://" + filePath);			
    				Intent intent = new Intent(Intent.ACTION_VIEW)
    			    .setDataAndType(uri, "video/mp4");
    				startActivity(intent);
    			}
    			
    			if (mimetype.contains("image")) {
    				//Uri uri = Uri.parse("content:/" + filePath);
    				Uri uri = getImageContentUri(getApplicationContext(),filePath);
    				//Uri uri = Uri.parse(new File(Environment.getExternalStorageDirectory().getPath()).toString());
    				startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "image/png"));
    				
    			}
			}
		});
	       
	}
	
	
	public static Uri getImageContentUri(Context context, String  filePath) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            /*if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }*/
        }
		return null;
    }
	
	private class getMessageContentListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			 msg = (MessageContent) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageContentListener onSuccess : Message : "
								+ msg.getContentType(), Toast.LENGTH_LONG);
				toast.show();
			}

			InputStream stream = msg.getStream();		
			GetMessageContentTestTask getMessageContentTestTask = new GetMessageContentTestTask();
			getMessageContentTestTask.execute(stream);// Image
			/*String binaryData = msg.getContent();
			if (msg.getContentType().contains("TEXT/PLAIN")) {
				TextView txt = (TextView) findViewById(R.id.mmsmessage);
				txt.setText(binaryData);
			} else if (msg.getContentType().contains("IMAGE/")) {

				byte[] decodedString = Base64
						.decode(binaryData, Base64.URL_SAFE);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);
				ImageView image = (ImageView) findViewById(R.id.mmsImageAttachment);
				image.setImageBitmap(decodedByte);
			} else {
				Log.d(TAG, "MMS Attachment : " + binaryData);
			} */

					}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageContentListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	public class GetMessageContentTestTask extends AsyncTask<InputStream, Void, String> {

		@Override
		protected String doInBackground(InputStream... params) {
			// TODO Auto-generated method stub
			InputStream instream = params[0];
			String rootPath = Environment.getExternalStorageDirectory().getPath();
			
			String dirPath = rootPath + "/InAppMessagingDownloads/";
			File iamDir = new File( dirPath );
			if (!iamDir.exists()) {
				iamDir.mkdirs();
			}
			
			File file = null;		
			String[] contentTypeStr = msg.getContentType().split("="); 
			String filePath = dirPath + contentTypeStr[1];
			file = new File(filePath);

			//String path = "/storage/emulated/0/DCIM/Camera/IMG_TEST1_MMS.JPG";
			//String path = "/storage/emulated/0/DCIM/Camera/Audio_MMS.wav";
			//String path = "/storage/emulated/0/DCIM/Camera/Video_MMS.mp4";
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int len = 0;
			try {
				while ((len = instream.read(buffer)) != -1) {
				    output.write(buffer, 0, len);
				}
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return filePath;
		}
		
		@Override
		protected void onPostExecute(String filePath) {
			super.onPostExecute(filePath);
			if (null != filePath) {
			
				Toast toast = Toast.makeText(getApplicationContext(),
						"Bitmap is not NULL", Toast.LENGTH_SHORT);
				toast.show();
				String[] fileName = filePath.split("InAppMessagingDownloads/");
				listItems.add(fileName[1]);
				adapter.notifyDataSetChanged();
				
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"InputStream is NULL", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		
	}
	
public void writeLog(byte[] text, String fileName) {
		
    	if (!Environment.MEDIA_MOUNTED.equals(Environment
    			.getExternalStorageState()))
    	return;

    	File sdCard = Environment.getExternalStorageDirectory();
    	File logFile = new File(sdCard.getAbsolutePath() + File.separator + fileName);

    	// delete the old file
    	if (logFile.exists()) {
    		logFile.delete();
    	}

    	// create a new shiny file
    	try {
    		logFile.createNewFile();
    	} catch (IOException e) {
    		new AlertDialog.Builder(this).setMessage(
    				"Unable to create log email file: " + e.getMessage())
    				.show();
    	}

    	FileOutputStream outFileStream = null;
    	try {
    		outFileStream = new FileOutputStream(logFile);
    	} catch (FileNotFoundException e) {
    		return;
    	}

    	try {
			outFileStream.write(text);
			outFileStream.close();
    	} catch (IOException e) {
	    	new AlertDialog.Builder(this).setMessage(
	    	"Unable to create log email file: " + e.getMessage())
	    	.show();
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mmscontent, menu);
		return true;
	}

}
