package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Map<HostEntry, IQuadroAvisos> mGroupAdresses;
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
        mGroupAdresses = new HashMap<HostEntry, IQuadroAvisos>();

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(1099);
            registry.bind(REG_NAME, this);
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(QuadroAvisos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            try {
                registry = LocateRegistry.createRegistry(1099);
                registry.bind(REG_NAME, this);
            } catch (AlreadyBoundException ex1) {
                Logger.getLogger(QuadroAvisos.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (AccessException ex1) {
                Logger.getLogger(QuadroAvisos.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        mLocalRegistry = registry;

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
            UnicastRemoteObject.unexportObject(this, true);
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

    public void broadcast(String mensagem) {
        synchronized (mGroupAdresses) {
            Iterator<Map.Entry<HostEntry, IQuadroAvisos>> it = mGroupAdresses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<HostEntry, IQuadroAvisos> e = it.next();
                IQuadroAvisos board = e.getValue();

                if (board == null) {
                    board = resolveEntry(e.getKey());
                    if (board != null) {
                        e.setValue(board);
                    }
                }

                if (board == null) {
                    it.remove();
                } else {
                    try {
                        board.notificar(mensagem);
                    } catch (RemoteException ex) {
                        it.remove();
                    }
                }
            }
        }
    }

    @Override
    public void receiveMessage(DatagramPacket packet) {
        synchronized (mGroupAdresses) {
            HostEntry entry = new HostEntry(packet.getAddress(), new String(packet.getData()).trim());
            if (!mGroupAdresses.containsKey(entry)) {
                mGroupAdresses.put(entry, resolveEntry(entry));
            }
        }
    }

    private IQuadroAvisos resolveEntry(HostEntry entry) {
        String url = "//" + entry.adress.getHostAddress() + ":1099/" + entry.reg_name;
        IQuadroAvisos other_board = null;

        try {
            other_board = (IQuadroAvisos) Naming.lookup(url);
        } catch (RemoteException ex) {
            // TODO
            Logger.getLogger(QuadroAvisos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            other_board = null;
        } catch (MalformedURLException ex) {
            // TODO
            Logger.getLogger(QuadroAvisos.class.getName()).log(Level.SEVERE, null, ex);
        }

        return other_board;
    }
}
