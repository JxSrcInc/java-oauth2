package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.net.proxy.tcp.Log;
import jxsource.net.proxy.util.ByteBuffer;
import jxsource.net.proxy.util.HttpUtil;
import jxsource.net.proxy.util.ThreadUtil;

/*
 * Base class to handle each Http request and response
 */
public class HttpPipeProcess {
		private InputStream in;
		private OutputStream out;
		private HttpLog log;
		private HttpUtil httpUtil = HttpUtil.build();
		private HttpHeader handler = HttpHeader.build();
		private String name;
		private boolean httpBodyLog;

		static final byte b13 = 13;
		static final byte b10 = 10;

		private final int End = 0;
		private final int LengthContent = 1;
		private final int ChunkContent = 2;

		// all those variables must reset in each transaction
		private int step; // it has three values: End, LengthContent and ChunkContent
		private long contentLength;
		private long outputLength;
		private boolean header;
		private boolean isChunkHeader;
		private int chunkSize;
		private byte[] chunkHeader;
		private boolean lastChunk;
		private HttpContext context;

		public static HttpPipeProcess build() {
			return new HttpPipeProcess();
		}

		public HttpPipeProcess init(String name, InputStream in, OutputStream out, HttpLog appLog, HttpContext context) {
			this.in = in;
			this.out = out;
			// logOut may be null if no output requires
			this.log = appLog;
			this.name = name;
			this.context = context;
			this.httpBodyLog = false;// httpBodyLog;
			return this;
		}

		public void proc() throws IOException {
			byte[] b = new byte[1024 * 8];
			int i = 0;
			ByteBuffer buf = new ByteBuffer();
			header = true;
			boolean transProcessing = true;
			lastChunk = false;
			while (transProcessing) {
				try {
					i = in.read(b);
				} catch (Exception ioe) {
					throw new IOException("Input stream error", ioe);
				}
				if (i != -1) {
					buf.append(b, 0, i);
					if (header) {
						int headerLen = 0;
						if ((headerLen = httpUtil.getHerdersLength(buf)) < 0) {
							// headers not complete. do next read
							continue;
						}
						// remove headerLen bytes from buf
						byte[] headerBytes = buf.remove(headerLen);
						handler.init(headerBytes);
						if(context != null) {
							headerBytes = context.getEditor().edit(handler);
						}
						output(headerBytes);
						log.logHeader(headerBytes);
						String headerValue = null;
						if ((headerValue = handler.getHeaderValue("Transfer-Encoding")) != null) {
							step = ChunkContent;
							isChunkHeader = true;
						} else if ((headerValue = handler.getHeaderValue("Content-Length")) != null) {
							step = LengthContent;
							contentLength = Long.parseLong(headerValue);
							outputLength = 0;
						} else {
							step = End;
						}
						header = false;
					}
					// process body after or skip header process.
					switch (step) {
					case LengthContent:
						outputLength += output(buf);
						if (outputLength < contentLength) {
							// read more from input stream
							break;
						} else {
							transProcessing = false;
						}
						break;
					case ChunkContent:
						if (outputChunk(buf)) {
							transProcessing = false;
						}
						// do next read any way either to continue chunk process
						// or block for next request/response
						break;
					default:
						// End.
						transProcessing = false;
					}
					// do next read with clear buf
					// read should block if header == true
				} else {
					// if(i == -1)
					// not reachable point in normal process
					// in which while loop break by transProcessing == false
					throw new IOException("Input stream return -1");
				}
			}
		}

		// return true if chunk complete
		private boolean outputChunk(ByteBuffer buf) throws IOException {
			if (!lastChunk) {
				do {
					if (isChunkHeader) {
						int chunkHeaderBytes = httpUtil.getChunkHeaderLength(buf);
						if (chunkHeaderBytes < 0) {
							// do next read because invalid chunk header
							return false;
						}
						chunkHeader = buf.subArray(0, chunkHeaderBytes);
						String s = new String(chunkHeader).trim();
						chunkSize = Integer.parseInt(s, 16);
						isChunkHeader = false;
						if (chunkSize == 0) {
							// last chunk
							lastChunk = true;
							break; // break do-while loop
						}
					}
					// process chunk
					int chunkBufSize = chunkSize + 2 + chunkHeader.length;
					if (buf.getLimit() < chunkBufSize) {
						// do next read because invalid chunk data
						return false;
					}
					// output one chunk with its header and data
					byte[] chunk = buf.remove(chunkBufSize);
					output(chunk);
					byte[] data = new byte[chunkSize];
					System.arraycopy(chunk, chunkHeader.length+2, data, 0, chunkSize);
					log.logContent(data);
					// do next read because the whole chunk process does not finish
					isChunkHeader = true;
				} while (isChunkHeader);
			}
			// lastChunk
			if (buf.getLimit() < 5) {
				// do next read to complete chunk
				// it shouldn't happen in most cases.
				// just for safe check;
				return false;
			} else {
				// chunk complete
				output(buf);
				return true;
			}
		}

		public void procLengthContent(int length, InputStream in) throws IOException {
			byte[] content = new byte[length];
			int pos = 0;
			while (pos < length) {
				try {
					int i = in.read(content, pos, length - pos);
					pos += i;
				} catch (Exception ioe) {
					throw new IOException("Output stream error", ioe);
				}
			}
			try {
				output(content);
				log.logContent(content);
			} catch (Exception e) {
				throw new IOException(name + " output Http body error", e);
			}
		}

		private int output(ByteBuffer buf) throws IOException {
			// clean buf
			byte[] data = buf.remove(buf.getLimit());
			return output(data);
		}

		private int output(byte[] data) throws IOException {
			out.write(data);
			out.flush();
			return data.length;
		}

		protected String getLogMsg(String info) {
			String msg = String.format("*** %s: %s(%d) %s", ThreadUtil.threadInfo(), name, this.hashCode(), info);
			return msg;

		}

	}
