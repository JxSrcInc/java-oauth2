package jxsource.aspectj.json.debug;

import java.util.List;
import java.util.Set;

/*
 * If object is prime, return object
 * If object is not prime, return class name
 * If object is List or Set of type of prime, return object
 * If object is List or Set of type of not prime, return List or Set of type String 
 * 	with value of type name + size
 */
public class ValueFactory {
	public static final String NUMBER = "Number";
	public static final String STRING = "String";
	public static final String CHAR = "Char";
	public static final String BOOLEAN = "Boolean";
	public static final String NULL = "Null";
	public static final String ARRAY = "Array";
	
	public static Object getValue(Object obj) {
		if(obj == null) {
			return "null";
		} else
		if(isPrime(obj)) {
			return obj;
		} else 
		if(obj.getClass().getName().charAt(0) == '[') {
			String cl = obj.getClass().getName();
			if(cl.length() == 2) {
				// prime array
				return obj;
			} else
			if(cl.contains("String")) {
				return obj;
			} else {
				return cl;
			}
		} else
		if(obj instanceof List) {
			List list = (List) obj;
			if(list.size() == 0) {
				return obj;
			} else {
				Object ele = list.get(0);
				if(isPrime(ele)){
					return obj;
				} else 
				if(ele != null){
					return "List<"+ele.getClass().getName()+"> size="+list.size();
				} else {
					return "List size="+list.size();					
				}
			}
		} else
		if(obj instanceof Set) {
			Set set = (Set) obj;
			if(set.size() == 0) {
				return obj;
			} else {
				Object[] ele = set.toArray();
				if(isPrime(ele[0])){
					return obj;
				} else {
					return "Set<"+ele[0].getClass().getName()+"> size="+ele.length;
				}
			}
		} else {
			return obj.getClass().getName();
		}
	}
	
	public static boolean isPrime(Object obj) {
		if(obj != null && (
				obj instanceof Number ||
				obj instanceof String ||
				obj instanceof Character ||
				obj instanceof Boolean 
				)) {
			return true;
		} else {
			return false;
		}
	}


}
