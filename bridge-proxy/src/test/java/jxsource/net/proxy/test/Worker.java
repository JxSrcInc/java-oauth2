package jxsource.net.proxy.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Worker implements ActionListener {

	InputStream inL2R;
	OutputStream outL2R;
	InputStream inR2L;
	OutputStream outR2L;
	
	InputStream localIn;
	OutputStream localOut;
	InputStream remoteIn;
	OutputStream remoteOut;
	
	TestPipe pipeL2R ;
	TestPipe pipeR2L ;
	Remote remote ;
	Local local ;
	
	Worker init() throws IOException {

		// outL2R -> remoteIn
		outL2R = new PipedOutputStream();
		remoteIn = new PipedInputStream((PipedOutputStream)outL2R);
		// inR2L -> remoteOut
		remoteOut = new PipedOutputStream();
		inR2L = new PipedInputStream((PipedOutputStream)remoteOut);
		// outR2L -> localIn
		outR2L = new PipedOutputStream();
		localIn = new PipedInputStream((PipedOutputStream)outR2L);
		// inL2R -> localOut
		localOut = new PipedOutputStream();
		inL2R = new PipedInputStream((PipedOutputStream)localOut);
		System.out.println(localOut.hashCode()+" -> "+inL2R.hashCode());
		pipeL2R = new TestPipe().init("L2R", inL2R, outL2R);
		pipeR2L = new TestPipe().init("R2L", inR2L, outR2L);
		remote = new Remote(inR2L, outL2R);
		local = new Local(inR2L, outL2R);
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e);
		
	}
	
	public void run() {
		Thread tLocal = new Thread(local);
		tLocal.start();
		Thread tL2R = new Thread(pipeL2R);
		tL2R.start();
		Thread tRemote = new Thread(remote);
		tRemote.start();
		Thread tR2L = new Thread(pipeR2L);
		tR2L.start();
	}

	public static void main(String...args) {
		try {
			new Worker().init().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
