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

	private ProcessContext context;
	private OutputStream out;
	private ByteBuffer cache;
	private long processed;
	private String filename;
	private String type;
	
	public static FileLog build(ProcessContext context, String type) {
		FileLog fileLog = new FileLog(context, type);
		return fileLog.init() ? fileLog : null;
	}

	private FileLog(ProcessContext context, String type) {
		this.context = context;
		this.type = type;
		reset();
	}

	public void save(byte[] data) {
		System.out.println("*** save "+filename+","+context.getValue(Constants.ContentEncoding)+","+data.length);
		if (out != null) {
			try {
				if ("gaip".equals(context.getValue(Constants.ContentEncoding))
						|| "br".equals(context.getValue(Constants.ContentEncoding))) {
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
			if (cache.getLimit() > 0 && context.getValue(Constants.ContentEncoding).equals("gzip")) {
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
			} else if (cache.getLimit() > 0 && context.getValue(Constants.ContentEncoding).equals("br")) {
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
			System.out.println("*** finish "+filename+","+context.getValue(Constants.ContentEncoding)+","+processed);
			System.out.println("*** -----------------------------");
		} catch (Exception e) {
			String msg = String.format("Error to save data with Content-Encoding '%s', len=%d, processed=%d, data:\n%s",
					context.getValue(Constants.ContentEncoding), cache.getLimit(), processed,
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

	private boolean init() {
		if (!context.getSessionContext().isDownloadData()) {
			return false;
		}
		if (out != null) {
			return true;
		}
		File dir = new File(context.getSessionContext().getDownloadDir());
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				System.out.println("Cannot create dir " + context.getSessionContext().getDownloadDir());
				return false;
			}
		}
		if (context.getSessionContext().isDownloadData()) {
			String extion = getContentType();
			if (extion != null) {
				String downloadMime = context.getSessionContext().getDownloadMime();
				if (downloadMime.contains(extion)) {
					filename = context.getSessionContext().getDownloadDir() + '/' + type+'-'+context.getSessionContext().getRemoteHost()
							+ '-'+Long.toString(System.currentTimeMillis()) + '.' + extion;
					System.out.println("*** init "+filename);
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
		String contentType = context.getSessionContext().getValue(Constants.ContentType);
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
