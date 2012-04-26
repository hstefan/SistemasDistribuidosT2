package com.hstefan.distrib.t2;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.SwingUtilities;

/**
 *
 * @author yuriks
 */
public class QuadroAvisos
		extends UnicastRemoteObject
		implements IQuadroAvisos {

	private QuadroAvisosGUI gui;
	private IServidorAvisos server;

	@SuppressWarnings("LeakingThisInConstructor")
	public QuadroAvisos(QuadroAvisosGUI gui) throws RemoteException {
		this.gui = gui;
		try {
			server = (IServidorAvisos) Naming.lookup("ServidorAvisos");
			server.setQuadro(this);
		} catch (NotBoundException ex) {
			System.out.println("Bulletin board server not found!");
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Invalid URL", ex);
		}
	}

	public void notificar(final String mensagem) throws RemoteException {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				gui.receiveMessage(mensagem);
			}
		});
	}

	public void broadcast(String mensagem) throws RemoteException {
		server.setAviso(mensagem);
	}
}
