package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class OptionsPane extends JPanel {

	//11-27-2022
	//12-3-2023
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3566964973617813743L;
	
	private JButton helpButton;
	private EmojiAdderGUI mainGUIPanel;
	private HelpWindow currHelpWindow;
	private JButton lightDarkModeButton;
	private JButton resetGeneratedDictionaryButton;
	
	public OptionsPane(EmojiAdderGUI mainGUIPanel) {

		this.mainGUIPanel = mainGUIPanel;
		
		final Font titleFont = new Font("Segoe UI", Font.BOLD, 36);
		final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 12);
		
		setLayout(new BorderLayout());
		
		
		//Title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		
		JLabel titleLabel = new JLabel("<html><p style=\"text-align:center;\">Emoji Options</p></html>");
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
		
		
		//Content panel (I couldn't find a better name)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		helpButton = new JButton("Help");
		helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(Box.createVerticalGlue());
		contentPanel.add(helpButton);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		
		lightDarkModeButton = new JButton("Change to Dark Mode");
		lightDarkModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(lightDarkModeButton);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		
		resetGeneratedDictionaryButton = new JButton("[Debug] Reset Auto-Match Dictionary");
		resetGeneratedDictionaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(resetGeneratedDictionaryButton);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		contentPanel.add(Box.createVerticalGlue());
		
		add(contentPanel, BorderLayout.CENTER);
		
		
		currHelpWindow = new HelpWindow();
		
		addListeners();
		
	}
	
	private void addListeners() {
		
		helpButton.addActionListener((e) -> {
			
			currHelpWindow.setVisible(true);
			
		});
		
		lightDarkModeButton.addActionListener((e) -> {
			
			FlatLaf lookAndFeel;
			
			if(UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
				
				lookAndFeel = new FlatDarkLaf();
				lightDarkModeButton.setText("Change to Light Mode");
				
			} else {
				
				lookAndFeel = new FlatLightLaf();
				lightDarkModeButton.setText("Change to Dark Mode");
				
			}
			
			try {
				
				UIManager.setLookAndFeel(lookAndFeel);
				
			} catch (UnsupportedLookAndFeelException ex) {

				ex.printStackTrace();

			}
			
			SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(this.getParent()));
			SwingUtilities.updateComponentTreeUI(currHelpWindow);
			mainGUIPanel.keepSomeNonLayoutLookAndFeel();
			currHelpWindow.keepSomeNonLayoutLookAndFeel();
			
		});
		
		resetGeneratedDictionaryButton.addActionListener((e) -> {
			
			
			
		});
		
	}
	
	private static class HelpWindow extends JFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7875975337355705975L;
		
		private JScrollPane scrollPane;
		
		public HelpWindow() {
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(475, 475);
			setTitle("Emoji Help Window");
			setIconImage(new ImageIcon(getClass().getResource("/cat-face-with-wry-smile_1f63c.png")).getImage());
			setLayout(new BorderLayout());
			
			JPanel titleAndHelpPanel = new JPanel();
			titleAndHelpPanel.setLayout(new BoxLayout(titleAndHelpPanel, BoxLayout.Y_AXIS));
			
			JLabel titleLabel = new JLabel("<html><p style=\"text-align:center;\">Emoji Help</p></html>");
			titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			titleLabel.setHorizontalAlignment(JLabel.CENTER);
			titleAndHelpPanel.add(Box.createRigidArea(new Dimension(0, 20)));
			titleAndHelpPanel.add(titleLabel);
			titleAndHelpPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			
			JLabel helpLabel = new JLabel("<html>\r\n"
					+ "<p style=\"text-align:center;\">In the <b>Adder</b> tab, enter in a sentence for emojis to be added to it.<br></br>You can define which emojis are added to which words in the dictionary,<br></br>which is accessed through the <b>Editor</b> tab.<br></br>If you didn't define a word, the program will look for one that matches (in the <br></br>future, this auto-matching can be toggled off in the <b>Options</b> tab). <br></br>\r\n"
					+ "    <br></br>In the editor tab, you can add, remove, or edit keywords. <br></br>You can also add, remove, or edit the emojis associated with each keyword. <br></br>\r\n"
					+ "    <br></br>When entering in the sentence, emojis will be added in front of each most words.<br></br>An emoji won't be added if:<br></br>•You didn't define the word, and the program didn't find a suitable emoji<br></br>•You defined the word, but you associated zero emojis with it<br></br>•You put a double colon after the word (ex.: \"word::\")<br></br>\r\n"
					+ "    <br></br>The emoji will replace the word if the you surrounded it with colons, <br></br> and the word was the name of an emoji (ex.: \":flushed:\" would be replaced with<br></br>the flushed emoji itself). <br></br>\r\n"
					+ "    <br></br><i>This program was created using Java's Swing GUI library with FlatLaf.</i>"
					+ "    <br></br><br></br><b>Emoji Adder " + Adder.VERSION + ", haomao 2023\r\n"
					+ "</p>\r\n"
					+ "\r\n"
					+ "</html>");
			helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			helpLabel.setHorizontalAlignment(JLabel.CENTER);
			titleAndHelpPanel.add(helpLabel);
			
			scrollPane = new JScrollPane(titleAndHelpPanel);
			scrollPane.setBorder(null);
			add(scrollPane, BorderLayout.CENTER);
			add(new JLabel("     "), BorderLayout.WEST);
			add(new JLabel("     "), BorderLayout.EAST);
			
		}
		
		public void keepSomeNonLayoutLookAndFeel() {
			
			scrollPane.setBorder(null);
			
		}
		
	}
	
	public static void main(String[] args) {
		
		EmojiAdderGUI.main(args);
		
	}
	
}
