package jxsource.oauth2.echoserver.entity;

public class ChunkEntity {
	public static final char CR = 13;
	public static final char LF = 10;
	public static final char[] CRLF = new char[] {CR,LF};

	public StringBuilder getChunkHead(long size) {
		return getChunkHeadwithExtension(size, "");
	}
	/*
	 * extension must start with ';'
	 */
	public StringBuilder getChunkHeadwithExtension(long size, String extension) {
		String s = Long.toHexString(size)+extension;
		StringBuilder ba = new StringBuilder();
		ba.append(s.getBytes());
		ba.append(CRLF);
		return ba;
	}

	// create single chunk
	public StringBuilder createChunk(char[] val) {
		StringBuilder chunk = getChunkHead(val.length);
		chunk.append(val);
		chunk.append(CRLF);
		return chunk;
	}
//	public StringBuilder createChunk(String val) {
//		StringBuilder chunk = getChunkHead(val.length());
//		chunk.append(val);
//		chunk.append(CRLF);
//		return chunk;
//	}
	
	public StringBuilder createLastChunk() {
		StringBuilder lastChunk = new StringBuilder();
		lastChunk.append('0');
		lastChunk.append(CRLF);
		return lastChunk;
	}

	// create multi chunks with each having length equals "size"
	public StringBuilder createChunk(int size, char[] val) {
		int len = Math.max(size, val.length);
		int pos = 0;
		int blockPos = 0;
		StringBuilder entity = new StringBuilder();
		int blockSize = Math.min(size, val.length);
		char[] block = null;
		do {
			block = new char[blockSize];
			for(blockPos=0; blockPos<blockSize; blockPos++) {
				block[blockPos] = val[pos++];
			}
			entity = createChunk(block);
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
