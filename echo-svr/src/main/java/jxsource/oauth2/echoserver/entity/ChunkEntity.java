package jxsource.oauth2.echoserver.entity;

public class ChunkEntity {
	public static final char CR = 13;
	public static final char LF = 10;
	public static final char[] CRLF = new char[] {CR,LF};


	public static ChunkEntity build() {
		return new ChunkEntity();
	}
	public String getChunkHead(long size) {
		return getChunkHeadwithExtension(size, "");
	}
	/*
	 * extension must start with ';'
	 */
	public String getChunkHeadwithExtension(long size, String extension) {
		String s = Long.toHexString(size)+extension;
		s += new String(CRLF);
		return s;
	}

	// create single chunk
	public String createChunk(char[] val) {
		String chunk = getChunkHead(val.length);
		chunk += new String(val);
		chunk += new String(CRLF);
		return chunk;
	}

	public String createChunk(String val) {
		String chunk = getChunkHead(val.length());
		chunk += new String(val);
		chunk += new String(CRLF);
		return chunk;
	}
	
	public StringBuilder createLastChunk() {
		StringBuilder lastChunk = new StringBuilder();
		lastChunk.append('0');
		lastChunk.append(CRLF);
		return lastChunk;
	}

	// create multi chunks with each having length equals "size"
	public String createChunk(int size, String val) {
		int len = Math.max(size, val.length());
		int pos = 0;
		int blockPos = 0;
		String entity = "";
		int blockSize = Math.min(size, val.length());
		int leftLen = val.length();
		char[] block = null;
		do {
			block = new char[blockSize];
			for(blockPos=0; blockPos<blockSize; blockPos++) {
				block[blockPos] = val.charAt(pos++);
			}
			entity += createChunk(block);
			leftLen = leftLen-size;
			blockSize = Math.min(size, leftLen);
		} while(pos < len);
		
//		
//		StringBuilder src = new StringBuilder();
//		src.append(val);
//		while(size < src.length()) {
//			byte[] srcChunk = src.remove((int)size);
//			StringBuilder chunk = createChunk(srcChunk);
//			entity.append(chunk);
//		}
//		StringBuilder chunk = createChunk(src.getArray());
//		entity.append(chunk);
//		entity.append(createLastChunk());
//		// tailer
//		entity.append("tailer:value".getBytes());
//		entity.append(CRLF);
//		// end chunk
//		entity.append(CRLF);
		return entity;
		
	}

}
