package net.patowen.songanalyzer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputController;

public class SongAnalyzerRunner {
	private JFrame frame;
	private JPanel panel;
	private Config config;
	private UserActionList userActionList;
	private FileDialogManager fileDialogManager;
	private Root root;
	
	@SuppressWarnings("serial")
	public SongAnalyzerRunner() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				config.saveConfig();
				frame.dispose();
			}
		});
		
		panel = new JPanel() {
			public void paint(Graphics g) {
				root.render((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setFocusable(true);
		
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				root.setWidth(e.getComponent().getWidth());
				root.setHeight(e.getComponent().getHeight());
			}
		});
		
		config = new Config();
		userActionList = new UserActionList();
		fileDialogManager = new FileDialogManager(panel);
		
		root = new Root(config, userActionList, fileDialogManager);
		
		new InputController(panel, root);
		
		frame.add(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new SongAnalyzerRunner();
	}
}
