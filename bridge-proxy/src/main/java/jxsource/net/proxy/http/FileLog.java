package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import jxsource.net.proxy.Constants;
import jxsource.net.proxy.Dispatcher;
import jxsource.net.proxy.util.ByteBuffer;

import org.brotli.dec.BrotliInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLog {
	private static Logger log = LoggerFactory.getLogger(FileLog.class);
	private DownloadManager downloadManager = DownloadManager.get();
	private ProcessContext context;
	private OutputStream out;
	private ByteBuffer cache;
	private long processed;
	private String filename;
	private String logName;
	
	private String contentEncoding;
	private String transferEncoding;
	private String contentType;
	private String contentLength;
	private String requestLine;
	
	public static FileLog build(ProcessContext context, String type) {
		FileLog fileLog = new FileLog(context, type);
		return fileLog.init() ? fileLog : null;
	}

	private FileLog(ProcessContext context, String logName) {
		this.context = context;
		this.logName = logName;
		reset();
	}
	
	public void save(byte[] data) {
//		System.err.println("*** save "+debugInfo()+","+data.length);
		if (out != null) {
			try {
				if ("gaip".equals(contentEncoding)
						|| "br".equals(contentEncoding)) {
					cache.append(data);
				} else {
					out.write(data);
					out.flush();
					processed += data.length;
				}
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
	}

	public void close() {
		try {
			if (cache.getLimit() > 0 && contentEncoding.equals("gzip")) {
				byte[] data = cache.getArray();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				GZIPInputStream gis = new GZIPInputStream(in);
				byte[] buf = new byte[1024 * 8];
				int i = 0;
				while ((i = gis.read(buf)) != -1) {
					out.write(buf, 0, i);
					out.flush();
					processed += i;
				}
				gis.close();
			} else if (cache.getLimit() > 0 && contentEncoding.equals("br")) {
				byte[] data = cache.getArray();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				BrotliInputStream bis = new BrotliInputStream(in);
				byte[] buf = new byte[1024 * 8];
				int i = 0;
				while ((i = bis.read(buf)) != -1) {
					out.write(buf, 0, i);
					out.flush();
					processed += i;
				}
				bis.close();
			}
			out.close();
//			System.err.println("*** finish: "+requestLine+"\n\t"+debugInfo()+", processed="+processed);
//			System.err.println("*** -----------------------------");
			downloadManager.save(filename, requestLine);	
		} catch (Exception e) {
			String msg = String.format("Error to save data with Content-Encoding '%s', len=%d, processed=%d, data:\n%s",
					contentEncoding, cache.getLimit(), processed,
					new String(cache.getArray()));
			log.error(msg,e);
		} finally {
			cache = null;
			out = null;			
		}
	}
	
	public void reset() {
		cache = new ByteBuffer();
		processed = 0;
	}
	
	private String debugInfo() {
		return String.format("file=%s, encode=%s, trns=%s, type=%s, len=%s", filename,
				contentEncoding,transferEncoding,contentType,contentLength);
	}

	private boolean init() {
		if (!context.getSessionContext().isDownloadData()) {
			return false;
		}
		if (out != null) {
			return true;
		}
		HttpHeader headers = (HttpHeader)context.getAttribute(Constants.HttpHeaders);
		contentEncoding = headers.getFirstHeader(Constants.ContentEncoding);
		transferEncoding = headers.getFirstHeader(Constants.TransferEncoding);
		contentType = headers.getFirstHeader(Constants.ContentType);
		contentLength = headers.getFirstHeader(Constants.ContentLength);
		requestLine = ((HttpHeader)context.getSessionContext().getRequestContext().getAttribute(Constants.HttpHeaders)).getFirstLine();

		File dir = new File(context.getSessionContext().getDownloadDir());
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				System.err.println("Cannot create dir " + context.getSessionContext().getDownloadDir());
				return false;
			}
		}
		if (context.getSessionContext().isDownloadData()) {
			String extion = getContentType();
			if (extion != null) {
				String downloadMime = context.getSessionContext().getDownloadMime();
				if (downloadMime.contains(extion)) {
					filename = context.getSessionContext().getDownloadDir() + '/' + logName+'-'+context.getSessionContext().getRemoteHost()
							+ '-'+Long.toString(System.currentTimeMillis()) + '.' + extion;
//					System.err.println("*** init "+debugInfo());
					try {
						out = new FileOutputStream(filename);
						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		out = null;
		return false;
	}

	protected String getContentType() {
		if (contentType != null) {
			int i = contentType.indexOf(";");
			if (i > 0) {
				contentType = contentType.substring(0, i).trim();
			}
			i = contentType.indexOf("/");
			if (i > 0) {
				contentType = contentType.substring(i + 1).trim();
			}
		}
		return contentType;
	}

}
