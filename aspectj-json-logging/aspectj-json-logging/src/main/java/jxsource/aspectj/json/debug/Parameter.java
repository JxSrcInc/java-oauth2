package jxsource.aspectj.json.debug;

public class Parameter {
	private String name;
	private Class<?> type;
	private Object value;
	public Class<?> getType() {
		return type;
	}
	public Parameter setType(Class<?> type) {
		this.type = type;
		return this;
	}
	public Object getValue() {
		return value;
	}
	public Parameter setValue(Object value) {
		this.value = value;
		return this;
	}
	public String getName() {
		return name;
	}
	public Parameter setName(String name) {
		this.name = name;
		return this;
	}
	
}
