package jxsource.net.proxy;

import jxsource.net.proxy.http.HttpRequestEditor;
import jxsource.net.proxy.http.HttpPipeContext;
import jxsource.net.proxy.http.HttpPipeLocalToRemote;
import jxsource.net.proxy.http.HttpPipeRemoteToLocal;
import jxsource.net.proxy.http.HttpResponselEditor;
import jxsource.net.proxy.http.HttpWorker;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();

	public static WorkerFactory build() {
		return new WorkerFactory();
	}

//	public Worker create(String remoteHost, int remotePort) {
//		if (appContext.getLog() instanceof PrintLog) {
//			return new PipeWorker().setPipeLocalToRemote(new PipeLocalToRemote())
//					.setPipeRemoteToLocal(new PipeRemoteToLocal())
//					.setLog(appContext.getLog(), appContext.isHttpBodyLog());
//		} else {
//			HttpPipeContext requestContext = new HttpPipeContext()
//					.setRemoteHost(remoteHost).setRemotePort(remotePort)
//					.setHttpHeaderEditor(new HttpRequestEditor());
//			HttpPipeContext responseContext = new HttpPipeContext()
//					.setRemoteHost(remoteHost).setRemotePort(remotePort)
//					.setHttpHeaderEditor(new HttpResponselEditor());
//			
//			return new PipeWorker().setPipeLocalToRemote(new HttpPipeLocalToRemote()
//						.setHttpPipeContext(requestContext))
//					.setPipeRemoteToLocal(new HttpPipeRemoteToLocal()
//							.setHttpPipeContext(responseContext))
//					.setLog(appContext.getLog(), appContext.isHttpBodyLog());
////					.setLogProcess(null);
//		}
//	}
	
	public Worker create(String remoteHost, int remotePort) {
		String connType = appContext.getConnType();
		switch(connType) {
		case Constants.ConnTcpType:
			return new PipeWorker().setPipeLocalToRemote(new PipeLocalToRemote())
					.setPipeRemoteToLocal(new PipeRemoteToLocal())
					.setLog(appContext.getLog(), appContext.isHttpBodyLog());
		case Constants.CoonHttpType:	
			return new HttpWorker()
					.setLog(appContext.getLog(), appContext.getLog());
			default:
				throw new RuntimeException("invalide connType: "+connType);
		}
	}

}
