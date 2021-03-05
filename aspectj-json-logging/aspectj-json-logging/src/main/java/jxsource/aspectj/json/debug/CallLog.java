package jxsource.aspectj.json.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class CallLog {

	public static final String BeforeCall = "BeforeCall";
	public static final String AfterCall = "AfterCall";
	public static final String ExceptionCall = "ExceptionCall";
	
	public static final String CallType = "type";
	public static final String Signature = "signature";
	public static final String ClassName = "class";
	public static final String MethodName = "method";
	public static final String Params = "params";
	protected String call_Type;
	protected String signatureVal;
	protected String class_Name;
	protected String method_Name;
	protected List<Parameter> args;
	
	
	protected CallLog(String callType) {
		this.call_Type = callType;
	}
//	public void init(JoinPoint jp) {
//    	Signature s = jp.getSignature();
//    	signature = s.toLongString();
//		className = s.getDeclaringTypeName();
//		methodName = s.getName();		
//		Class<?>[] types = ((CodeSignature) jp.getSignature()).getParameterTypes();
//		Object[] args = jp.getArgs();
//		for (int i = 0; i < types.length; i++) {
//				params.put(types[i].getSimpleName(),args[i]);								
//		}
//	}
	public void init(String signature, String className, String methodName, String[] names, Class<?>[] types, Object[] values) {
		this.signatureVal = signature;
		this.class_Name = className;
		this.method_Name = methodName;
		args = new ArrayList<>();
		assert(names.length == types.length && names.length == values.length);
		for(int i=0; i<names.length; i++) {
			args.add(new Parameter().setName(names[i])
					.setType(types[i]).setValue(values[i]));
		}
	}
	@Override
	public String toString() {
		String json = String.format("{%s:'%s', %s:'%', %s:[",
				CallType, call_Type, Signature,signatureVal, Params);
		int i = 1;
		for(Parameter arg: args) {
			Object value = arg.getValue();
			String strVal = value!=null?value.toString():"null";
			json += String.format("{%s:'%s'}", arg.getName(), strVal);
			if(++i < args.size()) {
				json += ',';
			}
		}
		json += "]}";
		return json;
	}
	public String getCallType() {
		return call_Type;
	}
	public String getClassName() {
		return class_Name;
	}
	public String getMethodName() {
		return method_Name;
	}
	public List<Parameter> getParams() {
		return args;
	}
	public String getSignature() {
		return signatureVal;
	}
}
