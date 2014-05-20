package com.att.ads.model;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.att.ads.AdLog;
import com.att.ads.Constants;

/**
 * Class encapsulating all of the data and parameters associated with an ad server request.
 * 
 */
public class AdServiceRequest {

	//Header Params
	public static final String PARAM_ACCEPT	   = "Accept";
	public static final String PARAM_AUTHORIZATION= "Authorization";
	public static final String PARAM_UDID	   	   = "Udid";

	//private final String PARAM_USERAGENT = "ua";

	//Body/QueryString Params
	private static final String PARAM_CATEGORY	   = "Category";
	private static final String PARAM_GENDER	   = "Gender";
	private static final String PARAM_ZIPCODE	   = "ZipCode";
	private static final String PARAM_AREACODE	   = "AreaCode";
	private static final String PARAM_CITY 	   	   = "City"; 
	private static final String PARAM_COUNTRY	   = "Country";
	private static final String PARAM_LONGITUDE	   = "Longitude";
	private static final String PARAM_LATITUDE	   = "Latitude";
	private static final String PARAM_MAXHEIGHT	   = "MaxHeight";
	private static final String PARAM_MAXWIDTH	   = "MaxWidth";
	private static final String PARAM_MINHEIGHT	   = "MinHeight";
	private static final String PARAM_MINWIDTH	   = "MinWidth";
	private static final String PARAM_TYPE	   	   = "Type";
	private static final String PARAM_TIMEOUT	   = "Timeout";
	private static final String PARAM_AGEGROUP	   = "AgeGroup";
	private static final String PARAM_KEYWORDS	   = "Keywords";
	
	private Map<String, String> parameters = new HashMap<String, String>();



	private AdLog adLog;

	/**
	 * Construct an ad server request object
	 * @param AdLog Logging object for diagnostic information
	 */
	public AdServiceRequest(AdLog adLog) {
		this.adLog = adLog;
	}

	/**
	 * Get URL of ad server.
	 * @return
	 */
	public String getAdserverURL() {
		return Constants.ADS_URL;
	}

	/**
	 * Optional.
	 * Set Keywords to search ad delimited by commas.
	 * @param keywords
	 * @return
	 */
	public void setKeywords(String keywords) {
		if(keywords != null) {
			parameters.put(PARAM_KEYWORDS, keywords);
		} else{
			parameters.remove(PARAM_KEYWORDS);
		}

	}

	public void setGender(String gender) {

		if(gender != null) {
			parameters.put(PARAM_GENDER, gender);
		} else{
			parameters.remove(PARAM_GENDER);
		}

	}

	/**
	 * Optional.
	 * Set the age group of the demographic audience of the app.
	 * @param ageGroup
	 * @return
	 */
	public void setAgeGroup(String ageGroup) {
		if(ageGroup != null) {
			parameters.put(PARAM_AGEGROUP, ageGroup);
		} else{
			parameters.remove(PARAM_AGEGROUP);
		}
	}

	/**
	 * Optional.
	 * Set Country of visitor. See codes here (http://www.mojiva.com/docs/iso3166.csv). 
	 * Will override country detected by IP. 
	 * @param country
	 * @return
	 */
	public void setCountry(String country) {
		if(country != null) {
			parameters.put(PARAM_COUNTRY, country);
		} else{
			parameters.remove(PARAM_COUNTRY);
		}

	}

	/**
	 * Optional.
	 * Set Region of visitor. See codes for US and Canada here (http://www.mojiva.com/docs/iso3166_2.csv), 
	 * others - here (http://www.mojiva.com/docs/fips10_4.csv). 
	 * @param region
	 * @return
	 */
	public void setCategory(String category) {
		if(category != null) {
			parameters.put(PARAM_CATEGORY, category);
		} else{
			parameters.remove(PARAM_CATEGORY);
		}

	}

	/**
	 * Optional.
	 * Set City of the device user (with state). For US only. 
	 * @param city
	 * @return
	 */
	public void setCity(String city) {
		if(city != null) {
			parameters.put(PARAM_CITY, city);
		} else{
			parameters.remove(PARAM_CITY);
		}

	}

