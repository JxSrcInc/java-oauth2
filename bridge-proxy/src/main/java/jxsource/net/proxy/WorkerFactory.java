package jxsource.net.proxy;

import jxsource.net.proxy.http.HttpRequestEditor;
import jxsource.net.proxy.http.ProcessContext;
import jxsource.net.proxy.http.HttpResponseEditor;
import jxsource.net.proxy.http.HttpWorker;
import jxsource.net.proxy.tcp.PipeLocalToRemote;
import jxsource.net.proxy.tcp.PipeRemoteToLocal;
import jxsource.net.proxy.tcp.PipeWorker;
import jxsource.net.proxy.tcp.PrintLog;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();

	public static WorkerFactory build() {
		return new WorkerFactory();
	}


	public Worker create(String remoteHost, int remotePort) {
		String connType = appContext.getConnType();
		switch(connType) {
		case Constants.ConnTcpType:
			return new PipeWorker().setPipeLocalToRemote(new PipeLocalToRemote())
					.setPipeRemoteToLocal(new PipeRemoteToLocal())
					.setLog(new PrintLog(), appContext.isTcpLog());
		case Constants.CoonHttpType:	
			return new HttpWorker();
			default:
				throw new RuntimeException("invalide connType: "+connType);
		}
	}

}
