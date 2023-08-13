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
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ButtonResponse {

	// Text area for the result
	private JTextArea responseArea;

	private JLabel responseDuration;

	private JButton sendButton;

	public ButtonResponse(JTextArea responseArea, JButton sendButton) {
		super();
		this.responseArea = responseArea;
		this.sendButton = sendButton;
		this.responseDuration = null;
	}

	public ButtonResponse(JTextArea responseArea, JLabel responseDuration, JButton sendButton) {
		super();
		this.responseArea = responseArea;
		this.responseDuration = responseDuration;
		this.sendButton = sendButton;
	}

	public void normalText() {
		Font font = new Font("monospaced", Font.PLAIN, 14);
		responseArea.setFont(font);
		responseArea.setBackground(Color.WHITE);
		responseArea.update(responseArea.getGraphics());
		sendButton.setEnabled(true);
	}

	public void updatingMessage() {
		updatingMessage("\n\n  ===> Waiting for response ...", Color.ORANGE);
	}

	public void updatingMessage(String msg, Color color) {
		sendButton.setEnabled(false);
		Font font = new Font("monospaced", Font.BOLD, 18);
		responseArea.setFont(font);
		responseArea.setBackground(Color.ORANGE);
		responseArea.setText("\n\n  ===> Waiting for response ...");
		responseArea.update(responseArea.getGraphics());
	}

	public void setResponse(String text) {
		responseArea.setText(text);
	}

	public void setDuration(long d) {
		if (responseDuration != null) {
			responseDuration.setText(formatDuration(d));
		}
	}

	private String formatDuration(long dur) {
		long ms = dur / 1000000;
		long ns = dur % 1000000;
		return ms + "ms " + ns + "ns";
	}
}
