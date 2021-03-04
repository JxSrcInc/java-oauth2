package jxsource.net.proxy.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import jxsource.net.proxy.Constants;
import org.brotli.dec.BrotliInputStream;

public class FileLog {

	private HttpContext context;
	private OutputStream out;

	public static FileLog build(HttpContext context) {
		FileLog fileLog = new FileLog(context);
		return fileLog.init() ? fileLog : null;
	}

	private FileLog(HttpContext context) {
		this.context = context;
	}

	public void save(byte[] data) {
		if (out != null) {
			try {
				if (context.getValue(Constants.ContentEncoding).equals("gzip")) {
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					GZIPInputStream gis = new GZIPInputStream(in);
					byte[] buf = new byte[1024 * 8];
					int i = 0;
					while ((i = gis.read(buf)) != -1) {
						out.write(buf, 0, i);
						out.flush();
					}
					gis.close();
				} else if (context.getValue(Constants.ContentEncoding).equals("br")) {
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					BrotliInputStream bis = new BrotliInputStream(in);
					byte[] buf = new byte[1024 * 8];
					int i = 0;
					while ((i = bis.read(buf)) != -1) {
						out.write(buf, 0, i);
						out.flush();
					}
					bis.close();
				} else {
					out.write(data);
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
	}

	public void close() {
		try {
			out.close();
		} catch (Exception e) {
			out = null;
		}
	}

	private boolean init() {
		if (!context.isDownloadData()) {
			return false;
		}
		if (out != null) {
			return true;
		}
		File dir = new File(context.getDownloadDir());
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				System.err.println("Cannot create dir " + context.getDownloadDir());
				return false;
			}
		}
		if (context.isDownloadData()) {
			String extion = getContentType();
			if (extion != null) {
				String downloadMime = context.getDownloadMime();
				if (downloadMime.contains(extion)) {
					String file = context.getDownloadDir() + '/' + context.getRemoteHost()
							+ Long.toString(System.currentTimeMillis()) + '.' + extion;
					System.err.println(file);
					try {
						out = new FileOutputStream(file);
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
		String contentType = context.getValue(Constants.ContentType);
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
