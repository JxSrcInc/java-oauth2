package jxsource.oauth2.echoserver;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

public class EchoServerApplicationTests {

	public static void main(String...args) {
		try {
			URL url = new URL("http://localhost:9988");
			InputStream in = url.openConnection().getInputStream();
			byte[] buf = new byte[1024*8];
			int i = 0;
			while((i=in.read(buf)) != -1) {
				System.out.print(new String(buf,0,i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
