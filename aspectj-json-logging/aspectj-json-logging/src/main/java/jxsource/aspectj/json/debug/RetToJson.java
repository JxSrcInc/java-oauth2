package jxsource.aspectj.json.debug;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jxsource.aspectj.json.debug.exception.ExceptionTrace;

/*
 * Convert CallLog to JsonNode
 */
public class RetToJson {
	private final ObjectMapper om = new ObjectMapper();
	public JsonNode convert(CallLog callLog) {
		if(callLog instanceof AfterCall) {
			return om.valueToTree(((AfterCall)callLog).getRet());
		} else 
		if(callLog instanceof ExceptionCall) {
			Exception ex = ((ExceptionCall)callLog).getException();
			ExceptionTrace trace = ExceptionTrace.build(ex);
			return om.valueToTree(trace);
		} else {
			return null;
		}
	}
	public static RetToJson build() {
		return new RetToJson();
	}
}
