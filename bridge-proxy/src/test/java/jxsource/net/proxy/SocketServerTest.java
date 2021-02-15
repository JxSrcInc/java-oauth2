package jxsource.net.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SocketServerTest {

	public static void main(String... args) {
		for(int i=0; i<1; i++) {
		new SocketServerTest().run();
		}
	}
		
	public void run() {
		try {
			URL url = new URL("http://localhost:9995");
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;
			InputStream in = http.getInputStream();
			byte[] buf = new byte[1024*8];
			int i = 0;
			System.out.println(http.getHeaderFields());
			while((i=in.read(buf)) != -1) {
				System.out.println(new String(buf,0,i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
