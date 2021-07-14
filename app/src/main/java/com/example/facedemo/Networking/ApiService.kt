package com.example.facedemo.Networking

import com.example.facedemo.Model.ApiRequest
import com.example.facedemo.Model.response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/predict")
    fun getResults(@Body str:ApiRequest) : Call<response>
}