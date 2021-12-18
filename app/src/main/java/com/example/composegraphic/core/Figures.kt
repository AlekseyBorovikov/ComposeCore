import com.example.composegraphic.core.createRotationMatrix

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import kotlin.math.sqrt

// 3D - Точка
data class Point3D(
    var x: Float,
    var y: Float,
    var z: Float,
)

/**
 * ГРАНЬ
 * соединяет 2 точки
 */
data class Edge(
    val firstVertex: Int,
    val secondVertex: Int,
    val color: Color
)

/**
 * ПОЛИГОН
 * хранит точки грани и возвращает рёбра грани
 */
class Poligon(
    val vortexesNums: List<Int>,
    val color: Color
){
    /**
     * Вычисляет все рёбра по контору
     * @return список рёбер
     */
    fun getEdges(): List<Edge>{
        val edges = mutableListOf<Edge>()
        for (i in vortexesNums.indices){
            if(i < vortexesNums.size - 1)
                edges.add(Edge(vortexesNums[i], vortexesNums[i + 1], color))
            else
                edges.add(Edge(vortexesNums[i], vortexesNums[0], color))
        }
        return edges
    }
}

// Модель фигуры, описываемая 3d - вершинами и гранями
data class Model3D(
    val vertexes: List<Point3D> = listOf(),
    val poligons: List<Poligon> = listOf() ,
)

// Модель фигуры, описываемая 2d - вершинами и гранями
data class Model2D(
    val vertexes: List<Offset>,
    val poligons: List<Poligon>,
)

// контейнер для всех преобразований фигуры
data class FigureInstance(
    val position: Point3D = Point3D(0f, 0f, 0f), // позиция
    val scale: Float = 1f, // масштаб
    val roll: Float = 0f, // поворот относительно X
    val yaw: Float = 0f, // поворот относительно Y
    val pitch: Float = 0f, // поворот относительно Z
)

// фигура, описываемая моделью и преобразованиями
open class Figure(
    model3D: Model3D = Model3D(),
    private var instance: FigureInstance = FigureInstance()
) {
    private var _model3D: Model3D = model3D

    var model3D
        get() = _model3D
        protected set(value) { _model3D = value }

    var position
        get() = instance.position
        set(value) {
            instance.position.x = value.x
            instance.position.y = value.y
            instance.position.z = value.z
        }

    // матрица преобразований
    val transformationMatrix: Matrix = Matrix()

    init {
        // Создаём матрицу преобразом при инициализации,
        // т.к. в процессе работы приложения instance меняться не будет
        createTransformationMatrix()
    }

    private fun createTransformationMatrix(){
        // инициализация матрицы перемещения
        val transitionMatrix = Matrix()
        transitionMatrix[0,3] = instance.position.x
        transitionMatrix[1,3] = instance.position.y
        transitionMatrix[2,3] = instance.position.z

        // инициализация матрицы мастабирования
        val scaleMatrix = Matrix()
        scaleMatrix[0,0] = instance.scale
        scaleMatrix[1,1] = instance.scale
        scaleMatrix[2,2] = instance.scale

        // инициализация матрицы поворота
        val rotationMatrix = createRotationMatrix(
            Math.toRadians(instance.roll.toDouble()),
            Math.toRadians(instance.yaw.toDouble()),
            Math.toRadians(instance.pitch.toDouble()),
        )

        // инициализация матрицы преобразований
        // transitionMatrix = transitionMatrix * scaleMatrix * rotationMatrix
        transformationMatrix.timesAssign(transitionMatrix)
        transformationMatrix.timesAssign(scaleMatrix)
        transformationMatrix.timesAssign(rotationMatrix)
    }
}

/**
 * ТЕТРАЭДР
 * Расширение класса фигуры,
 * имеет билдер для создания фигуры по радиусу описанной окружности
 */
