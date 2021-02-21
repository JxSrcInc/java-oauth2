package jxsource.net.proxy;

import jxsource.net.proxy.http.HttpPipeLocalToRemote;
import jxsource.net.proxy.http.HttpPipeRemoteToLocal;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();

	public static WorkerFactory build() {
		return new WorkerFactory();
	}

	public Worker create() {
		if (appContext.getLogProcess() instanceof LogProcessPrint) {
			return new Worker().setPipeLocalToRemote(new PipeLocalToRemote())
					.setPipeRemoteToLocal(new PipeRemoteToLocal())
					.setLogProcess(appContext.getLogProcess());
		} else {
			// LogProcessHttp
			return new Worker().setPipeLocalToRemote(new HttpPipeLocalToRemote())
					.setPipeRemoteToLocal(new HttpPipeRemoteToLocal())
					.setLogProcess(appContext.getLogProcess());
//					.setLogProcess(null);
		}
	}
}