	/**
	 * Optional.
	 * Set Area code of a user. For US only. 
	 * @param area
	 * @return
	 */
	public void setAreaCode(Integer area) {
		if(area != null) {
			parameters.put(PARAM_AREACODE, area.toString());
		} else{
			parameters.remove(PARAM_AREACODE);
		}

	}

	/**
	 * Optional.
	 * Set Zip/Postal code of user. For US only. 
	 * @param zip
	 * @return
	 */
	public void setZipCode(Integer zip) {
		if(zip != null) {			
			parameters.put(PARAM_ZIPCODE, zip.toString());
		} else{
			parameters.remove(PARAM_ZIPCODE);
		}

	}

	/**
	 * Optional.
	 * Set Latitude.
	 * @param latitude
	 * @return
	 */
	public void setLatitude(Double latitude) {
		if(latitude != null) {
			if((latitude>=-90)&&(latitude<=90))
				parameters.put(PARAM_LATITUDE, String.valueOf(latitude));
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"latitude="+latitude+"  (valid: -90<=double<=90)");	
		} else{
			parameters.remove(PARAM_LATITUDE);
		}
	}

	/**
	 * Optional.
	 * Set Longitude.
	 * @param longitude
	 * @return
	 */
	public void setLongitude(Double longitude) {
		if(longitude != null) {
			if((longitude>=-180)&&(longitude<=180))
				parameters.put(PARAM_LONGITUDE, String.valueOf(longitude));
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"longitude="+longitude+" (valid: -180<=double<=180)");	
		} else{
			parameters.remove(PARAM_LONGITUDE);
		}
	}

	/**
	 * Optional.
	 * Set minimum width of advertising. 
	 * @param minSizeX
	 * @return
	 */
	public void setMinWidth(Integer minSizeX) {
		if(minSizeX != null) {
			if(minSizeX>0)
				parameters.put(PARAM_MINWIDTH, String.valueOf(minSizeX));
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"minSizeX="+minSizeX.toString()+" valid>0");			
		} else {
			parameters.remove(PARAM_MINWIDTH);
		}
	
	}

	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 * @return
	 */
	public void setMinHeight(Integer minSizeY) {
		if((minSizeY != null)) {
			if((minSizeY>0))
				parameters.put(PARAM_MINHEIGHT, String.valueOf(minSizeY));
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"minSizeY="+minSizeY.toString()+" valid>0");				
		} else {
			parameters.remove(PARAM_MINHEIGHT);
		}	
	}

	/**
	 * Type of ads to be returned (1 - text, 2 - image). 
	 * You can set different combinations with these values. 
	 * For example, 3 = 1 + 2 (text + image)
	 * @param type Int ad type, default: 3 (text or image)
	 * @return Reference to update ad server request object
	 */
	public void setType(Integer type) {
		if(type != null) {
			if((type>0)&&(type<4))
				parameters.put(PARAM_TYPE, String.valueOf(type));
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"type="+type.toString()+" (valid: 1<=int<=3, 1 - text, 2 - image set combinations as sum of this values)");
		} else {
			parameters.remove(PARAM_TYPE);
		}
	
	}

	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param sizeX
	 * @return
	 */
	public void setMaxWidth(Integer sizeX) {
		if(sizeX != null) {
			if(sizeX>0)
			{
				if (sizeX < getMinWidth())
				{
					parameters.put(PARAM_MAXWIDTH, parameters.get(PARAM_MINWIDTH));
					adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeX="+sizeX.toString()+" <minSizeX");
				}
				else
				{
					parameters.put(PARAM_MAXWIDTH, String.valueOf(sizeX));
				}
			}
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeX="+sizeX.toString()+" valid>0");
		} else {
			parameters.remove(PARAM_MAXWIDTH);
		}
	
	}

	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param sizeY
	 * @return
	 */
	public void setMaxHeight(Integer sizeY) {
		if(sizeY != null) {
			if(sizeY>0)
			{
				if (sizeY < getMinHeight())
				{
					parameters.put(PARAM_MAXHEIGHT, parameters.get(PARAM_MINHEIGHT));
					adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeY="+sizeY.toString()+" <minSizeY");
				}
				else
				{
					parameters.put(PARAM_MAXHEIGHT, String.valueOf(sizeY));
				}
			}
			else
				adLog.log(com.att.ads.AdLog.LOG_LEVEL_3, com.att.ads.AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeY="+sizeY.toString()+" valid>0");
		} else {
			parameters.remove(PARAM_MAXHEIGHT);
		}
	
	}

	public String getKeywords() {

		return parameters.get(PARAM_KEYWORDS);

	}

	public String getGender() {

		return parameters.get(PARAM_GENDER);

	}

	public String getAgeGroup() {

		return parameters.get(PARAM_AGEGROUP);

	}

	public String getCountry() {

		return parameters.get(PARAM_COUNTRY);

	}

	public String getCategory() {

		return parameters.get(PARAM_CATEGORY);

	}

	public String getCity() {

		return parameters.get(PARAM_CITY);

	}

	public Integer getAreaCode() {

		String areaCode = parameters.get(PARAM_AREACODE);
		return getIntParameter(areaCode,null);

	}

	public Integer getZipCode() {

		String zipCode = parameters.get(PARAM_ZIPCODE);
		return getIntParameter(zipCode,null);

	}

	public Double getLatitude() {

		String lat = parameters.get(PARAM_LATITUDE);
		if(null != lat){
			return Double.valueOf(lat);
		}
		return null;

	}

	public Double getLongitude() {

		String lon = parameters.get(PARAM_LONGITUDE);
		if(null != lon){
			return Double.valueOf(lon);
		}
		return null;		
		

	}

	public Integer getMinWidth() {

		String minSizeX = parameters.get(PARAM_MINWIDTH);
		return getIntParameter(minSizeX,0);

	}

	public Integer getMinHeight() {

		String minSizeY = parameters.get(PARAM_MINHEIGHT);
		return getIntParameter(minSizeY,0);

	}

	public Integer getType() {

		String type = parameters.get(PARAM_TYPE);
		return getIntParameter(type, Constants.DEFAULT_AD_TYPE);

	}

	public Integer getMaxWidth() {
		String sizeX = null;


		sizeX = parameters.get(PARAM_MAXWIDTH);

		if (sizeX != null)
		{
			return getIntParameter(sizeX,0);
		}

		/*
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return new Integer(metrics.widthPixels);
		 */
		return new Integer(0);
	}

	public Integer getMaxHeight() {
		String sizeY;


		sizeY = parameters.get(PARAM_MAXHEIGHT);


		if (sizeY != null)
		{
			return getIntParameter(sizeY,0);
		}

		return new Integer(0);
	}


	public Integer getTimeout() {
		String timeoutStr = parameters.get(PARAM_TIMEOUT);
		if(timeoutStr != null) {
			return getIntParameter(timeoutStr,null);
		}
		return Constants.DEFAULT_AD_SERVER_TIMEOUT;
	}

	public void setTimeout(Integer timeout) {
		if(timeout != null && timeout>0) 
			parameters.put(PARAM_TIMEOUT, String.valueOf(timeout));

		else
			parameters.put(PARAM_TIMEOUT, String.valueOf(Constants.DEFAULT_AD_SERVER_TIMEOUT));

	}

	private Integer getIntParameter(String stringValue, Integer defValue) {
		if(stringValue != null) {
			return Integer.parseInt(stringValue);
		} else {
			return defValue;
		}
	}


	/**
	 * Creates URL with given parameters.
	 * @return
	 * @throws IllegalStateException if all the required parameters are not present.
	 */
	public synchronized String createURL() throws IllegalStateException
	{
		return this.toString(); 
	}

	public String toString() {
		StringBuilder builderToString = new StringBuilder();
		synchronized(this)
		{
			builderToString.append(getAdserverURL());
		}
		builderToString.append("?");
		synchronized(parameters)
		{
			appendParameters(builderToString, parameters);
		}
		return  builderToString.toString();
	}

	private void appendParameters(StringBuilder builderToString, Map<String, String> parameters) {
		if(parameters != null) {
			Set<String> keySet = parameters.keySet();
			int i = 0;

			for (Iterator<String> parameterNames = keySet.iterator(); parameterNames.hasNext();) {
				String param = parameterNames.next();
				String value = parameters.get(param);
				if(i++ != 0)
					builderToString.append("&");

				if(value != null) {
					try {
						builderToString.append(URLEncoder.encode(param, "UTF-8") + "=");
						builderToString.append(URLEncoder.encode(value, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
