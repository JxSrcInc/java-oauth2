package jxsource.aspectj.json.debug.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;

import com.fasterxml.jackson.databind.ObjectMapper;

import jxsource.aspectj.json.debug.AfterCall;
import jxsource.aspectj.json.debug.BeforeCall;
import jxsource.aspectj.json.debug.CallLog;
import jxsource.aspectj.json.debug.ExceptionCall;
import jxsource.aspectj.json.debug.ThreadLog;
import jxsource.aspectj.json.debug.ThreadLogLocal;

//@Aspect
//@Component
public class AspectUtil {
	private ObjectMapper om = new ObjectMapper();
//	@AfterReturning(pointcut = availPointcut, returning = "obj")
	public Object afterReturning(JoinPoint jp, Object obj) {
		ThreadLog log = ThreadLogLocal.get();
		AfterCall callLog = new AfterCall();
		callLog = initCallLog(callLog, jp);
		callLog.setRet(obj);
		log.addCall(callLog);
		return obj;
	}

//	@AfterThrowing(pointcut = availPointcut, throwing = "ex")
	public void exception(JoinPoint jp, Exception ex) {
		ThreadLog log = ThreadLogLocal.get();
		ExceptionCall callLog = new ExceptionCall();
		callLog = initCallLog(callLog, jp);
		callLog.setException(ex);
		log.addCall(callLog);
	}

//	@Before(availPointcut)
	public void before(JoinPoint jp) throws Throwable {
		ThreadLog log = ThreadLogLocal.get();
		BeforeCall callLog = new BeforeCall();
		callLog = initCallLog(callLog, jp);
		log.addCall(callLog);
	}

	public <T extends CallLog> T initCallLog(T log, JoinPoint jp) {
    	Signature s = jp.getSignature();
    	String signature = s.toLongString();
		String className = s.getDeclaringTypeName();
		String methodName = s.getName();		
		String[] names = ((CodeSignature) jp.getSignature()).getParameterNames();
		Class<?>[] types = ((CodeSignature) jp.getSignature()).getParameterTypes();
		Object[] args = jp.getArgs();
		log.init(signature, className, methodName, names, types, args);
		return log;
	}
}
