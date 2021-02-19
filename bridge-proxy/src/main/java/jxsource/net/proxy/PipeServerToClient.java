package jxsource.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;

public class PipeServerToClient extends Pipe{

	public PipeServerToClient(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super(name, in, out, logOut);
	}

}
