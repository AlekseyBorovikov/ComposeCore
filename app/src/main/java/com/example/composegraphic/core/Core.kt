package com.example.composegraphic

import Figure
import Point3D
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Matrix
import com.example.composegraphic.core.*

// Контейнер для всех преобразований
data class Instance(
    internal var position: Point3D = Point3D(0f, 0f, 0f), // позиция
    internal var scale: Float = 1f, // масштаб
    internal val roll: MutableState<Float> = mutableStateOf(0f), // поворот относительно X
    internal val pitch: MutableState<Float> = mutableStateOf(0f), // поворот относительно Z
    internal val yaw: MutableState<Float> = mutableStateOf(0f), // поворот относительно Y
)

/**
 * КАМЕРА
 * Экзампляр камеры
 * Содержит экземпляр всех преобразований камеры
 */
class Camera(){
    // экземпляр для всех преобразований
    private var instance: Instance = Instance()

    // поворот по оси X
    var roll: Float
        get() = instance.roll.value
        set(value) {
            this.instance.roll.value = value
            //пересчитать матрицу поворота и преобразований
            calculateRotationMatrix()
            calculateTransformationMatrix()
        }

    // поворот по оси Y
    var yaw: Float
        get() = instance.yaw.value
        set(value) {
            this.instance.yaw.value = value
            //пересчитать матрицу поворота и преобразований
            calculateRotationMatrix()
            calculateTransformationMatrix()
        }

    // поворот по оси Z
    var pitch: Float
        get() = instance.pitch.value
        set(value) {
            this.instance.pitch.value = value
            //пересчитать матрицу поворота и преобразований
            calculateRotationMatrix()
            calculateTransformationMatrix()
        }

    // Матрица смещения
    private val transitionMatrix: Matrix = Matrix()
    // Матрица поворота
    private var rotationMatrix: Matrix = Matrix()
    // Общая матрица преобразований
    var transformationMatrix: Matrix = Matrix()
        private set

    constructor(
        position: Point3D = Point3D(0f, 0f, 0f),
        scale: Float = 1f,
        roll: Float = 0f,
        yaw: Float = 0f,
        pitch: Float = 0f,
    ) : this() {
        instance.position = position
        instance.scale = scale
        instance.roll.value = roll
        instance.yaw.value = yaw
        instance.pitch.value = pitch
        // расчитать начальные матрицы
        calculateRotationMatrix()
        calculateTransitionMatrix()
        calculateTransformationMatrix()
    }

    /**
     * Вычисляет матрицу поворота камеры
     * @return матрица пооворота
     */
    private fun calculateRotationMatrix(){
        rotationMatrix = Matrix()
        rotationMatrix.timesAssign(
            createRotationMatrix(
                Math.toRadians(instance.roll.value.toDouble()),
                Math.toRadians(instance.yaw.value.toDouble()),
                Math.toRadians(instance.pitch.value.toDouble()),
            )
        )
        rotationMatrix.invert()
    }

    /**
     * Вычисляет матрицу поворота камеры
     * @return матрица поворота
     */
    private fun calculateTransitionMatrix(){
        transitionMatrix[0,3] = -instance.position.x
        transitionMatrix[1,3] = -instance.position.y
        transitionMatrix[2,3] = -instance.position.z
    }

    /**
     * Вычисляет матрицу преобразований камеры
     * @return матрица преобразований
     */
    private fun calculateTransformationMatrix(){
        transformationMatrix = Matrix()
        transformationMatrix.timesAssign(rotationMatrix)
        transformationMatrix.timesAssign(transitionMatrix)
    }
}

/**
 * ЭКРАН
 * содержит данные о физическом экране устройства и о сцене
 */
class Screen(
    val world: World,
){
    // Ширина устройства
    private var width: Float = 0f
    // Высота устройства
    private var height: Float = 0f
    // Матрица приведения к экранным координатам
    var viewPortMatrix: Matrix = Matrix()

    fun setSize(width: Float, height: Float){
        this.width = width
        this.height = height

        // инициализация начальной матрицы приведения к экранным координатам
        viewPortMatrix = calculateViewPortToCanvasMatrix()
    }

    /**
     * Вычисляет матрицу приведения к экранным координатам
     * @return матрица приведения к экранным координатам
     */
    private fun calculateViewPortToCanvasMatrix(): Matrix {
        val scale: Float
        val translationX: Float // смещение по X
        val translationY: Float // смещение по Y

        if (this.height > this.width) {
            scale = width
            translationX = 0f
            translationY = (this.height - this.width) / 2
        }
        else {
            scale = this.height
            translationY = (this.width - this.height) / 2
            translationX = 0f
        }

        // формула x = (x + 0.5) * scale / windowWidth + translationX
        // формула y = (y + 0.5) * scale / windowHeight + translationY
        val viewPortMatrix = Matrix()
        viewPortMatrix[0,0] = scale / this.world.windowWidth + translationX
        viewPortMatrix[0,3] = 0.5f * scale / this.world.windowWidth + translationX
        viewPortMatrix[1,1] = scale / this.world.windowHeight + translationY
        viewPortMatrix[1,3] = 0.5f * scale / this.world.windowHeight + translationY
        return viewPortMatrix
    }
}

/**
 * МИР ИЛИ СЦЕНА
 * содержит данные о сцене
 */
data class World(
    val XMax: Float, // Максимальное расстояние по X
    val YMax: Float, // Максимальное расстояние по Y
    val ZMax: Float, // Максимальное расстояние по Z
    val windowHeight: Float, // Высота окна
    val windowWidth: Float, // Ширина окна
    var distanceToWindow: MutableState<Float>, // Расстояние до окна
    val figures: MutableList<Figure> = mutableListOf(), // Список фигур
)
