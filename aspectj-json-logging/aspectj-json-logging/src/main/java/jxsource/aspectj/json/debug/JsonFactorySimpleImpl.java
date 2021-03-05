package jxsource.aspectj.json.debug;

import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * If input parameters and return object are Java Prims, keep their values
 * otherwise keep their class name.
 * Excepetion details will log in BaseController
 */
public class JsonFactorySimpleImpl extends JsonNodeFactoryImpl {

	public JsonFactorySimpleImpl() {
		super();
	}

	@Override
	public JsonNode createCallBefore(CallLog callLog) {
		ObjectNode node = om.createObjectNode();
		node.put(CallLog.CallType, callLog.getCallType());
		node.put(CallLog.ClassName, callLog.getClassName());
		node.put(CallLog.MethodName, callLog.getMethodName());
		node.put(CallLog.Signature, callLog.getSignature());
		ObjectNode params = om.createObjectNode();
		for (Parameter entry : callLog.getParams()) {
			Object param = entry.getValue();
			String key = entry.getName();
			// added to handle exception input parameter
			if (param == null) {
				params.set(key, null);
			} else {
				// keep class name
				params = setAttr(params, key, ValueFactory.getValue(param));
			}

		}
		node.set(CallLog.Params, params);
		return node;
	}

	public JsonNode createCallException(CallLog callLog) {
		ObjectNode node = om.createObjectNode();
		node.put(CallLog.CallType, callLog.getCallType());
		node.put(CallLog.ClassName, callLog.getClassName());
		node.put(CallLog.MethodName, callLog.getMethodName());

			Exception ex = ((ExceptionCall) callLog).getException();
			if (ex == null) {
				node.set(ExceptionCall.Exception, null);
			} else {
				node.put(ExceptionCall.Exception, ex.getClass().getName());
			}
		return node;
	}

	public JsonNode createCallAfter(CallLog callLog) {
		ObjectNode node = om.createObjectNode();
		node.put(CallLog.CallType, callLog.getCallType());
		node.put(CallLog.ClassName, callLog.getClassName());
		node.put(CallLog.MethodName, callLog.getMethodName());

			// no input parameters
			Object ret = ((AfterCall) callLog).getRet();
			if (ret == null) {
				node.set(AfterCall.Return, null);
			} else {
				Object value = ValueFactory.getValue(ret);
				if(value instanceof String) {
					node.put(AfterCall.Return, value.toString());
				} else {
					ObjectNode retNode = om.createObjectNode();
					retNode = setAttr(retNode, ret.getClass().getSimpleName(), ret);
					node.set(AfterCall.Return, retNode);
				}
			}
		return node;
	}


}
