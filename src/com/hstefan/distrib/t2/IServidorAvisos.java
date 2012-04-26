package com.hstefan.distrib.t2;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A bulletin board server. It keeps track of {@link IQuadroAvisos} that have
 * made their presence known to it using the {@link #setQuadro(IQuadroAvisos)}
 * method and notifies them when sent a message using the {@link #setAviso(String)}
 * method.
 *
 * @author yuriks
 */
public interface IServidorAvisos extends Remote {

	/**
	 * Broadcasts a new message to the registered {@link IQuadroAvisos}.
	 *
	 * @param mensagem The message to broadcast.
	 */
	void setAviso(String mensagem) throws RemoteException;

	/**
	 * Adds a new {@link IQuadroAvisos} to be notified when a message is
	 * broadcast.
	 *
	 * @param quadro The bulletin board to register as a message receiver.
	 */
	void setQuadro(IQuadroAvisos quadro) throws RemoteException;
}