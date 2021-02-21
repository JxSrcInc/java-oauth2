package jxsource.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeRemoteToLocal extends PipeBase{
	Logger log = LoggerFactory.getLogger(PipeRemoteToLocal.class);

	@Override
	public void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super.init(name, in, out, logOut);
	}

}
