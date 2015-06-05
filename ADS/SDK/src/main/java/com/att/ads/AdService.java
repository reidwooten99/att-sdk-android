package com.att.ads;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.att.ads.util.HttpClientFactory;

/**
 * Class to perform retrieve advertisement operation from server and assigned it
 * in a provided view.
 * 
 * @author ATT
 */
public class AdService {
	private static final String TAG = "AdService";
	private String accessToken = null;

	/**
	 * Constructor set the access Token.
	 * 
	 * @param accessToken
	 */
	public AdService(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Returns an ad response from server and sets result object to ad view.
	 * 
	 * @param url
	 * @param adView
	 * @param isCanceled
	 */
	public void getAd(String url, ATTAdView adView, boolean isCanceled) {
		String responseValue = null;
		BufferedInputStream bufferedInputStream = null;
		InputStream inputStream = null;
		HttpGet get = null;
		HttpClient client = null;
		try {
			System.setProperty("http.keepAlive", "false");
			client = HttpClientFactory.getThreadSafeClient();
			get = new HttpGet(url);
			Log.i(TAG, "accessToken :" + accessToken);
			get.addHeader(Constants.AUTHORIZATION, "BEARER " + accessToken);
			get.addHeader(Constants.XARG, "ClientSdk=att.ads.android." + Constants.SDK_VERSION);

			String udid = adView.getUdid();
			String category = adView.getCategory();
			if (null == udid) {
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_PARAMETER_ERROR,
						Constants.STR_UDID_PROBLEM));
				return;
			}
			if (null == category) {
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_PARAMETER_ERROR,
						Constants.STR_CATEGORY_PROBLEM));
				return;
			}

			get.addHeader(Constants.UDID, udid);
			String ua = adView.getUserAgent();
			get.addHeader("User-Agent", ua);
			Log.i(TAG, "user agent :" + ua);
			Log.i(TAG, "ads Service Request :" + url);
			Header[] harray = get.getAllHeaders();
			Log.i(TAG, "get.getAllHeaders() :");
			for (int i = 0; i < harray.length; i++) {
				Log.i(TAG, "Name: " + harray[i].getName() + " value:"
						+ harray[i].getValue());
			}

			HttpResponse response = client.execute(get);
			Log.i(TAG, "Response recieved " + response);

			if (null == response) {
				Log.e(TAG, Constants.STR_NULL_SERVER_RESPONSE);
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_ADSERVER_ERROR,
						Constants.STR_NULL_SERVER_RESPONSE));
				return;
			}

			if (response.getStatusLine().getStatusCode() != 200) {

				String erMessage = response.getStatusLine().toString();
				
				String responseBody = "";
				/* Get Resp Body */
				HttpEntity httpEntity = response.getEntity();
				if (null != httpEntity) {
					responseBody = EntityUtils.toString(httpEntity);
					if (null != responseBody && responseBody.length() > 0)
						erMessage = erMessage + " & HTTP Response - "
								+ responseBody;
				}

				Log.i(TAG, "Error Code : "
						+ response.getStatusLine().getStatusCode());
				Log.e(TAG, erMessage);
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_ADSERVER_ERROR, erMessage));

				// Clear access token in prefs for an unauthorized request
				if (response.getStatusLine().getStatusCode() == 401
						|| response.getStatusLine().getStatusCode() == 400) {
					Log.i(TAG, "Unauthorized access, clear cache");
					adView.clearCache();
				}

				return;
			}

			inputStream = response.getEntity().getContent();
			bufferedInputStream = new BufferedInputStream(inputStream, 1024);

			if (!isCanceled) {
				responseValue = readInputStream(bufferedInputStream, isCanceled);
			}

			Log.i(TAG, "ads Service Response :" + responseValue);

			if (null == responseValue || responseValue.length() == 0) {
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_ADSERVER_ERROR,
						Constants.STR_EMPTY_SERVER_RESPONSE));
				return;
			}

			if (!responseValue.contains("AdsResponse")) {
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_ADSERVER_ERROR, responseValue));
				return;
			}

			if (adView != null) {
				adView.setResult(responseValue, null);
			}
		} catch (ClientProtocolException e) {
			if (adView != null)
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_OAUTH_ERROR, e.toString() + ": "
								+ e.getMessage(), e));
		} catch (IOException e) {
			if (adView != null)
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_OAUTH_ERROR, e.toString() + ": "
								+ e.getMessage(), e));
		} finally {
			// In order to shut down the underlying
			// connection and release it back to the connection manager.
			get.abort();
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage() + e.fillInStackTrace());
				if (adView != null)
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_ADSERVER_ERROR, e.toString()
									+ ": " + e.getMessage(), e));
			}
		}

	}

	/**
	 * Converts an input stream sequence of the byte array to a string.
	 * 
	 * @param in
	 * @param isCanceled
	 * @return String
	 * @throws IOException
	 */
	private String readInputStream(BufferedInputStream in, boolean isCanceled)
			throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayBuffer byteBuffer = new ByteArrayBuffer(1);
		for (int n; (n = in.read(buffer)) != -1;) {
			if (isCanceled)
				return "";
			byteBuffer.append(buffer, 0, n);
		}
		return new String(byteBuffer.buffer(), 0, byteBuffer.length());
	}
}
