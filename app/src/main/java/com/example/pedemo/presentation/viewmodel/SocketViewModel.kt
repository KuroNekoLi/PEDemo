package com.example.pedemo.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedemo.data.model.Data
import com.example.pedemo.data.model.RiverChartData
import com.example.pedemo.data.util.MyFillFormatter
import com.example.pedemo.data.util.Resource
import com.example.pedemo.domain.SocketRepository
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SocketViewModel(
    private val socketRepository: SocketRepository,
    private val app: Application
) :AndroidViewModel(app) {
    private val _lineData = MutableLiveData<LineData?>()
    val lineData: MutableLiveData<LineData?> = _lineData
    private val _legendEntries = ArrayList<LegendEntry>()
    val legendEntries = _legendEntries
    private val _reversedRiverChartData = MutableLiveData<List<RiverChartData>>()
    val reversedRiverChartData: LiveData<List<RiverChartData>> = _reversedRiverChartData

    fun fetchData(stockId: String) = viewModelScope.launch(IO) {
        try {
            if (isNetworkAvailable(app)) {
                val apiResult: Resource<Data> = socketRepository.getSockData(stockId)
                if (apiResult is Resource.Success) {
                    val processStockData = processStockData(apiResult.data!!)
                    _lineData.postValue(processStockData)
                }
            } else {
                Log.i("SocketViewModel", "Error: Internet is not available")
            }
        } catch (e: Exception) {
            Log.e("SocketViewModel", "Error: ${e.message}")
        }
    }

    private fun isNetworkAvailable(context: Context?) : Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun processStockData(response: Data): LineData {
        val reversedRiverChartData : List<RiverChartData>
        val stockData = response.data[0]
        val peRatioBenchmark = stockData.peRatioBenchmark.mapNotNull { it.toFloatOrNull() }
        reversedRiverChartData = stockData.riverChartData.reversed()

        // 更新 LiveData 的值
        _reversedRiverChartData.postValue(reversedRiverChartData)

        val lineDataSets: ArrayList<ILineDataSet> = ArrayList()

        val colors = listOf(
            Color.GREEN, Color.BLUE, Color.GREEN,
            Color.YELLOW, Color.rgb(255, 165, 0), Color.RED
        )

        var previousDataSet: LineDataSet? = null


        for (i in peRatioBenchmark.indices) {
            val peRatio = peRatioBenchmark[i]
            val standardPrice =
                reversedRiverChartData[0].peRatioPriceBenchmark[i].toFloatOrNull() ?: continue
            val entries = ArrayList<Entry>()

            for ((index, riverChart) in reversedRiverChartData.withIndex()) {
                val yValue = peRatio * riverChart.epsLastFourSeasons.toFloatOrNull()!! ?: continue
                entries.add(Entry(index.toFloat(), yValue))
            }

            val lineDataSet = LineDataSet(entries, "${peRatio}倍 $standardPrice")
            lineDataSet.color = Color.BLACK
            lineDataSet.fillColor = colors[i]
            lineDataSet.fillFormatter = MyFillFormatter(previousDataSet)
            lineDataSet.setDrawFilled(true)
            lineDataSet.setDrawCircles(false)

            // Create a LegendEntry for each dataSet and add it to legendEntries
            val legendEntry = LegendEntry()
            legendEntry.label = lineDataSet.label
            legendEntry.formColor = lineDataSet.fillColor
            _legendEntries.add(legendEntry)

            lineDataSets.add(lineDataSet)
            previousDataSet = lineDataSet
        }

        // Adding the red average price line
        val avgPriceEntries = reversedRiverChartData.mapIndexed { index, riverChart ->
            Entry(index.toFloat(), riverChart.averagePrice.toFloatOrNull() ?: 0f)
        }
        val avgPriceDataSet = LineDataSet(avgPriceEntries, "最新月份股價")
        avgPriceDataSet.color = Color.RED
        avgPriceDataSet.setDrawCircles(false)

        lineDataSets.add(avgPriceDataSet)

        return LineData(lineDataSets)
    }
}