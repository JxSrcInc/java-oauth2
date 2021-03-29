package jxsource.net.proxy.http;

import jxsource.net.proxy.Constants;

public class ResponseLog extends HttpLog {


	public ResponseLog(ProcessContext context) {
		super(context);
	}

	@Override
	public void logHeader(byte[] data) {
		if (header) {
			String msg = context.getSessionContext().getValue(Constants.RequestHeaderBytes) + 
					new String(data) + 
					"\n------------------------------------";
			ps.println(msg);
		}
	}

}
