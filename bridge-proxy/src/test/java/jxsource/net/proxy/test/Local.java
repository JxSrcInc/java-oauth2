package jxsource.net.proxy.test;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import jxsource.net.proxy.util.ByteBuffer;

public class Local implements Runnable {

	InputStream in;
	OutputStream out;
	public Local(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public void run(){
		System.out.println("Local start");
		try {
		int size = 10;
		long pos = 0;
		long len = size * 10;
		ByteBuffer bb = new ByteBuffer(size);
		bb.append("0123456789".getBytes());
		for (int i = 0; i < 1; i++) {
			while (pos < len) {
				byte[] buf = bb.getArray(); 
				System.out.print(new String(buf));
				out.write(buf);
				out.flush();
				pos += size;
			}
			System.out.println("end local output");
			out.close();
			byte[] buf = new byte[size];
			int rLen = 0;
			int k = 0;
			while(rLen<len) {
				k = in.read(buf);
				System.out.print(new String(buf, 0, k));
				rLen += k;
			}
			System.out.println();
			
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.err.println("Local thread stop");

	}

}
