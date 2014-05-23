package com.att.api.error;

public class AttSdkError {
	
	private String errorMessage = null;
	private int httpResponseCode = 0;
	private String httpResponse = null;
	
	public AttSdkError() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AttSdkError(String errorMessage, int httpResponseCode, String httpResponse) {

		this.errorMessage = errorMessage;
		this.httpResponseCode = httpResponseCode;
		this.httpResponse = httpResponse;
		
	}
	public AttSdkError(int httpResponseCode, String httpResponse) {

		this.httpResponseCode = httpResponseCode;
		this.httpResponse = httpResponse;
		
	}

	public AttSdkError(String errorMessage) {
		
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public int getHttpResponseCode() {
		return httpResponseCode;
	}

	public String getHttpResponse() {
		return httpResponse;
	}

	
}
