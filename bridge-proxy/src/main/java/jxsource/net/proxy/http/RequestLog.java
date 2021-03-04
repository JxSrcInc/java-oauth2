package jxsource.net.proxy.http;

public class RequestLog extends HttpLog {

	private byte[] headerBytes;
	@Override
	public void logHeader(byte[] data) {
		headerBytes = data;
	}

	public byte[] getHeaderBytes() {
		return headerBytes;
	}
}
