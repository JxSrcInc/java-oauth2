package jxsource.util.buffer;

public class BufferException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public BufferException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public BufferException(String message) {
		super(message);
	}
	public BufferException(Throwable throwable) {
		super(throwable);
	}
	public BufferException() {
		super();
	}

}
