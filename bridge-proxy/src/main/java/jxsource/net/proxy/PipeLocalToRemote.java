package jxsource.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeLocalToRemote extends PipeBase{
	Logger log = LoggerFactory.getLogger(PipeLocalToRemote.class);

	@Override
	public void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super.init(name, in, out, logOut);
	}

}
