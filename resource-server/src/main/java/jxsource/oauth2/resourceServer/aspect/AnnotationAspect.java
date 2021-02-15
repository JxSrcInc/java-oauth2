package jxsource.oauth2.resourceServer.aspect;


public class AnnotationAspect {
// TODO: log4j does not work with other slf4j binding
//        private static final Logger logger = LogManager
//                        .getLogger(AnnotationAspect.class);
	final String security = "execution(* org.springframework.security..*(..)) "
    		+ "&& !within(is(FinalType)) "
			+ "&& !within(org.springframework.security.config.annotation..*)"
			;
	final String jxSrc = "within(jxsource.oauth2.resourceServer.security..*) || within(jxsource.oauth2.resourceServer.converter..*) ";

	
//	@Pointcut(security) 
//	public void beforeSecurity() {}
//	@Pointcut(security) 
//	public void afterSecurity() {}
//	@Pointcut(security) 
//	public void throwingSecurity() {}

}
 