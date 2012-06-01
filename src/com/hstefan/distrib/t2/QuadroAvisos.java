package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 *
 * @author yuriks, hstefan
 */
@SuppressWarnings("serial")
public class QuadroAvisos
        extends UnicastRemoteObject
        implements IQuadroAvisos, PeerListener {

    private final QuadroAvisosGUI gui;
    private final Set<HostEntry> mGroupAdresses;
    private final NotificadorPeerAtivo mNotificador;
    private final Registry mLocalRegistry;
    private final Peer peer;
    public static final String REG_NAME = "QuadroAvisos";

    public class NotificadorPeerAtivo extends Thread {

        private volatile boolean peerAtivo = true;

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            while (peerAtivo) {
                try {
                    peer.broadcastMensagem(REG_NAME);
                    Thread.sleep(200);
                } catch (IOException ex) {
                } catch (InterruptedException ex) {
                }
            }
        }

        public void signalStop() {
            peerAtivo = false;
        }
    }

    @SuppressWarnings({"LeakingThisInConstructor", "CallToThreadStartDuringObjectConstruction"})
    public QuadroAvisos(QuadroAvisosGUI gui, String host, int port) throws RemoteException, IOException {
        this.gui = gui;
        mGroupAdresses = new HashSet<HostEntry>();

        mLocalRegistry = LocateRegistry.createRegistry(port);
        try {
            mLocalRegistry.bind(REG_NAME, this);
        } catch (RemoteException ex) {
            //TODO
        } catch (AlreadyBoundException ex) {
            //TODO
        }

        peer = new Peer(host, port);
        peer.setListener(this);
        peer.registraPeer();

        mNotificador = new NotificadorPeerAtivo();
        mNotificador.start();
    }

    public void closing() {
        mNotificador.signalStop();

        try {
            peer.removePeer();
        } catch (IOException ex) {
            // TODO
        }

        try {
            mLocalRegistry.unbind(REG_NAME);
        } catch (RemoteException ex) {
            //TODO
        } catch (NotBoundException ex) {
            //TODO
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
        Registry r;
        IQuadroAvisos qr;

        synchronized (mGroupAdresses) {
            for (HostEntry e : mGroupAdresses) {
                r = LocateRegistry.getRegistry(e.adress.getHostAddress(), e.port);
                try {
                    qr = (IQuadroAvisos) r.lookup(e.reg_name);
                    qr.notificar(mensagem);
                } catch (NotBoundException ex) {
                    mGroupAdresses.remove(e);
                } catch (AccessException ex) {
                    //TODO
                }
            }
        }
    }

    @Override
    public void receiveMessage(DatagramPacket packet) {
        synchronized (mGroupAdresses) {
            mGroupAdresses.add(new HostEntry(packet.getAddress(), packet.getPort(),
                    new String(packet.getData()).trim()));
        }
    }
}
