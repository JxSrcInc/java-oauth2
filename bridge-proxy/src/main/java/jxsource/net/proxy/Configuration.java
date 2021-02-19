package jxsource.net.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Configuration {

	@Value("${proxy.log.process:jxsource.net.proxy.LogProcessHttp}")
	String logProcess;
	public void config() {
		LogContext.get().setLogProcess(logProcess);
	}
}
