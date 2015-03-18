package sandbox.rmi.client;

import sandbox.rmi.common.HelloRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloClient {

	public static void main(String[] args) {
		SecurityManager securityManager = System.getSecurityManager();
		if (null == securityManager) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);

			for (String object : registry.list()) {
				System.out.println(String.format("remote object %s", object));
			}

			HelloRMI server = (HelloRMI) registry.lookup("hello-server");
			System.out.println(server.getHello("john"));


		} catch (RemoteException | NotBoundException e) {
			throw new RuntimeException(e);
		}
	}



}
