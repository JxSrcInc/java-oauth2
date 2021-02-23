package jxsource.util.buffer.bytebuffer;

import java.io.IOException;
import java.io.OutputStream;

import jxsource.util.buffer.BufferException;

/*
 * Use OutputBuffer as an element in OutputStream chain.
 * Use OutputBufferSrc as chain's destination.
 */
public abstract class BufferOutputStream extends OutputStream {
	protected OutputStream next;
	
	protected void setNext(BufferOutputStream next) {
		this.next = next;
	}

	@Override
	public void write(int b) throws IOException {
		throw new BufferException("Not support write(int)");
	}

	@Override
	public abstract void write(byte[] b) throws IOException;

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		byte[] tmp = new byte[len];
		System.arraycopy(b, off, tmp, 0, len);
		write(tmp);
	}

	/*
	 * No action in this object.
	 * this action will finally pass to OutputBufferDst.flush().
	 * 
	 * To flush bytearray, use push() method.
	 */
	@Override
	public void flush() throws IOException {
		next.flush();
	}

	/*
	 * push all remaining bytes in bytearray to next OutputBuffer
	 */
	public abstract void push() throws IOException;

	@Override
	public void close() throws IOException {
		// clean remaining bytes in bytearray
		push();
		next.close();
	}
	
}
