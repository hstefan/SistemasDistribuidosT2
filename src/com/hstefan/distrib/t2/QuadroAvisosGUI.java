package com.hstefan.distrib.t2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import javax.swing.*;

/**
 *
 * @author yuriks, hstefan
 */
public class QuadroAvisosGUI {

    private JFrame frame;
    private JTextField textField;
    private DefaultListModel<String> messageListModel;
    private QuadroAvisos bboard;
    private static HostDialog hostDlg;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        QuadroAvisosGUI window = new QuadroAvisosGUI();
        QuadroAvisos bboard;
        try {
            bboard = new QuadroAvisos(window, hostDlg.getHost(),
                    hostDlg.getPort());
        } catch (RemoteException ex) {
            System.out.println("Failed to export object: " + ex.getMessage());
            System.exit(1);
            return;
        }
        window.setBulletinBoard(bboard);
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

        messageListModel = new DefaultListModel<String>();
        JList<String> messageList = new JList<String>(messageListModel);
        content_pane.add(messageList, BorderLayout.CENTER);

        JPanel input_section = new JPanel();
        input_section.setLayout(
                new BoxLayout(input_section, BoxLayout.LINE_AXIS));
        content_pane.add(input_section, BorderLayout.PAGE_END);

        textField = new JTextField();
        input_section.add(textField);

        JButton sendButton = new JButton("Enviar");
        input_section.add(sendButton);
        frame.getRootPane().setDefaultButton(sendButton);
        sendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sendMessage(textField.getText());
                textField.setText(null);
            }
        });

        hostDlg = new HostDialog(frame, true);
        hostDlg.setVisible(true);
    }

    void receiveMessage(String mensagem) {
        messageListModel.insertElementAt(mensagem, 0);
    }

    void sendMessage(String message) {
        try {
            bboard.broadcast(message);
        } catch (RemoteException ex) {
            receiveMessage("Failed to send message!");
        }
    }

    private void setBulletinBoard(QuadroAvisos bboard) {
        this.bboard = bboard;
    }
}
