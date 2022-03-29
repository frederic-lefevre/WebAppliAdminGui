package org.fl.webAppliAdmin.gui.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.gui.ButtonResponse;

public class RequestLogContent  extends SwingWorker<String,String> {

	private final LogInterface 	 logChoice ;
	private final boolean 		 askLogsCompression ;
	private final ButtonResponse getLogButtonResponse ;
	private final Logger 		 tLog ;

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
