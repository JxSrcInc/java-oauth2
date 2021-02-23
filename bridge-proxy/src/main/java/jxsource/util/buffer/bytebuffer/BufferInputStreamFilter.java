package jxsource.util.buffer.bytebuffer;

import java.io.IOException;

import jxsource.util.buffer.BufferException;

/*
 * If coming input stream contains several find/byte[],
 * BufferInputStreamFilter replace all find/byte[] with replace/byte[].
 * When a program calls read(byte[]) method, it gets a stream with
 * replace/byte[]. 
 * 
 * In other words, it filters find/byte[] in a stream and replace it
 * with replace/byte[].
 * 
 */
public class BufferInputStreamFilter extends BufferInputStream{
	protected byte[] find;
	protected byte[] replace;
	// buffer for output bytes to caller
	protected ByteArray outBuf = new ByteArray();
	// buffer for input bytes from next
	protected ByteArray inBuf = new ByteArray();
	protected boolean close;
	
	public BufferInputStreamFilter(BufferInputStream next, byte[] find) {
		this(next, find, null);
	}

	public BufferInputStreamFilter(BufferInputStream next, byte[] find, byte[] replace) {
		this.setInputBuffer(next);
		setFind(find);
		setReplace(replace);
	}

	/*
	 * Change find bytes. 
	 * It has effect only on the input after it is called
	 * 
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
	public BufferInputStreamFilter setFind(byte[] find) {
		if(find == null || find.length == 0) {
			throw new BufferException("find parameter cannot be empty.");
		} else {
			this.find = find;
		}
		return this;
	}

	/*
	 * Change replace bytes. 
	 * It has effect only on the input after it is called
	 */
	public BufferInputStreamFilter setReplace(byte[] replace) {
		if(replace == null) {
			this.replace = new byte[0]; 
		} else {
			this.replace = replace;
		}
		return this;
	}
	/*
	 * This is block read.
	 * It is possible that when next.read(byte[]) blocks,
	 * inBuf contains some 'find' that not processed.
	 * 
	 * Using nio does not fix the problem.
	 * Because, if 'next' does not close, 
	 * then inBuf always has some unprocessed bytes.
	 */
	@Override
	public int read(byte[] b) throws IOException {
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
		byte[] tmp = new byte[loadSize];
		// if queue contains multiple "find"
		// the next.read() will skip until queue does not contain "find"
		if(!close && inBuf.getLimit() < loadSize) {
			int i = 0;
			while((i=next.read(tmp)) != -1) {
				inBuf.append(tmp,0,i);
				if(inBuf.getLimit() < loadSize) {
					tmp = new byte[loadSize-inBuf.getLimit()];
				} else {
					// stop loading when queue has enough bytes
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
			ByteArrayReplace byteArrayReplace = new ByteArrayReplace();
			// end index of updated bytes in queue
			int i = byteArrayReplace.replace(inBuf,0,find,replace);
			outBuf.append(inBuf.remove(i));
		} else
		if(close) {
			// flush queue to bytearray
			outBuf.append(inBuf.remove(inBuf.getLimit()));
		} else {
			outBuf.append(inBuf.remove(inBuf.getLimit()-find.length));
		}
	}
	
	protected int copyOutBytes(byte[] b) {
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
			throw new BufferException("InputBufferStream at state that not close and bytearray has no butes.");
		}
	}

	@Override
	public int available() throws IOException {
		return outBuf.getLimit();
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
