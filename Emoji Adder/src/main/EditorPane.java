package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import support.SimilarityCalc;

public class EditorPane extends JPanel {

	//11-26-2022
	
	private static final long serialVersionUID = 1858353079974678812L;
	
	private static final double SIMILARITY_THRESHOLD = 0;
	private static final String KEYWORD_ICON_DIR = "/keyword.png";
	private static final String EMOJI_ICON_DIR = "/emoji.png";
	
	private JTextField wordField;
	private JButton findButton;
	
	private JTree emojisTree;
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private JPopupMenu treePopupMenu;
	private JMenuItem treePopupMenuItemKeyword;
	private JMenuItem treePopupMenuItemEmoji;
	private JMenuItem treePopupMenuItemDelete;
	private String previousSelectedNodeContent = null;
	
	private JButton addButton;
	private JButton deleteButton;
	private JTextField newEmojiField;
	private JTextField feedbackField = new JTextField("[default text to initialize feedbackField so that it could be used when loading emojis for the tree]");
	
	private final ImageIcon keywordIcon = new ImageIcon(getClass().getResource(KEYWORD_ICON_DIR));
	private final ImageIcon emojiIcon = new ImageIcon(getClass().getResource(EMOJI_ICON_DIR));
	
	public EditorPane() {
		
		//DONE: Adder pane (on school computers) isn't displaying emojis properly (likely a DictFileEditor issue)
		//It's a school computer issue
		
		//DONE: Use a JTree instead of a JList
			//DONE: Remove button
				//IGNORED: If a keyword only has one emoji and the user removes it, replace the removed emoji in the user dict map with null
			//DONE: Add button
			//DONE: Edit functionality
		
		//DONE: Put all the files you need into the jar file
		
		//DONE (0.7 release): Find button should output the word
		
		//IGNORED (0.7 release): Allow user to see "suggested" generated associated emojis
			//Should be a non-editable node (can do this through the cell editor)
		
		//DONE (0.7 release): Allow user to add emojis directly from a popup menu (creates a new empty node that automatically allows user to edit)
		
		//DONE (0.7 release): More suitable icons for the JTree
			//DONE: Fix issue where icon changes back to default icon when node is being edited
		
		//RESOLVED (0.7 release): Make sure that when closing the window during any file I/O, the I/O won't corrupt. Also, overall thread safety
			//DONE: Prevent window closing during file I/O from corrupting
			//DONE: AdderPane safety (finished a long time ago)
			//RESOLVED: EditorPane safety (use multithreading in EditorPane just like how you used multithreading in AdderPane) 
		
		//DOING (done if you're seeing this right now): Make a complete backup of the 0.7 release
		
		//DONE (0.7.1 release): Remove all unused classes
		
		//DONE (1.0 release): Settings and help tab
			//DONE (1.0 release): Allow for dark mode
		
		//DONE (1.0 release): Use FlatLaf
		
		//RESOLVED (1.0 release): Find better JTree icons (adjust to FlatLaf)
		
		//1.0.1 working changelog:
		//Bug (fixed): feedback text field gets a border after switching light/dark themes
		//Changed the style of the help window text
		
		//TODO (1.0.1 release): Give user option to automatically add suggested emojis or not
		
		//TODO (1.0.1 release): Add a reset generated dictionary button in OptionsPane
		
		//TODO (1.0.1 release): Condense all stored data into one file
		
		//TODO (1.0.1 release): Save the settings in a config file
				
		//TODO (1.1 release): Have an emoji keyboard (a table of some sort)
				
		final Font titleFont = new Font("Segoe UI", Font.BOLD, 36);
		final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 12);
		final Font ioTextFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
		final Font feedbackFont = new Font("Segoe UI", Font.PLAIN, 12);
		
		setLayout(new BorderLayout());
		
		
		
		
		//Title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		
		JLabel titleLabel = new JLabel("<html><p style=\"text-align:center;\">Emoji Editor</p></html>");
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
		
		
		
