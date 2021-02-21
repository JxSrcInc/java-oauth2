package jxsource.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;

public class PipeRemoteToLocal extends PipeBase{

	@Override
	public void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super.init(name, in, out, logOut);
	}

}
