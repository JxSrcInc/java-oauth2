package jxsource.util.buffer.bytebuffer;

import java.io.IOException;

import jxsource.util.buffer.BufferException;

/*
 * To remove byte[], set parameter replace to null
 */
public class BufferOutputStreamFilter extends BufferOutputStream{
	private ByteArray bytearray = new ByteArray();
	private byte[] find;
	private byte[] replace;
	private long outBytes = 0L;

	// remove constructor
	public BufferOutputStreamFilter(BufferOutputStream next, byte[] find) {
		this(next, find, null);
	}
	
	public BufferOutputStreamFilter(BufferOutputStream next, byte[] find, byte[] replace) {
		setNext(next);
		setFind(find);
		setReplace(replace);
	}

	/*
	 * Change find bytes. 
	 * It has effect only on the output after it is called
	 * However it applies to all bytes come in from write() method
	 * AND remaining bytes in bytearray before it is called.
	 * 
	 * Example
	 * 1. find = 345
	 * 2. write in 1234567890
	 * 3. remove 345 and bytearray = 1267890
	 * 4. output 1267
	 * 5. bytearray = 890
	 * 6. set find = 012
	 * 7. write in 1234567890
	 * 8. bytearray = 8901234567890
	 * 9. remove 012. NOTE bytearray = 8934567890
	 * 		0 from the remaining bytes in bytearray and 12 from the new write
	 * 		are removed !!!
	 */
	public BufferOutputStreamFilter setFind(byte[] find) {
		if(find == null || find.length == 0) {
			throw new BufferException("find parameter cannot be empty.");
		} else {
			this.find = find;
		}
		return this;
	}
	/*
	 * Change replace bytes. 
	 * It has effect only on the output after it is called
	 */
	public BufferOutputStreamFilter setReplace(byte[] replace) {
		if(replace == null) {
			this.replace = new byte[0]; 
		} else {
			this.replace = replace;
		}
		return this;
	}

	// return the number of output bytes
	private int update(ByteArray bytearray) {
		int i = bytearray.indexOf(find);
		if(i != -1) {
			bytearray.replaceFirst(find, replace);
			return i+replace.length;
		} else {
			/*
			 * if bytearray.getLimit() > find.length, then keep the last find.length bytes
			 * and output all other bytes. Because the output bytes contain no find bytes
			 * otherwise, output nothing
			 */
			return Math.max(0, bytearray.length() - find.length);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		bytearray.append(b);
		int outputBytes = 0;
		while((outputBytes = update(bytearray)) > 0) {
			byte[] buf = bytearray.remove(outputBytes);
			outBytes += buf.length;
			next.write(buf);
		}
	}

	/*
	 * write all bytes in bytearray;
	 */
	@Override
	public void push() throws IOException {
		byte[] buf = bytearray.remove(bytearray.getLimit());
		next.write(buf);
		if(next instanceof BufferOutputStream) {
			((BufferOutputStream)next).push();
		}
	}
	
	public long getOutBytes() {
		return outBytes;
	}
}
