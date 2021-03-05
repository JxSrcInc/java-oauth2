package jxsource.net.app;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class URIConn {

	public static void main(String...args) {
		try {
			new URIConn().conn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void conn() throws Exception {
		URL url = new URL("https://www.google.com");
		URLConnection conn = url.openConnection();
		conn.connect();
		System.out.println("********************* connected");
//		Map<String, List<String>> headers = conn.getHeaderFields();
//		for(String name: headers.keySet()) {
//			System.out.println("* "+name+" = "+headers.get(name));
//		}
//		conn.setDoOutput(true);
//		OutputStream out = 
//		System.out.println(conn);
		InputStream in = conn.getInputStream();
		byte[] b = new byte[1024*8];
		int i = 0;
		while((i=in.read(b)) != -1) {
			System.out.println(new String(b,0,i));
		}
	}
}
