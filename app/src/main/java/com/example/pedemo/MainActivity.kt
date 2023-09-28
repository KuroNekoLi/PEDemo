package com.example.pedemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pedemo.data.RetrofitInstance
import com.example.pedemo.data.api.SocketService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lineChart = findViewById<LineChart>(R.id.lineChart)

        CoroutineScope(Dispatchers.IO).launch {
            val apiService =
                RetrofitInstance.getRetrofitInstance().create(SocketService::class.java)
            val response = apiService.getStockInfo("2330")
            if (response.isSuccessful) {
                val stockData = response.body()?.data?.get(0) ?: return@launch
                val peRatioBenchmark = stockData.peRatioBenchmark.mapNotNull { it.toFloatOrNull() }
                val reversedRiverChartData = stockData.riverChartData.reversed()

                val lineDataSets: ArrayList<ILineDataSet> = ArrayList()

                val colors = listOf(
                    Color.GREEN, Color.BLUE, Color.GREEN,
                    Color.YELLOW,  Color.rgb(255, 165, 0), Color.RED
                )

                var previousDataSet: LineDataSet? = null

                // Declare legendEntries outside of the loop
                val legendEntries = ArrayList<LegendEntry>()

                for (i in peRatioBenchmark.indices) {
                    val peRatio = peRatioBenchmark[i]
                    val standardPrice = reversedRiverChartData[0].peRatioPriceBenchmark[i].toFloatOrNull() ?: continue
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
                    legendEntries.add(legendEntry)

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

                val lineData = LineData(lineDataSets)

                withContext(Dispatchers.Main) {
                    lineChart.renderer = MyLineLegendRenderer(lineChart, lineChart.animator, lineChart.viewPortHandler)
                    lineChart.data = lineData
                    lineChart.axisRight.isEnabled = true
                    lineChart.axisLeft.isEnabled = false
                    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    lineChart.xAxis.labelCount = reversedRiverChartData.size / 10 + 1
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(reversedRiverChartData.map { it.yearMonth })
                    lineChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

                    // Setting the custom legend entries
                    val legend = lineChart.legend
                    legend.setCustom(legendEntries)
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.isWordWrapEnabled = true
                    legend.xEntrySpace = 10f
                    legend.yEntrySpace = 5f
                    legend.maxSizePercent = 0.5f

                    lineChart.invalidate()
                }
            } else {
                Log.i("LinLi", "Error: ${response.errorBody()}")
            }
        }
    }
}
