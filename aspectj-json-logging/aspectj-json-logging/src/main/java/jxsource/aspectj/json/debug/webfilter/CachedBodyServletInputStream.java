package jxsource.aspectj.json.debug.webfilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class CachedBodyServletInputStream extends ServletInputStream{

    private ByteArrayInputStream cachedBodyInputStream;
    
    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }
	@Override
	public boolean isFinished() {
	    return cachedBodyInputStream.available() == 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		throw new NullPointerException("No ReadListener in "+this.getClass().getName());
	}

	@Override
	public int read() throws IOException {
	    return cachedBodyInputStream.read();
	}

}
