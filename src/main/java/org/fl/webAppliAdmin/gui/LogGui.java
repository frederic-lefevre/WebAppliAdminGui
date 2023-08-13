/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.webAppliAdmin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fl.webAppliAdmin.Control;
import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.LogInterfaceManager;
import org.fl.webAppliAdmin.gui.workers.DeleteLogs;
import org.fl.webAppliAdmin.gui.workers.DeleteResizeLogs;
import org.fl.webAppliAdmin.gui.workers.RequestLogContent;
import org.fl.webAppliAdmin.gui.workers.RequestOperatingInfos;
import org.fl.webAppliAdmin.gui.workers.RequestSmartEngineInformations;

import org.fl.util.AdvancedProperties;
import org.fl.util.swing.text.SearchableTextPane;

public class LogGui {

	private static final Logger cLog = Control.getLogger();
	
	private final static String logsBaseProperty = "webAppli.log.";

	private final LogInterfaceManager logInterfaceManager;
	private final Vector<LogInterface> logInterfaces;

	private final JPanel logPanel;
	private final JPanel commandPanel;

	// Get, save and delete log button
	private final JButton getButton;
	private final JButton deleteButton;
	private final JButton deleteResizeButton;
	private final JButton saveButton;

	private final JCheckBox compressLogs;

	// Get operating infos button
	private final JButton getOpInfoButton;
	private final JCheckBox iPLookUp;

	// Get smart engines infos button
	private final JButton getSeInfoButton;

	// Combo box to choose the log
	private JComboBox<LogInterface> logList;

	// New size for in-memory buffers
	private final JTextField resizeNum;

	// Information panel (log content and messages)
	private final JTextArea logContent;

	private final SearchableTextPane searchableTextArea;

	private final ButtonResponse getLogButtonResponse;
	private final ButtonResponse deleteLogButtonResponse;
	private final ButtonResponse deleteResizeLogButtonResponse;
	private final ButtonResponse getOpInfosButtonResponse;
	private final ButtonResponse getSeInfosButtonResponse;

	private final static Color[] SEARCH_HIGHLIGHTCOLORS = {Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA} ;
	
