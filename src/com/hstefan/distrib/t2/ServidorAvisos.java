package com.hstefan.distrib.t2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Concrete implementation of {@link IServidorAvisos}.
 *
 * @author yuriks
 */
public class ServidorAvisos
		extends UnicastRemoteObject
		implements IServidorAvisos {

	private ArrayList<IQuadroAvisos> registeredBoards;

	public ServidorAvisos() throws RemoteException {
		registeredBoards = new ArrayList<IQuadroAvisos>();
	}

	public void setAviso(String mensagem) throws RemoteException {
		for (IQuadroAvisos board : registeredBoards) {
			board.notificar(mensagem);
		}
	}

	public void setQuadro(IQuadroAvisos quadro) throws RemoteException {
		registeredBoards.add(quadro);
	}
}
