package jxsource.net.proxy;

import java.io.ByteArrayOutputStream;
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

import jxsource.net.proxy.http.HttpLog;
import jxsource.net.proxy.http.HttpPipeProcess;

public class TcpTester {

	
	static String domain = "localhost";// "www.google.com";
    static int port = 9004;//443;
    static String inputHttpRequestPath = "data/google-request.txt";
    
	public static void main(String...args) {
		new TcpTester().run(domain, port, false, inputHttpRequestPath);
	}
	public void run(String domain, int port, boolean ssl, String path) {
		try {
		    InetAddress addr = InetAddress.getByName(domain);
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket s = null;
			if(ssl)
				s = SSLSocketFactory.getDefault().createSocket();
			else
				s = SocketFactory.getDefault().createSocket();
//			s.setSoTimeout(3000);
			s.connect(sockaddr);
			byte[] buf = loadRequest(path);
			OutputStream out = s.getOutputStream();
			out.write(buf);
			out.flush();
			InputStream in = s.getInputStream();
			
			HttpPipeProcess p = HttpPipeProcess.build();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			p.init("test", in, bout, new HttpLog(), null).proc();
			System.out.println("****** recieved data:");
			System.out.println(bout.toString());

//			buf = new byte[1024*8];
//			int i=0;
//			long cnt = 0;
//			while((i=in.read(buf)) != -1) {
//				if(cnt == 0) System.out.println(""+((char)buf[0])+'\n');
//					System.out.println(new String(buf, 0, i));
//				cnt += i;
////				System.out.println("*** "+cnt);
//			}
//			System.out.println(sockaddr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] loadRequest(String path) throws IOException {
		InputStream in = new FileInputStream(path);
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
