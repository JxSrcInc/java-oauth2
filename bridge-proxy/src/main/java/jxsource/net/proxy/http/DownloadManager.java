package jxsource.net.proxy.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import jxsource.net.proxy.AppContext;

public class DownloadManager {
	private static DownloadManager me;
	private AppContext context;
	PrintStream ps;
	public static DownloadManager get() {
		if(me == null) {
			me = new DownloadManager();
		}
		return me;
	}
	
	private DownloadManager() {
		context = AppContext.get();
		if(init()) {
			persistent();
		}
	}
	
	private boolean init() {
		File dir = new File(context.getDownloadDir());
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				System.err.println("Cannot create dir " + context.getDownloadDir());
				return false;
			}
		}
		return true;
	}
	private void persistent() {
		try {
			ps = new PrintStream(new FileOutputStream(new File(context.getDownloadDir(),"file-manager.csv")));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file-manager.csv file");
		}
	}
	public synchronized void save(String file, String url) {
		if(ps != null) {
			ps.append(file+",\""+url+"\"\n");
			ps.flush();
			System.err.println("*** save: "+file+"\n\t"+url);
			System.err.println("*** -----------------------------");
		}
	}
}
