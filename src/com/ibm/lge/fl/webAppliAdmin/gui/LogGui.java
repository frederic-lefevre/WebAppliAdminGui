package com.ibm.lge.fl.webAppliAdmin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.swing.SearcherHighLighter;
import com.ibm.lge.fl.webAppliAdmin.LogInterface;
import com.ibm.lge.fl.webAppliAdmin.LogInterfaceManager;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.DeleteLogs;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.DeleteResizeLogs;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.RequestLogContent;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.RequestOperatingInfos;
import com.ibm.lge.fl.webAppliAdmin.gui.workers.RequestSmartEngineInformations;


public class LogGui {

	private Logger cLog ;
	
	private final static String logsBaseProperty = "webAppli.log." ;
	
	private LogInterfaceManager logInterfaceManager ;
	private Vector<LogInterface> logInterfaces ;
	
	private JPanel logPanel ;
	
	// Get and delete log button
	private JButton getButton ;
	private JButton deleteButton ;
	private JButton deleteResizeButton ;
	
	private JCheckBox compressLogs ;
	
	// Get operating infos button
	private JButton getOpInfoButton ;

	// Get smart engines infos button
	private JButton getSeInfoButton ;
	
	// Buttons and text field for search
	private JTextField searchText ;
	private JButton    searchButton ;
	private JButton    resetHighLightButton ;
	
	// Combo box to choose the log
	private JComboBox<LogInterface> logList ;
	
	// New size for in-memory buffers
	private JTextField resizeNum ;
	
	// Information panel (log content and messages)
	private JTextArea logContent ;
	
	private ButtonResponse getLogButtonResponse ;
	private ButtonResponse deleteLogButtonResponse ;
	private ButtonResponse deleteResizeLogButtonResponse ;
	private ButtonResponse getOpInfosButtonResponse ;
	private ButtonResponse getSeInfosButtonResponse ;
	
	private SearcherHighLighter searcherHighLighter ;
	
