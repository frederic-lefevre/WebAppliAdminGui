package org.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.gui.ButtonResponse;

import org.fl.util.json.JsonUtils;

public class DeleteResizeLogs  extends SwingWorker<String,String> {

	private final LogInterface logChoice ;
	private final ButtonResponse deleteResizeLogButtonResponse ;
	private final Logger tLog ;
	private final String newSizeStr ;
	
	public DeleteResizeLogs(LogInterface lc, ButtonResponse br, String ns, Logger l) {
		super();
		logChoice 					  = lc;
		deleteResizeLogButtonResponse = br;
		newSizeStr					  = ns ;
		tLog 						  = l;
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
			 deleteResizeLogButtonResponse.setResponse(get()) ;
			 deleteResizeLogButtonResponse.normalText();			
		} catch (InterruptedException e) {
			tLog.log(Level.SEVERE, "InterruptedException getting response", e);
		} catch (ExecutionException e) {
			tLog.log(Level.SEVERE, "ExecutionException getting response", e);
		}
	 }
	 
	 private String formatResponse(String resp) {
		 return JsonUtils.jsonPrettyPrint(resp, tLog) ;
	 }
}
