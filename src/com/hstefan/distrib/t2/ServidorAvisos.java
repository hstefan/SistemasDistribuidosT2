package com.hstefan.distrib.t2;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Concrete implementation of {@link IServidorAvisos}.
 *
 * @author yuriks
 */
public class ServidorAvisos
		extends UnicastRemoteObject
		implements IServidorAvisos {

	private ArrayList<IQuadroAvisos> registeredBoards;

	private static void startServer() throws RemoteException {
		try {
			System.out.println("Starting bulletin board server.");
			Naming.bind("ServidorAvisos", new ServidorAvisos());
			System.out.println("Bulletin board server ready.");
		} catch (AlreadyBoundException ex) {
			System.out.println("Some board server is already running!");
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Invalid URL", ex);
		}
	}

	public static void main(String[] args) {
		try {
			startServer();
		} catch (RemoteException ex) {
			System.out.println("Error contacting name registry: " + ex.getMessage());
			System.out.println("Trying to create registry...");
			try {
				LocateRegistry.createRegistry(1099);
				System.out.println("Registry created!");

				// Try again once
				startServer();
			} catch (RemoteException ex1) {
				System.out.println("Failed to create registry: " + ex1.getMessage());
			}
		}
	}

	public ServidorAvisos() throws RemoteException {
		registeredBoards = new ArrayList<IQuadroAvisos>();
	}

	public void setAviso(String mensagem) throws RemoteException {
		Iterator<IQuadroAvisos> it = registeredBoards.iterator();
		while (it.hasNext()) {
			IQuadroAvisos board = it.next();
			try {
				board.notificar(mensagem);
			} catch (RemoteException ex) {
				// Failed to send message to client,
				// remove it from connected list
				it.remove();
			}
		}
	}

	public void setQuadro(IQuadroAvisos quadro) throws RemoteException {
		registeredBoards.add(quadro);
	}
}
