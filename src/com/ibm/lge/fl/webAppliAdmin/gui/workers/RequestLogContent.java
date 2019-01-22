package com.ibm.lge.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.gui.ButtonResponse;

public class RequestLogContent  extends SwingWorker<String,String> {

	private LogInterface 	logChoice ;
	private boolean 		askLogsCompression ;
	private ButtonResponse  getLogButtonResponse ;
	private Logger 			tLog ;
	
	public RequestLogContent(LogInterface lc, boolean alc, ButtonResponse glbr, Logger l) {
		super();
		logChoice 			 = lc;
		askLogsCompression   = alc ;
		getLogButtonResponse = glbr;
		tLog 				 = l ;
	}

	@Override
	protected String doInBackground() throws Exception {
	
		getLogButtonResponse.updatingMessage();
		return logChoice.get(askLogsCompression);
	}
	 @Override 
	 public void done() {
		 try {
			 getLogButtonResponse.setResponse(get()) ;
			 getLogButtonResponse.normalText();			
		} catch (InterruptedException e) {
			tLog.log(Level.SEVERE, "InterruptedException getting response", e);
		} catch (ExecutionException e) {
			tLog.log(Level.SEVERE, "ExecutionException getting response", e);
		}
	 }
}
