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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val zoomScale = 5F
private const val widthLetter = 50F
private const val heightLetter = 80F
private const val paddingLetter = 5F

@Composable
fun LettersCanvas(
    modifier: Modifier = Modifier
){
    val widthCanvasDp = with(LocalDensity.current) {
        (widthLetter * 3 * zoomScale).toDp()
    }
    val heightCanvasDp = with(LocalDensity.current) {
        (heightLetter * zoomScale).toDp()
    }
    Canvas(
        modifier = modifier
            .size(widthCanvasDp, heightCanvasDp)
            .background(Color.LightGray),
        onDraw = {
            printFirstLetterLastName(this, scale = zoomScale)
            printLastLetterFirstName(this, offsetX = widthLetter * 2 * zoomScale, scale = zoomScale)
        }
    )
}

fun printFirstLetterLastName(
    drawScope: DrawScope,
    offsetX: Float = 0F,
    offsetY: Float = 0F,
    scale: Float = 1F,
    rotate: Float = 0F,
){
    val startX = (0F + paddingLetter) * scale
    val endX = (widthLetter - paddingLetter) * scale
    val topY = (0F + paddingLetter) * scale
    val bottomY = (heightLetter - paddingLetter) * scale
    var lastPoint: Offset
    with(drawScope){
        lastPoint = drawLukaLine(
            this,
            point0 = rotation(Offset(endX, topY), rotate, offsetX, offsetY),
            point1 = rotation(Offset(startX, topY), rotate, offsetX, offsetY)
        )
        lastPoint = drawLukaLine(
            this,
            point0 = lastPoint,
            point1 = rotation(Offset(startX, bottomY), rotate, offsetX, offsetY)
        )
        lastPoint = drawLukaLine(
            this,
            point0 = lastPoint,
            point1 = rotation(Offset(endX, bottomY), rotate, offsetX, offsetY)
        )
        lastPoint = drawLukaLine(
            this,
            point0 = lastPoint,
            point1 = rotation(Offset(endX, bottomY / 2), rotate, offsetX, offsetY)
        )
        lastPoint = drawLukaLine(
            this,
            point0 = lastPoint,
            point1 = rotation(Offset(startX, bottomY / 2), rotate, offsetX, offsetY)
        )
    }
}

fun rotation(point: Offset, rotate: Float = 0F, offsetX: Float = 0F, offsetY: Float = 0F): Offset{
    return Offset(
        x = point.x * cos(rotate) - point.y * sin(rotate) + offsetX,
        y = point.x * sin(rotate) + point.y * cos(rotate) + offsetY,
    )
}

fun printLastLetterFirstName(
    drawScope: DrawScope,
    offsetX: Float = 0F,
    offsetY: Float = 0F,
    scale: Float = 1F,
    rotate: Float = 0F
){
    val heightHead = 5 * scale
    val startX = (0F + paddingLetter) * scale
    val endX = (widthLetter - paddingLetter) * scale
    val topY = (0F + paddingLetter) * scale
    val bottomY = (heightLetter - paddingLetter) * scale
    var lastPoint: Offset
    with(drawScope){
        //print letter
        lastPoint = drawBresenhamLine(
            this,
            point0 = rotation(Offset(startX, topY + heightHead), rotate, offsetX, offsetY),
            point1 = rotation(Offset(startX, bottomY), rotate, offsetX, offsetY)
        )
        lastPoint = drawBresenhamLine(
            this, point0 = lastPoint,
            point1 = rotation(Offset(endX, topY + heightHead), rotate, offsetX, offsetY)
        )
        lastPoint = drawBresenhamLine(
            this, point0 = lastPoint,
            point1 = rotation(Offset(endX, bottomY), rotate, offsetX, offsetY)
        )

        //draw head letter
        lastPoint = drawBresenhamLine(
            this,
            point0 = rotation(Offset(startX + heightHead, topY), rotate, offsetX, offsetY),
            point1 = rotation(Offset(endX / 2, topY + heightHead), rotate, offsetX, offsetY)
        )
        lastPoint = drawBresenhamLine(
            this, point0 = lastPoint,
            point1 = rotation(Offset(endX - heightHead, topY), rotate, offsetX, offsetY)
        )
    }
}

fun drawLukaLine(
    drawScope: DrawScope,
    point0: Offset,
    point1: Offset
): Offset {
    drawScope.drawPoints(
        points = generateLukaLinePoints(point0, point1),
        pointMode = PointMode.Polygon,
        color = Color.Black,
        strokeWidth = 2f
    )
    return point1
}

fun generateLukaLinePoints(point0: Offset, point1: Offset): List<Offset>{
    val resultListPoint = mutableListOf(Offset(point0.x, point0.y))
    var x = point0.x
    var y = point0.y
    var cumul: Float
    val Xinc = if(point0.x < point1.x) 1 else -1
    val Yinc = if(point0.y < point1.y) 1 else -1
    val Dx = abs(point0.x - point1.x)
    val Dy = abs(point0.y - point1.y)
    if(Dx > Dy){
        cumul = Dx/2
        for (i in (0 until Dx.toInt())){
            x += Xinc
            cumul += Dy
            if(cumul >= Dx){
                cumul -= Dx
                y += Yinc
            }
            resultListPoint.add(Offset(x, y))
        }
    }
    else{
        cumul = Dy/2
        for (i in (0 until Dy.toInt())){
            y += Yinc
            cumul += Dx
            if(cumul >= Dy){
                cumul -= Dy
                x += Xinc
            }
            resultListPoint.add(Offset(x, y))
        }
    }
    return resultListPoint
}

fun drawBresenhamLine(
    drawScope: DrawScope,
    point0: Offset,
    point1: Offset,
    color: Color = Color.Black,
): Offset {
    drawScope.drawPoints(
        points = generateBresenhamLinePoints(point0, point1),
        pointMode = PointMode.Polygon,
        color = color,
        strokeWidth = 2f
    )
    return point1
}

fun generateBresenhamLinePoints(point0: Offset, point1: Offset): List<Offset>{
    val resultListPoint = mutableListOf(Offset(point0.x, point0.y))
    val Xinc = if(point0.x < point1.x) 1 else -1
    val Yinc = if(point0.y < point1.y) 1 else -1
    val Dx = abs(point0.x - point1.x)
    val Dy = abs(point0.y - point1.y)
    val Dx2 = Dx * 2
    val Dy2 = Dy * 2
    var x = point0.x
    var y = point0.y
    var S: Float
    var Dxy: Float

    if(Dx > Dy){
        S = Dy2 - Dx
        Dxy = Dy2 - Dx2
        for (i in (0 until Dx.toInt())){
            if(S >= 0){
                y += Yinc
                S += Dxy
            } else S += Dy2
            x += Xinc
            resultListPoint.add(Offset(x, y))
        }
    }
    else{
        S = Dx2 - Dy
        Dxy = Dx2 - Dy2
        for (i in (0 until Dy.toInt())){
            if(S >= 0){
                x += Xinc
                S += Dxy
            } else S += Dx2
            y += Yinc
            resultListPoint.add(Offset(x, y))
        }
    }
    return resultListPoint
}