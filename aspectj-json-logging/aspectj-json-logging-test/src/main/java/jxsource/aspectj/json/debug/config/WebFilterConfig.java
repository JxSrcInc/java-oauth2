package jxsource.aspectj.json.debug.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jxsource.aspectj.json.debug.webfilter.CachedBodyFilter;

@Configuration
public class WebFilterConfig {
	 @Bean
	 public FilterRegistrationBean < CachedBodyFilter > compressionFilterRegistrationBean() {
	  FilterRegistrationBean < CachedBodyFilter > registrationBean = new FilterRegistrationBean();
	  CachedBodyFilter cacheBodyFilter = new CachedBodyFilter();

	  registrationBean.setFilter(cacheBodyFilter);
	  registrationBean.addUrlPatterns("/*");
	  registrationBean.setOrder(1); //set precedence
	  return registrationBean;
	 }

}
