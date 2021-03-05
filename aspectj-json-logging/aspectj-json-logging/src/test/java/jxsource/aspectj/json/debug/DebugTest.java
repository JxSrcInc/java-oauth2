package jxsource.aspectj.json.debug;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import jxsource.aspectj.json.debug.ThreadLog;
import jxsource.aspectj.json.debug.ThreadLogLocal;

public class DebugTest {

	@Test
	public void test() {
		ThreadLog tLog = ThreadLogLocal.get();
		tLog.init("Test Application");
		JsonNode jNode = tLog.getJsonNode();
		String jStr = tLog.getJsonString();
	}
}
