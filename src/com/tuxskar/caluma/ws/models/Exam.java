package com.tuxskar.caluma.ws.models;

import java.util.Date;


public class Exam extends Timetable{
	private String title;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
