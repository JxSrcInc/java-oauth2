package jxsource.net.proxy.util.exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpParser;

public class HttpMessageProcessor {
	public static final String[] Methods = {"GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH","HTTP"};
	static final byte b13 = 13;
	static final byte b10 = 10;
	private Header[] headers;
	public static HttpMessageProcessor build() {
		return new HttpMessageProcessor();
	}
	public void init(byte[] httpHeaderBytes) throws HttpException, IOException {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (count = 0; count < httpHeaderBytes.length; count++) {
			builder.append((char)httpHeaderBytes[count]);
//			System.err.print((char)buf[count]);
			if (count > 2 && httpHeaderBytes[count] == b10 && httpHeaderBytes[count - 1] == b13) {
				break;
			}
		}
		if(count == httpHeaderBytes.length) {
			throw new IOException("Invalid http headers: "+new String(httpHeaderBytes));
		}
		String line = builder.toString().toUpperCase();
		int startIndex = 0;
		int indexMethod = 0;
		String method = null;
		for(startIndex=0; startIndex<line.length(); startIndex++) {
			boolean match = false;
			for(indexMethod=0; indexMethod<Methods.length; indexMethod++) {
				method = Methods[indexMethod];
				String m = line.substring(startIndex, startIndex+method.length());
				if(m.equals(method)) {
					match = true;
					break;
				}
			}
			if (match) {
				break;
			}
		}
		if(indexMethod == Methods.length) {
			throw new IOException("No valid method or 'HTTP' found: "+line);
		}
//		System.err.println(new String(httpHeaderBytes));
		byte[] headerBytes = new byte[httpHeaderBytes.length - line.length()];
		System.arraycopy(httpHeaderBytes, line.length(), headerBytes, 0, headerBytes.length);
		
		InputStream byteArrayInputStream = new ByteArrayInputStream(headerBytes);
		headers = HttpParser.parseHeaders(byteArrayInputStream,Charset.defaultCharset().name());
	}
	
	public String getHeaderValue(String headerName) {
		for(Header header: headers) {
			if(header.getName().equals(headerName)) {
				return header.getValue();
			}
		}
		return null;
	}

}
