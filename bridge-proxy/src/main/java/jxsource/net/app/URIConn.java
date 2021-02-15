package jxsource.net.app;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class URIConn {

	public static void main(String...args) {
		try {
			new URIConn().conn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void conn() throws Exception {
		URL url = new URL("https://devws.ba.ssa.gov:8443/cmmc/CmmcService?serviceName=CWASSI");
		url = new URL("https://jenkins-m1.ba.ssa.gov/job/ACCS/job/repos/job/accs-template-service/job/develop/292/consoleText");
//		url = new URL("https://devws.ba.ssa.gov:444/WSRR/7.5/Metadata/XML/Query/DataPowerProxyEndpointLookupBySLA?p1=DCSFTCv01.0&p2=FOLOv01.0");
//		url = new URL("https://valws.ba.ssa.gov:447/olcore/OfficeLookupCoreService?OFFICECODEA=1734001&QUERYTYPE=B&APPLICATION=CoreServices_TEST");
		URLConnection conn = url.openConnection();
//		conn.setRequestProperty("Sec-Fetch-Mode", "cors");
//		conn.setRequestProperty("Access-Control-Request-Method", "GET");
//		conn.setRequestProperty("Origin", "http://localhost:3000");
//		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 Edge/16.16299");
//		conn.setRequestProperty("Access-Control-Request-Headers", "requestid");
//		conn.setRequestProperty("Sec-Fetch-Site", "same-site");
//		conn.setRequestProperty("Referer", "http://localhost:3000/ssn");
		conn.connect();
		System.out.println("********************* connected");
//		Map<String, List<String>> headers = conn.getHeaderFields();
//		for(String name: headers.keySet()) {
//			System.out.println("* "+name+" = "+headers.get(name));
//		}
//		conn.setDoOutput(true);
//		OutputStream out = 
//		System.out.println(conn);
		InputStream in = conn.getInputStream();
		byte[] b = new byte[1024*8];
		int i = 0;
		while((i=in.read(b)) != -1) {
			System.out.println(new String(b,0,i));
		}
	}
}
