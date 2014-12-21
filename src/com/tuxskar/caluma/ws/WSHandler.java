package com.tuxskar.caluma.ws;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.TeachingSubject;
import com.tuxskar.caluma.ws.models.WSInfo;

public interface WSHandler {
	String SERVICE_ENDPOINT = "http://192.168.1.35:8000";
	static String android_key = "43bc34dc5b194f1d3bbf91e7342488b479fb681f";

	@GET("/schools")
	void listSchoolCB(Callback<WSInfo<School>> cb);

	@GET("/degree/{degree}/")
	void getDegree(@Path("degree") long degree, Callback<Degree> cb);
	
	@GET("/teachingsubject/{t_subject}/")
	void getTSubject(@Path("t_subject") long t_subject, Callback<TeachingSubject> cb);

}