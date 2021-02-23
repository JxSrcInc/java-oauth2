package jxsource.util.buffer.charbuffer;

import java.util.Arrays;

import jxsource.util.buffer.BufferException;
import jxsource.util.buffer.bytebuffer.ByteArray;

public class CharArray {
	private int increasesize = 1024 * 8 * 4;
	private char[] array = new char[increasesize];
	// data length in array
	private int limit = 0;
	
	public CharArray() {}
	
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
		CharArray other = (CharArray) obj;
		if (!Arrays.equals(getArray(), other.getArray()))
			return false;
		if (limit != other.limit)
			return false;
		return true;
	}

	public CharArray(int capacity) {
		array = new char[capacity];
	}
	
	public CharArray insert(int index, char[] data) {
		if(index > limit) {
			throw new RuntimeException("Insert index is out of CharArray limit.");
		}
/*		char[] tmp = new char[array.length+data.length];
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
	public CharArray replace(char[] replace, int offset, int length) {
		return replace(replace, 0, replace.length, offset, length);
	}
	public CharArray replace(char[] replace, int replaceOffset, int replaceLength, int offset, int length) {
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
/*
	public CharArray replace(char[] replace, int offset, int length) {
		char[] tmp = new char[array.length-length+replace.length];
		System.arraycopy(array, 0, tmp, 0, offset);
		System.arraycopy(replace, 0, tmp, offset, replace.length);
		System.arraycopy(array, offset+length, tmp, offset+replace.length, array.length-(offset+length));
		limit = limit - length + replace.length;
		array = tmp;
		return this;
	}
*/	
	public CharArray replaceFirst(char[] find, char[] replace) {
		return replace(find, replace, 0);
	}

	/*
	 * replace find in array starts from startIndex
	 */
	public CharArray replace(char[] find, char[] replace, int startIndex) {
		int index = indexOf(find, startIndex);
		if(index == -1) {
			return null;
		}
		return replace(replace, index, find.length);
/*		char[] tmp = new char[array.length-find.length+replace.length];
		System.arraycopy(array, 0, tmp, 0, index);
		System.arraycopy(replace, 0, tmp, index, replace.length);
		System.arraycopy(array, index+find.length, tmp, index+replace.length, array.length-(index+find.length));
		array = tmp;
		limit = limit - find.length+replace.length;

//		this.remove(index, find.length);
//		insert(index, replace);
		return this;
*/	}

	public int replaceAll(char[] find, char[] replace) {
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

	public int getLimit() {
		return limit;
	}
	public void append(char[] append) {
//		System.out.println(append.length+","+array.length);
		while(limit+append.length > array.length) {
			increaseSize();
		}
		System.arraycopy(append, 0, array, limit, append.length);
		limit += append.length;
	}
	
	public void append(char[] append, int offset, int length) {
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

	public void append(char append) {
		if(limit+1 > array.length) {
			increaseSize();
		}
		array[limit++] = append;
	}
	public void append(CharArray append) {
		append(append.getArray());
	}
	public void append(String append) {
		append(append.toCharArray());
	}
	// compare chars before limit
	public boolean equals(CharArray other) {
		return equal(getArray(), other.getArray());
	}
	// compare chars before limit
	public boolean equals(char[] other) {
		return equal(getArray(), other);
	}
	public boolean equals(String other) {
		return equal(getArray(), other.toCharArray());
	}
	public static boolean equal(char[] b1, char[] b2) {
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

	public char[] getArray() {
		char[] tmp = new char[limit];
		System.arraycopy(array, 0, tmp, 0, limit);
		return tmp;
	}
	public char get(int index) {
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
	private char[] increaseSize() {
		return increaseSize(increasesize);
	}
	private char[] increaseSize(int size) {
		char[] tmp = new char[array.length+size];
		System.arraycopy(array, 0, tmp, 0, array.length);
		array = tmp;
		return array;
	}
	public int indexOf(char b) {
		for(int i=0; i<limit; i++) {
			if(array[i] == b) {
				return i;
			}
		}
		return -1;
	}
	// return the first index of search char[] in array.
	public int indexOf(char[] b, int start) {
		if(start >= limit-b.length+1) {
			throw new BufferException("Index out of bounds.");
		}
		for(int i=start; i<limit-b.length+1; i++) {
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
	public int lastIndexOf(char[] find) {
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

	public int indexOf(char[] b) {
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
	 *  smart and automatically determine return chars
	 */
	public char[] remove(int length) {
		if(length < limit) {
			return remove(0, length);
		} else {
			return remove(0, limit);
		}
	}
	
	public char[] flush() {
		char[] tmp = new char[limit];
		System.arraycopy(array, 0, tmp, 0, limit);
		limit = 0;
		return tmp;
	}
	public char[] remove(int offset, int length) {
		if(limit < offset+length) {
			throw new BufferException("Invalide offset and length for remove: "
					+ "offset="+offset
					+ "length="+length
					+ "array.length="+array.length);
		}
		char[] removed = new char[length];
		System.arraycopy(array, offset, removed, 0, length);
//		char[] tmp = new char[array.length-length];
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

	public char[] subArray(int offset){
		if(offset >= limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset >= arraylimit");
		}
		char[] tmp = new char[limit-offset];
		System.arraycopy(array, offset, tmp, 0, limit-offset);
		return tmp;
	}
	public char[] subArray(int offset, int length){
		if(offset+length > limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset+length > arraylimit");
		}
		char[] tmp = new char[length];
		System.arraycopy(array, offset, tmp, 0, length);
		return tmp;
	}
	public void subArray(char[] target, int offset, int length){
		subArray(target, 0, offset, length);
	}
	// get subArray(offset, length) to target at start targetPos
	public void subArray(char[] target, int targetPos, int offset, int length){
		if(offset+length > limit) {
			throw new BufferException(getClass().getName()+".subArray(): offset+length > arraylimit");
		} 
		if(target.length-targetPos > length) {
			throw new BufferException(getClass().getName()+".subArray(): target out of rang.");
		}
		System.arraycopy(array, offset, target, targetPos, length);
	}
	public String toString() {
		return new String(getArray());
	}

	/*
	 * fill in parameter char[] with available chars 
	 */
	public int fillin(char[] dst) {
		int len = Math.min(dst.length, limit);
		System.arraycopy(array, 0, dst, 0, len);
		return len;
	}
	public int fillin(char[] dst, int offset, int length) {
		int len = Math.min(length, limit);
		System.arraycopy(array, 0, dst, offset, len);
		return len;
	}
	public boolean contains(char[] find) {
		return indexOf(find) != -1;
	}
}
