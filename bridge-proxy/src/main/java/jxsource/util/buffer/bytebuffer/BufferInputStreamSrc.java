package jxsource.util.buffer.bytebuffer;

import java.io.IOException;
import java.io.InputStream;

public class BufferInputStreamSrc extends BufferInputStream{
	public BufferInputStreamSrc(InputStream in) {
		next = in;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return next.read(b);
	}

	@Override
	public int available() throws IOException {
		return next.available();
	}

	@Override
	public long skip(long n) throws IOException {
		return next.skip(n);
	}

}
