package jxsource.net.proxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.ssl.SSLSocketFactory;

public class TcpHttpsTest {

	public static void main(String...args) {
		new TcpHttpsTest().run(args);
	}
	public void run(String...args) {
		try {
//		    InetAddress addr = InetAddress.getByName("localhost");
//		    int port = 8903;
		    InetAddress addr = InetAddress.getByName(args[0]);
		    int port = Integer.parseInt(args[1]);
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket s = SSLSocketFactory.getDefault().createSocket();
			s.connect(sockaddr);
			byte[] buf = loadRequest();
			OutputStream out = s.getOutputStream();
			out.write(buf);
			out.flush();
			InputStream in = s.getInputStream();
			buf = new byte[1024*8];
			int i=0;
			int cnt = 0;
			while((i=in.read(buf)) != -1 && cnt < buf.length) {
				System.out.println(new String(buf, 0, i));
				cnt += i;
			}
			System.out.println("OK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] loadRequest() throws IOException {
		InputStream in = new FileInputStream("data/http-request.txt");
		String s = "";
		int i = 0;
		byte[] buf = new byte[1024];
		while((i=in.read(buf))!= -1) {
			s += new String(buf, 0, i);
		}
		in.close();
		return s.getBytes();
	}
}
