package com.example.composegraphic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composegraphic.Lab1.CycleCanvas
import com.example.composegraphic.Lab1.LettersCanvas
import com.example.composegraphic.Lab1.ZoomCanvas


@Composable
fun Lab1Screen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(15.dp)) {
            ScreenPart1()
            ScreenPart2()
            ScreenPart3()
        }
    }
}


@Composable
fun ScreenPart1(){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 5.dp),
        elevation = 5.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Часть 1", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ){ append("Б") }
                    append("оровиков Алексе")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ){ append("Й") }
                },
                style = MaterialTheme.typography.body1
            )
            LettersCanvas(Modifier.padding(10.dp))
        }
    }
}

@Composable
fun ScreenPart2(){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 5.dp),
        elevation = 5.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Часть 2", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            Text(
                "\t\tВ этом алгоритме строится дуга окружности для первого квадранта, а координаты " +
                        "точек окружности для остальных квадрантов получаются симметрично. " +
                        "На каждом шаге алгоритма рассматриваются три пикселя, " +
                        "и из них выбирается наиболее подходящий путём сравнения расстояний от " +
                        "центра до выбранного пикселя с радиусом окружности.",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            CycleCanvas(Modifier.padding(10.dp))
            Column(Modifier.padding(5.dp)) {
                Text("R = 3 * (variant + 9)", style = MaterialTheme.typography.body1, color = Color.Green)
                Text("R = 5 * (variant + 9)", style = MaterialTheme.typography.body1, color = Color.Red)
                Text("R = 10 * (variant + 9)", style = MaterialTheme.typography.body1, color = Color.Blue)
            }
        }
    }
}

@Composable
fun ScreenPart3(){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 5.dp),
        elevation = 5.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Часть 3", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            ZoomCanvas(Modifier.padding(10.dp))

            Text(
                "Форма вращается относительно начала координат (для каждой бувы отдельно) и масштабируется жестами",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}


