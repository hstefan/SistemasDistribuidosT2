package com.hstefan.distrib.t2;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.*;

public class QuadroAvisosGUI {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		QuadroAvisosGUI window = new QuadroAvisosGUI();
		window.frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public QuadroAvisosGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content_pane = frame.getContentPane();

		JList messageList = new JList();
		content_pane.add(messageList, BorderLayout.CENTER);

		JPanel input_section = new JPanel();
		input_section.setLayout(
				new BoxLayout(input_section, BoxLayout.LINE_AXIS));
		content_pane.add(input_section, BorderLayout.PAGE_END);

		textField = new JTextField();
		input_section.add(textField);

		JButton sendButton = new JButton("Enviar");
		input_section.add(sendButton);
	}
}
