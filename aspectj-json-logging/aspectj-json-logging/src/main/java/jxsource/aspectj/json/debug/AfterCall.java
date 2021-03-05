package jxsource.aspectj.json.debug;

public class AfterCall extends CallLog{
	public static final String Return = "ret";
	private Object ret;
	
	public AfterCall() {
		super(AfterCall);
	}
	public Object getRet() {
		return ret;
	}
	public void setRet(Object ret) {
		this.ret = ret;
	}
	@Override
	public String toString() {
		String json = super.toString();
		json = json.substring(1, json.length()-1);
		if(ret != null) {
			json += String.format(", %s:{%s:'%s'}", Return, ret.getClass().getSimpleName(), ret.toString());
		} else {
			json += String.format(", %s:'null'",Return);
		}
		return '{'+json+'}';
	}
}
