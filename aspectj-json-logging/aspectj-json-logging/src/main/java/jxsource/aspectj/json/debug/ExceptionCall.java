package jxsource.aspectj.json.debug;

public class ExceptionCall extends CallLog{
	public static final String Exception = "ex";
	private Exception ex;
	
	public ExceptionCall() {
		super(ExceptionCall);
	}
	public Exception getException() {
		return ex;
	}
	public void setException(Exception ex) {
		this.ex = ex;
	}
	@Override
	public String toString() {
		String json = super.toString();
		json = json.substring(1, json.length()-1);
		if(ex != null) {
			json += String.format(", %s:{%s:'%s'}", Exception, ex.getClass().getSimpleName(), ex.getMessage());
		} else {
			json += String.format(", %s:'null'",Exception);
		}
		return '{'+json+'}';
	}

}
