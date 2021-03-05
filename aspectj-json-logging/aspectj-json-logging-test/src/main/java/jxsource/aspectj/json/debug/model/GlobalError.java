package jxsource.aspectj.json.debug.model;

import jxsource.aspectj.json.debug.ThreadLogLocal;

public class GlobalError {
	
	String errClass;
	String errMsg;
	String errId;

	public GlobalError setException(Exception ex) {
		errClass = ex.getClass().getName();
		errMsg = ex.getMessage();
		errId = ThreadLogLocal.get().getAppName();
		return this;
	}

	public String getErrClass() {
		return errClass;
	}

	public void setErrClass(String errClass) {
		this.errClass = errClass;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrId() {
		return errId;
	}

	public void setErrId(String errId) {
		this.errId = errId;
	}
	
}
