package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class RequestOperatingInfos extends SwingWorker<String,String> {

	private LogInterface logChoice ;
	private ButtonResponse getOpInfosButtonResponse ;
	private Logger tLog ;
	
	public RequestOperatingInfos(LogInterface logChoice, ButtonResponse getOpInfosButtonResponse, Logger tLog) {
		super();
		this.logChoice = logChoice;
		this.getOpInfosButtonResponse = getOpInfosButtonResponse;
		this.tLog = tLog;
	}

	@Override
	protected String doInBackground() throws Exception {
		
		getOpInfosButtonResponse.updatingMessage();
		String ret = logChoice.getOperatingInfos() ;
		return formatResponse(ret);
	}
	
	 @Override 
	 public void done() {
		 try {
			 getOpInfosButtonResponse.setResponse(get()) ;
			 getOpInfosButtonResponse.normalText();	
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
