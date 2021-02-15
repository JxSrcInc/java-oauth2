package jxsource.net.proxy;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HttpMessage {
	private ObjectMapper mapper = new ObjectMapper();
	protected String firstLine;
	protected Header[] headers = new Header[0];
	protected byte[] content = new byte[0];
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	public Header[] getHeaders() {
		return headers;
	}
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	public byte[] getBody() {
		return content;
	}
	public String getContent() {
		return new String(content);
	}
	public void setBody(byte[] body) {
		this.content = body;
	}
	public String getHeader(String name) {
		for(Header header: headers) {
			if(header.getName().toLowerCase().equals(name.toLowerCase())) {
				return header.getValue();
			}
		}
		return null;
	}
	public int getContentLength() {
		String len = getHeader("content-length");
		if(len != null) {
			return Integer.parseInt(len);
		} else {
			return 0;
		}
	}
	public boolean isTransferCodingChunked() {
		String transferEncoding = getHeader("transfer-encoding");
		if(transferEncoding!=null && transferEncoding.toLowerCase().equals("chunked")) {
			return true;
		} else {
			return false;
		}
	}
	@Override
	public String toString() {
//		try {
//			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
//		} catch (JsonProcessingException e) {
		return "\nHttpMessage [firstLine=" + firstLine + ", headers=" + Arrays.toString(headers) + ", content="
				+ new String(content) + "]";
//		}
	}
	
}
