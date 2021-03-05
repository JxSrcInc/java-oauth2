package jxsource.aspectj.json.debug;

public interface JsonFactory<T extends Object> {
	public T createCallBefore(CallLog callLog);
	public T createCallAfter(CallLog callLog);
	public T createCallException(CallLog callLog);
}
