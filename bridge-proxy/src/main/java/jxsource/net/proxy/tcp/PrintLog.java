package jxsource.net.proxy.tcp;

public class PrintLog extends Log {
	public void logLocalToRemote(byte[] data) {
		System.err.println(new String(data));
	};
	public void logRemoteToLocal(byte[] data) {
		System.out.println(new String(data));
	};

}