class Tetrahedron private constructor(
    model3D: Model3D, instance: FigureInstance
) : Figure(model3D, instance) {

    companion object {

        /**
         * Создаёт экзампляр фигуры по радиусу
         * @param:
         *  * R - радиус описанной окружности
         *  * position - позиция фигуры в мировых координатах
         * @return экземпляр фигуры тетраэдра
         */
        fun build(R: Float, position: Point3D): Tetrahedron {
            // радиус вписанной сферы
            val r = R / 3f
            // длина стороны тетраэдра
            val a = 2f * sqrt(6f) * R / 3f
            // радиус описанной окружности основания
            val Rb = 2f * sqrt(2f) * R / 3f
            // радиус вписанной окружности основания
            val rb = Rb / 2f

            // первая вершина тетраэдра
            val V = Point3D(0f, -r-R, 0f)
            // точка пересечения высоты и основания
            val Vo = Point3D(0f, 0f, 0f)
            // вершина основания тетраэдра
            val V1 = Point3D(Vo.x - Rb, Vo.y, Vo.z)
            // точка пересечения апофемы и стороны основания
            val V1o = Point3D(Vo.x + rb, Vo.y, Vo.z)
            // вершина основания тетраэдра
            val V2 = Point3D(V1o.x, V1o.y, V1o.z + a / 2)
            // вершина основания тетраэдра
            val V3 = Point3D(V1o.x, V1o.y, V1o.z - a / 2)

            val model3D = Model3D(
                vertexes = listOf(V, V1, V2, V3),
                poligons = listOf(
                    Poligon(listOf(0,1,2), Color.Red),
                    Poligon(listOf(0,2,3), Color.Red),
                    Poligon(listOf(0,1,3), Color.Red),
                    Poligon(listOf(1,2,3), Color.Blue),
                ),
            )

            val instance = FigureInstance(position)
            return Tetrahedron(model3D, instance)
        }
    }
}

/**
 * КУБ
 * Расширение класса фигуры,
 * имеет билдер для создания фигуры по длине стороны куба
 */
class Cube private constructor(
    model3D: Model3D, instance: FigureInstance
) : Figure(model3D, instance) {

    companion object {

        /**
         * Создаёт экзампляр фигуры по её стороне
         * @param:
         *  * a - сторона куба
         *  * position - позиция фигуры в мировых координатах
         * @return экземпляр фигуры куба
         */
        fun build(a: Float, position: Point3D): Cube {
            // вершина куба
            val V = Point3D(-a/2, 0f, a/2)
            val V1 = Point3D(V.x + a, V.y, V.z)
            val V2 = Point3D(V1.x, V1.y, V1.z - a)
            val V3 = Point3D(V2.x - a, V2.y, V2.z)
            val V4 = Point3D(V.x, V.y - a, V.z)
            val V5 = Point3D(V4.x + a, V4.y, V4.z)
            val V6 = Point3D(V5.x, V5.y, V5.z - a)
            val V7 = Point3D(V6.x - a, V6.y, V6.z)

            val model3D = Model3D(
                vertexes = listOf(V, V1, V2, V3, V4, V5, V6, V7),
                poligons = listOf(
//                    Poligon(listOf(0,1,2,3), Color.Yellow),
//                    Poligon(listOf(4,5,6,7), Color.Yellow),
//                    Poligon(listOf(2,1,5,6), Color.Red),
//                    Poligon(listOf(3,0,4,7), Color.Blue),
                    Poligon(listOf(0,1,2), Color.Yellow),
                    Poligon(listOf(0,2,3), Color.Yellow),
                    Poligon(listOf(4,5,6), Color.Yellow),
                    Poligon(listOf(4,6,7), Color.Yellow),

                    Poligon(listOf(0,1,4), Color.Green),
                    Poligon(listOf(1,4,5), Color.Green),
                    Poligon(listOf(2,3,6), Color.Green),
                    Poligon(listOf(3,6,7), Color.Green),

                    Poligon(listOf(2,1,5), Color.Red),
                    Poligon(listOf(2,5,6), Color.Red),
                    Poligon(listOf(3,0,4), Color.Blue),
                    Poligon(listOf(3,4,7), Color.Blue),
                ),
            )

            val instance = FigureInstance(position)
            return Cube(model3D, instance)
        }
    }
}

