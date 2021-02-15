package jxsource.net.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpRequest {

	public static void main(String... args) {
		String url = "https://devws.ba.ssa.gov:444/WSRR/7.5/Metadata/XML/Query/DataPowerProxyEndpointLookupBySLA?p1=DCSFTCv01.0&p2=FOLOv01.0";
		url = "https://valws.ba.ssa.gov:447/olcore/OfficeLookupCoreService?OFFICECODEA=1734001&QUERYTYPE=B&APPLICATION=CoreServices_TEST";
//		url = "http://localhost:9999/olcore/OfficeLookupCoreService?OFFICECODEA=1734001&QUERYTYPE=B&APPLICATION=CoreServices_TEST";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		try {
			// add request header
			request.addHeader("ConsumerID", "DCSFTCv01.0");
			request.addHeader("ContextID", "FOLOv01.0");
			HttpResponse response = client.execute(request);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
