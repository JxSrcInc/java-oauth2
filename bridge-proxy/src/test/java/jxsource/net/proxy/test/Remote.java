package jxsource.net.proxy.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Remote implements Runnable{
	
	InputStream in;
	OutputStream out;
	public Remote(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public  void run()  {
		System.out.println("remote start");
		byte[] buf = new byte[1024];
		int i=0;
		try {
		while((i=in.read(buf)) != -1) {
			out.write(buf,0,i);
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.err.println("Remote thread stop");
		
	}

}
