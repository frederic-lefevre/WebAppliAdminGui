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

package org.fl.webAppliAdmin.gui.workers;

import java.awt.Color;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.webAppliAdmin.ApiEndPoint;
import org.fl.webAppliAdmin.HmacGenerator;
import org.fl.webAppliAdmin.HttpExchange;
import org.fl.webAppliAdmin.gui.ButtonResponse;

import org.fl.util.json.JsonUtils;

public class RequestTester  extends SwingWorker<String,String> {

	private static final String CONNEXION_UNAVAILABLE = "The connexion is unavailable";
	private static final String NULL_OR_EMPY_RESPONSE = "Null or empty response";

	private final static HttpClient httpClient = HttpClient.newHttpClient();

	private final Logger tLog;
	private final ApiEndPoint apiEndPoint;
	private final String url;
	private final String body;
	private final String queryParam;

	// Text area for the result
	private ButtonResponse buttonResponse;

	public RequestTester(ApiEndPoint ap, String u, String b, ButtonResponse br, boolean askCompression, Logger l) {

		apiEndPoint = ap;
		url = u;
		body = b;
		tLog = l;
		buttonResponse = br;
		if (askCompression) {
			queryParam = "?compressReturn=true";
		} else {
			queryParam = "";
		}
	}

	@Override
	public String doInBackground() {

		String response;
		String method = apiEndPoint.getMethod();
		HmacGenerator hmacGenerator = apiEndPoint.getHmacGenerator();
		Charset charset = apiEndPoint.getCharset();

		buttonResponse.updatingMessage();
		HttpExchange sendReq = new HttpExchange(httpClient, url, method, hmacGenerator, charset);
		if (sendReq.isAvailable()) {
			String buffer = sendReq.send(queryParam, body);
			buttonResponse.setDuration(sendReq.getLastRequestDuration());
			if ((buffer != null) && (buffer.length() > 0)) {
				int beginBufferLength = Math.max(buffer.length(), 81) - 1;
				buttonResponse.updatingMessage(
						"\n\n  ===> Response received, formatting ...\n" + beginBufferLength + "...", Color.YELLOW);

				if (apiEndPoint.isJsonFormat()) {
					response = formatResponse(buffer);
				} else {
					response = buffer;
				}
			} else {
				response = NULL_OR_EMPY_RESPONSE;
			}

		} else {
			response = CONNEXION_UNAVAILABLE;
		}
		return response;
	}

	private String formatResponse(String resp) {
		return JsonUtils.jsonPrettyPrint(resp, tLog);
	}

	@Override
	public void done() {
		String resp = "Unknown response";
		try {
			resp = get();
		} catch (InterruptedException e) {
			resp = "InterruptedException getting response\n" + e;
			tLog.log(Level.SEVERE, resp, e);
		} catch (ExecutionException e) {
			resp = "ExecutionException getting response\n" + e;
			tLog.log(Level.SEVERE, "ExecutionException getting response", e);
		}
		buttonResponse.setResponse(resp);
		buttonResponse.normalText();
	}
	 
}
