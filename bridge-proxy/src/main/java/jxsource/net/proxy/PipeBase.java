package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.util.ThreadUtil;

/*
 * Pipe to pass data between client and server
 */
public class PipeBase extends Pipe {
	Logger logger = LoggerFactory.getLogger(PipeBase.class);

	@Override
	public void init(String name, InputStream in, OutputStream out, Log log, boolean activeLog) {
		super.init(name, in, out, log, activeLog);
	}

	protected void proc() throws IOException {
		byte[] buf = new byte[1024 * 8];
		int i = 0;
		while (true) {
			try {
				i = in.read(buf);
			} catch (Exception ioe) {
				throw new IOException("Input stream error", ioe);
			}
			if (i != -1) {
				try {
					out.write(buf, 0, i);
					out.flush();
				} catch (Exception ioe) {
					throw new IOException("Output stream error", ioe);
				}
//				if (activeLog && log != null) {
					if (name.contains(Constants.LocalToRemote)) {
						log.logLocalToRemote(buf, 0, i);
					} else {
						log.logRemoteToLocal(buf, 0, i);
					}
//				}
			}
		}
	}

}
