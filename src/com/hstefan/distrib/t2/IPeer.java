package com.hstefan.distrib.t2;

import java.io.IOException;

/**
 * A basic peer that should be used to implement broadcasting to a peer group.
 * Concrete implementation on {@link Peer}.
 * @author hstefan
 */
public interface IPeer {
    void registraPeer() throws IOException;
    void removePeer() throws IOException;
    void broadcastMensagem(String msg) throws IOException;
}
