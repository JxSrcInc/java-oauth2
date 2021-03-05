package jxsource.aspectj.json.debug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogTestApp {

	public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LogTestApp.class);
        springApplication.run(args);
	}

}
