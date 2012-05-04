package com.hstefan.distrib.t2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
*
* @author hstefan
*/
@SuppressWarnings("serial")
public class HostDialog extends JDialog {
	private JTextField txtHost;
	private JTextField txtPort;

	/**
	 * Create the dialog.
	 */
	public HostDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		setBounds(100, 100, 300, 60);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		txtHost = new JTextField();
		txtHost.setText("localhost");
		getContentPane().add(txtHost, BorderLayout.CENTER);
		txtHost.setColumns(10);
		
		JButton btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(btnConectar, BorderLayout.SOUTH);
		
		txtPort = new JTextField();
		txtPort.setText("1099");
		getContentPane().add(txtPort, BorderLayout.EAST);
		txtPort.setColumns(10);
	}
	
	public String getHost() {
		return txtHost.getText();
	}
	
	public int getPort() {
		return Integer.parseInt(txtPort.getText());
	}
}
