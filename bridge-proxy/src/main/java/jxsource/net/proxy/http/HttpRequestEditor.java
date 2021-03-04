package jxsource.net.proxy.http;

import jxsource.net.proxy.util.ByteBuffer;

/*
 * edit Hppt Headers in request.
 */
public class HttpRequestEditor extends HttpEditor{

	// include the last CRLFCRLF bytes
	@Override
	public byte[] edit(HttpHeader headers) {
		headers.removeHeader("Host");
		return headers.getBytes();
	}

}
