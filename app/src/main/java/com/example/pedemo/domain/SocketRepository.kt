package com.example.pedemo.domain

import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.StockData
import com.example.pedemo.data.util.Resource

interface SocketRepository {
    suspend fun getSockData(stockId:String) : Resource<Data>
}