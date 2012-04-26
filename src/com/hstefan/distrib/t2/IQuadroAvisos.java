package com.hstefan.distrib.t2;

/**
 * A bulletin board client. It can send and receive message to/from a
 * {@link IServidorAvisos}.
 *
 * @author yuriks
 */
public interface IQuadroAvisos {

	/**
	 * Called by {@link IServidorAvisos} when a new message is broadcasted.
	 *
	 * @param mensagem The message contents.
	 */
	void notificar(String mensagem);
}
