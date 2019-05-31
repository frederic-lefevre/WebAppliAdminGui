package com.ibm.lge.fl.webAppliAdmin;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.ibm.lge.fl.util.AdvancedProperties;

public class HmacGenerator {

	private final static String REQUEST         = "requestMethod=" ;
	private final static String URI				= "&uriPath=" ;
	private final static String CLIENT 			= "&clientId=" ;
	private final static String DEVICE 			= "&deviceId=" ;
	private final static String TIMESTAMP 		= "&timestamp=" ;

	private final String algorithm ;
	private final String secretKey ;
	private final String clientId  ;
	private final String uuid ;
	
	private Logger hLog ;
	
	private static Base64.Encoder base64Encoder = Base64.getEncoder() ;
	
	public HmacGenerator(AdvancedProperties hmacProperties, String baseProperty, Logger l) {
		
		algorithm = hmacProperties.getProperty(baseProperty + ".hmac.algorithm") ;
		secretKey = hmacProperties.getProperty(baseProperty + ".hmac.secretKey") ;
		clientId  = hmacProperties.getProperty(baseProperty + ".hmac.clientId") ;
		uuid	  = hmacProperties.getProperty(baseProperty + ".hmac.uuid") ;
		
		hLog = l ;
	}

	public String generate(String method, String path, String timestamp) {
				
		// Build the HMAC string to sign
		StringBuilder strToSign = new StringBuilder() ;
		strToSign.append(REQUEST).append(method) ;
		strToSign.append(URI).append(path);
		strToSign.append(CLIENT).append(clientId);
		strToSign.append(DEVICE).append(uuid);
		strToSign.append(TIMESTAMP).append(timestamp);
		
		hLog.fine("HMAC generation with :\n" + strToSign);
		
		// sign the string
		String hmacBuiltString = sha256hash(secretKey, algorithm, strToSign.toString(), hLog) ;
		
		// Check if HMAC are equals - return the result
		return hmacBuiltString;
	}
	
	// Sign a string with the secret key and the algorithm
	private String sha256hash(String secretKey, String algorithm, String strToHash, Logger hLog) {
		String hash = "";
	    try {
	        Mac sha256_HMAC = Mac.getInstance(algorithm);
	        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), algorithm);
	        sha256_HMAC.init(secret_key);

	        hash = base64Encoder.encodeToString(sha256_HMAC.doFinal(strToHash.getBytes()));
	    } catch (Exception e){
	    	hLog.log(Level.SEVERE, "Exception when hashing the string", e);
	    }
	    return hash;
	}

	public String getUuid() {
		return uuid;
	}
}
