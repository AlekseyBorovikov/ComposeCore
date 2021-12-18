package com.example.composegraphic.core

import Model2D
import Model3D
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.abs

/**
 * АЛГОРИТМ ОТРИСОВКИ ЛИНИИ
 *  - LineType.LUKA - алгоритм Лука,
 *  - LineType.BRESENHAM - алгоритм Брезентхема.
 */
enum class LineType {LUKA, BRESENHAM}

/**
 * ОТРИСОВКА МОДЕЛИ
 * рисует модель по граням одним из алгоритмов
 * @param: пространство отрисовки, моель
 */
fun draw(
    drawScope: DrawScope,
    model: Model2D
){
    if (model.vertexes.isNotEmpty())
        model.poligons.forEach { poligon ->
            poligon.getEdges().forEach { edge ->
                draw(
                    drawScope,
                    point0 = model.vertexes[edge.firstVertex],
                    point1 = model.vertexes[edge.secondVertex],
                    color = edge.color
                )
            }
        }
}

/**
 * ОТРИСОВКА ЗАКРАШЕННОЙ МОДЕЛИ
 * рисует модель по граням одним из алгоритмов
 * @param: пространство отрисовки, модель
 */
fun drawFilled(
    drawScope: DrawScope,
    model: Model2D
){
    if (model.vertexes.isNotEmpty()) {
        val vertexes = model.vertexes
        val poligins = model.poligons
        for(i in poligins.indices) {
            val poligon = poligins[i]
            // Сортировка точек так, что y0 <= y1 <= y2
            val vertexesIds = poligon.vortexesNums.toMutableList()
            var b: Int
            if (vertexes[vertexesIds[1]].y < vertexes[vertexesIds[0]].y) {
                b = vertexesIds[1]
                vertexesIds[1] = vertexesIds[0]
                vertexesIds[0] = b
            }
            if (vertexes[vertexesIds[2]].y < vertexes[vertexesIds[0]].y) {
                b = vertexesIds[2]
                vertexesIds[2] = vertexesIds[0]
                vertexesIds[0] = b
            }
            if (vertexes[vertexesIds[2]].y < vertexes[vertexesIds[1]].y) {
                b = vertexesIds[2]
                vertexesIds[2] = vertexesIds[1]
                vertexesIds[1] = b
            }

            Log.d("poligon $i", "${vertexesIds}")
            // Вычисление координат x рёбер треугольника
            val x01 = Interpolate(
                vertexes[vertexesIds[0]].y,
                vertexes[vertexesIds[0]].x,
                vertexes[vertexesIds[1]].y,
                vertexes[vertexesIds[1]].x,
            ).toMutableList()
            val x12 = Interpolate(
                vertexes[vertexesIds[1]].y,
                vertexes[vertexesIds[1]].x,
                vertexes[vertexesIds[2]].y,
                vertexes[vertexesIds[2]].x,
            )
            val x02 = Interpolate(
                vertexes[vertexesIds[0]].y,
                vertexes[vertexesIds[0]].x,
                vertexes[vertexesIds[2]].y,
                vertexes[vertexesIds[2]].x,
            )

            // Конкатенация коротких сторон
            x01.remove(x01[x01.lastIndex])
            val x012 = x01 + x12

            // Определяем, какая из сторон левая и правая
            val m = x012.size / 2
            val x_right: List<Float>
            val x_left: List<Float>
            if (x02[m] < x012[m]) {
                x_left = x02
                x_right = x012
            } else {
                x_left = x012
                x_right = x02
            }

            // Отрисовка горизонтальных отрезков
            val y0 = vertexes[vertexesIds[0]].y
            val y2 = vertexes[vertexesIds[2]].y
            val yList = y0.toInt()..y2.toInt()
            for (y in yList) {
//                drawScope.drawIntoCanvas {
//                    it.nativeCanvas.drawPoint()
//                }
                drawScope.drawLine(
                    poligon.color,
                    start = Offset(x_left[(y - y0).toInt()], y.toFloat()),
                    end = Offset(x_right[(y - y0).toInt()], y.toFloat()),
                    strokeWidth = 2f,
                )
            }
        }
    }
}

/**
 * ОТРИСОВКА ЛИНИИ
 * рисует линии одним из алгоритмов
 * @param:
 *      drawScope - пространство отрисовки,
 *      point0 - первая тоска,
 *      point1 - вторая точка,
 *      color - цвет,
 *      lineType - алгоритм
 */
