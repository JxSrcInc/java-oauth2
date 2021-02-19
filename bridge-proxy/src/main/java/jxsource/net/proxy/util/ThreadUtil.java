package jxsource.net.proxy.util;

public class ThreadUtil {
	public static Thread createThread(Runnable r) {
		return new Thread(r, r.getClass().getSimpleName());
	}
	
	public static String threadInfo() {
		return String.format("Thread(%s:%d)",
				Thread.currentThread().getName(), Thread.currentThread().hashCode());
	}
}
