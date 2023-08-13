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

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.webAppliAdmin.Control;
import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.gui.ButtonResponse;

import org.fl.util.json.JsonUtils;

public class DeleteResizeLogs  extends SwingWorker<String,String> {

	private static final Logger tLog = Control.getLogger();
	
	private final LogInterface logChoice;
	private final ButtonResponse deleteResizeLogButtonResponse;
	private final String newSizeStr;

	public DeleteResizeLogs(LogInterface lc, ButtonResponse br, String ns) {
		super();
		logChoice = lc;
		deleteResizeLogButtonResponse = br;
		newSizeStr = ns;
	}

	@Override
	protected String doInBackground() throws Exception {
		deleteResizeLogButtonResponse.updatingMessage();
		String ret = logChoice.deleteResize(newSizeStr);
		return formatResponse(ret);
	}

	@Override
	public void done() {
		try {
			deleteResizeLogButtonResponse.setResponse(get());
			deleteResizeLogButtonResponse.normalText();
		} catch (InterruptedException e) {
			tLog.log(Level.SEVERE, "InterruptedException getting response", e);
		} catch (ExecutionException e) {
			tLog.log(Level.SEVERE, "ExecutionException getting response", e);
		}
	}

	private String formatResponse(String resp) {
		return JsonUtils.jsonPrettyPrint(resp, tLog);
	}
}
