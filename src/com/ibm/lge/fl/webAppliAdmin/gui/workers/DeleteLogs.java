package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class DeleteLogs  extends SwingWorker<String,String> {

	private LogInterface logChoice ;
	private ButtonResponse deleteLogButtonResponse ;
	private Logger tLog ;
	
	public DeleteLogs(LogInterface logChoice, ButtonResponse deleteLogButtonResponse, Logger tLog) {
		super();
		this.logChoice = logChoice;
		this.deleteLogButtonResponse = deleteLogButtonResponse;
		this.tLog = tLog;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		deleteLogButtonResponse.updatingMessage();
		String ret = logChoice.delete();
		return formatResponse(ret);
	}
	
	 @Override 
	 public void done() {
		 try {
			 deleteLogButtonResponse.setResponse(get()) ;
			 deleteLogButtonResponse.normalText();			
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
