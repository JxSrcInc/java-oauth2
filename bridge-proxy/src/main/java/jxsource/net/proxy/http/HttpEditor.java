package jxsource.net.proxy.http;

/*
 * Class to edit Http Headers
 * 
 * LocalToRemote and RemoteToLocal sub classes 
 * can implement different editing process
 */
public interface HttpEditor {

	public abstract byte[] edit(byte[] src);
}

