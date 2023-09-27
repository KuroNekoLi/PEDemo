package com.example.pedemo

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.Math.min

class MyLineLegendRenderer(
    chart: LineDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : LineChartRenderer(chart, animator, viewPortHandler) {

    override fun drawLinearFill(c: Canvas?, dataSet: ILineDataSet?, trans: Transformer?, bounds: XBounds?) {
        val filled = mGenerateFilledPathBuffer

        val startingIndex = bounds?.min ?: 0
        val endingIndex = bounds?.range ?: 0 + startingIndex
        val indexInterval = 128

        var currentStartIndex: Int
        var currentEndIndex: Int
        var iterations = 0

        do {
            currentStartIndex = startingIndex + iterations * indexInterval
            currentEndIndex = currentStartIndex + indexInterval
            currentEndIndex = min(currentEndIndex, endingIndex)

            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet!!, currentStartIndex, currentEndIndex, filled)
                trans?.pathValueToPixel(filled)
                val drawable = dataSet.fillDrawable
                if (drawable != null) {
                    drawFilledPath(c, filled, drawable)
                } else {
                    drawFilledPath(c, filled, dataSet.fillColor, dataSet.fillAlpha)
                }
            }
            iterations++
        } while (currentStartIndex <= currentEndIndex)
    }

    private fun generateFilledPath(dataSet: ILineDataSet, startIndex: Int, endIndex: Int, outputPath: Path) {
        val boundaryEntry = (dataSet.fillFormatter as? MyFillFormatter)?.getFillLineBoundary() ?: return

        val phaseY = mAnimator.phaseY
        val filled = outputPath
        filled.reset()

        val entry = dataSet.getEntryForIndex(startIndex)

        filled.moveTo(entry.x, boundaryEntry[0].y)
        filled.lineTo(entry.x, entry.y * phaseY)

        var currentEntry: Entry? = null
        for (x in startIndex + 1..endIndex) {
            currentEntry = dataSet.getEntryForIndex(x)
            filled.lineTo(currentEntry.x, currentEntry.y * phaseY)
        }

        if (currentEntry != null) {
            filled.lineTo(currentEntry.x, boundaryEntry[endIndex].y)
        }

        for (x in endIndex downTo startIndex + 1) {
            val previousEntry = boundaryEntry[x]
            filled.lineTo(previousEntry.x, previousEntry.y * phaseY)
        }

        filled.close()
    }
}