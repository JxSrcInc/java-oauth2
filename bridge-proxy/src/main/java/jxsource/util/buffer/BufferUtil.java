package jxsource.util.buffer;

import java.nio.charset.Charset;

import jxsource.util.buffer.bytebuffer.ByteArray;
import jxsource.util.buffer.charbuffer.CharArray;

/*
 * Convert between char[] and byte[], and between ByteArray and CharArray
 */
public class BufferUtil {
	public static byte[] toBytes(char[] charArray) {
		return new String(charArray).getBytes();
	}

	public static byte[] toBytes(char[] charArray, Charset charset) {
		return new String(charArray).getBytes(charset);
	}

	public static char[] toChars(byte[] byteArray) {
		return new String(byteArray).toCharArray();
	}

	public static char[] toChars(byte[] byteArray, Charset charset) {
		return new String(byteArray, charset).toCharArray();
	}

	public static ByteArray toByteArray(char[] charArray) {
		ByteArray ba = new ByteArray();
		ba.append(new String(charArray).getBytes());
		return ba;
	}

	public static ByteArray toByteArray(char[] charArray, Charset charset) {
		ByteArray ba = new ByteArray();
		ba.append(new String(charArray).getBytes(charset));
		return ba;
	}

	public static ByteArray toByteArray(CharArray charArray) {
		return toByteArray(charArray.getArray());
	}

	public static ByteArray toByteArray(CharArray charArray, Charset charset) {
		return toByteArray(charArray.getArray(), charset);
	}

	public static CharArray toCharArray(byte[] byteArray) {
		CharArray ca = new CharArray();
		ca.append(new String(byteArray));
		return ca;
	}

	public static CharArray toCharArray(byte[] byteArray, Charset charset) {
		CharArray ca = new CharArray();
		ca.append(new String(byteArray, charset));
		return ca;
	}

	public static CharArray toCharArray(ByteArray byteArray) {
		return toCharArray(byteArray.getArray());
	}

	public static CharArray toCharArray(ByteArray byteArray, Charset charset) {
		return toCharArray(byteArray.getArray(), charset);
	}

	@Deprecated
	public static int getLength(Object array) {
		if (array instanceof char[]) {
			return ((char[]) array).length;
		} else if (array instanceof byte[]) {
			return ((byte[]) array).length;
		} else {
			throw new BufferException("Not char[] and byte[]: " + array);
		}
	}

	@Deprecated
	public static void setCharOrByte(Object array, int index, Object ele) {
		if (array instanceof char[]) {
			((char[]) array)[index] = ((Character)ele).charValue();
		} else if (array instanceof byte[]) {
			((byte[]) array)[index] = ((Byte)ele).byteValue();
		} else {
			throw new BufferException("Not char[] and byte[]: " + array);
		}
		
	}
}
