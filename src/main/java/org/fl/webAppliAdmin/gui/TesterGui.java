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

import org.fl.webAppliAdmin.ApiEndPoint;
import org.fl.webAppliAdmin.ApiEndPointManager;
import org.fl.webAppliAdmin.BodyRequest;
import org.fl.webAppliAdmin.Control;
import org.fl.webAppliAdmin.Host;
import org.fl.webAppliAdmin.HostManager;
import org.fl.webAppliAdmin.gui.workers.RequestTester;

import org.fl.util.AdvancedProperties;

public class TesterGui {

	private static final Logger tLog = Control.getLogger();
	
	private final static String testerHostBaseProperty = "webAppli.tester.host.";
	private final static String testerEndPointBaseProperty = "webAppli.tester.endPoint.";

	private final static String NO_BODY = "No body for this request";

	private final JPanel testerPanel;

	private final HostManager hostManager;
	private final Vector<Host> hostValues;

	private final ApiEndPointManager apiEndPointManager;
	private final Vector<ApiEndPoint> apiEndPoints;

	// Combo box to choose the host
	private final JComboBox<Host> hostList;

	// Combo box to choose the end point
	private final JComboBox<ApiEndPoint> endPointList;

	// Text field to edit the url
	private final JTextField urlEdit;

	// Text area for the body
	private final JTextArea bodyEdit;

	// Text area for the result
	private final JTextArea responseArea;

	// Button to send the request
	private final JButton sendButton;

	private final JCheckBox compressResponse;

	private final JComboBox<BodyRequest> bodyList;

	private final ButtonResponse testerButtonResponse;

	public TesterGui(AdvancedProperties adminProperties) {

		hostManager = new HostManager(adminProperties, testerHostBaseProperty);
		hostValues = hostManager.getHosts();

		apiEndPointManager = new ApiEndPointManager(adminProperties, testerEndPointBaseProperty);
		apiEndPoints = apiEndPointManager.getApiEndPoints();

		testerPanel = new JPanel();

		testerPanel.setLayout(new BoxLayout(testerPanel, BoxLayout.Y_AXIS));

		// Panel for choices : 2 combo boxes
		JPanel cPane = new JPanel();
		cPane.setLayout(new BoxLayout(cPane, BoxLayout.X_AXIS));
		testerPanel.add(cPane);

		// Combo box to choose the host
		hostList = new JComboBox<Host>(hostValues);
		hostList.setPreferredSize(new Dimension(200, 50));
		hostList.setMaximumSize(new Dimension(300, 80));
		hostList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cPane.add(hostList);

		// Combo box to choose the end point
		endPointList = new JComboBox<ApiEndPoint>(apiEndPoints);
		endPointList.setPreferredSize(new Dimension(300, 50));
		endPointList.setMaximumSize(new Dimension(600, 80));
		endPointList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cPane.add(endPointList);

		// Checkbox to ask for compression
		compressResponse = new JCheckBox("Ask compression");
		compressResponse.setSelected(false);
		cPane.add(compressResponse);

		// Execution time
		JLabel exDurationLbl = new JLabel(" Latest execution duration: ");
		exDurationLbl.setOpaque(true);
		exDurationLbl.setBackground(Color.WHITE);
		JLabel exDurationValue = new JLabel();
		exDurationValue.setOpaque(true);
		exDurationValue.setBackground(Color.WHITE);
		cPane.add(exDurationLbl);
		cPane.add(exDurationValue);

		// JPanel for Url and send button
		JPanel sPane = new JPanel();
		sPane.setLayout(new BoxLayout(sPane, BoxLayout.X_AXIS));
		testerPanel.add(sPane);

		// Text field to edit the url
		urlEdit = new JTextField(100);
		Font fontUrl = new Font("Verdana", Font.BOLD, 12);
		urlEdit.setPreferredSize(new Dimension(1600, 50));
		urlEdit.setFont(fontUrl);
		urlEdit.setHorizontalAlignment(JTextField.CENTER);
		urlEdit.setText(getCurrentUrl());
		sPane.add(urlEdit);

		sendButton = new JButton("Send");
		sendButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		sendButton.setBackground(Color.ORANGE);
		sPane.add(sendButton);

		sendButton.addActionListener(new SendRequest());
		;

		// Panel for the body and response
		JPanel tPane = new JPanel();
		tPane.setLayout(new BoxLayout(tPane, BoxLayout.X_AXIS));
		testerPanel.add(tPane);

		// Panel for body
		Vector<BodyRequest> bodies = ((ApiEndPoint) endPointList.getSelectedItem()).getBodies();
		JPanel bPane = new JPanel();
		bPane.setLayout(new BoxLayout(bPane, BoxLayout.Y_AXIS));
		tPane.add(bPane);

		// Combo box to choose the body
		bodyList = new JComboBox<BodyRequest>(bodies);
		bodyList.setPreferredSize(new Dimension(200, 50));
		bodyList.setMaximumSize(new Dimension(300, 80));
		bodyList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bPane.add(bodyList);
		bodyList.addActionListener(new UpdateBody());

		// Text area for the body
		bodyEdit = new JTextArea(30, 80);
		bodyEdit.setPreferredSize(new Dimension(800, 800));
		bodyEdit.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		JScrollPane scrollBody = new JScrollPane(bodyEdit);
		scrollBody.setPreferredSize(new Dimension(800, 800));
		updateCurrentBody();
		bPane.add(scrollBody);

		hostList.addActionListener(new UpdateUrl());
		endPointList.addActionListener(new UpdateUrl());
		endPointList.addActionListener(new UpdateBodyList());

		// Panel for response
		JPanel rPane = new JPanel();
		rPane.setLayout(new BoxLayout(rPane, BoxLayout.Y_AXIS));
		tPane.add(rPane);
		JLabel rLabel = new JLabel("Response");
		rPane.add(rLabel);

		// Text area for the response
		responseArea = new JTextArea(30, 80);
		responseArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		JScrollPane scrollResponse = new JScrollPane(responseArea);
		rPane.add(scrollResponse);

		testerButtonResponse = new ButtonResponse(responseArea, exDurationValue, sendButton);
	}

