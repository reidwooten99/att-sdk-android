package com.att.ads.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.text.format.Time;
import android.util.Log;

import com.att.ads.model.AdServiceResponse;

public class Utils {
	
	private static final String TAG = "Utils";
	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if((offset = resp.indexOf(start)) < 0)
			return "";
		if((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String scrapeIgnoreCase(String resp, String start, String stop) {
		int offset, len;
		String temp = resp.toLowerCase();
		start = start.toLowerCase();
		stop = stop.toLowerCase();
		
		if((offset = temp.indexOf(start)) < 0)
			return "";
		if((len = temp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String md5(String data) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			digester.update(data.getBytes());
			byte[] messageDigest = digester.digest();
			return Utils.byteArrayToHexString(messageDigest);
		} catch(NoSuchAlgorithmException e) {			
		}
		return null;
	}
	
	public static String byteArrayToHexString(byte[] array) {
		StringBuffer hexString = new StringBuffer();
		for (byte b : array) {
			int intVal = b & 0xff;
			if (intVal < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();		
	}
	
	public static String toHTML(AdServiceResponse adSvcResp) {
		return toHTML(adSvcResp.getType(), adSvcResp.getClickUrl(), adSvcResp.getImageUrl().getImage(), adSvcResp.getText());
	}

	public static String toHTML(String type, String clickUrl, String imageUrl, String text) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta name='viewport' content='target-density' dpi='device-dpi'/>");
		sb.append("<style>body{margin: 0px; padding: 0px; display:-webkit-box;-webkit-box-orient:horizontal;-webkit-box-pack:center;-webkit-box-align:center;}</style>");
		sb.append("</head><body><a href='");
		if(null == clickUrl || clickUrl.length()==0){
			sb.append("#");
		}else {
			sb.append(clickUrl);	
		}		
		sb.append("'>");
		if("image".equalsIgnoreCase(type)) {
			sb.append("<img src='");
			sb.append(imageUrl);
			sb.append("' border='0'/>");
		}else {
			sb.append(text);
		}		
		sb.append("</a></body></html>");
		return sb.toString();	
	}

	public static String toHTML(String content) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta name='viewport' content='target-density' dpi='device-dpi'/>");
		sb.append("<style>body{margin: 0px; padding: 0px; display:-webkit-box;-webkit-box-orient:horizontal;-webkit-box-pack:center;-webkit-box-align:center;}</style>");
		sb.append("</head><body>");
		//sb.append(content);
		String formattedContent = formattJSON(content);
		Log.i(TAG,"formattedContent is :"+formattedContent);
		sb.append(formattedContent);
		sb.append("</body></html>");
		return sb.toString();	
	}
	
	/*public static long getExpiresTimeInMS(int expiresIndInHours) {
		Date date = new Date();
		long currentTimeinMS = date.getTime();
		return currentTimeinMS + (expiresIndInHours*60*60*1000);
	}*/
	
	public static long getExpiresTimeInSeconds(int expiresIndInSeconds) {
		Date date = new Date();
		long currentTimeinMS = date.getTime();
		return ((currentTimeinMS/1000) + expiresIndInSeconds);
	}
	
	// Returns whether the token is expired or not. True - Expired, False - Not Expired
	public static boolean isExpires(long expiresInd) {
		if(expiresInd == 0)
			return false;

		Date date = new Date();
		long currentTimeinMS = date.getTime();
		return ((currentTimeinMS/1000)>expiresInd) ;
	}
	
	public static String getCurrentTime() {
		Time now = new Time(Time.getCurrentTimezone()); 
		now.setToNow();
		//sample :09-24 13:02:29.513
		return now.format("%m-%d T%H:%M:%S");
	}

	public static String formattJSON(String data) {
		String modS = data.replaceAll("\\\\", "");
		/*modS = modS.replaceAll("{", "{\n");
		modS = modS.replaceAll("}", "\n}");*/
		return modS;
	}
	
}
