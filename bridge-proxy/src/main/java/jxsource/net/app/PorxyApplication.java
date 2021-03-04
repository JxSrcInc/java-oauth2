package jxsource.net.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import jxsource.net.proxy.ProxySocketServer;
import jxsource.net.proxy.tcp.PipeWorker;

import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@ComponentScan(basePackages = { "jxsource.net.proxy" })
public class PorxyApplication implements CommandLineRunner{

	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private ProxySocketServer server;

	public static void main(String... args) {
//		SpringApplication.run(PorxyApplication.class, args);
		new SpringApplicationBuilder(PorxyApplication.class)
		  .web(WebApplicationType.NONE)
		  .run(args);
	}
	@Override
	public void run(String... args) throws Exception {
		server.start();
//		try {
//			ServerSocket ss = new ServerSocket(port);
//			
//			log.info("listening on " + ss.getInetAddress() + ":" + ss.getLocalPort() + " .....");
//			while (true) {
//				Socket client = ss.accept();
//				try {
//				log.info("accept: " + client.getInetAddress());
//				Worker worker = new Worker();//(Worker) context.getBean("Worker");
//				worker.setSocket(client);
//				new Thread(worker).start();
//				} catch(Exception se) {
//					log.error("fail to create Worker thread",se);
//					client.close();
//				}
//			}
//		} catch (IOException e) {
//			log.error("Error: stop application", e);
//			// TODO: use spring termination
//			System.exit(1);;
//		}
//		
	}

}
