package com.example.pedemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pedemo.data.RetrofitInstance
import com.example.pedemo.data.api.SocketService
import com.example.pedemo.data.util.MyFillFormatter
import com.example.pedemo.data.util.MyLineLegendRenderer
import com.example.pedemo.databinding.ActivityMainBinding
import com.example.pedemo.presentation.viewmodel.SocketViewModel
import com.example.pedemo.presentation.viewmodel.SocketViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: SocketViewModelFactory
    private lateinit var viewModel: SocketViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        viewModel = ViewModelProvider(this,factory)[SocketViewModel::class.java]
        viewModel.fetchData("2330")
        viewModel.lineData.observe(this){
            val legendEntries = viewModel.legendEntries

            lineChart.renderer = MyLineLegendRenderer(lineChart, lineChart.animator, lineChart.viewPortHandler)
            lineChart.data = it
            lineChart.axisRight.isEnabled = true
            lineChart.axisLeft.isEnabled = false
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            viewModel.reversedRiverChartData.observe(this) { reversedRiverChartData ->
                lineChart.xAxis.labelCount = reversedRiverChartData.size / 10 + 1
                lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(reversedRiverChartData.map { it.yearMonth })
            }
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
    }
}