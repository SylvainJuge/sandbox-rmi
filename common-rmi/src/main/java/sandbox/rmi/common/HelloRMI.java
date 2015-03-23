package sandbox.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface HelloRMI extends Remote {

	public String getHello(String name) throws BusinessCheckedException, RemoteException;

	public String getHello(List<String> name, Object o) throws BusinessCheckedException, RemoteException;

	public String getHello(String[] array) throws BusinessCheckedException, RemoteException;

	public String getHello(String[][] arrays) throws BusinessCheckedException, RemoteException;
}
