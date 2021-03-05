package jxsource.net.proxy.util;

import java.io.IOException;

/*
 * It is reusable
 */
public class HttpUtil {
	
	public static final String[] Methods = {"GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH","HTTP"};
	static final byte CR = 13;
	static final byte LF = 10;

	public static HttpUtil build() {
		return new HttpUtil();
	}
	// include last CRLFCRLF
	public int getHerdersLength(byte[] buf) throws IOException{
		return getHerdersLength(buf, buf.length);
	}

	// include last CRLFCRLF
	public int getHerdersLength(byte[] buf, int size) throws IOException{
		int count = 0;
		for (count = 0; count < size; count++) {
			if (count > 3 && buf[count] == LF && buf[count - 1] == CR && buf[count - 2] == LF && buf[count - 3] == CR) {
				// since count starts from 0, so the length of bytes is count+1
				return count+1;
			}
		}
		return -1;
	}

	// include last CRLFCRLF
	public int getHerdersLength(ByteBuffer buf) {
		int index = buf.indexOfCRLFCRLF();
		return (index==-1?index:index+4);
	}

	// include last CRLF
	public int getChunkHeaderLength(ByteBuffer buf) {
		int index = buf.indexOfCRLF();
		return (index==-1?index:index+2);
//		int count = 0;
//		byte[] bytes = buf.getArray();
//		for (count = 0; count < bytes.length; count++) {
//			if (count > 1 && bytes[count] == LF && bytes[count - 1] == CR) {
//				// TODO: remove additional bytes before request Method and response HTTP
//				return count+1;
//			}
//		}
//		return -1;

	}


}
