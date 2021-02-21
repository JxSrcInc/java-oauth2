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
	Logger log = LoggerFactory.getLogger(PipeBase.class);

	@Override
	public void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		super.init(name, in, out, logOut);
	}

	protected void proc() throws IOException {
			byte[] buf = new byte[1024 * 8];
			int i = 0;
			boolean isLogOutReady = true;
			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
				out.flush();
				if (isLogOutReady && logOut != null) {
					try {
						logOut.write(buf, 0, i);
						logOut.flush();
					} catch (IOException e) {
						// turn logOut off
						isLogOutReady = false;
						log.error(getLogMsg("logOut Exception"), e);
					}
				}
			}
	}


}
