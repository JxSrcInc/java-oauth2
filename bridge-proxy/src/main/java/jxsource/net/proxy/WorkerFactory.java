package jxsource.net.proxy;

import jxsource.net.proxy.http.HttpRequestEditor;
import jxsource.net.proxy.http.HttpPipeContext;
import jxsource.net.proxy.http.HttpPipeLocalToRemote;
import jxsource.net.proxy.http.HttpPipeRemoteToLocal;
import jxsource.net.proxy.http.HttpResponselEditor;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();

	public static WorkerFactory build() {
		return new WorkerFactory();
	}

	public Worker create(String remoteHost, int remotePort) {
		if (appContext.getLog() instanceof PrintLog) {
			return new Worker().setPipeLocalToRemote(new PipeLocalToRemote())
					.setPipeRemoteToLocal(new PipeRemoteToLocal())
					.setLog(appContext.getLog(), appContext.isActiveLog());
		} else {
			HttpPipeContext requestContext = new HttpPipeContext()
					.setRemoteHost(remoteHost).setRemotePort(remotePort)
					.setHttpHeaderEditor(new HttpRequestEditor());
			HttpPipeContext responseContext = new HttpPipeContext()
					.setRemoteHost(remoteHost).setRemotePort(remotePort)
					.setHttpHeaderEditor(new HttpResponselEditor());
			
			return new Worker().setPipeLocalToRemote(new HttpPipeLocalToRemote()
						.setHttpPipeContext(requestContext))
					.setPipeRemoteToLocal(new HttpPipeRemoteToLocal()
							.setHttpPipeContext(responseContext))
					.setLog(appContext.getLog(), appContext.isActiveLog());
//					.setLogProcess(null);
		}
	}
}