	public LogGui(AdvancedProperties adminProperties, Logger l) {

		cLog = l ;
		logInterfaceManager = new LogInterfaceManager(adminProperties, logsBaseProperty, cLog) ;
		
		logInterfaces = logInterfaceManager.getLogInterfaces() ;
		
		logPanel = new JPanel() ;
		
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

		JPanel serverChoicePanel = new JPanel() ;
		// Combo box to choose the log
		logList = new JComboBox<LogInterface>(logInterfaces) ;
		logList.setPreferredSize(new Dimension(400, 50));
		logList.setMaximumSize(new Dimension(600, 80));
		logList.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		serverChoicePanel.add(logList) ;
		logPanel.add(serverChoicePanel) ;
		
		JPanel mainPane = new JPanel() ;
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		logPanel.add(mainPane) ;
	
		// --------------------
		// First column : command panel
		JPanel commandPanel = new JPanel() ;
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));				
		
		// Buttons for operation on logs
		JPanel gbPanel = new JPanel() ;
		gbPanel.setLayout(new BoxLayout(gbPanel,  BoxLayout.X_AXIS));
		getButton = new JButton("Get logs") ;
		getButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		gbPanel.add(getButton) ;
		JPanel emptyPanelLog = new JPanel() ;
		emptyPanelLog.setPreferredSize(new Dimension(10, 10));
		gbPanel.add(emptyPanelLog) ;
		compressLogs = new JCheckBox("Compress logs") ;
		compressLogs.setSelected(false);
		gbPanel.add(compressLogs) ;
		commandPanel.add(gbPanel) ;
		
		JPanel emptyPanel1 = new JPanel() ;
		emptyPanel1.setPreferredSize(new Dimension(200, 10));
		commandPanel.add(emptyPanel1) ;
		
		JPanel dbPanel = new JPanel() ;
		dbPanel.setLayout(new BoxLayout(dbPanel,  BoxLayout.X_AXIS));
		deleteButton = new JButton("Delete all in-memory and in-DataBase logs") ;
		deleteButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		deleteButton.setBackground(Color.RED) ;
		dbPanel.add(deleteButton) ;
		commandPanel.add(dbPanel) ;

		JPanel emptyPanel2 = new JPanel() ;
		emptyPanel2.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel2) ;
		
		JPanel dbPanel2 = new JPanel() ;		
		dbPanel2.setLayout(new BoxLayout(dbPanel2,  BoxLayout.Y_AXIS));
		dbPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		deleteResizeButton = new JButton("Delete and Resize all in-memory and in-DataBase logs") ;
		deleteResizeButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		deleteResizeButton.setBackground(Color.RED) ;
		dbPanel2.add(deleteResizeButton) ;
		JPanel resizePanel = new JPanel() ;
		resizePanel.setLayout(new BoxLayout(resizePanel,  BoxLayout.X_AXIS));
		JLabel resizeLbl = new JLabel("New maximum size for in-memory buffers") ;
		resizeLbl.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		resizeNum = new JTextField(4) ;
		Font fontResize = new Font("Verdana", Font.BOLD, 18);
		resizeNum.setFont(fontResize);
		resizeNum.setBorder(BorderFactory.createLineBorder(Color.BLACK,10,true));
		resizeNum.setHorizontalAlignment(JTextField.CENTER) ;
		resizeNum.setMaximumSize(new Dimension(200, 60));
		dbPanel2.add(resizeLbl) ;
		resizePanel.add(resizeLbl) ;
		resizePanel.add(resizeNum) ;
		dbPanel2.add(resizePanel) ;
		commandPanel.add(dbPanel2) ;

		JPanel emptyPanel2_1 = new JPanel() ;
		emptyPanel2_1.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel2_1) ;
		
		JPanel gobPanel = new JPanel() ;
		gobPanel.setLayout(new BoxLayout(gobPanel,  BoxLayout.X_AXIS));
		getOpInfoButton = new JButton("Get application information") ;
		getOpInfoButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		gobPanel.add(getOpInfoButton) ;
		commandPanel.add(gobPanel) ;
		
		JPanel emptyPanel3 = new JPanel() ;
		emptyPanel3.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel3) ;
		
		JPanel gsbPanel = new JPanel() ;
		gsbPanel.setLayout(new BoxLayout(gsbPanel,  BoxLayout.X_AXIS));
		getSeInfoButton = new JButton("Get smart engine interfaces infos") ;
		getSeInfoButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		gsbPanel.add(getSeInfoButton) ;
		commandPanel.add(gsbPanel) ;
		
		JPanel emptyPanel4 = new JPanel() ;
		emptyPanel4.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel4) ;
		
		JPanel searchPanel = new JPanel() ;
		searchPanel.setLayout(new BoxLayout(searchPanel,  BoxLayout.X_AXIS));		
		searchText = new JTextField(20) ;
		searchButton = new JButton("Search") ;
		searchButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)) ;
		resetHighLightButton = new JButton("Reset") ;
		resetHighLightButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)) ;
		searchPanel.add(searchText) ;
		searchPanel.add(searchButton) ;
		searchPanel.add(resetHighLightButton) ;
		commandPanel.add(searchPanel) ;
		
		JPanel emptyPanel5 = new JPanel() ;
		emptyPanel5.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel5) ;
		
		getButton.addActionListener(new getLogListener());
		deleteButton.addActionListener(new deleteLogListener());
		deleteResizeButton.addActionListener(new deleteResizeLogListener());
		getOpInfoButton.addActionListener(new getOpInfoListener()) ;
		getSeInfoButton.addActionListener(new getSeInfoListener()) ;
		searchButton.addActionListener(new searchListener()) ;
		resetHighLightButton.addActionListener(new resetHighLightListener()) ;
		
		// --------------------
		// Second column : display panel
		JPanel displayPanel = new JPanel() ;
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		
		logContent = new JTextArea(40, 120);
		logContent.setEditable(false);
		logContent.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JScrollPane scrollPane1 = new JScrollPane(logContent) ;
		logContent.setText("\n\n ===> Choose a log target and an operation <===");
		displayPanel.add(scrollPane1) ;
		
		// Add the command and display panels to the main window
		mainPane.add(commandPanel) ;
		mainPane.add(displayPanel) ;
		
		getLogButtonResponse     	   = new ButtonResponse(logContent, getButton) ;
		deleteLogButtonResponse  	   = new ButtonResponse(logContent, deleteButton) ;
		deleteResizeLogButtonResponse  = new ButtonResponse(logContent, deleteResizeButton) ;
		getOpInfosButtonResponse 	   = new ButtonResponse(logContent, getOpInfoButton) ;
		getSeInfosButtonResponse 	   = new ButtonResponse(logContent, getSeInfoButton) ;
		
		searcherHighLighter = new SearcherHighLighter(logContent, cLog) ;
	}

	public JPanel getLogPanel() {
		return logPanel;
	}

	private class getLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			boolean askLogsCompression = compressLogs.isSelected() ;  
			RequestLogContent requestLogContent = new RequestLogContent(logChoice, askLogsCompression, getLogButtonResponse, cLog) ;
			requestLogContent.execute();			
		}
	}

	private class deleteLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			DeleteLogs deleteLogs = new DeleteLogs(logChoice, deleteLogButtonResponse, cLog) ;
			deleteLogs.execute();
		}
	}
	
	private class deleteResizeLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			String resizeNumStr = resizeNum.getText() ;
			try {
				Integer.parseInt(resizeNumStr) ;
				DeleteResizeLogs deleteResizeLogs = new DeleteResizeLogs(logChoice, deleteResizeLogButtonResponse, resizeNumStr, cLog) ;
				deleteResizeLogs.execute();
			} catch (NumberFormatException e) {
				Font font = new Font("monospaced", Font.BOLD, 18);
				logContent.setFont(font);
				logContent.setText("Enter a number. " + resizeNumStr + " is not a number");
				logContent.update(logContent.getGraphics());
			}
		}
	}
	
	private class searchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String searchStr = searchText.getText() ;
			
			if ((searchStr != null) && (! searchStr.isEmpty())) {
				searcherHighLighter.searchAndHighlight(searchStr, true);
			}
		}
	}
	
	private class resetHighLightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			searchText.setText("") ;
			searcherHighLighter.removeHighlights() ;			
		}
	}
	
	private class getOpInfoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			RequestOperatingInfos requestOpInfos = new RequestOperatingInfos(logChoice, getOpInfosButtonResponse, cLog) ;
			requestOpInfos.execute();			
		}
	}
	
	private class getSeInfoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			LogInterface logChoice = (LogInterface)logList.getSelectedItem() ;
			RequestSmartEngineInformations requestSeInfos = new RequestSmartEngineInformations(logChoice, getSeInfosButtonResponse, cLog) ;
			requestSeInfos.execute();			
		}
	}
}