	public LogGui(AdvancedProperties adminProperties) {

		logInterfaceManager = new LogInterfaceManager(adminProperties, logsBaseProperty);

		logInterfaces = logInterfaceManager.getLogInterfaces();

		logPanel = new JPanel();

		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

		JPanel serverChoicePanel = new JPanel();
		// Combo box to choose the server
		logList = new JComboBox<LogInterface>(logInterfaces);
		logList.setPreferredSize(new Dimension(400, 50));
		logList.setMaximumSize(new Dimension(600, 80));
		logList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		serverChoicePanel.add(logList);
		logPanel.add(serverChoicePanel);

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		logPanel.add(mainPane);

		// Log content
		logContent = new JTextArea(40, 120);
		logContent.setEditable(false);
		logContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		logContent.setText("\n\n ===> Choose a log target and an operation <===");

		searchableTextArea = new SearchableTextPane(logContent, SEARCH_HIGHLIGHTCOLORS, cLog);
		mainPane.add(searchableTextArea);

		// --------------------
		// Add controls to first column : command panel
		commandPanel = searchableTextArea.getCommandPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));

		// Buttons for operation on logs
		JPanel gbPanel = new JPanel();
		gbPanel.setLayout(new BoxLayout(gbPanel, BoxLayout.X_AXIS));
		getButton = new JButton("Get logs");
		getButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gbPanel.add(getButton);
		JPanel emptyPanelLog1 = new JPanel();
		emptyPanelLog1.setPreferredSize(new Dimension(10, 10));
		gbPanel.add(emptyPanelLog1);
		saveButton = new JButton("Save logs in file");
		saveButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gbPanel.add(saveButton);
		JPanel emptyPanelLog = new JPanel();
		emptyPanelLog.setPreferredSize(new Dimension(10, 10));
		gbPanel.add(emptyPanelLog);
		compressLogs = new JCheckBox("Compress logs");
		compressLogs.setSelected(false);
		gbPanel.add(compressLogs);
		commandPanel.add(gbPanel);

		// Empty panel to add space
		JPanel emptyPanel1 = new JPanel();
		emptyPanel1.setPreferredSize(new Dimension(200, 10));
		commandPanel.add(emptyPanel1);

		// Delete panel
		JPanel dbPanel = new JPanel();
		dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
		deleteButton = new JButton("Delete all in-memory and in-DataBase logs");
		deleteButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		deleteButton.setBackground(Color.RED);
		dbPanel.add(deleteButton);
		commandPanel.add(dbPanel);

		// Empty panel to add space
		JPanel emptyPanel2 = new JPanel();
		emptyPanel2.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel2);

		// Delete and resize log panel
		JPanel dbPanel2 = new JPanel();
		dbPanel2.setLayout(new BoxLayout(dbPanel2, BoxLayout.Y_AXIS));
		dbPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		deleteResizeButton = new JButton("Delete and Resize all in-memory and in-DataBase logs");
		deleteResizeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		deleteResizeButton.setBackground(Color.RED);
		dbPanel2.add(deleteResizeButton);
		JPanel resizePanel = new JPanel();
		resizePanel.setLayout(new BoxLayout(resizePanel, BoxLayout.X_AXIS));
		JLabel resizeLbl = new JLabel("New maximum size for in-memory buffers");
		resizeLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		resizeNum = new JTextField(4);
		Font fontResize = new Font("Verdana", Font.BOLD, 18);
		resizeNum.setFont(fontResize);
		resizeNum.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10, true));
		resizeNum.setHorizontalAlignment(JTextField.CENTER);
		resizeNum.setMaximumSize(new Dimension(200, 60));
		dbPanel2.add(resizeLbl);
		resizePanel.add(resizeLbl);
		resizePanel.add(resizeNum);
		dbPanel2.add(resizePanel);
		commandPanel.add(dbPanel2);

		// Empty panel to add space
		JPanel emptyPanel2_1 = new JPanel();
		emptyPanel2_1.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel2_1);

		// Get application info
		JPanel gobPanel = new JPanel();
		gobPanel.setLayout(new BoxLayout(gobPanel, BoxLayout.X_AXIS));
		getOpInfoButton = new JButton("Get application information");
		getOpInfoButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gobPanel.add(getOpInfoButton);
		iPLookUp = new JCheckBox("with IP addresses look up");
		iPLookUp.setSelected(false);
		gobPanel.add(iPLookUp);
		commandPanel.add(gobPanel);

		// Empty panel to add space
		JPanel emptyPanel3 = new JPanel();
		emptyPanel3.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel3);

		// Get smart engine infos
		JPanel gsbPanel = new JPanel();
		gsbPanel.setLayout(new BoxLayout(gsbPanel, BoxLayout.X_AXIS));
		getSeInfoButton = new JButton("Get smart engine interfaces infos");
		getSeInfoButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gsbPanel.add(getSeInfoButton);
		commandPanel.add(gsbPanel);

		// Empty panel to add space
		JPanel emptyPanel4 = new JPanel();
		emptyPanel4.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel4);

		// Empty panel to add space
		JPanel emptyPanel5 = new JPanel();
		emptyPanel5.setPreferredSize(new Dimension(200, 100));
		commandPanel.add(emptyPanel5);

		getButton.addActionListener(new getLogListener());
		saveButton.addActionListener(new saveLogContent());
		deleteButton.addActionListener(new deleteLogListener());
		deleteResizeButton.addActionListener(new deleteResizeLogListener());
		getOpInfoButton.addActionListener(new getOpInfoListener());
		getSeInfoButton.addActionListener(new getSeInfoListener());

		getLogButtonResponse = new ButtonResponse(logContent, getButton);
		deleteLogButtonResponse = new ButtonResponse(logContent, deleteButton);
		deleteResizeLogButtonResponse = new ButtonResponse(logContent, deleteResizeButton);
		getOpInfosButtonResponse = new ButtonResponse(logContent, getOpInfoButton);
		getSeInfosButtonResponse = new ButtonResponse(logContent, getSeInfoButton);

	}

	public JPanel getLogPanel() {
		return logPanel;
	}

	private class getLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			boolean askLogsCompression = compressLogs.isSelected();
			RequestLogContent requestLogContent = new RequestLogContent(logChoice, askLogsCompression,
					getLogButtonResponse, cLog);
			requestLogContent.execute();
		}
	}

	private class deleteLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			DeleteLogs deleteLogs = new DeleteLogs(logChoice, deleteLogButtonResponse);
			deleteLogs.execute();
		}
	}

	private class deleteResizeLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			String resizeNumStr = resizeNum.getText();
			try {
				Integer.parseInt(resizeNumStr);
				DeleteResizeLogs deleteResizeLogs = new DeleteResizeLogs(logChoice, deleteResizeLogButtonResponse,resizeNumStr);
				deleteResizeLogs.execute();
			} catch (NumberFormatException e) {
				Font font = new Font("monospaced", Font.BOLD, 18);
				logContent.setFont(font);
				logContent.setText("Enter a number. " + resizeNumStr + " is not a number");
				logContent.update(logContent.getGraphics());
			}
		}
	}

	private class getOpInfoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			boolean withIpLookUp = iPLookUp.isSelected();
			RequestOperatingInfos requestOpInfos = new RequestOperatingInfos(logChoice, withIpLookUp,
					getOpInfosButtonResponse, cLog);
			requestOpInfos.execute();
		}
	}

	private class getSeInfoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			RequestSmartEngineInformations requestSeInfos = new RequestSmartEngineInformations(logChoice,
					getSeInfosButtonResponse, cLog);
			requestSeInfos.execute();
		}
	}

	private class saveLogContent implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JFileChooser logChooser = new JFileChooser();
			logChooser.setApproveButtonText("Save log in file");
			int saveChoice = logChooser.showOpenDialog(logPanel);
			if (saveChoice == JFileChooser.APPROVE_OPTION) {
				File logFile = logChooser.getSelectedFile();

				try (BufferedWriter logWriter = Files.newBufferedWriter(logFile.toPath(), StandardCharsets.UTF_8)) {
					logContent.write(logWriter);
				} catch (Exception e) {
					cLog.log(Level.SEVERE, "Exception writing log file", e);
				}
			}

		}
	}
}
