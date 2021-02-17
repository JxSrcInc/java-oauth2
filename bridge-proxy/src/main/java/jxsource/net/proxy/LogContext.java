package jxsource.net.proxy;

/*
 * It is a global service register 
 */
public class LogContext {
	private static LogContext me;
	private Class<?> classLogProcess = LogProcessPrint.class;
	private LogContext() {}
	
	public static LogContext get() {
		if(me == null) {
			me = new LogContext();
		}
		return me;
	}
	
	public LogContext setLogProcess(Class<?> classLogProcess) {
		this.classLogProcess = classLogProcess;
		return this;
	}
	
	public LogProcess getLogProcess() throws InstantiationException, IllegalAccessException {
		return (LogProcess) classLogProcess.newInstance();
	}
}
