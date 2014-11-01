package com.tuxskar.caluma.ws;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.WSInfo;

public interface WSHandler {
	String SERVICE_ENDPOINT = "http://192.168.1.39:8000";
	static String android_key = "8749f6bfcea6e76632b36e47e78b8e92391bcc90";

	@GET("/schools")
	void listSchoolCB(Callback<WSInfo<School>> cb);

	@GET("/degree/{degree}/")
	void getDegree(@Path("degree") long degree, Callback<Degree> cb);

}