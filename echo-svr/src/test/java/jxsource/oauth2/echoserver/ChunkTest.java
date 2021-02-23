package jxsource.oauth2.echoserver;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.Test;

import jxsource.oauth2.echoserver.entity.ChunkEntity;

public class ChunkTest {

	@Test
	public void test() {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<100; i++) {
			builder.append("1234567890");
		}
    	String msg = builder.toString();	
    	msg = ChunkEntity.build().createChunk(50, msg);
		System.out.println(msg);
	}
}
