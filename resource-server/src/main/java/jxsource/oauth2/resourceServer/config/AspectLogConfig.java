package jxsource.oauth2.resourceServer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AspectLogConfig implements WebMvcConfigurer//extends WebMvcConfigurationSupport
{

	@Value("${spring.application.name}")
	private String appName;
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new LogInterceptor(appName)).addPathPatterns("/**");
    }
}
