package com.tuxskar.caluma.ws.models.users;

public class User extends LoginUser {
	String name, last_name, email;
	static String role = null;
	String registration_id;

	public User(String username, String password, String name, String last_name, String email) {
		super(username, password);
		this.name = name;
		this.last_name = last_name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRegistration_id() {
		return registration_id;
	}

	public void setRegistration_id(String registration_id) {
		this.registration_id = registration_id;
	}

}
