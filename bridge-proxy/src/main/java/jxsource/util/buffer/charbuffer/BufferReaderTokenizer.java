package jxsource.util.buffer.charbuffer;

import java.io.IOException;

import jxsource.util.buffer.BufferException;

public class BufferReaderTokenizer extends BufferReaderFilter {
	private int loadSize = 1024*8;
	public BufferReaderTokenizer(BufferReader next, char[] find) {
		super(next, find, find);
	}
	public void setLoadSize(int loadSize) {
		this.loadSize = loadSize;
	}
 
	public boolean hasNext() {
		if(outBuf.getLimit() > 0) {
			return true;
		}
		try {
			if(!inBuf.contains(find)) {
				this.loadFromNext(loadSize);
			}
			int i = inBuf.indexOf(find);
			if(i != -1) {
				outBuf.append(inBuf.remove(i+find.length));
				return true;
			} else {
				if(this.close && inBuf.getLimit() > 0) {
					outBuf.append(inBuf.flush());
					return true;
				} else {
					return false;
				}
			}
		} catch (IOException e) {
			throw new BufferException(e);
		}
	}
	
	public char[] next() {
		return next(false);
	}
	public char[] next(boolean withToken) {
		if(outBuf.getLimit() == 0) {
			if(!hasNext()) {
				return null;
			}
		}
		if(withToken) {
			return outBuf.flush();
		} else {
			char[] val = null;
			if(outBuf.lastIndexOf(find) == -1) {
				val = outBuf.flush();
			} else {
				val = outBuf.remove(outBuf.getLimit()-find.length);
			}
			outBuf.reset();
			return val;
		}
		
	}
}
