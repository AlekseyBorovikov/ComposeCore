package com.example.composegraphic.Lab2

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composegraphic.Lab1.*
import com.example.composegraphic.core.createRotationMatrix
import com.example.composegraphic.core.hsvToRgb
import com.example.composegraphic.core.rgbToHsv

private const val XMax = 1000f
private const val YMax = 1000f
private const val ZMax = 1000f
private const val EX = 0.5f
private const val EY = 0.5f
private var heightScale: Float = 1f
private var widthScale: Float = 1f
var d = mutableStateOf(0.5f)
    internal set

data class Object(
    val X: Float,
    val Y: Float,
    val Z: Float,
    var color: Color = Color.DarkGray
)

data class ScreenStar(
    val center: Offset,
    var color: Color
)

var globalRotateX = mutableStateOf(0f)
var globalRotateY = mutableStateOf(0f)
var globalRotateZ = mutableStateOf(0f)

private val starts = mutableListOf(
    Object(10F, 0F, 0F, Color.Yellow),
    Object(0F, 950F, 0F, Color.Red),
    Object(-465F, 25F, 725F, Color.Cyan),
)
private val cameraPosition = mutableStateOf(Object(0f, 0f, 0f))

@Composable
fun SceneCanvas(
    modifier: Modifier = Modifier
){
//    val widthCanvasDp = with(LocalDensity.current) { XMax.toDp() }
//    val heightCanvasDp = with(LocalDensity.current) { YMax.toDp() }
//    var distance by remember{ d }
    var distance by remember{ d }
    var rotateX by remember{ globalRotateX }
    var rotateY by remember{ globalRotateY }
    var rotateZ by remember{ globalRotateZ }

    ConstraintLayout(Modifier.fillMaxWidth()) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    heightScale = size.height.toFloat()
                    widthScale = size.width.toFloat()
                }
                .background(Color.LightGray),
            onDraw = {
                val R = 10f
                starts.forEach {
                    val screenStar = centerStar(it, rotateX, rotateY, rotateZ, distance)
                    if (screenStar != null)
                        drawBresenhamCycle(
                            this,
                            center = screenStar.center,
                            R = R, color = screenStar.color
                        )
                }
            }
        )
        val (controlButtons, viewPanel, coordPanel) = createRefs()
        ControlButtons(
            Modifier.constrainAs(controlButtons){
                end.linkTo(parent.end, 10.dp)
                top.linkTo(parent.top, 10.dp)
            }
        )
        ControlXYZ(
            Modifier.constrainAs(coordPanel) {
                start.linkTo(parent.start, 10.dp)
                bottom.linkTo(viewPanel.top, 5.dp)
            }
        )
        PrintXYZ(
            rotateX, rotateY, rotateZ, Modifier.constrainAs(viewPanel) {
                bottom.linkTo(parent.bottom, 5.dp)
                end.linkTo(parent.end, 5.dp)
            }
        )
    }
}

@Composable
private fun ControlButtons(modifier: Modifier = Modifier){
    Column(modifier){
        val step = 1f
        IconButton(onClick = {  }) {
            Icon(Icons.Filled.Add, "add star")
        }
        IconButton(onClick = { if (d.value + step <= XMax/2) d.value += step }) {
            Icon(Icons.Filled.ZoomIn, "zoom in")
        }
        IconButton(onClick = { if (d.value - step >= 0.5f) d.value -= step }) {
            Icon(Icons.Filled.ZoomOut, "zoom out")
        }
    }
}

@Composable
private fun PrintXYZ(rotateX: Float, rotateY: Float, rotateZ: Float, modifier: Modifier = Modifier){
    Row(modifier){
        Text(
            String.format("X: %.2f", rotateX),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = Color.Red,
            modifier = Modifier.padding(start = 5.dp)
        )
        Text(
            String.format("Y: %.2f", rotateY),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = Color.Blue,
            modifier = Modifier.padding(start = 5.dp)
        )
        Text(
            String.format("Z: %.2f", rotateZ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = Color.Black,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
private fun ControlXYZ(modifier: Modifier = Modifier){
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val sizeDp = if (maxWidth > maxHeight) maxHeight - 20.dp else maxWidth - 20.dp
        Column(modifier.fillMaxWidth().padding(top = 5.dp)) {
            Slider(
                value = globalRotateX.value,
                onValueChange = { globalRotateX.value = it },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red,
                    activeTrackColor = Color.Red
                ),
                modifier = Modifier.width(sizeDp)
            )
            Slider(
                value = globalRotateY.value,
                onValueChange = { globalRotateY.value = it },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Blue,
                    activeTrackColor = Color.Blue
                ),
                modifier = Modifier.width(sizeDp)
            )
            Slider(
                value = globalRotateZ.value,
                onValueChange = { globalRotateZ.value = it },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black
                ),
                modifier = Modifier.width(sizeDp)
            )
        }
    }
}

private fun centerStar(star: Object, rotateX: Float, rotateY: Float, rotateZ: Float, d: Float): ScreenStar?{
    val matrix = createRotationMatrix(
        Math.toRadians(rotateX.toDouble()),
        Math.toRadians(rotateY.toDouble()),
        Math.toRadians(rotateZ.toDouble()),
    )
    // поворот координат звезды в НСК
    val xn = matrix[0, 0] * star.X + matrix[0, 1] * star.Y + matrix[0, 2] * star.Z
    //Отсечение по плоскости экрана
    if (xn < d) return null
    val yn = matrix[1, 0] * star.X + matrix[1, 1] * star.Y + matrix[1, 2] * star.Z
    val zn = matrix[2, 0] * star.X + matrix[2, 1] * star.Y + matrix[2, 2] * star.Z
    // проекция на экран (d = 0.5)
    val screenX = zn/(xn/d + 1)
    val screenY = yn/(xn/d + 1)
    // отсечение по границам окна
    if ((-EX > screenX || screenX > EX) || (-EY > screenY || screenY > EY)) return null
    // расчет физических экранных координат
    val point = viewPort(Offset(screenX, screenY))
    // вычисление цвета
    val hsvColor = rgbToHsv(star.color.red, star.color.green, star.color.blue)
    val valueColor = (XMax - xn-d)/10
    val rgbColor = hsvToRgb(hsvColor.hue, hsvColor.saturation, valueColor)

    return ScreenStar(point, Color(rgbColor.red, rgbColor.green, rgbColor.blue))
}

private fun viewPort(point: Offset): Offset{
    val x = (point.x + 1F / 2) * widthScale
    val y = (point.y + 1F / 2) * heightScale
    return Offset(x, y)
}

