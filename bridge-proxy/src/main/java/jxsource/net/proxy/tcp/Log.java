package jxsource.net.proxy.tcp;

import jxsource.net.proxy.Constants;

public abstract class Log {

	public abstract void logLocalToRemote(byte[] data);
	public abstract void logRemoteToLocal(byte[] data);
	public void logPipe(String pipeName, byte[] data) {
		if(Constants.LocalToRemote.equals(pipeName)) {
			logLocalToRemote(data);
		} else {
			logRemoteToLocal(data);
		}
	}
	public void logPipe(String pipeName, byte[] data, int offset, int len) {
		if(Constants.LocalToRemote.equals(pipeName)) {
			logLocalToRemote(data, offset, len);
		} else {
			logRemoteToLocal(data, offset, len);
		}
	}
	public void logLocalToRemote(byte[] data, int offset, int len) {
		byte[] d = new byte[len];
		System.arraycopy(data, offset, d, 0, len);
		logLocalToRemote(d);
	};
	public void logRemoteToLocal(byte[] data, int offset, int len) {
		byte[] d = new byte[len];
		System.arraycopy(data, offset, d, 0, len);
		logRemoteToLocal(d);
	};
	
}