		//IO Panel (very large)
		JPanel ioPanel = new JPanel(new GridBagLayout());
		
		
		//Left panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		
		//The JTree
		rootNode = new DefaultMutableTreeNode("[root]");
		treeModel = new DefaultTreeModel(rootNode);
		loadEmojisTree();
		emojisTree = new JTree(treeModel);
		emojisTree.setShowsRootHandles(true);
		emojisTree.setRootVisible(false);
		emojisTree.setFont(ioTextFont);
		emojisTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		emojisTree.setEditable(true);
		emojisTree.setCellRenderer(new DefaultTreeCellRenderer() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -8785520926673960566L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				
				super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;			
				if(node.getAllowsChildren() == true) {
					
					//If the tree node represents a keyword
					setIcon(keywordIcon);
					
				} else {
					
					//If the tree node represents an emoji
					setIcon(emojiIcon);
					
				}
				
				System.out.println(node.getUserObject());
				System.out.println("getTreeCellRendererComponent: " + feedbackField.getBorder());
				
				return this;
				
			}
			
		});
		emojisTree.setCellEditor(new DefaultTreeCellEditor(emojisTree, (DefaultTreeCellRenderer) emojisTree.getCellRenderer()) {
			
			@Override
			/**
			 * 
			 * (Completely copied from DefaultTreeCellEditor.class
			 *
		     */
		    protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
				
		        if(renderer != null) {
		        	
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		        	
					if(node.getAllowsChildren() == true) {
						
						//Is a keyword
						editingIcon = keywordIcon;
						
					} else {
						
						//Is an emoji
						editingIcon = emojiIcon;
						
					}
		            
		            //Copied from DefaultTreeCellEditor.class
		            if(editingIcon != null)
		                offset = renderer.getIconTextGap() +
		                         editingIcon.getIconWidth();
		            else
		                offset = renderer.getIconTextGap();
		            
		        } else {
		        	
		        	System.out.println("BIG NUT");
		        	//Copied from DefaultTreeCellEditor.class
		            editingIcon = null;
		            offset = 0;
		            
		        }
		        
		    }
			
		});
		
		leftPanel.add(new JScrollPane(emojisTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		treePopupMenu = new JPopupMenu();
		treePopupMenuItemKeyword = new JMenuItem("add keyword");
		treePopupMenuItemEmoji = new JMenuItem("add emoji");
		treePopupMenuItemDelete = new JMenuItem("delete");
		treePopupMenu.add(treePopupMenuItemKeyword);
		treePopupMenu.add(treePopupMenuItemEmoji);
		treePopupMenu.add(treePopupMenuItemDelete);
		
		//The panel for the buttons right under the emoji list
		JPanel editListButtonPanel = new JPanel();
		editListButtonPanel.setLayout(new BoxLayout(editListButtonPanel, BoxLayout.X_AXIS));
		deleteButton = new JButton("Remove");
		newEmojiField = new JTextField(15);
		newEmojiField.setFont(ioTextFont);
		newEmojiField.setMaximumSize(new Dimension(10000, newEmojiField.getMinimumSize().height));
		addButton = new JButton("Add");
		editListButtonPanel.add(deleteButton);
		editListButtonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
//		editListButtonPanel.add(new JSeparator(SwingConstants.VERTICAL));
		editListButtonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		editListButtonPanel.add(newEmojiField);
		editListButtonPanel.add(Box.createRigidArea(new Dimension(2, 0)));
		editListButtonPanel.add(addButton);
		leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		leftPanel.add(editListButtonPanel);
		leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		
		//Right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		//Keyword text field panel
		JPanel keywordTextFieldPanel = new JPanel();
		keywordTextFieldPanel.setLayout(new BoxLayout(keywordTextFieldPanel, BoxLayout.X_AXIS));
		
		wordField = new JTextField(26);
		keywordTextFieldPanel.add(wordField);
		wordField.setFont(ioTextFont);
		wordField.setMaximumSize(new Dimension(10000, wordField.getMinimumSize().height));
		
		findButton = new JButton("Load");
		keywordTextFieldPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		keywordTextFieldPanel.add(findButton);
		
		rightPanel.add(keywordTextFieldPanel);
		
		JPanel feedbackLabelPanel = new JPanel();
		feedbackLabelPanel.setLayout(new BoxLayout(feedbackLabelPanel, BoxLayout.X_AXIS));
		feedbackField.setFont(feedbackFont);
		feedbackField.setMaximumSize(new Dimension(500000, 20));
		feedbackField.setEditable(false);
		feedbackField.setHorizontalAlignment(JTextField.RIGHT);
		feedbackField.setBorder(null);
		feedbackLabelPanel.add(feedbackField);
		
		
		rightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		rightPanel.add(Box.createVerticalGlue());
		rightPanel.add(feedbackLabelPanel);
		rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		
		
		ioPanel.add(leftPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
		ioPanel.add(rightPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 100, 0));
		
		
		
		add(ioPanel);
		
		addListeners();
		add(new JLabel("     "), BorderLayout.WEST);
		add(new JLabel("     "), BorderLayout.EAST);
		
	}
	
	private void addListeners() {
		
		//This listener will take care of cell editing
		treeModel.addTreeModelListener(new TreeModelListener() {
			
			@Override
			public void treeStructureChanged(TreeModelEvent e) {}
			
			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}
			
			@Override
			public void treeNodesInserted(TreeModelEvent e) {}
			
			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				
				//The tree model does not need to be changed, but the dictionary file does need to be changed.
				
				//I have no clue why this is the changed node. I got it off of the Java tutorials
				DefaultMutableTreeNode changedNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) e.getTreePath().getLastPathComponent()).getChildAt(e.getChildIndices()[0]);
				
				if(changedNode.getAllowsChildren() == true) {
					
					//If the changed node was a keyword, we'll try to edit the keyword
					if(DictFileEditor.editWord(previousSelectedNodeContent, changedNode.toString())) {
						
						//Keyword was edited
						feedbackField.setText("\"" + previousSelectedNodeContent + "\" was changed to \"" + changedNode.toString() + "\"");
						
					} else {
						
						//Keyword was not edited
						feedbackField.setText("the keyword \"" + changedNode.toString() + "\" already exists");
						changedNode.setUserObject(previousSelectedNodeContent); //Reversing the user edit to the node
						
					}
					
				} else {
					
					//If the changed node was an emoji, we'll try to edit the emoji
					if(DictFileEditor.editEmoji(changedNode.getParent().toString(), previousSelectedNodeContent, changedNode.toString())) {
						
						//Emoji was edited
						feedbackField.setText("\"" + previousSelectedNodeContent + "\" was changed to \"" + changedNode.toString() + "\"");
						
					} else {
						
						//Emoji was not edited
						feedbackField.setText("the emoji \"" + changedNode.toString() + "\" already exists in \"" + changedNode.getParent().toString() + "\"");
						changedNode.setUserObject(previousSelectedNodeContent); //Reversing the user edit to the node
						
					}
					
				}
				
			}
			
		});
		
		//This listener's mouseClicked method will open the popup menu and will also update previousSelectedNodeContent
		emojisTree.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				System.out.println(feedbackField.getBorder());
				
				emojisTree.setSelectionRow(emojisTree.getRowForLocation(e.getX(), e.getY()));
				
				try {
					
					previousSelectedNodeContent = ((DefaultMutableTreeNode) emojisTree.getSelectionPath().getLastPathComponent()).toString();
					
				} catch (NullPointerException exception) {
					
					previousSelectedNodeContent = null;
					
				}
				
				if(SwingUtilities.isRightMouseButton(e)) {
										
					if(emojisTree.getSelectionCount() == 0) {
						
						//If the user did not select a node
						treePopupMenuItemKeyword.setVisible(true);
						treePopupMenuItemEmoji.setVisible(false);
						treePopupMenuItemDelete.setVisible(false);
						
					} else {
						
						if(((DefaultMutableTreeNode) emojisTree.getSelectionPath().getLastPathComponent()).getAllowsChildren() == true) {
							
							//If the selected node is a keyword, we'll allow for the user to add emojis and delete
							treePopupMenuItemKeyword.setVisible(false);
							treePopupMenuItemEmoji.setVisible(true);
							treePopupMenuItemDelete.setVisible(true);
							
						} else {
							
							//If the selected node is an emoji, we'll only allow the user to delete
							treePopupMenuItemKeyword.setVisible(false);
							treePopupMenuItemEmoji.setVisible(false);
							treePopupMenuItemDelete.setVisible(true);
							
						}
						
					}
					
					treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
					
				}
				
			}
			
		});
		
		emojisTree.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					
					freezeControls(findButton, addButton, deleteButton);
					
					deleteSelectedNodeAndRemoveFromUserDict();
					
					unfreezeControls(findButton, addButton, deleteButton);
					
				}
				
			}
			
		});
		
		treePopupMenuItemKeyword.addActionListener((e) -> {
			
			DefaultMutableTreeNode newTreeNode = null;
			int count = 0;
			String newTreeNodeContent = "new keyword";
			while(newTreeNode == null) {
				
				if(count != 0) {
					
					newTreeNodeContent = "new keyword(" + count + ")";
					
				}
				
				newTreeNode = addNodeAndAddToUserDict(newTreeNodeContent);
				
				count++;
				
			}
			
			previousSelectedNodeContent = newTreeNodeContent;
			emojisTree.startEditingAtPath(new TreePath(newTreeNode.getPath()));
			
		});
		
		treePopupMenuItemEmoji.addActionListener((e) -> {
			
			DefaultMutableTreeNode newTreeNode = null;
			int count = 0;
			String newTreeNodeContent = "new emoji";
			while(newTreeNode == null) {
				
				if(count != 0) {
					
					newTreeNodeContent = "new emoji(" + count + ")";
					
				}
				
				newTreeNode = addNodeAndAddToUserDict(newTreeNodeContent);
				
				count++;
				
			}
			
			previousSelectedNodeContent = newTreeNodeContent;
			emojisTree.startEditingAtPath(new TreePath(newTreeNode.getPath()));
			
		});
		
		treePopupMenuItemDelete.addActionListener((e) -> {
			
			freezeControls(findButton, addButton, deleteButton);
			
			deleteSelectedNodeAndRemoveFromUserDict();
			
			unfreezeControls(findButton, addButton, deleteButton);
			
		});
		
		findButton.addActionListener((e) -> {
			
			freezeControls(findButton, addButton, deleteButton);
			
			if(wordField.getText().isEmpty()) {
				
				loadEmojisTree();
				
			} else {
				
				loadEmojisTree(wordField.getText());
				
			}
			
			unfreezeControls(findButton, addButton, deleteButton);
			
		});
		
		deleteButton.addActionListener((e) -> {
			
			freezeControls(findButton, addButton, deleteButton);
			
			deleteSelectedNodeAndRemoveFromUserDict();
						
			unfreezeControls(findButton, addButton, deleteButton);
			
		});
		
		addButton.addActionListener((e) -> {
			
			freezeControls(findButton, addButton, deleteButton);
			
			addNodeAndAddToUserDict(newEmojiField.getText());
			
			unfreezeControls(findButton, addButton, deleteButton);
			
		});
		
	}
	
	private void freezeControls(JButton... buttons) {
		
		for(int i = 0; i < buttons.length; i++) {
			
			buttons[i].setEnabled(false);
			
		}
		
	}
	
	private void unfreezeControls(JButton... buttons) {
		
		for(int i = 0; i < buttons.length; i++) {
			
			buttons[i].setEnabled(true);
			
		}
		
	}
	
	/**
	 * Loads the emoji tree with a target keyword in mind (most similar to target keyword to least similar)
	 * @param targetKeyword
	 */
	private void loadEmojisTree(String targetKeyword) {
		
		feedbackField.setText("loading the associated emojis for \"" + targetKeyword + "\"...");
		
		//Clearing the tree
		rootNode.removeAllChildren();
		
		//Getting the user dictionary iterator
		HashMap<String, ArrayList<String>> userDict = DictFileEditor.getUserDict();
		Iterator<String> keywordsIterator = userDict.keySet().iterator();
		
		//Looping through each keyword in the user dictionary, and then looping through each associated emoji in each keyword
		String currKeyword = null;
		ArrayList<String> currAssociatedEmojis = null;
		double similarityScore = 0;
		ArrayList<ComparableKeywordNode> keywordNodes = new ArrayList<ComparableKeywordNode>();
		while(keywordsIterator.hasNext()) {
			
			currKeyword = keywordsIterator.next();
			
			similarityScore = SimilarityCalc.compare(currKeyword, targetKeyword);
			if(similarityScore > SIMILARITY_THRESHOLD) {
				
				//If the current keyword and the target keyword are similar enough, we'll load its associated emojis and add it to the keywords ArrayList
				
				currAssociatedEmojis = userDict.get(currKeyword);
				
				DefaultMutableTreeNode newKeywordNode = new DefaultMutableTreeNode(currKeyword);
				newKeywordNode.setAllowsChildren(true); //Can act as a flag for this node, showing that it is a keyword
				keywordNodes.add(new ComparableKeywordNode(newKeywordNode, similarityScore));
				
				//If there are associated emojis
				if(currAssociatedEmojis != null) {
					
					//Looping through each associated emoji for the current keyword
					for(int i = 0; i < currAssociatedEmojis.size(); i++) {
						
						DefaultMutableTreeNode newEmojiNode = new DefaultMutableTreeNode(currAssociatedEmojis.get(i));
						newEmojiNode.setAllowsChildren(false); //Can act as a flag for this node, showing that it is an emoji
						newKeywordNode.add(newEmojiNode);
						
					}
					
				}
				
			}
			
		}
		
		//Sorting the keyword nodes, so that the most similar keyword can be displayed first
		Collections.sort(keywordNodes);
		Collections.reverse(keywordNodes);
		
		//Adding all of the keyword nodes to the root node, effectively displaying them
		for(int i = 0; i < keywordNodes.size(); i++) {
			
			rootNode.add(keywordNodes.get(i).getKeywordNode());
			
		}
		
		treeModel.reload(rootNode);
		
		feedbackField.setText("loaded the associated emojis for \"" + targetKeyword + "\"");
		
	}
	
	/**
	 * Loads all the emojis in no order
	 */
	private void loadEmojisTree() {
		
		feedbackField.setText("loading all associated emojis...");
		
		//Clearing the tree
		rootNode.removeAllChildren();
		
		//Getting the user dictionary iterator
		HashMap<String, ArrayList<String>> userDict = DictFileEditor.getUserDict();
		Iterator<String> keywordsIterator = userDict.keySet().iterator();
		
		//Looping through each keyword in the user dictionary, and then looping through each associated emoji in each keyword
		String currKeyword = null;
		ArrayList<String> currAssociatedEmojis = null;
		while(keywordsIterator.hasNext()) {
			
			currKeyword = keywordsIterator.next();
			currAssociatedEmojis = userDict.get(currKeyword);
			
			DefaultMutableTreeNode newKeywordNode = new DefaultMutableTreeNode(currKeyword);
			newKeywordNode.setAllowsChildren(true); //Can act as a flag for this node, showing that it is a keyword
			rootNode.add(newKeywordNode);
			
			//Looping through each associated emoji for the current keyword
			for(int i = 0; i < currAssociatedEmojis.size(); i++) {
				
				DefaultMutableTreeNode newEmojiNode = new DefaultMutableTreeNode(currAssociatedEmojis.get(i));
				newEmojiNode.setAllowsChildren(false); //Can act as a flag for this node, showing that it is an emoji
				newKeywordNode.add(newEmojiNode);
				
			}
			
		}
		
		treeModel.reload(rootNode);
		
		feedbackField.setText("loaded all associated emojis");
		
	}
	
	/**
	 * Removes the selected node (if there is a selected node)
	 * @return The removed node. Null if there was no selected node, or if the removal was unsuccessful. </br>
	 * <b>If the removal was unsuccessful, then that means that the emojis JTree is not in sync with the actual user dictionary</b>
	 */
	private DefaultMutableTreeNode deleteSelectedNodeAndRemoveFromUserDict() {
		
		DefaultMutableTreeNode selectedNode;
		
		try {
			
			selectedNode = (DefaultMutableTreeNode) emojisTree.getSelectionPath().getLastPathComponent();
			
		} catch (NullPointerException e) {
			
			return null;
			
		}
		
		//If it is a keyword
		if(selectedNode.getAllowsChildren() == true) {
			
			//First try to remove the word
			if(DictFileEditor.removeWordFromUserDict(selectedNode.toString())) {
				
				//If it did remove
				feedbackField.setText("\"" + selectedNode.toString() + "\" was removed");
				treeModel.removeNodeFromParent(selectedNode);
				
				return selectedNode;
				
			} else {
				
				//If it didn't remove
				feedbackField.setText("error: \"" + selectedNode.toString() + "\" was not removed");
				
				return null;
				
			}
			
		} else {
			
			//If it is an emoji
			
			//First, try to remove the emoji
			if(DictFileEditor.removeEmojiFromUserDict(selectedNode.getParent().toString(), selectedNode.toString())) {
				
				//If it did remove
				feedbackField.setText("\"" + selectedNode.toString() + "\" was removed from \"" + selectedNode.getParent().toString() + "\"");
				treeModel.removeNodeFromParent(selectedNode);
				
				return selectedNode;
				
			} else {
				
				//If it didn't remove
				feedbackField.setText("error: \"" + selectedNode.toString() + "\" was not removed");
				
				return null;
				
			}
			
		}
				
	}
	
	/**
	 * 
	 * Adds a new node to the tree based off of the currently selected node.
	 * If there is no selected node, then it will add an empty keyword entry to the user dictionary
	 * If there is a selected node, then it will add an emoji to the selected keyword (if the selected node is an emoji, it will add to that emoji's keyword)
	 * 
	 * @param nodeString The content of the new node
	 * @return A reference to the node that was added to the tree
	 */
	private DefaultMutableTreeNode addNodeAndAddToUserDict(String nodeString) {
		
		DefaultMutableTreeNode selectedNode;
		
		try {
			
			selectedNode = (DefaultMutableTreeNode) emojisTree.getSelectionPath().getLastPathComponent();
			
		} catch (NullPointerException e) {
			
			selectedNode = null;
			
		}

		
		if(selectedNode == null) {
			
			//If there is no selected node, then the user will be adding a keyword
			//(nodeString is the keyword)
			
			//First, try to add the keyword
			if(DictFileEditor.addWordToUserDict(nodeString, new ArrayList<String>())) {
				
				//If it did add
				
				feedbackField.setText("\"" + nodeString + "\" was added");
				
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(nodeString, true);
				treeModel.insertNodeInto(newTreeNode, rootNode, rootNode.getChildCount());
				emojisTree.scrollPathToVisible(new TreePath(newTreeNode.getPath()));
				
				return newTreeNode;
				
			} else {
				
				//If the keyword was already there (didn't add)
				feedbackField.setText("\"" + nodeString + "\" was NOT added (it already exists)");
				
				return null;
				
			}
			
		} else {
			
			//If there is a selected node, then the user will be adding an emoji
			//(nodeString is the emoji)
			
			DefaultMutableTreeNode targetKeywordNode;
			
			if(selectedNode.getAllowsChildren() == true) {
				
				//If the selected node is a keyword node, then the new emoji will be added to the selected node
				targetKeywordNode = selectedNode;
				
			} else {
				
				//If the selected node is an emoji node, then the new emoji will be added to the selected node's parent keyword node
				targetKeywordNode = (DefaultMutableTreeNode) selectedNode.getParent();
				
			}
			
			//First, try to add the keyword
			if(DictFileEditor.addEmojiToWordInUserDict(targetKeywordNode.toString(), nodeString)) {
				
				//If it did add
				feedbackField.setText("\"" + nodeString + "\" was added to \"" + targetKeywordNode.toString() + "\"");
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(nodeString, false);
				treeModel.insertNodeInto(newTreeNode, targetKeywordNode, targetKeywordNode.getChildCount());
				emojisTree.scrollPathToVisible(new TreePath(newTreeNode.getPath()));
				
				return newTreeNode;
				
			} else {
				
				//If the emoji was already there (didn't add)
				feedbackField.setText("\"" + nodeString + "\" was NOT added to \"" + targetKeywordNode.toString() + "\" (it is already added)");
				
				return null;
				
			}
			
		}
		
	}
	
	public void keepFeedbackFieldNoBorder() {
	
		if(feedbackField != null) {
			
			feedbackField.setBorder(null);
			
		}
		
	}
	
	private static class ComparableKeywordNode implements Comparable<ComparableKeywordNode> {
		
		private DefaultMutableTreeNode keyword;
		private double similarityScore;
		
		public ComparableKeywordNode(DefaultMutableTreeNode keyword, double similarityScore) {
			
			this.keyword = keyword;
			this.similarityScore = similarityScore;
			
		}

		@Override
		public int compareTo(ComparableKeywordNode o) {

			if(this.similarityScore > o.similarityScore) {
				
				return 1;
				
			} else if(this.similarityScore == o.similarityScore) {
				
				return 0;
				
			} else {
				
				return -1;
				
			}

		}
		
		public DefaultMutableTreeNode getKeywordNode() {
			
			return keyword;
			
		}
		
	}
	
	public static void main(String[] args) {
		
		EmojiAdderGUI.main(args);
		
	}
	
}
