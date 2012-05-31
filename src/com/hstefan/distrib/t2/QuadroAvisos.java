package com.hstefan.distrib.t2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static final String REG_NAME = "QuadroAvisos";

    @SuppressWarnings("LeakingThisInConstructor")
    public QuadroAvisos(QuadroAvisosGUI gui, String host, int port) throws RemoteException {
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
}
