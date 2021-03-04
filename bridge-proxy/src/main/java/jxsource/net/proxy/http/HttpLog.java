package jxsource.net.proxy.http;

import java.io.PrintStream;

public abstract class HttpLog {
	protected boolean header = true;
	protected boolean content;
	protected boolean export;
	protected PrintStream ps = System.out;
	protected HttpContext context;
	private FileLog fileLog;

	protected void startSave() {
		fileLog = FileLog.build("need implement");
	}
	public HttpLog setHttpContext(HttpContext context) {
		this.context = context;
		return this;
	}

	public HttpLog setPrintStream(PrintStream ps) {
		this.ps = ps;
		return this;
	}
	public HttpLog setExport(boolean export) {
		this.export = export;
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
		if(content) ps.println(new String(data));
		if(export && fileLog != null) fileLog.save(data);
	};
	
	protected void closeFileLog() {
		if(fileLog != null) {
			fileLog.close();
			fileLog = null;
		}
	}

}
