package jxsource.util.buffer.charbuffer;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class BufferWriterDst extends BufferWriter{
	
	public BufferWriterDst(OutputStreamWriter next) {
		setNext(null);
		this.next = next;
	}

	@Override
	public void write(char[] b) throws IOException {
		next.write(b);
	}

	@Override
	public void push() throws IOException {
		// OutputStreamWriter no such method and don't need it.
		// ignore
	}

}
