package jxsource.util.buffer;

import jxsource.util.buffer.bytebuffer.ByteArray;
/*
 * Not used anymore because of very bad performance
 */
/*
 * T must be byte[] or char[]
 */
@Deprecated
public abstract class BufferArray<T> {
	protected int increasesize = 20;//1024 * 8 * 4;
	protected T array;
	// data length in array
	protected int limit = 0;
	
	protected abstract T increaseSize(int size);
	protected abstract T create(int size);
	protected int getLength(T t) {
		return BufferUtil.getLength(t);
	}

	/*
	 * use getLimit()
	 */
	@Deprecated
	public int length() {
		return limit;
	}
	public void reset() {
		limit = 0;
	}
	
	public int getIncreasesize() {
		return increasesize;
	}

	public void setIncreasesize(int increasesize) {
		this.increasesize = increasesize;
	}

	public int getLimit() {
		return limit;
	}

	protected T increaseSize() {
		return increaseSize(increasesize);
	}
	
	protected void _insert(int index, T data) {
		if(index > limit) {
			throw new RuntimeException("Insert index is out of ByteArray limit.");
		}
		if(limit+getLength(data) > getLength(array)) {
			array = increaseSize();
		}
		System.arraycopy(array, index, array, index+getLength(data), limit-index);
		System.arraycopy(data, 0, array, index, getLength(data));		
		limit += getLength(data);
	}
	protected void _replace(byte[] replace, int replaceOffset, int replaceLength, int offset, int length) {
		if(offset+length > limit) {
			throw new RuntimeException("Replace offset+length is out of ByteArray limit.");
		}
		if(limit+replaceLength > getLength(array)) {
			array = increaseSize();
		}
		System.arraycopy(array, offset+length, array, offset+replace.length, limit-(offset+length));
		System.arraycopy(replace, replaceOffset, array, offset, replaceLength);
		limit = limit - length + replace.length;
	}
	
	public void append(byte[] append) {
//		System.out.println(append.length+","+array.length);
		while(limit+append.length > getLength(array)) {
			increaseSize();
		}
		System.arraycopy(append, 0, array, limit, append.length);
		limit += append.length;
	}
	
	public void append(byte[] append, int offset, int length) {
//		System.out.println(length+","+array.length);
		if(length > (append.length - offset)) {
			throw new BufferException(getClass().getName()+
					": length > (append.length - offset). length="+length+
					",append.length="+append.length+",offset="+offset);
		}
		while(limit+length > getLength(array)) {
			increaseSize(limit+length-getLength(array));
		}
		System.arraycopy(append, offset, array, limit, length);
		limit += length;
	}

	public void append(byte append) {
		if(limit+1 > getLength(array)) {
			increaseSize();
		}
		BufferUtil.setCharOrByte(array, limit++, append);
//		array[limit++] = append;
	}
	public void append(ByteArray append) {
		append(append.getArray());
	}

}
