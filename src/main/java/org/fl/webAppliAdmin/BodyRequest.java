package org.fl.webAppliAdmin;

public class BodyRequest {

	private final String bodyName ;
	private final String body ;
	
	public BodyRequest(String n, String b) {
		
		bodyName = n ;
		body 	 = b ;		
	}

	public String getBodyName() {
		return bodyName;
	}

	public String getBody() {
		return body;
	}

	public String toString() {
		return bodyName ;
	}
}
