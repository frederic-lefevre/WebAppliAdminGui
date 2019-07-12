package com.ibm.lge.fl.webAppliAdmin.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class ButtonResponse {

	// Text area for the result
	private JTextArea responseArea ;

	private JButton sendButton ;
	
	public ButtonResponse(JTextArea responseArea, JButton sendButton) {
		super();
		this.responseArea = responseArea;
		this.sendButton = sendButton;
	}
	
	 public void normalText() {
		 Font font = new Font("monospaced", Font.PLAIN, 14);
		 responseArea.setFont(font) ;
		 responseArea.setBackground(Color.WHITE) ;
		 responseArea.update(responseArea.getGraphics());
		 sendButton.setEnabled(true);
	 }
	 
	public void updatingMessage() {
		updatingMessage("\n\n  ===> Waiting for response ...", Color.ORANGE) ;
	}
	
	public void updatingMessage(String msg, Color color) {
		sendButton.setEnabled(false);
		Font font = new Font("monospaced", Font.BOLD, 18);
		responseArea.setFont(font) ;
		responseArea.setBackground(Color.ORANGE) ;
		responseArea.setText("\n\n  ===> Waiting for response ...");
		responseArea.update(responseArea.getGraphics());
	}
	
	public void setResponse(String text) {
		responseArea.setText(text);
	}
}
