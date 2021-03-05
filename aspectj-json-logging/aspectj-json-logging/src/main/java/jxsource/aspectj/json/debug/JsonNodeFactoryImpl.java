package jxsource.aspectj.json.debug;

import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeFactoryImpl implements JsonFactory {
	protected ObjectMapper om = new ObjectMapper();

	public JsonNodeFactoryImpl() {
		om.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	protected ObjectNode setAttr(ObjectNode node, String key, Object obj) {
		if (obj == null) {
			node.set(key, null);
		} else {
			try {
				String value = om.writeValueAsString(obj);
				try {
					node.set(key, om.readTree(value));
				} catch (Throwable t) {
					node.put(key, value);
				}
			} catch (Exception e) {
				node.put(key, obj.toString());
			}
		}

		return node;
	}

	public JsonNode createCallNode(CallLog callLog) {
		if (callLog.getCallType().equals(CallLog.BeforeCall)) {
			return createCallBefore(callLog);
		} else 		
		if (callLog.getCallType().equals(CallLog.AfterCall)) {
			return createCallAfter(callLog);
		} else {
			return createCallException(callLog);
		}
	}

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
				if (param instanceof Exception) {
					params = setException(params, key, (Exception) param);
				} else {
					params = setAttr(params, key, param);
				}
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
				ObjectNode retNode = om.createObjectNode();
				retNode = setException(retNode, ex.getClass().getSimpleName(), ex);
				node.set(ExceptionCall.Exception, retNode);
			}
		return node;
	}

	public JsonNode createCallAfter(CallLog callLog) {
		ObjectNode node = om.createObjectNode();
		node.put(CallLog.CallType, callLog.getCallType());
		node.put(CallLog.ClassName, callLog.getClassName());
		node.put(CallLog.MethodName, callLog.getMethodName());
		
			// add parameters to json 
			// because a method may change the input parameters
			ObjectNode params = om.createObjectNode();
			for (Parameter entry : callLog.getParams()) {
				Object param = entry.getValue();
				String key = entry.getName();
				// added to handle exception input parameter
				if (param == null) {
					params.set(key, null);
				} else {
					if (param instanceof Exception) {
						params = setException(params, key, (Exception) param);
					} else {
						params = setAttr(params, key, param);
					}
				}
			}
			node.set(CallLog.Params, params);
			Object ret = ((AfterCall) callLog).getRet();
			if (ret == null) {
				node.set(AfterCall.Return, null);
			} else {
				ObjectNode retNode = om.createObjectNode();
				retNode = setAttr(retNode, ret.getClass().getSimpleName(), ret);
				node.set(AfterCall.Return, retNode);
			}
		return node;
	}

	protected ObjectNode setException(ObjectNode node, String key, Throwable ex) {
		ObjectNode exNode = om.createObjectNode();
		exNode.put("class", ex.getClass().getName());
		exNode.put("message", ex.getMessage());
		Throwable cause = ex.getCause();
		if(cause != null) {
			ObjectNode causeNode = om.createObjectNode();
			String causeKey = cause.getClass().getSimpleName();
			causeNode = setException(causeNode, causeKey, cause);
			exNode.set("cause", causeNode);
		}
		node.set(key, exNode);
		return node;
	}

}
