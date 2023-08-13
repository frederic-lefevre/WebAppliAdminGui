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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fl.webAppliAdmin.LogInterface;
import org.fl.webAppliAdmin.LogInterfaceManager;
import org.fl.webAppliAdmin.gui.workers.RequestLogLevel;
import org.fl.webAppliAdmin.gui.workers.SetLogLevels;

import org.fl.util.AdvancedProperties;

public class LogLevelGui {

	private static final String showTitle = "Show logs property";
	private static final String changeTitle = "Change logging level";
	private static final String modeEmploi = "\n\n ===> Choose a log target and " + showTitle + " <===";

	private final static String logsBaseProperty = "webAppli.log.";

	private final LogInterfaceManager logInterfaceManager;
	private final Vector<LogInterface> logInterfaces;

	private final JPanel logLevelPanel;

	// Get and change level log button
	private final JButton changeLevelsButton;
	private final JButton getLevelsButton;

	// Combo box to choose the log
	private final JComboBox<LogInterface> logList;

	// Information panel (log content and messages)
	private final JTextArea logLevelContent;

	private final ButtonResponse setLevelButtonResponse;
	private final ButtonResponse getLevelButtonResponse;

	public LogLevelGui(AdvancedProperties adminProperties) {

		logInterfaceManager = new LogInterfaceManager(adminProperties, logsBaseProperty);

		logInterfaces = logInterfaceManager.getLogInterfaces();

		logLevelPanel = new JPanel();

		logLevelPanel.setLayout(new BoxLayout(logLevelPanel, BoxLayout.Y_AXIS));

		// Combo box to choose the log
		logList = new JComboBox<LogInterface>(logInterfaces);
		logList.setPreferredSize(new Dimension(200, 50));
		logList.setMaximumSize(new Dimension(300, 80));
		logList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		logLevelPanel.add(logList);

		JPanel levelActionPanel = new JPanel();
		levelActionPanel.setLayout(new BoxLayout(levelActionPanel, BoxLayout.X_AXIS));

		JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));

		// --------------------
		// Get logs properties (levels, formatter...)
		JPanel getLevelPanel = new JPanel();
		getLevelPanel.setPreferredSize(new Dimension(300, 650));
		getLevelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		getLevelsButton = new JButton(showTitle);
		getLevelsButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getLevelPanel.add(getLevelsButton);

		// --------------------
		// Change logs level
		JPanel changeLevelPanel = new JPanel();
		changeLevelPanel.setPreferredSize(new Dimension(300, 650));
		changeLevelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		changeLevelsButton = new JButton(changeTitle);
		changeLevelsButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		changeLevelsButton.setEnabled(false);
		changeLevelPanel.add(changeLevelsButton);

		commandPanel.add(getLevelPanel);
		commandPanel.add(changeLevelPanel);
		levelActionPanel.add(commandPanel);

		changeLevelsButton.addActionListener(new changeLogLevelListener());
		getLevelsButton.addActionListener(new getLevelsLogListener());

		// --------------------
		// Second column : display panel
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

		logLevelContent = new JTextArea(40, 120);
		logLevelContent.setEditable(true);
		logLevelContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane scrollPane1 = new JScrollPane(logLevelContent);
		logLevelContent.setText(modeEmploi);
		displayPanel.add(scrollPane1);

		// Add the command and display panels to the main window
		levelActionPanel.add(displayPanel);

		logLevelPanel.add(levelActionPanel);

		setLevelButtonResponse = new ButtonResponse(logLevelContent, changeLevelsButton);
		getLevelButtonResponse = new ButtonResponse(logLevelContent, getLevelsButton);

	}

	public JPanel getLogLevelPanel() {
		return logLevelPanel;
	}

	private class changeLogLevelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			String logLevelChoice = logLevelContent.getText();

			SetLogLevels setLogLevels = new SetLogLevels(logChoice, logLevelChoice, setLevelButtonResponse);
			setLogLevels.execute();
		}
	}

	private class getLevelsLogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LogInterface logChoice = (LogInterface) logList.getSelectedItem();
			RequestLogLevel requestLogLevel = new RequestLogLevel(logChoice, getLevelButtonResponse, changeLevelsButton);
			requestLogLevel.execute();
		}
	}
}
