package jxsource.net.proxy.http;

import java.util.HashMap;
import java.util.Map;

/*
 * Context used by RequestProcess and ResponseProcess separately
 * Use SessionContext to get common information and pass information between them
 */
public class ProcessContext {
	private String name;
	private Map<String, Object> attributes = new HashMap<>();
	private SessionContext sessionContext;

	public ProcessContext(String name) {
		this.name = name;
	}
	
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public ProcessContext setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		return this;
	}

	public ProcessContext addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public ProcessContext removeAttribute(String name) {
		attributes.remove(name);
		return this;
	}
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public String getValue(String name) {
		Object val = attributes.get(name);
		return val==null?null:val.toString();
	}
	
}
