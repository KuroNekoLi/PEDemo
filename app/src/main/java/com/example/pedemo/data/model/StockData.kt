package com.example.pedemo.data.model


import com.google.gson.annotations.SerializedName

data class StockData(
    @SerializedName("河流圖資料")
    val riverChartData: List<RiverChartData>,
    @SerializedName("本益比基準")
    val peRatioBenchmark: List<String>,
    @SerializedName("股票代號")
    val stockCode: String,
    @SerializedName("股票名稱")
    val stockName: String
)