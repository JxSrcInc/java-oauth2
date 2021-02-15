package jxsource.oauth2.resourceServer.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.ssa.accs.common.debug.webfilter.CachedBodyFilter;

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
