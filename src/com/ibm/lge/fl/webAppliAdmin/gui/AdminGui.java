package com.ibm.lge.fl.webAppliAdmin.gui;

import java.awt.EventQueue;

import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;


public class AdminGui extends JFrame {


	private static final long serialVersionUID = 1L;

	public static Logger cLog ;
	
	private static final String DEFAULT_PROP_FILE = "webAppliAdmin.properties" ;

	
	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminGui window = new AdminGui();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public AdminGui() {
		
		// access to properties and logger
		RunningContext adminRunningContext = new RunningContext("Administration for web applications", null, DEFAULT_PROP_FILE);
		cLog = adminRunningContext.getpLog() ;

		cLog.info("Start Administration for web applications") ;
		
		AdvancedProperties adminProperties = adminRunningContext.getProps();
		AdvancedProperties apiProperties	= adminProperties.getPropertiesFromFile("webAppli.configurationFile") ;
		
		setBounds(20, 20, 1600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Administration console for web application application") ;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		LogGui logGui 			= new LogGui(apiProperties, cLog) ;
		LogLevelGui logLevelGui = new LogLevelGui(apiProperties, cLog) ;
		TesterGui testerGui 	= new TesterGui(apiProperties, cLog) ;
		
		JTabbedPane operationTab = new JTabbedPane() ;
		operationTab.addTab("Get logs", logGui.getLogPanel());
		operationTab.addTab("Manage logs", logLevelGui.getLogLevelPanel());
		operationTab.addTab("API Request Tester", testerGui.getTesterPanel());
		
		getContentPane().add(operationTab) ;
		
	
	}
	

}
