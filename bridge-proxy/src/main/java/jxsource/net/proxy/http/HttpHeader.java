package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpParser;

import jxsource.net.proxy.util.ByteBuffer;

/*
 * It is reusable by call init() method.
 */
public class HttpHeader {
	public static final String[] Methods = { "GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE",
			"PATCH", "HTTP" };
	static final byte[] CRLF = ByteBuffer.CRLF;
	static final String StrCRLF = ByteBuffer.StrCRLF;
	private Map<String, String> headers = new HashMap<String, String>();
	private String firstLine;

	public static HttpHeader build() {
		return new HttpHeader();
	}

	public void init(byte[] httpHeaderBytes) throws HttpException, IOException {
		StringTokenizer st = new StringTokenizer(new String(httpHeaderBytes), StrCRLF);
		boolean ok = true;
		while (st.hasMoreTokens()) {
			String header = st.nextToken();
			if (ok) {
				ok = false;
				firstLine = header;
				if (!validateFirstLine(firstLine)) {
					throw new HttpException("invalid HTTP headers: \n" + new String(httpHeaderBytes));
				}
			} else {
				int i = header.indexOf(":");
				if (i > 0) {
					headers.put(header.substring(0, i).trim(), header.substring(i + 1).trim());
				}
			}
		}
	}

	private boolean validateFirstLine(String firstLine) {
		int startIndex = 0;
		int indexMethod = 0;
		String method = null;
		for (startIndex = 0; startIndex < firstLine.length(); startIndex++) {
			boolean match = false;
			for (indexMethod = 0; indexMethod < Methods.length; indexMethod++) {
				method = Methods[indexMethod];
				String m = firstLine.substring(startIndex, startIndex + method.length());
				if (m.equals(method)) {
					match = true;
					break;
				}
			}
			if (match) {
				break;
			}
		}
		return indexMethod < Methods.length;
	}

	public String getHeaderValue(String headerName) {
		Map.Entry<String, String> entry = getHeader(headerName);
		return entry == null ? null : entry.getValue();
	}

	// return 0 = set, 1 = add
	public int setHeader(String name, String value) {
		Map.Entry<String, String> entry = getHeader(name);
		if (entry != null) {
			// set
			headers.put(entry.getKey(), value);
			return 0;
		} else {
			// add
			headers.put(name, value);
			return 1;
		}
	}

	public int removeHeader(String name) {
		Map.Entry<String, String> entry = getHeader(name);
		if(entry == null) {
			return 0;
		} else {
			headers.remove(entry.getKey());
			return 1;
		}
	}
	public String getFirstLine() {
		return firstLine;
	}

	// include the last CRLFCRLF bytes
	public byte[] getBytes() {
		ByteBuffer bb = new ByteBuffer();
		bb.append(firstLine).append(CRLF);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			bb.append(entry.getKey()).append(": ").append(entry.getValue()).append(CRLF);
		}
		bb.append(CRLF);
		return bb.getArray();
	}

	// ignore case search
	public Map.Entry<String, String> getHeader(String name) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			if (entry.getKey().toLowerCase().equals(name.toLowerCase())) {
				return entry;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return new String(getBytes());
	}
}