	public JPanel getTesterPanel() {
		return testerPanel;
	}

	private String getCurrentUrl() {

		String hostSelect = ((Host) hostList.getSelectedItem()).getHostPart();
		String pathSelect = ((ApiEndPoint) endPointList.getSelectedItem()).getPath();
		return hostSelect + pathSelect;
	}

	private void updateCurrentBody() {

		if (bodyList != null) {
			BodyRequest selectBody = (BodyRequest) bodyList.getSelectedItem();
			if (selectBody != null) {
				String body = selectBody.getBody();
				bodyEdit.setEditable(true);
				bodyEdit.setBackground(Color.WHITE);
				Font font = new Font("monospaced", Font.PLAIN, 14);
				bodyEdit.setFont(font);
				bodyEdit.setColumns(80);
				bodyEdit.setText(body);
			} else {
				printNoBody();
			}
		} else {
			printNoBody();
		}
	}

	private void printNoBody() {
		bodyEdit.setEditable(false);
		bodyEdit.setBackground(Color.GRAY);
		Font font = new Font("monospaced", Font.BOLD, 18);
		bodyEdit.setFont(font);
		bodyEdit.setColumns(30);
		bodyEdit.setText(NO_BODY);
	}

	private void updateCurrentBodyList() {
		Vector<BodyRequest> bodies = ((ApiEndPoint) endPointList.getSelectedItem()).getBodies();
		bodyList.removeAllItems();
		for (BodyRequest bodyRequest : bodies) {
			bodyList.addItem(bodyRequest);
		}
	}

	private class UpdateUrl implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			urlEdit.setText(getCurrentUrl());
		}
	}

	private class UpdateBody implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateCurrentBody();
		}
	}

	private class UpdateBodyList implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateCurrentBodyList();
		}
	}

	private class SendRequest implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ApiEndPoint apiEndPoint = (ApiEndPoint) endPointList.getSelectedItem();
			String url = urlEdit.getText();
			String body = bodyEdit.getText();
			boolean askCompression = compressResponse.isSelected();
			tLog.finest(() -> "EndPoint =" + apiEndPoint.getName());

			RequestTester requestTester = new RequestTester(apiEndPoint, url, body, testerButtonResponse,
					askCompression);
			requestTester.execute();
		}
	}

}