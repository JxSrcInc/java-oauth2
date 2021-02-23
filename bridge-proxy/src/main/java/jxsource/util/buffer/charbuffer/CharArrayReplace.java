package jxsource.util.buffer.charbuffer;

import jxsource.util.buffer.BufferException;


public class CharArrayReplace {
	public boolean complete;
	private char[] target = new char[0];
	private int targetPos;
	private int srcPos;
	private char[] find;
	private char[] replace;
	private char[] src;
	
	/*
	 * method for BufferInputStream
	 */
	public int replace(CharArray src, int start, char[] find, char[] replace) {
		char[] srcArray = src.getArray();
		char[] data = replace(srcArray, start, find, replace);
		src.replace(data, 0, srcArray.length);
		int replaceChars = src.lastIndexOf(replace)+replace.length;
		return replaceChars;
	}
	/*
	 * replace parameter 'find' with parameter 'replace' in src start from parameter 'start'
	 * but not copy the reset of src to target
	 */
	public char[] replace(char[] src, int start, char[] find, char[] replace) {
		this.find = find;
		this.replace = replace;
		this.src = src;
		target = new char[src.length+replace.length];
		System.arraycopy(target, 0, src, 0, start);
		srcPos = start;
		targetPos = start;
		while(!complete) {
			_replace(src, find, replace);
			// increase target size
			target = increaseTargetSize();
		}
		// -> copy chars after last find in src to target
		int lastIndex = indexOf(src, srcPos-find.length, find);
		if(targetPos+src.length-srcPos > target.length) {
			target = increaseTargetSize();
		}
		System.arraycopy(src, srcPos, target, targetPos, src.length-srcPos);
		targetPos += src.length - srcPos;
		// <-
		char[] r = new char[targetPos];
		System.arraycopy(target, 0, r, 0, targetPos);
		return r;
	}
	private char[] increaseTargetSize() {
		char[] tmp = new char[target.length+src.length-srcPos+replace.length];
		System.arraycopy(target, 0, tmp, 0, target.length);
		return tmp;
		
	}
	/*
	 * replace first 'find' with 'replace' in 'src' starting from srcPos
	 */
	private void _replace(char[] src, char[] find, char[] replace) {
		int i = 0;
		while((i=indexOf(src, srcPos, find)) != -1) {
			if(i+replace.length > target.length) {
				target = increaseTargetSize();
			}
			// copy before find from src to target;
			System.arraycopy(src, srcPos, target, targetPos, i-srcPos);
			targetPos += i-srcPos;
			// add replace to target
			System.arraycopy(replace, 0, target, targetPos, replace.length);
			srcPos = i + find.length;
			targetPos += replace.length;
			if(srcPos >= src.length-find.length) {
				i = -1;
				break;
			}
		}
		complete = i == -1;
	}

	// return the first index of char[] find if b contains find.
	private int indexOf(char[] b, int start, char[] find) {
		/*
		 *  change b.length to b.length-find.length
		 *  because find must be in b.
		 */
		if(start >= b.length-find.length) {
			throw new BufferException("Index out of bounds.");
		}
		/*
		 *  change b.length to b.length-find.length
		 *  because find must be in b.
		 */
//		System.out.println(start+","+b.length+","+find.length);
		for(int i=start; i<b.length-find.length; i++) {
			int findCount = 0;
			for(int k=0; k<find.length; k++) {
				if(b[i+k] == find[k]) {
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
	

}
