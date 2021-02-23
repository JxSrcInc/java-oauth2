package jxsource.net.proxy.http;

import jxsource.net.proxy.Log;

public class HttpLog extends Log{
	public void logLocalToRemote(byte[] data) {
		System.err.println(new String(data));
	};
	public void logRemoteToLocal(byte[] data) {
		System.out.println(new String(data));
	};

}
