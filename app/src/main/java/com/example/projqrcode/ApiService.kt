package com.example.projqrcode

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/DS/getDados.php?")
    fun fetchDados(@Query("ra") tags: String): Call<ArrayList<EstruturaApi>>
}