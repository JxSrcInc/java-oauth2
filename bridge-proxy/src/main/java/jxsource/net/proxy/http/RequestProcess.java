package jxsource.net.proxy.http;

import java.io.InputStream;
import java.io.OutputStream;

public class RequestProcess extends HttpPipeProcess{


	public static RequestProcess build() {
		return new RequestProcess();
	}

	@Override
	public void init(String name, InputStream in, OutputStream out, HttpLog appLog, ProcessContext context) {
		super.init(name, in, out, appLog, context);
		editor = new HttpRequestEditor();
		editor.setHttpContext(context);
	}
}
