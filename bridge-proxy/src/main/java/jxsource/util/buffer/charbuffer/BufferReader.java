package jxsource.util.buffer.charbuffer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jxsource.util.buffer.BufferException;

/*
 * Use InputBuffer as an element in InputReader chain.
 * Use InputBufferSrc as chain's source
 */
public abstract class BufferReader extends InputStreamReader{

	public BufferReader() {
		/*
		 * super must be called. But initialized ByteArrayInputStream
		 * never used. All methods refer to next
		 */
		super(new ByteArrayInputStream(new byte[0]));
	}
	protected InputStreamReader next;
	// must be called in sub class.
	protected void setInputBuffer(BufferReader next) {
		this.next = next;
	}
	public abstract int read(char[] buf) throws IOException;
			
	public int read(char[] buf, int offset, int length) throws IOException {
		char[] tmp = new char[length];
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
	public abstract boolean ready() throws IOException; 

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
	@Override
	public String getEncoding() {
		// TODO Auto-generated method stub
		return next.getEncoding();
	}
	
}
