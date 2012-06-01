package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.*;

/**
 * {@link IPeer} implementation, calls method {@link receiveMessage} whenever a
 * {@link ReceiverThread} receives a message from the multi-cast group.
 *
 * @author hstefan
 */
public class Peer {

    private MulticastSocket mMulSocket = null;
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
        mMulSocket = new MulticastSocket(mPort);

        mReceiverThread = new ReceiverThread();
        mReceiverThread.start();

        mMulSocket.joinGroup(mGroupAdress);
    }

    public void removePeer() throws IOException {
        mMulSocket.leaveGroup(mGroupAdress);

        mReceiverThread.stopThread();
        mReceiverThread = null;

        mMulSocket.close();
        mMulSocket = null;
    }

    public void broadcastMensagem(String msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
                mGroupAdress, mPort);
        mMulSocket.send(packet);
    }

    private class ReceiverThread extends Thread {

        private volatile boolean mContinue;

        public void stopThread() {
            mContinue = false;
        }

        @Override
        public void run() {
            byte[] buf = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            mContinue = true;
            while (mContinue) {
                try {
                    mMulSocket.receive(packet);

                    if (listener != null)
                        listener.receiveMessage(packet);
                } catch (SocketException ex) {
                    // Do nothing, presumably it was closed from main thread
                } catch (IOException ex) {
                    //TODO
                }
            }
        }
    }
}
