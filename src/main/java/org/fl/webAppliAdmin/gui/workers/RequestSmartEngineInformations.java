package org.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.gui.ButtonResponse;

import org.fl.util.json.JsonUtils;

public class RequestSmartEngineInformations extends SwingWorker<String,String> {

	private final LogInterface    logChoice ;
	private final ButtonResponse  getSeInfosButtonResponse ;
	private final Logger 			tLog ;

	public RequestSmartEngineInformations(LogInterface lc, ButtonResponse br, Logger l) {
		super();
		logChoice 				 = lc ;
		getSeInfosButtonResponse = br ;
		tLog 					 = l;
	}

	@Override
	protected String doInBackground() throws Exception {
		
		getSeInfosButtonResponse.updatingMessage();
		String ret = logChoice.getSmartEnginesInfos() ;
		return formatResponse(ret);
	}
	
	 @Override 
	 public void done() {
		 try {
			 getSeInfosButtonResponse.setResponse(get()) ;
			 getSeInfosButtonResponse.normalText();	
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
