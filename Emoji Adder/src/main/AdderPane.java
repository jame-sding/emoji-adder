package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AdderPane extends JPanel {

	//11-26-2022
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3876242416256952065L;

	private JTextArea inputTextArea;
	private JTextArea outputTextArea;
	private JButton convertButton;
	private JButton cancelButton;
	private JButton clipboardButton;
	private JLabel feedbackLabel;
	
	private Thread currentConvertThread = null;
	
	public AdderPane() {
		
		final Font titleFont = new Font("Segoe UI", Font.BOLD, 36);
		final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 12);
		final Font ioTextFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
		final Font feedbackFont = new Font("Segoe UI", Font.PLAIN, 12);
		
		setLayout(new BorderLayout());
		
		
		
		
		//Title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		
		JLabel titleLabel = new JLabel("<html><p style=\"text-align:center;\">Emoji Adder</p></html>");
		titleLabel.setFont(titleFont);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(Box.createRigidArea(new Dimension(35, 35)));
		titlePanel.add(titleLabel);
		
		JLabel subtitleLabel = new JLabel("<html><p style=\"text-align:center;\">version " + Adder.VERSION + "</p></html>");
		subtitleLabel.setFont(subtitleFont);
		subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(Box.createRigidArea(new Dimension(7, 3)));
		titlePanel.add(subtitleLabel);
		titlePanel.add(Box.createRigidArea(new Dimension(12, 14)));
		
		add(titlePanel, BorderLayout.NORTH);
		
		
		
		//IO Panel (very large) (includes text boxes and buttons and feedback label)
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new BoxLayout(ioPanel, BoxLayout.Y_AXIS));
		
		
		//Text Panel
		JPanel textIOPanel = new JPanel();
		textIOPanel.setLayout(new BoxLayout(textIOPanel, BoxLayout.X_AXIS));
		
		inputTextArea = new JTextArea("the quick brown:: fox jumps over the lazy dog");
		inputTextArea.setFont(ioTextFont);
		inputTextArea.setWrapStyleWord(true);
		inputTextArea.setLineWrap(true);
		
		outputTextArea = new JTextArea("the quick brown fox 🦊 jumps over the lazy dog 🐶");
		outputTextArea.setEditable(false);
		outputTextArea.setFont(ioTextFont);
		outputTextArea.setWrapStyleWord(true);
		outputTextArea.setLineWrap(true);
		
		textIOPanel.add(new JScrollPane(inputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		textIOPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		textIOPanel.add(new JScrollPane(outputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		ioPanel.add(textIOPanel);
		
		
		//Buttons and Feedback Panel
		JPanel buttonsAndFeedbackPanel = new JPanel();
		buttonsAndFeedbackPanel.setLayout(new BoxLayout(buttonsAndFeedbackPanel, BoxLayout.X_AXIS));
		
		convertButton = new JButton("Convert");
		buttonsAndFeedbackPanel.add(convertButton);
		buttonsAndFeedbackPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		cancelButton = new JButton("Cancel");
		buttonsAndFeedbackPanel.add(cancelButton);
		buttonsAndFeedbackPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		clipboardButton = new JButton("Copy to Clipboard");
		buttonsAndFeedbackPanel.add(clipboardButton);
		buttonsAndFeedbackPanel.add(Box.createHorizontalGlue());
		
		addListeners();
		
		feedbackLabel = new JLabel("type in a sentence");
		feedbackLabel.setFont(feedbackFont);
		buttonsAndFeedbackPanel.add(feedbackLabel);
		
		ioPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		ioPanel.add(buttonsAndFeedbackPanel);
		ioPanel.add(Box.createRigidArea(new Dimension(45, 45)));
		
		add(ioPanel, BorderLayout.CENTER);
		add(new JLabel("     "), BorderLayout.WEST);
		add(new JLabel("     "), BorderLayout.EAST);
		
	}
	
	private void addListeners() {
		
		convertButton.addActionListener((e) -> {
			
			//Clear the output text area
			outputTextArea.setText("");
			
			//If the user clicked the convert button when the program was still converting something, cancel that conversion
			if(currentConvertThread != null && currentConvertThread.isAlive()) {
				
				cancelCurrentConversion();
				
			}
			
			currentConvertThread = new Thread(() -> {
				
				freezeControls(convertButton, clipboardButton);
				
				String inputTextAreaString = inputTextArea.getText().trim();
				
				//Counting the number of words in the input area by finding the number of spaces
				int totalWordCount = 1;
				for(int i = 0; i < inputTextAreaString.length(); i++) {
					
					if(inputTextAreaString.charAt(i) == ' ') {
						
						totalWordCount++;
						
					}
					
				}
				
				//Cycle through each word in the input
				Scanner textReader = new Scanner(inputTextAreaString);
				int wordCount = 0;
				while(textReader.hasNext()) {
					
					//For each word, output the word with the emoji
					outputTextArea.setText(outputTextArea.getText() + Adder.addWord(textReader.next()));
					wordCount++;
					feedbackLabel.setText("conversion progress: " + wordCount * 100 / totalWordCount + "%");
					
					//If the conversion gets interrupted (canceled)
					if(Thread.currentThread().isInterrupted()) {
						
						feedbackLabel.setText("canceled");
						break;
						
					}
					
				}
				
				textReader.close();
				
				unfreezeControls(convertButton, clipboardButton);
				
			});
			
			currentConvertThread.start();
			
		});
		
		cancelButton.addActionListener((e) -> {
			
			cancelCurrentConversion();
			
		});
		
		clipboardButton.addActionListener((e) -> {
			
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(outputTextArea.getText()), null);
			feedbackLabel.setText("copied to clipboard");
			
		});
		
	}
	
	private void unfreezeControls(JButton... buttons) {
		
		for(int i = 0; i < buttons.length; i++) {
			
			buttons[i].setEnabled(true);
			
		}
		
	}
	
	private void freezeControls(JButton... buttons) {
		
		for(int i = 0; i < buttons.length; i++) {
			
			buttons[i].setEnabled(false);
			
		}
		
	}
	
	public void cancelCurrentConversion() {
		
		//The thread is canceled by simply interrupting it (when interrupted, the loop in the thread is broken by the code)
		if(currentConvertThread != null && currentConvertThread.isAlive()) {
			
			feedbackLabel.setText("cancelling...");
			currentConvertThread.interrupt();
			
		}
		
		unfreezeControls(convertButton, clipboardButton);
		
	}
	
	public static void main(String[] args) {
		
		EmojiAdderGUI.main(args);
		
	}
	
}
