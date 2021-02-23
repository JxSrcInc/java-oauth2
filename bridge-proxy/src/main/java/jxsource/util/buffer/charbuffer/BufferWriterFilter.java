package jxsource.util.buffer.charbuffer;

import java.io.IOException;

import jxsource.util.buffer.BufferException;

/*
 * To remove char[], set parameter replace to null
 */
public class BufferWriterFilter extends BufferWriter{
	private CharArray chararray = new CharArray();
	private char[] find;
	private char[] replace;
	private long outBytes = 0L;

	// remove constructor
	public BufferWriterFilter(BufferWriter next, char[] find) {
		this(next, find, null);
	}
	
	public BufferWriterFilter(BufferWriter next, char[] find, char[] replace) {
		setNext(next);
		setFind(find);
		setReplace(replace);
	}

	/*
	 * Change find chars. 
	 * It has effect only on the output after it is called
	 * However it applies to all chars come in from write() method
	 * AND remaining chars in chararray before it is called.
	 * 
	 * Example
	 * 1. find = 345
	 * 2. write in 1234567890
	 * 3. remove 345 and chararray = 1267890
	 * 4. output 1267
	 * 5. chararray = 890
	 * 6. set find = 012
	 * 7. write in 1234567890
	 * 8. chararray = 8901234567890
	 * 9. remove 012. NOTE chararray = 8934567890
	 * 		0 from the remaining chars in chararray and 12 from the new write
	 * 		are removed !!!
	 */
	public BufferWriterFilter setFind(char[] find) {
		if(find == null || find.length == 0) {
			throw new BufferException("find parameter cannot be empty.");
		} else {
			this.find = find;
		}
		return this;
	}
	/*
	 * Change replace chars. 
	 * It has effect only on the output after it is called
	 */
	public BufferWriterFilter setReplace(char[] replace) {
		if(replace == null) {
			this.replace = new char[0]; 
		} else {
			this.replace = replace;
		}
		return this;
	}

	// return the number of output chars
	private int update(CharArray chararray) {
		int i = chararray.indexOf(find);
		if(i != -1) {
			chararray.replaceFirst(find, replace);
			return i+replace.length;
		} else {
			/*
			 * if chararray.getLimit() > find.length, then keep the last find.length chars
			 * and output all other chars. Because the output chars contain no find chars
			 * otherwise, output nothing
			 */
			return Math.max(0, chararray.length() - find.length);
		}
	}

	@Override
	public void write(char[] b) throws IOException {
		chararray.append(b);
		int outputBytes = 0;
		while((outputBytes = update(chararray)) > 0) {
			char[] buf = chararray.remove(outputBytes);
			outBytes += buf.length;
			next.write(buf);
		}
	}

	/*
	 * write all chars in chararray;
	 */
	@Override
	public void push() throws IOException {
		char[] buf = chararray.remove(chararray.getLimit());
		next.write(buf);
		if(next instanceof BufferWriter) {
			((BufferWriter)next).push();
		}
	}
	
	public long getOutBytes() {
		return outBytes;
	}
}
