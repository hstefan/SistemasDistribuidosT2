package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * {@link IPeer} implementation, calls method {@link receiveMessage} whenever a
 * {@link ReceiverThread} receives a message from the multi-cast group.
 *
 * @author hstefan
 */
public class Peer {

    private MulticastSocket mMulSocket;
    private InetAddress mGroupAdress;
    private ReceiverThread mReceiverThread;
    protected int mPort;
    protected String mHost;
    private PeerListener listener = null;
    public static final int BUFFER_SIZE = 1024;

    public Peer(String host, int port) {
        try {
            mHost = host;
            mPort = port;

            mGroupAdress = InetAddress.getByName(host);
            mMulSocket = new MulticastSocket(port);
            mReceiverThread = null;
        } catch (UnknownHostException ex) {
            //TODO
        } catch (IOException ex) {
            //TODO
        }
    }

    public void setListener(PeerListener listener) {
        this.listener = listener;
    }

    public void registraPeer() throws IOException {
        mMulSocket.joinGroup(mGroupAdress);
        mReceiverThread = new ReceiverThread();
        mReceiverThread.start();
    }

    public void removePeer() throws IOException {
        mMulSocket.leaveGroup(mGroupAdress);
        mReceiverThread.stopThread();
    }

    public void broadcastMensagem(String msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
                mGroupAdress, mPort);
        mMulSocket.send(packet);
    }

    private class ReceiverThread extends Thread {

        public static final int SLEEP_MS = 1000;
        private volatile boolean mContinue;

        public void stopThread() {
            mContinue = false;
        }

        @Override
        public void run() {
            mContinue = true;
            while (mContinue) {
                try {
                    byte[] buf = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    mMulSocket.receive(packet);

                    if (listener != null)
                        listener.receiveMessage(packet);
                } catch (IOException ex) {
                    //TODO
                }
            }
        }
    }
}
