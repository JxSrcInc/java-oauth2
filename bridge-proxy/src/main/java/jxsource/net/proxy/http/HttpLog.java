package jxsource.net.proxy.http;

import java.io.PrintStream;

public class HttpLog {
	private boolean header = true;
	private boolean content;
	private PrintStream ps = System.out;
	
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
	public void logHeader(byte[] data) {
		if(header)
		ps.println(new String(data));
	};

	public void logContent(byte[] data) {
		if(content)
		ps.println(new String(data));
	};

}
