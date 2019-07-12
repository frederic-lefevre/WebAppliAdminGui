package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class SetLogLevels  extends SwingWorker<String,String> {

	private final LogInterface   logChoice ;
	private final String		 logLevelChoice ;
	private final ButtonResponse setLevelButtonResponse ;
	private final Logger tLog ;
	
	public SetLogLevels(LogInterface logChoice, String logLevelChoice, ButtonResponse setLevelButtonResponse, Logger tLog) {
		super();
		this.logChoice = logChoice;
		this.logLevelChoice = logLevelChoice ;
		this.setLevelButtonResponse = setLevelButtonResponse;
		this.tLog = tLog;
	}

	@Override
	protected String doInBackground() throws Exception {
		setLevelButtonResponse.updatingMessage();
		String ret ; 
		try {
			JsonObject levelJsonResp = new JsonParser().parse(logLevelChoice).getAsJsonObject() ;
			JsonObject levelJson = levelJsonResp.get("data").getAsJsonObject() ;
			ret = logChoice.changeLevel(levelJson) ;
		} catch (Exception e) {
			ret = "Exception parsing levels json " + e.toString() ;
		}
		return formatResponse(ret);
	}
	
	 @Override 
	 public void done() {
		 try {
			 setLevelButtonResponse.setResponse(get()) ;
			 setLevelButtonResponse.normalText();			
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
