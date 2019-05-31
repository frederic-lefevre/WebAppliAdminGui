package com.ibm.lge.fl.webAppliAdmin;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.HttpContentTypeHeader;
import com.ibm.lge.fl.util.HttpHeader;
import com.ibm.lge.fl.util.HttpLink;
import com.ibm.lge.fl.util.HttpResponseContent;

public class HttpRequest {

	// HTTP methods
	public final static String GET_METHOD 	  = "GET" ; 
	public final static String DELETE_METHOD  = "DELETE" ; 
	public final static String PUT_METHOD 	  = "PUT" ; 
	public final static String POST_METHOD 	  = "POST" ;
	
	//HTTP headers
	private final static String AUTHORIZATION = "Authorization" ;
	private final static String DEVICE_ID 	  = "Device-Id" ;
	private final static String TIMESTAMP 	  = "Timestamp" ;
	
	private Logger lLog ;
	
	// HMAC element
	private final String uuid ;
	
	private final HmacGenerator hmacGenerator ;
	
	private final String  urlBase ;
	private final String  method ;
	private final Charset charset ;
	private       boolean available ;
	
	public HttpRequest(String u, String meth, AdvancedProperties props, HmacGenerator hg, Charset cs, Logger log) {
		
		available = true ;		
		
		lLog 	 	  = log ;
		urlBase 	  = u ;
		method 	 	  = meth ;
		charset  	  = cs ;
		hmacGenerator = hg ;
		uuid		  = hmacGenerator.getUuid() ;		
	}
	
	public CharBuffer send(String pathParam, String body) {
		
		String url = urlBase ;
		if ((pathParam != null) && (! pathParam.isEmpty())) {
			url = url + pathParam ;
		} 
		
		HttpLink urlLinkConnexion = new HttpLink(url, charset, lLog) ;
		
		CharBuffer resp = null ;
		try {
			URI uri = new URI(url) ;
			String path = uri.getPath() ;
				
			// Build HMAC http header
			long ts = System.currentTimeMillis()/1000 ;
			String timestamp = Long.toString(ts) ;
			
			String hmac = hmacGenerator.generate(method, path, timestamp) ;
			ArrayList<HttpHeader> httpHeaders = new ArrayList<HttpHeader>() ;
			httpHeaders.add(new HttpHeader(AUTHORIZATION, hmac)) ;
			httpHeaders.add(new HttpHeader(DEVICE_ID, 	  uuid)) ;
			httpHeaders.add(new HttpHeader(TIMESTAMP, 	  timestamp)) ;
						
			try {
				// Send the request
				HttpResponseContent httpResponse  ;
				switch (method) {
				case GET_METHOD : 
					httpResponse = urlLinkConnexion.sendGet( httpHeaders, true ) ;
					break ;
				case POST_METHOD :
					httpHeaders.add(new HttpContentTypeHeader(HttpContentTypeHeader.APPLICATION_JSON, charset)) ; 
					httpResponse = urlLinkConnexion.sendPost(body, httpHeaders, true ) ;
					break ;
				case PUT_METHOD :
					httpHeaders.add(new HttpContentTypeHeader(HttpContentTypeHeader.APPLICATION_JSON, charset))  ;
					httpResponse = urlLinkConnexion.sendPut(body, httpHeaders, true ) ;
					break ;
				case DELETE_METHOD : 
					httpResponse = urlLinkConnexion.sendDelete( httpHeaders, true ) ;
					break ;
				default : 
					httpResponse = null ;
					lLog.warning("Unknown HTTP method requested :\n" + method);
					break ;
				}
				
				// check the response
				if (httpResponse != null) {
					if (! httpResponse.isResponseReceived()) {
						resp = CharBuffer.wrap("No response received:\n" + httpResponse.toString()) ;
						lLog.warning(resp.toString());
					} else if (! httpResponse.isResponseCodeSucces()) {
						resp = CharBuffer.wrap("Bad HTTP response code:\n" + httpResponse.toString()) ;
						lLog.warning(resp.toString());						
					} else {
						resp = httpResponse.getContent() ;
						if (lLog.isLoggable(Level.FINEST)) {
							lLog.fine("Success HTTP response code from request:\n" + httpResponse.toString());
						}
					}
				} else {
					resp = CharBuffer.wrap("Null http response to " + method + " " + url) ;
					lLog.warning(resp.toString());					
				}
				
			} catch (Exception e) {
				resp = CharBuffer.wrap("Exception http request on url " + url + " : " + e) ;
				lLog.log(Level.SEVERE, "Exception in http request send", e);
			}
		
		} catch (URISyntaxException e) {
			lLog.log(Level.SEVERE, "Malformed url: " + url, e);
			available = false ;
		}
		return resp ;
	}

	public boolean isAvailable() {
		return available;
	}
}
