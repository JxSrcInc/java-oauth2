package jxsource.net.proxy.http;

import java.io.PrintStream;

public abstract class HttpLog {
	protected boolean header = true;
	protected boolean content;
	protected PrintStream ps = System.out;
	protected HttpContext context;

	public HttpLog setHttpContext(HttpContext context) {
		this.context = context;
		return this;
	}

	public HttpLog setPrintStream(PrintStream ps) {
		this.ps = ps;
		return this;
	}
	public HttpLog setHeaderLog(boolean header) {
		this.header = header;
		return this;
	}
	public HttpLog setContentLog(boolean content) {
		this.content = content;
		return this;
	}
	public abstract void logHeader(byte[] data);

	public void logContent(byte[] data) {
		if(content)
		ps.println(new String(data));
	};

}
