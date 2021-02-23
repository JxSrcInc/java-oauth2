package jxsource.util.buffer.bytebuffer;

import java.io.IOException;
import java.io.OutputStream;

public class BufferOutputStreamDst extends BufferOutputStream{
	
	public BufferOutputStreamDst(OutputStream next) {
		setNext(null);
		this.next = next;
	}

	@Override
	public void write(byte[] b) throws IOException {
		next.write(b);
	}

	@Override
	public void push() throws IOException {
		// OutputStream no such method and don't need it.
		// ignore
	}

}
