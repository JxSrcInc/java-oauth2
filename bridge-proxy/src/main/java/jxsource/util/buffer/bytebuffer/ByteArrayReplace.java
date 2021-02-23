package jxsource.util.buffer.bytebuffer;

import jxsource.util.buffer.BufferException;

/*
 * Replace find/byte[] with replace/byte[] in src/(ByteArray or byte[])
 */
public class ByteArrayReplace {
	public boolean complete;
	private byte[] target = new byte[0];
	private int targetPos;
	private int srcPos;
	private byte[] find;
	private byte[] replace;
	private byte[] src;
	
	/*
	 * method for BufferInputStream
	 */
	public int replace(ByteArray src, int start, byte[] find, byte[] replace) {
		byte[] srcArray = src.getArray();
		byte[] data = replace(srcArray, start, find, replace);
		src.replace(data, 0, srcArray.length);
		int replaceBytes = src.lastIndexOf(replace)+replace.length;
		return replaceBytes;
	}
	/*
	 * replace parameter 'find' with parameter 'replace' in src start from parameter 'start'
	 * but not copy the reset of src to target
	 */
	public byte[] replace(byte[] src, int start, byte[] find, byte[] replace) {
		this.find = find;
		this.replace = replace;
		this.src = src;
		target = new byte[src.length+replace.length];
		System.arraycopy(target, 0, src, 0, start);
		srcPos = start;
		targetPos = start;
		while(!complete) {
			_replace(src, find, replace);
			// increase target size
			target = increaseTargetSize();
		}
		// -> copy bytes after last find in src to target
		int lastIndex = indexOf(src, srcPos-find.length, find);
		if(targetPos+src.length-srcPos > target.length) {
			target = increaseTargetSize();
		}
		System.arraycopy(src, srcPos, target, targetPos, src.length-srcPos);
		targetPos += src.length - srcPos;
		// <-
		byte[] r = new byte[targetPos];
		System.arraycopy(target, 0, r, 0, targetPos);
		return r;
	}
	private byte[] increaseTargetSize() {
		byte[] tmp = new byte[target.length+src.length-srcPos+replace.length];
		System.arraycopy(target, 0, tmp, 0, target.length);
		return tmp;
		
	}
	/*
	 * replace first 'find' with 'replace' in 'src' starting from srcPos
	 */
	private void _replace(byte[] src, byte[] find, byte[] replace) {
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

	// return the first index of byte[] find if b contains find.
	private int indexOf(byte[] b, int start, byte[] find) {
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
