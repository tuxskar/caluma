package com.tuxskar.caluma.ws;

import com.tuxskar.caluma.gcm.models.SentMessageInfo;
import com.tuxskar.caluma.gcm.models.SentMessageToSubject;
import com.tuxskar.caluma.users.LoggedIn;
import com.tuxskar.caluma.ws.models.Degree;
import com.tuxskar.caluma.ws.models.School;
import com.tuxskar.caluma.ws.models.SimpleInfo;
import com.tuxskar.caluma.ws.models.TeachingSubject;
import com.tuxskar.caluma.ws.models.WSInfo;
import com.tuxskar.caluma.ws.models.users.CalumaDevice;
import com.tuxskar.caluma.ws.models.users.DeviceInfo;
import com.tuxskar.caluma.ws.models.users.LoginUser;
import com.tuxskar.caluma.ws.models.users.User;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface WSHandler {
    //	String SERVICE_ENDPOINT = "http://caluny.noip.me";
    String SERVICE_ENDPOINT = "http://192.168.1.132:8888/";

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

    @GET("/caluny/users/subject_subscribed/")
    void getSubjectSubscribed(Callback<WSInfo<SimpleInfo>> cb);

    @POST("/caluny/chat/send_subject_message/")
    void sendTeacherMessage(@Body SentMessageToSubject mts, Callback<SentMessageInfo> cb);

    @GET("/caluny/chat/messages/")
    void getAllTeachingSubjectMessages(@Query("receiver") long t_subject, Callback<WSInfo<MessageToSubject>> cb);

}