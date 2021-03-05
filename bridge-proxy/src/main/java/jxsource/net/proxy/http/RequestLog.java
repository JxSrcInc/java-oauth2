package jxsource.net.proxy.http;

import jxsource.net.proxy.Constants;

public class RequestLog extends HttpLog {

	public RequestLog(ProcessContext context) {
		super(context);
	}
//	private byte[] headerBytes;
//	@Override
	public void logHeader(byte[] data) {
		// pass request headers to response to display togeter
		context.getSessionContext().addAttribute(Constants.RequestHeaderBytes, new String(data));
//		if (header) {
//			ps.println(new String(data));
//		}
	}

//	public byte[] getHeaderBytes() {
//		return headerBytes;
//	}
}
