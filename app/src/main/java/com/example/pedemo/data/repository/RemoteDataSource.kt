package com.example.pedemo.data.repository

import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.StockData
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getStockData(stockId: String) : Response<Data>
}