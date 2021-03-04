package jxsource.net.proxy.tcp;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeLocalToRemote extends PipeBase{

	@Override
	public void init(String name, InputStream in, OutputStream out, Log log, boolean activeLog) {
		super.init(name, in, out, log, activeLog);
	}

}
