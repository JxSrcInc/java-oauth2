package jxsource.util.buffer.charbuffer;

import java.io.IOException;

import jxsource.util.buffer.BufferException;

public class BufferReaderFilter extends BufferReader{
	protected char[] find;
	protected char[] replace;
	// buffer for output chars to caller
	protected CharArray outBuf = new CharArray();
	// buffer for input chars from next
	protected CharArray inBuf = new CharArray();
	protected boolean close;
	
	public BufferReaderFilter(BufferReader next, char[] find) {
		this(next, find, null);
	}

	public BufferReaderFilter(BufferReader next, char[] find, char[] replace) {
		this.setInputBuffer(next);
		setFind(find);
		setReplace(replace);
	}

	/*
	 * Change find chars. 
	 * It has effect only on the input after it is called
	 * 
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
	public BufferReaderFilter setFind(char[] find) {
		if(find == null || find.length == 0) {
			throw new BufferException("find parameter cannot be empty.");
		} else {
			this.find = find;
		}
		return this;
	}

	/*
	 * Change replace chars. 
	 * It has effect only on the input after it is called
	 */
	public BufferReaderFilter setReplace(char[] replace) {
		if(replace == null) {
			this.replace = new char[0]; 
		} else {
			this.replace = replace;
		}
		return this;
	}
	/*
	 * This is block read.
	 * It is possible that when next.read(char[]) blocks
	 * inBuf contains some 'find' that not processed.
	 * 
	 * Using nio does not fix the problem.
	 * Because, if 'next' does not close, 
	 * then inBuf always has some unprocessed chars.
	 */
	@Override
	public int read(char[] b) throws IOException {
		int loadSize = b.length+find.length;
		loadFromNext(loadSize);
		updateOutBuf();
		return copyOutBytes(b);
	}
	protected void loadFromNext(int loadSize) throws IOException {
		/*
		 * load from next when all conditions below meet
		 * 1. next is not close
		 * 2. queue does not contain "find" 
		 */
		char[] tmp = new char[loadSize];
		// if queue contains multiple "find"
		// the next.read() will skip until queue does not contain "find"
		if(!close && inBuf.getLimit() < loadSize) {
			int i = 0;
			while((i=next.read(tmp)) != -1) {
				inBuf.append(tmp,0,i);
				if(inBuf.getLimit() < loadSize) {
					tmp = new char[loadSize-inBuf.getLimit()];
				} else {
					// stop loading when queue has enough chars
					break;
				}
			}
			if(i == -1) {
				close = true;
			}
		}
	}
	
	protected void updateOutBuf() {
		if(inBuf.contains(find)) {
			CharArrayReplace charArrayReplace = new CharArrayReplace();
			// end index of updated chars in queue
			int i = charArrayReplace.replace(inBuf,0,find,replace);
			outBuf.append(inBuf.remove(i));
		} else
		if(close) {
			// flush queue to chararray
			outBuf.append(inBuf.remove(inBuf.getLimit()));
		} else {
			outBuf.append(inBuf.remove(inBuf.getLimit()-find.length));
		}
	}
	
	protected int copyOutBytes(char[] b) {
		// determine outputBytes
		if(outBuf.getLimit() > 0) {
			int i = Math.min(outBuf.getLimit(), b.length);
			System.arraycopy(outBuf.remove(i), 0, b, 0, i);
			return i;
		} else 
		if(close) {
			return -1;
		} else {
			// safety check point
			throw new BufferException("InputBufferReader at state that not close and chararray has no butes.");
		}
	}

	@Override
	public boolean ready() throws IOException {
		return outBuf.getLimit() > 0;
	}

	/*
	 * This is a gatway to InputBufferSrc.skip() method
	 * It is meaningful when called at the beginning of input stream read
	 * Don't use it between reading process
	 */
	@Override
	public long skip(long n) throws IOException {
		return next.skip(n);
	}

}
