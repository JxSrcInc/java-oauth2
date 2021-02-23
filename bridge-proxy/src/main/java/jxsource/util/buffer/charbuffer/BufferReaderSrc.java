package jxsource.util.buffer.charbuffer;

import java.io.IOException;
import java.io.InputStreamReader;

public class BufferReaderSrc extends BufferReader{
	public BufferReaderSrc(InputStreamReader in) {
		next = in;
	}

	@Override
	public int read(char[] b) throws IOException {
		return next.read(b);
	}

	@Override
	public boolean ready() throws IOException {
		return next.ready();
	}

	@Override
	public long skip(long n) throws IOException {
		return next.skip(n);
	}

}
