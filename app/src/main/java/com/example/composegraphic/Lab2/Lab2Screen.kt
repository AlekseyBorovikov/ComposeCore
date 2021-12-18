package com.example.composegraphic

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.composegraphic.Lab2.*

@Composable
fun Lab2Screen(){
    Box(modifier = Modifier.fillMaxSize()) {
        SceneCanvas()
    }
}