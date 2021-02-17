package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpParser;

import jxsource.net.proxy.util.exception.HttpHeaderReaderException;

public class HttpHeaderReader {
	
	public static final String[] Methods = {"GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH","HTTP"};
	static final byte b13 = 13;
	static final byte b10 = 10;

	private int size = 1024 * 8;
	private byte[] httpHeaderBytes; 

	public static HttpHeaderReader build() {
		return new HttpHeaderReader();
	}
	public byte[] getHeaderBytes() {
		return httpHeaderBytes;
	}
	// TODO: handle concurrent?
	public byte[] getHeaderBytes(InputStream in) throws IOException, HttpHeaderReaderException{
		byte[] buf = new byte[size];
		int count = 0;
		for (count = 0; count < size; count++) {
			buf[count] = (byte) in.read();
//			System.err.print((char)buf[count]);
			if (count > 4 && buf[count] == b10 && buf[count - 1] == b13 && buf[count - 2] == b10 && buf[count - 3] == b13) {
				break;
			}
		}
		httpHeaderBytes =  new byte[count];
		System.arraycopy(buf, 0, httpHeaderBytes, 0, count);
		if(count == size) {
			throw new HttpHeaderReaderException();
		}
//		count = 0;
		return httpHeaderBytes;
	}
	
}
