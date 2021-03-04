package jxsource.net.proxy.http;

/*
 * Class to edit Http Headers
 * 
 * LocalToRemote and RemoteToLocal sub classes 
 * can implement different editing process
 */
public abstract class HttpEditor {

	protected HttpContext context;
	public void setHttpContext(HttpContext context) {
		this.context = context;
	}
	public abstract byte[] edit(HttpHeader bb);
}

