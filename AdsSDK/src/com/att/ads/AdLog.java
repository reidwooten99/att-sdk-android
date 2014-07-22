package com.att.ads;

import android.util.Log;

/**
 * Customized Logger to enable and disable the logger as user required.
 * And also a single place to control all the logging options.
 * 
 * @author ATT
 *
 */
public class AdLog {
	
	/**
	 * Log level value to turn logging off. The default.
	 */
	public static final int LOG_LEVEL_NONE =0;
	
	/**
	 * Log level value to log errors only.
	 */
	public static final int LOG_LEVEL_1 =1;
	
	/**
	 * Log level value to log errors and warnings.
	 */
	public static final int LOG_LEVEL_2 =2;
	
	/**
	 * Log level value to log errors, warnings and server traffic.
	 */
	public static final int LOG_LEVEL_3 =3;
	
	/**
	 * Log value associated with errors.
	 */
	public static final int LOG_TYPE_ERROR =1;
	
	/**
	 * Log value associated with warnings. 
	 */
	public static final int LOG_TYPE_WARNING =2;
	
	/**
	 * Log value associated with misc. diagnostics information (server traffic, etc.)
	 */
	public static final int LOG_TYPE_INFO =3;
	
	private int CurrentLogLevel = 4;
	
	private ATTAdView adView;
	
	private static int DefaultLevel = LOG_LEVEL_NONE;
	
    /**
     * Set default log level to one of the log level values defined in he AdLog class
     * (corresponding to errors, errors + warnings, or everything including server traffic.)
     * 
     * @param logLevel Int log level to set as the default value (initially NONE).
     */
	public static void setDefaultLogLevel(int logLevel)
	{
		DefaultLevel = logLevel;
	}
	
	/**
	 * Construct logging object.
	 * @param adView Base view object associated with logging
	 */
	public AdLog(ATTAdView adView)
	{
		this.adView =  adView;
		setLogLevel(DefaultLevel);
	}
	
	/**
	 * Log a message
	 * @param Level Int logging level, from 0 (none) to 3 (errors, warnings and server traffic)
	 * @param Type Log Int message type, 1 (error, 2 (warning) or 3 (info) 
	 * @param tag String log message tag
	 * @param msg String log message detail
	 */
	public void log(int Level, int Type, String tag, String msg)
	{		
		String resultTag = "["+Integer.toHexString(adView.hashCode())+"]"+ tag;
		
		if(Level<=CurrentLogLevel)
		{
			switch(Type)
			{
			case LOG_TYPE_ERROR: Log.e(resultTag, msg+' '); break;
			case LOG_TYPE_WARNING: Log.w(resultTag, msg+' ');break;
			default:
				Log.i(resultTag, msg+' ');
			}
			
			//logInternal(resultTag + msg);
		}
	}

	/**
	 * Set log message level to be recorded; log events at a higher level are ignored.
	 * @param logLevel Int log level to be recorded
	 */
	public void setLogLevel(int logLevel)
	{
		CurrentLogLevel = logLevel;
		switch(logLevel)
		{
		case LOG_LEVEL_1:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_1");break;
		case LOG_LEVEL_2:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_2");break;
		case LOG_LEVEL_3:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_3");break;
		default:
			log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_NONE");
		}		

	}

}
