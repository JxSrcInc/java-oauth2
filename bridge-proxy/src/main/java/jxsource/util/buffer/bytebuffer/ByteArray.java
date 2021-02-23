package jxsource.util.buffer.bytebuffer;

import java.util.Arrays;

import jxsource.util.buffer.BufferException;

public class ByteArray {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getArray());
		result = prime * result + limit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArray other = (ByteArray) obj;
		if (!Arrays.equals(getArray(), other.getArray()))
			return false;
		if (limit != other.limit)
			return false;
		return true;
	}
	public static final byte CR = 13;
	public static final byte LF = 10;
	public static final byte[] CRLF = new byte[] {CR,LF};
	protected int increasesize = 1024 * 8 * 4;
	protected byte[] array = new byte[increasesize];
	// data length in array
	protected int limit = 0;
	
	public ByteArray() {}
	
	public ByteArray(int capacity) {
		array = new byte[capacity];
	}
	
	public byte[] getFullBuffer() {
		return array;
	}
	public int getArrayLength() {
		return array.length;
	}
	public ByteArray insert(int index, byte[] data) {
		if(index > limit) {
			throw new RuntimeException("Insert index is out of ByteArray limit.");
		}
/*		byte[] tmp = new byte[array.length+data.length];
		System.arraycopy(array, 0, tmp, 0, index);
		System.arraycopy(data, 0, tmp, index, data.length);
		System.arraycopy(array, index, tmp, index+data.length, array.length-index);
		array = tmp;
*/
		if(limit+data.length > array.length) {
			increaseSize();
		}
		System.arraycopy(array, index, array, index+data.length, limit-index);
		System.arraycopy(data, 0, array, index, data.length);		
		limit += data.length;
		return this;
	}

	public ByteArray replace(byte[] replace, int offset, int length) {
		return replace(replace, 0, replace.length, offset, length);
	}
	public ByteArray replace(byte[] replace, int replaceOffset, int replaceLength, int offset, int length) {
/*		byte[] tmp = new byte[array.length-length+replace.length];
		System.arraycopy(array, 0, tmp, 0, offset);
		System.arraycopy(replace, 0, tmp, offset, replace.length);
		System.arraycopy(array, offset+length, tmp, offset+replace.length, array.length-(offset+length));
		array = tmp;
*/
		if(offset+length > limit) {
			throw new RuntimeException("Replace offset+length is out of ByteArray limit.");
		}
		if(limit+replaceLength > array.length) {
			increaseSize();
		}
		System.arraycopy(array, offset+length, array, offset+replace.length, limit-(offset+length));
		System.arraycopy(replace, replaceOffset, array, offset, replaceLength);
		limit = limit - length + replace.length;
		return this;
	}
	public ByteArray replaceFirst(byte[] find, byte[] replace) {
		return replace(find, replace, 0);
	}
	public ByteArray replace(byte[] find, byte[] replace, int start) {
		int index = indexOf(find, start);
		if(index == -1) {
			return null;
		}
		return replace(replace, index, find.length);
/*		
		byte[] tmp = new byte[array.length-find.length+replace.length];
		System.arraycopy(array, 0, tmp, 0, index);
		System.arraycopy(replace, 0, tmp, index, replace.length);
		System.arraycopy(array, index+find.length, tmp, index+replace.length, array.length-(index+find.length));
		array = tmp;
		limit = limit - find.length+replace.length;

//		this.remove(index, find.length);
//		insert(index, replace);
		return this;
*/	}

	public int replaceAll(byte[] find, byte[] replace) {
		int count = 0;
		while(replaceFirst(find, replace) != null) {
			count ++;
		}
		return count;
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

	public void setLimit(int newLimit) {
		if(newLimit > array.length) {
			increaseSize(newLimit-array.length);
		}
		this.limit = newLimit;
	}
	public int getLimit() {
		return limit;
	}
	public void append(byte[] append) {
//		System.out.println(append.length+","+array.length);
		while(limit+append.length > array.length) {
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
		while(limit+length > array.length) {
			increaseSize(limit+length-array.length);
		}
		System.arraycopy(append, offset, array, limit, length);
		limit += length;
	}

	public void append(byte append) {
		if(limit+1 > array.length) {
			increaseSize();
		}
		array[limit++] = append;
	}
	public void append(ByteArray append) {
		append(append.getArray());
	}
	// compare bytes before limit
	public boolean equals(ByteArray other) {
		return equal(getArray(), other.getArray());
	}
	// compare bytes before limit
	public boolean equals(byte[] other) {
		return equal(getArray(), other);
	}
	public static boolean equal(byte[] b1, byte[] b2) {
		if(b1.length != b2.length) {
			return false;
		} else {
			for(int i=0; i<b1.length; i++) {
				if(b1[i] != b2[i]){
					return false;
				}
			}
			return true;
		}
	}

	public byte[] getArray() {
		byte[] tmp = new byte[limit];
		System.arraycopy(array, 0, tmp, 0, limit);
		return tmp;
	}
	public byte get(int index) {
		if(index >= limit) {
			throw new BufferException(getClass().getName()+".get(): index >= arraylimit");
		}
		return array[index];
	}
	/*
	 * use getLimit()
	 */
	@Deprecated
	public int length() {
		return limit;
	}
	protected byte[] increaseSize() {
		return increaseSize(increasesize);
	}
	public byte[] increaseSize(int size) {
		byte[] tmp = new byte[array.length+size];
		System.arraycopy(array, 0, tmp, 0, array.length);
		array = tmp;
		return array;
	}
	public int indexOf(byte b) {
		for(int i=0; i<limit; i++) {
			if(array[i] == b) {
				return i;
			}
		}
		return -1;
	}

	// return the first index of search byte in array
	// starting from startIndex (include)
	public int indexOf(byte b, int startIndex) {
		if(startIndex >= limit) {
			throw new BufferException("Index out of bounds.");
		}
		for(int i=startIndex; i<limit; i++) {
			if(array[i] == b) {
				return i;
			}
		}
		return -1;
	}
	/*
	 *  return the first index of search byte[] in array
	 *  ending at endIndex (exclude)
	 */ 
	public int indexOf(int endIndex, byte b) {
		for(int i=0; i<Math.min(limit, endIndex); i++) {
			if(array[i] == b) {
				return i;
			}
		}
		return -1;
	}
	// return the first index of search byte[] in array
	// starting from startIndex (include)
	public int indexOf(byte[] b, int startIndex) {
		if(startIndex >= limit-b.length+1) {
			throw new BufferException("Index out of bounds.");
		}
		for(int i=startIndex; i<limit-b.length+1; i++) {
			int findCount = 0;
			for(int k=0; k<b.length; k++) {
				if(array[i+k] == b[k]) {
					findCount++;
				} else {
					// for better performance add: if not match, stop.
					break;
				}
			}
			if(findCount == b.length) {
				return i;
			}
		}
		return -1;
	}
	/*
	 *  return the first index of search byte[] in array
	 *  ending at endIndex (exclude)
	 */ 
	public int indexOf(int endIndex, byte[] b) {
		for(int i=0; i<Math.min(limit, endIndex); i++) {
			int findCount = 0;
			for(int k=0; k<b.length; k++) {
				if(array[i+k] == b[k]) {
					findCount++;
				} else {
					// for better performance add: if not match, stop.
					break;
				}
			}
			if(findCount == b.length) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(byte[] find) {
		for(int i=limit-find.length; i>=0; i--) {
			int findCount = 0;
			for(int k=0; k<find.length; k++) {
				if(array[i+k] == find[k]) {
					findCount++;
				} else {
					// for better performance add: if not match, stop.
					break;
				}
			}
			if(findCount == find.length) {
				return i;
			}
		}
		return -1;
	}

	public int indexOf(byte[] b) {
		for(int i=0; i<limit-b.length+1; i++) {
			int findCount = 0;
			for(int k=0; k<b.length; k++) {
				if(array[i+k] == b[k]) {
					findCount++;
				}
			}
			if(findCount == b.length) {
				return i;
			}
		}
		return -1;
	}

	/*
	 *  remove up to 'limit'
	 *  smart and automatically determine return bytes
	 */
	public byte[] remove(int length) {
		if(length < limit) {
			return remove(0, length);
		} else {
			return remove(0, limit);
		}
	}
	
	public byte[] flush() {
		byte[] tmp = new byte[limit];
		System.arraycopy(array, 0, tmp, 0, limit);
		limit = 0;
		return tmp;
	}
	public byte[] remove(int offset, int length) {
		if(limit < offset+length) {
			throw new BufferException("Invalide offset and length for remove: "
					+ "offset="+offset
					+ "length="+length
					+ "array.length="+array.length);
		}
		byte[] removed = new byte[length];
		System.arraycopy(array, offset, removed, 0, length);
//		byte[] tmp = new byte[array.length-length];
//		System.arraycopy(array, 0, tmp, 0, offset);
//		System.arraycopy(array, offset+length, tmp, offset, array.length-(offset+length));
//		array = tmp;
		System.arraycopy(array, offset+length, array, offset, array.length-(offset+length));
		limit = limit - length;
		return removed;
	}
	public void delete(int length) {
		delete(0, length);
	}
	public void delete(int offset, int length) {
		if(limit < offset+length) {
			throw new BufferException("Invalide offset and length for remove: "
					+ "offset="+offset
					+ "length="+length
					+ "array.length="+array.length);
		}
		System.arraycopy(array, offset+length, array, offset, array.length-(offset+length));
		limit = limit - length;
	}
	public byte[] subArray(int offset){
		if(offset >= limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset >= arraylimit");
		}
		byte[] tmp = new byte[limit-offset];
		System.arraycopy(array, offset, tmp, 0, limit-offset);
		return tmp;
	}
	public byte[] subArray(int offset, int length){
		if(offset+length > limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset+length > arraylimit");
		}
		byte[] tmp = new byte[length];
		System.arraycopy(array, offset, tmp, 0, length);
		return tmp;
	}
	public void subArray(byte[] target, int offset, int length){
		subArray(target, 0, offset, length);
	}
	// get subArray(offset, length) to target at start targetPos
	public void subArray(byte[] target, int targetPos, int offset, int length){
		if(offset+length > limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset+length > arraylimit");
		} 
		if(target.length-targetPos > length) {
			throw new BufferException(getClass().getName()+".subArray(): target out of rang.");
		}
		System.arraycopy(array, offset, target, targetPos, length);
	}
	public String toString() {
		return new String(array, 0, limit);
	}

	/*
	 * fill in parameter byte[] with available bytes 
	 */
	public int fillin(byte[] dst) {
		int len = Math.min(dst.length, limit);
		System.arraycopy(array, 0, dst, 0, len);
		return len;
	}
	public int fillin(byte[] dst, int offset, int length) {
		int len = Math.min(length, limit);
		System.arraycopy(array, 0, dst, offset, len);
		return len;
	}
	public boolean contains(byte[] find) {
		return indexOf(find) != -1;
	}
}
