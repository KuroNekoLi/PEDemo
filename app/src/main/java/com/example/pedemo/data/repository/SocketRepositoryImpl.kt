package com.example.pedemo.data.repository

import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.StockData
import com.example.pedemo.data.util.Resource
import com.example.pedemo.domain.SocketRepository
import retrofit2.Response

class SocketRepositoryImpl(private val remoteDataSource: RemoteDataSource) : SocketRepository {
    override suspend fun getSockData(stockId:String): Resource<Data> {
        return responseToResource(remoteDataSource.getStockData(stockId))
    }
    private fun responseToResource(response: Response<Data>):Resource<Data>{
        if(response.isSuccessful){
            response.body()?.let {result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(response.message())
    }
}