/**
 * ТРЕУГОЛЬНАЯ ПИРАМИДА
 * Расширение класса фигуры.
 * Три боковые грани - одинаковые равнобедренные треугольники, \
 * нижняя грань - равносторонний треугольник
 * имеет билдер для создания фигуры по радиусу описанной окружности
 * около основания и высоте
 */
class TriangularPyramid private constructor(
    model3D: Model3D, instance: FigureInstance
) : Figure(model3D, instance) {

    companion object {

        /**
         * Создаёт экзампляр фигуры по радиусу
         * @param:
         *  * R - радиус описанной окружности
         *  * h - высота пирамиды
         *  * position - позиция фигуры в мировых координатах
         * @return экземпляр фигуры пирамиды
         */
        fun build(R: Float, h: Float, position: Point3D): TriangularPyramid {
            // радиус вписанной окружности
            val r = R / 2f
            // длина стороны тетраэдра
            val a = R * sqrt(3f)

            // вершина пирамиды
            val V = Point3D(0f, -h, 0f)
            val V1 = Point3D(R, 0f, 0f)
            val V2 = Point3D(-r, 0f, a/2f)
            val V3 = Point3D(-r, 0f, -a/2f)

            val model3D = Model3D(
                vertexes = listOf(V, V1, V2, V3),
                poligons = listOf(
                    Poligon(listOf(0,1,2), Color.Blue),
                    Poligon(listOf(0,2,3), Color.Blue),
                    Poligon(listOf(0,1,3), Color.Blue),
                    Poligon(listOf(1,2,3), Color.Yellow),
                ),
            )

            val instance = FigureInstance(position)
            return TriangularPyramid(model3D, instance)
        }

    }
}

/**
 * УСЕЧЕННАЯ ПИРАМИДА
 * Расширение класса фигуры.
 * Четыре боковые грани - равносторонние трапеции,
 * нижняя и верхняя грани - квадраты
 * имеет билдер для создания фигуры по сторонам основания
 */
class TruncatedPyramid private constructor(
    model3D: Model3D, instance: FigureInstance
) : Figure(model3D, instance) {

    companion object {

        /**
         * Создаёт экзампляр фигуры по сторонам основания
         * @param:
         *  * at - сторона верхнего основания
         *  * ab - сторона нижнего основания
         *  * h - высота пирамиды
         *  * position - позиция фигуры в мировых координатах
         * @return экземпляр фигуры пирамиды
         */
        fun build(at: Float, ab: Float, h: Float, position: Point3D): TruncatedPyramid {
            // вершина нижнего основания пирамиды
            val V = Point3D(-ab/2, 0f, -ab/2)
            val V1 = Point3D(-ab/2, 0f, ab/2)
            val V2 = Point3D(ab/2, 0f, ab/2)
            val V3 = Point3D(ab/2, 0f, -ab/2)

            // вершина верхнего основания
            val V4 = Point3D(-at/2, -h, -at/2)
            val V5 = Point3D(-at/2, -h, at/2)
            val V6 = Point3D(at/2, -h, at/2)
            val V7 = Point3D(at/2, -h, -at/2)

            val model3D = Model3D(
                vertexes = listOf(V, V1, V2, V3, V4, V5, V6, V7),
                poligons = listOf(
                    Poligon(listOf(0,1,4), Color.Yellow),
                    Poligon(listOf(1,5,4), Color.Yellow),
                    Poligon(listOf(2,3,7), Color.Yellow),
                    Poligon(listOf(2,6,7), Color.Yellow),
                    Poligon(listOf(1,2,6), Color.Yellow),
                    Poligon(listOf(1,5,6), Color.Yellow),
                    Poligon(listOf(0,3,7), Color.Yellow),
                    Poligon(listOf(0,4,7), Color.Yellow),
                    Poligon(listOf(0,1,2), Color.Blue),
                    Poligon(listOf(0,2,3), Color.Blue),
                    Poligon(listOf(4,5,6), Color.Blue),
                    Poligon(listOf(4,6,7), Color.Blue),
                ),
            )

            val instance = FigureInstance(position)
            return TruncatedPyramid(model3D, instance)
        }

    }
}