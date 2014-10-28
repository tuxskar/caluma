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
	String SERVICE_ENDPOINT = "http://192.168.1.33:8000";
	
	@GET("/schools")
	List<WSInfo<School>> listSchool();
	
	@Headers("WWW-Authenticate: Basic realm='api'")
	@GET("/schools")
	void listSchoolCB(Callback<List<WSInfo<School>>> cb);
	
	@GET("/degree/{degree}/")
	  Degree detailDegree(@Path("degree") int degree);
	
}