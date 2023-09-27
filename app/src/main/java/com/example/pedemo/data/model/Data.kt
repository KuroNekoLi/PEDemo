package com.example.pedemo.data.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data")
    val data: List<StockData>
)
