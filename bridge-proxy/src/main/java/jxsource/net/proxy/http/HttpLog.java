package jxsource.net.proxy.http;

public class HttpLog {
	public void print(String name, byte[] data) {
		System.out.println(new String(data));
	};

}
