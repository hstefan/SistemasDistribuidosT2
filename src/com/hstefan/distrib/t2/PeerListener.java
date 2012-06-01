package com.hstefan.distrib.t2;

import java.net.DatagramPacket;

/**
 *
 * @author yuriks
 */
public interface PeerListener {

    void receiveMessage(DatagramPacket message);
}
