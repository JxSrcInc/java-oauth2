package jxsource.util.buffer.charbuffer;

import java.io.IOException;

/*
 * Read data from ByteInputBuffer and write to ByteOutputBuffer
 * If ByteOutputBuffer 
 */
public class CharBufferProcessor {
	private char[] buf = new char[1028*8];
	public long totalOutput;
	public long totalInput;
	
	public void setBufferSize(int length) { 
		buf = new char[length];
	}
	public long run(BufferReader inBuffer, BufferWriter outBuffer) throws IOException {
		totalOutput = 0;
		totalInput = 0;
		int length = 0;
		while((length = inBuffer.read(buf)) != -1) {
			totalInput += length;
			outBuffer.write(buf, 0, length);
			outBuffer.flush();
		}
		// flush outBuffer
		outBuffer.push();
		return totalOutput;
	}
}
