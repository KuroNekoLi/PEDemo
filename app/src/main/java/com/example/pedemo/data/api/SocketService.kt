package com.example.pedemo.data.api

import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.StockData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SocketService {
    @GET("per-river/interview")
    suspend fun getStockInfo(@Query("stock_id") stockId: String) : Response<Data>
}