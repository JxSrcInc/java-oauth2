package jxsource.net.proxy.http;

import java.io.InputStream;
import java.io.OutputStream;

public class ResponseProcess extends HttpPipeProcess{

	public static ResponseProcess build() {
		return new ResponseProcess();
	}
	
	@Override
	public void init(String name, InputStream in, OutputStream out, HttpLog appLog, ProcessContext context) {
		super.init(name, in, out, appLog, context);
		editor = new HttpRequestEditor();
		editor.setHttpContext(context);
	}

}
