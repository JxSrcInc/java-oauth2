package jxsource.util.buffer;


public class ByteUtil
{
	public static final byte[] CRLF = new byte[] {13,10};
	public static final byte[] CRLFCRLF = new byte[] {13,10,13,10};
	public static final byte SP = 32;
	public static final byte CR = 13;
	public static final byte LF = 10;
	public static final byte HT = 9;
	public static final byte COLON = 58; // :
	public static final byte SEMICOLON = 59; // ;
	public static final byte AND = 38; // &
	public static final byte UNDERLINE = 95; // _
	public static final byte SINGLE_QUOT = 39; // '
	public static final byte DOUBLE_QUOT = 34; // "
	public static final byte FORWARD_SLASH = 47; // /
	public static final byte BACK_SLASH = 92; // \
	public static final byte GREAT_THAN = 62; // >
	public static final byte LESS_THAN = 60; // <
	public static final byte EQUAL = 61; // =

    public ByteUtil()
    {
    }

    public static boolean isWhiteSpace(byte byte0)
    {
        return byte0 == 9 || byte0 == 10 || byte0 == 13 || byte0 == 32;
    }

    public static int firstNonWhiteSpaceByte(byte abyte0[])
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            if(!isWhiteSpace(abyte0[i]))
            {
                return i;
            }
        }

        return -1;
    }

    public static boolean isLastBytesEquals(byte[] data, byte[] toFind)
    {
    	if(data.length >= toFind.length)
    	{
    		return (getIndexOfArray(data,toFind,data.length-toFind.length,true) != -1);
    	}
    	return false;
    }
    public static int lastNonWhiteSpaceByte(byte abyte0[])
    {
        for(int i = abyte0.length - 1; i >= 0; i--)
        {
            if(!isWhiteSpace(abyte0[i]))
            {
                return i;
            }
        }

        return -1;
    }

    public static byte[] removeWhiteSpace(byte abyte0[])
    {
        byte abyte1[] = new byte[abyte0.length];
        int i = 0;
        for(int j = 0; j < abyte1.length; j++)
        {
            byte byte0 = abyte0[j];
            if(byte0 != 32 && byte0 != 10 && byte0 != 9 && byte0 != 13)
            {
                abyte1[i++] = byte0;
            }
        }

        byte abyte2[] = new byte[i];
        System.arraycopy(abyte1, 0, abyte2, 0, abyte2.length);
        return abyte2;
    }

    public static byte[] trim(byte abyte0[])
    {
        int i = -1;
        for(int j = 0; j < abyte0.length; j++)
        {
            byte byte0 = abyte0[j];
            if(byte0 == 32 || byte0 == 10 || byte0 == 9 || byte0 == 13)
            {
                continue;
            }
            i = j;
            break;
        }

        if(i == -1)
        {
            return new byte[0];
        }
        int k = -1;
        for(int l = abyte0.length - 1; l > -1; l--)
        {
            byte byte1 = abyte0[l];
            if(byte1 == 32 || byte1 == 10 || byte1 == 9 || byte1 == 13)
            {
                continue;
            }
            k = l + 1;
            break;
        }

        byte abyte1[] = new byte[k - i];
        System.arraycopy(abyte0, i, abyte1, 0, k - i);
        return abyte1;
    }

    public static boolean containWhiteSpace(byte abyte0[], int offset, int length)
    {
        if(abyte0 == null)
        {
            return false;
        }
        for(int i = offset; i < offset+length; i++)
        {
            byte byte0 = abyte0[i];
            if(byte0 != 32 && byte0 != 10 && byte0 != 9 && byte0 != 13)
            {
                return false;
            }
        }

        return true;
    }

    public static boolean containWhiteSpace(byte abyte0[])
    {
        if(abyte0 == null)
        {
            return false;
        }
        for(int i = 0; i < abyte0.length; i++)
        {
            byte byte0 = abyte0[i];
            if(byte0 != 32 && byte0 != 10 && byte0 != 9 && byte0 != 13)
            {
                return false;
            }
        }

        return true;
    }

    public static byte[] append(byte[] src, byte[] append, int offset, int len)
    {	byte[] target = new byte[src.length+len];
    	System.arraycopy(src,0,target,0,src.length);
    	System.arraycopy(append,offset,target,src.length,len);
    	return target;
	}

    public static byte[] append(byte[] src, byte[] append)
    {
		return append(src,append,0,append.length);
	}

    public static byte[] append(byte[] src, byte append)
    {
		return append(src,new byte[] {append});
	}

	public static int getIndexOfCRLF(byte[] data, int start)
	{	return getIndexOfArray(data, CRLF, start);
	}

	public static int getIndexOfArray(byte[] data, byte[] toFind)
	{
		return getIndexOfArray(data,toFind,0,true);
	}

	public static int getIndexOfArray(byte[] data, byte[] toFind, int start)
	{
		return getIndexOfArray(data,toFind,start,true);
	}

	public static int getIndexOfArray(byte[] data, byte[] toFind, boolean caseSensitive)
	{
		return getIndexOfArray(data,toFind,0,caseSensitive);
	}

	public static int getIndexOfArray(byte[] data, byte[] toFind, int start, boolean caseSensitive)
	{	if(data.length - start >= toFind.length)
		{	for(int i=start; i < (data.length - toFind.length + 1); i++)
			{	boolean find = true;
				for(int k=0; k<toFind.length; k++)
				{
					if(caseSensitive)
					{	if(data[i+k] != toFind[k])
						{	find = false;
							break;
						}
					} else
					{	if(toLowerCase(data[i+k]) != toLowerCase(toFind[k]))
						{	find = false;
							break;
						}
					}
				}
				if(find)
					return i;
			}
		}
		return -1;
	}

	public static int getIndexOfByte(byte[] data, byte toFind)
	{	return getIndexOfByte(data, toFind, 0);
	}

	public static int getIndexOfByte(byte[] data, byte toFind, int start)
	{
		return getIndexOfByte(data, toFind, start, true);
	}

	public static int getIndexOfByte(byte[] data, byte toFind, boolean caseSensitive)
	{
		return getIndexOfByte(data, toFind, 0, caseSensitive);
	}

	public static int getIndexOfByte(byte[] data, byte toFind, int start, boolean caseSensitive)
	{	for(int i=start; i < data.length; i++)
		{
			if(caseSensitive)
				if(data[i] == toFind)
					return i;
			else
				if(toLowerCase(data[i]) == toLowerCase(toFind))
					return i;
		}
		return -1;
	}

	public static int getIndexOfSP(byte[] data, int start)
	{	return getIndexOfByte(data, SP, start);
	}

	public static byte[] replace(byte[] src, byte[] search, byte[] replace, int offset)
	{	
		byte[] target = src;
		int pos = 0;
		if((pos = getIndexOfArray(target, search, offset)) != -1)
		{
			byte[] temp = new byte[target.length-search.length+replace.length];
			if(pos != 0)
			{
				System.arraycopy(target,0,temp,0,pos);
			}
			System.arraycopy(replace,0,temp,pos,replace.length);
			System.arraycopy(target,pos+search.length,
				temp,pos+replace.length,target.length-pos-search.length);
			target = temp;
			offset = pos+replace.length;
		}
		return target;
	}

	public static byte[] replace(byte[] src, byte[] search, byte[] replace)
	{	byte[] target = src;
		int offset = 0; // byte index in src starting to search
		int pos = 0; // first index of bytes match search starting from offset
		
		while((pos = getIndexOfArray(target, search, offset)) != -1)
		{
			byte[] temp = new byte[target.length-search.length+replace.length];
			if(pos != 0)
			{
				System.arraycopy(target,0,temp,0,pos);
			}
			System.arraycopy(replace,0,temp,pos,replace.length);
			System.arraycopy(target,pos+search.length,
				temp,pos+replace.length,target.length-pos-search.length);
			target = temp;
			offset = pos+replace.length;
		}
		return target;
	}
	
	public static byte[] subarray(byte[] src, int offset)
	{
		if(src.length < offset)
			return null;
		int length = src.length - offset;
		return subarray(src, offset, length);
	}

	public static byte[] subarray(byte[] src, int offset, int length)
	{
		if(src.length < offset+length)
			return null;
		byte[] b = new byte[length];
		System.arraycopy(src, offset, b, 0, length);
		return b;
	}

	public static byte[] addQuot(byte[] ret)
    {
		if((ret[0] == 34 && ret[ret.length-1] == 34) ||
			(ret[0] == 39 && ret[ret.length-1] == 39))
		{	return ret;
		}	else
		{	byte[] tmp = new byte[ret.length+2];
			tmp[0] = 34;
			System.arraycopy(ret,0,tmp,1,ret.length);
			tmp[tmp.length-1] = 34;
			return tmp;
		}
	}

	public static byte[] trimQuot(byte[] ret)
    {
		if((ret[0] == 34 && ret[ret.length-1] == 34) ||
			(ret[0] == 39 && ret[ret.length-1] == 39))
		{	return subarray(ret,1,ret.length-2);
		}	else
		{	return ret;
		}
	}


	public static byte toLowerCase(byte src)
	{
			if(64 < src && src <91)
				return (byte) (src + (byte)32);
			else
				return src;
	}

	public static byte[] toLowerCase(byte[] src)
	{
		byte[] ret = new byte[src.length];
		for(int i=0; i<src.length; i++)
		{
			ret[i] = toLowerCase(src[i]);
		}
		return ret;
	}

	public static byte[] toLowerCase(byte[] src, int offset, int length)
	{
		byte[] ret = new byte[src.length];
		System.arraycopy(src,0,ret,0,src.length);
		for(int i=offset; i<offset+length; i++)
		{	if(src[i] > 64 && src[i] < 91)
				ret[i] = (byte) (src[i] + 32);
			else
				ret[i] = src[i];
		}
		return ret;
	}

	public static byte toUpperCase(byte src)
	{
			if(96 < src && src <123)
				return (byte)(src - (byte)32);
			else
				return src;
	}

	public static byte[] toUpperCase(byte[] src)
	{
		byte[] ret = new byte[src.length];
		for(int i=0; i<src.length; i++)
		{
			ret[i] = toUpperCase(src[i]);
		}
		return ret;
	}

	public static byte[] toUpperCase(byte[] src, int offset, int length)
	{
		byte[] ret = new byte[src.length];
		System.arraycopy(src,0,ret,0,src.length);
		for(int i=offset; i<offset+length; i++)
		{	if(src[i] > 96 && src[i] < 123)
				ret[i] = (byte) (src[i] - 32);
			else
				ret[i] = src[i];
		}
		return ret;
	}

	public static boolean equal(byte[] a, byte[] b)
	{
		if(a.length == b.length)
		{
			for(int i=0; i<a.length; i++)
			{
				if(a[i] != b[i])
					return false;
			}
			return true;
		}
		return false;
	}
	
	// Change As Much As Possible
	public static byte[][] changeAMAP(byte[] src, byte[] toFind, byte[] toReplace)
	{
		byte[] body = new byte[0];
		int pos = 0;
		while((pos = getIndexOfArray(src, toFind)) != -1)
		{
			body = append(body, src, 0, pos);
			body = append(body, toReplace, 0, toReplace.length);
			src = subarray(src, pos+toFind.length, src.length-pos-toFind.length);
		}
		if(src.length >= toFind.length)
		{
			int len = src.length-toFind.length+1;
			body = append(body, src, 0, len);
			src = subarray(src, len, src.length-len);
		}
		return new byte[][] {body, src};
	}

}
