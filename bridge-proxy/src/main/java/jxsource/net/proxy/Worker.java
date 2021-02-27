package jxsource.net.proxy;

import java.io.InputStream;
import java.net.Socket;

public interface Worker extends Runnable{
	public void init(Socket localSocket, InputStream localSocketInput, Socket remoteSocket) throws Exception;
}
