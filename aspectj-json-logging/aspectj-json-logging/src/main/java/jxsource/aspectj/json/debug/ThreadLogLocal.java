package jxsource.aspectj.json.debug;

public class ThreadLogLocal {
    private static final ThreadLocal<ThreadLog> threadTraceLocal =
            new ThreadLocal<ThreadLog>() {
                @Override protected ThreadLog initialValue() {
                    return new ThreadLog();
            }
        };
	public static ThreadLog set(ThreadLog trace) {
		threadTraceLocal.set(trace);
		return threadTraceLocal.get();
	}
	// after remove, threadTraceLocal will create a new ThreadTrace object
	public static void reset() {
		threadTraceLocal.remove();
	}
	public static ThreadLog get() {
		return threadTraceLocal.get();
	}
}
