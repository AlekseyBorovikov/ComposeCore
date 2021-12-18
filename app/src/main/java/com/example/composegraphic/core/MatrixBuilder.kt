package com.example.composegraphic.core

import Point3D
import androidx.compose.ui.graphics.Matrix
import kotlin.math.cos
import kotlin.math.sin

/**
 * ГЕНЕРАЦИЯ МАТРИЦЫ ПОВОРОТА
 * матрица поворота вокруг оси x
 * @param: угол поворота в радианах
 * @return: матрица поворота вокруг x
 */
fun getMatrixXY(pitch: Double): Matrix {
    val matrixXY = Matrix()
    matrixXY[0, 0] = cos(pitch).toFloat()
    matrixXY[0, 1] = (-sin(pitch)).toFloat()
    matrixXY[1, 0] = sin(pitch).toFloat()
    matrixXY[1, 1] = cos(pitch).toFloat()
    return matrixXY
}

/**
 * ГЕНЕРАЦИЯ МАТРИЦЫ ПОВОРОТА
 * матрица поворота вокруг оси y
 * @param: угол поворота в радианах
 * @return: матрица поворота вокруг y
 */
fun getMatrixXZ(yaw: Double): Matrix {
    val matrixXZ = Matrix()
    matrixXZ[0, 0] = cos(yaw).toFloat()
    matrixXZ[0, 2] = sin(yaw).toFloat()
    matrixXZ[2, 0] = (-sin(yaw)).toFloat()
    matrixXZ[2, 2] = cos(yaw).toFloat()
    return matrixXZ
}

/**
 * ГЕНЕРАЦИЯ МАТРИЦЫ ПОВОРОТА
 * матрица поворота вокруг оси z
 * @param: угол поворота в радианах
 * @return: матрица поворота вокруг z
 */
fun getMatrixYZ(roll: Double): Matrix {
    val matrixYZ = Matrix()
    matrixYZ[1, 1] = cos(roll).toFloat()
    matrixYZ[1, 2] = (-sin(roll)).toFloat()
    matrixYZ[2, 1] = sin(roll).toFloat()
    matrixYZ[2, 2] = cos(roll).toFloat()
    return matrixYZ
}

/**
 * ГЕНЕРАЦИЯ ОБЩЕЙ МАТРИЦЫ ПОВОРОТА
 * матрица поворота вокруг трёх осей
 * @param: уголы поворота в радианах
 * @return: общая матрица поворота
 */
fun createRotationMatrix(roll: Double, yaw: Double, pitch: Double): Matrix {
    val matrix = Matrix()
    matrix.timesAssign(getMatrixXZ(yaw))
    matrix.timesAssign(getMatrixXY(pitch))
    matrix.timesAssign(getMatrixYZ(roll))
    return matrix
}

/**
 * ГЕНЕРАЦИЯ МАТРИЦЫ ПРОЕЦИРОВАНИЯ И ПЕРЕВОДА В ЭКРАННЫЕ КООРДИНАТЫ
 * @param: координата z, расстояние до окна, матрица перевода в экранные координаты
 * @return: матрица проецирования и перевода в экранные координаты
 */
fun getProjectAndVPMatrix(z: Float, d: Float, viewPortMatrix: Matrix): Matrix {
    val portMatrix = Matrix(
        floatArrayOf(
            viewPortMatrix[0, 0], viewPortMatrix[0, 1], viewPortMatrix[0, 2], viewPortMatrix[0, 3],
            viewPortMatrix[1, 0], viewPortMatrix[1, 1], viewPortMatrix[1, 2], viewPortMatrix[1, 3],
            viewPortMatrix[2, 0], viewPortMatrix[2, 1], viewPortMatrix[2, 2], viewPortMatrix[2, 3],
            viewPortMatrix[3, 0], viewPortMatrix[3, 1], viewPortMatrix[3, 2], viewPortMatrix[3, 3],
        )
    )
    portMatrix.timesAssign(getProjectMatrix(z, d))
    return portMatrix
}

/**
 * ГЕНЕРАЦИЯ МАТРИЦЫ ПРОЕЦИРОВАНИЯ
 * @param: координата z, расстояние до окна
 * @return: матрица проецирования
 */
fun getProjectMatrix(z: Float, d: Float): Matrix {
    val matrix = Matrix()
    matrix[0,0] = d/(z+d)
    matrix[1,1] = d/(z+d)
    matrix[3,2] = 1/(z+d)
    matrix[3,3] = d/(z+d)
    return matrix
}

/**
 * Перемножение вектора и матрицы
 * @param: вектор, матрица
 * @return: вектор
 */
fun multiplicationVectorAndMatrix(point3D: Point3D, matrix: Matrix): Point3D {
    val xn = matrix[0, 0] * point3D.x + matrix[0, 1] * point3D.y + matrix[0, 2] * point3D.z + matrix[0, 3]
    val yn = matrix[1, 0] * point3D.x + matrix[1, 1] * point3D.y + matrix[1, 2] * point3D.z + matrix[1, 3]
    val zn = matrix[2, 0] * point3D.x + matrix[2, 1] * point3D.y + matrix[2, 2] * point3D.z + matrix[2, 3]
    return Point3D(xn, yn, zn)
}