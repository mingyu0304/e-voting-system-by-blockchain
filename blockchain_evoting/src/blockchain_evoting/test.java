package blockchain_evoting;

import java.net.*;
public class test {

	public static void main(String[] args) {
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String ip = local.getHostAddress();
	}

}
