package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxsource.net.proxy.Constants;
import jxsource.net.proxy.tcp.Log;
import jxsource.net.proxy.util.ByteBuffer;
import jxsource.net.proxy.util.HttpUtil;
import jxsource.net.proxy.util.ThreadUtil;

/*
 * Base class to handle each Http request and response
 */
public abstract class HttpPipeProcess {
		protected InputStream in;
		protected OutputStream out;
		protected HttpLog httpLog;
		protected HttpUtil httpUtil = HttpUtil.build();
		protected HttpHeader httpHeader = HttpHeader.build();
		protected String name;

		static final byte b13 = 13;
		static final byte b10 = 10;

		protected final int End = 0;
		protected final int LengthContent = 1;
		protected final int ChunkContent = 2;

		// all those variables must reset in each transaction
		protected int step; // it has three values: End, LengthContent and ChunkContent
		protected long contentLength;
		protected long outputLength;
		protected boolean header;
		protected boolean isChunkHeader;
		protected int chunkSize;
		protected byte[] chunkHeader;
		protected boolean lastChunk;
		protected ProcessContext context;
		protected HttpEditor editor;

		public void init(String name, InputStream in, OutputStream out, HttpLog appLog, ProcessContext context) {
			this.in = in;
			this.out = out;
			// logOut may be null if no output requires
			this.httpLog = appLog;
			this.name = name;
			this.context = context;
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
						httpHeader.init(headerBytes);
						// cache Content-Type for FileLog to select file extension when saving content to file
//						context.addAttribute(Constants.ContentType, httpHeader.getFirstHeader(Constants.ContentType));
//						context.addAttribute(Constants.ContentEncoding, httpHeader.getFirstHeader(Constants.ContentEncoding));
//						context.addAttribute(Constants.TransferEncoding, httpHeader.getFirstHeader(Constants.TransferEncoding));
						context.addAttribute(Constants.HttpHeaders, httpHeader);
									// edit headers
						headerBytes = editor.edit(httpHeader);
						output(headerBytes);
						httpLog.logHeader(headerBytes);
						String headerValue = null;
						if ((headerValue = httpHeader.getFirstHeader(Constants.TransferEncoding)) != null) {
							step = ChunkContent;
							isChunkHeader = true;
							httpLog.startSave();
						} else if ((headerValue = httpHeader.getFirstHeader(Constants.ContentLength)) != null) {
							step = LengthContent;
							contentLength = Long.parseLong(headerValue);
							// reset Content-Length output count
							outputLength = 0;
							if(contentLength > 0) {
								httpLog.startSave();
							}
						} else {
							step = End;
						}
						header = false;
					}
					// process body after or skip header process.
					switch (step) {
					case LengthContent:
						httpLog.logContent(buf.getArray());
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
			httpLog.close();
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
					System.arraycopy(chunk, chunkHeader.length, data, 0, data.length);
					httpLog.logContent(data);
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
