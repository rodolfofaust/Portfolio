package br.com.sankhya.ctba.integracaoapi;

import java.util.List;

public class User {
	public String id;
	public String name;



    
    
    public User(String id, String name) {
		this.id = id;
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public static String getUserNameById(List<User> users, String id) {
        for (User user : users) {
        	/*
        	if (user.id.equals(id)) {
                return user.name;
            }
        	 */            
            return user.name;
        }
        return null;
    }


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

   

}
