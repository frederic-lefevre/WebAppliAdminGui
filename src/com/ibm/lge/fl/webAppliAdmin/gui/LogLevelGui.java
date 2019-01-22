package com.ibm.lge.fl.webAppliAdmin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.LogInterfaceManager;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.RequestLogLevel;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.SetLogLevels;

public class LogLevelGui {

	private static final String showTitle   = "Show logs property" ;
	private static final String changeTitle = "Change logging level" ;
	private static final String modeEmploi  = "\n\n ===> Choose a log target and " + showTitle + " <===" ;
	
	private final static String logsBaseProperty = "webAppli.log." ;
	
	private Logger cLog ;
	
	private LogInterfaceManager logInterfaceManager ;
	private Vector<LogInterface> logInterfaces ;
	
	private JPanel logLevelPanel ;
	
	// Get and change level log button
	private JButton changeLevelsButton ;
	private JButton getLevelsButton ;
	
	// Combo box to choose the log
	private JComboBox<LogInterface> logList ;
	
	// Information panel (log content and messages)
	private JTextArea logLevelContent ;
	
	private ButtonResponse setLevelButtonResponse ;
	private ButtonResponse getLevelButtonResponse ;
	
	public LogLevelGui(AdvancedProperties adminProperties, Logger l) {

		cLog = l ;
		logInterfaceManager = new LogInterfaceManager(adminProperties, logsBaseProperty, cLog) ;
		
		logInterfaces = logInterfaceManager.getLogInterfaces() ;
		
		logLevelPanel = new JPanel() ;
		
		logLevelPanel.setLayout(new BoxLayout(logLevelPanel, BoxLayout.Y_AXIS));
		
		// Combo box to choose the log
		logList = new JComboBox<LogInterface>(logInterfaces) ;
		logList.setPreferredSize(new Dimension(200, 50));
		logList.setMaximumSize(new Dimension(300, 80));
		logList.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		logLevelPanel.add(logList) ;
		
		JPanel levelActionPanel =  new JPanel() ;
		levelActionPanel.setLayout(new BoxLayout(levelActionPanel, BoxLayout.X_AXIS));
		
		JPanel commandPanel =  new JPanel() ;
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
		
		// --------------------
		// Get logs properties (levels, formatter...)
		JPanel getLevelPanel = new JPanel() ;
		getLevelPanel.setPreferredSize(new Dimension(300, 650));
		getLevelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		
		getLevelsButton = new JButton(showTitle) ;
		getLevelsButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		getLevelPanel.add(getLevelsButton) ;
		
		// --------------------
		// Change logs level
		JPanel changeLevelPanel = new JPanel() ;
		changeLevelPanel.setPreferredSize(new Dimension(300, 650));
		changeLevelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		
		changeLevelsButton = new JButton(changeTitle) ;
		changeLevelsButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		changeLevelsButton.setEnabled(false);
		changeLevelPanel.add(changeLevelsButton) ;
		
		commandPanel.add(getLevelPanel) ;
		commandPanel.add(changeLevelPanel) ;
		levelActionPanel.add(commandPanel) ;
		
		changeLevelsButton.addActionListener(new changeLogLevelListener());
		getLevelsButton.addActionListener(new getLevelsLogListener());
		
		// --------------------
		// Second column : display panel
		JPanel displayPanel = new JPanel() ;
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		
		logLevelContent = new JTextArea(40, 120);
		logLevelContent.setEditable(true);
		logLevelContent.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JScrollPane scrollPane1 = new JScrollPane(logLevelContent) ;
		logLevelContent.setText(modeEmploi);
		displayPanel.add(scrollPane1) ;
		
		// Add the command and display panels to the main window
		levelActionPanel.add(displayPanel) ;
		
		logLevelPanel.add(levelActionPanel) ;
		
		setLevelButtonResponse  = new ButtonResponse(logLevelContent, changeLevelsButton) ;
		getLevelButtonResponse  = new ButtonResponse(logLevelContent, getLevelsButton) ;

	}
	
	public JPanel getLogLevelPanel() {
		return logLevelPanel;
	}
	
	private class changeLogLevelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			String logLevelChoice  = logLevelContent.getText() ;

			SetLogLevels setLogLevels = new SetLogLevels(logChoice, logLevelChoice, setLevelButtonResponse, cLog) ;
			setLogLevels.execute();
		}
	}
	
	private class getLevelsLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			RequestLogLevel requestLogLevel = new RequestLogLevel(logChoice, getLevelButtonResponse, changeLevelsButton, cLog) ;
			requestLogLevel.execute();
		}
	}
}
