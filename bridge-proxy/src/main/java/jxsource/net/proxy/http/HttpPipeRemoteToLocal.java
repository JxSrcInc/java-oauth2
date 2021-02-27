package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Log;
import jxsource.net.proxy.PipeBase;
import jxsource.net.proxy.PipeRemoteToLocal;

@Deprecated
public class HttpPipeRemoteToLocal extends PipeRemoteToLocal {
	private HttpPipeProcess httpPipeProcess = HttpPipeProcess.build();
	private HttpEditor editor;

	public void init(String name, InputStream in, OutputStream out, Log log, boolean activeLog) {
		super.init(name, in, out, log, activeLog);
	}
	public HttpPipeRemoteToLocal setHttpPipeContext(HttpEditor editor) {
		this.editor = editor;
		return this;
	}

	@Override
	protected void proc() throws IOException {
		httpPipeProcess.init(name, in, out, log, editor).proc();
	}

}
