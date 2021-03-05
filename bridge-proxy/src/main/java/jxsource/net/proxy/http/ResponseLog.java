package jxsource.net.proxy.http;

public class ResponseLog extends HttpLog {

	private RequestLog requestLog;

	public ResponseLog(ProcessContext context) {
		super(context);
	}

	@Override
	public void logHeader(byte[] data) {
		if (header) {
			ps.println(new String(requestLog.getHeaderBytes()));
			ps.println(new String(data));
//			ps.println("---------------------------------");
		}
	}

	public ResponseLog setRequestLog(RequestLog requestLog) {
		this.requestLog = requestLog;
		return this;
	}
}
