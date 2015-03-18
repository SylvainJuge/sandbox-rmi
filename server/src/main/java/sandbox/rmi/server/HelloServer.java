package sandbox.rmi.server;

import sandbox.rmi.common.HelloRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloServer implements HelloRMI {

	public static final String OBJECT_NAME = "hello-service";
	public static final int SERVER_PORT = 40000;

	public static void main(String[] args) {

		final AtomicBoolean shutdown = new AtomicBoolean(false);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown.set(true);
			}
		});


		SecurityManager securityManager = System.getSecurityManager();
		if (null == securityManager) {
			System.setSecurityManager(new SecurityManager());
		}


		HelloServer server = new HelloServer();

		Registry registry;
		try {
			UnicastRemoteObject.exportObject(server, 0); // TODO : choix du port ?

			// 0 : port anonyme
			// >0 : port spécifique

			// registry port ?

			// get local registry on default port : 1099
			registry = LocateRegistry.getRegistry(SERVER_PORT);
			registry.bind(OBJECT_NAME, server);

		} catch (RemoteException | AlreadyBoundException e) {
			throw new RuntimeException(e);
		}

		while (!shutdown.get()) {
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			registry.unbind(OBJECT_NAME);
		} catch (RemoteException | NotBoundException e) {
			throw new RuntimeException(e);
		}


	}


	@Override
	public String getHello(String name) throws RemoteException {
		return String.format("hello %s", name);
	}
}
