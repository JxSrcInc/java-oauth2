package jxsource.net.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PortPing {

	public static void main(String...args) {
		Socket s = new Socket();
		int waitTime = 3000;
//		SocketAddress addr = new InetSocketAddress("sy51.sspf.ssa.gov",2006);
//		SocketAddress addr = new InetSocketAddress("sy52.sspf.ssa.gov",2007);
		SocketAddress addr = new InetSocketAddress("valws.ba.ssa.gov",447);
		System.out.println("connection to "+addr);
		try {
			s.connect(addr, 3000);
			Thread.sleep(waitTime);
			System.out.println("connected = "+s.isConnected());
			s.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
