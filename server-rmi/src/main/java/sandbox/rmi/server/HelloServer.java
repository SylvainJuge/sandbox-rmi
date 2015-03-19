package sandbox.rmi.server;

import sandbox.rmi.common.HelloRMI;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloServer implements HelloRMI {

	public static final String OBJECT_NAME = "hello-service";

	public static void main(String[] args) {

		final AtomicBoolean shutdown = new AtomicBoolean(false);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Stop server");
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
			HelloRMI remote = (HelloRMI) UnicastRemoteObject.exportObject(server, 0);

			registry = LocateRegistry.getRegistry();


			registry.rebind(OBJECT_NAME, remote);

		} catch (RemoteException e) {
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
	public String getHello(String name) {
		return String.format("hello %s", name);
	}
}
