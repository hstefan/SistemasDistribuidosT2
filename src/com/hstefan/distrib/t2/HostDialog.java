package com.hstefan.distrib.t2;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import layout.SpringUtilities;

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
        setBounds(100, 100, 300, 120);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        SpringLayout layout = new SpringLayout();
        Container content_panel = getContentPane();
        content_panel.setLayout(layout);
        
        txtHost = new JTextField();
        txtHost.setText("localhost");
        txtHost.setColumns(10);
        
        content_panel.add(txtHost);
        
        txtPort = new JTextField();
        txtPort.setText("1099");
        txtPort.setColumns(4);
        
        content_panel.add(txtPort);
        
        JButton btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        content_panel.add(btnConectar);
        
        SpringUtilities.makeCompactGrid(content_panel, 3, 1, 5, 5, 5, 5);
    }

    public String getHost() {
        return txtHost.getText();
    }

    public int getPort() {
        return Integer.parseInt(txtPort.getText());
    }
}
