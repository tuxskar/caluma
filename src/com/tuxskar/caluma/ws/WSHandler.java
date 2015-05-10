package com.tuxskar.caluma.ws;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import com.tuxskar.caluma.users.LoggedIn;
import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.TeachingSubject;
import com.tuxskar.caluma.ws.models.WSInfo;
import com.tuxskar.caluma.ws.models.users.CalumaDevice;
import com.tuxskar.caluma.ws.models.users.DeviceInfo;
import com.tuxskar.caluma.ws.models.users.LoginUser;
import com.tuxskar.caluma.ws.models.users.User;

public interface WSHandler {
//	String SERVICE_ENDPOINT = "http://caluny.noip.me";
	String SERVICE_ENDPOINT = "http://192.168.0.194:8888/";

	@GET("/schools")
	void listSchoolCB(Callback<WSInfo<School>> cb);

	@GET("/degree/{degree}/")
	void getDegree(@Path("degree") long degree, Callback<Degree> cb);
	
	@GET("/teachingsubject/{t_subject}/")
	void getTSubject(@Path("t_subject") long t_subject, Callback<TeachingSubject> cb);

	@POST("/caluny/users/api-token-auth/")
	void getUserToken(@Body LoginUser user, Callback<LoggedIn> cb);
	
	@POST("/caluny/users/create_user/")
	void createNewUser(@Body User user, Callback<LoggedIn> cb);
	
	@POST("/caluny/users/register_gcm_user/")
	void registerGcmDevice(@Body CalumaDevice device, Callback<DeviceInfo> cb);
}