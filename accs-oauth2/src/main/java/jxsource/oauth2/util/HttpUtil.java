package jxsource.oauth2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;

public class HttpUtil {
	public static String getAuth(String authVal) {
		String[] array = authVal.split(" ");
		// TODO remove binary part
		return array[0] + ' ' + JwtUtil.jwtBase64Decode(array[1]);
//		return array[0] + ' ' + new String(Base64Codec.BASE64URL.decode(array[1]));
	}
	public static String getHeaders(HttpHeaders headers) {
		String info = "";
		for(String name: headers.keySet() ) {
			if(name.equals("authorization")) {
				info += "\t"+name+"="+getAuth(headers.get(name).get(0)) + '\n';
			} else {
				info += "\t"+name+"="+headers.get(name) + '\n';
			}
		}
		return info;
	}
	public static String getRequestHeaders(HttpRequest req) {
		String info = "\nURL: "+req.getURI()+"\n"+
				"Header:\n";
		info += getHeaders(req.getHeaders());
		return info;
	}

	public static String getRequestInfo(HttpRequest req) {
		String info = "\nURL: "+req.getURI()+"\n"+
				"Header:\n";
		info += getHeaders(req.getHeaders());
		return info;
	}
	public static String getRequestInfo(HttpServletRequest req) {
		String info = "\nURL: "+req.getRequestURI()+"\n"+
				"QueryString: " + req.getQueryString()+"\n"+
				"Header:\n";
		Enumeration<String> headers = req.getHeaderNames();
		while(headers.hasMoreElements()) {
			String header = headers.nextElement();
			if(header.equals("authorization")) {
				info += "\t"+header+"="+getAuth(req.getHeader(header)) + '\n';
			} else {
				info += "\t"+header+"="+req.getHeader(header) + '\n';
			}
		}
		try {
			info += getBody(req.getInputStream());
		} catch(IOException ioe) {
			info += "Request input stream read error:"+ioe.getMessage();
		}
		return info;
	}
	public static String getRequestHeaders(HttpServletRequest req) {
		String info = "\nURL: "+req.getRequestURI()+"\n"+
				"QueryString: " + req.getQueryString()+"\n"+
				"Header:\n";
		Enumeration<String> headers = req.getHeaderNames();
		while(headers.hasMoreElements()) {
			String header = headers.nextElement();
			if(header.equals("authorization")) {
				info += "\t"+header+"="+getAuth(req.getHeader(header)) + '\n';
			} else {
				info += "\t"+header+"="+req.getHeader(header) + '\n';
			}
		}
		return info;
	}
	
	public static String getResponseInfo(ClientHttpResponse resp) {
		String info = "\nHeader:\n";
		HttpHeaders headers = resp.getHeaders();
		for(Entry<String, List<String>> header: headers.entrySet()) {
			info += "\t"+header.getKey()+"="+header.getValue() + '\n';
		}
		try {
			info += getBody(resp.getBody());
		} catch(IOException ioe) {
			info += "Request input stream read error:"+ioe.getMessage();
		}
		return info;
	}

	public static String getResponseInfo(ResponseEntity<String> resp) {
		String info = "\nHeader:\n";
		HttpHeaders headers = resp.getHeaders();
		for(Entry<String, List<String>> header: headers.entrySet()) {
			info += "\t"+header.getKey()+"="+header.getValue() + '\n';
		}
		info += resp.getBody();
		return info;
	}
 static String getBody(InputStream is ) throws IOException{
		String info = "";
		Reader in = new InputStreamReader(is);
		char[] cbuf = new char[1024*8];
		int i = 0;
		StringBuilder strBuilder = new StringBuilder();
		while((i=in.read(cbuf))!= -1 ) {
			strBuilder.append(cbuf,0,i);
		}
		info += "Content:>\n"+strBuilder.toString()+"\n<\n";
		return info;
	}

}
