package sandbox.rmi.common;

public class Service {

	private Service(){
		// unreachable constructor
	}

	public static String getName(){
		return "hello-service";
	}
}
