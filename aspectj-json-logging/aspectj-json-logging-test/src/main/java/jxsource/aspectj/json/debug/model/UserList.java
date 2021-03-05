package jxsource.aspectj.json.debug.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class UserList {
	Map<String, User> list = new HashMap<>();
	
	public UserList() {
		User scott = new User();
		scott.setAge(30);
		scott.setName("Scott");
		list.put("Scott", scott);
	}

	public User get(String name) {
		return list.get(name);
	}
	public Map<String, User> update(User user) {
		if(list.containsKey(user.name)) {
			Map<String, User> change = new HashMap<>();
			change.put("before", list.get(user.name));
			list.replace(user.name, user);
			change.put("after", list.get(user.name));
			return change;
		} else {
			throw new RuntimeException("cannot update. because "+user.name+" does not exist");
		}
	}
	public Collection<User> create(User user) {
		list.put(user.name, user);
		return list.values();
	}
	public Collection<User> delete(String name) {
		if(list.remove(name) == null) {
			throw new RuntimeException("cannot delete. because "+name+" does not exist");
		} else {
			return list.values();			
		}
	}
}
