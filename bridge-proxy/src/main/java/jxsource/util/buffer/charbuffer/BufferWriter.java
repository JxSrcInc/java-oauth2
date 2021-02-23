package jxsource.util.buffer.charbuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jxsource.util.buffer.BufferException;

/*
 * Use OutputBuffer as an element in Writer chain.
 * Use OutputBufferSrc as chain's destination.
 */
public abstract class BufferWriter extends OutputStreamWriter {
	public BufferWriter() {
		super(new ByteArrayOutputStream());
		// TODO Auto-generated constructor stub
	}

	protected OutputStreamWriter next;
	
	protected void setNext(BufferWriter next) {
		this.next = next;
	}

	@Override
	public void write(int b) throws IOException {
		throw new BufferException("Not support write(int)");
	}

	@Override
	public abstract void write(char[] b) throws IOException;

	@Override
	public void write(char[] b, int off, int len) throws IOException {
		char[] tmp = new char[len];
		System.arraycopy(b, off, tmp, 0, len);
		write(tmp);
	}

	/*
	 * No action in this object.
	 * this action will finally pass to OutputBufferDst.flush().
	 * 
	 * To flush chararray, use push() method.
	 */
	@Override
	public void flush() throws IOException {
		next.flush();
	}

	/*
	 * push all remaining chars in chararray to next OutputBuffer
	 */
	public abstract void push() throws IOException;

	@Override
	public void close() throws IOException {
		// clean remaining chars in chararray
		push();
		next.close();
	}

	@Override
	public String getEncoding() {
		return next.getEncoding();
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(), off, len);
	}
	
}
