package com.hstefan.distrib.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 *
 * @author yuriks, hstefan
 */
@SuppressWarnings("serial")
public class QuadroAvisos
        extends Peer
        implements IQuadroAvisos, Remote {
    
    private QuadroAvisosGUI gui;
    private Set<HostEntry> mGroupAdresses;
    private NotificadorPeerAtivo mNotificador;
    
    public static final String REG_NAME = "QuadroAvisos";

    public class NotificadorPeerAtivo extends Thread {
        private boolean peerAtivo;
        
        @Override
        public void run() {
            while(peerAtivo) {
                try {
                    broadcastMensagem(REG_NAME);
                    Thread.sleep(200);
                } catch (IOException ex) {
                } catch (InterruptedException ex) {
                }
            }
        }
        
        public void setPeerAtivo(boolean ativo) {
            if(peerAtivo != ativo && ativo) {
                start(); //inicia thread que notifica periodicamente a existência
                         //do peer
            }
            peerAtivo = ativo;
        }
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public QuadroAvisos(QuadroAvisosGUI gui, String host, int port) throws RemoteException {
        super(host, port);
        
        this.gui = gui;
        mGroupAdresses = new HashSet<HostEntry>();
        
        Registry reg = LocateRegistry.createRegistry(port);
        try {
            reg.bind(REG_NAME, this);
        } catch (AlreadyBoundException ex) {
            //TODO
        } catch (AccessException ex) {
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
        QuadroAvisos qr;
    
        for(HostEntry e : mGroupAdresses){
            r = LocateRegistry.getRegistry(e.adress.toString(), e.port);
            try {
                qr = (QuadroAvisos)r.lookup(e.reg_name);
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
                new String(packet.getData()) ) );
    }
    
    @Override
    protected void posRegistroPeer() {
        mNotificador.setPeerAtivo(true);
    }
    
    @Override
    protected void posRemocaoPeer() {
        mNotificador.setPeerAtivo(false);
    }
}
