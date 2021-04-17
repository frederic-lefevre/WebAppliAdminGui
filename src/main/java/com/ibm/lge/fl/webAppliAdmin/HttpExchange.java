package com.ibm.lge.fl.webAppliAdmin;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.HttpUtils;

public class HttpExchange {

	// HTTP methods
	public final static String GET_METHOD 	  = "GET" ; 
	public final static String DELETE_METHOD  = "DELETE" ; 
	public final static String PUT_METHOD 	  = "PUT" ; 
	public final static String POST_METHOD 	  = "POST" ;
	
	//HTTP headers
	private final static String AUTHORIZATION = "Authorization" ;
	private final static String DEVICE_ID 	  = "Device-Id" ;
	private final static String TIMESTAMP 	  = "Timestamp" ;
	
	// Some useful string for http content type headers 
	private final static String CONTENT_TYPE 	= "Content-Type" ;
	private final static String APPLICATION_JSON = "application/json" ;
	private final static String CHAR_SET = ";charset=" ;

	// Request time out in seconds
	private final static long REQUEST_TIME_OUT = 120 ;
	
	private Logger lLog ;
	
	// HMAC element
	private final String uuid ;
	
	private final HmacGenerator hmacGenerator ;
	
	private final HttpClient httpClient ;	
	private final String  	 urlBase ;
	private final String  	 method ;
	private final Charset 	 charset ;
	private       boolean 	 available ;
	private       long    	 lastRequestDuration ;
	
	public HttpExchange(HttpClient hc, String u, String meth, HmacGenerator hg, Charset cs, Logger log) {
		
		available = true ;		
		
		lLog 	 	  		= log ;
		httpClient			= hc ;
		urlBase 	  		= u ;
		method 	 	  		= meth ;
		charset  	  		= cs ;
		hmacGenerator 		= hg ;
		uuid		  		= hmacGenerator.getUuid() ;
		lastRequestDuration = -1 ;
	}
	
	public String send(String pathParam, String body) {
		
		String url = urlBase ;
		if ((pathParam != null) && (! pathParam.isEmpty())) {
			url = url + pathParam ;
		} 
		
		CharBuffer resp ;
		try {
			URI uri = new URI(url) ;
			
			String path = uri.getPath() ;
				
			// Build HMAC http header
			long ts = System.currentTimeMillis()/1000 ;
			String timestamp = Long.toString(ts) ;
			
			String hmac = hmacGenerator.generate(method, path, timestamp) ;
			
			HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
			        .uri(uri) 
			        .setHeader(AUTHORIZATION, hmac)
			        .setHeader(DEVICE_ID, 	  uuid)
			        .setHeader(TIMESTAMP, 	  timestamp)
			        .timeout(Duration.ofSeconds(REQUEST_TIME_OUT));
						
			long start = System.nanoTime() ;
			try {
				// Send the request
				
				HttpRequest httpRequest ;
				switch (method) {
				case GET_METHOD : 
					httpRequest = httpRequestBuilder.GET().build() ;					
					break ;
				case POST_METHOD :
					httpRequest = httpRequestBuilder.setHeader(CONTENT_TYPE, APPLICATION_JSON + CHAR_SET + charset.name())
											.POST(HttpRequest.BodyPublishers.ofString(body))
											.build() ;
					break ;
				case PUT_METHOD :
					httpRequest = httpRequestBuilder.setHeader(CONTENT_TYPE, APPLICATION_JSON + CHAR_SET + charset.name())
											.PUT(HttpRequest.BodyPublishers.ofString(body))
											.build() ;
					break ;
				case DELETE_METHOD : 
					httpRequest = httpRequestBuilder.GET().build() ;
					break ;
				default : 
					httpRequest = null ;
					lLog.severe("Unknown HTTP method requested :\n" + method);
					break ;
				}
				HttpResponse<InputStream> response = httpClient.send(httpRequest, BodyHandlers.ofInputStream()) ;
				
				// check the response
				if (response != null) {
					if (! HttpUtils.isResponseCodeSucces(response)) {
						resp = CharBuffer.wrap("Bad HTTP response code:\n" + HttpUtils.readHttpResponseInfos(response)) ;
						lLog.warning(resp.toString());						
					} else {
						resp = HttpUtils.readHttpResponse(response, charset, lLog) ;

						if (lLog.isLoggable(Level.FINEST)) {
							lLog.finest("Success HTTP response code from request:\n" 
										+  HttpUtils.readHttpResponseInfos(response) + "\n\n"
										+ resp.toString());
						}
					}
				} else {
					resp = CharBuffer.wrap("Null http response to " + method + " " + url) ;
					lLog.severe(resp.toString());					
				}
				
			} catch (Exception e) {
				resp = CharBuffer.wrap("Exception http request on url " + url + " : " + e) ;
				lLog.log(Level.SEVERE, "Exception in http request send", e);
			}
			lastRequestDuration = System.nanoTime() - start ;
			
		} catch (URISyntaxException e) {
			resp = CharBuffer.wrap( "Malformed url: " + url + " : " + e) ;
			lLog.log(Level.SEVERE, "Malformed url: " + url, e);
			available = false ;
		}
		return resp.toString() ;
	}

	public boolean isAvailable() {
		return available;
	}

	public long getLastRequestDuration() {
		return lastRequestDuration;
	}
}
