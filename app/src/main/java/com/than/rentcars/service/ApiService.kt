package com.than.rentcars.service

import com.than.rentcars.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("admin/car")
    fun getAllCar(): Call<List<GetAllCarResponseItem>>

    @POST("admin/auth/register")
    fun postRegister(@Body request: RegisterRequest): Call<PostRegisterResponse>

    @POST("admin/auth/login")
    fun postLogin(@Body request: LoginRequest): Call<PostLoginResponse>
}