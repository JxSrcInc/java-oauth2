package jxsource.net.proxy;

/*
 * It is a global service register 
 */
public class LogContext {
	private static LogContext me;
	private String classLogProcess = "jxsource.net.proxy.LogProcessHttp";
	private LogContext() {}
	
	public static LogContext get() {
		if(me == null) {
			me = new LogContext();
		}
		return me;
	}
	
	public LogContext setLogProcess(String classLogProcess) {
		this.classLogProcess = classLogProcess;
		return this;
	}
	
	public LogProcess getLogProcess() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (LogProcess) Class.forName(classLogProcess).newInstance();
	}
}
