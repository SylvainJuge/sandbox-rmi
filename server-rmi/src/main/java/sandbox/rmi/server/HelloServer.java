package sandbox.rmi.server;

import sandbox.rmi.common.HelloRMI;
import sandbox.rmi.common.Service;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloServer implements HelloRMI {



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


			registry.rebind(Service.getName(), remote);

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
			registry.unbind(Service.getName());
		} catch (RemoteException | NotBoundException e) {
			throw new RuntimeException(e);
		}


	}

	private final Random random;

	public HelloServer(){
		random = new Random();
	}

	@Override
	public String getHello(String name) {
		long rand = Math.abs(random.nextLong() % 1_000);
		if (rand % 10 == 0) {
			throw new RuntimeException("random failure");
		}
		try {
			Thread.sleep(rand);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return String.format("hello %s", name);
	}
}
