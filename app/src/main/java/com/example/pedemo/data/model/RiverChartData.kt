package com.example.pedemo.data.model


import com.google.gson.annotations.SerializedName

data class RiverChartData(
    @SerializedName("年月")
    val yearMonth: String,
    @SerializedName("近四季EPS")
    val epsLastFourSeasons: String,
    @SerializedName("本益比股價基準")
    val peRatioPriceBenchmark: List<String>
)