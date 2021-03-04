package jxsource.net.proxy.junit;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.junit.Before;
import org.junit.Test;

import jxsource.net.proxy.http.HttpEditor;
import jxsource.net.proxy.http.HttpHeader;
import jxsource.net.proxy.http.HttpRequestEditor;
import jxsource.net.proxy.util.ByteBuffer;
import jxsource.net.proxy.util.HttpUtil;
public class HttpTest {
	public static final byte CR = ByteBuffer.CR;
	public static final byte LF = ByteBuffer.LF;
	public static final byte[] CRLF = ByteBuffer.CRLF;
	public static final String StrCRLF = ByteBuffer.StrCRLF;
	public static final byte[] CRLFCRLF = ByteBuffer.CRLFCRLF;
	public static final String StrCRLFCRLF = ByteBuffer.StrCRLFCRLF;
	
	ByteBuffer headers;
	ByteBuffer _headers;
	
	@Before
	public void init() {
		_headers = new ByteBuffer()
			.append("HTTP/1.1 200 OK").append(CRLF)
			.append("Date: Sat, 27 Feb 2021 04:31:46 GMT").append(CRLF)
			.append("Host: localhost:9004").append(CRLF)
			.append("Transfer-Encoding: chunked").append(CRLF);
		headers = new ByteBuffer().append(_headers)
			.append(CRLF)
			.append("body");
	}

	byte[] getHeaderBytes() {
		int index = HttpUtil.build().getHerdersLength(headers);
		return headers.subArray(0,index);
	}
	
	@Test
	public void testUtilBadHeader() {
		int index = HttpUtil.build().getHerdersLength(_headers);
		assert(index==-1);
		assert(HttpUtil.build().getHerdersLength(_headers) == -1);
	}

	@Test
	public void testUtilHeaders() {
		int index = HttpUtil.build().getHerdersLength(headers);
		assert(headers.getLimit() == index+4);
	}
	
	@Test
	public void testHeader() throws HttpException, IOException {
		byte[] bufferBytes = getHeaderBytes();
		HttpHeader headers = HttpHeader.build();
		headers.init(bufferBytes);
		byte[] headerBytes = headers.getBytes();
//		System.out.println(new String(headerBytes));
		assert(headerBytes.length == bufferBytes.length);
	}
	
	@Test
	public void testEditor() throws HttpException, IOException {
		byte[] headerBytes = getHeaderBytes();
		HttpHeader headers = HttpHeader.build();
		headers.init(headerBytes);
		HttpEditor editor = new HttpRequestEditor();
		byte[] editorBytes = editor.edit(headers);
		System.out.println(new String(headerBytes));
		assert(headerBytes.length == editorBytes.length);
		
	}
	
	@Test
	public void testHttpHeaderRemove() throws HttpException, IOException {
		byte[] headerBytes = getHeaderBytes();
		HttpHeader headers = HttpHeader.build();
		headers.init(headerBytes);
		assert(headers.removeHeader("Host") == 1);
		System.out.println(headers);
		assert(headers.getHeader("Host") == null);
	}
	@Test
	public void testHttpRequestEditorRemove() throws HttpException, IOException {
		byte[] headerBytes = getHeaderBytes();
		HttpHeader headers = HttpHeader.build();
		headers.init(headerBytes);
		HttpEditor editor = new HttpRequestEditor();
		byte[] editorBytes = editor.edit(headers);
		System.out.println(new String(editorBytes));
	}

}
