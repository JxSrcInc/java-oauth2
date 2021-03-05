package jxsource.net.proxy.http;

import java.io.PrintStream;

public abstract class HttpLog {
	// enable or disable log for Http Headers 
	protected boolean header = true;
	// enable or disable log for Http Content
	protected boolean content;
	// enable or disable download Http Content to file
	protected boolean downloadData;
	protected PrintStream ps = System.out;
	protected ProcessContext context;
	private FileLog fileLog;

	
	public void startSave() {
		if(downloadData) {
			fileLog = FileLog.build(context, this.getClass().getSimpleName());
		}
	}
	protected HttpLog(ProcessContext context) {
		this.context = context;
		downloadData = context.isDownloadData();
	}

	public HttpLog setPrintStream(PrintStream ps) {
		this.ps = ps;
		return this;
	}
	public HttpLog setExport(boolean export) {
		this.downloadData = export;
		return this;
	}
	// enable or disable log for Http Headers 
	public HttpLog setHeaderLog(boolean header) {
		this.header = header;
		return this;
	}
	// enable or disable log for Http Content
	public HttpLog setContentLog(boolean content) {
		this.content = content;
		return this;
	}
	public abstract void logHeader(byte[] data);

	public void logContent(byte[] data) {
		if(content) ps.println(new String(data));
		if(downloadData && fileLog != null) fileLog.save(data);
	};
	
	protected void close() {
		if(fileLog != null) {
			fileLog.close();
			fileLog = null;
		}
	}

}
