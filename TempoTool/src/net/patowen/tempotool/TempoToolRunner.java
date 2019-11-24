/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.patowen.tempotool;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.patowen.tempotool.userinput.InputController;

public class TempoToolRunner {
	private JFrame frame;
	private JPanel panel;
	private Root root;
	private RootSizer rootSizer = new RootSizer();
	
	@SuppressWarnings("serial")
	public TempoToolRunner() {
		frame = new JFrame("Tempo Tool");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				root.destroy();
				frame.dispose();
			}
		});
		
		panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				root.forwardRender((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setFocusable(true);
		
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				rootSizer.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});
		
		root = new Root(frame, panel);
		root.setSizer(rootSizer);
		
		new InputController(panel, root);
		
		frame.add(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TempoToolRunner();
	}
}
