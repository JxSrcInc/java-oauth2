package jxsource.net.proxy.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.net.proxy.Pipe;

public class TestPipe extends Pipe {

	public TestPipe() {}
	public TestPipe init(String name, InputStream in, OutputStream out) {
		init(name, in, out, null, false);
		return this;
	}

	@Override
	protected void proc() throws IOException {
		System.out.print(getLogMsg(""+in.hashCode()+" -> "+out.hashCode()));
		byte[] buf = new byte[1024];
		int i=0;
		while((i=in.read(buf)) != -1) {
			out.write(buf,0,i);
		}		
	}

	
}
