package com.tuxskar.caluma.ws.models;

import java.sql.Time;

public class Timetable {
	private String description, period;
	private int week_day;
	private long id, t_subject;
	private Time start_time, end_time;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public int getWeek_day() {
		return week_day;
	}
	public void setWeek_day(int week_day) {
		this.week_day = week_day;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getT_subject() {
		return t_subject;
	}
	public void setT_subject(long t_subject) {
		this.t_subject = t_subject;
	}
	public Time getStart_time() {
		return start_time;
	}
	public void setStart_time(Time start_time) {
		this.start_time = start_time;
	}
	public Time getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Time end_time) {
		this.end_time = end_time;
	}
}
