package com.tuxskar.caluma.ws;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.WSInfo;

public interface WSHandler {
	@GET("/schools")
	List<WSInfo<School>> listSchool();
	
	@GET("/degree/{degree}/")
	  Degree detailDegree(@Path("degree") int degree);
	
}