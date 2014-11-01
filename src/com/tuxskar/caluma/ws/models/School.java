package com.tuxskar.caluma.ws.models;

import java.util.List;

public class School{
	private String name, url, address, university;
	private List<SimpleInfo> degrees;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUniversity() {
		return university;
	}
	public void setUniversity(String university) {
		this.university = university;
	}
	public List<SimpleInfo> getDegrees() {
		return degrees;
	}
	public void setDegrees(List<SimpleInfo> degrees) {
		this.degrees = degrees;
	}
	 
	public String toString() {
	    return this.name;
	}
	
	
}
