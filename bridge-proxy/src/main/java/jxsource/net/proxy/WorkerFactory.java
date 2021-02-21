package jxsource.net.proxy;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();
	
	public static WorkerFactory build() {
		return new WorkerFactory();
	}
	public Worker create() {
		return new Worker()
		.setPipeLocalToRemote(new PipeLocalToRemote())
		.setPipeRemoteToLocal(new PipeRemoteToLocal())
		.setLogProcess(appContext.getLogProcess());
	}
}
