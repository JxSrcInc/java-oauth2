package jxsource.net.proxy.util.exception;

public class HttpHeaderReaderException extends Exception {

	public HttpHeaderReaderException() {
		super("Buffer is not big enough to read Http Header");
	}
}
