package jxsource.net.proxy;

public class Configuration {

	public static void config() {
		LogContext.get().setLogProcess(LogProcessHttp.class);
	}
}
