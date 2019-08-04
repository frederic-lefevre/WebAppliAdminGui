package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.awt.Color;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.webAppliAdmin.ApiEndPoint;
import com.ibm.lge.fl.webAppliAdmin.HmacGenerator;
import com.ibm.lge.fl.webAppliAdmin.HttpRequest;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class RequestTester  extends SwingWorker<String,String> {

	private static final String CONNEXION_UNAVAILABLE = "The connexion is unavailable" ;
	private static final String NULL_OR_EMPY_RESPONSE = "Null or empty response" ;
	
	private final AdvancedProperties testerProperties ;
	private final Logger			 tLog ;
	private final ApiEndPoint 	     apiEndPoint ;
	private final String			 url ;
	private final String			 body ;
	
	// Text area for the result
	private ButtonResponse buttonResponse ;
	
	public RequestTester(ApiEndPoint ap, String u, String b, ButtonResponse br, AdvancedProperties p, Logger l) {
		
		apiEndPoint		 = ap ;
		url				 = u ;
		body			 = b ;
		testerProperties = p ;
		tLog			 = l ;
		buttonResponse	 = br ;
	}

	 @Override 
	 public String doInBackground() {

		String response ;
		String method 				= apiEndPoint.getMethod() ;
		HmacGenerator hmacGenerator = apiEndPoint.getHmacGenerator() ;
		Charset charset 			= apiEndPoint.getCharset() ;
		
		buttonResponse.updatingMessage() ;
		HttpRequest sendReq = new HttpRequest(url, method, testerProperties, hmacGenerator, charset, tLog) ;
		if (sendReq.isAvailable()) {
			CharBuffer buffer = sendReq.send("", body) ;
			if ((buffer != null) && (buffer.length() > 0)) {
				int beginBufferLength = Math.max(buffer.length(), 81) - 1 ;
				buttonResponse.updatingMessage("\n\n  ===> Response received, formatting ...\n" + beginBufferLength + "...", Color.YELLOW) ;
				String rawString = buffer.toString() ;
				if (apiEndPoint.isJsonFormat()) {
					response = formatResponse(rawString) ;
				} else {
					response = rawString ;
				}
			} else {
				response = NULL_OR_EMPY_RESPONSE  ;
			}
			
		} else {
			response = CONNEXION_UNAVAILABLE ;
		}
		return response ;	
	 }
	 
	 private String formatResponse(String resp) {
		 return JsonUtils.jsonPrettyPrint(resp, tLog) ;
	 }
	 
	 @Override 
	 public void done() {
		 String resp = "Unknown response" ;
		 try {
			 resp = get() ;
		 } catch (InterruptedException e) {
			 resp = "InterruptedException getting response\n" + e ;
			 tLog.log(Level.SEVERE, resp, e);
		 } catch (ExecutionException e) {
			 resp = "ExecutionException getting response\n" + e ;
			 tLog.log(Level.SEVERE, "ExecutionException getting response", e);
		 }
		 buttonResponse.setResponse(resp) ;
		 buttonResponse.normalText();			
	 }
	 
}