package com.weizu.myapplication.retrofitTest;

import com.weizu.myapplication.Bean.User;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestInterface {

    @GET(value = "/test/1.0/users")
    Call<List<User>> listUsers();   // retrofit2.Call;

    @GET(value = "/test/1.0/users/{userid}")
    Call<User> getUserById(@Path(value = "userid") char userId);

    @FormUrlEncoded
    @POST(value = "/test/1.0/users")
    Call<Void> addUser(@Field(value = "name") String name);
}
