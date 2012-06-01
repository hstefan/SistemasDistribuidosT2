package com.hstefan.distrib.t2;

import java.net.DatagramPacket;

/**
 *
 * @author yuriks
 */
public interface PeerListener {

    void posRegistroPeer();

    void posRemocaoPeer();

    void receiveMessage(DatagramPacket message);
}
