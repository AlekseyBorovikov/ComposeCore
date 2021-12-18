package com.example.composegraphic.Lab3

import Figure
import Point3D
import Tetrahedron
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composegraphic.*
import com.example.composegraphic.core.*

private val screen: Screen = Screen(
    world = World(
        XMax = 1000f,
        YMax = 1000f,
        ZMax = 1000f,
        windowHeight = 1f,
        windowWidth = 1f,
        distanceToWindow = mutableStateOf(0.5f)
    )
)

private val camera = Camera(
    position = Point3D(0f, 0f, 5f)
)

@Composable
fun Scene3D(
    modifier: Modifier = Modifier
){
    var distance by remember{ screen.world.distanceToWindow }
    screen.world.figures.addAll(createFigures())
    ConstraintLayout() {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            Canvas(
                modifier = modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        screen.setSize(size.width.toFloat(),size.height.toFloat())
                    }
                    .background(Color.LightGray),
                onDraw = {
                    renderScene(screen, camera).forEach{ model ->
                        drawFilled(this, model)
//                        draw(this, model)
                    }
                    Log.d("test", "print")
                }
            )
        }
        val (sliderAlfa, sliderBeta, sliderGamma) = createRefs()
        Slider(
            value = camera.yaw,
            onValueChange = { camera.yaw = it },
            valueRange = -360f..360f,
            steps = 36,
            colors = SliderDefaults.colors(
                thumbColor = Color.Yellow,
                activeTrackColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 10.dp).constrainAs(sliderGamma){
                start.linkTo(parent.start)
                bottom.linkTo(sliderAlfa.top, 5.dp)
            }
        )
        Slider(
            value = camera.roll,
            onValueChange = { camera.roll = it },
            valueRange = -180f..180f,
            steps = 36,
            colors = SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 10.dp).constrainAs(sliderAlfa){
                start.linkTo(parent.start)
                bottom.linkTo(sliderBeta.top, 5.dp)
            }
        )
        Slider(
            value = camera.pitch,
            onValueChange = { camera.pitch = it },
            valueRange = -180f..180f,
            steps = 36,
            colors = SliderDefaults.colors(
                thumbColor = Color.Blue,
                activeTrackColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 10.dp).constrainAs(sliderBeta){
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom, 5.dp)
            }
        )
    }
}

fun createFigures(): List<Figure>{
    return listOf(
        Tetrahedron.build(4f, Point3D(5f, 2f, 3f)),
        Cube.build(5f, Point3D(3f, 4f, -3f)),
        TriangularPyramid.build(4f, 2f, Point3D(9f, -3f, 3f)),
//        // по условию y = 2git branch -M main
        TruncatedPyramid.build(1f, 2f, 2f, Point3D(11f, -1f, -3f)),
    )
}
