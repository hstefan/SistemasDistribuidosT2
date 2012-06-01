package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.*;
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
        implements IQuadroAvisos, Remote, PeerListener {

    private QuadroAvisosGUI gui;
    private Set<HostEntry> mGroupAdresses;
    private NotificadorPeerAtivo mNotificador;
    private Registry mLocalRegistry;
    private Peer peer;
    public static final String REG_NAME = "QuadroAvisos";

    public class NotificadorPeerAtivo extends Thread {

        private boolean peerAtivo = true;

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

    @SuppressWarnings("LeakingThisInConstructor")
    public QuadroAvisos(QuadroAvisosGUI gui, String host, int port) throws RemoteException, IOException {
        this.gui = gui;
        mGroupAdresses = new HashSet<HostEntry>();

        mLocalRegistry = LocateRegistry.createRegistry(port);

        peer = new Peer(host, port);
        peer.setListener(this);
        peer.registraPeer();
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

    @Override
    public void receiveMessage(DatagramPacket packet) {
        mGroupAdresses.add(new HostEntry(packet.getAddress(), packet.getPort(),
                new String(packet.getData()).trim()));
    }

    @Override
    public void posRegistroPeer() {
        try {
            mLocalRegistry.bind(REG_NAME, this);
        } catch (RemoteException ex) {
            //TODO
        } catch (AlreadyBoundException ex) {
            //TODO
        }

        mNotificador = new NotificadorPeerAtivo();
        mNotificador.start();
    }

    @Override
    public void posRemocaoPeer() {
        mNotificador.signalStop();
        try {
            mLocalRegistry.unbind(REG_NAME);
        } catch (RemoteException ex) {
            //TODO
        } catch (NotBoundException ex) {
            //TODO
        }
    }
}
