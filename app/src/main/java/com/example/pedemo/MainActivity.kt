package com.example.pedemo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pedemo.data.RetrofitInstance
import com.example.pedemo.data.api.SocketService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
                Log.i("LinLi", response.body().toString());

            } else {
                Log.i("LinLi", "Error: ");
            }
        }


        // 定義每個月的EPS數據
        val epsData = mapOf(
            1f to 18f,
            2f to 18.5f,
            3f to 19f,
            4f to 19.5f,
            5f to 20f,
            6f to 20.5f,
            7f to 20.25f
        )

        // 定義不同本益比
        val peRatios = listOf(15f, 17.8f, 20.6f, 23.4f, 26.2f, 29f)

        val fillColors = listOf(
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.DKGRAY,
            Color.MAGENTA
        )

        val dataSets = mutableListOf<LineDataSet>()
        var previousSet: LineDataSet? = null

        for (index in peRatios.indices) {
            val peRatio = peRatios[index]
            val entries = epsData.map { (month, eps) ->
                Entry(month, eps * peRatio)
            }

            val set = LineDataSet(entries, "$peRatio 倍本益比")
            set.color = Color.BLACK
            set.lineWidth = 2f

            if (previousSet != null) {
                previousSet.setDrawFilled(true)
                previousSet.fillColor = fillColors[index - 1]
                previousSet.fillFormatter = MyFillFormatter(set)
            }

            set.setDrawCircleHole(false)
            dataSets.add(set)

            // Update the previousSet variable
            previousSet = set
        }


        val data = LineData(dataSets as List<ILineDataSet>?)

        lineChart.data = data
        lineChart.renderer = MyLineLegendRenderer(lineChart, lineChart.animator, lineChart.viewPortHandler)

        lineChart.invalidate()
    }
}

