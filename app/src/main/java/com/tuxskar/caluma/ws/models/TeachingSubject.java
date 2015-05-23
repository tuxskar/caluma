package com.tuxskar.caluma.ws.models;

import java.util.Date;

public class TeachingSubject {
    private String address;
    private int course;
    private long id, subject;
    private Date start_date, end_date;

    private Timetable timetables[];
    private Exam exams[];

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubject() {
        return subject;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Timetable[] getTimetables() {
        return timetables;
    }

    public void setTimetables(Timetable[] timetables) {
        this.timetables = timetables;
    }

    public Exam[] getExams() {
        return exams;
    }

    public void setExams(Exam[] exams) {
        this.exams = exams;
    }

}
