package jxsource.net.proxy.http;

public class RequestLog extends HttpLog {

	public RequestLog(ProcessContext context) {
		super(context);
	}
	private byte[] headerBytes;
	@Override
	public void logHeader(byte[] data) {
		headerBytes = data;
//		if (header) {
//			ps.println(new String(data));
//		}
	}

	public byte[] getHeaderBytes() {
		return headerBytes;
	}
}
