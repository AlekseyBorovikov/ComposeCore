package com.example.composegraphic.Lab1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity


private const val startScale = 5F
private const val maxScale = 6F
private const val minScale = 1F
private const val widthLetter = 50F
private const val heightLetter = 80F
private const val paddingLetter = 5F


@Composable
fun ZoomCanvas(
    modifier: Modifier = Modifier
){
    val widthCanvasDp = with(LocalDensity.current) {
        (widthLetter * 3 * maxScale).toDp()
    }
    val heightCanvasDp = with(LocalDensity.current) {
        (heightLetter * maxScale).toDp()
    }
    val sizeDp = if(widthCanvasDp > heightCanvasDp) widthCanvasDp else heightCanvasDp
    var scale by remember { mutableStateOf(startScale) }
    var rotation by remember { mutableStateOf(0f) }
    Canvas(
        modifier = modifier
            .size(sizeDp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _, pan, gestureZoom, gestureRotate ->
                        val resScale = scale * gestureZoom
                        if (resScale > minScale && resScale < maxScale) scale = resScale
                        rotation += gestureRotate / 50
                    }
                )
            }
    ){
        val sizeLetter = if (widthLetter > heightLetter) widthLetter else heightLetter
        val offsetToCenter = (sizeLetter * 3 * maxScale / 2) - (sizeLetter * 3 * scale / 2)
        printFirstLetterLastName(
            this, scale = scale,
            offsetX = offsetToCenter,
            offsetY = offsetToCenter,
            rotate = rotation
        )
        printLastLetterFirstName(
            this, scale = scale,
            offsetX = offsetToCenter + (widthLetter * 2) * scale,
            offsetY = offsetToCenter,
            rotate = rotation
        )
    }
}