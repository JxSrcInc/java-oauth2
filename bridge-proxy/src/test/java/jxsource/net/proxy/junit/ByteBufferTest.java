package jxsource.net.proxy.junit;

import org.junit.Before;
import org.junit.Test;

import jxsource.net.proxy.util.ByteBuffer;
public class ByteBufferTest {
	public static final byte CR = ByteBuffer.CR;
	public static final byte LF = ByteBuffer.LF;
	public static final byte[] CRLF = ByteBuffer.CRLF;
	public static final String StrCRLF = ByteBuffer.StrCRLF;
	public static final byte[] CRLFCRLF = ByteBuffer.CRLFCRLF;
	public static final String StrCRLFCRLF = ByteBuffer.StrCRLFCRLF;
	
	ByteBuffer headers;
	
	@Before
	public void init() {
		headers = new ByteBuffer()
			.append("HTTP/1.1 200 OK").append(CRLF)
			.append("Date: Sat, 27 Feb 2021 04:31:46 GMT").append(CRLF)
			.append("Transfer-Encoding: chunked").append(CRLF)
			.append(CRLF);
	}

	@Test
	public void testFirstCRLF() {
		int index = headers.indexOfCRLF();
		assert(index==15);
	}

	@Test
	public void testFirstCRLFCRLF() {
		int index = headers.indexOfCRLFCRLF();
		assert(index==80);
	}

}
