package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import com.ibm.lge.fl.util.json.JsonUtils;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class RequestLogLevel  extends SwingWorker<String,String> {

	private LogInterface logChoice ;
	private ButtonResponse getLevelButtonResponse ;
	private JButton setLevelButton ;
	private Logger tLog ;
	
	public RequestLogLevel(LogInterface lc, ButtonResponse glbr, JButton slb, Logger l) {
		super();
		logChoice 				= lc;
		getLevelButtonResponse  = glbr;
		setLevelButton 			= slb ;
		tLog 					= l;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		getLevelButtonResponse.updatingMessage();
		String ret = logChoice.getLevels();
		return formatResponse(ret);
	}
	
	 @Override 
	 public void done() {
		 try {
			 getLevelButtonResponse.setResponse(get()) ;
			 getLevelButtonResponse.normalText();	
			 setLevelButton.setEnabled(true);
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
