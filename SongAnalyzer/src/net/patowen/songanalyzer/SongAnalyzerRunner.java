package net.patowen.songanalyzer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SongAnalyzerRunner {
	private static Deck deck;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			public void paint(Graphics g) {
				deck.render((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(800, 600));
		
		GlobalStatus status = new GlobalStatus(panel);
		
		deck = new Deck(status);
		
		InputController inputController = new InputController(panel, deck);
		
		deck.setWidth(800);
		deck.setHeight(600);
		frame.add(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
