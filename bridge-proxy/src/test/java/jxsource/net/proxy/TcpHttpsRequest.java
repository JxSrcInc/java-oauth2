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

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class TcpHttpsRequest {

	public static void main(String...args) {
		new TcpHttpsRequest().run(args);
	}
	public void run(String...args) {
		try {
//		    InetAddress addr = InetAddress.getByName("www.google.com");
//		    int port = 443;//Integer.parseInt(args[1]);
		    InetAddress addr = InetAddress.getByName("localhost");
		    int port = 9004;//Integer.parseInt(args[1]);
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);
//			Socket s = SSLSocketFactory.getDefault().createSocket();
			Socket s = SocketFactory.getDefault().createSocket();
//			s.setSoTimeout(10000);
			s.connect(sockaddr);
			byte[] buf = loadRequest();
			OutputStream out = s.getOutputStream();
			out.write(buf);
			out.flush();
			InputStream in = s.getInputStream();
			buf = new byte[1024*8];
			int i=0;
			int cnt = 0;
			while((i=in.read(buf)) != -1) {
				System.out.print(new String(buf, 0, i));
				cnt += i;
			}
			System.out.println();
			System.out.println(cnt);
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
