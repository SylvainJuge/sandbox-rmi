package sandbox.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloRMI extends Remote {

	public String getHello(String name) throws RemoteException;
}
