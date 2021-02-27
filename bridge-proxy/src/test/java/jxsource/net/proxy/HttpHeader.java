package jxsource.net.proxy;

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


/*
 * It is reusable by call init() method.
 */
public class HttpHeader {
	public static final String[] Methods = {"GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH","HTTP"};
	static final byte RC = 13;
	static final byte LF = 10;
	static final String RCLF = new String(new byte[] {RC,LF});
	private Map<String, String> headers = new HashMap<String, String>();
	private String firstLine;
	
	public static HttpHeader build() {
		return new HttpHeader();
	}
	public void init(byte[] httpHeaderBytes) throws HttpException, IOException {
		StringTokenizer st = new StringTokenizer(new String(httpHeaderBytes), RCLF);
		firstLine = st.nextToken();
		if(!validateFirstLine(firstLine)) {
			throw new HttpException("invalid HTTP headers: \n"+new String(httpHeaderBytes));
		}
		while(st.hasMoreTokens()) {
			String header = st.nextToken();
			String[] item = header.split(":");
			headers.put(item[0], item[1]);
		}
	}
	
	private boolean validateFirstLine(String firstLine) {
		int startIndex = 0;
		int indexMethod = 0;
		String method = null;
		for(startIndex=0; startIndex<firstLine.length(); startIndex++) {
			boolean match = false;
			for(indexMethod=0; indexMethod<Methods.length; indexMethod++) {
				method = Methods[indexMethod];
				String m = firstLine.substring(startIndex, startIndex+method.length());
				if(m.equals(method)) {
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
		return headers.get(headerName);
	}
	
	public String getFirstLine() {
		return firstLine;
	}

}
