package jxsource.aspectj.json.debug;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringFactoryImpl implements JsonFactory<String> {
	private ObjectMapper om = new ObjectMapper();
	public JsonStringFactoryImpl() {
		om.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	public String createCallString(CallLog callLog) {
		if (callLog.getCallType().equals(CallLog.BeforeCall)) {
			return createCallBefore(callLog);
		} else
		if (callLog.getCallType().equals(CallLog.AfterCall)) {
				return createCallAfter(callLog);
		} else {
			return createCallException(callLog);
		}
	}

	public String createCallBefore(CallLog callLog) {
		String s = "{";
		s += String.format("%s:\"%s\",",CallLog.CallType, callLog.getCallType());
		s += String.format("%s:\"%s\",",CallLog.ClassName, callLog.getClassName());
		s += String.format("%s:\"%s\",",CallLog.MethodName, callLog.getMethodName());
		s += String.format("%s:\"%s\",",CallLog.Signature, callLog.getSignature());
		s += "params:{";
		List<Parameter> params = callLog.getParams();
		int i = 0;
		for (Parameter entry : params) {
			Object param = entry.getValue();
			String key = entry.getName();
			// added to handle exception input parameter
			if (param == null) {
				s += String.format("%s:\"null\"", key);
			} else {
				if (param instanceof Exception) {
					s += String.format("%s:%s", key, getException((Exception) param));
				} else {
					try {
						JsonNode jsonNode = om.readTree(param.toString());
						s += String.format("%s:%s", key, om.writeValueAsString(jsonNode).replace('"', '\''));
					} catch (IOException e) {
						s += String.format("%s:\"%s\"", key, param.toString().replace('"', '\''));
					}
				}
			}
			if(i++ < params.size()-1) {
				s += ',';
			}
		}
		s += "}}";
		return s;
	}

	public String createCallAfter(CallLog callLog) {
		String s = "{";
		s += String.format("%s:\"%s\",",CallLog.CallType, callLog.getCallType());
		s += String.format("%s:\"%s\",",CallLog.ClassName, callLog.getClassName());
		s += String.format("%s:\"%s\",",CallLog.MethodName, callLog.getMethodName());
			Object ret = ((AfterCall) callLog).getRet();
			if (ret == null) {
				s += String.format("%s:\"null\"", AfterCall.Return);
			} else {
				s += String.format("%s:{", AfterCall.Return);
				String key = ret.getClass().getSimpleName();
				try {
					JsonNode jsonNode = om.readTree(ret.toString());
					s += String.format("%s:%s", key, om.writeValueAsString(jsonNode).replace('"', '\''));
				} catch (IOException e) {
					s += String.format("%s:\"%s\"", key, ret.toString().replace('"', '\''));
				}
				s += '}';
			}
		s += "}";
		return s;
	}


	public String createCallException(CallLog callLog) {
		String s = "{";
		s += String.format("%s:\"%s\",",CallLog.CallType, callLog.getCallType());
		s += String.format("%s:\"%s\",",CallLog.ClassName, callLog.getClassName());
		s += String.format("%s:\"%s\",",CallLog.MethodName, callLog.getMethodName());

			Exception ex = ((ExceptionCall) callLog).getException();
			if (ex == null) {
				s += String.format("%s:\"null\"", ExceptionCall.Exception);
			} else {
				s += String.format("%s:%s", ex.getClass().getSimpleName(), getException(ex));
			}
		s += "}";
		return s;
	}

	protected String getException(Throwable ex) {
		String s = "{";
		s += String.format("class:\"%s\",", ex.getClass().getName());
		s += String.format("message:\"%s\"", ex.getMessage());
		Throwable cause = ex.getCause();
		if(cause != null) {
			String causeKey = cause.getClass().getSimpleName();
			String causeObj = getException(cause);
			s += String.format(",%s:%s", causeKey, causeObj);
			s += "}";
		} else {
			s += "}";
		}
		return s;
	}

}
