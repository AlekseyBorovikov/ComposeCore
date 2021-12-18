package com.example.composegraphic.Lab1

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity

private const val variantR = 4F
private const val width = 300F
private const val height = 300F
private const val scale = 2.5F

@Composable
fun CycleCanvas(
    modifier: Modifier = Modifier
){
    val widthCanvasDp = with(LocalDensity.current) {
        (width * scale).toDp()
    }
    val heightCanvasDp = with(LocalDensity.current) {
        (height * scale).toDp()
    }
    Canvas(
        modifier = modifier
            .size(widthCanvasDp, heightCanvasDp)
            .background(Color.LightGray),
        onDraw = {
            drawBresenhamCycle(
                this,
                center = Offset((width * scale)/2, (height * scale)/1.5F),
                R = 10 * (variantR + 9),
                color = Color.Blue
            )
            drawBresenhamCycle(
                this,
                center = Offset((width * scale)/3.5F, (height * scale)/2.2F),
                R = 3 * (variantR + 9),
                color = Color.Green
            )
            drawBresenhamCycle(
                this,
                center = Offset((width * scale)/1.5F, (height * scale)/3),
                R = 5 * (variantR + 9),
                color = Color.Red
            )
        }
    )
}

fun drawBresenhamCycle(drawScope: DrawScope, center: Offset, R: Float, color: Color = Color.Black){
    drawScope.drawPoints(
        generateBresenhamCyclePoints(center, R),
        pointMode = PointMode.Polygon,
        color = color
    )
}

fun generateBresenhamCyclePoints(center: Offset, R: Float): List<Offset>{
    val resultPointsList = mutableListOf(center)
    var x = 0F
    var y = R
    var delta = 1 - 2 * R
    var error = 0F
    while (y >= 0){
        resultPointsList.add(Offset(center.x + x, center.y + y))
        resultPointsList.add(Offset(center.x + x, center.y - y))
        resultPointsList.add(Offset(center.x - x, center.y + y))
        resultPointsList.add(Offset(center.x - x, center.y - y))
        error = 2 * (delta + y) - 1
        if(delta < 0 && error <= 0){
            ++x; delta += 2 * x + 1
            continue
        }
        error = 2 * (delta - x) - 1
        if(delta > 0 && error > 0){
            --y; delta += 1 - 2 * y
            continue
        }
        ++x; delta += 2 * (x - y); --y
    }
    return resultPointsList
}