fun draw(
    drawScope: DrawScope,
    point0: Offset,
    point1: Offset,
    color: Color = Color.Black,
    lineType: LineType = LineType.BRESENHAM,
): Offset {
    val points: List<Offset> = when(lineType){
        LineType.LUKA -> generateLukaLinePoints(point0, point1)
        LineType.BRESENHAM -> generateBresenhamLinePoints(point0, point1)
    }
    drawScope.drawPoints(points, PointMode.Polygon, color, strokeWidth = 2f)
    return point1
}

private fun Interpolate(i0: Float, d0: Float, i1: Float, d1: Float): List<Float> {
    if (i0 == i1) {
        return listOf(d0)
    }
    val values = mutableListOf<Float>()
    val a = (d1 - d0) / (i1 - i0)
    var d = d0
    val list = (i0.toInt()..i1.toInt())
    for (i in list) {
        values.add(d)
        d += a
    }
    return values
}

/**
 * РЕАЛИЗАЦИЯ АЛГОРИТМА ОТРИСОВКИ ЛУКИ
 * генерирует точки для отрисовки линии алгоритмом Лука
 * @param:
 *      point0 - первая тоска,
 *      point1 - вторая точка,
 */
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

/**
 * РЕАЛИЗАЦИЯ АЛГОРИТМА ОТРИСОВКИ БРЕЗЕНТХЕМА
 * генерирует точки для отрисовки линии алгоритмом БРЕЗЕНТХЕМА
 * @param:
 *      point0 - первая тоска,
 *      point1 - вторая точка,
 */
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
            try{ resultListPoint.add(Offset(x, y)) } catch (e: OutOfMemoryError){ return resultListPoint }
        }
    }
    return resultListPoint
}



/**
 * ПЕРЕВОД ЦВЕТА В ДРУГУЮ КОДИРОВКУ
 * переводит цвет из hsv в rgb
 * @param:
 *      hue - цветовой фон,
 *      saturation - насыщенность,
 *      value - яркость
 * @return: цвет в rgb
 */
fun hsvToRgb(
    hue: Float,
    saturation: Float,
    value: Float,
): RGBColor {
    val C: Float
    val X: Float
    val Y: Float
    val Z: Float
    val i: Int
    var H: Float = hue
    val S = saturation/100f
    val V = value/100f
    if (S.toInt() != 0){
        H /= 60f
        i = H.toInt()
        C = H - i

        X = V * (1 - S)
        Y = V * (1 - S * C)
        Z = V * (1 - S * (1 - C))
        when(i){
            0 -> return RGBColor((V * 255).toInt(), (Z * 255).toInt(), (X * 255).toInt())
            1 -> return RGBColor((Y * 255).toInt(), (V * 255).toInt(), (X * 255).toInt())
            2 -> return RGBColor((X * 255).toInt(), (V * 255).toInt(), (Z * 255).toInt())
            3 -> return RGBColor((X * 255).toInt(), (Y * 255).toInt(), (V * 255).toInt())
            4 -> return RGBColor((Z * 255).toInt(), (X * 255).toInt(), (V * 255).toInt())
            5 -> return RGBColor((V * 255).toInt(), (X * 255).toInt(), (Y * 255).toInt())
        }
    }
    return RGBColor((V * 255).toInt(), (V * 255).toInt(), (V * 255).toInt())
}

/**
 * ПЕРЕВОД ЦВЕТА В ДРУГУЮ КОДИРОВКУ
 * переводит цвет из rgb в hsv
 * @param:
 *      red - насыщенность красного цвета,
 *      green - насыщенность зелёного цвета,
 *      blue - насыщенность синего цвета
 * @return: цвет в hsv
 */
fun rgbToHsv(
    red: Float,
    green: Float,
    blue: Float,
): HSVColor {
    val rgbMin = if(red < green) (if(red < blue) red else blue) else (if(green < blue) green else blue)
    val rgbMax = if(red > green) (if(red > blue) red else blue) else (if(green > blue) green else blue)
    val V = rgbMax
    val delta = rgbMax - rgbMin
    if (delta < 0.00001) return HSVColor(0f, 0f, V * 100)
    val S: Float
    if( rgbMax > 0f ) {
        S = (delta / rgbMax)
    } else return HSVColor(0f, 0f, 0f)
    var H: Float
    if (red >= rgbMax) H = (green - blue) / delta
    else if (green >= rgbMax) H = 2f + (blue - red) / delta
    else H = 4f + (red - green) / delta
    H *= 60

    if(H < 0f) H += 360f
    return HSVColor(H, S * 100, V * 100)
}

//Цвет в rgb
data class RGBColor(
    val red: Int,
    val green: Int,
    val blue: Int,
)

//Цвет в hsv
data class HSVColor(
    val hue: Float,
    val saturation: Float,
    val value: Float,
)