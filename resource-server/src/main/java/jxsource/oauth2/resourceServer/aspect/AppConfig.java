package jxsource.oauth2.resourceServer.aspect;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

	@Bean
	public AnnotationAspect createAnnotationAspect() {
		return new AnnotationAspect();
	}
}
