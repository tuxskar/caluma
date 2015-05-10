package com.tuxskar.caluma.ws.models;

import java.util.List;

public class Degree extends SimpleInfo {
	private String school;
	private List<SubjectSimple> subjects;
	
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public List<SubjectSimple> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<SubjectSimple> subjects) {
		this.subjects = subjects;
	}
	
}
