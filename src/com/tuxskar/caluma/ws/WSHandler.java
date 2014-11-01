package com.tuxskar.caluma.ws;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.WSInfo;

public interface WSHandler {
	String SERVICE_ENDPOINT = "http://192.168.1.39:8000";
	static String android_key = "8749f6bfcea6e76632b36e47e78b8e92391bcc90";
	
	@GET("/schools")
	List<WSInfo<School>> listSchool();
	
	@Headers("WWW-Authenticate: Basic realm='api'")
	@GET("/schools")
	void listSchoolCB(Callback<WSInfo<School>> cb);
	
	@GET("/degree/{degree}/")
	  Degree detailDegree(@Path("degree") int degree);
	
}