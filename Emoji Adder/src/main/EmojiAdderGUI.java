package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

public class EmojiAdderGUI extends JFrame {

	//11-26-2022
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3178219709360498882L;
	
	private AdderPane adderPane;
	private EditorPane editorPane;
	private OptionsPane optionsPane;
	
	public EmojiAdderGUI() {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(616, 439);
		setTitle("Emoji Adder");
		setIconImage(new ImageIcon(getClass().getResource("/cat-face-with-wry-smile_1f63c.png")).getImage());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		adderPane = new AdderPane();
		editorPane = new EditorPane();
		optionsPane = new OptionsPane(this);
		
		tabbedPane.addTab("Adder", adderPane);
		tabbedPane.addTab("Editor", editorPane);
		tabbedPane.addTab("Options", optionsPane);
		
		add(tabbedPane);
		
	}
	
	/**
	 * Keep some look and feel aspects that aren't in the layout.</br>
	 * Created because there was an issue where the EditorPane's feedback field would get a border after switching FlatLaf themes.
	 * 
	 */
	public void keepSomeNonLayoutLookAndFeel() {
		
		editorPane.keepFeedbackFieldNoBorder();
		
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
    		
			try {
				
//	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	            UIManager.setLookAndFeel(new FlatLightLaf());
	            
			} catch (Exception e) {
				
	            e.printStackTrace();
	        
			} 
			
    		new EmojiAdderGUI().setVisible(true);
			
		});
		
	}

}
