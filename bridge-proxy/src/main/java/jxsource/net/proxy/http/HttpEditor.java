package jxsource.net.proxy.http;

/*
 * Class to edit Http Headers
 * 
 * LocalToRemote and RemoteToLocal sub classes 
 * can implement different editing process
 */
public abstract class HttpEditor {

	protected HttpPipeContext context;
	public HttpEditor setHttpPipeContext(HttpPipeContext context) {
		this.context = context;
		return this;
	}
	public abstract byte[] edit(byte[] src);
}

