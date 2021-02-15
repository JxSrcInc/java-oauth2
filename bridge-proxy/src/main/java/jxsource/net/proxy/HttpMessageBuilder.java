package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

public class HttpMessageBuilder {
	
	public HttpMessage extractMessage(InputStream is) throws Exception{
		String firstLine = HttpParser.readLine(is,Charset.defaultCharset().name());
		Header[] headers = HttpParser.parseHeaders(is,Charset.defaultCharset().name());
		HttpMessage msgObj = null;
		if(firstLine.trim().indexOf("HTTP") == 0) {
			msgObj = new HttpResponse();
		} else {
			msgObj = new HttpRequest();
		}
		msgObj.setFirstLine(firstLine);
		msgObj.setHeaders(headers);
		if(msgObj.isTransferCodingChunked()) {
			// TODO: chunk processing
			
		} else {
			int len = msgObj.getContentLength();
			if(len > 0) {
				msgObj.setBody(getContent(len, is));
			}
		}
		// skip Message end
//		getContent(4, is);
		
		return msgObj;
	}
	
	private byte[] getContent(int len, InputStream is) throws IOException{
		byte[] buf = new byte[len];
		int position = 0;
		while(position<len) {
			int i = is.read(buf,position, len-position);
			position += i;
		}
		return buf; 
	}
	
}
