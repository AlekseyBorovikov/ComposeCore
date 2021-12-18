package com.example.composegraphic.core

import Figure
import Model2D
import Model3D
import Point3D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import com.example.composegraphic.Camera
import com.example.composegraphic.Screen

/**
 * РЕНДЕРИНГ СЦЕНЫ
 * @param: экземпляр экрана и камеры
 * @return: преобразованные 2д-модели в экранных координатах
 */
fun renderScene(scene: Screen, camera: Camera): List<Model2D>{
    val renderedModels3D = renderModels(scene.world.figures)
    val renderedModels3DWithCamera = renderCamera(renderedModels3D, camera)
    val projectedModels = renderViewPosition(
        renderedModels3DWithCamera, scene.viewPortMatrix, scene.world.distanceToWindow.value
    )
    return projectedModels
}

/**
 * РЕНДЕРИНГ ФИГУР
 * применяет к каждой фигуре её матрицу преобразований
 * @param: список фигур
 * @return преобразованные 3д-модели в системе координат фигуры
 */
private fun renderModels(figures: List<Figure>): List<Model3D>{
    val renderedModels = mutableListOf<Model3D>()
    figures.forEach{ figure ->
        val vertexes = mutableListOf<Point3D>()
        figure.model3D.vertexes.forEach { point ->
            vertexes.add(multiplicationVectorAndMatrix(point, figure.transformationMatrix))
        }
        renderedModels.add(Model3D(vertexes, figure.model3D.poligons))
    }
    return renderedModels
}

/**
 * РЕНДЕРИНГ ТОЧЕК ПО ПОЛОЖЕНИЮ КАМЕРЫ
 * приводит модели к системе координат наблюдателя
 * @param: преобразованные 3д-модели в системе координат фигуры и объект камеры
 * @return преобразованные 3д-модели в системе координат наблюдателя
 */
private fun renderCamera(renderedModels3D: List<Model3D>, camera: Camera): List<Model3D>{
    val models3D = mutableListOf<Model3D>()
    renderedModels3D.forEach { model ->
        var addModel = true
        val renderedCameraVertexes: MutableList<Point3D> = mutableListOf()
        for (vertex in model.vertexes){
            renderedCameraVertexes.add(
                multiplicationVectorAndMatrix(vertex, camera.transformationMatrix)
            )
            // Отсечение объектов по плоскости окна
            // Если хотя бы одна вершина лежит за плоскостью - не отображать модель
            if (isFrontOfWindow(renderedCameraVertexes.last().z)) {
                addModel = false
                break
            }
        }
        if (addModel) models3D.add(Model3D(renderedCameraVertexes, model.poligons))
    }
    return models3D
}

/**
 * ПРОВЕРКА ВИДИМОСТИ
 * проверка видимости относительно окна
 * @param: расстояние до окна
 * @return boolean
 */
private fun isFrontOfWindow(distance: Float): Boolean = distance <= 0

/**
 * ПЕРЕВОД В ЭКРАННЫЕ КООРДИНАТЫ
 * проекция в двумерную систему координат и перевод в экранные координаты
 * @param:
 *      renderedModels3DWithCamera - преобразованные 3д-модели в системе координат наблюдателя
 *      viewPortMatrix - матрица перевода в экранные координаты
 *      distance - дистанция до экрана
 * @return преобразованные 2д-модели в экранных координатах
 */
private fun renderViewPosition(
    renderedModels3DWithCamera: List<Model3D>,
    viewPortMatrix: Matrix,
    distance: Float
): List<Model2D>{
    val projectedModels = mutableListOf<Model2D>()
    renderedModels3DWithCamera.forEach { model ->
        val projectedPoints = mutableListOf<Offset>()
        model.vertexes.forEach { v ->
            val pm = getProjectAndVPMatrix(v.z, distance, viewPortMatrix)
            val point3D = multiplicationVectorAndMatrix(v, pm)
            projectedPoints.add(Offset(point3D.x, point3D.y))
        }
        projectedModels.add(Model2D(projectedPoints, model.poligons))
    }

    return projectedModels
}