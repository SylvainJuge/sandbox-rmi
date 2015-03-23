package sandbox.rmi.server;

import sandbox.rmi.common.BusinessCheckedException;
import sandbox.rmi.common.HelloRMI;
import sandbox.rmi.common.Service;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloServer implements HelloRMI, MsgPrinter {

	public static void main(String[] args) {

		final AtomicBoolean shutdown = new AtomicBoolean(false);

		SecurityManager securityManager = System.getSecurityManager();
		if (null == securityManager) {
			System.setSecurityManager(new SecurityManager());
		}

		final HelloServer server = new HelloServer();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.doPrintMsg("Stop server");
				shutdown.set(true);
			}
		});

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

	public HelloServer() {
		random = new Random();
	}

	// public method that should not be included in RMI instrumentation
	@Override
	public void printMsg(String msg) {
		System.out.println(msg);
	}

	// another method in class that should not be instrumented
	public void doPrintMsg(String msg) {
		printMsg(msg);
	}

	@Override
	public String getHello(List<String> name, Object o) throws BusinessCheckedException, RemoteException {
		return "hello : " + Arrays.toString(name.toArray());
	}

	@Override
	public String getHello(String[] array) throws BusinessCheckedException, RemoteException {
		return "hello : " + Arrays.toString(array);
	}

	@Override
	public String getHello(String[][] arrays) throws BusinessCheckedException, RemoteException {
		StringBuilder sb = new StringBuilder("hello : ");
		for (String[] array : arrays) {
			sb.append(Arrays.toString(array));
		}
		return sb.toString();
	}

	@Override
	public String getHello(String name) throws BusinessCheckedException {
		long rand = Math.abs(random.nextLong() % 1_000);
		if ("oscar".equals(name)) {
			throw new BusinessRuntimeException("param failure");
		}
		if (rand % 10 == 0) {
			throw new BusinessCheckedException("random failure");
		}
		try {
			Thread.sleep(rand);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return String.format("hello %s", name);
	}

	public static class BusinessRuntimeException extends RuntimeException {
		private BusinessRuntimeException(String message) {
			super(message);
		}
	}

}
