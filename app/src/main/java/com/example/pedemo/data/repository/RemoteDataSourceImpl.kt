package com.example.pedemo.data.repository

import com.example.pedemo.data.api.SocketService
import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.StockData
import retrofit2.Response

class RemoteDataSourceImpl(private val socketService: SocketService) : RemoteDataSource{

    override suspend fun getStockData(stockId: String): Response<Data> {
        return socketService.getStockInfo(stockId )
    }
}