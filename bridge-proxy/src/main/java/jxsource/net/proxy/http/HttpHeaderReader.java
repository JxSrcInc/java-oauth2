package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Worker;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;

public class HttpHeaderReader {
	private static Logger log = LoggerFactory.getLogger(HttpHeaderReader.class);
	
	public static final String[] Methods = {"GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH","HTTP"};
	static final byte b13 = 13;
	static final byte b10 = 10;

	// TODO: assume the size is big enough to hold HTTP headers
	private int size = 1024 * 8;

	public static HttpHeaderReader build() {
		return new HttpHeaderReader();
	}
	// TODO: handle concurrent?
	public byte[] getHeaderBytes(InputStream in) throws IOException, HttpHeaderReaderException{
		byte[] buf = new byte[size];
		int count = 0;
		for (count = 0; count < size; count++) {
			buf[count] = (byte) in.read();
			if (count > 4 && buf[count] == b10 && buf[count - 1] == b13 && buf[count - 2] == b10 && buf[count - 3] == b13) {
				break;
			}
		}
		if(count == size) {
			log.error(String.format("**** Input stream has more then %d bytes: \n%s", size, new String(buf)));
			throw new HttpHeaderReaderException();
		}
		byte[] httpHeaderBytes =  new byte[count];
		System.arraycopy(buf, 0, httpHeaderBytes, 0, count);
		return httpHeaderBytes;
	}
	
}