package com.example.pedemo

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class MyFillFormatter(private val boundaryDataSet: ILineDataSet? = null) : IFillFormatter {

    override fun getFillLinePosition(dataSet: ILineDataSet?, dataProvider: LineDataProvider?): Float {
        return 0f
    }

    fun getFillLineBoundary(): List<Entry>? {
        return boundaryDataSet?.let { (it as LineDataSet).values }
    }
}
