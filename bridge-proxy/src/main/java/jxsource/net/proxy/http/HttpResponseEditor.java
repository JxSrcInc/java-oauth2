package jxsource.net.proxy.http;

/*
 * edit Headers in Http Response
 */
public class HttpResponseEditor extends HttpEditor {

	// include the last CRLFCRLF bytes
	@Override
	public byte[] edit(HttpHeader src) {
		return src.getBytes();
	}

}
