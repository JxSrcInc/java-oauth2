package jxsource.util.buffer.bytebuffer;

import java.io.IOException;
import java.io.InputStream;

import jxsource.util.buffer.BufferException;

/*
 * Use BufferInputStreamFilter as an element in InputStream chain.
 * Use BufferInputStreamSrc as chain's source
 */
public abstract class BufferInputStream extends InputStream{

	protected InputStream next;
	// must be called in sub class.
	protected void setInputBuffer(BufferInputStream next) {
		this.next = next;
	}
	public abstract int read(byte[] buf) throws IOException;
			
	public int read(byte[] buf, int offset, int length) throws IOException {
		byte[] tmp = new byte[length];
		int i = read(tmp);
		System.arraycopy(tmp, 0, buf, offset, i);
		return i;
	}
	@Override
	public int read() throws IOException {
		throw new BufferException("Not support read()");
	}
	@Override
	public abstract long skip(long n) throws IOException;
	@Override
	public abstract int available() throws IOException; 

	@Override
	public void close() throws IOException {
		next.close();
	}
	@Override
	public synchronized void mark(int readlimit) {
		throw new BufferException("Not support  mark(int)");
	}
	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("Not support  reset()");
	}
	@Override
	public boolean markSupported() {
		return false;
	}
	
}
