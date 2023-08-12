/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.webAppliAdmin;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.AdvancedProperties;

public class ApiEndPoint {

	private final static String METHOD_PROP  	  = ".method" ;
	private final static String PATH_PROP 	 	  = ".path" ;
	private final static String BODY_PROP 	 	  = ".body" ;
	private final static String CHARSET_PROP 	  = ".charset" ;
	private final static String BODY_CHARSET_PROP = ".bodyCharset" ;
	private final static String JSON_FORMAT  	  = ".jsonFormat" ;
	
	private final String  	  		  method ;
	private final String  	  		  path ;
	private final Vector<BodyRequest> bodies ;
	private final Charset 	  	  	  charset ;
	private final HmacGenerator		  hmacGenerator ;
	private final boolean   		  jsonFormat ;

	public ApiEndPoint(AdvancedProperties props, String baseProperty, Logger sLog) {
		
		method 		= props.getProperty(baseProperty + METHOD_PROP) ;
		path		= props.getProperty(baseProperty + PATH_PROP) ;
		jsonFormat 	= props.getBoolean(baseProperty + JSON_FORMAT, true) ;
		bodies		= new Vector<BodyRequest>() ;
		
		if (props.containsKey(baseProperty + BODY_PROP)) {
			// only one body
			Charset bodyCharset = getBodyCharset(props, baseProperty, sLog) ;
			bodies.add(new BodyRequest("body", props.getFileContent(baseProperty + BODY_PROP, bodyCharset))) ;
		} else {
			List<String> bodyProps = props.getKeysElements(baseProperty + BODY_PROP + ".") ;
			if (! bodyProps.isEmpty()) {
				Charset bodyCharset = getBodyCharset(props, baseProperty, sLog) ;
				for (String bp : bodyProps) {
					bodies.add(new BodyRequest(bp, props.getFileContent(baseProperty + BODY_PROP + "." + bp, bodyCharset))) ;
				}
			}
		} 
		
		String csStr = props.getProperty(baseProperty + CHARSET_PROP) ;
		Charset cs ;
		try {
			cs = Charset.forName(csStr) ;
		} catch (Exception e) {
			sLog.log(Level.SEVERE, "Exception when getting api interface charset with property " + baseProperty + CHARSET_PROP + ". Chartset default to UTF-8", e);
			cs	= StandardCharsets.UTF_8 ;
		}
		charset = cs ;
		
		hmacGenerator = new HmacGenerator(props, baseProperty, sLog) ;
	}

	public String getName() {
		return method + " " + path ;
	}
	
	public String toString() {
		return method + " " + path ;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public Vector<BodyRequest> getBodies() {
		return bodies;
	}

	public Charset getCharset() {
		return charset;
	}

	public HmacGenerator getHmacGenerator() {
		return hmacGenerator;
	}
	
	private Charset getBodyCharset(AdvancedProperties props, String baseProperty, Logger sLog) {
		
		String csBody = props.getProperty(baseProperty + BODY_CHARSET_PROP) ;
		Charset bodyCharset ;
		try {
			bodyCharset = Charset.forName(csBody) ;
		} catch (Exception e) {
			sLog.log(Level.SEVERE, "Exception when setting body file charset. Chartset set to default charset " + Charset.defaultCharset(), e);
			bodyCharset	= Charset.defaultCharset() ;
		}
		return bodyCharset ;
	}

	public boolean isJsonFormat() {
		return jsonFormat;
	}
}
