package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	protected Map<String, List<String>> headers = new HashMap<String, List<String>>();
	protected String firstLine;

	public static HttpHeader build() {
		return new HttpHeader();
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	public void init(byte[] httpHeaderBytes) throws HttpException, IOException {
		headers.clear();
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
					String name = header.substring(0, i).trim();
					String value = header.substring(i + 1).trim();
					List<String> list = headers.get(name);
					if(list == null) {
						list = new ArrayList<String>();
						headers.put(name, list);
					}
					list.add(value);
				}
			}
		}
	}

	protected boolean validateFirstLine(String firstLine) {
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

	public String getFirstHeader(String headerName) {
		Map.Entry<String, List<String>> entry = getHeader(headerName);
		if(entry == null) {
			return null;
		}
		List<String> list = entry.getValue();
		return list.size() == 0 ? null : list.get(0);
	}

	
	public void setHeader(String name, String value) {
		List<String> list = new ArrayList<String>();
		list.add(value);
		headers.put(name, list);
	}

	public void addHeader(String name, String value) {
		Map.Entry<String, List<String>> entry = getHeader(name);
		List<String> list = null;
		if(entry == null) {
			list = new ArrayList<>();
			headers.put(name, list);
		} else {
			list = entry.getValue();
		}
		list.add(value);
	}

	public void setHeader(String name, List<String> value) {
		headers.put(name, value);
	}

	public int removeHeader(String name) {
		Map.Entry<String, List<String>> entry = getHeader(name);
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
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			for(String value: entry.getValue()) {
				bb.append(entry.getKey()).append(": ").append(value).append(CRLF);
			}
		}
		bb.append(CRLF);
		return bb.getArray();
	}

	// ignore case search
	public Map.Entry<String, List<String>> getHeader(String name) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			if (entry.getKey().toLowerCase().equals(name.toLowerCase())) {
				return entry;
			}
		}
		return null;
	}

	public List<String> getHeaderValue(String name) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			if (entry.getKey().toLowerCase().equals(name.toLowerCase())) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new String(getBytes());
	}
	
	public HttpHeader resetHttpHeader() {
		firstLine = "";
		headers.clear();
		return this;
	}
	
	public HttpHeader setFirstLine (String firstLine) {
		this.firstLine = firstLine;
		return this;
	}
}
