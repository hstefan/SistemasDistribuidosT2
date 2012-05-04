package com.hstefan.distrib.t2;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Concrete implementation of {@link IServidorAvisos}.
 *
 * @author yuriks, hstefan
 */
@SuppressWarnings("serial")
public class ServidorAvisos
		extends UnicastRemoteObject
		implements IServidorAvisos {

	private ArrayList<IQuadroAvisos> registeredBoards;
	public static final int DEFAULT_PORT = 1099;
	
	private static void startServer() throws RemoteException {
		try {
			System.out.println("Starting bulletin board server.");
			Naming.bind("ServidorAvisos", new ServidorAvisos());
			System.out.println("Bulletin board server ready.");
		} catch (AlreadyBoundException ex) {
			System.out.println("Some board server is already running!");
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Invalid URL", ex);
		}
	}

	public static void main(String[] args) {
		Options opts = new Options();
		opts.addOption("p", "port", true, "The port where the registry will be created.");
		@SuppressWarnings("static-access")
		Option help = OptionBuilder.withLongOpt("help").withDescription("Prints this message.").create();
		opts.addOption(help);
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		
		int port = DEFAULT_PORT;
		try {
			cmd = parser.parse(opts, args);
			if(cmd.hasOption("help")) {
				HelpFormatter formater = new HelpFormatter();
				formater.printHelp("java com.hstefan.distrib.t1.server.ServerMain", opts);
				return;
			} if(cmd.hasOption("port")) {
				port = Integer.parseInt(cmd.getOptionValue("port"));
			}
		} catch (ParseException e) {
			System.out.println("Warning: failed to parse arguments, assuming default values.");
		}
		
		try {
			startServer();
		} catch (RemoteException ex) {
			System.out.println("Error contacting name registry: " + ex.getMessage());
			System.out.println("Trying to create registry...");
			try {				
				LocateRegistry.createRegistry(port);
				System.out.println("Registry created!");

				// Try again once
				startServer();
			} catch (RemoteException ex1) {
				System.out.println("Failed to create registry: " + ex1.getMessage());
			}
		}
	}

	public ServidorAvisos() throws RemoteException {
		registeredBoards = new ArrayList<IQuadroAvisos>();
	}

	public void setAviso(String mensagem) throws RemoteException {
		Iterator<IQuadroAvisos> it = registeredBoards.iterator();
		while (it.hasNext()) {
			IQuadroAvisos board = it.next();
			try {
				board.notificar(mensagem);
			} catch (RemoteException ex) {
				// Failed to send message to client,
				// remove it from connected list
				it.remove();
			}
		}
	}

	public void setQuadro(IQuadroAvisos quadro) throws RemoteException {
		registeredBoards.add(quadro);
	}
}
