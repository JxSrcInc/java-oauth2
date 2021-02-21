package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.PipeBase;
import jxsource.net.proxy.PipeRemoteToLocal;

public class HttpPipeRemoteToLocal extends PipeRemoteToLocal{
	Logger log = LoggerFactory.getLogger(HttpPipeRemoteToLocal.class);
	HttpPipeProcess httpPipeProcess = HttpPipeProcess.build();

	@Override
	public void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super.init(name, in, out, logOut);
	}

	@Override
	protected void proc() throws IOException {
		super.proc();
//		httpPipeProcess.init(name, in, out, logOut).proc();
	}

}
