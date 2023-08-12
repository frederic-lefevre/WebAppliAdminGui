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

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.fl.util.AdvancedProperties;

public class HmacGenerator {

	private final static String REQUEST = "requestMethod=";
	private final static String URI = "&uriPath=";
	private final static String CLIENT = "&clientId=";
	private final static String DEVICE = "&deviceId=";
	private final static String TIMESTAMP = "&timestamp=";

	private final String algorithm;
	private final String secretKey;
	private final String clientId;
	private final String uuid;

	private static final Logger hLog = Control.getLogger();

	private static Base64.Encoder base64Encoder = Base64.getEncoder();

	public HmacGenerator(AdvancedProperties hmacProperties, String baseProperty) {

		algorithm = hmacProperties.getProperty(baseProperty + ".hmac.algorithm");
		secretKey = hmacProperties.getProperty(baseProperty + ".hmac.secretKey");
		clientId = hmacProperties.getProperty(baseProperty + ".hmac.clientId");
		uuid = hmacProperties.getProperty(baseProperty + ".hmac.uuid");
	}

	public String generate(String method, String path, String timestamp) {

		// Build the HMAC string to sign
		StringBuilder strToSign = new StringBuilder();
		strToSign.append(REQUEST).append(method);
		strToSign.append(URI).append(path);
		strToSign.append(CLIENT).append(clientId);
		strToSign.append(DEVICE).append(uuid);
		strToSign.append(TIMESTAMP).append(timestamp);

		hLog.fine("HMAC generation with :\n" + strToSign);

		// sign the string
		String hmacBuiltString = sha256hash(secretKey, algorithm, strToSign.toString());

		// Check if HMAC are equals - return the result
		return hmacBuiltString;
	}

	// Sign a string with the secret key and the algorithm
	private String sha256hash(String secretKey, String algorithm, String strToHash) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance(algorithm);
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), algorithm);
			sha256_HMAC.init(secret_key);

			hash = base64Encoder.encodeToString(sha256_HMAC.doFinal(strToHash.getBytes()));
		} catch (Exception e) {
			hLog.log(Level.SEVERE, "Exception when hashing the string", e);
		}
		return hash;
	}

	public String getUuid() {
		return uuid;
	}
}
