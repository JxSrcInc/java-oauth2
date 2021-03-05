package jxsource.aspectj.json.debug.exception;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * Convert Java Exception to a JsonNode
 * Usage: JsoeNode exceptionNode = ExceptionTrace.build(Throwable).getJson()
 */
public class ExceptionTrace {
	private ObjectMapper om = new ObjectMapper();
	private String className;
	private String message;
	private ExceptionTrace cause;
	private List<JsonNode> stack;
	private int maxTraceLine = 5;
	private ExceptionTrace(Throwable t) {
		className = t.getClass().getName();
		message = t.getMessage();
		stack = new ArrayList<JsonNode>();
		StackTraceElement[] tTrace = t.getStackTrace();
		int i;
		for(i=0; i<Math.min(maxTraceLine, tTrace.length); i++) {
			StackTraceElement e = tTrace[i];
			String s = e.getClassName()+'.'+e.getMethodName();
			String l = "at line "+e.getLineNumber();
			ObjectNode node = om.createObjectNode();
			node.put(s,l);
			stack.add(node);
		}
		if(i == maxTraceLine) {
			ObjectNode node = om.createObjectNode();
			node.put("......", "");
			stack.add(node);
		}
		Throwable cause = t.getCause();
		if(cause != null) {
			this.cause = new ExceptionTrace(cause);
		}
	}
	public void setMaxTraceLine(int maxTraceLine) {
		this.maxTraceLine = maxTraceLine;
	}
	public JsonNode getJson() {
		ObjectNode node = om.createObjectNode();
		node.put("className", className);
			node.put("message", message);
		ArrayNode arrNode = om.createArrayNode();
		for(JsonNode s: stack) {
			arrNode.add(s);
		}
		node.set("stack", arrNode);
		if(cause != null) {
			node.set("cause", cause.getJson());
		}
		return node;
	}
	public String getClassName() {
		return className;
	}
	public String getMessage() {
		return message;
	}
	public ExceptionTrace getCause() {
		return cause;
	}
	@Override
	public String toString() {
		String ret = "**** "+className + ":"+ stack.size()+","+message + "\n";
		for(JsonNode s: stack) {
			ret += "\t"+s.asText()+'\n';
		}
		if(cause != null)
			ret += cause.toString();
		return ret;
	}
	public static ExceptionTrace build(Throwable t) {
		return new ExceptionTrace(t);
	}
}
