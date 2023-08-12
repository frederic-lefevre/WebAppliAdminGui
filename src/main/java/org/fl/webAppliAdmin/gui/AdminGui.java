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

import java.awt.EventQueue;

import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.swing.ApplicationTabbedPane;
import org.fl.webAppliAdmin.Control;

public class AdminGui extends JFrame {


	private static final long serialVersionUID = 1L;

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
		Control.init();
		RunningContext adminRunningContext = Control.getRunningContext();
		Logger cLog = Control.getLogger();

		cLog.info("Start Administration for web applications") ;
		
		AdvancedProperties adminProperties = adminRunningContext.getProps();
		AdvancedProperties apiProperties   = adminProperties.getPropertiesFromFile("webAppli.configurationFile") ;
		
		setBounds(20, 20, 1600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Administration console for web application application") ;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		LogGui logGui 			= new LogGui(apiProperties) ;
		LogLevelGui logLevelGui = new LogLevelGui(apiProperties, cLog) ;
		TesterGui testerGui 	= new TesterGui(apiProperties, cLog) ;
		
		ApplicationTabbedPane operationTab = new ApplicationTabbedPane(adminRunningContext) ;
		operationTab.add(logGui.getLogPanel(), 			 "Get rest api logs", 	 0);
		operationTab.add(logLevelGui.getLogLevelPanel(), "Manage rest api logs", 1);
		operationTab.add(testerGui.getTesterPanel(), 	 "API Request Tester", 	 2);

		operationTab.setSelectedIndex(0) ;
		
		getContentPane().add(operationTab) ;		
	}
	
}
