package com.att.iamsampleapp;

public class Config {
	
	private Config() {} // can't instantiate
	
    static String fqdn() {
        return "https://api.att.com";
    }
    
    static String token() {
    	return "VxkUc0Zq5TIco4wHbwfKpjyQ4OxTIEwu";
    }
    
    static String refreshToken() {
    	return "wUCCK63b2MlPIiiHeOkhAASK0VBV5sDM";
    }
    
    static String clientID(){
    	return "yhqcpjz7flanrld8sqptx2fwhewxmp22";
    }
    
    static String secretKey(){
    	return "vvuxtumi4gkpmlio2dckired2uctldfh";
    }
    
    static String appScope() {
    	return "IMMN, MIM";
    }
    
    static int messageLimit(){
    	return 500;
    }
    
    static int getMessageOffset(){
    	return 0;
    }
}
