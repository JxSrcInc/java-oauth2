package jxsource.net.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileOutputStream;

public class SocketInputStreamViewer {

	public static int port = 9999;
	class Worker extends Thread {
		Socket s;

		public Worker(Socket s) {
			this.s = s;
		}

		public void run() {
			System.out.println("process request from: " + s.getInetAddress());
			try {
				InputStream in = s.getInputStream();
				String path = "./log/call-" + System.currentTimeMillis();
				OutputStream outf = new FileOutputStream(path);
				OutputStream out = s.getOutputStream();
					byte[] b = new byte[1024*8];
					int i = 0;
					while ((i=in.read(b)) != -1) {
						System.out.println(new String(b, 0, i));
					}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public SocketInputStreamViewer() {
		try {
//			ServerSocket ss = new ServerSocket(Constants.port);
			ServerSocket ss = new ServerSocket(port);
			
			System.out.println("listening on " + ss.getInetAddress() + ":" + ss.getLocalPort() + " .....");
			while (true) {
				Socket client = ss.accept();
				System.out.println("accept: " + client.getInetAddress());
				new Worker(client).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		new SocketInputStreamViewer();
	}
}
