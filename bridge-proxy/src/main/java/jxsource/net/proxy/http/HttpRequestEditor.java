package jxsource.net.proxy.http;

import jxsource.net.proxy.AppContext;
import jxsource.net.proxy.util.ByteBuffer;

/*
 * edit Hppt Headers in request.
 */
public class HttpRequestEditor extends HttpEditor{

	private String domain;

	// include the last CRLFCRLF bytes
	@Override
	public byte[] edit(HttpHeader headers) {
		if(domain == null) {
			domain = AppContext.get().getRemoteDomain();
		}
		headers.removeHeader("Host");
		headers.setHeader("Host", domain);
		return headers.getBytes();
	}

}
