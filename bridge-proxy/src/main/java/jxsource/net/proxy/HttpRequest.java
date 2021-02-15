package jxsource.net.proxy;

import org.apache.commons.httpclient.Header;

public class HttpRequest extends HttpMessage{
	
	private String method;
	private String url;
	private String host;
	private int port;

	@Override
	public void setFirstLine(String firstLine) {
		super.setFirstLine(firstLine);
		String[] arr = firstLine.split(" ");
		method = arr[0];
		url = arr[1];
	}

	@Override
	public void setHeaders(Header[] headers) {
		super.setHeaders(headers);
		String value = getHeader("host");
		int index = value.indexOf(":");
		if(index == -1) {
			host = value;
			port = 80;
		} else {
			host = value.substring(0,index);
			port = Integer.parseInt(value.substring(index+1));
		}
	}
	
}
