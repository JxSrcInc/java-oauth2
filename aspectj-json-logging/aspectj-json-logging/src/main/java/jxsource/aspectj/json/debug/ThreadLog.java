package jxsource.aspectj.json.debug;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * 
 */
public class ThreadLog {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String appName = "Unknown";
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	private String bddId;
	private List<CallLog> stack = new ArrayList<CallLog>();
	private ObjectMapper om = new ObjectMapper();
	private JsonNodeFactoryImpl jsonFactory = new JsonNodeFactoryImpl();
	private JsonStringFactoryImpl stringFactory = new JsonStringFactoryImpl();
	private Date startTime;
	private JsonNode requestBody;
	private final double THOUSAND = 1000;
	public static final String RequestJson = "request-json";
	public ThreadLog() {
		om.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	/*
	 * allow re-use
	 */
	public ThreadLog init(String appName) {
		if(appName != null) {
			this.appName = appName;
		}
		Calendar calendar = Calendar.getInstance();
		startTime = calendar.getTime();
		String timeZone = calendar.getTimeZone().getID();
		this.appName = this.appName+' '+simpleDateFormat.format(startTime)+' '+timeZone;
		headers.clear();
		stack.clear();
		return this;
	}
	public String getAppName() {
		return appName;
	}
	public ThreadLog setRequestBody(String body) {
		try {
			requestBody = om.readTree(body);
		} catch (IOException e) {
			requestBody = om.createObjectNode();
			((ObjectNode)requestBody).put("Raw", body);
		}
		return this;
	}
	public JsonNode getRequestBody() {
		return requestBody;
	}
	public ThreadLog setHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public void addCall(CallLog callLog) {
		stack.add(callLog);
	}

	public List<CallLog> getStack() {
		return stack;
	}

	public String getHeader(String key) {
		return headers.get(key);
	}
	public String getBddId() {
		return bddId;
	}

	public void setBddId(String bddId) {
		this.bddId = bddId;
	}

	private void setTimeHeader() {
		Date endTime = new Date();
	    long diffInMillies = Math.abs(endTime.getTime() - startTime.getTime());
		String time = String.format("%s, used %f seconds", simpleDateFormat.format(startTime),((float)diffInMillies)/THOUSAND);
		headers.put("time", time);
	}
	@Override
	public String toString() {
		setTimeHeader();
		String s = "ThreadLog: ";
		for(Entry<String, String> header: headers.entrySet()) {
			s += String.format("%s=%s ", header.getKey(), header.getValue());
		}
		for(CallLog log: stack) {
			s += "\t"+log.toString()+"\n";
		}
		return s;
	}

	public JsonNode getJsonNode(JsonNodeFactoryImpl...factory) {
		JsonNodeFactoryImpl jsonFactory = this.jsonFactory;
		if(factory.length > 0) {
			jsonFactory = factory[0];
		}
		setTimeHeader();
		ObjectNode value = om.createObjectNode();
		value.put("Application", appName);
		for(Entry<String, String> header: headers.entrySet()) {
			value.put(header.getKey(), header.getValue());
		}
		if(requestBody != null) {
			value.set(RequestJson, requestBody);
		}
		ArrayNode stackNode = om.createArrayNode();
		for(CallLog callLog: stack) {
			stackNode.add(jsonFactory.createCallNode(callLog));
		}
		value.set("stack", stackNode);
		return value;
	}

	public String getJsonString(JsonStringFactoryImpl...stringFactory) {
		JsonStringFactoryImpl factory = this.stringFactory;
		if(stringFactory.length > 0) {
			factory = stringFactory[0];
		}
		setTimeHeader();
		String s = "{";
		s += String.format("Applicaion:\"%s\",", appName);
		for(Entry<String, String> header: headers.entrySet()) {
			s += String.format("%s:\"%s\",",header.getKey(), header.getValue());
		}
		if(requestBody != null) {
			s += RequestJson+':'+requestBody.toString();
		}
		s += "stack:[";
		for(int i=0; i<stack.size(); i++) {
			CallLog callLog = stack.get(i);
			s += String.format("%s", factory.createCallString(callLog));
			if(i < stack.size()-1) {
				s += ',';
			}
		}
		
		s += "]}";
		return s;
	}

	
	
}
